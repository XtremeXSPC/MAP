# 7. Importazione e Esportazione Dati

## 7.1 Formato CSV

### Formato Standard

```csv
attribute1,attribute2,attribute3
value1,value2,value3
value4,value5,value6
```

**Regole**:
- Prima riga = header (obbligatorio)
- Separatore = virgola
- Quote = opzionale per stringhe con virgole
- Encoding = UTF-8

### Esempio Valido

```csv
sepal_length,sepal_width,petal_length,species
5.1,3.5,1.4,setosa
4.9,3.0,1.4,setosa
7.0,3.2,4.7,versicolor
```

### Problemi Comuni

| Problema | Causa | Fix |
|----------|-------|-----|
| "Header missing" | Nessuna prima riga | Aggiungi intestazioni |
| "Column mismatch" | Righe lunghezze diverse | Uniforma colonne |
| "Encoding error" | UTF-8 vs ISO-8859-1 | Converti encoding |

---

## 7.2 Formati Export Supportati

### CSV

**Pro**:
- Universale (Excel, Python, R)
- Human-readable
- Lightweight

**Contro**:
- No metadata
- No struttura gerarchica

### TXT

**Pro**:
- Leggibilità massima
- Include tutte le info

**Contro**:
- Non parsabile automaticamente
- File grandi

### JSON

**Pro**:
- Struttura standard
- API-friendly
- Metadata incluso

**Contro**:
- Più pesante di CSV

### PNG (Chart)

**Pro**:
- Visualizzazione immediata
- Presentazioni

**Contro**:
- Dati non estraibili

### ZIP (Bundle)

**Pro**:
- Tutto incluso
- Compresso
- Pronto per condivisione

**Contro**:
- Dimensione maggiore

---

## 7.3 Batch Processing

### Script Export Multiplo

```bash
#!/bin/bash

RADIUS_VALUES=(0.0 0.2 0.5 0.8 1.0)

for r in "${RADIUS_VALUES[@]}"; do
    echo "Processing radius $r"

    # Avvia clustering
    echo "1\n$r\n2\nresults_r$r\n" | java qtClient.MainTest

    # Export
    # (opzione manuale GUI o script custom)
done

echo "Batch completed!"
```

### Parametric Study

Analizza impatto radius:

```python
# Python script per analizzare results
import json

radii = [0.0, 0.2, 0.5, 0.8, 1.0]
clusters = []

for r in radii:
    with open(f'results_r{r}.json') as f:
        data = json.load(f)
        clusters.append({
            'radius': r,
            'num_clusters': data['metadata']['numClusters'],
            'avg_size': data['metadata']['avgClusterSize']
        })

# Plot results
import matplotlib.pyplot as plt
plt.plot([c['radius'] for c in clusters],
         [c['num_clusters'] for c in clusters])
plt.xlabel('Radius')
plt.ylabel('Number of Clusters')
plt.title('Radius vs Clusters')
plt.show()
```

<!-- [IMMAGINE]: Grafico radius vs numero cluster - dimensione: 700x500 -->

---

[← Capitolo 6: Database](06_database.md) | [Capitolo 8: Interpretazione →](08_interpretazione.md)
