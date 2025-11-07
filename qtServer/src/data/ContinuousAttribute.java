package data;

/**
 * Classe che modella un attributo continuo (numerico).
 */
public class ContinuousAttribute extends Attribute {
    private double max;
    private double min;

    /**
     * Costruttore della classe ContinuousAttribute.
     *
     * @param name nome dell'attributo
     * @param index identificativo numerico dell'attributo
     * @param min valore minimo dell'attributo
     * @param max valore massimo dell'attributo
     */
    public ContinuousAttribute(String name, int index, double min, double max) {
        super(name, index);
        this.min = min;
        this.max = max;
    }

    /**
     * Calcola e restituisce il valore scalato nell'intervallo [0,1].
     *
     * @param v valore dell'attributo da scalare
     * @return valore scalato
     */
    public double getScaledValue(double v) {
        if (max == min) {
            return 0.0;
        }
        return (v - min) / (max - min);
    }
}
