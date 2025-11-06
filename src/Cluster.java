/**
 * Classe che modella un cluster.
 */
class Cluster {
    private Tuple centroid;
    private ArraySet clusteredData;

    /**
     * Costruttore della classe Cluster.
     *
     * @param centroid centroide del cluster
     */
    Cluster(Tuple centroid) {
        this.centroid = centroid;
        clusteredData = new ArraySet();
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
     * @param id identificativo della tupla
     * @return true se la tupla cambia cluster
     */
    boolean addData(int id) {
        return clusteredData.add(id);
    }

    /**
     * Verifica se una transazione è clusterizzata nel cluster corrente.
     *
     * @param id identificativo della tupla
     * @return true se la tupla è nel cluster
     */
    boolean contain(int id) {
        return clusteredData.get(id);
    }

    /**
     * Rimuove una tupla dal cluster.
     *
     * @param id identificativo della tupla
     */
    void removeTuple(int id) {
        clusteredData.delete(id);
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
     *
     * @return array di identificativi
     */
    int[] iterator() {
        return clusteredData.toArray();
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
        int array[] = clusteredData.toArray();
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
