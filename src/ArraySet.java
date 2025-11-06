import java.util.Arrays;

/**
 * Classe che modella un insieme di interi tramite un vettore di booleani.
 */
class ArraySet {
    private boolean set[];
    private int size = 0;
    private int cardinality = 0;

    /**
     * Costruttore della classe ArraySet.
     */
    ArraySet() {
        set = new boolean[50];
        for (int i = 0; i < set.length; i++)
            set[i] = false;
    }

    /**
     * Aggiunge un elemento all'insieme.
     *
     * @param i elemento da aggiungere
     * @return true se l'aggiunta modifica l'insieme
     */
    boolean add(int i) {
        if (i >= set.length) {
            // Allarga il set
            boolean temp[] = new boolean[set.length * 2];
            Arrays.fill(temp, false);
            System.arraycopy(set, 0, temp, 0, set.length);
            set = temp;
        }
        boolean added = set[i];
        set[i] = true;
        if (i >= size)
            size = i + 1;
        if (!added)
            cardinality++;
        return !added;
    }

    /**
     * Rimuove un elemento dall'insieme.
     *
     * @param i elemento da rimuovere
     * @return true se la rimozione modifica l'insieme
     */
    boolean delete(int i) {
        if (i < size) {
            boolean deleted = set[i];
            set[i] = false;
            if (i == size - 1) {
                // Aggiorna size
                int j;
                for (j = size - 1; j >= 0 && !set[j]; j--);
                size = j + 1;
            }
            if (deleted)
                cardinality--;
            return deleted;
        }
        return false;
    }

    /**
     * Verifica se un elemento è presente nell'insieme.
     *
     * @param i elemento da verificare
     * @return true se l'elemento è presente
     */
    boolean get(int i) {
        return set[i];
    }

    /**
     * Restituisce la cardinalità dell'insieme.
     *
     * @return numero di elementi nell'insieme
     */
    int size() {
        return cardinality;
    }

    /**
     * Converte l'insieme in un array di interi.
     *
     * @return array contenente gli elementi dell'insieme
     */
    int[] toArray() {
        int a[] = new int[0];
        for (int i = 0; i < size; i++) {
            if (get(i)) {
                int temp[] = new int[a.length + 1];
                System.arraycopy(a, 0, temp, 0, a.length);
                a = temp;
                a[a.length - 1] = i;
            }
        }
        return a;
    }
}
