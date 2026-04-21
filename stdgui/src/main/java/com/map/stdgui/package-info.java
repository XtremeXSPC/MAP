/**
 * The {@code com.map.stdgui} package provides small, reusable GUI utilities for
 * QT Clustering clients.
 * <p>
 * Its classes follow the spirit of Sedgewick and Wayne's standard libraries:
 * callers use compact static helpers where no persistent state is needed
 * ({@link com.map.stdgui.StdDialog}, {@link com.map.stdgui.StdFileDialog},
 * {@link com.map.stdgui.StdClipboard}) and simple instance APIs where a GUI
 * object has state ({@link com.map.stdgui.StdWindow},
 * {@link com.map.stdgui.StdTheme}).
 * <p>
 * JavaFX nodes, stages, scenes, dialogs, tasks, and thread dispatching are kept
 * behind this package boundary so application code can express what it wants to
 * do, not how JavaFX performs it.
 */
package com.map.stdgui;
