import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;


public class Phonomatic {
    String[][] tree = new String[4][10];
    /** Table Row #: */
    private static String[] one = {"\u0165", "\u0074\u02B0", "\u0074\u032A", "t",
    "d", "s", "z", "\u0257", "\u0258", "\u0259", "\u025A",
    "\u025B", "\u025C", "\u025D", "\u025E", "\u025F"};
    
    /** Table Values + Value Features: */
    private static String[] \u0250 = {"-cons", "+son", "+syll", "+lab", "+rnd",
        "-cor", "0", "0", "+dor", "-high", "+low", "-front", "-back",
        "+tense", "+phar", "-ATR", "+voice", "-SG", "-CG", "+cont", "-strid",
        "-lat", "-d rel", "-nasal"};
    private static String[] \u0251 = {"-cons", "+son", "+syll", "+lab", "-rnd",
        "-cor", "0", "0", "+dor", "-high", "+low", "-front", "+back",
        "-tense", "+phar", "-ATR", "+voice", "-SG", "-CG", "+cont", "-strid",
        "-lat", "-d rel", "-nasal"};
    private static String[] \u0252 = {"-cons", "+son", "+syll", "+lab", "+rnd",
        "-cor", "0", "0", "+dor", "-high", "+low", "-front", "+back",
        "-tense", "+phar", "-ATR", "+voice", "-SG", "-CG", "+cont", "-strid",
        "-lat", "-d rel", "-nasal"};
    private static String[] \u0253 = {"+son", "+syll", "+cons"};
    private static String[] \u0256 = {"+son", "+syll", "+cons"};
    private static String[] \u0257 = {"+son", "+syll", "+cons"};
    private static String[] \u0258 = {"+son", "+syll", "+cons"};
    private static String[] \u0259 = {"+son", "+syll", "+cons"};
    private static String[] \u025A = {"+son", "+syll", "+cons"};
    private static String[] \u025B = {"+son", "+syll", "+cons"};
    private static String[] \u025C = {"+son", "+syll", "+cons"};
    private static String[] \u025D = {"+son", "+syll", "+cons"};
    private static String[] \u025E = {"+son", "+syll", "+cons"};
    private static String[] \u025F = {"+son", "+syll", "+cons"};
    
    String[] two = new String[15];
    String[] three = new String[15];
    String[] four = new String[15];
    
    public static void main(String[] args) {        
        Scanner inputScan = new Scanner(System.in);
        while (inputScan.hasNext()) {
            String input = inputScan.next();
            for (int i = 0; i < 10; i++) {
                if (input.contains(one[i])) {
                    System.out.println(input);
                    System.out.println(Arrays.toString(\u0250));
                }
            }
        }
        System.out.println(Arrays.toString(one));
        inputScan.close();
    }    
}
