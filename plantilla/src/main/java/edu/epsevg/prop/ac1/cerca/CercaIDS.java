package edu.epsevg.prop.ac1.cerca;


import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaIDS extends Cerca {

    private static final int MAX_PROFUNDITAT = 50;

    public CercaIDS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        for (int limit = 0; limit <= MAX_PROFUNDITAT; limit++) {
            
            // Cada iteració reinicia la cerca amb un límit major
            Set<Mapa> visitats = usarLNT ? new HashSet<>() : null;
            Map<Mapa, Mapa> parents = new HashMap<>();
            Map<Mapa, Moviment> accions = new HashMap<>();

            Mapa resultat = dfsLimitat(inicial, limit, visitats, parents, accions, rc);

            if (resultat != null) {
                // Reconstruir camí des de la meta fins a l'inicial
                List<Moviment> cami = new ArrayList<>();
                Mapa est = resultat;
                while (parents.containsKey(est)) {
                    cami.add(0, accions.get(est));
                    est = parents.get(est);
                }
                rc.setCami(cami);
                return;
            }
        }
        // No s'ha trobat cap solució
        rc.setCami(null);
    }

    private Mapa dfsLimitat(Mapa actual, int limit, Set<Mapa> visitats,
                            Map<Mapa, Mapa> parents, Map<Mapa, Moviment> accions, ResultatCerca rc) {

        Deque<Map.Entry<Mapa, Integer>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>(actual, 0));
        if (usarLNT && visitats != null) visitats.add(actual);

        while (!stack.isEmpty()) {
            Map.Entry<Mapa, Integer> entry = stack.pop();
            Mapa mapaActual = entry.getKey();
            int profunditat = entry.getValue();
            rc.incNodesExplorats();

            if (mapaActual.esMeta()) {
                return mapaActual;
            }

            if (profunditat >= limit) {
                rc.incNodesTallats();
                continue;
            }

            for (Map.Entry<Mapa, Moviment> succ : successors(mapaActual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                // Control de cicles segons el mode
                if (!usarLNT && existeixEnBranca(nou, mapaActual, parents)) {
                    rc.incNodesTallats();
                    continue;
                }
                if (usarLNT && visitats.contains(nou)) {
                    rc.incNodesTallats();
                    continue;
                }

                stack.push(new AbstractMap.SimpleEntry<>(nou, profunditat + 1));
                if (usarLNT && visitats != null) visitats.add(nou);
                parents.put(nou, mapaActual);
                accions.put(nou, mov);
                rc.updateMemoria(stack.size() + (usarLNT && visitats != null ? visitats.size() : 0));
            }
        }

        return null;
    }
}
