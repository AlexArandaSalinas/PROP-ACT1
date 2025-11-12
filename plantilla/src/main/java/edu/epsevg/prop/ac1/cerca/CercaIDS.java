package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaIDS extends Cerca {

    private static final int MAX_PROFUNDITAT = 50;

    public CercaIDS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        boolean trobat = false;
        int limit = 0;

        while (!trobat && limit <= MAX_PROFUNDITAT) {

            // Cada iteració reinicia estructures
            Stack<Node> LNO = new Stack<>();
            Node inicialNode = new Node(inicial, null, null, 0, 0);
            LNO.push(inicialNode);

            // LNT només si cal
            HashMap<Mapa, Integer> LNT = usarLNT ? new HashMap<>() : null;

            while (!LNO.isEmpty()) {
                Node actual = LNO.pop();
                rc.incNodesExplorats();

                // Comprovar meta
                if (actual.estat.esMeta()) {
                    rc.setCami(reconstruirCami(actual));
                    trobat = true;
                    break;
                }

                // Tall per profunditat
                if (actual.depth >= limit) {
                    rc.incNodesTallats();
                    continue;
                }

                boolean explorar = true;

                // Control de repeticions amb LNT
                if (usarLNT) {
                    Integer profunditatAnterior = LNT.get(actual.estat);
                    if (profunditatAnterior != null && profunditatAnterior <= actual.depth) {
                        explorar = false;
                        rc.incNodesTallats();
                    } else {
                        LNT.put(actual.estat, actual.depth);
                    }
                }
                // Control de cicles si no hi ha LNT
                else if (comprobarCicle(actual)) {
                    explorar = false;
                    rc.incNodesTallats();
                }

                if (!explorar) continue;

                // Generar successors
                List<Moviment> moviments = actual.estat.getAccionsPossibles();

                // Limitem la quantitat de successors si el mapa és enorme
                if (moviments.size() > 20) {
                    moviments = moviments.subList(0, 20);
                }

                for (Moviment mov : moviments) {
                    try {
                        Mapa nouMapa = actual.estat.mou(mov);
                        Node nouNode = new Node(nouMapa, actual, mov, actual.depth + 1, actual.g + 1);
                        LNO.push(nouNode);
                    } catch (Exception e) {
                        // Si el moviment no és vàlid, el descartem
                        rc.incNodesTallats();
                    }
                }

                rc.updateMemoria(LNO.size() + (usarLNT ? LNT.size() : 0));

                // Evitar bloquejos: tall de seguretat, sino en el mapD mor
                /*if (rc.getNodesExplorats() > 500000) {
                    rc.setCami(null);
                    return;
                }*/
            }

            limit++;
        }

        if (!trobat) rc.setCami(null);
    }

}
