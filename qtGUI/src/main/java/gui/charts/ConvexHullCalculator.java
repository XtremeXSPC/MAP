package gui.charts;

//===---------------------------------------------------------------------------===//
// Imporazioni Java standard.
import java.util.*;
// Importazioni progetto.
import gui.utils.Point2D;
//===---------------------------------------------------------------------------===//

/**
 * Utility per calcolare l'inviluppo convesso (Convex Hull) di punti 2D.
 * <p>
 * Questa classe implementa l'algoritmo di Graham Scan e gestisce:
 * <ul>
 *   <li>Selezione del pivot (punto con Y minima)</li>
 *   <li>Ordinamento per angolo polare rispetto al pivot</li>
 *   <li>Costruzione dell'hull tramite stack con test di orientazione</li>
 * </ul>
 * <p>
 * Complessita' temporale: O(n log n) dovuta all'ordinamento.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see Point2D
 */
public class ConvexHullCalculator {

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Calcola il Convex Hull di un insieme di punti con Graham Scan.
     * <p>
     * Il risultato e' una lista di punti ordinata in senso antiorario a
     * partire dal pivot (punto piu' basso). Eventuali duplicati vengono rimossi
     * prima del calcolo.
     *
     * @param points lista di punti (minimo 3 punti richiesti)
     * @return lista ordinata di punti che formano il convex hull (senso antiorario)
     * @throws IllegalArgumentException se points e' null o ha meno di 3 elementi
     */
    public static List<Point2D> grahamScan(List<Point2D> points) {
        if (points == null) {
            throw new IllegalArgumentException("Convex hull richiede almeno 3 punti");
        }

        Set<Point2D> uniquePoints = new LinkedHashSet<>(points);
        List<Point2D> uniqueList = new ArrayList<>(uniquePoints);

        if (uniqueList.size() < 3) {
            throw new IllegalArgumentException("Convex hull richiede almeno 3 punti");
        }

        // 1. Trova punto con Y minima (pivot).
        Point2D pivot = findLowestPoint(uniqueList);

        // 2. Ordina punti per angolo polare rispetto al pivot.
        List<Point2D> sorted = new ArrayList<>(uniqueList);
        sorted.sort((p1, p2) -> {
            if (p1.equals(pivot))
                return -1;
            if (p2.equals(pivot))
                return 1;

            double angle1 = p1.polarAngleFrom(pivot);
            double angle2 = p2.polarAngleFrom(pivot);

            int angleCompare = Double.compare(angle1, angle2);
            if (angleCompare != 0)
                return angleCompare;

            // Se stesso angolo, prendi il più vicino al pivot.
            return Double.compare(pivot.distanceTo(p1), pivot.distanceTo(p2));
        });

        // 3. Graham Scan: costruisci hull usando stack.
        Deque<Point2D> hull = new ArrayDeque<>();
        hull.push(sorted.get(0));
        hull.push(sorted.get(1));

        for (int i = 2; i < sorted.size(); i++) {
            Point2D top = hull.pop();

            // Rimuovi punti che creano svolta a destra (clockwise).
            while (!hull.isEmpty() && ccw(hull.peek(), top, sorted.get(i)) <= 0) {
                top = hull.pop();
            }

            hull.push(top);
            hull.push(sorted.get(i));
        }

        return new ArrayList<>(hull);
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Trova il punto con coordinata Y minima (pivot).
     * <p>
     * In caso di pareggio sulla coordinata Y, sceglie il punto con
     * coordinata X minima (piu' a sinistra).
     *
     * @param points lista di punti
     * @return punto con Y minima (e X minima in caso di pareggio)
     */
    private static Point2D findLowestPoint(List<Point2D> points) {
        Point2D lowest = points.get(0);

        for (Point2D p : points) {
            if (p.getY() < lowest.getY() || (p.getY() == lowest.getY() && p.getX() < lowest.getX())) {
                lowest = p;
            }
        }

        return lowest;
    }

    /**
     * Test di orientazione per tre punti (counter-clockwise test).
     * <p>
     * Determina se i punti formano una svolta a sinistra (CCW), a destra (CW)
     * o sono collineari usando il prodotto vettoriale.
     *
     * @param p1 primo punto
     * @param p2 secondo punto
     * @param p3 terzo punto
     * @return valore positivo se CCW, negativo se CW, zero se collineari
     */
    private static double ccw(Point2D p1, Point2D p2, Point2D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }
}

//===---------------------------------------------------------------------------===//
