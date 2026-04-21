# ================================================================================= #
# ---------------------------- Makefile - qtGUI Module ---------------------------- #
# ================================================================================= #
# Modulo: qtGUI - Interfaccia grafica JavaFX per QT Clustering
# Build System: Maven
# Questo Makefile e' un wrapper per i comandi Maven.
# ================================================================================= #

# Include configurazione comune.
include ../make/common.mk

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Maven command.
MVN ?= mvn
MODULE_NAME = qtGUI
STDGUI_MODULE = stdgui
ROOT_POM = $(PROJECT_ROOT)/pom.xml
REACTOR_MVN = $(MVN) -f $(ROOT_POM) -pl $(MODULE_NAME) -am
STDGUI_MVN = $(MVN) -f $(ROOT_POM) -pl $(STDGUI_MODULE) -am

# Directory.
TARGET_DIR = target
JAR_FILE = $(TARGET_DIR)/qtGUI-1.0.0.jar

# =============================== TARGET PRINCIPALI =============================== #

# Target di default: compila il modulo.
.PHONY: all
all: compile

# Mostra help del modulo.
.PHONY: help
help:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(BLUE)# --------------------- qtGUI Module --------------------- #$(NC)\n"
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "\n"
	@printf "$(YELLOW)Target disponibili:$(NC)\n"
	@printf "  make compile       - Compila con Maven\n"
	@printf "  make jar           - Crea JAR con Maven package\n"
	@printf "  make run           - Esegui GUI con JavaFX\n"
	@printf "  make run-jar       - Esegui da JAR\n"
	@printf "  make clean         - Maven clean\n"
	@printf "  make javadoc       - Genera documentazione JavaDoc\n"
	@printf "  make test          - Esegui test Maven\n"
	@printf "  make help          - Mostra questo messaggio\n"
	@printf "\n"
	@printf "$(YELLOW)Note:$(NC)\n"
	@printf "  Questo modulo richiede Maven e JavaFX.\n"
	@printf "  I comandi Maven passano dal reactor root per includere stdgui.\n"
	@printf "  Il target run installa prima stdgui nel repository Maven locale.\n"
	@printf "\n"

# ================================= COMPILAZIONE ================================== #

# Compila con Maven.
.PHONY: compile
compile:
	@printf "$(BLUE)→ Compilazione qtGUI (Maven)...$(NC)\n"
	@$(REACTOR_MVN) compile -q
	@printf "$(GREEN)✓ qtGUI compilato$(NC)\n"

# ================================= CREAZIONE JAR ================================= #

# Crea JAR con Maven package.
.PHONY: jar
jar:
	@printf "$(BLUE)→ Creazione qtGUI JAR (Maven package)...$(NC)\n"
	@$(REACTOR_MVN) package -DskipTests -q
	@printf "$(GREEN)✓ qtGUI JAR creato in $(JAR_FILE)$(NC)\n"

# ================================== ESECUZIONE =================================== #

# Esegui GUI con Maven JavaFX plugin.
.PHONY: run
run:
	@printf "$(BLUE)→ Avvio qtGUI (JavaFX)...$(NC)\n"
	@printf "$(BLUE)→ Aggiornamento dipendenza locale stdgui...$(NC)\n"
	@$(STDGUI_MVN) clean install -DskipTests -q
	@$(MVN) javafx:run

# Esegui da JAR.
.PHONY: run-jar
run-jar: jar
	@printf "$(BLUE)→ Avvio qtGUI da JAR...$(NC)\n"
	@if [ -f "$(JAR_FILE)" ]; then \
		$(JAVA) -jar $(JAR_FILE); \
	else \
		printf "$(YELLOW)⚠ JAR $(JAR_FILE) non trovato. Esegui 'make jar' prima.$(NC)\n"; \
		exit 1; \
	fi

# ================================ DOCUMENTAZIONE ================================= #

# Genera JavaDoc con Maven.
.PHONY: javadoc
javadoc:
	@printf "$(BLUE)→ Generazione JavaDoc per qtGUI (Maven)...$(NC)\n"
	@$(REACTOR_MVN) javadoc:javadoc \
		-Dmaven.javadoc.failOnError=false \
		-Dmaven.javadoc.failOnWarnings=false \
		-q
	@mkdir -p $(DOCS_DIR)/gui
	@if [ -d "$(TARGET_DIR)/reports/apidocs" ]; then \
		cp -r $(TARGET_DIR)/reports/apidocs/* $(DOCS_DIR)/gui/; \
		printf "$(GREEN)✓ JavaDoc qtGUI generato in $(DOCS_DIR)/gui$(NC)\n"; \
	elif [ -d "$(TARGET_DIR)/site/apidocs" ]; then \
		cp -r $(TARGET_DIR)/site/apidocs/* $(DOCS_DIR)/gui/; \
		printf "$(GREEN)✓ JavaDoc qtGUI generato in $(DOCS_DIR)/gui$(NC)\n"; \
	else \
		printf "$(YELLOW)⚠ JavaDoc non generato correttamente$(NC)\n"; \
	fi

# ===================================== TEST ====================================== #

# Esegui test Maven.
.PHONY: test
test:
	@printf "$(BLUE)→ Esecuzione test qtGUI (Maven)...$(NC)\n"
	@$(REACTOR_MVN) test

# ==================================== PULIZIA ==================================== #

# Maven clean.
.PHONY: clean
clean:
	@printf "$(YELLOW)→ Pulizia qtGUI (Maven clean)...$(NC)\n"
	@$(REACTOR_MVN) clean -q
	@printf "$(GREEN)✓ qtGUI pulito$(NC)\n"

# =================================== UTILITIES =================================== #

# Mostra informazioni sul modulo.
.PHONY: info
info:
	@printf "$(BLUE)# ════════════════════════════════════════════════════════ #$(NC)\n"
	@printf "$(YELLOW)Informazioni qtGUI:$(NC)\n"
	@printf "  Build System: Maven\n"
	@printf "  Framework:    JavaFX\n"
	@printf "  JAR:          $(JAR_FILE)\n"
	@printf "\n"
	@printf "$(YELLOW)Stato:$(NC)\n"
	@if [ -d "$(TARGET_DIR)/classes" ]; then printf "  Compilato: $(GREEN)si$(NC)\n"; else printf "  Compilato: $(YELLOW)no$(NC)\n"; fi
	@if [ -f "$(JAR_FILE)" ]; then printf "  JAR:       $(GREEN)presente$(NC) ($$(du -h $(JAR_FILE) | cut -f1))\n"; else printf "  JAR:       $(YELLOW)assente$(NC)\n"; fi
	@printf "\n"
	@printf "$(YELLOW)Maven:$(NC)\n"
	@if which $(MVN) >/dev/null 2>&1; then \
		printf "  Versione:  $$($(MVN) -version 2>&1 | head -1)\n"; \
	else \
		printf "  $(RED)Maven non trovato!$(NC)\n"; \
	fi

# Verifica dipendenze Maven.
.PHONY: verify
verify:
	@printf "$(BLUE)→ Verifica dipendenze Maven...$(NC)\n"
	@$(REACTOR_MVN) dependency:resolve -q
	@printf "$(GREEN)✓ Dipendenze verificate$(NC)\n"

# ================================================================================= #
