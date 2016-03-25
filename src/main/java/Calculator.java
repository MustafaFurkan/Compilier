import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Calculates given equation to assembly code
 * Created by FURKAN MUSTAFA on 21.03.2016.
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
    private static int regCount = 0;
    private static int lineCount = 0;
    private ArrayList<Character> register = new ArrayList<Character>();
    private ArrayList<Integer> registerContent = new ArrayList<Integer>();
    private ArrayList<String> toASM = new ArrayList<String>();
    private ArrayList<String> inpInfix = new ArrayList<String>();

    /**
     * Translate operation which reads on txt to postfix version
     * @param infix string that is version of infix equation
     * @return string that is version of postfix
     */
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
            // Mul and Div have priority execution so check true if character has a priority
            if (val == '*' || val == '/'){
                tempVal = val;
                check = true;
            }
            // Pass space character on txt
            if (val == ' ')
                ;
            // Control immediate type
            else if (Character.isDigit(val) || Character.isJavaIdentifierStart(val)) {
                toPostFix.append(destroyed);
                toPostFix.append(' ');
                ++count;
            }else if(val == '+' || val == '-' || val == '*' || val == '=' || val == '/' ){
                // if character is a operation calculate in translateProcess method
                // it will fill their stack and arraylist about calculation
                translateProcess(val);
            }else
                // unknown character entered errors
                throw new SyntaxException("Unexpected Character: " + val);
            // if operator is assignment make count 0 to read new line from txt
            if (val == '=')
                count = 0;
            // if character is a operator decrease count for take operator and 2 expression before current operator
            if (count == 2 ){
                --count;
                // if initial character still going on
                if (tempVal != 'i')
                    toPostFix.append(tempVal);
                // if character has a priority, fix the current string to push the stack
                if (tempVal == '*' || tempVal == '/')
                    toPostFix.append(' ');
                tempVal = 'i';
                check = false;
            }
        }
        while (!opTranslateStack.empty()){
            char result = opTranslateStack.pop();
            // stack to string builder
            toPostFix.append(result);
            toPostFix.append(' ');
        }
        //return string
        return(toPostFix.toString());
    }

    /**
     * Inside the stack to change 2 expression and operator to constant int value
     * @param opValue operator type
     */
    private void translateProcess(char opValue){
        // Control priority operator
        if (check)
            return;
        // Check to stack is available
        if(opTranslateStack.empty()){
            opTranslateStack.push(opValue);
        }
        else{
            char opTopOfStack = opTranslateStack.peek();
            if (PRIORITY[OPERATORS.indexOf(opValue)] > PRIORITY[OPERATORS.indexOf(opTopOfStack)])
                opTranslateStack.push(opValue);
            else{
                // Fix the stack with priority rule(make assign op to last)
                while ((!opTranslateStack.empty())
                        && (PRIORITY[OPERATORS.indexOf(opTopOfStack)] < PRIORITY[OPERATORS.indexOf(opValue)])){
                    opTranslateStack.pop();
                    toPostFix.append(opTopOfStack);
                    toPostFix.append(' ');
                    if (!opTranslateStack.empty())
                        opTopOfStack = opTranslateStack.peek();
                }
                opTranslateStack.push(opValue);
            }
        }
    }

    /**
     * Translate postfix to assembly with current operator
     * Calculates variable in stack
     * @param postfix that version of postfix equation to calculate
     * @return result of calculation
     */
    public int toAssembly(String postfix){

        varStack= new Stack<Variable>();

        StringTokenizer destroy = new StringTokenizer(postfix, OPERATORS,true);
        StringBuffer toPostFix = new StringBuffer(postfix.length());

        while(destroy.hasMoreElements()){
            String destroyed = destroy.nextToken();
            Variable val = new Variable(destroyed.charAt(ZERO));
            // Check value is character or digit
            if (Character.isDigit(val.getChar()))
            {
                int tempint = Integer.parseInt(destroyed);
                val.setInt(tempint);
                val.setChar('.');
                varStack.push(val);
            }else if(val.getChar() == '+' || val.getChar() == '-' || val.getChar() == '*'
                    || val.getChar() == '/' || val.getChar() == '='){
                // if character is a operation call operation method
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
        }
        Variable total = varStack.pop();
        if (varStack.empty())
            return(total.getInt());
        // Halt the program if Calculation could not complated
        throw new ArithmeticException("Operation could not finis yet! " + varStack.toString());
    }

    /**
     * Checks all operation to divide them
     * @param eval that operation type
     * @return result of 1 step of calculation
     */
    private int operation(char eval){
        Variable rvalue = varStack.pop();
        Variable lvalue = varStack.pop();
        int result = 0;
        // Find operation
        switch (eval){
            case '+':
                result = opAdd(lvalue,rvalue);
                break;
            case '-':
                result = opSub(lvalue,rvalue);
                break;
            case '/':
                if(rvalue.getChar() == '.' && rvalue.getInt() == 0)
                    throw new ArithmeticException("Divide by Zero!" + rvalue.getInt());
                result = opDiv(lvalue,rvalue);
                break;
            case '*':
                result = opMul(lvalue,rvalue);
                break;
            case '=':
                result = opLoad(lvalue,rvalue);
                break;
        }
        // Resul of 1 step exuation
        return(result);
    }

    /**
     * Add operation add exp1 and exp2
     * @param exp1 that has character and int value to add
     * @param exp2 that has character and int value to add
     * @return result of 1 step execution
     */
    private int opAdd(Variable exp1, Variable exp2){

        StringBuilder asm = new StringBuilder();
        // if exp1 and exp2 are constant integer value
        if (exp1.getChar() == '.' && exp2.getChar() == '.'){
            // increase line
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
                ++lineCount;
                registerContent.add(opLoad(new Variable('t'),exp1));
                --lineCount;
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
                if (register.size() <= lineCount){
                    ++lineCount;
                    registerContent.add(opLoad(new Variable('t'),exp1));
                    --lineCount;
                }else {
                    register.set(lineCount,'t');
                    registerContent.set(lineCount, exp1.getInt());
                }
            }
            // Prepare assembly code
            ++lineCount;
            asm.append("add $t");
            asm.append(lineCount+1);
            asm.append(", $t");
            asm.append(lineCount);
            asm.append(", $t");
            asm.append(lineCount-1);
            asm.append("\n");
        }else if (exp1.getChar() != '.' && exp2.getChar() != '.'){
            // if exp1 and exp2 are already initilized
            for (int i=0; i<register.size(); ++i){
                if (register.get(i) == exp1.getChar()){
                    for (int j=0; j<register.size(); ++j){
                        if (register.get(j) == exp2.getChar()){
                            // Prepare assembly format
                            asm.append("add $t");
                            asm.append(lineCount);
                            asm.append(", $t");
                            asm.append(i);
                            asm.append(", $t");
                            asm.append(j);
                            asm.append("\n");

                            toASM.add(asm.toString());
                            return(registerContent.get(i) + registerContent.get(j));
                        }
                    }
                }
            }
        }else if (exp1.getChar() != '.' && exp2.getChar() == '.'){
            // if exp2 is digit exp1 is character
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp1.getChar()){
                    // Prepare assembly format
                    asm.append("add $t");
                    asm.append(lineCount+1);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp2.getInt() + registerContent.get(j));
                }
            }
        }else if (exp1.getChar() == '.' && exp2.getChar() != '.')
        {
            // exp1 is digit and exp2 is already initiliazed
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp1));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp1.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp2.getChar()){
                    // Prepare to assembly
                    asm.append("add $t");
                    asm.append(lineCount+1);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp1.getInt() + registerContent.get(j));
                }
            }
        }
        toASM.add(asm.toString());
        --lineCount;
        return(exp1.getInt() + exp2.getInt());
    }

    /**
     * Operation of sub
     * Sub exp1 and exp2
     * @param exp1 that has character and int value to sub
     * @param exp2 that has character and int value to sub
     * @return result of sub
     */
    private int opSub(Variable exp1, Variable exp2){
        StringBuilder asm = new StringBuilder();
        // if exp1 and exp2 are constant integer value
        if (exp1.getChar() == '.' && exp2.getChar() == '.'){
            // Increase line counter
            // exp1 and exp2 are constant integer value
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
                ++lineCount;
                registerContent.add(opLoad(new Variable('t'),exp1));
                --lineCount;
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
                if (register.size() <= lineCount){
                    ++lineCount;
                    registerContent.add(opLoad(new Variable('t'),exp1));
                    --lineCount;
                }else {
                    register.set(lineCount,'t');
                    registerContent.set(lineCount, exp1.getInt());
                }
            }
            // Prepare to assembly
            ++lineCount;
            asm.append("sub $t");
            asm.append(lineCount+1);
            asm.append(", $t");
            asm.append(lineCount);
            asm.append(", $t");
            asm.append(lineCount-1);
            asm.append("\n");
        // exp1 and exp2 are already initialized
        }else if (exp1.getChar() != '.' && exp2.getChar() != '.'){
            for (int i=0; i<register.size(); ++i){
                if (register.get(i) == exp1.getChar()){
                    for (int j=0; j<register.size(); ++j){
                        if (register.get(j) == exp2.getChar()){
                            asm.append("sub $t");
                            asm.append(lineCount);
                            asm.append(", $t");
                            asm.append(i);
                            asm.append(", $t");
                            asm.append(j);
                            asm.append("\n");

                            toASM.add(asm.toString());
                            return(registerContent.get(i) - registerContent.get(j));
                        }
                    }
                }
            }
        }else if (exp1.getChar() != '.' && exp2.getChar() == '.'){
            // exp2 is digit and exp1 is already initialized
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp1.getChar()){
                    // Prepare to assembly
                    asm.append("sub $t");
                    asm.append(lineCount+1);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(registerContent.get(j) - exp2.getInt());
                }
            }
        }else if (exp1.getChar() == '.' && exp2.getChar() != '.')
        {
            // exp1 is digit and exp2 already initialized
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp1));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp1.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp2.getChar()){
                    // Prepare to assembly
                    asm.append("sub $t");
                    asm.append(lineCount+1);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp1.getInt() - registerContent.get(j));
                }
            }
        }
        toASM.add(asm.toString());
        --lineCount;
        return(exp1.getInt() - exp2.getInt());
    }

    /**
     * The method calculate mul of exp1 and exp2
     * It convert ot assembly code
     * @param exp1 that has character and int value to mult
     * @param exp2 that has character and int value to mult
     * @return result of calculation
     */
    private int opMul(Variable exp1, Variable exp2){
        StringBuilder asm = new StringBuilder();
        // if exp1 and exp2 are constant integer value
        if (exp1.getChar() == '.' && exp2.getChar() == '.'){
            // Increase line count
            // exp1 and exp2 are constant integer value
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
                ++lineCount;
                registerContent.add(opLoad(new Variable('t'),exp1));
                --lineCount;
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
                if (register.size() <= lineCount){
                    ++lineCount;
                    registerContent.add(opLoad(new Variable('t'),exp1));
                    --lineCount;
                }else {
                    register.set(lineCount,'t');
                    registerContent.set(lineCount, exp1.getInt());
                }
            }
            // Convert to assembly
            ++lineCount;
            asm.append("mult $t");
            asm.append(lineCount);
            asm.append(", $t");
            asm.append(lineCount-1);
            asm.append("\n");
            asm.append("mflo $t");
            asm.append(lineCount+1);
            asm.append("\n");
        }else if (exp1.getChar() != '.' && exp2.getChar() != '.'){
            // exp1 and exp2 are already initialized
            for (int i=0; i<register.size(); ++i){
                if (register.get(i) == exp1.getChar()){
                    for (int j=0; j<register.size(); ++j){
                        if (register.get(j) == exp2.getChar()){
                            // Prepare to assembly
                            asm.append("mult $t");
                            asm.append(i);
                            asm.append(", $t");
                            asm.append(j);
                            asm.append("\n");
                            asm.append("mflo $t");
                            asm.append(lineCount);
                            asm.append("\n");

                            toASM.add(asm.toString());
                            return(registerContent.get(i) * registerContent.get(j));
                        }
                    }
                }
            }
        }else if (exp1.getChar() != '.' && exp2.getChar() == '.'){
            // exp1 is already initialized and exp2 is constant integer value
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp1.getChar()){
                    // Prepare to assembly code
                    asm.append("mult $t");
                    asm.append(j);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append("\n");
                    asm.append("mflo $t");
                    asm.append(lineCount+1);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp2.getInt() * registerContent.get(j));
                }
            }
        }else if (exp1.getChar() == '.' && exp2.getChar() != '.')
        {
            // exp1 is a digit and exp2 is already initialized
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp1));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp1.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp2.getChar()){
                    // Prepare to assembly code
                    asm.append("mult $t");
                    asm.append(lineCount);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append("\n");
                    asm.append("mflo $t");
                    asm.append(lineCount+1);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp1.getInt() * registerContent.get(j));
                }
            }
        }
        toASM.add(asm.toString());
        --lineCount;
        return(exp1.getInt() * exp2.getInt());
    }
    private int opDiv(Variable exp1, Variable exp2){
        StringBuilder asm = new StringBuilder();
        // if exp1 and exp2 are constant integer value
        if (exp1.getChar() == '.' && exp2.getChar() == '.'){
            // Increase line counter
            // exp1 and exp2 are constant integer value
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
                ++lineCount;
                registerContent.add(opLoad(new Variable('t'),exp1));
                --lineCount;
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
                if (register.size() <= lineCount){
                    ++lineCount;
                    registerContent.add(opLoad(new Variable('t'),exp1));
                    --lineCount;
                }else {
                    register.set(lineCount,'t');
                    registerContent.set(lineCount, exp1.getInt());
                }
            }
            // Prepare to assembly
            ++lineCount;
            asm.append("div $t");
            asm.append(lineCount);
            asm.append(", $t");
            asm.append(lineCount-1);
            asm.append("\n");
            asm.append("mfhi $t");
            asm.append(lineCount+1);
            asm.append("\n");

        }else if (exp1.getChar() != '.' && exp2.getChar() != '.'){
            // exp1 and exp2 are already initialized
            for (int i=0; i<register.size(); ++i){
                if (register.get(i) == exp1.getChar()){
                    for (int j=0; j<register.size(); ++j){
                        if (register.get(j) == exp2.getChar()){
                            // Prepare to assembly
                            asm.append("div $t");
                            asm.append(i);
                            asm.append(", $t");
                            asm.append(j);
                            asm.append("\n");
                            asm.append("mfhi $t");
                            asm.append(lineCount);
                            asm.append("\n");

                            toASM.add(asm.toString());
                            return(registerContent.get(i) / registerContent.get(j));
                        }
                    }
                }
            }
        }else if (exp1.getChar() != '.' && exp2.getChar() == '.'){
            // exp2 is a digit and exp1 is already initialized
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp2));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp2.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp1.getChar()){
                    // Prepare to assembly
                    asm.append("div $t");
                    asm.append(j);
                    asm.append(", $t");
                    asm.append(lineCount);
                    asm.append("\n");
                    asm.append("mfhi $t");
                    asm.append(lineCount+1);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(registerContent.get(j) / exp2.getInt());
                }
            }
        }else if (exp1.getChar() == '.' && exp2.getChar() != '.')
        {
            // exp1 is a digit and exp2 is already initialized
            // Check register is avaliable or not
            if (register.size() <= lineCount){
                registerContent.add(opLoad(new Variable('t'), exp1));
            }else {
                register.set(lineCount,'t');
                registerContent.set(lineCount,exp1.getInt());
            }
            for (int j=0; j<register.size(); ++j){
                if (register.get(j) == exp2.getChar()){
                    // Prepare to assembly code
                    asm.append("div $t");
                    asm.append(lineCount);
                    asm.append(", $t");
                    asm.append(j);
                    asm.append("\n");
                    asm.append("mfhi $t");
                    asm.append(lineCount+1);
                    asm.append("\n");

                    toASM.add(asm.toString());
                    return(exp1.getInt() / registerContent.get(j));
                }
            }
        }
        toASM.add(asm.toString());
        --lineCount;
        return(exp1.getInt() / exp2.getInt());
    }

    /**
     * Operator of assignment and move
     * @param exp1 that has character and int value of assignment. It can not be digit
     * @param exp2 that has character and int value of assignment
     * @return result of 1 step equation
     */
    private int opLoad(Variable exp1, Variable exp2){

        StringBuilder asm = new StringBuilder();
        if (exp1.getChar() == '.'){
            throw new SyntaxException("Left expression can not be constant number: " + exp1.getInt());
        }else if (Character.isLetter(exp2.getChar())){
            // check in register, if variable used before
            // move
            for(int i=0; i<register.size(); ++i){
                if(register.get(i) == exp2.getChar()){
                    exp2.setChar('.');
                    exp2.setInt(registerContent.get(i));
                    register.add(exp1.getChar());
                    registerContent.add(exp2.getInt());
                    // if creat over 8 register, halt the process
                    if (register.size() > 8)
                        throw new ArrayIndexOutOfBoundsException("We have only 9 registers" + register.size());
                for (int j=0; i<register.size(); ++j){
                    if (register.get(j) == exp1.getChar()){
                        // Prepare to assembly
                        asm.append("move $t");
                        asm.append(j);
                        asm.append(", ");
                        asm.append("$t");
                        asm.append(i);
                        asm.append("  #t");
                        asm.append(j);
                        asm.append("=t");
                        asm.append(i);
                        asm.append("\n");

                        toASM.add(asm.toString());
                        return(exp2.getInt());
                    }
                }
                throw new SyntaxException("Invalid register used" + exp1.getChar());
                }
            }
            throw new ExceptionInInitializerError("Variable couldn't find in register" + exp2.getChar());
        }else if (exp2.getChar() == '.'){
            // if exp2 is a digit
            register.add(exp1.getChar());
            registerContent.add(exp2.getInt());
        }
        // if creat over 8 register, halt the process
        if (register.size() > 8)
            throw new ArrayIndexOutOfBoundsException("We have only 9 registers" + register.size());
        // Prepare to assembly
        asm.append("li $t");
        asm.append(lineCount);
        asm.append(", ");
        asm.append(exp2.getInt());
        asm.append("  #variable ");
        asm.append(exp1.getChar());
        asm.append("\n");

        toASM.add(asm.toString());

        // increase liCount to control temp register
        regCount++;
        return(exp2.getInt());
    }

    /**
     * Read equation from txt
     * @param input name of file which want to read
     * @throws IOException
     */
    public void readTxt(String input) throws IOException {

        String line = null;
        File txtFile = new File(String.valueOf(input));
        FileReader fread = new FileReader(txtFile);
        BufferedReader bread = new BufferedReader(fread);

        while ((line = bread.readLine()) != null){
            inpInfix.add(line);
        }

        fread.close();
    }

    public void writeAsm(String output) throws IOException {
        File asmFile = new File(output);
        FileWriter fileWriter = new FileWriter(asmFile);

        for (int i=0; i<toASM.size(); ++i)
            fileWriter.write(toASM.get(i));
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * The method fix assembly code to prepare to write on another file
     * @return result of equation
     */
    public int toCalculate(){
        int result = 0;

        for (int i=0; i< inpInfix.size(); ++i){
            if(!inpInfix.get(i).contains("print"))
                // if is not print method
                result = toAssembly(translate(inpInfix.get(i)));
            else {
                // if line is print, prepare to convert assembly
                StringBuilder asm = new StringBuilder();
                String[] token = inpInfix.get(i).split(" ");
                // get int value which want to print
                char valPrint = token[1].charAt(ZERO);
                // if digit value want to print
                if (Character.isDigit(valPrint)){
                    int intPrint = (int) valPrint;
                    // Check register is avaliable or not
                    if (register.size() <= lineCount){
                        registerContent.add(opLoad(new Variable('t'), new Variable(intPrint)));
                        ++lineCount;
                    }else {
                        register.set(lineCount,'t');
                        registerContent.set(lineCount,intPrint);
                        if (register.size() <= lineCount){
                            ++lineCount;
                            registerContent.add(opLoad(new Variable('t'),new Variable(intPrint)));
                            --lineCount;
                        }else {
                            register.set(lineCount,'t');
                            registerContent.set(lineCount, intPrint);
                        }
                    }
                    // Prepare to assembly
                    asm.append("move $a0, $t");
                    asm.append(lineCount);
                    asm.append("\nli $v0, 1 #print_in\nsyscall");
                    toASM.add(asm.toString());
                }else{
                    // get character value which want ot print
                    for (int j=0; j<register.size(); ++j){
                        if (register.get(j) == valPrint){
                            asm.append("move $a0, $t");
                            asm.append(j);
                            asm.append("\nli $v0, 1 #print_in\nsyscall");
                            toASM.add(asm.toString());
                        }
                    }
                }
            }
            // Increase register count which is not temporary
            ++lineCount;
        }
        // return result of total equation
        return(result);
    }
}