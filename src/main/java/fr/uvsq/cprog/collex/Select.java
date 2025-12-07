package fr.uvsq.cprog.collex;

import java.util.function.Predicate;

/**
 * Opération SELECT de l'algèbre relationnelle.
 * Filtre une table en gardant uniquement les lignes
 * qui vérifient un prédicat.
 */
public final class Select implements Operation {

  /** Condition appliquée à chaque ligne. */
  private final Predicate<Ligne> condition;

  /**
   * Construit une opération de sélection.
   *
   * @param condition prédicat appliqué à chaque ligne
   */
  public Select(Predicate<Ligne> condition) {
    if (condition == null) {
      throw new IllegalArgumentException("La condition de SELECT ne peut pas être nulle.");
    }
    this.condition = condition;
  }

  @Override
  public Table appliquer(Table... tables) {
    if (tables == null || tables.length != 1) {
      throw new IllegalArgumentException(
          "SELECT doit recevoir exactement une table en entrée.");
    }

    Table entree = tables[0];
    Table resultat = new Table(entree.getAttributs());

    entree.getLignes().stream()
        .filter(condition)
        .forEach(resultat::ajouterLigne);

    return resultat;
  }
}
