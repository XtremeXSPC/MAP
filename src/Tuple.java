import java.util.Set;

/**
 * Classe che modella una tupla come sequenza di coppie attributo-valore.
 */
public class Tuple {
    private Item[] tuple;

    /**
     * Costruttore della classe Tuple.
     *
     * @param size numero di item che costituirà la tupla
     */
    public Tuple(int size) {
        tuple = new Item[size];
    }

    /**
     * Restituisce la lunghezza della tupla.
     *
     * @return lunghezza della tupla
     */
    public int getLength() {
        return tuple.length;
    }

    /**
     * Restituisce l'item in posizione i.
     *
     * @param i posizione dell'item
     * @return item in posizione i
     */
    public Item get(int i) {
        return tuple[i];
    }

    /**
     * Memorizza un item in posizione i.
     *
     * @param c item da memorizzare
     * @param i posizione
     */
    public void add(Item c, int i) {
        tuple[i] = c;
    }

    /**
     * Calcola la distanza tra la tupla corrente e un'altra tupla. La distanza è la somma delle
     * distanze tra gli item nelle stesse posizioni.
     *
     * @param obj tupla con cui calcolare la distanza
     * @return distanza tra le tuple
     */
    public double getDistance(Tuple obj) {
        double distance = 0.0;
        for (int i = 0; i < tuple.length; i++) {
            distance += tuple[i].distance(obj.get(i).getValue());
        }
        return distance;
    }

    /**
     * Calcola la distanza media tra la tupla corrente e le tuple in data identificate dagli indici
     * in clusteredData.
     *
     * @param data insieme di dati
     * @param clusteredData set di indici delle tuple
     * @return distanza media
     */
    public double avgDistance(Data data, Set<Integer> clusteredData) {
        double p = 0.0, sumD = 0.0;
        for (Integer tupleId : clusteredData) {
            double d = getDistance(data.getItemSet(tupleId));
            sumD += d;
        }
        p = sumD / clusteredData.size();
        return p;
    }
}
