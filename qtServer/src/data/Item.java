package data;

import java.io.Serializable;

/**
 * Classe astratta che modella un item (coppia attributo-valore).
 * Implementa Serializable per permettere la serializzazione binaria.
 */
public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private Attribute attribute;
    private Object value;

    /**
     * Costruttore della classe Item.
     *
     * @param attribute attributo coinvolto nell'item
     * @param value valore assegnato all'attributo
     */
    public Item(Attribute attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * Restituisce l'attributo.
     *
     * @return attributo
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Restituisce il valore.
     *
     * @return valore
     */
    public Object getValue() {
        return value;
    }

    /**
     * Restituisce una stringa rappresentante lo stato dell'oggetto.
     *
     * @return valore dell'item
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Calcola la distanza tra il valore dell'item corrente e un altro oggetto.
     *
     * @param a oggetto con cui calcolare la distanza
     * @return distanza
     */
    public abstract double distance(Object a);
}
