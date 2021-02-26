package File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by ar548 on 8/12/2016.
 */
public class ReadAll {

	private final String directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/DATA%20FILES/data/"; //04/01
	private final String directoryMIRD = "https://mirdcell.njms.rutgers.edu/UMDNJ/MIRD/RAD/";
	BufferedReader br = null;

	public ReadAll(){

	}

	public ArrayList<String> readAll(String iso, String dataType){
		ArrayList<String> list = new ArrayList<String>(  );
		String d = directory.concat(iso);
		String dMIRD = directoryMIRD.concat( iso );
		URL url = null;
		URLConnection urlConn = null;

		if(!(iso.contains( ".dat" ) || iso.contains( ".out" ) || iso.contains( ".MIRD" ))){
			d.concat( ".dat" );
		}
		System.out.println("8/12/2016 Isotope file name = " + iso);
		try{
			url = new URL( directory );
			System.out.println( "0401 url: " + url );
			urlConn = url.openConnection();
			urlConn.setDoInput( true );
			urlConn.setUseCaches( false );

			br = new BufferedReader( new InputStreamReader( urlConn.getInputStream() ) );

			String line = null;
			int startpoint = 0;
			if(dataType.equals( "BFUL" )){
				startpoint = 6;
			}
			else if(dataType.equals( "BAVG" )){
				while( !((line = br.readLine()).equals( "START RADIATION RECORDS" ) ) ){
					startpoint++;
				}
			}

			while((line = br.readLine()) != null){

			}

		}
		catch(IOException ioe){
			System.out.println();
		}







		return list;
	}
}
