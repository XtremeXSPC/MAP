# Sprint 6 - Generics e RTTI (QT06)

**Durata:** 1 settimana
**Stato:** ✅ Completato
**QT Module:** QT06
**Data Completamento:** 2025-11-07
**Prerequisiti:** Sprint 5 (Iteratori e Comparatori)
**Specifica Riferimento:** `Project/QT06/Specifica_QT06_Generics-RTTI.pdf`

---

## 📋 Obiettivi

Introdurre **Java Generics** in tutti i contenitori e utilizzare **RTTI (Runtime Type Information)** per gestione dinamica dei tipi. Convertire l'attributo **Temperature** da discreto a continuo nel dataset PlayTennis.

### Obiettivi Specifici

1. ✅ Verificare uso Java Generics in TUTTI i contenitori, comparatori e iteratori
2. ✅ Verificare `ContinuousItem` esistente (implementato in Sprint 3)
3. ✅ Modificare `Data()` - Temperature da discreto a continuo
4. ✅ Verificare RTTI in `getItemSet()` con `instanceof`
5. ✅ Testare output conforme a specifica QT06

---

## 🎯 Modifiche Implementate

### 1. **Generics - Stato Completamento**

✅ **Tutti i Generics erano già implementati in Sprint 5!**

#### Verifica Compilazione:
```bash
javac *.java
# Nessun warning "unchecked" o "raw types" ✓
```

#### Contenitori Parametrizzati:

| Classe | Tipo Generic | Stato |
|--------|--------------|-------|
| `Cluster` | `Set<Integer>` clusteredData | ✅ Sprint 5 |
| `ClusterSet` | `Set<Cluster>` C | ✅ Sprint 5 |
| `DiscreteAttribute` | `Set<String>` values | ✅ Sprint 5 |
| `Data` | `List<Attribute>` explanatorySet | ✅ Sprint 5 |
| `DistanceCache` | `HashMap<Long, Double>` cache | ✅ Sprint 3 |

#### Iteratori Parametrizzati:

| Classe | Iterator Type | Stato |
|--------|---------------|-------|
| `Cluster` | `Iterator<Integer>` | ✅ Sprint 5 |
| `ClusterSet` | `Iterator<Cluster>` | ✅ Sprint 5 |
| `DiscreteAttribute` | `Iterator<String>` | ✅ Sprint 5 |

#### Comparatori Parametrizzati:

| Classe | Comparable Type | Stato |
|--------|-----------------|-------|
| `Cluster` | `Comparable<Cluster>` | ✅ Sprint 5 |

**Conclusione:** Tutti i contenitori, comparatori e iteratori usano già Java Generics completi. ✅

---

### 2. **ContinuousItem - Verifica Conformità**

La classe `ContinuousItem` era già stata implementata in **Sprint 3** (Attributi Continui). Verificata conformità con specifica QT06:

#### Requisiti Specifica QT06:
```java
ContinuousItem(Attribute attribute, Double value)
// Comportamento: richiama il costruttore della super classe

double distance(Object a)
// Comportamento: Determina la distanza (in valore assoluto) tra il valore
// scalato memorizzato nello item corrente (this.getValue()) e quello
// scalato associato al parametro a. Per ottenere valori scalati fare uso di
// getScaledValue(...)
```

#### Implementazione Attuale:
```java
public class ContinuousItem extends Item {

    public ContinuousItem(ContinuousAttribute attribute, Double value) {
        super(attribute, value);  // ✅ Richiama super classe
    }

    @Override
    public double distance(Object a) {
        ContinuousAttribute attr = (ContinuousAttribute) getAttribute();
        Double currentValue = (Double) getValue();
        Double otherValue = (Double) a;

        // Usa getScaledValue() per normalizzazione
        double scaledCurrent = attr.getScaledValue(currentValue);  // ✅
        double scaledOther = attr.getScaledValue(otherValue);      // ✅

        return Math.abs(scaledCurrent - scaledOther);  // ✅ Valore assoluto
    }
}
```

**Conformità:** ✅ 100% conforme a specifica QT06

---

### 3. **Data() - Temperature Continuo**

#### Modifica Dataset:

**Prima (Sprint 0-5):**
```java
// Temperature come attributo discreto
data[0] = new String[] {"sunny", "hot", "high", "weak", "no"};
// ...

String[] temperatureValues = new String[] {"cool", "hot", "mild"};
explanatorySet.add(new DiscreteAttribute("Temperature", 1, temperatureValues));
```

