package minimax;

import lombok.Getter;


import org.apache.commons.lang3.tuple.Pair;

import utils.StringFormatting;
import ai.game_abstractions.EvaluationFucntion;
import ai.game_abstractions.MoveGenerator;



public class FourRowNegaMaxPlayer {
	private double gametime;
	private double gametimeRemaining;

	@Getter
	private int currentMove = 0;

	/** Estimated length of game in moves */
	private final int estimatedGameLength = 10;

	//NegaMax<FourRowState, FourRowMove, MoveGenerator<FourRowState, FourRowMove>, FourRowRules, EvaluationFucntion<FourRowState>> negaMax;
	//using custom NegaMax
	@Getter
	CustomNegaMax negaMax;
	private int iterativeDeepeningStep = 1;

	private final int maxDepth =30 ;
	
	//constructor of generic NegaMax
//	public FourRowNegaMaxPlayer(double timeForGameInSeconds, EvaluationFucntion<FourRowState> evaluationFunction) {
//	    this.gametime = timeForGameInSeconds;
//	    this.gametimeRemaining = timeForGameInSeconds;
//	    FourRowMoveGenerator moveGenerator = new FourRowMoveGenerator();
//	    FourRowRules rules = new FourRowRules();
//	    
//		negaMax = new NegaMax<FourRowState, FourRowMove, MoveGenerator<FourRowState, FourRowMove>, FourRowRules, EvaluationFucntion<FourRowState>>(
//				moveGenerator, rules, evaluationFunction);
//		
//		
//		
//	  }
	public void setDeepeningStep(int step){
		iterativeDeepeningStep =step;
	}
	//using custom negaMax instead of Generic
	public FourRowNegaMaxPlayer(double timeForGameInSeconds, FourRowEvaluation evaluationFunction) {
	    this.gametime = timeForGameInSeconds;
	    this.gametimeRemaining = timeForGameInSeconds;
	    FourRowMoveGenerator moveGenerator = new FourRowMoveGenerator();
	    FourRowRules rules = new FourRowRules();
	    /*
		negaMax = new NegaMax<FourRowState, FourRowMove, MoveGenerator<FourRowState, FourRowMove>, FourRowRules, EvaluationFucntion<FourRowState>>(
				moveGenerator, rules, evaluationFunction);
		*/
		//using customNegaMax
		negaMax = new CustomNegaMax(moveGenerator,rules,evaluationFunction);
	  }
	
	public void reset() {
		gametimeRemaining = gametime ;
	    currentMove = 0;
	  }
	public void stopSearch(){
		//negaMax.setStopSearch(true);
	}
	
	public Pair<Integer, FourRowMove> play(FourRowState state) {
		System.out.println(" inside negamax play()");
	    int estimatedMovesLeft = Math.max(20, estimatedGameLength - currentMove);
	    System.out.println("rem " + gametimeRemaining + " move " + currentMove);
	    double timeForMove = gametimeRemaining / estimatedMovesLeft;
	    System.out.println("time allowed " + timeForMove);
	    Pair<Integer, FourRowMove> bestOption = runIterativeDeepening(state, timeForMove);
	    gametimeRemaining -= negaMax.getElapsedTimeInMs() / 1000.0; 
	    currentMove++;
	    return bestOption;
	  }
	
//	public Pair<Integer, FourRowMove> runIterativeDeepening(FourRowState state, double timeInSeconds) {
//		Pair<Integer, FourRowMove> bestOption = negaMax
//				.search(state, maxDepth);
//	    double milliNodesPerSeconds = negaMax.getMilliNodesPerSeconds();
//	    System.out.println(StringFormatting.decimalFormat(milliNodesPerSeconds, 3) + "M nps " + " time: "
//	                       + negaMax.getElapsedTimeInMs() + " ms" + "");
//	    int numNodesPruned = negaMax.getNumNodesPruned();
//	    int numNodesExpanded = negaMax.getNumNodesExpanded();
//	    double percentagePruned = 100 * numNodesPruned / (numNodesExpanded + 1); // To avoid accidental division by 0
//	    System.out.println("Nodes searched " + StringFormatting.decimalFormat(numNodesExpanded / 1000000., 2) + "M "
//	                       + StringFormatting.decimalFormat(percentagePruned, 1) + "% pruned ");
//	    return bestOption;
//	  }

	
	public Pair<Integer, FourRowMove> runIterativeDeepening(FourRowState state, double timeInSeconds) {
		Pair<Integer, FourRowMove> bestOption = negaMax
				.searchWithIterativeDeepening(state, iterativeDeepeningStep,
						timeInSeconds, maxDepth);
//	    double milliNodesPerSeconds = negaMax.getMilliNodesPerSeconds();
//	    System.out.println(StringFormatting.decimalFormat(milliNodesPerSeconds, 3) + "M nps " + " time: "
//	                       + negaMax.getElapsedTimeInMs() + " ms" + "");
	    int numNodesPruned = negaMax.getNumNodesPruned();
	    int numNodesExpanded = negaMax.getNumNodesExpanded();
	    double percentagePruned = 100 * numNodesPruned / (numNodesExpanded + 1); // To avoid accidental division by 0
	    System.out.println("Nodes searched " + StringFormatting.decimalFormat(numNodesExpanded / 1.,2) 
	                      +" " + StringFormatting.decimalFormat(percentagePruned, 1) + " % pruned ");
	    return bestOption;
	  }

}
