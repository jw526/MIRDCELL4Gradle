package Update;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;

/**
 * Created by Alex on 7/6/2017.
 */
public class VersionChecker {

	private static final String thisVersion = "3.01";

	public static boolean checkVersion() {

		boolean needsUpdate = false;
		URLConnection UC = null;
		String publishedVersion = "";
		String urlStr = "https://mirdcell.njms.rutgers.edu/MIRDCell/version_check";
		try {
			URL url = new URL(urlStr);
			UC = url.openConnection();
			UC.setDoInput(true);
			UC.setUseCaches(false);

			BufferedReader br = new BufferedReader( new InputStreamReader( UC.getInputStream() ) );
			publishedVersion = br.readLine();
			System.out.println("Published version: " + publishedVersion);
			System.out.println("This version: " + thisVersion);
                        try{
                            if(Double.parseDouble(thisVersion) < Double.parseDouble(publishedVersion)){
				needsUpdate = true;
                            }
                        }catch (Exception e){
                        }
                        

		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Cannot connect  to the server. Some features will be limited.", "alert", JOptionPane.ERROR_MESSAGE);
			System.err.println("trouble reading url i/o");
		}

		if(needsUpdate){
			String Message = "MIRDcell not up to date with the most current version!\n" +
					"Click \'yes\' to get the newest version or click \'no\' to continue with the old version.  ";
			int n = JOptionPane.showConfirmDialog(
					null,
					Message,
					"Version Checker",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					UIManager.getIcon("OptionPane.questionIcon")
			);

			if(n == JOptionPane.YES_OPTION){
				//get the os
				try {
					String os = System.getProperty("os.name").toLowerCase();
					String url = "https://mirdcell.njms.rutgers.edu/";
					Runtime rt = Runtime.getRuntime();
					if (os.indexOf("win") >= 0) {
						// windows
						rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
					} else if (os.indexOf("mac") >= 0) {
						// mac
						rt.exec( "open " + url);
					}
					if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
						// linux
						String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror", "netscape","opera","links","lynx"};

						StringBuffer cmd = new StringBuffer();
						for (int i=0; i<browsers.length; i++) {
							cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
						}
						rt.exec(new String[] { "sh", "-c", cmd.toString() });
					}
				}
				catch(IOException ioe){
					System.err.println("version checker error");
					ioe.printStackTrace();
				}
				finally {
					System.exit(0);
				}
			}
			else if(n == JOptionPane.NO_OPTION){
				return false;
			}
		}
		return true;
	}

	public static void main(String args[]){
		System.out.println(checkVersion());
	}
}
