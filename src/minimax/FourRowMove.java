package minimax;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ai.game_abstractions.GameMove;

@Getter
@AllArgsConstructor
public class FourRowMove extends GameMove<FourRowState> {
	private int row;
	private int col;
	@Override
	public String toString() {
		return "FourRowMove [row=" + row + ", col=" + col + "]";
	}

	
}
