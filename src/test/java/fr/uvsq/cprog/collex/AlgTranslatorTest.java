package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AlgTranslatorTest {

  @Test
  void translateReqProjSelDansDossierTest1() throws Exception {
    // On travaille dans le répertoire test1 (comme dans l'énoncé avec test3)
    Path dir = Path.of("test1");
    assertTrue(Files.exists(dir), "Le répertoire test1 doit exister à la racine du projet");

    // Fichier .alg que l'on va traduire : test1/reqProjSel.alg
    Path algFile = dir.resolve("reqProjSel.alg");

    // Contenu conforme à l'exemple du TP :
    //
    // users: users.csv
    //
    // R1 = SELECT users age < 25
    // R  = PROJ R1 name
    //
    // (orders n'est pas nécessaire ici, on se limite à users)
    String algContent = """
        users: users.csv

        R1 = SELECT users age < 25
        R = PROJ R1 name
        """;

    // On (ré)écrit le .alg dans test1
    Files.writeString(algFile, algContent);

    // Appel du traducteur .alg -> .json
    AlgTranslator translator = new AlgTranslator();
    Path jsonPath = translator.translate(algFile.toString());

    // Le JSON doit être généré à côté, sous le nom reqProjSel.json
    Path expectedJsonPath = dir.resolve("reqProjSel.json");
    assertEquals(expectedJsonPath.toAbsolutePath(), jsonPath.toAbsolutePath());
    assertTrue(Files.exists(jsonPath), "Le fichier JSON généré doit exister dans test1");

    // On lit et on parse le JSON généré
    String jsonContent = Files.readString(jsonPath);
    JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();

    // ---------- Vérifications ----------

    // A) "tables" doit contenir users -> users.csv
    assertTrue(root.has("tables"), "Le JSON doit contenir un objet 'tables'");
    JsonObject tables = root.getAsJsonObject("tables");

    assertEquals("users.csv", tables.get("users").getAsString());

    // B) "plan" doit représenter :
    // PROJECT(columns=["name"], input=SELECT(...))
    assertTrue(root.has("plan"), "Le JSON doit contenir un objet 'plan'");
    JsonObject plan = root.getAsJsonObject("plan");

    // type = PROJECT
    assertEquals("PROJECT", plan.get("type").getAsString());

    // columns = ["name"]
    JsonArray cols = plan.getAsJsonArray("columns");
    assertNotNull(cols);
    assertEquals(1, cols.size());
    assertEquals("name", cols.get(0).getAsString());

    // input = SELECT
    JsonObject selectNode = plan.getAsJsonObject("input");
    assertEquals("SELECT", selectNode.get("type").getAsString());

    // prédicat = "age < 25"
    assertEquals("age < 25", selectNode.get("predicate").getAsString());

    // input du SELECT = SCAN users
    JsonObject scanNode = selectNode.getAsJsonObject("input");
    assertEquals("SCAN", scanNode.get("type").getAsString());
    assertEquals("users", scanNode.get("table").getAsString());
  }
}
