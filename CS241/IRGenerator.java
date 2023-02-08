package CS241;
import CS241.DLX.CodeGenerator;
import CS241.Grammer.*;

import java.util.ArrayList;
import java.util.HashMap;


public class IRGenerator {
    public Computation comp;
    public Program pgm;
    public CodeGenerator CG;

    public IRGenerator(Computation comp) {
        this.comp = comp;
        this.pgm = new Program();
        this.CG = new CodeGenerator();
    }


    public void ProcessComputation() {
        Block startBlock = new Block(pgm, Block.Type.normal);
        pgm.blocks.add(startBlock);

        for (VariableDeclaration var : comp.variables) {
            processVarDecl(var, pgm.cur_block());
        }

        for (FunctionDeclaration func : comp.functions) {
            processFunctionDec(func, pgm.cur_block());
        }

        for (Statement stmt : comp.statements) {
            processStatement(stmt, pgm.cur_block());
        }

        pgm.generateInstr(Instruction.Type.end);
    }


    public void processVarDecl(VariableDeclaration var, Block block) {
        for (int i : var.nameIDs) {
            Value x = new Value(Value.Type.variable, i);

//            Instruction index = pgm.findConstantInstr(0);
//            Value y;
//            if (index == null) {
//                y = new Value(Value.Type.instruction, pgm.instr_pc);
//                Instruction y_instr = pgm.generateConstantInstr(0);
//            }else{
//                y = new Value(Value.Type.instruction, index);
//            }
//            pgm.generateInstr(Instruction.Type.store, y, x);

            System.out.println("Warning: Variables are not initialized");
        }

    }

    public void processFunctionDec(FunctionDeclaration functions, Block block) {

    }


    private void processStatement(Statement stmt, Block block) {

        if (stmt instanceof Assignment) {
            Assignment assignment = (Assignment) stmt;
            processAssignment(assignment, block);

        } else if (stmt instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) stmt;
            processFunctionCall(functionCall, block);

        } else if (stmt instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) stmt;

