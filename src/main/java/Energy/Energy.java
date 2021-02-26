package Energy;

/**
 * Created by Alex Rosen on 8/8/2016.
 */
public class Energy {
	/**
	 * @param e0 : the initial energy of the particle when it was emitted
	 * @param rc : the radius of the cell
	 * @param rn : the radius of the nucleus of the cell
	 * @param dist : the distance from the center of the origin cell to the center of this cell
	 * @param maxDist : the maximum range that this particle at this energy can travel
	 */

	private static final int step = 1000;    // the number of steps to iterate over for each cell
	//public double dep_C_C = 0.0;
	//public double dep_Cs_C = 0.0;
	//public double dep_N_N = 0.0;
	//public double dep_Cs_N = 0.0;
	//public double dep_CY_N = 0.0;
	private double cellmass;
	private double xnuclmass;
	private double cytomass;
	private final double act = 1.0e-6;
	private final double conv = 7.508e-5;
	private int tst = 0;

	// screw reading in the file, im gonna be lazy (also this is both faster and will be a smaller file size in the end.  compile times be dammed (Alex Rosen 8/16/16)
	private static final double[][] coefPhoton = {
			// first column is energy in MeV and the rest is the coefficients for that energy
			{0.015, 3.1187801E-01, -1.7175846E-01, 6.5231289E-02, -1.4651157E-02, 1.9829754E-03, -1.6495732E-04, 8.4250159E-06, -2.5562337E-07, 4.2000887E-09, -2.8607554E-11},
			{0.020, 6.7244312E-01, -3.2219814E-01, 1.1910705E-01, -2.6496792E-02, 3.5705853E-03, -2.9632295E-04, 1.5112543E-05, -4.5809152E-07, 7.5217418E-09, -5.1206664E-11},
			{0.030, 1.6049021E+00, -3.7443278E-01, 1.3404206E-01, -2.9722587E-02, 4.0175878E-03, -3.3447682E-04, 1.7100141E-05, -5.1928160E-07, 8.5380989E-09, -5.8186664E-11},
			{0.040, 2.3258914E+00, 1.6755887E-01, 1.4082448E-02, -6.8449897E-03, 1.1886303E-03, -1.1062963E-04, 6.0027697E-06, -1.8892413E-07, 3.1797274E-09, -2.2023831E-11},
			{0.050, 2.6015869E+00, 8.7214986E-01, -4.9803181E-02, 5.3230159E-03, -2.7896369E-04, 7.1722051E-06, 5.6428014E-08, -9.1977777E-09, 2.2973861E-10, -1.9306663E-12},
			{0.060, 2.5636192E+00, 1.3126100E+00, -1.2297447E-02, 3.7005698E-03, -2.0809395E-04, 1.3803488E-05, -7.0410798E-07, 2.2711260E-08, -3.9936147E-10, 2.8902429E-12},
			{0.080, 2.2740654E+00, 1.4591243E+00, 1.2343571E-01, -5.1514559E-03, 7.4261826E-04, -4.4729504E-05, 1.8657546E-06, -4.9674830E-08, 7.5787695E-10, -4.9928964E-12},
			{0.100, 1.9944842E+00, 1.2912893E+00, 1.7504154E-01, -4.6931543E-03, 5.7474698E-04, -1.6654647E-05, 9.3820773E-09, 1.7482306E-08, -5.0704327E-10, 4.6485283E-12},
			{0.150, 1.6105426E+00, 9.0767161E-01, 1.7453811E-01, -6.9137843E-03, 8.3705211E-04, -4.6405745E-05, 1.8814298E-06, -5.0288247E-08, 7.7505308E-10, -5.1355447E-12},
			{0.200, 1.4156760E+00, 7.2018676E-01, 1.4124504E-01, -7.8670955E-03, 8.0505787E-04, -4.3429452E-05, 1.6003787E-06, -3.8876780E-08, 5.5534479E-10, -3.4904063E-12},
			{0.300, 1.1945598E+00, 5.3740039E-01, 9.4870113E-02, -9.7034674E-03, 1.0374319E-03, -6.5212691E-05, 2.5855268E-06, -6.3331138E-08, 8.7103041E-10, -5.1173023E-12},
			{0.400, 1.0772469E+00, 4.6034966E-01, 6.1001775E-02, -1.0744514E-02, 1.3505373E-03, -1.0114269E-04, 4.6452829E-06, -1.2825195E-07, 1.9452602E-09, -1.2393454E-11},
			{0.500, 9.9238222E-01, 4.1718270E-01, 3.3088575E-02, -8.7369286E-03, 1.2759176E-03, -1.0550458E-04, 5.2023283E-06, -1.5124722E-07, 2.3826137E-09, -1.5611191E-11},
			{0.600, 9.3207923E-01, 3.8918860E-01, 1.0862793E-02, -5.7619216E-03, 1.0139396E-03, -9.2681960E-05, 4.8885317E-06, -1.4935017E-07, 2.4448272E-09, -1.6519539E-11},
			{0.800, 8.4798975E-01, 3.5189649E-01, -2.3850962E-02, 1.0505992E-03, 1.8556544E-04, -3.1717948E-05, 2.1309767E-06, -7.4522243E-08, 1.3313360E-09, -9.5638605E-12},
			{1.000, 7.9276531E-01, 3.2093134E-01, -4.5320471E-02, 6.4806312E-03, -5.8020372E-04, 3.1937914E-05, -1.0606565E-06, 1.9962554E-08, -1.8192761E-10, 4.9467623E-13},
			{1.500, 7.1705743E-01, 2.4982775E-01, -6.3825660E-02, 1.2768833E-02, -1.5955824E-03, 1.2447039E-04, -6.0326459E-06, 1.7536365E-07, -2.7817859E-09, 1.8403903E-11},
			{2.000, 6.7488407E-01, 1.8814749E-01, -6.0323813E-02, 1.3251153E-02, -1.7658312E-03, 1.4458816E-04, -7.2745224E-06, 2.1774410E-07, -3.5352635E-09, 2.3830147E-11},
			{3.000, 6.0925391E-01, 1.0130699E-01, -4.1253766E-02, 9.9343531E-03, -1.3984767E-03, 1.1897410E-04, -6.1589190E-06, 1.8846267E-07, -3.1139299E-09, 2.1290227E-11}
	};
	private static final double[][] attCoef = {
			// E (MeV), mu(1/cm), me_en(1/cm)
			{0.015, 1.480, 1.2800},
			{0.020, 0.711, 0.5120},
			{0.030, 0.337, 0.1490},
			{0.040, 0.248, 0.0677},
			{0.050, 0.214, 0.0418},
			{0.060, 0.197, 0.0320},
			{0.080, 0.179, 0.0262},
			{0.100, 0.168, 0.0256},
			{0.150, 0.149, 0.0277},
			{0.200, 0.136, 0.0297},
			{0.300, 0.118, 0.0319},
			{0.400, 0.106, 0.0328},
			{0.500, .0966, 0.0330},
			{0.600, .0894, 0.0329},
			{0.800, .0785, 0.0321},
			{1.000, .0706, 0.0311},
			{1.500, .0575, 0.0284},
			{2.000, .0493, 0.0263},
			{3.000, .0396, 0.0233}
	};

