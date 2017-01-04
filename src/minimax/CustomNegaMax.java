package minimax;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.tuple.Pair;

import utils.StringFormatting;
//import ai.abstractions.GameRules.Player;
import ai.search_techniques.SearchOutOfTimeException;

public class CustomNegaMax {
	private FourRowMoveGenerator moveGenerator;
	private FourRowRules rules;
	//private FourRowEvaluation evaluationFunction;

	@Getter
	private int numNodesExpanded;
	private int pairings = 0;
	@Getter
	private int numNodesPruned;

	private long startTimeInMs;

	@Getter
	private long elapsedTimeInMs;
	@Getter
	private int threatSearchNodesExp;

	private long allowedTimeInMs = Long.MAX_VALUE;
	//private FourRowMove bestone;

	@Getter
	@Setter
	FastPairing pairing;
	@Setter
	private int captureMoveDepthReduction = 0;
	
	private long time;
	private boolean threat;

	// private HashMap<FourRowState, Pair<Integer, FourRowMove>> pairingsTable =
	// new HashMap<>();
	private HashMap<FourRowState, Pair<Integer, FourRowMove>> hashTable = new HashMap<>();
	private HashMap<FourRowState, Pair<Integer, FourRowMove>> winsTable = new HashMap<>();
	// HashMap<>();
	final boolean useHashTable = true;

	public CustomNegaMax(FourRowMoveGenerator moveGenerator, FourRowRules rules,
			FourRowEvaluation evaluationFunction) {
		super();
		this.moveGenerator = moveGenerator;
		this.rules = rules;
		threatSearchNodesExp=0;
		//this.evaluationFunction = evaluationFunction;
	}
	
	
	public Pair<Integer, FourRowMove> search(FourRowState state, int maxDepth) {
		reset();
		if (rules.isTerminal(state)) {
			return null;
		}
		int player;
		if(state.getBoard().getTurn())
			player=300;
		else
			player=0;
		Pair<Integer, FourRowMove> best = recursiveNegamax(state, 10 * maxDepth,
				player-300, player);
//		Pair<Integer, FourRowMove> best = recursiveNegamax(state, 10 * maxDepth,
//				-300, 300);
		elapsedTimeInMs = System.currentTimeMillis() - startTimeInMs;
		// System.out.println("hashTable " + hashTable.size());
		return best;
	}
 

	public Pair<Integer, FourRowMove> searchWithIterativeDeepening(FourRowState state,
			int iterativeDeepeningStep, double timeLimitInSeconds, int maxDepth) {

		boolean output = true;
		// reset() was here
		reset();
		allowedTimeInMs = (long) (1000 * timeLimitInSeconds);
		Pair<Integer, FourRowMove> best = null;
		for (int depth = 1; depth <= maxDepth; depth += iterativeDeepeningStep) {
		

			try {

			 	hashTable = new HashMap<>();
				long currentDepthStartTime = System.currentTimeMillis();
				int player ;
				if (state.getBoard().getTurn())
					player = 300;
				else
					player = 0;
				best = recursiveNegamax(state, 10 * depth,
 player - 300, player);
				//i save the best move in each depth so i can put it first in the moveGenerator and start
				//analyze it first , so i prune more moves.
//				if (best.getRight()!=null)
//					bestone = best.getRight();
				long currentDepthElapsedTime = System.currentTimeMillis()
						- currentDepthStartTime;
				String eval = StringFormatting.decimalFormat(
						1.0 * best.getLeft(), 3);
				int height = state.getBoard().getHeight();
				//int width = state.getBoard().getWidth();
				time+=currentDepthElapsedTime;
				printer(output, best, depth, currentDepthElapsedTime, eval,
						height);
				
				// check if game is over and stop

				// if (best.getLeft() == 0) {
				// pairing = new FastPairing(state.getBoard());
				// pairing.printer();
				// break;
				// }

				if (best.getLeft() == 300 || best.getLeft() == -300)
					break;
			} catch (SearchOutOfTimeException e) {
				if (output)
					System.out.println("Out of time, depth finished "
							+ (depth - 1));
				break;
			}
		}
		elapsedTimeInMs = System.currentTimeMillis() - startTimeInMs;

		return best;
	}

