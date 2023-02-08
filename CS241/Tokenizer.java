package CS241;

import java.util.HashMap;

public class Tokenizer {
    private FileReader myFileReader;
    private char inputSym; // the current character on the input
    private HashMap<String, Integer> token_table = new HashMap<>(){{
        put("main", 0);
        put("*", 1);
        put("/", 2);
        put("+", 11);
        put("-", 12);
        put("==", 20);
        put("!=", 21);
        put("<", 22);
        put(">=", 23);
        put("<=", 24);
        put(">", 25);
        put(".", 30);
        put(",", 31);
        put("[", 32);
        put("]", 34);
        put(")", 35);
        put("<-", 40);
        put("then", 41);
        put("do", 42);
        put("(", 50);
        put("number", 60);
        put("identifier", 61);
        put(";", 70);
        put("}", 80);
        put("od", 81);
        put("fi", 82);
        put("else", 90);
        put("let", 100);
        put("call", 101);
        put("if", 102);
        put("while", 103);
        put("return", 104);
        put("var", 110);
        put("array", 111);
        put("void", 112);
        put("function", 113);
        put("procedure", 114);
        put("{", 150);
        put("computation", 200);
        put("main", 200);
        put("eof", 255);
    }};

    public Tokenizer() {

    }

    private void next() {
        inputSym = myFileReader.GetNext();
    } // advance to the next character

    /* symmetrical to the FileReader class */
    public int GetNext(){
        String token = "";
        int value;

        while (inputSym == ' ' || inputSym == '\t') {
            next();
        }

        if (Character.isDigit(inputSym)){      // number
            token += inputSym;
            next();
            while (Character.isDigit(inputSym)){
                token += inputSym;
                next();
            }
            this.number = Integer.parseInt(token);
            value = token_table.get("number");

        }else if(Character.isAlphabetic(inputSym)){  // var
            token += inputSym;
            next();

            while (Character.isAlphabetic(inputSym) || Character.isDigit(inputSym)){
                token += inputSym;
                next();
            }

            if (token_table.containsKey(token)){
                value = token_table.get(token);
            }else{
                value = token_table.get("identifier");
                this.id = VariableTable.String2Id(token);
            }

        }else{   // other special characters
            token += inputSym;

            if (token.equals("§")) {
                value = token_table.get("eof");
                return value;
            }

            next();

            if ((token.equals("!") && inputSym == '=' ) ||
                (token.equals("=") && inputSym == '=' ) ||
                (token.equals("<") && inputSym == '=' ) ||
                (token.equals(">") && inputSym == '=' ) ||
                (token.equals("<") && inputSym == '-' )
            ){
                token += inputSym;
                next();
            }
            value = token_table.get(token);

        }
//        System.out.println(TokenId2String(value));
        return value;

    }

    // return current and advance to the next token on the input
    public int number;   // the last number encountered
    public int id; // the last identifier encountered


    public void Error(String errorMsg) {
        myFileReader.Error(errorMsg);
    }

    public Tokenizer(String fileName){
        this.myFileReader = new FileReader(fileName);
        this.next();
    }


    /* token table methods for display tokens */
    public String TokenId2String(int id){
        String curKey= "";
        if (token_table.containsValue(id)) {
            for (String s : token_table.keySet()) {
                curKey = s;
                if (token_table.get(curKey).equals(id)) {
                    break;
                }
            }
        }
        return curKey;
    }

//    public static void main(String[] args){
//        Tokenizer tok = new Tokenizer("/Users/yurunsong/Desktop/Pre.txt");
//        Tokenizer tok = new Tokenizer("main var a, b, c, d, e; {");
//        System.out.println(tok.token_table);
//        System.out.println(tok.myFileReader.content);
//        for(int i = 0; i <=80; i++) {
//            int tmp = tok.GetNext();
//            System.out.print(tmp);
//            System.out.print(" ");
//            System.out.print(tok.Id2String(tmp));
//            System.out.println(tok.Id2String(tmp));
//        }
//        System.out.println();
//        System.out.println(tok.identifer_table);


//    }

}


