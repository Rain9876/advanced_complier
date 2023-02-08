package CS241;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableTable {

//    private static HashMap<String, Integer> GlobalVariableIdent;

    //store the SSA form variables which have the same address

//    private static HashMap<Integer, ArrayList<SSAValue>> SSAUseChain;

//    private static HashMap<Integer, ArrayList<SSAValue>> BlockSSA;


    public static HashMap<String, Integer> identifer_table = new HashMap<>();


    public static String Id2String(int id){
        String curKey= "";
        if (identifer_table.containsValue(id)) {
            for (String s : identifer_table.keySet()) {
                curKey = s;
                if (identifer_table.get(curKey).equals(id)) {
                    break;
                }
            }
        }
        return curKey;
    }

    public static int String2Id(String name){
        int index;
        if (identifer_table.containsKey(name)) {
            index = identifer_table.get(name);
        }else{
            index = identifer_table.size() + 1;
            identifer_table.put(name, index);
        }
        return index;
    }


    public static HashMap<Integer, Value> VariMemoryAddress = new HashMap<>();   // Store Variable Memory address


    public static Value lookupAddress(int id) {
        if (VariMemoryAddress.containsKey(id)) {
            return VariMemoryAddress.get(id);
        } else {
            Value tmp = new Value();
            VariMemoryAddress.put(id, tmp);
            return tmp;
        }
    }



    public static Boolean isAssignedVari(int id) {
        if (VariMemoryAddress.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }



}
