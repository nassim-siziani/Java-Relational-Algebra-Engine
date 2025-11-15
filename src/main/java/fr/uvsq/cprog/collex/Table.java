package fr.uvsq.cprog.collex;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une table (ensemble de lignes et d'attributs).
 */
public class Table {


  /** Liste des noms d'attributs (colonnes). */
  private final List<String> attributs;

  /** Liste des lignes de la table. */
  private final List<Ligne> lignes;

  /**
   * Constructeur vide : crée une table sans colonnes ni lignes.
   */
  public Table() {
    this.attributs = new ArrayList<>();
    this.lignes = new ArrayList<>();
  }

  /**
   * Constructeur avec noms de colonnes.
   *
   * @param attributs noms des colonnes de la table
   */
  public Table(List<String> attributs) {
    this.attributs = new ArrayList<>(attributs);
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
    sb.append(String.join(",", attributs)).append("\n");
    for (Ligne l : lignes) {
      sb.append(l.toString()).append("\n");
    }
    return sb.toString();
  }
}
