package main;

import java.io.IOException;

import display.Display;
import encryption.Encryptor;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class Application implements Runnable
{
	private Encryptor encryptor;
	private Display display;
	private int width, height;
	public String title;

	private boolean running = false;
	private Thread thread;

	public Application(String title, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.title = title;
	}

	/**
	 * initializes encryptor and display
	 */
	private void init()
	{
		//initializing the display
		this.encryptor = new Encryptor();
		this.display = new Display(title, width, height);
	}

	/**
	 * ticks the program
	 */
	private void tick() throws IOException
	{

	}

	/**
	 * Renders the program
	 */
	private void render()
	{
		//Draw Here!
		width = display.getFrame().getWidth();
		height = display.getFrame().getHeight();
		//End Drawing!
		//bs.show();
		//g.dispose();
	}

	/**
	 * Runs the program, Main loop.
	 */
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

	/**
	 * @return width of application
	 */
	public int getWidth()
	{
		return this.width;
	}

	/**
	 * @return height of application
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Starts the thread and run() method
	 */
	public synchronized void start()
	{
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Stops the thread on close.
	 */
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