package File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JTextArea;

/**
 * @author Johnny  Wu
 */
public class ArrayListIn2 {

	//ArrayList<MyList> al = new ArrayList<MyList>();
	ArrayList<MyList> al = new ArrayList<MyList>();
	ArrayList<MyList2> list2 = new ArrayList<MyList2>();
	MyList ml = new MyList( "" );
	MyList2 mList2 = new MyList2( "" );
	File f;
	//String directory = "/DATA FILES/data/";
	String directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/DATA%20FILES/data/"; //04/01
	String directoryMIRD = "https://mirdcell.njms.rutgers.edu/UMDNJ/MIRD/RAD/";
	// 10/22/2010
	String directoryUser = "https://mirdcell.njms.rutgers.edu/User/"; //04/01

	String isotope = " ";
	int rc = 0, rn = 0;
	JTextArea jTextArea1;

	//04/01
	URL url;
	URLConnection urlConn;

	public ArrayListIn2(String iso) {
		//this.isotope = iso.toLowerCase(Locale.ENGLISH);
		this.isotope = iso; // 11/17/2010
	}

	public ArrayListIn2() {

	}

	//07202010
	public ArrayListIn2(JTextArea jTextArea1) {
		this.jTextArea1 = jTextArea1;
	}

	public ArrayList<MyList> read() throws IOException {

		directory = directory.concat( isotope );
		directoryMIRD = directoryMIRD.concat( isotope );

		System.out.println( "07/21/09 isofilename=" + isotope );
		//f revise by 07012010
		if(isotope.contains( ".dat" )) {
			// f = new File(directory);
		}
		//08/24/09 read in new data from MIRD CD
		else if(isotope.contains( ".out" ) || isotope.contains( ".MIRD" )) {
			// f = new File(directoryMIRD);
		}
		else {
			directory = directory.concat( ".dat" );
			//f = new File(directory);
		}
		System.out.println( "10/22/10 isofilename=" + directory );

		try {

			//04/01 add URL connection
			url = new URL( directory );
			System.out.println( "0401 url: " + url );
			urlConn = url.openConnection();
			urlConn.setDoInput( true );
			urlConn.setUseCaches( false );

			/**
			 InputStream in = url.openStream();
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String line = null;
			 while((line = br.readLine()) != null ) {
			 al.add(new MyList(line));
			 }
			 */
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							urlConn.getInputStream() ) );
			String inputLine = "";

			while((inputLine = br.readLine()) != null) {
				al.add( new MyList( inputLine ) );
			}
			br.close();

