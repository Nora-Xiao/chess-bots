package experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;
import tests.TestStartingPosition;

public class Question3 {
    public Searcher<ArrayMove, ArrayBoard> whitePlayer;
    public Searcher<ArrayMove, ArrayBoard> blackPlayer;
    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static List<String> fens;
    //public static long nodesInNonparallel;
    public static LongAdder nodes;
    //public static AtomicLong nodes;
    
    private ArrayBoard board;
     
    public static void main(String[] args) {
        Question3 game = new Question3();
        game.play();

        System.out.println("minimax: ");
        Searcher<ArrayMove, ArrayBoard> simple = new SimpleSearcher<ArrayMove, ArrayBoard>();
        for (int i = 1; i <= 5; i++) { //ply from 1 to 5
        //for (int i = 2; i <= 2; i++) {
        	countNodesForNonparallel(simple, i, 5);
        	//nodesInNonparallel = 0;
        	nodes.reset();
        }
      
        System.out.println("parallel: ");
        Searcher<ArrayMove, ArrayBoard> parallel = new ParallelSearcher<ArrayMove, ArrayBoard>();
        for (int i = 1; i <= 5; i++) {
        //for (int i = 1; i <= 3; i++) {
        	countNodesForParallel(parallel, i, i / 2);
        	//nodesInNonparallel = 0;
        	nodes.reset();
        }
       
        System.out.println("alphabeta: ");
        Searcher<ArrayMove, ArrayBoard> alphabeta = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
        for (int i = 1; i <= 5; i++) {
        	countNodesForNonparallel(alphabeta, i, 5);
        	//nodesInNonparallel = 0;
        	nodes.reset();
        	//nodes.set(0);
        }    
        
        System.out.println("jamboree: ");
        Searcher<ArrayMove, ArrayBoard> jamboree = new JamboreeSearcher<ArrayMove, ArrayBoard>();
        for (int i = 1; i <= 5; i++) {
        	countNodesForParallel(jamboree, i, i / 2);
        	//nodesInNonparallel = 0;
        	nodes.reset();
        	//nodes.set(0);
        }
    }
    
    public static void countNodesForNonparallel(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
    	//System.out.println(nodes.sum());
    	for (String fen : fens) {
    		TestStartingPosition.getBestMove(fen, searcher, depth, cutoff);
    		//System.out.println(nodesInNonparallel);
    		//System.out.println(nodes.sum());
    	}
    	//double average = (double) nodesInNonparallel / fens.size();
    	double average = (double) nodes.sum() / fens.size();
    	//double average = nodes.doubleValue() / fens.size();
		System.out.println("number of nodes with depth " + depth + ": " + average);
    }
    
    public static void countNodesForParallel(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
    	//System.out.println(nodesInNonparallel);
    	//System.out.println(nodes.sum());
    	for (String fen : fens) {
    		TestStartingPosition.getBestMove(fen, searcher, depth, cutoff);
    		//System.out.println(nodesInNonparallel);
    		//System.out.println(nodesInParallel.sum());
    	}
    	//double average = (double) (Long.sum(nodesInNonparallel, nodesInParallel.sum())) / fens.size();
    	double average = (double) nodes.sum() / fens.size();
    	//double average = nodes.doubleValue() / fens.size();
		System.out.println("number of nodes with depth " + depth + ": " + average);
    }

    public Question3() {
        setupWhitePlayer(new chess.bots.SimpleSearcher<ArrayMove, ArrayBoard>(), 3, 3);
        setupBlackPlayer(new chess.bots.AlphaBetaSearcher<ArrayMove, ArrayBoard>(), 4, 4);
        fens = new ArrayList<>();
        //nodesInNonparallel = 0;
        nodes = new LongAdder();
        //nodes = new AtomicLong();
    }
    
    public void play() {
       this.board = ArrayBoard.FACTORY.create().init(STARTING_POSITION);
       Searcher<ArrayMove, ArrayBoard> currentPlayer = this.blackPlayer;
       
       int turn = 0;
       
       /* Note that this code does NOT check for stalemate... */
       //boolean hasMoves = board.generateMoves().size() > 0;
       //boolean stalemate = !hasMoves && !board.inCheck();
       while (!board.inCheck() || board.generateMoves().size() > 0) {
       //while (!board.inCheck() || hasMoves || !stalemate) {
           currentPlayer = currentPlayer.equals(this.whitePlayer) ? this.blackPlayer : this.whitePlayer;
           fens.add(board.fen());
           //System.out.printf("%3d: " + board.fen() + "\n", turn);
           this.board.applyMove(currentPlayer.getBestMove(board, 1000, 1000));
           turn++;
       }
    }
    
    public Searcher<ArrayMove, ArrayBoard> setupPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        return searcher; 
    }
    public void setupWhitePlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.whitePlayer = setupPlayer(searcher, depth, cutoff);
    }
    public void setupBlackPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.blackPlayer = setupPlayer(searcher, depth, cutoff);
    }
}
