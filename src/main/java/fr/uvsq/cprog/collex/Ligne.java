package fr.uvsq.cprog.collex;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente une ligne d'une table :
 * une association attribut -> valeur.
 */
public class Ligne {

  /** Stockage des valeurs sous forme clé-valeur. */
  private final Map<String, String> valeurs;

  /** Constructeur par défaut. */
  public Ligne() {
    this.valeurs = new HashMap<>();
  }

  /**
   * Définit la valeur d'un attribut.
   * Les noms d'attributs sont normalisés en minuscules.
   *
   * @param attribut nom de l'attribut
   * @param valeur   valeur associée
   */
  public void setValeur(String attribut, String valeur) {
    if (attribut == null) {
      throw new IllegalArgumentException("Nom d'attribut null interdit");
    }
    valeurs.put(attribut.toLowerCase(), valeur);
  }

  /**
   * Récupère la valeur associée à un attribut (normalisation en minuscules).
   *
   * @param attribut nom de l'attribut
   * @return valeur ou null si l'attribut n'existe pas
   */
  public String getValeur(String attribut) {
    if (attribut == null) {
      return null;
    }
    return valeurs.get(attribut.toLowerCase());
  }

  /**
   * Retourne la map complète des attributs/valeurs.
   *
   * @return map des valeurs
   */
  public Map<String, String> getAttributs() {
    return valeurs;
  }
}
