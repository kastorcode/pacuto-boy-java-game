package com.kastorcode.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.kastorcode.graphics.Spritesheet;
import com.kastorcode.main.Game;
import com.kastorcode.main.NewerSound;
import com.kastorcode.world.Camera;
import com.kastorcode.world.Tile;
import com.kastorcode.world.World;


public class Player extends Entity {
	public static final NewerSound
		DEATH_SOUND = new NewerSound("/effects/death.wav"),
		FART_SOUND = new NewerSound("/effects/fart.wav");

	public static BufferedImage leftSprite;

	public boolean right, left, up, down;

	public int z = 0, lastDirection = 1, life = 2;


	public Player(int x, int y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);

		leftSprite = Spritesheet.getSprite(48, 0, Tile.TILE_SIZE, Tile.TILE_SIZE);
	}


	public void isCollidingWithFood () {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity current = Game.entities.get(i);

			if (current instanceof Food &&
				Entity.isColliding(this, current)
			) {
				Game.entities.remove(i);
				Game.foodCurrent++;

				if (Game.foodCurrent % 10 == 0) {
					FART_SOUND.play();
				}

				return;
			}
		}
	}


	public void tick () {
		if (life < 1) {
			DEATH_SOUND.play();
			Game.foodCurrent = 0;
			Game.foodCount = 0;
			Game.over("level1.png");
			return;
		}

		depth = 1;

		if (right && World.isFree((int)(x + speed), getY(), z)) {
			setX(x += speed);
			lastDirection = 1;
		}
		else if (left && World.isFree((int)(x - speed), getY(), z)) {
			setX(x -= speed);
			lastDirection = -1;
		}

		if (up && World.isFree(getX(), (int)(y - speed), z)) {
			setY(y -= speed);
		}
		else if (down && World.isFree(getX(), (int)(y + speed), z)) {
			setY(y += speed);
		}

		updateCamera();
		isCollidingWithFood();
	}


	public void render (Graphics g) {
		if (lastDirection == 1) {
			super.render(g);
		}
		else {
			g.drawImage(leftSprite, getX() - Camera.getX(), getY() - Camera.getY(), null);
		}
	}
}