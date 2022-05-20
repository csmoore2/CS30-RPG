package game;

import java.util.EnumMap;
import java.util.Map;

import game.ui.screens.AreaScreen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;  

public class Walls {
	/**
	 * This is the path to the CSV file that contains all of the walls for each map.
	 */
	private static final String CSV_FILEPATH = "res/RPGWalls - Sheet1.csv";

	/**
	 * This maps each zone to its corresponding array of booleans that represent its
	 * walls. If the value at a given position is true then it is a wall and the player
	 * cannot move on to it.
	 */
	private static final Map<Zone, boolean[][]> zoneToWallMap = new EnumMap<>(Zone.class);

	/**
	 * This method initializes the walls for each zone. It does so by
	 * loading the CSV file containing all the information on walls and
	 * populating each zone's array of walls.
	 */
	public static void initializeWalls() {
		// Try to load the CSV file containing all of the walls
		try {
			Files.lines(Path.of(CSV_FILEPATH)).forEach((line) -> {
				// Split the line into its three parts:
				//     1. the index of the zone
				//     2. the row of the wall
				//     3. the column of the wall
				String[] values = line.split(",");
				int zoneIndex = Integer.parseInt(values[0]) - 1;
				int col = Integer.parseInt(values[1]);
				int row = Integer.parseInt(values[2]);
				
				// Add the wall to the zone's map of walls
				boolean[][] zoneWallMap = zoneToWallMap.computeIfAbsent(
					Zone.values()[zoneIndex],
					(zne) -> new boolean[AreaScreen.ROWS_OF_TILES][AreaScreen.TILES_PER_ROW]
				);
				zoneWallMap[row][col] = true;
			});
		} catch (IOException e) {
			throw new RuntimeException("Unable to load walls!", e);
		}
	}

	/**
	 * This method returns whether or not there is a wall at the given position
	 * in the given zone.
	 * 
	 * @param zone the zone to check
	 * @param row  the row of the tile to check
	 * @param col  the columns of the tile to check
	 * 
	 * @return whether or not there is a wall at the given position in the given zone
	 */
	public static boolean getWallAtPosition(Zone zone, int row, int col) {
		return zoneToWallMap.get(zone)[row][col];
	}
}
