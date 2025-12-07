package fr.uvsq.cprog.collex;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une table (ensemble de lignes et d'attributs).
 */
public class Table {

  /** Nom de la table (pour les jointure) */
  private final String nom;

  /** Liste des noms d'attributs (colonnes). */
  private final List<String> attributs;

  /** Liste des lignes de la table. */
  private final List<Ligne> lignes;

  /**
   * Constructeur vide : crée une table sans colonnes ni lignes.
   */
  public Table() {
    this.nom = "";
    this.attributs = new ArrayList<>();
    this.lignes = new ArrayList<>();
  }

  /**
   * Constructeur avec noms de colonnes.
   *
   * @param attributs noms des colonnes de la table
   */
  public Table(List<String> attributs) {
    this.nom = "";
    this.attributs = new ArrayList<>();
    for (String attr : attributs) {
        if (attr == null) {
            throw new IllegalArgumentException("Nom d'attribut null interdit");
        }
        this.attributs.add(attr.trim().toLowerCase());
    }
    this.lignes = new ArrayList<>();
  }

  /**
  * Constructeur avecle nom de la table et les noms de colonnes.
  *
  * @param nom nom de la table
  * @param attributs noms des colonnes de la table
  */
  public Table(String nom, List<String> attributs) {
    this.nom = nom;
    this.attributs = new ArrayList<>();
    for (String attr : attributs) {
        if (attr == null) {
            throw new IllegalArgumentException("Nom d'attribut null interdit");
        }
        this.attributs.add(attr.trim().toLowerCase());
    }
    this.lignes = new ArrayList<>();
  }
  /**
   * Ajoute une ligne à la table.
   *
   * @param ligne la ligne à ajouter
   */
  public void ajouterLigne(Ligne ligne) {
    lignes.add(ligne);
  }

  /**
   * Renvoie la liste des lignes.
   *
   * @return la liste des lignes de la table
   */
  public List<Ligne> getLignes() {
    return lignes;
  }

  /**
   * Renvoie la liste des attributs.
   *
   * @return la liste des noms d'attributs
   */
  public List<String> getAttributs() {
    return attributs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    // === En-tête : colonnes séparées par des virgules ===
    for (int i = 0; i < attributs.size(); i++) {
        sb.append(attributs.get(i));
        if (i < attributs.size() - 1) {
            sb.append(",");
        }
    }
    sb.append("\n");

    // === Corps : chaque ligne dans l'ordre strict des attributs ===
    for (Ligne ligne : lignes) {
        for (int i = 0; i < attributs.size(); i++) {
            String attr = attributs.get(i);

            // Récupération de la valeur
            String valeur = ligne.getValeur(attr);

            // Les valeurs null -> chaîne vide
            sb.append(valeur == null ? "" : valeur);

            if (i < attributs.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("\n");
    }

    return sb.toString();
  }


 /**
 * Indique si la table contient un attribut avec ce nom,
 * en ignorant la casse (age, Age, aGE sont considérés identiques).
 *
 * @param attribut nom recherché
 * @return true si l'attribut existe, false sinon
 */
 public boolean contientAttribut(String attribut) {
    if (attribut == null) {
        return false;
    }
    String nomNormalise = attribut.trim().toLowerCase();
    return attributs.stream().anyMatch(a -> a.equals(nomNormalise));
}

  /**
   * Renvoie le nom de la table.
   * Le nom est utilisé notamment pour préfixer les attributs en sortie
   * d'une opération de jointure afin d'éviter les collisions de colonnes.
   *
   * @return le nom de la table
   */
public String getNom() {
    return nom;
}

}
