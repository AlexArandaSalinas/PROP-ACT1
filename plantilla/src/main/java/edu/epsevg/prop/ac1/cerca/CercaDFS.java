package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaDFS extends Cerca {
    private static final int MAX_PROFUNDITAT = 50;

    public CercaDFS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // Stack per DFS
        Deque<Map.Entry<Mapa, Integer>> stack = new ArrayDeque<>();
        Map<Mapa, Mapa> parents = new HashMap<>();
        Map<Mapa, Moviment> accions = new HashMap<>();
        Set<Mapa> visitats = usarLNT ? new HashSet<>() : null;

        stack.push(new AbstractMap.SimpleEntry<>(inicial, 0));
        if (usarLNT) visitats.add(inicial);

        while (!stack.isEmpty()) {
            Map.Entry<Mapa, Integer> entry = stack.pop();
            Mapa actual = entry.getKey();
            int profunditat = entry.getValue();
            rc.incNodesExplorats();

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

            // Limitar profunditat
            if (!usarLNT && profunditat >= MAX_PROFUNDITAT) {
                rc.incNodesTallats();
                continue;
            }

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                if (usarLNT && visitats.contains(nou)) {
                    rc.incNodesTallats();
                    continue;
                }

                stack.push(new AbstractMap.SimpleEntry<>(nou, profunditat + 1));
                if (usarLNT) visitats.add(nou);
                parents.put(nou, actual);
                accions.put(nou, mov);
                rc.updateMemoria(stack.size() + (usarLNT ? visitats.size() : 0));
            }
        }

        // Si no trobem meta
        rc.setCami(null);
    }
}
