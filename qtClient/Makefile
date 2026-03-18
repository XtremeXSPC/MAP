# ================================================================================= #
# -------------------------- Makefile - qtClient Module --------------------------- #
# ================================================================================= #
# Modulo: qtClient - Applicazione client per QT Clustering
# Package: default, keyboardinput
# ================================================================================= #

# Include configurazione comune.
include ../make/common.mk

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Directory.
SRC_DIR = src
BIN_DIR = bin

# Main class.
MAIN_CLASS = MainTest

# JAR file.
JAR_FILE = qtClient.jar

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
	@printf "$(BLUE)# ------------------- qtClient Module -------------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Target disponibili:$(NC)\n"
	@printf "  make compile       - Compila i sorgenti Java\n"
	@printf "  make jar           - Crea il file JAR\n"
	@printf "  make run           - Esegui il client (IP=localhost PORT=8080)\n"
	@printf "  make run-jar       - Esegui da JAR\n"
	@printf "  make clean         - Rimuovi file compilati\n"
	@printf "  make javadoc       - Genera documentazione JavaDoc\n"
	@printf "  make help          - Mostra questo messaggio\n"
	@printf "\n"
	@printf "$(YELLOW)Esempi:$(NC)\n"
	@printf "  make run IP=192.168.1.100 PORT=9999\n"
	@printf "\n"

# ================================= COMPILAZIONE ================================== #

# Compila i sorgenti.
.PHONY: compile
compile: $(COMPILE_MARKER)

$(COMPILE_MARKER): $(SOURCES)
	@printf "$(BLUE)→ Compilazione qtClient...$(NC)\n"
	@mkdir -p $(BIN_DIR)
	$(JAVAC) $(JFLAGS) $(BIN_DIR) $(SOURCES)
	@touch $(COMPILE_MARKER)
	@printf "$(GREEN)✓ qtClient compilato$(NC)\n"

# ================================= CREAZIONE JAR ================================= #

# Crea il file JAR.
.PHONY: jar
jar: compile
	@printf "$(BLUE)→ Creazione $(JAR_FILE)...$(NC)\n"
	@cd $(BIN_DIR) && $(JAR) cfe ../$(JAR_FILE) $(MAIN_CLASS) .
	@printf "$(GREEN)✓ $(JAR_FILE) creato$(NC)\n"

# ================================== ESECUZIONE =================================== #

# Variabili per esecuzione (possono essere sovrascritte).
IP ?= localhost
PORT ?= 8080

# Esegui il client.
.PHONY: run
run: compile
	@printf "$(BLUE)→ Avvio qtClient (connessione a $(IP):$(PORT))...$(NC)\n"
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS) $(IP) $(PORT)

# Esegui da JAR.
.PHONY: run-jar
run-jar: jar
	@printf "$(BLUE)→ Avvio qtClient da JAR (connessione a $(IP):$(PORT))...$(NC)\n"
	$(JAVA) -jar $(JAR_FILE) $(IP) $(PORT)

# ================================ DOCUMENTAZIONE ================================= #

# Genera JavaDoc.
.PHONY: javadoc
javadoc:
	@printf "$(BLUE)→ Generazione JavaDoc per qtClient...$(NC)\n"
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
		-sourcepath $(SRC_DIR) \
		-subpackages keyboardinput \
		$(SRC_DIR)/*.java 2>/dev/null || true
	@printf "$(GREEN)✓ JavaDoc qtClient generato in $(DOCS_DIR)/client$(NC)\n"

# ==================================== PULIZIA ==================================== #

# Pulisci i file compilati.
.PHONY: clean
clean:
	@printf "$(YELLOW)→ Pulizia qtClient...$(NC)\n"
	@rm -rf $(BIN_DIR)
	@rm -f $(JAR_FILE)
	@printf "$(GREEN)✓ qtClient pulito$(NC)\n"

# Pulisci solo i file compilati (non il JAR).
.PHONY: clean-bin
clean-bin:
	@printf "$(YELLOW)→ Pulizia binari qtClient...$(NC)\n"
	@rm -rf $(BIN_DIR)
	@printf "$(GREEN)✓ Binari qtClient rimossi$(NC)\n"

# =================================== UTILITIES =================================== #

# Mostra informazioni sul modulo.
.PHONY: info
info:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(YELLOW)Informazioni qtClient:$(NC)\n"
	@printf "  Sorgenti:    $(SRC_DIR)\n"
	@printf "  Binari:      $(BIN_DIR)\n"
	@printf "  Main class:  $(MAIN_CLASS)\n"
	@printf "  File Java:   $(words $(SOURCES))\n"
	@printf "  JAR:         $(JAR_FILE)\n"
	@printf "\n"
	@printf "$(YELLOW)Stato:$(NC)\n"
	@if [ -f $(COMPILE_MARKER) ]; then printf "  Compilato: $(GREEN)si$(NC)\n"; else printf "  Compilato: $(YELLOW)no$(NC)\n"; fi
	@if [ -f $(JAR_FILE) ]; then printf "  JAR:       $(GREEN)presente$(NC) ($$(du -h $(JAR_FILE) | cut -f1))\n"; else printf "  JAR:       $(YELLOW)assente$(NC)\n"; fi

# Lista i file sorgente.
.PHONY: list-sources
list-sources:
	@printf "$(YELLOW)File sorgente qtClient:$(NC)\n"
	@printf "$(SOURCES)" | tr ' ' '\n' | sed 's|^|  |'
	@printf "\n"

# ================================================================================= #
