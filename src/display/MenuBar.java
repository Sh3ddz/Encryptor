package display;

import encryption.Encryptor;
import utils.FileSelectionUtils;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class MenuBar extends JFrame implements MenuListener, ActionListener, KeyListener
{
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, optionsMenu, helpMenu;
	private JMenuItem openFiles, printSelected, removeRepeat, clearFiles, exit;
	private JMenuItem encrypt, decrypt;
	private JMenuItem howToUse, tutorialLink;
	private JCheckBoxMenuItem safeEncrypt, outputLog;

	private ImageIcon openFile = new ImageIcon(getClass().getResource("/res/openfile.png"));
	private ImageIcon print = new ImageIcon(getClass().getResource("/res/print.png"));
	private ImageIcon remove = new ImageIcon(getClass().getResource("/res/removerepeat.png"));
	private ImageIcon clear = new ImageIcon(getClass().getResource("/res/clear.png"));

	protected ImageIcon encryptImage = new ImageIcon(getClass().getResource("/res/encrypt.png"));
	protected ImageIcon decryptImage = new ImageIcon(getClass().getResource("/res/decrypt.png"));

	private ImageIcon howTo = new ImageIcon(getClass().getResource("/res/howto.png"));
	private ImageIcon tutorial = new ImageIcon(getClass().getResource("/res/tutorial.png"));


	public MenuBar()
	{
		init();
	}

	/**
	 * initializes the menu bar and components of it
	 */
	private void init()
	{
		this.addKeyListener(this);
		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		fileMenu.addMenuListener(this);
		menuBar.add(fileMenu);

		editMenu = new JMenu("Edit");
		editMenu.addMenuListener(this);
		menuBar.add(editMenu);

		optionsMenu = new JMenu("Options");
		optionsMenu.addMenuListener(this);
		menuBar.add(optionsMenu);

		helpMenu = new JMenu("Help");
		helpMenu.addMenuListener(this);
		menuBar.add(helpMenu);

		openFiles = new JMenuItem("Select Files", openFile);
		openFiles.addActionListener(this);
		fileMenu.add(openFiles);

		printSelected = new JMenuItem("Print Selected Files", print);
		printSelected.addActionListener(this);
		fileMenu.add(printSelected);

		removeRepeat = new JMenuItem("Remove Repeat Files", remove);
		removeRepeat.addActionListener(this);
		fileMenu.add(removeRepeat);

		clearFiles = new JMenuItem("Clear Selected Files", clear);
		clearFiles.addActionListener(this);
		fileMenu.add(clearFiles);

		encrypt = new JMenuItem("Encrypt Selected Files", encryptImage);
		encrypt.addActionListener(this);
		editMenu.add(encrypt);

		decrypt = new JMenuItem("Decrypt Selected Files", decryptImage);
		decrypt.addActionListener(this);
		editMenu.add(decrypt);

		safeEncrypt = new JCheckBoxMenuItem("Safe Encrypt");
		safeEncrypt.setMnemonic(KeyEvent.VK_S);
		safeEncrypt.setDisplayedMnemonicIndex(1);
		safeEncrypt.setSelected(Encryptor.safeEncrypt);
		optionsMenu.add(safeEncrypt);
		safeEncrypt.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					Encryptor.safeEncrypt = true;
					System.out.println("Safe Encrypt ENABLED");
				}
				if(e.getStateChange() == ItemEvent.DESELECTED)
				{
					Encryptor.safeEncrypt = false;
					System.out.println("Safe Encrypt DISABLED");
				}
			}
		});

		outputLog = new JCheckBoxMenuItem("Output Log");
		outputLog.setMnemonic(KeyEvent.VK_O);
		outputLog.setDisplayedMnemonicIndex(1);
		optionsMenu.add(outputLog);
		outputLog.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
					Display.openConsole();
				if(e.getStateChange() == ItemEvent.DESELECTED)
					Display.closeConsole();
			}
		});

		howToUse = new JMenuItem("How To Use", howTo);
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);

		tutorialLink = new JMenuItem("Watch the tutorial!", tutorial);
		tutorialLink.addActionListener(this);
		helpMenu.add(tutorialLink);

		exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(this);
		fileMenu.add(exit);
	}

	/**
	 * @return the JMenuBar object
	 */
	public JMenuBar getJMenuBar()
	{
		return this.menuBar;
	}

	/**
	 * Sets up the action events of the menu bar
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(exit))
			System.exit(0);

		if(e.getSource().equals(openFiles))
			FileSelectionUtils.selectFiles();

		if(e.getSource().equals(printSelected))
			Encryptor.printInformation();

		if(e.getSource().equals(removeRepeat))
			Encryptor.removeRepeatFiles();

		if(e.getSource().equals(clearFiles))
			Encryptor.clearSelectedFiles();

		if(e.getSource().equals(encrypt))
			Encryptor.setupEncryption();
			//Encryptor.encryptSelectedFiles();

		if(e.getSource().equals(decrypt))
			Encryptor.setupDecryption();
			//Encryptor.decryptSelectedFiles();

		if(e.getSource().equals(helpMenu))
			System.out.println("Help!");

		if(e.getSource().equals(tutorialLink))
			openWebpage("https://www.youtube.com/watch?v=7R8IUnq9kEs");
	}

	/**
	 * Does nothing currently
	 * @param e
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	/**
	 * checks for key presses and executes actions accordingly
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_X)
		{
			System.exit(0);
		}
	}

	/**
	 * Does nothing currently
	 * @param e
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	/**
	 * Detects the menu clicks
	 * @param e
	 */
	@Override
	public void menuSelected(MenuEvent e)
	{
		if(e.getSource().equals(exit))
		{
			System.exit(0);
		}
	}

	/**
	 * Does nothing currently
	 * @param e
	 */
	@Override
	public void menuDeselected(MenuEvent e)
	{

	}

	/**
	 * Does nothing currently
	 * @param e
	 */
	@Override
	public void menuCanceled(MenuEvent e)
	{

	}

	/**
	 * Opens the webpage of the given URI
	 * @param uri
	 * @return if the webpage was opened or not
	 */
	public static boolean openWebpage(URI uri)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				System.out.println("Opening "+uri+" on the default browser");
				desktop.browse(uri);
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * If the given URI is in string format it will convert it to a URI and call the other method above
	 * @param urlString
	 * @return if the webpage was opened or not
	 */
	public static boolean openWebpage(String urlString)
	{
		try
		{
			return openWebpage(new URL(urlString).toURI());
		} catch (URISyntaxException | MalformedURLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
