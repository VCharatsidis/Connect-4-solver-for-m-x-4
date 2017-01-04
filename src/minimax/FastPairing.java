package minimax;

import java.util.ArrayList;

import lombok.Getter;

import org.apache.commons.lang3.tuple.Pair;

import BitMap.Bitboard;

public class FastPairing {

	@Getter
	private Bitboard board;
	@Getter
	private int width;
	@Getter
	private int height;
	private long blackpieces;
	private long whitepieces;

	private long rowMask;
	private long firstDiagonalMask;
	private long secondDiagonalMask;
	private long columnMask;
	@Getter
	boolean[] rowGroups;
	@Getter
	boolean[] columnGroups;
	@Getter
	boolean[] firstDiagonalGroups;
	@Getter
	boolean[] secondDiagonalGroups;
	@Getter
	boolean[] rowGroupsBlack;
	@Getter
	boolean[] columnGroupsBlack;
	@Getter
	boolean[] firstDiagonalGroupsBlack;
	@Getter
	boolean[] secondDiagonalGroupsBlack;

	private long firstDiagonalMiniMaskA;
	private long firstDiagonalMiniMaskB;
	private long firstDiagonalMiniMaskC;

	private long secondDiagonalMiniMaskA;
	private long secondDiagonalMiniMaskB;
	private long secondDiagonalMiniMaskC;

	@Getter
	private long rowMiniMaskA;
	@Getter
	private long rowMiniMaskB;
	@Getter
	private long rowMiniMaskC;

	@Getter
	private ArrayList<Long> firstDiagonalBoard;
	@Getter
	private ArrayList<Long> secondDiagonalBoard;
	@Getter
	private ArrayList<Long> rowBoard;
	@Getter
	private ArrayList<Long> rowBoardFaster;


	@Getter
	private ArrayList<ArrayList<Long>> firstDiagonalLongs = new ArrayList<>();
	@Getter
	private ArrayList<ArrayList<Long>> secondDiagonalLongs = new ArrayList<>();
	@Getter
	private ArrayList<ArrayList<Long>> rowLongs = new ArrayList<>();
	// @Getter
	// private ArrayList<Pair<Long, Long>> firstXORsecond = new ArrayList<>();
	// @Getter
	// private ArrayList<Pair<Long, Pair<Long, Long>>> rowXORfirstsecond = new
	// ArrayList();
	@Getter
	private Pair<Long, Pair<Long, Pair<Long, Long>>> finalXOR;
	@Getter
	private boolean solution = false;
	private int rows = 0;
	private int columns = 0;
	private int fdia=0;
	private int sdia=0;

	public FastPairing(Bitboard board) {
		this.board = board;

			width = board.getWidth();
			height = board.getHeight();
			blackpieces = board.getBlackpieces();
			whitepieces = board.getWhitepieces();
			firstDiagonalGroupsBlack = new boolean[height - 3];
			firstDiagonalGroups = new boolean[height - 3];
			secondDiagonalGroupsBlack = new boolean[height - 3];
			secondDiagonalGroups = new boolean[height - 3];
			rowGroups = new boolean[height];
			rowGroupsBlack = new boolean[height];
			columnGroups = new boolean[(height - 3) * width];
			columnGroupsBlack = new boolean[(height - 3) * width];
			findGroups();
			for (int i = 0; i < rowGroups.length; i++) {
				if (rowGroups[i])
					rows++;
			}
			for (int p = 0; p < width; p++) {
				for (int z = 0; z < height - 3; z++) {
					if (columnGroups[p + z * width]) {
						columns++;
						break;
					}
				}

			}
			for (int i = 0; i < firstDiagonalGroups.length; i++) {
				if (firstDiagonalGroups[i])
					fdia++;
			}
			for (int i = 0; i < secondDiagonalGroups.length; i++) {
				if (secondDiagonalGroups[i])
					sdia++;
			}
			if(((sdia+fdia+columns+rows)*2)>Long.bitCount(~(blackpieces|whitepieces)))
				return;
			firstDiagonalBoard = new ArrayList<>();
			secondDiagonalBoard = new ArrayList<>();
			rowBoard = new ArrayList<>();
			rowBoardFaster = new ArrayList<>();
			makeFirstDiagonalLongs();
			long feedboard = 0;
			makeFirstDiagonalBoard(firstDiagonalLongs.size() - 1, feedboard);
			makeSecondDiagonalLongs();
			makeRowLongs();

		
		// feedboard = 0;
		// makeSecondDiagonalBoard(secondDiagonalLongs.size() - 1, feedboard);
		firstXORsecondFaster();
		// feedboard = 0;

		// makeRowBoard(rowLongs.size() - 1, feedboard);
		// int rows = 0;

		//   
		//
		// if (firstXORsecond.size() > 0
		// && Long.bitCount(~(firstXORsecond.get(0).getLeft()
		// | firstXORsecond.get(0).getRight() | whitepieces | blackpieces)) >=
		// (rows * 2)) {
		// rowXORdiagonals();
		// }
			// rowXORfirstsecond();


	}


