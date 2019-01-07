package display;

import encryption.Encryptor;
import utils.FileSelectionUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class Display
{
	private static JFrame frame;
	private static JPanel panel;
	private Canvas canvas;
	private MenuBar menuBar;

	private JButton encrypt;
	private JButton decrypt;
	private JButton clear;

	private JTextArea console;
	private static JScrollPane consoleScroll;

	private static JTextArea dragAndDrop;

	private static JScrollPane fileListScroll;
	public static JList fileList;

	public static JProgressBar progressBar;
	public static JLabel progressText;

	private static GridBagConstraints c = new GridBagConstraints();

	private String title;
	private static int width, height;

	private static int heightOffset = 86;

	public Display(String title, int width, int height)
	{
		this.title = title;
		Display.width = width;
		Display.height = height;
		createDisplay();
	}
	/**
	 * sets up all global variables and puts everything within the panel / frame
	 */
	private void createDisplay()
	{
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout((new BorderLayout()));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		menuBar = new MenuBar();

		panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(width, height));

		encrypt = new JButton("Encrypt Selected Files", this.menuBar.encryptImage);
		encrypt.setPreferredSize(new Dimension(width / 2, height / 11));
		encrypt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Encryptor.setupEncryption();
			}
		} );
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(encrypt, c);

		decrypt = new JButton("Decrypt Selected Files", this.menuBar.decryptImage);
		decrypt.setPreferredSize(new Dimension(width / 2, height / 11));
		decrypt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Encryptor.setupDecryption();
			}
		} );
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(decrypt, c);

		clear = new JButton("Clear Selected Files", this.menuBar.clear);
		clear.setPreferredSize(new Dimension(width / 2, height / 11));
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Encryptor.clearSelectedFiles();
			}
		} );
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 2;
		panel.add(clear, c);

		fileList = new JList();
		fileListScroll = new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fileListScroll.setPreferredSize(new Dimension(width / 2, height-heightOffset));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(fileListScroll, c);
		enableDragAndDrop(fileList);
		enableDragAndDrop(fileListScroll);

		dragAndDrop = new JTextArea("Drag and Drop Files here!\n");
		dragAndDrop.append("<- Or there!");
		dragAndDrop.setEditable(false);
		dragAndDrop.setPreferredSize(new Dimension(width / 2, height-heightOffset));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(dragAndDrop, c);
		enableDragAndDrop(dragAndDrop);

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
		consoleScroll.setPreferredSize(new Dimension(width, (height / 2)-heightOffset));
		openConsole();

		frame.setJMenuBar(menuBar.getJMenuBar());
		frame.setIconImage(new ImageIcon(getClass().getResource("/res/encryptor.png")).getImage());
		frame.add(panel);
		frame.pack();
	}

	/**
	 * makes the component parameter have drag and drop capabilities.
	 * @param component
	 */
	private void enableDragAndDrop(Component component)
	{
		DropTarget target=new DropTarget(component ,new DropTargetListener(){
			public void dragEnter(DropTargetDragEvent e) { }
			public void dragExit(DropTargetEvent e) { }
			public void dragOver(DropTargetDragEvent e) { }
			public void dropActionChanged(DropTargetDragEvent e) { }

			public void drop(DropTargetDropEvent e)
			{
				try
				{
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					java.util.List list=(java.util.List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

					File[] files = new File[list.size()];
					for(int i = 0; i < list.size(); i++)
					{
						File file = (File) list.get(i);
						files[i] = file;
					}
					Encryptor.addFiles(FileSelectionUtils.getAllFilesInDir(files));

				}catch(Exception ex){}
			}
		});
	}

	/**
	 * Sets up the progress bar and swingworker depending on the mode
	 * 1 = Encrypt, 2 = Decrypt
	 * @param mode
	 */
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
						dlg.dispose();
					}
				};

		sw.execute(); //starts the processing on a separate thread
		dlg.setVisible(true);
	}

	/**
	 * Updates the display JList with the given ArrayList of files
	 * @param files
	 */
	public static void updateList(ArrayList<File> files)
	{
		DefaultListModel listModel = new DefaultListModel();
		for (Object item : files)
		{
			File file = (File) item;
			listModel.addElement(file);
		}

		fileList.setModel(listModel);
	}

	/**
	 * @return the canvas
	 */
	public Canvas getCanvas()
	{
		return canvas;
	}

	/**
	 * @return the frame
	 */
	public static JFrame getFrame()
	{
		return frame;
	}

	/**
	 * @return the menu bar
	 */
	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	/**
	 * Opens the Console (output log)
	 * adds it to the JPanel and adjusts the size of the other components.
	 */
	protected static void openConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		panel.add(consoleScroll, c);
		fileListScroll.setPreferredSize(new Dimension(width / 2, height/2));
		dragAndDrop.setPreferredSize(new Dimension(width / 2, height/2));
		panel.updateUI();
	}

	/**
	 * Closes the Console (output log)
	 * removes it from the JPanel and adjusts the size of the other components.
	 */
	protected static void closeConsole()
	{
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		panel.remove(consoleScroll);
		fileListScroll.setPreferredSize(new Dimension(width / 2, height-heightOffset));
		dragAndDrop.setPreferredSize(new Dimension(width / 2, height-heightOffset));
		panel.updateUI();
	}

}