	// public so that range can be calculated outside this file and there fore only once per particle rather than like 200 times
	public static final double[][] coefAlpha = {
			/* Energy in keV  */{1.00000E+00, 1.50000E+00, 2.00000E+00, 2.50000E+00, 3.00000E+00, 4.00000E+00, 5.00000E+00, 6.00000E+00, 7.00000E+00, 8.00000E+00, 9.00000E+00, 1.00000E+01, 1.25000E+01, 1.50000E+01, 1.75000E+01, 2.00000E+01, 2.25000E+01, 2.50000E+01, 2.75000E+01, 3.00000E+01, 3.50000E+01, 4.00000E+01, 4.50000E+01, 5.00000E+01, 5.50000E+01, 6.00000E+01, 6.50000E+01, 7.00000E+01, 7.50000E+01, 8.00000E+01, 8.50000E+01, 9.00000E+01, 9.50000E+01, 1.00000E+02, 1.25000E+02, 1.50000E+02, 1.75000E+02, 2.00000E+02, 2.25000E+02, 2.50000E+02, 2.75000E+02, 3.00000E+02, 3.50000E+02, 4.00000E+02, 4.50000E+02, 5.00000E+02, 5.50000E+02, 6.00000E+02, 6.50000E+02, 7.00000E+02, 7.50000E+02, 8.00000E+02, 8.50000E+02, 9.00000E+02, 9.50000E+02, 1.00000E+03, 1.25000E+03, 1.50000E+03, 1.75000E+03, 2.00000E+03, 2.25000E+03, 2.50000E+03, 2.75000E+03, 3.00000E+03, 3.50000E+03, 4.00000E+03, 4.50000E+03, 5.00000E+03, 5.50000E+03, 6.00000E+03, 6.50000E+03, 7.00000E+03, 7.50000E+03, 8.00000E+03, 8.50000E+03, 9.00000E+03, 9.50000E+03, 1.00000E+04},
			/* Range (micron) */{3.27300E-02, 4.78900E-02, 6.29400E-02, 7.78100E-02, 9.24700E-02, 1.21100E-01, 1.48900E-01, 1.75700E-01, 2.01600E-01, 2.26700E-01, 2.51000E-01, 2.74600E-01, 3.30700E-01, 3.83100E-01, 4.32500E-01, 4.79300E-01, 5.23800E-01, 5.66300E-01, 6.07100E-01, 6.46200E-01, 7.20400E-01, 7.90000E-01, 8.55600E-01, 9.17900E-01, 9.77200E-01, 1.03400E+00, 1.08900E+00, 1.14100E+00, 1.19200E+00, 1.24100E+00, 1.28900E+00, 1.33500E+00, 1.38100E+00, 1.42500E+00, 1.63000E+00, 1.81700E+00, 1.98900E+00, 2.15100E+00, 2.30400E+00, 2.45000E+00, 2.59000E+00, 2.72500E+00, 2.98300E+00, 3.23000E+00, 3.46700E+00, 3.69900E+00, 3.92600E+00, 4.15000E+00, 4.37200E+00, 4.59300E+00, 4.81300E+00, 5.03400E+00, 5.25600E+00, 5.47900E+00, 5.70400E+00, 5.93100E+00, 7.10700E+00, 8.37400E+00, 9.74400E+00, 1.12300E+01, 1.28200E+01, 1.45300E+01, 1.63500E+01, 1.82900E+01, 2.24900E+01, 2.71100E+01, 3.21500E+01, 3.75900E+01, 4.34400E+01, 4.96700E+01, 5.63000E+01, 6.33000E+01, 7.06700E+01, 7.84200E+01, 8.65300E+01, 9.50000E+01, 1.03800E+02, 1.13000E+02},
			/* de/dx(kev/mic) */{3.27100E+01, 3.30500E+01, 3.34200E+01, 3.38600E+01, 3.43500E+01, 3.54600E+01, 3.66700E+01, 3.79300E+01, 3.92100E+01, 4.04900E+01, 4.17700E+01, 4.30400E+01, 4.61500E+01, 4.91600E+01, 5.20500E+01, 5.48400E+01, 5.75200E+01, 6.01200E+01, 6.26300E+01, 6.50600E+01, 6.97100E+01, 7.41100E+01, 7.83000E+01, 8.23000E+01, 8.61200E+01, 8.98000E+01, 9.33300E+01, 9.67400E+01, 1.00000E+02, 1.03200E+02, 1.06300E+02, 1.09300E+02, 1.12200E+02, 1.15100E+02, 1.28100E+02, 1.39700E+02, 1.50000E+02, 1.59300E+02, 1.67700E+02, 1.75200E+02, 1.82000E+02, 1.88100E+02, 1.98500E+02, 2.06900E+02, 2.13400E+02, 2.18400E+02, 2.22000E+02, 2.24500E+02, 2.26000E+02, 2.26600E+02, 2.26600E+02, 2.26000E+02, 2.24800E+02, 2.23300E+02, 2.21500E+02, 2.19300E+02, 2.05200E+02, 1.89800E+02, 1.75400E+02, 1.62500E+02, 1.51200E+02, 1.41500E+02, 1.33000E+02, 1.25700E+02, 1.13300E+02, 1.03500E+02, 9.53500E+01, 8.85500E+01, 8.27500E+01, 7.77700E+01, 7.34000E+01, 6.95400E+01, 6.61200E+01, 6.30600E+01, 6.03000E+01, 5.78000E+01, 5.55200E+01, 5.34400E+01}
	};

