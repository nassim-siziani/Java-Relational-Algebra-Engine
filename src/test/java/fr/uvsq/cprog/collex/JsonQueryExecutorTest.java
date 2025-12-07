package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonQueryExecutorTest {

  @Test
  void executeReqSelUsersDepuisDossierTest4() throws Exception {
    // Répertoire de test décrit dans l'énoncé
    Path dir = Path.of("test1");
    Path usersCsv = dir.resolve("users.csv");
    Path reqJson = dir.resolve("reqSelUsers.json");

    // Vérifications préalables (évite les faux négatifs si les fichiers manquent)
    assertTrue(Files.exists(dir), "Le répertoire test1 doit exister à la racine du projet");
    assertTrue(Files.exists(usersCsv), "Le fichier test1/users.csv doit exister");
    assertTrue(Files.exists(reqJson), "Le fichier test1/reqSelUsers.json doit exister");

    // Exécution de la requête JSON
    JsonQueryExecutor executor = new JsonQueryExecutor();
    Table res = executor.execute(reqJson.toString());

    // On vérifie que la requête a bien été évaluée

    // 1) La projection doit donner uniquement la colonne "name"
    assertEquals(List.of("name"), res.getAttributs(),
        "La requête reqSelUsers.json doit projeter uniquement la colonne 'name'");

    // 2) Les utilisateurs avec age < 25 sont Alice (22) et David (19)
    List<Ligne> lignes = res.getLignes();
    assertEquals(2, lignes.size(), "On attend 2 lignes (Alice et David)");

    String n1 = lignes.get(0).getValeur("name");
    String n2 = lignes.get(1).getValeur("name");

    // On vérifie l'ensemble des valeurs, sans supposer l'ordre
    assertTrue(
        (n1.equals("Alice") && n2.equals("David"))
            || (n1.equals("David") && n2.equals("Alice")),
        "Les noms attendus sont 'Alice' et 'David'"
    );
  }
}
