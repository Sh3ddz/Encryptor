package utils;

import encryption.Encryptor;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class FileSelectionUtils
{
	//Gets ALL the files in the directory, including all subdirectories and files within them
	//NON recursive, using stacks.
	public static ArrayList<File> getAllFilesInDir(File[] everythingInDirectory)
	{
		ArrayList<File> files = new ArrayList<File>();

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
			Encryptor.printInformation();
		}
	}
}
