package Test;

import static org.junit.Assert.*;
import minimax.FourRowEvaluation;
import minimax.FourRowMove;
import minimax.FourRowMoveGenerator;
import minimax.FourRowNegaMaxPlayer;
import minimax.FourRowRules;
import minimax.FourRowState;

import org.junit.Test;

import BitMap.Bitboard;

public class iFour {
	
	@Test
	public void isTerminalTest(){
		Bitboard test = new Bitboard(11);
		test.addStone(5, 1);
		test.addStone(6, 1);
		
		test.addStone(4, 2);
		test.addStone(5, 2);
		
		test.addStone(3, 3);
		test.addStone(4, 3);
		
		test.addStone(3, 1);
		test.addStone(7, 0);
		
		long blackpieces = test.getBlackpieces();
		long whitepieces = test.getWhitepieces();
		long highest = test.getBite(test.getSquare(4,3));
		
		FourRowState state = new FourRowState(test);
		FourRowRules rules = new FourRowRules();
	
		boolean result = rules.isTerminal(state);
		
		assertTrue(result);
	}
	@Test
	public void longToSquareTest(){
		Bitboard test = new Bitboard(11);
		test.addStone(5, 1);
		test.addStone(4, 1);
		
		int result = test.longToSquare(test.getWhitepieces());
		//assertEquals(result,17);
		System.out.println("longtosquare "+result);
		
		int result2 = test.longToSquare(test.getBlackpieces());
		assertEquals(result2,21);
	}
	@Test
	public void isFourTest() {
		Bitboard test = new Bitboard(11);
		
		test.addStone(5, 1);
		test.addStone(5, 2);
		
		test.addStone(4, 2);
		test.addStone(4, 3);
		
		test.addStone(3, 3);
		test.addStone(4, 1);
		
		test.addStone(6, 0);
		
		long blackpieces = test.getBlackpieces();
		long whitepieces = test.getWhitepieces();
		long highest = test.getBite(test.getSquare(3,3));
		
		boolean result = test.isFour(highest, blackpieces);
		
		Bitboard test2 = new Bitboard(11);
		test2.addStone(5, 1);
		test2.addStone(4, 1);
		
		test2.addStone(4, 0);
		test2.addStone(4, 2);
		
		test2.addStone(6, 2);
		test2.addStone(4, 3);
		
		test2.addStone(7, 3);
		
		long blackpieces2 = test2.getBlackpieces();
		long whitepieces2 = test2.getWhitepieces();
		long highest2 = test2.getBite(test2.getSquare(4,0));
		
		boolean result2 = test2.isFour(highest2, blackpieces2);
		assertTrue(result2);
	}
	@Test
	public void getSquareTest(){
		Bitboard test = new Bitboard(11);
		assertEquals(test.getSquare(7, 3),31);
	}
	@Test
	public void squareToRowColTest(){
		Bitboard test = new Bitboard(11);
		assertEquals(test.squareToRowCol(31)[1],3);
	}
	
	@Test
	public void isGameOverTest(){
		Bitboard test = new Bitboard(11);
		
		test.addStone(5, 1);
		test.addStone(6, 2);
		
		test.addStone(7, 1);
		test.addStone(7, 2);
		
		test.addStone(8, 1);
		test.addStone(8, 2);
		
		test.addStone(3, 1);
		test.addStone(5, 2);
		
		Bitboard test2 = new Bitboard(9);
		test2.addStone(4, 1);
		test2.addStone(3, 2);
		
		test2.addStone(3,0);
		test2.addStone(2, 2);
		
		test2.addStone(5, 2);
		test2.addStone(1, 2);
		
		test2.addStone(6, 3);
		long blackpieces = test2.getBlackpieces();
		long whitepieces = test2.getWhitepieces();
		boolean result = test2.isGameOver(blackpieces);
		
		assertTrue(result);
	}


	@Test
	public void numThreatsTest(){
		System.out.println("numThreatTest");
		
		Bitboard test = new Bitboard(9);
		test.addStone(4, 1);
		test.addStone(5,2);
		
		test.addStone(3, 1);
		test.addStone(2, 1);
		
		test.addStone(4, 0);
		test.addStone(3, 0);
		
		test.addStone(6, 1);
		test.addStone(5, 1);
		
		test.addStone(5, 0);
		test.addStone(0, 3);
		
		test.addStone(1, 2);
		test.addStone(2, 2);
		
		test.addStone(6, 0);
		test.addStone(7, 0);
		
		test.addStone(6, 2);
		test.addStone(6, 3);
		
		test.addStone(7, 2);
		test.addStone(8, 3);
		
		test.addStone(8, 2);
		//test.boardPrinter();
		
		int k = test.numThreats(true);
		System.out.println(test.getS())	;
		//System.out.println(result7);
		assertEquals(k,2)	;	
	}
	
