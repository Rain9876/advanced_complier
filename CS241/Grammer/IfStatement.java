package CS241.Grammer;
import java.util.*;

//import vcg.*;

public class IfStatement implements Statement {
	public Relation condi;
	public ArrayList<Statement> thenStmts;
	public ArrayList<Statement> elseStmts;
	
	public IfStatement() {
		condi = null;
		thenStmts = new ArrayList<>();
		elseStmts = new ArrayList<>();
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode thenStatementsNode = new VCGNode();
//		thenStatementsNode.setName("Then Statements");
//		for (Statement s : thenStatements) {
//			thenStatementsNode.getChildren().add(s.getVCGNode());
//		}
//
//		VCGNode elseStatementsNode = new VCGNode();
//		elseStatementsNode.setName("Else Statements");
//		for (Statement s : elseStatements) {
//			elseStatementsNode.getChildren().add(s.getVCGNode());
//		}
//
//		VCGNode ifNode = new VCGNode();
//		ifNode.setName("If Statement");
//		ifNode.getAttributes().add(new VCGAttribute<>("Relation", relation.getVCGNode()));
//		ifNode.getAttributes().add(new VCGAttribute<>("Then", thenStatementsNode));
//		ifNode.getAttributes().add(new VCGAttribute<>("Else", elseStatementsNode));
//
//		return ifNode;
//	}
	
}
