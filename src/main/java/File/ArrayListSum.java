/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.util.ArrayList;

/**
 * 10/17/2010
 *
 * @author QQ
 */
public class ArrayListSum {

	ArrayList<ArrayList> list1 = new ArrayList<ArrayList>();


	int max1 = 0;

	public ArrayListSum(ArrayList<ArrayList> list) {
		this.list1 = list;
	}

	public int maxSize() {
		int max = list1.get( 0 ).size();
		//System.out.println("10/17/2010 max = "+max);
		//find max size of arrayList
		//System.out.println("10/20/2010 max test = "+max);
		for(int i = 0; i < list1.size(); i++) {
			//compare list elements' size
			//System.out.println("10/20/2010 i for max = "+i+"                  10/21/2010 max test3 = "+list1.get(i).size());
			// error found when find max value!!!!!! 10/20/2010

			if(list1.get( i ).size() > max) {
				max = list1.get( i ).size();
			}

			//System.out.println("10/20/2010 max test2 = "+max);
			//System.out.println("");

		}
		return max;
	}

	public ArrayList ListSum() {
		ArrayList sumList = new ArrayList();
		max1 = maxSize();
		//resize arraylist
		//System.out.println("10/20/2010 max test3 = "+max1);
		for(int m = 0; m < list1.size(); m++) {
			int k = list1.get( m ).size();
			if(k < max1) {
				for(; k < max1; k++) {
					list1.get( m ).add( k, "0.00" );
				}
			}

		}//end resize
		//System.out.println("1017/2010 list: "+list1);
		System.out.println( "10/17/2010 max = " + max1 );

		for(int j = 0; j < max1; j++) {
			double sum = 0.0;
			String s3 = "";
			//System.out.println("j="+j);
			//sort all elements from the list, find max j
			for(int i = 0; i < list1.size(); i++) {
				//System.out.println("i: "+i);
				//System.out.println("10/21/2010 s3 = "+ s3);
				s3 = list1.get( i ).get( j ).toString();
				sum += Double.valueOf( s3 );
				//System.out.println("1017/2010 s3: \n"+s3 );
			}
			sumList.add( sum );
			//System.out.println("1017/2010 sum list: \n"+ sumList );

		}// end loop j (distance)
		return sumList;
	}

}
