package student_player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
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
	public Random r = new Random();
	public int player_id;
	public int opponnent; 
	public Tree monteCarloTree;
	public static ExecutorService executorService; 
	public long endTime;
	public Comparator<Node> muscoComparator = new Comparator<Node>() {
		public int compare(Node o1, Node o2){
			return Integer.compare(o1.numberOfPieces, o2.numberOfPieces);
		}
	};
	
	public Comparator<Node> swedeComparator = new Comparator<Node>() {
		public int compare(Node o1, Node o2){
			if (o1.getCurrentState().getKingPosition() == null){
				return 1;
			}
			
			else if (o2.getCurrentState().getKingPosition() == null){
				return -1;
			} 
			else {
			return Integer.compare(Coordinates.distanceToClosestCorner(o2.getCurrentState().getKingPosition()), Coordinates.distanceToClosestCorner(o1.getCurrentState().getKingPosition()));
		}
		}
	};
	

	
	public MonteCarloAlgorithm(){
			
	}
	

	public TablutMove strategyMove(TablutBoardState aBoardState, boolean IsIniitalMove, int aPlayerID){
	
		
		player_id = aPlayerID;
		monteCarloTree = new Tree(aBoardState);
		opponnent = aBoardState.getOpponent();
		
		
//		long currentTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis() + (IsIniitalMove ? 28000 : 750);
		
//		if (player_id == TablutBoardState.MUSCOVITE){
//			if (Coordinates.distanceToClosestCorner(aBoardState.getKingPosition())<3){
//				
//			}
//		}
		
		final Node prePopulateNode = monteCarloTree.rootNode;

		expandTree(prePopulateNode);
		
		double value = 1.4;
//		TablutMove move = getObviousMuscoviteMove(aBoardState);
//		
//		if (move != null){
//			return move;
//		}

		
//		if (player_id == TablutBoardState.SWEDE){
//			
//			TablutMove obviousMove = getObviousKingMove(aBoardState);
//			if (obviousMove != null){
//				return obviousMove;
//			}
//		}
//		
//		if (player_id == TablutBoardState.MUSCOVITE){
//			TablutMove obviousMove = getObviousMuscoviteMove(aBoardState);
//			if (obviousMove != null){
//				return obviousMove;
//			}
//		}
		
		TablutMove obviousMove = getObviousMuscoviteMove(aBoardState);
		if (obviousMove != null){
			return obviousMove;
		}
		
		
//		
		
		
		while (System.currentTimeMillis() < endTime) {
			

			Node currentNode =  prePopulateNode;
			
			
			while (!currentNode.getChildren().isEmpty()) {
	
				currentNode = nodeMaximizationUCBI(value,currentNode);
		
				 //is leaf now 
			}
			
			
			if (currentNode.getNumberOfVisits() > 0){
				expandTree(currentNode);
				currentNode = currentNode.getChildren().get(0);
			}
			
			rollOut(currentNode);
//				value = 1.5*value;
			
	

				
		}
			
		return nodeMaximizationUCBI(0,prePopulateNode).getMoveToGetHere();
//		return move;
				
	}
	
	public TablutMove getObviousKingMove(TablutBoardState aBoardState){
		
		Coord kingPosCoord = aBoardState.getKingPosition();
		
		List<TablutMove> kingMoves = aBoardState.getLegalMovesForPosition(kingPosCoord);
		
		for (TablutMove move : kingMoves){
			TablutBoardState clonedState = (TablutBoardState) aBoardState.clone();
			clonedState.processMove(move);
			if (clonedState.getWinner() == player_id){
//				System.out.println("hi");
				return move; 
			}
			
		}
		return null;
		
//		kingMoves = aBoardState.getAllLegalMoves();
//		for (TablutMove move : kingMoves){
//			TablutBoardState clonedState = (TablutBoardState) aBoardState.clone();
//			clonedState.processMove(move);
//			if (clonedState.getWinner() == player_id || clonedState.getNumberPlayerPieces(opponnent) < aBoardState.getNumberPlayerPieces(opponnent)){
//				return move; 
//			}
//			
//		}
//		return null;
		
		
		
	}
	
	public TablutMove getObviousMuscoviteMove(TablutBoardState aBoardState){
		
		List<TablutMove> kingMoves = aBoardState.getAllLegalMoves();
		
		for (TablutMove move : kingMoves){
			TablutBoardState clonedState = (TablutBoardState) aBoardState.clone();
			clonedState.processMove(move);
//			if (clonedState.getWinner() == player_id || clonedState.getNumberPlayerPieces(opponnent) < aBoardState.getNumberPlayerPieces(opponnent)){
			if (clonedState.getWinner() == player_id){
			return move; 
			}
			
		}
		return null;
	}
	
	public TablutMove loopCode(double value, Node prePopulateNode){
		Node currentNode =  prePopulateNode;
		if (currentNode.getChildren() == null) {
			
		}
		while (!currentNode.getChildren().isEmpty()) {
			currentNode = nodeMaximizationUCBI(value,currentNode);//is leaf now
		}
		
		if (currentNode.getNumberOfVisits() > 0){
			expandTree(currentNode);
			currentNode = currentNode.getChildren().get(r.nextInt(currentNode.getChildren().size()));
		}
		
		rollOut(currentNode);
		value = value*1.5;
		
		return nodeMaximizationUCBI(0,prePopulateNode).getMoveToGetHere();
		
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

		for (Node aNode : childrenArrayList){
			double value;
			
			value = MonteCarloAlgorithm.calculateUCBI(aValue, parentVisits, aNode.getCurrentAverageScore(), aNode.getNumberOfVisits());
			
			if (value == Double.MAX_VALUE){
				return aNode;
			} else if (value > bestValue){

				bestNode = aNode;
				
				bestValue = value;

			}
			
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
//		if (System.currentTimeMillis() - endTime < 3){
//			System.out.println("Backprop Method");
//		}

		currentNode.numberOfVisits++;
		currentNode.setCurrentAverageScore(value);
		Node runnerNode = currentNode.getParentNode();
		while (runnerNode != null){
			runnerNode.numberOfVisits++;
			runnerNode.setCurrentAverageScore(runnerNode.currentAverageScore+value);
			runnerNode = runnerNode.getParentNode();
		}
		
		
		
	}
	
	protected void expandTree(Node currentNode){
//		if (System.currentTimeMillis() - endTime < 3){
//			System.out.println("Expand Method");
//		}

		TablutBoardState currentState = currentNode.getCurrentState();
		ArrayList<TablutMove> legalMoves = currentState.getAllLegalMoves();
		
//		ArrayList<TablutMove> legalMoves = currentState.getAllLegalMoves();
//		
//		if (player_id == TablutBoardState.SWEDE){
//			legalMoves = currentState.getLegalMovesForPosition(currentState.getKingPosition());
//		}
//		
		for(TablutMove move : legalMoves){
			TablutBoardState clonedState = (TablutBoardState) currentState.clone();
			clonedState.processMove(move);
			currentNode.addChildNode(new Node(clonedState, currentNode, move));
		}
		if (player_id == TablutBoardState.MUSCOVITE){
			currentNode.getChildren().sort(muscoComparator);
		} else {
			currentNode.getChildren().sort(swedeComparator);
		}
		
		
	}
	
	protected void rollOut(Node currentNode){

		TablutBoardState clonedState = (TablutBoardState) currentNode.getCurrentState().clone();
		int i =0;

		
		while (!clonedState.gameOver()){
			Coord kingPos = clonedState.getKingPosition();
			TablutMove bestMove = (TablutMove) clonedState.getRandomMove();
			if (i==100 || System.currentTimeMillis() > endTime-100){		
					backPropStep(currentNode, 1);
					return;
			}
			
			
			if (player_id == TablutBoardState.SWEDE){
				if (i==100 || System.currentTimeMillis() > endTime-100){		
					backPropStep(currentNode, 10- Coordinates.distanceToClosestCorner(kingPos));
					return;
			}
				
//				List<TablutMove> options = clonedState.getAllLegalMoves();
	            
	             
	            // Don't do a move if it wouldn't get us closer than our current position.
	            int minDistance = Coordinates.distanceToClosestCorner(kingPos);
	            
	            // Iterate over moves from a specific position, the king's position!
	            for (TablutMove move : clonedState.getLegalMovesForPosition(kingPos)) {
//	            	for (TablutMove move : clonedState.getAllLegalMoves()) {
	                /*
	                 * Here it is not necessary to actually process the move on a copied boardState.
	                 * Note that it is more efficient NOT to copy the boardState. Consider this
	                 * during implementation...
	                 */
	                int moveDistance = Coordinates.distanceToClosestCorner(move.getEndPosition());
	                if (moveDistance < minDistance) {
	                    minDistance = moveDistance;
	                    bestMove = move;
	                }
	            
			}
			}
					
			clonedState.processMove(bestMove);
			
			i++;
		}
		
		backPropStep(currentNode, heuristicValue(clonedState));
		
	}
	
	protected double heuristicValue(TablutBoardState aState){
		
		if (aState.getWinner() == player_id){
//			if (player_id == TablutBoardState.MUSCOVITE){
//			return 100;
//			}
//			return 30000;
			return 30000;
		} else return 0;
			
			
				
			
		}

		
		
		
		
	}
		
		