	public void finalXOR(Pair<Long, Pair<Long, Long>> board) {
		long columns = 0;
		long x =0;
		long[] used = new long[height-3];
		int free=0;
		columns = (board.getLeft() | board.getRight().getLeft() | board
				.getRight().getRight());
			boolean goodBoard = true;
			
			for (int j = 0; j < width; j++) {
				used = new long[height-3];
				for (int k = 0; k < height - 3; k++) {
					south(k * width + j);
					x = (columnMask & ~columns);
					if ((columnMask & whitepieces) != 0)
						continue;
					free = Long.bitCount(x & ~blackpieces);
					if (free < 2) {
						goodBoard = false;
						break;
					}
					if(free<3){
						for(int z = 0; z<used.length;z++){
							if(Long.bitCount(used[z] & x & ~blackpieces)==1){
								goodBoard = false;
								break; 
							}
						}
					}
					used[k]=x;
				}
			}
			if (goodBoard) {
			finalXOR = Pair.of(~columns & ~whitepieces & ~blackpieces, board);
			return;
			}


	}

	public void rowXORdiagonals(Pair<Long, Long> board) {
		long solution;
		long diagonals = 0;

		diagonals = (board.getLeft() | board
					.getRight());

			boolean goodBoard = true;
			for (int k = 0; k < height; k++) {
				east(k * width);
				if ((rowMask & whitepieces) != 0)
					continue;

				long x;
				x = (rowMask & ~diagonals);

				if (Long.bitCount(x & ~blackpieces) < 2) {
					goodBoard = false;
					break;
				}

			}
			if (goodBoard) {
				long appropriateBoard = blackpieces | whitepieces | diagonals;
				solution = 0;

				

				makeRowBoardFaster(rowLongs.size() - 1, appropriateBoard,
						solution);
				int rB = rowBoardFaster.size();
			for (int j = rB; j < rowBoardFaster.size(); j++) {
				if (Long.bitCount(~(rowBoardFaster.get(j) | board.getLeft()
						| board.getRight() | whitepieces | blackpieces)) >= (columns * 2)) {

					finalXOR(Pair.of(rowBoardFaster.get(j), board));
					if (finalXOR != null)
						return;
				}

				}
			}

	}

