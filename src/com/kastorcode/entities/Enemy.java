package com.kastorcode.entities;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.kastorcode.graphics.Spritesheet;
import com.kastorcode.main.Game;
import com.kastorcode.world.AStar;
import com.kastorcode.world.Camera;
import com.kastorcode.world.Tile;
import com.kastorcode.world.Vector2i;


public class Enemy extends Entity {
	public static BufferedImage
		SPRITE1 = Spritesheet.getSprite(Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		SPRITE2 = Spritesheet.getSprite(Tile.TILE_SIZE * 2, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		SPRITE_GHOST = Spritesheet.getSprite(Tile.TILE_SIZE * 3, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);

	public boolean ghostMode = false;

	public int ghostFrames = 0, nextTime = rand.nextInt(60 * 5 - 60 * 3) + 60 * 3;


	public Enemy(double x, double y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);
	}


	public void tick () {
		depth = 0;

		if (ghostMode == false) {
			if (path == null || path.size() == 0) {
				Vector2i start = new Vector2i(((int)(x / Tile.TILE_SIZE)), ((int)(y / Tile.TILE_SIZE)));
				Vector2i end = new Vector2i(((int)(Game.player.x / Tile.TILE_SIZE)), ((int)(Game.player.y / Tile.TILE_SIZE)));
				path = AStar.findPath(Game.world, start, end);
			}
	
			if (new Random().nextInt(100) < 80) {
				followPath(path, speed);
			}
	
			if (x % Tile.TILE_SIZE == 0 &&
				y % Tile.TILE_SIZE == 0 &&
				new Random().nextInt(100) < 10
			) {
				Vector2i start = new Vector2i(((int)(x / Tile.TILE_SIZE)), ((int)(y / Tile.TILE_SIZE)));
				Vector2i end = new Vector2i(((int)(Game.player.x / Tile.TILE_SIZE)), ((int)(Game.player.y / Tile.TILE_SIZE)));
				path = AStar.findPath(Game.world, start, end);
			}
		}

		ghostFrames++;

		if (ghostFrames == nextTime) {
			ghostFrames = 0;
			nextTime = rand.nextInt(60 * 5 - 60 * 3) + 60 * 3;

			if (ghostMode == false) {
				ghostMode = true;
			}
			else {
				ghostMode = false;
			}
		}
	}


	public void destroySelf () {
		Game.entities.remove(this);
		return;
	}


	public boolean isCollidingWithPlayer () {
		Rectangle currentEnemy = new Rectangle(getX(), getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);

		if (currentEnemy.intersects(player) &&
			this.z == Game.player.z
		) {
			return true;
		}

		return false;
	}


	public void render (Graphics g) {
		if (ghostMode) {
			g.drawImage(SPRITE_GHOST, getX() - Camera.getX(), getY() - Camera.getY(), null);
		}
		else {
			super.render(g);
		}
	}
}