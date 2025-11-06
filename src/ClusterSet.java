/**
 * Classe che rappresenta un insieme di cluster.
 */
public class ClusterSet {
    private Cluster C[] = new Cluster[0];

    /**
     * Costruttore della classe ClusterSet.
     */
    public ClusterSet() {
    }

    /**
     * Aggiunge un cluster all'insieme.
     *
     * @param c cluster da aggiungere
     */
    public void add(Cluster c) {
        Cluster tempC[] = new Cluster[C.length + 1];
        for (int i = 0; i < C.length; i++)
            tempC[i] = C[i];
        tempC[C.length] = c;
        C = tempC;
    }

    /**
     * Restituisce il cluster in posizione i.
     *
     * @param i posizione del cluster
     * @return cluster in posizione i
     */
    public Cluster get(int i) {
        return C[i];
    }

    /**
     * Restituisce una stringa con i centroidi di tutti i cluster.
     *
     * @return stringa con i centroidi
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < C.length; i++) {
            if (C[i] != null) {
                str += i + ":" + C[i].toString() + "\n";
            }
        }
        return str;
    }

    /**
     * Restituisce una stringa dettagliata con lo stato di ciascun cluster.
     *
     * @param data insieme di dati
     * @return stringa dettagliata dei cluster
     */
    public String toString(Data data) {
        String str = "";
        for (int i = 0; i < C.length; i++) {
            if (C[i] != null) {
                str += i + ":" + C[i].toString(data) + "\n";
            }
        }
        return str;
    }
}
