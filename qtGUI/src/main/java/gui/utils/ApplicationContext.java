package gui.utils;

//===---------------------------------------------------------------------------===//
// Importazioni modelli e servizi.
import gui.models.ClusteringConfiguration;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.services.DataImportService;
import gui.services.ExportService;
//===---------------------------------------------------------------------------===//

/**
 * Singleton che mantiene il contesto dell'applicazione condiviso tra i controller.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Servizi principali (clustering, import, export)</li>
 *   <li>Configurazione corrente del clustering</li>
 *   <li>Risultati correnti del clustering</li>
 * </ul>
 * <p>
 * Il contesto evita accoppiamenti diretti tra controller e facilita
 * il passaggio di stato tra le viste.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApplicationContext {

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    private static ApplicationContext instance;

    private final ClusteringService clusteringService;
    private final DataImportService dataImportService;
    private final ExportService exportService;

    private ClusteringConfiguration currentConfiguration;
    private ClusteringResult currentResult;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore privato (Singleton pattern).
     */
    private ApplicationContext() {
        this.clusteringService = new ClusteringService();
        this.dataImportService = new DataImportService();
        this.exportService = new ExportService();
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

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
     * Restituisce il servizio di clustering.
     *
     * @return il servizio di clustering
     */
    public ClusteringService getClusteringService() {
        return clusteringService;
    }

    /**
     * Restituisce il servizio di importazione dati.
     *
     * @return il servizio di importazione dati
     */
    public DataImportService getDataImportService() {
        return dataImportService;
    }

    /**
     * Restituisce il servizio di esportazione.
     *
     * @return il servizio di esportazione
     */
    public ExportService getExportService() {
        return exportService;
    }

    //===--------------------------- STATE ACCESSORS ---------------------------===//

    /**
     * Imposta la configurazione clustering corrente.
     *
     * @param configuration configurazione
     */
    public void setCurrentConfiguration(ClusteringConfiguration configuration) {
        this.currentConfiguration = configuration;
    }

    /**
     * Restituisce la configurazione clustering corrente.
     *
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
     * Restituisce il risultato clustering corrente.
     *
     * @return il risultato clustering corrente
     */
    public ClusteringResult getCurrentResult() {
        return currentResult;
    }

    //===-------------------------- STATE OPERATIONS ---------------------------===//

    /**
     * Pulisce il contesto (reset configurazione e risultati).
     */
    public void clear() {
        this.currentConfiguration = null;
        this.currentResult = null;
    }
}

//===---------------------------------------------------------------------------===//
