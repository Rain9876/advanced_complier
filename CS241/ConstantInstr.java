package CS241;

public class ConstantInstr extends Instruction {

    public String type;
    public int value;


    public ConstantInstr(int id, Block block, int val) {
        super(id, block);
        this.type = "Const";
        this.value = val;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(this.id + ": ");
        sb.append(type  + " ");
        sb.append("#" + value);
        return sb.toString();
    }

}
