package CS241;

import CS241.Grammer.Expression;
import CS241.Grammer.Relation;
import CS241.Grammer.Term;
import com.sun.jdi.connect.Connector;

import java.util.ArrayList;

public class Instruction {

    public enum Type {
        add,
        sub,
        mul,
        div,
        cmp,

        adda,
        load,
        store,
        phi,
        end,

        bra,
        bne,
        beq,
        ble,
        blt,
        bge,
        bgt,

        read,
        write,
        writeNL,
    }

    public int id;
    public Block block;
    public Type type;
    public Value left;
    public Value right;

    public Instruction(int id, Block block){
        this.id = id;
        this.block = block;
    }

    public Instruction(int id, Block block, Type type, Value left, Value right){
        this.id = id;
        this.block = block;
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public void setInstructionType(Type type){
        this.type=type;
    }

    public void setBlock(Block b){ this.block = b; }

//    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(id + ": ");
        if (type != null) {
            sb.append(type.name() + " ");
        }
        if (left != null) {
            sb.append(left.toString() + " ");
        }
        if (right != null) {
            sb.append(right.toString());
        }
        if (left == null && right == null && type == null){
            sb.append("<Empty>");
        }
        return sb.toString();
    }


    public static Type ComplementRelationMap(Relation.Operator op){
        switch (op) {
            case LessThan:
                return Type.bge;
            case LessEqual:
                return Type.bgt;
            case GreaterThan:
                return Type.bgt;
            case GreaterEqual:
                return Type.blt;
            case Equal:
                return Type.bne;
            case NotEqual:
                return Type.beq;
            default:
                return Type.bra;
        }
//        switch (op) {
//            case LessThan:
//                return Type.blt;
//            case LessEqual:
//                return Type.ble;
//            case GreaterThan:
//                return Type.bgt;
//            case GreaterEqual:
//                return Type.bge;
//            case Equal:
//                return Type.beq;
//            case NotEqual:
//                return Type.bne;
//            default:
//                return Type.bra;
//        }

    }


    public static Type ExprOperatorMap(Expression.Operator op){
        switch (op) {
            case Plus:
                return Type.add;
            case Minus:
                return Type.sub;
            default:
                return null;
        }

    }

    public static Type TermOperatorMap(Term.Operator op){
        switch (op) {
            case Times:
                return Type.mul;
            case Divide:
                return Type.div;
            default:
                return null;
        }

    }

//    public String toString() {
//        StringBuilder sb = new StringBuilder("");
//        sb.append(id + ": ");
//
//        if(this.state == State.REPLACE){
//            sb.append(" (" + referenceInstrId + ")");
//            return sb.toString();
//        }
//
//        sb.append(InstructionType.getInstructionName(this.op) + " ");
//
//        if (this.state == State.NORMAL) {
//            if (BranchOp.contains(op)) {
//                sb.append(result2.toString());
//            } else {
//                sb.append(result1 != null ? result1.toString() + " " : "");
//                sb.append(result2 != null ? result2.toString() : "");
//            }
//        } else {
//            String var1="";
//            String var2="";
//            if (leftRepresentedByInstrId) {
//                var1 = "(" + this.getLeftResult().instrRef + ")";
//            } else if(result1!=null&&result1.type==Result.ResultType.constant){
//                var1 = Integer.toString(result1.value);
//            } else if(result1!=null&&result1.type==Result.ResultType.instruction){
//                var1 = "(" + result1.instrRef + ")";
//            } else{
//                var1 = this.variableName + "_" + s1.toString();
//            }
//            if(rightRepresentedByInstrId){
//                var2 = "(" + this.getRightResult().instrRef + ")";
//            } else if(result2!=null&&result2.type==Result.ResultType.constant){
//                var2 = Integer.toString(result2.value);
//            } else if(result2!=null&&result2.type==Result.ResultType.instruction){
//                var2 = "(" + result2.instrRef + ")";
//            } else{
//                var2 = this.variableName + "_" + s2.toString();
//            }
//
//            sb.append(variableName + "_" + instructionPC + " " + var1 + " " + var2);
//        }
//        if (deleted) {
//            sb.append("(deleted)");
//        }
//        return sb.toString();
//    }


}
