# ================================================================================= #
# ---------------------------- Makefile - qtExt Module ---------------------------- #
# ================================================================================= #
# Modulo: qtExt - Extensions (test suite e utility per benchmark)
# Package: tests, utility
# Dipendenze: qtServer (per classpath)
# ================================================================================= #

# Include configurazione comune.
include ../make/common.mk

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Directory.
BIN_DIR = bin
TESTS_DIR = tests
UTILITY_DIR = utility

# Directory qtServer (dipendenza).
SERVER_BIN = ../qtServer/bin
MYSQL_DRIVER = ../qtServer/JDBC/mysql-connector-java-9.5.0.jar

# Classpath.
CLASSPATH = $(BIN_DIR):$(SERVER_BIN):$(MYSQL_DRIVER)

# Trova tutti i file sorgente.
TEST_SOURCES = $(shell find $(TESTS_DIR) -name '*.java' 2>/dev/null)
UTILITY_SOURCES = $(shell find $(UTILITY_DIR) -name '*.java' 2>/dev/null)
ALL_SOURCES = $(TEST_SOURCES) $(UTILITY_SOURCES)

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
	@printf "$(BLUE)# --------------------- qtExt Module --------------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Target disponibili:$(NC)\n"
	@printf "  make compile         - Compila test e utility\n"
	@printf "  make test            - Esegui tutti i test\n"
	@printf "  make test-distance   - Test calcolo distanze\n"
	@printf "  make test-qt         - Test algoritmo QT\n"
	@printf "  make test-cluster    - Test operazioni cluster\n"
	@printf "  make test-data       - Test operazioni data\n"
	@printf "  make test-iterators  - Test iteratori e comparatori\n"
	@printf "  make test-continuous - Test attributi continui\n"
	@printf "  make clean           - Rimuovi file compilati\n"
	@printf "  make javadoc         - Genera documentazione JavaDoc\n"
	@printf "  make help            - Mostra questo messaggio\n"
	@printf "\n"
	@printf "$(YELLOW)Note:$(NC)\n"
	@printf "  Richiede qtServer compilato (dipendenza classpath).\n"
	@printf "\n"

# ================================= COMPILAZIONE ================================== #

# Assicura che qtServer sia compilato.
.PHONY: ensure-server
ensure-server:
	@printf "$(BLUE)→ Compilazione dipendenza qtServer...$(NC)\n"
	@$(MAKE) -C ../qtServer compile

# Compila test e utility.
.PHONY: compile
compile: ensure-server
	@printf "$(BLUE)→ Compilazione qtExt (test e utility)...$(NC)\n"
	@mkdir -p $(BIN_DIR)
	@rm -f $(COMPILE_MARKER)
	@if [ -n "$(TEST_SOURCES)" ]; then \
		$(JAVAC) $(JFLAGS) $(BIN_DIR) -cp $(SERVER_BIN):$(MYSQL_DRIVER) $(TEST_SOURCES); \
	fi
	@if [ -n "$(UTILITY_SOURCES)" ]; then \
		$(JAVAC) $(JFLAGS) $(BIN_DIR) -cp $(SERVER_BIN):$(MYSQL_DRIVER) $(UTILITY_SOURCES); \
	fi
	@touch $(COMPILE_MARKER)
	@printf "$(GREEN)✓ qtExt compilato$(NC)\n"

# ===================================== TEST ====================================== #

# Esegui tutti i test.
.PHONY: test
test: compile
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# ---------------- ESECUZIONE TEST SUITE ----------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@$(MAKE) --no-print-directory test-distance
	@printf "\n"
	@$(MAKE) --no-print-directory test-qt
	@printf "\n"
	@$(MAKE) --no-print-directory test-cluster
	@printf "\n"
	@$(MAKE) --no-print-directory test-data
	@printf "\n"
	@printf "$(GREEN)✓ Test suite completata!$(NC)\n"