			/**
			 *
			 *FileInputStream fis = new FileInputStream(f);
			 BufferedReader br= new BufferedReader(new InputStreamReader(fis));
			 String line = null;
			 while((line = br.readLine()) != null ) {
			 al.add(new MyList(line));
			 }
			 //System.out.println(al);
			 br.close();
			 */
		} catch(IOException e) {
			System.err.println( "*** IOexception ***" + e.getMessage() );
		}
		//testing ArrayList output
		// System.out.println(al+"\n"+"size = "+al.size());


		return al;
	}//end read

	public ArrayList read2(int rc, int rn) throws IOException {
		String directory2 = "/output/";
		directory2 = directory2.concat( isotope + "." + rc + rn );
		f = new File( directory2 );
		System.out.println( f );
		if(f.exists()) {
			try {
				FileInputStream fis = new FileInputStream( f );
				BufferedReader br = new BufferedReader( new InputStreamReader( fis ) );
				String line = null;
				while((line = br.readLine()) != null) {
					al.add( new MyList( line ) );
				}
				//System.out.println(al);
				br.close();
			} catch(IOException e) {
				System.err.println( "*** IOexception ***" + e.getMessage() );
			}
			//testing ArrayList output
			//System.out.println(al+"\n"+"size = "+al.size());
		}//end if

		return al;
	}//end read

	public ArrayList<MyList2> readAlpha(String alphaFileName) {
		String directory3 = "https://mirdcell.njms.rutgers.edu/UMDNJ/";
		directory3 = directory3.concat( alphaFileName );

		//07012010 revise urlconn
		try {
			url = new URL( directory3 );
			System.out.println( "0701 alpha url: " + url );
			urlConn = url.openConnection();
			urlConn.setDoInput( true );
			urlConn.setUseCaches( false );

			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							urlConn.getInputStream() ) );
			String inputLine = "";

			while((inputLine = br.readLine()) != null) {
				System.out.println( inputLine );
				list2.add( new MyList2( inputLine ) );
			}//07/01
			br.close();

		} catch(IOException e) {
			System.err.println( "*** IOexception ***" + e.getMessage() );
		}
		/** revise 07012010
		 f = new File(directory3);

		 if(f.exists()) {
		 try {
		 FileInputStream fis = new FileInputStream(f);
		 BufferedReader br= new BufferedReader(new InputStreamReader(fis));
		 String line = null;
		 while((line = br.readLine()) != null ) {
		 list2.add(new MyList2(line));
		 }
		 //System.out.println(al);
		 br.close();
		 } catch (IOException e) {
		 System.err.println("*** IOexception ***" + e.getMessage());
		 }
		 //testing ArrayList output
		 // System.out.println(al+"\n"+"size = "+al.size());
		 }//end if
		 */

		return list2;
	}//end read

	//072010 get jTextArea1 max line
	public int getMaxRow() {
		return jTextArea1.getRows();
	}

	public int getMaxRow(int rc, int rn) {
		String directory2 = "https://mirdcell.njms.rutgers.edu/UMDNJ/OUTPUT/";
		directory2 = directory2.concat( isotope + "." + rc + rn );
		f = new File( directory2 );
		System.out.println( f );
		if(f.exists()) {
			try {
				FileInputStream fis = new FileInputStream( f );
				BufferedReader br = new BufferedReader( new InputStreamReader( fis ) );
				String line = null;
				while((line = br.readLine()) != null) {
					al.add( new MyList( line ) );
				}
				//System.out.println(al);
				br.close();
			} catch(IOException e) {
				System.err.println( "*** IOexception ***" + e.getMessage() );
			}
			//testing ArrayList output
			//System.out.println(al+"\n"+"size = "+al.size());
		}//end if
		return al.size();
	}

	public void read3(int rc, int rn, JTextArea jTextArea1) throws IOException {
		String directory2 = "https://mirdcell.njms.rutgers.edu/UMDNJ/OUTPUT/";
		directory2 = directory2.concat( isotope + "." + rc + rn );
		String line = null;
		f = new File( directory2 );
		System.out.println( f );
		if(f.exists()) {
			try {
				FileInputStream fis = new FileInputStream( f );
				BufferedReader br = new BufferedReader( new InputStreamReader( fis ) );

				while((line = br.readLine()) != null) {
					//return line;
					jTextArea1.append( line + "\r\n" );
				}
				//System.out.println(al);
				br.close();
			} catch(IOException e) {
				System.err.println( "*** IOexception ***" + e.getMessage() );
			}
			//testing ArrayList output
			//System.out.println(al+"\n"+"size = "+al.size());
		}//end if
		//return line;
	}//end read method

	//08/05/09
	//read from user's temp dir
	public ArrayList readUser() throws IOException {
		String dirUser = "/User/";
		dirUser = dirUser.concat( isotope );
		System.out.println( "08/05/09 isofilename=" + isotope );
		System.out.println( "08/05/09 user dir=" + dirUser );
		if(isotope.contains( ".dat" )) {
			f = new File( dirUser );
		}
		else {
			dirUser = dirUser.concat( ".dat" );
			f = new File( dirUser );
		}
		System.out.println( "08/05/09 user file exist =" + f.exists() );
		if(f.exists()) {
			try {
				FileInputStream fis = new FileInputStream( f );
				BufferedReader br = new BufferedReader( new InputStreamReader( fis ) );
				String line = null;
				while((line = br.readLine()) != null) {
					al.add( new MyList( line ) );
				}
				//System.out.println(al);
				br.close();
			} catch(IOException e) {
				System.err.println( "*** IOexception ***" + e.getMessage() );
			}
			//testing ArrayList output
			// System.out.println(al+"\n"+"size = "+al.size());
		}//end if

		return al;
	}//end read

	/*******************
	 * 10/29/2010 revised read MIRD new file function
	 * read new MIRD file .RAD and store as ArrayList
	 */
	public ArrayList<MyList> readMIRD() {

		directoryMIRD = directoryMIRD.concat( isotope ).concat( ".RAD" );
		System.out.println( "10/29/10 MIRD filename=" + directoryMIRD );

		//System.out.println("f exists? : "+ f.exists());
		try {
			//04/01 add URL connection
			url = new URL( directoryMIRD );
			System.out.println( "10/29/10 MIRD url: " + url );
			urlConn = url.openConnection();
			urlConn.setDoInput( true );
			urlConn.setUseCaches( false );

			BufferedReader br = new BufferedReader( new InputStreamReader( urlConn.getInputStream() ) );
			String inputLine = "";
			while((inputLine = br.readLine()) != null) {
				// System.out.println(inputLine);
				al.add( new MyList( inputLine ) );
			}
			br.close();

		} catch(IOException e) {
			System.err.println( "*** IOexception ***" + e.getMessage() );
		}
		return al;
	}//end read new MIRD file



}//end class
