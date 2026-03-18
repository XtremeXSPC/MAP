# ================================================================================= #
# ----------- Makefile per Quality Threshold Clustering - Client/Server ----------- #
# ================================================================================= #
# Progetto: MAP - Quality Threshold Clustering Algorithm
# Linguaggio: Java
# Struttura Modulare:
#   - qtClient: Applicazione client (package default e keyboardinput)
#   - qtServer: Applicazione server (package data, mining, database, server)
#   - qtExt: Extensions (test suite e utility per benchmark)
#   - qtGUI: Interfaccia grafica (JavaFX, Maven)
#
# Ogni modulo ha il proprio Makefile in qtModule/Makefile.
# Questo file orchestra i moduli e gestisce i target di progetto.
# ================================================================================= #

# Include configurazione comune.
include make/common.mk

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Directory moduli.
CLIENT_DIR = qtClient
SERVER_DIR = qtServer
EXT_DIR = qtExt
GUI_DIR = qtGUI

# Directory documentazione e UML.
DOCS_DIR = docs/javadoc
UML_DIR = docs/uml
UML_OUTPUT_DIR = docs/uml/generated

# File JAR (posizioni nelle directory dei moduli).
CLIENT_JAR = $(CLIENT_DIR)/qtClient.jar
SERVER_JAR = $(SERVER_DIR)/qtServer.jar
GUI_JAR = $(GUI_DIR)/target/qtGUI-1.0.0.jar

# Trova file UML.
UML_SOURCES = $(shell find $(UML_DIR) -name '*.puml' 2>/dev/null)

# Variabili per esecuzione (possono essere sovrascritte da linea comando).
IP ?= localhost
PORT ?= 8080

# =============================== TARGET PRINCIPALI =============================== #

# Target di default: compila tutto.
.PHONY: all
all: client server ext gui
	@printf "$(GREEN)✓ Compilazione completata con successo!$(NC)\n"

# Target help: mostra utilizzo.
.PHONY: help
help:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# --------------- Makefile - QT Clustering --------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Compilazione:$(NC)\n"
	@printf "  make all             - Compila client, server, extensions e GUI\n"
	@printf "  make client          - Compila solo qtClient\n"
	@printf "  make server          - Compila solo qtServer\n"
	@printf "  make ext             - Compila solo qtExt (test e utility)\n"
	@printf "  make gui             - Compila solo qtGUI (Maven)\n"
	@printf "\n"
	@printf "$(YELLOW)JAR:$(NC)\n"
	@printf "  make jar             - Crea JAR per client, server e GUI\n"
	@printf "  make client-jar      - Crea solo qtClient.jar\n"
	@printf "  make server-jar      - Crea solo qtServer.jar\n"
	@printf "  make gui-jar         - Crea solo qtGUI JAR (Maven package)\n"
	@printf "\n"
	@printf "$(YELLOW)Esecuzione:$(NC)\n"
	@printf "  make run-client      - Esegui client (args: IP=localhost PORT=8080)\n"
	@printf "  make run-server      - Esegui server (args: PORT=8080)\n"
	@printf "  make run-gui         - Esegui GUI (Maven)\n"
	@printf "\n"
	@printf "$(YELLOW)Testing:$(NC)\n"
	@printf "  make test            - Esegui tutti i test\n"
	@printf "  make test-distance   - Test calcolo distanze\n"
	@printf "  make test-qt         - Test algoritmo QT\n"
	@printf "  make test-cluster    - Test operazioni cluster\n"
	@printf "  make test-data       - Test operazioni data\n"
	@printf "  make test-iterators  - Test iteratori\n"
	@printf "  make test-continuous - Test attributi continui\n"
	@printf "\n"
	@printf "$(YELLOW)Documentazione:$(NC)\n"
	@printf "  make javadoc         - Genera documentazione JavaDoc completa\n"
	@printf "  make javadoc-client  - Genera JavaDoc solo per qtClient\n"
	@printf "  make javadoc-server  - Genera JavaDoc solo per qtServer\n"
	@printf "  make javadoc-ext     - Genera JavaDoc solo per qtExt\n"
	@printf "  make javadoc-gui     - Genera JavaDoc solo per qtGUI (Maven)\n"
	@printf "\n"
	@printf "$(YELLOW)Diagrammi UML:$(NC)\n"
	@printf "  make uml             - Genera tutti i diagrammi (SVG + PNG)\n"
	@printf "  make uml-svg         - Genera solo diagrammi SVG\n"
	@printf "  make uml-png         - Genera solo diagrammi PNG\n"
	@printf "  make clean-uml       - Rimuove diagrammi generati\n"
	@printf "\n"
	@printf "$(YELLOW)Pulizia:$(NC)\n"
	@printf "  make clean           - Rimuove tutti i file compilati\n"
	@printf "  make clean-client    - Rimuove solo file compilati del client\n"
	@printf "  make clean-server    - Rimuove solo file compilati del server\n"
	@printf "  make clean-ext       - Rimuove solo file compilati di qtExt\n"
	@printf "  make clean-gui       - Rimuove build Maven GUI\n"
	@printf "  make clean-jar       - Rimuove solo i file JAR\n"
	@printf "  make clean-javadoc   - Rimuove la documentazione JavaDoc\n"
	@printf "\n"
	@printf "$(YELLOW)Utility:$(NC)\n"
	@printf "  make rebuild         - Pulisci e ricompila tutto\n"
	@printf "  make validate        - Verifica struttura progetto\n"
	@printf "  make package         - Crea distribuzione completa\n"
	@printf "  make help            - Mostra questo messaggio\n"
	@printf "\n"
	@printf "$(YELLOW)Esempi:$(NC)\n"
	@printf "  make all\n"
	@printf "  make jar\n"
	@printf "  make javadoc\n"
	@printf "  make uml\n"
	@printf "  make run-server PORT=9999\n"
	@printf "  make run-client IP=127.0.0.1 PORT=9999\n"
	@printf "  make run-gui\n"
	@printf "\n"

