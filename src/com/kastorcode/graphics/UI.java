package com.kastorcode.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.kastorcode.main.Game;


public class UI {
	public void render (Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 10));
		g.drawString(Game.foodCurrent + "/" + Game.foodCount, 2, 10);
	}
}