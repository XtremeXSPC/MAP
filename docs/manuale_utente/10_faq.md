# 10. FAQ e Riferimenti

## 10.1 Domande Frequenti

### Q: Qual è la differenza tra QT e K-Means?

**A**:
| Aspetto | QT Clustering | K-Means |
|---------|---------------|---------|
| Input | Radius (qualità) | K (numero cluster) |
| Output | K variabile | K fisso |
| Determinismo | SI | NO (dipende da seed) |
| Qualità | Garantita (radius) | Non garantita |
| Complessità | O(k×n²) | O(k×n×i) |
| Caso d'uso | Qualità critica | Velocità critica |

**Quando usare QT**: Quando la qualità dei cluster è più importante del numero.

**Quando usare K-Means**: Quando sai quanti cluster vuoi e performance sono critiche.

---

### Q: Come scelgo il radius ottimale?

**A**:

1. **Metodo Elbow** (raccomandato):
   - Testa radius: 0.0, 0.1, 0.2, ..., 1.0
   - Grafica radius vs num_clusters
   - Scegli "gomito" della curva

2. **Euristica dominio**:
   - Analisi esplorativa: radius 0.5
   - Alta precisione: radius 0.0-0.3
   - Overview rapida: radius 0.7-1.0

3. **Basato su metriche**:
   - Target: AvgDistance < 0.3
   - Target: 5-20 cluster

---

### Q: Il clustering può gestire valori mancanti (NULL)?

**A**: **Limitato**.

**Soluzione**:
1. **Preprocessing** (raccomandato):
   ```python
   # Rimuovi righe con NULL
   df = df.dropna()

   # Oppure imputa valori
   df['attr'].fillna(df['attr'].mode()[0])
   ```

2. **Database**: Usa `COALESCE`
   ```sql
   SELECT COALESCE(attribute, 'unknown') AS attribute FROM table
   ```

---

### Q: Posso usare QT per dataset con milioni di tuple?

**A**: **NO**, non scalabile per dataset enormi.

**Alternative**:
- **Sampling**: Usa 10% dataset rappresentativo
- **Pre-clustering**: K-Means veloce → QT sui centroidi
- **Algoritmi big data**: DBSCAN distribuito, Canopy clustering

**Limiti pratici**:
- < 1000 tuple: Veloce (< 1 min)
- 1000-10000 tuple: Lento (1-30 min)
- > 10000 tuple: Molto lento (> 1 ora)

---

### Q: La GUI non si avvia su Linux

**A**:

**Causa**: JavaFX mancante in alcune distribuzioni OpenJDK.

**Soluzione**:
```bash
# Ubuntu/Debian
sudo apt install openjfx

# Fedora
sudo dnf install openjfx

# Oppure usa Oracle JDK (include JavaFX)
```

Verifica:
```bash
java --list-modules | grep javafx
# Output atteso: javafx.base, javafx.controls, ...
```

---

### Q: Posso eseguire clustering su dati testuali?

**A**: **SI**, ma richiede preprocessing.

**Procedura**:
1. **Tokenizzazione**:
   ```python
   from sklearn.feature_extraction.text import CountVectorizer
   vectorizer = CountVectorizer(max_features=50)
   features = vectorizer.fit_transform(texts)
   ```

2. **Discretizzazione** (per QT):
   ```python
   # Converti features numeriche in categorie
   df['feature1_cat'] = pd.cut(features[:, 0], bins=5, labels=['very_low', 'low', 'med', 'high', 'very_high'])
   ```

3. **Export CSV** e usa QT normalmente

---

### Q: Risultati diversi tra TUI e GUI?

**A**: **NO**, algoritmo identico.

**Se diversi**:
- Verifica stesso radius
- Verifica stesso dataset
- Verifica versione server (TUI e GUI devono connettersi allo stesso server)

---

### Q: Come contribuire al progetto?

**A**:

Progetto accademico, contributi benvenuti:

1. **Fork** repository
2. **Branch** per feature: `git checkout -b feature/nome`
3. **Commit** con messaggi chiari
4. **Push** e crea Pull Request

