package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;


 public class SudokuFrame extends JFrame {
	JButton checkButton;
	JCheckBox checkBox;
	JTextArea westArea;
	JTextArea eastArea;
	public SudokuFrame() {
		super("Sudoku Solver");
		
		initialize();
		
		// Could do this:
		// setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void initialize() {
		setLayout(new BorderLayout(4, 4));
		initAreas();
	
		//Button
		checkButton = new JButton("Check");
		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				solve();
			}
		});
		
		//Check Box
		checkBox = new JCheckBox("Auto Check");
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 
				
			}
		});
		
		//Panel
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(checkButton, BorderLayout.LINE_START);
		southPanel.add(checkBox, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}
	private void initAreas() {
		//Areas
		westArea = new JTextArea(15, 20);
		eastArea = new JTextArea(15, 20);
	
		westArea.setBorder(new TitledBorder("Puzzle"));
		eastArea.setBorder(new TitledBorder("Solution"));
		
		add(westArea, BorderLayout.WEST);
		add(eastArea, BorderLayout.EAST);
	}
	
	private void solve() {
		try {
			Sudoku s = new Sudoku(Sudoku.textToGrid(westArea.getText()));
			int numOfSolutions = s.solve();
			if(numOfSolutions > 0) {
				long time = s.getElapsed();
				String solutionGrid = s.getSolutionText();
				eastArea.setText(solutionGrid);
				eastArea.append("solutions:" + numOfSolutions + "\n");
				eastArea.append("elapsed:" + time + "\n");
			} else {
				eastArea.setText("No solutions");
			}
		} catch (RuntimeException e){
			eastArea.setText("Parsing Problem");
		}
	}

	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
		
	}

	

}