	public void firstXORsecondFaster() {
		long solution;
		long firstDiagonals = 0;
		if (firstDiagonalBoard.size() == 0) {
			long appropriateBoard = blackpieces | whitepieces | firstDiagonals;
			solution = 0;
			makeSdiagonalBoardFaster(secondDiagonalLongs.size() - 1,
					appropriateBoard, solution);
			int rB = secondDiagonalBoard.size();
			if(rB==0){
				rowXORdiagonals(Pair.of((long)0,(long)0)) ;
			}
			makeSdiagonalBoardFaster(secondDiagonalLongs.size() - 1,
					appropriateBoard, solution);
			for (int j = rB; j < secondDiagonalBoard.size(); j++) {

				if (Long.bitCount(~(secondDiagonalBoard.get(j) | firstDiagonals
						| whitepieces | blackpieces)) >= (rows * 2)) {

					rowXORdiagonals(Pair.of((long) 0,
							secondDiagonalBoard.get(j)));
					if (finalXOR != null)
						return;
				}
			}
		}
		for (int i = 0; i < firstDiagonalBoard.size(); i++) {
			firstDiagonals = firstDiagonalBoard.get(i);

			boolean goodBoard = true;
			for (int k = 0; k < height; k++) {
				southWest(k * width);
				if ((secondDiagonalMask & whitepieces) != 0)
					continue;

				long x;
				x = (secondDiagonalMask & ~firstDiagonals);

				if (Long.bitCount(x & ~blackpieces) < 2) {
					goodBoard = false;
					break;
				}

			}
			if (goodBoard) {

				long appropriateBoard = blackpieces | whitepieces
						| firstDiagonals;
				solution = 0;

				
				makeSdiagonalBoardFaster(secondDiagonalLongs.size() - 1,
						appropriateBoard,
						solution);
				int rB = secondDiagonalBoard.size();
				for (int j = rB; j < secondDiagonalBoard.size(); j++) {


					if (Long.bitCount(~(secondDiagonalBoard.get(j)
							| firstDiagonals | whitepieces | blackpieces)) >= (rows * 2)) {

						rowXORdiagonals(Pair.of(firstDiagonalBoard.get(i),
								secondDiagonalBoard.get(j)));
						if (finalXOR != null)
							return;
					}
				}
			}

		}
	}

	// public void rowXORfirstsecond() {
	// Pair<Long, Pair<Long, Long>> a;
	// Pair<Long, Long> b = Pair.of((long) 0, (long) 0);
	// if (rowBoard.size() == 0 && firstXORsecond.size() > 0) {
	// for (int i = 0; i < firstXORsecond.size(); i++) {
	// a = Pair.of((long) 0, firstXORsecond.get(i));
	// rowXORfirstsecond.add(a);
	//
	// }
	// return;
	// }
	// else if (rowBoard.size() > 0 && firstXORsecond.size() == 0) {
	// for (int i = 0; i < rowBoard.size(); i++) {
	//
	// a = Pair.of(rowBoard.get(i), b);
	// rowXORfirstsecond.add(a);
	//
	// }
	// return;
	// }
	// else if (rowBoard.size() == 0 && firstXORsecond.size() == 0) {
	// a = Pair.of((long) 0, b);
	// rowXORfirstsecond.add(a);
	// return;
	// }
	// for (int i = 0; i < rowBoard.size(); i++) {
	// for (int j = 0; j < firstXORsecond.size(); j++) {
	// if ((rowBoard.get(i) & ~firstXORsecond.get(j).getLeft() & ~firstXORsecond
	// .get(j).getRight()) == rowBoard.get(i)) {
	// a = Pair.of(rowBoard.get(i), firstXORsecond.get(j));
	// rowXORfirstsecond.add(a);
	//
	// }
	// }
	// }
	// }

	// public void firstXORsecond() {
	// Pair<Long, Long> a;
	// if (firstDiagonalBoard.size() == 0 && secondDiagonalBoard.size() > 0) {
	// for (int i = 0; i < secondDiagonalBoard.size(); i++) {
	// a = Pair.of((long) 0, secondDiagonalBoard.get(i));
	// firstXORsecond.add(a);
	// }
	// }
	// else if (firstDiagonalBoard.size() > 0
	// && secondDiagonalBoard.size() == 0) {
	// for (int i = 0; i < firstDiagonalBoard.size(); i++) {
	// a = Pair.of(firstDiagonalBoard.get(i), (long) 0);
	// firstXORsecond.add(a);
	// }
	// }
	// else if (firstDiagonalBoard.size() == 0
	// && secondDiagonalBoard.size() == 0) {
	// a = Pair.of((long) 0, (long) 0);
	// firstXORsecond.add(a);
	// }
	// for (int i = 0; i < firstDiagonalBoard.size(); i++) {
	// for (int j = 0; j < secondDiagonalBoard.size(); j++) {
	// if ((firstDiagonalBoard.get(i) & ~secondDiagonalBoard.get(j)) ==
	// firstDiagonalBoard
	// .get(i)) {
	// a = Pair.of(firstDiagonalBoard.get(i),
	// secondDiagonalBoard.get(j));
	// firstXORsecond.add(a);
	//
	// }
	//
	// }
	// }
	//
	// }

