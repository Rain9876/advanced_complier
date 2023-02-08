package CS241.Grammer;

public class Relation  {


	public enum Operator {
		LessThan,
		LessEqual,
		Equal,
		NotEqual,
		GreaterEqual,
		GreaterThan;

		public String toString() {
			if (this == LessThan) return "<";
			if (this == LessEqual) return "<=";
			if (this == Equal) return "==";
			if (this == NotEqual) return "!=";
			if (this == GreaterEqual) return ">=";
			if (this == GreaterThan) return ">";
			return "null";
		}
	}
	
	public Operator op;
	public Expression left;
	public Expression right;

	public Relation() {
		op = Operator.Equal;
		left = null;
		right = null;
	}
	
//	public VCGNode getVCGNode() {
//		VCGNode node = new VCGNode();
//		node.setName("Relation");
//		node.getAttributes().add(new VCGAttribute<>("Operator", op.toString()));
//		node.getAttributes().add(new VCGAttribute<>("LHS", lhs.getVCGNode()));
//		node.getAttributes().add(new VCGAttribute<>("RHS", rhs.getVCGNode()));
//
//		return node;
//	}
	
}
