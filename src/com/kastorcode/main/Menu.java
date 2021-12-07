package com.kastorcode.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Menu {
	public static void saveGame (String[] keys, int[] values, int encode) {
		BufferedWriter write = null;

		try {
			write = new BufferedWriter(
				new FileWriter("save.txt")
			);
		}
		catch (IOException error) {}

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			key += " ";
			char[] value = Integer.toString(values[i]).toCharArray();
			
			for (int j = 0; j < value.length; j++) {
				value[j] += encode;
				key += value[j];
			}

			try {
				write.write(key);
				
				if (i < keys.length - 1) {
					write.newLine();
				}
			}
			catch (IOException error) {}
		}
		
		try {
			write.flush();
			write.close();
		}
		catch (IOException error) {}
	}


	public static void applySave (String save) {
		String[] keys = save.split("/");
		Game.over("level1.png");

		for (int i = 0; i < keys.length; i++) {
			String[] key_value = keys[i].split(" ");

			switch (key_value[0]) {
				case "foodCount": {
					Game.foodCount = Integer.parseInt(key_value[1]);
					break;
				}

				case "foodCurrent": {
					Game.foodCurrent = Integer.parseInt(key_value[1]);
					break;
				}

				case "life": {
					Game.player.life = Integer.parseInt(key_value[1]);
					break;
				}
			}
		}

		Game.pause = false;
	}


	public static String loadGame (int encode) {
		String line = "";
		File file = new File("save.txt");

		if (file.exists()) {
			try {
				String singleLine = null;
				
				BufferedReader reader = new BufferedReader(
					new FileReader("save.txt")
				);
				
				try {
					while ((singleLine = reader.readLine()) != null) {
						String[] key = singleLine.split(" ");
						char[] value = key[1].toCharArray();
						key[1] = "";

						for (int i = 0; i < value.length; i++) {
							value[i] -= encode;
							key[1] += value[i];
						}
						
						line += key[0];
						line += " ";
						line += key[1];
						line += "/";
					}
				}
				catch (IOException error) {}
			}
			catch (FileNotFoundException error) {}
		}

		return line;
	}
}