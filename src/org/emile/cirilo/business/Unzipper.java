package org.emile.cirilo.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.unzip.UnzipUtil;

public class Unzipper {
	
		private final int BUFF_SIZE = 4096;

	    /**
	     * Extracts a zip file specified by the zipFilePath to a directory specified by
	     * destDirectory (will be created if does not exists)
	     * @param zipFilePath
	     * @param destDirectory
	     * @throws IOException
	     */
	    public void unzip(String zipFilePath, String destDirectory, String passwd) throws IOException {
	        File destDir = new File(destDirectory);
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        
			ZipInputStream is = null;
			OutputStream os = null;
			
			try {
				// Initiate the ZipFile
				ZipFile zipFile = new ZipFile(zipFilePath);
				String destinationPath = destDirectory;
				
				// If zip file is password protected then set the password
				if (zipFile.isEncrypted()) {
					zipFile.setPassword(passwd);
				}
				
				//Get a list of FileHeader. FileHeader is the header information for all the
				//files in the ZipFile
				List fileHeaderList = zipFile.getFileHeaders();
				
				// Loop through all the fileHeaders
				for (int i = 0; i < fileHeaderList.size(); i++) {
					FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
					if (fileHeader != null) {
						
						//Build the output file
						String outFilePath = destinationPath + System.getProperty("file.separator") + fileHeader.getFileName();
						File outFile = new File(outFilePath);
						
						//Checks if the file is a directory
						if (fileHeader.isDirectory()) {
							//This functionality is up to your requirements
							//For now I create the directory
							outFile.mkdirs();
							continue;
						}
						
						//Check if the directories(including parent directories)
						//in the output file path exists
						File parentDir = outFile.getParentFile();
						if (!parentDir.exists()) {
							parentDir.mkdirs();
						}
						
						//Get the InputStream from the ZipFile
						is = zipFile.getInputStream(fileHeader);
						//Initialize the output stream
						os = new FileOutputStream(outFile);
						
						int readLen = -1;
						byte[] buff = new byte[BUFF_SIZE];
						
						//Loop until End of File and write the contents to the output stream
						while ((readLen = is.read(buff)) != -1) {
							os.write(buff, 0, readLen);
						}
						
						//To restore File attributes (ex: last modified file time, 
						//read only flag, etc) of the extracted file, a utility class
						//can be used as shown below
						UnzipUtil.applyFileAttributes(fileHeader, outFile);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (os != null) {
						os.close();
						os = null;
					}
					if (is != null) {
						is.close();
						is = null;
					}
				} catch (IOException e) {
				}
			}
	    }
		
}