	@Test
	public void numThreatsTest2(){
		System.out.println("numThreatTest22222");
		Bitboard test7 = new Bitboard(10);
		test7.addStone(7,0);
		test7.addStone(6, 0);
		
		test7.addStone(8, 0);
		test7.addStone(0, 0);
		
		test7.addStone(9, 0);
		//test7.addStone(3, 0);
		
		//test7.addStone(1, 2);
		
		//test7.boardPrinter();
		int k =test7.numThreats(true);
		assertEquals(k,0);	
		
	}
	@Test
	public void checkTwoVerticalTest(){
		Bitboard test = new Bitboard(9);
		test.addStone(4, 1);
		test.addStone(2, 1);
		
		test.addStone(5, 1);
		test.addStone(4, 2);
		
		//test.boardPrinter();
		boolean k = test.checkTwoVertical(test.getBlackpieces());
		int result=test.getS();
		System.out.println("checkTwoVerticalTest-getS() "+result);
		assertTrue(k);
	}
	
	@Test
	public void isThreatTest(){
		System.out.println("IS THREAT ");
		Bitboard test = new Bitboard(9);
		test.addStone(5, 1);
		test.addStone(6, 2);
		
		test.addStone(4, 1);
		test.addStone(3, 1);
		
		test.addStone(4, 3);
		test.addStone(5, 3);
		
		test.addStone(3, 2);
		test.addStone(3, 3);
		//test.boardPrinter();
		//boolean result = test.isThreat(4, 1, 1, 0, true);
		
		boolean result = test.isThreat(5,-1,2,0,true,2);
		assertTrue(result);
		//boolean result = test.isThreat(row, orientationR, col, orientationC, blackCheck)
	}

	@Test
	public void occupiedTest(){
		Bitboard test = new Bitboard(11);
		test.addStone(5,1);
		test.addStone(5,2);
		
		test.addStone(6,1);
		test.addStone(6,2);
		
		test.addStone(4,1);
		test.addStone(4,2);
		
		boolean result = (test.occupied(5, 2)==3);
		boolean result2 =(test.occupied(4,1)==2);
		boolean result3 = (test.occupied(3, 1)==1 && test.occupied(7, 1)==1);
		assertTrue(result2);
	}
	@Test
	public void checkTest(){
		Bitboard test = new Bitboard(11);
		test.addStone(5,1);
		test.addStone(5,2);
		
		test.addStone(6,1);
		test.addStone(6,2);
		
		test.addStone(4,1);
		test.addStone(4,2);
		
		long blackpieces = test.getBlackpieces();
		long whitepieces = test.getWhitepieces();
		
		long highest = test.getBite(test.getSquare(4,2));
		long sample = test.maskThreeSouth(highest);
		
		assertTrue(test.check(sample, whitepieces));
		
	}
	@Test
	public void goodMoveForBlackTest(){
		System.out.println("moveGoodForBlack");
		Bitboard test = new Bitboard(11);
		test.addStone(5, 1);
		test.addStone(6, 2);
		
		test.addStone(4, 1);
		test.addStone(3, 1);
		
		test.addStone(4, 3);
		test.addStone(5, 3);
		
		test.addStone(3, 2);
		test.addStone(3, 3);
		
		test.boardPrinter();
		//boolean result =test.goodMoveForBlack(8,2);
		//assertTrue(result);
	}
	@Test
	public void countThreatRowColTest(){
		System.out.println("countThreatRowCol");
		Bitboard test = new Bitboard(11);
		test.addStone(5, 1);
		test.addStone(6, 2);
		
		test.addStone(4,1);
		test.addStone(6,1);
		
		test.addStone(2, 1);
		test.addStone(3, 1);
		
		test.addStone(3, 2);
		test.addStone(6, 0);
		
		test.addStone(6, 3);
		test.addStone(1,0);
		
		//test.boardPrinter();
		int result = test.countThreatsRowCol(2, 3, true, 1);
		assertEquals(result,1);
	}

	@Test
	public void moveGenerationTest(){
		System.out.println("moveGenerationTest");
		Bitboard test = new Bitboard(9);
		test.addStone(4, 1);
		test.addStone(5, 2);
		
		test.addStone(3, 1);
		test.addStone(5, 1);
		
		test.addStone(5, 0);
		
		
		//test.boardPrinter();
		FourRowState state7 = new FourRowState(test);
		FourRowMove best = new FourRowMove(8,3);
		FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
//		for(FourRowMove move : mg7.getLegalMoves(state7, best)){
//			System.out.print(" r,c " + move.getRow()+move.getCol());
//		}
	}

	@Test	
	public void evalTest(){
		Bitboard test = new Bitboard(10);
		test.addStone(5, 1);
		test.addStone(5, 2);
		
		test.addStone(4, 1);
		test.addStone(6, 1);
		
		test.addStone(4, 3);
		test.addStone(5, 0);
		
		test.addStone(2, 3);
		//test.addStone(7, 2);
		test.boardPrinter();
		
		
		FourRowEvaluation eval = new FourRowEvaluation();
		FourRowState state = new FourRowState(test);
		
    	int result = eval.evaluate(state);
    	System.out.println("eval7= "+result);
    	assertEquals(result,1);
	}
	@Test
	public void checkJustThreeTest(){
		Bitboard test = new Bitboard(11);
		test.addStone(5, 1);
		test.addStone(3, 0);
		
		test.addStone(6, 1);
		test.addStone(3, 1);
		
		test.addStone(8,1);
		test.addStone(3,2);
		
		long blackpieces = test.getBlackpieces();
		long whitepieces = test.getWhitepieces();
		System.out.println("checkJustThree= "+test.checkJustThree(whitepieces));
		boolean result = test.checkJustThree(whitepieces);
		assertTrue(result);
	}
	
	
	
}