Aree di miglioramento:
- Ottimizzazioni performance
- Supporto PostgreSQL
- Metriche clustering aggiuntive (Silhouette, Davies-Bouldin)
- Esportazione PDF

---

## 10.2 Riferimenti Bibliografici

### Paper Originale QT

Heyer, L. J., Kruglyak, S., & Yooseph, S. (1999).
**Exploring expression data: identification and analysis of coexpressed genes.**
_Genome research_, 9(11), 1106-1115.

### Algoritmi Clustering

MacQueen, J. (1967).
**Some methods for classification and analysis of multivariate observations.**
_Proceedings of the Fifth Berkeley Symposium on Mathematical Statistics and Probability_, Volume 1: Statistics, 281--297.

Ester, M., Kriegel, H. P., Sander, J., & Xu, X. (1996).
**A density-based algorithm for discovering clusters in large spatial databases with noise.**
_Kdd_ (Vol. 96, No. 34, pp. 226-231).

### Metriche Clustering

Rousseeuw, P. J. (1987).
**Silhouettes: a graphical aid to the interpretation and validation of cluster analysis.**
_Journal of computational and applied mathematics_, 20, 53-65.

Davies, D. L., & Bouldin, D. W. (1979).
**A cluster separation measure.**
_IEEE transactions on pattern analysis and machine intelligence_, (2), 224-227.

---

## 10.3 Risorse Online

### Documentazione Ufficiale

- **Progetto GitHub**: https://github.com/XtremeXSPC/MAP
- **Wiki**: https://github.com/XtremeXSPC/MAP/wiki
- **Issues**: https://github.com/XtremeXSPC/MAP/issues

### Tutorial

- **Video tutorial GUI**: (link placeholder)
- **Blog post algoritmo**: (link placeholder)
- **Dataset esempi**: https://github.com/XtremeXSPC/MAP/tree/main/datasets

### Tecnologie Utilizzate

- **Java SE**: https://docs.oracle.com/en/java/javase/11/
- **JavaFX**: https://openjfx.io/
- **MySQL**: https://dev.mysql.com/doc/
- **PlantUML**: https://plantuml.com/

### Community

- **Stack Overflow**: Tag `quality-threshold-clustering`
- **Reddit**: r/MachineLearning (per discussioni clustering)

---

## 10.4 Glossario

| Termine | Definizione |
|---------|-------------|
| **Centroide** | Tupla rappresentativa del cluster |
| **Cluster** | Gruppo di tuple simili |
| **Distance Metric** | Funzione per misurare similarità (Hamming, Euclidean) |
| **Hamming Distance** | Numero attributi diversi / totale attributi |
| **PCA** | Principal Component Analysis - riduzione dimensionalità |
| **Radius** | Soglia massima distanza intra-cluster |
| **Tuple** | Riga del dataset (insieme di attributi) |
| **Discrete Attribute** | Attributo categorico (valori finiti) |
| **Continuous Attribute** | Attributo numerico (valori reali) |
| **Silhouette** | Metrica qualità cluster [-1, 1] |
| **Serialization** | Conversione oggetto → file binario |

---

## 10.5 Licenza e Copyright

**Licenza**: MIT License (open source)

```
MIT License

Copyright (c) 2025 MAP Project

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 10.6 Contatti

**Sviluppatori**:
- Nome: (placeholder)
- Email: (placeholder)
- GitHub: https://github.com/XtremeXSPC

**Segnalazione bug**:
- GitHub Issues: https://github.com/XtremeXSPC/MAP/issues

**Richieste feature**:
- GitHub Discussions: https://github.com/XtremeXSPC/MAP/discussions

---

## Fine Manuale Utente

Grazie per aver utilizzato **Quality Threshold Clustering**!

Per ulteriori informazioni, consulta:
- [README.md principale](../../README.md)
- [Documentazione tecnica moduli](../README.md)
- [Diagrammi UML](../uml/)

---

**Versione Manuale**: 1.0
**Data**: 2025-11-09
**Pagine**: ~50 (formato Markdown)

[← Capitolo 9: Troubleshooting](09_troubleshooting.md) | [Torna all'indice](00_INDICE.md)
