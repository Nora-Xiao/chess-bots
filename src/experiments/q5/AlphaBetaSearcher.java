package experiments.q5;

import java.util.Collections;
import java.util.List;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class AlphaBetaSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
    	/* Calculate the best move */
    	int infinity = evaluator.infty();
        BestMove<M> best = alphabeta(this.evaluator, board, ply, -infinity, infinity);
        return best.move;
    }
    
    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphabeta(Evaluator<B> evaluator, B board, int depth, int alpha, int beta) {
    	if (depth == 0) {
    		// evaluate tells us the value of the current position
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	
    	BestMove<M> bestMove = new BestMove<M>(alpha);
    	List<M> moves = board.generateMoves();
    	//Collections.sort(moves, (m1, m2)-> -Boolean.compare(m1.isCapture(), m2.isCapture()));
    	for (M move : moves) {
    		board.applyMove(move);
    		BestMove<M> possibleMove = alphabeta(evaluator, board, depth - 1, -beta, -alpha);
    		possibleMove = possibleMove.negate();
    		int value = possibleMove.value;
    		board.undoMove();
    		// If value is between alpha and beta, we've found a new lower bound
    		if (value > alpha) {
    			alpha = value;
    			possibleMove.move = move;
    			bestMove = possibleMove;
    		}
    		// If the value is bigger than beta, we won't actually be able to get this move
    		if (alpha >= beta) {
    			return bestMove;
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