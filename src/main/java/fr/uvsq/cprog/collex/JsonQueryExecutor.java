package fr.uvsq.cprog.collex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Exécute une requête algébrique décrite dans un fichier JSON.
 *
 * Format attendu :
 * {
 *   "tables": {
 *     "users": "users.csv",
 *     "orders": "orders.csv"
 *   },
 *   "plan": {
 *     "type": "PROJECT",
 *     "columns": ["name"],
 *     "input": {
 *       "type": "SELECT",
 *       "predicate": "age < 25",
 *       "input": {
 *         "type": "SCAN",
 *         "table": "users"
 *       }
 *     }
 *   }
 * }
 */
public final class JsonQueryExecutor {

  /**
   * Exécute une requête JSON et retourne la table résultat.
   *
   * @param jsonFile nom ou chemin du fichier JSON
   * @return table résultat
   */
  public Table execute(String jsonFile) {
    try {
      Path jsonPath = Path.of(jsonFile);
      String contenu = Files.readString(jsonPath);

      JsonObject root = JsonParser.parseString(contenu).getAsJsonObject();

      // 1) Charger les tables déclarées
      JsonObject tablesJson = root.getAsJsonObject("tables");
      Map<String, Table> tables = new HashMap<>();

      if (tablesJson != null) {
        for (Map.Entry<String, JsonElement> e : tablesJson.entrySet()) {
          String nomLogique = e.getKey();                // ex: "users"
          String nomFichier = e.getValue().getAsString(); // ex: "users.csv"

          // Résolution par rapport au répertoire du JSON (conforme au sujet)
          Path csvPath = (jsonPath.getParent() == null)
              ? Path.of(nomFichier)
              : jsonPath.getParent().resolve(nomFichier);

          Table t = new Scan(csvPath.toString()).appliquer();
          tables.put(nomLogique.toLowerCase(), t);
        }
      }

      // 2) Évaluer récursivement le plan
      JsonObject plan = root.getAsJsonObject("plan");
      if (plan == null) {
        throw new IllegalArgumentException("Le fichier JSON ne contient pas de champ 'plan'.");
      }

      return evalPlan(plan, tables);

    } catch (IOException e) {
      throw new RuntimeException("Erreur de lecture du fichier JSON : " + jsonFile, e);
    }
  }

  /**
   * Évalue récursivement un nœud de plan JSON.
   */
  private Table evalPlan(JsonObject plan, Map<String, Table> tables) {
    String type = plan.get("type").getAsString().toUpperCase();

    return switch (type) {
      case "SCAN" -> evalScan(plan, tables);
      case "SELECT" -> evalSelect(plan, tables);
      case "PROJECT" -> evalProject(plan, tables);
      case "JOIN" -> evalJoin(plan, tables);
      default -> throw new IllegalArgumentException("Type d'opération inconnu : " + type);
    };
  }

  // ---------- SCAN ----------

  private Table evalScan(JsonObject plan, Map<String, Table> tables) {
    String tableName = plan.get("table").getAsString().toLowerCase();
    Table t = tables.get(tableName);
    if (t == null) {
      throw new IllegalArgumentException("Table logique inconnue dans SCAN : " + tableName);
    }
    return t;
  }

  // ---------- SELECT ----------

  private Table evalSelect(JsonObject plan, Map<String, Table> tables) {
    String predicateStr = plan.get("predicate").getAsString();
    JsonObject input = plan.getAsJsonObject("input");
    if (input == null) {
      throw new IllegalArgumentException("SELECT doit contenir un champ 'input'.");
    }

    Table entree = evalPlan(input, tables);
    Predicate<Ligne> predicate = parsePredicate(predicateStr);

    Select select = new Select(predicate);
    return select.appliquer(entree);
  }

  // ---------- PROJECT ----------

