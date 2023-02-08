package CS241;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Program {
    public Parser par;
    public int block_pc;
    public int instr_pc;
    public ArrayList<Block> blocks;
    public HashMap<Integer, Instruction> instructions;
    public HashMap<Integer, Integer> dominators;
    public HashMap<Integer, ArrayList<Integer>> dominees;
    public HashMap<Instruction.Type, ArrayList<Instruction>> CSE;
//    public VariableTable vari_table;


    public Program() {
        instructions = new HashMap<>();
        blocks = new ArrayList<>();
        dominators = new HashMap<>();
        dominees = new HashMap<>();
        CSE = new HashMap<>();
        block_pc = 0;
        instr_pc = 1;

        add_block(new Block(this, Block.Type.normal));  // Block constant
    }

    public void add_block(Block block) {
        block.pgm = this;
        blocks.add(block);
        block_pc += 1;
    }

    public Block cur_block() {
        return blocks.get(block_pc);
    }


    public Instruction add_instr_cur_block(){
        Instruction tmp = new Instruction(instr_pc++, cur_block());
        cur_block().add_instr(tmp);
        tmp.setBlock(cur_block());
        return tmp;
    }

    public Instruction add_instr_block(int id){
        Instruction tmp = new Instruction(instr_pc++, cur_block());
        cur_block().add_instr(tmp);
        tmp.setBlock(cur_block());
        return tmp;
    }


    public Instruction generateInstr(Instruction.Type type,  Value x, Value y){
        Instruction instr = new Instruction(instr_pc++, cur_block(), type, (x==null)?null:x.deepclone(), (y ==null) ? null : y.deepclone());
        cur_block().add_instr(instr);
        instr.setBlock(cur_block());
        return instr;
    }

    public Instruction generateInstr(Instruction.Type type,  Value x){
        Instruction instr = new Instruction(instr_pc++, cur_block(), type, (x==null)?null:x.deepclone(),null);
        cur_block().add_instr(instr);
        instr.setBlock(cur_block());
        return instr;
    }

    public Instruction generateInstr(Instruction.Type type){
        Instruction instr = new Instruction(instr_pc++, cur_block(), type, null,null);
        cur_block().add_instr(instr);
        instr.setBlock(cur_block());
        return instr;
    }

    public Instruction generateConstantInstr(int val){
        Instruction instr = new ConstantInstr(instr_pc++, this.blocks.get(0), val);
        this.blocks.get(0).add_instr(instr);
        return instr;
    }

    public Instruction generateConstantInstr(Value val){
        Instruction instr = new ConstantInstr(instr_pc++, this.blocks.get(0), val.value);
        this.blocks.get(0).add_instr(instr);
        return instr;
    }

    public Instruction findConstantInstr(int val){
        for (Instruction i: this.blocks.get(0).instr_list){
            if (((ConstantInstr)i).value == val){
                return ((ConstantInstr)i);
            }
        }
        return  null;
    }


    public Instruction isEliminable(Instruction instr){
        Instruction.Type op_type = instr.type;
        if (!this.CSE.containsKey(op_type)) {
            ArrayList<Instruction> instr_list = new ArrayList<>();
            this.CSE.put(op_type, instr_list);
            return null;
        }
        ArrayList<Instruction> instr_cse = this.CSE.get(op_type);
        if (instr_cse.size() > 0) {
            for (Instruction i : instr_cse) {
                if ((i.left.instrRef.id == instr.left.instrRef.id) &&
                        (i.right.instrRef.id == instr.right.instrRef.id)) {
                    this.instr_pc--;
                    return i;
                }
            }
        }
        return null;
    }


    public void addIntoCSE(Instruction instr){
        Instruction.Type op_type = instr.type;
        if (this.CSE.containsKey(op_type)){
            this.CSE.get(op_type).add(instr);   // Clone instruction ?
        }else{
            ArrayList<Instruction> instr_list = new ArrayList<>();
            instr_list.add(instr);
            this.CSE.put(op_type, instr_list);
        }
    }


    public Instruction processCSE(Instruction instr, Block b){
        Instruction.Type op_type = instr.type;
        if (!this.CSE.containsKey(op_type)) {
            ArrayList<Instruction> instr_list = new ArrayList<>();
            instr_list.add(instr);
            this.CSE.put(op_type, instr_list);
            b.add_instr(instr);
            return instr;
        }

        ArrayList<Instruction> instr_list = this.CSE.get(op_type);
        for (Instruction i : instr_list) {
            if ((i.left.instrRef.id == instr.left.instrRef.id) &&
                    (i.right.instrRef.id == instr.right.instrRef.id)) {
                this.instr_pc--;
                return i;
            }
        }
        instr_list.add(instr);
        b.add_instr(instr);
        return instr;
    }


    public String toString() {
//        return super.toString();
        StringBuilder sb = new StringBuilder("");
        sb.append("Program: \n");
        for (Block b : blocks) {
            sb.append(b.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
