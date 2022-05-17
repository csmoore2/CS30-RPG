package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;  

public class Walls {

	private final String CSV_FILEPATH = "res/RPGWalls - Sheet1.csv";
	
	//public static List<List<Integer>> walls1 = new ArrayList();
	String line = "";
	String[] values;
	
	public static int[][] fwalls1 = new int[9][9];
	public static int[][] fwalls2 = new int[9][9];
	public static int[][] fwalls3 = new int[9][9];
	public static int[][] fwalls4 = new int[9][9];
	public static int[][] fwalls5 = new int[9][9];
	
	public Walls() throws FileNotFoundException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(CSV_FILEPATH));
		
		while(((line = br.readLine()) != null) & (line.charAt(0) != 2)) {
			values = line.split(",");
			int a = Integer.parseInt(values[1]);
			int b =	Integer.parseInt(values[2]);
			fwalls1[a][b] = 1;
			}
		while(((line = br.readLine()) != null) & (line.charAt(0) != 3)) {
			values = line.split(",");
			int a = Integer.parseInt(values[1]);
			int b =	Integer.parseInt(values[2]);
			fwalls2[a][b] = 1;
			}
		while(((line = br.readLine()) != null) & (line.charAt(0) != 4)) {
			values = line.split(",");
			int a = Integer.parseInt(values[1]);
			int b =	Integer.parseInt(values[2]);
			fwalls3[a][b] = 1;
			}
		while(((line = br.readLine()) != null) & (line.charAt(0) != 5)) {
			values = line.split(",");
			int a = Integer.parseInt(values[1]);
			int b =	Integer.parseInt(values[2]);
			fwalls4[a][b] = 1;
			}
		while(((line = br.readLine()) != null) & (line.charAt(0) != 6)) {
			values = line.split(",");
			int a = Integer.parseInt(values[1]);
			int b =	Integer.parseInt(values[2]);
			fwalls5[a][b] = 1;
			}
		
		br.close();
	}
	
	public void printThis()
		{System.out.println(Arrays.deepToString(fwalls1).replace("], ", "]\n"));}
	
}
