package CS241;

import CS241.Grammer.Relation;

import java.lang.reflect.Field;

public class Value{

    public enum Type{
        constant, variable, register, condition, branch, instruction;
    }

    public Type type;              //result type
    public int value;              //value if constant
    public int varIdent;           //id if variable
    public int address;            //address if variable
    public int left;               //ssa version if variable
    public int right;              //ssa version if variable
    public int regno;              //register number if register
    public Instruction instrRef;
    public Relation.Operator relOp;
    public Block branchBlock;


    public Value() {}


    public Value(Type type,
                 int value,
                 int varIdent,
                 int address,
                 int left,
                 int right,
                 int regno,
                 Instruction instrRef,
                 Relation.Operator relOp,
                 Block branchBlock) {

        this.value = value;
        this.type = type;
        this.varIdent = varIdent;
        this.address = address;
        this.left = left;
        this.right = right;
        this.regno = regno;
        this.branchBlock = branchBlock;
        this.instrRef = instrRef;
        this.relOp = relOp;
    }

    public Value(Type type, int inputValue){
        switch(type) {
            case constant:
                this.type = type;
                this.value = inputValue;
                break;
            case variable:
                this.type = type;
                this.varIdent = inputValue;
                this.address = 0;
                break;
            case register:
                this.type = type;
                this.regno = inputValue;
                break;
            case instruction:
                this.type = type;
                // define branch detail later
                break;
            case branch:
                this.type = type;
                // define branch detail later
            default:
                break;
        }
    }

    public String toString() {
        switch (type) {
            case constant:
                return "(" + instrRef.id + ")";
            case variable:
                return "(" + instrRef.id + ")";
//                return VariableTable.Id2String(this.varIdent);
            case register:
                return "(" + instrRef.id + ")";
            case condition:
                return "(" + instrRef.id + ")";
            case instruction:
                return "(" + instrRef.id + ")";
            case branch:
                return  "(" + branchBlock.instr_list.get(0).id + ")";
//                return  "(issue)";
            default:
                return "Value";
        }
    }

    public void setInstrRef(Instruction instr){
        this.instrRef = instr;
    }

    public void setAddress(){
//        this.address = addr;
        VariableTable.VariMemoryAddress.put(this.varIdent, this);
    }

//    com.rits.cloning.Cloner;

    public Value clone() {
        return new Value(this.type,
                        this.value,
                        this.varIdent,
                        this.address,
                        this.left,
                        this.right,
                        this.regno,
                        this.instrRef,
                        this.relOp,
                        this. branchBlock);
    }

    public Value deepclone() {
        return new Value(this.type,
                this.value,
                this.varIdent,
                this.address,
                this.left,
                this.right,
                this.regno,
                (this.instrRef == null)? null:
                new Instruction(
                    this.instrRef.id,
                    this.instrRef.block,
                    this.instrRef.type,
                    this.instrRef.left,
                    this.instrRef.right
                    ),
                this.relOp,
                this.branchBlock
                );
    }


//    public Value deepclone() {
//        Cloner cloner = new Cloner();
//        Value val = cloner.deepClone(this);
//        return val;
//    }


    //    public static Value buildBranch(Block branchBlock){
//        Value result=new Value();
//        result.type=Type.branch;
//        result.branchBlock=branchBlock;
//        return result;
//    }
//
//    public static Value buildConstant(int value){
//        Value result=new Value();
//        result.type=Type.constant;
//        result.value=value;
//        return result;
//    }

//
//    @Override
//    public Object clone() {
//        Value val = null;
//        try {
//            val = (Value) super.clone();
//        } catch (CloneNotSupportedException e) {
//            val = new Value();
//            Field[] FromA = val.getClass().getDeclaredFields();
//        }
//        return val;
//    }
//
//
//    if (fromFields != null && tooFields != null) {
//        for (Field tooF : tooFields) {
//            logger.debug("toofield name #0 and type #1", tooF.getName(), tooF.getType().toString());
//            try {
//                // Check if that fields exists in the other method
//                Field fromF = fromClass.getDeclaredField(tooF.getName());
//                if (fromF.getType().equals(tooF.getType())) {
//                    tooF.set(tooF, fromF);
//                }
//            } catch (SecurityException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//    }
}
