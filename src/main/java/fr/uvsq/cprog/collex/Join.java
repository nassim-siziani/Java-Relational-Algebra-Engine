package fr.uvsq.cprog.collex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Opération JOIN de l'algèbre relationnelle.
 *
 * <p>Cette opération combine deux tables en ne gardant que les lignes
 * pour lesquelles les attributs de jointure ont des valeurs égales.</p>
 *
 * <p>Dans le résultat :
 * <ul>
 *   <li>Les attributs ambigus (présents dans les deux tables) sont préfixés
 *   par le nom de leur table uniquement à la sortie du JOIN.</li>
 *   <li>Les noms d'attributs sont résolus de manière insensible à la casse
 *   (comme en SQL).</li>
 * </ul>
 * </p>
 */
public final class Join implements Operation {

  private final String attrLeft;
  private final String attrRight;

  public Join(String attrLeft, String attrRight) {
    if (attrLeft == null || attrRight == null) {
      throw new IllegalArgumentException(
          "Les attributs de jointure ne peuvent pas être null.");
    }
    this.attrLeft = attrLeft.trim().toLowerCase();
    this.attrRight = attrRight.trim().toLowerCase();
  }

  @Override
  public Table appliquer(Table... tables) {
    if (tables == null || tables.length != 2) {
      throw new IllegalArgumentException(
          "JOIN doit recevoir exactement deux tables en entrée.");
    }

    Table t1 = tables[0];
    Table t2 = tables[1];

    if (!t1.contientAttribut(attrLeft)) {
      throw new IllegalArgumentException(
          "Attribut de jointure introuvable dans la table de gauche : "
              + attrLeft);
    }
    if (!t2.contientAttribut(attrRight)) {
      throw new IllegalArgumentException(
          "Attribut de jointure introuvable dans la table de droite : "
              + attrRight);
    }

    // ---------------------------------------------------------
    // 1) Détection des attributs communs (ambigus) - case insensitive
    // ---------------------------------------------------------
    Set<String> communsLower = new HashSet<>();
    for (String a1 : t1.getAttributs()) {
      for (String a2 : t2.getAttributs()) {
        if (a1.equalsIgnoreCase(a2)) {
          communsLower.add(a1.toLowerCase());
        }
      }
    }

    // Cas spécial : si les 2 attributs de jointure ont le même nom,
    // on ne préfixe pas ce nom-là.
    if (attrLeft.equals(attrRight)) {
      communsLower.remove(attrLeft);
    }

    // Helper local
    java.util.function.Predicate<String> estCommun =
        a -> communsLower.contains(a.toLowerCase());

    // ---------------------------------------------------------
    // 2) Construction des colonnes
    // ---------------------------------------------------------
    List<String> colonnes = new ArrayList<>();

    // Attribut de jointure de gauche en premier
    colonnes.add(attrLeft);

    // Colonnes de gauche (hors attrLeft)
    for (String a : t1.getAttributs()) {
      if (a.equalsIgnoreCase(attrLeft)) {
        continue;
      }
      String nomFinal = estCommun.test(a)
          ? t1.getNom() + "." + a
          : a;
      colonnes.add(nomFinal);
    }

    // Colonnes de droite
    for (String a : t2.getAttributs()) {
      if (a.equalsIgnoreCase(attrRight) && attrRight.equals(attrLeft)) {
        continue;
      }
      String nomFinal = estCommun.test(a)
          ? t2.getNom() + "." + a
          : a;
      colonnes.add(nomFinal);
    }

    Table resultat = new Table("Join_" + t1.getNom() + "_" + t2.getNom(),
        colonnes);

    // ---------------------------------------------------------
    // 3) Remplissage des lignes
    // ---------------------------------------------------------
    for (Ligne l1 : t1.getLignes()) {
      String v1 = l1.getValeur(attrLeft);

      for (Ligne l2 : t2.getLignes()) {
        String v2 = l2.getValeur(attrRight);

        boolean egales;
        if (v1 == null && v2 == null) {
          egales = true;
        } else if (v1 == null || v2 == null) {
          egales = false;
        } else {
          egales = v1.equals(v2);
        }

        if (egales) {
          Ligne nouvelle = new Ligne();

          // 3.1) attribut de jointure gauche
          nouvelle.setValeur(attrLeft, v1);

          // 3.2) colonnes de gauche
          for (String a : t1.getAttributs()) {
            if (a.equalsIgnoreCase(attrLeft)) {
              continue;
            }
            String nomFinal = estCommun.test(a)
                ? t1.getNom() + "." + a
                : a;
            nouvelle.setValeur(nomFinal, l1.getValeur(a));
          }

          // 3.3) colonnes de droite
          for (String a : t2.getAttributs()) {
            if (a.equalsIgnoreCase(attrRight) && attrRight.equals(attrLeft)) {
              continue;
            }
            String nomFinal = estCommun.test(a)
                ? t2.getNom() + "." + a
                : a;
            nouvelle.setValeur(nomFinal, l2.getValeur(a));
          }

          resultat.ajouterLigne(nouvelle);
        }
      }
    }

    return resultat;
  }
}
