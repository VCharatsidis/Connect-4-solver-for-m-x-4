package BitMap;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Bitboard {

	private int height;
	private int width;
	@Setter
	private boolean blackToplay;
	@Setter
	private int moveNumber;
	@Setter
	private long whitepieces;
	@Setter
	private long blackpieces;
	@Setter
	@Getter
	private long blackLastMove;
	private long lastMove;
	@Getter
	@Setter
	private int threatSearchNodesExpanded;

	// these 3 ints help us find defence when opponent thretens for double
	// threat in his next move
	// square is also used for single threats
	private int square;
	private int threatSquare;
	private int threatSquare2;

	// uper and lower bound are set 2 rows ahead and bellow of the furthest
	// moves up and dow starting from the middle
	@Setter
	private int uperBound;
	@Setter
	private int lowerBound;
	boolean [] rows;

	public Bitboard(int height) {
		this.height = height;
		width = 4;

		blackToplay = true;
		moveNumber = 0;
		whitepieces = 0;
		blackpieces = 0;
		threatSearchNodesExpanded=0;
		
		if (height % 2 == 0) {
				uperBound = height/2;
		} else  
			uperBound = (height + 1) / 2 - 1 ;
		lowerBound  = uperBound;


	}

	// second constructor so i can easily copy bitbords
	public Bitboard(Bitboard other) {
		this.blackToplay = other.blackToplay;
		this.blackpieces = other.blackpieces;
		this.whitepieces = other.whitepieces;
		this.lastMove = other.lastMove;
		this.height = other.height;
		this.width = other.width;
		this.moveNumber = other.moveNumber;
		this.uperBound = other.uperBound;
		this.lowerBound = other.lowerBound;
		this.blackLastMove = other.blackLastMove;
		
	}

	// this method returns the square that is an empty - threat square or just
	// has potential to become a threat
	public int getS(){
		return square;
	}

	// again this method like getS() finds the squares where someone must play
	// to avoid a double threat
	public int getThreatSquare(){
		return threatSquare;
	}
	public int getThreatSquare2(){
		return threatSquare2;
	}

	// checks if coordinates are within the board
	public boolean isWithinBounds(int row, int col) {
		if (row >= height || row < 0 || col >= width || col < 0)
			return false;
		return true;
	}

	public int getUperBound(){
		return uperBound;
	}
	public int getLowerBound(){
		return lowerBound;
	}

	// check if symetric and if yes we try half moves
	public boolean symetry() {
		if (height % 2 == 0)
			return false;
		int middleRow = (height - 1) / 2;
		for (int row = 0; row < middleRow; row++) {
			for (int col = 0; col < width; col++) {

				if (occupied(middleRow + middleRow - row, col) != occupied(row,
						col)) {
					return false;
				}
			}
		}

		return true;
	}
	// this method assigns and returns a long number of 0 and 1 for every square
	// of our board
	public long getBite(int square) {
		return 1L << square;
	}

	// i dont know if i use this method but checks if our row and the 2 above
	// and bellow have a stone , if they are all empty is true
	public boolean badMove(int row) {

		if (rowUsed(row) || rowUsed(row + 1) || rowUsed(row + 2))
			return false;

		if (rowUsed(row) || rowUsed(row - 1) || rowUsed(row - 2))
			return false;
		return true;

	}

	// i dont use this method ,its for future plans , it returns the first full
	// row of stones starting from bottom
	public int returnFullRowBottom(){
		for(int row=(height-1);row>=0;row--){
			if(rowFull(row))
				return row;
		}
		return height;
	}

	// i dont use this method ,its for future plans , it returns the first full
	// row of stones starting from top
	public int returnFullRowTop(){
		for(int row=0;row<height;row++){
			if(rowFull(row))
				return row;
		}
		return height;
	}

	// is true if a row is full , i dont use this method
	public boolean rowFull(int row){
		for(int col=0;col<width;col++){
			if (occupied(row,col)==1)
				return false;
		}
		return true;
	}

	// this method is true if the given row,col have at least a neightbour black
	// stone checking adjacent and diagonaly
	public boolean goodMoveWhite(int row,int col){
		if (col == 1 || col == 2) {
			if (occupied(row - 1, col) != 2 && occupied(row - 1, col - 1) != 2
					&& occupied(row - 1, col + 1) != 2
					&& occupied(row, col - 1) != 2
					&& occupied(row, col + 1) != 2
					&& occupied(row + 1, col - 1) != 2
					&& occupied(row + 1, col) != 2
					&& occupied(row + 1, col + 1) != 2)
				return false;
		}
		if (col == 0) {
			if (occupied(row - 1, col) != 2 && occupied(row - 1, col + 1) != 2
					&& occupied(row, col + 1) != 2
					&& occupied(row + 1, col) != 2
					&& occupied(row + 1, col + 1) != 2)
				return false;
		}
		if (col == 3) {
			if (occupied(row - 1, col) != 2 && occupied(row - 1, col - 1) != 2
					&& occupied(row, col - 1) != 2
					&& occupied(row + 1, col) != 2
					&& occupied(row + 1, col - 1) != 2)
				return false;
		}
		return true;
	}

	// this method returns true if in the given row col there is potential
	// (there are no opponents stones and we have 1 stone of ours already)
	// for score 4 in at least one of the vertical, horizontal or diagonal axis

	public boolean goodMoveForBlack(int row , int col,boolean black){
		if(goodRowBlack(row,black) || colUsedBlack(row,col,black) || diagUsedBlack(row,col,black) || secondDiagUsedBlack(row,col,black))
			return true;
		return false;
	}

	// i dont use this method , it was supposed to count potential of a black
	// move
	public int veryGoodMoveBlack(int row,int col){
		int counter= 0;
		if(goodRowBlack(row,true))
				counter++;
		if(colUsedBlack(row,col,true))
				counter++;
		if(diagUsedBlack(row,col,true))
				counter++;
		if(secondDiagUsedBlack(row,col,true))
				counter++;
		return counter;
	}

	// this method for the given row col checks if we can do a score 4 in the
	// vertical axis
	public boolean colUsedBlack(int row,int col,boolean black){
		int b=0;
		if(!black)
			b=1;
		for (int i = row-3; i < row+1; i++) {
			if(isWithinBounds(i,col)){
				if(isThreat(i,1,col,0,black,2)){
					if(i==row-3){
						if(occupied(i-1,col)==2+b)
							continue;
					}
					else if(i==row){
						if(occupied(i+4,col)==2+b)
							continue;
					}
					return true;
				}
			}
		}
		return false;
		
		
	}

	// this method for the given row col checks if we can do a score 4 in the
	// diagonal axis
	public boolean diagUsedBlack(int row,int col,boolean black){
		int b=0;
		if(!black)
			b=1;
		if (row<height-3 ){
			if(col==0){
				if (occupied(row+1,col+1)==3-b || occupied(row+2,col+2)==3-b || occupied(row+3,col+3)==3-b)
					return false;
				if(occupied(row+1,col+1)==2+b || occupied(row+2,col+2)==2+b || occupied(row+3,col+3)==2+b)
					return true;
			}
		}
		if (row<height-2){
			if(col==1){
				if(row>0){
					if (occupied(row-1,col-1)==3-b || occupied(row+1,col+1)==3-b || occupied(row+2,col+2)==3-b)
						return false;
					if(occupied(row-1,col-1)==2+b || occupied(row+1,col+1)==2+b || occupied(row+2,col+2)==2+b)
						return true;
				}
			}
		}
		if(row<height-1){
			 if(col==2){
				if (row>1){
					if (occupied(row-2,col-2)==3-b || occupied(row-1,col-1)==3-b || occupied(row+1,col+1)==3-b)
						return false;
					if(occupied(row-2,col-2)==2+b || occupied(row-1,col-1)==2+b || occupied(row+1,col+1)==2+b){
						return true;
					}
				}
			}
		}
		if(row<height){
			if(col==3){
				if(row>2){
					if (occupied(row-3,col-3)==3-b || occupied(row-2,col-2)==3-b ||occupied(row-1,col-1)==3-b)
						return false;
					if(occupied(row-3,col-3)==2 || occupied(row-2,col-2)==2 || occupied(row-1,col-1)==2)
						return true;
				}
			}
		}
		return false;
	}

	// this method for the given row col checks if we can do a score 4 in the
	// second diagonal axis
	public boolean secondDiagUsedBlack(int row, int col, boolean black) {
		int b = 0;
		if (!black)
			b = 1;
		if (row < height) {
			if (col == 0) {
				if (row > 2) {
					if (occupied(row - 1, col + 1) == 3 - b
							|| occupied(row - 2, col + 2) == 3 - b
							|| occupied(row - 3, col + 3) == 3 - b)
						return false;
					if (occupied(row - 1, col + 1) == 2 + b
							|| occupied(row - 2, col + 2) == 2 + b
							|| occupied(row - 3, col + 3) == 2 + b)
						return true;
				}
			}
		}
		if (row < height - 1) {
			if (col == 1) {
				if (row > 1) {
					if (occupied(row + 1, col - 1) == 3 - b
							|| occupied(row - 1, col + 1) == 3 - b
							|| occupied(row - 2, col + 2) == 3 - b)
						return false;
					if (occupied(row + 1, col - 1) == 2 + b
							|| occupied(row - 1, col + 1) == 2 + b
							|| occupied(row - 2, col + 2) == 2 + b)
						return true;
				}
			}
		}
		if (row < height - 2) {
			if (col == 2) {

				if (row > 0) {
					if (occupied(row + 2, col - 2) == 3 - b
							|| occupied(row + 1, col - 1) == 3 - b
							|| occupied(row - 1, col + 1) == 3 - b)
						return false;
					if (occupied(row + 2, col - 2) == 2 + b
							|| occupied(row + 1, col - 1) == 2 + b
							|| occupied(row - 1, col + 1) == 2 + b)
						return true;
				}
			}
		}
		if (row < height - 3) {
			if (col == 3) {

				if (occupied(row + 3, col - 3) == 3 - b
						|| occupied(row + 2, col - 2) == 3 - b
						|| occupied(row + 1, col - 1) == 3 - b)
					return false;

				if (occupied(row + 3, col - 3) == 2 + b
						|| occupied(row + 2, col - 2) == 2 + b
						|| occupied(row + 1, col - 1) == 2 + b) {
					return true;
				}
			}
		}
		return false;
	}

	// this method checks if the given player (boolean black) threats for s4 if
	// theatCounter=0 or can make a threat if threatCounter=1
	// in the first diagonal for column 2
	public boolean isThreatColumnTwoFirstDiag(int row,int col,boolean black, int threatCounter){
		int copy = square;
		int check=0;
		int k;
		if(black){
			check =2;
			k=1;
		}
		else{
			check=3;
			k=-1;
		}
		if (occupied(row+1,col+1)==check+k || occupied(row-1,col-1)==check+k || occupied(row-2,col-2)==check+k)
			return false;
		if(col==2){
			if(occupied(row+1,col+1)==check)
					threatCounter++;
			else if(occupied(row+1,col+1)==1)
				square = getSquare(row+1, col+1);
			
			if(occupied(row-1,col-1)==check)
					threatCounter++;
			else if(occupied(row-1,col-1)==1)
				square = getSquare(row-1, col-1);
			
			if(occupied(row-2,col-2)==check)
					threatCounter++;
			else if(occupied(row-2,col-2)==1)
				square = getSquare(row-2, col-2);
			
		}
		if(threatCounter<=2)
			square = copy;
		return threatCounter>2;
	}

	// this method checks if the given player (boolean black) threats for s4 if
	// theatCounter=0 or can make a threat if threatCounter=1
	// in the first diagonal for column 1
	public boolean isThreatColumnOneFirstDiag(int row,int col,boolean black,int threatCounter){
		int copy = square;
		int check=0;
		int k;
		if(black){
			check =2;
			k=1;
		}
		else{
			check=3;
			k=-1;
		}
		if (occupied(row+1,col+1)==check+k || occupied(row-1,col-1)==check+k || occupied(row+2,col+2)==check+k)
			return false;
		if(col==1){
			if(occupied(row+1,col+1)==check)
					threatCounter++;
			else if(occupied(row+1,col+1)==1)
				square = getSquare(row+1, col+1);
			
			if(occupied(row-1,col-1)==check)
					threatCounter++;
			else if(occupied(row-1,col-1)==1)
				square = getSquare(row-1, col-1);
			
			if(occupied(row+2,col+2)==check)
					threatCounter++;
			else if(occupied(row+2,col+2)==1)
				square = getSquare(row+2, col+2);
			
			if(threatCounter<=2)
				square =copy;
			
		}
		return threatCounter>2;
	}

	// this method checks if the given player (boolean black) threats for s4 if
	// theatCounter=0 or can make a threat if threatCounter=1
	// in the Second diagonal column 2

	public boolean isThreatColumnTwoSecondDiag(int row,int col,boolean black,int threatCounter){
		int copy = square;
		int check=0;
		int k;
		if(black){
			check =2;
			k=1;
		}
		else{
			check=3;
			k=-1;
		}
		if (occupied(row+1,col-1)==check+k || occupied(row-1,col+1)==check+k || occupied(row+2,col-2)==check+k)
			return false;
		if(col==2){
			if(occupied(row+1,col-1)==check)
					threatCounter++;
			else if(occupied(row+1,col-1)==1)
					square = getSquare(row+1, col-1);
			
			if(occupied(row-1,col+1)==check)
					threatCounter++;
			else if(occupied(row-1,col+1)==1)
					square = getSquare(row-1,col+1);
			
			if(occupied(row+2,col-2)==check)
					threatCounter++;
			else if(occupied(row+2,col-2)==1)
					square = getSquare(row+2,col-2);
			
			if(threatCounter<=2)
				square =copy;
			
		}
		return threatCounter>2;
	}

	// this method checks if the given player (boolean black) threats for s4 if
	// theatCounter=0 or can make a threat if threatCounter=1
	// in the Second diagonal column one

	public boolean isThreatColumnOneSecondDiag(int row,int col,boolean black,int threatCounter){
		int copy=square;
		int check=0;
		int k;
		if(black){
			check =2;
			k=1;
		}
		else{
			check=3;
			k=-1;
		}
		if (occupied(row+1,col-1)==check+k || occupied(row-1,col+1)==check+k || occupied(row-2,col+2)==check+k)
			return false;
		if(col==1){
			if(occupied(row+1,col-1)==check)
					threatCounter++;
			else if(occupied(row+1,col-1)==1)
				square = getSquare(row+1, col-1);
			
			if(occupied(row-1,col+1)==check)
					threatCounter++;
			else if(occupied(row-1,col+1)==1)
				square = getSquare(row-1, col+1);
			
			if(occupied(row-2,col+2)==check)
					threatCounter++;
			else if(occupied(row-2,col+2)==1)
				square = getSquare(row-2, col+2);
			
			if(threatCounter<=2)
				square =copy;
			
		}
		return threatCounter>2;
	}

	// this method checks if the given row or the 2 rows above and bellow have a
	// white stone in them

	public boolean badMoveForWhite(int row) {
		if (rowUsedWhite(row) || rowUsedWhite(row + 1) || rowUsedWhite(row + 2)
				|| rowUsedWhite(row - 1) || rowUsedWhite(row - 2))
			return false;
		return true;
	}

	// this method checks if the given row or the 2 rows above and bellow have a
	// BLACK stone in them
	
	public boolean badMoveForBlack(int row) {
		if (rowUsedBlack(row) || rowUsedBlack(row + 1) || rowUsedBlack(row + 2) || rowUsedBlack(row - 1)
				|| rowUsedBlack(row - 2))
			return false;
		return true;
	}

	// if row has a white stone
	public boolean rowUsedWhite(int row){
		for (int col = 0; col < width; col++) {
			//if (isWithinBounds(row, col))
				if (occupied(row, col) == 3 )
					return true;
		}
		return false;
	}

	// if row has a black stone
	public boolean rowUsedBlack(int row) {
		boolean k = false;
		for (int col = 0; col < width; col++) {
			// if (isWithinBounds(row, col))
			if (occupied(row, col) == 2)
				k = true;
		}
		return k;
	}

	// if Row has potential for black to s4
	public boolean goodRowBlack(int row,boolean black){

		int b=0;
		if(!black)
			b=1;
		boolean k = false;
		int col=0;
		
		for ( col = 0; col < width; col++) {
			
			if(occupied(row,2)==3-b && col==3 && occupied(row,1)!=2+b)
				return false;
			if (occupied(row, 3) == 3 - b )
				return false;
				if (occupied(row, col) == 2+b ){
	
					k = true;
				}
		}
		return k;
	}

	// if the row has a stone in it black or white
	public boolean rowUsed(int row) {
		for (int col = 0; col < width; col++) {
			//if (!isWithinBounds(row, col))
				//continue;
			if (occupied(row, col) != 1)
				return true;
		}
		return false;
	}

	// this method checks if a player for a given square has 2 stones vertical
	// with enough space to make a vertical double threat in the next move
	public boolean checkTwoSouth(long highest){
		int[] coords;
		coords = squareToRowCol(longToSquare(highest));
		// System.out.println("highest "+highest);
		boolean caseA=false;
		boolean caseB=false;
		
		int row = coords[0];
		int col = coords[1];
		// System.out.println("checkTwoSouth row,col " + row + col);
		if(row<height-3 && row>1){
			if(occupied(row-1,col)==1 && occupied(row+2,col)==1 && occupied(row+3,col)==1 && occupied(row-2,col)==1){
				square=getSquare(row+2,col);
				return true;
			}
		}
		if (row < (height - 3) && row > 0){
			
			caseA = (occupied(row-1,col)==1 && occupied(row+2,col)==1 && occupied(row+3,col)==1);
			square = getSquare(row+2,col);
			if(caseA)
				return true;
		}
		if (row < height - 2 && row > 1) {
			 caseB = (occupied(row-1,col)==1 && occupied(row+2,col)==1 && occupied(row-2,col)==1);
			square = getSquare(row + 2, col);
			
		}
		return caseB;
	}

	// this method iterates threw the whole bord and see if the given player can
	// make a vertical double threat
	public boolean checkTwoVertical(long pieces){
		long copyBoard = pieces;
		
		while (copyBoard !=0){
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard &~ highest;
			if (check(twoSouth(highest),pieces) && checkTwoSouth(highest))
				return true;	
		}
		return false;
	}

	// these 2 methods help us run the above method
	public long twoSouth(long highest){
		long southA;
		southA = highest <<width;
		return southA|=highest;
		
	}
	public boolean check(long chunkOfpieces, long pieces) {
		if ((chunkOfpieces | pieces) == pieces)
			return true;
		return false;
	}

	// this is one of The Most Important method , count not only the real
	// threats for given coordinates but also
	// returns 2 if the given row call can make a double threat ( this works if
	// threatCounter==1)
	// if threatCounter==2 means that someone already made a double threat.
	// it also return the empty squares that the double threats are
	public int countThreatsRowCol(int row , int col,boolean black,int threatCounter){
		int rowSquare=-1;
		int colSquare=-1;
		int fdiaSquare=-1;
		int sdiaSquare=-1;
		int threat1=-1;
		int threat2=-1;
		if(!isWithinBounds(row,col))
			return 0;
		int threat=0;
		//row check
		if (isThreat(row,0,0,1,black,threatCounter)){
			threat++;
			if (square!= getSquare(row,col))
				threat1 = square;
			else
				threat1 = threatSquare;
			
		}
		//vertical all checks all columns 
		for (int i = row-3; i < row+1; i++) {
			
			if(isWithinBounds(i,col)){
				if(isThreat(i,1,col,0,black,threatCounter)){
					threat++;
					if (threat==1){
						if (square!= getSquare(row,col)){
							threat1 = square;
						}else
							threat1 = threatSquare;
					}else if(threat==2){
						if(square!=getSquare(row,col))
							threat2 = square;
						else
							threat2 = threatSquare;
					}
						
					break;
				}
			}
		}
		if (threat>1 && threatCounter==0){
			threatSquare =threat1;
			threatSquare2 = threat2;
			return threat;
		}
		if(row>2){
			if(col==0){
				//second diagonal col 0
				if(isThreat(row,-1,col,1,black,threatCounter)){
					threat++;
					if (threat==1){
						if (square!= getSquare(row,col)){
							threat1 = square;
						}else
							threat1 = threatSquare;
					}else if(threat==2){
						if(square!=getSquare(row,col))
							threat2 = square;
						else
							threat2 = threatSquare;
					}
				}
			
			}
			if(col==3){
				// first diagonal col 3
				if(isThreat(row,-1,col,-1,black,threatCounter)){
					threat++;
					if (threat==1){
						if (square!= getSquare(row,col)){
							threat1 = square;
						}else
							threat1 = threatSquare;
					}else if(threat==2){
						if(square!=getSquare(row,col))
							threat2 = square;
						else
							threat2 = threatSquare;
					}
				}
			}		
		}
		if(row<height-3){
			
			if(col==0){
				// first diagonal col 0
				if (isThreat(row,1,col,1,black,threatCounter)){
					threat++;
					if (threat==1){
						if (square!= getSquare(row,col)){
							threat1 = square;
						}else
							threat1 = threatSquare;
					}else if(threat==2){
						if(square!=getSquare(row,col))
							threat2 = square;
						else
							threat2 = threatSquare;
					}
				}
					
			}
			if(col==3){
				//second diagonal col 3
				if (isThreat(row,1,3,-1,black,threatCounter)){
					threat++;
				
					if (threat==1){
						if (square!= getSquare(row,col)){
							threat1 = square;
						}else
							threat1 = threatSquare;
					}else if(threat==2){
						if(square!=getSquare(row,col))
							threat2 = square;
						else
							threat2 = threatSquare;
					}
				}
			}
		}
		if (threat>1 && threatCounter==0){
			threatSquare =threat1;
			threatSquare2 = threat2;
			return threat;
		}
		//check first and second diagonal for col =1
		if(row<height-2 && row>0 && col==1){
			if(isThreatColumnOneFirstDiag(row,col,black,threatCounter)){
				fdiaSquare =  square;
				threat++;
			}
			
		}
		if(row<height-1 && row>1 && col==1){
			if(isThreatColumnOneSecondDiag(row,col,black,threatCounter)){
				sdiaSquare =  square;
				threat++;
			}
			
		}
		//check first and second diagonal for col=2
		if(row<height-1 &&  row>1 && col==2){
			if(isThreatColumnTwoFirstDiag(row,col,black,threatCounter)){
				fdiaSquare =  square;
				threat++;
			}
			
		}
		if(row<height-2 && row>0 && col==2){
			if(isThreatColumnTwoSecondDiag(row,col,black,threatCounter)){
				sdiaSquare =  square;
				threat++;
			}
			
		}
		if (threat>1 && threat1!=-1){
			threatSquare =threat1;
			threatSquare2 = square;
			return threat;
		}
		else if(threat>1 && threat1==-1){
			if(rowSquare!=square && rowSquare!=-1 ){
				threatSquare2 = rowSquare;
			}
			if(colSquare!=square && colSquare!=-1){
				threatSquare2 = colSquare;
			}
			if(fdiaSquare!=square && fdiaSquare!=-1){
				threatSquare2 = fdiaSquare;
			}
			if(sdiaSquare!=square && sdiaSquare!=-1){
				threatSquare2 = sdiaSquare;
			}
			threatSquare=square;
		}
		return threat;
	}

	// the methods bellow make a mask of 3 stones in a
	// row,col,diagonal,secondiagonal and test them if they fit , so if they fit
	// in the board
	// the given player has 3 stones in the row col diagonal or second diagonal
	// respectively
	public boolean wasThreat(){
		long x=0;
		x= southWest(lastMove);
		if((x & blackpieces)==x)
			return true;
		x= southEast(lastMove);
		if((x & blackpieces)==x)
			return true;
		x= east(lastMove);
		if((x & blackpieces)==x)
			return true;
		x= south(lastMove);
		if((x & blackpieces)==x)
			return true;
		x= north(lastMove);
		if((x & blackpieces)==x)
			return true;
		x=northWest(lastMove);
		if((x & blackpieces)==x)
			return true;
		x=northEast(lastMove);
		if((x & blackpieces)==x)
			return true;
		x=west(lastMove);
		if((x & blackpieces)==x)
			return true;
		
		return false;
	}
	public long northWest(long highestBit){
		long northWestA,northWestB,northWestC;
		
		northWestA = highestBit>>(width+1);
		northWestB = highestBit>> 2*(width+1);
		northWestC = highestBit>>3*(width+1);
		
		return northWestA|=northWestB|=northWestC;
	}
	public long southWest(long highestBit) {
		long southWest;
		long southWestA, southWestB, southWestC;

		southWestA = highestBit << (width - 1);
		southWestB = highestBit << 2 * (width - 1);
		southWestC = highestBit << 3 * (width - 1);

		southWest = southWestA |= southWestB |= southWestC ;
		return southWest;
	}
	public long west(long highestBit){
		long westA,westB,westC;
		westA = highestBit >> 1;
		westB = highestBit >> 2;
		westC = highestBit >> 3;
		return westA|=westB|=westC;
	}
	public long east(long highestBit) {
		long east;
		long eastA, eastB, eastC;

		eastA = highestBit << 1;
		eastB = highestBit << 2;
		eastC = highestBit << 3;

		east = eastA |= eastB |= eastC ;
		return east;
	}
	public long northEast(long highestBit){
		long northEastA,northEastB,northEastC;
		
		northEastA = highestBit>>(width-1);
		northEastB = highestBit>>2*(width-1);
		northEastC = highestBit>>3*(width-1);
		
		return northEastA|=northEastB|=northEastC;
	}
	public long southEast(long highestBit) {
		long southEast;
		long southEastA, southEastB, southEastC;

		southEastA = highestBit << (width + 1);
		southEastB = highestBit << 2 * (width + 1);
		southEastC = highestBit << 3 * (width + 1);

		southEast = southEastA |= southEastB |= southEastC ;
		return southEast;
	}
	public long north(long highestBit){
		long northA,northB,northC;
		northA = highestBit>>width;
		northB = highestBit>>2*width;
		northC = highestBit>>3*width;
		
		return northA|=northB|=northC;
	}
	public long south(long highestBit) {
		long south = 0;
		long southA = 0, southB = 0, southC = 0;

		southA = highestBit << width;
		southB = highestBit << 2 * width;
		southC = highestBit << 3 * width;

		south = southA |= southB |= southC ;
		return south;
	}

	// this method use the above masks and iterates threw the board to find if
	// they fit
	public boolean checkJustThree(long pieces) {
		long copyBoard = pieces;

		while (copyBoard != 0) {
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard & ~highest;
			if (justThreeInARow(highest, pieces) > 0) {
				return true;
			}
		}
		return false;
	}

	// this method checks if someone threats a vertical double threat , its the
	// case when one stone has the above and bellow square empty
	// and the 2 rows bellow there is a stone of same color with the bellow also
	// empty so the move in the midle makes vertival double threat
	public boolean checkTwoVerticalSpecialCase(boolean black){
		int checked;
		if (black){
			 checked =2;
		}else{
			checked =3;
		}
		for (int row=uperBound;row<=lowerBound-2;row++){
			for(int col=0;col<width;col++){
				if(row>=height-3 || row==0)
					continue;
				if (occupied(row,col)==checked && occupied(row+1,col)==1 &&
						occupied(row+2,col)==checked && occupied(row+3,col)==1 && occupied(row-1,col)==1){
					square = getSquare(row+1,col);
					return true;
				}
								
			}
		}
		
		return false;
	}

	// i think i dont call this method
	public boolean isColSingleThreat(long pieces){
		long copyBoard = pieces;

		while (copyBoard != 0) {
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard & ~highest;
			
			if (columnThreats(highest, pieces)==1) {
				return true;
			}
		}
		return false;
	}

	public int countColThreats(long pieces) {
		long copyBoard = pieces;
		int max = 0;
		while (copyBoard != 0) {
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard & ~highest;
			int colthreats = columnThreats(highest, pieces);
			if(colthreats==2)
				return 2;
			if (colthreats>max) {
				max = colthreats;
			}
		}
		return max;
	}

	// this is also one of the most important methods , it iterates threw the
	// board for a given player and finds how many
	// threats has 0 1 or 2 .( i could use the countThreatsRowCol for that dont
	// ask me why i didnt )
	public int numThreats(boolean blackCheck){
		
		int countColumnThreats =0;
		if (blackCheck){
			countColumnThreats = countColThreats(blackpieces);
			if (countColumnThreats==2){
				return 2;
			}
		}
		else{
			countColumnThreats = countColThreats(whitepieces);
			if (countColumnThreats==2){
				return 2;
			}
		}
		int threatCounter = 0;
		int copySquare =0;

		for(int row = uperBound; row  <=lowerBound; row++){
						
						// row check
						if (rowUsed(row)){
							if(isThreat(row,0,0,1,blackCheck,0)){
								
								threatCounter ++;
								if(threatCounter>1){
									if(copySquare != square)
										return 2;
									else
										threatCounter--;
								}
								copySquare = square;
							}
						}
						// first diagonal check
						if (row<height-3){
							
							if(isThreat(row,1,0,1,blackCheck,0)){
								
								threatCounter++;
								if (threatCounter>1){
									if(copySquare != square)
										return 2;
									else
										threatCounter--;
								}
								copySquare = square;
							}
							if(isThreat(row,1,3,-1,blackCheck,0)){
								
								threatCounter++;
								if(threatCounter>1){
									if(copySquare != square)
										return 2;
									else
										threatCounter--;
								}
								copySquare = square;
							}
							//column check
						}	
						for( int col =0;col<width;col++){
								if(isThreat(row,1,col,0,blackCheck,0)){
									threatCounter++;
									if(threatCounter>1){
										if(copySquare != square)
											return 2;
										else
											threatCounter--;
									}
									copySquare = square;
								}
						}
		}
		return Math.max(threatCounter, countColumnThreats);
	}

	public int findOneThreat(boolean blackCheck) {

		int countColumnThreats = 0;
		if (blackCheck) {
			countColumnThreats = countColThreats(blackpieces);
			if (countColumnThreats > 0) {
				return 1;
			}
		} else {
			countColumnThreats = countColThreats(whitepieces);
			if (countColumnThreats > 0) {
				return 1;
			}
		}

		for (int row = uperBound; row <= lowerBound; row++) {

			// row check
			if (rowUsed(row)) {
				if (isThreat(row, 0, 0, 1, blackCheck, 0)) {

					return 1;
				}
			}
			// first diagonal check
			if (row < height - 3) {

				if (isThreat(row, 1, 0, 1, blackCheck, 0)) {

					return 1;
				}
				if (isThreat(row, 1, 3, -1, blackCheck, 0)) {

					return 1;
				}
				// column check
			}
			for (int col = 0; col < width; col++) {
				if (isThreat(row, 1, col, 0, blackCheck, 0)) {
					return 1;
				}
			}
		}
		
		return 0;
	}
	

	// check if there is a threat in the given axis that is determined form the
	// increments in the given row,col for the given player
	// it also can count if the player has 2 stones or 1 stone in the given axis
	// and potential for s4
	// it also stores the empty squares with potential

	public boolean isThreat(int row, int incrementR, int col,
			int incrementC, boolean blackCheck,int threatCounter) {
		if(row>=height-3 && incrementR==1)
			return false;
		// shouldnt use rowSquare as name etc ,its just copy pasted.
		int rowSquare=-1;
		int colSquare=-1;
		int fdiaSquare=-1;
		int sdiaSquare=-1;
		int counter = 0;
		int check = blackCheck ? 0 : 1;
		
		
		if (occupied(row, col) == 3 - check)
				return false;
		if (occupied(row + incrementR, col + incrementC) == 3 - check)
				return false;
		if (occupied(row + 2 * incrementR, col + 2 * incrementC) == 3 - check)
				return false;
		if (occupied(row + 3 * incrementR, col + 3 * incrementC) == 3 - check)
				return false;
		
		int copy = square;

		if (occupied(row, col) == 2 + check)
			counter++;
		else if (occupied(row, col) == 1){
			
			square = getSquare(row, col);
			rowSquare = square;
		}

		if (occupied(row + incrementR, col + incrementC) == 2 + check)
			counter++;
		else if (occupied(row + incrementR, col + incrementC) == 1){
			square = getSquare(row + incrementR, col + incrementC);
			colSquare = square;
		}

		if (occupied(row + 2 * incrementR, col + 2 * incrementC) == 2 + check)
			counter++;
		else if (occupied(row + 2 * incrementR, col + 2 * incrementC) == 1){
			square = getSquare(row + 2 * incrementR, col + 2 * incrementC);
			fdiaSquare = square;
		}

		if (occupied(row + 3 * incrementR, col + 3 * incrementC) == 2 + check)
			counter++;
		else if (occupied(row + 3 * incrementR, col + 3 * incrementC) == 1){
			square = getSquare(row + 3 * incrementR, col + 3 * incrementC);
			sdiaSquare = square;
		}
		// shouldnt use rowSquare as name etc ,its just copy pasted.
		if(rowSquare!=square && rowSquare!=-1){
			threatSquare = rowSquare;
		}
		if(colSquare!=square && colSquare!=-1){
			threatSquare = colSquare;
		}
		if(fdiaSquare!=square && fdiaSquare!=-1){
			threatSquare = fdiaSquare;
		}
		if(sdiaSquare!=square && sdiaSquare!=-1){
			threatSquare = sdiaSquare;
		}
		
		if (counter+threatCounter <= 2)
			square = copy;
		return counter+threatCounter > 2;
	}

	// i think i dont use this method , use the masks to find if there are 3
	// stones in the row for a given square and player
	public int justThreeInARow(long highest, long pieces) {
		int counter = 0;
		if (check(maskThreeSouth(highest), pieces))
			counter++;
		if (check(maskThreeSouthWest(highest), pieces))
			counter++;
		if (check(maskThreeSouthEast(highest), pieces))
			counter++;
		if (check(maskThreeEast(highest), pieces))
			counter++;
		return counter;
	}
	

	public int columnThreats(long highest, long pieces) {
		int[] coords;
		coords = squareToRowCol(longToSquare(highest));
		// System.out.println("highest "+highest);

		int row = coords[0];
		int col = coords[1];
		
			
		if (row < (height - 2)) {
			if (check(maskThreeSouth(highest), pieces)){
				if (row > 0 && row < height - 3) {
					if ((occupied(row - 1, col) == 1)
							&& (occupied(row + 3, col) == 1)) {

						square = getSquare(row - 1, col);
						return 2;
					}
				}
				if (row > 0) {
					if ((occupied(row - 1, col) == 1)) {
						
						square = getSquare(row-1,col);
						return 1;
					}
				}
				if (row < height - 3) {
					if (occupied(row + 3, col) == 1) {
					
						square = getSquare(row - 1, col);
						return 1;
					}
				}
			}
		}
		return 0;
	}

	public long maskThreeSouth(long highestBit) {

		long southA = 0, southB = 0;

		southA = highestBit << width;
		southB = highestBit << 2 * width;

		return southA |= southB |= highestBit;
	}

	public long maskThreeSouthEast(long highestBit) {


		long southEastA, southEastB;

		southEastA = highestBit << (width + 1);
		southEastB = highestBit << 2 * (width + 1);

		return southEastA |= southEastB |= highestBit;
	}

	public long maskThreeEast(long highestBit) {

		long eastA, eastB;

		eastA = highestBit << 1;
		eastB = highestBit << 2;

		return eastA |= eastB |= highestBit;
	}

	public long maskThreeSouthWest(long highestBit) {

		long southWestA, southWestB;

		southWestA = highestBit << (width - 1);
		southWestB = highestBit << 2 * (width - 1);

		return southWestA |= southWestB |= highestBit;
	}

	// this method checks for a given square if there is score 4 that starts
	// from that square
	// so with the gameOver method that iterates threw the board we see if the
	// game is over
	public boolean isFour(long highest, long pieces) {
		int[] coords;
		boolean kapa = false;
		coords = squareToRowCol(longToSquare(highest));
		int row = coords[0];
		int col = coords[1];

		if (row < (height - 3))
			if (check(south(highest), pieces))
				kapa = true;
		if (col == 0) {
			if (check(east(highest), pieces))
				kapa = true;
			if (row < height - 3)
				if (check(southEast(highest), pieces))
					kapa = true;
		}
		if (col == 3) {
			// System.out.println("check for second diagonal");
			if (row < height - 3)
				if (check(southWest(highest), pieces))
					kapa = true;
		}

		return kapa;
	}

	
	public boolean isDoubleThreatB(long pieces) {
		long copyBoard = pieces;

		while (copyBoard != 0) {
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard & ~highest;
			if (justThreeInARow(highest, pieces) > 1) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isGameOver(long pieces) {
		long copyBoard = pieces;

		while (copyBoard != 0) {
			long highest = Long.highestOneBit(copyBoard);
			copyBoard = copyBoard & ~highest;
			if (isFour(highest, pieces)) {
				return true;
			}
		}
		return false;
	}

	// this method made all the difference , boosted a lot the speed of the
	// program
	// if a player has a threat , then it runs a search , after it answers the
	// threat , checks if the first player has threat again
	// stops if the starting player has no threat , or if the opponent has a
	// threat after he answered a threat
	// it also stops if after the threat barrage finds a score 4 .
	// it takes a while since it might checks all possible threat combinations
	public int checkAtariBarage(Bitboard board, boolean playerWhoAttacks,
			int layer) {
		layer++;
		if (layer > 8)
			return 0;
		Bitboard studyboard = new Bitboard(board);
		boolean turn = studyboard.getTurn();
		//evaluation
		if (layer > 1) {
			int numberOfThreatsB = 0;
			numberOfThreatsB = studyboard.numThreats(true);

			if (numberOfThreatsB == 2)
				return 300;

			if (playerWhoAttacks) {
				if (!turn) {
					if (numberOfThreatsB == 0)
						return 0;
				}
			}
		}

		int coords[];
		
		if(turn!=playerWhoAttacks){
			// int threats = studyboard.numThreats(playerWhoAttacks);

			coords = studyboard.squareToRowCol(studyboard.getS());
			if(studyboard.isLegal(coords[0], coords[1])){

				studyboard.addStone(coords[0], coords[1]);
				threatSearchNodesExpanded++;
				if (studyboard.findOneThreat(false) > 0) {

					return 0;
				}
				int check= checkAtariBarage(studyboard, playerWhoAttacks, layer);
				threatSearchNodesExpanded += studyboard.getThreatSearchNodesExpanded();
				return check;
			}	
			
		}else{	

			List<Integer> atari = new ArrayList<Integer>();
			int height =studyboard.getHeight();
			int width = studyboard.getWidth();
			boolean areThreats=false;
			if (layer > 0) {
				if (studyboard.checkTwoVertical(studyboard.getBlackpieces()))
					return 300;
				if (studyboard.checkTwoVerticalSpecialCase(blackToplay))
					return 300;
			}
			for(int row=studyboard.getUperBound();row<height;row++){
				for(int col=0;col<width;col++){
					if (studyboard.occupied(row, col)==1){

						int threats = studyboard.countThreatsRowCol(row, col,
								playerWhoAttacks, 1);

						if(threats==2){
							if(playerWhoAttacks)
								return 300;
							else
								return 0;
						}else if(threats==1){
							if (layer > 1) {
								long lastAtari = studyboard.getBlackLastMove();
								long currentAtari = getBite(row * width + col);
								if ((south(lastAtari) & currentAtari) != 0
										|| (southEast(lastAtari) & currentAtari) != 0
										|| (southWest(lastAtari) & currentAtari) != 0
										|| (east(lastAtari) & currentAtari) != 0) {
									areThreats = true;
									atari.add(studyboard.getSquare(row, col));
								} else if ((south(currentAtari) & lastAtari) != 0
										|| (southEast(currentAtari) & lastAtari) != 0
										|| (southWest(currentAtari) & lastAtari) != 0
										|| (east(currentAtari) & lastAtari) != 0) {
									areThreats = true;
									atari.add(studyboard.getSquare(row, col));
								}
							} else {
								areThreats = true;
								atari.add(studyboard.getSquare(row, col));
							}
						}

					}
						
				}
			}

			if(areThreats ){
				for(Integer square : atari){
					coords = studyboard.squareToRowCol(square);
					if(studyboard.isLegal(coords[0], coords[1])){
						Bitboard studyboard2 = new Bitboard(studyboard);
						
						studyboard2.addStone(coords[0], coords[1]);
						threatSearchNodesExpanded++;
						int check = checkAtariBarage(studyboard2,
								playerWhoAttacks, layer);
						threatSearchNodesExpanded += studyboard2.getThreatSearchNodesExpanded();
						if(playerWhoAttacks){
							if(check==300){
								return check;
							}
						}
						else{
							if (check == 0)
								return check;
						}
							
					}
				}
			}
			else{
				return 0;
			}
			return 0;
		}
		return 0;
	}

	// this method was useful for debug it prints the board in the console
	public void boardPrinter() {

		System.out.println("4Row printer");

		long stone;
		for (int i = -1; i < height; i++) {
			if (i == 0)
				System.out.println();
			for (int j = -1; j < width; j++) {
				stone = getBite(getSquare(i, j));
				if (i == -1 && j != -1)
					System.out.print(j + "  ");
				else if (i != -1 && j == -1) {
					if (i < 10)
						System.out.print(i + "  ");
					else
						System.out.print(i + " ");
				} else if ((stone & getBlackpieces()) == stone)
					System.out.print("B  ");
				else if ((stone & getWhitepieces()) == stone)
					System.out.print("W  ");
				else if (height == -1 && width == -1)
					System.out.print("   ");
				else
					System.out.print("-  ");

			}
			System.out.println();
		}
		if (isGameOver(blackpieces))
			System.out.println("black wins");
		if (isGameOver(whitepieces))
			System.out.println("white wins");

	}
	public void pass(){
		blackToplay = !blackToplay;
	}
	public int getMoveNumber() {
		return moveNumber;
	}

	public boolean getTurn() {
		return blackToplay;
	}

	public long getLastMove() {
		return lastMove;
	}


	public long getBlackpieces() {
		return blackpieces;
	}

	public long getWhitepieces() {
		return whitepieces;
	}

	public long board() {
		return whitepieces | blackpieces;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long emptySquares() {
		return ~whitepieces & ~blackpieces;
	}

	// take coords and make them square , all board has heigth*width squares
	public int getSquare(int row, int col) {
		return row * width + col;
	}

	// checks if the move is legal
	public boolean isLegal(int row, int col) {

		if (col >= width || col < 0 || row < 0 || row >= height) {
			return false;
		}
		if (occupied(row, col) != 1) {
			return false;
		}
		// first move only in the middle
		return true;
	}

	// takes a long ( that represents a square ) and make it coords (row ,col)
	public int[] highestBitToCoords(long highestBit) {
		int square = longToSquare(highestBit);
		int[] coords = squareToRowCol(square);
		return coords;
	}

	// this method takes a long and makes it square
	public int longToSquare(long l) {
		return Long.numberOfTrailingZeros(l);
	}

	public int[] squareToRowCol(int square) {
		int[] rowcol = { 0, 0 };
		rowcol[0] = (int) Math.floor(square / width);
		rowcol[1] = square % width;
		return rowcol;
	}

	// check if a given row,col is occupied and returns 1 if empty 2 if black 3
	// if white
	public int occupied(int row, int col) {
		long intersection = getBite(getSquare(row, col));
		if ((intersection | emptySquares()) == emptySquares()) {
			// empty
			return 1;
		} else if ((intersection | blackpieces) == blackpieces) {
			// black
			return 2;
		}
		// white
		return 3;
	}
	public void setLastMove(int row, int col){
		lastMove = getBite(getSquare(row,col));
		if((lastMove & blackpieces)==blackpieces)
			blackLastMove = lastMove;
	}
	public void setNewBounds(){
		for(int row=0;row<height;row++){
			for(int col=0;col<width;col++){
				if(occupied(row,col)==2){
					if(row<=2)
						uperBound=0;
					
					else
						uperBound=row-2;
					return;
				}
			}
		}
		for(int row=height-1;row>=0;row--){
			for(int col=0;col<width;col++){
				if(occupied(row,col)==2){
					if(row>=height-1)
						lowerBound=height-1;
					
					else
						uperBound=row+2;
					return;
				}
			}
		}
	}
	// i dont use this method but this removes a stone that has been played
	public void removeStone(int row, int col){
		long stoneToRemove = getBite(getSquare(row,col));
		if ((stoneToRemove & blackpieces)==stoneToRemove){
			blackpieces = blackpieces &~ stoneToRemove;
		}else{
			whitepieces = whitepieces &~stoneToRemove;
		}
		
		blackToplay = !blackToplay;
		moveNumber -= 1;
	}

	// add a stone in the board and widens the uperBound and loerBound when it
	// should , it also change turns
	public void addStone(int row, int col) {

		if (!isLegal(row, col)) {

			return;
		}

		long newStone = getBite(getSquare(row, col));
		if (blackToplay) {
			blackpieces |= newStone;
			blackLastMove = newStone;
			if(row==uperBound){
				if(row>1)
					uperBound = uperBound -2;
				else if (row>0)
					uperBound = uperBound-1;
			}
			else if(row==uperBound+1){
				if(row>1)
					uperBound = uperBound-1;
			}
			if (row < uperBound)
				uperBound = 0;
			//lowerBound
			if(row==lowerBound){
				if(row<height-2)
					lowerBound = lowerBound+2;
				else if(row<height-1)
					lowerBound = lowerBound+1;
			}
			else if(row==lowerBound-1){
				if(row<height-2)
					lowerBound = lowerBound+1;
			}
			if (row > lowerBound)
				lowerBound = height - 1;
			

		} else {

			whitepieces |= newStone;
//			if(row==uperBound){
//				if(row>1)
//					uperBound = uperBound -2;
//				else if (row>0)
//					uperBound = uperBound-1;
//			}
//			else if(row==uperBound+1){
//				if(row>1)
//					uperBound = uperBound-1;
//			}
//			if (row < uperBound)
//				uperBound = 0;
//			//lowerbound
//			if(row==lowerBound){
//				if(row<height-2)
//					lowerBound = lowerBound+2;
//				else if(row<height-1)
//					lowerBound = lowerBound+1;
//			}
//			else if(row==lowerBound-1)
//				if(row<height-2)
//					lowerBound = lowerBound+1;
//			if (row > lowerBound)
//				lowerBound = height - 1;
			
		}
		
		lastMove = newStone;
		board();
		
		blackToplay = !blackToplay;
		moveNumber += 1;

	}

	// the bellow 2 methods are used for the hashset that saves a position , and
	// if the position might occure in a different move order
	// it is not analised again
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + blackCaptives;
		result = prime * result + (blackToplay ? 1231 : 1237);
		result = prime * result + (int) (blackpieces ^ (blackpieces >>> 32));
		result = prime * result + height;

		// result = prime * result + whiteCaptives;
		result = prime * result + (int) (whitepieces ^ (whitepieces >>> 32));
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bitboard other = (Bitboard) obj;

		if (blackToplay != other.blackToplay)
			return false;
		if (blackpieces != other.blackpieces)
			return false;
		if (height != other.height)
			return false;
		if (whitepieces != other.whitepieces)
			return false;
		if (width != other.width)
			return false;

		return true;
	}

}
