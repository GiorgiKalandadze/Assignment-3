package assign3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	private class Spot{
		private int row;
		private int col;
		private int number;
		private int numOfValues;
		
		public Spot(int n) {
			this.number = n;
		}
		public Spot(int r, int c, int n, int v) {
			this.row = r;
			this.col = c;
			this.number = n;
			this.numOfValues = v;
		}
		
		public void setRow(int r) {
			this.row = r;
		}
		public void setColumn(int c) {
			this.col = c;
		}
		public void setNumber(int n) {
			this.number = n;
		}
		public void setNumOfValues(int v) {
			this.numOfValues = v;
		}
		
		public int getRow() {
			return row;
		}
		public int getColumn() {
			return col;
		}
		public int getNumber() {
			return number;
		}
		public int getNumOfValues() {
			return numOfValues;
		}
		
	}
	
	Spot[][] table; //Keeps board's current condition
	Spot[][] tableStatic; //Keeps board's initial condition
	int[][] answerTable;	//Keeps board's last condition of the first answer
	ArrayList<Spot> spotList; //Keeps the spots which are empty
	private int currAnswer = 0; //Variable to save number of solutions
	private long workTime;  //Time spent on the solve() method. (On recursion)
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	public static final int MAX_VALUES = 9; //Maximum quantity of numbers in each spot
	public static final int EMPTY_VALUE = 0;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}

		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
			
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		table = new Spot[SIZE][SIZE];
		tableStatic = new Spot[SIZE][SIZE];
		answerTable = new int[SIZE][SIZE];
		spotList = new ArrayList<Sudoku.Spot>();
		
		copyNumbers(ints);
		makeTable(ints);
		sortSpots();
	}
	
	/**
	 * Sets up based on the given text.
	 */
	public Sudoku(String text) {
		int [][] ints = textToGrid(text);
		copyNumbers(ints);
		makeTable(ints);
		sortSpots();
	}
	/*
	 * This method copys numbers from 2D "ints" array to 2D spot's "table" array
	 */
	private void copyNumbers(int [][] ints) {
		for(int i = 0; i < ints[0].length; i++) {
			for(int j = 0; j < ints.length; j++) {
				int currentNumber = ints[i][j];
				Spot newSpot = new Spot(currentNumber);
				table[i][j] = newSpot;
			}		
		}
	}
	/*
	 * This method copys sudoku table from given board to out table
	 */
	private void makeTable(int [][] ints) {
		for(int i = 0; i < ints[0].length; i++) {
			for(int j = 0; j < ints.length; j++) {
				int currentNumber = ints[i][j];
				
				Set<Integer> st = new HashSet<Integer>();
				
				if(currentNumber == 0) {
					st = searchLegalValues(i, j);
				} 
				
				Spot newSpot = new Spot(i, j, currentNumber, st.size());
				Spot tmpSpot = new Spot(i, j, currentNumber, st.size());
				
				if(currentNumber == 0) {
					spotList.add(newSpot);
				}
			
				table[i][j] = newSpot;
				tableStatic[i][j] = tmpSpot; 
			}
		}
		sortSpots();
	}

	
	//
	private Set<Integer> searchLegalValues(int r, int c) {
		
		Set<Integer> s = new HashSet<Integer>();
		int currentNumber;
		
		//Check horizontal
		for(int col = 0; col < SIZE; col++) {
			currentNumber = table[r][col].getNumber();
			s.add(currentNumber);
		}
		
		//Vertical
		for(int row = 0; row < SIZE; row++) {
			currentNumber = table[row][c].getNumber();
			s.add(currentNumber);
		}
		
		//3X3
		for(int row = PART * (r / PART); row < PART * (r / PART) + PART; row++) {
			for(int col = PART * (c / PART); col < PART * (c / PART) + PART; col++) {

				currentNumber = table[row][col].getNumber();
				s.add(currentNumber);
			}
		}
		s.remove(EMPTY_VALUE);
		
		//Return set that contains only legal values
		Set<Integer> result = fullSet();
		for(Integer e : s) {
			result.remove(e);
		}
		
		return result;
	}
	
	
	/*
	 * This method compares two spots according to their number of values(legal numbers)
	 * If they eqaul then it checks coordinates
	 */	
	private void sortSpots() {
		Collections.sort(spotList, new Comparator<Spot>(){
			@Override
			public int compare(Spot o1, Spot o2) {
				if(o1.numOfValues < o2.numOfValues) return -1;
				if(o1.numOfValues > o2.numOfValues) return 1;
				return 0;
			}
		});
	}
	
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		long startTime = System.currentTimeMillis();
		int startIndex = 0;
		rec(startIndex);
		long endTime = System.currentTimeMillis();
		workTime = endTime - startTime;
		return currAnswer; // YOUR CODE HERE
	}

	/*
	 * Helper method for solving
	 * Ends when it has checked all spots
	 */
	private void rec(int index) {
		//Base Case
		if(index >= spotList.size()) {
			if(currAnswer == 0) {
				copyAnswer();
			}
			currAnswer++;
			return;
		}
		//Edge Case
		if(currAnswer > MAX_SOLUTIONS) return;
		
		Spot currSpot = spotList.get(index); //Get current Spot from the list

		//Set of all numbers that can be written in this spot, int other words, all legal numbers
		Set<Integer> currSet = searchLegalValues(currSpot.getRow(), currSpot.getColumn()); 
		//Iterate over set and try all possible values
		for(Integer num : currSet) {
			currSpot.setNumber(num); //Set 
			rec(index + 1); //Call recursion function
			currSpot.setNumber(EMPTY_VALUE); //Back to previous value
		}
		
		return;
	}
	
	/*
	 * Copy numbers from 2D spot array "table" to 2D int array "answer table"
	 */
	private void copyAnswer() {
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				answerTable[i][j] = table[i][j].getNumber();
			}
		}
	}
	
	
	/* 
	 * This method returns set filled with number from 1 to 9
	 */
	private Set<Integer> fullSet(){
		Set<Integer> s = new HashSet<Integer>();
		for(int i = 1; i <= 9; i++) {
			s.add(i);
		}
		return s;
	}
	
	public String getSolutionText() {
		String answer = "";
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				answer += answerTable[i][j] + " "; 
			} 
			answer += "\n";
		}
		return answer; 
	}
	
	public long getElapsed() {
		return workTime;
	}
	
	@Override
	public String toString() {
		String answer = "";
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				answer += table[i][j].number + " "; 
			}
			answer += "\n";
		}
		return answer;
	}
	

	

}
