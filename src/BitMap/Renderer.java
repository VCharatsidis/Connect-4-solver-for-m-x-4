package BitMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import minimax.FourRowEvaluation;
import minimax.FourRowMove;
import minimax.FourRowMoveGenerator;
import minimax.FourRowNegaMaxPlayer;
import minimax.FourRowState;




//import com.sun.glass.events.KeyEvent;

/**
  * Provides I/O.
  * 
  *
   */
public class Renderer extends JPanel {

private static final long serialVersionUID = -494530433694385328L;

/**
 * Number of rows/columns.
 */

public static final int WIDTH=4;

/**
 * Number of tiles in row/column. (Size - 1)
 */

public static final int TILE_SIZE = 40;
public static final int BORDER_SIZE = 40;

/**
 * Black/white player/stone
 * 
 *
 */
public enum State {
    BLACK, WHITE
}

private static int height;

private boolean current_player;
private Bitboard board;
private Point lastMove;
private boolean [] checked;

	private FourRowState state;
	private FourRowEvaluation eval;
	private FourRowNegaMaxPlayer player;
	
	private List<int []> movesPlayed = new ArrayList<int[]>();


public Renderer(Bitboard board,int height) {
	this.height = height;
	this.board = board;
    this.setBackground(Color.ORANGE);
    
    	state = new FourRowState(board);
		eval = new FourRowEvaluation();
    	player = new FourRowNegaMaxPlayer(99999999, eval);
    	checked = new boolean[height*WIDTH];
    	Arrays.fill(checked, false);
    // Black always starts
    current_player = this.board.getTurn();
		setFocusable(true);
		addListener1();
		addListener();
	}

	private class Keyboard implements KeyListener {

		@Override
		public void keyTyped(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			
		        
			if(e.getKeyCode()==KeyEvent.VK_R){
					    int[] moveToRemove = (int[])movesPlayed.get(0);
					    movesPlayed.remove(0);
					    int[] restoreLastMove={-1,-1};
					    if(movesPlayed.size()>0)
					    	restoreLastMove = (int[])movesPlayed.get(0);
					    int[] coords =board.squareToRowCol(board.longToSquare(board.getLastMove()));
						board.removeStone(moveToRemove[0],moveToRemove[1]);
						lastMove = new Point(restoreLastMove[1],restoreLastMove[0]);
						current_player = board.getTurn();
						board.setNewBounds();
						board.setLastMove(restoreLastMove[1],restoreLastMove[0]);
						FourRowState state7 = new FourRowState(board);
						FourRowMove best = new FourRowMove(height-1,WIDTH-1);
						FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
						System.out.print("moves to consider : ");
						
						for(FourRowMove move : mg7.getLegalMoves(state7)){
							int colum = move.getCol();
							String column ="";
							if(colum==0)
								column="A";
							if(colum==1)
								column="B";
							if(colum==2)
								column="C";
							if(colum==3)
								column="D";
							System.out.print(" r,c " + (height-move.getRow())+column);
						}
						repaint();
					
		         
			}
			if (e.getKeyCode() == KeyEvent.VK_Y) {  
				    System.out.println("Threatd");
					Thread t = new Thread(new Runnable() {
						public void run(){
				
				         		if(board.getMoveNumber()%2==0){
				         			player.setDeepeningStep(1);
				         		}else{
				         			player.setDeepeningStep(1);
				         		}
				         		
				        	    FourRowMove bestmove = player.play(state).getRight();
				        	    
				        	
				        	    int col = bestmove.getCol();
								int row = bestmove.getRow();
								
								board.addStone(row, col);
								lastMove = new Point(col, row);
								int[] coords= {row,col};
								movesPlayed.add(0,coords);
								
								current_player = board.getTurn();
								FourRowState state7 = new FourRowState(board);
								FourRowMove best = new FourRowMove(height-1,WIDTH-1);
								FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
								System.out.print("moves to consider : ");
								
								for(FourRowMove move :mg7.getLegalMoves(state7)){
									int colum = move.getCol();
									String column ="";
									if(colum==0)
										column="A";
									if(colum==1)
										column="B";
									if(colum==2)
										column="C";
									if(colum==3)
										column="D";
									System.out.print(" r,c " + (height-move.getRow())+column);
									repaint();
								}
						}
						
					});
					t.start();
				
					repaint();

			}
			
			if(e.getKeyCode()==KeyEvent.VK_S){
				//player.getNegaMax().setStopSearch(true);
				
				board.boardPrinter();
				repaint();
			}
			         
				//System.out.println("col "+bestmove.getCol()+" row "+bestmove.getRow());
				
							
			if (e.getKeyCode() == KeyEvent.VK_P) {
				board.pass();
				current_player = board.getTurn();
				board.boardPrinter();
			}
			
			

		}

		@Override
		public void keyReleased(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub

	}

}
private class Mouse extends MouseAdapter{
	@Override
    
