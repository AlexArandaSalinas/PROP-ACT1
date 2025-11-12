package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaDFS extends Cerca {
    private static final int MAX_PROFUNDITAT = 50;

    public CercaDFS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {

        // Pila de nodes per DFS
        Deque<Node> lno = new ArrayDeque<>();
        Set<Mapa> lnt = usarLNT ? new HashSet<>() : null;

        // Crear el node inicial
        Node inicialNode = new Node(inicial, null, null, 0, 0);
        lno.push(inicialNode);
        if (usarLNT){
            lnt.add(inicial);
        }

        while (!lno.isEmpty()) {
            Node actualNode = lno.pop();
            Mapa actual = actualNode.estat;
            int profunditat = actualNode.depth;

            rc.incNodesExplorats();

            // Si és meta reconstruir camí
            if (actual.esMeta()) {
                List<Moviment> cami = new ArrayList<>();
                Node n = actualNode;
                while (n.pare != null) {
                    cami.add(0, n.accio);
                    n = n.pare;
                }
                rc.setCami(cami);
                return;
            }

            // Limitar profunditat si no es LNT
            if (!usarLNT && profunditat >= MAX_PROFUNDITAT) {
                rc.incNodesTallats();
                continue;
            }

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                // Control de cicles sense LNT
                if (!usarLNT && existeixEnBranca(nou, actualNode)) {
                    rc.incNodesTallats();
                    continue;
                }

                // Control de cicles amb LNT
                if (usarLNT && lnt.contains(nou)) {
                    rc.incNodesTallats();
                    continue;
                }

                Node nouNode = new Node(nou, actualNode, mov, profunditat + 1, actualNode.g + 1);
                lno.push(nouNode);
                if (usarLNT){
                    lnt.add(nou);
                }
            }

            rc.updateMemoria(lno.size() + (usarLNT ? lnt.size() : 0));
        }

        // No trobem meta
        rc.setCami(null);
    }
}
