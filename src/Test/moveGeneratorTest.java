package Test;

import minimax.FourRowMove;
import minimax.FourRowMoveGenerator;
import minimax.FourRowState;

import org.junit.Test;

import BitMap.Bitboard;

public class moveGeneratorTest {
	@Test
	public void moveGenerationTest(){
		System.out.println("moveGenerationTest");
		Bitboard test = new Bitboard(10);
		test.addStone(5, 0);
		test.addStone(5, 1);
		
		test.addStone(5, 2);
		test.addStone(6, 2);
		
		test.addStone(4, 0);
		test.addStone(6, 0);
		
	    test.addStone(6, 3);
	    test.addStone(4 ,1);
		test.boardPrinter();
//		
//		test.addStone(3, 0);
//		test.addStone(5, 2);
		
		//test.boardPrinter();
		System.out.println("square "+test.getS());
		System.out.println("checkTwoVerticalWhite "+test.checkTwoVertical(test.getWhitepieces()));
		System.out.println("checkTwoVertical "+test.checkTwoVertical(test.getBlackpieces()));
		System.out.println("square test.getS() = "+test.getS());
		System.out.println("lowebound= "+test.getLowerBound()+" uperBound= "+test.getUperBound());
		FourRowState state7 = new FourRowState(test);
		FourRowMove best = new FourRowMove(10,3);
		FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
//		for(FourRowMove move : mg7.getLegalMoves(state7, best)){
//			System.out.print(" r,c " + move.getRow()+move.getCol());
//		}
	}
}
