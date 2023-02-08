package CS241.Grammer;

import java.util.*;

//import vcg.*;

public class VariableDeclaration{
	public TypeDeclaration type;
	public ArrayList<Integer> nameIDs;
	
	public VariableDeclaration() {
		type = new TypeDeclaration();
		nameIDs = new ArrayList<>();
	}

	
//	public VCGNode getVCGNode() {
//		VCGNode node = new VCGNode();
//		node.setName("Variable Declaration");
//		node.getAttributes().add(new VCGAttribute<>("Type", type.getVCGNode()));
//		node.getAttributes().add(new VCGAttribute<>("Name IDs", nameIDs.toString()));
//
//		return node;
//	}


}
