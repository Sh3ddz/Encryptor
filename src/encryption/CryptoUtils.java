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
			FileOutputStream outputStream = new FileOutputStream(outputFile);

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

			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);
			//output for debugging
			/*
			for(int i = 0; i < outputBytes.length; i++)
			{
				System.out.print(outputBytes[i] + ", ");
			}
			System.out.println();
			*/
			outputStream.write(iv);
			outputStream.write(outputBytes);

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
			FileOutputStream outputStream = new FileOutputStream(outputFile);

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

			byte[] inputBytes = new byte[(int) inputFile.length()-iv.length]; //subtracting the iv length so it doesnt read extra bits at the end.
			inputStream.read(inputBytes);
			//output for debugging
			/*
			for(int i = 0; i < inputBytes.length; i++)
			{
				System.out.print(inputBytes[i] + ", ");
			}
			System.out.println();
			*/

			byte[] outputBytes = cipher.doFinal(inputBytes);

			outputStream.write(outputBytes);

			inputStream.close();
			outputStream.close();

		} catch (Exception ex)
		{
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}
}