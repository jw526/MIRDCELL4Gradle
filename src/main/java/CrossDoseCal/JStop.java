/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

/**
 * @author Johnny  Wu
 */
public class JStop {
	int jstop = 0, jstopmax = 0;
	double range = 0;
	int maxabs = 130;

	public JStop() {

	}

	public int getJStop(double range, double rc, double rn) {
		//behrooz
		if (((range + rn + rc) + 5) < (double) (maxabs)) {
			jstop = (int) ((range + rn + rc) + 5);
		} else {
			jstop = maxabs;
		}
		if (jstop > jstopmax) {
			jstopmax = jstop;
		}
		return jstopmax;
	}

	//Behrooz June 2012

	public void SetMax(int MaxDist) {
		this.maxabs = MaxDist;
	}


}
