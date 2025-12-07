package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Point d'entrée de l'application.
 *
 * Usage depuis un répertoire de test :
 *   java -jar ../target/collex-jar-with-dependencies.jar reqSelUsers.json
 *   java -jar ../target/collex-jar-with-dependencies.jar reqProjSel.alg
 */
public final class Main {

  private Main() {
    // pas d'instance
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage : java -jar <jar> <requete.json|requete.alg>");
      System.exit(1);
    }

    String requete = args[0];

    if (requete.endsWith(".json")) {
      executerJson(requete);
    } else if (requete.endsWith(".alg")) {
      traduireAlg(requete);
    } else {
      System.err.println(
          "Format de requête non supporté (seulement .json et .alg)");
      System.exit(1);
    }
  }

  /**
   * Exécute une requête décrite dans un fichier JSON
   * et écrit le résultat dans results/output.csv
   * (comme demandé dans le sujet).
   */
  private static void executerJson(String jsonFile) {
    try {
      JsonQueryExecutor executor = new JsonQueryExecutor();
      Table resultat = executor.execute(jsonFile);

      // Créer le répertoire results dans le répertoire courant
      Path resultsDir = Path.of("results");
      Files.createDirectories(resultsDir);

      // Fichier output.csv
      Path output = resultsDir.resolve("output.csv");

      // Table -> CSV (toString() de Table)
      String csv = resultat.toString();

      Files.writeString(output, csv + System.lineSeparator(),
          StandardCharsets.UTF_8);

      System.out.println("Résultat écrit dans results/output.csv");
    } catch (IOException e) {
      System.err.println("Erreur lors de l'écriture du résultat : " + e.getMessage());
      System.exit(1);
    } catch (RuntimeException e) {
      System.err.println("Erreur : " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Traduit un fichier .alg en .json (sans exécuter),
   * et affiche "Traduction en <nom>.json"
   * comme dans l’énoncé.
   */
  private static void traduireAlg(String algFile) {
    try {
      AlgTranslator translator = new AlgTranslator();
      Path jsonPath = translator.translate(algFile);

      // On affiche exactement le message attendu :
      // Traduction en reqProjSel.json
      System.out.println("Traduction en " + jsonPath.getFileName());

    } catch (RuntimeException e) {
      System.err.println("Erreur lors de la traduction : " + e.getMessage());
      System.exit(1);
    }
  }
}
