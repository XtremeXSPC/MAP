# MAP - Quality Threshold Clustering

Progetto universitario per il corso di Metodi Avanzati di Programmazione. Il repository raccoglie una implementazione in Java dell'algoritmo di clustering Quality Threshold, organizzata in moduli separati per backend, interfaccia grafica, client testuale e test.

## Moduli

| Modulo     | Ruolo                                                                                              | Tecnologie principali |
| ---------- | -------------------------------------------------------------------------------------------------- | --------------------- |
| `qtServer` | Backend dell'algoritmo QT, gestione dataset, JDBC, server socket multi-client                      | Java, JDBC, socket    |
| `qtGUI`    | Applicazione desktop per eseguire clustering in locale, visualizzare risultati ed esportare report | JavaFX, Maven, XChart |
| `qtClient` | Client testuale che dialoga con `qtServer` tramite socket                                          | Java                  |
| `qtExt`    | Test e utility di supporto                                                                         | Java                  |

## Requisiti

- `JDK 21` per compilare ed eseguire l'intero progetto in modo uniforme.
- `Maven` per il modulo `qtGUI`.
- `make` per usare i target di orchestrazione del repository.
- `MySQL` solo se si vogliono usare le funzionalita' di caricamento da database.

Note operative:

- `qtGUI` usa Maven e include i sorgenti di `qtServer` nel proprio classpath di build.
- `qtServer` e `qtExt` usano il driver JDBC locale in `qtServer/JDBC/mysql-connector-java-9.5.0.jar`.
- La GUI non richiede l'avvio del server socket: puo' eseguire clustering direttamente in locale.

## Build

Compilazione completa:

```bash
make all
```

Compilazione per modulo:

```bash
make client
make server
make ext
make gui
```

Creazione dei JAR:

```bash
make jar
```

Esecuzione della suite di test del modulo `qtExt`:

```bash
make test
```

## Esecuzione

Avvio della GUI:

```bash
make run-gui
```

Avvio del server socket:

```bash
make run-server PORT=8080
```

Avvio del client testuale:

```bash
make run-client IP=localhost PORT=8080
```

La coppia `qtClient`/`qtServer` serve per la modalita' client-server. Per l'uso locale tramite interfaccia grafica e' sufficiente `qtGUI`.

## Sorgenti dati supportate

- Dataset hardcoded `PlayTennis`.
- Dataset `Iris` incluso nelle risorse della GUI.
- File CSV.
- Tabelle MySQL.
- File serializzati `.dmp` per il salvataggio e il riutilizzo di clustering gia' eseguiti.

## Struttura del repository

```text
MAP/
├── Makefile
├── README.md
├── data/                      # Dataset CSV di esempio e benchmark
├── docs/
│   ├── manuale_utente/
│   │   ├── gui_images/        # Immagini della GUI
│   │   └── latex/             # Manuale utente in LaTeX
│   └── uml/
├── qtClient/                  # Client testuale
├── qtExt/                     # Test e utility
├── qtGUI/                     # Applicazione JavaFX
├── qtServer/                  # Backend e server socket
└── setup_database.sql         # Script SQL di esempio
```

## Documentazione

- Manuale utente LaTeX: `docs/manuale_utente/latex`
- README specifici di modulo: `qtServer/README.md`, `qtGUI/README.md`, `qtClient/README.md`, `qtExt/README.md`
- Diagrammi UML: `docs/uml`

## Comandi utili

```bash
make help
make javadoc
make uml
make clean
```
