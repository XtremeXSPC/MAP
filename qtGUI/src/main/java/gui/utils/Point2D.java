package gui.utils;

/**
 * Rappresenta un punto in spazio 2D con operazioni geometriche.
 * <p>
 * Classe immutabile per rappresentare coordinate cartesiane 2D e
 * fornire operazioni geometriche come calcolo distanza e angolo polare.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.1.0
 */
public class Point2D {

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    private final double x;
    private final double y;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce un punto 2D con coordinate specificate.
     *
     * @param x coordinata X
     * @param y coordinata Y
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Restituisce la coordinata X del punto.
     *
     * @return coordinata X
     */
    public double getX() {
        return x;
    }

    /**
     * Restituisce la coordinata Y del punto.
     *
     * @return coordinata Y
     */
    public double getY() {
        return y;
    }

    /**
     * Calcola la distanza euclidea da questo punto ad un altro punto.
     *
     * <p>Formula: sqrt((x2-x1)^2 + (y2-y1)^2)</p>
     *
     * @param other altro punto
     * @return distanza euclidea
     */
    public double distanceTo(Point2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calcola l'angolo polare di questo punto rispetto a un punto pivot.
     *
     * <p>L'angolo è calcolato usando atan2(dy, dx) ed è nell'intervallo [-π, π].</p>
     *
     * <p>Usato nell'algoritmo Graham Scan per ordinare i punti per angolo
     * rispetto al punto più basso (pivot).</p>
     *
     * @param pivot punto di riferimento (pivot)
     * @return angolo polare in radianti, intervallo [-π, π]
     */
    public double polarAngleFrom(Point2D pivot) {
        return Math.atan2(y - pivot.y, x - pivot.x);
    }

    //===--------------------------- OBJECT METHODS ----------------------------===//

    /**
     * Rappresentazione testuale del punto.
     *
     * @return stringa nel formato "(x, y)"
     */
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    /**
     * Verifica uguaglianza tra due punti.
     *
     * <p>Due punti sono uguali se hanno le stesse coordinate X e Y.</p>
     *
     * @param obj oggetto da confrontare
     * @return true se i punti sono uguali
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Point2D))
            return false;
        Point2D other = (Point2D) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    /**
     * Calcola hash code del punto.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }
}

//===---------------------------------------------------------------------------===//
