package encryption;

import display.Display;

import javax.crypto.Cipher;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 * This class doesn't actually handle the encryption, but just about everything else.
 */
public class Encryptor
{
	private static final int ENCRYPT = 1;
	private static final int DECRYPT = 2;

	private static ArrayList<File> files;
	private static String password;

	public static boolean safeCrypt = false;

	public Encryptor()
	{
		files = new ArrayList<>();
		fixKeyLength();
	}

	/**
	 * Sets up for encryption
	 * Sets up the progress bar to show encryption progress
	 */
	public static void setupEncryption()
	{
		promptPasswordChoice();
		if(password == null)
			return;

		Display.setupProgressBar(ENCRYPT);
	}

	/**
	 * Loops through the selected files list and encrypts them using the password entered by the user
	 */
	public static void encryptSelectedFiles()
	{
		System.out.println("Encrypting files please wait...");
		ArrayList<File> encryptedFiles = new ArrayList<>();
		long totalMegaBytes = 0;

		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		for(int i = 0; i < files.size(); i++)
		{
			try
			{
				elapsedTime = System.currentTimeMillis() - startTime;

				File inputFile = files.get(i);
				File encryptedFile = new File(files.get(i).getAbsolutePath()+".encrypted");
				CryptoUtils.encrypt(Encryptor.password, inputFile, encryptedFile);

				Display.progressBar.setValue(i+1);
				int percent = (int)(((double)(i+1)/(double)(files.size()))*100);
				totalMegaBytes += (files.get(i).length()/1000000);
				Display.progressText.setText("Encrypting Files: "+(i+1)+"/"+files.size()+" | "+percent+"% | "+totalMegaBytes+" MB | "+elapsedTime/1000.0+"s elapsed");

				if(!safeCrypt)
					inputFile.delete();
				encryptedFiles.add(encryptedFile);
			} catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Encrypted "+files.size()+" files | "+totalMegaBytes+"MB in "+elapsedTime/1000.0+"s");

		files = encryptedFiles;
		Display.updateList(files);
		CryptoUtils.resetKeySpecs();
		System.out.println("File Encryption done, thank you!");
	}

	/**
	 * Sets up for smooth decryption
	 * removes non-encrypted files so it doesnt loop through and decrypt files that dont need to be
	 * removes repeated files for the same reason as above
	 * prompts the user to enter the password for decryption
	 * Sets up the progress bar to show decryption progress
	 */
	public static void setupDecryption()
	{
		removeNonEncryptedFiles();
		removeRepeatFiles();
		promptPassword();

		Display.setupProgressBar(DECRYPT);
	}

	/**
	 * Loops through the selected files list and decrypts them using the password entered by the user
	 */
	public static void decryptSelectedFiles()
	{
		System.out.println("Decrypting files please wait...");
		ArrayList<File> decryptedFiles = new ArrayList<>();
		long totalMegaBytes = 0;

		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		for(int i = 0; i < files.size(); i++)
		{
			try
			{
				elapsedTime = System.currentTimeMillis() - startTime;

				File encryptedFile = files.get(i);
				File decryptedFile;

				if(safeCrypt)
					decryptedFile = new File(files.get(i).getAbsolutePath().substring(0,files.get(i).getAbsolutePath().length()-10)+".decrypted");
				else
					decryptedFile = new File(files.get(i).getAbsolutePath().substring(0,files.get(i).getAbsolutePath().length()-10));


				CryptoUtils.decrypt(Encryptor.password, encryptedFile, decryptedFile);
				//if it successfully decrypted
				if(CryptoUtils.successfulCrypto)
				{
					Display.progressBar.setValue(i + 1);
					int percent = (int) (((double) (i + 1) / (double) (files.size())) * 100);
					totalMegaBytes += (files.get(i).length() / 1000000);
					Display.progressText.setText("Decrypting Files: " + (i + 1) + "/" + files.size() + " | " + percent + "% | " + totalMegaBytes + " MB | "+elapsedTime/1000.0+"s elapsed");

					if(!safeCrypt)
						encryptedFile.delete();
					decryptedFiles.add(decryptedFile);
				}
				else //if it doesn't successfully decrypt, keep the encrypted files in the selected files list.
					decryptedFiles.add(encryptedFile);
			} catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Decrypted "+files.size()+" files | "+totalMegaBytes+"MB in "+elapsedTime/1000.0+"s");

		files = decryptedFiles;
		Display.updateList(files);
		CryptoUtils.resetKeySpecs();
		System.out.println("File Decryption done, thank you!");
	}

	/**
	 * Prompts the user if they want to generate a random password or enter a custom one.
	 */
	private static void promptPasswordChoice()
	{
		String[] options = new String[] {"Random Password", "Custom Password"};
		int response = JOptionPane.showOptionDialog(Display.getFrame(), "Would you like a randomly generated password, or a custom one?", "PasswordPrompt",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		if(response == 0)
			generateRandomPassword();
		else
			getCustomPassword();
	}

	/**
	 * Generates a random ascii password
	 * from ascii values min-max (i'd recommend keeping it between 33-126 for normal chars, can cause errors otherwise)
	 * Has nothing to do with the key security it simply generates a random password for the user.
	 */
	private static void generateRandomPassword()
	{
		String randPassword = "";
		int passwordLength = 48;
		SecureRandom random = new SecureRandom();
		int max = 126;
		int min = 33;

		for(int i = 0; i < passwordLength; i++)
		{
			int asciiValue = random.nextInt(max-min+1)+min;
			char toAppend = (char)asciiValue;
			randPassword += toAppend;
		}

		showPassword(randPassword);
		Encryptor.password = randPassword;
	}

	/**
	 * Displays the randomly generated password to the user
	 * Allows the user to copy and save the password for future use.
	 * @param password
	 */
	private static void showPassword(String password)
	{
		JTextArea ta = new JTextArea(1, password.length());
		ta.setText(password);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setCaretPosition(0);
		ta.setEditable(false);

		int n = JOptionPane.showOptionDialog(Display.getFrame(), new JScrollPane(ta),"YOUR PASSWORD, PLEASE SAVE.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, new Object[] {"Copy to clipboard", "OK"}, JOptionPane.YES_OPTION);

		if (n == JOptionPane.YES_OPTION)
		{
			StringSelection stringSelection = new StringSelection(password);
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(stringSelection, null);
			JOptionPane.showMessageDialog(Display.getFrame(), "Copied to clipboard!", "Copied!", JOptionPane.INFORMATION_MESSAGE,null);
 		} else if (n == JOptionPane.NO_OPTION)
		{
			System.out.println("OK");
		}
	}

	/**
	 * Allows the user to enter a custom password
	 * Used in Encryption
	 */
	private static void getCustomPassword()
	{
		String customPassword = JOptionPane.showInputDialog(Display.getFrame(), "Enter your custom password");
		Encryptor.password = customPassword;
	}

	/**
	 * Prompts the decrypt password from the user
	 * Used in Decryption
	 */
	private static void promptPassword()
	{
		String promptedPassword = JOptionPane.showInputDialog(Display.getFrame(), "Enter your password!");
		Encryptor.password = promptedPassword;
	}

	/**
	 * @param files
	 */
	public static void setFiles(ArrayList<File> files)
	{
		Encryptor.files = files;
	}

	/**
	 * @return Arraylist of files
	 */
	public static ArrayList<File> getFiles() { return files; }

	/**
	 * Adds an ArrayList of File types to the selected files list
	 * @param files
	 */
	public static void addFiles(ArrayList<File> files)
	{
		Encryptor.files.addAll(files);
		Encryptor.removeRepeatFiles();
		Display.updateList(Encryptor.files);
		Encryptor.printInformation();
	}

	/**
	 * Removes any files that have the same absolute path from the selected files list.
	 */
	public static void removeRepeatFiles()
	{
		ArrayList<File> result = new ArrayList<>();

		HashSet<File> set = new HashSet<>();

		for (File item : Encryptor.files)
		{
			if (!set.contains(item))
			{
				result.add(item);
				set.add(item);
			}
		}
		System.out.println("Removed repeat files.");
		Encryptor.files = result;
	}

	/**
	 * Removes any files that don't end in .encrypted from the selected files list.
	 */
	private static void removeNonEncryptedFiles()
	{
		ArrayList<File> nonEnc = new ArrayList<>();
		for(int i = 0; i < files.size(); i++)
		{
			if(!files.get(i).getAbsolutePath().substring(files.get(i).getAbsolutePath().length()-10).equals(".encrypted"))
			{
				nonEnc.add(files.get(i));
			}
		}
		//removing them
		for(int i = 0; i < nonEnc.size(); i++)
		{
			files.remove(nonEnc.get(i));
		}
		Display.updateList(files);
	}

	/**
	 * Clears all files from the selected file list.
	 */
	public static void clearSelectedFiles()
	{
		Encryptor.files.clear();
		Display.updateList(files);
		System.out.println("Cleared selected files.");
	}

	/**
	 * Allows 256 bit keys even if the required java security files aren't installed.
	 * Mine wouldn't install correctly so this does what the files would do, essentially.
	 */
	private static void fixKeyLength() {
		String errorString = "Failed manually overriding key-length permissions.";
		int newMaxKeyLength;
		try
		{
			if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256)
			{
				Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
				Constructor con = c.getDeclaredConstructor();
				con.setAccessible(true);
				Object allPermissionCollection = con.newInstance();
				Field f = c.getDeclaredField("all_allowed");
				f.setAccessible(true);
				f.setBoolean(allPermissionCollection, true);

				c = Class.forName("javax.crypto.CryptoPermissions");
				con = c.getDeclaredConstructor();
				con.setAccessible(true);
				Object allPermissions = con.newInstance();
				f = c.getDeclaredField("perms");
				f.setAccessible(true);
				((Map) f.get(allPermissions)).put("*", allPermissionCollection);

				c = Class.forName("javax.crypto.JceSecurityManager");
				f = c.getDeclaredField("defaultPolicy");
				f.setAccessible(true);
				Field mf = Field.class.getDeclaredField("modifiers");
				mf.setAccessible(true);
				mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
				f.set(null, allPermissions);

				newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
			}
		} catch (Exception e)
		{
			throw new RuntimeException(errorString, e);
		}
		if (newMaxKeyLength < 256)
			throw new RuntimeException(errorString); // hack failed
	}

	/**
	 * Prints all the files that are selected into the console log.
	 */
	public static void printInformation()
	{
		if(files.size() == 0)
			System.out.println("No files selected! Please select some.");
		else
		{
			System.out.println("Current Selected Files: ");
			for(int i = 0; i < files.size(); i++)
			{
				System.out.println("Selected " + files.get(i).getAbsolutePath());
			}
		}
	}
}
