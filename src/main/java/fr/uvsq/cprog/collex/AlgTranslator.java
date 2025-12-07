package fr.uvsq.cprog.collex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Traduit un fichier .alg en un fichier .json correspondant
 * au format attendu par JsonQueryExecutor.
 *
 * Exemple .alg :
 *
 * users: users.csv
 * orders: orders.csv
 *
 * R1 = SELECT users age < 25
 * R  = PROJ R1 name
 *
 * → reqProjSel.json équivalent.
 */
public final class AlgTranslator {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Traduit un fichier .alg en .json (même base de nom).
   *
   * @param algFile chemin du fichier .alg
   * @return chemin du fichier .json généré
   */
  public Path translate(String algFile) {
    try {
      Path algPath = Path.of(algFile);
      List<String> lignes = Files.readAllLines(algPath);

      // 1) Séparer déclarations de tables et expressions R = ...
      Map<String, String> tables = new LinkedHashMap<>();
      List<String> exprs = new ArrayList<>();

      boolean inDeclarations = true;
      for (String brute : lignes) {
        String line = brute.trim();
        if (line.isEmpty()) {
          // ligne vide : après ça on passe aux expressions
          inDeclarations = false;
          continue;
        }

        if (inDeclarations) {
          // format: nomTable: fichier.csv
          int idx = line.indexOf(':');
          if (idx <= 0 || idx == line.length() - 1) {
            throw new IllegalArgumentException("Déclaration de table invalide : " + line);
          }
          String nom = line.substring(0, idx).trim();
          String fichier = line.substring(idx + 1).trim();
          tables.put(nom, fichier);
        } else {
          // expression algébrique : R1 = ...
          exprs.add(line);
        }
      }

      if (tables.isEmpty()) {
        throw new IllegalArgumentException("Aucune table déclarée dans le fichier .alg");
      }
      if (exprs.isEmpty()) {
        throw new IllegalArgumentException("Aucune expression algébrique dans le fichier .alg");
      }

      // 2) Construire les JSON de plan pour chaque variable R1, R2, ...
      Map<String, JsonObject> relations = new HashMap<>();

      for (String expr : exprs) {
        parseExpression(expr, tables, relations);
      }

      // 3) Le résultat est la dernière variable définie
      String lastVar = exprs.get(exprs.size() - 1).split("=")[0].trim();
      JsonObject finalPlan = relations.get(lastVar);
      if (finalPlan == null) {
        throw new IllegalStateException("Impossible de trouver le plan pour " + lastVar);
      }

      // 4) Construire l'objet racine JSON { "tables": {...}, "plan": {...} }
      JsonObject root = new JsonObject();

      JsonObject tablesJson = new JsonObject();
      for (Map.Entry<String, String> e : tables.entrySet()) {
        tablesJson.addProperty(e.getKey(), e.getValue());
      }
      root.add("tables", tablesJson);
      root.add("plan", finalPlan);

      // 5) Écriture du fichier .json
      String baseName = algPath.getFileName().toString();
      if (baseName.endsWith(".alg")) {
        baseName = baseName.substring(0, baseName.length() - 4);
      }
      String jsonName = baseName + ".json";

      Path jsonPath = (algPath.getParent() == null)
          ? Path.of(jsonName)
          : algPath.getParent().resolve(jsonName);

      Files.writeString(jsonPath, gson.toJson(root));

      return jsonPath;

    } catch (IOException e) {
      throw new RuntimeException("Erreur de lecture/écriture pour : " + algFile, e);
    }
  }

  /**
   * Analyse une ligne de type :
   *   R1 = SELECT users age < 25
   *   R2 = PROJ R1 name
   *   R3 = JOIN R1 orders id user_id
   */
  private void parseExpression(String expr,
                               Map<String, String> tables,
                               Map<String, JsonObject> relations) {

    String[] parts = expr.split("=", 2);
    if (parts.length != 2) {
      throw new IllegalArgumentException("Expression invalide (manque '=') : " + expr);
    }

    String var = parts[0].trim();          // ex: R1
    String right = parts[1].trim();        // ex: SELECT users age < 25

    String[] tokens = right.split("\\s+");
    if (tokens.length < 2) {
      throw new IllegalArgumentException("Expression algébrique trop courte : " + expr);
    }

    String op = tokens[0].toUpperCase();

    JsonObject plan;
    switch (op) {
      case "SELECT" -> plan = buildSelectPlan(tokens, tables, relations);
      case "PROJ"   -> plan = buildProjectPlan(tokens, tables, relations);
      case "JOIN"   -> plan = buildJoinPlan(tokens, tables, relations);
      default -> throw new IllegalArgumentException("Opération inconnue dans .alg : " + op);
    }

    relations.put(var, plan);
  }

