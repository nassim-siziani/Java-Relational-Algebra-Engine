package fr.uvsq.cprog.collex;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class SelectTest {

    @Test
    public void testSelectionSimple() {
        // Table d'entrée
        Table t = new Table();
        Ligne l1 = new Ligne();
        l1.setValeur("age", "20");

        Ligne l2 = new Ligne();
        l2.setValeur("age", "30");

        t.ajouterLigne(l1);
        t.ajouterLigne(l2);

        // prédicat : garder les lignes où age < 25
        Predicate<Ligne> condition = ligne -> 
            Integer.parseInt(ligne.getValeur("age")) < 25;

        Select select = new Select(condition);

        // Exécution
        Table res = select.appliquer(t);

        // Vérifications
        assertEquals(1, res.getLignes().size());
        assertEquals("20", res.getLignes().get(0).getValeur("age"));

        // Vérifier que la table d'origine n'est pas modifiée
        assertEquals(2, t.getLignes().size());
    }


    @Test
    public void testSelectionAucuneLigne() {
        Table t = new Table();
        Ligne l = new Ligne();
        l.setValeur("age", "40");
        t.ajouterLigne(l);

        Predicate<Ligne> cond = ligne -> false;

        Select s = new Select(cond);
        Table res = s.appliquer(t);

        assertEquals(0, res.getLignes().size());
    }


    @Test
    public void testConstructeurNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Select(null)
        );
    }
}
