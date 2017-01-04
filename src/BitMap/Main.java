package BitMap;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Builds UI and starts the game.
 *
 */
public class Main {

public static final String TITLE = "4-in-a-row";
public static final int BORDER_SIZE = 20;

public static void main(String[] args) {
    new Main().init();
}

private void init() {
	
	JOptionPane pane = new JOptionPane();
    JFrame f = new JFrame(); 
    f.setTitle(TITLE);
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JPanel container = new JPanel();
    container.setBackground(Color.GRAY);
    container.setLayout(new BorderLayout());
    f.add(container);
    container.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
    int height;
    height =  Integer.parseInt( pane.showInputDialog(f,"Please type the height of the board ,Not more than 16"));
   
    Bitboard startingboard = new Bitboard(height);
    Renderer board = new Renderer(startingboard,height);
    container.add(board);

    f.pack();
    f.setResizable(false);
    f.setLocationByPlatform(true);
    f.setVisible(true);
}}
