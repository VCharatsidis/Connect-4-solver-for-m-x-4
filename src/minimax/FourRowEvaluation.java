package minimax;

import ai.game_abstractions.EvaluationFucntion;


		
public class FourRowEvaluation extends EvaluationFucntion<FourRowState> {
	private FastPairing pairing ;
	
	@Override
	public int evaluate(FourRowState state) {
		boolean turn = state.getBoard().getTurn();
		
//		if (state.getBoard().isGameOver(state.getBoard().getBlackpieces()))
//			return 300;
//		if (state.getBoard().isGameOver(state.getBoard().getWhitepieces()))
//			return 0;
		int numberOfThreatsB=0;

		if(!turn){
			numberOfThreatsB = state.getBoard().numThreats(true);
			if(numberOfThreatsB==2)
				return 300;
		}
		//numberOfThreatsW = state.getBoard().numThreats(false);
		
		//single threat
//		if (numberOfThreatsB>0){
//			if(turn)
//				return 300;
//		}
//		if (numberOfThreatsW>0){
//			if(!turn)
//				return 0;
//		}
		//double threat for score 4
		
		
//		if(numberOfThreatsW==2)
//			return 0;
//		if(turn && numberOfThreatsW==0){	
//			if(state.getBoard().checkAtariBarage(state.getBoard(),true)==300){
//				
//				return 300;
//			}
//		}
//		if(!turn && numberOfThreatsB==0){
//			if(state.getBoard().checkAtariBarage(state.getBoard(),false)==-300){
//				
//				return 0;
//			}
//		} 
//		if( state.getBoard().getMoveNumber()>13  && numberOfThreatsB==0 && numberOfThreatsW==0  ){
//			pairing = new FastPairing(state.getBoard());
//			if(pairing.getFinal  XOR()!=null){
//				return  0;
//			}
//		}  
		
		
		return 0  ;
		
	}

}

