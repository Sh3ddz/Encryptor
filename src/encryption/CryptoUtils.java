package encryption;
import utils.FileSelectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 * This class handles the encryption / decryption
 */
public class CryptoUtils
{
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static SecretKey secretKey;
	private static byte[] salt;

	public static boolean successfulCrypto = false;
	/**
	 * Generates secure random salt value
	 * @return array of random salt bytes
	 */
	private static byte[] generateSalt()
	{
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Generates the secret key using the given password and salt values from the file
	 * @param password
	 */
	private static void generateSecretKey(String password)
	{
		try
		{
			if(salt == null)
			{
				salt = generateSalt();
			}
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			CryptoUtils.secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
		} catch(NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Resets the secret key and salt values,
	 * so it can encrypt multiple different instances without restarting
	 */
	public static void resetKeySpecs()
	{
		secretKey = null;
		salt = null;
	}

	/**
	 * Encrypts the selected input file using the given password
	 * Generates the secret key given the password
	 * Generates a secure random IV for each file
	 *
	 * Writes the IV, salt, and ciphertext to the output file.
	 * Using AES 256 bit encryption.
	 * @param password
	 * @param inputFile
	 * @param outputFile
	 * @throws CryptoException
	 */
	public static void encrypt(String password, File inputFile, File outputFile) throws CryptoException
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(inputFile);
			FileOutputStream output = new FileOutputStream(outputFile);

			if(secretKey == null)
				generateSecretKey(password);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);

			byte[] iv = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

			String header = "HEADER Encrypted using Encryptor";
			byte[] headerBytes = header.getBytes("UTF-8");
			headerBytes = cipher.doFinal(headerBytes);

			String fileExtension = FileSelectionUtils.getFileExtension(inputFile.getAbsolutePath());
			byte[] fileExtensionBytes = new byte[256];
			System.arraycopy(fileExtension.getBytes("UTF-8"),0, fileExtensionBytes,0, fileExtension.getBytes("UTF-8").length);
			//fileExtensionBytes = cipher.doFinal(fileExtensionBytes);

			output.write(headerBytes);
			output.write(iv);
			output.write(salt);
			output.write(fileExtensionBytes);

			CipherOutputStream outputStream = new CipherOutputStream(output, cipher);

			byte[] buffer = new byte[8192];
			int count;
			while((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			inputStream.close();
			outputStream.close();
			successfulCrypto = true;
			Encryptor.addFilesLastChanged(outputFile);
		} catch (Exception ex)
		{
			successfulCrypto = false;
			throw new CryptoException("Error encrypting file", ex);
		}
	}

	/**
	 * Decrypts the selected input file using the given password
	 * Reads the IV values from the file to use for decryption
	 * Reads the salt values from the file to use to generate the secret key
	 * Outputs the decrypted text to the selected output file
	 *
	 * Using AES 256 bit encryption.
	 * @param password
	 * @param inputFile
	 * @param outputFile
	 * @throws CryptoException
	 */
	public static void decrypt(String password, File inputFile, File outputFile) throws CryptoException
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(inputFile);

			byte[] headerBytes = new byte[48];
			inputStream.read(headerBytes);
			byte[] iv = new byte[16];
			inputStream.read(iv);
			salt = new byte[20];
			inputStream.read(salt);
			byte[] fileExtensionBytes = new byte[256];
			inputStream.read(fileExtensionBytes);

			if(secretKey == null)
				generateSecretKey(password);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

			//checking key
			//if the key isn't valid
			if(!checkKeyValidity(cipher, headerBytes))
			{
				System.out.println("WRONG KEY for file: " + inputFile.getAbsolutePath());
				inputStream.close();
				if(Encryptor.safeCrypt)
					outputFile.delete();
				successfulCrypto = false;
				return;
			}

			//getting the file extension data so it can transform the files back into their original extension types
			//fileExtensionBytes = cipher.doFinal(fileExtensionBytes);
			fileExtensionBytes = new String(fileExtensionBytes).replaceAll("\0", "").getBytes();
			String fileExtension = new String(fileExtensionBytes, "UTF-8");
			outputFile = new File(outputFile.getAbsolutePath()+"."+fileExtension);

			CipherOutputStream outputStream = new CipherOutputStream(new FileOutputStream(outputFile), cipher);

			byte[] buffer = new byte[8192];
			int count;
			while ((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			inputStream.close();
			outputStream.close();
			successfulCrypto = true;
			Encryptor.addFilesLastChanged(outputFile);
		} catch (Exception ex)
		{
			successfulCrypto = false;
			throw new CryptoException("Error decrypting file", ex);
		}
	}

	/**
     * Checks if the given decryption key is right
     * returns true if the key is valid, returns false if the key is not valid
	 * @param cipher
	 * @param headerBytes
	 * @return if the key is valid or not
	 */
	private static boolean checkKeyValidity(Cipher cipher, byte[] headerBytes)
	{
		//checking if the key is right or not
		try
		{
			headerBytes = cipher.doFinal(headerBytes);
			String header = new String(headerBytes, "UTF-8");
			if(!header.equals("HEADER Encrypted using Encryptor"))
				return false;
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
}