# ======================= COMPILAZIONE (DELEGATA AI MODULI) ======================= #

# Compila qtClient.
.PHONY: client
client:
	@$(MAKE) -C $(CLIENT_DIR) compile

# Compila qtServer.
.PHONY: server
server:
	@$(MAKE) -C $(SERVER_DIR) compile

# Compila qtExt.
.PHONY: ext
ext:
	@$(MAKE) -C $(EXT_DIR) compile

# Compila qtGUI.
.PHONY: gui
gui:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) compile; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)\n"; \
	fi

# ======================= CREAZIONE JAR (DELEGATA AI MODULI) ======================= #

# Crea tutti i JAR.
.PHONY: jar
jar: client-jar server-jar gui-jar
	@printf "$(GREEN)✓ File JAR creati con successo!$(NC)\n"
	@printf "$(BLUE)→ JAR disponibili:$(NC)\n"
	@if [ -f "$(CLIENT_JAR)" ]; then ls -lh $(CLIENT_JAR); fi
	@if [ -f "$(SERVER_JAR)" ]; then ls -lh $(SERVER_JAR); fi
	@if [ -f "$(GUI_JAR)" ]; then ls -lh $(GUI_JAR); fi

# Crea JAR di qtClient.
.PHONY: client-jar
client-jar:
	@$(MAKE) -C $(CLIENT_DIR) jar

# Crea JAR di qtServer.
.PHONY: server-jar
server-jar:
	@$(MAKE) -C $(SERVER_DIR) jar

# Crea JAR della GUI.
.PHONY: gui-jar
gui-jar:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) jar; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)\n"; \
	fi

# ======================== ESECUZIONE (DELEGATA AI MODULI) ======================== #

# Esegui qtServer.
.PHONY: run-server
run-server:
	@$(MAKE) -C $(SERVER_DIR) run PORT=$(PORT)

# Esegui qtClient.
.PHONY: run-client
run-client:
	@$(MAKE) -C $(CLIENT_DIR) run IP=$(IP) PORT=$(PORT)

# Esegui qtServer da JAR.
.PHONY: run-server-jar
run-server-jar:
	@$(MAKE) -C $(SERVER_DIR) run-jar PORT=$(PORT)

# Esegui qtClient da JAR.
.PHONY: run-client-jar
run-client-jar:
	@$(MAKE) -C $(CLIENT_DIR) run-jar IP=$(IP) PORT=$(PORT)

