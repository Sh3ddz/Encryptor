package encryption;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils
{
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static SecretKey secretKey;
	private static byte[] salt;
	/*
	 *  Generates secure random salt value
	 */
	private static byte[] generateSalt()
	{
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}

	/*
	 *  Generates the secret key using the given password and salt values from the file
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

	public static void resetKeySpecs()
	{
		secretKey = null;
		salt = null;
	}

	/*
	 *  Encrypts the selected input file using the given password
	 *  Generates the secret key given the password
	 *  Generates a secure random IV for each file
	 *
	 *  Writes the IV, salt, and ciphertext to the output file.
	 *  Using AES 256 bit encryption.
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

			output.write(headerBytes);
			output.write(iv);
			output.write(salt);

			CipherOutputStream outputStream = new CipherOutputStream(output, cipher);

			byte[] buffer = new byte[8192];
			int count;
			while ((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			inputStream.close();
			outputStream.close();

		} catch (Exception ex)
		{
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	/*
	 *  Decrypts the selected input file using the given password
	 *  Reads the IV values from the file to use for decryption
	 *  Reads the salt values from the file to use to generate the secret key
	 *  Outputs the decrypted text to the selected output file
	 *
	 *  Using AES 256 bit encryption.
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
				outputFile.delete();
				return;
			}

			CipherOutputStream outputStream = new CipherOutputStream(new FileOutputStream(outputFile), cipher);

			byte[] buffer = new byte[8192];
			int count;
			while ((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			inputStream.close();
			outputStream.close();

		} catch (Exception ex)
		{
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	/*
     * Checks if the given decryption key is right
     * returns true if the key is valid, returns false if the key is not valid
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
		} catch(BadPaddingException e)
		{
			e.printStackTrace();
			return false;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
}