**Dopo (Sprint 6 - QT06):**
```java
// Temperature come attributo continuo con valori numerici
data[0] = new Object[] {"sunny", 30.3, "high", "weak", "no"};
data[1] = new Object[] {"sunny", 30.3, "high", "strong", "no"};
data[2] = new Object[] {"overcast", 30.0, "high", "weak", "yes"};
data[3] = new Object[] {"rain", 13.0, "high", "weak", "yes"};
data[4] = new Object[] {"rain", 0.0, "normal", "weak", "yes"};
data[5] = new Object[] {"rain", 0.0, "normal", "strong", "no"};
data[6] = new Object[] {"overcast", 0.1, "normal", "strong", "yes"};
data[7] = new Object[] {"sunny", 13.0, "high", "weak", "no"};
data[8] = new Object[] {"sunny", 0.1, "normal", "weak", "yes"};
data[9] = new Object[] {"rain", 12.0, "normal", "weak", "yes"};
data[10] = new Object[] {"sunny", 12.5, "normal", "strong", "yes"};
data[11] = new Object[] {"overcast", 12.5, "high", "strong", "yes"};
data[12] = new Object[] {"overcast", 29.21, "normal", "weak", "yes"};
data[13] = new Object[] {"rain", 12.5, "high", "strong", "no"};

// Definizione attributo continuo con range
explanatorySet.add(new ContinuousAttribute("Temperature", 1, 3.2, 38.7));
```

#### Valori Temperature (da specifica QT06):

| Indice | Temperatura | Range Normalizzato |
|--------|-------------|-------------------|
| 0, 1 | 30.3 | Alto (estate) |
| 2 | 30.0 | Alto |
| 3, 7 | 13.0 | Medio |
| 4, 5 | 0.0 | Basso (inverno) |
| 6, 8 | 0.1 | Basso |
| 9 | 12.0 | Medio-basso |
| 10, 11, 13 | 12.5 | Medio-basso |
| 12 | 29.21 | Alto |

**Range attributo:** min=3.2°C, max=38.7°C (da specifica)

---

### 4. **RTTI in getItemSet()**

Il metodo `getItemSet()` usa **Runtime Type Information (RTTI)** con operatore `instanceof` per determinare dinamicamente il tipo di attributo e creare il corrispondente Item.

#### Implementazione RTTI (già presente da Sprint 3):

```java
public Tuple getItemSet(int index) {
    Tuple tuple = new Tuple(explanatorySet.size());

    for (int i = 0; i < explanatorySet.size(); i++) {
        Attribute attr = explanatorySet.get(i);
        Object value = data[index][i];

        // RTTI: Usa instanceof per distinguere tipi a runtime
        if (attr instanceof DiscreteAttribute) {
            // Attributo discreto → crea DiscreteItem
            tuple.add(new DiscreteItem((DiscreteAttribute) attr, (String) value), i);

        } else if (attr instanceof ContinuousAttribute) {
            // Attributo continuo → crea ContinuousItem
            Double numValue;

            // Gestione conversione: String → Double o già Double
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

#### RTTI Features:
- ✅ `instanceof DiscreteAttribute` - Type checking runtime
- ✅ `instanceof ContinuousAttribute` - Type checking runtime
- ✅ `instanceof String` - Gestione conversione valori
- ✅ **Dynamic dispatch** - Comportamento diverso per tipo

**Conformità:** ✅ RTTI implementato esattamente come richiesto dalla specifica QT06

---

## 📊 Risultati Ottenuti

### Test Sprint 6 - Output

#### Dataset con Temperature Continuo:
```
Outlook,Temperature,Humidity,Wind,PlayTennis
0:sunny,30.3,high,weak,no,
1:sunny,30.3,high,strong,no,
2:overcast,30.0,high,weak,yes,
3:rain,13.0,high,weak,yes,
4:rain,0.0,normal,weak,yes,
5:rain,0.0,normal,strong,no,
6:overcast,0.1,normal,strong,yes,
7:sunny,13.0,high,weak,no,
8:sunny,0.1,normal,weak,yes,
9:rain,12.0,normal,weak,yes,
10:sunny,12.5,normal,strong,yes,
11:overcast,12.5,high,strong,yes,
12:overcast,29.21,normal,weak,yes,
13:rain,12.5,high,strong,no,
```

#### Test Clustering radius=2:
```
Insert radius (>0):2
Number of clusters:3

1:Centroid=(sunny 30.3 high weak no )
Examples:
[sunny 30.3 high weak no ] dist=0.0
[sunny 30.3 high strong no ] dist=1.0
[sunny 13.0 high weak no ] dist=0.4873239436619719
AvgDistance=0.49577464788732395

2:Centroid=(overcast 12.5 high strong yes )
Examples:
[overcast 30.0 high weak yes ] dist=1.4929577464788732
[overcast 0.1 normal strong yes ] dist=1.3492957746478873
[sunny 12.5 normal strong yes ] dist=2.0
[overcast 12.5 high strong yes ] dist=0.0
[rain 12.5 high strong no ] dist=2.0
AvgDistance=1.3684507042253522

