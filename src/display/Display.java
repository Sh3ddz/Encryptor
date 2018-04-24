package display;

import encryption.Encryptor;
import utils.FileSelectionUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

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

	private JTextArea dragAndDrop;

	public static JList fileList;

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

		panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(width, height));

		fileList = new JList();
		JScrollPane fileListScroll = new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fileListScroll.setPreferredSize(new Dimension(width / 2, height / 2));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(fileListScroll, c);

		dragAndDrop = new JTextArea("Drag and Drop Files here!");
		dragAndDrop.setEditable(false);
		dragAndDrop.setPreferredSize(new Dimension(width / 2, height / 2));
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(dragAndDrop, c);
		enableDragAndDrop();

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

		frame.setJMenuBar(menuBar.getJMenuBar());
		frame.setIconImage(new ImageIcon(getClass().getResource("/res/encryptor.png")).getImage());
		frame.add(panel);
		frame.pack();
	}

	private void enableDragAndDrop()
	{
		DropTarget target=new DropTarget(dragAndDrop ,new DropTargetListener(){
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

	public static void updateList(ArrayList<File> files)
	{
		DefaultListModel listModel = new DefaultListModel();
		for (Object item : files) {
			File file = (File) item;
			listModel.addElement(file);
		}

		fileList.setModel(listModel);
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