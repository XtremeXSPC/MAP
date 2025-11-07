# Sprint 0 - Struttura Base del Progetto

## Obiettivo

Implementare le classi fondamentali per rappresentare i dati e la struttura base del sistema di clustering Quality Threshold.

## Durata

1 settimana

---

## Backlog dello Sprint

### 1. Classe `Attribute` (Abstract)

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Classe astratta che rappresenta un attributo generico in un sistema di clustering. Fornisce l'interfaccia base per attributi discreti e continui.

#### Criteri di Accettazione

- [ ] Implementare costruttore con nome e indice simbolico
- [ ] Implementare metodi getter per `name` e `index`
- [ ] Implementare metodo `toString()` per rappresentazione testuale

#### Dettagli Implementativi

```java
public abstract class Attribute {
    private String name;
    private int index;

    // Costruttore e metodi
}
```

**File:** `src/Attribute.java`

---

### 2. Classe `DiscreteAttribute`

**Priorità:** Alta
**Story Points:** 3

#### Descrizione

Estende `Attribute` per rappresentare attributi discreti con un insieme finito di valori possibili (es. "sunny", "overcast", "rain").

#### Criteri di Accettazione

- [ ] Estendere la classe `Attribute`
- [ ] Gestire array di valori discreti
- [ ] Implementare metodo per ottenere numero di valori distinti
- [ ] Implementare metodo per calcolare la frequenza di un valore

#### Dettagli Implementativi

```java
public class DiscreteAttribute extends Attribute {
    private String values[];

    DiscreteAttribute(String name, int index, String values[]);
    int getNumberOfDistinctValues();
    int frequency(Data data, Object v);
}
```

**File:** `src/DiscreteAttribute.java`

---

### 3. Classe `ContinuousAttribute`

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Estende `Attribute` per rappresentare attributi continui con valori numerici (min/max).

#### Criteri di Accettazione

- [ ] Estendere la classe `Attribute`
- [ ] Gestire valori min e max dell'attributo

#### Dettagli Implementativi

```java
public class ContinuousAttribute extends Attribute {
    private double min;
    private double max;

    ContinuousAttribute(String name, int index, double min, double max);
    double getMin();
    double getMax();
}
```

**File:** `src/ContinuousAttribute.java`

---

### 4. Classe `Item` (Abstract)

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Classe astratta che modella una coppia (Attributo, Valore). Rappresenta un singolo item in una tupla.

#### Criteri di Accettazione

- [ ] Implementare costruttore con attributo e valore
- [ ] Implementare getter per attributo e valore
- [ ] Implementare metodo astratto `distance()` per calcolare la distanza
- [ ] Implementare `toString()`

#### Dettagli Implementativi

```java
public abstract class Item {
    private Attribute attribute;
    private Object value;

    abstract double distance(Object a);
}
```

**File:** `src/Item.java`

---

### 5. Classe `DiscreteItem`

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Estende `Item` per rappresentare item con attributi discreti.

#### Criteri di Accettazione

- [ ] Estendere la classe `Item`
- [ ] Implementare calcolo della distanza (0 se uguali, 1 se diversi)

#### Dettagli Implementativi

```java
class DiscreteItem extends Item {
    DiscreteItem(DiscreteAttribute attribute, String value);
    double distance(Object a); // 0 se uguali, 1 se diversi
}
```

**File:** `src/DiscreteItem.java`

---

### 6. Classe `Tuple`

**Priorità:** Alta
**Story Points:** 3

#### Descrizione

Rappresenta una tupla come sequenza di item (coppie attributo-valore).

#### Criteri di Accettazione

- [ ] Implementare array di `Item`
- [ ] Implementare metodo `add()` per aggiungere item
- [ ] Implementare metodo `get()` per ottenere item
- [ ] Implementare metodo `getLength()` per ottenere lunghezza
- [ ] Implementare metodo `getDistance()` per calcolare distanza tra tuple

#### Dettagli Implementativi

```java
public class Tuple {
    private Item[] tuple;

    public Tuple(int size);
    public void add(Item c, int i);
    public Item get(int i);
    public int getLength();
    public double getDistance(Tuple obj);
    public double avgDistance(Data data, int clusteredData[]);
}
```

**Calcolo Distanza:**

```
distance(t1, t2) = Σ(i=0 to n-1) t1[i].distance(t2[i]) / n
```

**File:** `src/Tuple.java`

---

### 7. Classe `ArraySet`

**Priorità:** Alta
**Story Points:** 3

#### Descrizione

Modella un insieme di interi senza duplicati, utilizzato per tracciare gli indici delle tuple clusterizzate.

#### Criteri di Accettazione

- [ ] Implementare array dinamico di interi
- [ ] Implementare metodo `add()` che evita duplicati
- [ ] Implementare metodo `get()` per verificare presenza
- [ ] Implementare metodo `delete()` per rimuovere elementi
- [ ] Implementare metodo `size()` per ottenere dimensione
- [ ] Implementare metodo `toArray()` per conversione in array

#### Dettagli Implementativi

```java
class ArraySet {
    private int[] set;
    private int size;

    ArraySet();
    boolean add(int item);
    boolean get(int item);
    void delete(int item);
    int size();
    int[] toArray();
}
```

**File:** `src/ArraySet.java`

---

### 8. Classe `Data`

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Modella l'insieme di transazioni (dataset). Carica e gestisce i dati di esempio per il clustering.

#### Criteri di Accettazione

