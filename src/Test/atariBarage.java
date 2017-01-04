package Test;


import static org.junit.Assert.*;

import org.junit.Test;

import BitMap.Bitboard;

public class atariBarage {
	
	@Test
	public void atariBarageTest(){
		System.out.println("atariBarageTest");
		Bitboard test = new Bitboard(9);
		test.addStone(5, 0);
		test.addStone(6, 0);
		
		test.addStone(4, 1);
		test.addStone(7, 0);
		
		test.addStone(5, 2);
		test.addStone(8, 0);
		
		
		test.boardPrinter();
//		long result = (test.south(test.getBlackLastMove()) & test.getBit(test.getSquare(2, 3)));
//		long result2 = (test.east(test.getBlackLastMove()) & test.getBit(test.getSquare(2, 3)));
//		long result3 = (test.southEast(test.getBlackLastMove()) & test.getBit(test.getSquare(2, 3)));
//		long result4 = (test.southWest(test.getBlackLastMove()) & test.getBit(test.getSquare(2, 3)));
//		long result5 = (test.south(test.getBit(test.getSquare(2, 3)))) & test.getBlackLastMove();
//		long result6 = (test.east(test.getBit(test.getSquare(2, 3)))) & test.getBlackLastMove();
//		long result7 = (test.southEast(test.getBit(test.getSquare(2, 3)))) & test.getBlackLastMove();
//		long result8 = (test.southWest(test.getBit(test.getSquare(2, 3)))) & test.getBlackLastMove();
		int lastrow = test.squareToRowCol(test.longToSquare(test.getBlackLastMove()))[0];
		int lastcol = test.squareToRowCol(test.longToSquare(test.getBlackLastMove()))[1];
		boolean result10 = (lastrow==2 || lastcol ==3 || lastrow+lastcol==6);
		System.out.println("last move"+test.squareToRowCol(test.longToSquare(test.getBlackLastMove()))[0]+test.squareToRowCol(test.longToSquare(test.getBlackLastMove()))[1]);
		//long result9 = result7 ;
		
		assertTrue(result10);
	}
}
