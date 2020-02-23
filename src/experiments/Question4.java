package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import cse332.chess.interfaces.Searcher;
import tests.TestStartingPosition;

public class Question4 {
	public static final String START = "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq -";
	public static final String MID = "r3k2r/pp5p/2n1p1p1/q1pp1p2/5B2/2bP1Q2/PPP2PPP/R4RK1 w kq -";
	public static final String END = "2k3r1/p6p/2n5/3pp3/1pp1P3/2qP4/P1P1K2P/R1R5 b - -";
	
	public static void main(String[] args) {
		Searcher<ArrayMove, ArrayBoard> parallel = new chess.bots.ParallelSearcher<ArrayMove, ArrayBoard>();
		System.out.println("Parallel Searcher: ");
		test("starting board", START, parallel);
		test("middle board", MID, parallel);
		test("ending board", END, parallel);
	
		Searcher<ArrayMove, ArrayBoard> jamboree = new chess.bots.JamboreeSearcher<ArrayMove, ArrayBoard>();
		System.out.println("Jamboree searcher: ");
		test("starting board", START, jamboree);
		test("middle board", MID, jamboree);
		test("ending board", END, jamboree);
	}
	
	public static void test(String boardType, String board, Searcher<ArrayMove, ArrayBoard> searcher) {
		System.out.println(boardType + ": ");
        for (int i = 0; i <= 5; i++) { //cutoff = 0, 1, 2, 3, 4, 5
		//for (int i = 1; i <= 5; i++) {
        	double totalTime = 0;
        	for (int j = 1; j <= 5; j++) { // 5 trials
        		long startTime = System.currentTimeMillis();
        		TestStartingPosition.getBestMove(board, searcher, 5, i);
                long endTime = System.currentTimeMillis();
                if (j >= 3) { // Throw away first 2 runs to exclude JVM warmup
                    totalTime += (endTime - startTime);
                }
        	}
        	System.out.println("time for cutoff " + i + ": " + totalTime);
        }
	}
}
