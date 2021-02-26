/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author QQ
 */
public class ArrayListTest {
    
    ArrayList<String[]> list = new ArrayList<String[]>();
    ArrayList<ArrayList> list1 = new ArrayList<ArrayList>();
    ArrayList list2 = new ArrayList();
    String[] s1 = {"1.0", "2.0"};
    String[] s2 = {"3.0", "4.0", "5.0"};
    String s3 = "";
    int max = 0;

    ArrayList l1 = new ArrayList();
    ArrayList l2 = new ArrayList();
    
    ArrayList sumList = new ArrayList();


    
    //10/17/2010 new loop for sum, j, i
    public ArrayListTest(){
        l1.add("1.0");
        l1.add("2.0");
        l2.add("3.0");
        l2.add("4.0");
        l2.add("5.0");
        l2.add("6.0");
        
        
        list1.add(l1);
        list1.add(l2);
        int maxSize = 0;
        int max = list1.get(0).size();
        //find max size of arrayList
        for(int i =0; i < list1.size(); i++){
            //compare list elements' size
            if(list1.get(i).size()> max){
               max = list1.get(i).size();
               
            }else{
                max = max;
            }
            
        }
        //resize arraylist
        int m =0;
        for(; m <list1.size();m++){
           for(int k = list1.get(m).size();k < max ;k++){
             list1.get(m).add("0.00");
           }
        }//end resize
        System.out.println("1017/2010 list: "+list1);
        System.out.println("10/17/2010 max = "+max);
        
        for(int j=0;j< max;j++){
            double sum =0.0;
            System.out.println("j="+j);
            //sort all elements from the list, find max j
            for(int i=0;i<list1.size();i++){
                System.out.println("i: "+i);
                s3 = list1.get(i).get(j).toString();
                sum += Double.valueOf(s3);
                System.out.println("1017/2010 s3: \n"+s3 );

                //sum = Double.valueOf(s3)++;
                               //int sum = list1.get(i).getLine(j)++;
                               //String column = sum.toString();
            }
            sumList.add(sum);
            System.out.println("1017/2010 sum list: \n"+ sumList );
                           //Arraylist newList;
                           //newList.append(column);
        }
    }


    public static void main(String[] args){
        new ArrayListTest();
    }

}
