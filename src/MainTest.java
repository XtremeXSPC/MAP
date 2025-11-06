/**
 * Classe di test per l'algoritmo Quality Threshold.
 */
public class MainTest {
    /**
     * Metodo main per testare l'algoritmo QT.
     *
     * @param args argomenti della linea di comando
     */
    public static void main(String[] args) {
        Data data = new Data();
        System.out.println(data);
        double radius = 2.0;
        QTMiner qt = new QTMiner(radius);
        int numIter = qt.compute(data);
        System.out.println("Number of clusters:" + numIter);
        System.out.println(qt.getC().toString(data));
    }
}
