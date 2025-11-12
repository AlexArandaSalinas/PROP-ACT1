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
        //LNT
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

            Node actual = lno.poll();
            rc.incNodesExplorats();

            // Si és meta, reconstruir camí
            if (actual.estat.esMeta()) {
                rc.setCami(reconstruirCami(actual));
                return;
            }

            // Expansión de sucesores válidos
            for (Moviment mov : actual.estat.getAccionsPossibles()) {
                Mapa nouEstat = actual.estat.mou(mov);

                if (usarLNT) {
                    if (lnt.containsKey(nouEstat)) {
                        rc.incNodesTallats();
                        continue;
                    }
                    lnt.put(nouEstat, actual.depth + 1);
                } else {
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
}
