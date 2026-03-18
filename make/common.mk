# ================================================================================= #
# --------------------- Configurazione Comune - QT Clustering --------------------- #
# ================================================================================= #
# Variabili condivise tra tutti i Makefile dei moduli.
# Include: comandi, flag, colori, e funzioni di utilita'.
# ================================================================================= #

# ========================== VARIABILI DI CONFIGURAZIONE ========================== #

# Comandi Java.
JAVAC ?= javac
JAVA ?= java
JAR ?= jar

# Flag del compilatore.
JFLAGS ?= -encoding UTF-8 -d

# Colori per output (ANSI escape codes).
GREEN = \033[0;32m
BLUE = \033[0;34m
YELLOW = \033[1;33m
RED = \033[0;31m
NC = \033[0m

# ============================= FUNZIONI DI UTILITA' ============================== #

# Funzione per stampare messaggi informativi.
define print_info
	@printf "$(BLUE)→ $(1)$(NC)\n"
endef

# Funzione per stampare messaggi di successo.
define print_success
	@printf "$(GREEN)✓ $(1)$(NC)\n"
endef

# Funzione per stampare avvisi.
define print_warning
	@printf "$(YELLOW)⚠ $(1)$(NC)\n"
endef

# Funzione per stampare errori.
define print_error
	@printf "$(RED)✗ $(1)$(NC)\n"
endef

# ================================= PATH PROGETTO ================================= #

# Calcola il percorso root del progetto (usato nei Makefile dei moduli).
# Ogni modulo puo' sovrascrivere PROJECT_ROOT se necessario.
PROJECT_ROOT ?= $(shell cd $(dir $(lastword $(MAKEFILE_LIST)))/.. && pwd)

# Directory documentazione (relativa a PROJECT_ROOT).
DOCS_DIR = $(PROJECT_ROOT)/docs/javadoc

# ================================================================================= #
