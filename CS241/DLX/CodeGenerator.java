package CS241.DLX;

import CS241.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator {

    public int buff[];
    public int pc = 0;
    public int sp = 0; //register, set stack pointer, calculate the expression depth,

    public DLX dlx = new DLX();
    public boolean[] Register_in_use = new boolean[32];
    public ArrayList<Integer> ExpressionBuff;
    public ArrayList<Integer> functionStack;
    public ArrayList<String> CodeExpression;

    public HashMap<Integer, Integer> sym_table = new HashMap<>();  // ident id and offset address
    // deposit

    public CodeGenerator() {
        Register_in_use[0] = true;
        for (int i = 0; i < 32; i++) {
            Register_in_use[i] = false;
        }
        ExpressionBuff = new ArrayList<>();
        CodeExpression = new ArrayList<>();
    }


    public void putF1(int op, int a, int b, int c) {
        ExpressionBuff.add(DLX.F1(op,a, b,c));
        CodeExpression.add(DLX.mnemo[op] + " R" + b + " -> R" + a + " & " + c);
    }

    public void putF2(int op,  int a, int b, int c) {
        ExpressionBuff.add(DLX.F2(op, a, b, c));
        CodeExpression.add(DLX.mnemo[op] + " R" + a + " R" + b +" R" + c);

    }

    public void putF3(int op, String opt, int c) {
        ExpressionBuff.add(DLX.F3(op, c));
        CodeExpression.add(DLX.mnemo[op] + " R" + c);
    }


    public int AllocateReg() {
        // find a register free,
        for (int i = 1; i < 32; i++) {
            if (!Register_in_use[i]) {
                Register_in_use[i] = true;
                return i;
            }
        }
        return 0;
    }

    public void DeAllocate(Value a) {
        Register_in_use[a.regno] = false;
        a.regno=0;
        a.type = a.varIdent > 0? Value.Type.constant: Value.Type.variable;
        // free the variable memory
    }


    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (String i : CodeExpression){
            sb.append(i + "\n");
        }
        return sb.toString();
    }
}
