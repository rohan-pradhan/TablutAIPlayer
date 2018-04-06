package student_player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import coordinates.Coord;
import coordinates.Coordinates;
import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

public class MonteCarloAlgorithm {
	
	public static final double EXPLORATION_PARAMATER = 2;
	public Random r;
	public int player_id;
	public Tree monteCarloTree;
	public static ExecutorService executorService; 
	
	
	public MonteCarloAlgorithm(int aPlayerID){
		player_id = aPlayerID;
		
		
			
	}
	
	
	
	public TablutMove strategyMove(TablutBoardState aBoardState, boolean IsIniitalMove){
		
		monteCarloTree = new Tree(aBoardState);
		
		long currentTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis() + (IsIniitalMove ? 28000 : 1300);
		
//		if (player_id == TablutBoardState.MUSCOVITE){
//			if (Coordinates.distanceToClosestCorner(aBoardState.getKingPosition())<3){
//				
//			}
//		}
		
		Node prePopulateNode = monteCarloTree.rootNode;

		expandTree(prePopulateNode);
		

		
		double value = 1.4;
		
		
		while (System.currentTimeMillis() < endTime) {
//			
//			Callable<Void> callable = new Callable<Void>() {
//
//				@Override
//				public Void call() throws Exception {
//					// TODO Auto-generated method stub
//					loopCode(value, prePopulateNode);
//					return null;
//				}
//			
//			};
//			
//			Future<Void> future = executorService.submit(callable);
//			
//			try {
//				future.get(endTime - System.currentTimeMillis()-100, TimeUnit.MILLISECONDS);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (TimeoutException e) {
//				break;
//			}
			
//			
//			Node currentNode =  prePopulateNode;
//			
//			
//			while (!currentNode.getChildren().isEmpty()) {
//	
//			currentNode = nodeMaximizationUCBI(value,currentNode);
//		
//				 //is leaf now 
//			}
//			
//			
//			if (currentNode.getNumberOfVisits() > 0){
//				expandTree(currentNode);
//				currentNode = currentNode.getChildren().get(0);
//			}
//			
//			rollOut(currentNode);
			
			loopCode(value, prePopulateNode);

				
		}

		
		return nodeMaximizationUCBI(0,prePopulateNode).getMoveToGetHere();
				
	}
	
	public void loopCode(double value, Node prePopulateNode){
		Node currentNode =  prePopulateNode;
		if (currentNode.getChildren() == null) {
			System.out.println("Simi");
		}
		while (!currentNode.getChildren().isEmpty()) {
			currentNode = nodeMaximizationUCBI(value,currentNode);//is leaf now
			if (currentNode == null){
				System.out.println("Sunny");
			}
		}
		
		if (currentNode.getNumberOfVisits() > 0){
			expandTree(currentNode);
			currentNode = currentNode.getChildren().get(0);
		}
		
		rollOut(currentNode);
		value = value*1.5;
		
	}
	
	
	
	public static double calculateUCBI(double aValue, int parentVisits, double currentScore, int nodeVisits){
		if (nodeVisits == 0) {
			return Double.MAX_VALUE;
		}
		
		return (((double)currentScore/(double)nodeVisits) + (aValue)*Math.sqrt(Math.log(parentVisits)/(double)nodeVisits));

	}
	
	public static TablutMove moveToBestNode(Node parentNode){
		Node goalNode = nodeMaximizationUCBI(0,parentNode);
		TablutBoardState cloneState = (TablutBoardState) parentNode.getCurrentState().clone();
		for (TablutMove aMove : cloneState.getAllLegalMoves()){
			TablutBoardState cloneState2 = (TablutBoardState) cloneState.clone();
			cloneState2.processMove(aMove);
			if (cloneState2.equals(goalNode.getCurrentState())){
				return aMove;
			}
		}
		return null;
		
	}
	
	public static Node nodeMaximizationUCBI(double aValue, Node parentNode){
		int parentVisits = parentNode.getNumberOfVisits();
		ArrayList<Node> childrenArrayList = parentNode.getChildren();
		Node bestNode = null; 
		double bestValue = Double.MIN_VALUE;
		if (childrenArrayList.isEmpty()){
			System.out.println("oh lawd");
		}
		for (Node aNode : childrenArrayList){
			if (aNode == null) System.out.println("playstation");
			double value = MonteCarloAlgorithm.calculateUCBI(aValue, parentVisits, aNode.getCurrentAverageScore(), aNode.getNumberOfVisits());
			if (value == Double.MAX_VALUE){
				
				return aNode;
			} else if (value > bestValue){
				if (aNode == null){
					System.out.println("null");
				}
				bestNode = aNode;
				
				bestValue = value;
				if (bestNode == null) {
					System.out.println("ha");
				}
			}
			else {
//				System.out.println("Value wrong: " + value);
			}
			
		}
		
		if (bestNode == null) {
			System.out.println(bestValue);
			System.out.println("ha2");
		}
		return bestNode;
	}
	
