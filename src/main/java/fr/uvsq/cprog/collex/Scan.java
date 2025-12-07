package fr.uvsq.cprog.collex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Opérateur SCAN de l'algèbre relationnelle.
 *
 * <p>Lit un fichier CSV et construit une {@link Table} :</p>
 * <ul>
 *   <li>les noms d'attributs (en-tête) sont normalisés en minuscules, sans
 *   espaces superflus,</li>
 *   <li>les cellules vides deviennent la chaîne vide {@code ""},</li>
 *   <li>aucune ligne vide n'est ajoutée à la table,</li>
 *   <li>l'ordre des colonnes est celui de l'en-tête du fichier.</li>
 * </ul>
 */
public final class Scan implements Operation {

  /** Chemin (relatif) vers le fichier CSV à lire. */
  private final String cheminCsv;

  /**
   * Construit un opérateur SCAN.
   *
   * @param cheminCsv chemin du fichier CSV (relatif au répertoire courant)
   */
  public Scan(String cheminCsv) {
    if (cheminCsv == null || cheminCsv.isBlank()) {
      throw new IllegalArgumentException("Le chemin du CSV ne peut pas être nul ou vide.");
    }
    this.cheminCsv = cheminCsv;
  }

  @Override
  public Table appliquer(Table... tables) {
    // SCAN n'utilise pas les tables en entrée : il lit uniquement le fichier CSV.
    try (BufferedReader reader = new BufferedReader(new FileReader(cheminCsv))) {

      String ligneEntete = reader.readLine();
      if (ligneEntete == null || ligneEntete.isBlank()) {
        throw new IllegalArgumentException(
            "Le fichier CSV " + cheminCsv + " ne contient pas d'en-tête valide.");
      }

      // Noms des colonnes : trim + lower-case pour être cohérent avec Table/Join/Project.
      List<String> attributs = Arrays.stream(ligneEntete.split(","))
          .map(String::trim)
          .map(String::toLowerCase)
          .toList();

      String nomTable = extraireNomTable(cheminCsv);
      Table table = new Table(nomTable, attributs);

      String ligne;
      while ((ligne = reader.readLine()) != null) {
        // On ignore complètement les lignes vides / blanches.
        if (ligne.isBlank()) {
          continue;
        }

        String[] valeursBrutes = ligne.split(",", -1); // -1 pour garder les champs vides
        List<String> valeurs = new ArrayList<>(attributs.size());

        for (int i = 0; i < attributs.size(); i++) {
          String v = i < valeursBrutes.length ? valeursBrutes[i] : "";
          v = v.trim();
          // Champ vide => chaîne vide ""
          if (v.isEmpty()) {
            valeurs.add("");
          } else {
            valeurs.add(v);
          }
        }

        Ligne l = new Ligne();
        for (int i = 0; i < attributs.size(); i++) {
          l.setValeur(attributs.get(i), valeurs.get(i));
        }
        table.ajouterLigne(l);
      }

      return table;

    } catch (IOException e) {
      throw new RuntimeException(
          "Erreur lors de la lecture du fichier CSV : " + cheminCsv, e);
    }
  }

  /**
   * Extrait un nom de table à partir du chemin du fichier CSV.
   *
   * <p>Exemples :</p>
   * <ul>
   *   <li>{@code "users.csv"} → {@code "users"}</li>
   *   <li>{@code "data/orders.csv"} → {@code "orders"}</li>
   * </ul>
   *
   * @param chemin chemin du fichier
   * @return nom de table dérivé du nom du fichier, en minuscules
   */
  private static String extraireNomTable(String chemin) {
    String fichier = chemin;

    // Gestion des éventuels répertoires (Unix ou Windows)
    int slash = fichier.lastIndexOf('/');
    int backslash = fichier.lastIndexOf('\\');
    int sep = Math.max(slash, backslash);
    if (sep >= 0) {
      fichier = fichier.substring(sep + 1);
    }

    // Suppression de l'extension .csv si présente
    String lower = fichier.toLowerCase();
    if (lower.endsWith(".csv")) {
      fichier = fichier.substring(0, fichier.length() - 4);
    }

    return fichier.toLowerCase().trim();
  }
}
