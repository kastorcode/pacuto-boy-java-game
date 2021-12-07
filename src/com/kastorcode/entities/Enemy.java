package com.kastorcode.entities;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.kastorcode.graphics.Spritesheet;
import com.kastorcode.main.Game;
import com.kastorcode.main.NewerSound;
import com.kastorcode.world.AStar;
import com.kastorcode.world.Camera;
import com.kastorcode.world.Tile;
import com.kastorcode.world.Vector2i;


public class Enemy extends Entity {
	public static final NewerSound DAMAGE_SOUND = new NewerSound("/effects/damage.wav");

	public static BufferedImage[] SPRITES = {
		Spritesheet.getSprite(Tile.TILE_SIZE * 5, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		Spritesheet.getSprite(Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		Spritesheet.getSprite(Tile.TILE_SIZE * 2, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		Spritesheet.getSprite(Tile.TILE_SIZE * 3, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE),
		Spritesheet.getSprite(Tile.TILE_SIZE * 4, Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE)
	};

	public boolean
		ghostMode = false,
		findingPath = false;

	public int
		ghostFrames = 0,
		nextTime = rand.nextInt(60 * 6 - 60 * 2) + 60 * 2;


	public Enemy(double x, double y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);
	}


	public void tick () {
		depth = 0;

		if (ghostMode == false) {
			if (isCollidingWithPlayer()) {
				destroySelf();
				Game.player.life--;

				if (Game.player.life > 0) {
					DAMAGE_SOUND.play();
				}
			}
			else {
				if (calculateDistance(getX(), getY(), Game.player.getX(), Game.player.getY())
					< 600 &&
					Game.findPathQueue.size() < 10 &&
					!findingPath
				) {
					findingPath = true;
					Game.findPathQueue.add(new Runnable() {
						public void run() {
							try {
								Vector2i start = new Vector2i(((int)(x / Tile.TILE_SIZE)), ((int)(y / Tile.TILE_SIZE)));
								Vector2i end = new Vector2i(((int)(Game.player.x / Tile.TILE_SIZE)), ((int)(Game.player.y / Tile.TILE_SIZE)));
								path = AStar.findPath(Game.world, start, end);
								findingPath = false;
								Game.findingPath = false;
							}
							catch (NullPointerException error) {
								findingPath = false;
							}
						}
					});
				}

				followPath(path, speed);
			}
		}

		ghostFrames++;

		if (ghostFrames == nextTime) {
			ghostFrames = 0;

			if (ghostMode == false) {
				ghostMode = true;
				nextTime = rand.nextInt(60 * 3 - 60) + 60;
			}
			else {
				ghostMode = false;
				nextTime = rand.nextInt(60 * 9 - 60 * 3) + 60 * 3;
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
			g.drawImage(SPRITES[0], getX() - Camera.getX(), getY() - Camera.getY(), null);
		}
		else {
			super.render(g);
		}
	}
}