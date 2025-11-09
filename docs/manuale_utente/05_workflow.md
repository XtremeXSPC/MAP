# 5. Workflow Comuni

## 5.1 Clustering da CSV

### Workflow Completo GUI

1. **Preparazione CSV**
   ```csv
   attr1,attr2,attr3,class
   value1,value2,value3,classA
   value4,value5,value6,classB
   ```

2. **Importazione**
   - Home → Load CSV File
   - Seleziona file
   - Verifica preview

3. **Clustering**
   - Clustering → Imposta radius (es. 0.5)
   - Start Clustering
   - Attendi completamento

4. **Analisi**
   - Results → Visualizza scatter chart
   - Esamina tabella cluster
   - Controlla metriche

5. **Export**
   - Export Results → ZIP
   - Salva bundle completo

**Tempo**: 5-10 minuti

---

## 5.2 Clustering da Database

### Workflow TUI

```bash
# Avvia server
cd qtServer/bin && java server.MultiServer

# In altro terminale
cd qtClient/bin && java qtClient.MainTest

# Comandi
0                    # Carica da DB
mytable              # Nome tabella
1                    # Clustering
0.5                  # Radius
2                    # Salva
results_mytable      # Nome file
4                    # Esci
```

### Workflow GUI

1. Home → Load from Database
2. Inserisci credenziali MySQL
3. Test Connection
4. Load Data
5. Clustering → Radius → Start
6. Results → Esporta

---

## 5.3 Analisi e Interpretazione

### Metriche Chiave

| Metrica | Significato | Valore Ideale |
|---------|-------------|---------------|
| Num Cluster | Quanti gruppi | Dipende da dominio |
| Avg Distance | Compattezza | < 0.3 (buono) |
| Cluster Size | Dimensione media | Uniforme |
| Max Distance | Outlier | < radius |

### Scatter Chart

<!-- [IMMAGINE]: Scatter chart con 5 cluster colorati - dimensione: 800x600 -->
<!-- Mostra: PCA 2D con punti colorati per cluster, legenda -->

**Interpretazione**:
- **Cluster separati**: Buona qualità
- **Cluster sovrapposti**: Aumenta radius
- **Molti cluster piccoli**: Riduci radius

---

## 5.4 Esportazione Risultati

### Formato CSV

```csv
ClusterID,TupleIndex,Centroid,Distance
0,0,"sunny hot high weak no",0.0
0,1,"sunny hot high weak no",0.2
1,3,"overcast hot high weak yes",0.0
```

**Uso**: Import in Excel, Python pandas, R

### Formato TXT

```
CLUSTERING RESULTS REPORT
=========================
Dataset: playtennis
Radius: 0.0
Clusters: 11

CLUSTER 0
---------
Size: 3
Centroid: sunny hot high weak no
...
```

**Uso**: Documentazione, report

### Formato JSON

```json
{
  "metadata": {
    "dataset": "playtennis",
    "radius": 0.0,
    "numClusters": 11
  },
  "clusters": [
    {
      "id": 0,
      "centroid": ["sunny","hot","high","weak","no"],
      "tuples": [...]
    }
  ]
}
```

**Uso**: API, web app, scripting

### Formato ZIP

Contiene:
- `results.csv`
- `report.txt`
- `data.json`
- `chart.png`
- `config.properties`

**Uso**: Condivisione, backup, archiviazione

---

[← Capitolo 4: TUI](04_tui.md) | [Capitolo 6: Database →](06_database.md)
