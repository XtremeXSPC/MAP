# Sprint 3 - Supporto Attributi Continui

## Obiettivo

Estendere il sistema di clustering Quality Threshold per supportare attributi numerici/continui usando distanza Euclidea normalizzata, abilitando il clustering su dataset misti (discreti + continui).

## Durata

1 settimana

## Prerequisiti

- Sprint 0 completato (classi base)
- Sprint 1 completato (algoritmo QT)
- `ContinuousAttribute` già implementato con normalizzazione

---

## Backlog dello Sprint

### 1. Implementazione `ContinuousItem`

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Creare la classe `ContinuousItem` che estende `Item` per gestire valori numerici continui con distanza Euclidea normalizzata.

#### Criteri di Accettazione

- [ ] Classe `ContinuousItem` estende `Item`
- [ ] Costruttore accetta `ContinuousAttribute` e valore `Double`
- [ ] Metodo `distance(Object a)` implementa distanza Euclidea normalizzata
- [ ] Normalizzazione usa `ContinuousAttribute.getScaledValue()`
- [ ] Gestione corretta dei tipi (Double vs Object)
- [ ] Javadoc completo per classe e metodi

#### Dettagli Implementativi

```java
public class ContinuousItem extends Item {
    /**
     * Costruttore per ContinuousItem.
     *
     * @param attribute attributo continuo
     * @param value valore numerico (Double)
     */
    public ContinuousItem(ContinuousAttribute attribute, Double value);

    /**
     * Calcola distanza Euclidea normalizzata tra valori continui.
     *
     * Formula: |scaledValue1 - scaledValue2|
     * dove scaledValue = (value - min) / (max - min)
     *
     * @param a valore con cui confrontare (Double)
     * @return distanza normalizzata in [0, 1]
     */
    @Override
    public double distance(Object a);
}
```

#### Formula Distanza

**Distanza Euclidea Normalizzata (1D):**

```
distance = |v1_scaled - v2_scaled|

dove:
  v_scaled = (v - min) / (max - min)
  min, max = range attributo
```

**Esempio:**

```
Attributo: Temperature (min=10, max=40)
Value 1: 15°C → scaled = (15-10)/(40-10) = 0.167
Value 2: 25°C → scaled = (25-10)/(40-10) = 0.500
Distance: |0.167 - 0.500| = 0.333
```

**Proprietà:**

- Range output: [0, 1] (normalizzato)
- Compatibile con distanza Hamming discreta (anche in [0, 1])
- Permette combinazione distanze discrete/continue

**File:** `src/ContinuousItem.java`

---

### 2. Correzione Bug in `Data.getItemSet()`

**Priorità:** Critica
**Story Points:** 3

#### Descrizione

Correggere il metodo `Data.getItemSet()` che attualmente crea sempre `DiscreteItem` anche quando l'attributo è continuo, causando ClassCastException.

#### Bug Identificato

**Codice attuale (src/Data.java:164-171):**

```java
public Tuple getItemSet(int index) {
    Tuple tuple = new Tuple(explanatorySet.length);
    for (int i = 0; i < explanatorySet.length; i++) {
        // BUG: Cast sempre a DiscreteAttribute e String
        tuple.add(new DiscreteItem((DiscreteAttribute) explanatorySet[i],
                (String) data[index][i]), i);
    }
    return tuple;
}
```

**Problema:**

- Assume tutti gli attributi siano discreti
- Cast `(DiscreteAttribute)` fallisce se attributo è continuo
- Cast `(String)` fallisce se valore è numerico

#### Soluzione Proposta

```java
public Tuple getItemSet(int index) {
    Tuple tuple = new Tuple(explanatorySet.length);
    for (int i = 0; i < explanatorySet.length; i++) {
        Attribute attr = explanatorySet[i];
        Object value = data[index][i];

        if (attr instanceof DiscreteAttribute) {
            // Attributo discreto → DiscreteItem
            tuple.add(new DiscreteItem((DiscreteAttribute) attr, (String) value), i);
        } else if (attr instanceof ContinuousAttribute) {
            // Attributo continuo → ContinuousItem
            Double numValue;
            if (value instanceof String) {
                numValue = Double.parseDouble((String) value);
            } else {
                numValue = (Double) value;
            }
            tuple.add(new ContinuousItem((ContinuousAttribute) attr, numValue), i);
        }
    }
    return tuple;
}
```