    public void mouseReleased(MouseEvent e) {
		System.out.println("mouse event ");
        // Converts to float for float division and then rounds to
        // provide nearest intersection.
//        int row =(int) Math.floor(e.getY());
//               
//        int col = (int) Math.floor(e.getX());
		
        int row = (int)Math.floor((float) ((e.getY() - BORDER_SIZE))/BORDER_SIZE);
                
        int col =(int) Math.floor((float) ((e.getX() - BORDER_SIZE))/BORDER_SIZE);
        String column="";
		if(col==0)
			column="A";
		if(col==1)
			column="B";
		if(col==2)
			column="C";
		if(col==3)
			column="D";       
        System.out.println("row "+(height-row));
		System.out.println("col "+column);
        // DEBUG INFO
        // System.out.println(String.format("y: %d, x: %d", row, col));

        // Check wherever it's valid
        if (row > height || col > WIDTH-1 || row <0 || col <0) {
            return;
        }
        
        //lastMove was after board.addStone
        	
        	board.addStone(row, col);
        	System.out.println("uperbound "+board.getUperBound())   ;
			lastMove = new Point(col, row);
			int[] coords= {row,col};
			movesPlayed.add(0,coords);
			System.out.println("movesplayed "+movesPlayed.size());
			current_player = board.getTurn();
			FourRowState state7 = new FourRowState(board);
			
			System.out.print("moves to consider : ");
			FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
			
			for(FourRowMove move :mg7.getLegalMoves(state7)){
				int colum = move.getCol();
				
				if(colum==0)
					column="A";
				if(colum==1)
					column="B";
				if(colum==2)
					column="C";
				if(colum==3)
					column="D";
				System.out.print(" c,r " +(height- move.getRow())+column);
			}
		
        // Switch current player
		//board.boardPrinter();
        repaint();
 
    }
}
public static void drawBoard(Bitboard board){
	JFrame f = new JFrame();
    
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JPanel container = new JPanel();
    container.setBackground(Color.GRAY);
    container.setLayout(new BorderLayout());
    f.add(container);

    Renderer drawboard = new Renderer(board,height);
    container.add(drawboard);

    f.pack();
    f.setResizable(false);
    f.setLocationByPlatform(true);
    f.setVisible(true);
		f.setFocusable(true);
	}

	private void addListener1() {
		addKeyListener(new Keyboard());
	}

	private void addListener() {
		addMouseListener(new Mouse());
	}
 

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(Color.BLACK);
    // Draw rows.
    for (int i = 0; i < height+1; i++) {
    	
    	g.drawString("A",585,(height+1)*42);
    	g.drawString("B",95,(height+1)*42);
    	g.drawString("C",135,(height+1)*42);
    	g.drawString("D",175,(height+1)*42);
    	for(int k=height-1;k>=0;k--){
    		String x = Integer.toString(k+1);
    		g.drawString(x, 20, k*(-40)+height*42);
    	}
        g2.drawLine(BORDER_SIZE, i * TILE_SIZE + BORDER_SIZE, TILE_SIZE
                * (WIDTH+1) , i * TILE_SIZE + BORDER_SIZE);
    }
    // Draw columns.
    for (int i = 0; i < WIDTH+1; i++) {
        g2.drawLine(i * TILE_SIZE + BORDER_SIZE, BORDER_SIZE, i * TILE_SIZE
                + BORDER_SIZE, TILE_SIZE *( height) + BORDER_SIZE);
    }
//    checked = new boolean[board.getWidth()
//           				* board.getHeight()];
//           		Arrays.fill(checked, false);
    // Iterate over intersections
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < WIDTH; col++) {
            
            if (board.occupied(row, col)!=1) {
                if (board.occupied(row,col) == 2) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }
              
               // board.boardPrinter();
                g2.fillOval(col * TILE_SIZE  + TILE_SIZE ,
                        row * TILE_SIZE  + TILE_SIZE ,
                        TILE_SIZE, TILE_SIZE);
            }
        }
    }
    
//	for(int i=0;i<board.getHeight();i++){
//		for(int j=0;j<board.getWidth();j++){
//			if(checked[i*board.getWidth()+j]){
//				g2.setColor(Color.ORANGE);
//			 	g2.fillOval(i * TILE_SIZE  + TILE_SIZE ,
//	                 j * TILE_SIZE  + TILE_SIZE ,
//	                 TILE_SIZE, TILE_SIZE);
//			}
//		}
//	}
    FourRowState state7 = new FourRowState(board);
	
	FourRowMoveGenerator mg7 = new FourRowMoveGenerator();
	
    int k=0;
	for(FourRowMove move : mg7.getLegalMoves(state7)){
		
		// checked[move.getRow()*board.getWidth()+move.getCol()]=true;
		 g2.setColor(Color.GRAY);
		 g2.fillOval(move.getCol() * TILE_SIZE  + TILE_SIZE ,
                 move.getRow() * TILE_SIZE  + TILE_SIZE ,
                 TILE_SIZE, TILE_SIZE);
		 g2.setColor(Color.WHITE);
		 String x = Integer.toString(k+1);
		 g.drawString(x, move.getCol() * TILE_SIZE  + (TILE_SIZE+20) , (move.getRow()+1) * TILE_SIZE  + 20 );
		 k++;
		 int colum = move.getCol();
		String column="";	
			if(colum==0)
				column="A";
			if(colum==1)
				column="B";
			if(colum==2)
				column="C";
			if(colum==3)
				column="D";
		//System.out.print(" c,r " + (board.getHeight()-move.getRow())+column);
		  
	}
    // Highlight last move
    if (lastMove != null) {
        g2.setColor(Color.RED);
        g2.drawOval(lastMove.x * TILE_SIZE  + TILE_SIZE ,
                lastMove.y * TILE_SIZE + TILE_SIZE ,
                TILE_SIZE, TILE_SIZE);
    }
}
  
@Override
public Dimension getPreferredSize() {
    return new Dimension((height) * TILE_SIZE + BORDER_SIZE * 2,
            (height) * TILE_SIZE + BORDER_SIZE * 2);
}

}