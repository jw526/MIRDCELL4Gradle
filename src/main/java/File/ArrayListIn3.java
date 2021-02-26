package File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alex Rosen on 8/18/2016.
 */
public class ArrayListIn3 {

	private static final String MIRDdir = "https://mirdcell.njms.rutgers.edu/UMDNJ/MIRD/RAD/";
	private static final String dir = "https://mirdcell.njms.rutgers.edu/UMDNJ/DATA%20FILES/data/";

	public static ArrayList<double[]> readMIRDdata(String isotope, boolean user) {
		// read data for the beta average and the user
		System.out.println("entering readMIRDdata");
		ArrayList<double[]> data = new ArrayList<double[]>();
		String directoryMIRD = MIRDdir.concat( isotope ).concat( ".RAD" );
		double[] datums = new double[3];
		try {
			URL u = new URL( directoryMIRD );
			System.out.println( "8/18/2016 MIRD url = " + u );
			URLConnection uConn = u.openConnection();
			uConn.setDoInput( true );
			uConn.setUseCaches( false );

			BufferedReader br = new BufferedReader( new InputStreamReader( uConn.getInputStream() ) );
			String input = "";
			String[] split;
			int x = 0;
			while(!(input = br.readLine()).equals( "START RADIATION RECORDS" )) {
			}     // read until the start of the radiation records
			while(!(input = br.readLine()).equals( "END RADIATION RECORDS" )) {
				System.out.println(input);
				split = input.trim().split( "\\s+" );
				for(int i = 0; i < 3; i++) {
					datums[i] = Double.parseDouble( split[i] );
				}
				data.add(new double[]{Double.parseDouble( split[0] ), Double.parseDouble( split[1] ), Double.parseDouble( split[2] )});
			}
			br.close();
		} catch(MalformedURLException e) {
			System.err.println( "*** MalformedURLException ***\n" + e.getMessage() );

		} catch(IOException e) {
			System.err.println( "*** IOexception ***\n" + e.getMessage() );
		}

		return data;
	}

	public static ArrayList<double[]> readOTHERdata(String isotope) {
		System.out.println("entering readOTHERdata");
		// beta full energy spectrum
		// TODO as dr howell what the val Delta(g-rad/mic.C-h) is / is needed for
		ArrayList<double[]> data = new ArrayList<double[]>();
		String directory = dir.concat( isotope );
		double[] datums = new double[4];
		int numDecays;
		if(!isotope.contains( ".dat" ) && !isotope.contains( ".out" ) && !isotope.contains( ".MIRD" )) {
			directory = directory.concat( ".dat" );
		}

		try {
			URL u = new URL( directory );
			System.out.println( "8/18/2016 nonMIRD url = " + u );
			URLConnection uConn = u.openConnection();
			uConn.setDoInput( true );
			uConn.setUseCaches( false );

			BufferedReader br = new BufferedReader( new InputStreamReader( uConn.getInputStream() ) );
			String input = "";
			String[] split;

			//laziness for the win
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			split = br.readLine().trim().split( "\\s+" );
			numDecays = Integer.parseInt( split[0] );
			for(int i = 0; i < numDecays; i++) {
				split = br.readLine().trim().split( "\\s+" );
				for(int j = 0; j < 4; j++) {
					datums[j] = Double.parseDouble( split[j+1] );
				}
				data.add(Arrays.copyOf(datums, 4)); //Use array.copyOf to copy array, as array is object
			}
			br.close();

		} catch(MalformedURLException e) {
			System.err.println( "*** MalformedURLException ***\n" + e.getMessage() );
		} catch(IOException e) {
			System.err.println( "*** IOexception ***\n" + e.getMessage() );
		}

		return data;
	}
}