package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaBFS extends Cerca {

    public CercaBFS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {

        //LNO - Frontier
        Queue<Node> lno = new ArrayDeque<>();
        Map<Mapa, Integer> lnt = usarLNT ? new HashMap<>() : null; 
        
        // Nodo inicial
        Node inicialNode = new Node(inicial, null, null, 0, 0);
        lno.add(inicialNode);
        if (usarLNT){
            lnt.put(inicial, 0);
        }

        rc.updateMemoria(1);

        // Bucle principal
        while (!lno.isEmpty()) {
            
            //Extraer sig Node
            Node actual = lno.poll();
            rc.incNodesExplorats();

            // Si és meta, reconstruir camí
            if (actual.estat.esMeta()) {
                List<Moviment> cami = new ArrayList<>();
                Node n = actual;
                while (n.pare != null) {
                    cami.add(0, n.accio);
                    n = n.pare;
                }
                rc.setCami(cami);
                return;
            }

            // Expansió de successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual.estat)) {
                Mapa nouEstat = succ.getKey();
                Moviment mov = succ.getValue();

                boolean descartar = false;

                if (usarLNT) {
                    // Evitem estats ja visitats amb menor profunditat
                    if (lnt.containsKey(nouEstat)) {
                        rc.incNodesTallats();
                        continue;
                    }
                    lnt.put(nouEstat, actual.depth + 1);
                } else {
                    // Control de cicles a la branca actual
                    if (existeixEnBranca(nouEstat, actual)) {
                        rc.incNodesTallats();
                        continue;
                    }
                }

                Node nouNode = new Node(nouEstat, actual, mov, actual.depth + 1, 0);
                lno.add(nouNode);

                rc.updateMemoria(lno.size() + (usarLNT ? lnt.size() : 0));
            }
        }

        // Si no hi ha solució
        rc.setCami(null);
    }

    /**
     * Control de cicles per la branca actual (quan no es fa servir LNT).
     */
    private boolean existeixEnBranca(Mapa estat, Node node) {
        Node actual = node;
        while (actual != null) {
            if (actual.estat.equals(estat)) return true;
            actual = actual.pare;
        }
        return false;
    }
}
