package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;  

public class Walls {

	private static final String CSV_FILEPATH = "res/RPGWalls - Sheet1.csv";
	
	//public static List<List<Integer>> walls1 = new ArrayList();
	static String line = "";
	static String[] values;
	
	public static int[][] fwalls1 = new int[9][9];
	public static int[][] fwalls2 = new int[9][9];
	public static int[][] fwalls3 = new int[9][9];
	public static int[][] fwalls4 = new int[9][9];
	public static int[][] fwalls5 = new int[9][9];
	public static int[][][] arrays = new int[][][] {fwalls1, fwalls2, fwalls3, fwalls4, fwalls5};
	
	static
	{		
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
		
		System.out.println(Arrays.deepToString(fwalls1).replace("], ", "]\n")+"\n");
		System.out.println(Arrays.deepToString(fwalls2).replace("], ", "]\n")+"\n");
		System.out.println(Arrays.deepToString(fwalls3).replace("], ", "]\n")+"\n");
		System.out.println(Arrays.deepToString(fwalls4).replace("], ", "]\n")+"\n");
		System.out.println(Arrays.deepToString(fwalls5).replace("], ", "]\n")+"\n");
		
	}
}