	public static Node nodeMinimizationUCBI(double aValue, Node parentNode){
		int parentVisits = parentNode.getNumberOfVisits();
		ArrayList<Node> childrenArrayList = parentNode.getChildren();
		Node bestNode = null; 
		double bestValue = Double.MAX_VALUE; 
		for (Node aNode : childrenArrayList){
			double value = MonteCarloAlgorithm.calculateUCBI(aValue, parentVisits, aNode.getCurrentAverageScore(), aNode.getNumberOfVisits());
			if (value <= bestValue){
				bestNode = aNode;
				bestValue = value;
			}
			
		}
		return bestNode;
	}
	
	protected void backPropStep(Node currentNode, double value){
		currentNode.setNumberOfVisits((currentNode.getNumberOfVisits()+1));
		currentNode.setCurrentAverageScore(value);
		Node runnerNode = currentNode.getParentNode();
		while (runnerNode != null){
			runnerNode.setNumberOfVisits(runnerNode.getNumberOfVisits()+1);
			runnerNode.setCurrentAverageScore(runnerNode.getCurrentAverageScore()+value);
			runnerNode = runnerNode.getParentNode();
		}
		
		
		
	}
	
	protected void expandTree(Node currentNode){
		ArrayList<TablutMove> legalMoves = currentNode.getCurrentState().getAllLegalMoves();
		TablutBoardState currentState = currentNode.getCurrentState();
//		ArrayList<TablutMove> legalMoves = currentState.getAllLegalMoves();
//		
//		if (player_id == TablutBoardState.SWEDE){
//			legalMoves = currentState.getLegalMovesForPosition(currentState.getKingPosition());
//		}
//		
		for(TablutMove move : legalMoves){
			TablutBoardState clonedState = (TablutBoardState) currentState.clone();
			clonedState.processMove(move);
			Node nodeToAdd = new Node(clonedState, currentNode, move);
			currentNode.addChildNode(nodeToAdd);
		}
			
		
	}
	
	protected void rollOut(Node currentNode){

		TablutBoardState currentState = currentNode.getCurrentState();
//		Node simNode = currentNode.clone();
		TablutBoardState clonedState = (TablutBoardState) currentState.clone();
		int i =0;

		
		while (!clonedState.gameOver()){
			if (i==50){		
					backPropStep(currentNode, 0);

				
			}
		
			
//			System.out.println("is legal moves empty? " + legalMoves.isEmpty());
			Random rn = new Random();
			ArrayList<TablutMove> legalMoves = clonedState.getAllLegalMoves();
			TablutMove move = legalMoves.get(rn.nextInt(legalMoves.size()));

			clonedState.processMove(move);
			
			i++;
		}
		
//		double valueAtTerminalState = heuristicValue(currentNode, clonedState, prevState, i);
		double valueAtTerminalState = heuristicValue(clonedState, i);

		backPropStep(currentNode, valueAtTerminalState);
		
	}
	
	protected double heuristicValue(TablutBoardState aState, int numberOfIters){
		
//		if (player_id == TablutBoardState.MUSCOVITE){
//		if (aState.getWinner() == player_id){
//			int hi = aState.getOpponent();
//			Coord kingCordCoord = currentNode.getCurrentState().getKingPosition();
//			int distance = 1;
//			if (kingCordCoord != null) distance = Coordinates.distanceToClosestCorner(kingCordCoord);
//			
//			return (10.0+distance*currentNode.getCurrentState().getNumberPlayerPieces(player_id)/currentNode.getCurrentState().getNumberPlayerPieces(hi));
//			}
//			
//		 else {
//			return (1);
//		} }
//		else {
//			int hi = aState.getOpponent();
//			Coord kingCordCoord = currentNode.getCurrentState().getKingPosition();
//			int distance = 1;
//			if (kingCordCoord != null) distance = Coordinates.distanceToClosestCorner(kingCordCoord);
//			if (aState.getWinner() == player_id){
//
//				
//				return (10*aState.getNumberPlayerPieces(player_id)/distance);
//				}
//				
//			 else {
//				return (-1)*Math.exp(distance);
//			
//			
//		}
//		
//	}
		
		
		
		double value = 0; 
		
//		int opponent = aState.getOpponent();
//		value += aState.getNumberPlayerPieces(player_id) - aState.getNumberPlayerPieces(opponent) ;
//		
//		if (player_id == TablutBoardState.SWEDE){
//			if(Coordinates.distanceToClosestCorner(aState.getKingPosition()) < Coordinates.distanceToClosestCorner(aPrevState.getKingPosition())) {
//				value += 10;
//			}
//				
//		}
//		else {
//			if (!currentNode.getCurrentState().gameOver()){
//				int distance = Coordinates.distanceToClosestCorner(currentNode.getCurrentState().getKingPosition());
//
//					value-= 50-Math.pow(distance, 2);
//				
//			}
//		}
//		
//		if (aState.getNumberPlayerPieces(opponent) < aPrevState.getNumberPlayerPieces(opponent)){
//			value += 1; 
//		}
		
		if (aState.getWinner() == player_id){
			value += 30000;
			
		}

		
		
		
		return value;
	}
		
		

}
