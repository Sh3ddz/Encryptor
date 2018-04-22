package display;

import encryption.Encryptor;

import java.awt.*;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class Display
{

	private static JFrame frame;
	private static JPanel panel;
	private Canvas canvas;
	private MenuBar menuBar;
	private JTextArea console;
	private static JScrollPane consoleScroll;

	public static JProgressBar progressBar;
	public static JLabel progressText;

	private static GridBagConstraints c = new GridBagConstraints();

	private String title;
	private int width, height;

	public Display(String title, int width, int height)
	{
		this.title = title;
		this.width = width;
		this.height = height;
		createDisplay();
	}

	private void createDisplay()
	{
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout((new BorderLayout()));
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		menuBar = new MenuBar();

		console = new JTextArea();
		console.setEditable(false);
		PrintStream out = new PrintStream(new TextAreaOutputStream(console));
		System.setOut(out);
		System.setErr(out);
		System.out.println("Output Log: ");
		DefaultCaret caret = (DefaultCaret)console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		console.setCaretPosition(console.getDocument().getLength());

		consoleScroll = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		consoleScroll.setPreferredSize(new Dimension(width, height/2));

		panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(width, height));

		JTextArea fileList = new JTextArea("Future Files List Here");
		JScrollPane fileListScroll = new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fileListScroll.setPreferredSize(new Dimension(width / 2, height / 2));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(fileListScroll, c);

		JTextArea dragAndDrop = new JTextArea("Future Drag and Drop Window Here");
		JScrollPane dragAndDropScroll = new JScrollPane(dragAndDrop, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		dragAndDropScroll.setPreferredSize(new Dimension(width / 2, height / 2));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(dragAndDropScroll, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;

		frame.setJMenuBar(menuBar.getJMenuBar());
		frame.setIconImage(new ImageIcon(getClass().getResource("/res/encryptor.png")).getImage());
		frame.add(panel);
		frame.pack();
	}

	public static void setupProgressBar(int mode)
	{
		final JDialog dlg;
		if(mode == 1)
		{
			dlg = new JDialog(Display.getFrame(), "Encrypting Files...", true);
			progressBar = new JProgressBar(0, Encryptor.getFiles().size());
			progressText = new JLabel("Encrypting Files: ");
		}
		else
		{
			dlg = new JDialog(Display.getFrame(), "Decrypting Files...", true);
			progressBar = new JProgressBar(0, Encryptor.getFiles().size());
			progressText = new JLabel("Decrypting Files: ");
		}
		dlg.add(BorderLayout.CENTER, progressBar);
		dlg.add(BorderLayout.NORTH, progressText);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(300, 75);
		dlg.setLocationRelativeTo(frame);

		SwingWorker<Void, Void> sw =
				new SwingWorker<Void, Void>()
				{
					@Override
					protected Void doInBackground() throws Exception
					{
						if(mode ==1)
							Encryptor.encryptSelectedFiles();
						else
							Encryptor.decryptSelectedFiles();

						setProgress(100);
						return null;
					}

					@Override
					protected void done()
					{
						dlg.dispose();//close the modal dialog
					}
				};

		sw.execute(); // this will start the processing on a separate thread
		dlg.setVisible(true);
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public static JFrame getFrame()
	{
		return frame;
	}

	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	protected static void openConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(consoleScroll, c);
		panel.updateUI();
	}

	protected static void closeConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.remove(consoleScroll);
		panel.updateUI();
	}

}