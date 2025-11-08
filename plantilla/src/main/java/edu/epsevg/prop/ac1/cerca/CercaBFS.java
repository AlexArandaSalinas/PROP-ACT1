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
        // Frontier: cua FIFO per BFS
        Queue<Mapa> frontier = new ArrayDeque<>();
        // Mapes per reconstruir el camí: estat fill -> estat pare
        Map<Mapa, Mapa> parents = new HashMap<>();
        Map<Mapa, Moviment> accions = new HashMap<>();
        // Llista tancada (per evitar repetir estats)
        Set<Mapa> visitats = usarLNT ? new HashSet<>() : null;

        // Afegir estat inicial
        frontier.add(inicial);
        if (usarLNT) visitats.add(inicial);

        // Bucle principal BFS
        while (!frontier.isEmpty()) {
            Mapa actual = frontier.poll();  // Treure el primer estat
            rc.incNodesExplorats();         // Comptem nodes explorats

            // Si hem arribat a la meta
            if (actual.esMeta()) {
                // Reconstruir camí des de la meta fins a l'inicial
                List<Moviment> cami = new ArrayList<>();
                Mapa est = actual;
                while (parents.containsKey(est)) {
                    cami.add(0, accions.get(est)); // afegir al principi
                    est = parents.get(est);
                }
                rc.setCami(cami);
                return;
            }

            // Generar successors (moviments vàlids)
            for (Map.Entry<Mapa, Moviment> succ : successors(actual)) {
                Mapa nou = succ.getKey();
                Moviment mov = succ.getValue();

                // Evitar repetir estats si fem servir llista tancada
                if (usarLNT && visitats.contains(nou)) {
                    rc.incNodesTallats(); // comptem nodes tallats
                    continue;
                }

                // Afegir a frontier i marcar com visitat
                frontier.add(nou);
                if (usarLNT) visitats.add(nou);

                // Guardar pare i moviment per reconstruir camí
                parents.put(nou, actual);
                accions.put(nou, mov);

                // Actualitzar memòria (opcional)
                rc.updateMemoria(frontier.size() + (usarLNT ? visitats.size() : 0));
            }
        }

        // Si s'acaba la cua i no trobem meta → no hi ha solució
        rc.setCami(null);
    }
}
