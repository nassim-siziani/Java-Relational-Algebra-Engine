package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class LigneTest {

  @Test
  void setEtGetValeurFonctionnentCorrectement() {
    Ligne l = new Ligne();

    l.setValeur("id", "1");
    l.setValeur("name", "Alice");

    assertEquals("1", l.getValeur("id"));
    assertEquals("Alice", l.getValeur("name"));
  }

  @Test
  void getValeurRenvoieNullSiAttributAbsent() {
    Ligne l = new Ligne();

    l.setValeur("id", "1");

    assertNull(l.getValeur("age"),
        "Un attribut non défini doit retourner null");
  }

  @Test
  void getAttributsRetourneLaMapDesColonnes() {
    Ligne l = new Ligne();

    l.setValeur("id", "1");
    l.setValeur("name", "Bob");

    Map<String, String> attrs = l.getAttributs();

    assertTrue(attrs.containsKey("id"));
    assertTrue(attrs.containsKey("name"));
    assertEquals("1", attrs.get("id"));
    assertEquals("Bob", attrs.get("name"));
    assertEquals(2, attrs.size());
  }

  @Test
  void ecrasementDeValeurFonctionne() {
    Ligne l = new Ligne();

    l.setValeur("id", "1");
    l.setValeur("id", "2");

    assertEquals("2", l.getValeur("id"),
        "La dernière valeur affectée doit écraser la précédente");
  }
}
