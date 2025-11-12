package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.*;
import java.util.*;

/** 
 * Distància de Manhattan a la clau més propera 
 * (si queden per recollir) o a la sortida.
 */
public class HeuristicaBasica implements Heuristica {
    
    @Override
    public int h(Mapa estat) {
        return (int) Math.round(avaluar(estat));
    }

    private double avaluar(Mapa mapa) {
        if (mapa.esMeta()) return 0;

        List<Posicio> agents = mapa.getAgents();
        Posicio sortida = mapa.getSortidaPosicio();
        
        double millorDistancia = Double.MAX_VALUE;

        // Calcular la mínima distància Manhattan entre qualsevol agent i la sortida
        for (Posicio agent : agents) {
            double d = distanciaManhattan(agent, sortida);
            if (d < millorDistancia) millorDistancia = d;
        }

        return millorDistancia;
    }

    private double distanciaManhattan(Posicio a, Posicio b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
