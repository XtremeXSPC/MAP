package data;

/**
 * Classe che modella un item continuo (coppia attributo continuo-valore numerico).
 *
 * <p>
 *   Questa classe estende {@link Item} per gestire attributi con valori numerici continui.
 *   La distanza tra due valori continui è calcolata usando la distanza Euclidea normalizzata,
 *   che produce un valore nell'intervallo [0, 1] compatibile con la distanza
 *   di Hamming utilizzata per gli attributi discreti.
 * </p>
 *
 * <p><b>Formula Distanza:</b></p>
 *
 * <pre>
 *   distance = |scaledValue1 - scaledValue2|
 *
 *   dove:
 *     scaledValue = (value - min) / (max - min)
 *     min, max = range dell'attributo
 * </pre>
 *
 * @see Item
 * @see ContinuousAttribute
 * @see DiscreteItem
 */
public class ContinuousItem extends Item {

    /**
     * Costruttore della classe ContinuousItem.
     *
     * <p>
     *   Crea un item continuo con l'attributo e il valore specificati. Il valore deve essere un
     *   numero nell'intervallo [min, max] dell'attributo, altrimenti la normalizzazione
     *   potrebbe produrre risultati fuori range [0, 1].
     * </p>
     *
     * @param attribute attributo continuo associato all'item
     * @param value valore numerico dell'item (Double)
     * @throws NullPointerException se attribute o value sono null
     */
    public ContinuousItem(ContinuousAttribute attribute, Double value) {
        super(attribute, value);
    }

    /**
     * Calcola la distanza Euclidea normalizzata tra il valore dell'item corrente e un altro
     * valore numerico.
     *
     * <p>
     *   La distanza è calcolata come il valore assoluto della differenza tra i due valori
     *   normalizzati nell'intervallo [0, 1] usando min-max scaling. Questo garantisce che la
     *   distanza sia compatibile con la distanza di Hamming (0 o 1) usata per gli attributi
     *   discreti.
     * </p>
     *
     * <p><b>Algoritmo:</b></p>
     * <ol>
     *   <li>Normalizza il valore corrente: v1_scaled = (v1 - min) / (max - min)</li>
     *   <li>Normalizza il valore da confrontare: v2_scaled = (v2 - min) / (max - min)</li>
     *   <li>Calcola distanza: |v1_scaled - v2_scaled|</li>
     * </ol>
     *
     * <p><b>Proprietà:</b></p>
     * <ul>
     *   <li>Range output: [0, 1] (normalizzato)</li>
     *   <li>Simmetrica: distance(a, b) = distance(b, a)</li>
     *   <li>Identità: distance(a, a) = 0</li>
     *   <li>Non-negatività: distance(a, b) ≥ 0</li>
     * </ul>
     *
     * @param a valore numerico con cui calcolare la distanza (deve essere Double)
     * @return distanza Euclidea normalizzata nell'intervallo [0, 1]
     * @throws ClassCastException se 'a' non è un Double
     * @throws NullPointerException se 'a' è null
     */
    @Override
    public double distance(Object a) {
        // Cast dell'attributo a ContinuousAttribute per accedere a getScaledValue.
        ContinuousAttribute attr = (ContinuousAttribute) getAttribute();

        // Ottieni il valore corrente dell'item come Double.
        Double currentValue = (Double) getValue();

        // Cast del parametro a Double.
        Double otherValue = (Double) a;

        // Normalizza entrambi i valori nell'intervallo [0, 1].
        // usando la formula: (value - min) / (max - min).
        double scaledCurrent = attr.getScaledValue(currentValue);
        double scaledOther = attr.getScaledValue(otherValue);

        // Calcola la distanza Euclidea normalizzata (1D).
        // Per una dimensione, la distanza Euclidea è semplicemente il valore
        // assoluto della differenza.
        return Math.abs(scaledCurrent - scaledOther);
    }
}
