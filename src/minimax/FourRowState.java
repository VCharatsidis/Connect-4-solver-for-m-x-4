package minimax;



import BitMap.Bitboard;
import ai.game_abstractions.GameState;

public class FourRowState extends GameState {

	
	private Bitboard board ;
	public Bitboard getBoard(){
		return board;
	}
	
	public FourRowState (Bitboard board){
		this.board = board;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FourRowState  copy() {
		Bitboard copy = new Bitboard(board);
		return new FourRowState(copy);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
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
		FourRowState other = (FourRowState) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		return true;
	}
	
	
}