	public void makeRowBoardFaster(int size, long board, long solution) {

		if (rowLongs.size() == 0) {
			return;
		}
		if (size > -1) {
			for (int g1 = 0; g1 < rowLongs.get(size).size(); g1++) {
				if ((board & rowLongs.get(size).get(g1)) == 0) {

					solution |= rowLongs.get(size).get(g1);
					makeRowBoardFaster(size - 1, board, solution);
					solution = solution & ~rowLongs.get(size).get(g1);
				}
			}
		}
		if (size == -1) {

			rowBoardFaster.add(solution);
		}
	}

	public void makeRowBoard(int size, long board) {

		if (rowLongs.size() == 0) {
			return;
		}
		if (size > -1) {
			for (int g1 = 0; g1 < rowLongs.get(size).size(); g1++) {
				board |= rowLongs.get(size).get(g1);
				makeRowBoard(size - 1, board);
				board = board & ~rowLongs.get(size).get(g1);
			}
		}
		if (size == -1) {
			rowBoard.add(board);
		}
	}

	public void makeSdiagonalBoardFaster(int size, long board, long solution) {

		if (secondDiagonalLongs.size() == 0) {
			return;
		}
		if (size > -1) {
			for (int g1 = 0; g1 < secondDiagonalLongs.get(size).size(); g1++) {
				if ((board & secondDiagonalLongs.get(size).get(g1)) == 0) {

					solution |= secondDiagonalLongs.get(size).get(g1);
					makeSdiagonalBoardFaster(size - 1, board, solution);
					solution = solution
							& ~secondDiagonalLongs.get(size).get(g1);
				}
			}
		}
		if (size == -1) {

			secondDiagonalBoard.add(solution);
		}
	}

	public void makeSecondDiagonalBoard(int size, long board) {
		if (secondDiagonalLongs.size() == 0) {
			return;
		}
		if (size > -1) {
			for (int g1 = 0; g1 < secondDiagonalLongs.get(size).size(); g1++) {
				board |= secondDiagonalLongs.get(size).get(g1);
				makeSecondDiagonalBoard(size - 1, board);
				board = board & ~secondDiagonalLongs.get(size).get(g1);

			}
		}
		if (size == -1) {
			secondDiagonalBoard.add(board);
		}
	}
	public void makeFirstDiagonalBoard(int size, long board) {
		if (firstDiagonalLongs.size() == 0) {

			return;
		}
		if (size > -1) {
			for (int g1 = 0; g1 < firstDiagonalLongs.get(size).size(); g1++) {
				board |= firstDiagonalLongs.get(size).get(g1);
				makeFirstDiagonalBoard(size - 1, board);
				board = board & ~firstDiagonalLongs.get(size).get(g1);

			}
		}
		if (size == -1) {
			firstDiagonalBoard.add(board);
		}
	}
	
	public void findGroups() {
		rowGroups();
		columnGroups();
		firstDiagonalGroups();
		secondDiagonalGroups();
	}

