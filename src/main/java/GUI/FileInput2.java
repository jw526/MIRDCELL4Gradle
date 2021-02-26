package GUI;

import java.io.*;
import java.util.*;

public class FileInput2
{
 /** Creates a new instance of FileReadIn */
       // String cellRadius = "0";
	//String nuclearRadius = "0";
	//int distance = 0;
 	String directory = "/CellDose/isotope";
        //file directory, change if neccessary
	//String directory = "D:/STUDY/java doc/CellDoseInterface/dist/radNucs/rc";
        //String directory = "radNucs/rc";
	String isotope = " ";
        
	String radiationType = " ";
        int time= 0;
        double yieldPerDecay= 0;
        double energy= 0;
        double delta= 0;
        
	
	int readPosition0 = 0;
	int gibberish = 0;
	File f;
        int maxRow;
  
	public FileInput2(String iso,  int line0)
	{
		//this.cellRadius = Integer.toString(rc);              
		
		this.isotope = iso;
		//this.readPosition = pos;
                this.readPosition0 = line0;
               
	}
        
	/**
     * if (tpye == ' BETA '' KCONV'. ' LCONV'.' MCONV'. .'KLXAUG' +'LMMAUG'. 'MXYAUG'. ' LLXCK'.+	'KLLAUG'. '  CONV'. 'LMXAUG'.     +		 
'LXYAUG'. 'KXYAUG'. ' MMXCK'.+ ' NNXCK'. 'NXYAUG'.' OOXCK'.' ALPHA'. 'NCONV'. ' OCONV')  {
    
        CellDoseCal cdCal = new CellDoseCal();  
     }
	     

     else if (' GAMMA'. 'KXALPH'. 'KXBETA'.'LXALPH'.'LXBETA'.+'MXALPH'.'NXALPH') {
        
           System.out.println(" Gamma & X-ray not allowed.");

    }
     */
	
        
        //void init() is used in button2 action in CellDose.java
	public void init() throws Exception
	{
		
                directory = directory.concat("/");
		directory = directory.concat(isotope+".dat");
  		//should add directory path change function
                System.out.println("directory="+directory);
		String firstEntry = "Radiat";	
		f = new File(directory);
		
                //testing point
                 System.out.println("f ="+f);
                System.out.println("f.exist="+f.exists());
		if(f.exists())
		{
			FileInputStream fisA = new FileInputStream(f);
			InputStreamReader isrA = new InputStreamReader(fisA);
			BufferedReader br = new BufferedReader(isrA);
			
                        
			StringTokenizer stA;
			String inputA, tokenA;
			boolean foundFirstEntry = false;
			//testing pointinputA 
                       // System.out.println("String inputA="+inputA);
			while( (inputA = br.readLine()) != null )
			{
				stA = new StringTokenizer(inputA);
                                System.out.println("StringTokenizer= "+stA.hasMoreTokens());
				while(stA.hasMoreTokens())
				{
					tokenA = stA.nextToken();
                                         System.out.println("token="+tokenA);
                                        //error
					if( tokenA.equals(firstEntry) )
					{
						foundFirstEntry = true;
					}
                                       
				}
				if(! foundFirstEntry)
				{
					gibberish++;
				}
                                
                               
			}
		}
                // System.out.println("token="+tokenA);
                 System.out.println("firstEntry="+firstEntry);
                 System.out.print("gibberish="+gibberish);
	}
	
        //void retrieve() is used in button2 action in CellDose.java
	public void retrieve() throws Exception
	{
		/**
                 *given first entry, find gibberish and offset
                
			offset = gibberish +2;
		}	
                 */
		int offset;
		
                
	        offset = gibberish + 2;
                int offset2 = gibberish + 1;
		System.out.println("offset="+offset);
                      		
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader rdr = new BufferedReader(isr);
			
		StringTokenizer st;
		String input, token, token1, token2, token3, token4;
                String maxRowToken = null, totalEnergy;
              
		int i = 0;
		//int j = 0;
		//int k = 0;
		
                //for read in isotope files, k = column, i = row, k (0, 4), i (0, maxIstop).
                //k=0, radiation type; k=1, number; k=2, decay; k=3, energy; k=4, delta.
		while( (input = rdr.readLine()) != null)
		{
			st = new StringTokenizer(input);
			
                        if( i == offset2 ) {
                            while(st.hasMoreTokens()) {
                               int j =0;
                               if( j== readPosition0) {
                                   
                                maxRowToken = st.nextToken();
                                maxRow = Integer.parseInt(maxRowToken);
                                totalEnergy = st.nextToken();
                                
                               }
                                System.out.println("row="+maxRowToken);
                            }
                           
                        }
			if( i == offset || i >offset && i < Integer.parseInt(maxRowToken) )
			{
                            System.out.println("i="+i);
                           
			    while(st.hasMoreTokens())
			    {
                                                             
                                int k=0;
                                if( k == readPosition0 ) {
                                    
					    token = st.nextToken();                                            
					    radiationType = token;
                                     
                                            token1 = st.nextToken();
                                            time = Integer.parseInt(token1);
 
                                            token2 = st.nextToken();                                          
                                            yieldPerDecay = Double.parseDouble(token2);
                                                                                    
                                            token3 = st.nextToken();                                          
                                            energy = Double.parseDouble(token3);
                                                                                      
                                            token4 = st.nextToken();                                         
                                            delta = Double.parseDouble(token4);
                                                                               
			        }
                                       // System.out.print("k="+k);
                                       //System.out.println("readPosition="+readPosition);
                                      System.out.println("radiationType="+radiationType);
                                      System.out.println("time="+time);
                                      System.out.println("yield="+yieldPerDecay);
                                      System.out.println("energy="+energy); 
                                      System.out.println("delta="+delta); 
                                                         
                           }//end while 
			}
			i++;	
            
		}
	}
	
	public int getGibberish()
	{
		return gibberish;
                
	}
		
	public String getRadiationType() {
            return radiationType;
        }
        
        public int getTime() {
            return time;
        }
        
        public double getYieldPerDecay() {
            return yieldPerDecay;
        }
        
        public double getEnergy() {
            return energy;
        }
        
        public double getDelta() {
            return delta;
        }
	
        //new max row function
        public int getMaxIstop() {
            return maxRow;
        }
 /**     
//error in get max istop
	public int getMaxIstop() throws Exception
	{
		String input, token = " ";
		StringTokenizer st;
		
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader rdr = new BufferedReader(isr);
		int i = 0;
		
		while( (input = rdr.readLine()) != null)
		{
			st = new StringTokenizer(input);
			if( i == (gibberish - 2) )
			{
				token = st.nextToken();
				break;
			}
			i++;
		}
		return Integer.parseInt(token);		
	}
       */
	
}