#### Criteri di Accettazione

- [ ] Metodo controlla tipo attributo con `instanceof`
- [ ] Crea `DiscreteItem` per attributi discreti
- [ ] Crea `ContinuousItem` per attributi continui
- [ ] Gestisce parsing da String a Double per valori continui
- [ ] Test con dataset solo discreti (playtennis.csv)
- [ ] Test con dataset solo continui (iris.csv)
- [ ] Test con dataset misti

**File:** `src/Data.java` (modificare metodo `getItemSet()`)

---

### 3. Dataset Iris

**Priorità:** Alta
**Story Points:** 3

#### Descrizione

Creare dataset Iris standard per machine learning (150 tuple, 4 attributi continui, 1 attributo discreto target).

#### Dataset Iris Specification

**Attributi:**

1. `sepal_length` - Lunghezza sepalo (cm) - Continuo
2. `sepal_width` - Larghezza sepalo (cm) - Continuo
3. `petal_length` - Lunghezza petalo (cm) - Continuo
4. `petal_width` - Larghezza petalo (cm) - Continuo
5. `species` - Specie (setosa, versicolor, virginica) - Discreto

**Statistiche:**

- 150 tuple totali
- 50 tuple per specie
- Range sepal_length: 4.3 - 7.9 cm
- Range sepal_width: 2.0 - 4.4 cm
- Range petal_length: 1.0 - 6.9 cm
- Range petal_width: 0.1 - 2.5 cm

#### Formato CSV

```csv
sepal_length,sepal_width,petal_length,petal_width,species
5.1,3.5,1.4,0.2,setosa
4.9,3.0,1.4,0.2,setosa
...
```

#### Criteri di Accettazione

- [ ] File `data/iris.csv` creato con 150 tuple
- [ ] Header corretto con 5 colonne
- [ ] Valori continui con 1 decimale
- [ ] 50 tuple per ogni specie (setosa, versicolor, virginica)
- [ ] Dataset carica correttamente con `Data("data/iris.csv")`
- [ ] Inferenza tipi automatica riconosce 4 continui + 1 discreto

**File:** `data/iris.csv`

**Fonte dati:** UCI Machine Learning Repository - Iris Dataset

---

### 4. Dataset Misto (Mixed Attributes)

**Priorità:** Media
**Story Points:** 2

#### Descrizione

Creare dataset sintetico con attributi sia discreti che continui per testare capacità sistema su dati misti.

#### Specifica Dataset

**Nome:** `data/weather_mixed.csv`

**Attributi:**

1. `outlook` - Condizione cielo (sunny, overcast, rain) - Discreto
2. `temperature` - Temperatura in °C (10-40) - Continuo
3. `humidity` - Umidità percentuale (50-100) - Continuo
4. `wind` - Vento (strong, weak) - Discreto
5. `play` - Decisione (yes, no) - Discreto

**Dimensione:** 30 tuple

**Esempio:**

```csv
outlook,temperature,humidity,wind,play
sunny,25.5,65.0,weak,yes
overcast,22.0,80.5,strong,yes
rain,18.5,95.0,weak,no
...
```

#### Criteri di Accettazione

- [ ] File `data/weather_mixed.csv` creato
- [ ] 30 tuple con dati realistici
- [ ] 2 attributi continui (temperature, humidity)
- [ ] 3 attributi discreti (outlook, wind, play)
- [ ] Caricamento con inferenza tipi corretta
- [ ] Clustering funzionante

**File:** `data/weather_mixed.csv`

---

## Testing

### Test Funzionali

#### Test 1: ContinuousItem Distance

**Setup:**