# Esegui GUI.
.PHONY: run-gui
run-gui:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) run; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata$(NC)\n"; \
		exit 1; \
	fi

# Esegui GUI da JAR.
.PHONY: run-gui-jar
run-gui-jar:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) run-jar; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata$(NC)\n"; \
		exit 1; \
	fi

# ============================ TEST (DELEGATI A qtExt) ============================ #

# Esegui tutti i test.
.PHONY: test
test:
	@$(MAKE) -C $(EXT_DIR) test

# Test calcolo distanze.
.PHONY: test-distance
test-distance:
	@$(MAKE) -C $(EXT_DIR) test-distance

# Test algoritmo QT.
.PHONY: test-qt
test-qt:
	@$(MAKE) -C $(EXT_DIR) test-qt

# Test operazioni cluster.
.PHONY: test-cluster
test-cluster:
	@$(MAKE) -C $(EXT_DIR) test-cluster

# Test operazioni data.
.PHONY: test-data
test-data:
	@$(MAKE) -C $(EXT_DIR) test-data

# Test iteratori e comparatori.
.PHONY: test-iterators
test-iterators:
	@$(MAKE) -C $(EXT_DIR) test-iterators

# Test attributi continui.
.PHONY: test-continuous
test-continuous:
	@$(MAKE) -C $(EXT_DIR) test-continuous

# =================== DOCUMENTAZIONE (DELEGATA + AGGREGAZIONE) ==================== #

# Genera tutta la documentazione JavaDoc.
.PHONY: javadoc
javadoc: javadoc-client javadoc-server javadoc-ext javadoc-gui
	@printf "$(GREEN)✓ Documentazione JavaDoc generata con successo!$(NC)\n"
	@printf "$(BLUE)→ Documentazione disponibile in:$(NC)\n"
	@printf "  - Client: $(DOCS_DIR)/client/index.html\n"
	@printf "  - Server: $(DOCS_DIR)/server/index.html\n"
	@printf "  - Extensions: $(DOCS_DIR)/ext/index.html\n"
	@printf "  - GUI: $(DOCS_DIR)/gui/index.html\n"

# Genera JavaDoc per qtClient.
.PHONY: javadoc-client
javadoc-client:
	@$(MAKE) -C $(CLIENT_DIR) javadoc

# Genera JavaDoc per qtServer.
.PHONY: javadoc-server
javadoc-server:
	@$(MAKE) -C $(SERVER_DIR) javadoc

# Genera JavaDoc per qtExt.
.PHONY: javadoc-ext
javadoc-ext:
	@$(MAKE) -C $(EXT_DIR) javadoc

# Genera JavaDoc per qtGUI.
.PHONY: javadoc-gui
javadoc-gui:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) javadoc; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)\n"; \
	fi

# ======================= DIAGRAMMI UML (TARGET DI PROGETTO) ======================= #

# Genera tutti i diagrammi (SVG + PNG).
.PHONY: uml
uml: uml-svg uml-png
	@printf "$(GREEN)✓ Diagrammi UML generati con successo!$(NC)\n"
	@printf "$(BLUE)→ Diagrammi disponibili in:$(NC)\n"
	@printf "  - SVG: $(UML_OUTPUT_DIR)/svg/\n"
	@printf "  - PNG: $(UML_OUTPUT_DIR)/png/\n"

