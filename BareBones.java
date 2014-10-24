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
                case "while": {
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    Integer currentValue = variableHash.get(nextVariable);
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
                    if (checkCode.equals("not0do;")) {
                        currentLoop++;
                        while (currentValue != 0) {
                            variableHash = readCode(codeArray, variableHash, pointer, line, currentLoop);
                            currentValue = variableHash.get(nextVariable);
                        }
                        pointer = loopTracker.get(currentLoop);
                        currentLoop--;
                    }
                    break;
                }
                case "end": {
                    if (currentLoop > 0) {
                        pointer++;
                        if (!codeArray[pointer].equals(";")) {
                            throw new LanguageException("ERROR: All commands must be followed by a semi colon Line: " + line);
                        } else {
                            loopTracker.put(currentLoop, pointer);
                        }
                        leaveLoop = true;

                    } else {
                        throw new LanguageException("ERROR: End loop without while clause on line " + line);
                    }
                    break;
                }
                case ";": {
                    throw new LanguageException("ERROR: No code entered before ';' on line " + line);
                }
                default:
                    throw new LanguageException("ERROR: '" + codeArray[pointer] + "' command not recognised on line " + line);
            }
            pointer++;
            line++;
        }
        return variableHash;
    }

    public String[] getStringArray(String code) {
        code = code.replace("[\n\r]", "");
        code = code.replace(";", " ; ");
        code = code.replaceAll("\\s+", " ");
        System.out.println(code);
        String[] codeArray = code.split("\\s+");
        return codeArray;
    }

    public String readHashMap(HashMap<String, Integer> variableArray) {
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
}
