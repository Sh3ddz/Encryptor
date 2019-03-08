package utils;

import encryption.Encryptor;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
/**
 * @author Sh3ddz - https://github.com/Sh3ddz
 */
public class FileSelectionUtils
{
	/**
	 * gets all the files that are in the selected directory or multiple directories
	 * @param everythingInDirectory all the directories and files that the user selected
	 * @return Arraylist of files, all the files within selected directories as well as selected files
	 */
	public static ArrayList<File> getAllFilesInDir(File[] everythingInDirectory)
	{
		ArrayList<File> files = new ArrayList<>();

		//creating the filesStack so we can get every file within a directory and sub directories.
		Stack<File> everythingStack = new Stack<>();
		Stack<File> filesStack = new Stack<>();
		Stack<File> directoriesStack = new Stack<>();

		for(int i = 0; i < everythingInDirectory.length; i++)
		{
			everythingStack.add(everythingInDirectory[i]);
		}

		while(!everythingStack.isEmpty())
		{
			File currentFile = everythingStack.pop();
			//System.out.println("Current: "+currentFile.getAbsolutePath());
			if(currentFile.isDirectory())
				directoriesStack.add(currentFile);
			else
				files.add(currentFile);
			//getting all the files out of the directories
			while(!directoriesStack.isEmpty())
			{
				//getting all the files in the directory and sending them back
				//to the everything stack to be sorted again
				File[] dirArr = currentFile.listFiles();
				if(dirArr != null)
				{
					for(int i = 0; i < dirArr.length; i++)
					{
						everythingStack.add(dirArr[i]);
					}
				}
				directoriesStack.pop();
			}
			while(!filesStack.isEmpty())
			{
				files.add(filesStack.pop());
			}
		}
		return files;
	}
	/**
	 * Sets up a file chooser and allows the user to select files in the file chooser
	 */
	public static void selectFiles()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Files");
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//fileChooser.setFileFilter(new FileNameExtensionFilter("Properties file", "properties"));

		File workingDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setCurrentDirectory(workingDirectory);

		//getting the path of the new world file and then loading it.
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			ArrayList<File> files = new ArrayList<File>();

			File[] arrFiles = fileChooser.getSelectedFiles();

			for(int i = 0; i < arrFiles.length; i++)
			{
				if(arrFiles[i].isDirectory())
				{
					File[] arrEverythingInDirectory = arrFiles[i].listFiles();
					ArrayList<File> filesToAdd = getAllFilesInDir(arrEverythingInDirectory);
					for(int j = 0; j < filesToAdd.size(); j++)
					{
						files.add(filesToAdd.get(j));
					}
				}
				else
				{
					files.add(arrFiles[i]);
				}
			}

			Encryptor.addFiles(files);
		}
	}

	/**
	 * Removes the file extension from the file path ex: abc.txt -> abc
	 * @param path
	 * @return the path of the file without the file extension
	 */
	public static String removeFileExtension(String path)
	{
		String inputFilePathTrimmed = "";
		int loc = getFileExtensionLoc(path);
		//if it's just a raw file type with no . anything
		if(loc == 0)
			inputFilePathTrimmed = path;
		else //else trim it properly
			inputFilePathTrimmed = path.substring(0, loc-1);
		return inputFilePathTrimmed;
	}

	/**
	 * Gets the file extension
	 * @param path
	 * @return the file extension
	 */
	public static String getFileExtension(String path)
	{
		int loc = getFileExtensionLoc(path);

		if(loc == 0)
			return path;
		else
			return path.substring(loc);
	}

	/**
	 * Gets the length of the file extension
	 * @param path
	 * @return the length of the file extension
	 */
	public static int getFileExtensionLength(String path)
	{
		return path.length() - getFileExtensionLoc(path);
	}

	/**
	 * Gets the location of the file extension in the path
	 * @param path
	 * @return the location of the file extension in the path
	 */
	private static int getFileExtensionLoc(String path)
	{
		int fileTypeLoc = 0;
		for(int i = path.length(); i > 0; i--)
		{
			if(path.substring(i-1, i).equals("."))
			{
				fileTypeLoc = i;
				break;
			}
		}
		return fileTypeLoc;
	}
}
