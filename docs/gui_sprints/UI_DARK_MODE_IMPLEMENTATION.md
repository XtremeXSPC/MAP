# Implementazione Dark Mode - QT Clustering GUI

## Panoramica

Questa documentazione descrive l'implementazione completa del dark mode per l'applicazione QT Clustering GUI, garantendo un'esperienza visiva uniforme e professionale simile alle moderne applicazioni desktop.

## Problematiche Risolte

### 1. Applicazione Parziale del Tema Scuro

**Problema originale:**

- Il tema scuro veniva applicato solo a MenuBar, ToolBar e ScrollBar
- I contenitori principali (VBox, HBox, BorderPane) rimanevano con sfondo bianco/grigio chiaro
- Le card e i form non avevano stili dark appropriati

**Soluzione implementata:**

- Aggiunto stile `-fx-background-color: #1e1e1e` al root
- Creati selettori CSS per tutti i contenitori layout
- Aggiornati stili per card, form-section, e altri componenti

## Modifiche al File dark-theme.css

### Sezione Root - Background Globale

```css
.root {
  -fx-font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  -fx-font-size: 14px;
  -fx-base: #2b2b2b;
  -fx-background: #1e1e1e;
  -fx-control-inner-background: #3c3f41;
  -fx-accent: #4a9eff;
  -fx-background-color: #1e1e1e;  /* AGGIUNTO */
}
```

**Nota:** L'aggiunta di `-fx-background-color` garantisce che lo sfondo sia scuro anche prima che gli altri stili vengano applicati.

### Layout Containers - Background Scuro

```css
/* Selettori con classi CSS */
.vbox, .hbox, .border-pane, .stack-pane, .grid-pane, .flow-pane, .anchor-pane {
  -fx-background-color: #1e1e1e;
}

/* Selettori per tipi JavaFX */
VBox, HBox, BorderPane, StackPane, GridPane, FlowPane, AnchorPane {
  -fx-background-color: #1e1e1e;
}
```

**Motivazione:** JavaFX applica stili sia tramite classi CSS che tipi di nodi. Per garantire copertura completa, definiamo stili per entrambi.

### ScrollPane - Viewport Scuro

```css
.scroll-pane {
  -fx-background-color: #1e1e1e;
}

.scroll-pane > .viewport {
  -fx-background-color: #1e1e1e;
}

.scroll-pane .content {
  -fx-background-color: #1e1e1e;
}
```

**Motivazione:** Lo ScrollPane ha una struttura interna complessa. Dobbiamo stilizzare il contenitore principale, il viewport, e il content per evitare "strisce bianche" durante lo scrolling.

### Card e Form Section

```css
.card {
  -fx-background-color: #2b2b2b;
  -fx-border-color: #4a4a4a;      /* Aggiornato da #3c3c3c */
  -fx-border-width: 1;             /* AGGIUNTO */
  -fx-border-radius: 8;
  -fx-background-radius: 8;
  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 2);
  -fx-padding: 20;
}

.form-section {
  -fx-background-color: #1e1e1e;   /* AGGIUNTO */
  -fx-padding: 20;
  -fx-spacing: 15;
}
```

**Motivazione:**

