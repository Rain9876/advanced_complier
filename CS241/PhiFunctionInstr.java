package CS241;

import java.util.HashMap;

public class PhiFunctionInstr extends Instruction{
    public Instruction left;
    public Instruction right;
    public int vari_id;
//    private HashMap<Integer, Instruction> phiInstructionMap;

    public PhiFunctionInstr(int id, Block block, int vari_id){
        super(id, block);
        this.vari_id = vari_id;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(id + ": ");
        sb.append("Phi ");
        if (left != null) {
            sb.append("(" +left.id+ ") ");
        }
        if (right != null) {
            sb.append("(" +right.id+ ")");
        }
        return sb.toString();
    }




// createPhiInIfJoinBlocks
// updateReferenceForPhiVarInIfJoinBlock
// createPhiInWhileJoinBlocks
// updateReferenceForPhiVarInLoopBody

}





