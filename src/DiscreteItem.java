/**
 * Classe che modella un item discreto (coppia attributo discreto-valore discreto).
 */
public class DiscreteItem extends Item {
    /**
     * Costruttore della classe DiscreteItem.
     *
     * @param attribute attributo discreto
     * @param value valore discreto
     */
    public DiscreteItem(DiscreteAttribute attribute, String value) {
        super(attribute, value);
    }

    /**
     * Calcola la distanza tra il valore dell'item corrente e un altro oggetto.
     * Restituisce 0 se i valori sono uguali, 1 altrimenti.
     *
     * @param a oggetto con cui calcolare la distanza
     * @return 0 se uguali, 1 altrimenti
     */
    @Override
    public double distance(Object a) {
        return getValue().equals(a) ? 0 : 1;
    }
}
