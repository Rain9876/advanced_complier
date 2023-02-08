package CS241;

import CS241.Grammer.*;

import java.util.ArrayList;
import java.util.HashMap;



public class Parser {
    public Tokenizer myTokenizer;
    private int inputSym; // the current token on the input
//    private  CodeGenerator CD = new CodeGenerator();

    private void next(){
        inputSym = myTokenizer.GetNext();
    } // advance to the next token

    void CheckFor(int token){
        if (inputSym == token) {
            next();
        }else {
            myTokenizer.Error("SyntaxErr " +
                    myTokenizer.TokenId2String(token) + " " );
        }
    }

    public Parser(String fileName) {  // constructor: open file and cache its frontmost token in “inputSym’
        myTokenizer = new Tokenizer(fileName); next();
    }


    public Computation computation(){
        Computation comp = new Computation();

        CheckFor(Sym.mainToken);    // Check main token

        while (inputSym == Sym.varToken || inputSym == Sym.arrToken){
            comp.variables.add(varDecl());
        };

        while(inputSym == Sym.voidToken || inputSym == Sym.funcToken){
            comp.functions.add(funcDecl());
        };

        CheckFor(Sym.beginToken);

        if (inputSym == Sym.letToken ||
                inputSym == Sym.callToken ||
                inputSym == Sym.ifToken ||
                inputSym == Sym.whileToken ||
                inputSym == Sym.returnToken){
            comp.statements = stateSequence();
        };
        CheckFor(Sym.endToken);
        CheckFor(Sym.periodToken);
        CheckFor(Sym.eofToken);
        return comp;
    };

    public VariableDeclaration varDecl() {
        VariableDeclaration varsD = new VariableDeclaration();
        varsD.type = typeDecl();

        if(inputSym == Sym.ident) {
            varsD.nameIDs.add(myTokenizer.id);
            next();
        }

        while(inputSym==Sym.commaToken){
            next();
            if(inputSym == Sym.ident) {
                varsD.nameIDs.add(myTokenizer.id);
                next();
            }
        };
        CheckFor(Sym.semiToken);

        return varsD;
    }

    public TypeDeclaration typeDecl(){
        TypeDeclaration type = new TypeDeclaration();

        if (inputSym==Sym.varToken){
            next();
            return type;
        }else if(inputSym == Sym.arrToken){
            next();

            if (inputSym == Sym.openbracketToken){     // left Square Bracket
                next();
                if (inputSym == Sym.number) {
                    type.dimensions.add(myTokenizer.number);
                }
            }
            CheckFor(Sym.closebracketToken);

            while (inputSym == Sym.openbracketToken){
                next();
                if (inputSym == Sym.number) {
                    type.dimensions.add(myTokenizer.number);
                }
                CheckFor(Sym.closebracketToken);
            }
            return type;

        }else{
            myTokenizer.Error("Expected 'var' or 'array'");
            return null;
        }
    };

    public FunctionDeclaration funcDecl(){
        FunctionDeclaration func = new FunctionDeclaration();

        if (inputSym == Sym.voidToken){
            func.isProcedure = true;
        }
        CheckFor(Sym.funcToken);
        CheckFor(Sym.ident);
        func.parameterIDs = formalParam();
        CheckFor(Sym.semiToken);
        func.funcBody = funcBody();
        CheckFor(Sym.semiToken);
        return func;
    }


    public ArrayList<Integer> formalParam() {
        ArrayList<Integer> params = new ArrayList<>();
        CheckFor(Sym.openparenToken);
        if (inputSym == Sym.ident) {
            params.add(myTokenizer.id);
            next();
            while (inputSym == Sym.commaToken) {
                next();
                if (inputSym == Sym.ident) {
                    params.add(myTokenizer.id);
                    next();
                }
            }
        }
        CheckFor(Sym.closeparenToken);
        return params;
    }


    public FunctionBody funcBody() {
        FunctionBody funcB = new FunctionBody();
        while (inputSym ==Sym.varToken || inputSym == Sym.arrToken){
            funcB.variables.add(varDecl());
        }
        CheckFor(Sym.beginToken);
        if (inputSym == Sym.letToken ||
                inputSym == Sym.callToken ||
                inputSym == Sym.ifToken ||
                inputSym == Sym.whileToken ||
                inputSym == Sym.returnToken){
            funcB.statements = stateSequence();

        }
        CheckFor(Sym.semiToken);

        return funcB;
    }



    public ArrayList<Statement> stateSequence(){
        ArrayList<Statement> stmts = new ArrayList<>();
        Statement stmt = statement();
        stmts.add(stmt);

        while (inputSym == Sym.semiToken){
            next();
            if (inputSym == Sym.letToken || inputSym == Sym.callToken ||
                    inputSym == Sym.ifToken || inputSym == Sym.whileToken ||
                    inputSym == Sym.returnToken) {
                stmt = statement();
                stmts.add(stmt);
            }else{
                return stmts;
            }

        }
        return stmts;

    }

