package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;

import java.util.*;

/**
 * Heuristica avançada: Al vostre gust ;-)
 */
public class HeuristicaAvancada implements Heuristica {
    
    @Override
    public int h(Mapa estat) {
        if (estat.esMeta()){
            return 0;
        }

        List<Posicio> agents = estat.getAgents();
        Posicio sortida = estat.getSortidaPosicio();

        // Lista de posiciones de claves pendientes
        List<Posicio> clausPendents = estat.getClausPendents();
        
        if (clausPendents.isEmpty()) {
            // No quedan claus nomes la sortida
            return distanciaManhattanMinima(agents, sortida);
        }

        // 1: distancia mínima del agent a la primera clau
        int distInici = Integer.MAX_VALUE;
        Posicio primeraClau = null;
        for (Posicio agent : agents) {
            for (Posicio clau : clausPendents) {
                int d = distanciaManhattan(agent, clau);
                if (d < distInici) {
                    distInici = d;
                    primeraClau = clau;
                }
            }
        }

        // 2: resta de claus
        int distClaus = 0;
        List<Posicio> clausRestants = new ArrayList<>(clausPendents);
        clausRestants.remove(primeraClau);
        Posicio actual = primeraClau;

        while (!clausRestants.isEmpty()) {
            Posicio seguent = clausRestants.get(0);
            int minDist = distanciaManhattan(actual, seguent);
            for (Posicio c : clausRestants) {
                int d = distanciaManhattan(actual, c);
                if (d < minDist) {
                    minDist = d;
                    seguent = c;
                }
            }
            distClaus += minDist;
            actual = seguent;
            clausRestants.remove(seguent);
        }

        // distancia de l'última clau a la sortida
        int distSortida = distanciaManhattan(actual, sortida);

        // sumar tot
        return distInici + distClaus + distSortida;
    }

        
    public int distanciaManhattan(Posicio a, Posicio b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private int distanciaManhattanMinima(List<Posicio> agents, Posicio dest) {
        int minDist = Integer.MAX_VALUE;
        for (Posicio a : agents) {
            int d = distanciaManhattan(a, dest);
            if (d < minDist) minDist = d;
        }
        return minDist;
    }
}
