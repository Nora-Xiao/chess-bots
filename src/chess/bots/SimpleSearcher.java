package chess.bots;

import java.util.List;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth) {
    	if (depth == 0) {
    		// evaluate tells us the value of the current position
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	
    	int bestValue = -(evaluator.infty());
    	BestMove<M> bestMove = null;
    	List<M> moves = board.generateMoves();
    	for (M move : moves) {
    		board.applyMove(move);
    		BestMove<M> possibleMove = minimax(evaluator, board, depth - 1);
    		possibleMove = possibleMove.negate();
    		int value = possibleMove.value;
    		board.undoMove();
    		if (value > bestValue) {
    			bestValue = value;
    			possibleMove.move = move;
    			bestMove = possibleMove;
    		}
    	}
    	if (moves.isEmpty()) {
    		if (board.inCheck()) {
    			return new BestMove<M>(-evaluator.mate() - depth);
    		} else {
    			return new BestMove<M>(-evaluator.stalemate());
    		}
    	}
    	
    	return bestMove;
    }
}