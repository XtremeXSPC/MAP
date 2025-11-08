# ============================================================================ #
# Makefile per Quality Threshold Clustering - Client/Server
# ============================================================================ #
# Progetto: MAP - Quality Threshold Clustering Algorithm
# Linguaggio: Java
# Struttura:
#   - qtClient: Applicazione client (package default e keyboardinput)
#   - qtServer: Applicazione server (package data, mining, database, server)
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

# Main classes
CLIENT_MAIN = MainTest
SERVER_MAIN = server.MultiServer

# JAR files
CLIENT_JAR = qtClient.jar
SERVER_JAR = qtServer.jar

# MySQL JDBC Driver
MYSQL_DRIVER = qtServer/database/JDBC/mysql-connector-java-8.0.17.jar

# Classpath con MySQL driver
SERVER_CLASSPATH = $(SERVER_BIN):$(MYSQL_DRIVER)

# Trova tutti i file sorgente
CLIENT_SOURCES = $(shell find $(CLIENT_SRC) -name '*.java')
SERVER_SOURCES = $(shell find $(SERVER_SRC) -name '*.java')

# File marker per tracking compilazione
CLIENT_MARKER = $(CLIENT_BIN)/.compiled
SERVER_MARKER = $(SERVER_BIN)/.compiled

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
all: client server
	@echo "$(GREEN)✓ Compilazione completata con successo!$(NC)"

# Target help: mostra utilizzo
.PHONY: help
help:
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo "$(BLUE)  Makefile - Quality Threshold Clustering$(NC)"
	@echo "$(BLUE)═══════════════════════════════════════════════════════════$(NC)"
	@echo ""
	@echo "$(YELLOW)Target disponibili:$(NC)"
	@echo "  make all          - Compila client e server"
	@echo "  make client       - Compila solo qtClient"
	@echo "  make server       - Compila solo qtServer"
	@echo ""
	@echo "  make jar          - Crea JAR per client e server"
	@echo "  make client-jar   - Crea solo qtClient.jar"
	@echo "  make server-jar   - Crea solo qtServer.jar"
	@echo ""
	@echo "  make run-client   - Esegui client (args: IP=localhost PORT=8080)"
	@echo "  make run-server   - Esegui server (args: PORT=8080)"
	@echo ""
	@echo "  make clean        - Rimuove tutti i file compilati"
	@echo "  make clean-client - Rimuove solo file compilati del client"
	@echo "  make clean-server - Rimuove solo file compilati del server"
	@echo "  make clean-jar    - Rimuove solo i file JAR"
	@echo ""
	@echo "  make rebuild      - Pulisci e ricompila tutto"
	@echo "  make help         - Mostra questo messaggio"
	@echo ""
	@echo "$(YELLOW)Esempi:$(NC)"
	@echo "  make all"
	@echo "  make jar"
	@echo "  make run-server PORT=9999"
	@echo "  make run-client IP=127.0.0.1 PORT=9999"
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

# ============================================================================ #
# Creazione JAR
# ============================================================================ #

# Crea entrambi i JAR
.PHONY: jar
jar: client-jar server-jar
	@echo "$(GREEN)✓ File JAR creati con successo!$(NC)"
	@if ls *.jar 1> /dev/null 2>&1; then ls -lh *.jar; fi

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

# ============================================================================ #
# Pulizia
# ============================================================================ #

# Pulisci tutto
.PHONY: clean
clean: clean-client clean-server clean-jar
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

# Pulisci solo JAR
.PHONY: clean-jar
clean-jar:
	@echo "$(YELLOW)→ Rimozione file JAR...$(NC)"
	@rm -f $(CLIENT_JAR) $(SERVER_JAR)
	@echo "$(GREEN)✓ File JAR rimossi$(NC)"

# Ricompila tutto da zero
.PHONY: rebuild
rebuild: clean all
	@echo "$(GREEN)✓ Rebuild completato!$(NC)"

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
	@echo "$(YELLOW)Stato compilazione:$(NC)"
	@if [ -f $(CLIENT_MARKER) ]; then echo "  Client:  $(GREEN)✓ compilato$(NC)"; else echo "  Client:  $(YELLOW)✗ non compilato$(NC)"; fi
	@if [ -f $(SERVER_MARKER) ]; then echo "  Server:  $(GREEN)✓ compilato$(NC)"; else echo "  Server:  $(YELLOW)✗ non compilato$(NC)"; fi
	@echo ""
	@echo "$(YELLOW)File JAR:$(NC)"
	@if [ -f $(CLIENT_JAR) ]; then echo "  $(CLIENT_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(CLIENT_JAR) | cut -f1))"; else echo "  $(CLIENT_JAR): $(YELLOW)✗ non presente$(NC)"; fi
	@if [ -f $(SERVER_JAR) ]; then echo "  $(SERVER_JAR): $(GREEN)✓ presente$(NC) ($$(du -h $(SERVER_JAR) | cut -f1))"; else echo "  $(SERVER_JAR): $(YELLOW)✗ non presente$(NC)"; fi
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

# ============================================================================ #