/**
 * Classe che modella un attributo discreto (categorico).
 */
public class DiscreteAttribute extends Attribute {
    private String[] values;

    /**
     * Costruttore della classe DiscreteAttribute.
     *
     * @param name nome dell'attributo
     * @param index identificativo numerico dell'attributo
     * @param values array di stringhe rappresentanti il dominio dell'attributo
     */
    public DiscreteAttribute(String name, int index, String[] values) {
        super(name, index);
        this.values = values;
    }

    /**
     * Restituisce il numero di valori distinti nel dominio dell'attributo.
     *
     * @return numero di valori discreti
     */
    public int getNumberOfDistinctValues() {
        return values.length;
    }

    /**
     * Restituisce il valore discreto in posizione i.
     *
     * @param i posizione del valore
     * @return valore discreto
     */
    public String getValue(int i) {
        return values[i];
    }
}
