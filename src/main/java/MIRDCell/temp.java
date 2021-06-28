/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MIRDCell;

/**
 *
 * @author jianchao
 */
import com.skew.slsqp4j.OptimizeResult;
import com.skew.slsqp4j.Slsqp;
import com.skew.slsqp4j.constraints.ConstraintType;
import com.skew.slsqp4j.constraints.VectorConstraint;
import com.skew.slsqp4j.functions.Vector2ScalarFunc;
import com.skew.slsqp4j.functions.Vector2VectorFunc;
import java.util.Arrays;

/**
 *
 * @author jianchao
 */
public class temp {
public static final class VectorConstraintFunction implements Vector2VectorFunc
    {
        @Override
        public double[] apply(double[] x, double... arg)
        {   
            //x is specific acti. for each drug.
            double tgtSF = 0.01; //arg[0];
            int numcells = 1000; //(int)arg[1];
            double[] doses = new double[numcells];
            double[] probs = new double[numcells];
            double prob_value = Arrays.stream(probs).average().getAsDouble();
            return new double[] {prob_value - tgtSF};
        }
    }
    public static final class VectorObjectiveFunction implements Vector2ScalarFunc
    {

        public double apply(double[] x, double... arg) {
            return  Arrays.stream(x).sum();
        }
        
    }
    public static void main(String[] args) {
     int numDrugs = 4;
     double[] q = new double[numDrugs];
        
     final VectorConstraint constraint = new VectorConstraint.VectorConstraintBuilder()
        .withConstraintType(ConstraintType.EQ)
        .withConstraintFunction(new VectorConstraintFunction())
        .build();
    final Slsqp slsqp = new Slsqp.SlsqpBuilder()
        .withLowerBounds(q)
        .withObjectiveFunction(new VectorObjectiveFunction())
        .addVectorConstraint(constraint)
        .build();
    
    
    final OptimizeResult result = slsqp.minimize(q);
    System.out.print(Arrays.toString(result.resultVec()));
    }
}