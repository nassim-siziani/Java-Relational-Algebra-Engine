package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Tests unitaires pour la classe Table.
 */
public class TableTest {

  /** Vérifie l’ajout de lignes et la récupération d’attributs. */
  @Test
  public void testAjoutEtLecture() {
    Table table = new Table();
    table.getAttributs().addAll(List.of("id", "name"));

    Ligne ligne1 = new Ligne();
    ligne1.setValeur("id", "1");
    ligne1.setValeur("name", "Alice");

    table.ajouterLigne(ligne1);

    assertEquals(1, table.getLignes().size());
    assertEquals("Alice", table.getLignes().get(0).getValeur("name"));
  }

  /** Vérifie la création de table avec liste d’attributs. */
  @Test
  public void testConstructeurAvecAttributs() {
    List<String> attributs = List.of("id", "age");
    Table table = new Table(attributs);
    assertEquals(attributs, table.getAttributs());
    assertTrue(table.getLignes().isEmpty());
  }

  /** Vérifie le format du toString(). */
  @Test
  public void testToString() {
    Table table = new Table();
    table.getAttributs().addAll(List.of("id", "name"));

    Map<String, String> map = new HashMap<>();
    map.put("id", "1");
    map.put("name", "Bob");
    table.ajouterLigne(new Ligne(map));

    String texte = table.toString();
    assertTrue(texte.contains("id"));
    assertTrue(texte.contains("Bob"));
  }
}