	private void reset() {
		pairings = 0;
		hashTable = new HashMap<>();
		numNodesExpanded = 0;
		numNodesPruned = 0;
		startTimeInMs = System.currentTimeMillis();
		threatSearchNodesExp=0;

	}
	
	private Pair<Integer, FourRowMove> recursiveNegamax(FourRowState state,
			int remainingDepth, double alpha, double beta ) {


//		Player activePlayer = rules.getActivePlayer(state);
		boolean activePlayer = state.getBoard().getTurn()? true:false;
		int scoreMultiplier = state.getBoard().getTurn()?1:-1;
		// if (pairingsTable.containsKey(state))
		// return pairingsTable.get(state);
		if (winsTable.containsKey(state)) {
			return winsTable.get(state);
		}
		if (useHashTable && hashTable.containsKey(state)) {
			return hashTable.get(state);
		}


		// Periodically check if we are out of time and terminate
		if (numNodesExpanded % 10000 == 0
				&& System.currentTimeMillis() - startTimeInMs > allowedTimeInMs) {
			throw new SearchOutOfTimeException();
		}

		// Whether we find the exact value of a position or an alpha/beta bound
		boolean exactBound = true;
		int numberOfThreatsW = 0;
		int numberOfThreatsB = 0;
		int terminalValue =0;
		if (!state.getBoard().getTurn()) {
			numberOfThreatsB = state.getBoard().numThreats(true);
			if (numberOfThreatsB == 2){
				terminalValue = scoreMultiplier*300;
				return Pair.of(terminalValue, null);
			}
		} else {
			numberOfThreatsW = state.getBoard().numThreats(false);
			if (numberOfThreatsW == 2){
				terminalValue = scoreMultiplier*-300;
				return Pair.of(terminalValue,null);
			}
			else if (state.getBoard().getMoveNumber() > 5 && remainingDepth<=0 && terminalValue==0 ) {
				if (numberOfThreatsW == 0 ) {
					if (state.getBoard().checkAtariBarage(state.getBoard(),
							true, 0) == 300){
						threatSearchNodesExp += state.getBoard().getThreatSearchNodesExpanded();
						return Pair.of(scoreMultiplier * 300, null) ;  
					}
					threatSearchNodesExp += state.getBoard().getThreatSearchNodesExpanded();
				}
			}
		}
		if(remainingDepth<=0)
			 return Pair.of(0, null);
//		return false;
//		if (remainingDepth <= 0 || rules.isTerminal(state)) {
//			//state.getBoard().boardPrinter();
//			// TODO Constant for NO_MOVE
//
//			int terminalValue = 0;
//			if (remainingDepth > 0) {
//				if (!state.getBoard().getTurn())
//					terminalValue = scoreMultiplier * 300;
//				else
//					terminalValue = scoreMultiplier *-300;
//			}
//			else if (state.getBoard().getTurn()) {
//				if (state.getBoard().getMoveNumber() > 6) {
//					numberOfThreatsW = state.getBoard().findOneThreat(false);
//					if (numberOfThreatsW == 0) {
//						if (state.getBoard().checkAtariBarage(state.getBoard(),
//								true, 0) == 300){
//							threatSearchNodesExp += state.getBoard().getThreatSearchNodesExpanded();
//							terminalValue = scoreMultiplier * 300;  
//						}
//					}
//				}
//			}
//			// if (terminalValue == 0) {
//			// if (remainingDepth > 1 && state.getBoard().getMoveNumber() < 17)
//			// {
//			// pairingsTable.put(state,
//			// Pair.of(terminalValue, (FourRowMove) null));
//			// }
//			// pairings++;
//			// if (pairings % 30000000 == 0) {
//			// state.getBoard().boardPrinter();
//			// pairing = new FastPairing(state.getBoard());
//			// pairing.printer();
//			// System.out.println(pairings + " pairings done");
//			// }
//			// }
//
//			return Pair.of(terminalValue, null);
//			
//		}

		FourRowState clonedState = state.copy();
		
		// Instead of the typical negation of the opponent's score the score
		// multiplier is used to support games where
		// the players don't always alternate turns
		FourRowMove bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		
		threat = moveGenerator.getThreat();
		for (FourRowMove move : moveGenerator.getLegalMoves(state)) {
			int adjustedDepth =10; 

			FourRowState newState = rules.applyMove(state, move);
			Pair<Integer, FourRowMove> result;
			int value;
			int newRemainingDepth = remainingDepth - adjustedDepth;

			// ////// CUT /////////////////////////////////////////////////
			// Cut cut = new Cut(newState.getBoard());
			// if (cut.getCut() != -1 && state.getBoard().getHeight()>=9) {
			// // System.out.println(cut.getCut());
			//
			// Bitboard a = new Bitboard(cut.getCut() + 3);
			// Bitboard b = new Bitboard(state.getBoard().getHeight()
			// - cut.getCut());
			// long big = 1L;
			// long small;
			// for (int i = 0; i < cut.getCut() + 3; i++) {
			// small = 1L << i;
			// big = big | small;
			// }
			// a.setBlackpieces(big & state.getBoard().getBlackpieces());
			// a.setBlackpieces(big & state.getBoard().getWhitepieces());
			//
			// small = 0;
			// big = 0;
			// for (int i = cut.getCut(); i < state.getBoard().getHeight(); i++)
			// {
			// small = 1L << i;
			// big = big | small;
			// }
			// b.setBlackpieces(state.getBoard().getBlackpieces() & ~big);
			// b.setWhitepieces(state.getBoard().getWhitepieces() & ~big);
			//
			// a.setBlackToplay(state.getBoard().getTurn());
			// b.setBlackToplay(state.getBoard().getTurn());
			//
			// a.setLowerBound(0);
			// a.setUperBound(a.getHeight());
			//
			// b.setLowerBound(0);
			// b.setUperBound(b.getHeight());
			// // System.out.println("a board");
			// // a.boardPrinter();
			// // System.out.println("b board");
			// // b.boardPrinter();
			// FourRowState aState = new FourRowState(a);
			// FourRowState bState = new FourRowState(b);
			// if (rules.getActivePlayer(newState) != activePlayer) {
			// // result = Math.max(a, b)
			// result = recursiveNegamax(aState, newRemainingDepth, -beta,
			// -alpha);
			// int value1 = -result.getLeft();
			//
			// Pair<Integer, FourRowMove> result2 = recursiveNegamax(
			// bState, newRemainingDepth, -beta, -alpha);
			// int value2 = -result.getLeft();
			// value = Math.max(value1, value2);
			// } else {
			// result = recursiveNegamax(aState, newRemainingDepth, alpha,
			// beta);
			// int value1 = result.getLeft();
			// Pair<Integer, FourRowMove> result2 = recursiveNegamax(
			// bState, newRemainingDepth, alpha, beta);
			// int value2 = result.getLeft();
			// value = Math.max(value1, value2);
			// }
			// }
			// // ////////// end CUT /////////////////////////
			// else {
				if (newState.getBoard().getTurn() != activePlayer) {
					// Alternating moves

					result = recursiveNegamax(newState, newRemainingDepth,
							-beta, -alpha);
					value = -result.getLeft();

				} else {

					result = recursiveNegamax(newState, newRemainingDepth,
							alpha, beta);
					value = result.getLeft();
				}
			// }

			if (value > bestScore) {
				bestScore = value;
				bestMove = move;
			}
			alpha = Math.max(alpha, bestScore);
			numNodesExpanded++;
			if (alpha >= beta) {
				numNodesPruned++;
				exactBound = false;
				break;
			}

			// Reset the state since we've applied a move

			state = clonedState;
			
		}

		Pair<Integer, FourRowMove> best = Pair.of(bestScore, bestMove);

		if (bestScore == 300 || bestScore==-300)
			winsTable.put(clonedState, best);
		else if (state.getBoard().getMoveNumber() < 19)

			hashTable.put(clonedState, best);
		
		return best;
	}
	
