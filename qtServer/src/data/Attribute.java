package data;

import java.io.Serializable;

/**
 * Classe astratta che modella un attributo generico. Implementa Serializable per
 * permettere la serializzazione binaria.
 */
public abstract class Attribute implements Serializable {
    private String name;
    private int index;

    /**
     * Costruttore della classe Attribute.
     *
     * @param name nome simbolico dell'attributo
     * @param index identificativo numerico dell'attributo
     */
    public Attribute(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Restituisce il nome dell'attributo.
     *
     * @return nome dell'attributo
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce l'identificativo numerico dell'attributo.
     *
     * @return identificativo numerico dell'attributo
     */
    public int getIndex() {
        return index;
    }

    /**
     * Restituisce una stringa rappresentante lo stato dell'oggetto.
     *
     * @return nome dell'attributo
     */
    @Override
    public String toString() {
        return name;
    }
}
