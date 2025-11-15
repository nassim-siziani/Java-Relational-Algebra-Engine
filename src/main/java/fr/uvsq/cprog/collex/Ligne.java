package fr.uvsq.cprog.collex;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente une ligne d'une table, sous forme d'association attribut → valeur.
 */
public class Ligne {

  /** Dictionnaire des paires attribut/valeur. */
  private final Map<String, String> attributs;

  /** Constructeur vide : crée une ligne sans valeurs initiales. */
  public Ligne() {
    this.attributs = new HashMap<>();
  }

  /**
   * Constructeur à partir d'une map existante.
   *
   * @param attributs map contenant les paires attribut/valeur initiales
   */
  public Ligne(Map<String, String> attributs) {
    this.attributs = new HashMap<>(attributs);
  }

  /**
   * Définit la valeur d'un attribut dans cette ligne.
   *
   * @param nomAttribut nom de l'attribut
   * @param valeur valeur associée à cet attribut
   */
  public void setValeur(String nomAttribut, String valeur) {
    attributs.put(nomAttribut, valeur);
  }

  /**
   * Renvoie la valeur associée à un attribut.
   *
   * @param nomAttribut nom de l'attribut
   * @return la valeur correspondante, ou null si absente
   */
  public String getValeur(String nomAttribut) {
    return attributs.get(nomAttribut);
  }

  /**
   * Renvoie la map complète d'attributs.
   *
   * @return la map des attributs de cette ligne
   */
  public Map<String, String> getAttributs() {
    return attributs;
  }

  @Override
  public String toString() {
    return attributs.toString();
  }
}   
