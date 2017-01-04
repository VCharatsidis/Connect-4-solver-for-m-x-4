package Test;

import static org.junit.Assert.*;

import org.junit.Test;

import BitMap.Bitboard;

public class moves {
	@Test
	public void goodMoveBlackTest(){
		System.out.println("goodMoveBlackTest");
		Bitboard test = new Bitboard(9);
		test.addStone(4, 1);
		test.addStone(4, 0);
		
		test.addStone(3, 1);
		test.addStone(5, 1);
		
		test.addStone(2, 0);
		test.addStone(4, 2);
		//test.boardPrinter();
		//boolean result =  test.goodMoveForBlack(0, 3);
		//assertTrue(result);
	}
	
	@Test
	public void countThreatsRowColTest(){
		System.out.println("countThreatsRowColTest");
		Bitboard test = new Bitboard(9);
		test.addStone(4, 1);
		test.addStone(5, 2);
		
		test.addStone(3, 1);
		test.addStone(2, 1);
		
		test.addStone(3, 2);
		test.addStone(4, 0);
		
		test.addStone(3, 3);
		test.addStone(3, 0);
		
		test.addStone(5, 1);
		test.addStone(6, 1);
		
		test.addStone(5, 0);
		test.addStone(2, 3);
		
		test.boardPrinter();
		
		int result = test.countThreatsRowCol(2, 0, false, 1);
		assertEquals(result,2);
	}
}
