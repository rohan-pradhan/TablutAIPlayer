package student_player;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutPlayer;
import student_player.MonteCarloAlgorithm;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
	
	MonteCarloAlgorithm strategyAlgorithm;
	
    public StudentPlayer() {
        super("260613559");
        strategyAlgorithm = new MonteCarloAlgorithm();
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
//        MyTools.getSomething();
        
//    	System.out.println("PlayerID is: " + player_id);

        // Is random the best you can do?
//       boolean flag = boardState.getTurnNumber() == 0);
//        Move myMove = strategyAlgorithm.strategyMove(boardState, boardState.getTurnNumber()==0);
        
//        Move myMove = strategyAlgorithm.strategyMove(boardState, false);

//        myMove = null;

        // Return your move to be processed by the server.
        return strategyAlgorithm.strategyMove(boardState, false, player_id);
    }
}