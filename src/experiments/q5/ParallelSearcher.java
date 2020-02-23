package experiments.q5;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;
import cse332.exceptions.NotYetImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import chess.bots.BestMove;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    private static ForkJoinPool POOL;
    private static final int divideCUTOFF = 4;
    
    public void setPool(int k) {
    	POOL = new ForkJoinPool(k);
    }
    
    public M getBestMove(B board, int myTime, int opTime) {
        List<M> moves = board.generateMoves();
        return POOL.invoke(new SearchTask(0, moves.size(), moves, ply, board, false)).move;
    }

    private class SearchTask extends RecursiveTask<BestMove<M>> {
        int lo, hi;
        List<M> moves;
        int depth;
        B board;
        boolean copy;

        // divide and conquer
        // until you reach 2 and you make a best move
        // after you make a best move, keep doing it
        // until you reach the CUTOFF, where you call minmax
        // and return the bestMove
        public SearchTask(int lo, int hi, List<M> moves, int depth, B board, boolean copy) {
            this.board = board;
            this.lo = lo;
            this.hi = hi;
            this.moves = moves;
            this.depth = depth;
            this.copy = copy;
        }

        @Override
        protected BestMove<M> compute() {
        	if (moves.isEmpty()) {
        		return SimpleSearcher.minimax(evaluator, board, depth);
        	}
            if (copy) {
                this.board = board.copy();
                board.applyMove(moves.get(lo));
            }
            if (depth <= cutoff) {
                return SimpleSearcher.minimax(evaluator, board, depth);
            }
            if (copy) {
                this.lo = 0;
                this.moves = board.generateMoves();
                this.hi = moves.size();
            }
            if (hi - lo <= divideCUTOFF) { // sequential cut off, fork them sequentially
            	if (hi == lo) {
            		return new BestMove<>(-(evaluator.infty()));
            	}
                List<SearchTask> list = new ArrayList<>();
                // do a 2 for loop, fork all of them, except the last one and then join
                for (int i = lo; i < hi; i++) {
                    SearchTask cur = new SearchTask(i, hi, moves, depth - 1, board, true);
                    if (i != hi - 1) {
                        cur.fork();
                    }
                    list.add(cur);
                }
                BestMove<M> lastMove = list.get(list.size() - 1).compute();
                lastMove = lastMove.negate();
                int bestValue = -(evaluator.infty());
                int index = 0;
                int i = 0;
                for (SearchTask s : list) {
                    if (i != list.size() - 1) {
                        BestMove<M> curMove = s.join();
                        curMove = curMove.negate();
                        if (curMove.value > bestValue) {
                            index = i + lo;
                            bestValue = curMove.value;
                        }
                    }
                    i++;
                }
                if (lastMove.value > bestValue) {
                    index = hi - 1;
                    bestValue = lastMove.value;
                }
                return new BestMove<M>(moves.get(index), bestValue);
            }

            int mid = lo + (hi - lo) / 2;

            SearchTask left = new SearchTask(lo, mid, moves, depth, board, false);
            SearchTask right = new SearchTask(mid, hi, moves, depth, board, false);

            left.fork();

            BestMove<M> rightResult = right.compute();
            BestMove<M> leftResult = left.join();

            return rightResult.value > leftResult.value ? rightResult: leftResult;
        }
    }
}