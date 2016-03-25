import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main class that test the Calculator Convert to Assembly
 * Created by Furkan Mustafa on 21.03.2016.
 */
public class Main {


    /**
     * Main method that test the project
     * @param args line input argument
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        Calculator a = new Calculator();
        // read file and fill into inputInfix arraylist
        a.readTxt("input.txt");
        System.out.println(a.toCalculate());
        a.writeAsm("output.asm");
        //a.toAssembly("24 4 /");
        //a b +
        //b a b 3 * - =
        //System.out.println(a.translate("C = A / 3 * B + 21"));
        //A / 3 * B + 21
        //A * 3 - B - 2 * 3
//"A - B * 3 * 4"
        //a.postFix("3 3 3 * -");

    }


}