	public void makeSecondDiagonalLongs() {
		ArrayList<Long> group = new ArrayList<>();
		int sdia = 0;
		for (int i = 0; i < secondDiagonalGroups.length; i++)
			if (secondDiagonalGroups[i])
				sdia++;
		if (sdia == 0)
			return;
		for (int i = 0; i < secondDiagonalGroups.length; i++) {

			if (secondDiagonalGroupsBlack[i]) {
				group = new ArrayList<>();
				secondDiagonalMiniMaskA(i * width);
				if ((secondDiagonalMiniMaskA & ~blackpieces) == secondDiagonalMiniMaskA)
					group.add(secondDiagonalMiniMaskA);
				secondDiagonalMiniMaskB(i * width);
				if ((secondDiagonalMiniMaskB & ~blackpieces) == secondDiagonalMiniMaskB)
					group.add(secondDiagonalMiniMaskB);
				secondDiagonalMiniMaskC(i * width);
				if ((secondDiagonalMiniMaskC & ~blackpieces) == secondDiagonalMiniMaskC)
					group.add(secondDiagonalMiniMaskC);
				secondDiagonalMiniMaskA((i + 1) * width - 1);
				if ((secondDiagonalMiniMaskA & ~blackpieces) == secondDiagonalMiniMaskA)
					group.add(secondDiagonalMiniMaskA);
				secondDiagonalMiniMaskB((i + 1) * width - 1);
				if ((secondDiagonalMiniMaskB & ~blackpieces) == secondDiagonalMiniMaskB)
					group.add(secondDiagonalMiniMaskB);
				secondDiagonalMiniMaskA((i + 2) * width - 2);
				if ((secondDiagonalMiniMaskA & ~blackpieces) == secondDiagonalMiniMaskA)
					group.add(secondDiagonalMiniMaskA);
				secondDiagonalLongs.add(group);
			} else if (secondDiagonalGroups[i]) {
				group = new ArrayList<>();
				secondDiagonalMiniMaskA(i * width);
				group.add(secondDiagonalMiniMaskA);
				secondDiagonalMiniMaskB(i * width);
				group.add(secondDiagonalMiniMaskB);
				secondDiagonalMiniMaskC(i * width);
				group.add(secondDiagonalMiniMaskC);
				secondDiagonalMiniMaskA((i + 1) * width - 1);
				group.add(secondDiagonalMiniMaskA);
				secondDiagonalMiniMaskB((i + 1) * width - 1);
				group.add(secondDiagonalMiniMaskB);
				secondDiagonalMiniMaskA((i + 2) * width - 2);
				group.add(secondDiagonalMiniMaskA);
				secondDiagonalLongs.add(group);
			}

		}
	}

	public void makeFirstDiagonalLongs() {
		int fdia = 0;
		for (int i = 0; i < firstDiagonalGroups.length; i++)
			if (firstDiagonalGroups[i])
				fdia++;
		if (fdia == 0)
			return;
		ArrayList<Long> group = new ArrayList<>();
		for (int i = 0; i < firstDiagonalGroups.length; i++) {

			if (firstDiagonalGroupsBlack[i]) {
				group = new ArrayList<>();
				firstDiagonalMiniMaskA(i * width);
				if ((firstDiagonalMiniMaskA & ~blackpieces) == firstDiagonalMiniMaskA)
					group.add(firstDiagonalMiniMaskA);
				firstDiagonalMiniMaskB(i * width);
				if ((firstDiagonalMiniMaskB & ~blackpieces) == firstDiagonalMiniMaskB)
					group.add(firstDiagonalMiniMaskB);
				firstDiagonalMiniMaskC(i * width);
				if ((firstDiagonalMiniMaskC & ~blackpieces) == firstDiagonalMiniMaskC)
					group.add(firstDiagonalMiniMaskC);
				firstDiagonalMiniMaskA((i + 1) * width + 1);
				if ((firstDiagonalMiniMaskA & ~blackpieces) == firstDiagonalMiniMaskA)
					group.add(firstDiagonalMiniMaskA);
				firstDiagonalMiniMaskB((i + 1) * width + 1);
				if ((firstDiagonalMiniMaskB & ~blackpieces) == firstDiagonalMiniMaskB)
					group.add(firstDiagonalMiniMaskB);
				firstDiagonalMiniMaskA((i + 2) * width + 2);
				if ((firstDiagonalMiniMaskA & ~blackpieces) == firstDiagonalMiniMaskA)
					group.add(firstDiagonalMiniMaskA);
				firstDiagonalLongs.add(group);
			} else if (firstDiagonalGroups[i]) {
				group = new ArrayList<>();
				firstDiagonalMiniMaskA(i * width);
				group.add(firstDiagonalMiniMaskA);
				firstDiagonalMiniMaskB(i * width);
				group.add(firstDiagonalMiniMaskB);
				firstDiagonalMiniMaskC(i * width);
				group.add(firstDiagonalMiniMaskC);
				firstDiagonalMiniMaskA((i + 1) * width + 1);
				group.add(firstDiagonalMiniMaskA);
				firstDiagonalMiniMaskB((i + 1) * width + 1);
				group.add(firstDiagonalMiniMaskB);
				firstDiagonalMiniMaskA((i + 2) * width + 2);
				group.add(firstDiagonalMiniMaskA);
				firstDiagonalLongs.add(group);
			}


		}
	}

