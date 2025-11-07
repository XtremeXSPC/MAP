import java.util.HashSet;
import java.util.Iterator;

/**
 * Classe che modella un cluster.
 * Ottimizzata con HashSet per operazioni O(1) invece di O(n).
 */
class Cluster {
    private Tuple centroid;
    private HashSet<Integer> clusteredData;

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
     * Ottimizzato: O(1) average invece di O(n) con ArraySet.
     *
     * @param id identificativo della tupla
     * @return true se la tupla è stata aggiunta (non era già presente)
     */
    boolean addData(int id) {
        return clusteredData.add(id);
    }

    /**
     * Verifica se una transazione è clusterizzata nel cluster corrente.
     * Ottimizzato: O(1) average invece di O(n) con ArraySet.
     *
     * @param id identificativo della tupla
     * @return true se la tupla è nel cluster
     */
    boolean contain(int id) {
        return clusteredData.contains(id);
    }

    /**
     * Rimuove una tupla dal cluster.
     * Ottimizzato: O(1) average invece di O(n) con ArraySet.
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
     * Restituisce un array con gli identificativi delle tuple nel cluster.
     * Converte HashSet in array ordinato.
     *
     * @return array di identificativi ordinato
     */
    int[] iterator() {
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
        int array[] = iterator();  // Usa il metodo iterator() che converte HashSet → array
        for (int i = 0; i < array.length; i++) {
            str += "[";
            for (int j = 0; j < data.getNumberOfExplanatoryAttributes(); j++)
                str += data.getValue(array[i], j) + " ";
            str += "] dist=" + getCentroid().getDistance(data.getItemSet(array[i])) + "\n";
        }
        str += "\nAvgDistance=" + getCentroid().avgDistance(data, array);
        return str;
    }
}
