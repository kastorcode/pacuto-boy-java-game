package com.kastorcode.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.kastorcode.entities.Enemy;
import com.kastorcode.entities.Entity;
import com.kastorcode.entities.Food;
import com.kastorcode.main.Game;
import com.kastorcode.main.Window;


public class World {
	public static int WIDTH, HEIGHT;

	public static Tile[] tiles;


	// Normal constructor
	public World (String name) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource("/images/" + name));
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			int[] pixels = new int[WIDTH * HEIGHT];
			tiles = new Tile[WIDTH * HEIGHT];
			BufferedImage lastTile = Tile.TILE_FLOOR;
			map.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);

			for (int xx = 0; xx < WIDTH; xx++) {
				for (int yy = 0; yy < HEIGHT; yy++) {
					int pixel = pixels[xx + (yy * WIDTH)];
					//tiles[xx + (yy * WIDTH)] = new Tile(xx * 16, yy * 16, Tile.TILE_GRASS);

					switch (pixel) {
						// Floor
						case 0xff00ff00: {
							lastTile = Tile.TILE_FLOOR;
							tiles[xx + (yy * WIDTH)] = new Tile(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, lastTile);
							break;
						}

						// Wall
						case 0xffeca249: {
							tiles[xx + (yy * WIDTH)] = new WallTile(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, Tile.TILE_WALL);
							break;
						}

						// Food
						case 0xffffff00: {
							tiles[xx + (yy * WIDTH)] = new Tile(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, lastTile);
							Food food = new Food(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, 0, Food.SPRITE);
							Game.entities.add(food);
							Game.foodCount++;
							break;
						}

						// Enemy
						case 0xff000000: {
							tiles[xx + (yy * WIDTH)] = new Tile(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, lastTile);
							Enemy enemy = new Enemy(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, 1, Enemy.SPRITES[Entity.rand.nextInt(Enemy.SPRITES.length - 1) + 1]);
							Game.entities.add(enemy);
							break;
						}

						// Player
						case 0xffffffff: {
							tiles[xx + (yy * WIDTH)] = new Tile(xx * Tile.TILE_SIZE, yy * Tile.TILE_SIZE, lastTile);
							Game.player.setX(xx * Tile.TILE_SIZE);
							Game.player.setY(yy * Tile.TILE_SIZE);
							break;
						}
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	// Random constructor
	/* public World (String name) {
		Game.player.setX(0);
		Game.player.setY(0);
		WIDTH = 100;
		HEIGHT = 100;
		tiles = new Tile[WIDTH * HEIGHT];

		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				tiles[x + y * WIDTH] = new WallTile(x * 16, y * 16, Tile.TILE_WALL);
			}
		}

		int direction = 0, x = 0, y = 0;

		for (int i = 0; i < 200; i++) {
			tiles[x + y * WIDTH] = new FloorTile(x * 16, y * 16, Tile.TILE_FLOOR);

			switch (direction) {
				case 0: {
					// Right
					if (x < WIDTH) {
						x++;
					}
					break;
				}
				case 1: {
					// Left
					if (x > 0) {
						x--;
					}
					break;
				}
				case 2: {
					// Down
					if (y < HEIGHT) {
						y++;
					}
					break;
				}
				case 3: {
					// Up
					if (y > 0) {
						y--;
					}
					break;
				}
			}

			if (Game.rand.nextInt(100) < 30) {
				direction = Game.rand.nextInt(4);
			}
		}
	} */


	public static boolean isFree (int nextX, int nextY, int zPlayer) {
		int x1 = nextX / Tile.TILE_SIZE;
		int y1 = nextY / Tile.TILE_SIZE;
		
		int x2 = (nextX + Tile.TILE_SIZE - 1) / Tile.TILE_SIZE;
		int y2 = nextY / Tile.TILE_SIZE;
		
		int x3 = nextX / Tile.TILE_SIZE;
		int y3 = (nextY + Tile.TILE_SIZE - 1) / Tile.TILE_SIZE;
		
		int x4 = (nextX + Tile.TILE_SIZE - 1) / Tile.TILE_SIZE;
		int y4 = (nextY + Tile.TILE_SIZE - 1) / Tile.TILE_SIZE;
		
		if (!(
			tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
			tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
			tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
			tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile
		)) {
			return true;
		}
		
		if (zPlayer > 0) {
			return true;
		}
		
		return false;
	}


	public static boolean isFreeDynamic (int nextX, int nextY, int width, int height) {
		int x1 = nextX / Tile.TILE_SIZE;
		int y1 = nextY / Tile.TILE_SIZE;

		int x2 = (nextX + width - 1) / Tile.TILE_SIZE;
		int y2 = nextY / Tile.TILE_SIZE;

		int x3 = nextX / Tile.TILE_SIZE;
		int y3 = (nextY + height - 1) / Tile.TILE_SIZE;

		int x4 = (nextX + width - 1) / Tile.TILE_SIZE;
		int y4 = (nextY + height - 1) / Tile.TILE_SIZE;

		return !(
			tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
			tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
			tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
			tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile
		);
	}


	public void render (Graphics g) {
		int xStart = Camera.getX() >> 4;
		int yStart = Camera.getY() >> 4;
		int xFinal = xStart + (Window.WIDTH >> 4);
		int yFinal = yStart + (Window.HEIGHT >> 4);

		for (int xx = xStart; xx <= xFinal; xx++) {
			for (int yy = yStart; yy <= yFinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;

				try {
					Tile tile = tiles[xx + (yy * WIDTH)];
					tile.render(g);
				}
				catch (NullPointerException error) {}
			}
		}
	}
}