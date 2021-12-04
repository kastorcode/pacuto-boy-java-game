package com.kastorcode.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kastorcode.entities.Entity;
import com.kastorcode.entities.Player;
import com.kastorcode.graphics.Spritesheet;
import com.kastorcode.graphics.UI;
import com.kastorcode.world.World;


public class Game extends Window implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private Thread thread;

	private boolean isRunning;

	private BufferedImage image;

	public static List<Entity> entities;

	public static Spritesheet spritesheet;

	public static World world;

	public static Player player = null;

	public UI ui;

	public static String state = "MENU";

	public static String[] bgSounds = new String[4];

	public static NewerSound bgSound;

	public boolean saveGame = false;

	public int[] pixels;

	public static int foodCurrent = 0, foodCount = 0;

	public int sceneTime = 0, sceneMaxTime = 60 * 3;


	public Game () {
		super();

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		spritesheet = new Spritesheet("spritesheet.png");
		entities = new ArrayList<Entity>();
		player = new Player(0, 0, 16, 16, 2, Spritesheet.getSprite(32, 0, 16, 16));
		world = new World("level1.png");
		ui = new UI();
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities.add(player);
	}


	public synchronized void start () {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}


	public synchronized void stop () {
		isRunning = false;

		try {
			thread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public static void main (String args[]) {
		Game game = new Game();
		game.start();
	}


	public void tick () {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.tick();
		}
	}


	public static void over (String level) {
		if (bgSound != null) {
			bgSound.stop();
		}

		entities = new ArrayList<Entity>();
		spritesheet = new Spritesheet("spritesheet.png");
		player = new Player(0, 0, 16, 16, 2, Spritesheet.getSprite(32, 0, 16, 16));

		entities.add(player);

		world = new World("/" + level);
		Game.player.updateCamera();
		return;
	}


	public void render () {
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();

		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.render(g);
		}

		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();

		if (fullScreen) {
			g.drawImage(image, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null);
		}
		else {
			g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		}

		bs.show();
	}


	public void run () {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop();
	}


	@Override
	public void keyPressed (KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D: {
				player.right = true;
				break;
			}

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A: {
				player.left = true;
				break;
			}

			case KeyEvent.VK_F:
			case KeyEvent.VK_F11: {
				toggleFullScreen();
				break;
			}
		}

		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W: {
				player.up = true;
				break;
			}
			
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S: {
				player.down = true;
				break;
			}
		}
	}


	@Override
	public void keyReleased (KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D: {
				player.right = false;
				break;
			}

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A: {
				player.left = false;
				break;
			}
		}
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W: {
				player.up = false;
				break;
			}
			
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S: {
				player.down = false;
				break;
			}
		}
	}


	@Override
	public void keyTyped (KeyEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseClicked (MouseEvent arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseEntered (MouseEvent arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseExited (MouseEvent arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mousePressed (MouseEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseReleased (MouseEvent arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}