- [ ] Implementare matrice di dati (Object[][])
- [ ] Inizializzare dataset PlayTennis (14 esempi, 5 attributi)
- [ ] Implementare schema attributi (explanatorySet)
- [ ] Implementare metodo `getNumberOfExamples()`
- [ ] Implementare metodo `getNumberOfExplanatoryAttributes()`
- [ ] Implementare metodo `getValue()` per accedere ai valori
- [ ] Implementare metodo `getItemSet()` per creare tuple
- [ ] Implementare metodo `toString()` per visualizzare dati

#### Dataset PlayTennis

| Outlook  | Temperature | Humidity | Wind   | PlayTennis |
| -------- | ----------- | -------- | ------ | ---------- |
| sunny    | hot         | high     | weak   | no         |
| sunny    | hot         | high     | strong | no         |
| overcast | hot         | high     | weak   | yes        |
| rain     | mild        | high     | weak   | yes        |
| rain     | cool        | normal   | weak   | yes        |
| rain     | cool        | normal   | strong | no         |
| overcast | cool        | normal   | strong | yes        |
| sunny    | mild        | high     | weak   | no         |
| sunny    | cool        | normal   | weak   | yes        |
| rain     | mild        | normal   | weak   | yes        |
| sunny    | mild        | normal   | strong | yes        |
| overcast | mild        | high     | strong | yes        |
| overcast | hot         | normal   | weak   | yes        |
| rain     | mild        | high     | strong | no         |

#### Dettagli Implementativi

```java
public class Data {
    private Object data[][];
    private int numberOfExamples;
    private Attribute explanatorySet[];

    public Data();
    public int getNumberOfExamples();
    public int getNumberOfExplanatoryAttributes();
    public Attribute[] getAttributeSchema();
    public Object getValue(int exampleIndex, int attributeIndex);
    public Tuple getItemSet(int index);
}
```

**File:** `src/Data.java`

---

## Diagramma delle Classi

```
┌─────────────────┐
│   <<abstract>>  │
│    Attribute    │
│─────────────────│
│ - name: String  │
│ - index: int    │
└────────┬────────┘
         │
    ┌────┴─────┐
    │          │
┌───▼────────┐ │ ┌──────────────────┐
│ Discrete   │ │ │  Continuous      │
│ Attribute  │ │ │  Attribute       │
│────────────│ │ │──────────────────│
│ - values[] │ │ │ - min: double    │
└────────────┘ │ │ - max: double    │
               │ └──────────────────┘
               │
    ┌──────────▼──────────┐
    │    <<abstract>>     │
    │        Item         │
    │─────────────────────│
    │ - attribute         │
    │ - value: Object     │
    └──────────┬──────────┘
               │
               │
        ┌──────▼────────┐
        │ DiscreteItem  │
        └───────────────┘

┌─────────────────┐      ┌────────────────┐
│     Tuple       │◆────►│     Item       │
│─────────────────│ n    └────────────────┘
│ - tuple: Item[] │
└─────────────────┘

┌──────────────────┐
│      Data        │
│──────────────────│
│ - data[][]       │
│ - explanatorySet │
│ - numberOfEx...  │
└──────────────────┘

┌──────────────────┐
│    ArraySet      │
│──────────────────│
│ - set: int[]     │
│ - size: int      │
└──────────────────┘
```

---

## Test e Validazione

### Test della classe Data

```bash
cd src
javac *.java
java Data
```

**Output Atteso:**

```
Outlook,Temperature,Humidity,Wind,PlayTennis
1:sunny,hot,high,weak,no,
2:sunny,hot,high,strong,no,
3:overcast,hot,high,weak,yes,
...
```

### Test della classe Tuple

```java
Data data = new Data();
Tuple t1 = data.getItemSet(0);
Tuple t2 = data.getItemSet(1);
double distance = t1.getDistance(t2);
System.out.println("Distance: " + distance);
```

---

## Definizione di "Done"

- [ ] Tutte le classi compilano senza errori
- [ ] Ogni classe ha javadoc per tutti i metodi pubblici
- [ ] La classe Data stampa correttamente il dataset
- [ ] Il metodo getDistance() calcola correttamente la distanza tra tuple
- [ ] Tutte le classi sono committate nel repository

---

## Note Tecniche

### Convenzioni di Codifica

- Visibilità package-private per classi interne (Cluster, ArraySet, etc.)
- Visibilità public per classi principali (Data, QTMiner)
- Javadoc completo per tutti i metodi pubblici

### Struttura del Progetto

```
MAP/
├── src/
│   ├── Attribute.java
│   ├── ContinuousAttribute.java
│   ├── DiscreteAttribute.java
│   ├── Item.java
│   ├── DiscreteItem.java
│   ├── Tuple.java
│   ├── ArraySet.java
│   └── Data.java
└── docs/
    └── sprints/
        └── SPRINT_0.md
```

---

## Rischi e Mitigazioni

| Rischio                           | Probabilità | Impatto | Mitigazione                           |
| --------------------------------- | ----------- | ------- | ------------------------------------- |
| Errori nel calcolo della distanza | Media       | Alto    | Test approfonditi con casi noti       |
| Dataset hardcoded limitante       | Bassa       | Medio   | Documentare per futuri refactoring    |
| Gestione memoria ArraySet         | Bassa       | Medio   | Implementare raddoppio array dinamico |

---

## Retrospettiva

### Cosa è andato bene

- Struttura chiara e modulare
- Separazione tra attributi discreti e continui
- Design pattern Template Method per Item/Attribute

### Cosa migliorare

- Considerare l'uso di Collections invece di array nativi
- Aggiungere validazione input nei costruttori
- Implementare equals() e hashCode() dove appropriato

### Action Items

- [ ] Valutare refactoring di ArraySet con ArrayList in Sprint futuri
- [ ] Aggiungere test unitari (JUnit) in Sprint 2
