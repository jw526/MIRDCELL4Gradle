package Graphics;


import java.awt.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Johnny  Wu
 */
//isotope slection function
public class IsotopePanel {
        
        List l;
        String[] isotopeFileNames = {"ac225","at211","bi204","bi206",
	"bi210","bi211","bi212","bi213", "bi214","c14","cr51","cu64","cu67",
	"f18","fe55","fe59", "ga67","gd148","h3","i123","i125","i-131","in111",
	"in114","in114m","lu177","p32","p33","pb203","pb204m","pb209","pb210",
	"pb211","pb212","pb214","po209","po210","po211","po212","po213","po214","po215","po216","po218","pt193m","pt195m","ra223",
	"ra224","ra226","re186","re188","rn222","s35","sm153","sn117m","sr89",
	"sr90","tc99m","tl200","tl201","tl202","tl206","tl207","tl208","tl209",
	"tl210","w188","y88","y90","y91"};
	
	String[] isotopes = {"Ac-225","At-211","Bi-204","Bi-206","Bi-210",
	"Bi-211","Bi-212","Bi-213","Bi-214","C-14","Cr-51","Cu-64","Cu-67",
	"F-18","Fe-55","Fe-59","Ga-67","Gd-148","H-3","I-123","I-125","I-131",
	"In-111","In-114","In-114m","Lu-177","P-32","P-33","Pb-203","Pb-204m",
	"Pb-209","Pb-210","Pb-211","Pb-212","Pb-214","Po-209","Po-210","Po-211","Po-212","Po-213","Po-214","Po-215","Po-216","Po-218","Pt-193m",
	"Pt-195m","Ra-223","Ra-224","Ra-226","Re-186","Re-188","Rn-222","S-35",
	"Sm-153","Sn-117m","Sr-89","Sr-90","Tc-99m","Tl-200","Tl-201","Tl-202",
	"Tl-206","Tl-207","Tl-208","Tl-209","Tl-210","W-188","Y-88","Y-90","Y-91"};
		
	public IsotopePanel() {
		l = new List();
		
		for(int i = 0; i<isotopes.length;i++) {
			l.add(isotopes[i]);
		}
		
		l.select(0);
                l.setFont(new java.awt.Font("Tahoma", 1, 12));
		ScrollPane sp = new ScrollPane();
		sp.add(l);
		sp.setLocation(30, 520);
                sp.setSize(126, 135);
		
	}
	
	public String getSelectedIsotopeFileName() {		
		return isotopeFileNames[l.getSelectedIndex()];
	}
	
	public String getSelectedIsotope() {
		return isotopes[l.getSelectedIndex()];
	}
	
	public List getList() {
		return l;
	}
}       
