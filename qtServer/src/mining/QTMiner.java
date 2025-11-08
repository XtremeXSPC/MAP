package mining;

import java.io.*;
import data.*;

/**
 * Classe che implementa l'algoritmo Quality Threshold per il clustering. Versione
 * ottimizzata con cache delle distanze. Supporta serializzazione e de-serializzazione dei
 * cluster (QT07).
 */
public class QTMiner {
    private ClusterSet C;
    private double radius;
    private boolean enableOptimizations;
    private DistanceCache distanceCache;

    /**
     * Costruttore della classe QTMiner (backwards compatible). Disabilita caching di default
     * (basato su risultati benchmark). Le ottimizzazioni strutturali (HashSet/ArrayList) sono
     * sempre attive.
     *
     * @param radius raggio dei cluster
     */
    public QTMiner(double radius) {
        this(radius, false); // Caching disabilitato di default
    }

    /**
     * Costruttore con controllo ottimizzazioni.
     *
     * @param radius raggio dei cluster
     * @param enableOptimizations true per abilitare cache e ottimizzazioni
     */
    public QTMiner(double radius, boolean enableOptimizations) {
        C = new ClusterSet();
        this.radius = radius;
        this.enableOptimizations = enableOptimizations;
    }

    /**
     * Costruttore che carica cluster da file serializzato (QT07). De-serializza l'oggetto
     * ClusterSet da file binario.
     *
     * @param fileName percorso + nome file (senza estensione .dmp)
     * @throws FileNotFoundException se il file non esiste
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se la classe serializzata non è trovata
     */
    public QTMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(fileName + ".dmp");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        C = (ClusterSet) in.readObject();
        in.close();
        fileIn.close();

        // Imposta valori di default per attributi non serializzati
        this.radius = 0;
        this.enableOptimizations = false;
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
     * Restituisce il cache delle distanze (se abilitato).
     *
     * @return cache oppure null se disabilitato
     */
    public DistanceCache getDistanceCache() {
        return distanceCache;
    }

    /**
     * Esegue l'algoritmo QT per scoprire i cluster. Versione ottimizzata con cache distanze.
     *
     * @param data insieme di dati
     * @return numero di cluster scoperti
     */
    public int compute(Data data) {
        // Inizializza cache se ottimizzazioni abilitate
        if (enableOptimizations) {
            // Cache distanze fino a 2×radius per efficienza memoria
            distanceCache = new DistanceCache(data, true, radius * 2.5);
        } else {
            distanceCache = new DistanceCache(data, false, 0);
        }

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
            int clusteredTupleId[] = c.getTupleIDs();
            for (int i = 0; i < clusteredTupleId.length; i++) {
                isClustered[clusteredTupleId[i]] = true;
            }
            countClustered += c.getSize();
        }

        return numclusters;
    }

    /**
     * Salva i cluster in un file binario serializzato (QT07). Serializza l'oggetto ClusterSet
     * in formato .dmp.
     *
     * @param fileName percorso + nome file (senza estensione .dmp)
     * @throws FileNotFoundException se il percorso non è valido
     * @throws IOException se si verificano errori di I/O
     */
    public void salva(String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName + ".dmp");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(C);
        out.close();
        fileOut.close();
        System.out.println("Saving clusters in " + fileName + ".dmp");
    }

    /**
     * Costruisce un cluster candidato per ogni tupla non ancora clusterizzata e restituisce
     * il cluster più popoloso. Versione ottimizzata con cache distanze.
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
                        // Usa cache se disponibile, altrimenti calcola direttamente
                        double distance;
                        if (distanceCache != null && distanceCache.isEnabled()) {
                            distance = distanceCache.getDistance(i, j);
                        } else {
                            distance = centroid.getDistance(data.getItemSet(j));
                        }

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