- Le card devono avere un colore leggermente diverso dallo sfondo (#2b2b2b vs #1e1e1e) per creare profondità
- Il bordo più chiaro (#4a4a4a) migliora la separazione visiva
- form-section necessita dello sfondo scuro per uniformità

### Separatori

```css
.separator {
  -fx-background-color: transparent;   /* AGGIUNTO */
}

.separator .line {
  -fx-border-color: #4a4a4a;          /* Aggiornato da #3c3c3c */
  -fx-border-width: 1 0 0 0;          /* AGGIUNTO */
}
```

**Motivazione:** I separatori devono essere visibili ma discreti. Il colore #4a4a4a è abbastanza chiaro da essere visibile su #1e1e1e ma non invadente.

### Menu e MenuBar - Copertura Completa

```css
.menu-bar .menu {
  -fx-background-color: #2b2b2b;     /* AGGIUNTO */
}

.menu-bar .menu .label {
  -fx-text-fill: #e0e0e0;            /* AGGIUNTO */
}

.menu-bar .menu:hover {
  -fx-background-color: #3c3f41;     /* AGGIUNTO */
}

.menu-bar .menu:showing {
  -fx-background-color: #3c3f41;     /* AGGIUNTO */
}

.separator-menu-item .line {
  -fx-border-color: #555555;         /* AGGIUNTO */
  -fx-border-width: 1 0 0 0;         /* AGGIUNTO */
}
```

**Motivazione:** Gli stili originali non coprivano i singoli menu, solo la MenuBar. Questo causava menu bianchi quando aperti.

### ListView (Nuovo)

```css
.list-view {
  -fx-background-color: #3c3f41;
  -fx-border-color: #555555;
}

.list-cell {
  -fx-background-color: #3c3f41;
  -fx-text-fill: #e0e0e0;
}

.list-cell:filled:selected {
  -fx-background-color: #4a6a8a;
  -fx-text-fill: white;
}

.list-cell:filled:hover {
  -fx-background-color: #4a4a4a;
}
```

**Motivazione:** ListView è utilizzato in molti dialoghi. Senza questi stili, apparirebbe con sfondo bianco.

### Region e Pane

```css
.region {
  -fx-background-color: transparent;
}

.pane {
  -fx-background-color: #1e1e1e;
}
```

**Motivazione:**

- Region viene utilizzato come spacer nei layout HBox/VBox e deve essere trasparente
- Pane generico deve avere sfondo scuro

### Hyperlink (Nuovo)

```css
.hyperlink {
  -fx-text-fill: #4a9eff;
  -fx-underline: true;
}

.hyperlink:visited {
  -fx-text-fill: #9b6bff;
}

.hyperlink:hover {
  -fx-text-fill: #6bb5ff;
}
```

**Motivazione:** I link devono essere distinguibili con colori appropriati per il dark mode.

## Palette Colori Dark Mode

| Elemento  | Colore              | Uso                           |
| --------- | ------------------- | ----------------------------- |
| `#1e1e1e` | Nero grigio scuro   | Sfondo principale             |
| `#2b2b2b` | Grigio molto scuro  | Card, contenitori elevati     |
| `#3c3f41` | Grigio scuro        | Input controls, tab           |
| `#4a4a4a` | Grigio medio scuro  | Hover states, bordi secondari |
| `#555555` | Grigio medio        | Bordi primari                 |
| `#808080` | Grigio chiaro       | Icone, elementi disabili      |
| `#b0b0b0` | Grigio molto chiaro | Label secondarie              |
| `#e0e0e0` | Quasi bianco        | Testo primario                |
| `#4a9eff` | Blu chiaro          | Accent, link, focus           |
| `#4a6a8a` | Blu scuro           | Selezioni                     |
| `#27ae60` | Verde               | Successo, pulsanti primari    |
| `#e74c3c` | Rosso               | Errori, pulsanti danger       |
| `#f39c12` | Arancione           | Warning                       |

## Verifica dell'Implementazione

### Checklist Visiva

- [ ] MenuBar completamente scura (non grigio chiaro)
- [ ] ToolBar completamente scura
- [ ] Contenuto principale (VBox/HBox) con sfondo #1e1e1e
- [ ] Card visibilmente distinte con #2b2b2b
- [ ] Separatori visibili ma discreti
- [ ] ScrollBar scura con thumb grigio
- [ ] TextField/TextArea con sfondo #3c3f41
- [ ] ComboBox con dropdown scuro
- [ ] CheckBox/RadioButton con mark visibile
- [ ] Button con hover effect
- [ ] Table/TreeView con righe scure alternate
- [ ] Dialog con sfondo scuro completo

### Test Funzionali

1. **Test Menu:**
   - Aprire ogni menu e verificare sfondo scuro
   - Hover su MenuItem deve mostrare #4a4a4a
   - SeparatorMenuItem deve avere linea grigia

2. **Test Form:**
   - Aprire "Nuova Analisi di Clustering"
   - Verificare che tutte le card abbiano sfondo #2b2b2b
   - Verificare che form-section abbia sfondo #1e1e1e

3. **Test Scroll:**
   - Aprire Impostazioni (ha ScrollPane)
   - Scrollare e verificare nessuna "striscia bianca"
   - Verificare ScrollBar scura

4. **Test Input:**
   - Inserire testo in TextField
   - Verificare sfondo #3c3f41 e testo #e0e0e0
   - Testare focus (bordo blu)

5. **Test Toggle:**
   - Cambiare tema da Light a Dark nelle Impostazioni
   - Verificare transizione immediata
   - Cambiare di nuovo a Light e verificare ripristino

## Best Practices per Futuri Sviluppi

### 1. Utilizzare Classi CSS Personalizzate

Quando aggiungi nuovi componenti, assegna classi CSS:

```java
vbox.getStyleClass().add("custom-container");
```

```css
/* dark-theme.css */
.custom-container {
  -fx-background-color: #1e1e1e;
}
```

### 2. Testare con Entrambi i Temi

Prima di committare codice UI, verifica:

```bash
# Apri app con Light theme
# Apri Impostazioni -> Cambia a Dark
# Naviga tutte le schermate
# Verifica uniformità colori
```

### 3. Evitare Stili Inline

❌ **Evitare:**

```java
vbox.setStyle("-fx-background-color: white;");
```

✅ **Preferire:**

```java
vbox.getStyleClass().add("form-section");
```

### 4. Utilizzare Variabili CSS (Future)

Per facilitare manutenzione futura, considerare uso di variabili CSS JavaFX:

```css
.root {
  -dark-bg-primary: #1e1e1e;
  -dark-bg-secondary: #2b2b2b;
  -dark-text-primary: #e0e0e0;
}

.card {
  -fx-background-color: -dark-bg-secondary;
}
```

## Risoluzione Problemi

### Problema: Componente rimane bianco in dark mode

**Diagnosi:**

1. Verificare che il componente non abbia stili inline
2. Controllare se il componente ha una classe CSS custom
3. Verificare gerarchia dei selettori CSS

**Soluzione:**

```css
/* Aggiungere selettore specifico in dark-theme.css */
.nome-classe-componente {
  -fx-background-color: #1e1e1e;
  -fx-text-fill: #e0e0e0;
}
```

### Problema: Testo invisibile su sfondo scuro

**Diagnosi:**
Il componente ha testo nero di default.

**Soluzione:**

```css
.nome-componente .label {
  -fx-text-fill: #e0e0e0;
}
```

### Problema: Bordi invisibili

**Diagnosi:**
Bordo troppo scuro su sfondo scuro.

**Soluzione:**

```css
.nome-componente {
  -fx-border-color: #4a4a4a;
  -fx-border-width: 1;
}
```

## Risorse Esterne

- [JavaFX CSS Reference](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [Material Design Dark Theme](https://material.io/design/color/dark-theme.html)
- [Dark Mode UI Best Practices](https://www.smashingmagazine.com/2020/04/dark-mode-user-interfaces-design/)

## Changelog

### 2025-11-09 - v2.0 (Dark Mode Completo)

- Aggiunto background scuro per tutti i contenitori layout
- Aggiornati stili card e form-section
- Aggiunti stili per ListView, Hyperlink, Pane
- Migliorati separatori e menu
- Corretti colori bordi per maggiore visibilità

### 2025-11-09 - v1.0 (Iniziale)

- Implementazione base dark theme per MenuBar, ToolBar, controlli

---

**Autore:** Claude AI Assistant
**Data:** 9 Novembre 2025
**Versione:** 2.0
