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
            Node resultat = cercaLimitada(inicial, limit, rc);
            if (resultat != null) {
                rc.setCami(reconstruirCami(resultat));
                trobat = true;
            }
            limit++;
        }

        if (!trobat){
            rc.setCami(null);
        }
    }

    private Node cercaLimitada(Mapa inicial, int limit, ResultatCerca rc) {
        
        Stack<Node> LNO = new Stack<>();
        Node inicialNode = new Node(inicial, null, null, 0, 0);
        LNO.push(inicialNode);

        HashMap<Mapa, Integer> LNT = usarLNT ? new HashMap<>() : null;

        while (!LNO.isEmpty()) {
            Node actual = LNO.pop();
            rc.incNodesExplorats();

            if (actual.estat.esMeta()){
                return actual;
            }

            if (actual.depth >= limit) {
                rc.incNodesTallats();
                continue;
            }

            boolean explorar = true;

            if (usarLNT) {
                Integer profunditatAnterior = LNT.get(actual.estat);
                if (profunditatAnterior != null && profunditatAnterior <= actual.depth) {
                    explorar = false;
                    rc.incNodesTallats();
                } else {
                    LNT.put(actual.estat, actual.depth);
                }
            } else if (comprobarCicle(actual)) {
                explorar = false;
                rc.incNodesTallats();
            }

            if (!explorar) continue;

            List<Moviment> moviments = actual.estat.getAccionsPossibles();
            for (Moviment mov : moviments) {
                try {
                    Mapa nouMapa = actual.estat.mou(mov);
                    Node nouNode = new Node(nouMapa, actual, mov, actual.depth + 1, actual.g + 1);
                    LNO.push(nouNode);
                } catch (Exception e) {
                    rc.incNodesTallats();
                }
            }

            rc.updateMemoria(LNO.size() + (usarLNT ? LNT.size() : 0));
        }

        return null;
    }
}
