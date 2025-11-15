package fr.uvsq.cprog.collex;

import java.util.function.Predicate;

/**
 * Opération SELECT de l'algèbre relationnelle.
 * Filtre une table en gardant uniquement les lignes
 * qui vérifient un prédicat.
 */
public class Select implements Operation {
  final public Predicate<Ligne> condition;

  /**
     * Construit une opération de sélection.
     *
     * @param condition prédicat appliqué à chaque ligne
     */
  Select(Predicate<Ligne> condition) {
    if(condition == null) {
      throw new IllegalArgumentException("Condition null.");
    }
    this.condition = condition;
  }

  /**
     * Applique le SELECT : garde les lignes qui satisfont la condition.
     *
     * @param tables une table d'entrée (une seule)
     * @return une nouvelle table filtrée
     */
  @Override
  public Table appliquer(Table... tables) {
    if (tables == null || tables.length != 1) {
      throw new IllegalArgumentException("Select prend exactement une table.");
    }

    Table resultat = new Table(tables[0].getAttributs());

    tables[0].getLignes().stream()
      .filter(condition)
      .forEach(resultat::ajouterLigne);

    return resultat;
  }
}