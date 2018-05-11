package display;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class TextAreaOutputStream extends OutputStream
{
	private JTextArea textControl;

	/**
	 * sets textControl to the given param
	 * @param control
	 */
	public TextAreaOutputStream(JTextArea control)
	{
		textControl = control;
	}

	/**
	 * writes the given integer to the JTextArea
	 * @param b
	 * @throws IOException
	 */
	public void write(int b) throws IOException
	{
		textControl.append( String.valueOf((char)b));
	}
}