	public void makeRowLongs() {
		int rows = 0;
		for (int i = 0; i < rowGroups.length; i++)
			if (rowGroups[i])
				rows++;
		if (rows == 0)
			return;
		ArrayList<Long> group = new ArrayList<>();
		for (int i = 0; i < rowGroups.length; i++) {

			if (rowGroupsBlack[i]) {
				group = new ArrayList<>();
				rowMiniMaskA(i * width);
				if ((rowMiniMaskA & ~blackpieces) == rowMiniMaskA)
					group.add(rowMiniMaskA);
				rowMiniMaskB(i * width);
				if ((rowMiniMaskB & ~blackpieces) == rowMiniMaskB)
					group.add(rowMiniMaskB);
				rowMiniMaskC(i * width);
				if ((rowMiniMaskC & ~blackpieces) == rowMiniMaskC)
					group.add(rowMiniMaskC);
				rowMiniMaskA(i * width + 1);
				if ((rowMiniMaskA & ~blackpieces) == rowMiniMaskA)
					group.add(rowMiniMaskA);
				rowMiniMaskB(i * width + 1);
				if ((rowMiniMaskB & ~blackpieces) == rowMiniMaskB)
					group.add(rowMiniMaskB);
				rowMiniMaskA(i * width + 2);
				if ((rowMiniMaskA & ~blackpieces) == rowMiniMaskA)
					group.add(rowMiniMaskA);
				rowLongs.add(group);
			} else if (rowGroups[i]) {
				group = new ArrayList<>();
				rowMiniMaskA(i * width);
				group.add(rowMiniMaskA);
				rowMiniMaskB(i * width);
				group.add(rowMiniMaskB);
				rowMiniMaskC(i * width);
				group.add(rowMiniMaskC);
				rowMiniMaskA(i * width + 1);
				group.add(rowMiniMaskA);
				rowMiniMaskB(i * width + 1);
				group.add(rowMiniMaskB);
				rowMiniMaskA(i * width + 2);
				group.add(rowMiniMaskA);
				rowLongs.add(group);
			}

		}
	}

	// ROW minimasks

	public void rowMiniMaskA(int shift) {
		long a;
		long highestBit = 1L << shift;
		a = highestBit << 1;
		rowMiniMaskA = a |= highestBit;
	}

	public void rowMiniMaskB(int shift) {
		long a;
		long highestBit = 1L << shift;
		a = highestBit << 2;
		rowMiniMaskB = a |= highestBit;
	}

	public void rowMiniMaskC(int shift) {
		long a;
		long highestBit = 1L << shift;
		a = highestBit << 3;
		rowMiniMaskC = a |= highestBit;
	}

	// SECOND diagonal minimasks

	public void secondDiagonalMiniMaskA(int shift) {
		long southWestA;
		long highestBit = 1L << shift + 3;
		southWestA = highestBit << (width - 1);
		secondDiagonalMiniMaskA = southWestA |= highestBit;
	}

	public void secondDiagonalMiniMaskB(int shift) {
		long southEastA;
		long highestBit = 1L << shift + 3;
		southEastA = highestBit << (2 * width - 2);
		secondDiagonalMiniMaskB = southEastA |= highestBit;
	}

	public void secondDiagonalMiniMaskC(int shift) {
		long southEastA;
		long highestBit = 1L << shift + 3;
		southEastA = highestBit << (3 * width - 3);
		secondDiagonalMiniMaskC = southEastA |= highestBit;
	}

	// FIRST diagonal minimasks

	public void firstDiagonalMiniMaskA(int shift) {
		long southEastA;
		long highestBit = 1L << shift;
		southEastA = highestBit << (width + 1);
		firstDiagonalMiniMaskA = southEastA |= highestBit;
	}


	public void firstDiagonalMiniMaskB(int shift) {
		long southEastA;
		long highestBit = 1L << shift;
		southEastA = highestBit << (2 * width + 2);
		firstDiagonalMiniMaskB = southEastA |= highestBit;
	}

