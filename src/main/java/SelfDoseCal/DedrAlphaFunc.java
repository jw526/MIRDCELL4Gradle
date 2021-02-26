/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SelfDoseCal;

/**
 * @author Johnny  Wu
 */
public class DedrAlphaFunc {
	double r;

	public DedrAlphaFunc() {

	}

	public double getDedrAlFunc(double r) {

		double dedrAlpha = 0;
		/**
		 *  FUNCTION DEDRal (R) IMPLICIT REAL*8 (A-H,O-Z) COMMON /ELECTRON/ XRANGE, EKEV
		 *  if(r.gt.0.0) then
		 *      DEDRal = 260.0/r**(0.333333)
		 *  else
		 *      dedral=0.0
		 *  end if
		 */
		if (r > 0) {
			dedrAlpha = Math.pow(260.0D/r, 1.0D/3.0D);
			// tried changing the last num form 3 to 2 to see if this changes the output for just an electron
			// didnt work
		}
		else{
			dedrAlpha = 0;
		}
		return dedrAlpha;
	}

}
