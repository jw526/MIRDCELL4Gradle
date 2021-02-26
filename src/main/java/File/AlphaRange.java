/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.util.ArrayList;

/**
 * @author Johnny  Wu
 */
public class AlphaRange {

	ArrayListIn2 ali = new ArrayListIn2();
	MyList2 mList2 = new MyList2( "" );
	//ArrayList<MyList2> list = new ArrayList<MyList2>();
	String fileName = "";
	double ekev = 0, range = 0, ener, rang, ded;

	public AlphaRange() {
	}

	/**
	 * read in alpha-water file;
	 * get energy, range, ded values by row;
	 * <p/>
	 * first, compute alpha rage;
	 * second, call dedr1, compute dedr1 value.
	 * <p/>
	 * (this is not accurate any more (Alex Rosen))
	 */
	public double getAlphaRange(double ekev, ArrayList<MyList2> list2) {

		//try{
		//list.addAll(ali.readAlpha(fileName));

		//list = ali.readAlpha(fileName);
		// list2 total size == 79, why only run from 1-77 (why ignore the edge vals)
		for(int j = 1; j < 78; j++) {
			//list's size keeps doubling!!!!!!!!!!!!!!!!!!

			MyList2 mList21 = list2.get( j ); //0701 indexOutOfBound exception, index=1, size=0
			//System.out.println("0702 list2 alpha file size2: "+list2.size());
			System.out.println( "0701510 list2 line  " + j + ": " + mList21 );
			MyList2 mList22 = list2.get( j + 1 );
			ener = Double.parseDouble( mList21.getLine1() );
			rang = Double.parseDouble( mList21.getLine2() );
			ded = Double.parseDouble( mList21.getLine3() );
			if(ekev == ener) {
				range = rang;
			}
			else if(ekev > ener && ekev < Double.parseDouble( mList22.getLine1() )) {
				//range=rang(j)+(rang(j+1)-rang(j))*(ekev-ener(j))/(ener(j+1)-ener(j))
				range = rang + (Double.parseDouble( mList22.getLine2() ) - rang) * (ekev - ener) / (Double.parseDouble( mList22.getLine1() ) - ener);

			}
			//System.out.println("11/05/2010 alpha range = " +range);
		}

		//}catch(IOException ioe){
		//    System.out.println(ioe);
		//}
		return range;
	}

}
