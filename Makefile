# ============================================================================ #
# Makefile per Quality Threshold Clustering - Client/Server
# ============================================================================ #
# Progetto: MAP - Quality Threshold Clustering Algorithm
# Linguaggio: Java
# Struttura:
#   - qtClient: Applicazione client (package default e keyboardinput)
#   - qtServer: Applicazione server (package data, mining, database, server)
#   - qtExt: Extensions (test suite e utility per benchmark)
#   - qtGUI: Interfaccia grafica (JavaFX, Maven)
# ============================================================================ #

# Variabili di configurazione
# ---------------------------------------------------------------------------- #
JAVAC = javac
JAVA = java
JAR = jar

# Flag del compilatore
JFLAGS = -encoding UTF-8 -d

# Directory
CLIENT_SRC = qtClient/src
CLIENT_BIN = qtClient/bin
SERVER_SRC = qtServer/src
SERVER_BIN = qtServer/bin
EXT_SRC = qtExt
EXT_BIN = qtExt/bin
GUI_DIR = qtGUI
DOCS_DIR = docs/javadoc
UML_DIR = docs/uml
UML_OUTPUT_DIR = docs/uml/generated

# Main classes
CLIENT_MAIN = MainTest
SERVER_MAIN = server.MultiServer
GUI_MAIN = gui.Launcher

# JAR files
CLIENT_JAR = qtClient.jar
SERVER_JAR = qtServer.jar
GUI_JAR = qtGUI/target/qtGUI-1.0.0.jar

# MySQL JDBC Driver
MYSQL_DRIVER = qtServer/JDBC/mysql-connector-java-8.0.17.jar

# Classpath con MySQL driver
SERVER_CLASSPATH = $(SERVER_BIN):$(MYSQL_DRIVER)
EXT_CLASSPATH = $(EXT_BIN):$(SERVER_BIN):$(MYSQL_DRIVER)

# Trova tutti i file sorgente
CLIENT_SOURCES = $(shell find $(CLIENT_SRC) -name '*.java')
SERVER_SOURCES = $(shell find $(SERVER_SRC) -name '*.java')
EXT_TESTS = $(shell find $(EXT_SRC)/tests -name '*.java' 2>/dev/null)
EXT_UTILITY = $(shell find $(EXT_SRC)/utility -name '*.java' 2>/dev/null)
UML_SOURCES = $(shell find $(UML_DIR) -name '*.puml' 2>/dev/null)

# File marker per tracking compilazione
CLIENT_MARKER = $(CLIENT_BIN)/.compiled
SERVER_MARKER = $(SERVER_BIN)/.compiled
EXT_MARKER = $(EXT_BIN)/.compiled

