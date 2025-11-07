QT Algorithm - Comprehensive Performance Report
Generated: 2025-11-07T10:13:53.035228588
================================================================================


======================================================================
PERFORMANCE COMPARISON REPORT
======================================================================

Dataset: synthetic_small.csv (50 tuples, 5 attributes)
Radius: 0.40
Clusters Found: 42

----------------------------------------------------------------------
Metric                                Baseline       Optimized  Improvement
----------------------------------------------------------------------
Execution Time                           19 ms           18 ms        5.3%
Speedup                                   1.06x
Distance Calculations                        0           26759          0%
Cache Hits                                 N/A            1886
Cache Hit Rate                             N/A          6.6%
Memory Used                               0 MB            0 MB
----------------------------------------------------------------------

EVALUATION:
  ⚠ MINIMAL - Limited improvement

======================================================================



======================================================================
PERFORMANCE COMPARISON REPORT
======================================================================

Dataset: synthetic_medium.csv (200 tuples, 8 attributes)
Radius: 0.40
Clusters Found: 198

----------------------------------------------------------------------
Metric                                Baseline       Optimized  Improvement
----------------------------------------------------------------------
Execution Time                          498 ms          664 ms      -33.3%
Speedup                                   0.75x
Distance Calculations                        0         2586944          0%
Cache Hits                                 N/A           21346
Cache Hit Rate                             N/A          0.8%
Memory Used                               0 MB            0 MB
----------------------------------------------------------------------

EVALUATION:
  ⚠ MINIMAL - Limited improvement

======================================================================



======================================================================
PERFORMANCE COMPARISON REPORT
======================================================================

Dataset: synthetic_large.csv (1000 tuples, 10 attributes)
Radius: 0.40
Clusters Found: 989

----------------------------------------------------------------------
Metric                                Baseline       Optimized  Improvement
----------------------------------------------------------------------
Execution Time                        52257 ms        76600 ms      -46.6%
Speedup                                   0.68x
Distance Calculations                        0       322481115          0%
Cache Hits                                 N/A          592614
Cache Hit Rate                             N/A          0.2%
Memory Used                               0 MB            0 MB
----------------------------------------------------------------------

EVALUATION:
  ⚠ MINIMAL - Limited improvement

======================================================================



SUMMARY TABLE
--------------------------------------------------------------------------------
Dataset Size    | Baseline Time | Optimized Time | Speedup |  Hit Rate
--------------------------------------------------------------------------------
Small (50)      |         19 ms |          18 ms |   1.06x |      6.6%
Medium (200)    |        498 ms |         664 ms |   0.75x |      0.8%
Large (1000)    |      52257 ms |       76600 ms |   0.68x |      0.2%

⚠ Optimizations show LIMITED improvement

Data structures optimizations provide O(n)→O(1) improvements.
