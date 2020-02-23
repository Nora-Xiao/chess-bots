package experiments.q5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    private static ForkJoinPool POOL;
    private static final int divideCUTOFF = 4;
    private static final double PERCENTAGE_CUTOFF = 0.4;
    
    public void setPool(int k) {
    	POOL = new ForkJoinPool(k);
    }
    
    public M getBestMove(B board, int myTime, int opTime) {
        List<M> moves = board.generateMoves();
        int infinity = evaluator.infty();
        return POOL.invoke(new SearchTask(0, moves.size(), moves, ply, board, false, -infinity, infinity)).move;
    }

    private class SearchTask extends RecursiveTask<BestMove<M>> {
        int lo, hi;
        List<M> moves;
        int depth;
        B board;
        boolean copy;
        int alpha;
        int beta;

        // divide and conquer
        // until you reach 2 and you make a best move
        // after you make a best move, keep doing it
        // until you reach the CUTOFF, where you call minmax
        // and return the bestMove
        public SearchTask(int lo, int hi, List<M> moves, int depth, B board, boolean copy, int alpha, int beta) {
            this.board = board;
            this.lo = lo;
            this.hi = hi;
            this.moves = moves;
            this.depth = depth;
            this.copy = copy;
            this.alpha = alpha;
            this.beta = beta;
        }

        @Override
        protected BestMove<M> compute() {
        	if (moves.isEmpty()) {
        		return AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
        	}
            if (copy) {
                this.board = board.copy();
                board.applyMove(moves.get(lo));
            }
            if (depth <= cutoff) {
                return AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
            }
            if (copy) {
                this.lo = 0;
                this.moves = board.generateMoves();
                //Collections.sort(moves, (m1, m2)-> -Boolean.compare(m1.isCapture(), m2.isCapture()));
                if(moves.isEmpty()) {
                    return AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
                }
                this.hi = moves.size();
            }

            BestMove<M> bestMove = new BestMove<M>(-evaluator.infty()); // -evaluator.infty()
            // compute some of them directly and divide and conquer the rest of the moves list
            if (lo == 0 && hi == moves.size()) { // when you have a new board, you want to compute some part of it directly
            	//Collections.sort(moves, (m1, m2)-> -Boolean.compare(m1.isCapture(), m2.isCapture()));
            	for (int i = lo; i < PERCENTAGE_CUTOFF * hi; i++) {
            		//AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
                    // compute the moves in sequence
                    M move = moves.get(i);
                    board.applyMove(move);
                    List<M> nextMoves = board.generateMoves();
                    
                    SearchTask thisTask = new SearchTask(0, nextMoves.size(), nextMoves, depth - 1, board, false, -beta, -alpha);
                    int value = thisTask.compute().negate().value;
                    board.undoMove();
                    if (value > alpha) {
                        alpha = value;
                        bestMove.move = move;
                        bestMove.value = alpha;
                    }
                    if (alpha >= beta) {
                        return bestMove;
                    }
                }
                lo = (int) (PERCENTAGE_CUTOFF * hi);
            }
            // divide and conquer the rest of the moves list
            // reach the divideCUTOFF, fork them sequentially except the last one, compute the last one using
            // the current thread
            if (hi - lo <= divideCUTOFF) {
            	
            	if (hi == lo) {
            		return new BestMove<M>(-evaluator.infty());
            	}
            	
                List<SearchTask> list = new ArrayList<>();
                // do a 2 for loop, fork all of them, except the last one and then join
                list.add(new SearchTask(lo, hi, moves, depth - 1, board, true, -beta, -alpha));
                for (int i = lo + 1; i < hi; i++) {
                    SearchTask cur = new SearchTask(i, hi, moves, depth - 1, board, true, -beta, -alpha);
                    cur.fork();
                    list.add(cur);
                }
                //System.err.println("Moves size: " + moves.size() + " - Lo: " + lo + " - Hi: " + hi);
                BestMove<M> lastMove = list.get(0).compute();
                lastMove = lastMove.negate();
                int i = 0;
                if (lastMove.value > alpha) {
                    alpha = lastMove.value;
                    bestMove.move = moves.get(lo);
                    bestMove.value = alpha;
                }
                if (alpha >= beta) {
                    return bestMove;
                }
                for (SearchTask s : list) {
                	if (i != 0) {
                    BestMove<M> curMove = s.join();
                    curMove = curMove.negate();
                    if (curMove.value > alpha) {
                        alpha = curMove.value;
                        bestMove.move = moves.get(i + lo);
                        bestMove.value = alpha;
                    }
                    if (alpha >= beta) {
                        return bestMove;
                    }
                	}
                    i++;
                }
                return bestMove;
            }
            int mid = lo + (hi - lo) / 2;

            SearchTask left = new SearchTask(lo, mid, moves, depth, board, false, alpha, beta);
            SearchTask right = new SearchTask(mid, hi, moves, depth, board, false, alpha, beta);

            left.fork();

            BestMove<M> rightResult = right.compute();
            BestMove<M> leftResult = left.join();

            bestMove =  bestMove.value > leftResult.value ? bestMove : leftResult;
            bestMove = bestMove.value > rightResult.value ? bestMove : rightResult;
            return bestMove;
        }
    }
}