```java
ContinuousAttribute temp = new ContinuousAttribute("Temperature", 0, 10.0, 40.0);
ContinuousItem item1 = new ContinuousItem(temp, 15.0);
ContinuousItem item2 = new ContinuousItem(temp, 25.0);
```

**Test:**

```java
double distance = item1.distance(25.0);
// Expected: |((15-10)/(40-10)) - ((25-10)/(40-10))|
//         = |(5/30) - (15/30)| = |0.167 - 0.500| = 0.333
assertEquals(0.333, distance, 0.001);
```

---

#### Test 2: Data.getItemSet() con Attributi Misti

**Setup:**

```java
Data data = new Data("data/weather_mixed.csv");
Tuple tuple = data.getItemSet(0);
```

**Test:**

```java
// Verifica tipi item corretti
assertTrue(tuple.get(0) instanceof DiscreteItem);  // outlook
assertTrue(tuple.get(1) instanceof ContinuousItem); // temperature
assertTrue(tuple.get(2) instanceof ContinuousItem); // humidity
assertTrue(tuple.get(3) instanceof DiscreteItem);  // wind
assertTrue(tuple.get(4) instanceof DiscreteItem);  // play
```

---

#### Test 3: Clustering Iris Dataset

**Setup:**

```java
Data iris = new Data("data/iris.csv");
QTMiner miner = new QTMiner(0.3, false); // radius = 0.3
```

**Test:**

```java
int numClusters = miner.compute(iris);

// Aspettato: 3-5 cluster (idealmente 3 per le 3 specie)
assertTrue(numClusters >= 3 && numClusters <= 5);

// Verifica tutti i punti clusterizzati
ClusterSet clusters = miner.getC();
int totalPoints = 0;
for (int i = 0; i < numClusters; i++) {
    totalPoints += clusters.get(i).getSize();
}
assertEquals(150, totalPoints);
```

---

#### Test 4: Clustering Dataset Misto

**Setup:**

```java
Data mixed = new Data("data/weather_mixed.csv");
QTMiner miner = new QTMiner(0.5, false);
```

**Test:**

```java
int numClusters = miner.compute(mixed);

// Dovrebbe creare cluster ragionevoli
assertTrue(numClusters >= 2 && numClusters <= 10);

// Output per ispezione manuale
System.out.println(miner.getC().toString(mixed));
```

---

#### Test 5: Distanza Mista (Discrete + Continuous)

**Setup:**

```java
// Tuple 1: (sunny, 25.0, 70.0, weak, yes)
// Tuple 2: (sunny, 30.0, 80.0, weak, yes)
```

**Test:**

```java
double distance = tuple1.getDistance(tuple2);

// Distance calculation:
// - outlook: same (sunny) → 0
// - temperature: |scaled(25) - scaled(30)| → continua
// - humidity: |scaled(70) - scaled(80)| → continua
// - wind: same (weak) → 0
// - play: same (yes) → 0
// Total: (0 + d_temp + d_hum + 0 + 0) / 5

// Verifica distanza ragionevole
assertTrue(distance >= 0.0 && distance <= 1.0);
```

---

### Test Regressione

#### Verifica Dataset Discreti Funzionano Ancora

**Test:**

```java
@Test
public void testPlayTennisStillWorking() {
    Data data = new Data(); // Costruttore hardcoded
    QTMiner miner = new QTMiner(0.0, false);
    int numClusters = miner.compute(data);

    // Deve funzionare come prima di Sprint 4
    assertTrue(numClusters > 0);
}

@Test
public void testPlayTennisCSVStillWorking() {
    Data data = new Data("data/playtennis.csv");
    QTMiner miner = new QTMiner(0.5, false);
    int numClusters = miner.compute(data);

    assertTrue(numClusters > 0);
}
```

---

## Architettura Modificata

### Gerarchia Item dopo Sprint 4

