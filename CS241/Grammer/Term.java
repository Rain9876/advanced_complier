package CS241.Grammer;

import java.util.*;

public class Term {
	public enum Operator {
		Times,
		Divide;
	}
	
	public ArrayList<Factor> factors;
	public ArrayList<Operator> operators;
	
	public Term() {
		factors = new ArrayList<>();
		operators = new ArrayList<>();
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode node = new VCGNode();
//		node.setName("Term");
//		for (int i = 0; i < factors.size(); i++) {
//			node.getChildren().add(factors.get(i).getVCGNode());
//			if (i < operators.size()) {
//				node.getChildren().add(operators.get(i).getVCGNode());
//			}
//		}
//
//		return node;
//	}
	
}
