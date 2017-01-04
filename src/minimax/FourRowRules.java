package minimax;

//import ai.game_abstractions.GameRules;

//import ai.abstractions.GameRules;

public class FourRowRules {
	
	//private FastPairing pairing;
	//private int pairings;
	//@Override
//	public ai.abstractions.GameRules.Player getActivePlayer(FourRowState state) {
//		return state.getBoard().getTurn() ? Player.MAX : Player.MIN;
//
//	}
	public FourRowRules(){
		
	}

	
	public boolean isTerminal(FourRowState state) {

		int numberOfThreatsW = 0, numberOfThreatsB = 0;
		if (!state.getBoard().getTurn()) {
			numberOfThreatsB = state.getBoard().numThreats(true);
			if (numberOfThreatsB == 2)
				return true;
		} else {
			numberOfThreatsW = state.getBoard().numThreats(false);
			if (numberOfThreatsW == 2)
				return true;
		}
		return false;
		// if (state.getBoard().getMoveNumber() > 13) {
		// if (state.getBoard().numThreats(true) == 0
		// && state.getBoard().numThreats(false) == 0) {
		// pairing = new FastPairing(state.getBoard());
		// if (pairing.getFinalXOR() != null) {
		// pairings++;
		// if (pairings % 40000000 == 0) {
		// System.out.println(pairings);
		// pairing.printer();
		// }
		// return true;
		// }
		// }
		// }


	}
	
	
	public FourRowState applyMove(FourRowState state, FourRowMove move) {
		FourRowState newState = state.copy();
	
		newState.getBoard().addStone(move.getRow(), move.getCol());
		
		return newState;
	}

}
