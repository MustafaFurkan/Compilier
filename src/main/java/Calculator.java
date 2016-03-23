import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by SKYKING on 21.03.2016.
 */
public class Calculator {

    private static final int ZERO = 0;
    private static final String OPERATORS = "+-*/= ";
    private Stack<Integer> opStack;
    private Stack<Character> opTranslateStack;
    private Stack<Variable> varStack;
    private static final int[] PRIORITY = {1, 2, 3, 4, 5, 0};
    private StringBuilder toPostFix;
    private Character tempVal = 'i';
    private Boolean check = false;
    private static int registerCount = 0;
    private ArrayList<Character> register;
    private ArrayList<Integer> registerContent;

    public String translate(String infix){

        opTranslateStack = new Stack<Character>();
        toPostFix = new StringBuilder();




        char val;

        StringTokenizer destroy = new StringTokenizer(infix, OPERATORS,true);

        StringBuffer toPostFix = new StringBuffer(infix.length());
        int count =0;
        while(destroy.hasMoreElements()) {

            String destroyed = destroy.nextToken();

            val = destroyed.charAt(ZERO);
//System.out.print(val);
            if (val == '*' || val == '/'){
                tempVal = val;
                check = true;
            }

            if (val == ' ')
                ;
            else if (Character.isDigit(val) || Character.isJavaIdentifierStart(val)) {
                toPostFix.append(destroyed);
                toPostFix.append(' ');
                System.out.println(toPostFix.toString() + "#### ");
                ++count;
                System.out.println(count);
                //bak buraya
            }
            else if(val == '+' || val == '-' || val == '*' || val == '=' || val == '/' ){
                System.out.println("val: " + val);
                translateProcess(val);
               //check = false;
            }

            else
                throw new SyntaxException("Unexpected Character: " + val);

            if (val == '=')
                count = 0;
            if (count == 2 ){
                --count;
                if (tempVal != 'i')
                toPostFix.append(tempVal);
                if (tempVal == '*' || tempVal == '/')
                    toPostFix.append(' ');
                tempVal = 'i';
                check = false;
            }

        }
        while (!opTranslateStack.empty()){
            char result = opTranslateStack.pop();
            toPostFix.append(result);
            toPostFix.append(' ');
        }
        return(toPostFix.toString());

//    return(null);
    }

    private void translateProcess(char opValue){
        if (check)
            return;
        if(opTranslateStack.empty()){System.out.println(opValue);
            //System.out.println(opValue); GİRİYO STACK TE YOK
            opTranslateStack.push(opValue);}
        else{//System.out.println(opValue);
            char opTopOfStack = opTranslateStack.peek();
            if (PRIORITY[OPERATORS.indexOf(opValue)] > PRIORITY[OPERATORS.indexOf(opTopOfStack)])
                opTranslateStack.push(opValue);
            else{
                //System.out.println(opValue + " " + "topofstack" + opTopOfStack + "#");
                while ((!opTranslateStack.empty())
                        && (PRIORITY[OPERATORS.indexOf(opTopOfStack)] < PRIORITY[OPERATORS.indexOf(opValue)])){
                    opTranslateStack.pop();
                    //System.out.println(opTopOfStack + "#### " );

                    toPostFix.append(opTopOfStack);
                    toPostFix.append(' ');

                    if (!opTranslateStack.empty())
                        opTopOfStack = opTranslateStack.peek();
                }
                //opTranslateStack.push(opTopOfStack);
                opTranslateStack.push(opValue);
            }

        }

    }

