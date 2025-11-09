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

            // Reiniciar estructuras para cada iteración
            Set<Mapa> visitats = usarLNT ? new HashSet<>() : null;

            Node resultat = dfsLimitat(
                    new Node(inicial, null, null, 0, 0),
                    limit,
                    visitats,
                    rc
            );

            if (resultat != null) {
                // Reconstruir camí
                List<Moviment> cami = new ArrayList<>();
                Node n = resultat;
                while (n.pare != null) {
                    cami.add(0, n.accio);
                    n = n.pare;
                }
                rc.setCami(cami);
                return;
            }
        }

        // No trobem cap solució
        rc.setCami(null);
    }

    /**
     * Cerca DFS amb límit de profunditat
     */
    private Node dfsLimitat(Node inicialNode, int limit, Set<Mapa> visitats, ResultatCerca rc) {

        Deque<Node> pila = new ArrayDeque<>();
        pila.push(inicialNode);
        if (usarLNT && visitats != null) visitats.add(inicialNode.estat);

        while (!pila.isEmpty()) {
            Node actualNode = pila.pop();
            Mapa actual = actualNode.estat;
            rc.incNodesExplorats();

            // Si trobem meta
            if (actual.esMeta()) {
                return actualNode;
            }

            // Tall per profunditat
            if (actualNode.depth >= limit) {
                rc.incNodesTallats();
                continue;
            }

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                // Control de cicles segons mode
                if (!usarLNT && existeixEnBranca(nou, actualNode)) {
                    rc.incNodesTallats();
                    continue;
                }
                if (usarLNT && visitats.contains(nou)) {
                    rc.incNodesTallats();
                    continue;
                }

                Node nouNode = new Node(nou, actualNode, mov, actualNode.depth + 1, actualNode.g + 1);
                pila.push(nouNode);
                if (usarLNT && visitats != null) visitats.add(nou);
                rc.updateMemoria(pila.size() + (usarLNT && visitats != null ? visitats.size() : 0));
            }
        }

        return null; // No hi ha solució dins d’aquest límit
    }
}
