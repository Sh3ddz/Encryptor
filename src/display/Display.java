package display;

import java.awt.*;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class Display
{

	private JFrame frame;
	private static JPanel panel;
	private Canvas canvas;
	private MenuBar menuBar;
	private JTextArea console;
	private static JScrollPane consoleScroll;

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
		//consoleScroll.setLocation(0, height / 2);

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
		//panel.add(consoleScroll, c);

		frame.setJMenuBar(menuBar.getJMenuBar());
		frame.setIconImage(new ImageIcon(getClass().getResource("/res/encryptor.png")).getImage());
		frame.add(panel);
		frame.pack();
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	public static void openConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(consoleScroll, c);
		panel.updateUI();
	}

	public static void closeConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.remove(consoleScroll);
		panel.updateUI();
	}

}