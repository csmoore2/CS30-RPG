package game;

import java.util.Arrays;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;  

public class Walls {

		/**
		 * The filepath
		 */
		private static final String CSV_FILEPATH = "res/RPGWalls - Sheet1.csv";
		
		/**
		 * Variable initializing the array of values which are assigned in the following code
		 */
		static String[] values;
		
		/**
		 * Set of 2d arrays consisting of the locations where a wall exists
		 */
		public static int[][] fwalls1 = new int[9][9];
		public static int[][] fwalls2 = new int[9][9];
		public static int[][] fwalls3 = new int[9][9];
		public static int[][] fwalls4 = new int[9][9];
		public static int[][] fwalls5 = new int[9][9];
		
		/**
		 * Array of 2d arrays
		 */
		public static int[][][] arrays = new int[][][] {fwalls1, fwalls2, fwalls3, fwalls4, fwalls5};
		
		static
		{		
			// Load data from the csv file directly into array format
			try {
				Files.lines(Path.of(CSV_FILEPATH)).forEach((line) -> {
					int arrayIndex;
					values = line.split(",");
					arrayIndex = Integer.parseInt(values[0])-1;
					int a = Integer.parseInt(values[1]);
					int b = Integer.parseInt(values[2]);
					arrays[arrayIndex][a][b] = 1;
				});
			} catch (FileNotFoundException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
			// Initialize the locations of bosses to be walls (so they cannot be walked into until
			// the lesser enemies have been defeated.
			arrays[1][4][1] = 1;
			arrays[2][7][4] = 1;
			arrays[3][4][7] = 1;
			arrays[4][1][4] = 1;
			
		}
}
