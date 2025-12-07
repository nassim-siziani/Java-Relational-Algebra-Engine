package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class TableTest {

  @Test
  void attributsSontNormalisesEnMinuscules() {
    Table t = new Table("Users", List.of("ID", "Name", "Age"));

    assertEquals(List.of("id", "name", "age"), t.getAttributs(),
        "Les attributs doivent être stockés en minuscules");
  }

  @Test
  void contientAttributIgnoreLaCasse() {
    Table t = new Table("Users", List.of("Id", "Name"));

    assertTrue(t.contientAttribut("id"));
    assertTrue(t.contientAttribut("ID"));
    assertTrue(t.contientAttribut("Id"));
    assertFalse(t.contientAttribut("age"));
  }

  @Test
  void ajouterLigneEtToStringProduisentUnCsvPropre() {
    Table t = new Table("Users", List.of("id", "name"));

    Ligne l1 = new Ligne();
    l1.setValeur("id", "1");
    l1.setValeur("name", "Alice");

    Ligne l2 = new Ligne();
    l2.setValeur("id", "2");
    l2.setValeur("name", "Bob");

    t.ajouterLigne(l1);
    t.ajouterLigne(l2);

    String csv = t.toString().trim(); // pour ignorer un éventuel \n final

    String attendu = """
        id,name
        1,Alice
        2,Bob""";

    assertEquals(attendu, csv);
  }
}