    public String toAssembly(String postfix){

        varStack= new Stack<Variable>();
      //  ArrayList<Integer> expression = new ArrayList<Integer>();
        //Stack decOfStack = new Stack();

       // expression.add((char) 65);
/*        expression.add((int) 'a');
char x;


        System.out.println(Character.toChars(expression.get(0)));
        if (Character.isDigit(expression.get(0)))
            System.out.println("x");
System.exit(0);
*/



        StringTokenizer destroy = new StringTokenizer(postfix, OPERATORS,true);

        StringBuffer toPostFix = new StringBuffer(postfix.length());

        while(destroy.hasMoreElements()){

            String destroyed = destroy.nextToken();

            Variable val = new Variable(destroyed.charAt(ZERO));
//System.out.println("yakup----------------" + destroyed.charAt(ZERO));
System.out.println(val.getChar() + " " + val.getInt() + "----------------");


//System.exit(0);
//const ayarla DEGIS
            if (Character.isDigit(val.getChar()))
            // if is digit
           // if(val.getChar() == '.')
            {
                int tempint = Integer.parseInt(destroyed);
                val.setInt(tempint);
                val.setChar('.');

                //Variable vi = new Variable(tempint);
                varStack.push(val);

System.out.println(val.getChar() + " " + val.getInt() + "----------------" + "rakam " + tempint);
            }else if(val.getChar() == '+' || val.getChar() == '-' || val.getChar() == '*'
                    || val.getChar() == '/' || val.getChar() == '='){
//System.exit(0);

                Variable result = new Variable('.',operation(val.getChar()));
                varStack.push(result);
            }else if (Character.isLetter(val.getChar())){
                varStack.push(val);

            }
            else if(val.getChar() == ' ' || val.getChar() == '.')
            //Ignore space character and specific control character
            ;
            // throw exception if invalid character in line
            else
                throw new SyntaxException("Invalid character: " + val.getChar());
System.out.println(varStack.toString());
        }
        Variable total = varStack.pop();
        if (varStack.empty())
            System.out.println(total.getInt());

            //System.out.print("size:" + destroyed.length() + ":");

            // System.out.print(val);
/*
            if(Character.isDigit(val)){

                //expression.add((int)val);

                int vi = Integer.parseInt(destroyed);
                varStack.push(vi);
            }

            else if(val == '+' || val == '-' || val == '*' || val == '/' || val == '='){

                int result = operation(val);
                opStack.push(result);
            }
            else if(val == ' ')
                ;
            else
                throw new SyntaxException("Invalid character: " + val);


        }
        int total = opStack.pop();
        if (opStack.empty())
            System.out.println(total);
        //return(total);
*/
        //  System.out.println(opStack.peek());
        return(null);
    }

    /*
    public String postFix(String postfix){

        opStack = new Stack<Integer>();

        //Stack decOfStack = new Stack();

        char val;

        StringTokenizer destroy = new StringTokenizer(postfix, OPERATORS,true);

        StringBuffer toPostFix = new StringBuffer(postfix.length());

        while(destroy.hasMoreElements()){

            String destroyed = destroy.nextToken();

            val = destroyed.charAt(ZERO);

            //System.out.print("size:" + destroyed.length() + ":");

           // System.out.print(val);

            if(Character.isDigit(val)){

                int vi = Integer.parseInt(destroyed);
                opStack.push(vi);
            }else if(val == '+' || val == '-' || val == '*' || val == '/' || val == '='){

                int result = operation(val);
                opStack.push(result);
            }else if(val == ' ')
                ;
            else
                throw new SyntaxException("Invalid character: " + val);


        }
            int total = opStack.pop();
        if (opStack.empty())
            System.out.println(total);
            //return(total);

      //  System.out.println(opStack.peek());
        return(null);
    }
*/
    private int operation(char eval){
        Variable rvalue = varStack.pop();
        Variable lvalue = varStack.pop();
// 0 yap result u
int result = 0;

        switch (eval){
            case '+':
                result = opAdd(lvalue,rvalue);
                //result = rvalue. + lvalue;
                break;
            case '-':
                result = opSub(lvalue,rvalue);
                //result = lvalue - rvalue;
                break;
            case '/':
                result = opDiv(lvalue,rvalue);
                //result = lvalue / rvalue;
                break;
            case '*':
                result = opMul(lvalue,rvalue);
                //result = rvalue * lvalue;
                break;
            case '=':
                opLoad(lvalue,rvalue);
                System.out.println("assignment op");
                break;
                /*if (Character.isDigit(rvalue)){
                    register[registerCount] = lvalue;
                }*/
        }
        return(result);
    }
    private int opAdd(Variable exp1, Variable exp2){

        return(0);
    }
    private int opSub(Variable exp1, Variable exp2){

        return(0);
    }
    private int opMul(Variable exp1, Variable exp2){

        return(0);
    }
    private int opDiv(Variable exp1, Variable exp2){

        return(0);
    }
    private void opLoad(Variable exp1, Variable exp2){
        // int e göre bak
        if (Character.isDigit(exp1.getChar())){


        }else if (Character.isDigit(exp2.getChar())){


        }


    }

    public void readTxt(String input) throws IOException {

//        System.out.println("input.txt");
  //      System.exit(0);
        String line = null;
        File txtFile = new File(String.valueOf(input));
        FileReader fread = new FileReader(txtFile);
        BufferedReader bread = new BufferedReader(fread);
        StringBuffer sbuff = new StringBuffer();

        while ((line = bread.readLine()) != null){
System.out.println(line);

                sbuff.append(line);
                sbuff.append("\n");



        }
        fread.close();
    }



}

/*
if(destroyed.length() == 1){
        if(val == '+' || val == '-' || val == '*' || val == '/' || val == '='){

        System.out.print(val);
                  while (!decOfStack.empty()){
                      toPostFix.append(" ");
                      toPostFix.append((String)decOfStack.pop());

                      if (destroyed.length() == 1 && val == ' ')
                          ;
                      decOfStack.push(destroyed);


        }else
        System.out.print(".");
*/