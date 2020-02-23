package experiments.q6;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import cse332.chess.interfaces.Searcher;
import tests.TestStartingPosition;

public class Question6 {
	public static final String START = "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq -";
	public static final String MID = "r3k2r/pp5p/2n1p1p1/q1pp1p2/5B2/2bP1Q2/PPP2PPP/R4RK1 w kq -";
	public static final String END = "2k3r1/p6p/2n5/3pp3/1pp1P3/2qP4/P1P1K2P/R1R5 b - -";
	
	/* parallel: start 2
	 * 			mid 2
	 * 			end 2
	 * Jamboree: start 3
	 * 			mid 3
	 * 			end 4
	 */
	
	public static void main(String[] args) {
		/*
		System.out.println("Parallel Searcher: ");
		ParallelSearcher<ArrayMove, ArrayBoard> parallel = new ParallelSearcher<ArrayMove, ArrayBoard>();
		parallel.setPool(27);
		test("starting board", START, parallel, 27, 2);
		parallel = new ParallelSearcher<ArrayMove, ArrayBoard>();
		
		parallel.setPool(32);
		test("middle board", MID, parallel, 32, 2);
		parallel = new ParallelSearcher<ArrayMove, ArrayBoard>();
		
		parallel.setPool(26);
		test("ending board", END, parallel, 26, 2);
		
		
		System.out.println("Jamboree Searcher: ");
		JamboreeSearcher<ArrayMove, ArrayBoard> jamboree = new JamboreeSearcher<ArrayMove, ArrayBoard>();
		jamboree.setPool(16);
		test("starting board", START, jamboree, 16, 3);
		
		jamboree = new JamboreeSearcher<ArrayMove, ArrayBoard>();
		jamboree.setPool(32);
		test("middle board", MID, jamboree, 32, 3);
		
		jamboree = new JamboreeSearcher<ArrayMove, ArrayBoard>();
		jamboree.setPool(28);
		test("ending board", END, jamboree, 28, 4);
		*/
		/*
		System.out.println("Alphabeta Searcher: ");
		AlphaBetaSearcher<ArrayMove, ArrayBoard> alpha = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
		test("starting board", START, alpha, 0, 3);
		
		alpha = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
		test("middle board", MID, alpha, 0, 3);
		
		alpha = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
		test("ending board", END, alpha, 0, 3);
		*/
		System.out.println("Simple Searcher: ");
		SimpleSearcher<ArrayMove, ArrayBoard> simple = new SimpleSearcher<ArrayMove, ArrayBoard>();
		test("starting board", START, simple, 0, 3);
		
		simple = new SimpleSearcher<ArrayMove, ArrayBoard>();
		test("middle board", MID, simple, 0, 3);
		
		simple = new SimpleSearcher<ArrayMove, ArrayBoard>();
		test("ending board", END, simple, 0, 3);
	}
	
	public static void test(String boardType, String board, Searcher<ArrayMove, ArrayBoard> searcher, int k, int cutoff) {
		System.out.println(boardType + ": ");
        	double totalTime = 0;
        	for (int j = 0; j < 20; j++) { // 5 trials
        		long startTime = System.currentTimeMillis();
        		TestStartingPosition.getBestMove(board, searcher, 5, cutoff);
                long endTime = System.currentTimeMillis();
                if (j >= 3) { // Throw away first 2 runs to exclude JVM warmup
                    totalTime += (endTime - startTime);
                }
        	}
        	System.out.println("time time : " + totalTime);
	}
}
