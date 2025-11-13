package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.heuristica.Heuristica;
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.Comparator;
import java.util.*;


public class CercaAStar extends Cerca {

    private final Heuristica heur;

    public CercaAStar(boolean usarLNT, Heuristica heur) { 
        super(usarLNT); 
        this.heur = heur; 
    }


   @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        // Comparator per ordenar per f(n) = g + h
        Comparator<Node> comparator = Comparator.comparingDouble(n -> n.g + heur.h(n.estat));
        PriorityQueue<Node> lno = new PriorityQueue<>(comparator);

        // Taules auxiliars
        Map<Mapa, Integer> gCost = new HashMap<>();
        Set<Mapa> lnt = usarLNT ? new HashSet<>() : null;

        // Node inicial
        Node inicialNode = new Node(inicial, null, null, 0, 0);
        lno.add(inicialNode);
        gCost.put(inicial, 0);

        rc.updateMemoria(1);

        while (!lno.isEmpty()) {
            Node actual = lno.poll();
            rc.incNodesExplorats();

            // Si és la meta -> reconstruir camí
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

            // Marcar com tancat
            if (usarLNT) lnt.add(actual.estat);

            for (Moviment mov : actual.estat.getAccionsPossibles()) {
                Mapa nouEstat = actual.estat.mou(mov);
                int nouG = actual.g + 1;

                if (usarLNT && lnt.contains(nouEstat)) {
                    rc.incNodesTallats();
                    continue;
                }

                if (gCost.containsKey(nouEstat) && gCost.get(nouEstat) <= nouG) {
                    rc.incNodesTallats();
                    continue;
                }

                gCost.put(nouEstat, nouG);
                Node nouNode = new Node(nouEstat, actual, mov, actual.depth + 1, nouG);
                lno.add(nouNode);
            }
            
            rc.updateMemoria(lno.size() + (usarLNT ? lnt.size() : 0));

        }      

        // Si no es troba solució
        rc.setCami(null);
    }
}