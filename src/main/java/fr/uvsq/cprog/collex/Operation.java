package fr.uvsq.cprog.collex;

/**
 * Représente une opération algébrique (SELECT, PROJECT, JOIN, SCAN)
 * appliquée sur une ou plusieurs tables.
 *
 * <p>Chaque opération prend une ou plusieurs tables en entrée et produit
 * une nouvelle table en sortie. L'évaluation doit être pure : l'opération
 * ne doit jamais modifier les tables d'entrée mais retourner un nouvel objet
 * {@link Table} contenant le résultat.</p>
 *
 * <p>Les classes qui implémentent cette interface doivent garantir que
 * l'opération est bien définie : par exemple, une projection doit vérifier
 * que les attributs existent, une jointure doit vérifier la cohérence des
 * clés, etc.</p>
 *
 * <p>Exemples d'opérations implémentant cette interface :</p>
 * <ul>
 *   <li>{@code Select} — sélectionne les lignes respectant un prédicat</li>
 *   <li>{@code Projection} — garde uniquement certains attributs</li>
 *   <li>{@code Join} — joint deux tables sur un attribut commun</li>
 *   <li>{@code Scan} — lit un fichier CSV et retourne une table</li>
 * </ul>
 *
 * @author VotreNom
 */
public interface Operation {

    /**
     * Applique l'opération sur une ou plusieurs tables et retourne le résultat.
     *
     * <p>Le nombre de tables attendues dépend de l'opération :
     * <ul>
     *   <li>SELECT et PROJECT — 1 table en entrée</li>
     *   <li>JOIN — 2 tables en entrée</li>
     *   <li>SCAN — aucune table en entrée (lecture depuis fichier)</li>
     * </ul>
     * </p>
     *
     * @param tables Les tables en entrée de l'opération
     * @return Une nouvelle table contenant le résultat
     * @throws IllegalArgumentException si le nombre ou la structure des tables
     *         ne correspond pas à l'opération
     */
    Table appliquer(Table... tables);
}
