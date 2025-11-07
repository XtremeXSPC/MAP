import java.util.HashMap;

/**
 * Cache per memorizzare distanze già calcolate tra tuple.
 * Utilizza una HashMap sparsa per efficienza memoria con chiave Long costruita da (i,j).
 *
 * Performance:
 * - Get/Put: O(1) average
 * - Memoria: Solo distanze calcolate (sparse), non matrice completa
 *
 * Thread-safe: NO (uso single-threaded)
 *
 * Note: Classe nel package di default per accesso a Data/Tuple.
 */
public class DistanceCache {

    private HashMap<Long, Double> cache;
    private Data data;
    private boolean enabled;
    private double maxCachedDistance;

    // Statistiche
    private int hits = 0;
    private int misses = 0;
    private int calculations = 0;

    /**
     * Costruttore del cache con limite distanza.
     *
     * @param data dataset di riferimento
     * @param enabled se il cache è abilitato
     * @param maxCachedDistance distanza massima da cachare (filtra cache)
     */
    public DistanceCache(Data data, boolean enabled, double maxCachedDistance) {
        this.data = data;
        this.enabled = enabled;
        this.maxCachedDistance = maxCachedDistance;
        this.cache = new HashMap<>();
    }

    /**
     * Costruttore semplificato con cache abilitata e nessun limite distanza.
     *
     * @param data dataset di riferimento
     */
    public DistanceCache(Data data) {
        this(data, true, Double.MAX_VALUE);
    }

    /**
     * Ottiene la distanza tra due tuple, con caching lazy.
     * Se la distanza non è in cache, la calcola e la memorizza.
     *
     * @param tupleId1 ID prima tupla
     * @param tupleId2 ID seconda tupla
     * @return distanza tra le tuple
     */
    public double getDistance(int tupleId1, int tupleId2) {
        if (!enabled) {
            calculations++;
            return calculateDistance(tupleId1, tupleId2);
        }

        // Normalizza ordine (i < j) per simmetria
        if (tupleId1 > tupleId2) {
            int temp = tupleId1;
            tupleId1 = tupleId2;
            tupleId2 = temp;
        }

        // Crea chiave univoca
        long key = makeKey(tupleId1, tupleId2);

        // Cerca in cache
        Double cached = cache.get(key);
        if (cached != null) {
            hits++;
            return cached;
        }

        // Cache miss - calcola e memorizza
        misses++;
        calculations++;
        double distance = calculateDistance(tupleId1, tupleId2);

        // Memorizza solo se sotto threshold (riduce memoria)
        if (distance <= maxCachedDistance) {
            cache.put(key, distance);
        }

        return distance;
    }

    /**
     * Calcola la distanza tra due tuple usando il dataset.
     *
     * @param tupleId1 ID prima tupla
     * @param tupleId2 ID seconda tupla
     * @return distanza calcolata
     */
    private double calculateDistance(int tupleId1, int tupleId2) {
        Tuple t1 = data.getItemSet(tupleId1);
        Tuple t2 = data.getItemSet(tupleId2);
        return t1.getDistance(t2);
    }

    /**
     * Crea chiave univoca Long da due ID int.
     * Formula: (id1 << 32) | id2
     *
     * @param id1 primo ID (deve essere < id2)
     * @param id2 secondo ID
     * @return chiave Long univoca
     */
    private long makeKey(int id1, int id2) {
        return ((long) id1 << 32) | (id2 & 0xFFFFFFFFL);
    }

    /**
     * Pulisce completamente il cache.
     */
    public void clear() {
        cache.clear();
        hits = 0;
        misses = 0;
        calculations = 0;
    }

    /**
     * Abilita o disabilita il cache.
     *
     * @param enabled true per abilitare, false per disabilitare
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Verifica se il cache è abilitato.
     *
     * @return true se abilitato
     */
    public boolean isEnabled() {
        return enabled;
    }

    // ========== STATISTICHE ==========

    /**
     * Restituisce numero di cache hit.
     *
     * @return numero hit
     */
    public int getHitCount() {
        return hits;
    }

    /**
     * Restituisce numero di cache miss.
     *
     * @return numero miss
     */
    public int getMissCount() {
        return misses;
    }

    /**
     * Restituisce numero totale di calcoli distanza eseguiti.
     *
     * @return numero calcoli
     */
    public int getCalculations() {
        return calculations;
    }

    /**
     * Restituisce hit rate percentuale.
     *
     * @return hit rate [0.0, 1.0]
     */
    public double getHitRate() {
        int total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }

    /**
     * Restituisce dimensione corrente del cache.
     *
     * @return numero entry in cache
     */
    public int getSize() {
        return cache.size();
    }

    /**
     * Stima memoria usata dal cache in bytes.
     * Approssimativo: ogni entry HashMap ~40 bytes + Long (16) + Double (16)
     *
     * @return bytes usati (stima)
     */
    public long getEstimatedMemoryBytes() {
        return cache.size() * 72L;  // ~72 bytes per entry
    }

    /**
     * Stima memoria in MB.
     *
     * @return MB usati (stima)
     */
    public double getEstimatedMemoryMB() {
        return getEstimatedMemoryBytes() / (1024.0 * 1024.0);
    }

    /**
     * Restituisce stringa con statistiche dettagliate.
     *
     * @return stringa statistiche
     */
    public String getStats() {
        return String.format(
            "DistanceCache Statistics:\n" +
            "  Status: %s\n" +
            "  Cache Size: %d entries\n" +
            "  Memory: %.2f MB\n" +
            "  Hits: %d\n" +
            "  Misses: %d\n" +
            "  Hit Rate: %.2f%%\n" +
            "  Total Calculations: %d\n" +
            "  Calculations Saved: %d (%.1f%%)",
            enabled ? "ENABLED" : "DISABLED",
            cache.size(),
            getEstimatedMemoryMB(),
            hits,
            misses,
            getHitRate() * 100,
            calculations,
            hits,
            calculations > 0 ? (hits * 100.0 / (hits + calculations)) : 0.0
        );
    }

    @Override
    public String toString() {
        return String.format("DistanceCache[size=%d, hitRate=%.2f%%]",
            cache.size(), getHitRate() * 100);
    }
}
