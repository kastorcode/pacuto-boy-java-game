package com.kastorcode.entities;

import java.awt.image.BufferedImage;

import com.kastorcode.graphics.Spritesheet;
import com.kastorcode.world.Tile;


public class Food extends Entity {
	public static final BufferedImage SPRITE = Spritesheet.getSprite(0, 16, Tile.TILE_SIZE, Tile.TILE_SIZE);


	public Food(double x, double y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);
		depth = 0;
	}
}