            processIfStatement(ifStatement, block);

        } else if (stmt instanceof WhileStatement) {
            WhileStatement whileStatement = (WhileStatement) stmt;

            processWhileStatement(whileStatement, block);

        } else if (stmt instanceof ReturnStatement) {
            ReturnStatement returnStatement = (ReturnStatement) stmt;

            processReturnStatement(returnStatement, block);
        }
    }


    private void processAssignment(Assignment assignment, Block block) {
        Designator desig = assignment.designator;
        Expression expr = assignment.expression;
        int id = desig.id;
//        String name = sym_table.IDToName(id);

//        if(joinBlocks!=null&&joinBlocks.size()>0){
//            if(!variable.isArrayDesignator)
//                joinBlocks.peek().createPhiFunction(variable.varIdent);
//        }
        Value exprValue = processExpression(expr, block);
        // Assert a variable not exists
//        Value vari = new Value(Value.Type.variable, id);

        block.VariInstrRefTable.put(id, exprValue.instrRef);


        VariableTable.VariMemoryAddress.put(id, exprValue);
//        Instruction instr = block.generateInstr(Instruction.Type.store, exprValue, vari);
//        pgm.vari_version_table.put(id, instr.id);

        if ((block.BlockType == Block.Type.if_then) && (block.followBlock.BlockType == Block.Type.if_join)){
            block.followBlock.generatePhiFunction(id, exprValue.clone().instrRef, true);
        }else if ((block.BlockType == Block.Type.if_else) && (block.followBlock.BlockType == Block.Type.if_join)){
            block.followBlock.generatePhiFunction(id, exprValue.clone().instrRef, false);
        }

        if ((block.BlockType == Block.Type.while_do) && block.preBlock.BlockType == Block.Type.while_join){
            Instruction temp = block.preBlock.VariInstrRefTable.get(id);
            block.preBlock.generatePhiFunction(id, temp, true);
            Instruction inst = block.preBlock.generatePhiFunction(id, exprValue.clone().instrRef, false);
//            renamePhiVari(id, inst);  // after all phi is created!
            block.preBlock.VariInstrRefTable.put(id, inst);

        }

    }

    public Value processFunctionCall(FunctionCall function, Block block) {
        String name = VariableTable.Id2String(function.funcID);
        Value val = null;
        if (!name.equals("")){
            if (name.equals("OutputNum")){
                if (function.paramExpressions.size() == 1){
                        Value param = processExpression(function.paramExpressions.get(0), block);
//                        if (param.type == Value.Type.constant){  // unknown
                            block.generateInstr(Instruction.Type.write, param);
//                        }
                }
                return null;
            }else if (name.equals("InputNum")) {
                Instruction instr = null;
                Value num = new Value(Value.Type.constant, 0);
                load(num, block);
                num.regno = CG.AllocateReg();
                this.CG.putF2(0, num.regno, 0,0);                   // load Register 0 to num
                num.type = Value.Type.register;
                this.CG.putF2(50, num.regno, 0,0);                  // read
                if (function.paramExpressions.size() == 0) {
                    instr = block.generateInstr(Instruction.Type.read);
                }
                num.instrRef = instr;
                return num;
            }else if (name.equals("OutputNewLine")) {
                if (function.paramExpressions.size() == 0)
                    block.generateInstr(Instruction.Type.writeNL);
                return null;
            }else{
                return null;
            }
        }
        return val;
    }

    private Value processRelation(Relation rel, Block b) {

        Value exprLeft = processExpression(rel.left, b);                  // DLX? execute? How?

        Value exprRight = processExpression(rel.right, b);

        Instruction instr = b.generateInstr(Instruction.Type.cmp, exprLeft, exprRight);

        if (b.BlockType == Block.Type.while_join){
            b.add_renamePhiVariWhile(exprLeft);
            b.add_renamePhiVariWhile(exprRight);
        }

        Value relation = new Value();
        relation.relOp = rel.op;
        relation.type = Value.Type.condition;
        relation.instrRef = instr;

        return relation;

    }

    private void processIfStatement(IfStatement ifStmt, Block block) {
        Value relation = processRelation(ifStmt.condi, block);
        Value rel_branch = new Value(Value.Type.branch, 0);     // fall through, 0 is to be linked
        Instruction rel_instr = block.generateInstr(Instruction.RelationMap(relation.relOp), relation, rel_branch);

        Block joinBlock = new Block(pgm, Block.Type.if_join);
        joinBlock.clone_VariableVersionTable(block.VariInstrRefTable);

        Block thenBlock = new Block(pgm, Block.Type.if_then);
        thenBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        thenBlock.preBlock = block;
        thenBlock.followBlock = joinBlock;

        for (Statement stmt : ifStmt.thenStmts)
            processStatement(stmt, thenBlock);

        if (thenBlock.instr_list.size() == 0){
            thenBlock.generateInstr(null);
        }

        Value bra_branch = new Value(Value.Type.branch, 0);    // fall through, 0 is to be linked
        Instruction bra_instr = thenBlock.generateInstr(Instruction.Type.bra,  bra_branch);

        Block elseBlock = new Block(pgm, Block.Type.if_else);
        elseBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        elseBlock.preBlock = block;
        elseBlock.followBlock = joinBlock;

        rel_instr.right.branchBlock = elseBlock;
        if (ifStmt.elseStmts.size() > 0) {
            for (Statement stmt : ifStmt.elseStmts)
                processStatement(stmt, elseBlock);
        }

        if (elseBlock.instr_list.size() == 0){
            elseBlock.generateInstr(null);  // create empty SSA
        }


        for(int vari_id: joinBlock.PhiInstrRefTable.keySet()){
            joinBlock.PhiInstrRefTable.get(vari_id).left = thenBlock.VariInstrRefTable.get(vari_id);
            joinBlock.PhiInstrRefTable.get(vari_id).right = elseBlock.VariInstrRefTable.get(vari_id);
//            joinBlock.PhiInstrRefTable.get(vari_id).id = pgm.instr_pc++;
            joinBlock.VariInstrRefTable.put(vari_id, joinBlock.PhiInstrRefTable.get(vari_id));
            VariableTable.VariMemoryAddress.get(vari_id).instrRef = joinBlock.PhiInstrRefTable.get(vari_id);
        }

//        joinBlock.VariInstrRefTable.put(joinBlock.PhiInstrRefTable);
//        joinBlock.clone_VariableVersionTable(joinBlock.PhiInstrRefTable);

        pgm.add_block(thenBlock);
        thenBlock.BlockID = pgm.block_pc;

        pgm.add_block(elseBlock);
        elseBlock.BlockID = pgm.block_pc;

        pgm.add_block(joinBlock);
        joinBlock.BlockID = pgm.block_pc;
        bra_instr.left.branchBlock = joinBlock;
    }

    private void processWhileStatement(WhileStatement whileStmt, Block block) {
//        HashMap<Integer, Integer> ssa_version = pgm.cur_block().VariableVersionTable_clone();

        Block joinBlock = new Block(Block.Type.while_join);
        joinBlock.clone_VariableVersionTable(block.VariInstrRefTable);

        Block doBlock = new Block(Block.Type.while_do);
        doBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        doBlock.preBlock = joinBlock;

        Block nextBlock = new Block(Block.Type.normal);
        nextBlock.preBlock = joinBlock;

        Value relation = processRelation(whileStmt.condi, joinBlock);
        Value rel_branch = new Value(Value.Type.branch, 0);
        joinBlock.generateInstr(Instruction.RelationMap(relation.relOp), rel_branch);


        for (Statement stmt : whileStmt.body) {
            processStatement(stmt, doBlock);
        }

        if (doBlock.instr_list.size() == 0){
            doBlock.generateInstr(null);  // create empty SSA
        }

        Value bra_branch = new Value(Value.Type.branch, 0);

        doBlock.generateInstr(Instruction.Type.bra, bra_branch);

//        updateReferenceForPhiVarInLoopBody(joinBlock, doBlock);
//        updateReferenceForPhiVarInJoinBlock(joinBlock);

        // end do
        Block followBlock = new Block(Block.Type.normal);
        pgm.add_block(followBlock);
    }


    private void processReturnStatement(ReturnStatement retStmt, Block b){
        Expression returnExpr = retStmt.expression;
        Value returnValue = (returnExpr != null) ? processExpression(returnExpr, b) : null;
    }


    private Value processExpression( Expression expr, Block b) {
        Value result = processTerm(expr.terms.get(0), b).deepclone();

        for (int i = 1; i < expr.terms.size(); i++) {
            Value termVal = processTerm(expr.terms.get(i), b);
            Expression.Operator exprOp = expr.operators.get(i - 1);
            Instruction instr = b.generateSubExpressInstr(Instruction.ExprOperatorMap(exprOp), result, termVal);

            compute(Instruction.ExprOperatorMap(exprOp),result, termVal, b);

//            if (result.type == Value.Type.register){

            result.instrRef = instr;
//            }

//            if (b.BlockType== Block.Type.while_do){
//                b.preBlock.add_renamePhiVariWhile(result);
//                b.preBlock.add_renamePhiVariWhile(termVal);
//            }
        }

        return result;
    }

    private Value processTerm(Term term, Block b) {
        Value result = processFactor(term.factors.get(0), b);

        for (int i = 1; i < term.factors.size(); i++) {
            Value factorValue = processFactor(term.factors.get(i), b);

            Term.Operator termOp = term.operators.get(i - 1);

            Instruction instr = b.generateSubExpressInstr(Instruction.TermOperatorMap(termOp), result, factorValue);

            compute(Instruction.TermOperatorMap(termOp), result, factorValue, b);

//            if (result.type == Value.Type.register){
            result.instrRef = instr;
//            }
//            if (b.BlockType== Block.Type.while_do){
//                b.preBlock.add_renamePhiVariWhile(result);
//                b.preBlock.add_renamePhiVariWhile(factorValue);
//            }
        }

        return result;
    }

    private Value processFactor(Factor factor, Block b){
        if (factor instanceof Designator) {
            int var_id = ((Designator)(factor)).id;
            Value x;
            if (VariableTable.VariMemoryAddress.containsKey(var_id)){
                x = VariableTable.lookupAddress(var_id);
            }else{
                x = new Value(Value.Type.variable, var_id);
                VariableTable.VariMemoryAddress.put(var_id, x);
            }
            return x;
        }else if (factor instanceof Constant){
            int val = ((Constant)(factor)).value;
            Instruction constInstr = pgm.findConstantInstr(val);
            Value num = new Value(Value.Type.constant, val);
            if (constInstr == null) {
                Instruction y_instr = pgm.generateConstantInstr(num);
                num.instrRef = y_instr;
            }else{
                num.instrRef = constInstr;
            }
            return num;
        }else if (factor instanceof Expression){
            Expression expr = (Expression)(factor);
            return processExpression(expr, b);

        }else if (factor instanceof FunctionCall) {
            FunctionCall call = (FunctionCall)(factor);
            return processFunctionCall(call, b);
        }else{
            return null;
        }
    }





    public void load(Value x, Block b) {
        if (x.type == Value.Type.variable) {    // if it is a variable, then load from x.address, what is x.address
            x.regno = CG.AllocateReg();
            CG.putF1(32, x.regno, 0,x.varIdent);
//            b.generateInstr(Instruction.Type.load, x);
            x.type = Value.Type.register;
        } else if (x.type == Value.Type.constant) {
            if (x.value == 0) x.regno = 0;
            else {
                x.regno = CG.AllocateReg();
                CG.putF1(16, x.regno, 0, x.value);
            }
            x.type = Value.Type.register;
        }
    }


    public void compute(Instruction.Type type, Value x, Value y, Block b) {
        if (x.type == Value.Type.constant && y.type == Value.Type.constant) {
            switch (type) {
                case add:
                    x.value = x.value + y.value;
                    break;
                case sub:
                    x.value = x.value - y.value;
                    break;
                case mul:
                    x.value = x.value * y.value;
                    break;
                case div:
                    x.value = x.value / y.value;
                    break;
            }
        } else {
            load(x, b);

            if (x.regno == 0) {
                x.regno = CG.AllocateReg();
                CG.putF2(0, x.regno, 0,0);
//                b.generateInstr(Instruction.Type.add, );
            }

            if (y.type == Value.Type.constant) {
                switch (type) {
                    case add:
                        CG.putF1(16, x.regno, x.regno, y.value);
                        break;
                    case sub:
                        CG.putF1(17, x.regno, x.regno, y.value);
                        break;
                    case mul:
                        CG.putF1(18, x.regno, x.regno, y.value);
                        break;
                    case div:
                        CG.putF1(19, x.regno, x.regno, y.value);
                        break;
                }
            } else {
                load(y, b);
                switch (type) {
                    case add:
                        CG.putF2(0, x.regno, x.regno, y.regno);
                        break;
                    case sub:
                        CG.putF2(1, x.regno, x.regno, y.regno);
                        break;
                    case mul:
                        CG.putF2(2, x.regno, x.regno, y.regno);
                        break;
                    case div:
                        CG.putF2(3, x.regno, x.regno, y.regno);
                        break;
                }
                if (x.regno != y.regno) {
                    CG.DeAllocate(y);
                }
            }
        }
    }
//
//    public Value computeRelation(Relation rel, Value x, Value y) {
//        if (x.type == Value.Type.constant && y.type == Value.Type.constant) {
//            int xc = x.value;
//            int yc = y.value ;
//            int rc;
//            if (xc > yc) {
//                rc = +1;
//            }else if (xc < yc) {
//                rc = -1;
//            }else{
//                rc = 0;
//            }
//            return new Value(Value.Type.constant, rc);
//        }else{
//
//            Instruction instr = pgm.generateInstr(Instruction.Type.cmp, x, y);
//
//            Value relation = new Value();
//            relation.relOp = rel.op;
//            relation.type = Value.Type.condition;
//            relation.instrRef = instr;
//
//            return relation;
//        }
//    }
}