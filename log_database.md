mvn -f pom.xml javafx:run                                                                                                                                                                                                             
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------------< com.map:qtGUI >----------------------------
[INFO] Building QT Clustering GUI 1.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] >>> javafx:0.0.8:run (default-cli) > process-classes @ qtGUI >>>
[WARNING] 6 problems were encountered while building the effective model for org.openjfx:javafx-controls:jar:21.0.1 during dependency collection step for project (use -X to see details)
[INFO]
[INFO] --- build-helper:3.4.0:add-source (add-source) @ qtGUI ---
[INFO] Source directory: /Volumes/LCS.Data/MAP/qtGUI/../qtServer/src added.
[INFO]
[INFO] --- resources:3.3.1:resources (default-resources) @ qtGUI ---
[INFO] Copying 7 resources from src/main/resources to target/classes
[INFO]
[INFO] --- compiler:3.11.0:compile (default-compile) @ qtGUI ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] <<< javafx:0.0.8:run (default-cli) < process-classes @ qtGUI <<<
[INFO]
[INFO]
[INFO] --- javafx:0.0.8:run (default-cli) @ qtGUI ---
[WARNING] Module name not found in <mainClass>. Module name will be assumed from module-info.java
00:33:30.371 [main] INFO  gui.MainApp - Lancio GUI QT Clustering...
00:33:30.386 [JavaFX Application Thread] INFO  gui.MainApp - Avvio applicazione GUI QT Clustering...
00:33:30.485 [JavaFX Application Thread] INFO  gui.controllers.MainController - Inizializzazione MainController...
00:33:30.486 [JavaFX Application Thread] INFO  gui.controllers.MainController - MainController inizializzato con successo
00:33:30.819 [JavaFX Application Thread] INFO  gui.MainApp - Applicazione avviata con successo
00:33:33.488 [JavaFX Application Thread] INFO  gui.controllers.MainController - Nuova Analisi cliccato
00:33:33.488 [JavaFX Application Thread] INFO  gui.controllers.MainController - Navigazione verso la vista: home.fxml
00:33:33.508 [JavaFX Application Thread] INFO  gui.controllers.HomeController - Inizializzazione HomeController...
00:33:33.508 [JavaFX Application Thread] INFO  gui.controllers.HomeController - Sorgente dati cambiata in: Hardcoded (PlayTennis)
00:33:33.509 [JavaFX Application Thread] INFO  gui.controllers.HomeController - HomeController inizializzato con successo
00:33:36.805 [JavaFX Application Thread] INFO  gui.controllers.HomeController - Sorgente dati cambiata in: Database
00:33:46.009 [JavaFX Application Thread] INFO  gui.controllers.HomeController - Avvio clustering con parametri:
00:33:46.009 [JavaFX Application Thread] INFO  gui.controllers.HomeController -   Sorgente dati: DATABASE
00:33:46.009 [JavaFX Application Thread] INFO  gui.controllers.HomeController -   Radius: 0.2
00:33:46.010 [JavaFX Application Thread] INFO  gui.controllers.HomeController -   Caching abilitato: true
00:33:46.010 [JavaFX Application Thread] INFO  gui.controllers.HomeController -   Logging verboso: false
00:33:46.022 [JavaFX Application Thread] INFO  g.controllers.ClusteringController - Inizializzazione ClusteringController...
00:33:46.024 [JavaFX Application Thread] INFO  g.controllers.ClusteringController - Task di clustering avviato
00:33:46.024 [JavaFX Application Thread] INFO  g.controllers.ClusteringController - ClusteringController inizializzato con successo
00:33:46.024 [Thread-3] INFO  gui.services.DataImportService - Caricamento dataset da database: localhost:3306/MapDB - tabella: MapDB
00:33:46.029 [JavaFX Application Thread] INFO  gui.controllers.HomeController - Navigazione a vista clustering completata
00:33:46.238 [Thread-3] INFO  gui.services.DataImportService - Connessione database stabilita
00:33:46.294 [Thread-3] ERROR gui.services.DataImportService - Errore SQL durante caricamento dati
java.sql.SQLException: null
        at qtGUI@1.0.0/database.TableData.getDistinctTransazioni(TableData.java:57)
        at qtGUI@1.0.0/data.Data.<init>(Data.java:102)
        at qtGUI@1.0.0/gui.services.DataImportService.loadDataFromDatabase(DataImportService.java:155)
        at qtGUI@1.0.0/gui.controllers.ClusteringController$1.call(ClusteringController.java:164)
        at qtGUI@1.0.0/gui.controllers.ClusteringController$1.call(ClusteringController.java:120)
        at javafx.graphics@21.0.1/javafx.concurrent.Task$TaskCallable.call(Task.java:1399)
        at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
        at java.base/java.lang.Thread.run(Thread.java:1583)
00:33:46.296 [JavaFX Application Thread] ERROR g.controllers.ClusteringController - Clustering fallito
java.lang.RuntimeException: Errore durante caricamento dataset: null
        at qtGUI@1.0.0/gui.controllers.ClusteringController$1.call(ClusteringController.java:188)
        at qtGUI@1.0.0/gui.controllers.ClusteringController$1.call(ClusteringController.java:120)
        at javafx.graphics@21.0.1/javafx.concurrent.Task$TaskCallable.call(Task.java:1399)
        at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
        at java.base/java.lang.Thread.run(Thread.java:1583)
Caused by: java.sql.SQLException: null
        at qtGUI@1.0.0/database.TableData.getDistinctTransazioni(TableData.java:57)
        at qtGUI@1.0.0/data.Data.<init>(Data.java:102)
        at qtGUI@1.0.0/gui.services.DataImportService.loadDataFromDatabase(DataImportService.java:155)
        at qtGUI@1.0.0/gui.controllers.ClusteringController$1.call(ClusteringController.java:164)
        ... 4 common frames omitted
00:33:57.114 [JavaFX Application Thread] INFO  g.controllers.ClusteringController - Pulsante annulla cliccato
