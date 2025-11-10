package gui.charts;

import gui.utils.Point2D;
import java.util.*;

/**
 * Calcola l'inviluppo convesso (convex hull) di un insieme di punti 2D
 * usando l'algoritmo Graham Scan.
 *
 * <p>L'algoritmo Graham Scan è un metodo efficiente per calcolare il convex hull
 * con complessità temporale O(n log n), dove n è il numero di punti.</p>
 *
 * <h3>Algoritmo</h3>
 * <ol>
 *   <li>Trova il punto con coordinata Y minima (pivot)</li>
 *   <li>Ordina i punti per angolo polare rispetto al pivot</li>
 *   <li>Usa uno stack per costruire l'hull scartando punti interni</li>
 * </ol>
 *
 * <h3>Esempio d'uso</h3>
 * <pre>{@code
 * List<Point2D> points = Arrays.asList(
 *     new Point2D(0, 0),
 *     new Point2D(1, 0),
 *     new Point2D(1, 1),
 *     new Point2D(0, 1),
 *     new Point2D(0.5, 0.5)  // punto interno
 * );
 *
 * List<Point2D> hull = ConvexHullCalculator.grahamScan(points);
 * // Risultato: [(0,0), (1,0), (1,1), (0,1)] - vertici del quadrato
 * }</pre>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.1.0
 * @see Point2D
 */
public class ConvexHullCalculator {

    /**
     * Calcola il convex hull di un insieme di punti usando algoritmo Graham Scan.
     *
     * <p>L'algoritmo restituisce i punti del convex hull ordinati in senso
     * antiorario (counter-clockwise) partendo dal punto più basso.</p>
     *
     * <p><strong>Complessità:</strong> O(n log n) per l'ordinamento dei punti</p>
     *
     * @param points lista di punti (minimo 3 punti richiesti)
     * @return lista ordinata di punti che formano il convex hull (senso antiorario)
     * @throws IllegalArgumentException se points è null o ha meno di 3 elementi
     */
    public static List<Point2D> grahamScan(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException(
                "Convex hull richiede almeno 3 punti"
            );
        }

        // 1. Trova punto con Y minima (pivot)
        Point2D pivot = findLowestPoint(points);

        // 2. Ordina punti per angolo polare rispetto al pivot
        List<Point2D> sorted = new ArrayList<>(points);
        sorted.sort((p1, p2) -> {
            if (p1.equals(pivot)) return -1;
            if (p2.equals(pivot)) return 1;

            double angle1 = p1.polarAngleFrom(pivot);
            double angle2 = p2.polarAngleFrom(pivot);

            int angleCompare = Double.compare(angle1, angle2);
            if (angleCompare != 0) return angleCompare;

            // Se stesso angolo, prendi il più vicino al pivot
            return Double.compare(
                pivot.distanceTo(p1),
                pivot.distanceTo(p2)
            );
        });

        // 3. Graham Scan: costruisci hull usando stack
        Deque<Point2D> hull = new ArrayDeque<>();
        hull.push(sorted.get(0));
        hull.push(sorted.get(1));

        for (int i = 2; i < sorted.size(); i++) {
            Point2D top = hull.pop();

            // Rimuovi punti che creano svolta a destra (clockwise)
            while (!hull.isEmpty() &&
                   ccw(hull.peek(), top, sorted.get(i)) <= 0) {
                top = hull.pop();
            }

            hull.push(top);
            hull.push(sorted.get(i));
        }

        return new ArrayList<>(hull);
    }

    /**
     * Trova il punto con coordinata Y minima (più in basso).
     *
     * <p>In caso di pareggio sulla coordinata Y, sceglie il punto
     * con coordinata X minima (più a sinistra).</p>
     *
     * <p>Questo punto diventa il pivot per l'ordinamento polare.</p>
     *
     * @param points lista di punti
     * @return punto con Y minima (e X minima in caso di pareggio)
     */
    private static Point2D findLowestPoint(List<Point2D> points) {
        Point2D lowest = points.get(0);

        for (Point2D p : points) {
            if (p.getY() < lowest.getY() ||
                (p.getY() == lowest.getY() && p.getX() < lowest.getX())) {
                lowest = p;
            }
        }

        return lowest;
    }

    /**
     * Test di orientazione per tre punti (Counter-Clockwise test).
     *
     * <p>Determina se tre punti formano una svolta a sinistra (CCW),
     * a destra (CW), o sono collineari.</p>
     *
     * <p><strong>Formula:</strong> Cross product dei vettori (p1→p2) e (p1→p3):
     * <br><code>(p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)</code></p>
     *
     * <p>Il segno del risultato indica:</p>
     * <ul>
     *   <li><strong>&gt; 0:</strong> svolta a sinistra (counter-clockwise)</li>
     *   <li><strong>&lt; 0:</strong> svolta a destra (clockwise)</li>
     *   <li><strong>= 0:</strong> punti collineari</li>
     * </ul>
     *
     * @param p1 primo punto
     * @param p2 secondo punto
     * @param p3 terzo punto
     * @return valore positivo se CCW, negativo se CW, zero se collineari
     */
    private static double ccw(Point2D p1, Point2D p2, Point2D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) -
               (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }
}
