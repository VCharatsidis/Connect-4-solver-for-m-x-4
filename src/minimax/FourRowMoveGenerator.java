package minimax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import BitMap.Bitboard;


public class FourRowMoveGenerator {
	
	private boolean threat;
	public boolean getThreat(){
		return threat;
	}
	public Iterable<FourRowMove> getLegalMoves(FourRowState state) {
		threat=false;
		List<FourRowMove> moves = new ArrayList<FourRowMove>();
		if(state.getBoard().getMoveNumber()==0){
			if(state.getBoard().getHeight()%2==0){
				moves.add(new FourRowMove(state.getBoard().getHeight()/2,0));
				moves.add(new FourRowMove(state.getBoard().getHeight()/2,1));
			}else{
				moves.add(new FourRowMove((state.getBoard().getHeight()-1)/2,1));
				moves.add(new FourRowMove((state.getBoard().getHeight()-1)/2,0));
			}
		}
		boolean blackToplay = state.getBoard().getTurn();

		int[] coords;
		//if there is a single threat we dont waste time searching moves 
		
		int uperBound = state.getBoard().getUperBound();
		int lowerBound = state.getBoard().getLowerBound();
		int blackThreats = state.getBoard().numThreats(true);
		int blacksquare = state.getBoard().getS();
		int whiteThreats = state.getBoard().numThreats(false);
		int whitesquare = state.getBoard().getS();
		boolean checkTwoVerticalWhite = state.getBoard().checkTwoVertical(state.getBoard().getWhitepieces());
		int checkTwoVerticalWhiteSquare = state.getBoard().getS();
		boolean checkTwoVertical = state.getBoard().checkTwoVertical(state.getBoard().getBlackpieces());
		int checkTwoVerticalSquare = state.getBoard().getS();
		threat= blacksquare==1;
		if (blackToplay){
			if (blackThreats>0){
				//if there is score 4 return it as only move
				coords = state.getBoard().squareToRowCol(blacksquare);
				if (state.getBoard().isLegal(coords[0], coords[1])){
					moves.add(new FourRowMove(coords[0],coords[1]));
					return moves;
				}
			
			}else if(whiteThreats>0){
				//if opponent threats for score 4 defend as only move	
				coords = state.getBoard().squareToRowCol(whitesquare);
				if (state.getBoard().isLegal(coords[0], coords[1])){
					moves.add(new FourRowMove(coords[0],coords[1]));
					return moves;
				}
				// if we threat for double threat IN COLUMN return it as only
				// move
			} else if (checkTwoVertical) {
				coords = state.getBoard()
						.squareToRowCol(checkTwoVerticalSquare);
				if (state.getBoard().isLegal(coords[0], coords[1])) {
					moves.add(new FourRowMove(coords[0], coords[1]));
					moves.add(new FourRowMove(coords[0] - 3, coords[1]));
					return moves;
				}
			}
				
		}else{
			//the above comments also for white
			// the only filter for white is to make a score 4 if possible and to
			// answer a threat for score 4 .
			if (whiteThreats>0){
				coords = state.getBoard().squareToRowCol(whitesquare);
				if (state.getBoard().isLegal(coords[0], coords[1])){
					moves.add(new FourRowMove(coords[0],coords[1]));
					
					return moves;
				}
			}
			else if (blackThreats>0){
				
				coords = state.getBoard().squareToRowCol(blacksquare);
				if (state.getBoard().isLegal(coords[0], coords[1])){
					moves.add(new FourRowMove(coords[0],coords[1]));

					return moves;
				}
			
			} else if (checkTwoVerticalWhite) {
				coords = state.getBoard().squareToRowCol(
						checkTwoVerticalWhiteSquare);
				if (state.getBoard().isLegal(coords[0], coords[1])) {
					moves.add(new FourRowMove(coords[0] - 3, coords[1]));
					moves.add(new FourRowMove(coords[0], coords[1]));
					return moves;
				}
			}
		}


			// check for move that makes double threats IN COLUMN for a special
			// shape that was not checked above

		int boardwidth = state.getBoard().getWidth();
			boolean checkTwoVerticalSpecial = state.getBoard()
					.checkTwoVerticalSpecialCase(blackToplay);
			int checkTwoVerticalSpecialSquare = state.getBoard().getS();

						if (checkTwoVerticalSpecial) {
							coords = state.getBoard().squareToRowCol(
									checkTwoVerticalSpecialSquare);
							if (state.getBoard().isLegal(coords[0], coords[1])) {
								moves.add(new FourRowMove(coords[0], coords[1]));
								return moves;
							}

		}
			boolean[] checked = new boolean[state.getBoard().getWidth()
					* state.getBoard().getHeight()];
			Arrays.fill(checked, false);
			// check if a move makes double threat in GENERAL if double threat
		// not found single threats
			// are stored as moves

			int threatsRowCol = 0;
			for (int row = uperBound; row <= lowerBound; row++) {
				for (int col = 0; col < boardwidth; col++) {
								
					if (state.getBoard().occupied(row, col) == 1) {
						threatsRowCol = state.getBoard().countThreatsRowCol(
								row, col, blackToplay, 1);

						if (threatsRowCol > 1) {
							if (state.getBoard().isLegal(row, col)) {
								moves.removeAll(moves);
								moves.add(new FourRowMove(row, col));
								return moves;
							}
						} else if (threatsRowCol == 1) {
							
							if (state.getBoard().isLegal(row, col)  ) {

								moves.add(new FourRowMove(row, col));
								checked[row * boardwidth + col] = true;
							}
									}
								}
				}
			}



			// check if opponent threats for double threat in COLUMN , if yes
		// player has 2-4 moves to block .
		if (blackToplay) {

				if (checkTwoVerticalWhite) {

					coords = state.getBoard().squareToRowCol(
							checkTwoVerticalWhiteSquare);
					if (state.getBoard().isLegal(coords[0], coords[1])) {
					if (!checked[coords[0] * state.getBoard().getWidth()
							+ coords[1]])
						moves.add(new FourRowMove(coords[0], coords[1]));
					if (!checked[(coords[0] - 3) * state.getBoard().getWidth()
							+ coords[1]])
						moves.add(new FourRowMove(coords[0] - 3, coords[1]));
					if (!state.getBoard().isLegal(coords[0] + 1, coords[1])) {
						if (state.getBoard().isLegal(coords[0] - 4, coords[1])) {
							if (!checked[(coords[0] - 4)
									* state.getBoard().getWidth() + coords[1]])
								moves.add(new FourRowMove(coords[0] - 4,
										coords[1]));
						}
						}
 else if (!state.getBoard().isLegal(coords[0] - 4,
							coords[1])) {
						if (state.getBoard().isLegal(coords[0] + 1, coords[1])) {

							moves.add(new FourRowMove(coords[0] + 1, coords[1]));
						}
						}
					return moves;
				}

			}
		} else {
				if (checkTwoVertical) {

					coords = state.getBoard().squareToRowCol(
							checkTwoVerticalSquare);
					if (state.getBoard().isLegal(coords[0], coords[1])) {
						moves.add(new FourRowMove(coords[0], coords[1]));
						moves.add(new FourRowMove(coords[0] - 3, coords[1]));
						if (!state.getBoard().isLegal(coords[0] + 1, coords[1])) {
							if (state.getBoard().isLegal(coords[0] - 4,
									coords[1])) {
								moves.add(new FourRowMove(coords[0] - 4,
										coords[1]));
							}
						}
 else if (!state.getBoard().isLegal(coords[0] - 4,
								coords[1])) {
							if (state.getBoard().isLegal(coords[0] + 1,
									coords[1])) {
								moves.add(new FourRowMove(coords[0] + 1,
										coords[1]));
							}
						}
						return moves;
					}
				}
			}
			// check if opponent threats for column double threat SPECIAL CASE ,
			// if yes player has 2 moves to block
		checkTwoVerticalSpecial = state.getBoard().checkTwoVerticalSpecialCase(
				!blackToplay);
		checkTwoVerticalSpecialSquare = state.getBoard().getS();
		if (checkTwoVerticalSpecial) {

			coords = state.getBoard().squareToRowCol(
					checkTwoVerticalSpecialSquare);
			if (state.getBoard().isLegal(coords[0], coords[1])) {
				moves.add(new FourRowMove(coords[0], coords[1]));
				if (state.getBoard().isLegal(coords[0] - 2, coords[1])) {
					moves.add(new FourRowMove(coords[0] - 2, coords[1]));

				}
				if (state.getBoard().isLegal(coords[0] + 2, coords[1])) {
					moves.add(new FourRowMove(coords[0] + 2, coords[1]));
				}
				return moves;
			}

		}
			// check if opponent threats for column double threat in GENERAL and
			// prevent it .
				int counter=0;
				threatsRowCol = 0;
				for (int row = uperBound; row <= lowerBound; row++) {
					for (int col = 0; col < boardwidth; col++) {

						if (state.getBoard().occupied(row, col) == 1) {
							threatsRowCol = state.getBoard()
.countThreatsRowCol(
								row, col, !blackToplay,
											1);

							if (threatsRowCol > 1) {
							
								
								if (state.getBoard().isLegal(row, col)) {
									if(counter>0){
										int k =moves.size();
										for(int z=k-1;z>k-counter-1;z--){
											moves.remove(z);
										}
									}
									
									coords = state
											.getBoard()
											.squareToRowCol(
													state.getBoard()
															.getThreatSquare2());
									if (state.getBoard().isLegal(coords[0],
											coords[1])) {
										moves.add(new FourRowMove(coords[0],
												coords[1]));
									}
									coords = state.getBoard().squareToRowCol(
											state.getBoard().getThreatSquare());
									if (state.getBoard().isLegal(coords[0],
											coords[1])) {
										moves.add(new FourRowMove(coords[0],
												coords[1]));
									}
									moves.add(new FourRowMove(row, col));
									
									return moves;
								}
							}
							
							else if(threatsRowCol==1){
								if(!blackToplay){
								if (state.getBoard().isLegal(row, col)){
									if(!checked[row*state.getBoard().getWidth()+col]){
										checked[row*state.getBoard().getWidth()+col]=true;
										counter++;
										moves.add(new FourRowMove(row, col));
									}
										
								}
							}
							}
						}
					}
					}


		int row;
		int col;


		int rowDown = -1;
		int rowMid = -1;
		int rowUp = -1;
		
		Bitboard firstboard = new Bitboard(state.getBoard());
		
		rowDown = firstboard.squareToRowCol(firstboard.longToSquare(firstboard
				.getBlackLastMove()))[0] + 1;
		rowMid = firstboard.squareToRowCol(firstboard.longToSquare(firstboard
				.getBlackLastMove()))[0];
		rowUp = firstboard.squareToRowCol(firstboard.longToSquare(firstboard
				.getBlackLastMove()))[0] - 1;

		int colMid = firstboard.squareToRowCol(firstboard
				.longToSquare(firstboard.getBlackLastMove()))[1];
		int colLeft = firstboard.squareToRowCol(firstboard
				.longToSquare(firstboard.getBlackLastMove()))[1] - 1;
		int colRight = firstboard.squareToRowCol(firstboard
				.longToSquare(firstboard.getBlackLastMove()))[1] + 1;
		
		int k;
		int j;
		if (blackToplay) {
			
			k = uperBound+1;
			j = lowerBound-1;
//			if(l<rowMid-3)
//				k++;
		} else {
			k =0;
			j = state.getBoard().getHeight()-1;
//			k = uperBound;
//			j = lowerBound;

		}
		boardwidth = state.getBoard().getWidth();

		boolean symetry = false;
		if (state.getBoard().getMoveNumber() < 5) {
			symetry = state.getBoard().symetry();
			if (symetry) {
				j = (state.getBoard().getHeight() - 1) / 2;
			}
		}

		if (blackToplay) {

			for (col = colLeft; col <= colRight; col++) {
				
				if (firstboard.isLegal(rowUp, col)) {
					if (!checked[rowUp * boardwidth + col]) {
						
						checked[rowUp * boardwidth + col] = true;
						if (state.getBoard().goodMoveForBlack(rowUp,
								col, blackToplay) || state.getBoard().countThreatsRowCol(rowUp, col,
										false, 1) > 0){
							moves.add(new FourRowMove(rowUp, col));
							//System.out.println(Math.abs(rowMid-rowMidW)>1);
							
							
						}

						
					}
				}
				if (!(symetry && rowDown > j)) {
					if (firstboard.isLegal(rowDown, col)) {
						if (!checked[rowDown * boardwidth + col]) {
						
							checked[rowDown * boardwidth + col] = true;
							if (state.getBoard().goodMoveForBlack(rowDown,
									col, blackToplay) || state.getBoard().countThreatsRowCol(rowDown, col,
											false, 1) > 0){
								moves.add(new FourRowMove(rowDown, col));
								
							}
						}
					}
				}
				if (firstboard.isLegal(rowMid, col)) {
					if (!checked[rowMid * boardwidth + col]) {
						
						checked[rowMid * boardwidth + col] = true;
						if (state.getBoard().goodMoveForBlack(rowMid,
								col, blackToplay) || state.getBoard().countThreatsRowCol(rowMid, col,
										false, 1) > 0){
							moves.add(new FourRowMove(rowMid, col));
							
						}
					}
				}
	
			}
			for (col = 0; col < boardwidth; col++) {
				if (firstboard.isLegal(rowMid, col)) {
					if (!checked[rowMid * boardwidth + col]) {
						
						checked[rowMid * boardwidth + col] = true;
						if (state.getBoard().goodMoveForBlack(rowMid,
								col, blackToplay) || state.getBoard().countThreatsRowCol(rowMid, col,
										false, 1) > 0){
							moves.add(new FourRowMove(rowMid, col));
							
						}
					}
				}
			}
			if ((rowUp - 1) >= 0 && (rowUp - 1) < state.getBoard().getHeight()) {
				if (!checked[(rowUp - 1) * boardwidth + colMid]) {
					if (firstboard.isLegal(rowUp - 1, colMid)) {
						
						checked[(rowUp - 1) * boardwidth + colMid] = true;
						if (state.getBoard().goodMoveForBlack(
								rowUp - 1, colMid, blackToplay) || state.getBoard().countThreatsRowCol(
										rowUp - 1, colMid, false, 1) > 0) {
							moves.add(new FourRowMove(rowUp - 1, colMid));
							
						}
					}
				}
			}
			if (!symetry &&(rowDown + 1) >= 0
					&& (rowDown + 1) < state.getBoard().getHeight()) {
				if (!checked[(rowDown + 1) * boardwidth + colMid]) {
					if (firstboard.isLegal(rowDown + 1, colMid)) {
						
						checked[(rowDown + 1) * boardwidth + colMid] = true;
						if (state.getBoard().goodMoveForBlack(
								rowDown + 1, colMid, blackToplay) || state.getBoard().countThreatsRowCol(
										rowDown+1, colMid, false, 1) > 0) {
							moves.add(new FourRowMove(rowDown + 1, colMid));
							
						}
					}
				}
			}
		
		}
 else {
	 		
			for (col = colLeft; col <= colRight; col++) {
				
				if (firstboard.isLegal(rowUp, col)) {
					
					if (!checked[rowUp * boardwidth + col]) {
						if(state.getBoard().getMoveNumber()==1){
							moves.add(new FourRowMove(rowUp, col));
							checked[rowUp * boardwidth + col] = true;
						}
						
						else if (state.getBoard().goodMoveForBlack(
								rowUp , col, blackToplay) || state.getBoard().countThreatsRowCol(
										rowUp, col,false, 1) > 0) {
							
							moves.add(new FourRowMove(rowUp, col));
							checked[rowUp * boardwidth + col] = true;
						}
						
					}
					
				}
				if (!(symetry && rowDown > j)) {
					if (firstboard.isLegal(rowDown, col)) {
						if (!checked[rowDown * boardwidth + col]) {
							if(state.getBoard().getMoveNumber()==1){
								moves.add(new FourRowMove(rowDown, col));
								checked[rowDown * boardwidth + col] = true;
							}
						
							else if (state.getBoard().goodMoveForBlack(
									rowDown , col,  blackToplay) || state.getBoard().countThreatsRowCol(
											rowDown, col, false, 1) > 0) {
								moves.add(new FourRowMove(rowDown, col));
								checked[rowDown * boardwidth + col] = true;
							
							}

						}
					}
				}
				if (firstboard.isLegal(rowMid, col)) {
					if (!checked[rowMid * boardwidth + col]) {
						if(state.getBoard().getMoveNumber()==1){
							moves.add(new FourRowMove(rowMid, col));
							checked[rowMid * boardwidth + col] = true;
						}
						else if (state.getBoard().goodMoveForBlack(
								rowMid , col, blackToplay) || state.getBoard().countThreatsRowCol(
										rowMid, col, false, 1) > 0) {
							moves.add(new FourRowMove(rowMid, col));
							checked[rowMid * boardwidth + col] = true;
						}
					}
				}
			}
			for (col = 0; col < boardwidth; col++) {
					if (firstboard.isLegal(rowMid, col)) {
						if (!checked[rowMid * boardwidth + col]) {
							if(state.getBoard().getMoveNumber()==1){
								moves.add(new FourRowMove(rowMid, col));
								checked[rowMid * boardwidth + col] = true;
							}
							else if (state.getBoard().goodMoveForBlack(
									rowMid , col, blackToplay) || state.getBoard().countThreatsRowCol(
											rowMid, col, false, 1) > 0) {
								moves.add(new FourRowMove(rowMid, col));
								checked[rowMid * boardwidth + col] = true;
							}
							

						}
					}
			

			}
			
		}
		for (row = k; row <= j; row++) {

			for (col = 0; col < boardwidth; col++) {

				if (checked[row * state.getBoard().getWidth() + col])
					continue;
				if (!state.getBoard().isLegal(row, col))
					continue;
				
				//check if black move worth to add
				if(blackToplay ){

					if(state.getBoard().badMoveForBlack(row))
						break;
					
					if (state.getBoard().goodMoveForBlack(row,
							col, blackToplay) || state.getBoard().countThreatsRowCol(row, col,
									false, 1) > 0){
						moves.add(new FourRowMove(row, col));
					}
				}
				//check if white move worth to add
				else {
					moves.add(new FourRowMove(row, col));
				}
			}
		}

		return moves;
	}

	
	
	

	

}
