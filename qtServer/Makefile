# ================================================================================= #
# -------------------------- Makefile - qtServer Module --------------------------- #
# ================================================================================= #
# Modulo: qtServer - Applicazione server per QT Clustering
# Package: data, mining, database, server
# ================================================================================= #

# Include configurazione comune.
include ../make/common.mk

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Directory.
SRC_DIR = src
BIN_DIR = bin

# Main class.
MAIN_CLASS = server.MultiServer

# JAR file.
JAR_FILE = qtServer.jar

# MySQL JDBC Driver.
MYSQL_DRIVER = JDBC/mysql-connector-java-9.5.0.jar

# Classpath con MySQL driver.
CLASSPATH = $(BIN_DIR):$(MYSQL_DRIVER)

# Trova tutti i file sorgente.
SOURCES = $(shell find $(SRC_DIR) -name '*.java')

# File marker per tracking compilazione.
COMPILE_MARKER = $(BIN_DIR)/.compiled

# =============================== TARGET PRINCIPALI =============================== #

# Target di default: compila il modulo.
.PHONY: all
all: compile

# Mostra help del modulo.
.PHONY: help
help:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# ------------------- qtServer Module -------------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Target disponibili:$(NC)\n"
	@printf "  make compile       - Compila i sorgenti Java\n"
	@printf "  make jar           - Crea il file JAR\n"
	@printf "  make run           - Esegui il server (PORT=8080)\n"
	@printf "  make run-jar       - Esegui da JAR\n"
	@printf "  make clean         - Rimuovi file compilati\n"
	@printf "  make javadoc       - Genera documentazione JavaDoc\n"
	@printf "  make help          - Mostra questo messaggio\n"
	@printf "\n"
	@printf "$(YELLOW)Esempi:$(NC)\n"
	@printf "  make run PORT=9999\n"
	@printf "\n"

# ================================= COMPILAZIONE ================================== #

# Compila i sorgenti.
.PHONY: compile
compile:
	@printf "$(BLUE)→ Compilazione qtServer...$(NC)\n"
	@mkdir -p $(BIN_DIR)
	@rm -f $(COMPILE_MARKER)
	$(JAVAC) $(JFLAGS) $(BIN_DIR) $(SOURCES)
	@touch $(COMPILE_MARKER)
	@printf "$(GREEN)✓ qtServer compilato$(NC)\n"

# ================================= CREAZIONE JAR ================================= #

# Crea il file JAR.
.PHONY: jar
jar: compile
	@printf "$(BLUE)→ Creazione $(JAR_FILE)...$(NC)\n"
	@cd $(BIN_DIR) && $(JAR) cfe ../$(JAR_FILE) $(MAIN_CLASS) .
	@printf "$(GREEN)✓ $(JAR_FILE) creato$(NC)\n"

# ================================== ESECUZIONE =================================== #

# Variabili per esecuzione (possono essere sovrascritte).
PORT ?= 8080

# Esegui il server.
.PHONY: run
run: compile
	@printf "$(BLUE)→ Avvio qtServer sulla porta $(PORT)...$(NC)\n"
	$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS) $(PORT)

# Esegui da JAR.
.PHONY: run-jar
run-jar: jar
	@printf "$(BLUE)→ Avvio qtServer da JAR sulla porta $(PORT)...$(NC)\n"
	$(JAVA) -jar $(JAR_FILE) $(PORT)

# ================================ DOCUMENTAZIONE ================================= #

# Genera JavaDoc.
.PHONY: javadoc
javadoc:
	@printf "$(BLUE)→ Generazione JavaDoc per qtServer...$(NC)\n"
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
		-sourcepath $(SRC_DIR) \
		-subpackages data:mining:database:server \
		2>/dev/null || true
	@printf "$(GREEN)✓ JavaDoc qtServer generato in $(DOCS_DIR)/server$(NC)\n"

# ==================================== PULIZIA ==================================== #

# Pulisci i file compilati.
.PHONY: clean
clean:
	@printf "$(YELLOW)→ Pulizia qtServer...$(NC)\n"
	@rm -rf $(BIN_DIR)
	@rm -f $(JAR_FILE)
	@printf "$(GREEN)✓ qtServer pulito$(NC)\n"

# Pulisci solo i file compilati (non il JAR).
.PHONY: clean-bin
clean-bin:
	@printf "$(YELLOW)→ Pulizia binari qtServer...$(NC)\n"
	@rm -rf $(BIN_DIR)
	@printf "$(GREEN)✓ Binari qtServer rimossi$(NC)\n"

# =================================== UTILITIES =================================== #

# Mostra informazioni sul modulo.
.PHONY: info
info:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(YELLOW)Informazioni qtServer:$(NC)\n"
	@printf "  Sorgenti:    $(SRC_DIR)\n"
	@printf "  Binari:      $(BIN_DIR)\n"
	@printf "  Main class:  $(MAIN_CLASS)\n"
	@printf "  File Java:   $(words $(SOURCES))\n"
	@printf "  JAR:         $(JAR_FILE)\n"
	@printf "  JDBC Driver: $(MYSQL_DRIVER)\n"
	@printf "\n"
	@printf "$(YELLOW)Stato:$(NC)\n"
	@if [ -f $(COMPILE_MARKER) ]; then printf "  Compilato: $(GREEN)si$(NC)\n"; else printf "  Compilato: $(YELLOW)no$(NC)\n"; fi
	@if [ -f $(JAR_FILE) ]; then printf "  JAR:       $(GREEN)presente$(NC) ($$(du -h $(JAR_FILE) | cut -f1))\n"; else printf "  JAR:       $(YELLOW)assente$(NC)\n"; fi
	@if [ -f $(MYSQL_DRIVER) ]; then printf "  JDBC:      $(GREEN)presente$(NC)\n"; else printf "  JDBC:      $(RED)mancante$(NC)\n"; fi

# Lista i file sorgente.
.PHONY: list-sources
list-sources:
	@printf "$(YELLOW)File sorgente qtServer:$(NC)\n"
	@printf "$(SOURCES)" | tr ' ' '\n' | sed 's|^|  |'
	@printf "\n"

# Esporta classpath per altri moduli (usato da qtExt).
.PHONY: get-classpath
get-classpath:
	@echo "$(CLASSPATH)"

# ================================================================================= #
