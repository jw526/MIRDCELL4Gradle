package CrossDoseCal;

import File.MyList2;

import java.util.ArrayList;

/**
 * Created by Alex Rosen on 8/1/2016.
 */
public class RangeCalc {

	// gamma- and X- ray equations found at http://www.phys.hawaii.edu/~teb/NucRad.pdf

	private static final double conv = .00001;      // has to be this accurate

	public static double GammaRange() {
		// this will be way beyond the range of the cluster anyways
		return 5000;
	}

	public static double XrayRange(double en) {
		// this will be way beyond the range of the cluster anyways
		return 5000;
	}

	public static double AnnihilationQuantaRange(double en) {
		return 0.0;
	}

	public static double BetaPosRange(double en) {
		return 0.0;
	}

	public static double BetaNegRange(double e0) {
		double r;
		if(e0 > 0.6) {
			r = 0.0431 * Math.pow( (e0 + 0.367), 1.77 ) - 0.007;
			double dr = r / 100.0;
			double e = 0.0;
			boolean pos = ((e - e0) > 0.0);

			// this loop checks how much energy is used to obtain
			// a certain range and runs repeatedly until
			// you are with in (conv)*100 % of that range
			while((Math.abs( (e - e0) / e0 ) >= conv)) {
				e = 5.9 * Math.pow( (r + 0.007), 0.565 ) + 0.00413 * Math.pow( r, 1.33 ) - 0.367;
				if(pos) {
					if((e - e0) < 0.0) {
						pos = !pos;
						dr = -dr / 3.0;
					}
				}
				else {
					if((e - e0) > 0.0) {
						pos = !pos;
						dr = -dr / 3.0;
					}
				}
				r += dr;
			}
		}
		else if(e0 > 0.06 && e0 <= 0.6) {
			r = 1.523805e-03 + 0.038154 * e0 - 7.01803e-04 * Math.pow( e0, 2 ) + 0.036283 * Math.pow( e0, 3 );
		}
		else {
			r = 0.01233 * e0 + 2.25 * Math.pow( e0, 2 ) - 23.333 * Math.pow( e0, 3 );
		}
		return r;
	}

	public static double InternalConversionRange(double en) {
		return 0.0;
	}

	public static double AugerRange(double en) {
		// it is likely that this will never leave the cell any way so it doesnt matter here
		return 0.0;
	}

	public static double AlphaRange(double ekev, ArrayList<MyList2> list2) {
		double ener, rang, ded, range = 0.0;

		for(int i = 0; i < 78; i++) {
			MyList2 mList21 = list2.get( i );
			MyList2 mList22 = list2.get( i + 1 );
			System.out.println( "0701510 list2 line  " + i + ": " + mList21 );

			ener = Double.parseDouble( mList21.getLine1() );
			rang = Double.parseDouble( mList21.getLine2() );
			ded = Double.parseDouble( mList21.getLine3() ); // this is never used for some reason

			if(ekev == ener) {
				range = rang;
			}
			else if(ekev > ener && ekev < Double.parseDouble( mList22.getLine1() )) {
				range = rang + (Double.parseDouble( mList22.getLine2() ) - rang) * (ekev - ener) / (Double.parseDouble( mList22.getLine1() ) - ener);
			}
		}
		return range;
	}

	public static double DaughterRecoilRange(double en) {
		// this can likely be done with simple physics E = 1/2*m*v^2
		// it is likely that this will not matter for cross dose anyway though
		return 0.0;
	}

	public static double FisionFragmentRange(double en) {
		// this can likely be done with simple physics
		//      E = 1/2*m*v^2 to get initial velocity
		//      v = v0-ft for total range

		// it is likely that this will not matter for cross dose anyway though
		return 0.0;
	}

	public static double NeutronRange(double en) {
		return 0.0;
	}

}