package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe che modella un attributo discreto (categorico). Implementa Iterable per iterazione sui
 * valori distinti (ordinati alfabeticamente).
 */
public class DiscreteAttribute extends Attribute implements Iterable<String> {
    private Set<String> values;

    /**
     * Costruttore della classe DiscreteAttribute.
     *
     * @param name nome dell'attributo
     * @param index identificativo numerico dell'attributo
     * @param values array di stringhe rappresentanti il dominio dell'attributo
     */
    public DiscreteAttribute(String name, int index, String[] values) {
        super(name, index);
        this.values = new TreeSet<>();
        for (String value : values) {
            this.values.add(value);
        }
    }

    /**
     * Restituisce il numero di valori distinti nel dominio dell'attributo.
     *
     * @return numero di valori discreti
     */
    public int getNumberOfDistinctValues() {
        return values.size();
    }

    /**
     * Restituisce un iteratore sui valori distinti dell'attributo. I valori sono ordinati
     * alfabeticamente (TreeSet).
     *
     * @return iteratore sui valori
     */
    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }
}