	public Energy(double cellmass, double xnuclmass, double cytomass) {
		this.cellmass = cellmass;
		this.xnuclmass = xnuclmass;
		this.cytomass = cytomass;
	}

	public double[] getPhotonEnergy(double e0, double YEILD, double rc, double rn, double dist, String locale) {
		//TODO this needs to be fixed so that it works as it does in the fortran code and not like a particle
		double[] dep = new double[8];
		double dedr;

		double delta = 2.13 * e0 * YEILD/1000.0;

		double absf1 = 0.0;
		double absf2 = 0.0;
		double absf3 = 0.0;
		double absf4 = 0.0;
		double absf5 = 0.0;
		double absfr = 0.0;

		double dose1 = 0.0;
		double dose2 = 0.0;
		double dose3 = 0.0;
		double dose4 = 0.0;
		double dose5 = 0.0;
		double dose6 = 0.0;
		double dose7 = 0.0;
		double dose8 = 0.0;

		if(e0 <= .015 || e0 >= 3.0) {
			System.out.println( "Photon Energy not in range .015 < e0 < 3.0. No energy will be deposited." );
			return new double[]{0, 0, 0, 0, 0, 0, 0, 0};
		}

		// find what energy range youre in
		int k;
		for(k = 0; k < 19; k++) {
			if(e0 >= coefPhoton[k][0] && e0 <= coefPhoton[k + 1][0]) {
				break;
			}
		}

		double[] x = new double[5];
		double[] p = new double[5];
		double[] r = new double[5];
		double[] a = new double[5];
		p[0] = dist;
		r[0] = rc;
		a[0] = rn;
		for(int j = 1; j < 5; j++) {
			p[j] = p[j - 1] * p[0];
			r[j] = r[j - 1] * r[0];
			a[j] = a[j - 1] * a[0];
		}

		// get the energy deposited into the cell
		// range is always larger than the cluster so there is no edge case
		double dx = (4 * rc / step);
		for(double i = (dist - rc - rc) + dx/2D; i <= (dist + rc + rc); i += dx) {
			// did i go up an energy range?
			if(i >= coefPhoton[k + 1][0]) {
				k++;
			}

			// TODO ask dr howell if the XPHI equation is the correct equation for for dedr from the dose20.f file
			dedr = (e0 - coefPhoton[k][0]) / (coefPhoton[k + 1][0] - coefPhoton[k][0]) * (phi( i, k + 1 ) - phi( i, k )) + phi( i, k );

			// TODO this needs to be changed when i actually decide how the case is going ot work
			x[0] = i;
			for(int j = 1; j < 5; j++) {
				x[j] = x[j - 1] * i;
			}

			absf1 += dedr * PSI.getVpsi( x, p, r, r );// Cell to Cell
			absf2 += dedr * PSI.getSpsi( x, p, r, r );// Cell surface to Cell
			absf3 += dedr * PSI.getVpsi( x, p, a, a );// Nucleus to Nucleus
			absf5 += dedr * PSI.getSpsi( x, p, r, a );// Cell surface to Nucleus
			absfr += dedr * PSI.getVpsi( x, p, r, a );// Cytoplasm to Nucleus
		}

		absf1 *= dx / e0;
		absf2 *= dx / e0;
		absf3 *= dx / e0;
		absf5 *= dx / e0;
		absfr *= dx / e0;
		absf4 = (xnuclmass / cytomass) * ((cellmass / xnuclmass) * absfr - absf3);

		dose1 = conv * absf1 * delta * act / cellmass;
		dose2 = conv * absf2 * delta * act / cellmass;
		dose3 = conv * absf3 * delta * act / xnuclmass;
		dose4 = conv * absf4 * delta * act / xnuclmass;
		dose5 = conv * absf5 * delta * act / xnuclmass;
		dose6 = dose4;
		dose7 = (dose2 * cellmass - dose5 * xnuclmass) / cytomass;
		dose8 = (dose1 * cellmass * cellmass) / (cytomass * cytomass) - (2 * dose4 * xnuclmass) / cytomass - (dose3 * xnuclmass * xnuclmass) / (cytomass * cytomass);

		dep = new double[]{dose1, dose2, dose3, dose4, dose5, dose6, dose7, dose8};
		return dep;
	}

