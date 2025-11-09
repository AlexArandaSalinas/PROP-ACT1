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
        if (mapa.esMeta()){
            return 0;
        }

        double millorDistancia = Double.MAX_VALUE;

        List<Posicio> agents = mapa.getAgents();

        // Llista de claus pendents (caràcters 'a'..'z' que encara no estan recollits)
        List<Posicio> clausPendents = new ArrayList<>();
        for (int i = 0; i < mapa.getN(); i++) {
            for (int j = 0; j < mapa.getM(); j++) {
                int cell = getCell(mapa, i, j);
                if (Character.isLowerCase(cell)) {
                    char key = (char) cell;
                    if (!mapa.teClau(key)) {
                        clausPendents.add(new Posicio(i, j));
                    }
                }
            }
        }

        // Si encara hi ha claus → distància mínima agent - clau pendent
        if (!clausPendents.isEmpty()) {
            for (Posicio agent : agents) {
                for (Posicio clau : clausPendents) {
                    double d = distanciaManhattan(agent, clau);
                    if (d < millorDistancia) millorDistancia = d;
                }
            }
        } 
        // Si no hi ha claus pendents → distància mínima agent - sortida
        else {
            Posicio sortida = mapa.getSortidaPosicio();
            for (Posicio agent : agents) {
                double d = distanciaManhattan(agent, sortida);
                if (d < millorDistancia) millorDistancia = d;
            }
        }

        return millorDistancia;
    }

    private double distanciaManhattan(Posicio a, Posicio b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Accés directe al contingut de la cel·la (permet llegir el grid del mapa)
     */
    private int getCell(Mapa mapa, int i, int j) {
        try {
            java.lang.reflect.Field f = mapa.getClass().getDeclaredField("grid");
            f.setAccessible(true);
            int[][] grid = (int[][]) f.get(mapa);
            return grid[i][j];
        } catch (Exception e) {
            throw new RuntimeException("Error accedint al grid del mapa", e);
        }
    }
}
