package ftp;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SendMyFiles {

	static Properties props;

	public static void main(String[] args) {

		SendMyFiles sendMyFiles = new SendMyFiles();

		File propertiesDirectory = new File("resources/properties.properties");
		File ftpFileDirectory = new File("resources/domainData.txt");
		System.out.println(propertiesDirectory.getAbsolutePath());
		String propertiesFile = propertiesDirectory.getAbsolutePath();
		String fileToFTP = "resources/domainData.txt";//ftpFileDirectory.getAbsolutePath();
		sendMyFiles.startFTP(propertiesFile, fileToFTP);

	}

	public boolean startFTP(String propertiesFilename, String fileToFTP) {

		props = new Properties();
		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {

			props.load(new FileInputStream(propertiesFilename));
			String serverAddress = props.getProperty("serverAddress").trim();
			String userId = props.getProperty("userId").trim();
			String password = props.getProperty("password").trim();
			String remoteDirectory = props.getProperty("remoteDirectory").trim();
			String localDirectory = props.getProperty("localDirectory").trim();
			long timeInMillis = System.currentTimeMillis();
			String fileNameToCopy = "someFile";
			String fileExtension = ".xml";
			String fileName = fileNameToCopy + timeInMillis + fileExtension;
			
			// check if the file exists
			String filepath = localDirectory + fileToFTP;
			System.out.println(filepath);
			File file = new File(filepath);
			if (!file.exists())
				throw new RuntimeException("Error. Local file not found");

			// Initializes the file manager
			manager.init();

			// Setup our SFTP configuration
			FileSystemOptions opts = new FileSystemOptions();
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

			// Create the SFTP URI using the host name, userid, password, remote path and file name
//			String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress + "/" + remoteDirectory + fileToFTP;
//			System.out.println(sftpUri);
			
//			URI uri1 = new URI("sftp", "flportal:f!p0rtal", "127.0.0.1", -1, "/C:/temp", null, null);
//			URI uri = new URI("sftp", "flportal:f!p0rtal", "10.205.17.186", 22, "/testMsg.xml", null, null);
			URI uri = new URI("sftp", userId + ":" + password , serverAddress, 22, "/"+fileName, null, null);
//			System.out.println(uri.toString());
			// Create local file object
			FileObject localFile = manager.resolveFile(file.getAbsolutePath());

			// Create remote file object
			FileObject remoteFile = manager.resolveFile(uri.toString(), opts);

			// Copy local file to sftp server
			remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
			System.out.println("File upload successful");

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			manager.close();
		}

		return true;
	}

}