	public void firstDiagonalMiniMaskC(int shift) {
		long southEastA;
		long highestBit = 1L << shift;
		southEastA = highestBit << (3 * width + 3);
		firstDiagonalMiniMaskC = southEastA |= highestBit;
	}

	public void secondDiagonalGroups() {
		for (int i = 0; i < height - 3; i++) {
			southWest(i * width);
			if ((secondDiagonalMask & ~board.getWhitepieces()) == secondDiagonalMask)
				secondDiagonalGroups[i] = true;

			secondDiagonalGroupsBlack[i] = (((secondDiagonalMask & ~board
					.getBlackpieces()) != secondDiagonalMask) && secondDiagonalGroups[i]);
		}
	}

	public void firstDiagonalGroups() {

		for (int i = 0; i < height - 3; i++) {
			southEast(i * width);
			if ((firstDiagonalMask & ~board.getWhitepieces()) == firstDiagonalMask)
				firstDiagonalGroups[i] = true;

			firstDiagonalGroupsBlack[i] = (((firstDiagonalMask & ~board
					.getBlackpieces()) != firstDiagonalMask) && firstDiagonalGroups[i]);

		}
	}

	public void rowGroups() {

		for (int i = 0; i < height; i++) {
			east(i * width);
			if ((rowMask & ~board.getWhitepieces()) == rowMask)
				rowGroups[i] = true;
			boolean overlap = ((rowMask & ~board.getBlackpieces()) == rowMask);
			rowGroupsBlack[i] = (!overlap && rowGroups[i]);
		}

	}

	public void columnGroups() {
		for (int i = 0; i < height - 3; i++) {
			for (int j = 0; j < width; j++) {
				south(i * width + j);
				if ((columnMask & ~board.getWhitepieces()) == columnMask)
					columnGroups[i * width + j] = true;
				boolean overlap = ((columnMask & ~board.getBlackpieces()) == columnMask);

				columnGroupsBlack[i * width + j] = (!overlap && columnGroups[i
						* width + j]);

			}
		}
	}



	public void southWest(int shift) {

		long southWestA, southWestB, southWestC;
		long highestBit = 1L << (width - 1);
		highestBit = highestBit << shift;
		southWestA = highestBit << (width - 1);
		southWestB = highestBit << 2 * (width - 1);
		southWestC = highestBit << 3 * (width - 1);

		secondDiagonalMask = southWestA |= southWestB |= southWestC |= highestBit;

	}

	public void east(int shift) {

		long eastA, eastB, eastC;
		long highestBit = 1L << shift;
		eastA = highestBit << 1;
		eastB = highestBit << 2;
		eastC = highestBit << 3;

		rowMask = eastA |= eastB |= eastC |= highestBit;

	}

	public void southEast(int shift) {

		long southEastA, southEastB, southEastC;
		long highestBit = 1L << shift;
		southEastA = highestBit << (width + 1);
		southEastB = highestBit << 2 * (width + 1);
		southEastC = highestBit << 3 * (width + 1);

		firstDiagonalMask = southEastA |= southEastB |= southEastC |= highestBit;

	}

	public void south(int shift) {

		long southA = 0, southB = 0, southC = 0;

		long highestBit = 1L << shift;
		southA = highestBit << width;
		southB = highestBit << 2 * width;
		southC = highestBit << 3 * width;

		columnMask = southA |= southB |= southC |= highestBit;

	}

	public void printer() {
		System.out.println("pairing printer");
		long stone;
		int n = 0;

		if (finalXOR != null) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					stone = board.getBite(board.getSquare(i, j));

					if ((stone & finalXOR.getLeft()) == stone)
						System.out.print("|  ");
					else if ((stone & finalXOR.getRight().getLeft()) == stone)
						System.out.print("-  ");
					else if ((stone & finalXOR.getRight().getRight()
							.getLeft()) == stone)
						System.out.print("\\  ");
					else if ((stone & finalXOR.getRight().getRight()
							.getRight()) == stone)
						System.out.print("/  ");
					else if ((stone & board.getBlackpieces()) == stone)
						System.out.print("B  ");
					else if ((stone & board.getWhitepieces()) == stone)
						System.out.print("W  ");

					else
						System.out.print(".  ");

				}
				System.out.println();
			}
		}
	}

}
