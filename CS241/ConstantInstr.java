package CS241;

public class ConstantInstr extends Instruction {

    public String type;


    public ConstantInstr(int id, Block block, Value value) {
        super(id, block);
        this.type = "Const";
        this.left = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(this.id + ": ");
        sb.append(type  + " ");
        sb.append("#" + left.value);
        return sb.toString();
    }

}
