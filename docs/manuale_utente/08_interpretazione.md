# 8. Interpretazione Risultati

## 8.1 Metriche Cluster

### Dimensione Cluster (Size)

**Definizione**: Numero tuple nel cluster

```
Cluster 0: Size = 5 tuple
Cluster 1: Size = 3 tuple
```

**Interpretazione**:
- Size uniforme = buon bilanciamento
- Size molto diversi = cluster dominanti + outlier
- Size = 1 = possibili outlier

### Distanza Media (AvgDistance)

**Definizione**: Media distanze tuple-centroide

```
AvgDistance = Σ distance(tuple, centroid) / size
```

**Interpretazione**:
| AvgDistance | Significato | Qualità |
|-------------|-------------|---------|
| < 0.2 | Cluster molto compatto | Ottima |
| 0.2 - 0.4 | Cluster moderato | Buona |
| 0.4 - 0.6 | Cluster disperso | Accettabile |
| > 0.6 | Cluster molto disperso | Bassa |

### Numero Cluster

**Definizione**: Totale cluster scoperti

**Relazione con Radius**:
```
Radius ↓ → Num Cluster ↑
Radius ↑ → Num Cluster ↓
```

**Interpretazione**:
- Troppi cluster (> 50% tuple) = radius troppo basso
- Troppo pochi cluster (< 5) = radius troppo alto
- Numero ragionevole = 5-20 cluster (dipende da dataset)

---

## 8.2 Scatter Chart PCA

### Cos'è PCA?

**Principal Component Analysis**: Riduzione dimensionalità da N attributi a 2D.

<!-- [IMMAGINE]: Scatter chart con cluster colorati e centroidi - dimensione: 1000x700 -->

### Interpretazione Chart

**Elementi Visivi**:
- **Punti**: Tuple (colorate per cluster)
- **Centroidi**: Marker più grande (es. stella)
- **Distanza**: Vicinanza visiva = similarità
- **Colori**: Cluster diversi = colori diversi

**Pattern da Cercare**:

1. **Cluster ben separati**
   ```
   ●●●        ■■■        ▲▲▲
   ●●    →    ■■    →    ▲▲
   ●●●        ■■■        ▲▲▲
   ```
   Qualità ALTA

2. **Cluster sovrapposti**
   ```
   ●●■■▲▲
   ●■■▲▲▲
   ●●■▲▲
   ```
   Qualità BASSA → Aumenta radius

3. **Molti cluster piccoli**
   ```
   ●  ■  ▲  ★  ◆  ♦  +  x
   ```
   Radius troppo basso

---

## 8.3 Scelta Radius Ottimale

### Metodo Elbow

1. Esegui clustering con radius crescenti: 0.0, 0.1, 0.2, ..., 1.0
2. Conta numero cluster per ogni radius
3. Grafica radius vs num_clusters
4. Trova "gomito" (elbow point)

<!-- [IMMAGINE]: Grafico elbow method - dimensione: 700x500 -->

```
Num     │
Cluster │  *
        │   *
   20   │    *
        │     *
   10   │       *
        │         * ← GOMITO (radius ottimale ≈ 0.5)
    5   │           *
        │             *
        └─────────────────────
         0.0  0.5  1.0  Radius
```

**Radius ottimale**: Punto dove curva si appiattisce

### Metodo Silhouette

**Formula**:
```
silhouette(i) = (b(i) - a(i)) / max(a(i), b(i))

a(i) = distanza media intra-cluster
b(i) = distanza media inter-cluster
```

**Range**: [-1, 1]
- Silhouette > 0.5 = buon cluster
- Silhouette < 0.2 = cluster dubbioso

### Criteri Dominio-Specifici

**Esempio: Marketing Segmentation**
- Troppi segmenti (> 10) = difficile gestire
- Troppo pochi (< 3) = perdita informazione
- Radius ottimale: quello che produce 5-8 cluster

**Esempio: Anomaly Detection**
- Molti cluster piccoli = OK (ogni anomalia diversa)
- Radius basso preferibile

---

## 8.4 Confronto con Classificazione Ground Truth

Se hai labels reali (es. specie iris):

### Confusion Matrix

```
               Predicted
           C0    C1    C2
Actual  A  30    2     1
        B   1   28     3
        C   0    2    31
```

### Purezza Cluster

```
purity = Σ max(cluster_i ∩ class_j) / n
```

Purezza > 0.8 = buon allineamento con ground truth

---

[← Capitolo 7: Import/Export](07_import_export.md) | [Capitolo 9: Troubleshooting →](09_troubleshooting.md)
