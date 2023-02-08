//package CS241;
//
//import CS241.Grammer.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class SSA {
//    private SymbolTable symbolTable;
//    private Computation computation;
//    private Program program;
//
//
//    public SSA(HashMap<Integer, Integer> table, Computation comp) {
//        symbolTable = table;
//        computation = comp;
//        program = new Program();
//    }
//
//
//    private void processStatements(Context ctx, ArrayList<Statement> statements) {
//        for (Statement stmt : statements) {
//            if (stmt instanceof Assignment) {
//                Assignment assignment = (Assignment) stmt;
//
//                processAssignment(ctx, assignment);
//
//            }else if (stmt instanceof FunctionCall) {
//                FunctionCall functionCall = (FunctionCall) stmt;
//
//                processFunctionCall(ctx, functionCall);
//
//            }else if (stmt instanceof IfStatement) {
//                IfStatement ifStatement = (IfStatement) stmt;
//
//                processIfStatement(ctx, ifStatement);
//
//            }else if (stmt instanceof WhileStatement) {
//                WhileStatement whileStatement = (WhileStatement) stmt;
//
//                processWhileStatement(ctx, whileStatement);
//
//            }else if (stmt instanceof ReturnStatement) {
//                ReturnStatement returnStatement = (ReturnStatement) stmt;
//
//                processReturnStatement(ctx, returnStatement);
//            }
//        }
//    }
//
//
//    private void processAssignment(Context ctx, Assignment assignment) {
//        Designator desig = assignment.designator;
//        Expression expr = assignment.expression;
//
//        int id = desig.id;
//
//        String name = symbolTable.IDToName(id);
//
//        Value exprValue = processExpression(ctx, expr);
//
//        if (canTreatAsLocalVariable(ctx, id)) {
//            Instruction instr = ctx.currentBlock.addInstruction();
//            instr.setOperator(Instruction.Operator.move);
//            instr.setArgX(exprValue);
//            instr.setArgY(new VariableValue(name, id, instr.getNumber()));
//
//            ctx.table.getVersions().put(id, instr.getNumber());
//
//        }else if (symbolTable.isVariable(id)) {
//            Instruction instr = ctx.currentBlock.addInstruction();
//            instr.setOperator(Instruction.Operator.store);
//            instr.setArgX(exprValue);
//            instr.setArgY(new MemoryAddressValue(name, id));
//
//            ctx.table.getVersions().put(id, instr.getNumber());
//
//        }else{
//            Value offsetValue = computeOffset(ctx, desig);
//
//            Instruction addaInstr = ctx.currentBlock.addInstruction();
//            addaInstr.setOperator(Instruction.Operator.adda);
//            addaInstr.setArgX(new MemoryAddressValue(name, id));
//            addaInstr.setArgY(offsetValue);
//
//            Instruction storeInstr = ctx.currentBlock.addInstruction();
//            storeInstr.setOperator(Instruction.Operator.store);
//            storeInstr.setArgX(exprValue);
//            storeInstr.setArgY(addaInstr.getResult());
//        }
//    }
//
//
//
//
//
//}
