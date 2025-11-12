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

    /* 
    * Mètode per generar successors
    */
    protected List<Map.Entry<Mapa, Moviment>> successors(Mapa actual) {
        List<Map.Entry<Mapa, Moviment>> res = new ArrayList<>();

        for (int aid = 1; aid <= actual.getAgents().size(); aid++) {
            for (Direccio d : Direccio.values()) {
                try {
                    Moviment mov = new Moviment(aid, d, false);
                    Mapa nou = actual.mou(mov);
                    res.add(new AbstractMap.SimpleEntry<>(nou, mov));
                } catch (IllegalArgumentException e) {
                    // moviment invàlid
                }
            }
        }

        return res;
    }
    
    /*
    * Fa un get amb la profunditat de l'estat actual
    */
    protected int getProfunditat(Mapa est, Map<Mapa, Mapa> parents) {
        int prof = 0;
        Mapa p = parents.get(est);
        while (p != null) {
            prof++;
            p = parents.get(p);
        }
        return prof;
    }
    
     /*
     * Control de cicles per la branca actual (quan no es fa servir LNT).
     */
    protected boolean existeixEnBranca(Mapa estat, Node node) {
        Node actual = node;
        while (actual != null) {
            if (actual.estat.equals(estat)){
                return true;
            }
            actual = actual.pare;
        }
        return false;
    }
    
    /**
     * Reconstrueix el camí del node final fins a l’inicial.
     */
    protected List<Moviment> reconstruirCami(Node nodeFinal) {
        LinkedList<Moviment> cami = new LinkedList<>();
        Node actual = nodeFinal;
        while (actual.pare != null) {
            cami.addFirst(actual.accio);
            actual = actual.pare;
        }
        return cami;
    }

    /**
     * Comprova si hi ha un cicle al camí (sense LNT).
     */
    protected boolean comprobarCicle(Node node) {
        Mapa estatActual = node.estat;
        Node pare = node.pare;
        while (pare != null) {
            if (pare.estat.equals(estatActual)) {
                return true;
            }
            pare = pare.pare;
        }
        return false;
    }
}