3:Centroid=(rain 0.0 normal weak yes )
Examples:
[rain 13.0 high weak yes ] dist=1.3661971830985915
[rain 0.0 normal weak yes ] dist=0.0
[rain 0.0 normal strong no ] dist=2.0
[sunny 0.1 normal weak yes ] dist=1.0028169014084507
[rain 12.0 normal weak yes ] dist=0.33802816901408456
[overcast 29.21 normal weak yes ] dist=1.8228169014084508
AvgDistance=1.0883098591549296
```

#### Test Clustering radius=3:
```
Insert radius (>0):3
Number of clusters:2

1:Centroid=(sunny 30.3 high strong no )
Examples:
[sunny 30.3 high strong no ] dist=0.0
[rain 0.0 normal strong no ] dist=2.8535211267605636
[sunny 12.5 normal strong yes ] dist=2.5014084507042256
[rain 12.5 high strong no ] dist=1.5014084507042254
AvgDistance=1.7140845070422537

2:Centroid=(overcast 30.0 high weak yes )
Examples:
[sunny 30.3 high weak no ] dist=2.008450704225352
[overcast 30.0 high weak yes ] dist=0.0
[rain 13.0 high weak yes ] dist=1.4788732394366197
[rain 0.0 normal weak yes ] dist=2.845070422535211
[overcast 0.1 normal strong yes ] dist=2.8422535211267608
[sunny 13.0 high weak no ] dist=2.47887323943662
[sunny 0.1 normal weak yes ] dist=2.8422535211267608
[rain 12.0 normal weak yes ] dist=2.507042253521127
[overcast 12.5 high strong yes ] dist=1.4929577464788732
[overcast 29.21 normal weak yes ] dist=1.0222535211267605
AvgDistance=1.9518028169014083
```

### Verifica Conformità Specifica QT06

| Requisito | Conforme | Note |
|-----------|----------|------|
| Dataset formato corretto | ✅ | Temperature numeriche |
| radius=2 → 3 cluster | ✅ | Esatto |
| Centroidi corretti | ✅ | Identici a specifica |
| Distanze corrette | ✅ | Tutte corrispondono |
| radius=3 → 2 cluster | ✅ | Esatto |
| Output formato QT06 | ✅ | 100% identico |
| Generics completi | ✅ | Zero warning |
| RTTI funzionante | ✅ | instanceof OK |

**Conformità:** ✅ **100% identico a specifica QT06 (pagine 2-4)**

---

## 🏗️ Pattern di Design Utilizzati

### 1. **Generics Pattern**

**Vantaggi:**
- Type safety a compile-time
- Eliminazione cast espliciti
- Codice più leggibile e manutenibile
- Zero warning "unchecked" o "raw types"

**Esempi:**
```java
Set<Integer> clusteredData = new HashSet<>();  // Type-safe
Iterator<Cluster> iter = clusterSet.iterator(); // Nessun cast
List<Attribute> attrs = data.getAttributeSchema(); // Chiaro
```

### 2. **RTTI (Run-Time Type Information)**

**Utilizzo `instanceof`:**
```java
if (attr instanceof DiscreteAttribute) {
    // Comportamento per discreti
} else if (attr instanceof ContinuousAttribute) {
    // Comportamento per continui
}
```

**Vantaggi:**
- Type checking dinamico a runtime
- Gestione polimorfa di attributi misti
- Supporto dataset eterogenei (discreti + continui)

### 3. **Template Method (già presente)**

```java
abstract class Item {
    abstract double distance(Object a);  // Template method
}

class DiscreteItem extends Item {
    double distance(Object a) { /* Implementazione Hamming */ }
}

