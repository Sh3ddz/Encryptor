package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import display.Display;
import display.MenuBar;
import encryption.Encryptor;

public class Application implements Runnable
{
	private Encryptor encryptor;
	private Display display;
	private int width, height;
	public String title;

	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;

	//config
	private int TPS = 0;
	private int FPS = 0;
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 0;
	public boolean debugMode = false;

	public Application(String title, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.title = title;
	}

	private void init()
	{
		//initializing the display
		this.encryptor = new Encryptor();
		this.display = new Display(title, width, height);
	}

	private void tick() throws IOException
	{

	}

	private void render()
	{
		//bs = display.getCanvas().getBufferStrategy();
		//if(bs == null)
		//{
		//	display.getCanvas().createBufferStrategy(3); //original is 3
		//	return;
		//}
		//g = bs.getDrawGraphics();
		//Clear Screen
		//g.clearRect(0, 0, width, height);
		//Draw Here!
		width = display.getFrame().getWidth();
		height = display.getFrame().getHeight();

		//End Drawing!
		//bs.show();
		//g.dispose();
	}

	public void run()
	{
		init();

		while(running)
		{
			try
			{
				tick();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			render();
		}

		stop();

	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public synchronized void start()
	{
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop()
	{
		if(!running)
			return;
		running = false;
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}