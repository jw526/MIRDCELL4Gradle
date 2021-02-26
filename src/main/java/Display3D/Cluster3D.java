/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Display3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Behrooz
 */
public class Cluster3D {

	public int CellNumber = 0;
	public int Radius;
	public int Height;
	String Shape = "Sphere";
	int Dist = 0;

	public Cluster3D(int Cellnumber, String shape, int Distance) {
		// old constructor. TO BE DELETED
		this.CellNumber = Cellnumber;
		this.Shape = shape;
		this.Dist = Distance;
	}

	// Alex Rosen 7/22/2016
	public Cluster3D(int raduis, int height, int Cellnumber, String shape, int Distance) {
		this.CellNumber = Cellnumber;
		this.Height = height;
		this.Radius = raduis;
		this.Shape = shape;
		this.Dist = Distance;

		if(shape.equals( "Sphere" )) {
			this.Height = this.Radius;
			if(Radius != 0) {
				for(int x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
					for(int y = -(Radius / Dist) * Dist; y <= (Radius); y = y + Dist) {
						for(int z = -(Radius / Dist) * Dist; z <= (Radius); z = z + Dist) {
							if((x * x + y * y + z * z) <= (Radius * Radius)) {
								CellNumber++;
							}
						}
					}
				}
			}
			else if(CellNumber != 0) {
				int i = 0;
				int r;
				int cr = (Dist / 2);
				for(r = cr; i < CellNumber; r = (r + cr)) {
					i = 0;
					for(int x = -(r / Dist) * Dist; x <= (r); x = x + Dist) {
						for(int y = -(r / Dist) * Dist; y <= (r); y = y + Dist) {
							for(int z = -(r / Dist) * Dist; z <= (r); z = z + Dist) {
								if((x * x + y * y + z * z) <= (r * r)) {
									i++;
								}
							}
						}
					}
				}
				Radius = r - cr;
			}
		}
		else if(shape.equals( "Rod" )) {
			for(int y = 0; y <= (Height); y = y + Dist) {
				for(int x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
					for(int z = -(Radius / Dist) * Dist; z <= (Radius); z = z + Dist) {
						if(((x * x) + (z * z)) <= (Radius * Radius)) {
							CellNumber++;
						}
					}
				}
			}
		}
		else if(shape.equals( "Cone" )) {
			for(double y = Dist; y <= (Height); y = y + Dist) {
				for(double z = -(int) ((Radius * y) / (Height * Dist)) * Dist; z <= (Radius * y) / (Height); z = z + Dist) {
					for(double x = -(int) ((Radius * y) / (Height * Dist)) * Dist; x <= (Radius * y) / (Height); x = x + Dist) {
						if((x * x + z * z) / (y * y) <= ((double) (Radius * Radius) / (double) (Height * Height))) {
							CellNumber++;
						}
					}
				}
			}
		}
		else if(shape.equals( "Ellipsoid" )) {
			for(double x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
				for(double y = -(Height / Dist) * Dist; y <= (Height); y = y + Dist) {
					for(double z = -(Radius / Dist) * Dist; z <= (Radius); z = z + Dist) {
						if(((x * x) / (Radius * Radius) + (y * y) / (Height * Height) + (z * z) / (Radius * Radius)) <= 1) {
							CellNumber++;
						}
					}
				}
			}
		}
		else {/* this should never happen*/}
	}


	/**
	 * Alex Rosen 7/27/2016
	 * <p/>
	 * cell[i][0] = 1
	 * cell[i][1] = x coordinate
	 * cell[i][2] = x coordinate
	 * cell[i][3] = x coordinate
	 * cell[i][4] = {1,0} is this cell labeled or not respectively
	 * cell[i][5] = activity of radiological assigned to the cell, 0 if the cell is unlabeled
	 * cell[i][6] = activity * a constant for where the activity is located in the cell
	 * cell[i][7] = the dose of radiation that the cell gets from self and cross dose (this will be the total dose that the cell recieves from everything;
	 * //---  the below is not yet implemented  ---\\
	 * cell[i][8] = gamma-ray dose
	 * cell[i][9] = x-ray dose
	 * cell[i][10] = beta particles dose
	 * cell[i][11] = internal conversion electrons dose
	 * cell[i][12] = auger electron's dose (this has to be done so that it effects the nucleus much more that the cytoplasm)
	 * cell[i][13] = alpha particles dose
	 * cell[i][14] = daughter recoil dose
	 * cell[i][15] = Fission Fragment dose
	 * cell[i][16] = Neutron dose
	 */