	protected static double phi(double i, int k) {
		final double RHO_M = 1.0; // TODO ask dr howell what this variable means
		double B = 0;
		for(int j = 0; j < 10; j++) {
			B += coefPhoton[k][j] * Math.pow( attCoef[k][1] * i, j + 1 );
		}
		double phi = attCoef[k][2] / RHO_M * Math.exp( -attCoef[k][1] * i ) * B / (4 * Math.PI * i * i);
		return phi;
	}

	public double[] getAlphaEnergy(double e0, double YEILD, double rc, double rn, double dist, double maxDist) {

		//System.out.println( "e0: " + e0 + "\tyeild: " + YEILD + "\trc: " + rc + "\trn: " + rn + "\tdist: " + dist );

		double range = 0.0;
		double dedr = 0.0;
		double[] dep;
		double delta = 2.13 * e0 * YEILD/1000.0;

		double absf1 = 0.0;
		double absf2 = 0.0;
		double absf3 = 0.0;
		double absf4 = 0.0;
		double absf5 = 0.0;
		double absfr = 0.0;

		double dose1;
		double dose2;
		double dose3;
		double dose4;
		double dose5;
		double dose6;
		double dose7;
		double dose8;

		// get the range of the particle
		// TODO move this outside this file
		range = AlphaRange( e0 );
		//System.out.println("Alpha range = " + range);
		if(dist == 0){
			return new double[]{0, 0, 0, 0, 0, 0, 0, 0};
		}
		if(range <= dist - rc - rc) {
			// if the particle does not travel far enough to reach the cell
			return new double[]{0, 0, 0, 0, 0, 0, 0, 0};
		}
		else {
			double start = dist - rc - rc;


			int k = 0;
			if(start <= coefAlpha[1][0]){
				k = 0;
			}
			else {
				for(k = 0; k < 78; k++) {
					if(coefAlpha[1][k] <= start && start < coefAlpha[1][k + 1]) {
						break;
					}
				}
			}
			//System.out.println( "Alex Rosen\tAlpha start=" + k );

			// variables for the geofactor. passing the arrays is a much faster process than passing each variable
			double[] x = new double[5];
			double[] p = new double[5];
			double[] r = new double[5];
			double[] a = new double[5];
			p[0] = dist;
			r[0] = rc;
			a[0] = rn;
			for(int j = 1; j < 5; j++) {
				p[j] = p[j - 1] * p[0];
				r[j] = r[j - 1] * r[0];
				a[j] = a[j - 1] * a[0];
			}

			double dx = (range > dist - rc - rc && range <= dist + rc + rc) ? (range - (dist - rc - rc)) / step : (4.0 * rc / step); // is this the end of the track or not
			for(double i = start+dx/2D; i <= range && i <= (dist + rc + rc); i += dx) {

				if(k < 78) {
					if(i >= coefAlpha[1][k + 1]) {
						k++;
					}
					dedr = coefAlpha[2][k] + (coefAlpha[2][k + 1] - coefAlpha[2][k]) * (i - coefAlpha[1][k]) / (coefAlpha[1][k + 1] - coefAlpha[1][k]);
				}
				else {
					System.out.println("Alpha has too much energy for the given data.  Using Max for the data.");
					dedr = coefAlpha[2][77];
				}

				x[0] = i;
				for(int j = 1; j < 5; j++) {
					x[j] = x[j - 1] * i;
				}

				absf1 += dedr * PSI.getVpsi( x, p, r, r );// Cell to Cell
				absf2 += dedr * PSI.getSpsi( x, p, r, r );// Cell surface to Cell
				absf3 += dedr * PSI.getVpsi( x, p, a, a );// Nucleus to Nucleus
				absf5 += dedr * PSI.getSpsi( x, p, r, a );// Cell surface to Nucleus
				absfr += dedr * PSI.getVpsi( x, p, r, a );// Cytoplasm to Nucleus

			}
			//System.out.println("absf1: " + absf1 + "\tabsf2: " + absf2 + "\tabsf3: " + absf3 + "\tabsf4: " + absf4 + "\tabsf5: " + absf5 + "\tabsfr: " + absfr + "\\\\\\\\\\\\\\\\\\\\\\\\\\");

			absf1 *= dx / e0;
			absf2 *= dx / e0;
			absf3 *= dx / e0;
			absf5 *= dx / e0;
			absfr *= dx / e0;
			absf4 = (xnuclmass / cytomass) * ((cellmass / xnuclmass) * absfr - absf3);
			//System.out.println("absf1: " + absf1 + "\tabsf2: " + absf2 + "\tabsf3: " + absf3 + "\tabsf4: " + absf4 + "\tabsf5: " + absf5 + "\tabsfr: " + absfr);

			dose1 = conv * absf1 * delta * act / cellmass;
			dose2 = conv * absf2 * delta * act / cellmass;
			dose3 = conv * absf3 * delta * act / xnuclmass;
			dose4 = conv * absf4 * delta * act / xnuclmass;
			dose5 = conv * absf5 * delta * act / xnuclmass;
			dose6 = dose4;
			dose7 = (dose2 * cellmass - dose5 * xnuclmass) / cytomass;
			dose8 = (dose1 * cellmass * cellmass) / (cytomass * cytomass) - (2 * dose4 * xnuclmass) / cytomass - (dose3 * xnuclmass * xnuclmass) / (cytomass * cytomass);

			//System.out.println(dose1 + "\t" + dose2 + "\t" + dose3 + "\t" + dose4 + "\t" + dose5 + "\t" + dose6 + "\t" + dose7 + "\t" + dose8 + "\t");

			dep = new double[]{dose1, dose2, dose3, dose4, dose5, dose6, dose7, dose8};
		}
		return dep;
	}

