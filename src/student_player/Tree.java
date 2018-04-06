package student_player;

import tablut.TablutBoardState;

public class Tree {
	Node rootNode;
	
	public Tree(TablutBoardState aBoardState){
		rootNode = new Node(aBoardState);
	}

}