	public double[][] generateSphere(int r) {
		System.out.println( CellNumber );

		//it is a sphere stupid; things need to be changed to sphere.
		double[][] cells = new double[CellNumber][8];
		int i = 0;
		if(Shape.equals( "Sphere" )) {

			int Radius = r;
			ArrayList<Double> rlist = new ArrayList<Double>();
			for(int xstep = 0; xstep <= Radius; xstep += Dist) {
				for(int x = -xstep; x <= xstep; x += 2 * (xstep == 0 ? 3 * Radius : xstep)) {
					for(int ystep = 0; ystep <= Radius; ystep += Dist) {
						for(int y = -ystep; y <= ystep; y += 2 * (ystep == 0 ? 3 * Radius : ystep)) {
							for(int zstep = 0; zstep <= Radius; zstep += Dist) {
								for(int z = -zstep; z <= zstep; z += 2 * (zstep == 0 ? 3 * Radius : zstep)) {
									rlist.add( 0.0 + x * x + y * y + z * z );
								}
							}
						}
					}
				}
			}
			Collections.sort( rlist );  //sort ascending
			double radiusCut = Radius * Radius;
			if(CellNumber < rlist.size()) {
				radiusCut = rlist.get( CellNumber - 1 );   //cells of small distance  have priority
			}
			for(int xstep = 0; xstep <= Radius; xstep += Dist) {
				for(int x = -xstep; x <= xstep; x += 2 * (xstep == 0 ? 3 * Radius : xstep)) {
					for(int ystep = 0; ystep <= Radius; ystep += Dist) {
						for(int y = -ystep; y <= ystep; y += 2 * (ystep == 0 ? 3 * Radius : ystep)) {
							for(int zstep = 0; zstep <= Radius; zstep += Dist) {
								for(int z = -zstep; z <= zstep; z += 2 * (zstep == 0 ? 3 * Radius : zstep)) {
									int dist2 = x * x + y * y + z * z;
									if(dist2 <= radiusCut && dist2 <= (Radius * Radius)) {
										if(i < CellNumber) {
											try {
												//System.out.println("i="+i + " cellNumber=" +CellNumber);
												cells[i][0] = 1;
												cells[i][1] = x;
												cells[i][2] = y;
												cells[i][3] = z;
												cells[i][4] = 0;
												cells[i][5] = 0.0;
												cells[i][6] = 0.0;
												cells[i][7] = 0.0;
											} catch(NullPointerException e) {
												System.out.println( i );
											}
										}
										//System.out.println("i++="+i + " cellNumber=" +CellNumber+ " Radius="+Radius + " Dist="+Dist);
										i++;
									}
								}
							}
						}
					}
				}
			}

		}

		return cells;
	}


	private void shuffle(ArrayList<Integer> ids) {
		Random rgen = new Random( 999 );   //fixed random number seed
		int len = ids.size();
		for(int i = 0; i < ids.size(); i++) {
			int randomPosition = rgen.nextInt( len );
			int temp = ids.get( i );
			ids.set( i, ids.get( randomPosition ) );
			ids.set( randomPosition, temp );
		}


	}

	public void put_cells(double[][] cells, int index, int x, int y, int z) {
		try {
			cells[index][0] = 1;
			cells[index][1] = x;
			cells[index][2] = y;
			cells[index][3] = z;
			cells[index][4] = 0;
			cells[index][5] = 0.0;
			cells[index][6] = 0.0;
			cells[index][7] = 0.0;
		} catch(NullPointerException e) {
			System.out.println( index );
		}
	}

	public double[][] generateCircle(int r, int cr) {
		System.out.println( CellNumber );

		double[][] cells = new double[CellNumber][8];
		int i = 0;

		if(Shape.equals( "Circle" )) {
			int Radius = r;
			cells[i][0] = 1;
			cells[i][1] = 0;
			cells[i][2] = 0;
			cells[i][3] = 0;
			cells[i][4] = 0;
			cells[i][5] = 0.0;
			cells[i][6] = 0.0;
			cells[i][7] = 0.0;
			i++;
			for(int k = cr; k <= (r / cr + 1) * cr; k = (k + cr)) {
				for(int x = -k; x <= k; x = x + cr) {
					for(int y = -k; y <= k; y = y + cr) {
						if((x * x + y * y) <= (k * k) && (x * x + y * y) <= (r * r) && (x * x + y * y) > ((k - cr) * (k - cr))) {
							if(i < CellNumber) {
								try {
									cells[i][0] = 1;
									cells[i][1] = x;
									cells[i][2] = y;
									cells[i][3] = 0;
									cells[i][4] = 0;
									cells[i][5] = 0.0;
									cells[i][6] = 0.0;
									cells[i][7] = 0.0;

								} catch(NullPointerException e) {
									System.out.println( i );
								}
							}
							i++;


						}
					}
				}

			}


		}

		return cells;

	}

