#!/bin/bash
# ============================================================================ #
# Script per importare CSV nel database MapDB.
# ============================================================================ #

DB_USER="LCS-MAP"
DB_NAME="MapDB"
DATA_DIR="data"

echo "# ============== Importazione CSV in MapDB =============== #"

# Funzione per importare CSV.
import_csv() {
    local table=$1
    local csv_file=$2
    local columns=$3

    echo ""
    echo "→ Importazione $table da $csv_file..."

    # Conta righe (escluso header).
    local total_rows=$(($(wc -l < "$csv_file") - 1))
    echo "  Righe da importare: $total_rows"

    # Genera INSERT statements (salta header).
    tail -n +2 "$csv_file" | while IFS=, read -r values; do
        # Converte la riga CSV in formato SQL.
        # Sostituisce virgole con ,' e aggiunge apici.
        formatted_values=$(echo "$values" | sed "s/,/','/g" | sed "s/^/'/; s/$/'/")

        echo "INSERT INTO $table ($columns) VALUES ($formatted_values);"
    done | mysql -u "$DB_USER" "$DB_NAME" 2>&1

    # Verifica count.
    local imported=$(mysql -u "$DB_USER" "$DB_NAME" -sN -e "SELECT COUNT(*) FROM $table;")
    echo "  ✓ Righe importate: $imported"
}

# Import playtennis.
import_csv "playtennis" \
    "$DATA_DIR/playtennis.csv" \
    "Outlook, Temperature, Humidity, Wind, PlayTennis"

# Import weather_mixed.
import_csv "weather_mixed" \
    "$DATA_DIR/weather_mixed.csv" \
    "outlook, temperature, humidity, wind, play"

# Import iris (se presente - verifica header prima).
if [ -f "$DATA_DIR/iris.csv" ]; then
    # Leggi header per determinare nomi colonne.
    header=$(head -1 "$DATA_DIR/iris.csv")
    echo ""
    echo "→ Header iris.csv: $header"

    # Se header contiene nomi colonne validi, importa altrimenti salta.
    if [[ "$header" == *"sepal"* ]] || [[ "$header" == *"petal"* ]]; then
        import_csv "iris" \
            "$DATA_DIR/iris.csv" \
            "sepal_length, sepal_width, petal_length, petal_width, species"
    else
        echo "  ⚠ Header non riconosciuto, import iris saltato"
    fi
fi

# Riepilogo finale.
echo ""
echo "# =================== Riepilogo Import =================== #"
mysql -u "$DB_USER" "$DB_NAME" -e "
    SELECT 'playtennis' AS tabella, COUNT(*) AS righe FROM playtennis
    UNION ALL
    SELECT 'weather_mixed', COUNT(*) FROM weather_mixed
    UNION ALL
    SELECT 'iris', COUNT(*) FROM iris;
"

echo ""
echo "✓ Import completato!"