# Colori per output (opzionali)
GREEN = \033[0;32m
BLUE = \033[0;34m
YELLOW = \033[1;33m
NC = \033[0m # No Color

# ============================================================================ #
# Target principali
# ============================================================================ #

# Target di default: compila tutto
.PHONY: all
all: client server ext gui
	@echo "$(GREEN)✓ Compilazione completata con successo!$(NC)"

# Target help: mostra utilizzo
.PHONY: help
help:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Makefile - Quality Threshold Clustering$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo ""
	@echo "$(YELLOW)Compilazione:$(NC)"
	@echo "  make all             - Compila client, server, extensions e GUI"
	@echo "  make client          - Compila solo qtClient"
	@echo "  make server          - Compila solo qtServer"
	@echo "  make ext             - Compila solo qtExt (test e utility)"
	@echo "  make gui             - Compila solo qtGUI (Maven)"
	@echo ""
	@echo "$(YELLOW)JAR:$(NC)"
	@echo "  make jar             - Crea JAR per client, server e GUI"
	@echo "  make client-jar      - Crea solo qtClient.jar"
	@echo "  make server-jar      - Crea solo qtServer.jar"
	@echo "  make gui-jar         - Crea solo qtGUI JAR (Maven package)"
	@echo ""
	@echo "$(YELLOW)Esecuzione:$(NC)"
	@echo "  make run-client      - Esegui client (args: IP=localhost PORT=8080)"
	@echo "  make run-server      - Esegui server (args: PORT=8080)"
	@echo "  make run-gui         - Esegui GUI (Maven)"
	@echo ""
	@echo "$(YELLOW)Testing:$(NC)"
	@echo "  make test            - Esegui tutti i test"
	@echo "  make test-distance   - Test calcolo distanze"
	@echo "  make test-qt         - Test algoritmo QT"
	@echo "  make test-cluster    - Test operazioni cluster"
	@echo "  make test-data       - Test operazioni data"
	@echo "  make test-iterators  - Test iteratori"
	@echo "  make test-continuous - Test attributi continui"
	@echo ""
	@echo "$(YELLOW)Documentazione:$(NC)"
	@echo "  make javadoc         - Genera documentazione JavaDoc completa"
	@echo "  make javadoc-client  - Genera JavaDoc solo per qtClient"
	@echo "  make javadoc-server  - Genera JavaDoc solo per qtServer"
	@echo "  make javadoc-ext     - Genera JavaDoc solo per qtExt"
	@echo "  make javadoc-gui     - Genera JavaDoc solo per qtGUI (Maven)"
	@echo ""
	@echo "$(YELLOW)Diagrammi UML:$(NC)"
	@echo "  make uml             - Genera tutti i diagrammi (SVG + PNG)"
	@echo "  make uml-svg         - Genera solo diagrammi SVG"
	@echo "  make uml-png         - Genera solo diagrammi PNG"
	@echo "  make clean-uml       - Rimuove diagrammi generati"
	@echo ""
	@echo "$(YELLOW)Pulizia:$(NC)"
	@echo "  make clean           - Rimuove tutti i file compilati"
	@echo "  make clean-client    - Rimuove solo file compilati del client"
	@echo "  make clean-server    - Rimuove solo file compilati del server"
	@echo "  make clean-ext       - Rimuove solo file compilati di qtExt"
	@echo "  make clean-gui       - Rimuove build Maven GUI"
	@echo "  make clean-jar       - Rimuove solo i file JAR"
	@echo "  make clean-javadoc   - Rimuove la documentazione JavaDoc"
	@echo ""
	@echo "$(YELLOW)Utility:$(NC)"
	@echo "  make rebuild         - Pulisci e ricompila tutto"
	@echo "  make validate        - Verifica struttura progetto"
	@echo "  make package         - Crea distribuzione completa"
	@echo "  make help            - Mostra questo messaggio"
	@echo ""
	@echo "$(YELLOW)Esempi:$(NC)"
	@echo "  make all"
	@echo "  make jar"
	@echo "  make javadoc"
	@echo "  make uml"
	@echo "  make run-server PORT=9999"
	@echo "  make run-client IP=127.0.0.1 PORT=9999"
	@echo "  make run-gui"
	@echo ""

# ============================================================================ #
# Compilazione
# ============================================================================ #

# Compila client
.PHONY: client
client: $(CLIENT_MARKER)

$(CLIENT_MARKER): $(CLIENT_SOURCES)
	@echo "$(BLUE)→ Compilazione qtClient...$(NC)"
	@mkdir -p $(CLIENT_BIN)
	$(JAVAC) $(JFLAGS) $(CLIENT_BIN) $(CLIENT_SOURCES)
	@touch $(CLIENT_MARKER)
	@echo "$(GREEN)✓ qtClient compilato$(NC)"

# Compila server
.PHONY: server
server: $(SERVER_MARKER)

$(SERVER_MARKER): $(SERVER_SOURCES)
	@echo "$(BLUE)→ Compilazione qtServer...$(NC)"
	@mkdir -p $(SERVER_BIN)
	$(JAVAC) $(JFLAGS) $(SERVER_BIN) $(SERVER_SOURCES)
	@touch $(SERVER_MARKER)
	@echo "$(GREEN)✓ qtServer compilato$(NC)"

# Compila qtExt (test e utility)
.PHONY: ext
ext: $(EXT_MARKER)

$(EXT_MARKER): $(EXT_TESTS) $(EXT_UTILITY) server
	@echo "$(BLUE)→ Compilazione qtExt (test e utility)...$(NC)"
	@mkdir -p $(EXT_BIN)
	@if [ -n "$(EXT_TESTS)" ]; then $(JAVAC) $(JFLAGS) $(EXT_BIN) -cp $(SERVER_CLASSPATH) $(EXT_TESTS); fi
	@if [ -n "$(EXT_UTILITY)" ]; then $(JAVAC) $(JFLAGS) $(EXT_BIN) -cp $(SERVER_CLASSPATH) $(EXT_UTILITY); fi
	@touch $(EXT_MARKER)
	@echo "$(GREEN)✓ qtExt compilato$(NC)"

# Compila qtGUI (Maven)
.PHONY: gui
gui:
	@echo "$(BLUE)→ Compilazione qtGUI (Maven)...$(NC)"
	@if [ -d "$(GUI_DIR)" ]; then \
		cd $(GUI_DIR) && mvn compile -q; \
		echo "$(GREEN)✓ qtGUI compilato$(NC)"; \
	else \
		echo "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)"; \
	fi

# ============================================================================ #
# Creazione JAR
# ============================================================================ #

# Crea tutti i JAR
.PHONY: jar
jar: client-jar server-jar gui-jar
	@echo "$(GREEN)✓ File JAR creati con successo!$(NC)"
	@if ls *.jar 1> /dev/null 2>&1; then ls -lh *.jar; fi
	@if [ -f "$(GUI_JAR)" ]; then ls -lh $(GUI_JAR); fi

# Crea JAR del client
.PHONY: client-jar
client-jar: client
	@echo "$(BLUE)→ Creazione $(CLIENT_JAR)...$(NC)"
	@cd $(CLIENT_BIN) && $(JAR) cfe ../../$(CLIENT_JAR) $(CLIENT_MAIN) .
	@echo "$(GREEN)✓ $(CLIENT_JAR) creato$(NC)"

# Crea JAR del server
.PHONY: server-jar
server-jar: server
	@echo "$(BLUE)→ Creazione $(SERVER_JAR)...$(NC)"
	@cd $(SERVER_BIN) && $(JAR) cfe ../../$(SERVER_JAR) $(SERVER_MAIN) .
	@echo "$(GREEN)✓ $(SERVER_JAR) creato$(NC)"

# Crea JAR della GUI (Maven)
.PHONY: gui-jar
gui-jar:
	@echo "$(BLUE)→ Creazione qtGUI JAR (Maven package)...$(NC)"
	@if [ -d "$(GUI_DIR)" ]; then \
		cd $(GUI_DIR) && mvn package -DskipTests -q; \
		echo "$(GREEN)✓ qtGUI JAR creato in $(GUI_JAR)$(NC)"; \
	else \
		echo "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)"; \
	fi

# ============================================================================ #
# Esecuzione
# ============================================================================ #

# Variabili per esecuzione (possono essere sovrascritte da linea comando)
IP ?= localhost
PORT ?= 8080

# Esegui server
.PHONY: run-server
run-server: server
	@echo "$(BLUE)→ Avvio qtServer sulla porta $(PORT)...$(NC)"
	$(JAVA) -cp $(SERVER_CLASSPATH) $(SERVER_MAIN) $(PORT)

# Esegui client
.PHONY: run-client
run-client: client
	@echo "$(BLUE)→ Avvio qtClient (connessione a $(IP):$(PORT))...$(NC)"
	$(JAVA) -cp $(CLIENT_BIN) $(CLIENT_MAIN) $(IP) $(PORT)

# Esegui server da JAR
.PHONY: run-server-jar
run-server-jar: server-jar
	@echo "$(BLUE)→ Avvio server da JAR sulla porta $(PORT)...$(NC)"
	$(JAVA) -jar $(SERVER_JAR) $(PORT)

# Esegui client da JAR
.PHONY: run-client-jar
run-client-jar: client-jar
	@echo "$(BLUE)→ Avvio client da JAR (connessione a $(IP):$(PORT))...$(NC)"
	$(JAVA) -jar $(CLIENT_JAR) $(IP) $(PORT)

# Esegui GUI (Maven)
.PHONY: run-gui
run-gui:
	@echo "$(BLUE)→ Avvio qtGUI (JavaFX)...$(NC)"
	@if [ -d "$(GUI_DIR)" ]; then \
		cd $(GUI_DIR) && mvn javafx:run; \
	else \
		echo "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata$(NC)"; \
		exit 1; \
	fi

# Esegui GUI da JAR
.PHONY: run-gui-jar
run-gui-jar: gui-jar
	@echo "$(BLUE)→ Avvio qtGUI da JAR...$(NC)"
	@if [ -f "$(GUI_JAR)" ]; then \
		$(JAVA) -jar $(GUI_JAR); \
	else \
		echo "$(YELLOW)⚠ JAR $(GUI_JAR) non trovato. Esegui 'make gui-jar' prima.$(NC)"; \
		exit 1; \
	fi

# ============================================================================ #
# Testing
# ============================================================================ #

# Esegui tutti i test
.PHONY: test
test: ext
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Esecuzione Test Suite$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo ""
	@$(MAKE) --no-print-directory test-distance
	@echo ""
	@$(MAKE) --no-print-directory test-qt
	@echo ""
	@$(MAKE) --no-print-directory test-cluster
	@echo ""
	@$(MAKE) --no-print-directory test-data
	@echo ""
	@echo "$(GREEN)✓ Test suite completata!$(NC)"

# Test calcolo distanze
.PHONY: test-distance
test-distance: ext
	@echo "$(YELLOW)→ Test Calcolo Distanze...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestDistanceCalculations

# Test algoritmo QT
.PHONY: test-qt
test-qt: ext
	@echo "$(YELLOW)→ Test Algoritmo QT...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestQTAlgorithm

# Test operazioni cluster
.PHONY: test-cluster
test-cluster: ext
	@echo "$(YELLOW)→ Test Operazioni Cluster...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestClusterOperations

# Test operazioni data
.PHONY: test-data
test-data: ext
	@echo "$(YELLOW)→ Test Operazioni Data...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestDataOperations

# Test iteratori e comparatori
.PHONY: test-iterators
test-iterators: ext
	@echo "$(YELLOW)→ Test Iteratori e Comparatori...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestIteratorsComparators

# Test attributi continui (interattivo)
.PHONY: test-continuous
test-continuous: ext
	@echo "$(YELLOW)→ Test Attributi Continui (Interattivo)...$(NC)"
	@$(JAVA) -cp $(EXT_CLASSPATH) tests.TestContinuousAttributes

# ============================================================================ #
# Documentazione JavaDoc
# ============================================================================ #

# Genera tutta la documentazione JavaDoc
.PHONY: javadoc
javadoc: javadoc-client javadoc-server javadoc-ext javadoc-gui
	@echo "$(GREEN)✓ Documentazione JavaDoc generata con successo!$(NC)"
	@echo "$(BLUE)→ Documentazione disponibile in:$(NC)"
	@echo "  - Client: $(DOCS_DIR)/client/index.html"
	@echo "  - Server: $(DOCS_DIR)/server/index.html"
	@echo "  - Extensions: $(DOCS_DIR)/ext/index.html"
	@echo "  - GUI: $(DOCS_DIR)/gui/index.html"

# Genera JavaDoc per qtClient
.PHONY: javadoc-client
javadoc-client:
	@echo "$(BLUE)→ Generazione JavaDoc per qtClient...$(NC)"
	@mkdir -p $(DOCS_DIR)/client
	@javadoc -d $(DOCS_DIR)/client \
		-encoding UTF-8 \
		-charset UTF-8 \
		-docencoding UTF-8 \
		-windowtitle "QT Client Documentation" \
		-doctitle "Quality Threshold Clustering - Client" \
		-header "QT Client" \
		-footer "MAP Project" \
		-author \
		-version \
		-use \
		-private \
		-sourcepath $(CLIENT_SRC) \
		-subpackages keyboardinput \
		$(CLIENT_SRC)/*.java 2>/dev/null || true
	@echo "$(GREEN)✓ JavaDoc qtClient generato in $(DOCS_DIR)/client$(NC)"

# Genera JavaDoc per qtServer
.PHONY: javadoc-server
javadoc-server:
	@echo "$(BLUE)→ Generazione JavaDoc per qtServer...$(NC)"
	@mkdir -p $(DOCS_DIR)/server
	@javadoc -d $(DOCS_DIR)/server \
		-encoding UTF-8 \
		-charset UTF-8 \
		-docencoding UTF-8 \
		-windowtitle "QT Server Documentation" \
		-doctitle "Quality Threshold Clustering - Server" \
		-header "QT Server" \
		-footer "MAP Project" \
		-author \
		-version \
		-use \
		-private \
		-classpath $(MYSQL_DRIVER) \
		-sourcepath $(SERVER_SRC) \
		-subpackages data:mining:database:server \
		2>/dev/null || true
	@echo "$(GREEN)✓ JavaDoc qtServer generato in $(DOCS_DIR)/server$(NC)"

# Genera JavaDoc per qtExt
.PHONY: javadoc-ext
javadoc-ext:
	@echo "$(BLUE)→ Generazione JavaDoc per qtExt...$(NC)"
	@mkdir -p $(DOCS_DIR)/ext
	@if [ -d "$(EXT_SRC)" ]; then \
		javadoc -d $(DOCS_DIR)/ext \
			-encoding UTF-8 \
			-charset UTF-8 \
			-docencoding UTF-8 \
			-windowtitle "QT Extensions Documentation" \
			-doctitle "Quality Threshold Clustering - Extensions" \
			-header "QT Extensions" \
			-footer "MAP Project" \
			-author \
			-version \
			-use \
			-private \
			-classpath $(SERVER_CLASSPATH) \
			-sourcepath $(EXT_SRC) \
			-subpackages tests:utility \
			2>/dev/null || true; \
		echo "$(GREEN)✓ JavaDoc qtExt generato in $(DOCS_DIR)/ext$(NC)"; \
	else \
		echo "$(YELLOW)⚠ Directory $(EXT_SRC) non trovata, skip$(NC)"; \
	fi

# Genera JavaDoc per qtGUI (Maven)
.PHONY: javadoc-gui
javadoc-gui:
	@echo "$(BLUE)→ Generazione JavaDoc per qtGUI (Maven)...$(NC)"
	@if [ -d "$(GUI_DIR)" ]; then \
		mvn -f $(GUI_DIR)/pom.xml javadoc:javadoc \
			-Dmaven.javadoc.failOnError=false \
			-Dmaven.javadoc.failOnWarnings=false \
			-q; \
		mkdir -p $(DOCS_DIR)/gui; \
		if [ -d "$(GUI_DIR)/target/reports/apidocs" ]; then \
			cp -r $(GUI_DIR)/target/reports/apidocs/* $(DOCS_DIR)/gui/; \
			echo "$(GREEN)✓ JavaDoc qtGUI generato in $(DOCS_DIR)/gui$(NC)"; \
		elif [ -d "$(GUI_DIR)/target/site/apidocs" ]; then \
			cp -r $(GUI_DIR)/target/site/apidocs/* $(DOCS_DIR)/gui/; \
			echo "$(GREEN)✓ JavaDoc qtGUI generato in $(DOCS_DIR)/gui$(NC)"; \
		else \
			echo "$(YELLOW)⚠ JavaDoc non generato correttamente$(NC)"; \
		fi; \
	else \
		echo "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)"; \
	fi

# ============================================================================ #
# Diagrammi UML PlantUML
# ============================================================================ #

# Genera tutti i diagrammi (SVG + PNG)
.PHONY: uml
uml: uml-svg uml-png
	@echo "$(GREEN)✓ Diagrammi UML generati con successo!$(NC)"
	@echo "$(BLUE)→ Diagrammi disponibili in:$(NC)"
	@echo "  - SVG: $(UML_OUTPUT_DIR)/svg/"
	@echo "  - PNG: $(UML_OUTPUT_DIR)/png/"

# Genera solo diagrammi SVG
.PHONY: uml-svg
uml-svg:
	@echo "$(BLUE)→ Generazione diagrammi UML (formato SVG)...$(NC)"
	@if [ -z "$(UML_SOURCES)" ]; then \
		echo "$(YELLOW)⚠ Nessun file .puml trovato in $(UML_DIR)$(NC)"; \
	else \
		for puml in $(UML_SOURCES); do \
			rel_path=$${puml#$(UML_DIR)/}; \
			rel_dir=$$(dirname $$rel_path); \
			output_dir="$(UML_OUTPUT_DIR)/svg/$$rel_dir"; \
			mkdir -p "$$output_dir"; \
			echo "  Generando SVG per $$rel_path..."; \
			plantuml -tsvg -o "$(PWD)/$$output_dir" "$$puml" 2>/dev/null || true; \
		done; \
		echo "$(GREEN)✓ Diagrammi SVG generati in $(UML_OUTPUT_DIR)/svg$(NC)"; \
	fi

# Genera solo diagrammi PNG
.PHONY: uml-png
uml-png:
	@echo "$(BLUE)→ Generazione diagrammi UML (formato PNG)...$(NC)"
	@if [ -z "$(UML_SOURCES)" ]; then \
		echo "$(YELLOW)⚠ Nessun file .puml trovato in $(UML_DIR)$(NC)"; \
	else \
		for puml in $(UML_SOURCES); do \
			rel_path=$${puml#$(UML_DIR)/}; \
			rel_dir=$$(dirname $$rel_path); \
			output_dir="$(UML_OUTPUT_DIR)/png/$$rel_dir"; \
			mkdir -p "$$output_dir"; \
			echo "  Generando PNG per $$rel_path..."; \
			plantuml -tpng -o "$(PWD)/$$output_dir" "$$puml" 2>/dev/null || true; \
		done; \
		echo "$(GREEN)✓ Diagrammi PNG generati in $(UML_OUTPUT_DIR)/png$(NC)"; \
	fi

# Lista tutti i file PlantUML
.PHONY: list-uml
list-uml:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(YELLOW)File PlantUML trovati:$(NC)"
	@if [ -z "$(UML_SOURCES)" ]; then \
		echo "  $(YELLOW)Nessun file .puml trovato$(NC)"; \
	else \
		echo "$(UML_SOURCES)" | tr ' ' '\n' | sed 's|^|  - |'; \
		echo ""; \
		echo "$(YELLOW)Totale:$(NC) $(words $(UML_SOURCES)) file"; \
	fi
	@echo ""

# ============================================================================ #
# Pulizia
# ============================================================================ #

# Pulisci tutto
.PHONY: clean
clean: clean-client clean-server clean-ext clean-gui clean-jar clean-javadoc clean-uml
	@echo "$(GREEN)✓ Pulizia completata$(NC)"

# Pulisci solo client
.PHONY: clean-client
clean-client:
	@echo "$(YELLOW)→ Pulizia qtClient...$(NC)"
	@rm -rf $(CLIENT_BIN)
	@echo "$(GREEN)✓ qtClient pulito$(NC)"

# Pulisci solo server
.PHONY: clean-server
clean-server:
	@echo "$(YELLOW)→ Pulizia qtServer...$(NC)"
	@rm -rf $(SERVER_BIN)
	@echo "$(GREEN)✓ qtServer pulito$(NC)"

# Pulisci solo qtExt
.PHONY: clean-ext
clean-ext:
	@echo "$(YELLOW)→ Pulizia qtExt...$(NC)"
	@rm -rf $(EXT_BIN)
	@echo "$(GREEN)✓ qtExt pulito$(NC)"

# Pulisci solo GUI (Maven)
.PHONY: clean-gui
clean-gui:
	@echo "$(YELLOW)→ Pulizia qtGUI (Maven clean)...$(NC)"
	@if [ -d "$(GUI_DIR)" ]; then \
		cd $(GUI_DIR) && mvn clean -q; \
		echo "$(GREEN)✓ qtGUI pulito$(NC)"; \
	else \
		echo "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)"; \
	fi

# Pulisci solo JAR
.PHONY: clean-jar
clean-jar:
	@echo "$(YELLOW)→ Rimozione file JAR...$(NC)"
	@rm -f $(CLIENT_JAR) $(SERVER_JAR)
	@echo "$(GREEN)✓ File JAR rimossi$(NC)"

# Pulisci diagrammi UML generati
.PHONY: clean-uml
clean-uml:
	@echo "$(YELLOW)→ Rimozione diagrammi UML generati...$(NC)"
	@rm -rf $(UML_OUTPUT_DIR)
	@echo "$(GREEN)✓ Diagrammi UML rimossi$(NC)"

# Pulisci solo JavaDoc
.PHONY: clean-javadoc
clean-javadoc:
	@echo "$(YELLOW)→ Rimozione documentazione JavaDoc...$(NC)"
	@rm -rf $(DOCS_DIR)
	@echo "$(GREEN)✓ JavaDoc rimosso$(NC)"

# Ricompila tutto da zero
.PHONY: rebuild
rebuild: clean all
	@echo "$(GREEN)✓ Rebuild completato!$(NC)"

# ============================================================================ #
# Validazione e Packaging
# ============================================================================ #

# Valida la struttura del progetto
.PHONY: validate
validate:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Validazione Struttura Progetto$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo ""
	@echo "$(YELLOW)Verificando directory...$(NC)"
	@errors=0; \
	for dir in $(CLIENT_SRC) $(SERVER_SRC) $(GUI_DIR); do \
		if [ -d "$$dir" ]; then \
			echo "  $(GREEN)✓$(NC) $$dir"; \
		else \
			echo "  $(YELLOW)✗$(NC) $$dir (mancante)"; \
			errors=$$((errors + 1)); \
		fi; \
	done; \
	echo ""; \
	echo "$(YELLOW)Verificando strumenti...$(NC)"; \
	if which $(JAVAC) >/dev/null 2>&1; then \
		echo "  $(GREEN)✓$(NC) javac ($$($(JAVAC) -version 2>&1))"; \
	else \
		echo "  $(YELLOW)✗$(NC) javac (non trovato)"; \
		errors=$$((errors + 1)); \
	fi; \
	if which $(JAVA) >/dev/null 2>&1; then \
		echo "  $(GREEN)✓$(NC) java ($$($(JAVA) -version 2>&1 | head -1))"; \
	else \
		echo "  $(YELLOW)✗$(NC) java (non trovato)"; \
		errors=$$((errors + 1)); \
	fi; \
	if which mvn >/dev/null 2>&1; then \
		echo "  $(GREEN)✓$(NC) maven ($$(mvn -version 2>&1 | head -1))"; \
	else \
		echo "  $(YELLOW)⚠$(NC) maven (non trovato - necessario per qtGUI)"; \
	fi; \
	if which plantuml >/dev/null 2>&1; then \
		echo "  $(GREEN)✓$(NC) plantuml"; \
	else \
		echo "  $(YELLOW)⚠$(NC) plantuml (non trovato - necessario per UML)"; \
	fi; \
	echo ""; \
	echo "$(YELLOW)Verificando file sorgenti...$(NC)"; \
	echo "  Client:  $(words $(CLIENT_SOURCES)) file Java"; \
	echo "  Server:  $(words $(SERVER_SOURCES)) file Java"; \
	echo "  UML:     $(words $(UML_SOURCES)) file PlantUML"; \
	echo ""; \
	if [ $$errors -eq 0 ]; then \
		echo "$(GREEN)✓ Validazione completata con successo!$(NC)"; \
	else \
		echo "$(YELLOW)⚠ Validazione completata con $$errors errori$(NC)"; \
		exit 1; \
	fi

# Crea un pacchetto completo di distribuzione
.PHONY: package
package: clean all jar javadoc uml
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Creazione Pacchetto Distribuzione$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo ""
	@PKG_NAME="qtClustering-$$(date +%Y%m%d-%H%M%S)"; \
	PKG_DIR="dist/$$PKG_NAME"; \
	echo "$(YELLOW)Creando directory pacchetto: $$PKG_DIR$(NC)"; \
	mkdir -p "$$PKG_DIR"/{bin,docs,diagrams,src}; \
	echo ""; \
	echo "$(YELLOW)Copiando JAR files...$(NC)"; \
	cp -v $(CLIENT_JAR) "$$PKG_DIR/bin/" 2>/dev/null || true; \
	cp -v $(SERVER_JAR) "$$PKG_DIR/bin/" 2>/dev/null || true; \
	if [ -f "$(GUI_JAR)" ]; then cp -v $(GUI_JAR) "$$PKG_DIR/bin/qtGUI.jar"; fi; \
	echo ""; \
	echo "$(YELLOW)Copiando documentazione...$(NC)"; \
	if [ -d "$(DOCS_DIR)" ]; then cp -r $(DOCS_DIR)/* "$$PKG_DIR/docs/"; fi; \
	if [ -d "$(UML_OUTPUT_DIR)" ]; then cp -r $(UML_OUTPUT_DIR)/* "$$PKG_DIR/diagrams/"; fi; \
	cp -v README.md CLAUDE.md "$$PKG_DIR/" 2>/dev/null || true; \
	echo ""; \
	echo "$(YELLOW)Copiando sorgenti...$(NC)"; \
	cp -r $(CLIENT_SRC) "$$PKG_DIR/src/qtClient"; \
	cp -r $(SERVER_SRC) "$$PKG_DIR/src/qtServer"; \
	if [ -d "$(GUI_DIR)/src" ]; then cp -r $(GUI_DIR)/src "$$PKG_DIR/src/qtGUI"; fi; \
	echo ""; \
	echo "$(YELLOW)Creando archivio...$(NC)"; \
	cd dist && tar czf "$$PKG_NAME.tar.gz" "$$PKG_NAME"; \
	echo ""; \
	echo "$(GREEN)✓ Pacchetto creato con successo!$(NC)"; \
	echo "$(BLUE)→ File: dist/$$PKG_NAME.tar.gz$(NC)"; \
	ls -lh "dist/$$PKG_NAME.tar.gz"

# ============================================================================ #
# Utility e debug
# ============================================================================ #

# Mostra informazioni progetto
.PHONY: info
info:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Informazioni Progetto$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(YELLOW)Client:$(NC)"
	@echo "  Sorgenti:    $(CLIENT_SRC)"
	@echo "  Binari:      $(CLIENT_BIN)"
	@echo "  Main class:  $(CLIENT_MAIN)"
	@echo "  File Java:   $(words $(CLIENT_SOURCES))"
	@echo ""
	@echo "$(YELLOW)Server:$(NC)"
	@echo "  Sorgenti:    $(SERVER_SRC)"
	@echo "  Binari:      $(SERVER_BIN)"
	@echo "  Main class:  $(SERVER_MAIN)"
	@echo "  File Java:   $(words $(SERVER_SOURCES))"
	@echo ""
	@echo "$(YELLOW)Extensions (qtExt):$(NC)"
	@echo "  Sorgenti:    $(EXT_SRC)"
	@echo "  Binari:      $(EXT_BIN)"
	@echo "  Test:        $(words $(EXT_TESTS))"
	@echo "  Utility:     $(words $(EXT_UTILITY))"
	@echo ""
	@echo "$(YELLOW)GUI (qtGUI):$(NC)"
	@echo "  Directory:   $(GUI_DIR)"
	@if [ -d "$(GUI_DIR)" ]; then \
		echo "  Maven:       $(GREEN)✓ progetto presente$(NC)"; \
		if [ -f "$(GUI_JAR)" ]; then \
			echo "  JAR:         $(GREEN)✓ $(GUI_JAR)$(NC)"; \
		else \
			echo "  JAR:         $(YELLOW)✗ non compilato$(NC)"; \
		fi; \
	else \
		echo "  Maven:       $(YELLOW)✗ non presente$(NC)"; \
	fi
	@echo ""
	@echo "$(YELLOW)UML:$(NC)"
	@echo "  Directory:   $(UML_DIR)"
	@echo "  File .puml:  $(words $(UML_SOURCES))"
	@if [ -d "$(UML_OUTPUT_DIR)" ]; then \
		echo "  Generati:    $(GREEN)✓ $(UML_OUTPUT_DIR)$(NC)"; \
	else \
		echo "  Generati:    $(YELLOW)✗ non generati$(NC)"; \
	fi
	@echo ""
	@echo "$(YELLOW)Stato compilazione:$(NC)"
	@if [ -f $(CLIENT_MARKER) ]; then echo "  Client:  $(GREEN)✓ compilato$(NC)"; else echo "  Client:  $(YELLOW)✗ non compilato$(NC)"; fi
	@if [ -f $(SERVER_MARKER) ]; then echo "  Server:  $(GREEN)✓ compilato$(NC)"; else echo "  Server:  $(YELLOW)✗ non compilato$(NC)"; fi
	@if [ -f $(EXT_MARKER) ]; then echo "  qtExt:   $(GREEN)✓ compilato$(NC)"; else echo "  qtExt:   $(YELLOW)✗ non compilato$(NC)"; fi
	@echo ""
	@echo "$(YELLOW)File JAR:$(NC)"
	@if [ -f $(CLIENT_JAR) ]; then echo "  $(CLIENT_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(CLIENT_JAR) | cut -f1))"; else echo "  $(CLIENT_JAR): $(YELLOW)✗ non presente$(NC)"; fi
	@if [ -f $(SERVER_JAR) ]; then echo "  $(SERVER_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(SERVER_JAR) | cut -f1))"; else echo "  $(SERVER_JAR): $(YELLOW)✗ non presente$(NC)"; fi
	@if [ -f $(GUI_JAR) ]; then echo "  $(GUI_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(GUI_JAR) | cut -f1))"; else echo "  GUI JAR: $(YELLOW)✗ non presente$(NC)"; fi
	@echo ""

# Verifica dipendenze esterne (per esempio database)
.PHONY: check-deps
check-deps:
	@echo "$(BLUE)→ Verifica dipendenze...$(NC)"
	@which $(JAVAC) > /dev/null || (echo "$(YELLOW)⚠ javac non trovato!$(NC)" && exit 1)
	@which $(JAVA) > /dev/null || (echo "$(YELLOW)⚠ java non trovato!$(NC)" && exit 1)
	@which $(JAR) > /dev/null || (echo "$(YELLOW)⚠ jar non trovato!$(NC)" && exit 1)
	@echo "$(GREEN)✓ Tutte le dipendenze sono disponibili$(NC)"
	@echo ""
	@$(JAVAC) -version
	@$(JAVA) -version

# Lista tutti i file sorgente
.PHONY: list-sources
list-sources:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(YELLOW)File sorgente qtClient:$(NC)"
	@echo "$(CLIENT_SOURCES)" | tr ' ' '\n'
	@echo ""
	@echo "$(YELLOW)File sorgente qtServer:$(NC)"
	@echo "$(SERVER_SOURCES)" | tr ' ' '\n'
	@echo ""
	@echo "$(YELLOW)File sorgente qtExt (Test):$(NC)"
	@echo "$(EXT_TESTS)" | tr ' ' '\n'
	@echo ""
	@echo "$(YELLOW)File sorgente qtExt (Utility):$(NC)"
	@echo "$(EXT_UTILITY)" | tr ' ' '\n'

# ============================================================================ #