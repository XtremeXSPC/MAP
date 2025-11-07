import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Classe che modella un cluster. Implementa Iterable per supportare enhanced for-loop e Comparable
 * per ordinamento automatico. Implementa Serializable per permettere la serializzazione binaria.
 */
class Cluster implements Iterable<Integer>, Comparable<Cluster>, Serializable {
    private Tuple centroid;
    private Set<Integer> clusteredData;

    /**
     * Costruttore della classe Cluster.
     *
     * @param centroid centroide del cluster
     */
    Cluster(Tuple centroid) {
        this.centroid = centroid;
        clusteredData = new HashSet<>();
    }

    /**
     * Restituisce il centroide del cluster.
     *
     * @return centroide
     */
    Tuple getCentroid() {
        return centroid;
    }

    /**
     * Aggiunge una tupla al cluster.
     * 
     * Ottimizzato: O(1) in media invece di O(n) con ArraySet.
     *
     * @param id identificativo della tupla
     * @return true se la tupla è stata aggiunta (non era già presente)
     */
    boolean addData(int id) {
        return clusteredData.add(id);
    }

    /**
     * Verifica se una transazione è clusterizzata nel cluster corrente.
     * 
     * Ottimizzato: O(1) in media invece di O(n) con ArraySet.
     *
     * @param id identificativo della tupla
     * @return true se la tupla è nel cluster
     */
    boolean contain(int id) {
        return clusteredData.contains(id);
    }

    /**
     * Rimuove una tupla dal cluster.
     * 
     * Ottimizzato: O(1) in media invece di O(n) con ArraySet.
     *
     * @param id identificativo della tupla
     */
    void removeTuple(int id) {
        clusteredData.remove(id);
    }

    /**
     * Restituisce la dimensione del cluster.
     *
     * @return numero di tuple nel cluster
     */
    int getSize() {
        return clusteredData.size();
    }

    /**
     * Restituisce un array con gli identificativi delle tuple nel cluster. Converte Set in array
     * ordinato.
     *
     * @return array di identificativi ordinato
     */
    int[] getTupleIDs() {
        int[] array = new int[clusteredData.size()];
        int index = 0;
        for (Integer id : clusteredData) {
            array[index++] = id;
        }
        // Ordina per consistenza con versione precedente
        java.util.Arrays.sort(array);
        return array;
    }

    /**
     * Restituisce un iteratore sugli identificativi delle tuple nel cluster. Implementazione del
     * pattern Iterator per supportare enhanced for-loop.
     *
     * @return iteratore su tuple IDs
     */
    @Override
    public Iterator<Integer> iterator() {
        return clusteredData.iterator();
    }

    /**
     * Confronta questo cluster con un altro in base alla popolosità (dimensione). Implementa
     * l'interfaccia Comparable per ordinamento automatico in TreeSet.
     *
     * <p>
     * Ordine crescente: cluster più piccoli prima dei più grandi.
     * </p>
     * <p>
     * In caso di parità di dimensione, viene utilizzato il confronto tra hashCode per garantire la
     * consistenza dell'ordinamento. Questo non implica che i cluster siano considerati uguali, ma
     * solo che l'ordinamento è deterministico.
     * </p>
     *
     * @param other cluster da confrontare
     * @return -1 se questo cluster è più piccolo, +1 se è più grande, oppure il risultato del
     *         confronto tra hashCode se le dimensioni sono uguali (0 solo se gli hashCode sono
     *         uguali)
     */
    @Override
    public int compareTo(Cluster other) {
        if (this.getSize() < other.getSize()) {
            return -1;
        } else if (this.getSize() > other.getSize()) {
            return +1;
        } else {
            // Stessa dimensione: confronta centroidi
            int centroidCmp;
            if (this.centroid instanceof Comparable && other.centroid instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<Tuple> comparableCentroid = (Comparable<Tuple>) this.centroid;
                centroidCmp = comparableCentroid.compareTo(other.centroid);
            } else {
                centroidCmp = this.centroid.equals(other.centroid) ? 0
                        : this.centroid.hashCode() - other.centroid.hashCode();
            }
            if (centroidCmp != 0) {
                return centroidCmp;
            }
            // Centroidi uguali: confronta tuple IDs ordinati
            int[] thisIDs = this.getTupleIDs();
            int[] otherIDs = other.getTupleIDs();
            java.util.Arrays.sort(thisIDs);
            java.util.Arrays.sort(otherIDs);
            int minLen = Math.min(thisIDs.length, otherIDs.length);
            for (int i = 0; i < minLen; i++) {
                if (thisIDs[i] != otherIDs[i]) {
                    return Integer.compare(thisIDs[i], otherIDs[i]);
                }
            }
            // Se tutte le tuple sono uguali, confronta lunghezza array
            return Integer.compare(thisIDs.length, otherIDs.length);
        }
    }

    /**
     * Restituisce una stringa rappresentante il centroide del cluster.
     *
     * @return stringa con il centroide
     */
    @Override
    public String toString() {
        String str = "Centroid=(";
        for (int i = 0; i < centroid.getLength(); i++)
            str += centroid.get(i) + " ";
        str += ")";
        return str;
    }

    /**
     * Restituisce una stringa dettagliata del cluster con le tuple contenute.
     *
     * @param data insieme di dati
     * @return stringa dettagliata del cluster
     */
    public String toString(Data data) {
        String str = "Centroid=(";
        for (int i = 0; i < centroid.getLength(); i++)
            str += centroid.get(i) + " ";
        str += ")\nExamples:\n";
        int array[] = getTupleIDs(); // Converte Set → array ordinato
        for (int i = 0; i < array.length; i++) {
            str += "[";
            for (int j = 0; j < data.getNumberOfExplanatoryAttributes(); j++)
                str += data.getValue(array[i], j) + " ";
            str += "] dist=" + getCentroid().getDistance(data.getItemSet(array[i])) + "\n";
        }
        str += "\nAvgDistance=" + getCentroid().avgDistance(data, clusteredData);
        return str;
    }
}
