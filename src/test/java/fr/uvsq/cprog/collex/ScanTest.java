package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScanTest {

  @Test
  void scanLitUsersCsvDepuisDossierTest1() throws Exception {
    // Chemin vers test1/users.csv à la racine du projet
    Path csvPath = Path.of("test1", "users.csv");

    assertTrue(Files.exists(csvPath),
        "Le fichier test1/users.csv doit exister pour ce test");

    // Exécute le scan
    Scan scan = new Scan(csvPath.toString());
    Table table = scan.appliquer();

    // Vérifie le nom de la table (basename du fichier -> "users")
    assertEquals("users", table.getNom());

    // Vérifie les colonnes
    assertEquals(List.of("id", "name", "age"), table.getAttributs());

    // Vérifie le nombre de lignes
    List<Ligne> lignes = table.getLignes();
    assertEquals(4, lignes.size(), "On doit avoir exactement 4 lignes");

    // 1ère ligne
    Ligne l1 = lignes.get(0);
    assertEquals("1", l1.getValeur("id"));
    assertEquals("Alice", l1.getValeur("name"));
    assertEquals("22", l1.getValeur("age"));

    // 4ème ligne
    Ligne l4 = lignes.get(3);
    assertEquals("4", l4.getValeur("id"));
    assertEquals("David", l4.getValeur("name"));
    assertEquals("19", l4.getValeur("age"));
  }
}
