package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modella una transazione (riga) letta dal database. Ogni Example contiene una lista di
 * valori eterogenei (String, Double, ecc.).
 *
 * @author Appice A.
 * @version 1.0
 */
public class Example implements Comparable<Example>, Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Lista di valori che compongono la transazione
     */
    private List<Object> example = new ArrayList<Object>();

    /**
     * Aggiunge un valore alla transazione.
     *
     * @param o oggetto da aggiungere
     */
    public void add(Object o) {
        example.add(o);
    }

    /**
     * Restituisce il valore alla posizione i.
     *
     * @param i indice del valore
     * @return valore alla posizione i
     */
    public Object get(int i) {
        return example.get(i);
    }

    /**
     * Confronta questa transazione con un'altra transazione.
     *
     * @param ex transazione da confrontare
     * @return valore negativo, zero o positivo se questa transazione è minore, uguale o
     *         maggiore di ex
     */
    @SuppressWarnings("unchecked")
    public int compareTo(Example ex) {
        int i = 0;
        for (Object o : ex.example) {
            if (!o.equals(this.example.get(i)))
                return ((Comparable<Object>) o).compareTo(example.get(i));
            i++;
        }
        return 0;
    }

    /**
     * Restituisce una rappresentazione testuale della transazione.
     *
     * @return stringa con tutti i valori separati da spazio
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Object o : example)
            str.append(o.toString()).append(" ");
        return str.toString();
    }
}
