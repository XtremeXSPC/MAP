package gui.utils;

import gui.models.ClusteringConfiguration;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.services.DataImportService;
import gui.services.ExportService;

/**
 * Singleton che mantiene il contesto dell'applicazione condiviso tra i controller.
 * Permette il passaggio di dati tra le diverse view.
 *
 * @author MAP Team
 * @version 1.0.0
 * @since Sprint 2
 */
public class ApplicationContext {

    private static ApplicationContext instance;

    private final ClusteringService clusteringService;
    private final DataImportService dataImportService;
    private final ExportService exportService;

    private ClusteringConfiguration currentConfiguration;
    private ClusteringResult currentResult;

    /**
     * Costruttore privato (Singleton pattern).
     */
    private ApplicationContext() {
        this.clusteringService = new ClusteringService();
        this.dataImportService = new DataImportService();
        this.exportService = new ExportService();
    }

    /**
     * Restituisce l'istanza singleton del contesto applicazione.
     *
     * @return istanza ApplicationContext
     */
    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    /**
     * @return il servizio di clustering
     */
    public ClusteringService getClusteringService() {
        return clusteringService;
    }

    /**
     * @return il servizio di importazione dati
     */
    public DataImportService getDataImportService() {
        return dataImportService;
    }

    /**
     * @return il servizio di esportazione
     */
    public ExportService getExportService() {
        return exportService;
    }

    /**
     * Imposta la configurazione clustering corrente.
     *
     * @param configuration configurazione
     */
    public void setCurrentConfiguration(ClusteringConfiguration configuration) {
        this.currentConfiguration = configuration;
    }

    /**
     * @return la configurazione clustering corrente
     */
    public ClusteringConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    /**
     * Imposta il risultato clustering corrente.
     *
     * @param result risultato
     */
    public void setCurrentResult(ClusteringResult result) {
        this.currentResult = result;
    }

    /**
     * @return il risultato clustering corrente
     */
    public ClusteringResult getCurrentResult() {
        return currentResult;
    }

    /**
     * Pulisce il contesto (reset configurazione e risultati).
     */
    public void clear() {
        this.currentConfiguration = null;
        this.currentResult = null;
    }
}
