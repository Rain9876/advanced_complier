package CS241.Grammer;


import java.util.ArrayList;

public class Designator implements Factor {
	public int id;
	public ArrayList<Expression> expressions;
	
	public Designator() {
		id = -1;
		expressions = new ArrayList<>();
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode node = new VCGNode();
//		node.setName("Designator");
//		node.getAttributes().add(new VCGAttribute<>("ID", Integer.toString(id)));
//		for (Expression e : indexExpressions) {
//			node.getChildren().add(e.getVCGNode());
//		}
//
//		return node;
//	}
	
}
