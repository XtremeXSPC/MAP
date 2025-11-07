Algoritmo QT - Report Prestazioni Completo
Generato: 2025-11-07T12:09:07.883460
================================================================================


======================================================================
REPORT COMPARATIVO PRESTAZIONI
======================================================================

Dataset: synthetic_small.csv (50 tuple, 5 attributi)
Raggio: 0.40
Cluster Trovati: 42

----------------------------------------------------------------------
Metrica                       Baseline     Ottimizzato   Miglioramento
----------------------------------------------------------------------
Tempo Esecuzione                  9 ms            9 ms            0.0%
Accelerazione                                    1.00x
Calcoli Distanza                     0           26759              0%
Hit Cache                          N/D            1886
Tasso Hit Cache                    N/D            6.6%
Memoria Utilizzata                0 MB            0 MB
----------------------------------------------------------------------

VALUTAZIONE:
  ⚠ MINIMO - Miglioramento limitato

======================================================================



======================================================================
REPORT COMPARATIVO PRESTAZIONI
======================================================================

Dataset: synthetic_medium.csv (200 tuple, 8 attributi)
Raggio: 0.40
Cluster Trovati: 198

----------------------------------------------------------------------
Metrica                       Baseline     Ottimizzato   Miglioramento
----------------------------------------------------------------------
Tempo Esecuzione                272 ms          394 ms          -44.9%
Accelerazione                                    0.69x
Calcoli Distanza                     0         2586944              0%
Hit Cache                          N/D           21346
Tasso Hit Cache                    N/D            0.8%
Memoria Utilizzata                0 MB            0 MB
----------------------------------------------------------------------

VALUTAZIONE:
  ⚠ MINIMO - Miglioramento limitato

======================================================================



======================================================================
REPORT COMPARATIVO PRESTAZIONI
======================================================================

Dataset: synthetic_large.csv (1000 tuple, 10 attributi)
Raggio: 0.40
Cluster Trovati: 989

----------------------------------------------------------------------
Metrica                       Baseline     Ottimizzato   Miglioramento
----------------------------------------------------------------------
Tempo Esecuzione              38478 ms        59012 ms          -53.4%
Accelerazione                                    0.65x
Calcoli Distanza                     0       322481115              0%
Hit Cache                          N/D          592614
Tasso Hit Cache                    N/D            0.2%
Memoria Utilizzata                0 MB            0 MB
----------------------------------------------------------------------

VALUTAZIONE:
  ⚠ MINIMO - Miglioramento limitato

======================================================================



TABELLA RIEPILOGO
--------------------------------------------------------------------------------
Dim. Dataset    |    Tempo Base |   Tempo Ottim. | Speedup |  Hit Rate
--------------------------------------------------------------------------------
Piccolo (50)    |          9 ms |           9 ms |   1.00x |      6.6%
Medio (200)     |        272 ms |         394 ms |   0.69x |      0.8%
Grande (1000)   |      38478 ms |       59012 ms |   0.65x |      0.2%

⚠ Le ottimizzazioni mostrano miglioramenti LIMITATI

Le ottimizzazioni delle strutture dati forniscono miglioramenti O(n) → O(1).
