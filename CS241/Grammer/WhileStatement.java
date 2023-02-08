package CS241.Grammer;
import java.util.*;

public class WhileStatement implements Statement {
	public Relation condi;
	public ArrayList<Statement> body;
	
	public WhileStatement() {
		condi = null;
		body = new ArrayList<>();
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode statementsNode = new VCGNode();
//		statementsNode.setName("Statements");
//		for (Statement s : statements) {
//			statementsNode.getChildren().add(s.getVCGNode());
//		}
//
//		VCGNode whileNode = new VCGNode();
//		whileNode.setName("While Statement");
//		whileNode.getAttributes().add(new VCGAttribute<>("Relation", relation.getVCGNode()));
//		whileNode.getAttributes().add(new VCGAttribute<>("Statements", statementsNode));
//
//		return whileNode;
//	}
	
}