# Genera solo diagrammi SVG.
.PHONY: uml-svg
uml-svg:
	@printf "$(BLUE)→ Generazione diagrammi UML (formato SVG)...$(NC)\n"
	@if [ -z "$(UML_SOURCES)" ]; then \
		printf "$(YELLOW)⚠ Nessun file .puml trovato in $(UML_DIR)$(NC)\n"; \
	else \
		for puml in $(UML_SOURCES); do \
			rel_path=$${puml#$(UML_DIR)/}; \
			rel_dir=$$(dirname $$rel_path); \
			output_dir="$(UML_OUTPUT_DIR)/svg/$$rel_dir"; \
			mkdir -p "$$output_dir"; \
			printf "  Generando SVG per $$rel_path...\n"; \
			plantuml -tsvg -o "$(PWD)/$$output_dir" "$$puml" 2>/dev/null || true; \
		done; \
		printf "$(GREEN)✓ Diagrammi SVG generati in $(UML_OUTPUT_DIR)/svg$(NC)\n"; \
	fi

# Genera solo diagrammi PNG.
.PHONY: uml-png
uml-png:
	@printf "$(BLUE)→ Generazione diagrammi UML (formato PNG)...$(NC)\n"
	@if [ -z "$(UML_SOURCES)" ]; then \
		printf "$(YELLOW)⚠ Nessun file .puml trovato in $(UML_DIR)$(NC)\n"; \
	else \
		for puml in $(UML_SOURCES); do \
			rel_path=$${puml#$(UML_DIR)/}; \
			rel_dir=$$(dirname $$rel_path); \
			output_dir="$(UML_OUTPUT_DIR)/png/$$rel_dir"; \
			mkdir -p "$$output_dir"; \
			printf "  Generando PNG per $$rel_path...\n"; \
			plantuml -tpng -o "$(PWD)/$$output_dir" "$$puml" 2>/dev/null || true; \
		done; \
		printf "$(GREEN)✓ Diagrammi PNG generati in $(UML_OUTPUT_DIR)/png$(NC)\n"; \
	fi

# Lista tutti i file PlantUML.
.PHONY: list-uml
list-uml:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(YELLOW)File PlantUML trovati:$(NC)\n"
	@if [ -z "$(UML_SOURCES)" ]; then \
		printf "  $(YELLOW)Nessun file .puml trovato$(NC)\n"; \
	else \
		printf "$(UML_SOURCES)" | tr ' ' '\n' | sed 's|^|  - |'; \
		printf "\n"; \
		printf "$(YELLOW)Totale:$(NC) $(words $(UML_SOURCES)) file\n"; \
	fi
	@printf "\n"

# ========================= PULIZIA (DELEGATA AI MODULI) ========================== #

# Pulisci tutto.
.PHONY: clean
clean: clean-client clean-server clean-ext clean-gui clean-jar clean-javadoc clean-uml
	@printf "$(GREEN)✓ Pulizia completata$(NC)\n"

# Pulisci solo qtClient.
.PHONY: clean-client
clean-client:
	@$(MAKE) -C $(CLIENT_DIR) clean

# Pulisci solo qtServer.
.PHONY: clean-server
clean-server:
	@$(MAKE) -C $(SERVER_DIR) clean

# Pulisci solo qtExt.
.PHONY: clean-ext
clean-ext:
	@$(MAKE) -C $(EXT_DIR) clean

# Pulisci solo GUI.
.PHONY: clean-gui
clean-gui:
	@if [ -d "$(GUI_DIR)" ]; then \
		$(MAKE) -C $(GUI_DIR) clean; \
	else \
		printf "$(YELLOW)⚠ Directory $(GUI_DIR) non trovata, skip$(NC)\n"; \
	fi

# Pulisci solo JAR (dal root).
.PHONY: clean-jar
clean-jar:
	@printf "$(YELLOW)→ Rimozione file JAR...$(NC)\n"
	@rm -f $(CLIENT_JAR) $(SERVER_JAR)
	@printf "$(GREEN)✓ File JAR rimossi$(NC)\n"

# Pulisci diagrammi UML generati.
.PHONY: clean-uml
clean-uml:
	@printf "$(YELLOW)→ Rimozione diagrammi UML generati...$(NC)\n"
	@rm -rf $(UML_OUTPUT_DIR)
	@printf "$(GREEN)✓ Diagrammi UML rimossi$(NC)\n"

# Pulisci solo JavaDoc.
.PHONY: clean-javadoc
clean-javadoc:
	@printf "$(YELLOW)→ Rimozione documentazione JavaDoc...$(NC)\n"
	@rm -rf $(DOCS_DIR)
	@printf "$(GREEN)✓ JavaDoc rimosso$(NC)\n"

# Ricompila tutto da zero.
.PHONY: rebuild
rebuild: clean all
	@printf "$(GREEN)✓ Rebuild completato!$(NC)\n"

# ======================== UTILITIES (TARGET DI PROGETTO) ========================= #

# Valida la struttura del progetto.
.PHONY: validate
validate:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# ------------ VALIDAZIONE STRUTTURA PROGETTO ------------ #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Verificando directory moduli...$(NC)\n"
	@errors=0; \
	for dir in $(CLIENT_DIR) $(SERVER_DIR) $(EXT_DIR) $(GUI_DIR); do \
		if [ -d "$$dir" ]; then \
			printf "  $(GREEN)✓$(NC) $$dir\n"; \
		else \
			printf "  $(YELLOW)✗$(NC) $$dir (mancante)\n"; \
			errors=$$((errors + 1)); \
		fi; \
	done; \
	printf "\n"; \
	printf "$(YELLOW)Verificando Makefile dei moduli...$(NC)\n"; \
	for dir in $(CLIENT_DIR) $(SERVER_DIR) $(EXT_DIR) $(GUI_DIR); do \
		if [ -f "$$dir/Makefile" ]; then \
			printf "  $(GREEN)✓$(NC) $$dir/Makefile\n"; \
		else \
			printf "  $(YELLOW)✗$(NC) $$dir/Makefile (mancante)\n"; \
			errors=$$((errors + 1)); \
		fi; \
	done; \
	printf "\n"; \
	printf "$(YELLOW)Verificando strumenti...$(NC)\n"; \
	if which $(JAVAC) >/dev/null 2>&1; then \
		printf "  $(GREEN)✓$(NC) javac ($$($(JAVAC) -version 2>&1))\n"; \
	else \
		printf "  $(YELLOW)✗$(NC) javac (non trovato)\n"; \
		errors=$$((errors + 1)); \
	fi; \
	if which $(JAVA) >/dev/null 2>&1; then \
		printf "  $(GREEN)✓$(NC) java ($$($(JAVA) -version 2>&1 | head -1))\n"; \
	else \
		printf "  $(YELLOW)✗$(NC) java (non trovato)\n"; \
		errors=$$((errors + 1)); \
	fi; \
	if which mvn >/dev/null 2>&1; then \
		printf "  $(GREEN)✓$(NC) maven ($$(mvn -version 2>&1 | head -1))\n"; \
	else \
		printf "  $(YELLOW)⚠$(NC) maven (non trovato - necessario per qtGUI)\n"; \
	fi; \
	if which plantuml >/dev/null 2>&1; then \
		printf "  $(GREEN)✓$(NC) plantuml\n"; \
	else \
		printf "  $(YELLOW)⚠$(NC) plantuml (non trovato - necessario per UML)\n"; \
	fi; \
	printf "\n"; \
	if [ $$errors -eq 0 ]; then \
		printf "$(GREEN)✓ Validazione completata con successo!$(NC)\n"; \
	else \
		printf "$(YELLOW)⚠ Validazione completata con $$errors errori$(NC)\n"; \
		exit 1; \
	fi

# Crea un pacchetto completo di distribuzione.
.PHONY: package
package: clean all jar javadoc
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# ---------- CREAZIONE PACCHETTO DISTRIBUZIONE ----------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@PKG_NAME="qtClustering-$$(date +%Y%m%d-%H%M%S)"; \
	PKG_DIR="dist/$$PKG_NAME"; \
	printf "$(YELLOW)Creando directory pacchetto: $$PKG_DIR$(NC)\n"; \
	mkdir -p "$$PKG_DIR"/{bin,docs,src}; \
	printf "\n"; \
	printf "$(YELLOW)Copiando JAR files...$(NC)\n"; \
	cp -v $(CLIENT_JAR) "$$PKG_DIR/bin/" 2>/dev/null || true; \
	cp -v $(SERVER_JAR) "$$PKG_DIR/bin/" 2>/dev/null || true; \
	if [ -f "$(GUI_JAR)" ]; then cp -v $(GUI_JAR) "$$PKG_DIR/bin/qtGUI.jar"; fi; \
	printf "\n"; \
	printf "$(YELLOW)Copiando documentazione...$(NC)\n"; \
	if [ -d "$(DOCS_DIR)" ]; then cp -r $(DOCS_DIR)/* "$$PKG_DIR/docs/"; fi; \
	cp -v README.md CLAUDE.md "$$PKG_DIR/" 2>/dev/null || true; \
	printf "\n"; \
	printf "$(YELLOW)Copiando sorgenti...$(NC)\n"; \
	cp -r $(CLIENT_DIR)/src "$$PKG_DIR/src/qtClient"; \
	cp -r $(SERVER_DIR)/src "$$PKG_DIR/src/qtServer"; \
	if [ -d "$(GUI_DIR)/src" ]; then cp -r $(GUI_DIR)/src "$$PKG_DIR/src/qtGUI"; fi; \
	printf "\n"; \
	printf "$(YELLOW)Creando archivio...$(NC)\n"; \
	cd dist && tar czf "$$PKG_NAME.tar.gz" "$$PKG_NAME"; \
	printf "\n"; \
	printf "$(GREEN)✓ Pacchetto creato con successo!$(NC)\n"; \
	printf "$(BLUE)→ File: dist/$$PKG_NAME.tar.gz$(NC)\n"; \
	ls -lh "dist/$$PKG_NAME.tar.gz"

# ================================= INFORMAZIONI ================================== #

# Mostra informazioni progetto.
.PHONY: info
info:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# -------------- INFORMAZIONI SUL PROGETTO --------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Struttura Modulare:$(NC)\n"
	@printf "  Makefile principale: ./Makefile\n"
	@printf "  Configurazione:      ./make/common.mk\n"
	@printf "\n"
	@printf "$(YELLOW)Moduli:$(NC)\n"
	@for dir in $(CLIENT_DIR) $(SERVER_DIR) $(EXT_DIR) $(GUI_DIR); do \
		if [ -d "$$dir" ]; then \
			printf "  $(GREEN)✓$(NC) $$dir/Makefile\n"; \
		else \
			printf "  $(YELLOW)✗$(NC) $$dir (mancante)\n"; \
		fi; \
	done
	@printf "\n"
	@printf "$(YELLOW)File JAR:$(NC)\n"
	@if [ -f $(CLIENT_JAR) ]; then printf "  $(CLIENT_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(CLIENT_JAR) | cut -f1))\n"; else printf "  $(CLIENT_JAR): $(YELLOW)✗ non presente$(NC)\n"; fi
	@if [ -f $(SERVER_JAR) ]; then printf "  $(SERVER_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(SERVER_JAR) | cut -f1))\n"; else printf "  $(SERVER_JAR): $(YELLOW)✗ non presente$(NC)\n"; fi
	@if [ -f $(GUI_JAR) ]; then printf "  $(GUI_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(GUI_JAR) | cut -f1))\n"; else printf "  GUI JAR: $(YELLOW)✗ non presente$(NC)\n"; fi
	@printf "\n"
	@printf "$(YELLOW)UML:$(NC)\n"
	@printf "  Directory:   $(UML_DIR)\n"
	@printf "  File .puml:  $(words $(UML_SOURCES))\n"
	@if [ -d "$(UML_OUTPUT_DIR)" ]; then \
		printf "  Generati:    $(GREEN)✓ $(UML_OUTPUT_DIR)$(NC)\n"; \
	else \
		printf "  Generati:    $(YELLOW)✗ non generati$(NC)\n"; \
	fi
	@printf "\n"

# Verifica dipendenze esterne.
.PHONY: check-deps
check-deps:
	@printf "$(BLUE)→ Verifica dipendenze...$(NC)\n"
	@which $(JAVAC) > /dev/null || (printf "$(YELLOW)⚠ javac non trovato!$(NC)\n" && exit 1)
	@which $(JAVA) > /dev/null || (printf "$(YELLOW)⚠ java non trovato!$(NC)\n" && exit 1)
	@which $(JAR) > /dev/null || (printf "$(YELLOW)⚠ jar non trovato!$(NC)\n" && exit 1)
	@printf "$(GREEN)✓ Tutte le dipendenze sono disponibili$(NC)\n"
	@printf "\n"
	@$(JAVAC) -version
	@$(JAVA) -version

# Lista tutti i file sorgente.
.PHONY: list-sources
list-sources:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@$(MAKE) -C $(CLIENT_DIR) list-sources
	@$(MAKE) -C $(SERVER_DIR) list-sources
	@$(MAKE) -C $(EXT_DIR) list-sources

# ================================================================================= #
# End of Makefile.
