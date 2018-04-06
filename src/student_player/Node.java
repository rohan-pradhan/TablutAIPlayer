package student_player;
import java.util.ArrayList;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

public class Node {
	private int numberOfVisits; 
	private double currentAverageScore;
	private TablutBoardState currentState;
	private ArrayList<Node> children;
	private Node parentNode; 
	private TablutMove moveToBestState;
	private TablutMove moveToGetHere;
	
	public TablutMove getMoveToGetHere() {
		return moveToGetHere;
	}

	public void setMoveToGetHere(TablutMove moveToGetHere) {
		this.moveToGetHere = moveToGetHere;
	}

	public Node(TablutBoardState aCurrentState){
		numberOfVisits =0;
		currentAverageScore = 0;
		currentState = aCurrentState; 
		children = new ArrayList<Node>();
		parentNode = null;
		
	}
	
	public Node(TablutBoardState aCurrentState, Node parent){
		numberOfVisits =0;
		currentAverageScore = 0;
		currentState = aCurrentState; 
		children = new ArrayList<Node>();
		parentNode = parent;
		
	}
	
	public Node(TablutBoardState aCurrentState, Node parent, TablutMove aMove){
		numberOfVisits =0;
		currentAverageScore = 0;
		currentState = aCurrentState; 
		children = new ArrayList<Node>();
		parentNode = parent;
		moveToGetHere = aMove;
		
	}
	
	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public Node clone() {
		Node nodeToReturn = new Node(null);
		nodeToReturn.setCurrentState((TablutBoardState)currentState.clone());
		nodeToReturn.setCurrentAverageScore(currentAverageScore);
		nodeToReturn.setNumberOfVisits(numberOfVisits);
//		nodeToReturn.setChildren((ArrayList<Node>)children.clone());
		return nodeToReturn;
	}
	
	public void addChildNode(Node nodeToAdd){
		children.add(nodeToAdd);
	}

	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public double getCurrentAverageScore() {
		return currentAverageScore;
	}

	public void setCurrentAverageScore(double currentAverageScore) {
		this.currentAverageScore = currentAverageScore;
	}

	public TablutBoardState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(TablutBoardState currentState) {
		this.currentState = currentState;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
	
	
	
	
}
