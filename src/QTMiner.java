/**
 * Classe che implementa l'algoritmo Quality Threshold per il clustering.
 */
public class QTMiner {
    private ClusterSet C;
    private double radius;

    /**
     * Costruttore della classe QTMiner.
     *
     * @param radius raggio dei cluster
     */
    public QTMiner(double radius) {
        C = new ClusterSet();
        this.radius = radius;
    }

    /**
     * Restituisce l'insieme di cluster.
     *
     * @return insieme di cluster
     */
    public ClusterSet getC() {
        return C;
    }

    /**
     * Esegue l'algoritmo QT per scoprire i cluster.
     *
     * @param data insieme di dati
     * @return numero di cluster scoperti
     */
    public int compute(Data data) {
        int numclusters = 0;
        boolean isClustered[] = new boolean[data.getNumberOfExamples()];
        for (int i = 0; i < isClustered.length; i++)
            isClustered[i] = false;

        int countClustered = 0;
        while (countClustered != data.getNumberOfExamples()) {
            // Ricerca cluster più popoloso
            Cluster c = buildCandidateCluster(data, isClustered);
            C.add(c);
            numclusters++;

            // Rimuovo tuple clusterizzate da dataset
            int clusteredTupleId[] = c.iterator();
            for (int i = 0; i < clusteredTupleId.length; i++) {
                isClustered[clusteredTupleId[i]] = true;
            }
            countClustered += c.getSize();
        }

        return numclusters;
    }

    /**
     * Costruisce un cluster candidato per ogni tupla non ancora clusterizzata
     * e restituisce il cluster più popoloso.
     *
     * @param data insieme di dati
     * @param isClustered informazione sullo stato di clusterizzazione delle tuple
     * @return cluster candidato più popoloso
     */
    private Cluster buildCandidateCluster(Data data, boolean isClustered[]) {
        Cluster bestCluster = null;
        int maxClusterSize = 0;

        // Per ogni tupla non ancora clusterizzata
        for (int i = 0; i < data.getNumberOfExamples(); i++) {
            if (!isClustered[i]) {
                // Crea un cluster candidato con centroide la tupla i-esima
                Tuple centroid = data.getItemSet(i);
                Cluster candidateCluster = new Cluster(centroid);

                // Aggiungi al cluster candidato tutte le tuple non clusterizzate
                // che distano al più radius dal centroide
                for (int j = 0; j < data.getNumberOfExamples(); j++) {
                    if (!isClustered[j]) {
                        double distance = centroid.getDistance(data.getItemSet(j));
                        if (distance <= radius) {
                            candidateCluster.addData(j);
                        }
                    }
                }

                // Se il cluster candidato è più popoloso del migliore trovato finora
                if (candidateCluster.getSize() > maxClusterSize) {
                    bestCluster = candidateCluster;
                    maxClusterSize = candidateCluster.getSize();
                }
            }
        }

        return bestCluster;
    }
}
