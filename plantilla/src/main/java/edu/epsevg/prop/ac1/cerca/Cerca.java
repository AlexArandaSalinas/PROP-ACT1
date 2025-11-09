package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public abstract class Cerca {
    protected final boolean usarLNT;

    public Cerca(boolean usarLNT) {
        this.usarLNT = usarLNT;
    }

    public abstract void ferCerca(Mapa inicial, ResultatCerca rc);

    // ==============================
    // Mètode per generar successors
    // ==============================
    protected List<Map.Entry<Mapa, Moviment>> successors(Mapa actual) {
        List<Map.Entry<Mapa, Moviment>> res = new ArrayList<>();

        for (int aid = 1; aid <= actual.getAgents().size(); aid++) {
            for (Direccio d : Direccio.values()) {
                try {
                    Moviment mov = new Moviment(aid, d, false);
                    Mapa nou = actual.mou(mov);
                    res.add(new AbstractMap.SimpleEntry<>(nou, mov));
                } catch (IllegalArgumentException e) {
                    // moviment invàlid → mur, porta tancada, col·lisió
                }
            }
        }

        return res;
    }
    
    protected int getProfunditat(Mapa est, Map<Mapa, Mapa> parents) {
        int prof = 0;
        Mapa p = parents.get(est);
        while (p != null) {
            prof++;
            p = parents.get(p);
        }
        return prof;
    }

    protected boolean existeixEnBranca(Mapa nou, Mapa actual, Map<Mapa, Mapa> parents) {
        Mapa p = actual;
        while (parents.containsKey(p)) {
            if (p.equals(nou)) return true;
            p = parents.get(p);
        }
        return false;
    }
     /**
     * Control de cicles per la branca actual (quan no es fa servir LNT).
     */
    protected boolean existeixEnBranca(Mapa estat, Node node) {
        Node actual = node;
        while (actual != null) {
            if (actual.estat.equals(estat)) return true;
            actual = actual.pare;
        }
        return false;
    }
}
