package encryption;

import javax.crypto.Cipher;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;

//ABOUT:
//          This encryptor uses AES encryption algorithm with a 256 bit key and random iv values for extreme security.
//          it only requires the 256 bit or 32 char key/password to decrypt the files for ease of access.

//TO DO:DONEprompt the user to enter a key or generate a random one
//          drag and drop files into the window to select them.
//          create a list like UI interface to select / deselect files for encryption / decryption
//   DONE   Have an option for safe encryption or not (safe encryption doesnt overwrite or delete the original file, just adds a .encrypted and .decrypted file.)
public class Encryptor
{
	private static ArrayList<File> files;
	//you can either use a 128 bit or 256 bit key.
	//16 chars = 128 bit | 32 chars = 256 bit.
	private static String key = "]KPYqg$:izYBp~'n]KPYqg$:izYBp~'n";
	public static boolean safeEncrypt = true;

	public Encryptor()
	{
		files = new ArrayList<>();

		fixKeyLength();
	}

	public static void encryptSelectedFiles()
	{
		System.out.println("Encrypting files please wait...");
		//Encryption
		//if there is no key, generate one.
		promptKeyChoice();
		for(int i = 0; i < files.size(); i++)
		{
			try
			{
				File inputFile = files.get(i);
				File encryptedFile = new File(files.get(i).getAbsolutePath()+".encrypted");
				CryptoUtils.encrypt(Encryptor.key, inputFile, encryptedFile);
				if(!safeEncrypt)
					inputFile.delete();

			} catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		System.out.println("File Encryption done, thank you!");
	}

	public static void decryptSelectedFiles()
	{
		System.out.println("Decrypting files please wait...");

		removeNonEncryptedFiles();
		printInformation();
		promptKey();
		for(int i = 0; i < files.size(); i++)
		{
			try
			{
				File encryptedFile = files.get(i);
				File decryptedFile;

				if(safeEncrypt)
					decryptedFile = new File(files.get(i).getAbsolutePath().substring(0,files.get(i).getAbsolutePath().length()-10)+".decrypted");
				else
					decryptedFile = new File(files.get(i).getAbsolutePath().substring(0,files.get(i).getAbsolutePath().length()-10));

				CryptoUtils.decrypt(Encryptor.key, encryptedFile, decryptedFile);
				if(!safeEncrypt)
					encryptedFile.delete();

			} catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		System.out.println("File Decryption done, thank you!");
	}

	private static void promptKeyChoice()
	{
		String[] options = new String[] {"Random Key", "Custom Key"};
		int response = JOptionPane.showOptionDialog(null, "Would you like a randomly generated key, or a custom one?", "KeyPrompt",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		if(response == 0)
			generateRandomKey();
		else
			getCustomKey();
	}

	private static void generateRandomKey()
	{
		String randKey = "";
		int keyLength = 32;

		for(int i = 0; i < keyLength; i++)
		{
			int asciiValue = 33 + (int)(Math.random() * ((126 - 33) + 1));
			char toAppend = (char)asciiValue;
			randKey += toAppend;
		}

		JTextArea ta = new JTextArea(1, 32);
		ta.setText(randKey);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setCaretPosition(0);
		ta.setEditable(false);

		JOptionPane.showMessageDialog(null, new JScrollPane(ta), "YOUR RANDOM KEY(FOR DECRYPTING, PLEASE SAVE)", JOptionPane.INFORMATION_MESSAGE,null);

		Encryptor.key = randKey;
	}

	private static void getCustomKey()
	{
		int diff = 0;
		String customKey = JOptionPane.showInputDialog(null, "Enter your custom key (no more than 32 characters)");
		while(customKey.length() > 32)
			customKey = JOptionPane.showInputDialog(null, "Enter your custom key (Please enter one no more than 32 characters!)");
		if(customKey.length() < 32) diff = 32 - customKey.length();

		for(int i = 0; i < diff; i++)
			customKey += "-";

		Encryptor.key = customKey;
	}

	private static void promptKey()
	{
		int diff = 0;
		String promptedKey = JOptionPane.showInputDialog(null, "Enter your key!");
		Encryptor.key = promptedKey;

		if(promptedKey.length() < 32) diff = 32 - promptedKey.length();

		for(int i = 0; i < diff; i++)
			promptedKey += "-";

		Encryptor.key = promptedKey;
	}

	public static void setFiles(ArrayList<File> files)
	{
		Encryptor.files = files;
	}

	public static void addFiles(ArrayList<File> files)
	{
		Encryptor.files.addAll(files);
	}

	public static void removeRepeatFiles()
	{
		// Store unique items in result.
		ArrayList<File> result = new ArrayList<>();

		// Record encountered Files in HashSet.
		HashSet<File> set = new HashSet<>();

		// Loop over argument list.
		for (File item : Encryptor.files)
		{
			// If File is not in set, add it to the list and the set.
			if (!set.contains(item))
			{
				result.add(item);
				set.add(item);
			}
		}
		System.out.println("Removed repeat files.");
		Encryptor.files = result;
	}

	private static void removeNonEncryptedFiles()
	{
		ArrayList<File> nonEnc = new ArrayList<>();
		//I dont remove them in the same loop, because removing elements from a list while looping causes bad errors.
		//getting all the non encrypted files
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
	}

	public static void clearSelectedFiles()
	{
		Encryptor.files.clear();
		System.out.println("Cleared selected files");
	}

	//wasnt allowing 256 key length for some reason, so added this
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
