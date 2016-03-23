/**
 * Created by SKYKING on 23.03.2016.
 */
public class Variable {

    private int elementInt;
    private char elementChar;

    Variable(){
        elementChar = '.';
        elementInt = 0;
    }
    Variable(int varInt){
        elementChar = '.';
        elementInt = varInt;
    }
    Variable(char varChar){
        elementChar = varChar;
        elementInt = 0;
    }
    Variable(int varInt, char varChar){
        elementChar = varChar;
        elementInt = varInt;
    }
    Variable(char varChar, int varInt){
        elementChar = varChar;
        elementInt = varInt;
    }
    public void setChar(char varChar){
        elementChar = varChar;
    }
    public void setInt(int varInt)
    {
        elementInt = varInt;
    }
    public int getInt(){
        return(elementInt);
    }
    public char getChar(){
        return(elementChar);
    }
    public int getVariable(int var){
        elementInt = var;
        return(var);
    }
    public char getVariable(char var){
        elementChar = var;
        return(var);
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        // Print description of list
        result.append("CHARACTER[");
        result.append(this.getChar());
        result.append("]-INTEGER[");
        result.append(this.getInt());
        result.append("]");

        // Return content of toString which was personalized by user.
        return(result.toString());
    }



}
