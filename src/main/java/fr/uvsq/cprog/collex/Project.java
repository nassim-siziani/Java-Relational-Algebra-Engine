package fr.uvsq.cprog.collex;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implémentation de l'opération PROJECT de l'algèbre relationnelle.
 * Cette opération conserve uniquement une liste d'attributs spécifiés.
 */
public final class Project implements Operation {

  /** Liste des attributs demandés dans la projection (tels que fournis). */
  private final List<String> attributs;

  /**
   * Construit une opération de projection.
   *
   * @param attributs liste des noms d'attributs à garder (peut contenir "*")
   */
  public Project(List<String> attributs) {
    if (attributs == null || attributs.isEmpty()) {
      throw new IllegalArgumentException(
          "La projection doit contenir au moins un attribut.");
    }
    // On garde la version trim pour l'affichage / debug,
    // la normalisation (lower-case) se fera à l'application.
    this.attributs = attributs.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .collect(Collectors.toList());
  }

  @Override
  public Table appliquer(Table... tables) {
    if (tables == null || tables.length != 1) {
      throw new IllegalArgumentException(
          "PROJECT doit recevoir exactement une table en entrée.");
    }

    Table entree = tables[0];

    // Cas particulier : PROJ *  → on ne change pas la table
    if (attributs.size() == 1 && "*".equals(attributs.get(0))) {
      return entree;
    }

    // Normalisation des noms d'attributs en lower-case pour correspondre
    // aux noms stockés dans Table/Scan.
    List<String> attrsNorm = attributs.stream()
        .map(a -> a.trim().toLowerCase())
        .collect(Collectors.toList());

    // Vérification que tous les attributs existent dans la table d'entrée
    for (String attr : attrsNorm) {
      if (!entree.contientAttribut(attr)) {
        throw new IllegalArgumentException(
            "Attribut inconnu dans la projection : " + attr);
      }
    }

    // Création de la table résultat avec uniquement les colonnes projetées
    Table resultat = new Table(attrsNorm);

    // Pour chaque ligne de la table d'entrée, on construit une nouvelle ligne
    // contenant uniquement les attributs projetés.
    entree.getLignes().forEach(ligne -> {
      Ligne nouvelle = new Ligne();
      for (String attr : attrsNorm) {
        nouvelle.setValeur(attr, ligne.getValeur(attr));
      }
      resultat.ajouterLigne(nouvelle);
    });

    return resultat;
  }
}
