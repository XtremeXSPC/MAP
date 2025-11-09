package mining;

import java.io.Serializable;
import data.Data;

/**
 * Classe che incapsula tutti i dati necessari per il clustering:
 * ClusterSet, Data e radius. Utilizzata per la serializzazione completa
 * dei risultati di clustering su file .dmp.
 *
 * @author MAP Team
 * @version 1.0.0
 * @since Sprint 4
 */
public class SerializableClusteringData implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClusterSet clusterSet;
    private Data data;
    private double radius;

    /**
     * Costruttore che inizializza tutti i campi.
     *
     * @param clusterSet l'insieme dei cluster scoperti
     * @param data il dataset utilizzato per il clustering
     * @param radius il raggio massimo utilizzato per il clustering
     */
    public SerializableClusteringData(ClusterSet clusterSet, Data data, double radius) {
        this.clusterSet = clusterSet;
        this.data = data;
        this.radius = radius;
    }

    /**
     * Restituisce l'insieme dei cluster.
     *
     * @return ClusterSet
     */
    public ClusterSet getClusterSet() {
        return clusterSet;
    }

    /**
     * Restituisce il dataset.
     *
     * @return Data
     */
    public Data getData() {
        return data;
    }

    /**
     * Restituisce il radius utilizzato.
     *
     * @return radius
     */
    public double getRadius() {
        return radius;
    }
}
