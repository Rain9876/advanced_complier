package CS241;
import CS241.Grammer.Computation;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class test {
    public static void main(String[] args) throws IOException {
        String currentWorkingDir = System.getProperty("user.dir");
        String test = currentWorkingDir + "/src/CS241/TestCase/007.txt";
        Parser p = new Parser(test);
        Computation comp = p.computation();

        System.out.println(VariableTable.identifer_table.toString());

        IRGenerator ir = new IRGenerator(comp);
        ir.ProcessComputation();
        System.out.println(ir.pgm);

        System.out.println("====================================================");
        System.out.println("The SSA variable table of each block: ");
        for(Block i : ir.pgm.blocks) {
            System.out.println(i.showVariInstrRefTable());
        }


//         Q1, cmp (1)
        // Q2, subexpression DLX execute， when DLX execute, is individual project include DLX
        // Q3, variable scope?
        // Q4, if only then block, then phi function? Will joint block empty or not?




//        System.out.println(ir.CG.ExpressionBuff.size());
//        DLX dlx = new DLX();
//        int[] intArray = new int[ir.CG.ExpressionBuff.size()];
//        for (int i = 0; i < intArray.length; i++) {
//            intArray[i] = ir.CG.ExpressionBuff.get(i);
//            System.out.println(DLX.disassemble(intArray[i]));
//        }
//
//        DLX.load(intArray);
//        DLX.execute();

    }
}
