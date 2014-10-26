/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barebones;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author lpp1g14
 */
public class BareBones {

    //There is some advice on exteneding barebones at the end of the class
    //
    //Hash map keeps track of the which ends refer to which loops
    //It is set up as such <Loop , Pointer>
    protected HashMap<Integer, Integer> loopTracker = new HashMap<>();

    //Class HashMap. The class needs so many variables to allow for the recursion
    public HashMap<String, Integer> readCode(String[] codeArray, HashMap<String, Integer> variableHash, int pointer, int line, int currentLoop) throws LanguageException {
        //Used to allow the end statement to work
        boolean leaveLoop = false;
        //Go through the whole array reading every command unless we are told to leave the loop because of recursion
        while (pointer < codeArray.length - 1 && !leaveLoop) {
            //As all commands start off with the command first this works
            switch (codeArray[pointer]) {
                case "clear": {
                    //Common bits to clear, incr, decr and while
                    //Increasing the pointer moves the pointer to the variable name
                    pointer++;
                    //Get the name of the variable used in the command
                    String nextVariable = codeArray[pointer];
                    //Update or put the variable into the VariableHash and set it to 0
                    variableHash.put(nextVariable, 0);
                    //Increase the pointer and check the command ends in a semi-colon
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon Line: " + line);
                    }
                    break;
                }
                case "incr": {
                    //Common bits to clear, incr, decr and while
                    //Increasing the pointer moves the pointer to the variable name
                    pointer++;
                    //Get the name of the variable used in the command
                    String nextVariable = codeArray[pointer];
                    //Get the current value of the variable
                    Integer currentValue = variableHash.get(nextVariable);
                    //If the variable doesn't exist then set it to 1
                    if (currentValue == null) {
                        variableHash.put(nextVariable, 1);
                        //Else increase the current value by one and replace it
                    } else {
                        Integer newValue = currentValue + 1;
                        variableHash.put(nextVariable, newValue);
                    }
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon Line: " + line);
                    }
                    break;
                }
                case "decr": {
                    //Same as the incr function except it subtracts rather than adds
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    Integer currentValue = variableHash.get(nextVariable);
                    if (currentValue == null) {
                        variableHash.put(nextVariable, -1);
                    } else {
                        Integer newValue = currentValue - 1;
                        variableHash.put(nextVariable, newValue);
                    }
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon Line: " + line);
                    }
                    break;
                }
                //Probable one of the trickiest parts to get working was the while loop
                case "while": {
                    //Common bits to clear, incr, decr and while
                    //Increasing the pointer moves the pointer to the variable name
                    pointer++;
                    //Get the name of the variable used in the command
                    String nextVariable = codeArray[pointer];
                    //Get the current value of the variable
                    Integer currentValue = variableHash.get(nextVariable);
                    /*checkCode is a string that holds the rest of the while 
                     *loop (equal to "not0do;") as at the moment the while 
                     *loop just checks it is not equal to zero
                     *
                     *If you wanted to add more functionality you can simply 
                     *increase the pointer checking each word as you go
                     */
                    String checkCode = null;
                    for (int i = 0; i <= 3; i++) {
                        pointer++;
                        if (checkCode == null) {
                            checkCode = codeArray[pointer];
                        } else {
                            checkCode = checkCode + codeArray[pointer];
                        }
                    }
                    pointer++;

                    /*If you did want to improve the while loop you can do an
                     *switch statement that checks the operand i.e. not, <, >, ==.
                     *and then read in the variable and put it in the correct while loop
                     */
                    if (checkCode.equals("not0do;")) {
                        /*The currentLoop variable allows the program to keep 
                         * track of what loop it's in. The base program runs
                         * on loop 0 and isn't actually a loop. If the program enters
                         * a loop this goes up to 1.
                         */
                        currentLoop++;
                        //Perform the while loop
                        while (currentValue != 0) {
                            /*This is where it get's quite cool the function
                             * then calls itself recursivly but is on a different
                             *currentLoop
                             */
                            variableHash = readCode(codeArray, variableHash, pointer, line, currentLoop);
                            /* We then have to recheck the current value as 
                             *it doesn't automatically update itself                            
                             */
                            currentValue = variableHash.get(nextVariable);
                        }
                        /*loopTracker stores where the end of a while loop is
                         *so the program can jump straight to there once the while
                         *loop completes
                         */
                        pointer = loopTracker.get(currentLoop);
                        //Then we return to the previous loop
                        currentLoop--;
                    }
                    //Then break out of the if statement
                    break;
                }
                case "end": {
                    /*Another difficult function to add so it worked with the 
                     *while loop. It's also set up so that it could be the end
                     *of a recursive if statement.
                     */
                    //This if statment checks someone hasn't added too many
                    //end commands
                    if (currentLoop > 0) {
                        //Increase the pointer by one
                        pointer++;
                        //check the command ends with a ;
                        if (!codeArray[pointer].equals(";")) {
                            //If it doesn't throw an exception
                            throw new LanguageException("ERROR: All commands must be followed by a semi colon Line: " + line);
                        } else {
                            //Else put a new item in the loopTracker detailing
                            //where the loop ends
                            loopTracker.put(currentLoop, pointer);
                        }
                        //then set the leave loop variable to true
                        leaveLoop = true;
                        //If they have added too many end commands an exception is thrown
                    } else {
                        throw new LanguageException("ERROR: End loop without while clause on line " + line);
                    }
                    break;
                }
                case ";": {
                    //Checks there are no empty commands
                    //Also quite useful while debugging the interpreter
                    throw new LanguageException("ERROR: No code entered before ';' on line " + line);
                }
                default:
                    //If a random command has been entered that isn't recognised throw an exception
                    throw new LanguageException("ERROR: '" + codeArray[pointer] + "' command not recognised on line " + line);
            }
            //Increase the pointer and line
            pointer++;
            //line refers to the command rather than  which line it is shown on in the gui
            line++;
        }
        return variableHash;
    }

    public String[] getStringArray(String code) {
        //This turns read code into a string array that can be randomly accessed more easily
        //First we turn all line jumps into nothing
        code = code.replace("[\n\r]", "");
        //Then we put spaces around semi colons
        code = code.replace(";", " ; ");
        //Then we replace a spaces bigger than one with one space. This allows
        //multiple indented lines. 
        code = code.replaceAll("\\s+", " ");
        //For debugging purposed print out the code
        System.out.println(code);
        //Turn the string into an array by splitting the code on spaces
        String[] codeArray = code.split("\\s+");
        //Then return the codeArray
        return codeArray;
    }

    public String readHashMap(HashMap<String, Integer> variableArray) {
        /*After the code has been read a hash map of the variables is returned
         *these aren't very useful to read so I made a method that takes a hashmap
         *and by using an iterator turns it into a string with all the variables 
         *that were used an their final value
         */
        String returnedText = null;
        Iterator it = variableArray.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            if (returnedText == null) {
                returnedText = pairs.getKey() + " = " + pairs.getValue();
            } else {
                returnedText = returnedText + "\n" + pairs.getKey() + " = " + pairs.getValue();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return returnedText;
    }

    /*For the extended barbones:
     *
     *If you want to implement an If there are some suggestions in the end class
     *
     *If you are thinking of adding operands such as +, -, *, / etc
     *Can i recommend a way being a command e.g. math then variable e.g. X = X+Y
     *So the final command would be math X = X + Y;
     *
     *And then you can have a math class with other methods such as ^2, sqrt(), 
     *sin(), cos (), tan(), arcsin(), arccos(), arctan(), cosec(), sec(), cot(),
     *log(), ln(), ! [factorial]
     *Then if you're feeling really brave add sinh(), cosh(), tanh(), arsinh(), 
     *arcosh(), artanh(), cosech(), sech(), coth()
     *
     *You could also implement using scientific notation
     *
     *You could also implement strings though you may have to change the HashMap
     *variableHash so that it stores an object as apposed an Integer
     *
     *Another useful function could be print which could be implemented by adding
     *a new method into TheGUI class
     *
     *Hope this helps
     */
}