  private Table evalProject(JsonObject plan, Map<String, Table> tables) {
    JsonArray colsJson = plan.getAsJsonArray("columns");
    if (colsJson == null || colsJson.size() == 0) {
      throw new IllegalArgumentException("PROJECT doit contenir un tableau 'columns' non vide.");
    }

    java.util.List<String> cols = new java.util.ArrayList<>();
    for (JsonElement e : colsJson) {
      cols.add(e.getAsString());
    }

    JsonObject input = plan.getAsJsonObject("input");
    if (input == null) {
      throw new IllegalArgumentException("PROJECT doit contenir un champ 'input'.");
    }

    Table entree = evalPlan(input, tables);
    Project proj = new Project(cols);
    return proj.appliquer(entree);
  }

  // ---------- JOIN ----------

  private Table evalJoin(JsonObject plan, Map<String, Table> tables) {
    JsonObject left = plan.getAsJsonObject("left");
    JsonObject right = plan.getAsJsonObject("right");
    if (left == null || right == null) {
      throw new IllegalArgumentException("JOIN doit contenir 'left' et 'right'.");
    }

    String onLeft = plan.get("onLeft").getAsString();
    String onRight = plan.get("onRight").getAsString();

    Table tLeft = evalPlan(left, tables);
    Table tRight = evalPlan(right, tables);

    Join join = new Join(onLeft, onRight);
    return join.appliquer(tLeft, tRight);
  }

  // ---------- Parsing des prédicats SELECT ----------

  /**
   * Construit un prédicat de sélection à partir d'une chaîne
   * du type "age < 25" ou "name = 'Alice'".
   */
  private Predicate<Ligne> parsePredicate(String predicate) {
    if (predicate == null || predicate.isBlank()) {
      throw new IllegalArgumentException("Prédicat SELECT vide.");
    }

    // format : attribut op valeur
    // ex : "age < 25" ou "name = 'Alice'"
    String[] tokens = predicate.trim().split("\\s+", 3);
    if (tokens.length != 3) {
      throw new IllegalArgumentException("Format de prédicat invalide : " + predicate);
    }

    String attr = tokens[0].toLowerCase(); // attribut, insensible à la casse
    String op = tokens[1];
    String rawValue = tokens[2].trim();

    boolean isString;
    String stringValue = null;
    Double numericValue = null;

    if (rawValue.startsWith("'") && rawValue.endsWith("'") && rawValue.length() >= 2) {
      // Chaîne de caractères : on enlève les quotes
      isString = true;
      stringValue = rawValue.substring(1, rawValue.length() - 1);
    } else {
      // On essaie de parser en nombre
      try {
        numericValue = Double.parseDouble(rawValue);
        isString = false;
      } catch (NumberFormatException e) {
        // Valeur non numérique sans quotes => chaîne brute
        isString = true;
        stringValue = rawValue;
      }
    }

    if (isString) {
      String valFinale = stringValue;
      return ligne -> {
        String v = ligne.getValeur(attr);
        if (v == null) {
          return false;
        }
        return switch (op) {
          case "=" -> v.equals(valFinale);
          case "<" -> v.compareTo(valFinale) < 0;   // ordre ASCII (consigne du prof)
          case ">" -> v.compareTo(valFinale) > 0;
          default -> throw new IllegalArgumentException(
              "Opérateur de comparaison invalide pour chaîne : " + op);
        };
      };
    } else {
      double cible = numericValue;
      return ligne -> {
        String v = ligne.getValeur(attr);
        if (v == null || v.isEmpty()) {
          return false;
        }
        double x;
        try {
          x = Double.parseDouble(v);
        } catch (NumberFormatException e) {
          // Valeur non numérique dans la colonne alors qu'on compare à un nombre
          return false;
        }

        return switch (op) {
          case "=" -> x == cible;
          case "<" -> x < cible;
          case ">" -> x > cible;
          default -> throw new IllegalArgumentException(
              "Opérateur de comparaison invalide pour nombre : " + op);
        };
      };
    }
  }
}
