package Energy;
/**
 * Created by Alex Rosen on 8/17/2016.
 */
public class PSI {

	public static double getVpsi(double[] x, double[] p, double[] r, double[] a) {
		//x[n] = x^(n+1)     |  same for p, r, a

		double vpsiGeoFactor;

		if(x[0] >= (p[0] - r[0] - a[0]) && x[0] <= (p[0] - r[0] + a[0])) {
			vpsiGeoFactor = (x[4] - 5.0D * p[0] * x[3] + 10.0D * x[2] * (-r[1] + p[1] - a[1]) + 10.0D * x[1] * (3.0D * a[1] * p[0] - 2.0D * a[2]) + x[0] * (30.0D * a[1] * r[1] - 30.0D * a[1] * p[1] + 40.0D * a[2] * p[0] - 15.0D * a[3]) + 20.0D * a[2] * r[1] - 20.0D * a[2] * p[1] + 15.0D * a[3] * p[0] - 4.0D * a[4] - x[1] * (20.0D * r[2] - 30.0D * p[0] * r[1] + 10.0D * p[2]) - x[0] * (15.0D * r[3] - 40.0D * p[0] * r[2] + 30.0D * p[1] * r[1] - 5.0D * p[3]) - 4.0D * r[4] + 15.0D * p[0] * r[3] - 20.0D * r[2] * (p[1] - a[1]) - r[1] * (30.0D * a[1] * p[0] - 10.0D * p[2]) - p[4] + 10.0D * a[1] * p[2]) / (160.0D * p[0] * x[0] * r[2]);
		}
		else if(x[0] >= (p[0] - r[0] + a[0]) && x[0] <= (p[0] + r[0] - a[0]) && r[0] != a[0]) {
			vpsiGeoFactor = (a[2] / (20.0D * p[0] * x[0] * r[2])) * (-5.0D * x[1] + 10.0D * p[0] * x[0] + 5.0D * r[1] - 5.0D * p[1] - a[1]);
		}
		else if(x[0] >= (p[0] + r[0] - a[0]) && x[0] <= (p[0] + r[0] + a[0])) {
			vpsiGeoFactor = (-x[4] + 5.0D * p[0] * x[3] - 10.0D * x[2] * (-r[1] + p[1] - a[1]) - 10.0D * x[1] * (3.0D * a[1] * p[0] + 2.0D * a[2]) - x[0] * (30.0D * a[1] * r[1] - 30.0D * a[1] * p[1] - 40.0D * a[2] * p[0] - 15.0D * a[3]) + 20.0D * a[2] * r[1] - 20.0D * a[2] * p[1] - 15.0D * a[3] * p[0] - 4.0D * a[4] - x[1] * (20.0D * r[2] + 30.0D * p[0] * r[1] - 10.0D * p[2]) - x[0] * (-15.0D * r[3] - 40.0D * p[0] * r[2] - 30.0D * p[1] * r[1] + 5.0D * p[3]) - 4.0D * r[4] - 15.0D * p[0] * r[3] - 20.0D * r[2] * (p[1] - a[1]) - r[1] * (-30.0D * a[1] * p[0] + 10.0D * p[2]) + p[4] - 10.0D * a[1] * p[2]) / (160.0D * p[0] * x[0] * r[2]);
		}
		else {
			vpsiGeoFactor = 0.0D;
		}
		//System.out.println(vpsiGeoFactor);

		if(vpsiGeoFactor > 0){
			return vpsiGeoFactor;
		}
		else{
			return 0.0;
		}
	}

	public static double getSpsi(double[] x, double[] p, double[] r, double[] a) {
		//x[n] = x^(n+1)     |  same for p, r, a
		double spsiGeoFactor = 0;

		// SURFACE TO NUCLEUS
		if((x[0] >= (p[0] - r[0] - a[0])) && (x[0] <= (p[0] - r[0] + a[0]))) {
			spsiGeoFactor = (-x[2] - 3.0 * x[1] * (r[0] - p[0]) - 3.0 * x[0] * (-a[1] + r[1] - 2.0 * p[0] * r[0] + p[1]) - r[2] + 3.0 * p[0] * r[1] - 3.0 * r[0] * (p[1] - a[1]) + p[2] + 2.0 * a[2] - 3.0 * a[1] * p[0]) / (24.0 * p[0] * r[0] * x[0]);
		}
		else if((x[0] >= (p[0] - r[0] + a[0])) && (x[0] <= (p[0] - a[0] + r[0])) && (r[0] != a[0])) {
			spsiGeoFactor = a[2] / (6.0 * p[0] * r[0] * x[0]);
		}
		else if((x[0] >= (p[0] - a[0] + r[0])) && (x[0] <= (p[0] + a[0] + r[0]))) {
			spsiGeoFactor = (x[2] - 3.0 * x[1] * (r[0] + p[0]) - 3.0 * x[0] * (a[1] - r[1] - 2.0 * p[0] * r[0] - p[1]) - r[2] - 3.0 * p[0] * r[1] - 3.0 * r[0] * (p[1] - a[1]) - p[2] + 2.0 * a[2] + 3.0 * a[1] * p[0]) / (24.0 * p[0] * r[0] * x[0]);
		}
		else {
			spsiGeoFactor = 0.0;
		}

		if(spsiGeoFactor > 0){
			return spsiGeoFactor;
		}
		else{
			return 0.0;
		}

	}

}