    public Statement statement(){
        switch (inputSym) {
            case Sym.letToken:
                return assignment();
            case Sym.callToken:
                return funcCall();
            case Sym.ifToken:
                return ifStatement();
            case Sym.whileToken:
                return whileStatement();
            case Sym.returnToken:
                return returnStatement();
            default:
                myTokenizer.Error("SyntaxErr of Statement");
                return null;
        }
    }

    public Statement assignment(){
        Assignment assign = new Assignment();
        CheckFor(Sym.letToken);
        assign.designator = designator();
        CheckFor(Sym.becomesToken);
        assign.expression = expression();
        return assign;
    }

    public FunctionCall funcCall(){
        FunctionCall funcC = new FunctionCall();
        CheckFor(Sym.callToken);
        CheckFor(Sym.ident);
        funcC.funcID = myTokenizer.id;
        if(inputSym == Sym.openparenToken){
            next();
            if (inputSym == Sym.ident ||
                    inputSym == Sym.number  ||
                    inputSym == Sym.openparenToken ||
                    inputSym == Sym.callToken ){
                funcC.paramExpressions.add(expression());
                while (inputSym == Sym.commaToken){
                    next();
                    funcC.paramExpressions.add(expression());
                }
            }
            CheckFor(Sym.closeparenToken);
        }
        return funcC;

    }

    public IfStatement ifStatement(){
        IfStatement istmt = new IfStatement();
        CheckFor(Sym.ifToken);
        istmt.condi = relation();
        CheckFor(Sym.thenToken);
        istmt.thenStmts = stateSequence();
        if (inputSym == Sym.elseToken){
            next();
            istmt.elseStmts = stateSequence();
        }
        CheckFor(Sym.fiToken);
        return istmt;
    }

    public WhileStatement whileStatement(){
        WhileStatement wstmt = new WhileStatement();
        CheckFor(Sym.whileToken);
        wstmt.condi = relation();
        CheckFor(Sym.doToken);
        wstmt.body = stateSequence();
        CheckFor(Sym.odToken);
        return wstmt;
    }

    public ReturnStatement returnStatement(){
        ReturnStatement ret = new ReturnStatement();
        CheckFor(Sym.returnToken);
        if (inputSym == Sym.ident ||
                inputSym == Sym.number  ||
                inputSym == Sym.openparenToken ||
                inputSym == Sym.callToken ){
            ret.expression = expression();
        }
        return ret;
    }


    public Designator designator(){
        Designator x = new Designator();
        if (inputSym == Sym.ident){
            x.id = myTokenizer.id;
            next();
            while(inputSym==Sym.openbracketToken){
                next();
                x.expressions.add(expression());
                CheckFor(Sym.closeparenToken);
            }

        }
        return x;
    }

    public Relation relation(){
        Relation rel = new Relation();
        rel.left = expression();
        switch (inputSym){
            case Sym.eqlToken:
                rel.op = Relation.Operator.Equal;
                break;
            case Sym.neqToken:
                rel.op = Relation.Operator.NotEqual;
                break;
            case Sym.lssToken:
                rel.op = Relation.Operator.LessThan;
                break;
            case Sym.geqToken:
                rel.op = Relation.Operator.GreaterEqual;
                break;
            case Sym.leqToken:
                rel.op = Relation.Operator.LessEqual;
                break;
            case Sym.gtrToken:
                rel.op = Relation.Operator.GreaterThan;
                break;
            default:
                myTokenizer.Error("SyntaxErr");
        }
        next();
        rel.right = expression();

        return rel;

    }

    public Expression expression(){
        Expression ex = new Expression();
        ex.terms.add(term());
        while(inputSym == Sym.plusToken || inputSym == Sym.minusToken){
            if (inputSym == 11){
                next();
                ex.operators.add(Expression.Operator.Plus);
                ex.terms.add(term());
            }else {
                next();
                ex.operators.add(Expression.Operator.Minus);
                ex.terms.add(term());
            }
        }
        return ex;

    }

    public Term term() {
        Term t = new Term();
         t.factors.add(factor());
        while (inputSym == 1 || inputSym == 2){
            if (inputSym == 1){
                next();
                t.operators.add(Term.Operator.Times);
                t.factors.add(factor());
            }else {
                next();
                t.operators.add(Term.Operator.Divide);
                t.factors.add(factor());
            }
        }
        return t;
    }

    public Factor factor(){
        if (inputSym == Sym.ident){
            return designator();
        }else if (inputSym == Sym.number){
            Constant c = new Constant();
            c.value = myTokenizer.number;
            next();
            return c;
        }else if (inputSym == Sym.openbracketToken){
            next();
            Expression ex = expression();
            CheckFor(Sym.closeparenToken);
            return ex;
        }else{
            return funcCall();
        }
    }

//    public static void main(String args[]){
//        String test = "main var a, b, c, d, e; { let a <- 1; let b <- a; let c <- b; let d <- b + c; let e <- a + b; if a < 0 then let d <- d + e; let a <- d else let d <- e fi; write (b);}.";
//        Parser p = new Parser(test);
//        p.computation();
//
//    }

}