	public double[][] generateEllipsoid(int r, int h) {
		System.out.println( CellNumber );
		double[][] cells = new double[CellNumber][8];
		int i = 0;
		double radius = Dist / 2;
		if(Shape.equals( "Ellipsoid" )) {
			double Radius = (double) r;
			int Height = h;
			for(double x = -((int) Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
				for(double y = -((int) Height / Dist) * Dist; y <= (Height); y = y + Dist) {
					for(double z = -((int) Radius / Dist) * Dist; z <= (Radius); z = z + Dist) {
						if(((x * x) / (Radius * Radius) + (y * y) / (Height * Height) + (z * z) / (Radius * Radius)) <= 1) {
							if(i < cells.length) {
								try {
									cells[i][0] = 1;
									cells[i][1] = x;
									cells[i][2] = y;
									cells[i][3] = z;
									cells[i][4] = 0;
									cells[i][5] = 0.0;
									cells[i][6] = 0.0;
									cells[i][7] = 0.0;
								} catch(NullPointerException e) {
									System.out.println( i );
								}
							}
							i++;

						}
					}
				}
			}
		}

		return cells;

	}

	public double[][] generateEllipse(int r, int h) {
		System.out.println( CellNumber );
		double[][] cells = new double[CellNumber][8];
		int i = 0;
		double radius = Dist / 2;
		if(Shape.equals( "Ellipse" )) {
			double Radius = (double) r;
			int Height = h;
			for(double x = -((int) Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
				for(double y = -((int) Height / Dist) * Dist; y <= (Height); y = y + Dist) {
					if(((x * x) / (Radius * Radius) + (y * y) / (Height * Height)) <= 1) {
						if(i < cells.length) {
							try {
								cells[i][0] = 1;
								cells[i][1] = x;
								cells[i][2] = y;
								cells[i][3] = 0;
								cells[i][4] = 0;
								cells[i][5] = 0.0;
								cells[i][6] = 0.0;
								cells[i][7] = 0.0;
							} catch(NullPointerException e) {
								System.out.println( i );
							}
						}
						i++;


					}
				}
			}
		}

		return cells;

	}

	public double[][] generateRectangle(int r, int h) {
		System.out.println( CellNumber );
		double[][] cells = new double[CellNumber][8];
		int i = 0;
		double radius = Dist / 2;
		if(Shape.equals( "Rectangle" )) {
			i = 0;
			double Radius = (double) r;
			double Height = (double) h;
			for(int x = (int) (-(Radius * .5 / Dist) * Dist); x <= (Radius * .5); x = x + Dist) {
				for(int y = (int) (-(Height * .5 / Dist) * Dist); y <= (Height * .5); y = y + Dist) {

					if(i < cells.length) {
						try {
							cells[i][0] = 1;
							cells[i][1] = x;
							cells[i][2] = y;
							cells[i][3] = 0;
							cells[i][4] = 0;
							cells[i][5] = 0.0;
							cells[i][6] = 0.0;
							cells[i][7] = 0.0;
						} catch(NullPointerException e) {
							System.out.println( i );
						}
					}
					i++;


				}
			}
		}

		return cells;

	}

	public double[][] generateRod(int r, int h) {
		System.out.println( CellNumber );
		double[][] cells = new double[CellNumber][8];
		int i = 0;
		double radius = Dist / 2;
		if(Shape.equals( "Rod" )) {
			int Radius = r;
			int Height = h;
			for(int y = 0; y <= (Height); y = y + Dist) {
				for(int x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
					for(int z = -(Radius / Dist) * Dist; z <= (Radius); z = z + Dist) {
						if(((x * x) + (z * z)) <= (Radius * Radius)) {
							if(i < cells.length) {
								try {
									cells[i][0] = 1;
									cells[i][1] = x;
									cells[i][2] = -y;
									cells[i][3] = z;
									cells[i][4] = 0;
									cells[i][5] = 0.0;
									cells[i][6] = 0.0;
									cells[i][7] = 0.0;
								} catch(NullPointerException e) {
									System.out.println( i );
								}
							}
							i++;

						}
					}
				}
			}
		}

		return cells;

	}

	public double[][] generateCone(int r, int h) {
		//System.out.println(CellNumber);
		double[][] cells = new double[CellNumber][8];
		int i = 1;
		double radius = Dist / 2;
		cells[0][0] = 1;
		cells[0][1] = 0;
		cells[0][2] = 0;
		cells[0][3] = 0;
		cells[0][4] = 0;
		cells[0][5] = 0.0;
		cells[0][6] = 0.0;
		cells[0][7] = 0.0;
		if(Shape.equals( "Cone" )) {
			double Radius = (double) r;
			int Height = h;
			for(double y = Dist; y <= (Height); y = y + Dist) {
				for(double z = -(int) ((Radius * y) / (Height * Dist)) * Dist; z <= (Radius * y) / (Height); z = z + Dist) {
					for(double x = -(int) ((Radius * y) / (Height * Dist)) * Dist; x <= (Radius * y) / (Height); x = x + Dist) {
						if((x * x + z * z) / (y * y) <= ((Radius * Radius) / (Height * Height))) {
							if(i < cells.length) {
								//System.out.println(i + " x" + x + " y" + y + " z" + z);
								try {
									cells[i][0] = 1;
									cells[i][1] = x;
									cells[i][2] = -y;
									cells[i][3] = z;
									cells[i][4] = 0;
									cells[i][5] = 0.0;
									cells[i][6] = 0.0;
									cells[i][7] = 0.0;
								} catch(NullPointerException e) {
									System.out.println( i );
								}
							}
							i++;

						}
					}
				}
			}
		}

		return cells;

	}
}
