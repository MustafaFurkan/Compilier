import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by SKYKING on 21.03.2016.
 */
public class Main {



    public static void main(String args[]) throws IOException {

        Calculator a = new Calculator();

        //a.readTxt("input.txt");

        a.toAssembly("b a b 3 * - =");
        //a 3 =
        //System.out.println(a.translate("C = A / 3 * B + 21"));
        //A / 3 * B + 21
        //A * 3 - B - 2 * 3
//"A - B * 3 * 4"
        //a.postFix("3 3 3 * -");

    }


}
