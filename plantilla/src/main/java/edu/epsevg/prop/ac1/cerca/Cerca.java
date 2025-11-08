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
}
