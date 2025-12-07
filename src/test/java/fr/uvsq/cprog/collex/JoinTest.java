package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

import fr.uvsq.cprog.collex.Join;
import fr.uvsq.cprog.collex.Table;

class JoinTest {

 @Test
void joinAvecAttributsAmbigusPrefixeCorrectement() {
  // Table Users : id, nom
  Table users = new Table("Users", List.of("id", "nom"));

  Ligne u1 = new Ligne();
  u1.setValeur("id", "1");
  u1.setValeur("nom", "Alice");

  Ligne u2 = new Ligne();
  u2.setValeur("id", "2");
  u2.setValeur("nom", "Bob");

  users.ajouterLigne(u1);
  users.ajouterLigne(u2);

  // Table Compagnie : manager, nom
  Table compagnie = new Table("Compagnie", List.of("manager", "nom"));

  Ligne c1 = new Ligne();
  c1.setValeur("manager", "1");
  c1.setValeur("nom", "ACME");

  Ligne c2 = new Ligne();
  c2.setValeur("manager", "3");
  c2.setValeur("nom", "Other");

  compagnie.ajouterLigne(c1);
  compagnie.ajouterLigne(c2);

  // JOIN Users Compagnie id manager
  Join join = new Join("id", "manager");
  Table res = join.appliquer(users, compagnie);

  assertEquals(
      List.of("id", "users.nom", "manager", "compagnie.nom"),
      res.getAttributs()
  );

  assertEquals(1, res.getLignes().size());

  Ligne r = res.getLignes().get(0);
  assertEquals("1", r.getValeur("id"));
  assertEquals("Alice", r.getValeur("users.nom"));
  assertEquals("1", r.getValeur("manager"));
  assertEquals("ACME", r.getValeur("compagnie.nom"));
}

  @Test
  void joinSurMemeNomAttributDeJointureNeCreePasDeDoublon() {
    // Table T1 : id, valeur
    Table t1 = new Table("T1", List.of("id", "valeur"));
    Ligne a = new Ligne();
    a.setValeur("id", "10");
    a.setValeur("valeur", "A");
    t1.ajouterLigne(a);

    // Table T2 : id, info
    Table t2 = new Table("T2", List.of("id", "info"));
    Ligne b = new Ligne();
    b.setValeur("id", "10");
    b.setValeur("info", "B");
    t2.ajouterLigne(b);

    // JOIN T1 T2 id id
    Join join = new Join("id", "id");
    Table res = join.appliquer(t1, t2);

    // Colonnes attendues :
    // - id (une seule fois)
    // - valeur
    // - info
    assertEquals(
        List.of("id", "valeur", "info"),
        res.getAttributs()
    );

    assertEquals(1, res.getLignes().size());
    Ligne r = res.getLignes().get(0);

    assertEquals("10", r.getValeur("id"));
    assertEquals("A", r.getValeur("valeur"));
    assertEquals("B", r.getValeur("info"));
  }
}