  // SELECT <source> <attr> <op> <value>
  // ex: SELECT users age < 25
  private JsonObject buildSelectPlan(String[] tokens,
                                     Map<String, String> tables,
                                     Map<String, JsonObject> relations) {

    if (tokens.length < 5) {
      throw new IllegalArgumentException("SELECT attend : SELECT src attr op value");
    }

    String source = tokens[1];   // table ou variable
    String attr = tokens[2];     // attribut
    String op = tokens[3];       // opérateur
    String value = tokens[4];    // peut être un nombre ou 'chaine'

    JsonObject inputPlan = resolveSource(source, tables, relations);

    String predicate = attr + " " + op + " " + value;

    JsonObject plan = new JsonObject();
    plan.addProperty("type", "SELECT");
    plan.addProperty("predicate", predicate);
    plan.add("input", inputPlan);

    return plan;
  }

  // PROJ <source> attr1 attr2 ...
  // ex: PROJ R1 name age
  private JsonObject buildProjectPlan(String[] tokens,
                                      Map<String, String> tables,
                                      Map<String, JsonObject> relations) {

    if (tokens.length < 3) {
      throw new IllegalArgumentException("PROJ attend : PROJ src col1 col2 ...");
    }

    String source = tokens[1];

    JsonObject inputPlan = resolveSource(source, tables, relations);

    JsonArray cols = new JsonArray();
    for (int i = 2; i < tokens.length; i++) {
      cols.add(tokens[i]);
    }

    JsonObject plan = new JsonObject();
    plan.addProperty("type", "PROJECT");
    plan.add("columns", cols);
    plan.add("input", inputPlan);

    return plan;
  }

  // JOIN <left> <right> <onLeft> <onRight>
  // ex: JOIN R1 orders id user_id
  private JsonObject buildJoinPlan(String[] tokens,
                                   Map<String, String> tables,
                                   Map<String, JsonObject> relations) {

    if (tokens.length != 5) {
      throw new IllegalArgumentException(
          "JOIN attend exactement : JOIN left right attrLeft attrRight");
    }

    String leftSrc = tokens[1];
    String rightSrc = tokens[2];
    String onLeft = tokens[3];
    String onRight = tokens[4];

    JsonObject leftPlan = resolveSource(leftSrc, tables, relations);
    JsonObject rightPlan = resolveSource(rightSrc, tables, relations);

    JsonObject plan = new JsonObject();
    plan.addProperty("type", "JOIN");
    plan.add("left", leftPlan);
    plan.add("right", rightPlan);
    plan.addProperty("onLeft", onLeft);
    plan.addProperty("onRight", onRight);

    return plan;
  }

  /**
   * Résout une source : soit une table déclarée (users),
   * soit une variable R1 déjà définie.
   *
   * Si c'est une table, on renvoie :
   *   { "type": "SCAN", "table": "<nomTable>" }
   *
   * Si c'est une variable, on renvoie son plan JSON.
   */
  private JsonObject resolveSource(String name,
                                   Map<String, String> tables,
                                   Map<String, JsonObject> relations) {

    if (relations.containsKey(name)) {
      // déjà une variable calculée (R1, R2, ...)
      return relations.get(name);
    }

    if (tables.containsKey(name)) {
      // c'est une table de base -> SCAN
      JsonObject scan = new JsonObject();
      scan.addProperty("type", "SCAN");
      scan.addProperty("table", name);
      return scan;
    }

    throw new IllegalArgumentException(
        "Source inconnue dans .alg (ni table ni variable) : " + name);
  }
}
