package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

/**
 * Test unitaire de la classe Ligne.
 */
public class LigneTest {  
  /**
   * Vérifie la création et la lecture de valeurs.
   */
  @Test
  public void testSetEtGetValeur() {
      Ligne ligne = new Ligne();
      ligne.setValeur("id", "1");
      ligne.setValeur("name", "Alice");  
      assertEquals("1", ligne.getValeur("id"));
      assertEquals("Alice", ligne.getValeur("name"));
  }  
  /**
   * Vérifie la construction d'une ligne à partir d'une map existante.
   */
  @Test
  public void testConstructeurAvecMap() {
      Map<String, String> data = new HashMap<>();
      data.put("age", "22");
      Ligne ligne = new Ligne(data);  
      assertEquals("22", ligne.getValeur("age"));
  }  
  /**
   * Vérifie la méthode toString.
   */
  @Test
  public void testToString() {
      Ligne ligne = new Ligne();
      ligne.setValeur("id", "1");
      assertTrue(ligne.toString().contains("id=1"));
  }
}
