package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class SelectTest {

  @Test
  void selectFiltreCorrectementLesLignes() {
    // Table d'entrée
    Table t = new Table("Users", List.of("id", "age"));

    Ligne l1 = new Ligne();
    l1.setValeur("id", "1");
    l1.setValeur("age", "22");

    Ligne l2 = new Ligne();
    l2.setValeur("id", "2");
    l2.setValeur("age", "30");

    Ligne l3 = new Ligne();
    l3.setValeur("id", "3");
    l3.setValeur("age", "19");

    t.ajouterLigne(l1);
    t.ajouterLigne(l2);
    t.ajouterLigne(l3);

    // Predicate : garder uniquement les lignes avec age < 25
    Predicate<Ligne> ageMoinsDe25 = ligne -> {
      String val = ligne.getValeur("age");
      return val != null && Integer.parseInt(val) < 25;
    };

    Select select = new Select(ageMoinsDe25);
    Table res = select.appliquer(t);

    // Les colonnes doivent être identiques
    assertEquals(List.of("id", "age"), res.getAttributs());

    // On ne garde que l1 (22) et l3 (19)
    assertEquals(2, res.getLignes().size());

    Ligne r1 = res.getLignes().get(0);
    Ligne r2 = res.getLignes().get(1);

    assertEquals("1", r1.getValeur("id"));
    assertEquals("22", r1.getValeur("age"));

    assertEquals("3", r2.getValeur("id"));
    assertEquals("19", r2.getValeur("age"));
  }

  @Test
  void selectSansAucunMatchProduitZeroLigne() {
    Table t = new Table("Users", List.of("id", "age"));

    Ligne l1 = new Ligne();
    l1.setValeur("id", "1");
    l1.setValeur("age", "40");

    t.ajouterLigne(l1);

    // Predicate : age < 10 → ne matche personne
    Predicate<Ligne> ageMoinsDe10 = ligne -> {
      String val = ligne.getValeur("age");
      return val != null && Integer.parseInt(val) < 10;
    };

    Select select = new Select(ageMoinsDe10);
    Table res = select.appliquer(t);

    // Même en-tête
    assertEquals(List.of("id", "age"), res.getAttributs());
    // Mais aucune ligne
    assertEquals(0, res.getLignes().size());
  }
}
