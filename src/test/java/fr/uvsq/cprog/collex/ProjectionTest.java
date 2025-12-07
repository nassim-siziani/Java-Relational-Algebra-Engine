package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProjectTest {

  @Test
  void projectionSimpleSurQuelquesColonnes() {
    // Table d'entrée
    Table t = new Table("Users", List.of("id", "name", "age"));

    Ligne l1 = new Ligne();
    l1.setValeur("id", "1");
    l1.setValeur("name", "Alice");
    l1.setValeur("age", "22");

    Ligne l2 = new Ligne();
    l2.setValeur("id", "2");
    l2.setValeur("name", "Bob");
    l2.setValeur("age", "30");

    t.ajouterLigne(l1);
    t.ajouterLigne(l2);

    // PROJ name,age (avec un mélange de casse)
    Project proj = new Project(List.of("Name", "AGE"));
    Table res = proj.appliquer(t);

    // On doit avoir uniquement les colonnes demandées, en minuscules
    assertEquals(List.of("name", "age"), res.getAttributs());

    assertEquals(2, res.getLignes().size());

    Ligne r1 = res.getLignes().get(0);
    assertEquals("Alice", r1.getValeur("name"));
    assertEquals("22", r1.getValeur("age"));
    assertNull(r1.getValeur("id"));

    Ligne r2 = res.getLignes().get(1);
    assertEquals("Bob", r2.getValeur("name"));
    assertEquals("30", r2.getValeur("age"));
    assertNull(r2.getValeur("id"));
  }

  @Test
  void projectionEtoileRetourneLaTableOriginale() {
    Table t = new Table("Users", List.of("id", "name"));

    Ligne l = new Ligne();
    l.setValeur("id", "1");
    l.setValeur("name", "Alice");
    t.ajouterLigne(l);

    Project projAll = new Project(List.of("*"));
    Table res = projAll.appliquer(t);

    // Ici, on a choisi de renvoyer la même instance (comportement pratique)
    assertSame(t, res, "PROJ * doit renvoyer la table d'entrée telle quelle");
  }

  @Test
  void projectionSurAttributInexistantDeclencheUneException() {
    Table t = new Table("Users", List.of("id", "name"));

    Project proj = new Project(List.of("age")); // n'existe pas

    assertThrows(IllegalArgumentException.class,
        () -> proj.appliquer(t),
        "PROJ sur un attribut inexistant doit lever une exception");
  }
}