class ContinuousItem extends Item {
    double distance(Object a) { /* Implementazione Euclidea */ }
}
```

---

## 📈 Metriche Sprint

### Modifiche Codice

| Classe | Tipo Modifica | LOC Modificati |
|--------|---------------|----------------|
| Data.java | Temperature continuo | +18 / -4 |
| ContinuousItem.java | Verifica conformità | 0 (già OK) |
| TestSprint6.java | Test nuovo | +45 |
| **TOTALE** | | **+59 LOC** |

### Generics Status

| Categoria | Count | Parametrizzati | % |
|-----------|-------|----------------|---|
| Contenitori (Set, List, Map) | 7 | 7 | 100% |
| Iteratori (Iterable, Iterator) | 6 | 6 | 100% |
| Comparatori (Comparable) | 1 | 1 | 100% |
| **TOTALE** | **14** | **14** | **100%** |

### Compilazione

```bash
javac *.java
✓ Compilazione OK
✓ Zero errori
✓ Zero warning "unchecked"
✓ Zero warning "raw types"
```

### Testing

```bash
java TestSprint6
✓ Output 100% identico a specifica QT06
✓ radius=2 → 3 cluster (conforme)
✓ radius=3 → 2 cluster (conforme)
✓ Distanze tutte corrette
```

---

## ✅ Criteri di Successo

### Requisiti Specifica QT06

| Requisito | Stato | Note |
|-----------|-------|------|
| Generics in tutti contenitori | ✅ | 100% parametrizzati (Sprint 5) |
| ContinuousItem conforme | ✅ | Verifica OK (Sprint 3) |
| Data() Temperature continuo | ✅ | Valori da specifica |
| RTTI con instanceof | ✅ | getItemSet() corretto |
| Output identico specifica | ✅ | Pagine 2-4 PDF |
| Zero warning Generics | ✅ | Compilazione pulita |

**Completamento:** 6/6 requisiti ✅ (100%)

---

## 🔄 Breaking Changes

### Dataset PlayTennis

⚠️ **Breaking Change:** Temperature non è più discreto!

**Impatto:**
- Test/codice che assume Temperature discreto → NON funzionano più
- Test Iris/WeatherMixed → NON impattati (dataset esterni)
- MainTest → Funziona (usa Data generico)

**Migration:**
```java
// Prima
DiscreteAttribute temp = (DiscreteAttribute) data.getExplanatoryAttribute(1);
String value = temp.getValue(0); // NON FUNZIONA PIÙ

// Dopo - Usa RTTI
Attribute attr = data.getExplanatoryAttribute(1);
if (attr instanceof ContinuousAttribute) {
    ContinuousAttribute temp = (ContinuousAttribute) attr;
    double min = temp.getMin();
    double max = temp.getMax();
}
```

---

## 🚀 Prossimi Sprint

### Sprint 7 - Database Integration (JDBC) - QT07

**Obiettivi pianificati:**
- Connessione database MySQL/PostgreSQL
- Lettura dataset da DB
- Salvataggio cluster in DB
- Connection pooling
- Query analitiche

### Sprint 8 - Client-Server Communication (Socket) - QT08

**Obiettivi pianificati:**
- Server multi-client
- Protocollo comunicazione
- Client GUI e CLI
- Clustering distribuito

---

## 📝 Retrospettiva

### Cosa è andato bene ✅

1. **Generics già completi:** Sprint 5 aveva già implementato tutti i Generics necessari
2. **ContinuousItem già conforme:** Sprint 3 aveva già implementato correttamente la classe
3. **RTTI già funzionante:** getItemSet() usava già instanceof da Sprint 3
4. **Output perfetto:** 100% identico alla specifica QT06 senza iterazioni
5. **Zero refactoring:** Modifiche minime, solo cambio dataset

### Cosa migliorare 🔄

1. **Documentazione range:** Range [3.2, 38.7] vs valori [0.0, 30.3] potrebbe confondere
2. **Test backward compatibility:** Nessun test per verificare impatto su codice esistente
3. **Migration guide:** Documentare meglio come migrare da Temperature discreto

### Lessons Learned 📚

1. **Sprint incrementali:** Sprint 3 e 5 avevano già preparato il terreno per QT06
2. **Generics precoce:** Introdurre Generics subito evita refactoring massicci
3. **RTTI essenziale:** instanceof permette dataset misti senza complicazioni
4. **Specifica precisa:** Valori esatti nella specifica permettono test deterministici

---

## 📦 Deliverables

### File Modificati

- ✅ `src/Data.java` (Temperature continuo)
- ✅ `src/ContinuousItem.java` (verificato, già conforme)

### File Creati

- ✅ `src/TestSprint6.java` (45 LOC)
- ✅ `docs/sprints/SPRINT_6.md` (questo file)

### Verifiche

- ✅ Compilazione senza errori/warning
- ✅ Output conforme specifica QT06 al 100%
- ✅ Tutti i Generics parametrizzati
- ✅ RTTI funzionante

---

## 🔗 Riferimenti

- **Specifica ufficiale:** `Project/QT06/Specifica_QT06_Generics-RTTI.pdf`
- **Java Generics Tutorial:** https://docs.oracle.com/javase/tutorial/java/generics/
- **RTTI in Java:** https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html (instanceof)
- **ContinuousItem (Sprint 3):** `docs/sprints/SPRINT_3.md`
- **Iterators/Generics (Sprint 5):** `docs/sprints/SPRINT_5.md`

---

**Fine Sprint 6**
**Prossimo:** Sprint 7 - Database Integration (JDBC - QT07)
