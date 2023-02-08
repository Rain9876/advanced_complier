package CS241;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Block {


    public enum Type{
        normal, if_then, if_else, if_join, while_join, while_do
    }

    public Program pgm;
    public int BlockID;
    public int size;
    public int depth;
    public Block preBlock;
    public Block followBlock;
    public ArrayList<Instruction> instr_list;
    public HashMap<Integer, Instruction> VariInstrRefTable; // Store all variable
    public HashMap<Integer, PhiFunctionInstr> PhiInstrRefTable;
//    public HashMap<Integer, ArrayList<Instruction>> renamePhiVariWhile;
    public ArrayList<Integer> PhiVar;
    public Type BlockType;

//    private Block ifThenBlock;
//    private Block ifFollowBlock;
//    private Block whileDoBlock;
//    private Block whileFollowBlock;
//    private Block joinBlock;

    public Block(Type type){
        this.VariInstrRefTable = new HashMap<>();
        this.BlockType = type;
        if ((BlockType == Type.if_join) || (BlockType == Type.while_join)){
            PhiInstrRefTable = new HashMap<>();
            PhiVar = new ArrayList<>();
        }
//        if (BlockType == Type.while_join){
//            renamePhiVariWhile = new HashMap<>();
//        }
    }

    public Block(Program pgm, Type type) {
        this(type);
        this.pgm = pgm;
        this.BlockID = pgm.block_pc;
        this.instr_list = new ArrayList<>();
    }


    public void add_instr(Instruction instr){
        instr.block = this;
        instr_list.add(instr);
        size += 1;
    }

    public void clone_VariableVersionTable(HashMap<Integer, Instruction> table) {
        this.VariInstrRefTable.putAll(table);
    }

//    public Integer id_from_VariableVersionTable(Instruction instr) {
//        Integer key = null;
//        for (Map.Entry<Integer, Instruction> entry : this.VariInstrRefTable.entrySet()) {
//            if (entry.getValue().equals(instr)) {
//                key = entry.getKey();
//                return key;
//            }
//        }
//        return key;
//    }

    public Instruction generateInstr(Instruction.Type type,  Value x, Value y){
        Instruction instr = new Instruction(pgm.instr_pc++, this, type, (x==null)?null:x.deepclone(), (y ==null) ? null : y.deepclone());
        this.add_instr(instr);
        return instr;
    }

    public Instruction generateSubExpressInstr(Instruction.Type type,  Value x, Value y){
        Instruction instr = new Instruction(pgm.instr_pc++, this, type, (x==null)?null:x.deepclone(), (y ==null) ? null : y.deepclone());
        return pgm.processCSE(instr, this);
    }

    public Instruction generateInstr(Instruction.Type type,  Value x){
        Instruction instr = new Instruction(pgm.instr_pc++, this, type, (x==null)?null:x.deepclone(),null);
        this.add_instr(instr);
        return instr;
    }

    public Instruction generateInstr(Instruction.Type type) {
        Instruction instr = new Instruction(pgm.instr_pc++, this, type, null, null);
        this.add_instr(instr);
        return instr;
    }


    public Instruction generatePhiFunction(int val_id, Instruction instr,  boolean left){
        PhiFunctionInstr phi_instr;
        if (PhiInstrRefTable.containsKey(val_id)) {
            phi_instr = PhiInstrRefTable.get(val_id);
            if (left) {
                phi_instr.left = instr;
            }else{
                phi_instr.right = instr;
            }
        }else{
            phi_instr = new PhiFunctionInstr(pgm.instr_pc++, this, val_id);
            if (left) {
                phi_instr.left = instr;
            }else{
                phi_instr.right = instr;
            }
            // Phi function at the top of join block
            this.instr_list.add(PhiInstrRefTable.size(), phi_instr);
            PhiInstrRefTable.put(val_id, phi_instr);
        }
        return phi_instr;
    }


//    public void rename_Phi_vari_instr(int id, Instruction instr){
//        if (renamePhiVariWhile.containsKey(id)){
//
//        }
//    }
//
//    public void add_renamePhiVariWhile(Value v){
//        if (v.type == Value.Type.variable){
//            if (this.renamePhiVariWhile.containsKey(v.varIdent)){
//                this.renamePhiVariWhile.get(v.varIdent).add(v.instrRef);
//            }else {
//                this.renamePhiVariWhile.put(v.varIdent, new ArrayList<>() {{
//                    add(v.instrRef);
//                }});
//            }
//        }
//    }


//    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        String suffix = (BlockType == Type.if_join)? "join ": "";
        sb.append(suffix + "BB" + BlockID + ": [\n");
        for (Instruction instr : instr_list) {
            sb.append(instr.toString());
            sb.append("\n");
            }
        sb.append("]");
        return sb.toString();
    }

    public String showVariInstrRefTable() {
        StringBuilder sb = new StringBuilder("");
        sb.append("BB" + BlockID + " vari table: {\n");
        for (Integer i : VariInstrRefTable.keySet()) {
            int instr_id = VariInstrRefTable.get(i).id;
            String name = VariableTable.Id2String(i);
            sb.append(name + ": ("+ instr_id + ")");
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }


}