```
         ┌──────────────────┐
         │  <<abstract>>    │
         │      Item        │
         │──────────────────│
         │ - attribute      │
         │ - value: Object  │
         │──────────────────│
         │ + distance(...)  │ (abstract)
         └────────┬─────────┘
                  △
                  │ extends
         ┌────────┴─────────┐
         │                  │
┌────────▼────────┐  ┌──────▼──────────┐
│  DiscreteItem   │  │ ContinuousItem  │
│─────────────────│  │─────────────────│
│ + distance(...) │  │ + distance(...) │
│   → Hamming     │  │   → Euclidean   │
│   → 0 or 1      │  │   → [0, 1]      │
└─────────────────┘  └─────────────────┘
```

### Flusso Data.getItemSet()

```
Data.getItemSet(index)
  │
  ├─► per ogni attributo i:
  │     │
  │     ├─► if (attr instanceof DiscreteAttribute)
  │     │     └─► new DiscreteItem(attr, stringValue)
  │     │
  │     └─► else if (attr instanceof ContinuousAttribute)
  │           └─► new ContinuousItem(attr, doubleValue)
  │
  └─► return Tuple
```

---

## Deliverables

### Codice

- [ ] `src/ContinuousItem.java` - Nuova classe
- [ ] `src/Data.java` - Metodo `getItemSet()` modificato

### Dataset

- [ ] `data/iris.csv` - Dataset Iris (150 tuple)
- [ ] `data/weather_mixed.csv` - Dataset misto (30 tuple)

### Documentazione

- [ ] `docs/sprints/SPRINT_4.md` - Questo documento
- [ ] Javadoc per `ContinuousItem`
- [ ] Aggiornamento `SPRINT_ROADMAP.md`
- [ ] (Opzionale) `docs/SPRINT_4_RESULTS.md` - Report risultati

### Test

- [ ] Test manuali con `MainTest`
- [ ] Verifica clustering Iris
- [ ] Verifica clustering dataset misto
- [ ] Test regressione playtennis

---

## Metriche di Successo

### Criteri Quantitativi

| Metrica                 | Target                 | Verifica                                                 |
| ----------------------- | ---------------------- | -------------------------------------------------------- |
| Classi implementate     | 1 nuova + 1 modificata | ContinuousItem + Data                                    |
| Dataset creati          | 2                      | iris.csv + weather_mixed.csv                             |
| Test funzionali passati | 5/5                    | Tutti i test in sezione Testing                          |
| Test regressione        | 2/2                    | PlayTennis ancora funzionante                            |
| LOC aggiunte            | ~80-100                | ContinuousItem (~40) + Data fix (~20) + dataset (~30-40) |

### Criteri Qualitativi

- [ ] **Compatibilità:** Dataset esistenti funzionano senza modifiche
- [ ] **Estensibilità:** Nuovi attributi continui facilmente aggiungibili
- [ ] **Correttezza:** Distanze normalizzate in [0, 1]
- [ ] **Documentazione:** Javadoc completo e chiaro
- [ ] **Usabilità:** Inferenza tipi automatica da CSV

---

## Esempio Output Clustering Iris

```
Dataset: data/iris.csv (150 tuples, 5 attributes)
Radius: 0.3

Numero di cluster: 3

Cluster 1: (Setosa)
Centroid=(5.1, 3.5, 1.4, 0.2, setosa)
Examples:
[5.1, 3.5, 1.4, 0.2, setosa] dist=0.0
[4.9, 3.0, 1.4, 0.2, setosa] dist=0.15
[5.0, 3.6, 1.4, 0.2, setosa] dist=0.08
...
Size: 50, AvgDistance: 0.12

Cluster 2: (Versicolor)
Centroid=(5.9, 2.8, 4.3, 1.3, versicolor)
Examples:
[5.9, 2.8, 4.3, 1.3, versicolor] dist=0.0
[6.0, 2.9, 4.5, 1.5, versicolor] dist=0.18
...
Size: 48, AvgDistance: 0.22

Cluster 3: (Virginica)
Centroid=(6.5, 3.0, 5.5, 2.0, virginica)
Examples:
[6.5, 3.0, 5.5, 2.0, virginica] dist=0.0
[6.7, 3.1, 5.6, 2.4, virginica] dist=0.24
...
Size: 52, AvgDistance: 0.25
```

