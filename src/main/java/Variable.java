/**
 * Class that keep integer and character value to push in stack like a cell
 * Created by Furkan Mustafa on 23.03.2016.
 */
public class Variable {

    private int elementInt;
    private char elementChar;

    /**
     * Default constructer
     */
    Variable(){
        elementChar = '.';
        elementInt = 0;
    }

    /**
     * Contructer that takes integer value
     * @param varInt int value
     */
    Variable(int varInt){
        elementChar = '.';
        elementInt = varInt;
    }

    /**
     * Constructer that takes character value
     * @param varChar character value
     */
    Variable(char varChar){
        elementChar = varChar;
        elementInt = 0;
    }

    /**
     * Constructer that takes integer and character values
     * @param varInt integer value
     * @param varChar character value
     */
    Variable(int varInt, char varChar){
        elementChar = varChar;
        elementInt = varInt;
    }

    /**
     * Constructer that takes integer and character values
     * @param varChar character value
     * @param varInt integer value
     */
    Variable(char varChar, int varInt){
        elementChar = varChar;
        elementInt = varInt;
    }

    /**
     * Change the value of character
     * @param varChar parameter that change to character
     */
    public void setChar(char varChar){
        elementChar = varChar;
    }

    /**
     * Change the value of integer
     * @param varInt parameter that change to integer
     */
    public void setInt(int varInt)
    {
        elementInt = varInt;
    }

    /**
     * to take integer value
     * @return integer
     */
    public int getInt(){
        return(elementInt);
    }

    /**
     * to take character value
     * @return character
     */
    public char getChar(){
        return(elementChar);
    }

    /**
     * Overload to get integer value
     * @param var integer value to set and compare
     * @return the compared value with another overloaded
     */
    public int getVariable(int var){
        elementInt = var;
        return(var);
    }

    /**
     * Overload to take character value
     * @param var character value to set and compare
     * @return the compared value with another overloaded
     */
    public char getVariable(char var){
        elementChar = var;
        return(var);
    }

    /**
     * override to display class
     * @return string to test
     */
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
