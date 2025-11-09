package gui.utils;

import java.awt.Color;

/**
 * Utility per generare palette di colori distinguibili per la visualizzazione dei cluster.
 * Fornisce colori predefiniti e generazione dinamica per numero arbitrario di cluster.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class ColorPalette {

    /**
     * Palette predefinita di 12 colori distinguibili.
     * Basata sulla palette ColorBrewer "Paired" per massima distinguibilità.
     */
    private static final Color[] DEFAULT_PALETTE = { new Color(31, 120, 180), // Blu
            new Color(255, 127, 0), // Arancione
            new Color(51, 160, 44), // Verde
            new Color(227, 26, 28), // Rosso
            new Color(166, 206, 227), // Blu chiaro
            new Color(253, 191, 111), // Arancione chiaro
            new Color(178, 223, 138), // Verde chiaro
            new Color(251, 154, 153), // Rosa
            new Color(106, 61, 154), // Viola
            new Color(202, 178, 214), // Viola chiaro
            new Color(255, 255, 153), // Giallo
            new Color(177, 89, 40) // Marrone
    };

    /**
     * Ottiene un colore dalla palette per l'indice specificato.
     * Se l'indice supera la palette predefinita, genera colori dinamicamente.
     *
     * @param clusterIndex indice del cluster (0-based)
     * @return colore associato al cluster
     */
    public static Color getColor(int clusterIndex) {
        if (clusterIndex < 0) {
            throw new IllegalArgumentException("Cluster index deve essere non negativo");
        }

        // Usa palette predefinita se possibile
        if (clusterIndex < DEFAULT_PALETTE.length) {
            return DEFAULT_PALETTE[clusterIndex];
        }

        // Altrimenti genera colore dinamicamente usando HSB
        return generateColorHSB(clusterIndex);
    }

    /**
     * Genera un colore usando lo spazio HSB (Hue, Saturation, Brightness).
     * Distribuisce uniformemente i colori lungo il cerchio cromatico.
     *
     * @param index indice del colore da generare
     * @return colore generato
     */
    private static Color generateColorHSB(int index) {
        // Golden ratio conjugate per distribuzione uniforme
        float goldenRatio = 0.618033988749895f;
        float hue = (index * goldenRatio) % 1.0f;
        float saturation = 0.7f; // Saturazione fissa al 70%
        float brightness = 0.9f; // Luminosità fissa al 90%

        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * Genera un array di colori per il numero specificato di cluster.
     *
     * @param numClusters numero di cluster
     * @return array di colori
     */
    public static Color[] getColors(int numClusters) {
        if (numClusters <= 0) {
            throw new IllegalArgumentException("Numero cluster deve essere positivo");
        }

        Color[] colors = new Color[numClusters];
        for (int i = 0; i < numClusters; i++) {
            colors[i] = getColor(i);
        }
        return colors;
    }

    /**
     * Converte un colore AWT in una stringa esadecimale (es. "#FF5733").
     *
     * @param color colore da convertire
     * @return stringa esadecimale
     */
    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Converte un colore AWT in un colore JavaFX.
     *
     * @param awtColor colore AWT
     * @return colore JavaFX
     */
    public static javafx.scene.paint.Color toJavaFX(Color awtColor) {
        return javafx.scene.paint.Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(),
                awtColor.getAlpha() / 255.0);
    }

    /**
     * Restituisce il numero di colori nella palette predefinita.
     *
     * @return numero di colori predefiniti
     */
    public static int getDefaultPaletteSize() {
        return DEFAULT_PALETTE.length;
    }

    /**
     * Ottiene un colore con trasparenza.
     *
     * @param clusterIndex indice del cluster
     * @param alpha valore alpha (0-255)
     * @return colore con trasparenza
     */
    public static Color getColorWithAlpha(int clusterIndex, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha deve essere tra 0 e 255");
        }

        Color baseColor = getColor(clusterIndex);
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
    }
}
