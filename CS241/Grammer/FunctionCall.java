package CS241.Grammer;

import java.util.*;

public class FunctionCall implements Statement, Factor {
	public int funcID;
	public ArrayList<Expression> paramExpressions;
	
	public FunctionCall() {
		funcID = -1;
		paramExpressions = new ArrayList<>();
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode node = new VCGNode();
//		node.setName("Function Call");
//		node.getAttributes().add(new VCGAttribute<>("Function ID", Integer.toString(functionID)));
//		for (Expression e : parameterExpressions) {
//			node.getChildren().add(e.getVCGNode());
//		}
//
//		return node;
//	}
	
}
