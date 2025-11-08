package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaDFS extends Cerca {

    public CercaDFS(boolean usarLNT) { 
        super(usarLNT); 
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // Frontier: pila LIFO per DFS
        Deque<Mapa> frontier = new ArrayDeque<>();
        
        // Mapes per reconstruir el camí
        Map<Mapa, Mapa> parents = new HashMap<>();
        Map<Mapa, Moviment> accions = new HashMap<>();
        
        // Llista tancada (per evitar repetir estats)
        Set<Mapa> visitats = usarLNT ? new HashSet<>() : null;

        // Afegir estat inicial
        frontier.push(inicial);
        if (usarLNT) visitats.add(inicial);

        // Bucle principal DFS
        while (!frontier.isEmpty()) {
            Mapa actual = frontier.pop();  // Treure el darrer estat
            rc.incNodesExplorats();

            // Si hem arribat a la meta
            if (actual.esMeta()) {
                // Reconstruir camí
                List<Moviment> cami = new ArrayList<>();
                Mapa est = actual;
                while (parents.containsKey(est)) {
                    cami.add(0, accions.get(est));
                    est = parents.get(est);
                }
                rc.setCami(cami);
                return;
            }

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                // Evitar repetir estats si fem servir llista tancada
                if (usarLNT && visitats.contains(nou)) {
                    rc.incNodesTallats();
                    continue;
                }

                // Afegir a frontier i marcar com visitat
                frontier.push(nou);
                if (usarLNT) visitats.add(nou);

                // Guardar pare i moviment
                parents.put(nou, actual);
                accions.put(nou, mov);

                rc.updateMemoria(frontier.size() + (usarLNT ? visitats.size() : 0));
            }
        }

        // Si no trobem meta
        rc.setCami(null);
    }
}
