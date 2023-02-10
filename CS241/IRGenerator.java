package CS241;
import CS241.DLX.CodeGenerator;
import CS241.Grammer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


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
        Value exprValue = processExpression(expr, block).deepclone();
        exprValue.varIdent = id;
        exprValue.type = Value.Type.variable;

//        if (exprValue.type == Value.Type.constant) {
//            exprValue.varIdent = id;
//            exprValue.type = Value.Type.variable;
//        }
        // Assert a variable not exists
//        Value vari = new Value(Value.Type.variable, id);

        block.VariInstrRefTable.put(id, exprValue.instrRef);
        VariableTable.VariMemoryAddress.put(id, exprValue);

//        Instruction instr = block.generateInstr(Instruction.Type.store, exprValue, vari);
//        pgm.vari_version_table.put(id, instr.id);

        if ((block.BlockType == Block.Type.if_then) && (block.followBlock.BlockType == Block.Type.if_join)){
            block.followBlock.generatePhiFunction(id, exprValue.clone().instrRef, true);  // generate left Phi
        }else if ((block.BlockType == Block.Type.if_else) && (block.followBlock.BlockType == Block.Type.if_join)){
            block.followBlock.generatePhiFunction(id, exprValue.clone().instrRef, false); // generate right Phi
        }

        if ((block.BlockType == Block.Type.while_do) && block.preBlock.BlockType == Block.Type.while_join){
            Block join_block = block.preBlock;
            Instruction temp = join_block.VariInstrRefTable.get(id);
            join_block.generatePhiFunction(id, temp, true);
            Instruction phi_inst = join_block.generatePhiFunction(id, exprValue.clone().instrRef, false);
//            renamePhiVari(id, inst);  // after all phi is created!
            join_block.PhiVar.add(id);
            join_block.VariInstrRefTable.put(id, phi_inst);

            for(Instruction instr: join_block.instr_list){
                if (!(instr instanceof PhiFunctionInstr)){
                    if (instr.left != null && join_block.PhiVar.contains(instr.left.varIdent))
                        instr.left.instrRef = join_block.VariInstrRefTable.get(instr.left.varIdent);   // whether need to clone instr!
                    if (instr.right != null && join_block.PhiVar.contains(instr.right.varIdent))
                        instr.right.instrRef = join_block.VariInstrRefTable.get(instr.right.varIdent);   // whether need to clone instr!
//                    Instruction new_instr_link = pgm.processCSEinWhile(instr, block);
                }
            }

             // follows the order: FIFO, first assignment, first in PhiVar,

//            for (int assigned_vari: joinBlock.PhiVar) {
            for (Instruction instr : block.instr_list) {
                if ((instr.left != null &&  id == instr.left.varIdent )||
                        (instr.right != null && id == instr.right.varIdent)) {
                    if (instr.left != null &&  id == instr.left.varIdent )
                        instr.left.instrRef = join_block.VariInstrRefTable.get(instr.left.varIdent);   // whether need to clone instr!
                    if (instr.right != null && id == instr.right.varIdent)
                        instr.right.instrRef = join_block.VariInstrRefTable.get(instr.right.varIdent);   // whether need to clone instr!
//                    Instruction new_instr_link = pgm.processCSEinWhile(instr, block);
//                    int var_id = doBlock.id_from_VariableVersionTable(instr);
//                    doBlock.VariInstrRefTable.put(var_id, new_instr_link);
//                    System.out.println("");
//                exprValue.instrRef = new_instr_link;
//                block.VariInstrRefTable.put(id, exprValue.instrRef);
//                VariableTable.VariMemoryAddress.put(id, exprValue);
                }
            }
//            }

//            for (Instruction instr : block.instr_list){
//                if (join_block.PhiVar.contains(instr.left.varIdent))
//                    instr.left.instrRef = join_block.VariInstrRefTable.get(instr.left.varIdent);   // whether need to clone instr!
//                if (join_block.PhiVar.contains(instr.right.varIdent))
//                    instr.right.instrRef = join_block.VariInstrRefTable.get(instr.right.varIdent);   // whether need to clone instr!
//                Instruction new_instr_link = pgm.processCSEinWhile(instr, block);
//
//                exprValue.instrRef = new_instr_link;
//
//                block.VariInstrRefTable.put(id, exprValue.instrRef);
//                VariableTable.VariMemoryAddress.put(id, exprValue);
//            }

        }


    }

    public Value processFunctionCall(FunctionCall function, Block block) {
        String name = VariableTable.Id2String(function.funcID);
        Value val = null;
        if (!name.equals("")){
            if (name.equals("OutputNum")){
                if (function.paramExpressions.size() == 1){
                        Value param = processExpression(function.paramExpressions.get(0), block);

                        ToEndOfDomineeBlock(block).generateInstr(Instruction.Type.write, param);
//                        if (param.type == Value.Type.constant){  // unknown
//                        if (block.BlockType == Block.Type.while_do || block.BlockType == Block.Type.if_then ) {

//                            Instruction bra_instr= null;
//                            if (block.dominees.size() != 0) {
//                                Block cur_block = block.dominees.get(0);
//
//                                while ((cur_block.BlockType != Block.Type.while_join) && (cur_block.dominees.size() != 0)) {
//                                    cur_block =  cur_block.dominees.get(cur_block.dominees.size()-1);
//                                }
//                                if (cur_block.BlockType == Block.Type.while_join) {
//                                    // while block always has its dominates, go to the follow
//                                    bra_instr = cur_block.dominees.get(cur_block.dominees.size() - 1).generateInstr(Instruction.Type.write, param);   // fall through, 0 is to be linked
//                                }else{ // means no more dominators; Direct add bra to here
//                                    bra_instr = cur_block.generateInstr(Instruction.Type.write, param);   // fall through, 0 is to be linked
//                                }
//                            }else{
//                                bra_instr = block.generateInstr(Instruction.Type.write, param);   // fall through, 0 is to be linked
//                            }
//
//                            Instruction bra_instr = null;
//                            Block cur_block = block;
//                            while ((cur_block.BlockType != Block.Type.while_join) &&
//                                    (cur_block.BlockType != Block.Type.if_join) &&
//                                    (cur_block.dominees.size() != 0)) {
//                                cur_block = cur_block.dominees.get(0);
//                            }
//                            if (cur_block.BlockType == Block.Type.while_join) {
//                                bra_instr = cur_block.dominees.get(cur_block.dominees.size() - 1).generateInstr(Instruction.Type.write, param);   // fall through, 0 is to be linked
//                            } else {
//                                bra_instr = cur_block.generateInstr(Instruction.Type.write, param);   // fall through, 0 is to be linke
//                            }
////                        }

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

//        if (b.BlockType == Block.Type.while_join){
//            b.add_renamePhiVariWhile(exprLeft);
//            b.add_renamePhiVariWhile(exprRight);
//        }

        Value relation = new Value();
        relation.relOp = rel.op;
        relation.type = Value.Type.condition;
        relation.instrRef = instr;

        return relation;

    }

    private void processIfStatement(IfStatement ifStmt, Block block) {

        Value relation = processRelation(ifStmt.condi, block);
        Instruction rel_branch_instr = block.generateInstr(Instruction.ComplementRelationMap(relation.relOp),
                                                    relation, new Value(Value.Type.branch, 0));  // fall through, 0 is to be linked

        Block joinBlock = new Block(pgm, Block.Type.if_join);
        joinBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        joinBlock.preBlock = block;
        block.dominees.add(joinBlock);

        Block thenBlock = new Block(pgm, Block.Type.if_then);
        thenBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        thenBlock.preBlock = block;
        thenBlock.followBlock = joinBlock;
        block.dominees.add(thenBlock);
        pgm.add_block(thenBlock, true);
        thenBlock.BlockID = pgm.block_pc;

        for (Statement stmt : ifStmt.thenStmts)
            processStatement(stmt, thenBlock);

        if(pgm.cur_block().instr_list.size() == 0){
            pgm.cur_block().generateInstr(null);
        }

        if (thenBlock.instr_list.size() == 0){
            thenBlock.generateInstr(null);
        }

        // if thenBlock constains joint block, pass bra instruction to nested join block
        // else

        Block bra_block = ToEndOfDomineeBlock(thenBlock);
        Instruction bra_instr;
        if (bra_block.instr_list.size() == 1) {
            bra_instr = replaceEmptyInstrChecker(bra_block, Instruction.Type.bra, new Value(Value.Type.branch, 0), null);
        }else{
            bra_instr = bra_block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));
        }


//        System.out.println("*********");
//        System.out.println(bra_block);
//        System.out.println(bra_block.instr_list.size());
//        if (bra_block.instr_list.size() == 2){
//            Instruction replace_check_instr = bra_block.instr_list.get(0);
//            System.out.println("*********");
//            System.out.println(replace_check_instr);
//            replaceEmptyInstrChecker(replace_check_instr, bra_instr);
//        }
//        Instruction bra_instr = null;
//        Block cur_block = thenBlock;
//        while ((cur_block.BlockType != Block.Type.while_join) &&
//                (cur_block.BlockType != Block.Type.if_join) &&
//                (cur_block.dominees.size()!=0)){
//            cur_block =  cur_block.dominees.get(0);
//        }
//
//        if (cur_block.BlockType == Block.Type.while_join) {
//            bra_instr = cur_block.dominees.get(cur_block.dominees.size() - 1).generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//        }else if (cur_block.BlockType == Block.Type.if_join){ // means no more dominators; Direct add bra to here
//            bra_instr = cur_block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//        }else{
//            bra_instr = cur_block.generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linke
//        }

//
//        if (thenBlock.dominees.size() != 0) {
//            Block cur_block = thenBlock.dominees.get(0);   // could be do-block, then_block or normal_block
//            while ((cur_block.BlockType != Block.Type.while_join) && (cur_block.dominees.size() != 0)) {
//                cur_block =  cur_block.dominees.get(cur_block.dominees.size()-1);
//            }
//            if (cur_block.BlockType == Block.Type.while_join) {
//                // while block always has its dominates, go to the follow
//                bra_instr = cur_block.dominees.get(cur_block.dominees.size() - 1).generateInstr(Instruction.Type.bra,
//                        new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }else{ // means no more dominators; Direct add bra to here
//                bra_instr = cur_block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }
//        }else{
//            bra_instr = thenBlock.generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//        }


//        Instruction bra_instr= null;
//        if (thenBlock.dominees.size() != 0) {
//            bra_instr = thenBlock.dominees.get(thenBlock.dominees.size()-1).generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
////            if (block.BlockType != Block.Type.normal && joinBlock.instr_list.size() == 0){
////                joinBlock.generateInstr(null);  // create empty SSA
////            }
//        }else{
//            bra_instr = thenBlock.generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//        }

        Block elseBlock = null;
        if (ifStmt.elseStmts.size() > 0) {
            elseBlock = new Block(pgm, Block.Type.if_else);
            elseBlock.clone_VariableVersionTable(block.VariInstrRefTable);
            elseBlock.preBlock = block;
            elseBlock.followBlock = joinBlock;
//            pgm.add_block(elseBlock);
//            elseBlock.BlockID = pgm.block_pc;
            block.dominees.add(elseBlock);
            pgm.add_block(elseBlock, true);
            elseBlock.BlockID = pgm.block_pc;

            rel_branch_instr.right.branchBlock = elseBlock;
            if (ifStmt.elseStmts.size() > 0) {
                for (Statement stmt : ifStmt.elseStmts)
                    processStatement(stmt, elseBlock);
            }

            if(pgm.cur_block().instr_list.size() == 0){
                pgm.cur_block().generateInstr(null);
            }

            if (elseBlock.instr_list.size() == 0) {
                elseBlock.generateInstr(null);  // create empty SSA
            }

//            Instruction bra_instr= null;
//            if (thenBlock.dominees.size() != 0) {
//                bra_instr = elseBlock.dominees.get(elseBlock.dominees.size()-1).generateInstr(Instruction.Type.bra,
//                        new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }else{
//                bra_instr = elseBlock.generateInstr(Instruction.Type.bra,
//                        new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }

        }else{
            rel_branch_instr.right.branchBlock = joinBlock;
        }

        for(int vari_id: joinBlock.PhiInstrRefTable.keySet()){
            joinBlock.PhiInstrRefTable.get(vari_id).left = thenBlock.VariInstrRefTable.get(vari_id);

            if (ifStmt.elseStmts.size() > 0) {
                joinBlock.PhiInstrRefTable.get(vari_id).right = elseBlock.VariInstrRefTable.get(vari_id);
            }else{
                joinBlock.PhiInstrRefTable.get(vari_id).right = joinBlock.VariInstrRefTable.get(vari_id);
            }
//            joinBlock.PhiInstrRefTable.get(vari_id).id = pgm.instr_pc++;
            joinBlock.VariInstrRefTable.put(vari_id, joinBlock.PhiInstrRefTable.get(vari_id));
            VariableTable.VariMemoryAddress.get(vari_id).instrRef = joinBlock.PhiInstrRefTable.get(vari_id);
        }

//        joinBlock.VariInstrRefTable.put(joinBlock.PhiInstrRefTable);
//        joinBlock.clone_VariableVersionTable(joinBlock.PhiInstrRefTable);


        // dominant
//        block.dominees.add(joinBlock);

//        pgm.add_block(thenBlock);
//        thenBlock.BlockID = pgm.block_pc;

        pgm.add_block(joinBlock, true);
        joinBlock.BlockID = pgm.block_pc;

//        pgm.add_block(joinBlock);
//        joinBlock.BlockID = pgm.block_pc;

        bra_instr.left.branchBlock = joinBlock;

//        if (elseBlock != null) {
//            rel_branch_instr.right.branchBlock = elseBlock;
//        }else{
//            rel_branch_instr.right.branchBlock = joinBlock;
//        }
//        if (block.BlockType != Block.Type.normal && joinBlock.BlockType == Block.Type.){
//
//        }

    }

    private void processWhileStatement(WhileStatement whileStmt, Block block) {

        Block joinBlock = new Block(pgm, Block.Type.while_join);
        joinBlock.clone_VariableVersionTable(block.VariInstrRefTable);
        block.dominees.add(joinBlock);

        if (block.instr_list.size() == 0){
            block.generateInstr(null);
//            block = joinBlock;
        }

        pgm.add_block(joinBlock, true);
        joinBlock.BlockID = pgm.block_pc;

        Block doBlock = new Block(pgm, Block.Type.while_do);
        doBlock.clone_VariableVersionTable(joinBlock.VariInstrRefTable);
        doBlock.preBlock = joinBlock;
        doBlock.followBlock = joinBlock;
        joinBlock.dominees.add(doBlock);
        pgm.add_block(doBlock, true);
        doBlock.BlockID = pgm.block_pc;


        Block nextBlock = new Block(pgm, Block.Type.normal);
        nextBlock.preBlock = joinBlock;
        joinBlock.dominees.add(nextBlock);

        Value relation = processRelation(whileStmt.condi, joinBlock);
        Instruction rel_branch_instr = joinBlock.generateInstr(Instruction.ComplementRelationMap(relation.relOp),
                                                                relation, new Value(Value.Type.branch, 0));

        for (Statement stmt : whileStmt.body) {
            processStatement(stmt, doBlock);
        }

        if(pgm.cur_block().size == 0){
            pgm.cur_block().generateInstr(null);
        }

        if (doBlock.instr_list.size() == 0){
            doBlock.generateInstr(null);
        }

//        // follows the order: FIFO, first assignment, first in PhiVar,
//        for (int assigned_vari: joinBlock.PhiVar) {
//            for (Instruction instr : doBlock.instr_list) {
//                if ((instr.left != null &&  assigned_vari == instr.left.varIdent )||
//                        (instr.right != null && assigned_vari == instr.right.varIdent)) {
//                    if (instr.left != null &&  assigned_vari == instr.left.varIdent )
//                        instr.left.instrRef = joinBlock.VariInstrRefTable.get(instr.left.varIdent);   // whether need to clone instr!
//                    if (instr.right != null && assigned_vari == instr.right.varIdent)
//                        instr.right.instrRef = joinBlock.VariInstrRefTable.get(instr.right.varIdent);   // whether need to clone instr!
////                    Instruction new_instr_link = pgm.processCSEinWhile(instr, block);
////                    int var_id = doBlock.id_from_VariableVersionTable(instr);
////                    doBlock.VariInstrRefTable.put(var_id, new_instr_link);
////                    System.out.println("");
////                exprValue.instrRef = new_instr_link;
////                block.VariInstrRefTable.put(id, exprValue.instrRef);
////                VariableTable.VariMemoryAddress.put(id, exprValue);
//                }
//            }
//        }

        int i = 0;
        Iterator<Instruction> iterator = doBlock.instr_list.iterator();
        while (iterator.hasNext()) {
            Instruction instr = iterator.next();
            if (instr.id == -1) {
                int var_id = joinBlock.PhiVar.get(i);
                Instruction new_instr_link = pgm.processCSEinWhile(instr);
                doBlock.VariInstrRefTable.put(var_id, new_instr_link);
                if (instr.id == -1) {
                    iterator.remove();
                }
            }
            i++;
        }


            //
//        for (int i=0; i <= doBlock.instr_list.size(); i++){
//            if (doBlock.instr_list.get(i).id == -1) {
//                int var_id = joinBlock.PhiVar.get(i);
//                Instruction new_instr_link = pgm.processCSEinWhile(doBlock.instr_list.get(i), doBlock, i);
//                doBlock.VariInstrRefTable.put(var_id, new_instr_link);
//                if (doBlock.instr_list.get(i).id == -1){
//                }
////            }else{
////                pgm.processCSEinWhile(doBlock.instr_list.get(i), doBlock);  //update CSE
//            }
//        }

//        if (doBlock.instr_list.size() == 0){
//            doBlock.generateInstr(null);  // create empty SSA
//        }

        nextBlock.clone_VariableVersionTable(joinBlock.VariInstrRefTable);
        for(Integer var_id: nextBlock.VariInstrRefTable.keySet()){
            VariableTable.VariMemoryAddress.get(var_id).instrRef = nextBlock.VariInstrRefTable.get(var_id);
        }

//        Instruction bra_instr = doBlock.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));

//        Instruction bra_instr= null;
//        if (doBlock.dominees.size() != 0) {
//            Block cur_block = doBlock.dominees.get(0);
//
//            while ((cur_block.BlockType != Block.Type.while_join) && (cur_block.dominees.size() != 0)) {
//                cur_block =  cur_block.dominees.get(cur_block.dominees.size()-1);
//            }
//            if (cur_block.BlockType == Block.Type.while_join) {
//                // while block always has its dominates, go to the follow
//                bra_instr = cur_block.dominees.get(cur_block.dominees.size() - 1).generateInstr(Instruction.Type.bra,
//                        new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }else{ // means no more dominators; Direct add bra to here
//                bra_instr = cur_block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//            }
//        }else{
//            bra_instr = doBlock.generateInstr(Instruction.Type.bra,
//                    new Value(Value.Type.branch, 0));   // fall through, 0 is to be linked
//        }
        Block bra_block = ToEndOfDomineeBlock(doBlock);
        Instruction bra_instr;
        if (bra_block.instr_list.size() == 1) {
            bra_instr = replaceEmptyInstrChecker(bra_block, Instruction.Type.bra, new Value(Value.Type.branch, 0), null);
        }else{
            bra_instr = bra_block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));
        }

        pgm.add_block(nextBlock, true);
        nextBlock.BlockID = pgm.block_pc;

        rel_branch_instr.right.branchBlock = nextBlock;
        bra_instr.left.branchBlock = joinBlock;
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

            result.instrRef = instr;

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

    public Block ToEndOfDomineeBlock(Block b) {
        Block cur_block = b;
        if (b.BlockType == Block.Type.normal){
            return b;
        }
        while ((cur_block.BlockType != Block.Type.while_join) &&
                (cur_block.BlockType != Block.Type.if_join) &&
                (cur_block.dominees.size()!=0)){
            cur_block =  cur_block.dominees.get(0);
        }

        if ((cur_block.BlockType == Block.Type.while_join)) {  // take fellow block
            cur_block = cur_block.dominees.get(cur_block.dominees.size() - 1);
        }
        return cur_block;
    }

    public Instruction replaceEmptyInstrChecker(Block block, Instruction.Type type, Value x, Value y){
        Instruction bra_instr;
        Instruction empty_checker = block.instr_list.get(0);
        if (empty_checker.type == null) {
            bra_instr = new Instruction(type, x,y);
            bra_instr.id = empty_checker.id;
            bra_instr.block = block;
            block.instr_list.remove(0);
            block.instr_list.add(bra_instr);
        }else{
            bra_instr = block.generateInstr(Instruction.Type.bra, new Value(Value.Type.branch, 0));
        }
        return bra_instr;
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