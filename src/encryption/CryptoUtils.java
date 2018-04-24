package encryption;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils
{
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static SecretKey secretKey;

	private static void generateSecretKey(String key)
	{
		try
		{
			int keyLength = 32;
			byte[] newKey = key.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			newKey = sha.digest(newKey);
			newKey = Arrays.copyOf(newKey, keyLength); // use only first (amount, see above) of bits
			secretKey = new SecretKeySpec(newKey, ALGORITHM);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(inputFile);
			FileOutputStream output = new FileOutputStream(outputFile);

			generateSecretKey(key);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			//output for debugging
			/*
			for(int i = 0; i < iv.length; i++)
			{
				System.out.print(iv[i] + ", ");
			}
			System.out.println();
			*/
			output.write(iv);

			CipherOutputStream outputStream = new CipherOutputStream(output, cipher);

			byte[] buffer = new byte[8192];
			int count;
			while ((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			//output for debugging
			/*
			for(int i = 0; i < outputBytes.length; i++)
			{
				System.out.print(outputBytes[i] + ", ");
			}
			System.out.println();
			*/

			inputStream.close();
			outputStream.close();

		} catch (Exception ex)
		{
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(inputFile);

			generateSecretKey(key);

			byte[] iv = new byte[16];
			inputStream.read(iv);
			//output for debugging
			/*
			for(int i = 0; i < iv.length; i++)
			{
				System.out.print(iv[i] + ", ");
			}
			System.out.println();
			*/
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

			CipherOutputStream outputStream = new CipherOutputStream(new FileOutputStream(outputFile), cipher);

			byte[] buffer = new byte[8192];
			int count;
			while ((count = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, count);
			}

			//output for debugging
			/*
			for(int i = 0; i < inputBytes.length; i++)
			{
				System.out.print(inputBytes[i] + ", ");
			}
			System.out.println();
			*/

			inputStream.close();
			outputStream.close();

		} catch (Exception ex)
		{
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}
}