	public static double AlphaRange(double e0) {
		double range = 0.0;
		for(int i = 0; i < 78; i++) {
			if(e0 >= coefAlpha[0][i] && e0 < coefAlpha[0][i + 1]) {
				range = coefAlpha[1][i] + (coefAlpha[1][i + 1] - coefAlpha[1][i]) * (e0 - coefAlpha[0][i]) / (coefAlpha[0][i + 1] - coefAlpha[0][i]);
				break;
			}
		}
		return range;
	}

	public double[] getElectronEnergy(double e0, double YEILD, double rc, double rn, double dist, double maxDist) {
		//System.out.println( "e0: " + e0 + "\tyeild: " + YEILD + "\trc: " + rc + "\trn: " + rn + "\tdist: " + dist );
		double[] dep = new double[8];
		double dedr = 0.0;

		double delta = 2.13 * e0 * YEILD/1000.0;

		double absf1 = 0.0;
		double absf2 = 0.0;
		double absf3 = 0.0;
		double absf4 = 0.0;
		double absf5 = 0.0;
		double absfr = 0.0;

		double dose1;
		double dose2;
		double dose3;
		double dose4;
		double dose5;
		double dose6;
		double dose7;
		double dose8;

		// get the range of the particle
		// TODO move this outside this file so that the range only needs to be calculated once
		double range = electronRange( e0 );
		//System.out.println("8/17/2016 Electron with e0= " + e0 + " has range = " + range);

		if(dist == 0) {
			return new double[]{0, 0, 0, 0, 0, 0, 0, 0};
		}
		if(range < dist - rc - rc) {
			// if the particle does not travel far enough to reach the cell
			return new double[]{0, 0, 0, 0, 0, 0, 0, 0};
		}
		else {
			double start = dist - rc - rc;

			// variables for the geofactor. passing the arrays is a much faster process than passing each variable
			double[] x = new double[5];
			double[] p = new double[5];
			double[] r = new double[5];
			double[] a = new double[5];
			p[0] = dist;
			r[0] = rc;
			a[0] = rn;
			for(int j = 1; j < 5; j++) {
				p[j] = p[j - 1] * p[0];
				r[j] = r[j - 1] * r[0];
				a[j] = a[j - 1] * a[0];
			}

			double dx = (range > dist - rc - rc && range <= dist + rc + rc) ? (range - (dist - rc - rc)) / step : (4.0 * rc / step); // is this the end of the track or not
			for(double i = start+dx/2D; i <= range && i <= (dist + rc + rc); i += dx) {

				if(i < 0) {
					continue;
				}
				dedr = getElectronDEDR( range-i );// range - i because this takes the distance left in the track

				x[0] = i;
				for(int j = 1; j < 5; j++) {
					x[j] = x[j - 1] * i;
				}

				absf1 += dedr * PSI.getVpsi( x, p, r, r );// Cell to Cell;
				absf2 += dedr * PSI.getSpsi( x, p, r, r );// Cell surface to Cell;
				absf3 += dedr * PSI.getVpsi( x, p, a, a );// Nucleus to Nucleus;
				absf5 += dedr * PSI.getSpsi( x, p, r, a );// Cell surface to Nucleus;
				absfr += dedr * PSI.getVpsi( x, p, r, a );// Cytoplasm to Nucleus;
				//System.out.println("dedr: " + dedr + "\tdist: " + i + PSI1 + "\t" + PSI2 + "\t" + PSI3 + "\t" + PSI5 + "\t" + PSIr);

			}
			absf1 *= dx / e0;
			absf2 *= dx / e0;
			absf3 *= dx / e0;
			absf5 *= dx / e0;
			absfr *= dx / e0;
			absf4 = (xnuclmass / cytomass) * ((cellmass / xnuclmass) * absfr - absf3);

			dose1 = conv * absf1 * delta * act / cellmass; // C<-C
			dose2 = conv * absf2 * delta * act / cellmass; // C<-CS
			dose3 = conv * absf3 * delta * act / xnuclmass; // N<-N
			dose4 = conv * absf4 * delta * act / xnuclmass; // Cy<-N
			dose5 = conv * absf5 * delta * act / xnuclmass; // N<-CS
			dose6 = dose4; // Cy<-N
			dose7 = (dose2 * cellmass - dose5 * xnuclmass) / cytomass; // Cy<-CS
			dose8 = (dose1 * cellmass * cellmass) / (cytomass * cytomass) - (2 * dose4 * xnuclmass) / cytomass - (dose3 * xnuclmass * xnuclmass) / (cytomass * cytomass); // Cy<-Cy

			//System.out.println(absf1 + "\t" + absf2 + "\t" +absf3 + "\t" +absf4 + "\t" +absf5 + "\t" +absfr + "\t");
			//System.out.println(dose1 + "\t" + dose2 + "\t" + dose3 + "\t" + dose4 + "\t" + dose5 + "\t" + dose6 + "\t" + dose7 + "\t" + dose8 + "\t");

			dep = new double[] {dose1, dose2, dose3, dose4, dose5, dose6, dose7, dose8}; // yes I know that these are out of order
		}
		return dep;
	}

	public static double electronRange(double e0) {

		final double conv = .00001; // how close the e- range has to be to the real range
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

	public static double getElectronDEDR(double r) {

		final double a = 10.5, b = 1126.05, c = -9.251137e+05, d = 2.59298e+08, e = 4.96439e+10;
		double dedr = 0;

		if(r >= 0.02) {
			dedr = 3.3335 * Math.pow( (r + 0.007), (-0.435) ) + 0.0055 * Math.pow( r, 0.33 );
		}
		else if(r < 0.02 && r > 0.0038) {
			dedr = 29.5 - 666.67 * r;
		}
		else if(r > 0.0 && r <= 0.0038) {
			double r2 = r*r;
			double r3 = r2*r;
			double r4 = r3*r;
			dedr = a + b*r + c*r2 + d*r3 + e*r4;
		}
		else {

			dedr = 0.0;
		}

		return dedr;
	}
}