---

## Rischi e Mitigazioni

### Rischio 1: Overflow/Underflow Numerico

**Probabilità:** Bassa
**Impatto:** Medio

**Mitigazione:**

- Normalizzazione garantisce valori in [0, 1]
- Evita calcoli su valori molto grandi/piccoli
- Validazione range min < max in ContinuousAttribute

---

### Rischio 2: Parsing Errori da CSV

**Probabilità:** Media
**Impatto:** Alto

**Mitigazione:**

- Try-catch in Data.getItemSet() per NumberFormatException
- Validazione formato durante inferAttributeType()
- Messaggi errore chiari con riga e colonna

---

### Rischio 3: Performance su Dataset Continui

**Probabilità:** Bassa
**Impatto:** Basso

**Mitigazione:**

- Distanza Euclidea ha stessa complessità O(1) di Hamming
- Normalizzazione pre-calcolata in ContinuousAttribute (min, max)
- Eventuale caching distanze (già disponibile da Sprint 3)

---

## Note Implementative

### Normalizzazione Valori

**Metodo Min-Max:**

```
scaled_value = (value - min) / (max - min)
```

**Proprietà:**

- Output in [0, 1]
- Preserva proporzioni relative
- Sensibile a outlier (min/max estremi)

**Alternativa (non implementata ora):**

- Z-score normalization: (value - mean) / std_dev
- Robust a outlier ma output non limitato a [0, 1]

### Combinazione Distanze Discrete/Continue

Attualmente in `Tuple.getDistance()`:

```java
distance = Σ(item[i].distance(...)) / n
```

**Per dataset misti:**

- Distanza Hamming (discrete): 0 o 1
- Distanza Euclidea (continuous): [0, 1] (normalizzata)
- **Media aritmetica funziona** perché entrambe in [0, 1]

**Futura ottimizzazione (Sprint successivo):**

- Pesi per attributi: `w[i] * distance[i]`
- Feature selection: ignorare attributi irrilevanti

---

## Riferimenti

### Dataset

- **Iris Dataset:** Fisher, R.A. (1936). "The use of multiple measurements in taxonomic problems"
- **UCI ML Repository:** <https://archive.ics.uci.edu/ml/datasets/iris>

### Metriche di Distanza

- **Hamming Distance:** Per attributi categorici
- **Euclidean Distance:** Per attributi numerici
- **Normalizzazione:** Min-max scaling

### Java API

- `instanceof` operator: Type checking a runtime
- `Double.parseDouble()`: String to double conversion

---

## Definition of Done

Sprint 4 è completato quando:

1. [x] Classe `ContinuousItem` implementata e testata
2. [x] Bug `Data.getItemSet()` corretto
3. [x] Dataset `iris.csv` creato e caricabile
4. [x] Dataset `weather_mixed.csv` creato e caricabile
5. [x] Clustering funzionante su dataset continui
6. [x] Clustering funzionante su dataset misti
7. [x] Test regressione passati (playtennis ancora funziona)
8. [x] Javadoc completo
9. [x] Documentazione aggiornata
10. [x] Codice committato e pushato

---

## Prossimi Passi (Post Sprint 4)

Dopo aver completato Sprint 4, si aprono nuove possibilità:

### Possibili Sprint 5+ Enhancements

1. **Metriche Qualità (Sprint 6)**
   - Silhouette coefficient per dataset continui
   - Confronto QT vs K-Means su Iris

2. **Visualizzazione (Sprint 5)**
   - Scatter plot 2D per Iris (PCA se >2 dimensioni)
   - Colori diversi per cluster

3. **Pesi Attributi**
   - Permettere pesi diversi per attributi discreti/continui
   - Feature importance

4. **Altri Dataset Standard**
   - Wine dataset (13 attributi continui)
   - Breast Cancer Wisconsin (misto)

---

**Fine Sprint 4 Documentation**

**Versione:** 1.0
**Data Creazione:** 2025-11-07
**Autore:** Claude AI Assistant
**Status:** 📝 Pianificato