# Test calcolo distanze.
.PHONY: test-distance
test-distance: compile
	@printf "$(YELLOW)→ Test Calcolo Distanze...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestDistanceCalculations

# Test algoritmo QT.
.PHONY: test-qt
test-qt: compile
	@printf "$(YELLOW)→ Test Algoritmo QT...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestQTAlgorithm

# Test operazioni cluster.
.PHONY: test-cluster
test-cluster: compile
	@printf "$(YELLOW)→ Test Operazioni Cluster...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestClusterOperations

# Test operazioni data.
.PHONY: test-data
test-data: compile
	@printf "$(YELLOW)→ Test Operazioni Data...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestDataOperations

# Test iteratori e comparatori.
.PHONY: test-iterators
test-iterators: compile
	@printf "$(YELLOW)→ Test Iteratori e Comparatori...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestIteratorsComparators

# Test attributi continui (interattivo).
.PHONY: test-continuous
test-continuous: compile
	@printf "$(YELLOW)→ Test Attributi Continui (Interattivo)...$(NC)\n"
	@$(JAVA) -cp $(CLASSPATH) tests.TestContinuousAttributes

# ================================ DOCUMENTAZIONE ================================= #

# Genera JavaDoc.
.PHONY: javadoc
javadoc:
	@printf "$(BLUE)→ Generazione JavaDoc per qtExt...$(NC)\n"
	@mkdir -p $(DOCS_DIR)/ext
	@javadoc -d $(DOCS_DIR)/ext \
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
		-classpath $(SERVER_BIN):$(MYSQL_DRIVER) \
		-sourcepath . \
		-subpackages tests:utility \
		2>/dev/null || true
	@printf "$(GREEN)✓ JavaDoc qtExt generato in $(DOCS_DIR)/ext$(NC)\n"

# ==================================== PULIZIA ==================================== #

# Pulisci i file compilati.
.PHONY: clean
clean:
	@printf "$(YELLOW)→ Pulizia qtExt...$(NC)\n"
	@rm -rf $(BIN_DIR)
	@printf "$(GREEN)✓ qtExt pulito$(NC)\n"

# =================================== UTILITIES =================================== #

# Mostra informazioni sul modulo.
.PHONY: info
info:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(YELLOW)Informazioni qtExt:$(NC)\n"
	@printf "  Binari:      $(BIN_DIR)\n"
	@printf "  Test:        $(TESTS_DIR) ($(words $(TEST_SOURCES)) file)\n"
	@printf "  Utility:     $(UTILITY_DIR) ($(words $(UTILITY_SOURCES)) file)\n"
	@printf "\n"
	@printf "$(YELLOW)Dipendenze:$(NC)\n"
	@printf "  Server BIN:  $(SERVER_BIN)\n"
	@printf "  JDBC Driver: $(MYSQL_DRIVER)\n"
	@printf "\n"
	@printf "$(YELLOW)Stato:$(NC)\n"
	@if [ -f $(COMPILE_MARKER) ]; then printf "  Compilato: $(GREEN)si$(NC)\n"; else printf "  Compilato: $(YELLOW)no$(NC)\n"; fi
	@if [ -d "$(SERVER_BIN)" ]; then printf "  Server:    $(GREEN)disponibile$(NC)\n"; else printf "  Server:    $(YELLOW)non compilato$(NC)\n"; fi

# Lista i file sorgente.
.PHONY: list-sources
list-sources:
	@printf "$(YELLOW)File sorgente qtExt (Test):$(NC)\n"
	@printf "$(TEST_SOURCES)" | tr ' ' '\n' | sed 's|^|  |'
	@printf "\n"
	@printf "$(YELLOW)File sorgente qtExt (Utility):$(NC)\n"
	@printf "$(UTILITY_SOURCES)" | tr ' ' '\n' | sed 's|^|  |'
	@printf "\n"

# ================================================================================= #
