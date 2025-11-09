package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaBFS extends Cerca {

    public CercaBFS(boolean usarLNT) { 
        super(usarLNT); 
    }

    //LNO = frontier
    //LNT = visitats
    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        // Llista de nodes oberts (LNO) . frontier
        Queue<Mapa> frontier = new ArrayDeque<>();
        
        Map<Mapa, Mapa> parents = new HashMap<>();
        Map<Mapa, Moviment> accions = new HashMap<>();
        
        // LNT: map de visitats amb profunditat
        Map<Mapa, Integer> visitatsLNT = usarLNT ? new HashMap<>() : null;

        frontier.add(inicial);
        if (usarLNT) visitatsLNT.put(inicial, 0);

        while (!frontier.isEmpty()) {
            Mapa actual = frontier.poll();
            rc.incNodesExplorats();

            if (actual.esMeta()) {
                List<Moviment> cami = new ArrayList<>();
                Mapa est = actual;
                while (parents.containsKey(est)) {
                    cami.add(0, accions.get(est));
                    est = parents.get(est);
                }
                rc.setCami(cami);
                return;
            }

            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                boolean descartar = false;

                if (usarLNT) {
                    int profunditatActual = parents.containsKey(actual) ? 1 + getProfunditat(actual, parents) : 0;
                    if (visitatsLNT.containsKey(nou) && visitatsLNT.get(nou) <= profunditatActual) {
                        descartar = true;
                    } else {
                        visitatsLNT.put(nou, profunditatActual);
                    }
                } else {
                    // Control de cicles a la branca actual
                    if (existeixEnBranca(nou, actual, parents)) {
                        descartar = true;
                    }
                }

                if (descartar) {
                    rc.incNodesTallats();
                    continue;
                }

                frontier.add(nou);
                parents.put(nou, actual);
                accions.put(nou, mov);
                rc.updateMemoria(frontier.size() + (usarLNT ? visitatsLNT.size() : 0));
            }
        }

        rc.setCami(null);
    }
}
