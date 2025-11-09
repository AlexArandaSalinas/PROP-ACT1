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

    private static class Node {
        Mapa mapa;
        double g; // cost acumulat
        double h; // heurística
        double f; // g + h

        Node(Mapa mapa, double g, double h) {
            this.mapa = mapa;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // Comparator per ordenar per f(n)
        Comparator<Node> comparator = Comparator.comparingDouble(n -> n.f);
        PriorityQueue<Node> oberts = new PriorityQueue<>(comparator);

        Map<Mapa, Mapa> parents = new HashMap<>();
        Map<Mapa, Moviment> accions = new HashMap<>();
        Map<Mapa, Double> gScore = new HashMap<>();

        // Llista de nodes tancats (LNT)
        Map<Mapa, Double> tancats = usarLNT ? new HashMap<>() : null;

        Node inicialNode = new Node(inicial, 0, heur.h(inicial));
        oberts.add(inicialNode);
        gScore.put(inicial, 0.0);

        while (!oberts.isEmpty()) {
            Node actualNode = oberts.poll();
            Mapa actual = actualNode.mapa;
            rc.incNodesExplorats();

            // Si és meta → reconstruir camí
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

            // Marcar com tancat
            if (usarLNT) tancats.put(actual, actualNode.g);

            // Generar successors
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();
                double nouG = actualNode.g + 1; // cost d'un moviment

                // Si està tancat amb cost menor → tallar
                if (usarLNT && tancats.containsKey(nou) && tancats.get(nou) <= nouG) {
                    rc.incNodesTallats();
                    continue;
                }

                // Si ja el tenim amb millor cost → ignorar
                if (gScore.containsKey(nou) && gScore.get(nou) <= nouG) {
                    rc.incNodesTallats();
                    continue;
                }

                gScore.put(nou, nouG);
                double nouH = heur.h(nou);
                Node nouNode = new Node(nou, nouG, nouH);

                oberts.add(nouNode);
                parents.put(nou, actual);
                accions.put(nou, mov);
                rc.updateMemoria(oberts.size() + (usarLNT ? tancats.size() : 0));
            }
        }

        // Si no hi ha solució
        rc.setCami(null);
    }
}
