package tests;

import keyboardinput.Keyboard;

/**
 * Test per verificare il funzionamento della classe Keyboard. Questo test dimostra che
 * Keyboard gestisce correttamente input invalidi.
 */
public class TestKeyboardInput {

    public static void main(String[] args) {
        System.out.println("=== Test Keyboard Input (QT03) ===\n");

        // Test 1: readInt con errori
        System.out.println("Test 1: readInt()");
        System.out.println("Prova a inserire: abc, 123, -999");
        System.out.print("Inserisci un intero: ");
        int num = Keyboard.readInt();
        System.out.println("Valore letto: " + num);
        System.out.println("ErrorCount: " + Keyboard.getErrorCount());
        System.out.println();

        // Test 2: readDouble con errori
        System.out.println("Test 2: readDouble()");
        System.out.println("Prova a inserire: xyz, 0.5, -1.5");
        System.out.print("Inserisci un double: ");
        double dnum = Keyboard.readDouble();
        System.out.println("Valore letto: " + dnum);
        System.out.println("Is NaN? " + Double.isNaN(dnum));
        System.out.println("ErrorCount: " + Keyboard.getErrorCount());
        System.out.println();

        // Test 3: readString
        System.out.println("Test 3: readString()");
        System.out.print("Inserisci una stringa: ");
        String str = Keyboard.readString();
        System.out.println("Valore letto: '" + str + "'");
        System.out.println();

        // Test 4: readWord
        System.out.println("Test 4: readWord()");
        System.out.print("Inserisci una parola: ");
        String word = Keyboard.readWord();
        System.out.println("Valore letto: '" + word + "'");
        System.out.println();

        System.out.println("=== Test completato ===");
        System.out.println("Totale errori Keyboard: " + Keyboard.getErrorCount());
    }
}
