package main;

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
	 * Runs the program, Main loop.
	 */
	public void run()
	{
		init();
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