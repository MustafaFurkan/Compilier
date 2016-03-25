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
        Calculator test1 = new Calculator();
        Calculator test2 = new Calculator();
        // read file and fill into inputInfix arraylist
        try {
            a.readTxt("input.txt");
            System.out.println("Result of input file: ");
            System.out.println(a.toCalculate());
            a.writeAsm("output.asm");
        }catch (Exception e){
            System.out.println(e);
        }

        System.out.println("----------- Testing -----------");
        System.out.println("-Infix to Postfix-");
        System.out.println("-Infix form-");
        System.out.println("C = A / 3 * B + 21");
        System.out.println("-Postfix form-");
        System.out.println(test1.translate("C = A / 3 * B + 21"));

        System.out.println("--------- Calculation ---------");
        System.out.println("-Add-");
        System.out.println("24 4 +");
        System.out.println("Result: " + test2.toAssembly("24 4 +"));

        System.out.println("-Sub-");
        System.out.println("1 5 -");
        System.out.println("Result: " + test2.toAssembly("1 5 -"));

        System.out.println("-Mul-");
        System.out.println("24 3 *");
        System.out.println("Result: " + test2.toAssembly("24 3 *"));

        System.out.println("-Div-");
        System.out.println("44 4 /");
        try {
            System.out.println("Result: " + test2.toAssembly("44 4 /"));
        }catch (ArithmeticException e){
            System.out.println(e);
        }

        System.out.println("-Exeption-");
        System.out.println("44 0 /");
        try {
            System.out.println("Result: " + test2.toAssembly("44 0 /"));
        }catch (ArithmeticException e){
            System.out.println(e);
        }


        System.out.println("-Equation-");
        System.out.println("a b 3 * -");
        System.out.println("Result: " + test2.toAssembly("a b 3 * -"));

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