	public double getMilliNodesPerSeconds() {
		return elapsedTimeInMs != 0 ? numNodesExpanded
				/ (1000.0 * elapsedTimeInMs) : 0;
	}

	private void printer(boolean output, Pair<Integer, FourRowMove> best,
			int depth, long currentDepthElapsedTime, String eval, int height) {
		if (output) {
//			if (currentDepthElapsedTime >= 1000
//					&& currentDepthElapsedTime < 60000) {
				int col = best.getRight().getCol();
				String column = "";
				if (col == 0)
					column = "A";
				if (col == 1)
					column = "B";
				if (col == 2)
					column = "C";
				if (col == 3)
					column = "D";
				System.out.println(" D " + depth + " M "
						+ column + (height - best.getRight().getRow())
						+ " eval " + eval + " T " + time
						/ 1000 + " seconds " + " pairings = " + pairings
						+ " n-exp = " + getNumNodesExpanded()
						+ " n-pruned " + numNodesPruned+" TSN-exp "+threatSearchNodesExp +" total exp "+(getNumNodesExpanded()+threatSearchNodesExp));
			//}
		}
//			} else if (currentDepthElapsedTime >= 60000
//					&& currentDepthElapsedTime < 3600000) {
//				int col = best.getRight().getCol();
//				String column = "";
//				if (col == 0)
//					column = "A";
//				if (col == 1)
//					column = "B";
//				if (col == 2)
//					column = "C";
//				if (col == 3)
//					column = "D";
//				System.out.println("depth " + depth + " move col,row = "
//						+ column + (height - best.getRight().getRow())
//						+ " eval " + eval + " time " + currentDepthElapsedTime
//						/ 60000 + " minutes " + " pairings = " + pairings
//						+ " nodes expanded = " + getNumNodesExpanded()
//						+ " nodes pruned " + numNodesPruned+" threat S nodes expanded "+threatSearchNodesExp);
//
//			} else if (currentDepthElapsedTime >= 3600000) {
//				int col = best.getRight().getCol();
//				String column = "";
//				if (col == 0)
//					column = "A";
//				if (col == 1)
//					column = "B";
//				if (col == 2)
//					column = "C";
//				if (col == 3)
//					column = "D";
//				System.out.println("depth " + depth + " move col,row = "
//						+ column + (height - best.getRight().getRow())
//						+ " eval " + eval + " time " + currentDepthElapsedTime
//						/ 3600000 + " hours " + " pairings = " + pairings
//						+ " nodes expanded = " + getNumNodesExpanded()
//						+ " nodes pruned " + numNodesPruned+" threat S nodes expanded "+threatSearchNodesExp);
//
//			} else {
//				int col = best.getRight().getCol();
//				String column = "";
//				if (col == 0)
//					column = "A";
//				if (col == 1)
//					column = "B";
//				if (col == 2)
//					column = "C";
//				if (col == 3)
//					column = "D";
//				System.out.println("depth " + depth + " move col,row = "
//						+ column + (height - best.getRight().getRow())
//						+ " eval " + eval + " time " + currentDepthElapsedTime
//						+ " miliseconds " + " pairings = " + pairings
//						+ " nodes expanded = " + getNumNodesExpanded()
//						+ " nodes pruned " + numNodesPruned+" threat S nodes expanded "+threatSearchNodesExp);
//
//			}
//
//		}
	}

}
