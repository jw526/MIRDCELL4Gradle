/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Display3D;

/**
 *
 * @author Behrooz
 */
public class Point3D {
   public int x, y, z;
   public Point3D( int X, int Y, int Z ) {
      x = X;  y = Y;  z = Z;
   }
   public int getDistance(Point3D p)
   {
   double r = Math.sqrt(Math.pow((this.x-p.x), 2)+Math.pow((this.y-p.y), 2)+Math.pow((this.z-p.z), 2))   ; 
   return (int) Math.round(r);
   }
}
