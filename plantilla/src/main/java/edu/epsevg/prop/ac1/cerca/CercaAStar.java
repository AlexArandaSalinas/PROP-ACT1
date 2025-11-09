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
        PriorityQueue<Node> oberts = new PriorityQueue<>(comparator);

        // Taules auxiliars
        Map<Mapa, Integer> gScore = new HashMap<>();
        Set<Mapa> tancats = usarLNT ? new HashSet<>() : null;

        // Node inicial
        Node inicialNode = new Node(inicial, null, null, 0, 0);
        oberts.add(inicialNode);
        gScore.put(inicial, 0);

        rc.updateMemoria(1);

        while (!oberts.isEmpty()) {
            Node actual = oberts.poll();
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
            if (usarLNT) tancats.add(actual.estat);

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual.estat)) {
                Mapa nouEstat = succ.getKey();
                Moviment accio = succ.getValue();
                int nouG = actual.g + 1; // cost uniforme

                // Si ja està tancat → tallar
                if (usarLNT && tancats.contains(nouEstat)) {
                    rc.incNodesTallats();
                    continue;
                }

                // Si ja tenim un cost millor → ignorar
                if (gScore.containsKey(nouEstat) && gScore.get(nouEstat) <= nouG) {
                    rc.incNodesTallats();
                    continue;
                }

                gScore.put(nouEstat, nouG);
                Node nouNode = new Node(nouEstat, actual, accio, actual.depth + 1, nouG);
                oberts.add(nouNode);

                rc.updateMemoria(oberts.size() + (usarLNT ? tancats.size() : 0));
            }
        }

        // Si no es troba solució
        rc.setCami(null);
    }
}