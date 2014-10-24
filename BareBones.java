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

    public HashMap<String, Integer> readCode(String[] codeArray, HashMap<String, Integer> variableArray, int pointer, int line, int currentLoop) throws LanguageException {
        boolean leaveLoop = false;
        while (pointer < codeArray.length - 1 && !leaveLoop) {
            switch (codeArray[pointer]) {
                case "clear": {
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    variableArray.put(nextVariable, 0);
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon");
                    }
                    break;
                }
                case "incr": {
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    Integer currentValue = variableArray.get(nextVariable);
                    if (currentValue == null) {
                        variableArray.put(nextVariable, 1);
                    } else {
                        Integer newValue = currentValue + 1;
                        variableArray.put(nextVariable, newValue);
                    }
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon");
                    }
                    break;
                }
                case "decr": {
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    Integer currentValue = variableArray.get(nextVariable);
                    if (currentValue == null) {
                        variableArray.put(nextVariable, -1);
                    } else {
                        Integer newValue = currentValue - 1;
                        variableArray.put(nextVariable, newValue);
                    }
                    pointer++;
                    if (!codeArray[pointer].equals(";")) {
                        throw new LanguageException("ERROR: All commands must be followed by a semi colon");
                    }
                    break;
                }
                case "while": {
                    pointer++;
                    String nextVariable = codeArray[pointer];
                    Integer currentValue = variableArray.get(nextVariable);
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
                            variableArray = readCode(codeArray, variableArray, pointer, line, currentLoop);
                            currentValue = variableArray.get(nextVariable);
                        }
                        pointer = loopTracker.get(currentLoop);
                        currentLoop--;
                    }
                    break;
                }
                case "end": {
                    if (currentLoop > 0) {
                        Integer endLoopPointer = loopTracker.get(currentLoop);
                        //if (endLoopPointer == null) {
                            pointer++;
                            if (!codeArray[pointer].equals(";")) {
                                throw new LanguageException("ERROR: All commands must be followed by a semi colon");
                            } else {
                                loopTracker.put(currentLoop, pointer);
                            }
                            leaveLoop = true;
                        //}else{
                        //    leaveLoop = true;
                        //}
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
        return variableArray;
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
