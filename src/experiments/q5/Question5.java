package experiments.q5;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import cse332.chess.interfaces.Searcher;
import tests.TestStartingPosition;

public class Question5 {
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
		System.out.println("Parallel Searcher: ");
		int[] possibleK = new int[] {2, 16, 32};
		int[] possibleK2 = new int[]{14, 15, 16, 17};
		int[] possibleK3 = new int[]{27};
		int[] possibleK4 = new int[]{30};
		int[] possibleK5 = new int[]{25};
		int[] possibleK6= new int[]{31};
		for (int k : possibleK2) {
			/*
			ParallelSearcher<ArrayMove, ArrayBoard> parallel = new ParallelSearcher<ArrayMove, ArrayBoard>();
			parallel.setPool(k);
			test("starting board", START, parallel, k, 2);
			test("middle board", MID, parallel, k, 2);
			test("ending board", END, parallel, k, 2);
			*/
			JamboreeSearcher<ArrayMove, ArrayBoard> jamboree = new JamboreeSearcher<ArrayMove, ArrayBoard>();
			jamboree.setPool(k);
			test("starting board", START, jamboree, k, 3);
			//test("middle board", MID, jamboree, k, 3);
			//test("ending board", END, jamboree, k, 4);
		}
	}
	
	public static void test(String boardType, String board, Searcher<ArrayMove, ArrayBoard> searcher, int k, int cutoff) {
		System.out.println(boardType + ": ");
        	double totalTime = 0;
        	for (int j = 1; j <= 5; j++) { // 5 trials
        		long startTime = System.currentTimeMillis();
        		TestStartingPosition.getBestMove(board, searcher, 5, cutoff);
                long endTime = System.currentTimeMillis();
                if (j >= 3) { // Throw away first 2 runs to exclude JVM warmup
                    totalTime += (endTime - startTime);
                }
        	}
        	System.out.println("time for processors " + k + ": " + totalTime);
	}
}
