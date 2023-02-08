package CS241.Grammer;

import java.util.ArrayList;

public class FunctionBody {

    public ArrayList<VariableDeclaration> variables;
    public ArrayList<Statement> statements;

    public FunctionBody() {
        variables = new ArrayList<>();
        statements = new ArrayList<>();
    }

    public int getReservedSpace() {
        int n = 0;
        for (VariableDeclaration var : variables) {
            n += var.nameIDs.size() * var.type.getSize();
        }
        return n * 4;
    }
}
