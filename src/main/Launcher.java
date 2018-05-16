package main;

import javax.swing.*;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class Launcher
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		Application app = new Application("Encryptor", 640, 480);
		app.start();
	}
}
