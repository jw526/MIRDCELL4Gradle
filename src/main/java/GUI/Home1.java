/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Activity.*;
import Display3D.*;
import File.*;
import Plot.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.URISyntaxException;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import com.skew.slsqp4j.*;
import SelfDoseCal.SelfDose_2;
import com.skew.slsqp4j.constraints.ConstraintType;
import com.skew.slsqp4j.constraints.VectorConstraint;
import com.skew.slsqp4j.functions.Vector2ScalarFunc;
import com.skew.slsqp4j.functions.Vector2VectorFunc;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Home1 extends JApplet implements ActionListener {
        public static final String version = "V3.07";

	CellCanvasDist cc2;
	CellCanvasInfo cc3;
        //CellCanvasInfoNew cellCanvasInfoNew1  = new CellCanvasInfoNew();
	//Behrooz June 26
	Cell3DCanvas C3D;
	Cell2DCanvas C2D;
        Cell3DSlice C3DS;
	int c, n, d, pro, maxRow, numDrugs = 4;
	Color newBack = new Color(204, 204, 255);
	String iso = "";
        String fileName;
	//WriteToOutputFile writeFile = new WriteToOutputFile();
	CalTest3D calTest3D = new CalTest3D();

	FileReadWrite frw = new FileReadWrite();
	int minimum = 0, maximum = 10000;
	public int text1, text2, text3;
	int Radius = 0;
	int Height = 0;
	double[][] cell, PlotOutput2D, PlotOutput3D;// BEHROOZ 06/15/201
	int mx, my;
	Random randomgen = new Random();
	int jPanelWidth = 500;
	double ZoomReset = 2;
        double ZoomResetSlice = 2;

	long justRan = 0;
	double[] coefficients = new double[11];
	int degree = 0, longestaxis, sumsubsequence = 0;
	double[][][] sVals;
	double[][] selfSVals;
        
        double[] avgActivity, avgDose, avgSelfDose, avgCrossDose, ulAvgCrossDose;
        int[] numCellsAt, ulNumCellsAt;

	// Alex Rosen 6/1/2017
	// for the seperation of alpha and beta doses via table on the complex radiobiological parameters tab and equations from roger
	boolean useComplexRadiobiologicalParams = false;
	double[][] complexRadiobiologicalParams = {
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
			{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}
	};
	SurvivalCalculationComplex SurvCalcComplex;
        SurvivalCalculation SurvCalc;
        Map drugIndexNameMap;
        java.util.List drugNameList;

	// units: gy^-1 / gy^-2 alternating
	// columns:
	// 0            ?self ( C?C )
	// 1            ?self ( C?C )
	// 2            ?self ( C?CS )
	// 3            ?self ( C?CS )
	// 4            ?self ( N?N )
	// 5            ?self ( N?N )
	// 6            ?self ( N?Cy )
	// 7            ?self ( N?Cy )
	// 8            ?self ( N?CS )
	// 9            ?self ( N?CS )
	// 10           ?cross ( C?C )
	// 11           ?cross ( C?C )
	//Rows:
	// radiation type (ICODE)

	// fractional radioactivity distribution
	// indexes are as follows: {cell (no nucleus), nucleus, cytoplasm, cellsurface}
	double[] activityFractions = {0, 0, 0, 100};
        
        //Jianchao 4/9/20
        TreeMap<Double, Double> radiusActivityMap = new TreeMap<>();
        JFileChooser fileChooser;
        java.util.List<java.util.List<Double>> drugList;
        DrugPanel[] drugPanels;
        InputCheck inputCheck;
        SortedMap<Knee.pair, Integer> kneeDrugMap;
        
	@SuppressWarnings("CallToThreadDumpStack")
	public Home1() {
            

		initComponents();
                addDrugPanels();
                fileChooser = new JFileChooser();
                inputCheck = new InputCheck();
		cc2 = new CellCanvasDist();
		cc2.setSize(700, 400);
		cc2.setLocation(0, 100);
		cc2.setBackground(jPanel31.getBackground());
		jPanel31.add(cc2);

		//display CellCanvas in top right
		//cc3 = new CellCanvasInfo();
		//cc3.setSize( 700, 400 );
		//cc3.setLocation( 0, 100 );
		//cc3.setBackground( jPanel42.getBackground() );
		//jPanel42.add( cc3 );

		//Behrooz June 26 : display Cell 3D Canvas
		C3D = new Cell3DCanvas();
		C3D.setSize(570, 500);
		C3D.setBackground(Color.WHITE);
		C3D.setLocation(0, 0);
		jPanel12.add(C3D);

		C2D = new Cell2DCanvas();
		C2D.setSize(570, 500);
		C2D.setBackground(Color.WHITE);
		C2D.setLocation(0, 0);
		jPanel33.add(C2D);
                //JCW 6/2/20: 3DSlice tab
                C3DS = new Cell3DSlice();
		C3DS.setSize(570, 480);
		C3DS.setBackground(Color.WHITE);
		C3DS.setLocation(0, 0);
		jPanel59.add(C3DS);
                
                PlotOutput2D = new double[8][];
                PlotOutput3D = new double[8][];
		SurvCalcComplex = new SurvivalCalculationComplex();                  
		// check for new version
		Update.VersionChecker.checkVersion();
                //temperarily disable DrugPlanning Tab 
                jTabbedPane1.setEnabledAt(4, false);
	}

	/**
	 * This method is called from within the init() method to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        
        private void addDrugPanels (){
            numDrugs = Integer.parseInt(jTextField37.getText());
            drugPanels = new DrugPanel[numDrugs];
            jPanel61.removeAll();
            for(int i=0; i<numDrugs;i++){
                DrugPanel drugPanel = new DrugPanel();
                drugPanels[i] = drugPanel;
                jPanel61.add(drugPanel);
                drugPanel.lb_drugName.setText("Drug " + (i+1));
            }
            jPanel61.revalidate();
            jPanel61.repaint();
        }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jLabel24 = new javax.swing.JLabel();
        jFrame2 = new javax.swing.JFrame();
        jLabel25 = new javax.swing.JLabel();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jFileChooser1 = new javax.swing.JFileChooser();
        jDialog1 = new javax.swing.JDialog();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jLabel18 = new javax.swing.JLabel();
        jPanel57 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panel2 = new java.awt.Panel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField11 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        panel4 = new java.awt.Panel();
        jButton5 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();
        jRadioButton4 = new javax.swing.JRadioButton();
        jPanel23 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        panel5 = new java.awt.Panel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton12 = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_2 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        button9 = new java.awt.Button();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField1.setEditable(true);
        button5 = new java.awt.Button();
        jLabel37 = new javax.swing.JLabel();
        button8 = new java.awt.Button();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField2.setEditable(true);
        button3 = new java.awt.Button();
        jLabel38 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jPanel41 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField17 = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        cellCanvasInfoNew1 = new GUI.CellCanvasInfoNew();
        jLabel110 = new javax.swing.JLabel();
        jLabel110.setVisible(false);
        jPanel19 = new javax.swing.JPanel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        panel1 = new java.awt.Panel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel53 = new javax.swing.JPanel();
        jPanel54 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jPanel55 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel105 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        button4 = new java.awt.Button();
        jTextField3 = new javax.swing.JTextField();
        button6 = new java.awt.Button();
        jLabel9 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jPanel50 = new javax.swing.JPanel();
        jPanel51 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton18 = new javax.swing.JButton();
        jPanel37 = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jTextField45 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jComboBox8 = new javax.swing.JComboBox();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jTextField43 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel39 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jPanel40 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox();
        jPanel48 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel56 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel122 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField30 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel46 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel50.setVisible(false);
        jTextField9 = new javax.swing.JTextField();
        jTextField9.setVisible(false);
        jLabel104 = new javax.swing.JLabel();
        jLabel104.setVisible(false);
        jTextField14 = new javax.swing.JTextField();
        jTextField14.setVisible(false);
        jButton1 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel13 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jComboBox11 = new javax.swing.JComboBox();
        jComboBox12 = new javax.swing.JComboBox();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jPanel49 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jPanel56 = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel111 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jButton23 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jButton22 = new javax.swing.JButton();
        jPanel59 = new javax.swing.JPanel();
        jButton24 = new javax.swing.JButton();
        jLabel112 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jButton26 = new javax.swing.JButton();
        jTextField19 = new javax.swing.JTextField();
        jLabel121 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jPanel58 = new javax.swing.JPanel();
        jPanel60 = new javax.swing.JPanel();
        jPanel63 = new javax.swing.JPanel();
        jLabel124 = new javax.swing.JLabel();
        btn_drugExport = new javax.swing.JButton();
        btn_drugImport = new javax.swing.JButton();
        jPanel64 = new javax.swing.JPanel();
        jTextField37 = new javax.swing.JTextField();
        jLabel135 = new javax.swing.JLabel();
        jButton27 = new javax.swing.JButton();
        jLabel123 = new javax.swing.JLabel();
        jPanel62 = new javax.swing.JPanel();
        cb_uploadDrugData = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        tf_tgtSF = new javax.swing.JTextField();
        cb_SF = new javax.swing.JComboBox<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel61 = new javax.swing.JPanel();
        jPanel70 = new javax.swing.JPanel();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel65 = new javax.swing.JPanel();
        jPanel66 = new javax.swing.JPanel();
        jPanel67 = new javax.swing.JPanel();
        jPanel68 = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        jButton16 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jLabel138 = new javax.swing.JLabel();
        jLabel139 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jButton13 = new javax.swing.JButton();
        jPanel29 = new javax.swing.JPanel();
        jPanel46 = new javax.swing.JPanel();
        jPanel47 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel18 = new javax.swing.JPanel();
        jPanel43 = new javax.swing.JPanel();
        jPanel44 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel71 = new javax.swing.JPanel();
        jLabel126 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel125 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel128 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();

        jFrame1.setTitle("2-D Colony Panel");
        jFrame1.setEnabled(false);

        jLabel24.setText("test popup frame for 2-D Colony model");

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        jFrame2.setTitle("3-D Cluster Panel");

        jLabel25.setText("3-D Cluster model");

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Copy");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);
        jPopupMenu1.add(jSeparator1);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setText("Print");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        jMenuItem9.setText("Save As");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem9);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem6.setText("Copy");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem6);
        jPopupMenu2.add(jSeparator2);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem8.setText("Print");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem8);

        jMenuItem10.setText("Save As");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem10);

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1090, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );

        jMenuItem12.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem12.setText("Copy");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem12);
        jPopupMenu3.add(jSeparator5);

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem14.setText("Print");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem14);

        jMenuItem15.setText("Save As");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem15);

        jLabel18.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("OUTPUT");

        javax.swing.GroupLayout jPanel57Layout = new javax.swing.GroupLayout(jPanel57);
        jPanel57.setLayout(jPanel57Layout);
        jPanel57Layout.setHorizontalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel57Layout.setVerticalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(238, 238, 238));
        setPreferredSize(new java.awt.Dimension(1600, 900));
        getContentPane().setLayout(new java.awt.FlowLayout());

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setName("Source Radiation"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1400, 850));
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        panel2.setBackground(new java.awt.Color(204, 204, 255));
        panel2.setPreferredSize(new java.awt.Dimension(450, 300));

        jRadioButton1.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton1.setText("Alpha Particle");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        jRadioButton2.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton2.setText("Electron");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        jTextField11.setText("1");
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        jTextField11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField11KeyReleased(evt);
            }
        });

        jLabel20.setText(" Yield / Decay");

        jLabel21.setText("Energy (MeV)");

        jTextField10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextField10MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField10MousePressed(evt);
            }
        });
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField10KeyReleased(evt);
            }
        });

        jPanel22.setBackground(new java.awt.Color(102, 102, 255));

        jLabel28.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Monoenergetic Particle Emitter");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jTextField11))
                        .addGap(36, 36, 36)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField10, jTextField11});

        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(22, 22, 22)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(22, 22, 22)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTextField10, jTextField11});

        panel4.setBackground(new java.awt.Color(204, 204, 255));
        panel4.setPreferredSize(new java.awt.Dimension(450, 300));

        jButton5.setText("Add Radiation (Click for each to be added)");
        jButton5.setName(""); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton11.setText("Save Radionuclide (to your disk)");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton10.setText("Confirm List of Radiations");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton9.setText("Open");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton8.setText("Help");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jRadioButton3.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton3.setText("Create");
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });
        jRadioButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton3MouseClicked(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Choose radiation", "Alpha", "Annihilation quanta", "Auger electrons", "Beta+ particles", "Beta- particles", "Daughter Recoil(Alpha decay)", "Fission fragments", "Gamma rays", "Internal conversion Electrons", "Neutrons", "X-rays" }));
        jComboBox1.setEnabled(false);
        jComboBox1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBox1FocusGained(evt);
            }
        });
        jComboBox1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jComboBox1MouseClicked(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jRadioButton4.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton4.setText("Retrieve");
        jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton4ItemStateChanged(evt);
            }
        });

        jPanel23.setBackground(new java.awt.Color(102, 102, 255));

        jLabel29.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("User Created Radionuclide");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextField12.setEnabled(false);

        jLabel43.setText("Energy (MeV) : ");

        jLabel51.setText(" Yield / Decay : ");

        jTextField13.setEnabled(false);

        jLabel75.setText("Name:");

        jTextField4.setEnabled(false);
        jTextField4.setText(" ");

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator4)
            .addGroup(panel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .addGroup(panel4Layout.createSequentialGroup()
                        .addComponent(jLabel75)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton8))
                    .addGroup(panel4Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton8, jButton9});

        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton4)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel75)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(jLabel43)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        panel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton11, jButton8, jButton9});

        panel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton10, jButton5});

        panel5.setBackground(new java.awt.Color(204, 204, 255));
        panel5.setPreferredSize(new java.awt.Dimension(450, 300));

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setSelectionBackground(new java.awt.Color(0, 153, 255));
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList2);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Radionuclide");

        jRadioButton13.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton13.setText("<html>&beta Full Energy Spectrum</html>");
        jRadioButton13.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton13ItemStateChanged(evt);
            }
        });

        jRadioButton12.setBackground(new java.awt.Color(204, 204, 255));
        jRadioButton12.setText("<html>&beta Average Energy Spectrum</html>");
        jRadioButton12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton12ItemStateChanged(evt);
            }
        });

        jPanel21.setBackground(new java.awt.Color(102, 102, 255));

        jLabel19.setBackground(new java.awt.Color(0, 204, 51));
        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Predefined MIRD Radionuclide");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel14.setBackground(new java.awt.Color(255, 102, 102));
        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panel5Layout.createSequentialGroup()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addComponent(jRadioButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBackground(new java.awt.Color(204, 204, 255));
        jPanel20.setPreferredSize(new java.awt.Dimension(450, 300));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("This area will be populated upon \nselecting Source Radiation below.");
        jTextArea2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 204, 0)));
        jTextArea2.setEditable(false);
        jTextArea2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea2MousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea2);

        jButton12.setBackground(new java.awt.Color(204, 204, 204));
        jButton12.setText("Reset");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jPanel24.setBackground(new java.awt.Color(102, 102, 255));

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Input Data for Calculation ");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addGap(0, 123, Short.MAX_VALUE)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                    .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(149, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Source Radiation", jPanel1);

        jPanel2.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));

        label_2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_2.setText("    Target ?  Source");

        jPanel26.setBackground(new java.awt.Color(204, 204, 255));
        jPanel26.setPreferredSize(new java.awt.Dimension(500, 520));

        jPanel27.setBackground(new java.awt.Color(102, 102, 255));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Target Region(s) ");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Cell", "Nucleus", "Cytoplasm", "Nucleus & Cytoplasm" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList3.setMinimumSize(new java.awt.Dimension(125, 132));
        jList3.setSelectedIndex(1);
        jList3.setSelectionBackground(new java.awt.Color(0, 204, 51));
        jList3.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList3ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList3);

        button9.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button9.setLabel("+");
        button9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button9ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("um");

        jTextField1.setText("5");
        jTextField1.setEditable(false);
        jTextField1.setName("rc"); // NOI18N
        jTextField1.setEditable(true);
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        button5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button5.setLabel("-");
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        jLabel37.setText("Radius of Cell (RC)");
        jLabel37.setPreferredSize(new java.awt.Dimension(110, 14));

        button8.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button8.setLabel("+");
        button8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button8ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("um");

        jTextField2.setText("3");
        jTextField2.setEditable(false);
        jTextField2.setName("rn"); // NOI18N
        jTextField2.setEditable(true);
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        button3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button3.setLabel("-");
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        jLabel38.setText("Radius of Nucleus (RN)");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel26Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel26Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(button5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(29, 29, 29))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(button5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel26Layout.createSequentialGroup()
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(3, 3, 3))
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(177, Short.MAX_VALUE))
        );

        jPanel34.setBackground(new java.awt.Color(204, 204, 255));
        jPanel34.setPreferredSize(new java.awt.Dimension(450, 300));

        jPanel41.setBackground(new java.awt.Color(102, 102, 255));

        jLabel44.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Result:");

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 522, Short.MAX_VALUE))
        );

        jPanel42.setBackground(new java.awt.Color(204, 204, 255));
        jPanel42.setPreferredSize(new java.awt.Dimension(450, 520));

        jPanel45.setBackground(new java.awt.Color(102, 102, 255));

        jLabel65.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 255, 255));
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("<html> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Target Region(s) &larr; Source Region</html>");

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel65, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel106.setText("% activity in Nucleus");

        jTextField15.setText("100");
        jTextField15.setName("rn"); // NOI18N
        jTextField15.setPreferredSize(new java.awt.Dimension(75, 35));
        jTextField15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField15KeyReleased(evt);
            }
        });

        jTextField16.setText("0");
        jTextField16.setName("rn"); // NOI18N
        jTextField16.setPreferredSize(new java.awt.Dimension(75, 35));
        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });
        jTextField16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField16KeyReleased(evt);
            }
        });

        jTextField17.setText("0");
        jTextField17.setName("rn"); // NOI18N
        jTextField17.setPreferredSize(new java.awt.Dimension(75, 35));
        jTextField17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField17ActionPerformed(evt);
            }
        });
        jTextField17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField17KeyReleased(evt);
            }
        });

        jLabel108.setText("% activity on Cell Surface");
        jLabel108.setPreferredSize(new java.awt.Dimension(110, 14));

        jLabel107.setText("% activity in Cytoplasm");

        jLabel109.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel109.setText("Total: 100");

        javax.swing.GroupLayout cellCanvasInfoNew1Layout = new javax.swing.GroupLayout(cellCanvasInfoNew1);
        cellCanvasInfoNew1.setLayout(cellCanvasInfoNew1Layout);
        cellCanvasInfoNew1Layout.setHorizontalGroup(
            cellCanvasInfoNew1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        cellCanvasInfoNew1Layout.setVerticalGroup(
            cellCanvasInfoNew1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 458, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addComponent(cellCanvasInfoNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel42Layout.createSequentialGroup()
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel108, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel107, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                            .addComponent(jLabel106, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 22, Short.MAX_VALUE)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel42Layout.createSequentialGroup()
                                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 2, Short.MAX_VALUE))))
                    .addGroup(jPanel42Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)))
                .addGap(40, 40, 40))
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel42Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel106, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cellCanvasInfoNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jLabel110.setVisible(false);
        jLabel110.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel110.setForeground(new java.awt.Color(255, 0, 0));
        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel110.setText("Activity Fractions Must Total to 100");
        jLabel110.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel110.setVisible(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2559, Short.MAX_VALUE)
                        .addComponent(label_2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(667, 667, 667)
                        .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(3333, 3333, 3333)
                    .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label_2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel110)
                .addContainerGap(253, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(120, 120, 120)
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                    .addGap(120, 120, 120)))
        );

        jTabbedPane1.addTab("Cell Source/Target", jPanel2);

        jTabbedPane5.setPreferredSize(new java.awt.Dimension(1400, 800));

        panel1.setBackground(new java.awt.Color(204, 204, 255));

        jTextField7.setText("0");
        jTextField7.setEnabled(true);
        jTextField7.setVisible(true);

        jTextField8.setText("0");
        jTextField8.setEnabled(true);
        jTextField8.setVisible(true);

        jLabel16.setText("<html> <p class=MsoNormal><span lang=EN-US>Gy<sup>-1</sup></span></p> </html>");
        jLabel16.setEnabled(true);
        jLabel16.setVisible(true);

        jPanel25.setBackground(new java.awt.Color(102, 102, 255));

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Simple Linear Quadratic Parameters");
        jLabel12.setEnabled(true);
        jLabel12.setVisible(true);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel58.setText("Self Absorbed Dose : ");

        jLabel59.setText("Cross Absorbed Dose : ");

        jLabel60.setText("<html> <p class=MsoNormal><span lang=EN-US>&alpha;<sub>self</sub> = </span></p> </html>");
        jLabel60.setEnabled(true);
        jLabel13.setVisible(true);

        jLabel61.setText("<html> <p class=MsoNormal><span lang=EN-US>&alpha;<sub>cross</sub> = </span></p> </html>");
        jLabel61.setEnabled(true);
        jLabel13.setVisible(true);

        jLabel62.setText("<html> <p class=MsoNormal><span lang=EN-US>&beta;<sub>self</sub> = </span></p> </html>");
        jLabel62.setEnabled(true);
        jLabel13.setVisible(true);

        jLabel63.setText("<html> <p class=MsoNormal><span lang=EN-US>&beta;<sub>cross</sub> = </span></p> </html>");
        jLabel63.setEnabled(true);
        jLabel13.setVisible(true);

        jTextField32.setText("1");

        jTextField34.setText("1");

        jLabel64.setText("The probability that a given cell survives is calculated using the linear quadratic model.");

        jLabel66.setText("<html> <p class=MsoNormal><span lang=EN-US>Gy<sup>-1</sup></span></p> </html>");
        jLabel66.setEnabled(true);
        jLabel16.setVisible(true);

        jLabel67.setText("<html> <p class=MsoNormal><span lang=EN-US>Gy<sup>-2</sup></span></p> </html>");
        jLabel67.setEnabled(true);
        jLabel16.setVisible(true);

        jLabel68.setText("<html> <p class=MsoNormal><span lang=EN-US>Gy<sup>-2</sup></span></p> </html>");
        jLabel68.setEnabled(true);
        jLabel16.setVisible(true);

        try{
            jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/equation.gif")));
        } catch (Exception e){
            jLabel10.setText("<html><h2>P = e <sup>-&alpha;<sub>self</sub>D<sub>self</sub>-&beta;<sub>self</sub>D<sup>2</sup><sub>self</sub></sup> &times; e <sup>-&alpha;<sub>cross</sub>D<sub>cross</sub>-&beta;<sub>cross</sub>D<sup>2</sup><sub>cross</sub></sup></h2></html>");
        }

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel58)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel64)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                            .addComponent(jTextField34))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 932, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel64)
                .addGap(37, 37, 37)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(171, 171, 171))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTextField7, jTextField8});

        jTabbedPane5.addTab("Simple Radiobiological Parameters", panel1);

        jPanel53.setBackground(new java.awt.Color(204, 204, 255));

        jPanel54.setBackground(new java.awt.Color(102, 102, 255));

        jLabel33.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("                                    Complex Linear Quadratic Parameters");
        jLabel33.setEnabled(true);
        jLabel12.setVisible(true);

        jButton14.setText("Show Equation");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton14)
                .addGap(259, 259, 259))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jButton14))
                .addContainerGap())
        );

        jTable1.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1, 2, 3", "<html>&gamma;-rays, X-rays, AQ</html>",  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"4, 5, 6", "<html>&beta;+, &beta;-, IE</html>",  new Double(0.25),  new Double(0.0),  new Double(0.25),  new Double(0.0),  new Double(0.25),  new Double(0.0),  new Double(0.25),  new Double(0.0)},
                {"7", "Auger electrons",  new Double(2.3),  new Double(0.0),  new Double(0.25),  new Double(0.0),  new Double(0.25),  new Double(0.0),  new Double(0.25),  new Double(0.0)},
                {"8", "Alpha particles",  new Double(1.4),  new Double(0.0),  new Double(1.4),  new Double(0.0),  new Double(1.4),  new Double(0.0),  new Double(1.4),  new Double(0.0)},
                {"9", "Daughter recoil (alpha decay)",  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"10", "Fission fragments",  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"11", "Neutrons",  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0),  new Double(0.0)}
            },
            new String [] {
                "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jTable1.setPreferredSize(new java.awt.Dimension(700, 112));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setRowHeight(60);
        jTable1.setFillsViewportHeight(true);
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTable1KeyTyped(evt);
            }
        });
        jScrollPane7.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jCheckBox2.setText("Use Complex Radiobiological Parameters");
        jCheckBox2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox2StateChanged(evt);
            }
        });

        jLabel105.setForeground(new java.awt.Color(255, 0, 0));
        jLabel105.setText("<html><p><b>NOTE: You must click off of each cell or press enter after editing them for the new value to be saved.</b></p><p><b>NOTE: S Values for ICODES 1, 2, 3, 9, 10, and 11 are not calculated and not used in dose calculations.</b></p></html>");

        jButton3.setText("Import");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Export");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel55Layout = new javax.swing.GroupLayout(jPanel55);
        jPanel55.setLayout(jPanel55Layout);
        jPanel55Layout.setHorizontalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel55Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel55Layout.createSequentialGroup()
                        .addComponent(jCheckBox2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4))
                    .addComponent(jScrollPane7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel55Layout.setVerticalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBox2)
                        .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton4)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane5.addTab("Complex Radiobiological Parameters", jPanel53);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1375, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Radiobiological Parameters", jPanel19);

        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane2MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPane2MousePressed(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(204, 204, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(450, 300));

        jPanel28.setBackground(new java.awt.Color(102, 102, 255));

        jLabel36.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Distance Between Cells");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        button4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button4.setLabel("+");
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        jTextField3.setText("10");
        jTextField3.setName("distance"); // NOI18N
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextField3PropertyChange(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
        });

        button6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        button6.setLabel("-");
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("um");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(2, 2, 2)))
                    .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        jPanel31.setBackground(new java.awt.Color(204, 204, 255));
        jPanel31.setPreferredSize(new java.awt.Dimension(450, 300));

        jPanel32.setBackground(new java.awt.Color(102, 102, 255));

        jLabel39.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Result:");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 410, Short.MAX_VALUE))
        );

        jPanel50.setBackground(new java.awt.Color(204, 204, 255));

        jPanel51.setBackground(new java.awt.Color(204, 255, 204));
        jPanel51.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        jLabel89.setBackground(new java.awt.Color(102, 102, 255));
        jLabel89.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel89.setForeground(new java.awt.Color(255, 255, 255));
        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel89.setText("Progress:");
        jLabel89.setOpaque(true);

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel89, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel50Layout.createSequentialGroup()
                .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton7.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jButton7.setText("Compute");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel50, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 314, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(312, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("1-D Cell Pair", jPanel5);

        jButton18.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jButton18.setText("Compute");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jPanel37.setBackground(new java.awt.Color(204, 204, 255));

        jPanel38.setBackground(new java.awt.Color(204, 255, 204));
        jPanel38.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        jLabel88.setBackground(new java.awt.Color(102, 102, 255));
        jLabel88.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel88.setText("Progress:");
        jLabel88.setOpaque(true);

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel88, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel36.setBackground(new java.awt.Color(204, 204, 255));
        jPanel36.setPreferredSize(new java.awt.Dimension(425, 250));

        jLabel81.setBackground(new java.awt.Color(102, 102, 255));
        jLabel81.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(255, 255, 255));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel81.setText("Cell Labeling");
        jLabel81.setOpaque(true);

        jLabel82.setText("Number of Cells Labeled:");

        jTextField44.setText("0");
        jTextField44.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jTextField45.setText("100");
        jTextField45.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jLabel83.setText("Percentage of cells that are Labeled (%):");

        jLabel84.setText("Max mean Activity per Cell (All Cells) (Bq):");

        jTextField46.setText(".001");
        jTextField46.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField46KeyReleased(evt);
            }
        });

        jLabel85.setText("Time integrated activity coefficient (hr):");

        jTextField47.setText("100");
        jTextField47.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField47KeyReleased(evt);
            }
        });

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal Distribution (Random)", "Log-Normal Distribution (Random)", "Uniform Distribution (Random)" }));
        jComboBox8.setSelectedIndex(2);
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });

        jLabel86.setText("Labeling Method:");

        jLabel87.setText("Shape Factor (Bq):");
        jLabel87.setVisible(false);

        jTextField48.setText("1");
        jTextField48.setVisible(false);
        jTextField48.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField48KeyReleased(evt);
            }
        });

        jLabel100.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel100.setForeground(new java.awt.Color(255, 51, 51));
        jLabel100.setText("<HTML>Negative activity values.<BR>Please decrease value of standard deviation.</HTML>");
        jLabel100.setVisible(false);

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel87)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel82)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel84)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel86)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel85)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addComponent(jLabel83)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel100, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel35.setBackground(new java.awt.Color(204, 204, 255));
        jPanel35.setPreferredSize(new java.awt.Dimension(500, 350));

        jLabel76.setBackground(new java.awt.Color(102, 102, 255));
        jLabel76.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(255, 255, 255));
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setText("Cell Geometry");
        jLabel76.setOpaque(true);

        jLabel34.setText("Shape:");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Circle", "Ellipse", "Rectangle" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });

        jLabel40.setText("Radius (um):");

        jTextField41.setText("100");
        jTextField41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField41ActionPerformed(evt);
            }
        });
        jTextField41.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField41KeyReleased(evt);
            }
        });

        jLabel77.setText("Height (um):");
        jLabel77.setVisible(false);

        jTextField42.setText("0");
        jTextField42.setVisible(false);
        jTextField42.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField42KeyReleased(evt);
            }
        });

        jLabel78.setText("Number of Cells:");

        jTextField43.setEditable(false);
        jTextField43.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField43KeyReleased(evt);
            }
        });

        jTextField36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField36ActionPerformed(evt);
            }
        });
        jTextField36.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField36KeyReleased(evt);
            }
        });

        jLabel74.setText("Distance Between Cells (um):");

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel77)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel78)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(199, Short.MAX_VALUE))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77)
                    .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jTabbedPane4.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane4.setPreferredSize(new java.awt.Dimension(570, 600));

        jLabel69.setText("Select the Vertical Axis:");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Surviving Fraction of Labeled cells", "Surviving Fraction of Unlabeled cells", "Surviving Fraction of mixed population of cells", "Tumor Control Probability" }));
        jComboBox9.setSelectedIndex(2);
        jComboBox9.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox9ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );

        jLabel72.setText("Select the Domain:");

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean activity per cell (Bq)", "Mean absorbed dose to cells (Gy)", "Mean activity per labeled cell (Bq)", "Mean absorbed dose to labeled cells (Gy)", "Mean absorbed dose to unlabeled cells (Gy)", "Mean decay per cell (Bq)", "Mean decay per labeled cell (Bq)" }));
        jComboBox10.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox10ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69)
                    .addComponent(jLabel72))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Surviving Fraction Curve", jPanel39);

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 861, Short.MAX_VALUE)
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 755, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Activity Histogram", jPanel48);

        jPanel33.setBackground(new java.awt.Color(255, 255, 255));
        jPanel33.setPreferredSize(new java.awt.Dimension(5, 5));

        jButton15.setText("+");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton19.setText("Reset");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("-");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jLabel90.setBackground(new java.awt.Color(204, 255, 204));
        jLabel90.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel90.setOpaque(true);
        jLabel90.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel91.setText("Unlabeled & Dead Cell");

        jLabel92.setBackground(new java.awt.Color(0, 153, 0));
        jLabel92.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel92.setOpaque(true);
        jLabel92.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel93.setText("Unlabeled & Alive Cell");

        jLabel70.setBackground(new java.awt.Color(255, 204, 204));
        jLabel70.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel70.setOpaque(true);
        jLabel70.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel79.setText("Labeled & Dead Cell");

        jLabel80.setBackground(new java.awt.Color(255, 0, 0));
        jLabel80.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel80.setOpaque(true);
        jLabel80.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel31.setText("Labeled & Alive Cell");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel79))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel91))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel93)))
                .addGap(22, 22, 22))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(698, Short.MAX_VALUE)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton20)
                            .addComponent(jButton19)
                            .addComponent(jButton15)
                            .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79))))
                .addContainerGap())
        );

        jTabbedPane4.addTab("2-D Colony", jPanel33);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("2-D Colony", jPanel4);

        jPanel6.setPreferredSize(new java.awt.Dimension(1190, 800));

        jPanel15.setBackground(new java.awt.Color(204, 204, 255));
        jPanel15.setPreferredSize(new java.awt.Dimension(425, 85));

        jPanel14.setBackground(new java.awt.Color(204, 255, 204));
        jPanel14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        jLabel53.setBackground(new java.awt.Color(102, 102, 255));
        jLabel53.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("Progress:");
        jLabel53.setOpaque(true);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel16.setBackground(new java.awt.Color(204, 204, 255));
        jPanel16.setPreferredSize(new java.awt.Dimension(500, 255));

        jLabel54.setBackground(new java.awt.Color(102, 102, 255));
        jLabel54.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText("Cell Geometry");
        jLabel54.setOpaque(true);

        jLabel3.setText("Shape:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sphere", "Rod", "Cone", "Ellipsoid" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel17.setText("Radius (um):"); // NOI18N

        jTextField26.setText("100");
        jTextField26.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField26KeyReleased(evt);
            }
        });

        jLabel42.setText("Height (um):"); // NOI18N
        jLabel42.setVisible(false);

        jTextField27.setText("0");
        jTextField27.setVisible(false);
        jTextField27.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField27KeyReleased(evt);
            }
        });

        jLabel4.setText("Number of Cells:");

        jTextField25.setEditable(false);
        jTextField25.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField25KeyReleased(evt);
            }
        });

        jTextField35.setText("10");
        jTextField35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField35ActionPerformed(evt);
            }
        });
        jTextField35.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField35KeyReleased(evt);
            }
        });

        jLabel73.setText("Distance Between Cells (um):");

        jCheckBox1.setBackground(new java.awt.Color(204, 204, 255));
        jCheckBox1.setText("Cold Region");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Drug Penetration Depth (um):");
        jLabel30.setVisible(false);

        jTextField5.setVisible(false);
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField5KeyTyped(evt);
            }
        });

        jCheckBox3.setText("Assume dead?");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox3))
                    .addComponent(jLabel122, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(66, 155, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73)
                    .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel122, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jComboBox2.setSelectedIndex(0);
        jLabel56.getAccessibleContext().setAccessibleName("jLabel56");
        jCheckBox3.setVisible(false);

        jPanel17.setBackground(new java.awt.Color(204, 204, 255));
        jPanel17.setPreferredSize(new java.awt.Dimension(500, 300));

        jLabel55.setBackground(new java.awt.Color(102, 102, 255));
        jLabel55.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("Cell Labeling");
        jLabel55.setOpaque(true);

        jLabel49.setText("Number of Cells Labeled:");

        jTextField30.setText("0");
        jTextField30.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField30KeyReleased(evt);
            }
        });

        jTextField28.setText("100");
        jTextField28.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField28KeyReleased(evt);
            }
        });

        jLabel47.setText("Percentage of cells that are Labeled (%):");

        jLabel48.setText("Max mean Activity per Cell (All Cells) (Bq):");

        jTextField29.setText(".001");
        jTextField29.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField29KeyReleased(evt);
            }
        });

        jLabel52.setText("Time integrated activity coefficient (hr):");

        jTextField31.setText("100");
        jTextField31.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField31KeyReleased(evt);
            }
        });

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal Distribution (Random)", "Log-Normal Distribution (Random)", "Uniform Distribution (Random)", "Linear (Radial)", "Exponential (Radial)", "Polynomial (Radial)", "4 Par Log-Normal (Radial)", "Import CSV (r, relative A/cell) (Radial)", "Import CSV (r, decay/cell) (Radial)" }));
        jComboBox5.setSelectedIndex(2);
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jLabel46.setText("Labeling Method:");

        jLabel57.setText("Shape Factor (Bq):");
        jLabel57.setVisible(false);

        jTextField33.setText(".0001");
        jTextField33.setVisible(false);
        jTextField33.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField33KeyReleased(evt);
            }
        });

        jLabel101.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel101.setForeground(new java.awt.Color(255, 51, 51));
        jLabel101.setText("<HTML>Negative activity values.<BR>Please decrease value of standard deviation.</HTML>");
        jLabel101.setVisible(false);

        jLabel27.setText("Center Value (%)");
        jLabel27.setVisible(false);

        jTextField6.setText("0");
        jTextField6.setToolTipText("The amount as a % of the maximum (edge) value that the activity distribution should approach.  ");
        jTextField6.setVisible(false);

        jLabel50.setText("CenterActivity Lvl: ");

        jLabel104.setText("Radial Shape Parameter");

        jButton1.setText("Show Equation");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel101)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel104)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                    .addComponent(jLabel46)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton1))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel17Layout.createSequentialGroup()
                                    .addComponent(jLabel49)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel50)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton17.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jButton17.setText("Compute");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jTabbedPane3.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane3.setPreferredSize(new java.awt.Dimension(650, 600));

        jPanel13.setPreferredSize(new java.awt.Dimension(439, 800));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 563, Short.MAX_VALUE)
        );

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean activity per cell (Bq)", "Mean absorbed dose to cells (Gy)", "Mean activity per labeled cell (Bq)", "Mean absorbed dose to labeled cells (Gy)", "Mean absorbed dose to unlabeled cells (Gy)", "Mean decay per cell (Bq)", "Mean decay per labeled cell (Bq)" }));
        jComboBox11.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox11ItemStateChanged(evt);
            }
        });

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Surviving Fraction of Labeled cells", "Surviving Fraction of Unlabeled cells", "Surviving Fraction of mixed population of cells", "Tumor Control Probability" }));
        jComboBox12.setSelectedIndex(2);
        jComboBox12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox12ItemStateChanged(evt);
            }
        });

        jLabel102.setText("Select the Vertical Axis:");

        jLabel103.setText("Select the Domain:");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel102)
                    .addComponent(jLabel103))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox11, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        jTabbedPane3.addTab("Surviving Fraction Curve", jPanel13);

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 725, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Activity Histogram", jPanel49);

        javax.swing.GroupLayout jPanel56Layout = new javax.swing.GroupLayout(jPanel56);
        jPanel56.setLayout(jPanel56Layout);
        jPanel56Layout.setHorizontalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel56Layout.setVerticalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 683, Short.MAX_VALUE)
        );

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean Activity per Labeled Cell", "Mean Absorbed Dose to Labeled Cells", "Mean Self Absorbed Dose to Labeled Cells", "Mean Cross Absorbed Dose to Labeled Cells", "Mean Decays per Labeled Cell", "Mean Cross Absorbed Dose to Unlabeled Cells", "Mean Absorbed Dose To Cells" }));
        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });

        jLabel111.setText("Axial height (cell diameters)");
        jLabel111.setToolTipText("");

        jTextField18.setText("0");
        jTextField18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jPanel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jPanel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jTabbedPane3.addTab("Radial Histogram", jPanel30);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setPreferredSize(new java.awt.Dimension(600, 600));
        jPanel12.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel12MouseDragged(evt);
            }
        });
        jPanel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel12MousePressed(evt);
            }
        });

        jButton21.setText("+");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel32.setText("Labeled & Alive Cell");

        jLabel97.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel97.setText("Labeled & Dead Cell");

        jLabel98.setBackground(new java.awt.Color(255, 51, 51));
        jLabel98.setText(" X");
        jLabel98.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel98.setOpaque(true);
        jLabel98.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel98.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel98MouseClicked(evt);
            }
        });

        jLabel96.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel96.setText("Unlabeled & Alive Cell");

        jLabel95.setBackground(new java.awt.Color(51, 255, 51));
        jLabel95.setText(" X");
        jLabel95.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel95.setOpaque(true);
        jLabel95.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel95.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel95MouseClicked(evt);
            }
        });

        jLabel71.setBackground(new java.awt.Color(255, 204, 204));
        jLabel71.setText(" X");
        jLabel71.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel71.setOpaque(true);
        jLabel71.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel71MouseClicked(evt);
            }
        });

        jButton23.setText("-");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jLabel15.setText("Click and Drag to Rotate");

        jLabel94.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel94.setText("Unlabeled & Dead Cell");

        jLabel99.setBackground(new java.awt.Color(204, 255, 204));
        jLabel99.setText(" X");
        jLabel99.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel99.setOpaque(true);
        jLabel99.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel99.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel99MouseClicked(evt);
            }
        });

        jButton22.setText("Reset");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel97))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jButton23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21))
                    .addComponent(jLabel15))
                .addGap(64, 64, 64)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel94))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel96)))
                .addGap(82, 82, 82))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(638, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton23)
                            .addComponent(jButton22)
                            .addComponent(jButton21)
                            .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel97))))
                .addGap(41, 41, 41))
        );

        jTabbedPane3.addTab("3-D Cluster", jPanel12);

        jPanel59.setBackground(new java.awt.Color(255, 255, 255));
        jPanel59.setPreferredSize(new java.awt.Dimension(600, 600));
        jPanel59.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel59MouseDragged(evt);
            }
        });
        jPanel59.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanel59MouseWheelMoved(evt);
            }
        });
        jPanel59.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel59MousePressed(evt);
            }
        });

        jButton24.setText("+");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jLabel112.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel112.setText("Labeled & Alive Cell");

        jLabel113.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel113.setText("Labeled & Dead Cell");

        jLabel114.setBackground(new java.awt.Color(255, 51, 51));
        jLabel114.setText(" X");
        jLabel114.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel114.setOpaque(true);
        jLabel114.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel114.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel114MouseClicked(evt);
            }
        });

        jLabel115.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel115.setText("Unlabeled & Alive Cell");

        jLabel116.setBackground(new java.awt.Color(51, 255, 51));
        jLabel116.setText(" X");
        jLabel116.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel116.setOpaque(true);
        jLabel116.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel116.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel116MouseClicked(evt);
            }
        });

        jLabel117.setBackground(new java.awt.Color(255, 204, 204));
        jLabel117.setText(" X");
        jLabel117.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel117.setOpaque(true);
        jLabel117.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel117.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel117MouseClicked(evt);
            }
        });

        jButton25.setText("-");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jLabel118.setText("Scroll mouse to change slice");

        jLabel119.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel119.setText("Unlabeled & Dead Cell");

        jLabel120.setBackground(new java.awt.Color(204, 255, 204));
        jLabel120.setText(" X");
        jLabel120.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel120.setOpaque(true);
        jLabel120.setPreferredSize(new java.awt.Dimension(20, 20));
        jLabel120.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel120MouseClicked(evt);
            }
        });

        jButton26.setText("Reset");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jTextField19.setText("0");

        jLabel121.setText("Axial Height (cell diameters)");

        javax.swing.GroupLayout jPanel59Layout = new javax.swing.GroupLayout(jPanel59);
        jPanel59.setLayout(jPanel59Layout);
        jPanel59Layout.setHorizontalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel113))
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel112))
                    .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jButton25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton24))
                    .addComponent(jLabel118)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 138, Short.MAX_VALUE))
        );
        jPanel59Layout.setVerticalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel59Layout.createSequentialGroup()
                .addContainerGap(591, Short.MAX_VALUE)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel118)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton25)
                            .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton24)
                                .addComponent(jButton26))))
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel59Layout.createSequentialGroup()
                                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel59Layout.createSequentialGroup()
                                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel113))))))
                .addGap(50, 50, 50))
        );

        jTabbedPane3.addTab("3-D Slice", jPanel59);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 692, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(17, 17, 17)))
                .addContainerGap())
        );

        jTabbedPane3.getAccessibleContext().setAccessibleName("3-D Cluster");

        jTabbedPane2.addTab("3-D Cluster", jPanel6);
        jPanel6.getAccessibleContext().setAccessibleName("");

        jTabbedPane1.addTab("Multicellular Geometry", jTabbedPane2);

        jPanel60.setPreferredSize(new java.awt.Dimension(1057, 118));

        jPanel63.setBackground(new java.awt.Color(204, 204, 255));
        jPanel63.setPreferredSize(new java.awt.Dimension(425, 250));

        jLabel124.setBackground(new java.awt.Color(102, 102, 255));
        jLabel124.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel124.setForeground(new java.awt.Color(255, 255, 255));
        jLabel124.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel124.setText("Drug Profile");
        jLabel124.setOpaque(true);

        btn_drugExport.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        btn_drugExport.setText("export");
        btn_drugExport.setPreferredSize(new java.awt.Dimension(79, 20));
        btn_drugExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_drugExportActionPerformed(evt);
            }
        });

        btn_drugImport.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        btn_drugImport.setText("import");
        btn_drugImport.setPreferredSize(new java.awt.Dimension(79, 20));
        btn_drugImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_drugImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addComponent(jLabel124, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btn_drugImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_drugExport, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel124, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_drugExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_drugImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel64.setBackground(new java.awt.Color(204, 204, 255));
        jPanel64.setPreferredSize(new java.awt.Dimension(425, 150));

        jTextField37.setText("4");
        jTextField37.setMinimumSize(new java.awt.Dimension(6, 16));
        jTextField37.setPreferredSize(new java.awt.Dimension(12, 16));
        jTextField37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField37ActionPerformed(evt);
            }
        });
        jTextField37.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField37KeyReleased(evt);
            }
        });

        jLabel135.setText("Number of Drugs:");

        jButton27.setBackground(new java.awt.Color(255, 255, 255));
        jButton27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton27.setText("Compute");
        jButton27.setPreferredSize(new java.awt.Dimension(103, 35));
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jLabel123.setBackground(new java.awt.Color(102, 102, 255));
        jLabel123.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel123.setForeground(new java.awt.Color(255, 255, 255));
        jLabel123.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel123.setText("Progress:");
        jLabel123.setOpaque(true);

        jPanel62.setBackground(new java.awt.Color(204, 255, 204));
        jPanel62.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        cb_uploadDrugData.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose Types of Data to Upload", "Biological (e.g. fluorescence)", "Effective " }));
        cb_uploadDrugData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_uploadDrugDataActionPerformed(evt);
            }
        });

        jLabel41.setText("File Not Selected!");

        tf_tgtSF.setText("0.001");
        tf_tgtSF.setPreferredSize(new java.awt.Dimension(65, 16));

        cb_SF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Target SF", "Custom SF", "Target SF 2" }));
        cb_SF.setPreferredSize(new java.awt.Dimension(76, 16));
        cb_SF.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_SFItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel64Layout = new javax.swing.GroupLayout(jPanel64);
        jPanel64.setLayout(jPanel64Layout);
        jPanel64Layout.setHorizontalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cb_SF, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel135, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField37, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                    .addComponent(tf_tgtSF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(54, 54, 54)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cb_uploadDrugData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel123, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel64Layout.setVerticalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel123)
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel64Layout.createSequentialGroup()
                                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cb_uploadDrugData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel135)
                                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tf_tgtSF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cb_SF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel60Layout = new javax.swing.GroupLayout(jPanel60);
        jPanel60.setLayout(jPanel60Layout);
        jPanel60Layout.setHorizontalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel60Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel64, javax.swing.GroupLayout.DEFAULT_SIZE, 1370, Short.MAX_VALUE)
                    .addComponent(jPanel63, javax.swing.GroupLayout.DEFAULT_SIZE, 1370, Short.MAX_VALUE)))
        );
        jPanel60Layout.setVerticalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel60Layout.createSequentialGroup()
                .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel63, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jScrollPane9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane9.setPreferredSize(new java.awt.Dimension(1047, 523));

        jPanel61.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jScrollPane9.setViewportView(jPanel61);

        javax.swing.GroupLayout jPanel58Layout = new javax.swing.GroupLayout(jPanel58);
        jPanel58.setLayout(jPanel58Layout);
        jPanel58Layout.setHorizontalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel58Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1370, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel58Layout.createSequentialGroup()
                    .addComponent(jPanel60, javax.swing.GroupLayout.DEFAULT_SIZE, 1380, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel58Layout.setVerticalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel58Layout.createSequentialGroup()
                .addContainerGap(236, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel58Layout.createSequentialGroup()
                    .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 696, Short.MAX_VALUE)))
        );

        jTabbedPane6.addTab("2-D Colony", jPanel58);

        jTabbedPane7.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane7.setPreferredSize(new java.awt.Dimension(570, 600));

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1385, Short.MAX_VALUE)
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 755, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel65Layout = new javax.swing.GroupLayout(jPanel65);
        jPanel65.setLayout(jPanel65Layout);
        jPanel65Layout.setHorizontalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel65Layout.setVerticalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane7.addTab("Tumor Control Probability", jPanel65);

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1385, Short.MAX_VALUE)
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 755, Short.MAX_VALUE)
        );

        jTabbedPane7.addTab("Activity Histogram", jPanel67);

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1385, Short.MAX_VALUE)
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 755, Short.MAX_VALUE)
        );

        jTabbedPane7.addTab("Radial Histogram", jPanel68);

        jPanel69.setBackground(new java.awt.Color(255, 255, 255));
        jPanel69.setPreferredSize(new java.awt.Dimension(5, 5));

        jButton16.setText("+");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton28.setText("Reset");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setText("-");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jLabel138.setBackground(new java.awt.Color(204, 255, 204));
        jLabel138.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel138.setOpaque(true);
        jLabel138.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel139.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel139.setText("Unlabeled & Dead Cell");

        jLabel140.setBackground(new java.awt.Color(0, 153, 0));
        jLabel140.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel140.setOpaque(true);
        jLabel140.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel141.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel141.setText("Unlabeled & Alive Cell");

        jLabel142.setBackground(new java.awt.Color(255, 204, 204));
        jLabel142.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel142.setOpaque(true);
        jLabel142.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel143.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel143.setText("Labeled & Dead Cell");

        jLabel144.setBackground(new java.awt.Color(255, 0, 0));
        jLabel144.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel144.setOpaque(true);
        jLabel144.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel145.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel145.setText("Labeled & Alive Cell");

        javax.swing.GroupLayout jPanel69Layout = new javax.swing.GroupLayout(jPanel69);
        jPanel69.setLayout(jPanel69Layout);
        jPanel69Layout.setHorizontalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel69Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel143))
                    .addGroup(jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel144, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel145)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel138, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel139))
                    .addGroup(jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel141)))
                .addGap(22, 22, 22))
        );
        jPanel69Layout.setVerticalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel69Layout.createSequentialGroup()
                .addContainerGap(698, Short.MAX_VALUE)
                .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel138, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel69Layout.createSequentialGroup()
                        .addComponent(jLabel141, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton29)
                            .addComponent(jButton28)
                            .addComponent(jButton16)
                            .addComponent(jLabel139, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel69Layout.createSequentialGroup()
                        .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel145, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel144, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel143))))
                .addContainerGap())
        );

        jTabbedPane7.addTab("2-D Colony", jPanel69);

        javax.swing.GroupLayout jPanel70Layout = new javax.swing.GroupLayout(jPanel70);
        jPanel70.setLayout(jPanel70Layout);
        jPanel70Layout.setHorizontalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1390, Short.MAX_VALUE)
            .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1390, Short.MAX_VALUE))
        );
        jPanel70Layout.setVerticalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 794, Short.MAX_VALUE)
            .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel70Layout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addComponent(jTabbedPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addGap(6, 6, 6)))
        );

        jTabbedPane6.addTab("Results", jPanel70);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane6)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane6)
        );

        jTabbedPane1.addTab("Drug Planning", jPanel10);

        jPanel7.setBackground(new java.awt.Color(204, 204, 255));

        jPanel8.setBackground(new java.awt.Color(102, 102, 255));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        jButton2.setText("Print");
        jButton2.setVisible(false);

        jButton6.setText("Save As");
        jButton6.setVisible(false);

        jTextArea1.setColumns(20);
        jTextArea1.setVisible(false);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel22.setText("                                                                                                                                       ");

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jTextArea3.setEditable(false);
        jTextArea3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea3MousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTextArea3);

        jLabel26.setText("                                                                                                                                       ");

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jTextArea5.setEditable(false);
        jTextArea5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea5MousePressed(evt);
            }
        });
        jScrollPane8.setViewportView(jTextArea5);

        jButton13.setText("Save");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(280, 280, 280)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton2)
                                .addComponent(jButton6))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(376, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Output", jPanel3);

        jPanel46.setBackground(new java.awt.Color(204, 204, 255));

        jPanel47.setBackground(new java.awt.Color(102, 102, 255));

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("MIRDcell Version " + version);

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextPane1.setEditable(false);
        jTextPane1.setBackground(new java.awt.Color(204, 204, 255));
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setText("<html>\r<font face=\"tahoma\" size=\"3\">\n  <head>\r\n  </head>\r\n  <body>\r\n    <p style=\"margin-top: 0\" >\r\n      \r<html>\n<ul>\n<li><b>Radionuclide Radiation Data</b>\n<ul>\n<li><b>&beta Full energy spectrum</b>\n<ul>\n<li>The radiation data were taken from MIRD: Radionuclide Data and Decay Schemes. 1st edition. Society of Nuclear Medicine; 1989. The full logarithmically binned beta spectra were used for all beta particles.  Use of the continuous beta spectrum is essential for some applications of cellular dosimetry. Some of the spectra contained in excess of one thousand different radiations for a given radionuclide, many of which are insignificant for internal dosimetry.  Accordingly, only those radiations which contributed greater than 0.1% to the total delta for that particular radiation type were retained. The different radiation types considered separately in this manner were alpha particles, beta particles, and conversion electrons.  No cutoff was implemented for beta particles or Auger electrons.\n</ul>\n<li><b>&beta Average radiation spectrum</b>\n<ul>\n<li>Eckerman KF, Endo A. MIRD: Radionuclide Data and Decay Schemes. 2nd edition. Society of Nuclear Medicine; 2008.<a href=\"http://interactive.snm.org/index.cfm?PageID=7581\"> (http://interactive.snm.org/index.cfm?PageID=7581)</a>.  Data were taken from the files in the accompanying CD.\n</ul>\n</ul>\n<li><b>Cellular S Values</b>\n<ul>\n<li>Not included in the S values are contributions from gamma rays, X rays, neutrons, and recoil energy.\n<li>Liquid water is used as the medium for all source and target regions, and the intervening medium.\n<li><b>Self-Dose S Values</b>\n<ul>\n<li>The self-dose S values are calculated using the methods described in Goddu SM, Howell RW, Bouchet LG, Bolch WE, Rao DV. MIRD Cellular S values: self-absorbed dose per unit cumulated activity for selected radionuclides and monoenergetic electron and alpha particle emitters incorporated into different cell compartments. Reston , VA : Society of Nuclear Medicine; 1997.<a href=\" http://www.ncbi.nlm.nih.gov/pubmed/8295004\"> http://www.ncbi.nlm.nih.gov/pubmed/8295004</a>)\n</ul>\n<li><b>Cross-Dose S Values</b>\n<ul>\n<li>The cross-dose S values are calculated using the methods described in S.M. Goddu, D.V. Rao, R.W. Howell.  Multicellular dosimetry for micrometastases:  dependence of self-dose versus cross-dose to cell nuclei on type and energy of radiation and subcellular distribution of radionuclides, J Nucl Med 35, 521-530 (1994).<a href=\" http://www.ncbi.nlm.nih.gov/pubmed/8113908\"> http://www.ncbi.nlm.nih.gov/pubmed/8113908</a>\n</ul>\n</ul>\n<li><b>Disclaimer</b>\n<ul>\n<li>MIRDcell. This software, released by the MIRD Committee of the Society of Nuclear Medicine and Molecular Imaging, is intended for use by the nuclear medicine research community for the purpose of evaluating cellular radiation absorbed doses and surviving fractions of cells in multicellular clusters. MIRDcell was designed to provide investigators with the capability to compare the effect of several variables on the response of multicellular clusters. The output of this software has not been approved by the US FDA for clinical management of patients. Through access to the software, the investigator agrees to use this software for educational and research purposes only and agrees to hold the inventors, Rutgers University, and the Society of Nuclear Medicine and Molecular Imaging harmless from liability arising from use of this software. Rutgers University holds United States Patent 9,623,262 B2 for this software.\n</ul>\n</ul>\n</ul>\n</ul>\n\n</html>    \n</p>\r\n  </body>\r</font>\n</html>\r\n");
        jTextPane1.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                jTextPane1HyperlinkUpdate(evt);
            }
        });
        jScrollPane5.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel46Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Information", jPanel29);

        jPanel43.setBackground(new java.awt.Color(204, 204, 255));
        jPanel43.setPreferredSize(new java.awt.Dimension(719, 750));

        jPanel44.setBackground(new java.awt.Color(102, 102, 255));

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Contributors to MIRDcell");

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel5.setText("<html><p>Jianchao Wang</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Version 3, 3D Slice, cold region, user-defined activity distribution, improvements and bug fixes</p><p></p><p>Sumudu Katugampola</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Version 3, 3D activity distribution, bio-effect modeling</p><p></p><p>Alex Rosen</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Version 3, complex radiobiology, radially dependent activity distributions, improvements and bug fixes</p><p></p><p>Behrooz Vaziri </p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;GUI for Version 2, 1D, 2D and 3D models</p><p></p><p>Elizabeth Paul</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;GUI for Version 2, 1 D model</p><p></p><p>Han Wu</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;GUI for Version 1, 1 D model</p><p></p><p>Darshan Trivedi</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;GUI for Version 0, 1 D model</p><p></p><p>S Murty Goddu</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Algorithms for calculating S values, compilation of beta full radiation spectra</p><p></p><p>Chris Haydock, Kandula Sastry, and Dandamudi Rao</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Algorithms for calculating S values, conceptual design</p><p></p><p>Atam Dhawan</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Advisory role for software development</p><p></p><p>Roger W Howell</p><p>&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Responsible for overall content</p></html>");
        jLabel5.setMinimumSize(new java.awt.Dimension(67, 100));
        jLabel5.setPreferredSize(new java.awt.Dimension(805, 600));

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addComponent(jPanel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(333, 333, 333))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(39, 39, 39)
                .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Credits", jPanel18);

        getContentPane().add(jTabbedPane1);

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        getContentPane().add(jLabel35);

        jPanel71.setPreferredSize(new java.awt.Dimension(180, 850));

        jLabel126.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel126.setText(version);
        jPanel71.add(jLabel126);

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDcell.png")));
        jLabel45.setPreferredSize(new java.awt.Dimension(150, 150));
        jLabel45.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel45MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel45);

        jLabel125.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDLogo.png")));
        jLabel125.setPreferredSize(new java.awt.Dimension(150, 150));
        jLabel125.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel125MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel125);

        jSeparator6.setMinimumSize(new java.awt.Dimension(50, 50));
        jSeparator6.setPreferredSize(new java.awt.Dimension(200, 10));
        jPanel71.add(jSeparator6);

        jLabel128.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel128.setText("Other MIRD Software");
        jLabel128.setPreferredSize(new java.awt.Dimension(160, 20));
        jPanel71.add(jLabel128);

        jLabel127.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDsoft.png")));
        jLabel127.setToolTipText("");
        jLabel127.setPreferredSize(new java.awt.Dimension(100, 80));
        jLabel127.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel127MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel127);

        jLabel129.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDcalc.png")));
        jLabel129.setPreferredSize(new java.awt.Dimension(100, 80));
        jLabel129.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel129MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel129);

        jLabel130.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDct.png")));
        jLabel130.setPreferredSize(new java.awt.Dimension(100, 80));
        jLabel130.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel130MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel130);
        jLabel130.setVisible(false);

        jLabel131.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/MIRDfit.png")));
        jLabel131.setPreferredSize(new java.awt.Dimension(100, 80));
        jLabel131.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel131MouseClicked(evt);
            }
        });
        jPanel71.add(jLabel131);
        jLabel131.setVisible(false);

        getContentPane().add(jPanel71);
    }// </editor-fold>//GEN-END:initComponents

	private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
		//JW 042721
                if(jList2.getSelectedIndex()==-1) return;
		jLabel18.setText("OUTPUT");
		jLabel22.setText("                                                                                                                                       ");
		//jButton6.setText("                      "); //delete 0702
		//button7.setLabel("Compute");//09/09/2010
		//jButton8.setText("Compute");//09/20/2010
		jList3.setSelectedIndex(jList3.getSelectedIndex());
		//updated by 07/14/09

		String getSelectedIsoName;
		getSelectedIsoName = String.valueOf(jList2.getSelectedValue());
		//get list index
		if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
			cc2.setIsotope("Monoenergetic:  " + getSelectedIsoName);

		} else {
			cc2.setIsotope("Radionuclide:  " + getSelectedIsoName);
		}
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		cc2.repaint();

		//add value to textField
		jTextArea1.setText("");
		jTextArea2.setText("");
		//jTextField1.setText("5"); // 11/17/2010
		//jTextField2.setText("3");
		//jTextField3.setText("10");

		//reset canvas updated 07/06/2009
		// cc2.getSourceCell().setRC(rc.getValue1() * factor);
		// cc2.getTargetCell().setRC(rc.getValue1() * factor);
		// cc2.getSourceCell().setRN(rn.getValue2() * factor);
		// cc2.getTargetCell().setRN(rn.getValue2() * factor);
		//jProgressBar5.setValue(0);
		//cc.resetNuclei();
		//cc.resetDistance();
		//08/05/09
		String directory = "";

		if (jRadioButton3.isSelected() || jRadioButton4.isSelected()) {
			directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/User/";
		}
		else if (getSelectedIsoName.contains(".out") || getSelectedIsoName.contains(".MIRD")) {
			directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/OUTPUT/";
		} else if (jRadioButton13.isSelected()) {//update 07/22/09
			directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/DATA%20FILES/data/";
		} else if (jRadioButton12.isSelected()) {
			directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/MIRD/RAD/";
		}
		String NewFileName;

		System.out.println("selected1:" + getSelectedIsoName);
		String getSelectedIsoName2 = "";

		if (getSelectedIsoName.contains(".dat")) {
			getSelectedIsoName2 = getSelectedIsoName.toLowerCase(Locale.ENGLISH);
			System.out.println("selected2:" + getSelectedIsoName2);
			NewFileName = directory.concat(getSelectedIsoName2);

		} else if (getSelectedIsoName.contains(".out") || getSelectedIsoName.contains(".MIRD")) {
			getSelectedIsoName2 = getSelectedIsoName.toLowerCase(Locale.ENGLISH);
			System.out.println("selected2:" + getSelectedIsoName2);
			NewFileName = directory.concat(getSelectedIsoName2);

		} else if (jRadioButton12.isSelected()) {
			NewFileName = directory.concat(getSelectedIsoName).concat(".RAD");
		} else {
			getSelectedIsoName2 = getSelectedIsoName.toLowerCase(Locale.ENGLISH);
			System.out.println("selected2:" + getSelectedIsoName2);
			NewFileName = directory.concat(getSelectedIsoName2).concat(".dat");

		}
		String monoNewFileName = directory.concat(getSelectedIsoName2);

		System.out.println("selected file URL:" + NewFileName);
		frw.readURLFile(NewFileName, jTextArea2);

		if (getSelectedIsoName.compareTo("new_Alpha.dat") == 0) {
			frw.readFile(monoNewFileName, jTextArea2);
		} else if (getSelectedIsoName.compareTo("new_Electron.dat") == 0) {
			frw.readFile(monoNewFileName, jTextArea2);
		}
                
		try {
			jTextArea2.setCaretPosition(jTextArea2.getLineStartOffset(1));
		} catch (BadLocationException ex) {
                        JOptionPane.showMessageDialog(null, "Beta spectrum not available", "ERROR",JOptionPane.ERROR_MESSAGE);
                        //jRadioButton12.setSelected(true);
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}

		if (jRadioButton13.isSelected()) { //full spectrum
			if (jTextArea2.getLineCount() < 3) {
				return;
			}
			String t = jTextArea2.getText();
			Scanner s = new Scanner(t);
			String l = s.nextLine();
			l = s.nextLine();
			int i = l.length() - 1;
			String word = l.split("\\s+")[4];
			char unit = word.charAt(word.length() - 1);
			Double mag = Double.parseDouble(word.substring(0, word.length() - 1)) * 1.443;
			switch (unit) {
				case 'y':
					mag *= 8760.0;
					break;
				case 'd':
					mag *= 24;
					break;
				case 'h':
					// do nothing because the time is already correct
					break;
				case 'm':
					mag /= 60.0;
					break;
				case 's':
					mag /= 3600.0;
					break;
				default:
					System.err.println("invalid unit please check data");
					break;
			}
			NumberFormat f = new DecimalFormat("0.0000E00");
			jTextField31.setText(f.format(mag));
			jTextField47.setText(f.format(mag));
		} else if (jRadioButton12.isSelected()) { //avg spectrum
			if (jTextArea2.getLineCount() < 3) {
				return;
			}
			String t = jTextArea2.getText();
			Scanner s = new Scanner(t);
			String l = s.nextLine();
			l = s.nextLine();
			String w = l.split("\\s+")[1];
			char unit = w.charAt(w.length() - 1);
                        Double mag = 100.0;
                        try{
                            mag = Double.parseDouble(w.substring(0, w.length() - 1)) * 1.443;
                        } catch(Exception e) {
                            jTextField31.setText("100");
                            jTextField47.setText("100");
                        }
			
			switch (unit) {
				case 'y':
					mag *= 8760.0;
					break;
				case 'd':
					mag *= 24;
					break;
				case 'h':
					// do nothing because the time is already correct
					break;
				case 'm':
					mag /= 60.0;
					break;
				case 's':
					mag /= 3600.0;
					break;
				default:
					System.err.println("invalid unit please check data");
					break;
			}
			NumberFormat f = new DecimalFormat("0.0000E00");
			jTextField31.setText(f.format(mag));
			jTextField47.setText(f.format(mag));
		}
	}//GEN-LAST:event_jList2ValueChanged

	private void jList3ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList3ValueChanged

		//Alex Rosen
		// for complex radiobiological params tab
		if (jList3.getSelectedIndex() == 0) {
			jTable1.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][]{
							{"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
							{"7", "Auger electrons", new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
							{"8", "Alpha particles", new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0)},
							{"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"10", "Fission fragments", new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0)},
							{"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
					},
					new String[]{
                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>","<html> <p>&alpha - cross</p> <p>(C<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(C<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>"
                                        }
			) {
				Class[] types = new Class[]{
						java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
				};
				boolean[] canEdit = new boolean[]{
						false, false, true, true, true, true, true, true
				};

				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		} else if (jList3.getSelectedIndex() == 1) {
			jTable1.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][]{
							{"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.83), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
							{"7", "Auger electrons", new Double(2.3), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
							{"8", "Alpha particles", new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0)},
							{"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
					},
					new String[]{
                                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>","<html> <p>&alpha - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>"
                                        }
			) {
				Class[] types = new Class[]{
						java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
				};
				boolean[] canEdit = new boolean[]{
						false, false, true, true, true, true, true, true, true, true
				};

				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		} else if (jList3.getSelectedIndex() == 2) {
			jTable1.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][]{
							{"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0)},
							{"7", "Auger electrons", new Double(0.025), new Double(0.0), new Double(0.083), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0)},
							{"8", "Alpha particles", new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0)},
							{"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
					},
					new String[]{
                                                "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - cross</p> <p>(Cy<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(Cy<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>"
                                        }
			) {
				Class[] types = new Class[]{
						java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
				};
				boolean[] canEdit = new boolean[]{
						false, false, true, true, true, true, true, true, true, true
				};

				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		} else if (jList3.getSelectedIndex() == 3) {
                    if (!jCheckBox2.isSelected()) { //added by Jianchao Wang 9/18/18
                        JOptionPane.showMessageDialog(jScrollPane4, "Please use complex radiobiological parameters!");
                        jCheckBox2.setSelected(true);
                    } 
			jTable1.setModel(new javax.swing.table.DefaultTableModel(
					new Object[][]{
							{"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.83), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.083), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.025), new Double(0.0)},
							{"7", "Auger electrons", new Double(2.3), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.230), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.025), new Double(0.0)},
							{"8", "Alpha particles", new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(1.4), new Double(0.0), new Double(0.14), new Double(0.0)},
							{"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
							{"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
					},
					new String[]{
                                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(N<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - cross</p> <p>(Cy<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - cross</p> <p>(Cy<sub>i</sub>&larr;C<sub>j</sub>)</p> <p>(Gy<sup>-2</sup>)</p>"
                                        }
			) {
				Class[] types = new Class[]{
						java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
				};
				boolean[] canEdit = new boolean[]{
						false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
				};

				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			});
		}


		//Alex Rosen
		// this is for the new cell source/target page
		cellCanvasInfoNew1.setTarget(jList3.getSelectedIndex());
		if (jList3.getSelectedIndex() == 0) {
			jLabel107.setText("% activity in Cell");
			jLabel106.setVisible(false);
			jTextField16.setText("" + (100D - Double.parseDouble(jTextField17.getText())));
			jTextField15.setText("0");
			jTextField15.setVisible(false);
                         
		} else {
			jLabel107.setText("% activity in Cytoplasm");
			jLabel106.setVisible(true);
                        jTextField16.setText ("0");
			jTextField15.setText("100");
			jTextField15.setVisible(true);
		}
                
                double x1, x2, x3;

		try {
			x1 = Double.parseDouble(jTextField15.getText());
			x2 = Double.parseDouble(jTextField16.getText());
			x3 = Double.parseDouble(jTextField17.getText());

			double total = x1 + x2 + x3;
			jLabel109.setText("Total: " + total);
			if (total != 100) {
				jLabel109.setBackground(Color.PINK);
			}
		} catch (Exception e) {
                    x1 = 0;
                    x2 = 0;
                    x3 = 100;
		}
                
                //JCW 9/1/20 Render color when switching to cell as target
                
                cellCanvasInfoNew1.setFractions(x1, x2, x3);
                
                      
		cc2.setProcess(jList3.getSelectedIndex());
                cc2.updateSource(x1, x2, x3);
		cc2.repaint();

		//07/02 new get dose
		String getTextArea1 = jTextArea1.getText();

		ArrayList list = new ArrayList();
		StringTokenizer tokens = new StringTokenizer(getTextArea1, "\n");
		while (tokens.hasMoreTokens()) {
			list.add(tokens.nextElement());
		}

		c = Integer.parseInt(jTextField1.getText());
		n = Integer.parseInt(jTextField2.getText());
		d = Integer.parseInt(jTextField3.getText());
		maxRow = jTextArea1.getLineCount() - 1;// 09/09/2010
		String selfDose, crossDose;
		//get rc/rn/dist value
		System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d);

		//updated 07/14/09
		//String getSelectedIsoName ="";
		iso = String.valueOf(jList2.getSelectedValue());
		System.out.println("selected iso file name: " + iso);
		pro = jList3.getSelectedIndex();
                cc2.setProcess(pro);
		
	}//GEN-LAST:event_jList3ValueChanged

	private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
		//update by 07/17/09
		cc2.setIsotope("Radionuclide:  ");
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		cc2.repaint();

		//add value to textField
		jTextArea1.setText("");
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Monoenergetic Alpha Particle ");
		} else if (jRadioButton2.isSelected()) {
			jTextArea2.setText("Monoenergetic electron selected");
		}
		jTextField10.setText("");

		//reset canvas updated 07/06/2009
		if (evt.getStateChange() == ItemEvent.SELECTED) {

			jRadioButton1.setSelected(false);
			jRadioButton12.setSelected(false);
			jRadioButton13.setSelected(false);
			jRadioButton3.setSelected(false);
			jRadioButton4.setSelected(false);
			jTextField10.setEnabled(true);
			jTextField11.setEnabled(true);
			jTextField10.setEditable(true);
			jTextField11.setEditable(true);

			jTextField12.setText("");
			jTextField12.setEditable(false);
			jTextField13.setText("");
			jTextField13.setEditable(false);
			jTextField4.setText("");
			jTextField4.setEditable(false);
			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton13.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton2.setBackground(Color.green);

			//0702
			cc2.setIsotope("Radionuclide:  " + jRadioButton2.getText());
			cc2.repaint();

			if (evt.getSource() == jRadioButton2) {
				String[] isotopes = {"new_Electron"};

				//updated 07/14/09
				Object[] listData;
				listData = (Object[]) isotopes;
				jList2.setListData(listData);

				System.out.println("test jRadioButton2 status = " + jRadioButton2.isSelected());
			}
		} else {
			jRadioButton1.setEnabled(true);
			jRadioButton12.setEnabled(true);
			jRadioButton13.setEnabled(true);
			jRadioButton3.setEnabled(true);
			jRadioButton4.setEnabled(true);
			jTextField10.setEnabled(false);
			jTextField11.setEnabled(false);
			jTextField10.setEditable(false);
			jTextField11.setEditable(false);
			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(newBack);

			//button7.setLabel("Compute");
			jLabel18.setText("OUTPUT");
			jLabel22.setText("                                                                                                                                       ");
			//jButton8.setText("Compute");//09/20/2010
			jComboBox1.setSelectedIndex(0);

			String[] isotopes = {"a1KeV", "a1KeV_2", "a1KeV_3", "a1MeV_alpha", "a1MeV_2_alpha", "a1MeV_3_alpha", "alpha-mono", "a1000", "a5000", "a10000"};

			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);

		}
	}//GEN-LAST:event_jRadioButton2ItemStateChanged

	private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
		//update by 07/17/09
		cc2.setIsotope("Radionuclide:  ");
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		cc2.repaint();

		//add value to textField
		jTextArea1.setText("");
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Monoenergetic Alpha Particle ");
		} else if (jRadioButton2.isSelected()) {
			jTextArea2.setText("Monoenergetic electron selected");
		}

		jTextField10.setText("");
		//reset canvas updated 07/06/2009
		if (evt.getStateChange() == ItemEvent.SELECTED) {

			jRadioButton2.setSelected(false);
			jRadioButton12.setSelected(false);
			jRadioButton13.setSelected(false);
			jRadioButton3.setSelected(false);
			jRadioButton4.setSelected(false);
			jTextField10.setEnabled(true);
			jTextField11.setEnabled(true);
			jTextField10.setEditable(true);
			jTextField11.setEditable(true);
			jRadioButton13.setBackground(newBack); // 11/17/2010
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton1.setBackground(Color.green);
			jTextField12.setText("");
			jTextField12.setEditable(false);
			jTextField13.setText("");
			jTextField13.setEditable(false);
			jTextField4.setText("");
			jTextField4.setEditable(false);

			if (evt.getSource() == jRadioButton1) {
				//add 07/29/10. revised 11/29/2010
				//if (jRadioButton1.isSelected()) {
				//   jTextArea2.setText("Absorbed Source used: Water");
				//}

				String[] isotopes = {"new_Alpha"};

				//0702
				cc2.setIsotope("Radionuclide:  " + jRadioButton1.getText());
				//updated 07/14/09
				Object[] listData;
				listData = (Object[]) isotopes;
				jList2.setListData(listData);

				System.out.println("test checkbox1 status = " + jRadioButton1.isSelected());
			}
			//updated 07/02/10
			//new UnderConstructionMsg(new javax.swing.JFrame(), true).setVisible(true);

		} else {

			jRadioButton2.setEnabled(true);
			jRadioButton12.setEnabled(true);
			jRadioButton13.setEnabled(true);
			jRadioButton3.setEnabled(true);
			jRadioButton4.setEnabled(true);
			jTextField10.setEnabled(false);
			jTextField11.setEnabled(false);
			jTextField10.setEditable(false);
			jTextField11.setEditable(false);
			jRadioButton13.setBackground(newBack); // 11/17/2010
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton1.setBackground(newBack);

			// button7.setLabel("Compute");
			jLabel18.setText("OUTPUT");
			jLabel22.setText("                                                                                                                                       ");

			//jButton8.setText("Compute");//09/20/2010
			String[] isotopes = {"a1KeV", "a1KeV_2", "a1KeV_3", "a1MeV_alpha", "a1MeV_2_alpha", "a1MeV_3_alpha", "alpha-mono", "a1000", "a5000", "a10000"};

			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);

		}

	}//GEN-LAST:event_jRadioButton1ItemStateChanged

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
		//08/23/09

		if (jTextArea2.getText().contains("Enter the Name")) {
			String Name = jTextField4.getText();
			jTextArea2.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide:" + Name + "   Half-Life:         Number: " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");
		}
		String radType = jComboBox1.getSelectedItem().toString();
		//revised 10/25/2010
		String en1 = jTextField12.getText();
		String yield1 = jTextField13.getText();
		/**
		 * ****************************
		 * get number of radiation types, find num position (line (5,1)), add
		 * number to that position format en1, yield1 into 0.0000E00 delta =
		 * 2.13*en1*yield1 format: "id Radiation yield/decay Energy Detal"
		 */
		int num1 = jTextArea2.getLineCount() - 6;
		NumberFormat formatter = new DecimalFormat("0.00000E00");
		double en11 = Double.parseDouble(en1);
		double yield11 = Double.parseDouble(yield1);
		//double delta11 = 2.13*en11*yield11;
		String en111 = formatter.format(en11);
		String yield111 = formatter.format(yield11);
		//String delta111 = formatter.format(delta11);
		//String id = String.valueOf(num1);
		/**
		 * **********
		 * ICODE for radiation types Table A.2. Description of ICODE Variable.
		 * ICODE Mnemonic for ICODE Description 1 G Gamma rays 2 X x-rays 3 AQ
		 * Annihilation quanta 4 B+ Beta+ particles 5 B- Beta√¢‚Ç¨‚Äú particles
		 * 6 IE Internal conversion Electrons 7 AE Auger electrons 8 A Alpha
		 * particles 9 AR Daughter Recoil (Alpha decay) 10 FF Fission fragments
		 * 11 N Neutrons Prompt and delayed radiations of spontaneous fission.
		 */
		String icode = "", radCode = "";
		if (radType.contains("Gamma rays")) {
			icode = String.valueOf(1);
			radCode = "G";
		} else if (radType.contains("X-rays")) {
			icode = String.valueOf(2);
			radCode = "X";
		} else if (radType.contains("Annihilation quanta")) {
			icode = String.valueOf(3);
			radCode = "AQ";
		} else if (radType.contains("Beta+")) {
			icode = String.valueOf(4);
			radCode = "B+";
		} else if (radType.contains("Beta-")) {
			icode = String.valueOf(5);
			radCode = "B-";
		} else if (radType.contains("Internal conversion")) {
			icode = String.valueOf(6);
			radCode = "IE";
		} else if (radType.contains("Auger electrons")) {
			icode = String.valueOf(7);
			radCode = "AE";
		} else if (radType.equals("Alpha")) {
			icode = String.valueOf(8);
			radCode = "A";
		} else if (radType.contains("Daughter Recoil")) {
			icode = String.valueOf(9);
			radCode = "AR";
		} else if (radType.contains("Fission fragments")) {
			icode = String.valueOf(10);
			radCode = "FF";
		} else if (radType.contains("Neutrons")) {
			icode = String.valueOf(11);
			radCode = "N";
		}
		jTextArea2.append(icode + "   " + yield111 + "      " + en111 + "       " + radCode + "\n");
	}//GEN-LAST:event_jButton5ActionPerformed

	private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
		//07/24/09
		//if Create selected
		if (evt.getStateChange() == ItemEvent.SELECTED) {

			jRadioButton1.setSelected(false); // 11/17/2010
			jRadioButton2.setSelected(false);
			jRadioButton4.setSelected(false);
			jRadioButton12.setSelected(false);
			jRadioButton13.setSelected(false);
			jButton9.setEnabled(true);
			//jTextField10.setEditable(false);
			//jTextField11.setEditable(false);
			jComboBox1.setEnabled(true);
			jList2.setEnabled(false);
			jTextField13.setEnabled(true);
			jTextField12.setEnabled(true);
			jTextField4.setEnabled(true);

			jTextField12.setEditable(true);

			jTextField13.setEditable(true);

			jTextField4.setEditable(true);

			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton13.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton3.setBackground(Color.green);

			//updated user-created 11/01/2010
			jTextArea2.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide: Enter the Name        Half-Life:   " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");
			//updated 07/20/09

			if (evt.getSource() == jRadioButton3) {
				jButton5.setEnabled(true);
				jButton9.setEnabled(false);
				//jRadioButton12.setEnabled(false);
				jButton10.setEnabled(true);
				jButton11.setEnabled(true);
			}

		} else {
			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(newBack);

			jTextArea2.setText("");
			jTextArea1.setText("");
			jTextField10.setEnabled(false);
			jTextField11.setEnabled(false);
			jTextField10.setEditable(false);
			jTextField11.setText("1");
			jRadioButton2.setEnabled(true);
			jRadioButton12.setEnabled(true);
			jRadioButton13.setEnabled(true);
			jRadioButton4.setEnabled(true);
			jRadioButton1.setEnabled(true);
			jButton5.setEnabled(false);
			jButton10.setEnabled(false);
			jComboBox1.setEnabled(false);

			jLabel18.setText("                                                           OUTPUT");
			jLabel22.setText("                                                                                                                                       ");
			String[] isotopes = {};
			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);

		}
	}//GEN-LAST:event_jRadioButton3ItemStateChanged

	private void jRadioButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton4ItemStateChanged
		// update 07/27/09
		//if selected
		if (evt.getStateChange() == ItemEvent.SELECTED) {

			jRadioButton1.setSelected(false);
			jRadioButton2.setSelected(false);
			jRadioButton3.setSelected(false);
			jRadioButton13.setSelected(false);
			jRadioButton12.setSelected(false);
			jTextField10.setEnabled(false);
			jTextField11.setEnabled(false);
			jTextField10.setEditable(false);
			jTextField11.setEditable(false);
			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton13.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton4.setBackground(Color.green);

			if (evt.getSource() == jRadioButton4) {
				jButton9.setEnabled(true);
				jButton5.setEnabled(false);
				jList2.setEnabled(false);
				//jRadioButton12.setEnabled(false);
				jButton10.setEnabled(false);
				jButton11.setEnabled(false);
			}

		} else {
			// 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(newBack);

			jTextArea2.setText("");
			jTextArea1.setText("");
			jButton5.setEnabled(false);
			jButton9.setEnabled(false);
			jRadioButton2.setEnabled(true);
			jRadioButton12.setEnabled(true);
			jRadioButton13.setEnabled(true);
			jRadioButton1.setEnabled(true);
			jRadioButton3.setEnabled(true);

			jLabel18.setText("                                                           OUTPUT");
			jLabel22.setText("                                                                                                                                       ");
			String[] isotopes = {};

			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);

		}
	}//GEN-LAST:event_jRadioButton4ItemStateChanged

	private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		text1 = Integer.parseInt(jTextField1.getText());
		text2 = Integer.parseInt(jTextField2.getText());
		text3 = Integer.parseInt(jTextField3.getText());

		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);
		cc2.repaint();
		
		if (text1 > text2 + 1) {
			text1--;
			jTextField1.setText(Integer.toString(text1));//number in jTextField1 decreased once button clicked

			jTextField3.setText(Integer.toString(2 * text1));
			jTextField3.setText(Integer.toString(2 * text1));
			jTextField3.setText(Integer.toString(2 * text1));
			

			// Alex Rosen 7/20/2017
			// adding this for the new cell source/target layout stuff
			cellCanvasInfoNew1.setRC(text1);

			cc2.Dist = 2 * text1;
			cc2.source.setRC(text1 * cc2.factor);
			cc2.target.setRC(text1 * cc2.factor);
			cc2.source.setRN(text2 * cc2.factor);
			cc2.target.setRN(text2 * cc2.factor);
			cc2.repaint();

			//070610
		}
		jTextArea1.setText(""); //clear text area
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Absorbed Source used: Water");
		}
		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");
	}//GEN-LAST:event_button5ActionPerformed

	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);
		cc2.repaint();

		text2 = Integer.parseInt(jTextField2.getText());
		if (text2 > minimum + 1) {
			text2--;
			jTextField2.setText(Integer.toString(text2));

			// Alex Rosen 7/20/2017
			// adding this for the new cell source/target layout stuff
			cellCanvasInfoNew1.setRN(text2);

			cc2.Dist = 2 * text1;
			cc2.source.setRN(text2 * cc2.factor);
			cc2.target.setRN(text2 * cc2.factor);
			cc2.repaint();

		} else if (text2 == 1) {
			text2 = 1;
			jTextField2.setText(Integer.toString(text2));
		}

		//070610
		jTextArea1.setText(""); //clear text area
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Absorbed Source used: Water");
		}

		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");
	}//GEN-LAST:event_button3ActionPerformed

	private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);
		cc2.repaint();

		text1 = Integer.parseInt(jTextField1.getText());
		text3 = Integer.parseInt(jTextField3.getText());

		if (text3 > 2 * text1) {
			text3--;
			jTextField3.setText(Integer.toString(text3));
			cc2.Dist = text3;
			cc2.repaint();

		} else if (text3 == 2 * text1) {
			text3 = 2 * text1;
			jTextField3.setText(Integer.toString(text3));
			cc2.Dist = text3;
			cc2.repaint();

		}

		//071310 set default line to view in textarea
		try {
			if (!jTextArea1.getText().isEmpty()) {
				jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(text3 - 1));
			}

		} catch (BadLocationException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		ArrayList list = new ArrayList();
		StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
		while (tokens.hasMoreTokens()) {
			list.add(tokens.nextElement());
		}
		//System.out.println("0702 area1 list: "+list.get(0).toString().trim().split("\\s+")[1]);

		c = Integer.parseInt(jTextField1.getText());
		n = Integer.parseInt(jTextField2.getText());
		d = text3;
		maxRow = jTextArea1.getLineCount() - 1;// 072010

		String selfDose;
		String crossDose;
		//get rc/rn/dist value
		System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d + ", maxRow = " + maxRow);
		
		iso = String.valueOf(jList2.getSelectedValue());
		System.out.println("selected iso file name: " + iso);
		pro = jList3.getSelectedIndex();
		//readData = new ReadInData(iso);
		
		try {
			//if(d< 1303){
			if (d < maxRow + 1) {
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				crossDose = list.get(d - 1).toString().trim().split("\\s+")[pro + 1];
				cc2.setSelfDose("Self Absorbed Dose to Source Cell : " + selfDose + " Gy/Bq-s");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : " + crossDose + " Gy/Bq-s");
				cc2.repaint();

				//add value to textField
			} else {
				//modified 08/05/09
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				cc2.setSelfDose("Self Absorbed Dose to Source Cell : " + selfDose + " Gy/Bq-s");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : 0.00E00 Gy/Bq-s/ Exceeds range of particle(s)");
				cc2.repaint();

			}
		} catch (Exception e) {
			cc2.setSelfDose("Self Absorbed Dose to Source Cell : Check Input");
			cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : Check Input");
			cc2.repaint();
		}
	}//GEN-LAST:event_button6ActionPerformed

	private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {
		//07/07/09
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		d = Integer.parseInt(jTextField3.getText());
		c = Integer.parseInt(jTextField1.getText());
		n = Integer.parseInt(jTextField1.getText());
		cc2.Dist = Integer.parseInt(jTextField3.getText());

		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			
			ArrayList list = new ArrayList();
			StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
			while (tokens.hasMoreTokens()) {
				list.add(tokens.nextElement());
			}

			c = Integer.parseInt(jTextField1.getText());
			n = Integer.parseInt(jTextField2.getText());
			d = Integer.parseInt(jTextField3.getText());
			maxRow = jTextArea1.getLineCount() - 1;// 072010

			String selfDose, crossDose;
			//get rc/rn/dist value
			System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d);
			
			iso = String.valueOf(jList2.getSelectedValue());
			System.out.println("selected iso file name: " + iso);
			pro = jList3.getSelectedIndex();
			//readData = new ReadInData(iso);
			
			try {
				if (d < maxRow + 1) {
					selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
					crossDose = list.get(d - 1).toString().trim().split("\\s+")[pro + 1];
					cc2.setSelfDose("Self Absorbed Dose to Source Cell : " + selfDose + " Gy/Bq-s");
					cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : " + crossDose + " Gy/Bq-s");
					cc2.repaint();

				} else {
					//modified 08/05/09
					selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
					cc2.setSelfDose("Self Absorbed Dose to Source Cell : " + selfDose + " Gy/Bq-s");
					cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : 0.00E00 Gy/Bq-s/ Exceeds range of particle(s)");
					cc2.repaint();

				}
			} catch (Exception e) {
				cc2.setSelfDose("Self Absorbed Dose to Source Cell : Check Input");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : Check Input");
				cc2.repaint();
			}
		}
	}

	private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {
		//07012010
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			//070610
			jTextArea1.setText(""); //clear text area
			if (jRadioButton1.isSelected()) {
				jTextArea2.setText("Absorbed Source used: Water");
			}//add 07/29/10
			jLabel18.setText("                                                           OUTPUT");
			jLabel22.setText("                                                                                                                                       ");

		}//end if key == ENTER
	}

	private void button8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button8ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);
		cc2.repaint();

		text1 = Integer.parseInt(jTextField1.getText());
		text2 = Integer.parseInt(jTextField2.getText());
		if (text2 < text1 - 1) {
			text2++;
			jTextField2.setText(Integer.toString(text2));

			// Alex Rosen 7/20/2017
			// adding this for the new cell source/target layout stuff
			cellCanvasInfoNew1.setRN(text2);

			cc2.Dist = 2 * text1;
			cc2.source.setRN(text2 * cc2.factor);
			cc2.target.setRN(text2 * cc2.factor);
			cc2.repaint();

		} else if (text2 == text1) {
			text2 = text1;
			jTextField2.setText(Integer.toString(text2));
		}//end if

		jTextArea1.setText(""); //clear text area
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Absorbed Source used: Water");
		}

		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");
	}//GEN-LAST:event_button8ActionPerformed

	private void button9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button9ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		text1 = Integer.parseInt(jTextField1.getText());

		text3 = Integer.parseInt(jTextField3.getText());
		
		text1++;
		jTextField1.setText(Integer.toString(text1));
		jTextField3.setText(Integer.toString(2 * text1));

		text2 = Integer.parseInt(jTextField2.getText());
		if (text2 > text1 - 1) {
			jTextField2.setText(Integer.toString(text1 - 1));
		}

		//when user's distance input < 2rc, set distance = 2rc 07/27/10
		jTextField3.setText(Integer.toString(2 * text1));
		jTextField35.setText(Integer.toString(2 * text1));
		jTextField36.setText(Integer.toString(2 * text1));
		

		// Alex Rosen 7/20/2017
		// adding this for the new cell source/target layout stuff
		cellCanvasInfoNew1.setRC(text1);

		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);

		cc2.Dist = 2 * text1;
		cc2.source.setRC(text1 * cc2.factor);
		cc2.target.setRC(text1 * cc2.factor);
		cc2.source.setRN(text2 * cc2.factor);
		cc2.target.setRN(text2 * cc2.factor);
		cc2.Dist = 2 * text1;
		cc2.repaint();

		jTextArea1.setText(""); //clear text area
		if (jRadioButton1.isSelected()) {
			jTextArea2.setText("Absorbed Source used: Water");
		}

		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");
	}//GEN-LAST:event_button9ActionPerformed

	private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
		cc2.setSelfDose("");
		cc2.setCrossDose("");
		double N = Double.parseDouble(jTextField15.getText());
		double Cy = Double.parseDouble(jTextField16.getText());
		double CS = Double.parseDouble(jTextField17.getText());
		cc2.updateSource(N, Cy, CS);

		text3 = Integer.parseInt(jTextField3.getText());
		text3++;
		cc2.Dist = text3;
		cc2.repaint();
		jTextField3.setText(Integer.toString(text3));
		jTextArea1.requestFocus();

		//071310 set default line to view in textarea
		try {
			if (!jTextArea1.getText().isEmpty()) {
				jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(text3 - 1));
			}

		} catch (BadLocationException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		ArrayList list = new ArrayList();
		StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
		while (tokens.hasMoreTokens()) {
			list.add(tokens.nextElement());
		}

		c = Integer.parseInt(jTextField1.getText());
		n = Integer.parseInt(jTextField2.getText());
		d = text3;
		maxRow = jTextArea1.getLineCount() - 1;// 072010

		String selfDose, crossDose;
		//get rc/rn/dist value
		System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d + ", maxRow" + maxRow);

		//updated 07/14/09
		//String getSelectedIsoName ="";
		iso = String.valueOf(jList2.getSelectedValue());
		System.out.println("selected iso file name: " + iso);
		pro = jList3.getSelectedIndex();
		//readData = new ReadInData(iso);
		
		try {
			//if(d< 1303){
			if (d < maxRow + 1) {
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				crossDose = list.get(d - 1).toString().trim().split("\\s+")[pro + 1];
				cc2.setSelfDose("Self Absorbed Dose to Source Cell : " + selfDose + " Gy/Bq-s");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : " + crossDose + " Gy/Bq-s");
				cc2.repaint();

				//add value to textField
			} else {
				//modified 08/05/09
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				cc2.setSelfDose("Self AbsorbedDose to Source Cell : " + selfDose + " Gy/Bq-s");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : 0.00E00 Gy/Bq-s/ Exceeds range of particle(s)");
				cc2.repaint();

			}
		} catch (Exception e) {
			cc2.setSelfDose("Self Absorbed Dose to Source Cell : Check Input");
			cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : Check Input");
			cc2.repaint();
		}
	}//GEN-LAST:event_button4ActionPerformed

	private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {
		//07012010
		cc2.setSelfDose("");
		cc2.setCrossDose("");

		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			//070610
			jTextArea1.setText(""); //clear text area
			if (jRadioButton1.isSelected()) {
				jTextArea2.setText("Absorbed Source used: Water");
			} //add 07/29/10
			jLabel18.setText("                                                           OUTPUT");
			jLabel22.setText("                                                                                                                                       ");
		}

		cc2.repaint();
	}

	private void jTextField10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField10MouseEntered

	}//GEN-LAST:event_jTextField10MouseEntered

	private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
		try {
			jTextArea5.print();
		} catch (PrinterException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}//GEN-LAST:event_jMenuItem4ActionPerformed

	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
		jTextArea5.copy();
	}//GEN-LAST:event_jMenuItem2ActionPerformed

	private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
		jTextArea3.copy();
	}//GEN-LAST:event_jMenuItem6ActionPerformed

	private void jTextField10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField10MousePressed
		jTextField10.setBackground(Color.white);
		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");

	}//GEN-LAST:event_jTextField10MousePressed

	private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
		int option = jFileChooser1.showSaveDialog(Home1.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			try {
				File file = jFileChooser1.getSelectedFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				jTextArea5.write(out);
				out.flush();
				out.close(); //writes the content to the file

				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}//end if

	}//GEN-LAST:event_jMenuItem9ActionPerformed

	private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
		new InstructionDialog(new javax.swing.JFrame(), true).setVisible(true);
	}//GEN-LAST:event_jButton8ActionPerformed

	private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
		int option = jFileChooser1.showSaveDialog(Home1.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			try {
				File file = jFileChooser1.getSelectedFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				jTextArea3.write(out);
				out.flush();
				out.close(); //writes the content to the file

				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}//end if
	}//GEN-LAST:event_jMenuItem10ActionPerformed

	private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
		jTextArea2.setText("");
		int option = jFileChooser1.showOpenDialog(Home1.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			FileReader reader = null;
			String readLine;
			try {
				File file = jFileChooser1.getSelectedFile();
                                fileName = file.getAbsolutePath();
				FileInputStream fis = new FileInputStream(file);
				//BufferedWriter out = new BufferedWriter(new FileWriter(file));
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				while ((readLine = in.readLine()) != null) {
					jTextArea2.append(readLine + "\n");
					if (readLine.contains("New-Radionuclide:")) {
						jTextField4.setText(readLine.substring(readLine.indexOf(":") + 1, readLine.indexOf(" ", readLine.indexOf(":") + 2)));
					}
				}
				in.close(); //writes the content to the file
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}//end if
	}//GEN-LAST:event_jButton9ActionPerformed

	private void jRadioButton12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton12ItemStateChanged
		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");

		jTextArea1.setText("");
		jTextArea2.setText("");

		if (evt.getStateChange() == ItemEvent.SELECTED) {
			cc2.setIsotope("Radionuclide:  ");
			cc2.setSelfDose("");
			cc2.setCrossDose("");
			cc2.repaint();

			//disable jCheckBox2, jCheckBox3
			jList2.setEnabled(true);
			jRadioButton1.setSelected(false); // 11/17/2010
			jRadioButton2.setSelected(false);
			jRadioButton3.setSelected(false);
			jRadioButton4.setSelected(false);
			jRadioButton13.setSelected(false);
			jTextField10.setEditable(false);
			jTextField11.setText("");
			jTextField11.setEditable(false);
			jTextField12.setText("");
			jTextField12.setEditable(false);
			jTextField13.setText("");
			jTextField13.setEditable(false);
			jTextField4.setText("");
			jTextField4.setEditable(false);

			//jTextField12.setEditable(false);
			//update: disable radiation type function 0702
			//jLabel19.setEnabled(false);
			jComboBox1.setEnabled(false);
			jButton5.setEnabled(false);
			//jRadioButton13.setEnabled(false); // 11/17/2010
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton13.setBackground(newBack);
			jRadioButton12.setBackground(Color.green);

			if (evt.getSource() == jRadioButton12) {
				//02/09/2014 add parent/daughters
				String[] isotopes = {"Ac-225", "Ac-225+daughters", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-211+daughters", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-213+daughters", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-223+daughters", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

				//if (evt.getSource() == jRadioButton12) {
				//revised 11/21/2010
				//  String[] isotopes = {"Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};
				//updated 07/14/09
				Object[] listData;
				listData = (Object[]) isotopes;
				jList2.setListData(listData);

			}
			//updated 07/02/10  disabled on 07152010
			//new UnderConstructionMsg(new javax.swing.JFrame(), true).setVisible(true);

		} else {
			//reset
//            button7.setLabel("Compute");
			//jButton8.setText("Compute");//09/20/2010
			cc2.setIsotope("Radionuclide:  ");
			cc2.setSelfDose("");
			cc2.setCrossDose("");
			cc2.repaint();

			//jTextField12.setEditable(true);
			//update: disable radiation type function 0702
			//jList2.setEnabled(false);
			jRadioButton13.setEnabled(true);
			jRadioButton12.setEnabled(true);
			jRadioButton1.setEnabled(true);
			jRadioButton2.setEnabled(true);
			jRadioButton3.setEnabled(true);
			jRadioButton4.setEnabled(true);
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(newBack);
			//revised 11/21/2010
			String[] isotopes = {"Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);
		}

	}//GEN-LAST:event_jRadioButton12ItemStateChanged

	private void jRadioButton13ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton13ItemStateChanged
		jLabel18.setText("                                                           OUTPUT");
		jLabel22.setText("                                                                                                                                       ");

		//add value to textField
		jTextArea1.setText("");
		jTextArea2.setText("");
		
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			cc2.setIsotope("Radionuclide:  ");
			cc2.setSelfDose("");
			cc2.setCrossDose("");
			cc2.repaint();

			jList2.setEnabled(true);

			jRadioButton1.setSelected(false); // 11/17/2010
			jRadioButton2.setSelected(false);
			jRadioButton3.setSelected(false);
			jRadioButton4.setSelected(false);
			jRadioButton12.setSelected(false);
			jTextField10.setEditable(false);
			jTextField11.setText("");
			jTextField11.setEditable(false);
			jTextField12.setText("");
			jTextField12.setEditable(false);
			jTextField13.setText("");
			jTextField13.setEditable(false);
			jTextField4.setText("");
			jTextField4.setEditable(false);

			jComboBox1.setEnabled(false);
			jButton5.setEnabled(false);
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(Color.green);

			if (evt.getSource() == jRadioButton13) {
				String[] isotopes = {"Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

				Object[] listData;
				listData = (Object[]) isotopes;
				jList2.setListData(listData);

			}
		} else {
			cc2.setIsotope("Radionuclide:  ");
			cc2.setSelfDose("");
			cc2.setCrossDose("");
			cc2.repaint();
			
			jRadioButton12.setEnabled(true);
			jRadioButton1.setEnabled(true);
			jRadioButton2.setEnabled(true);
			jRadioButton3.setEnabled(true);
			jRadioButton4.setEnabled(true);
			jRadioButton1.setBackground(newBack);
			jRadioButton2.setBackground(newBack);
			jRadioButton3.setBackground(newBack);
			jRadioButton4.setBackground(newBack);
			jRadioButton12.setBackground(newBack);
			jRadioButton13.setBackground(newBack); // 11/17/2010

			//revised 11/21/2010
			String[] isotopes = {"Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

			//updated 07/14/09
			Object[] listData;
			listData = (Object[]) isotopes;
			jList2.setListData(listData);
		}
	}//GEN-LAST:event_jRadioButton13ItemStateChanged

	private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
		jTextArea2.append("END RADIATION RECORDS");
	}//GEN-LAST:event_jButton10ActionPerformed

	private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
		int option = jFileChooser1.showSaveDialog(Home1.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			try {
				File file = jFileChooser1.getSelectedFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				jTextArea2.write(out);
				out.flush();
				out.close();
				
				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}//end if
	}//GEN-LAST:event_jButton11ActionPerformed

	//10/25/2010 add image
	private void jTextArea2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea2MousePressed
		if (evt.getButton() == MouseEvent.BUTTON3) {
			jPopupMenu3.show(evt.getComponent(),
					evt.getX(), evt.getY());
		}
	}//GEN-LAST:event_jTextArea2MousePressed

	private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
		jTextArea1.setText(""); //clear text area

		if (jRadioButton3.isSelected()) {
			//updated user-created 11/01/2010
			jTextArea2.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide:          Half-Life:         Number: " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");
		} else {
			jTextArea2.setText("This area will be populated upon" + "\n" + "selecting Source Radiation below.");
		}
		jList2.clearSelection(); //07012010
		jList3.setSelectedIndex(0);
	}//GEN-LAST:event_jButton12ActionPerformed
        
        DefaultListSelectionModel shapeModel = new DefaultListSelectionModel();
        
	private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed

		jTextField25.setText("");
		JComboBox shapes = (JComboBox) evt.getSource();
		String shape = (String) shapes.getSelectedItem();
		jLabel56.setText(""); 
                int labeling = jComboBox5.getSelectedIndex();
		if ("Sphere".equals(shape)) {                   
                    jLabel42.setVisible(false);
                    jTextField27.setVisible(false);
                    jLabel17.setText("Radius (um):");
                    jTextField25.setText("");
                    jTextField25.setEditable(false);
                    //jianchao 5/4/20 radial histogram input: # cell...
                    jTextField18.setVisible(false);
                    jLabel111.setVisible(false);

                    DefaultListSelectionModel model = new DefaultListSelectionModel();
                    model.addSelectionInterval(0, 8);
                    RenderComboBox enableRenderer = new RenderComboBox(model);
                    jComboBox5.setRenderer(enableRenderer);
                    shapeModel = model;
		} else if ("Cone".equals(shape)) {
                        jLabel42.setVisible(true);
                        jLabel42.setText("Height (um):");
                        jTextField27.setVisible(true);
                        if (labeling == 7){
                            jComboBox5.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select that labeling for Cone", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else {
                            jLabel17.setText("Radius (um):");
                            jTextField25.setText("");
                            jTextField25.setEditable(false);
                            jTextField18.setVisible(true);
                            jLabel111.setVisible(true);
                            //disable certain labeling methods
                            DefaultListSelectionModel model = new DefaultListSelectionModel();
                            model.addSelectionInterval(0, 6);
                            //model.addSelectionInterval(6, 6);
                            RenderComboBox enableRenderer = new RenderComboBox(model);
                            jComboBox5.setRenderer(enableRenderer);
                            shapeModel = model;
                        }
			
		} else if ("Rod".equals(shape)) {
                        jLabel42.setVisible(true);
                        jLabel42.setText("Height (um):");
                        jTextField27.setVisible(true);
                        if (labeling == 7 ){
                            jComboBox5.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select that labeling for Rod", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else {
                            jLabel17.setText("Radius (um):");
                            jTextField25.setText("");
                            jTextField25.setEditable(false);
                            ////jianchao 5/4/20 radial histogram input: # cell...
                            jTextField18.setVisible(true);
                            jLabel111.setVisible(true); 

                            DefaultListSelectionModel model = new DefaultListSelectionModel();
                            model.addSelectionInterval(0, 6);
                            RenderComboBox enableRenderer = new RenderComboBox(model);
                            jComboBox5.setRenderer(enableRenderer);
                            shapeModel = model;
                        }
			
		} else if ("Ellipsoid".equals(shape)) {
                        jLabel42.setVisible(true);
                        jLabel42.setText("Long Axis (um):");
                        jTextField27.setVisible(true);
                        jLabel17.setText("Short Axis (um):");
                        if (labeling == 3 || labeling == 4 || labeling == 5 || labeling == 6 || labeling == 7){
                            jComboBox5.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select that labeling for Ellipsoid", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else {
                            jTextField25.setText("");
                            jTextField25.setEditable(false);
                            jTextField18.setVisible(true);
                            jLabel111.setVisible(true);

                            DefaultListSelectionModel model = new DefaultListSelectionModel();
                            model.addSelectionInterval(0, 2);
                            RenderComboBox enableRenderer = new RenderComboBox(model);
                            jComboBox5.setRenderer(enableRenderer);
                            shapeModel = model;
                        }
			
		}
	}//GEN-LAST:event_jComboBox2ActionPerformed

	private void jPanel12MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MouseDragged
		// Behrooz June 26
		int new_mx = evt.getX();
		int new_my = evt.getY();

		C3D.azimuth -= new_mx - mx;
		C3D.elevation += new_my - my;
		C3D.repaint();

		// update our data
		mx = new_mx;
		my = new_my;

		evt.consume();

	}//GEN-LAST:event_jPanel12MouseDragged

	private void jPanel12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MousePressed

		mx = evt.getX();
		my = evt.getY();
		evt.consume();

	}//GEN-LAST:event_jPanel12MousePressed

	private void jTextField25KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField25KeyReleased

		jTextField25.setBackground(Color.WHITE); //num cell in 3D
		jTextArea1.setText("");
		JTextField radius = (JTextField) evt.getSource();
		String Cellcount = radius.getText();
		jTextField26.setText("");
		jTextField27.setText("");
		try {
			int TempVal = Integer.parseInt(Cellcount);
		} catch (NumberFormatException nfe) {
			radius.setText("");
			System.out.printf("error in Radius");
		}
	}//GEN-LAST:event_jTextField25KeyReleased

	private void jTextField26KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField26KeyReleased
		// Behrooz June 26
//		jTextField26.setBackground(Color.WHITE); //radius in 3D
//		jTextArea1.setText("");
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		jTextField25.setText("");
//		try {
//			int TempVal = Integer.parseInt(Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField26KeyReleased

	private void jTextField27KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField27KeyReleased
		// Behrooz June 26
//		jTextField27.setBackground(Color.WHITE); //height in 3D
//		jTextArea1.setText("");
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		jTextField25.setText("");
//		try {
//			int TempVal = Integer.parseInt(Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField27KeyReleased

	private void jTextField30KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField30KeyReleased
		// Behrooz June 26
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		jTextField28.setText("");
//		try {
//			int TempVal = Integer.parseInt(Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField30KeyReleased

	private void jTextField28KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField28KeyReleased
		// Behrooz June 26
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		jTextField30.setText("");
//		try {
//			double TempVal = Double.parseDouble(Cellcount);
//			if (TempVal > 100) {
//				jTextField45.setText("100");
//			}
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField28KeyReleased

	private void jTextField29KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField29KeyReleased
		// Behrooz June 26
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		try {
//			double TempVal = Double.parseDouble("0" + Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField29KeyReleased

	private void jTextField31KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField31KeyReleased
		// Behrooz June 26
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		try {
//			double TempVal = Double.parseDouble("0" + Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField31KeyReleased

	private void jTextField33KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField33KeyReleased
		// Behrooz June 26
//		JTextField text = (JTextField) evt.getSource();
//		String Cellcount = text.getText();
//		try {
//			double TempVal = Double.parseDouble("0" + Cellcount);
//		} catch (NumberFormatException nfe) {
//			text.setText("");
//		}
	}//GEN-LAST:event_jTextField33KeyReleased

	private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
		text3 = Integer.parseInt(jTextField3.getText());
		cc2.Dist = text3;
		cc2.repaint();
	}//GEN-LAST:event_jTextField3ActionPerformed

	private void jTabbedPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MouseClicked
		jTextArea1.setText("");
		jTextField35.setText(jTextField3.getText());
		jTextField36.setText(jTextField3.getText());
	}//GEN-LAST:event_jTabbedPane2MouseClicked

	private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
		//Multicelluler geometry tab: 3D Compute
		long TS = System.currentTimeMillis();
		if (TS - justRan < 500) {
			// yes this is a stupid hacky fix but it works
			// basically is the program just ran make it so
			// that it cannot run again immediately
			return;
		}


		// Alex Rosen 7/13/2017
		try {
			// the textfield 15 has 2 spots in the array because when the target region is changed it will be recycled
			activityFractions[0] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[1] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[2] = Double.parseDouble(jTextField16.getText()) / 100D;
			activityFractions[3] = Double.parseDouble(jTextField17.getText()) / 100D;
		} catch (Exception e) {
			System.err.println("please fill in the % activity fields text field ");
		}

		jTextArea5.setText("");
		jTextArea3.setText("");

		//<editor-fold desc="Generate the cluster and all the cells in it">
		// Asthetics and book keeping
		if (jTextField25.getText().isEmpty()) {
			jTextField25.setBackground(Color.PINK);
		} else {
			jTextField25.setBackground(Color.WHITE);
		}
		if (jTextField26.getText().isEmpty()) {
			jTextField26.setBackground(Color.PINK);
		} else {
			jTextField26.setBackground(Color.WHITE);
		}
		if (jTextField27.getText().isEmpty()) {
			jTextField27.setBackground(Color.PINK);
		} else {
			jTextField27.setBackground(Color.WHITE);
		}
                
		text3 = Integer.parseInt(jTextField35.getText());     // JTF35 = distance between cells
		text1 = Integer.parseInt(jTextField1.getText());
		if (text3 < text1 * 2) {
			text3 = text1 * 2;
		}
		jTextField3.setText(Integer.toString(text3));
		jTextField35.setText(Integer.toString(text3));
		jTextField36.setText(Integer.toString(text3));

		cc2.Dist = text3;
		cc2.repaint();
		int Dist = Integer.parseInt(jTextField3.getText());
		// Reads in the shape from GUI
		String Shape = jComboBox2.getSelectedItem().toString();
		// Initialize some of the parameters
		longestaxis = 0;  //for the axis in the 3-D cluster graph under the multicellular section
		int cellnumber = 0;
                int coldcellnumber = 0;
		int MaxDist = 0; //max dist to calculate S-Values. 
		// if The selected shape is a sphere then it determines if the radius or the number of the cells are given
		if (Shape.equals("Sphere")) {
			// Alex Rosen 7/22/2016
			if (!jTextField26.getText().equals("")) {
				Radius = Integer.parseInt(jTextField26.getText());
				cellnumber = 0;
			} else if (!jTextField25.getText().equals("")) {
				cellnumber = Integer.parseInt(jTextField25.getText());
				//Radius = 0;
			}
		} else if (Shape.equals("Cone")) {
			// Alex Rosen 7/22/2016
			if (!jTextField26.getText().equals("") && !jTextField27.getText().equals("")) {
				Radius = Integer.parseInt(jTextField26.getText());
				Height = Integer.parseInt(jTextField27.getText());
				cellnumber = 1;
			}
		} else if (Shape.equals("Ellipsoid")) {
			if (!jTextField26.getText().equals("") && !jTextField27.getText().equals("")) {
				Radius = Integer.parseInt(jTextField26.getText())/2 ;
				Height = Integer.parseInt(jTextField27.getText())/2 ;
				cellnumber = 0;
			}
		} else if (Shape.equals("Rod")) {
			if (!jTextField26.getText().equals("") && !jTextField27.getText().equals("")) {
				Radius = Integer.parseInt(jTextField26.getText());
				Height = Integer.parseInt(jTextField27.getText());
				cellnumber = 0;
			}
		}

		// Alex Rosen 8/3/2016
		Cluster3D Cluster = new Cluster3D(Radius, Height, cellnumber, Shape, Dist);
                                                
		if (Cluster.CellNumber > 15000000) {
			int n = JOptionPane.showConfirmDialog(
					this,
					"Looks like you are trying to generate a cluster with " + Cluster.CellNumber + " cells in it.\nThis is likely too many cells for your computer to handle.  Are you sure you wish to continue?",
					"Uh Oh!",
					JOptionPane.YES_NO_OPTION
			);

			if (n == JOptionPane.YES_OPTION) {
				System.out.println("You were warned.");
				// do nothing, let the user continue
			} else if (n == JOptionPane.NO_OPTION) {
				// exit so that the user doesnt crash the program
				return;
			} else {
				return;
			}
		}
                // calculate # of cells in cold region. 6/17/20
                double coldRadius = 0 , shellwidth = 0;
                if (!jTextField5.getText().isEmpty()) {
				shellwidth = Double.parseDouble(jTextField5.getText());
                                //shellwidth = Radius - coldRadius;
		}
                if (jCheckBox1.isSelected()){
                    String msg = "Invalid Cold-Region radius, please enter between ";
                    if (!Shape.equals("Sphere")&&!Shape.equals("Ellipsoid")){
                        if(shellwidth < 0 || shellwidth >= Height/2.0){
                            if (Radius - Height/2.0 <= 0){
                                msg += "0 and " + Radius;
                            } else{
                                msg += "0 and " + Height/2.0;;
                            }
                            JOptionPane.showMessageDialog(null, msg, "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    
                    }
                    if (Shape.equals("Sphere")){
                        msg += "0 and " + Radius;
                        if(shellwidth < 0 || shellwidth >= Radius){
                            JOptionPane.showMessageDialog(null, msg, "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        Cluster3D coldRegion = new Cluster3D(Radius - (int)shellwidth, Radius - (int)shellwidth, 0, Shape, Dist);
                        coldcellnumber = coldRegion.CellNumber;
                    }  else if (Shape.equals("Rod")) {
                        Cluster3D coldRegion = new Cluster3D(Radius - (int)shellwidth, Height -(int)shellwidth *2 , 0, Shape, Dist);
                        coldcellnumber = coldRegion.CellNumber;
                    }  else if (Shape.equals("Ellipsoid")) {
                        msg += "0 and " + Radius;;
                        if(shellwidth < 0 || shellwidth >= Radius){
                            JOptionPane.showMessageDialog(null, msg, "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        Cluster3D coldRegion = new Cluster3D(Radius - (int)shellwidth, Height -(int)shellwidth , 0, Shape, Dist); //(int)(shellwidth * Height *Height /(Radius * Math.sqrt(Height*Height + Radius*Radius)))
                        coldcellnumber = coldRegion.CellNumber;
                    }  else {
                        Cluster3D coldRegion = new Cluster3D((int)((Height - shellwidth)*(double) Radius / (double) Height) - (int)(shellwidth * Math.sqrt(Height*Height + Radius*Radius)/Height), Height -(int)shellwidth - (int)(shellwidth * Math.sqrt(Height*Height + Radius*Radius) /Radius), 1, "Cone", Dist);
                        //Cluster3D coldRegion = new Cluster3D(Radius - (int)(shellwidth * Math.sqrt(Height*Height + Radius*Radius)/Height), Height -(int)shellwidth - (int)(shellwidth * Height *Height /(Radius * Math.sqrt(Height*Height + Radius*Radius))), 1, "Cone", Dist);
                        coldcellnumber = coldRegion.CellNumber;
                    }                                   
                    jLabel122.setText("Cold Cells: " + coldcellnumber);
                } 
                double packingratio = 0;
                double cellvol = (4.0 / 3) * Math.PI * Math.pow(Double.parseDouble(jTextField1.getText()), 3);  
                
		jTextField25.setText("" + (cellnumber = Cluster.CellNumber));
                //Jianchao 2/24/20 fixed packing ratio                
		if (Shape.equals("Sphere")) {
			cell = Cluster.generateSphere(Radius);
                        MaxDist = 2*Radius;
                        packingratio = (double) cellnumber * Math.pow(Double.parseDouble(jTextField1.getText()) / (double) Radius, 3);
		} else if (Shape.equals("Rod")) {
			cell = Cluster.generateRod(Radius, Height);
                        MaxDist = (int) Math.sqrt(4*Radius*Radius + Height* Height);
                        packingratio = (double) cellnumber * cellvol / (Math.PI * Radius * Radius * Height) ;
		} else if (Shape.equals("Ellipsoid")) {
			cell = Cluster.generateEllipsoid(Radius , Height );
                        MaxDist = (int) Math.max (2*Radius , 2*Height );
                        packingratio = (double) cellnumber * cellvol / ((4.0 / 3) * Math.PI * Radius * Radius * Height) ;
		} else if (Shape.equals("Cone")) {
			cell = Cluster.generateCone(Radius, Height);
                        //Jianchao Wang 2/20/20
                        MaxDist = (int) Math.max(2*Radius ,Math.sqrt(Radius*Radius+Height*Height));
                        packingratio = (double) cellnumber * cellvol / ((1.0 / 3) * Math.PI * Radius * Radius * Height) ;
		}

		longestaxis = Math.max(Radius, Height);

		//double packingratio = (double) cellnumber * Math.pow(Double.parseDouble(jTextField1.getText()) / (double) Radius, 3);
		//System.err.println(packingratio);
		jLabel56.setText("Packing Density : " + String.format("%.2g%n", packingratio));

		// Asthetics and book keeping
		if (jTextField25.getText().isEmpty()) {
			jTextField25.setBackground(Color.PINK);
		} else {
			jTextField25.setBackground(Color.WHITE);
		}
		if (jTextField26.getText().isEmpty()) {
			jTextField26.setBackground(Color.PINK);
		} else {
			jTextField26.setBackground(Color.WHITE);
		}
		if (jTextField27.getText().isEmpty()) {
			jTextField27.setBackground(Color.PINK);
		} else {
			jTextField27.setBackground(Color.WHITE);
		}

//		if (jTextArea1.getText().isEmpty()) {

			jPanel6.revalidate();

			//revised 09/09/2010
			if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
				if (jTextField10.getText().isEmpty()) {
					jTextField10.setBackground(Color.red);
				} else {
					jTextField10.setBackground(Color.white);
				}
			}

			jLabel22.setText("   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s");
			jLabel26.setText("Distance   S(C<--C)   S(C<--CS)    S(N<--N)    S(N<--Cy)    S(N<--CS)    S(Cy<--N)    S(Cy<--CS)    S(Cy<--Cy)");
			jTextArea1.setText("");

			//<editor-fold desc="Part 1/2: calculate svalues">
			// Alex Rosen 8/26/2016
			ArrayList<double[]> data;
			double rCell = Double.parseDouble(jTextField1.getText());
			double rNuc = Double.parseDouble(jTextField2.getText());
			//double[] r = new double[longestaxis]; 
                        //jianchao wang 11/12/18 correct one? 
                        double[] r = new double[MaxDist + 1]; //- 2 * (int) rCell];
			for (int i = 0; i < r.length; i++) {
				r[i] = i + rCell + rCell;
			}

			if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
				// mono-energetics
				System.out.println("new electron/alpha selected!");
				Double en = Double.parseDouble(jTextField10.getText());
				Double xn = Double.parseDouble(jTextField11.getText()); // 11/17/2010
				data = new ArrayList<double[]>();
				if (jRadioButton2.isSelected()) {
					// is electron
					System.out.println("adding to data: e-");
					data.add(new double[]{5.0, xn, en});
				} else if (jRadioButton1.isSelected()) {
					// is alpha
					System.out.println("adding to data: alpha");
					data.add(new double[]{8.0, xn, en});
				}


				CalTest3D_2 calTest3D_2 = new CalTest3D_2();
				SelfDose_2 selfDose_2 = new SelfDose_2();
				selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
				sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel14, MaxDist, r, data);
			} else if (jRadioButton3.isSelected() || jRadioButton4.isSelected()) {
				// user created decay schemes
				data = new ArrayList<double[]>();
				Scanner s = new Scanner(jTextArea2.getText());
				String input = "";
				String[] split;
				double datums[] = new double[4];
				while (!(input = s.nextLine()).equals("START RADIATION RECORDS")) {
					// do nothng
				}
				while (!(input = s.nextLine()).equals("END RADIATION RECORDS")) {
					split = input.trim().split("\\s+");
					for (int i = 0; i < 3; i++) {
						datums[i] = Double.parseDouble(split[i]);
					}
					data.add(new double[]{Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])});
				}
				datums = null; // free up some memory (every little bit helps)

				CalTest3D_2 calTest3D_2 = new CalTest3D_2();
				SelfDose_2 selfDose_2 = new SelfDose_2();
				selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
				sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel14, MaxDist, r, data);
			} else if (jRadioButton12.isSelected()) {
				// beta average spectrum
				iso = String.valueOf(jList2.getSelectedValue());
				System.out.println("selected MIRD iso file name: " + iso);

				data = ArrayListIn3.readMIRDdata(iso, true);
				CalTest3D_2 calTest3D_2 = new CalTest3D_2();
				SelfDose_2 selfDose_2 = new SelfDose_2();
				selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
				sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel14, MaxDist, r, data);
			} else if (jRadioButton13.isSelected()) {
				// beta full spectrum
				iso = String.valueOf(jList2.getSelectedValue()).toLowerCase(Locale.ENGLISH);
				System.out.println("selected iso file name: " + iso);

				data = ArrayListIn3.readOTHERdata(iso);
				CalTest3D_2 calTest3D_2 = new CalTest3D_2();
				SelfDose_2 selfDose_2 = new SelfDose_2();
				selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
				sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel14, MaxDist, r, data);
			}
			data = null;    // to free up space
			Toolkit.getDefaultToolkit().beep(); // done with part 1
			//</editor-fold>

			// Alex Rosen 082217
			// printing this to debug alpha particles giving incorrect dose
//			NumberFormat nff = new DecimalFormat("0.00E0");
//			System.err.println("printing svalues");
//			for (int i = 0; i < sVals[0].length; i++) {
//				for (int j = 0; j < sVals[0][0].length; j++) {
//					System.out.print(nff.format(sVals[0][i][j]) + "  ");
//				}
//				System.out.println();
//			}
//			System.err.println("end printing svalues: svalues match");

			ArrayList list = new ArrayList();
			StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
			while (tokens.hasMoreTokens()) {
				list.add(tokens.nextElement());
			}
			c = Integer.parseInt(jTextField1.getText());
			n = Integer.parseInt(jTextField2.getText());
			d = Integer.parseInt(jTextField3.getText());
			maxRow = jTextArea1.getLineCount() - 1;
			System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d);
			iso = String.valueOf(jList2.getSelectedValue());
			System.out.println("selected iso file name: " + iso);
			pro = jList3.getSelectedIndex();

//		}

		String text = jTextArea1.getText();
		String[] lines = text.split("\\r?\\n");
		int radiationtarget = jList3.getSelectedIndex();
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		C3D.Zoom = ((double) jPanelWidth / 2) / (double) longestaxis;
		ZoomReset = C3D.Zoom;
                //JCW C3DS
                C3DS.Zoom = ((double) jPanelWidth / 2) / (double) longestaxis;
		ZoomResetSlice = C3DS.Zoom;
                
		int labelcellnum = 0;
		double labelcellp = 100;
		if (!jTextField28.getText().equals("")) {  // JTF 28 = % of labeled cells
			// JTF 30 = # of labeled cells
			labelcellp = Double.parseDouble(jTextField28.getText());
			if (labelcellp < 100) {
				labelcellnum = (int) (cellnumber / 100.0 * labelcellp);
				jTextField30.setText(Integer.toString(labelcellnum));
			} else {
				labelcellp = 100;
				labelcellnum = cellnumber;
				jTextField30.setText(Integer.toString(labelcellnum));
				jTextField28.setText(String.format("%.4g%n", labelcellp));
			}
		} else if (!jTextField30.getText().equals("")) {
			labelcellnum = Integer.parseInt(jTextField30.getText());
			if (labelcellnum <= cellnumber) {
				labelcellp = (100.0 * labelcellnum / cellnumber);
				jTextField28.setText(String.format("%.4g%n", labelcellp));
			} else {
				labelcellp = 100;
				labelcellnum = cellnumber;
				jTextField30.setText(Integer.toString(labelcellnum));
				jTextField28.setText(String.format("%.4g%n", labelcellp));
			}
		}

		double MeanActivity = 0, MAC = 0;
		double Tau = 0;
		double AccuActivity = 0;
		double SelfAlpha = 1, CrossAlpha = 1.0, SelfBeta = 0, CrossBeta = 0;
		if (!jTextField7.getText().equals("")) {
			CrossBeta = Double.parseDouble(jTextField7.getText());
		}
		if (!jTextField8.getText().equals("")) {
			SelfBeta = Double.parseDouble(jTextField8.getText());
		}
		if (!jTextField34.getText().equals("")) {
			CrossAlpha = Double.parseDouble(jTextField34.getText());
		}
		if (!jTextField32.getText().equals("")) {
			SelfAlpha = Double.parseDouble(jTextField32.getText());
		}

		System.out.println(CrossBeta + "  " + SelfBeta + "  " + CrossAlpha + "   " + SelfAlpha);

		if (!jTextField29.getText().equals("") && !jTextField31.getText().equals("")) {

			Tau = Double.parseDouble(jTextField31.getText()) * 3600.0;

			MAC = Double.parseDouble(jTextField29.getText());
			MeanActivity = MAC * (double) cellnumber / (double) labelcellnum;
			AccuActivity = MeanActivity * Tau;
		}
		double ShapeFactor, Mu = 1;

		if (!jTextField33.getText().equals("")) {
			Mu = Double.parseDouble(jTextField33.getText());
		}

		//System.err.println("Time:" + Tau + "---> Mean:" + MeanActivity + " ===" + AccuActivity);
		double[][] celllabel = new double[labelcellnum][8];
		int temp3 = 0;
		double AveCellActivity = 0;
		jLabel101.setVisible(false);

		//<editor-fold desc="Assign the activity to each cell">
                //JCW 6/2/20. 3-D Slice tab: setting axial height
		int axialHeight = 0;
                try {
                    axialHeight = Integer.parseInt(jTextField19.getText()) * text3;
                } catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Invalid Axial Height in 3-D Slice", "alert", JOptionPane.ERROR_MESSAGE);
                }
		if (Shape.equals("Cone") || Shape.equals("Rod")) {
                        C3DS.axialHeight = -axialHeight;
                        if (axialHeight < 0 || axialHeight > Height ){
                            JOptionPane.showMessageDialog(null, "Invalid Axial Height in 3-D Slice", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
			//offset = 3.0 * Height / 4.0;
		} else if (Shape.equals("Ellipsoid")) {
                        C3DS.axialHeight = axialHeight;
                        if (axialHeight < -Height || axialHeight > Height ){
                            JOptionPane.showMessageDialog(null, "Invalid Axial Height in 3-D Slice", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
			//offset = Height / 2.0;
		} else {
                        C3DS.axialHeight = axialHeight;
                        if (axialHeight < -Radius || axialHeight > Radius ){
                            JOptionPane.showMessageDialog(null, "Invalid Axial Height in 3-D Slice", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
			//offset = 0.0;
		}
                //calculating doses based on different distribution
                boolean[] resetFlag = new boolean[]{false};
                int[] resetLabelCellNum = new int[]{0};
		if (jComboBox5.getSelectedIndex() == 0 ) {
			// normal distribution
			ShapeFactor = Mu;
                        NormalDistribution a = new NormalDistribution(0, ShapeFactor);
			double[] NormalD = new double[labelcellnum];
			for (int i = 0; i < labelcellnum; i++) {
				NormalD[i] = a.sample();
				if ((NormalD[i] + MeanActivity) < 0) {
					jLabel101.setVisible(true);
					return;
				}
			}
			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
				//shellWidth = Radius - tempShellWidth;
			}

			cell = Activity.generateNormalActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					MeanActivity, ShapeFactor, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));                                                                               
                        }
// calculating self-dose
			int n = 0;
                        int labeledCellNum = 0;
			double sum1 = 0.0;
                        
			for (int i = 0; i < cellnumber; i++) { //jianchao wang 1/15/19
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
					    /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
					sum1 += cell[i][5];
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
			//AveCellActivity = sum1 / labelcellnum;
		} else if (jComboBox5.getSelectedIndex() == 1) {
			// log-normal distribution
			ShapeFactor = Mu;

			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

			cell = Activity.generateLogNormalActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					MeanActivity, ShapeFactor, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }

			int n = 0;
                        int labeledCellNum = 0;
			double sum1 = 0.0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
					sum1 += cell[i][5];
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
			//AveCellActivity = sum1 / labelcellnum;
		} else if (jComboBox5.getSelectedIndex() == 2) {
			// uniform distribution

			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

			cell = Activity.generateUniformActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					MeanActivity, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }
                        

			int n = 0;
                        int labeledCellNum = 0;
			//double sum1 = 0.0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
                                   
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
                                
                                // System.out.println("cell[i][6]: " + cell[i][6]);
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
			//AveCellActivity = AccuActivity;
		} else if (jComboBox5.getSelectedIndex() == 3) {
			// linear Distribution
			double constantProvided = Double.parseDouble(jTextField33.getText()) / 100.0;
			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

//			cell = ActivityLinear.generateActivity(
//					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
//					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
//					Shape, text1,
//					AccuActivity, constantProvided, MeanActivity, Tau,
//					cell
//			);
                        cell = Activity.generateLinearActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape, text1,
					AccuActivity, constantProvided, MeanActivity, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }

			int n = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
		} else if (jComboBox5.getSelectedIndex() == 4) {
			// exponential Distribution
			double constantProvided = 0.0;
			double b = Double.parseDouble(jTextField33.getText());
			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

//			cell = ActivityExponential.generateActivity(
//					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
//					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
//					Shape,
//					AccuActivity, b, constantProvided, MeanActivity, Tau,
//					cell
//			);
                        cell = Activity.generateExponentialActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					AccuActivity, b, constantProvided, MeanActivity, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }

			int n = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
		} else if (jComboBox5.getSelectedIndex() == 5) {
			// polynomial distribution
			// currently for sphere only, but including extra code for the future
                        
                        double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}
//			cell = ActivityPolynomial.generateActivity(
//					cellnumber, Radius, Height, labelcellnum, degree,
//					coefficients,
//					Shape,
//					MeanActivity, Tau,
//					cell
//			);
                        cell = Activity.generatePolynomialActivity(
                                        jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Radius, Height, labelcellnum, degree,
					coefficients, shellWidth, AccuActivity,
					Shape,
					MeanActivity, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }

			int n = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
		} else if (jComboBox5.getSelectedIndex() == 6) {
			// 4 Var Log Distrubution
			double a = 0, b = 0, a0 = 0, x0 = 0;
			try {
				a = Double.parseDouble(jTextField33.getText());
			} catch (NumberFormatException e) {
				jTextField33.setBackground(Color.PINK);
				return;
			}
			try {
				b = Double.parseDouble(jTextField6.getText());
			} catch (NumberFormatException e) {
				jTextField33.setBackground(Color.PINK);
				return;
			}
			try {
				a0 = Double.parseDouble(jTextField9.getText());
			} catch (NumberFormatException e) {
				jTextField33.setBackground(Color.PINK);
				return;
			}
			try {
				x0 = Double.parseDouble(jTextField14.getText());
			} catch (NumberFormatException e) {
				jTextField14.setBackground(Color.PINK);
				return;
			}
			double constantProvided = 0;
                        
                        double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

			// currently for sphere only, but including extra code for the future
//			cell = ActivityLogNormalRadial.generateActivity(
//					jCheckBox1.isSelected(),
//					a, b, x0, a0,
//					cellnumber, Radius, labelcellnum,
//					Shape,
//					AccuActivity, constantProvided, MeanActivity, Tau,
//					cell
//			);

                        cell = Activity.generate4VarLogActivity(
					jCheckBox1.isSelected(),jCheckBox3.isSelected(),
					a, b, x0, a0,
					cellnumber, Radius, labelcellnum, Height,
					Shape, shellWidth,text3,
					AccuActivity, constantProvided, MeanActivity, Tau, coldcellnumber, resetFlag, resetLabelCellNum,
					cell
			);
                        
                        if(resetFlag[0] == true){
                            jTextField30.setText(Integer.toString(resetLabelCellNum[0]));
                            labelcellp = (100.0 * resetLabelCellNum[0] / cellnumber);
                            jTextField28.setText(nf.format(labelcellp));
//                            jPanel14.getGraphics().clearRect(0, 0, jPanel14.getWidth(), jPanel14.getHeight());
                        }
                        
			int counter = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                                                
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[counter++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
		} else if (jComboBox5.getSelectedIndex() == 7) {
			// import CSV - user defined 
                        //Jianchao 4/10/2020 
			double constantProvided = Double.parseDouble(jTextField6.getText()) / 100.0;
			double b = Double.parseDouble(jTextField33.getText());
			double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

			cell = ActivityImport.generateActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					radiusActivityMap,MeanActivity, Tau,
					cell
			);

			int n = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
                                //System.err.println(cell[i][5]+" "+ cell[i][6]);
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
        } else if (jComboBox5.getSelectedIndex() == 8) {
			// user upload(r, decay/sec) 030121
                        double shellWidth = 0.0;
			if (!jTextField5.getText().isEmpty()) {
				shellWidth = Double.parseDouble(jTextField5.getText());
			}

			cell = ActivityImport.generateActivity(
					jCheckBox1.isSelected(), jCheckBox3.isSelected(),
					cellnumber, Height, labelcellnum, longestaxis, Radius, shellWidth,
					Shape,
					radiusActivityMap,1.0, 1.0,
					cell
			);

			int n = 0;
                        int labeledCellNum = 0;
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					if (radiationtarget == 0) {
						// Radiation Target: Cell
                        /* self, C->C + self, CS->C */
						cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
					} else if (radiationtarget == 1) {
						// Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
					} else if (radiationtarget == 2) {
						// Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					} else if (radiationtarget == 3) {
						// Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
						cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
					}
					System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
					temp3++;
                                        labeledCellNum++;
				}
                                //System.err.println(cell[i][5]+" "+ cell[i][6]);
			}
                        jTextField30.setText(Integer.toString(labeledCellNum));
                        labelcellp = (100.0 * labeledCellNum / cellnumber);
			jTextField28.setText(nf.format(labelcellp));
        }
		//</editor-fold>

		System.err.println("temp 3 -->" + temp3 + "Start");
		final long start = System.currentTimeMillis();

		rCell = Double.parseDouble(jTextField1.getText()); //jianchao wang 9/12/18
                //cross-dose calculation
		SurvCalc = new SurvivalCalculation();
                
		if (useComplexRadiobiologicalParams) {
			cell = SurvCalcComplex.calculateSurvival(
					cell, celllabel, sVals, selfSVals, complexRadiobiologicalParams, activityFractions,
					MAC, Dist, (int) rCell,
					cellnumber, jPanel14.getHeight(), jPanel14.getWidth(), radiationtarget,
					jTextArea5,
					jPanel14.getGraphics()
			);
                        //copy array PlotOutput
                        for(int i = 0; i < 8; i++){
                              double[] aMatrix = SurvCalcComplex.PlotOutput[i];
                              PlotOutput3D[i] = new double[101];
                              System.arraycopy(aMatrix, 0, PlotOutput3D[i], 0, 101);
                        }
		} else {
			cell = SurvCalc.calculateSurvival(
					cell, celllabel, sVals, activityFractions,
					CrossAlpha, CrossBeta, SelfAlpha, SelfBeta, MAC, rCell,
					cellnumber, Dist * Dist, jPanel14.getHeight(), jPanel14.getWidth(), ((lines.length) * (lines.length)), radiationtarget,
					jPanel14.getGraphics()
			);
                        //copy array PlotOutput
                        for(int i = 0; i < 8; i++){
                              double[] aMatrix = SurvCalc.PlotOutput[i];
                              PlotOutput3D[i] = new double[101];
                              System.arraycopy(aMatrix, 0, PlotOutput3D[i], 0, 101);
                        }
		}

		C3D.r = (int) rCell;
		C3D.cell = cell;
		C3D.Radius = longestaxis;
		C3D.repaint();
                
                C3DS.r = (int) rCell;
		C3DS.cell = cell;
		C3DS.Radius = longestaxis;
		C3DS.repaint();

		//<editor-fold desc="Output significant data">
		NumberFormat formatter = new DecimalFormat("0.00E0");
		String tempsource = "";
//		temp1 = jList3.getSelectedValue().toString();
		tempsource = ""
				+ "Radius of cell (um) =   " + jTextField1.getText() + "\n"
				+ "Radius of cell nucleus (um) =    " + jTextField2.getText() + "\n"
				+ "Shape =   " + Shape;
		if (Shape.equals("Sphere")) {
			tempsource = tempsource + "\n" + jLabel17.getText() + "  " + jTextField26.getText();
		} else {
			tempsource = tempsource + "\n" + jLabel17.getText() + "  " + jTextField26.getText() + "\n" + jLabel42.getText() + "  " + jTextField27.getText();
		}
		tempsource += "\n";
                tempsource += "Cold Region: " + (jCheckBox1.isSelected()? "Yes" + "; Penetrating Depth(um): " + jTextField5.getText() : "No");
                tempsource += "\n";
		tempsource += "Target Region: " + jList3.getSelectedValue().toString() + "\n";
		tempsource += "Source Regions:\n"
				+ jLabel108.getText() + ": " + jTextField17.getText() + "%\n"
				+ jLabel107.getText() + ": " + jTextField16.getText() + "%\n"
				+ jLabel106.getText() + ": " + jTextField15.getText() + "%\n\n";

		if (useComplexRadiobiologicalParams) {
			tempsource += "Complex Linear Quadratic Parameters" + "\n";
			TableModel m = jTable1.getModel();
			if (jList3.getSelectedIndex() == 0) {
                            // Target Region: Cell
                            tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                            tempsource += "     \t(C<--C)\t(C<--C)\t(C<--CS)\t(C<--CS)\t(Ci<--Cj)\t(Ci<--Cj)\n";
                        } else if (jList3.getSelectedIndex() == 1) {
                            // Target Region: Nucleus
                            tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                            tempsource += "     \t(N<--N)\t(N<--N)\t(N<--Cy)\t(N<--Cy)\t(N<--CS)\t(N<--CS)\t(Ni<--Cj)\t(Ni<--Cj)\n";
                        } else if (jList3.getSelectedIndex() == 2) {
                            // Target Region: Cytoplasm
                            tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                            tempsource += "     \t(CS<--N)\t(CS<--N)\t(CS<--Cy)\t(CS<--Cy)\t(CS<--CS)\t(CS<--CS)\t(Cyi<--Cj)\t(Cyi<--Cj)\n";
                        } else if (jList3.getSelectedIndex() == 3) {
                            // Target Region: Nucleus & Cytoplasm
                            tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\talpha-cross\tbeta-cross\n";
                            tempsource += "     \t(N<--N)\t(N<--N)\t(N<--Cy)\t(N<--Cy)\t(N<--CS)\t(N<--CS)\t(CS<--N)\t(CS<--N)\t(CS<--Cy)\t(CS<--Cy)\t(CS<--CS)\t(CS<--CS)\t(Ni<--Cj)\t(Ni<--Cj)\t(Cyi<--Cj)\t(Cyi<--Cj)\n";
                        }

			for (int i = 0; i < m.getRowCount(); i++) {
				for (int j = 0; j < m.getColumnCount(); j++) {
					if (j == 1) {
						continue;
					}
					tempsource += m.getValueAt(i, j).toString() + '\t';
				}
				tempsource += "\n";
			}
		} else {
			tempsource += "Simple Linear Quadratic Parameters"
					+ "Alphaself (Gy^-1) =    " + jTextField32.getText() + "\n"
					+ "Betaself (Gy^-2) =     " + jTextField8.getText() + "\n"
					+ "Alphacross (Gy^-1) =   " + jTextField34.getText() + "\n"
					+ "Betacross (Gy^-2) =    " + jTextField7.getText() + "\n";
		}

		if (jRadioButton12.isSelected()) {
			tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Average Energy Spectrum" + "\n\n" + tempsource;
		} else if (jRadioButton13.isSelected()) {
			tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Full Energy Spectrum" + "\n\n" + tempsource;
		} else if (jRadioButton1.isSelected()) {
			tempsource = "Monoenergetic particle emitter =   Alpha Particle" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n" + tempsource;
		} else if (jRadioButton2.isSelected()) {
			tempsource = "Monoenergetic particle emitter =   Electron" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n\n" + tempsource;
		} else if (jRadioButton4.isSelected()) { //retrieve
                        tempsource = "User Created Radionuclide: " + fileName;
                } else if (jRadioButton3.isSelected()) { //create
                        tempsource = "User Created Radionuclide: Created";
                }
                                //jianchao 5/22/20
                String poly = "Polynomial Coefficients: ";
                if(jComboBox5.getSelectedItem().toString().equals("Polynomial (Radial)")){
                                                    for (int i = degree; i >= 0; i--) {
                                                        poly += ("a" + i + "=" + coefficients[i] +" ");
                                                    }
                                                }
		jTextArea5.append( version.concat("\n").concat(tempsource).concat("\n")
						+ "Number of Cells =   " + cellnumber + "\n"
						+ "Distance between centers of neighboring cells (um) =    " + jTextField35.getText() + "\n"
						+ jLabel56.getText() + "\n"
						+ "Number of cells that are labeled =   " + jTextField30.getText() + "\n"
						+ "Percentage of cells that are labeled (%)=   " + jTextField28.getText() + "\n"
						+ "Distribution of radioactivity =   " + jComboBox5.getSelectedItem().toString() + "\n"
						+ (jLabel57.isVisible() ? jLabel57.getText() + "= " + jTextField33.getText() + ", " : "") 
                                                + (jLabel27.isVisible() ? jLabel27.getText() + "= " + jTextField6.getText() + ", " : "")
                                                + (jLabel104.isVisible() ? jLabel104.getText() + "= " + jTextField14.getText() + ", " : "")
                                                + (jLabel50.isVisible() ? jLabel50.getText() + "= " + jTextField9.getText() + "\n" : "")
                                                + (jComboBox5.getSelectedItem().toString().equals("Polynomial (Radial)")? poly + "\n" : "")
						+ "Maximum mean activity per cell (Bq) =   " + jTextField29.getText() + "\n"
						+ "Time integrated activity coefficient (hr) =   " + jTextField31.getText() + "\n" + "\n"
						+ "MAC = mean activity per cell (Bq)" + "\n"
						+ "MDC = mean absorbed dose to target region(s) of cells (Gy)" + "\n"
						+ "MALC =mean activity per labeled cell (Bq)" + "\n"
						+ "MDLC = mean absorbed dose to labeled cells (Gy)" + "\n"
						+ "MDUC = mean absorbed dose to unlabeled cells (Gy)" + "\n" + "\n"
		);
		/*"Mean activity per cell (Bq) =   "+ formatter.format(activitytotal/(cellnumber*Tau)) +"\n"+
		 "Mean absorbed dose to all cells (Gy) =   "+formatter.format( MeanABD) +"\n"+
         "Mean activity per labeled cell (Bq) =   "+ formatter.format(activitytotal/(labelcellnum *Tau))+"\n"+
         "Mean absorbed dose to labeled cells (Gy) =   "+ formatter.format(MeanABDL) +"\n");*/

                //jianchao 4/13/2020
                if (jComboBox5.getSelectedIndex() == 7){
                    jTextArea5.append( "User Uploaded Data:\n");
                    for (Map.Entry<Double, Double> entry : radiusActivityMap.entrySet()) {
                        jTextArea5.append( "[" + entry.getKey() + ", " + entry.getValue() + "]\n"); 
                    }
                    jTextArea5.append("\n");
                }
		if (useComplexRadiobiologicalParams) {
			jTextArea5.append(SurvCalcComplex.output);
		} else {
			jTextArea5.append("MAC(Bq)\tMDC(Gy)\tMALC(Bq)\tMDLC(Gy)\tMDUC(Gy)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n");
			for (int i = 0; i < SurvCalc.PlotOutput[0].length; i++) {
				jTextArea5.append(""
						+ formatter.format(SurvCalc.PlotOutput[0][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[1][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[2][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[3][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[4][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[5][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[6][i]) + " \t"
						+ formatter.format(SurvCalc.PlotOutput[7][i]) + " \n"
				);
			}
		}

		//</editor-fold>

		//<editor-fold desc="Generate surviving fraction curve graph">

		//series.add(0, 1);
		double miny = 1.0 / (double) cellnumber;

		if (!jTextArea5.getText().isEmpty()) {
			try {
				jTextArea5.setCaretPosition(jTextArea5.getLineStartOffset(1));
			} catch (BadLocationException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if (!jTextArea3.getText().isEmpty()) {
			try {
				jTextArea3.setCaretPosition(jTextArea3.getLineStartOffset(1));
			} catch (BadLocationException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
                
                //generate SF curve
                SurvivalFraction survivalFraction = new SurvivalFraction();
                if(useComplexRadiobiologicalParams){
                   survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, SurvCalcComplex.PlotOutput, Tau, cellnumber);
                } else{
                    survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, SurvCalc.PlotOutput, Tau, cellnumber);
                }
                
		//</editor-fold>

		//<editor-fold desc="Generate Histogram graph">
		double[] v1 = new double[celllabel.length];
		for (int i = 0; i < celllabel.length; i++) {
			v1[i] = celllabel[i][5] / Tau;
		}

		HistogramDataset Histdataset = new HistogramDataset();
		int bin = 1000;
		Histdataset.addSeries("", v1, bin);

		JFreeChart charth = ChartFactory.createHistogram(
				"Initial Activity Labeled Per Cell (Bq)",
				"Activity (Bq)",
				"Number of Cells",
				Histdataset,
				PlotOrientation.VERTICAL,
				false,
				true,
				false
		);

		charth.setBackgroundPaint(new Color(230, 230, 230));
		XYPlot xyplot = (XYPlot) charth.getPlot();

		NumberAxis RangeAxis1 = (NumberAxis) xyplot.getRangeAxis();
		RangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		NumberAxis DomainAxis1 = (NumberAxis) xyplot.getDomainAxis();
		DomainAxis1.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		DomainAxis1.setNumberFormatOverride(new DecimalFormat("0.00E00"));
		if (jComboBox5.getSelectedIndex() == 2) {
			// uniform distribution
			DomainAxis1.setRange(v1[0] - .000000001, v1[0] + .000000001);
		}
		xyplot.setForegroundAlpha(0.7F);
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
		xyplot.setRangeGridlinePaint(new Color(150, 150, 150));

		XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
		xybarrenderer.setShadowVisible(false);
		xybarrenderer.setBarPainter(new StandardXYBarPainter());
		ChartPanel CPH = new ChartPanel(charth);
		jPanel49.removeAll();
		jPanel49.setLayout(new java.awt.BorderLayout());
		jPanel49.add(CPH, BorderLayout.CENTER);
		jPanel49.validate();

		Graphics progress = jPanel14.getGraphics();
		int Progwidth = jPanel14.getWidth();
		int ProgHeight = jPanel14.getHeight();
		progress.clearRect(0, 0, Progwidth, ProgHeight);
		progress.setColor(Color.GREEN);
		progress.fillRect(1, 1, Progwidth, ProgHeight);
		progress.setColor(Color.BLACK);
		progress.drawString("Done", Progwidth / 2 - 5, ProgHeight / 2 + 6);
		//</editor-fold>

		//Update the output textarea
		jTextArea3.append( "Distance   S(C<--C)  S(C<--CS)   S(N<--N)   S(N<--Cy)   S(N<--CS)   S(Cy<--N)   S(Cy<--CS)   S(Cy<--Cy)\n" );
		jTextArea3.append( "   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s\n" );
		jTextArea3.append(jTextArea1.getText());

		long TE = System.currentTimeMillis();
		System.out.println((TE - TS) / 1000.0);
		System.out.println((TE - start) / 1000.0);

		//<editor-fold desc="Generate Radial Activity Distribution Histogram">

		// get the avg activity per radius
		// TODO this will need to be sitched over when the new dosing is fully implemented
		avgActivity = new double[2 * longestaxis + 1];
		avgDose = new double[2 * longestaxis + 1];
                avgSelfDose = new double[2 * longestaxis + 1];
                avgCrossDose = new double[2 * longestaxis + 1];
		numCellsAt = new int[2 * longestaxis + 1];

                ulAvgCrossDose = new double[2 * longestaxis + 1];  //unlabeled avg cross-dose
		ulNumCellsAt = new int[2 * longestaxis + 1]; //unlabeled # of cell at r

		int rToCell;
                if(Shape.equals("Sphere")){
                    //jTextField18.setEnabled(false); 
                    for (int i = 0; i < cell.length; i++) {
			if (cell[i][4] != 0) {
				rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][2]), 2) + Math.pow((cell[i][3]), 2)));
				avgActivity[rToCell] += cell[i][5];     // sum the activity at each radius
                                avgDose[rToCell] += cell[i][6] + cell[i][7];  //jianchao 4/3/20 self-dose + cross-dose
                                avgSelfDose[rToCell] += cell[i][6];     //jianchao 4/7/20 added self-dose 
                                avgCrossDose[rToCell] += cell[i][7];    //jianchao 4/7/20 added cross-dose
				numCellsAt[rToCell]++;                  // the number of cells at each radius
			} else{ //unlabeled
                                rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][2]), 2) + Math.pow((cell[i][3]), 2)));
                                ulAvgCrossDose[rToCell] += cell[i][7];
                                ulNumCellsAt[rToCell]++;
                        }
                    }
                    for (int i = 0; i < longestaxis + 1; i++) {
                        if (numCellsAt[i] != 0) {
                            avgActivity[i] /= numCellsAt[i] * Tau;            // get the average (the tau is to remove the time activity constant
                            avgDose[i] /= numCellsAt[i];
                            avgSelfDose[i] /= numCellsAt[i];
                            avgCrossDose[i] /= numCellsAt[i];
                        } 
                        if (ulNumCellsAt[i] != 0) {
                            ulAvgCrossDose[i] /= ulNumCellsAt[i];
                        }
                    }
                }
		
                //jianchao 5/4/20. Plot for Rod shape
                if(Shape.equals("Rod")) {
                    try{
                        int yCell = Integer.parseInt(jTextField18.getText()); 
                        int distanceBtwnCells = Integer.parseInt(jTextField35.getText()); 
                        int h = Integer.parseInt(jTextField27.getText());
                        int y = yCell * distanceBtwnCells;                          
                        if (yCell < 0 || yCell > h/distanceBtwnCells ) {
                                JOptionPane.showMessageDialog(null, "Invalid axial height in Radial Histogram", "alert", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        //JCW 5/5/20.noticed cell[i][2] is the height axis, and is always negative.
                        for (int i = 0; i < cell.length; i++) {
                            if (cell[i][2] == -y){
                                if (cell[i][4] != 0 ) {                           
                                    rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                    avgActivity[rToCell] += cell[i][5];     // sum the activity at each rToCell
                                    avgDose[rToCell] += cell[i][6] + cell[i][7];  //jianchao 4/3/20 self-dose + cross-dose
                                    avgSelfDose[rToCell] += cell[i][6];     //jianchao 4/7/20 added self-dose 
                                    avgCrossDose[rToCell] += cell[i][7];    //jianchao 4/7/20 added cross-dose
                                    numCellsAt[rToCell]++;                  // the number of cells at each radius
//                                    System.err.println(y+" "+rToCell+" "+cell[i][5]);
                                }  else{
                                    rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                    ulAvgCrossDose[rToCell] += cell[i][7];
                                    ulNumCellsAt[rToCell]++;
                                }
                            }
                            
                        }
                        for (int i = 0; i < Radius; i++) {
                            if (numCellsAt[i] != 0) {
                                    avgActivity[i] /= numCellsAt[i] * Tau;            // get the average (the tau is to remove the time activity constant
                                    avgDose[i] /= numCellsAt[i];
                                    avgSelfDose[i] /= numCellsAt[i];
                                    avgCrossDose[i] /= numCellsAt[i];
                            }
                            if (ulNumCellsAt[i] != 0) {
                                ulAvgCrossDose[i] /= ulNumCellsAt[i];
                            }
                        }
                    }catch (NumberFormatException nfe){
                            JOptionPane.showMessageDialog(null, "invalid input: not a number.", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                    }
                }
                
                //jianchao 5/27/20. Plot for Ellipsoid shape
                if(Shape.equals("Ellipsoid")) {
                    try{
                        int yCell = Integer.parseInt(jTextField18.getText()); 
                        int distanceBtwnCells = Integer.parseInt(jTextField35.getText()); 
                        int h = Integer.parseInt(jTextField27.getText())/2;
                        int y = yCell * distanceBtwnCells;                          
                        if (yCell < -h/distanceBtwnCells || yCell > h/distanceBtwnCells ) {
                                JOptionPane.showMessageDialog(null, "Invalid axial height in Radial Histogram", "alert", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        //JCW 5/5/20.noticed cell[i][2] is the height axis, and is always negative.
                        for (int i = 0; i < cell.length; i++) {
                            if (cell[i][2] == -y){
                                if (cell[i][4] != 0) {                           
                                rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                avgActivity[rToCell] += cell[i][5];     // sum the activity at each rToCell
                                avgDose[rToCell] += cell[i][6] + cell[i][7];  //jianchao 4/3/20 self-dose + cross-dose
                                avgSelfDose[rToCell] += cell[i][6];     //jianchao 4/7/20 added self-dose 
                                avgCrossDose[rToCell] += cell[i][7];    //jianchao 4/7/20 added cross-dose
                                numCellsAt[rToCell]++;                  // the number of cells at each radius
//                                    System.err.println(y+" "+rToCell+" "+cell[i][5]);
                                } else{
                                    rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                    ulAvgCrossDose[rToCell] += cell[i][7];
                                    ulNumCellsAt[rToCell]++;
                                }
                            }
                            
                        }
                        for (int i = 0; i < Radius; i++) {
                            if (numCellsAt[i] != 0) {
                                    avgActivity[i] /= numCellsAt[i] * Tau;            // get the average (the tau is to remove the time activity constant
                                    avgDose[i] /= numCellsAt[i];
                                    avgSelfDose[i] /= numCellsAt[i];
                                    avgCrossDose[i] /= numCellsAt[i];
                            }
                            if (ulNumCellsAt[i] != 0) {
                                ulAvgCrossDose[i] /= ulNumCellsAt[i];
                            }
                        }
                    }catch (NumberFormatException nfe){
                            JOptionPane.showMessageDialog(null, "invalid input: not a number.", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                    }
                }
                //jianchao 5/27/20. Plot for Cone shape
                if(Shape.equals("Cone")) {
                    try{
                        int yCell = Integer.parseInt(jTextField18.getText()); 
                        int distanceBtwnCells = Integer.parseInt(jTextField35.getText()); 
                        int h = Integer.parseInt(jTextField27.getText());
                        int y = yCell * distanceBtwnCells;  
                        //jianchao 5/29/20 for debugging purpose
//                        int cnt = 0;
//                        for(int i = 0; i< h/distanceBtwnCells +1; i++){
//                            for (int j = 0; j < cell.length; j++){
//                                //rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
//                                if(-cell[j][2] == i * distanceBtwnCells){
//                                    cnt++;
//                                    System.err.println(i * distanceBtwnCells+" "+cell[j][1]+" "+ cell[j][3] + " "+cell[j][6] + " "+ cell[j][7]);
//                                }
//                                
//                            }
//                        }
//                        System.err.println(cnt);
                        
                        if (yCell < 0 || yCell > h/distanceBtwnCells ) {
                                JOptionPane.showMessageDialog(null, "Invalid axial height in Radial Histogram", "alert", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        //JCW 5/5/20.noticed cell[i][2] is the height axis, and is always negative.
                        
                        for (int i = 0; i < cell.length; i++) {
                            if (cell[i][2] == -y) {
                                if (cell[i][4] != 0 ) {                           
                                    rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                    avgActivity[rToCell] += cell[i][5];     // sum the activity at each rToCell
                                    avgDose[rToCell] += cell[i][6] + cell[i][7];  //jianchao 4/3/20 self-dose + cross-dose
                                    avgSelfDose[rToCell] += cell[i][6];     //jianchao 4/7/20 added self-dose 
                                    avgCrossDose[rToCell] += cell[i][7];    //jianchao 4/7/20 added cross-dose
                                    numCellsAt[rToCell]++;                  // the number of cells at each radius                                   
                                } else{
                                    rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][3]), 2)));
                                    ulAvgCrossDose[rToCell] += cell[i][7];
                                    ulNumCellsAt[rToCell]++;
                                }
                            }
                            
                        }
                        for (int i = 0; i < Radius; i++) {
                            if (numCellsAt[i] != 0) {
                                    avgActivity[i] /= numCellsAt[i] * Tau;            // get the average (the tau is to remove the time activity constant
                                    avgDose[i] /= numCellsAt[i];
                                    avgSelfDose[i] /= numCellsAt[i];
                                    avgCrossDose[i] /= numCellsAt[i];
                            }
                            if (ulNumCellsAt[i] != 0) {
                                ulAvgCrossDose[i] /= ulNumCellsAt[i];
                            }
                        }
                    }catch (NumberFormatException nfe){
                            JOptionPane.showMessageDialog(null, "invalid input: not a number.", "alert", JOptionPane.ERROR_MESSAGE);
                            return;
                    }
                }

		// output the sctivity as a function of radius from center of mass. it should be noted that this is not necessarily distance from edge
		jTextArea5.append("Activity | Absorbed Dose as a function of radius (from the center of mass):\n");
		jTextArea5.append("Radial position (um)\tActivity (Bq)\tAvg Absorbed Dose(Gy)\tAvg Self Absorbed Dose(Gy)\tAvg Cross Absorbed Dose(Gy)\tAvg Cross Absorbed Dose(Gy)\t Avg Absorbed Dose(Gy)\n");
                jTextArea5.append("\t\t(labeled cell)\t\t(labeled cell)\t\t(labeled cell)\t\t(labeled cell)\t\t(unlabeled cell)\t\t(all cells)\n");

		DecimalFormat f = new DecimalFormat("0.000E0");

		for (int i = 0; i < Radius+1; i++) {
                    if(numCellsAt[i] != 0 || ulNumCellsAt[i] != 0)
                    jTextArea5.append("" + i + "\t\t" + f.format(avgActivity[i]) + "\t\t   " + f.format(avgDose[i]) + "\t\t   "+ f.format(avgSelfDose[i]) + "\t\t   "+ f.format(avgCrossDose[i]) + "\t\t   "+ f.format(ulAvgCrossDose[i]) + "\t\t   " + f.format(avgDose[i] * numCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i]) + ulAvgCrossDose[i]* ulNumCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i])) +"\n");			
		}

		// generate the data set
                RadialHistogram radialHistogram = new RadialHistogram();
                radialHistogram.generateHistogram(Shape, avgActivity, avgDose, avgSelfDose, avgCrossDose, ulAvgCrossDose, numCellsAt, ulNumCellsAt, Tau, longestaxis, Radius, jPanel56, jComboBox3);
		//</editor-fold>

		jTextArea3.setCaretPosition(0);
		justRan = System.currentTimeMillis();

	}//GEN-LAST:event_jButton17ActionPerformed

	private ArrayList<Double> Merge(LinkedList<Double> L, ArrayList<Double> A) {
		int i = 0, j = 0;
		int e = L.size();
		if (A.size() == 0) {
			for (double d : L) {
				A.add(d);
			}
			return A;
		} else {
			while (i < e && j < A.size()) {
				if (L.getFirst().compareTo(A.get(j)) < 0) {
					A.add(j, L.getFirst());
					L.removeFirst();
					i++;
				} else if (L.getFirst().equals(A.get(j))) {
					L.removeFirst();
					i++;
				} else {
					j++;
				}
			}
			if (i < e) {
				for (int k = i; k < e; k++) {
					A.add(L.getFirst());
					L.removeFirst();
				}
			}
			return A;
		}
	}

	private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
		jComboBox2.setEnabled(true);
		jCheckBox1.setEnabled(true);
		jLabel104.setVisible(false);
		jTextField14.setVisible(false);
		if (jComboBox5.getSelectedIndex() == 0 ) {
			// normal distribution
                        //below 4 line is to for the gray out appearance
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 3);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        
			jLabel57.setVisible(true);
			jTextField33.setVisible(true);
			jTextField33.setText("0.0001");
			jLabel57.setText("Standard Deviation (Bq):");
			jLabel27.setVisible(false);
			jTextField6.setVisible(false);
			jTextField9.setVisible(false);
			jLabel50.setVisible(false);
                        jTextField29.setEnabled(true);
                        
                        jTextField29.setVisible(true);
                        jTextField31.setVisible(true);
                        jLabel48.setVisible(true);
                        jLabel52.setVisible(true);
		} else if (jComboBox5.getSelectedIndex() == 1) {
			// log-normal distribution
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 3);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        
			jLabel57.setVisible(true);
			jTextField33.setVisible(true);
			jLabel57.setText("Shape Factor (σ):");
			jTextField33.setText("1.0");
			jLabel27.setVisible(false);
			jTextField6.setVisible(false);
			jTextField9.setVisible(false);
			jLabel50.setVisible(false);
                        jTextField29.setEnabled(true);
                        
                        jTextField29.setVisible(true);
                        jTextField31.setVisible(true);
                        jLabel48.setVisible(true);
                        jLabel52.setVisible(true);
		} else if (jComboBox5.getSelectedIndex() == 2) {
			// uniform distribution
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 3);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        
			jLabel57.setVisible(false);
			jTextField33.setVisible(false);
			jLabel27.setVisible(false);
			jTextField6.setVisible(false);
			jTextField9.setVisible(false);
			jLabel50.setVisible(false);
                        jTextField29.setEnabled(true);
                        
                        jTextField29.setVisible(true);
                        jTextField31.setVisible(true);
                        jLabel48.setVisible(true);
                        jLabel52.setVisible(true);
		} else if (jComboBox5.getSelectedIndex() == 3) {
			// linear distribution
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 2);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        if(!shapeModel.isSelectedIndex(3)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select linear distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            jLabel57.setText("Starting Activity as % of Edge:");
                            jTextField33.setText("0.0");
                            jLabel57.setVisible(true);
                            jTextField33.setVisible(true);
                            jLabel27.setVisible(false);
                            jTextField6.setVisible(false);
                            jTextField9.setVisible(false);
                            jLabel50.setVisible(false);
                            jTextField29.setEnabled(true);
                            
                            jTextField29.setVisible(true);
                            jTextField31.setVisible(true);
                            jLabel48.setVisible(true);
                            jLabel52.setVisible(true);
                        }
			
		} else if (jComboBox5.getSelectedIndex() == 4) {
			// exponential distribution
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 2);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        if(!shapeModel.isSelectedIndex(4)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select exponential distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            jLabel57.setText("Exponential Factor:");

                            jTextField33.setText("" + (5.0 / Double.parseDouble(jTextField26.getText())));    // this is the 5/R from the paper

                            jLabel27.setVisible(false);
                            jTextField6.setVisible(false);
                            jLabel57.setVisible(true);
                            jTextField33.setVisible(true);
                            jTextField6.setVisible(false);
                            jTextField9.setVisible(false);
                            jLabel50.setVisible(false);
                            jTextField29.setEnabled(true);
                            
                            jTextField29.setVisible(true);
                            jTextField31.setVisible(true);
                            jLabel48.setVisible(true);
                            jLabel52.setVisible(true);
                        }
			
		} else if (jComboBox5.getSelectedIndex() == 5) {
			// polynomial distribution
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 2);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        if(!shapeModel.isSelectedIndex(5)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select polynomial distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            try {
				degree = Integer.parseInt(JOptionPane.showInputDialog("Enter the degree of the polynomial (no higher than 10)", "0"));
                                for (int i = degree; i >= 0; i--) {
				coefficients[i] = Double.parseDouble(JOptionPane.showInputDialog("a^" + i, "0"));
                                } //jianchao 4/1/20 added for loop inside the try block to prevent hang
                            } catch (NumberFormatException e) {
                                    //degree = 0 or user canceled
                                    jComboBox2.setSelectedIndex(0);
                                    System.out.println(e.getMessage());
                            }

                            jCheckBox1.setEnabled(true);
                            jTextField9.setVisible(false);
                            jLabel50.setVisible(false);
                            jTextField29.setEnabled(true);
                            jLabel57.setVisible(false);
                            jTextField33.setVisible(false);
                            jLabel27.setVisible(false);
                            jTextField6.setVisible(false);
                            
                            jTextField29.setVisible(true);
                            jTextField31.setVisible(true);
                            jLabel48.setVisible(true);
                            jLabel52.setVisible(true);
                        }
			
		} else if (jComboBox5.getSelectedIndex() == 6) {
			// 4-var log function
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 2);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        if(!shapeModel.isSelectedIndex(6)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select 4-var log distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            //jComboBox2.setSelectedIndex(0);
                            jComboBox2.setEnabled(true);
                            //jCheckBox1.setSelected(false);
                            jCheckBox1.setEnabled(true);
                            jLabel57.setText("a");
                            jTextField33.setText("");
                            jLabel27.setText("b");
                            jTextField6.setText("");
                            jLabel57.setVisible(true);
                            jTextField33.setVisible(true);
                            jLabel50.setText("y0");
                            jLabel27.setVisible(true);
                            jTextField6.setVisible(true);
                            jTextField9.setVisible(true);
                            jLabel50.setVisible(true);
                            jLabel104.setVisible(true);
                            jTextField14.setVisible(true);
                            jLabel104.setText("x0");
                            jTextField29.setEnabled(true);
                            
                            jTextField29.setVisible(true);
                            jTextField31.setVisible(true);
                            jLabel48.setVisible(true);
                            jLabel52.setVisible(true);
                        }
			
		} else if (jComboBox5.getSelectedIndex() == 7) {
			// upload (r, relative acti/cell). Jianchao 4/9/20
                        DefaultListSelectionModel model = new DefaultListSelectionModel();
                        model.addSelectionInterval(0, 0);
                        RenderComboBox enableRenderer = new RenderComboBox(model);
                        jComboBox2.setRenderer(enableRenderer);
                        if(!shapeModel.isSelectedIndex(7)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select user-defined distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            //jComboBox2.setSelectedIndex(0);
                            jComboBox2.setEnabled(true);
                            jCheckBox1.setSelected(false);
                            jCheckBox1.setEnabled(false);
                            jLabel57.setVisible(false);
                            jTextField33.setVisible(false);
                            jLabel27.setVisible(false);
                            jTextField6.setVisible(false);
                            jTextField9.setVisible(false);
                            jTextField29.setEnabled(true);
                            jLabel50.setVisible(false);
                            
                            jTextField29.setVisible(true);
                            jTextField31.setVisible(true);
                            jLabel48.setVisible(true);
                            jLabel52.setVisible(true);
                            
                            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            String line = "";
                            BufferedReader br = null;
                            TreeMap<Double, Double> map = new TreeMap<>();
                            try {
                                    double max = -1, min = Double.MAX_VALUE;
                                    br = new BufferedReader(new FileReader(file));
                                    while ((line = br.readLine()) != null) {
                                        if ("".equals(line)) break;
                                        String arr[] = line.split(",");
                                        double r = Double.parseDouble(arr[0]); 
                                        double activity = Double.parseDouble(arr[1]);
                                        map.put(r, activity);
                                        max = Math.max (max, r);
                                        min = Math.min(min, r);
                                    }

                                    if (map.size()< 2) {
                                        JOptionPane.showMessageDialog(null, "not enough lines in imported data", "alert", JOptionPane.ERROR_MESSAGE);
                                    }
                                    else if (max != Double.parseDouble(jTextField26.getText()) || min != 0){
                                        JOptionPane.showMessageDialog(null, "Data for maximum and minimum radial positions must match cluster radius and r=0, respectively.", "alert", JOptionPane.ERROR_MESSAGE);
                                    }                                 
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Oops, something went wrong. Please check the data", "alert", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                if (br != null) {
                                    try {
                                        br.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            radiusActivityMap = map;
                        }
                       }
		} else if (jComboBox5.getSelectedIndex() == 8) {
			// upload (r, decay/sec)
                        if(!shapeModel.isSelectedIndex(8)){
                            jComboBox2.setSelectedIndex(0);
                            JOptionPane.showMessageDialog(null, "You can't select Exponential Non-Zero distribution for this shape", "ERROR",JOptionPane.ERROR_MESSAGE);
                        } else{
                            jComboBox2.setEnabled(true);
                            jCheckBox1.setSelected(false);
                            jCheckBox1.setEnabled(false);
                            jLabel57.setVisible(false);
                            jTextField33.setVisible(false);
                            jLabel27.setVisible(false);
                            jTextField6.setVisible(false);
                            jTextField9.setVisible(false);                            
                            jLabel50.setVisible(false);
                            //set max mean acti and time integerated act. coef invisible
                            jTextField29.setVisible(false);
                            jTextField31.setVisible(false);
                            jLabel48.setVisible(false);
                            jLabel52.setVisible(false);
                            
                            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            String line = "";
                            BufferedReader br = null;
                            TreeMap<Double, Double> map = new TreeMap<>();
                            try {
                                    double max = -1, min = Double.MAX_VALUE;
                                    br = new BufferedReader(new FileReader(file));
                                    while ((line = br.readLine()) != null) {
                                        if ("".equals(line)) break;
                                        String arr[] = line.split(",");
                                        double r = Double.parseDouble(arr[0]); 
                                        double activity = Double.parseDouble(arr[1]);
                                        map.put(r, activity);
                                        max = Math.max (max, r);
                                        min = Math.min(min, r);
                                    }

                                    if (map.size()< 2) {
                                        JOptionPane.showMessageDialog(null, "not enough lines in imported data", "alert", JOptionPane.ERROR_MESSAGE);
                                    }
                                    else if (max != Double.parseDouble(jTextField26.getText()) || min != 0){
                                        JOptionPane.showMessageDialog(null, "Data for maximum and minimum radial positions must match cluster radius and r=0, respectively.", "alert", JOptionPane.ERROR_MESSAGE);
                                    }                                 
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Oops, something went wrong. Please check the data", "alert", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                if (br != null) {
                                    try {
                                        br.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            radiusActivityMap = map;
                        }
                    }
                }
			
	}//GEN-LAST:event_jComboBox5ActionPerformed

	private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
		// Behrooz June 26
		//JTextField radius = (JTextField) evt.getSource();
		String text = jTextField1.getText();
		try {
			int TempVal = Integer.parseInt(text);
			text1 = Integer.parseInt(jTextField1.getText());
			text2 = Integer.parseInt(jTextField2.getText());
			text3 = Integer.parseInt(jTextField3.getText());
                        //Jianchao 3/26/20 bug fixes so user can put in 10s
			if (text1 <= 0) {
				jTextField1.setText(Integer.toString(2));//number in jTextField1 decreased once button clicked
				jTextField2.setText(Integer.toString(1));
				jTextField3.setText(Integer.toString(4));
			} else {
				jTextField3.setText(Integer.toString(2 * text1));
				jTextField35.setText(Integer.toString(2 * text1));
				jTextField36.setText(Integer.toString(2 * text1));
				cc2.Dist = 2 * text1;
				cc2.source.setRC(text1 * cc2.factor);
				cc2.target.setRC(text1 * cc2.factor);
				cc2.source.setRN(text2 * cc2.factor);
				cc2.target.setRN(text2 * cc2.factor);

				if (text2 > text1 ) {
					jTextField2.setText(Integer.toString(text1));
				}

				//070610
				jTextArea1.setText(""); //clear text area
				if (jRadioButton1.isSelected()) {
					jTextArea2.setText("Absorbed Source used: Water");
				}
			}
		} catch (NumberFormatException nfe) {
			int tempval = Integer.parseInt(jTextField2.getText()) + 1;
			//radius.setText(Integer.toString(tempval));
			System.out.printf("error in input");
		}

		// Alex Rosen 7/20/2017
		// adding this for the new cell source/target layout stuff
		cellCanvasInfoNew1.setRC(text1);

		try {
			text1 = Integer.parseInt(jTextField1.getText());
		} catch (NumberFormatException e) {
		}
		jTextField3.setText(Integer.toString(2 * text1));

		cc2.setSelfDose("");
		cc2.setCrossDose("");
		cc2.repaint();
	}//GEN-LAST:event_jTextField1KeyReleased

	private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
		// Behrooz June 26
		//JTextField radius = (JTextField) evt.getSource();
		String text = jTextField2.getText();
		try {
			int TempVal = Integer.parseInt(text);
			text1 = Integer.parseInt(jTextField1.getText());
			text2 = Integer.parseInt(jTextField2.getText());
			text3 = Integer.parseInt(jTextField3.getText());
                        //Jianchao 3/26/20 bug fixes so user can put in 10s
			if (text1 <= 0) {
				jTextField1.setText(Integer.toString(2));//number in jTextField1 decreased once button clicked
				jTextField2.setText(Integer.toString(1));
				jTextField3.setText(Integer.toString(4));
			} else {
				jTextField3.setText(Integer.toString(2 * text1));
				jTextField35.setText(Integer.toString(2 * text1));
				jTextField36.setText(Integer.toString(2 * text1));

				cc2.Dist = 2 * text1;
				cc2.source.setRC(text1 * cc2.factor);
				cc2.target.setRC(text1 * cc2.factor);
				cc2.source.setRN(text2 * cc2.factor);
				cc2.target.setRN(text2 * cc2.factor);

				if (text2 > text1 ) {
					jTextField2.setText(Integer.toString(text1));
				}

				//070610
				jTextArea1.setText(""); //clear text area
				if (jRadioButton1.isSelected()) {
					jTextArea2.setText("Absorbed Source used: Water");
				}
			}
		} catch (NumberFormatException nfe) {
			//int tempval = Integer.parseInt(jTextField1.getText()) - 1;
			//radius.setText(Integer.toString(tempval));
			System.out.printf("error in input");
		}

		// Alex Rosen 7/20/2017
		// adding this for the new cell source/target layout stuff
		cellCanvasInfoNew1.setRN(text2); //Jianchao 3/17/20

		cc2.setSelfDose("");
		cc2.setCrossDose("");
		cc2.repaint();
	}//GEN-LAST:event_jTextField2KeyReleased

	private void jTextField35KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField35KeyReleased
		jTextField3.setText(jTextField35.getText());
		jTextField36.setText(jTextField35.getText());
	}//GEN-LAST:event_jTextField35KeyReleased

	private void jTabbedPane2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MousePressed

		jTextField36.setText(jTextField3.getText());
		jTextField35.setText(jTextField3.getText());
		text1 = Integer.parseInt(jTextField1.getText());
		text3 = Integer.parseInt(jTextField3.getText());

		if (text3 > 2 * text1) {
			jTextField3.setText(Integer.toString(text3));
			cc2.Dist = text3;
			cc2.repaint();
		} else {
			text3 = 2 * text1;
			jTextField3.setText(Integer.toString(text3));
			cc2.Dist = text3;
			cc2.repaint();
		}
	}//GEN-LAST:event_jTabbedPane2MousePressed

	private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
		// 1-D cell pair compute button
		jTextArea5.setText("");
		jTextArea3.setText("");
		text3 = Integer.parseInt(jTextField3.getText());
		jTextField36.setText(Integer.toString(text3));
		jTextField35.setText(Integer.toString(text3));
		cc2.Dist = text3;
		cc2.repaint();
		int MaxDist = Integer.parseInt(jTextField3.getText());

		//revised 09/09/2010
		if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
			if (jTextField10.getText().isEmpty()) {
				jTextField10.setBackground(Color.red);
			} else if (!jTextField10.getText().isEmpty()) {
				jTextField10.setBackground(Color.white);
			}
		}
		jLabel22.setText("   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s");
		jLabel26.setText("Distance   S(C<--C)   S(C<--CS)    S(N<--N)    S(N<--Cy)    S(N<--CS)    S(Cy<--N)    S(Cy<--CS)    S(Cy<--Cy)");
		jTextArea1.setText("");

		// Alex Rosen 8/26/2016
		ArrayList<double[]> data;
		double rCell = Double.parseDouble(jTextField1.getText());
		double rNuc = Double.parseDouble(jTextField2.getText());
		double[] r = new double[2 * MaxDist + 1 - 2 * (int) rCell];
		for (int i = 0; i < r.length; i++) {
			r[i] = i + rCell + rCell;
		}

		if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
			// mono energetic aplha particles and electrons respectively

			//updated 09/14/2016
			System.out.println("new electron/alpha selected!");
			Double en = Double.parseDouble(jTextField10.getText());
			Double xn = Double.parseDouble(jTextField11.getText()); // 11/17/2010

			data = new ArrayList<double[]>();

			if (jRadioButton2.isSelected()) {
				// is electron
                                iso = "Electron";
				System.out.println("adding to data: e-");
				data.add(new double[]{5.0, xn, en});
			} else if (jRadioButton1.isSelected()) {
				// is alpha
                                iso = "Alpha Particle";
				System.out.println("adding to data: alpha");
				data.add(new double[]{8.0, xn, en});
			}

			CalTest3D_2 calTest3D_2 = new CalTest3D_2();
			SelfDose_2 selfDose_2 = new SelfDose_2();
			selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
			sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel51, MaxDist, r, data);
		} else if (jRadioButton3.isSelected() || jRadioButton4.isSelected()) {
			//for user created

			data = new ArrayList<double[]>();
			Scanner s = new Scanner(jTextArea2.getText());
			String input = "";
			String[] split;
			double datums[] = new double[4];
			while (!(input = s.nextLine()).equals("START RADIATION RECORDS")) {
				// do nothng
			}
			while (!(input = s.nextLine()).equals("END RADIATION RECORDS")) {
				split = input.trim().split("\\s+");
				for (int i = 0; i < 3; i++) {
					datums[i] = Double.parseDouble(split[i]);
				}
				data.add(new double[]{Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])});
			}

			CalTest3D_2 calTest3D_2 = new CalTest3D_2();
			SelfDose_2 selfDose_2 = new SelfDose_2();
			selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
			sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel51, MaxDist, r, data);

		} else if (jRadioButton12.isSelected()) {
			// for new data from MIRD CD
			// beta average

			//123456798
			iso = String.valueOf(jList2.getSelectedValue());
			System.out.println("selected MIRD iso file name: " + iso);

			data = ArrayListIn3.readMIRDdata(iso, true);
			CalTest3D_2 calTest3D_2 = new CalTest3D_2();
			SelfDose_2 selfDose_2 = new SelfDose_2();
			selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
			sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel51, MaxDist, r, data);

		} else if (jRadioButton13.isSelected()) {
			// Pre-defined radionuclide
			// beta full
			iso = String.valueOf(jList2.getSelectedValue()).toLowerCase(Locale.ENGLISH);
			System.out.println("selected iso file name: " + iso);

			data = ArrayListIn3.readOTHERdata(iso);
			CalTest3D_2 calTest3D_2 = new CalTest3D_2();
			SelfDose_2 selfDose_2 = new SelfDose_2();
			selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
			sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel51, MaxDist, r, data);

		}

		Toolkit.getDefaultToolkit().beep();
		/**
		 * ******************
		 * add 070610 once Enter pressed, show changes on values
		 */
		Graphics progress = jPanel51.getGraphics();
		int Progwidth = jPanel51.getWidth();
		int ProgHeight = jPanel51.getHeight();
		progress.clearRect(0, 0, Progwidth, ProgHeight);
		progress.setColor(Color.GREEN);
		progress.fillRect(1, 1, Progwidth, ProgHeight);
		progress.setColor(Color.BLACK);
		progress.drawString("100%", Progwidth / 2 - 5, ProgHeight / 2 + 6);

		ArrayList list = new ArrayList();
		StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
		while (tokens.hasMoreTokens()) {
			list.add(tokens.nextElement());
		}
		
		c = Integer.parseInt(jTextField1.getText());
		n = Integer.parseInt(jTextField2.getText());
		d = Integer.parseInt(jTextField3.getText());
		maxRow = jTextArea1.getLineCount() - 1;// 09/09/2010

		String selfDose, crossDose;
                double selfD = 0, crossD = 0, selfDNu = 0, selfDCy = 0, crossDNu = 0, crossDCy = 0;
		System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d);

		//updated 07/14/09
		System.out.println("selected iso file name: " + iso);
		pro = jList3.getSelectedIndex(); //target
		//readData = new ReadInData(iso);

		/**
		 * ********
		 * 070210 get self/cross dose
		 */
                
                //072820 v3.01 JCW
                try {
			// the textfield 15 has 2 spots in the array because when the target region is changed it will be recycled
			activityFractions[0] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[1] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[2] = Double.parseDouble(jTextField16.getText()) / 100D;
			activityFractions[3] = Double.parseDouble(jTextField17.getText()) / 100D;
		} catch (Exception e) {
			System.err.println("please fill in the % activity fields text field ");
		}
                switch (pro){
                    // Radiation Target: Cell
                    case(0): /* self, C->C + self, CS->C */
                        selfD = selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3];
                        crossD = sVals[0][0][0] * activityFractions[2] + sVals[0][0][1] * activityFractions[3]; 
                        break;
                    // Radiation Target: Nucleus
                    case(1): /* self, N->N + self, Cy->N + self, CS->N */
                        selfD = selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3];
                        crossD = sVals[0][0][2] * activityFractions[1] + sVals[0][0][3] * activityFractions[2] + sVals[0][0][4] * activityFractions[3];
                        break;
                    // Radiation Target: Cytoplasm
                    case(2): /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        selfD = selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3];
                        crossD = sVals[0][0][5] * activityFractions[1] + sVals[0][0][7] * activityFractions[2] + sVals[0][0][6] * activityFractions[3];
                        break;

                    // Radiation Target: Nucleus + Cyto
                    case(3):  /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        selfDNu = selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3];
                        selfDCy = selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3];
                        crossDNu = sVals[0][0][2] * activityFractions[1] + sVals[0][0][3] * activityFractions[2] + sVals[0][0][4] * activityFractions[3];
                        crossDCy = sVals[0][0][5] * activityFractions[1] + sVals[0][0][7] * activityFractions[2] + sVals[0][0][6] * activityFractions[3];
                        break;
                }
                NumberFormat nf = new DecimalFormat("0.00E00");
                cc2.setProcess(pro);
                //v2 code
                String doseInfo = "";
		try {
			if (d < maxRow + 1) {                          
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				crossDose = list.get(d - 1).toString().trim().split("\\s+")[pro + 1];
                                if(pro == 3){
                                    cc2.setSelfDose("Self Absorbed Dose to Nucleus = " + nf.format(selfDNu) + " Gy/Bq-s. Self Absorbed Dose to Cytoplasm = " + nf.format(selfDCy) + " Gy/Bq-s");
                                    cc2.setCrossDose("Cross Absorbed Dose to Neighbor Nucleus = " + nf.format(crossDNu) + " Gy/Bq-s. Cross Absorbed Dose to Neighbor Cytoplasm = " + nf.format(crossDCy) + " Gy/Bq-s");
                                    doseInfo += "Self Absorbed Dose to Nucleus = " + nf.format(selfDNu) + " Gy/Bq-s. Self Absorbed Dose to Cytoplasm = " + nf.format(selfDCy) + " Gy/Bq-s\n";
                                    doseInfo += "Cross Absorbed Dose to Neighbor Nucleus = " + nf.format(crossDNu) + " Gy/Bq-s. Cross Absorbed Dose to Neighbor Cytoplasm = " + nf.format(crossDCy) + " Gy/Bq-s\n";
                                } else if(pro == 2){
                                    cc2.setSelfDose("Self Absorbed Dose to Cytoplasm = " + nf.format(selfD) + " Gy/Bq-s");
                                    cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cytoplasm = " + nf.format(crossD) + " Gy/Bq-s");
                                    doseInfo += "Self Absorbed Dose to Cytoplasm = " + nf.format(selfD) + " Gy/Bq-s\n";
                                    doseInfo += "Cross Absorbed Dose to Neighbor Cytoplasm = " + nf.format(crossD) + " Gy/Bq-s\n";
                                } else if(pro == 1){
                                    cc2.setSelfDose("Self Absorbed Dose to Nucleus = " + nf.format(selfD) + " Gy/Bq-s");
                                    cc2.setCrossDose("Cross Absorbed Dose to Neighbor Nucleus = " + nf.format(crossD) + " Gy/Bq-s");
                                    doseInfo += "Self Absorbed Dose to Nucleus = " + nf.format(selfD) + " Gy/Bq-s\n";
                                    doseInfo += "Cross Absorbed Dose to Neighbor Nucleus = " + nf.format(crossD) + " Gy/Bq-s\n";
                                } else if(pro == 0){
                                    cc2.setSelfDose("Self Absorbed Dose to Cell = " + nf.format(selfD) + " Gy/Bq-s");
                                    cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell = " + nf.format(crossD) + " Gy/Bq-s");
                                    doseInfo += "Self Absorbed Dose to Cell = " + nf.format(selfD) + " Gy/Bq-s\n";
                                    doseInfo += "Cross Absorbed Dose to Neighbor Cell = " + nf.format(crossD) + " Gy/Bq-s\n";
                                }
				
				cc2.isotope = iso;
				cc2.repaint();

				//add value to textField
			} else {
				//modified 08/05/09
				selfDose = list.get(0).toString().trim().split("\\s+")[pro + 1];
				cc2.setSelfDose("Self Absorbed Dose to Cell : " + selfDose + " Gy/Bq-s");
				cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : Exceeds range of particle(s)");
				cc2.isotope = iso;
				cc2.repaint();
			}
		} catch (Exception e) {
			cc2.setSelfDose("Self Absorbed Dose to Cell : Check Input");
			cc2.setCrossDose("Cross Absorbed Dose to Neighbor Cell : Check Input");
			cc2.isotope = iso;
			cc2.repaint();
		}
		/**
		 * ********************
		 * revised 10/24/2010 when rc>11
		 */
		//071310 set default line to view in textarea
		try {
			if (!jTextArea1.getText().isEmpty()) {
				jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(1));
			}
		} catch (BadLocationException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}

		//Update the output textarea: s-value
		jTextArea3.append( "Distance   S(C<--C)  S(C<--CS)   S(N<--N)   S(N<--Cy)   S(N<--CS)   S(Cy<--N)   S(Cy<--CS)   S(Cy<--Cy)\n" );
		jTextArea3.append( "   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s\n" );
		jTextArea3.append(jTextArea1.getText());
                
                //update the output
                String tempsource = "", temp1;
                temp1 = jList3.getSelectedValue().toString();
                tempsource = "Radius of cell (um) =   " + jTextField1.getText() + "\n"
                + "Radius of cell nucleus (um) =    " + jTextField2.getText() + "\n"
                + "Distance between cells (um) =    " + jTextField3.getText() + "\n";

                tempsource += "\n";
                tempsource += "Target Region: " + jList3.getSelectedValue().toString() + "\n";
                tempsource += "Source Regions:\n"
                + "    Cell Surface: " + jTextField17.getText() + "%\n"
                + "    Cytoplasm   : " + jTextField16.getText() + "%\n"
                + "    Nucleus     : " + jTextField15.getText() + "%\n\n";

                if (jRadioButton12.isSelected()) {
                tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Average Energy Spectrum" + "\n" + tempsource;
                } else if (jRadioButton13.isSelected()) {
                    tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Full Energy Spectrum" + "\n" + tempsource;
                } else if (jRadioButton1.isSelected()) {
                    tempsource = "Monoenergetic particle emitter =   Alpha Particle" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n" + tempsource;
                } else if (jRadioButton2.isSelected()) {
                    tempsource = "Monoenergetic particle emitter =   Electron" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n" + tempsource;
                }
                
                jTextArea5.append(
                    version.concat("\n")
                    + "1-D Cell Pair\n\n" 
                    + tempsource
                    + doseInfo
                );
	}//GEN-LAST:event_jButton7ActionPerformed

	private void jTextField35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField35ActionPerformed
		text3 = Integer.parseInt(jTextField35.getText());
		jTextField3.setText(Integer.toString(text3));
		jTextField36.setText(Integer.toString(text3));
		cc2.Dist = text3;
		cc2.repaint();
	}//GEN-LAST:event_jTextField35ActionPerformed

	private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
		C3D.Zoom = C3D.Zoom * 1.5;
		C3D.repaint();
	}//GEN-LAST:event_jButton21ActionPerformed

	private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
		C3D.Zoom = ZoomReset;
		C3D.azimuth = 35;
		C3D.elevation = 30;
		C3D.repaint();
	}//GEN-LAST:event_jButton22ActionPerformed

	private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
		C3D.Zoom = C3D.Zoom * .75;
		C3D.repaint();
	}//GEN-LAST:event_jButton23ActionPerformed

	private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
            jLabel110.setVisible(false);
            if(jTabbedPane1.getSelectedIndex()!= 4 && jTabbedPane1.getSelectedIndex()!= 0 && jTabbedPane1.getSelectedIndex()!= 1 && jTabbedPane1.getSelectedIndex()!= 5 && jTabbedPane1.getSelectedIndex()!= 6 && jTabbedPane1.getSelectedIndex()!= 7){    
		if (!(jRadioButton12.isSelected() || jRadioButton13.isSelected() || jRadioButton1.isSelected() || jRadioButton2.isSelected() || jRadioButton3.isSelected() || jRadioButton4.isSelected())) {
			jTabbedPane1.setSelectedIndex(0);
			jLabel14.setText("Please select a source!");
		} else if ((jRadioButton12.isSelected() || jRadioButton13.isSelected()) && (jList2.getSelectedIndex() == -1)) {
			jTabbedPane1.setSelectedIndex(0);
			jLabel14.setText("Please select a source!");
		} else {
			jLabel14.setText("");
		}
               
		jTextField35.setText(jTextField3.getText());
		jTextField36.setText(jTextField3.getText());
            }
	}//GEN-LAST:event_jTabbedPane1MouseClicked

	private void jTextArea5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MousePressed
		if (evt.getButton() == MouseEvent.BUTTON3) {
			jPopupMenu1.show(evt.getComponent(),
					evt.getX(), evt.getY());
		}
	}//GEN-LAST:event_jTextArea5MousePressed

	private void jTextArea3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea3MousePressed
		if (evt.getButton() == MouseEvent.BUTTON3) {
			jPopupMenu2.show(evt.getComponent(),
					evt.getX(), evt.getY());
		}
	}//GEN-LAST:event_jTextArea3MousePressed

	private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
		jTextArea2.copy();
	}//GEN-LAST:event_jMenuItem12ActionPerformed

	private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
		try {
			//09/09/2010 add print function
			jTextArea2.print();
		} catch (PrinterException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}//GEN-LAST:event_jMenuItem14ActionPerformed

	private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
		int option = jFileChooser1.showSaveDialog(Home1.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			try {
				File file = jFileChooser1.getSelectedFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				jTextArea2.write(out);
				out.flush();
				out.close(); //writes the content to the file

				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}//GEN-LAST:event_jMenuItem15ActionPerformed

	private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
		try {
			// 09/20/2010 add Print to popup menu in Input area
			jTextArea3.print();
		} catch (PrinterException ex) {
			Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}//GEN-LAST:event_jMenuItem8ActionPerformed

	private void jTextPane1HyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_jTextPane1HyperlinkUpdate
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				Desktop.getDesktop().browse(evt.getURL().toURI());
			} catch (URISyntaxException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}//GEN-LAST:event_jTextPane1HyperlinkUpdate

	private void jComboBox11ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox11ItemStateChanged
		//jButton17ActionPerformed(null);
                //JCW 8/13/20 Update curve without recalculate
                SurvivalFraction survivalFraction = new SurvivalFraction();
                double cellnumber = 0;
                try {
                    cellnumber = Double.parseDouble(jTextField25.getText());  
                } catch (Exception e) {
                    cellnumber = 0;
                }
                double miny = .00001;
                double Tau = Double.parseDouble(jTextField31.getText()) * 3600.0;
                if(cellnumber > 0) miny = 1/cellnumber; 

                try {                     
                    if(useComplexRadiobiologicalParams){
                       survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, SurvCalcComplex.PlotOutput, Tau, cellnumber);
                    } else{
                        survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, SurvCalc.PlotOutput, Tau, cellnumber);
                    }
//                    survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, PlotOutput3D, Tau);
                } catch (Exception e) {

                }                

	}//GEN-LAST:event_jComboBox11ItemStateChanged

	private void jComboBox12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox12ItemStateChanged
		//jButton17ActionPerformed(null);
                //JCW 8/13/20 Update curve without recalculate
                SurvivalFraction survivalFraction = new SurvivalFraction();
                double cellnumber = 0;
                try {
                    cellnumber = Double.parseDouble(jTextField25.getText());  
                } catch (Exception e) {
                    cellnumber = 0;
                }
                        
                double miny = .00001;
                double Tau = Double.parseDouble(jTextField31.getText()) * 3600.0;
                if(cellnumber > 0) miny = 1/cellnumber; 
                try {
                    survivalFraction.generateSurvivalCurve(jPanel11, jComboBox11, jComboBox12, miny, PlotOutput3D, Tau, cellnumber);
                } catch (Exception e) {

                }                    
	}//GEN-LAST:event_jComboBox12ItemStateChanged

	private void jTextField10KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyReleased
		jTextArea1.setText("");
	}//GEN-LAST:event_jTextField10KeyReleased

	private void jTextField11KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyReleased
		jTextArea1.setText("");
	}//GEN-LAST:event_jTextField11KeyReleased

	private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
		if (jCheckBox1.isSelected()) {
			double shellWidth = Double.parseDouble(jTextField26.getText()) / 4.0;
			jLabel30.setVisible(true);
			jTextField5.setText("" + shellWidth);
			jTextField5.setVisible(true);
                        jCheckBox3.setVisible(true);
		} else {
			jLabel30.setVisible(false);
			jTextField5.setVisible(false);
                        jLabel122.setText("");
                        jCheckBox3.setSelected(false);
                        jCheckBox3.setVisible(false);
                        jTextField28.setText("100");
		}
	}//GEN-LAST:event_jCheckBox1ActionPerformed

	private void jTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyTyped
		// necrotic textField edited
		int TempVal = 0;
		String s = jTextField5.getText() + evt.getKeyChar();
		try {
			TempVal = Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			jTextField5.setText("");
		}
	}//GEN-LAST:event_jTextField5KeyTyped

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		EquationsImage e = new EquationsImage("/res/Equations.PNG");
	}//GEN-LAST:event_jButton1ActionPerformed

	private void jCheckBox2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox2StateChanged
		useComplexRadiobiologicalParams = jCheckBox2.isSelected();
	}//GEN-LAST:event_jCheckBox2StateChanged

	private void jTable1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyTyped

	}//GEN-LAST:event_jTable1KeyTyped

	private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
		jTable1.getTableHeader().setPreferredSize(new Dimension(750, 72));
		TableModel model = jTable1.getModel();
		for (int i = 0; i < model.getColumnCount() - 2; i++) {
			try {
				complexRadiobiologicalParams[0][i] = Double.parseDouble(model.getValueAt(0, i + 2).toString());
				complexRadiobiologicalParams[1][i] = Double.parseDouble(model.getValueAt(0, i + 2).toString());
				complexRadiobiologicalParams[2][i] = Double.parseDouble(model.getValueAt(0, i + 2).toString());
				complexRadiobiologicalParams[3][i] = Double.parseDouble(model.getValueAt(1, i + 2).toString());
				complexRadiobiologicalParams[4][i] = Double.parseDouble(model.getValueAt(1, i + 2).toString());
				complexRadiobiologicalParams[5][i] = Double.parseDouble(model.getValueAt(1, i + 2).toString());
				complexRadiobiologicalParams[6][i] = Double.parseDouble(model.getValueAt(2, i + 2).toString());
				complexRadiobiologicalParams[7][i] = Double.parseDouble(model.getValueAt(3, i + 2).toString());
				complexRadiobiologicalParams[8][i] = Double.parseDouble(model.getValueAt(4, i + 2).toString());
				complexRadiobiologicalParams[9][i] = Double.parseDouble(model.getValueAt(5, i + 2).toString());
				complexRadiobiologicalParams[10][i] = Double.parseDouble(model.getValueAt(6, i + 2).toString());
			} catch (Exception e) {
				System.err.println("could not complete table read in");
				System.err.println(e.getClass());
			}
		}
                System.out.println("This is what user input into table:");
		for (int i = 0; i < complexRadiobiologicalParams.length; i++) {
			for (int j = 0; j < complexRadiobiologicalParams[i].length; j++) {
				System.out.print(complexRadiobiologicalParams[i][j] + "  ");
			}
			System.out.println();
		}
	}//GEN-LAST:event_jTable1PropertyChange

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		// the import from csv button
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Scanner sc = new Scanner(file);
				for (int h = 0; h < jTable1.getRowCount(); h++) {
					if (sc.hasNextLine()) {
						String arr[] = sc.nextLine().split(",");
						int i;
						for (i = 2; i < jTable1.getColumnCount() && i < arr.length + 2; i++) {
							jTable1.getModel().setValueAt(Double.parseDouble(arr[i - 2]), h, i);
						}
						if (i < jTable1.getColumnCount())
							System.err.println("Too few  entries in given data.  Please check that it is the correct file.");
						if (i < arr.length)
							System.err.println("Too many entries in given data. Please check that it is the correct file.");
					} else {
						System.err.println("not enough lines in imported data");
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}//GEN-LAST:event_jButton3ActionPerformed

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
		// The export as csv button
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				FileWriter wr = new FileWriter(file);
				String line = "";
				for (int h = 0; h < jTable1.getRowCount(); h++) {
					for (int i = 2; i < jTable1.getColumnCount(); i++) {
						line += ',' + jTable1.getModel().getValueAt(h, i).toString();
					}
					line = line.substring(1) + '\n';
					wr.write(line);
					line = "";
				}

				wr.flush();
				wr.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}//GEN-LAST:event_jButton4ActionPerformed

	private void jTextField16KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField16KeyReleased
		try {
			double N = Double.parseDouble(jTextField15.getText());
                        double Cy = Double.parseDouble(jTextField16.getText());
                        double CS = Double.parseDouble(jTextField17.getText());

			double total = N + Cy + CS;
			jLabel109.setText("Total: " + total);
			if (total != 100) {
				jLabel109.setForeground(Color.RED);
				jLabel110.setVisible(true);
			} else {
				jLabel109.setForeground(Color.BLACK);
				jLabel110.setVisible(false);
			}
                        cc2.updateSource(N, Cy, CS);
                        //JCW 8/17/20 Render color on the src only when > 0
                       cellCanvasInfoNew1.setFractions(N, Cy, CS);
                       
		} catch (Exception e) {
		}
		cc2.repaint();
                System.out.println("Cy  " + jTextField16.getText());
	}//GEN-LAST:event_jTextField16KeyReleased

	private void jTextField15KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField15KeyReleased
		try {
			double N = Double.parseDouble(jTextField15.getText());
                        double Cy = Double.parseDouble(jTextField16.getText());
                        double CS = Double.parseDouble(jTextField17.getText());

			double total = N + Cy + CS;
			jLabel109.setText("Total: " + total);
			if (total != 100) {
				jLabel109.setForeground(Color.RED);
				jLabel110.setVisible(true);
			} else {
				jLabel109.setForeground(Color.BLACK);
				jLabel110.setVisible(false);
			}
                        cc2.updateSource(N, Cy, CS);
                        //JCW 8/17/20 Render color on the src only when > 0
                       cellCanvasInfoNew1.setFractions(N, Cy, CS);
                       
		} catch (Exception e) {
		}
		cc2.repaint();
                System.out.println("N   " + jTextField15.getText());
	}//GEN-LAST:event_jTextField15KeyReleased

	private void jTextField17KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField17KeyReleased
		try {
			double N = Double.parseDouble(jTextField15.getText());
                        double Cy = Double.parseDouble(jTextField16.getText());
                        double CS = Double.parseDouble(jTextField17.getText());

			double total = N + Cy + CS;
			jLabel109.setText("Total: " + total);
			if (total != 100) {
				jLabel109.setForeground(Color.RED);
				jLabel110.setVisible(true);
			} else {
				jLabel109.setForeground(Color.BLACK);
				jLabel110.setVisible(false);
			}
                        cc2.updateSource(N, Cy, CS);
                        //JCW 8/17/20 Render color on the src only when > 0
                       cellCanvasInfoNew1.setFractions(N, Cy, CS);
                       
		} catch (Exception e) {
		}
		cc2.repaint();
                
                System.out.println("CS  " + jTextField17.getText());
	}//GEN-LAST:event_jTextField17KeyReleased

	private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jTextField16ActionPerformed

	private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jTextField11ActionPerformed

	private void jTextField3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextField3PropertyChange

	}//GEN-LAST:event_jTextField3PropertyChange

	private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
//		jButton17ActionPerformed(null);               
                RadialHistogram radialHistogram = new RadialHistogram();
                String Shape = jComboBox2.getSelectedItem().toString();
                try{
                    double Tau = Double.parseDouble(jTextField31.getText()) * 3600.0;
                    radialHistogram.generateHistogram(Shape, avgActivity, avgDose, avgSelfDose, avgCrossDose, ulAvgCrossDose, numCellsAt, ulNumCellsAt, Tau, longestaxis, Radius, jPanel56, jComboBox3);
                } catch (Exception e){
                    
                }
                
                
	}//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField17ActionPerformed

    private void jTextField18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField18ActionPerformed

    private void jLabel95MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel95MouseClicked
        C3D.luFlag = !C3D.luFlag;
        C3D.repaint();
        if(C3D.luFlag == true){
            jLabel95.setText(" X");
        } else {
            jLabel95.setText("");
        }
        
    }//GEN-LAST:event_jLabel95MouseClicked

    private void jLabel98MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel98MouseClicked
        C3D.llFlag = !C3D.llFlag;
        C3D.repaint();
        if(C3D.llFlag == true){
            jLabel98.setText(" X");
        } else {
            jLabel98.setText("");
        }
    }//GEN-LAST:event_jLabel98MouseClicked

    private void jLabel71MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel71MouseClicked
        C3D.dlFlag = !C3D.dlFlag;
        C3D.repaint();
        if(C3D.dlFlag == true){
            jLabel71.setText(" X");
        } else {
            jLabel71.setText("");
        }
    }//GEN-LAST:event_jLabel71MouseClicked

    private void jLabel99MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel99MouseClicked
        C3D.duFlag = !C3D.duFlag;
        C3D.repaint();
        if(C3D.duFlag == true){
            jLabel99.setText(" X");
        } else {
            jLabel99.setText("");
        }
    }//GEN-LAST:event_jLabel99MouseClicked

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
        C3DS.Zoom = C3DS.Zoom * 1.25;
        C3DS.repaint();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jLabel114MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel114MouseClicked
        // TODO add your handling code here:
        C3DS.llFlag = !C3DS.llFlag;
        C3DS.repaint();
        if(C3DS.llFlag == true){
            jLabel114.setText(" X");
        } else {
            jLabel114.setText("");
        }
    }//GEN-LAST:event_jLabel114MouseClicked

    private void jLabel116MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel116MouseClicked
        // TODO add your handling code here:
        C3DS.luFlag = !C3DS.luFlag;
        C3DS.repaint();
        if(C3DS.luFlag == true){
            jLabel116.setText(" X");
        } else {
            jLabel116.setText("");
        }
    }//GEN-LAST:event_jLabel116MouseClicked

    private void jLabel117MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel117MouseClicked
        // TODO add your handling code here:
        C3DS.dlFlag = !C3DS.dlFlag;
        C3DS.repaint();
        if(C3DS.dlFlag == true){
            jLabel117.setText(" X");
        } else {
            jLabel117.setText("");
        }
    }//GEN-LAST:event_jLabel117MouseClicked

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
        C3DS.Zoom = C3DS.Zoom * .75;
        C3DS.repaint();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jLabel120MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel120MouseClicked
        // TODO add your handling code here:
        C3DS.duFlag = !C3DS.duFlag;
        C3DS.repaint();
        if(C3DS.duFlag == true){
            jLabel120.setText(" X");
        } else {
            jLabel120.setText("");
        }
    }//GEN-LAST:event_jLabel120MouseClicked

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
        C3DS.Zoom = ZoomReset;
        C3DS.azimuth = 0;
        C3DS.elevation = 90;
        C3DS.repaint();
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jPanel59MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel59MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel59MousePressed

    private void jPanel59MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel59MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel59MouseDragged

    private void jPanel59MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel59MouseWheelMoved
        // TODO add your handling code here:
        int notches = evt.getWheelRotation();
        int numCell = 0;
        String shape = jComboBox2.getSelectedItem().toString();
        try {
            numCell = Integer.parseInt(jTextField19.getText());
        } catch (Exception e){
            e.printStackTrace();
        }
        //move UP, notches is negative. Down, positive.
        numCell -= notches;
        
        if (shape.equals("Rod")|| shape.equals("Cone")){
               if(numCell*text3 <= Height && numCell*text3 >= 0){
                   C3DS.axialHeight = -numCell*text3;
                   jTextField19.setText(String.valueOf(numCell));
                   C3DS.repaint();
               }
           } else if (shape.equals("Ellipsoid")){
               if(numCell*text3 >= -Height && numCell*text3 <= Height){
                   C3DS.axialHeight = numCell*text3;
                   jTextField19.setText(String.valueOf(numCell));
                   C3DS.repaint();
               }     
           } else {
               if(numCell*text3 >= -Radius && numCell*text3 <= Radius){
                   C3DS.axialHeight = -numCell*text3;
                   jTextField19.setText(String.valueOf(numCell));
                   C3DS.repaint();
               }
           }
    }//GEN-LAST:event_jPanel59MouseWheelMoved

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // save the output
        JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try(FileWriter wr = new FileWriter(file)) {				
				jTextArea5.write(wr);
				wr.flush();
				wr.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        C2D.Zoom = C2D.Zoom * .75;
        C2D.repaint();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        C2D.Zoom = ZoomReset;
        C2D.repaint();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        C2D.Zoom = C2D.Zoom * 1.5;
        C2D.repaint();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jComboBox10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox10ItemStateChanged
        //jButton18ActionPerformed(null);
        //JCW 8/13/20 Update curve without recalculate
                SurvivalFraction survivalFraction = new SurvivalFraction();
                double cellnumber = 0;
                try {
                    cellnumber = Double.parseDouble(jTextField25.getText());  
                } catch (Exception e) {
                    cellnumber = 0;
                }
                double miny = .00001;
                double Tau = Double.parseDouble(jTextField47.getText()) * 3600.0;
                if(cellnumber > 0) miny = 1/cellnumber; 
                try {
                    survivalFraction.generateSurvivalCurve(jPanel40, jComboBox10, jComboBox9, miny, PlotOutput2D, Tau, cellnumber);
                } catch (Exception e) {}
    }//GEN-LAST:event_jComboBox10ItemStateChanged

    private void jComboBox9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox9ItemStateChanged
        //jButton18ActionPerformed(null);
        //JCW 8/13/20 Update curve without recalculate
                SurvivalFraction survivalFraction = new SurvivalFraction();
                double cellnumber = 0;
                try {
                    cellnumber = Double.parseDouble(jTextField43.getText());  
                } catch (Exception e) {
                    cellnumber = 0;
                }
                double miny = .00001;
                double Tau = Double.parseDouble(jTextField47.getText()) * 3600.0;
                if(cellnumber > 0) miny = 1/cellnumber; 

                try {
                    survivalFraction.generateSurvivalCurve(jPanel40, jComboBox10, jComboBox9, miny, PlotOutput2D, Tau, cellnumber);
                } catch (Exception e) {}

    }//GEN-LAST:event_jComboBox9ItemStateChanged

    private void jTextField36KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField36KeyReleased
        jTextField3.setText(jTextField36.getText());
        jTextField35.setText(jTextField36.getText());
    }//GEN-LAST:event_jTextField36KeyReleased

    private void jTextField36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField36ActionPerformed
        text3 = Integer.parseInt(jTextField36.getText());
        jTextField3.setText(Integer.toString(text3));
        jTextField35.setText(Integer.toString(text3));
        cc2.Dist = text3;
        cc2.repaint();
    }//GEN-LAST:event_jTextField36ActionPerformed

    private void jTextField43KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField43KeyReleased

        // Behrooz June 26
        jTextField43.setBackground(Color.WHITE);
        jTextArea1.setText("");
        JTextField radius = (JTextField) evt.getSource();
        String Cellcount = radius.getText();
        jTextField41.setText("");
        jTextField42.setText("");
        try {
            int TempVal = Integer.parseInt(Cellcount);
        } catch (NumberFormatException nfe) {
            radius.setText("");
            System.out.printf("error in Radius");
        }
    }//GEN-LAST:event_jTextField43KeyReleased

    private void jTextField42KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField42KeyReleased
        // Behrooz June 26
        jTextField42.setBackground(Color.WHITE);
        jTextArea1.setText("");
        JTextField text = (JTextField) evt.getSource();
        String Cellcount = text.getText();
        jTextField43.setText("");
        try {
            int TempVal = Integer.parseInt(Cellcount);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField42KeyReleased

    private void jTextField41KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField41KeyReleased
        // Behrooz June 26
        jTextField41.setBackground(Color.WHITE);
        jTextArea1.setText("");
        JTextField text = (JTextField) evt.getSource();
        String Cellcount = text.getText();
        jTextField43.setText("");
        try {
            int TempVal = Integer.parseInt(Cellcount);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField41KeyReleased

    private void jTextField41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField41ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField41ActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        jTextField43.setText("");

        JComboBox shapes = (JComboBox) evt.getSource();
        String shape = (String) shapes.getSelectedItem();
        if ("Circle".equals(shape)) {
            jLabel77.setVisible(false);
            jTextField42.setVisible(false);
            jLabel40.setText("Radius (um):");
            jTextField43.setText("");
            jTextField43.setEditable(false);

        } else if ("Ellipse".equals(shape)) {
            jLabel77.setVisible(true);
            jLabel77.setText("Long Axis (um):");
            jLabel40.setText("Short Axis (um):");
            jTextField42.setVisible(true);
            jTextField43.setText("");
            jTextField43.setEditable(false);
        } else if ("Rectangle".equals(shape)) {
            jLabel77.setVisible(true);
            jLabel77.setText("Height (um):");
            jLabel40.setText("Width (um):");
            jTextField42.setVisible(true);
            jTextField43.setText("");
            jTextField43.setEditable(false);
        }

    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void jTextField48KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField48KeyReleased

        // Behrooz June 26
        JTextField text = (JTextField) evt.getSource();
        String TempNumber = text.getText();
        try {
            double TempVal = Double.parseDouble("0" + TempNumber);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }

    }//GEN-LAST:event_jTextField48KeyReleased

    private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed

        //Behrooz
        if (jComboBox8.getSelectedIndex() == 0) {
            // normal 2D
            jLabel87.setVisible(true);
            jTextField48.setVisible(true);
            jTextField48.setText("0.0001");
            jLabel87.setText("Standard Deviation (Bq):");
        } else if (jComboBox8.getSelectedIndex() == 1) {
            // log-normal 2D
            jLabel87.setVisible(true);
            jTextField48.setVisible(true);
            jLabel87.setText("Shape Factor (σ):");
            jTextField48.setText("1.0");
        } else if (jComboBox8.getSelectedIndex() == 2) {
            // uniform 2D
            jLabel87.setVisible(false);
            jTextField48.setVisible(false);
        } else if (jComboBox8.getSelectedIndex() == 3) {
            // linear
            jLabel87.setVisible(true);
            jTextField48.setVisible(true);
            jLabel87.setText("Constant to Approace as a % f edge Value:");
            jTextField48.setText("0.0");
        } else if (jComboBox8.getSelectedIndex() == 4) {
            // exponential 2D
            jLabel87.setVisible(true);
            jTextField48.setVisible(true);
            jLabel87.setText("Exponential Factor:");
            double R = 5.0 / Double.parseDouble(jTextField41.getText());
            jTextField48.setText("" + R);
        }
    }//GEN-LAST:event_jComboBox8ActionPerformed

    private void jTextField47KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField47KeyReleased
        // Behrooz June 26
        JTextField text = (JTextField) evt.getSource();
        String TempNumber = text.getText();
        try {
            double TempVal = Double.parseDouble("0" + TempNumber);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField47KeyReleased

    private void jTextField46KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField46KeyReleased
        // Behrooz June 26
        JTextField text = (JTextField) evt.getSource();
        String TempNumber = text.getText();
        try {
            double TempVal = Double.parseDouble("0" + TempNumber);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField46KeyReleased

    private void jTextField45KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField45KeyReleased
        // Behrooz June 26
        JTextField text = (JTextField) evt.getSource();
        String TempNumber = text.getText();
        jTextField44.setText("");
        try {
            double TempVal = Double.parseDouble("0" + TempNumber);
            if (TempVal > 100) {
                jTextField45.setText("100");
            }
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField45KeyReleased

    private void jTextField44KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField44KeyReleased
        // Behrooz June 26
        JTextField text = (JTextField) evt.getSource();
        String TempNumber = text.getText();
        try {
            int TempVal = Integer.parseInt(TempNumber);
        } catch (NumberFormatException nfe) {
            text.setText("");
        }
    }//GEN-LAST:event_jTextField44KeyReleased

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        //Multicellular Tab: 2D compute
        long TS = System.currentTimeMillis();
        if (TS - justRan < 500) {
            // yes this is a stupid hacky fix but it works
            // basically is the program just ran make it so
            // that it cannot run again immediately
            return;
        }
        
        try {
			// the textfield 15 has 2 spots in the array because when the target region is changed it will be recycled
			activityFractions[0] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[1] = Double.parseDouble(jTextField15.getText()) / 100D;
			activityFractions[2] = Double.parseDouble(jTextField16.getText()) / 100D;
			activityFractions[3] = Double.parseDouble(jTextField17.getText()) / 100D;
		} catch (Exception e) {
			System.err.println("please fill in the % activity fields text field ");
		}

        jTextArea5.setText("");
        jTextArea3.setText("");
        if (jTextField41.getText().isEmpty()) {
            jTextField41.setBackground(Color.PINK);
        } else {
            jTextField41.setBackground(Color.WHITE);
        }
        if (jTextField42.getText().isEmpty()) {
            jTextField42.setBackground(Color.PINK);
        } else {
            jTextField42.setBackground(Color.WHITE);
        }
        if (jTextField43.getText().isEmpty()) {
            jTextField43.setBackground(Color.PINK);
        } else {
            jTextField43.setBackground(Color.WHITE);
        }
        text3 = Integer.parseInt(jTextField36.getText());
        text1 = Integer.parseInt(jTextField1.getText());
        if (text3 < text1 * 2) {
            text3 = text1 * 2;
        }
        jTextField3.setText(Integer.toString(text3));
        jTextField35.setText(Integer.toString(text3));
        jTextField36.setText(Integer.toString(text3));
        cc2.Dist = text3;
        cc2.repaint();

        // BEHROOZ 06/15/2012
        int Dist = Integer.parseInt(jTextField3.getText());
        // Reads in the shape from GUI
        String Shape = jComboBox7.getSelectedItem().toString();
        // Initialize some of the parameters
        int longestaxis = 0;
        int Height = 0;
        int cellnumber = 0;
        int MaxDist;
        // if The selected shape is a sphere then it determines if the radius or the number of the cells are given
        if (Shape.equals("Circle")) {
            if (!jTextField43.getText().equals("")) {
                cellnumber = Integer.parseInt(jTextField43.getText());
                int i = 0;
                int r;
                int cr = (Dist / 2);
                for (r = cr; i < cellnumber; r = (r + cr)) {
                    i = 0;
                    for (int x = -(r / Dist) * Dist; x <= (r); x = x + Dist) {
                        for (int y = -(r / Dist) * Dist; y <= (r); y = y + Dist) {
                            if ((x * x + y * y) <= (r * r)) {
                                i++;
                            }
                        }
                    }
                }
                Radius = r - cr;
                jTextField41.setText(Integer.toString(Radius));
            } else if (!jTextField41.getText().equals("")) {
                Radius = Integer.parseInt(jTextField41.getText());
                for (int x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
                    for (int y = -(Radius / Dist) * Dist; y <= (Radius); y = y + Dist) {

                        if ((x * x + y * y) <= (Radius * Radius)) {
                            cellnumber++;
                        }

                    }
                }
                jTextField43.setText(Integer.toString(cellnumber));
            }

            // BEHROOZ 06/15/2012
            Cluster3D Cluster = new Cluster3D(cellnumber, "Circle", Dist);
            cell = Cluster.generateCircle(Radius, (Dist));
            longestaxis = Radius;

        } else if (Shape.equals("Ellipse")) {
            if (!jTextField41.getText().equals("") && !jTextField42.getText().equals("")) {
                Radius = Integer.parseInt(jTextField41.getText()) / 2;
                Height = Integer.parseInt(jTextField42.getText()) / 2;
                cellnumber = 0;
                for (double x = -(Radius / Dist) * Dist; x <= (Radius); x = x + Dist) {
                    for (double y = -(Height / Dist) * Dist; y <= (Height); y = y + Dist) {

                        if (((x * x) / (Radius * Radius) + (y * y) / (Height * Height)) <= 1) {
                            cellnumber++;
                        }

                    }
                }
                jTextField43.setText(Integer.toString(cellnumber));

            }

            Cluster3D Cluster = new Cluster3D(cellnumber, "Ellipse", Dist);
            cell = Cluster.generateEllipse(Radius, Height);

            if (Height > Radius) {
                longestaxis = Height;
            } else {
                longestaxis = Radius;
            }

        } else if (Shape.equals("Rectangle")) {
            if (!jTextField41.getText().equals("") && !jTextField42.getText().equals("")) {
                Radius = Integer.parseInt(jTextField41.getText());
                Height = Integer.parseInt(jTextField42.getText());
                cellnumber = (int) ((Radius / Dist) + 1) * (int) ((Height / Dist) + 1);

                jTextField43.setText(Integer.toString(cellnumber));

            }

            Cluster3D Cluster = new Cluster3D(cellnumber, "Rectangle", Dist);
            cell = Cluster.generateRectangle(Radius, Height);
            if (Height > Radius) {
                longestaxis = Height / 2;
            } else {
                longestaxis = Radius / 2;
            }
        }

        MaxDist = longestaxis;
        if (jTextField41.getText().isEmpty()) {
            jTextField41.setBackground(Color.PINK);
        } else {
            jTextField41.setBackground(Color.WHITE);
        }
        if (jTextField42.getText().isEmpty()) {
            jTextField42.setBackground(Color.PINK);
        } else {
            jTextField42.setBackground(Color.WHITE);
        }
        if (jTextField43.getText().isEmpty()) {
            jTextField43.setBackground(Color.PINK);
        } else {
            jTextField43.setBackground(Color.WHITE);
        }

        if (jTextArea1.getText().isEmpty()) {
            if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
                if (jTextField10.getText().isEmpty()) {
                    jTextField10.setBackground(Color.red);
                } else if (!jTextField10.getText().isEmpty()) {
                    jTextField10.setBackground(Color.white);
                }
            }

            jLabel22.setText("   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s");
            jLabel26.setText("Distance   S(C<--C)   S(C<--CS)    S(N<--N)    S(N<--Cy)    S(N<--CS)    S(Cy<--N)    S(Cy<--CS)    S(Cy<--Cy)");
            jTextArea1.setText("");

            ArrayList<double[]> data;
            double rCell = Double.parseDouble(jTextField1.getText());
            double rNuc = Double.parseDouble(jTextField2.getText());

            double[] r = new double[2 * longestaxis + 1 - 2 * (int) rCell];
            for (int i = 0; i < r.length; i++) {
                r[i] = i + rCell + rCell;
            }

            if (jRadioButton1.isSelected() || jRadioButton2.isSelected()) {
                // mono energetic aplha particles and electrons respectively

                //updated 09/14/2016
                System.out.println("new electron/alpha selected!");
                Double en = Double.parseDouble(jTextField10.getText());
                Double xn = Double.parseDouble(jTextField11.getText()); // 11/17/2010

                data = new ArrayList<double[]>();

                if (jRadioButton2.isSelected()) {
                    // is electron
                    System.out.println("adding to data: e-");
                    data.add(new double[]{5.0, xn, en});
                } else if (jRadioButton1.isSelected()) {
                    // is alpha
                    System.out.println("adding to data: alpha");
                    data.add(new double[]{8.0, xn, en});
                }

                CalTest3D_2 calTest3D_2 = new CalTest3D_2();
                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel38, MaxDist, r, data);
            } else if (jRadioButton3.isSelected() || jRadioButton4.isSelected()) {
                //for user created

                data = new ArrayList<double[]>();
                Scanner s = new Scanner(jTextArea2.getText());
                String input = "";
                String[] split;
                double datums[] = new double[4];
                while (!(input = s.nextLine()).equals("START RADIATION RECORDS")) {
                    // do nothng
                }
                while (!(input = s.nextLine()).equals("END RADIATION RECORDS")) {
                    split = input.trim().split("\\s+");
                    for (int i = 0; i < 3; i++) {
                        datums[i] = Double.parseDouble(split[i]);
                    }
                    data.add(new double[]{Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])});
                }

                CalTest3D_2 calTest3D_2 = new CalTest3D_2();
                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel38, MaxDist, r, data);

            } else if (jRadioButton12.isSelected()) {
                // for new data from MIRD CD
                // beta average

                //123456798
                iso = String.valueOf(jList2.getSelectedValue());
                System.out.println("selected MIRD iso file name: " + iso);

                data = ArrayListIn3.readMIRDdata(iso, true);
                CalTest3D_2 calTest3D_2 = new CalTest3D_2();
                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel38, MaxDist, r, data);

            } else if (jRadioButton13.isSelected()) {
                // Pre-defined radionuclide
                // beta full
                iso = String.valueOf(jList2.getSelectedValue()).toLowerCase(Locale.ENGLISH);
                System.out.println("selected iso file name: " + iso);

                data = ArrayListIn3.readOTHERdata(iso);
                CalTest3D_2 calTest3D_2 = new CalTest3D_2();
                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                sVals = calTest3D_2.calcDoses_test(rCell, rNuc, jTextArea1, jPanel38, MaxDist, r, data);

            }

            Toolkit.getDefaultToolkit().beep();

            ArrayList list = new ArrayList();
            StringTokenizer tokens = new StringTokenizer(jTextArea1.getText(), "\n");
            while (tokens.hasMoreTokens()) {
                list.add(tokens.nextElement());
            }
            c = Integer.parseInt(jTextField1.getText());
            n = Integer.parseInt(jTextField2.getText());
            d = Integer.parseInt(jTextField3.getText());
            maxRow = jTextArea1.getLineCount() - 1;
            System.out.println("rc=" + c + ", rn=" + n + ", distance=" + d);
            iso = String.valueOf(jList2.getSelectedValue());
            System.out.println("selected iso file name: " + iso);
            pro = jList3.getSelectedIndex();

        }

        String text = jTextArea1.getText(); //invisible text area to store self s-values: [column - total doses for each tgt<-src]
        String[] lines = text.split("\\r?\\n");
        int radiationtarget;
        radiationtarget = jList3.getSelectedIndex();
        C2D.Zoom = ((double) jPanelWidth / 2) / (double) longestaxis * .75;
        ZoomReset = C2D.Zoom;
        double rCell = Double.parseDouble(jTextField1.getText());
        C2D.r = (int) rCell;
        int labelcellnum = 0;
        double labelcellp;
        if (!jTextField45.getText().equals("")) {
            labelcellp = Double.parseDouble(jTextField45.getText());
            if (labelcellp < 100) {
                labelcellnum = (int) (cellnumber / 100.0 * labelcellp);
                jTextField44.setText(Integer.toString(labelcellnum));
            } else {
                labelcellp = 100;
                labelcellnum = cellnumber;
                jTextField44.setText(Integer.toString(labelcellnum));
                jTextField45.setText(String.format("%.3g%n", labelcellp));
            }
        } else if (!jTextField44.getText().equals("")) {
            labelcellnum = Integer.parseInt(jTextField44.getText());
            if (labelcellnum <= cellnumber) {
                labelcellp = (100.0 * labelcellnum / cellnumber);
            } else {
                labelcellp = 100;
                labelcellnum = cellnumber;
                jTextField44.setText(Integer.toString(labelcellnum));
            }
            jTextField45.setText(String.format("%.3g%n", labelcellp));
        }

        double MeanActivity = 0, MAC = 0;
        double Tau = 0;
        double AccuActivity = 0;
        double SelfAlpha = 1.0, CrossAlpha = 1.0, SelfBeta = 0, CrossBeta = 0;
        if (!jTextField7.getText().equals("")) {
            CrossBeta = Double.parseDouble(jTextField7.getText());
        }
        if (!jTextField8.getText().equals("")) {
            SelfBeta = Double.parseDouble(jTextField8.getText());
        }
        if (!jTextField34.getText().equals("")) {
            CrossAlpha = Double.parseDouble(jTextField34.getText());
        }
        if (!jTextField32.getText().equals("")) {
            SelfAlpha = Double.parseDouble(jTextField32.getText());
        }

        if (!jTextField46.getText().equals("") && !jTextField47.getText().equals("")) {

            Tau = Double.parseDouble(jTextField47.getText()) * 3600.0;

            MAC = Double.parseDouble(jTextField46.getText());
            MeanActivity = MAC * (double) cellnumber / (double) labelcellnum;
            AccuActivity = MeanActivity * Tau;
        }
        double ShapeFactor, Mu = 1;

        if (!jTextField48.getText().equals("")) {
            Mu = Double.parseDouble(jTextField48.getText());
        }

        //System.err.println("Time:" + Tau + "---> Mean:" + MeanActivity + " ===" + AccuActivity);
        double[][] celllabel = new double[labelcellnum][8];
        int temp3 = 0;
        double AveCellActivity = 0;
        jLabel100.setVisible(false);
        if (jComboBox8.getSelectedIndex() == 0) {
            int n = 0;
            // 2D normal Distribution
            ShapeFactor = Mu;
            NormalDistribution a = new NormalDistribution(0, ShapeFactor);
            double[] NormalD = new double[labelcellnum];
            for (int i = 0; i < labelcellnum; i++) {
                NormalD[i] = a.sample();
                if ((MeanActivity + NormalD[i]) < 0) {                    
                    jLabel100.setVisible(true);
                    return;
                }
            }
            
            for(int i = 0; i < labelcellnum; i++) {
                NormalD[i] = a.sample();
                int j = randomgen.nextInt( cellnumber );
                if(cell[j][4] != 0) {
                        i--;
                }
                else {
                    cell[j][5] = (MeanActivity + NormalD[i]) * Tau;
                    cell[j][4] = 1;
                }
            }

            for (int i = 0; i < labelcellnum; i++) {

                if (cell[i][4] != 0) {

                    if (radiationtarget == 0) {
                        // Radiation Target: Cell
                        /* self, C->C + self, CS->C */
                        cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
                    } else if (radiationtarget == 1) {
                        // Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                    } else if (radiationtarget == 2) {
                        // Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    } else if (radiationtarget == 3) {
                        // Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    }

                    System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
                    temp3++;
                    AveCellActivity = AveCellActivity + cell[i][5];
                }
            }
            AveCellActivity = AveCellActivity / labelcellnum;

        } else if (jComboBox8.getSelectedIndex() == 1) {
            // 2D log-normal distribution
            int n = 0;
            ShapeFactor = Mu;
            double sclaeFactor = Math.log(MeanActivity) - Math.pow(ShapeFactor, 2) / 2;

            LogNormalDistribution a = new LogNormalDistribution(sclaeFactor, ShapeFactor);
            double[] lognormal = a.sample(labelcellnum);
            for(int i = 0; i < labelcellnum; i++) {
                int j = randomgen.nextInt( cellnumber );
                if(cell[j][4] != 0) {
                        i--;
                }
                else {
                    cell[j][5] = (lognormal[i]) * Tau;
                    cell[j][4] = 1;
                }
            }
            for (int i = 0; i < labelcellnum; i++) {

                if (cell[i][4] != 0) {
                
                    if (radiationtarget == 0) {
                        // Radiation Target: Cell
                        /* self, C->C + self, CS->C */
                        cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
                    } else if (radiationtarget == 1) {
                        // Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                    } else if (radiationtarget == 2) {
                        // Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    } else if (radiationtarget == 3) {
                        // Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    }

                    //System.err.println(cell[j][6]);
                    System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
                    temp3++;
                    AveCellActivity = AveCellActivity + cell[i][5];
                }

            }
            AveCellActivity = AveCellActivity / labelcellnum;
        } else if (jComboBox8.getSelectedIndex() == 2) {
            // 2D uniform Distribution
            int n = 0;
            
            for(int i = 0; i < labelcellnum; i++) {
                int j = randomgen.nextInt( cellnumber );
                if(cell[j][4] != 0) {
                        i--;
                }
                else {
                        cell[j][4] = 1;
                        cell[j][5] = AccuActivity;
                }
            }

            for (int i = 0; i < labelcellnum; i++) {
                if (cell[i][4] != 0) {
                    if (radiationtarget == 0) {
                        // Radiation Target: Cell
                        /* self, C->C + self, CS->C */
                        cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
                    } else if (radiationtarget == 1) {
                        // Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                    } else if (radiationtarget == 2) {
                        // Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    } else if (radiationtarget == 3) {
                        // Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    }
                    System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
                    temp3++;
                }
            }

            AveCellActivity = AccuActivity;
        } else if (jComboBox8.getSelectedIndex() == 3) {
            // 2D Linear Distribution
            double rToCell;
            double sum1 = 0.0;
            double shortestaxis = 0;
            if (Shape.equals("Rectangle")) {
                if (Radius > Height) {
                    shortestaxis = Height / 2;
                } else {
                    shortestaxis = Radius / 2;
                }
            }
            for (int i = 0; i < labelcellnum; i++) {
                int j = randomgen.nextInt(cellnumber);
                if (cell[j][4] != 0) {
                    i--;
                } else {
                    cell[j][4] = 1;

                    if (Shape.equals("Rectangle")) {
                        if (Radius / 2.0 - Math.abs(cell[j][1]) <= Height / 2.0 - Math.abs(cell[j][2])) {
                            rToCell = shortestaxis - (Radius / 2.0 - Math.abs(cell[j][1]));
                        } else {
                            rToCell = shortestaxis - (Height / 2.0 - Math.abs(cell[j][2]));
                        }
                        cell[j][5] = rToCell / shortestaxis;
                    } else {
                        // TODO I know that this is for circle only but i don't quite know what to do for the ellipse yet
                        rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][2], 2));
                        cell[j][5] = rToCell / (Math.PI * longestaxis * longestaxis * longestaxis);
                    }

                    sum1 += cell[j][5];
                }
            }

            // normalize the data to fit the values provided by the user
            int n = 0;
            for (int i = 0; i < cellnumber; i++) {
                if (cell[i][4] != 0) {
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;

                    if (radiationtarget == 0) {
                        // Radiation Target: Cell
                        /* self, C->C + self, CS->C */
                        cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
                    } else if (radiationtarget == 1) {
                        // Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                    } else if (radiationtarget == 2) {
                        // Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    } else if (radiationtarget == 3) {
                        // Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    }

                    System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
                    temp3++;
                }
            }

        } else if (jComboBox8.getSelectedIndex() == 4) {
            // 2D Exponential Distribution

            double rToCell;
            double sum1 = 0.0;
            double A1;
            double A2;
            double b = Double.parseDouble(jTextField48.getText());
            double shortestaxis = 0;
            if (Shape.equals("Rectangle")) {
                if (Radius > Height) {
                    shortestaxis = Height / 2;
                } else {
                    shortestaxis = Radius / 2;
                }
            }
            for (int i = 0; i < labelcellnum; i++) {
                int j = randomgen.nextInt(cellnumber);
                if (cell[j][4] != 0) {
                    i--;
                } else {
                    cell[j][4] = 1;

                    if (Shape.equals("Rectangle")) {
                        if (Radius / 2.0 - Math.abs(cell[j][1]) <= Height / 2.0 - Math.abs(cell[j][2])) {
                            rToCell = shortestaxis - (Radius / 2.0 - Math.abs(cell[j][1]));
                        } else {
                            rToCell = shortestaxis - (Height / 2.0 - Math.abs(cell[j][2]));
                        }
                        A1 = Math.exp(b * rToCell) - 1.0; // TODO maybe ask Dr howell what would be best to do here
                        cell[j][5] = A1;
                    } else {
                        // TODO I know that this is for circle only but i don't quite know what to do for the ellipse yet
                        rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][2], 2));
                        A1 = Math.exp(b * (rToCell)) - 1.0;
                        A2 = Math.exp(b * (longestaxis)) * (longestaxis / b - 1.0 / (b * b)) - longestaxis * longestaxis / 2.0;
                        cell[j][5] = A1 / (A2 * 2.0 * Math.PI);
                    }
                    sum1 += cell[j][5];
                }
            }

            int n = 0;
            for (int i = 0; i < cellnumber; i++) {
                if (cell[i][4] != 0) {
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;

                    if (radiationtarget == 0) {
                        // Radiation Target: Cell
                        /* self, C->C + self, CS->C */
                        cell[i][6] = cell[i][5] * (selfSVals[0][0] * activityFractions[2] + selfSVals[0][1] * activityFractions[3]);
                    } else if (radiationtarget == 1) {
                        // Radiation Target: Nucleus
                        /* self, N->N + self, Cy->N + self, CS->N */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3]);
                    } else if (radiationtarget == 2) {
                        // Radiation Target: Cytoplasm
                        /* self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    } else if (radiationtarget == 3) {
                        // Radiation Target: Nucleus & Cytoplasm
                        /* self, N->N + self, Cy->N + self, CS->N + self, N->Cy + self, Cy->Cy + self, CS->Cy */
                        cell[i][6] = cell[i][5] * (selfSVals[0][2] * activityFractions[1] + selfSVals[0][3] * activityFractions[2] + selfSVals[0][4] * activityFractions[3] + selfSVals[0][5] * activityFractions[1] + selfSVals[0][7] * activityFractions[2] + selfSVals[0][6] * activityFractions[3]);
                    }

                    System.arraycopy(cell[i], 0, celllabel[n++], 0, 8);
                    temp3++;
                }
            }

        }

        //MeanActivity =0;
        System.err.println("temp 3 -->" + temp3 + "Start");
        final long start = System.currentTimeMillis();
        
        SurvCalc = new SurvivalCalculation();
        if (useComplexRadiobiologicalParams) {
            cell = SurvCalcComplex.calculateSurvival(
                cell, celllabel, sVals, selfSVals, complexRadiobiologicalParams, activityFractions,
                MAC, Dist, (int) rCell,
                cellnumber, jPanel38.getHeight(), jPanel38.getWidth(), radiationtarget,
                jTextArea5, jPanel38.getGraphics()
            );
            //copy array PlotOutput
            for(int i = 0; i < 8; i++){
                  double[] aMatrix = SurvCalcComplex.PlotOutput[i];
                  PlotOutput2D[i] = new double[101];
                  System.arraycopy(aMatrix, 0, PlotOutput2D[i], 0, 101);
            }
        } else {
            cell = SurvCalc.calculateSurvival( //(lines.length) * (lines.length) not used
                cell, celllabel, sVals, activityFractions,
                CrossAlpha, CrossBeta, SelfAlpha, SelfBeta, MAC, rCell,
                cellnumber, (Dist * Dist), jPanel38.getHeight(), jPanel38.getWidth(), ((lines.length) * (lines.length)), radiationtarget,
                jPanel38.getGraphics()
            );
            //copy array PlotOutput
            for(int i = 0; i < 8; i++){
                  double[] aMatrix = SurvCalc.PlotOutput[i];
                  PlotOutput2D[i] = new double[101];
                  System.arraycopy(aMatrix, 0, PlotOutput2D[i], 0, 101);
            }
        }


        C2D.cell = cell;
        C2D.Radius = longestaxis;
        C2D.repaint();

        NumberFormat formatter = new DecimalFormat("0.##E0");
        String tempsource = "", temp1 = "";
        temp1 = jList3.getSelectedValue().toString();
        tempsource = ""
        + "Radius of cell (um) =   " + jTextField1.getText() + "\n"
        + "Radius of cell nucleus (um) =    " + jTextField2.getText() + "\n"
        + "Shape =   " + Shape;
        if (Shape.equals("Circle")) {
            tempsource = tempsource + "\n" + jLabel40.getText() + "  " + jTextField41.getText();
        } else {
            tempsource = tempsource + "\n" + jLabel40.getText() + "  " + jTextField41.getText() + "\n" + jLabel77.getText() + "  " + jTextField42.getText();
        }

        tempsource += "\n";
        tempsource += "Target Region: " + jList3.getSelectedValue().toString() + "\n";
        tempsource += "Source Regions:\n"
        + "    Cell Surface: " + jTextField17.getText() + "%\n"
        + "    Cytoplasm   : " + jTextField16.getText() + "%\n"
        + "    Nucleus     : " + jTextField15.getText() + "%\n\n";

        if (useComplexRadiobiologicalParams) {
            tempsource += "Complex Linear Quadratic Parameters" + "\n";
            TableModel m = jTable1.getModel();
            if (jList3.getSelectedIndex() == 0) {
                // Target Region: Cell
                tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                tempsource += "     \t(C<--C)\t(C<--C)\t(C<--CS)\t(C<--CS)\t(Ci<--Cj)\t(Ci<--Cj)\n";
            } else if (jList3.getSelectedIndex() == 1) {
                // Target Region: Nucleus
                tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                tempsource += "     \t(N<--N)\t(N<--N)\t(N<--Cy)\t(N<--Cy)\t(N<--CS)\t(N<--CS)\t(Ni<--Cj)\t(Ni<--Cj)\n";
            } else if (jList3.getSelectedIndex() == 2) {
                // Target Region: Cytoplasm
                tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\n";
                tempsource += "     \t(CS<--N)\t(CS<--N)\t(CS<--Cy)\t(CS<--Cy)\t(CS<--CS)\t(CS<--CS)\t(Cyi<--Cj)\t(Cyi<--Cj)\n";
            } else if (jList3.getSelectedIndex() == 3) {
                // Target Region: Nucleus & Cytoplasm
                tempsource += "ICODE\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-self\tbeta-self\talpha-cross\tbeta-cross\talpha-cross\tbeta-cross\n";
                tempsource += "     \t(N<--N)\t(N<--N)\t(N<--Cy)\t(N<--Cy)\t(N<--CS)\t(N<--CS)\t(CS<--N)\t(CS<--N)\t(CS<--Cy)\t(CS<--Cy)\t(CS<--CS)\t(CS<--CS)\t(Ni<--Cj)\t(Ni<--Cj)\t(Cyi<--Cj)\t(Cyi<--Cj)\n";
            }

            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (j == 1) {
                        continue;
                    }
                    tempsource += m.getValueAt(i, j).toString() + '\t';
                }
                tempsource += "\n";
            }
        } else {
            tempsource += "Simple Linear Quadratic Parameters"
            + "Alphaself (Gy^-1) =    " + jTextField32.getText() + "\n"
            + "Betaself (Gy^-2) =     " + jTextField8.getText() + "\n"
            + "Alphacross (Gy^-1) =   " + jTextField34.getText() + "\n"
            + "Betacross (Gy^-2) =    " + jTextField7.getText() + "\n";
        }

        if (jRadioButton12.isSelected()) {
            tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Average Energy Spectrum" + "\n" + tempsource;
        } else if (jRadioButton13.isSelected()) {
            tempsource = "Radionuclide =   " + iso + "\n" + "Spectrum type =   Beta Full Energy Spectrum" + "\n" + tempsource;
        } else if (jRadioButton1.isSelected()) {
            tempsource = "Monoenergetic particle emitter =   Alpha Particle" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n" + tempsource;
        } else if (jRadioButton2.isSelected()) {
            tempsource = "Monoenergetic particle emitter =   Electron" + "\n" + "Energy of monoenergetic particle (MeV) =    " + jTextField10.getText() + "\n" + "Yield of monoenergetic particle =    " + jTextField11.getText() + "\n" + tempsource;
        }
        jTextArea5.append(
            version.concat("\n")
            +  tempsource + "\n"
            + "Number of Cells =   " + cellnumber + "\n"
            + "Distance between centers of neighboring cells (um) =    " + jTextField36.getText() + "\n"
            + "Number of cells that are labeled =   " + jTextField44.getText() + "\n"
            + "Percentage of cells that are labeled (%)=   " + jTextField45.getText() + "\n"
            + "Distribution of radioactivity =   " + jComboBox8.getSelectedItem().toString() + "\n"
            + jLabel87.getText() + "=    " + jTextField48.getText() + "\n"
            + "Maximum mean activity per cell (Bq) =   " + jTextField46.getText() + "\n"
            + "Time integrated activity coefficient (hr) =   " + jTextField47.getText() + "\n" + "\n"
            + "MAC = mean activity per cell (Bq)" + "\n"
            + "MDC = mean absorbed dose to target region(s) of cells (Gy)" + "\n"
            + "MALC =mean activity per labeled cell (Bq)" + "\n"
            + "MDLC = mean absorbed dose to labeled cells (Gy)" + "\n"
            + "MDUC = mean absorbed dose to unlabeled cells (Gy)" + "\n" + "\n"
            
        );
        /*"Mean activity per cell (Bq) =   "+ formatter.format(activitytotal/(cellnumber*Tau)) +"\n"+
        "Mean absorbed dose to all cells (Gy) =   "+formatter.format( MeanABD) +"\n"+
        "Mean activity per labeled cell (Bq) =   "+ formatter.format(activitytotal/(labelcellnum *Tau))+"\n"+
        "Mean absorbed dose to labeled cells (Gy) =   "+ formatter.format(MeanABDL) +"\n");*/
        formatter = new DecimalFormat("0.00E00");
        //jTextArea5.append( "surviving fraction (%)  "+ formatter.format(LiveCell/cellnumber*100));

        if (useComplexRadiobiologicalParams) {
            jTextArea5.append(SurvCalcComplex.output);
        } else {
            jTextArea5.append("MAC(Bq)\tMDC(Gy)\tMALC(Bq)\tMDLC(Gy)\tMDUC(Gy)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n");
            for (int i = 0; i < SurvCalc.PlotOutput[0].length; i++) {
                jTextArea5.append(""
                    + formatter.format(SurvCalc.PlotOutput[0][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[1][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[2][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[3][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[4][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[5][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[6][i]) + " \t"
                    + formatter.format(SurvCalc.PlotOutput[7][i]) + " \n"
                );
            }
        }

        String yLabel = "";
        String xLabel = "";
        XYSeries series = new XYSeries("XYGraph");
        series.add(0, 1);
        double miny = 1.0 / (double) cellnumber;

        if (!jTextArea5.getText().isEmpty()) {
            try {
                jTextArea5.setCaretPosition(jTextArea5.getLineStartOffset(1));
            } catch (BadLocationException ex) {
                Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!jTextArea3.getText().isEmpty()) {
            try {
                jTextArea3.setCaretPosition(jTextArea3.getLineStartOffset(1));
            } catch (BadLocationException ex) {
                Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //generate SF curve
        SurvivalFraction survivalFraction = new SurvivalFraction();
        if(useComplexRadiobiologicalParams){
           survivalFraction.generateSurvivalCurve(jPanel40, jComboBox10, jComboBox9, miny, SurvCalcComplex.PlotOutput, Tau, cellnumber);
        } else{
            survivalFraction.generateSurvivalCurve(jPanel40, jComboBox10, jComboBox9, miny, SurvCalc.PlotOutput, Tau, cellnumber);
        }

        double[] v1 = new double[celllabel.length];
        for (int i = 0; i < celllabel.length; i++) {
            v1[i] = celllabel[i][5] / Tau;
        }

        HistogramDataset Histdataset = new HistogramDataset();
        int bin = 1000;
        Histdataset.addSeries("", v1, bin);

        JFreeChart charth = ChartFactory.createHistogram(
            "Initial Activity Labeled Per Cell (Bq)",
            "Activity (Bq)",
            "Number of Cells",
            Histdataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        charth.setBackgroundPaint(new Color(230, 230, 230));

        XYPlot xyplot = (XYPlot) charth.getPlot();

        NumberAxis RangeAxis1 = (NumberAxis) xyplot.getRangeAxis();
        RangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis DomainAxis1 = (NumberAxis) xyplot.getDomainAxis();
        DomainAxis1.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        DomainAxis1.setNumberFormatOverride(new DecimalFormat("0.00E00"));
        if (jComboBox8.getSelectedIndex() == 2) {
            DomainAxis1.setRange(v1[0] - .00000000001, v1[0] + .00000000001);
        }
        xyplot.setForegroundAlpha(0.7F);
        xyplot.setBackgroundPaint(Color.WHITE);
        xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
        xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
        XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
        xybarrenderer.setShadowVisible(false);
        xybarrenderer.setBarPainter(new StandardXYBarPainter());
        ChartPanel CPH = new ChartPanel(charth);
        jPanel48.removeAll();
        jPanel48.setLayout(new java.awt.BorderLayout());
        jPanel48.add(CPH, BorderLayout.CENTER);
        jPanel48.validate();

        //Update the output textarea
        jTextArea3.append( "Distance   S(C<--C)  S(C<--CS)   S(N<--N)   S(N<--Cy)   S(N<--CS)   S(Cy<--N)   S(Cy<--CS)   S(Cy<--Cy)\n" );
        jTextArea3.append( "   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s\n" );
        jTextArea3.append(jTextArea1.getText());

        // get the avg activity per radius
        double avgActivity[] = new double[2 * longestaxis + 1];
        double avgDose[] = new double[2 * longestaxis + 1];
        int numCellsAt[] = new int[2 * longestaxis + 1];
        //		for (int i = 0; i <= longestaxis; i++) {
            //			numCellsAt[i] = 0;
            //			avgActivity[i] = 0;
            //			avgDose[i] = 0;
            //		}
        int rToCell;
        for (int i = 0; i < cell.length; i++) {
            rToCell = (int) Math.rint(Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][2]), 2)));
            avgActivity[rToCell] += cell[i][5];    // sum the activity at each radius
            avgDose[rToCell] += cell[i][7] + cell[i][6];        // sum the dose at each radius
            numCellsAt[rToCell]++;                      // the number of cells at each radius
        }
        //		for(int i = 0; i < avgDose.length; i++){
            //			System.out.println("avgDose["+ i+ "] = " + avgDose[i]);
            //		}
        for (int i = 0; i < longestaxis + 1; i++) {
            if (numCellsAt[i] != 0) {
                avgActivity[i] /= numCellsAt[i] * Tau;            // get the average (the tau is to remove the time activity constant
                    avgDose[i] /= numCellsAt[i];
                }
            }

            // output the sctivity as a function of radius from center of mass. it should be noted that this is not necessarily distance from edge
            jTextArea5.append("Activity | Absorbed Dose as a function of radius (from the center of mass):\n");
            for (int i = 0; i < avgActivity.length; i++) {
                if (avgActivity[i] != 0) {
                    jTextArea5.append("" + i + "\t" + formatter.format(avgActivity[i]) + "    |    " + formatter.format(avgDose[i]) + "\n");
                }
            }

            // generate the data set
            String Ylab = "";
            XYSeries seriesR = new XYSeries("Radial Dataset");
            if (jComboBox3.getSelectedIndex() == 0) {
                for (int i = 0; i < longestaxis + 1; i++) {
                    if (avgActivity[i] != 0) {
                        seriesR.add(i, avgActivity[i]);
                    }
                }
                Ylab = "Mean Activity per cell (Bq)";
            } else {
                for (int i = 0; i < longestaxis + 1; i++) {
                    if (avgDose[i] != 0) {
                        seriesR.add(i, avgDose[i]);
                    }
                }
                Ylab = "Mean Absorbed Dose To Cells (Gy)";
            }
            seriesR.add(0.0, Double.MIN_NORMAL);
            XYSeriesCollection RadialSet = new XYSeriesCollection();
            RadialSet.addSeries(seriesR);

            // generate the chart
            JFreeChart radial = ChartFactory.createXYBarChart(
                "",                             // Title
                "r (um)",                       // x-axis Label
                false,                          // time label shown?
                Ylab,   // y-axis Label
                RadialSet,                      // Dataset
                PlotOrientation.VERTICAL,       // Plot Orientation
                false,                          // Show Legend?
                true,                           // Use tooltips?
                false                           // Configure chart to generate URLs?
            );
            radial.setBackgroundPaint(new Color(230, 230, 230));
            XYPlot xyplotR = (XYPlot) radial.getPlot();

            // asethetics stuff for the charts
            NumberAxis RangeAxisR = (NumberAxis) xyplotR.getRangeAxis();
            RangeAxisR.setStandardTickUnits(NumberAxis.createStandardTickUnits());
            NumberAxis DomainAxisR = (NumberAxis) xyplotR.getDomainAxis();
            xyplotR.setForegroundAlpha(0.7F);
            xyplotR.setBackgroundPaint(Color.WHITE);
            xyplotR.setDomainGridlinePaint(new Color(150, 150, 150));
            xyplotR.setRangeGridlinePaint(new Color(150, 150, 150));

            // Display the chart
            XYBarRenderer xybarrendererR = (XYBarRenderer) xyplotR.getRenderer();
            xybarrendererR.setShadowVisible(false);
            ChartPanel CPHR = new ChartPanel(radial);
//            jPanel52.removeAll();
//            jPanel52.setLayout(new java.awt.BorderLayout());
//            jPanel52.add(CPHR, BorderLayout.CENTER);
//            jPanel52.validate();

            Graphics progress = jPanel38.getGraphics();
            int Progwidth = jPanel38.getWidth();
            int ProgHeight = jPanel38.getHeight();
            progress.clearRect(0, 0, Progwidth, ProgHeight);
            progress.setColor(Color.GREEN);
            progress.fillRect(1, 1, Progwidth, ProgHeight);
            progress.setColor(Color.BLACK);
            progress.drawString("Done", Progwidth / 2 - 5, ProgHeight / 2 + 6);

            long TE = System.currentTimeMillis();
            System.out.println(TE - TS);
            System.out.println(TE - start);

            justRan = System.currentTimeMillis();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        EquationsImage e = new EquationsImage("/res/ComplexEquations.png");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBox1MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jComboBox1MouseClicked

    private void jRadioButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3MouseClicked

    private void jComboBox1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBox1FocusGained
        // TODO add your handling code here:
        jComboBox1.revalidate();
    }//GEN-LAST:event_jComboBox1FocusGained

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        //Compute button in Drug Planning Tab
        jTextArea5.setText("");
        jTextArea3.setText("");
        jTextArea1.setText("");
        long TS = System.currentTimeMillis();
        if (TS - justRan < 500) {
            // yes this is a stupid hacky fix but it works
            // basically is the program just ran make it so
            // that it cannot run again immediately
            return;
        }      
        //cal Self S-Values
        ArrayList<double[]> data;
        double rCell = Double.parseDouble(jTextField1.getText());
        double rNuc = Double.parseDouble(jTextField2.getText());
        java.util.List<double[][]> selfSList = new java.util.LinkedList<>(); //self s-value list

        for(int i=0; i<numDrugs; i++){
            jTextArea1.setText("");
            if (drugPanels[i].jComboBox1.getSelectedIndex() == 1) {
                // mono energetic aplha particles and electrons respectively

                System.out.println("new electron/alpha selected!");
                Double en = drugPanels[i].monoPanel.en;
                Double xn = drugPanels[i].monoPanel.yield; 

                data = new ArrayList<double[]>();

                if (drugPanels[i].monoPanel.RB_e.isSelected()) {
                    // is electron
                    System.out.println("adding to data: e-");
                    data.add(new double[]{5.0, xn, en});
                } else if (drugPanels[i].monoPanel.RB_alpha.isSelected()) {
                    // is alpha
                    System.out.println("adding to data: alpha");
                    data.add(new double[]{8.0, xn, en});
                }

                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
            } else if (drugPanels[i].jComboBox1.getSelectedIndex() == 2) {
                //for user created

                data = new ArrayList<double[]>();
                Scanner s = new Scanner(drugPanels[i].jTextArea1.getText());
                String input = "";
                String[] split;
                double datums[] = new double[4];
                while (!(input = s.nextLine()).equals("START RADIATION RECORDS")) {
                    // do nothng
                }
                while (!(input = s.nextLine()).equals("END RADIATION RECORDS")) {
                    split = input.trim().split("\\s+");
                    for (int j = 0; j < 3; j++) {
                        datums[j] = Double.parseDouble(split[j]);
                    }
                    data.add(new double[]{Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])});
                }

                SelfDose_2 selfDose_2 = new SelfDose_2();
                selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);

            } else if (drugPanels[i].jComboBox1.getSelectedIndex() == 0) {
                // for new data from MIRD CD
                // beta average

                if (drugPanels[i].predefRadPanel1.RB_BetaAvg.isSelected()) {
                    iso = String.valueOf(drugPanels[i].predefRadPanel1.radList.getSelectedValue());
                    System.out.println("selected MIRD iso file name: " + iso);

                    data = ArrayListIn3.readMIRDdata(iso, true);
                    SelfDose_2 selfDose_2 = new SelfDose_2();
                    selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                } else if (drugPanels[i].predefRadPanel1.RB_BetaFull.isSelected()){
                    // Pre-defined radionuclide
                    // beta full
                    iso = String.valueOf(drugPanels[i].predefRadPanel1.radList.getSelectedValue()).toLowerCase(Locale.ENGLISH);
                    System.out.println("selected iso file name: " + iso);

                    data = ArrayListIn3.readOTHERdata(iso);
                    SelfDose_2 selfDose_2 = new SelfDose_2();
                    selfSVals = selfDose_2.calcSelfDose(rCell, rNuc, data, jTextArea1);
                }                   
            }
            selfSList.add(i, selfSVals);
            jTextArea3.append("Drug" + (i+1) +"\n" );
            jTextArea3.append(jTextArea1.getText());
        }
        //cal activity and dose
        if(drugList == null){
            JOptionPane.showMessageDialog(null,"Error: Please Upload Data.", "Error Message",JOptionPane.ERROR_MESSAGE);
            return;
        }       
               
        jLabel22.setText("   um          Gy/Bq-s       Gy/Bq-s        Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s       Gy/Bq-s");
        jLabel26.setText("Distance   S(C<--C)   S(C<--CS)    S(N<--N)    S(N<--Cy)    S(N<--CS)    S(Cy<--N)    S(Cy<--CS)    S(Cy<--Cy)");
        
        if (drugList != null){
            int cellnumber = drugList.get(0).size();
            if (cellnumber > 0){
                if(inputCheck.getNum(tf_tgtSF) * cellnumber < 1) {
                    JOptionPane.showMessageDialog(null,
                    "Error: Please enter a SF bigger than " + 1.0/cellnumber, "Error Message",
                    JOptionPane.ERROR_MESSAGE);
                    tf_tgtSF.setText(String.valueOf(1.0/cellnumber));
                    return;
                }     
            }
        }          
        int sel = cb_SF.getSelectedIndex();
        switch (sel){
            case 0:
                tgtSFCompute(selfSList, 2);
                break;
            case 1:
                customSFCompute(selfSList);
                break;
            case 2:
                tgtSFCompute (selfSList, 1);
                break;
        }
        
        justRan = System.currentTimeMillis();
    }//GEN-LAST:event_jButton27ActionPerformed
    
    public void customSFCompute (java.util.List selfSList){
        sumsubsequence = 0; 
        java.util.List<Double> LList = new java.util.LinkedList<>(); //L list
        for(int i=0; i<numDrugs; i++){
            LList.add(i, inputCheck.getNum(drugPanels[i].TF_L));
        }        
        int cellnumber = drugList.get(0).size(); //size of the first RFU
        java.util.List<java.util.List<Object>> drugComboList = new LinkedList<>();       
        NumberFormat f = new DecimalFormat("0.000E00");
        StringBuilder outputSummary = new StringBuilder();
        StringBuilder outputDetail = new StringBuilder();
        drugIndexNameMap  = new HashMap<Integer, String>();
        Graphics progress = jPanel62.getGraphics();
        int Progwidth = jPanel62.getWidth();
        int ProgHeight = jPanel62.getHeight();
        //sb.append("Drug     Cell    Activity    Dose    Survival Prob.  Alive?  Administered Amount\n");
        LinkedList<String> drugs = new LinkedList<>();
        java.util.List<Integer> cells  = new LinkedList<>();
        java.util.List<Integer> usedDrugIndices  = new LinkedList<>();
        java.util.List<Double> OptimizedSpecificActs = new LinkedList<>();
        int[] sortedIndices = new int[cellnumber];
        for(int i=0; i<numDrugs; i++){
            drugs.add(drugPanels[i].lb_drugName.getText());
            drugIndexNameMap.put(i, drugPanels[i].lb_drugName.getText());
        }
        drugNameList = new LinkedList<String>();
        drugNameList.addAll(drugs);
        
        for(int j=0; j<cellnumber; j++){
            cells.add(j);
            sortedIndices[j] = j;
        }
        int[] indices = Arrays.copyOf(sortedIndices, cellnumber);
        final AtomicInteger cnt = new AtomicInteger(0);
        double sp = 1;
        //Optimize on specific activity
        progress.clearRect(0, 0, Progwidth, ProgHeight);
        progress.setColor(Color.BLACK);
        progress.drawString("Processing Data. Please wait...", Progwidth / 2 - 5, ProgHeight / 2 + 6);
        while(!drugs.isEmpty()){
            java.util.List<Double> subList;
            int cutoff = 0;
            String drug = getNextDrug(drugs, cells, LList, sortedIndices);
            int drugIndex = drugNameList.indexOf(drug);
            usedDrugIndices.add(drugIndex);
            double sfCutoff = inputCheck.getNum(drugPanels[drugIndex].TF_sfCutOff);
            if(sfCutoff >= 1.0) {
                drugs.remove(drug);
                continue;
            } else if (sfCutoff <= 0.0) {
                JOptionPane.showMessageDialog(null,"Please Enter SF Between 0 - 1.", "Error Message",JOptionPane.ERROR_MESSAGE);
                return;
            }
            //get binding sites, multiply the list with L
            if(usedDrugIndices.size()>1){
                subList = Arrays.stream(sortedIndices).parallel().mapToDouble(e -> drugList.get(drugIndex).get(e) * LList.get(drugIndex)/1e5 ).boxed().collect(Collectors.toList());
//                for(int i=0; i<sortedIndices.length; i++){
//                    subList.add(drugList.get(drug).get(sortedIndices[i]) * LList.get(drug)/1e5);                                      
//                }
                
            } else{
                subList = drugList.get(drugIndex).parallelStream().map(e -> e * LList.get(drugIndex)/1e5).collect(Collectors.toList());
            }
            final java.util.List<Double> bs = subList;
            //sort the value in subList and map it to the index
            //add peek(ele -> {cnt.incrementAndGet(); update the progress bar}) after mapToInt
            sortedIndices = IntStream.range(0, subList.size())
                    .boxed().parallel().sorted((i, j) -> bs.get(i).compareTo(bs.get(j)))
                    .mapToInt(ele -> ele).toArray();
            cutoff = (int)(sfCutoff * bs.size());
            int index = sortedIndices[cutoff];
            //search for optimal specific acti. 
            double s = 0; 

            if(usedDrugIndices.size()>1){
                //get the surv. prob. using doses from sum of previous drugs
                int prevDrugIndex = usedDrugIndices.get(usedDrugIndices.size()-2);
                sp *= getSurvProb(prevDrugIndex, index, OptimizedSpecificActs.get(OptimizedSpecificActs.size()-1), selfSList);
                s = binarySearch(1e10, sfCutoff, drugIndex, index, sp, selfSList); //set max search value as 1e10, change as needed
            } else {
                s = binarySearch(1e10, sfCutoff, drugIndex, index, 1.0, selfSList);
            }
                    
            if(s>0) OptimizedSpecificActs.add(s); 
            else {
                JOptionPane.showMessageDialog(null, "Specific Activity for " + drug + " exceeds range, please adjust input parameters!", "alert", JOptionPane.ERROR_MESSAGE);
                progress.clearRect(0, 0, Progwidth, ProgHeight);
                return;
            } 
            
            drugPanels[drugIndex].TF_SA.setText(f.format(s));
            if(cutoff == 0) break;
            sortedIndices = Arrays.copyOfRange(sortedIndices, 0, cutoff);
            
            drugs.remove(drug);
        }
        outputSummary.append("Drugs Used\t\tOptimal Specific Acti.(GBq/mol)\n");
        outputSummary.append(usedDrugIndices.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList()).toString());
        outputSummary.append("\t\t");
        outputSummary.append(OptimizedSpecificActs.stream().map(i -> f.format(i)).collect(Collectors.toList()).toString());
        outputDetail.append("\nCell\tSurvival Prob after: ");
        usedDrugIndices.forEach(i -> {
            outputDetail.append(drugIndexNameMap.get(i)).append("\t");
            });
        outputDetail.append("Alive?\n");
        
        // cal. SF using the optimal SA
        
        double[] svp = new double[cellnumber];
        Arrays.fill(svp, 1.0);
        boolean[] alive= new boolean[cellnumber]; //cell alive or not
        double[] allLive = {0};
//        IntStream.range(0, cellnumber).parallel()
//                .forEach(i -> {
//                    for(int j=0; j<usedDrugs.size(); j++){
//                        svp[i] *= getSurvProb(usedDrugs.get(j), i, OptimizedSpecificActs.get(j), selfSList);
//                    }
//                    if (randomgen.nextDouble() < svp[i]) {
//                        allLive[0]++;
//                        //alive[i] = true;    
//                    }            
//                });
        double rand, percent;
        for(int i=0; i<cellnumber; i++){
            outputDetail.append(i).append("\t\t");
            for(int j=0; j<usedDrugIndices.size(); j++){
                svp[i] *= getSurvProb(usedDrugIndices.get(j), i, OptimizedSpecificActs.get(j), selfSList);
                outputDetail.append(f.format(svp[i])).append("\t");
            }

            rand = randomgen.nextDouble();
            if (rand < svp[i]) {
                allLive[0]++;
                alive[i] = true;    
            }            
            if(alive[i]) outputDetail.append("yes\n");
            else outputDetail.append("no\n");
            //set the progress bar
            percent = (double) i / (double) cellnumber * (double) Progwidth;
            progress.clearRect( 0, 0, Progwidth, ProgHeight );
            progress.setColor( Color.GREEN );
            progress.fillRect( 1, 1, (int) percent, 28 );
            progress.setColor(Color.BLACK);
            progress.drawString("Calculating : " + String.format("%1$.2f", percent / Progwidth * 100) + "%", Progwidth / 2 - 50, ProgHeight / 2 + 6);
        }
        Double[] sfPerCombo = new Double[1];
        sfPerCombo[0] = allLive[0]/cellnumber;
        drugComboList.add(usedDrugIndices.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList()));       
        //display result chart
        DrugPlot plot = new DrugPlot();
        plot.generateDrugSurvival(jPanel66, sfPerCombo, drugComboList);
        
        //create comparison chart
        progress.clearRect(0, 0, Progwidth, ProgHeight);
        progress.setColor(Color.GREEN);
        progress.fillRect(1, 1, Progwidth, ProgHeight);
        progress.setColor(Color.BLACK);
        progress.drawString("Calculating Equiactivity. Please wait...", Progwidth / 2 - 5, ProgHeight / 2 + 6);
        
        int[] drugIndices = IntStream.range(0, numDrugs).map(i -> i+1).toArray();
        final java.util.List<java.util.List<Integer>> drugComboList4Comparison = getSubsequences(drugIndices);
        //set SF cutoff based on the combo size
        double overallSF = 1; 
        overallSF = IntStream.range(0, numDrugs).mapToDouble(i -> inputCheck.getNum(drugPanels[i].TF_sfCutOff)).reduce(1,(a, b) -> a*b);
        final double SF = overallSF;
        LinkedHashMap<java.util.List<String>, java.util.List<Double>> actMap = new LinkedHashMap<>();
        //parallelism computation
//        IntStream.range(0, drugComboList4Comparison.size()).parallel()
//                .forEach(i -> actMap.put(drugComboList4Comparison.get(i), getComboActivity(drugComboList4Comparison.get(i), SF, cells, LList, indices, selfSList )));
        for(int i=0; i<drugComboList4Comparison.size()-1; i++){
            java.util.List dc = drugComboList4Comparison.get(i).stream().map(e -> e-1).collect(Collectors.toList());            
            java.util.List dcAct = getComboActivity(dc, SF, cells, LList, indices, selfSList );
            java.util.List l = (java.util.List) dc.stream().map(e -> drugIndexNameMap.get(e)).collect(Collectors.toList());
            actMap.put(l, dcAct);
            outputSummary.append("\n").append(l.toString()).append("\t\t").append(dcAct.stream().map(e -> f.format(e)).collect(Collectors.toList()).toString());
        }
        //add the all drugs combo to the map
        java.util.List allCombo = usedDrugIndices.stream().map(e -> drugIndexNameMap.get(e)).collect(Collectors.toList());
        java.util.List allActi =  usedDrugIndices.stream().map(i -> inputCheck.getNum(drugPanels[i].TF_Amount) * inputCheck.getNum(drugPanels[i].TF_SA)).collect(Collectors.toList());
        actMap.put(allCombo, allActi);
        jTextArea5.setText(outputSummary.toString());
        jTextArea5.setCaretPosition(0);

        final int STACKEDBAR = 1, BAR = 0;
        plot.generateActivityChart(jPanel67, actMap, STACKEDBAR);
        
        Toolkit.getDefaultToolkit().beep();
        progress.clearRect(0, 0, Progwidth, ProgHeight);
        progress.setColor(Color.GREEN);
        progress.fillRect(1, 1, Progwidth, ProgHeight);
        progress.setColor(Color.BLACK);
        progress.drawString("Done", Progwidth / 2 - 5, ProgHeight / 2 + 6);
    }
    public void tgtSFCompute (java.util.List selfSList, int algo){
        sumsubsequence = 0; 
        java.util.List<Double> LList = new java.util.LinkedList<>(); //L list
        for(int i=0; i<numDrugs; i++){
            LList.add(i, inputCheck.getNum(drugPanels[i].TF_L));
        }        
        int cellnumber = drugList.get(0).size(); //size of the first RFU     
        NumberFormat f = new DecimalFormat("0.000E00");
        
        Graphics progress = jPanel62.getGraphics();

        StringBuilder outputSummary = new StringBuilder();
        drugIndexNameMap  = new HashMap<Integer, String>();
        LinkedList<String> drugs = new LinkedList<>();
        int[] sortedIndices = new int[cellnumber];
        for(int i=0; i<numDrugs; i++){
            drugs.add(drugPanels[i].lb_drugName.getText());
            drugIndexNameMap.put(i, drugPanels[i].lb_drugName.getText());
            drugPanels[i].TF_sfCutOff.setText("0");
            drugPanels[i].TF_SA.setText("0");
        }
        drugNameList = new LinkedList<String>();
        drugNameList.addAll(drugs);
        
        for(int j=0; j<cellnumber; j++){
            sortedIndices[j] = j;
        }
        int[] indices = Arrays.copyOf(sortedIndices, cellnumber);
        double presetSF = inputCheck.getNum(tf_tgtSF);
       
        int Progwidth = jPanel62.getWidth();
        int ProgHeight = jPanel62.getHeight();       
        int[] d = IntStream.range(0, numDrugs).map(i -> i+1).toArray(); //set the right sequence for 1 drug set
        final java.util.List<java.util.List<Integer>> drugComboList = getSubsequences(d);
        java.util.List drugComboNameList = new LinkedList<>();
        double[] svp = new double[cellnumber];
        LinkedList<Double> sfList = new LinkedList<>();
        LinkedHashMap<java.util.List<String>, java.util.List<Double>> actMap = new LinkedHashMap<>();
        double MAXSA = 1e11;
        AtomicInteger pc = new AtomicInteger(0);

        for(int i=0; i<drugComboList.size(); i++){ 
            java.util.List<Integer> dc = drugComboList.get(i).stream().map(e -> e-1).collect(Collectors.toList()); 
            java.util.List<Double> dcAct = getComboActivity3(dc, presetSF, LList, cellnumber, selfSList);
            java.util.List<String> l = (java.util.List) dc.stream().map(e -> drugIndexNameMap.get(e)).collect(Collectors.toList());
            actMap.put(l, dcAct);
            drugComboNameList.add(l);
            outputSummary.append("\n").append(l.toString()).append("\t\t").append(dcAct.stream().map(e -> f.format(e)).collect(Collectors.toList()).toString());
            //cal SF for drug combo
            Arrays.fill(svp, 1.0);
            AtomicInteger cnt = new AtomicInteger(0); 
            IntStream.range(0, cellnumber).parallel().forEach(k -> {
                pc.incrementAndGet();
                for(int j=0; j<dc.size(); j++){
                    svp[k] *= getSurvProb(dc.get(j), k, dcAct.get(j)/inputCheck.getNum(drugPanels[dc.get(j)].TF_Amount), selfSList);
                }

                double rand = randomgen.nextDouble();
                if (rand < svp[k]) {
                    cnt.incrementAndGet();
                }            
                 //set the progress bar
                double percent = pc.doubleValue() / (cellnumber * drugComboList.size()) *  Progwidth;
                progress.clearRect( 0, 0, Progwidth, ProgHeight );
                progress.setColor( Color.GREEN );
                progress.fillRect( 1, 1, (int)percent, 28 );
                progress.setColor(Color.BLACK);
                progress.drawString("Calculating : " + String.format("%1$.2f", percent / Progwidth * 100) + "%", Progwidth / 2 - 50, ProgHeight / 2 + 6);
            });
            sfList.add(cnt.doubleValue()/cellnumber);
            
        }

//        for(int i=0; i<drugComboList.size(); i++){ 
//            java.util.List<Integer> dc = drugComboList.get(i).stream().map(e -> e-1).collect(Collectors.toList()); 
//            java.util.List<Integer> dc2 = new LinkedList<Integer>(dc);
//            java.util.List<Double> dcAct = (algo == 2 ? getComboActivity2(dc, presetSF, LList, indices, selfSList ): getComboActivity1(dc, presetSF, LList, indices, selfSList));
//            java.util.List<String> l = (java.util.List) dc.stream().map(e -> drugIndexNameMap.get(e)).collect(Collectors.toList());
//            actMap.put(l, dcAct);
//            drugComboNameList.add(l);
//            outputSummary.append("\n").append(l.toString()).append("\t\t").append(dcAct.stream().map(e -> f.format(e)).collect(Collectors.toList()).toString());
//            //cal SF for drug combo
//            Arrays.fill(svp, 1.0);
//            double rand, percent, cnt = 0; 
//            for(int k=0; k<cellnumber; k++){
//                pc++;
//                for(int j=0; j<dc.size(); j++){
//                    svp[k] *= getSurvProb(dc.get(j), k, dcAct.get(j)/inputCheck.getNum(drugPanels[dc.get(j)].TF_Amount), selfSList);
//                }
//
//                rand = randomgen.nextDouble();
//                if (rand < svp[k]) {
//                    cnt++; 
//                }            
//                 //set the progress bar
//                percent = pc / (cellnumber * drugComboList.size()) *  Progwidth;
//                progress.clearRect( 0, 0, Progwidth, ProgHeight );
//                progress.setColor( Color.GREEN );
//                progress.fillRect( 1, 1, (int) percent, 28 );
//                progress.setColor(Color.BLACK);
//                progress.drawString("Calculating : " + String.format("%1$.2f", percent / Progwidth * 100) + "%", Progwidth / 2 - 50, ProgHeight / 2 + 6);
//            }
//            sfList.add(cnt/cellnumber);
//            //add reverse order combo for comparison  
//            if (dc.size() == 1 ) continue;
//            if (dc.size() == numDrugs ) {
//                //write the specific activity to the GUI
//                IntStream.range(0, numDrugs).forEach(x -> drugPanels[dc.get(x)].TF_SA.setText(f.format(dcAct.get(x)/inputCheck.getNum(drugPanels[dc.get(x)].TF_Amount))));
//                continue;
//            }
//            java.util.List<Double> dcAct2 = (algo == 2 ? getComboActivity2(dc, presetSF, false, LList, indices, selfSList ): getComboActivity1(dc, presetSF, false, LList, indices, selfSList));
//            java.util.List l2 = (java.util.List) dc2.stream().map(e -> drugIndexNameMap.get(e)).collect(Collectors.toList());
//            actMap.put(l2, dcAct2);
//            //cal SF for reverse drug combo
//            cnt = 0; 
//            Arrays.fill(svp, 1.0);
//            for(int k=0; k<cellnumber; k++){
//                for(int j=0; j<dc2.size(); j++){
//                    svp[k] *= getSurvProb(dc2.get(j), k, dcAct2.get(j)/inputCheck.getNum(drugPanels[dc2.get(j)].TF_Amount), selfSList);
//                }
//
//                rand = randomgen.nextDouble();
//                if (rand < svp[k]) {
//                    cnt++;   
//                }                        
//            }
//            sfList.add(cnt/cellnumber);
//            drugComboNameList.add(l2);
//        }
        jTextArea5.setText("Drug Combo\t\tOptimal Specific Activity");
        jTextArea5.setText(outputSummary.toString());
        jTextArea5.setCaretPosition(0);
        //generate SF bar chart
        DrugPlot plot = new DrugPlot();
        plot.generateDrugSurvival(jPanel66, sfList, drugComboNameList);
        

        //generate equi activity charts
        final int STACKEDBAR = 1, BAR = 0;
        plot.generateActivityChart(jPanel67, actMap, STACKEDBAR);
        
        Toolkit.getDefaultToolkit().beep();
        progress.clearRect(0, 0, Progwidth, ProgHeight);
        progress.setColor(Color.GREEN);
        progress.fillRect(1, 1, Progwidth, ProgHeight);
        progress.setColor(Color.BLACK);
        progress.drawString("Done", Progwidth / 2 - 5, ProgHeight / 2 + 6);
    }

    /**
     * cal. the avg. LFU based on a window 
     * window = cellIndex +- numCell cells, total 5 cells in this case
     * @param sortedIndices sorted cell indices based on mean binding site
     * @param cutoff cutoff pt in the sortedIndices  
     * @param drugIndex 
     * @return avg RFU for that drug
     */
     public double getAvgRFU(int[] sortedIndices, int cutoff, int drugIndex){
         final int numCell = 2; 
         if (sortedIndices.length < 2 * numCell +1){
             return IntStream.range(0, sortedIndices.length).mapToDouble(i -> drugList.get(drugIndex).get(i)).average().getAsDouble();
         }
         double avg = 0;
         int lastIndex = sortedIndices.length - 1;
         if (cutoff + numCell > lastIndex) {
             int offset = cutoff + numCell - lastIndex;
             avg = IntStream.range(cutoff - numCell - offset, sortedIndices.length ).mapToDouble(i -> drugList.get(drugIndex).get(i)).average().getAsDouble();
         } else if (cutoff - numCell < 0){
             int offset = numCell - cutoff;
             avg = IntStream.range(0, cutoff + numCell + offset + 1).mapToDouble(i -> drugList.get(drugIndex).get(i)).average().getAsDouble();
         } else {
             avg = IntStream.range(cutoff - numCell, cutoff + numCell +1).mapToDouble(i -> drugList.get(drugIndex).get(i)).average().getAsDouble();
         }
         return avg;
     }
    /**
     * find knee pt given a drug
     * @param res keeping resulting (KneePair, DrugIndex)
     * @param max max number to search
     * @param drug drug index
     * @param sortedIndices sorted cell indices based on mean binding site
     * @param survProb initial surviving prob.
     * @param selfSList Self S values
     * @return knee point(SA, SF) for the drug
     */
    public void findDrugKnee(SortedMap res,double max, int drug, int[] sortedIndices, double survProb, java.util.List<double[][]> selfSList){
        final double[] y = {1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.09, 0.08, 0.07, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01, 0.0075, 0.005, 0.004, 0.003, 0.002, 0.001};
        double[] x = new double[y.length];
        IntStream.range(0, y.length).forEach(i -> {
            int c = (y[i] * sortedIndices.length < 1 ? sortedIndices.length: (int)(y[i] * sortedIndices.length));
            int ind = sortedIndices[c-1];
            x[i] = binarySearch(max, y[i]*survProb, drug, ind, survProb, selfSList);
        });
        int kneeIndex = Knee.findKnee(x, y);         
        res.put(new Knee.pair(x[kneeIndex],y[kneeIndex]), drug);
    }
    /*  Java code to generate all possible subsequences. 
    Time Complexity O(n * 2^n) */
 
    java.util.List getSubsequences(int[] arr) 
    { 
        int n = arr.length;
        /* Number of subsequences is (2**n -1)*/
        int opsize = (int)Math.pow(2, n);
        
        java.util.List<java.util.List<Integer>> drugComboList = new LinkedList<>();
        HashMap<java.util.List, Integer> map = new HashMap<>();
        /* Run from counter 000..1 to 111..1*/
        for (int counter = 1; counter < opsize; counter++) 
        {
            java.util.List<Integer> drugCombo = new LinkedList<>();
            for (int j = 0; j < n; j++) 
            { 
                /* Check if jth bit in the counter is set 
                    If set then print jth element from arr[] */

                if (BigInteger.valueOf(counter).testBit(j)){
                    //System.out.print(arr[j]+" ");
                    sumsubsequence++;
                    drugCombo.add(arr[j]);                    
                }               
            }
            map.put(drugCombo, drugCombo.size());

            //System.out.println(); 
        }
        map = sortByValue(map);
        for(java.util.List<Integer> combo: map.keySet()){
            drugComboList.add(combo);
        }
        //return a list not including all drugs
        return drugComboList.subList(0, opsize-1);
    }
    
    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> map) {
        java.util.List<java.util.Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(java.util.Map.Entry.comparingByValue());

        HashMap<K, V> result = new LinkedHashMap<>();
        for (java.util.Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }     
    /**
     * find the drug with max mean binding sites
     * @param drugs drug indices
     * @param cells cell number list
     * @param LList L value list
     * @param sortedIndices cell indices sorted based on # of drug binding sites
     * @return next drug index
     */
    public String getNextDrug(java.util.List drugs, java.util.List<Integer> cells, java.util.List<Double> LList, int[] sortedIndices){
        if(drugs.size() == 1){
            return (String)drugs.get(0);
        }
        double maxBindingSites = 0;
        int cell; 
        String drug;
        TreeMap<Double, String> treemap = new TreeMap<>();
        for(int i=0; i<drugs.size(); i++){
            drug = (String)drugs.get(i);
            int drugIndex = drugNameList.indexOf(drug);
            for(int j=0; j<sortedIndices.length; j++){
                cell = cells.get(sortedIndices[j]);
                maxBindingSites += drugList.get(drugIndex).get(cell) * LList.get(i)/1e5;
            }
            treemap.put(maxBindingSites, drug);
            maxBindingSites = 0;
        }
        return treemap.lastEntry().getValue();
    }
    

     /**
     * search for specific activity that yield surv. prob = tgt. 
     * @param max max Specific Activity to search
     * @param tgt relative target Survival Prob - SP to achieve.
     * @param drug drug index
     * @param index cell index
     * @param survProb initial surviving prob.
     * @param selfSList Self S values
     * @return specific activity to reach the threshold SF
     */
    public double binarySearch(double max, double tgt, int drug, int index, double survProb, java.util.List<double[][]> selfSList) 
    { 
        if (tgt == 1) return 0;
        double l = 0, r = max, temp = 0; 
        int radTgt = drugPanels[drug].jComboBox2.getSelectedIndex();
        Double[] af = drugPanels[drug].aFractions;
        double L = drugPanels[drug].L;
        double Tau = drugPanels[drug].tiac * 3600.0 * drugPanels[drug].amt;
        
        while (l <= r) { 
            if(l < 0 ||r < 0) return temp;
            double m = l + (r - l) / 2; 
            temp = m;
            double dose = 0;
            double surv = survProb;
            //cal. surv prob.

            double acti = drugList.get(drug).get(index) * L * m * Math.pow(10, 9) / (6.02 * Math.pow(10, 23));
            switch (radTgt){
                    case 0: //radiation target: Cell.   
                            for (int ICODE = 0; ICODE < 11; ICODE++) {                                
                                /* self, C<-C */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][0] * af[1] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);  
                                }
                                /* self, C<-CS */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][1] * af[2] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                                }
                            }                                      
                        break;
                    case 1: //radiation target: Nucleus.
                            for (int ICODE = 0; ICODE < 11; ICODE++) {
                                /* self, N<-N */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][2] * af[0] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                                }
                                /* self, N<-Cy */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][3] * af[1] * acti;                
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);                   
                                }
                                /* self, N<-CS */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][4] * af[2] * acti;          
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);
                                }
                            }                           
                        break;
                    case 2: //radiation target: Cytoplasm.
                            for (int ICODE = 0; ICODE < 11; ICODE++) {
                                /* self, Cy<-N */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][5] * af[0] * acti;                                   
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                                }
                                /* self, Cy<-Cy */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][7] * af[1] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                                }
                                /* self, Cy<-CS */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][6] * af[2] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);        
                                }
                            }                                       
                        break;
                    case 3: //radiation target: Nucleus & Cytoplasm.
                            for (int ICODE = 0; ICODE < 11; ICODE++) {
                                /* self, Cy<-N */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][2] * af[0] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                                }
                                /* self, Cy<-Cy */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][3] * af[1] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                                }
                                /* self, Cy<-CS */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][4] * af[2] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);        
                                }
                                /* self, Cy<-N */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][6] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][7] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][5] * af[0] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][6] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][7] * dose * dose);
                                }
                                /* self, Cy<-Cy */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][8] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][9] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][7] * af[1] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][8] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][9] * dose * dose);        
                                }
                                /* self, Cy<-CS */
                                if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][10] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][11] != 0) {
                                    dose += Tau * selfSList.get(drug)[ICODE + 1][6] * af[2] * acti;
                                    surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][10] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][11] * dose * dose);                                         
                                }
                        }                                       
                        break;
                    case 4: //radiation target: Cell Surface.
                        break;
                }
            
            // Check if surv is close to tgt * starting SP
            if (surv >= tgt * survProb * 0.9 && surv <= tgt * survProb * 1.1){
                //System.out.println("" + dose);
                return m; 
            }                
  
            // If surv greater, ignore left half 
            if (surv > tgt * survProb * 1.1) 
                l = m + 1; 
  
            // If surv smaller, ignore right half 
            else
                r = m - 1; 
        } 
  
        // if we reach here, element not present 
        return 0; 
    } 
    
       /**
     *
     * @param drug drug index
     * @param index Cell#
     * @param sa specific activity
     * @param selfSList Self S values
     * @return
     */
    public double getSurvProb(int drug, int index, double sa, java.util.List<double[][]> selfSList){
        double surv = 1.0;
        int radTgt = drugPanels[drug].jComboBox2.getSelectedIndex();
        Double[] af = drugPanels[drug].aFractions;
        double L = inputCheck.getNum(drugPanels[drug].TF_L);
        double Tau = inputCheck.getNum(drugPanels[drug].TF_TimeIntegratedAct) * 3600.0 * inputCheck.getNum(drugPanels[drug].TF_Amount);
        double acti = drugList.get(drug).get(index) * L * sa * Math.pow(10, 9) / (6.02 * Math.pow(10, 23));
        double dose = 0;
        switch (radTgt){
                case 0: //radiation target: Cell.   
                        for (int ICODE = 0; ICODE < 11; ICODE++) {
                            /* self, C<-C */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][0] * af[1] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);  
                            }
                            /* self, C<-CS */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][1] * af[2] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                            }
                        }                                      
                    break;
                case 1: //radiation target: Nucleus.
                        for (int ICODE = 0; ICODE < 11; ICODE++) {
                            /* self, N<-N */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][2] * af[0] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                            }
                            /* self, N<-Cy */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][3] * af[1] * acti;                
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);                   
                            }
                            /* self, N<-CS */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][4] * af[2] * acti;          
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);
                            }
                        }                           
                    break;
                case 2: //radiation target: Cytoplasm.
                        for (int ICODE = 0; ICODE < 11; ICODE++) {
                            /* self, Cy<-N */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][5] * af[0] * acti;                                   
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                            }
                            /* self, Cy<-Cy */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][7] * af[1] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                            }
                            /* self, Cy<-CS */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][6] * af[2] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);        
                            }
                        }                                       
                    break;
                case 3: //radiation target: Nucleus & Cytoplasm.
                        for (int ICODE = 0; ICODE < 11; ICODE++) {
                            /* self, Cy<-N */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][2] * af[0] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][0] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][1] * dose * dose);
                            }
                            /* self, Cy<-Cy */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][3] * af[1] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][2] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][3] * dose * dose);        
                            }
                            /* self, Cy<-CS */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][4] * af[2] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][4] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][5] * dose * dose);        
                            }
                            /* self, Cy<-N */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][6] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][7] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][5] * af[0] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][6] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][7] * dose * dose);
                            }
                            /* self, Cy<-Cy */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][8] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][9] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][7] * af[1] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][8] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][9] * dose * dose);        
                            }
                            /* self, Cy<-CS */
                            if (drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][10] != 0 || drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][11] != 0) {
                                dose += Tau * selfSList.get(drug)[ICODE + 1][6] * af[2] * acti;
                                surv *= Math.exp(-1D * drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][10] * dose - drugPanels[drug].radbio.complexRadiobiologicalParams[ICODE][11] * dose * dose);                                         
                            }
                    }                                       
                    break;
                case 4: //radiation target: Cell Surface.
                    break;
                
        }
        return surv;
    }; 
    
    /**
     * get Equi Activity for custom SF.
     * @param cocktail Drug Combo
     * @param sfProduct overall Survival prob. cutoff
     * @param cells cell# list
     * @param LList L value List
     * @param indices array for storing sorted indices
     * @param selfSList Self S values
     * @return
     */
    public java.util.List getComboActivity(java.util.List<Integer> cocktail, double sfProduct, java.util.List<Integer> cells, java.util.List<Double> LList, int[] indices, java.util.List selfSList){
        java.util.List<Integer> usedDrugs  = new LinkedList<>();
        java.util.List<Double> OptimizedSpecificActs = new LinkedList<>();
        java.util.List<Double> ActiList = new LinkedList<>();
//        java.util.List<Integer> drugCombo = new java.util.LinkedList<>();
//        drugCombo.addAll(cocktail);
        int[] sortedIndices = Arrays.copyOf(indices, indices.length);
        double denominator = cocktail.stream().mapToDouble(x -> inputCheck.getNum(drugPanels[x].TF_sfCutOff)).reduce(1, (a, b) -> a * b);
        String drug;
        double sp = 1;
        java.util.List drugs = cocktail.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList());
        cocktail.clear();
        while(!drugs.isEmpty()){
            java.util.List<Double> subList;
            int cutoff = 0;
            int drugIndex;
            if(drugs.size()==1){
                drug =(String) drugs.get(0);
            } else {
                drug = getNextDrug(drugs, cells, LList, sortedIndices);
            }
            drugIndex = drugNameList.indexOf(drug);
            cocktail.add(drugIndex);
            double sfCutoff = inputCheck.getNum(drugPanels[drugIndex].TF_sfCutOff) * Math.pow(sfProduct/denominator, 1.0/drugs.size());

            usedDrugs.add(drugIndex);
            //get binding sites, multiply the list with L
            if(usedDrugs.size()>1){
                subList = Arrays.stream(sortedIndices).parallel().mapToDouble(e -> drugList.get(drugIndex).get(e) * LList.get(drugIndex)/1e5 ).boxed().collect(Collectors.toList());
                
            } else{
                subList = drugList.get(drugIndex).parallelStream().map(e -> e * LList.get(drugIndex)/1e5).collect(Collectors.toList());
            }
            final java.util.List<Double> bs = subList;
            sortedIndices = IntStream.range(0, subList.size())
                    .boxed().parallel().sorted((i, j) -> bs.get(i).compareTo(bs.get(j)))
                    .mapToInt(ele -> ele).toArray();
            cutoff = (int)(sfCutoff * bs.size());
            int index = sortedIndices[cutoff];
            //search for optimal specific acti. 
            double s = 0; 
            if(usedDrugs.size()>1){
                //get the surv. prob. using doses from sum of previous drugs
                int prevDrug = usedDrugs.get(usedDrugs.size()-2);
                sp *= getSurvProb(prevDrug, index, OptimizedSpecificActs.get(OptimizedSpecificActs.size()-1), selfSList);
                s = binarySearch(1e12, sfCutoff, drugIndex, index, sp, selfSList);
            } else {
                s = binarySearch(1e12, sfCutoff, drugIndex, index, 1.0, selfSList);
            }
                    
            if(s>0) {
                OptimizedSpecificActs.add(s);
                ActiList.add(s * inputCheck.getNum(drugPanels[drugIndex].TF_Amount));
            } 
            else {
                OptimizedSpecificActs.add(0.0);
                ActiList.add(0.0);
            } 
                        
            if(cutoff == 0) break;
            sortedIndices = Arrays.copyOfRange(sortedIndices, 0, cutoff);
            
            drugs.remove(drug);
        }
        return ActiList;
    }
    
    public java.util.List getComboActivity1(java.util.List<Integer> cocktail, double presetSF, java.util.List<Double> LList, int[] indices, java.util.List selfSList){
        return getComboActivity1(cocktail, presetSF, true, LList, indices, selfSList);
    }
    /**
     * get Equi Activity for target SF using the knee algo.
     * @param cocktail Drug Combo, also modified to represent priority
     * @param presetSF user entered SF
     * @param orderFlag true: use the least SA; false: use the most SA
     * @param LList L value List
     * @param indices cell indices[numcell]
     * @param selfSList Self S values
     * @return Activity List
     */
    public java.util.List getComboActivity1(java.util.List<Integer> cocktail, double presetSF, boolean orderFlag, java.util.List<Double> LList, int[] indices, java.util.List selfSList){
        java.util.List<Double> OptimizedSpecificActs = new LinkedList<>();
        java.util.List<Double> OptimizedSF = new LinkedList<>();
        java.util.List<Double> ActiList = new LinkedList<>();
        int allComboSize = cocktail.size();
        int[] sortedIndices = Arrays.copyOf(indices, indices.length);
        java.util.List<Integer> usedDrugIndices  = new LinkedList<>();
        LinkedList<Integer> usedCellIndices = new LinkedList<>();
        double tgtSF = 1, MAXSA = 1e12;
        java.util.List drugs = cocktail.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList());
        cocktail.clear();
         while(!drugs.isEmpty() && tgtSF > presetSF){

            //custom comparator: compare on x-Specific Activity
            Comparator<Knee.pair> SAComparator = new Comparator<Knee.pair>(){
                public int compare(Knee.pair a, Knee.pair b){
                    return Double.compare(a.getX(), b.getX());  
                }
            };
            TreeMap<Knee.pair, Integer> kneeDrugMap = new TreeMap<>(SAComparator);
            
            int cutoff = 0;

            double s = 1, sf = 0; 
            double[] sp = {1.0};
            if(usedDrugIndices.size()>0){
                //get the surv. prob. using doses from sum of previous drugs
                for(int i=0; i<usedDrugIndices.size(); ++i) {
                    sp[0] *= getSurvProb(usedDrugIndices.get(i), usedCellIndices.get(i), OptimizedSpecificActs.get(i), selfSList);
                }
                
                for(int k=0; k<drugs.size(); ++k) {
                    int di = drugNameList.indexOf(drugs.get(k));
                    //get binding sites, multiply the list with L
                    java.util.List<Double> BSList = Arrays.stream(sortedIndices).parallel().mapToDouble(e -> drugList.get(di).get(e) * LList.get(di)/1e5 ).boxed().collect(Collectors.toList());
                    //sort and store index
                    sortedIndices = IntStream.range(0, BSList.size())
                        .boxed().parallel().sorted((i, j) -> BSList.get(i).compareTo(BSList.get(j)))
                        .mapToInt(ele -> ele).toArray();
                    findDrugKnee(kneeDrugMap, MAXSA, di, sortedIndices, sp[0], selfSList);               
                }
            } else {
                for(int k=0; k<drugs.size(); ++k) {
                    int di = drugNameList.indexOf(drugs.get(k));
                    //get binding sites, multiply the list with L
                    java.util.List<Double> BSList = drugList.get(di).parallelStream().map(i -> i * LList.get(di)/1e5).collect(Collectors.toList());
                    sortedIndices = IntStream.range(0, BSList.size())
                        .boxed().parallel().sorted((i, j) -> BSList.get(i).compareTo(BSList.get(j)))
                        .mapToInt(ele -> ele).toArray();
                    findDrugKnee(kneeDrugMap, MAXSA, di, sortedIndices, 1.0, selfSList);               
                }
                
            }
            int drugIndex = (orderFlag == true? kneeDrugMap.get(kneeDrugMap.firstKey()) : kneeDrugMap.get(kneeDrugMap.lastKey()));
            cocktail.add(drugIndex);
            String drug = (String) drugNameList.get(drugIndex);
            s = kneeDrugMap.firstKey().getX();
            sf = kneeDrugMap.firstKey().getY();
            tgtSF *= sf;
            usedDrugIndices.add(drugIndex);
            cutoff = (int)(tgtSF * sortedIndices.length);      
            usedCellIndices.add(sortedIndices[cutoff]);
            //adjust the last drug's SA
            if(tgtSF < presetSF && sf != 0 ){
                double prevSF = tgtSF/sf;
                sf = presetSF/prevSF;
                tgtSF = presetSF;
                cutoff = (int)(sf * sortedIndices.length);
                s = binarySearch(s, sf, drugIndex, sortedIndices[cutoff], 1, selfSList);               
                
            }
            if(drugs.size() == 1){
                double prevSF = tgtSF/sf;
                sf = presetSF/prevSF;
                tgtSF = presetSF;
                cutoff = (int)(sf * sortedIndices.length);
                s = binarySearch(MAXSA, sf, drugIndex, sortedIndices[cutoff], 1, selfSList);               
                
            }
            OptimizedSpecificActs.add(s);           
            OptimizedSF.add(sf);
            ActiList.add(s * inputCheck.getNum(drugPanels[drugIndex].TF_Amount));
            //write the sf cutoff for alldrugcombo to the GUI
            if(allComboSize == numDrugs) drugPanels[drugIndex].TF_sfCutOff.setText(String.valueOf(inputCheck.round(sf, 3)));
            //adjust the SA because of the doses from all other used drugs
            for(int i=0; i<usedDrugIndices.size()-1; ++i) {
                double multiplier = getSurvProb(usedDrugIndices.get(i), sortedIndices[cutoff], OptimizedSpecificActs.get(i), selfSList);
                double adjSA = binarySearch(MAXSA, tgtSF, usedDrugIndices.get(i), usedCellIndices.get(i), multiplier, selfSList );
                OptimizedSpecificActs.set(i, adjSA);
                ActiList.set(i, adjSA * inputCheck.getNum(drugPanels[usedDrugIndices.get(i)].TF_Amount));
            }
            //sortedIndices = Arrays.copyOfRange(sortedIndices, 0, cutoff);
            
            drugs.remove(drug);
        }
        while(!drugs.isEmpty()){
            int d = drugNameList.indexOf(drugs.remove(0));
            cocktail.add(d);
            ActiList.add(0.0);
        }
        
        return ActiList;
    }
    
    public java.util.List getComboActivity2(java.util.List<Integer> cocktail, double presetSF, java.util.List<Double> LList, int[] indices, java.util.List selfSList){
        return getComboActivity2(cocktail, presetSF, true, LList,indices, selfSList);
    }
    /**
     * get Equi Activity for target SF using the knee algo.
     * @param cocktail Drug Combo, also modified to represent priority
     * @param presetSF user entered SF
     * @param orderFlag true: use the least SA; false: use the most SA
     * @param LList L value List
     * @param indices array for storing sorted indices
     * @param selfSList Self S values
     * @return Activity List
     */
    public java.util.List getComboActivity2(java.util.List<Integer> cocktail, double presetSF, boolean orderFlag, java.util.List<Double> LList, int[] indices, java.util.List selfSList){
        java.util.List<Double> OptimizedSpecificActs = new LinkedList<>();
        java.util.List<Double> OptimizedSF = new LinkedList<>();
        java.util.List<Double> ActiList = new LinkedList<>();
        int allComboSize = cocktail.size();
        int[] sortedIndices = Arrays.copyOf(indices, indices.length);
        java.util.List<Integer> usedDrugIndices  = new LinkedList<>();
        LinkedList<Integer> usedCellIndices = new LinkedList<>();
        double tgtSF = 1, MAXSA = 1e12;
        java.util.List drugs = cocktail.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList());
        cocktail.clear();
         while(!drugs.isEmpty() && tgtSF > presetSF){

            //custom comparator: compare on x-Specific Activity
            Comparator<Knee.pair> SAComparator = new Comparator<Knee.pair>(){
                public int compare(Knee.pair a, Knee.pair b){
                    return Double.compare(a.getX(), b.getX());  
                }
            };
            TreeMap<Knee.pair, Integer> kneeDrugMap = new TreeMap<>(SAComparator);
            
            int cutoff = 0;

            double s = 1, sf = 0; 
            double[] sp = {1.0};
            if(usedDrugIndices.size()>0){
                //get the surv. prob. using doses from sum of previous drugs
                for(int i=0; i<usedDrugIndices.size(); ++i) {
                    sp[0] *= getSurvProb(usedDrugIndices.get(i), usedCellIndices.get(i), OptimizedSpecificActs.get(OptimizedSpecificActs.size()-1), selfSList);
                }
                
                for(int k=0; k<drugs.size(); ++k) {
                    int di = drugNameList.indexOf(drugs.get(k));
                    //get binding sites, multiply the list with L
                    java.util.List<Double> BSList = Arrays.stream(sortedIndices).parallel().mapToDouble(e -> drugList.get(di).get(e) * LList.get(di)/1e5 ).boxed().collect(Collectors.toList());
                    //sort and store index
                    sortedIndices = IntStream.range(0, BSList.size())
                        .boxed().parallel().sorted((i, j) -> BSList.get(i).compareTo(BSList.get(j)))
                        .mapToInt(ele -> ele).toArray();
                    findDrugKnee(kneeDrugMap, MAXSA, di, sortedIndices, sp[0], selfSList);               
                }
            } else {
                for(int k=0; k<drugs.size(); ++k) {
                    int di = drugNameList.indexOf(drugs.get(k));
                    //get binding sites, multiply the list with L
                    java.util.List<Double> BSList = drugList.get(di).parallelStream().map(i -> i * LList.get(di)/1e5).collect(Collectors.toList());
                    sortedIndices = IntStream.range(0, BSList.size())
                        .boxed().parallel().sorted((i, j) -> BSList.get(i).compareTo(BSList.get(j)))
                        .mapToInt(ele -> ele).toArray();
                    findDrugKnee(kneeDrugMap, MAXSA, di, sortedIndices, 1.0, selfSList);               
                }
                
            }
            int drugIndex = (orderFlag == true? kneeDrugMap.get(kneeDrugMap.firstKey()) : kneeDrugMap.get(kneeDrugMap.lastKey()));
            cocktail.add(drugIndex);
            String drug = (String) drugNameList.get(drugIndex);
            s = kneeDrugMap.firstKey().getX();
            sf = kneeDrugMap.firstKey().getY();
            tgtSF *= sf;
            usedDrugIndices.add(drugIndex);
            cutoff = (int)(tgtSF * sortedIndices.length);      
            usedCellIndices.add(sortedIndices[cutoff]);
            //adjust the last drug's SA
            if(tgtSF < presetSF && sf != 0){
                double prevSF = tgtSF/sf;
                sf = presetSF/prevSF;
                s = binarySearch(s, sf, drugIndex, sortedIndices[cutoff], 1, selfSList);               
                tgtSF = presetSF;
                cutoff = (int)(tgtSF * sortedIndices.length);
            }
            if(drugs.size() == 1){
                double prevSF = tgtSF/sf;
                sf = presetSF/prevSF;
                s = binarySearch(MAXSA, sf, drugIndex, sortedIndices[cutoff], 1, selfSList);               
                tgtSF = presetSF;
                cutoff = (int)(tgtSF * sortedIndices.length);
            }
            OptimizedSpecificActs.add(s);           
            OptimizedSF.add(sf);
            ActiList.add(s * inputCheck.getNum(drugPanels[drugIndex].TF_Amount));
            //write the sf cutoff for alldrugcombo to the GUI
            if(allComboSize == numDrugs) drugPanels[drugIndex].TF_sfCutOff.setText(String.valueOf(inputCheck.round(sf, 3)));
            //adjust the SA because of the doses from all other used drugs
            for(int i=0; i<usedDrugIndices.size()-1; ++i) {
                double adjSA = binarySearch(MAXSA, tgtSF, usedDrugIndices.get(i), usedCellIndices.get(i), 1, selfSList );
                OptimizedSpecificActs.set(i, adjSA);
                ActiList.set(i, adjSA * inputCheck.getNum(drugPanels[usedDrugIndices.get(i)].TF_Amount));
            }
            //sortedIndices = Arrays.copyOfRange(sortedIndices, 0, cutoff);
            
            drugs.remove(drug);
        }
        while(!drugs.isEmpty()){
            int d = drugNameList.indexOf(drugs.remove(0));
            cocktail.add(d);
            ActiList.add(0.0);
        }
        
        return ActiList;
    }
    // optimized drug acti with slsqp method
    public java.util.List getComboActivity3(java.util.List<Integer> cocktail, double presetSF, java.util.List<Double> LList, int numcells, java.util.List selfSList){

        int comboSize = cocktail.size();
        ArrayList<Double> actiList = new ArrayList<>();
        double[] upperLimits = new double[comboSize];
        double[] lowerLimits = new double[comboSize];
        //java.util.List drugs = cocktail.stream().map(i -> drugIndexNameMap.get(i)).collect(Collectors.toList());
        
        Vector2VectorFunc constraintFunction = (double[] x1, double... arg) -> {
            //x is specific acti. for each drug.
            double[] probs = new double[numcells];
            Arrays.fill(probs, 1.0);
            IntStream.range(0, x1.length).forEach(i -> {
                upperLimits[i] = inputCheck.getNum(drugPanels[cocktail.get(i)].tf_saUpperLimit);
                lowerLimits[i] = inputCheck.getNum(drugPanels[cocktail.get(i)].tf_saLowerLimit);
                IntStream.range(0, numcells).parallel().forEach(j -> {
                    probs[j] *= getSurvProb(cocktail.get(i), j, x1[i], selfSList);
                });
            });
            double prob_value = Arrays.stream(probs).average().getAsDouble();
            return new double[]{prob_value - presetSF};
        };
        
        final VectorConstraint constraint = new VectorConstraint.VectorConstraintBuilder()
        .withConstraintType(ConstraintType.EQ)
        .withConstraintFunction(constraintFunction)
        .build();
        
        double[] q = new double[comboSize];
        final Slsqp slsqp = new Slsqp.SlsqpBuilder()
            .withLowerBounds(lowerLimits)
            .withUpperBounds(upperLimits)
            .withObjectiveFunction(new VectorObjectiveFunction())
            .addVectorConstraint(constraint)
            .build();
        
        final OptimizeResult result = slsqp.minimize(q);
        
        for (Double e: result.resultVec()){
            actiList.add(e);
        }
        
        return actiList;
    }
    
    
    public static final class VectorObjectiveFunction implements Vector2ScalarFunc
    {

        public double apply(double[] x, double... arg) {
            return  Arrays.stream(x).sum();
        }
        
    }
    
    private void jTextField37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField37ActionPerformed

    }//GEN-LAST:event_jTextField37ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton29ActionPerformed

    private void cb_uploadDrugDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_uploadDrugDataActionPerformed
        //Drug Planning tab Upload btn
        switch(cb_uploadDrugData.getSelectedIndex()){
            case 0: //select file
                jLabel41.setText("File Not Selected!");
                break;
            case 1: //biological
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String line = "";
                BufferedReader br = null;
                drugList = new java.util.LinkedList<>();
                for(int i=0; i<numDrugs; i++){
                    java.util.List<Double> RFUs = new java.util.LinkedList<>();
                    drugList.add(i, RFUs);
                }
                try {
                        //double max = -1, min = Double.MAX_VALUE;
                        br = new BufferedReader(new FileReader(file));

                        while ((line = br.readLine()) != null) {
                            if ("".equals(line)) break;
                            String arr[] = line.split(",");
                            for(int i=0; i<numDrugs; i++){

                                drugList.get(i).add(Double.parseDouble(arr[i]));

                            }
                        }

                        if (drugList.get(0).size()< 2) {
                            JOptionPane.showMessageDialog(null, "not enough lines in imported data", "alert", JOptionPane.ERROR_MESSAGE);
                        } else {
                            jLabel41.setText(file.getName());
                            for(int i=0; i<numDrugs; i++){
                                drugPanels[i].TF_NumCellsLabeled.setText(String.valueOf(drugList.get(0).size()));
                                drugPanels[i].TF_NumCellsLabeled.setEditable(false);
                            }
                        }                              
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Oops, something went wrong. Please check the data", "alert", JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                }
                break;
            case 2:
                //effective
                
                break;    
        }
    }//GEN-LAST:event_cb_uploadDrugDataActionPerformed

    private void jTextField37KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField37KeyReleased
        numDrugs = Integer.parseInt(jTextField37.getText());
        drugPanels = new DrugPanel[numDrugs];
        jPanel61.removeAll();
        for(int i=0; i<numDrugs;i++){
            DrugPanel drugPanel = new DrugPanel();
            drugPanels[i] = drugPanel;
            jPanel61.add(drugPanel);
            drugPanel.lb_drugName.setText("Drug " + (i+1));
        }
        jPanel61.revalidate();
        jPanel61.repaint();
    }//GEN-LAST:event_jTextField37KeyReleased

    private void btn_drugImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_drugImportActionPerformed
        // import
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            cb_uploadDrugData.setSelectedIndex(0);
            File file = fileChooser.getSelectedFile();
            String data = "";                  
            try {
                data = new String(Files.readAllBytes(Paths.get(file.toURI())));
                String[] drugParams = data.split(";");                   
                jTextField37.setText(drugParams[0].trim());
                addDrugPanels();
                for(int i = 0; i < numDrugs; i++){
                    drugPanels[i].importDrugParam(drugParams[i+1].trim());
                }
            } catch (IOException ioe) {
                    ioe.printStackTrace();
            }
                        
        }
    }//GEN-LAST:event_btn_drugImportActionPerformed

    private void btn_drugExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_drugExportActionPerformed
        // export
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //StringBuilder sb = new StringBuilder();
            try {
                FileWriter wr = new FileWriter(file);
                String line = "";
                line = jTextField37.getText() + "\n;\n" ;
                wr.write(line);
                for(int i = 0; i < numDrugs; i++) {
                                       
                    wr.write(drugPanels[i].saveDrugParam());
                }
                wr.flush();
                wr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }//GEN-LAST:event_btn_drugExportActionPerformed

    private void cb_SFItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb_SFItemStateChanged
        switch (cb_SF.getSelectedIndex()){
            case 0: //tgt SF
                tf_tgtSF.setVisible(true);
                IntStream.range(0, numDrugs).forEach(i -> {
                   drugPanels[i].TF_sfCutOff.setEditable(false);
                   drugPanels[i].TF_sfCutOff.setText("0");
                });
                break;
            case 1: //custom SF
                tf_tgtSF.setVisible(false);
                IntStream.range(0, numDrugs).forEach(i -> {
                   drugPanels[i].TF_sfCutOff.setEditable(true);
                   drugPanels[i].TF_sfCutOff.setText("0.1");
                });
                break;            
        }
        jPanel64.revalidate();
        jPanel64.repaint();
    }//GEN-LAST:event_cb_SFItemStateChanged

    private void jLabel127MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel127MouseClicked
        openWebpage("https://mirdsoft.org/");
        
    }//GEN-LAST:event_jLabel127MouseClicked

    private void jLabel129MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel129MouseClicked
        openWebpage("https://mirdsoft.org/mirdcalc");
    }//GEN-LAST:event_jLabel129MouseClicked

    private void jLabel130MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel130MouseClicked
        openWebpage("https://mirdsoft.org/mirdct");
    }//GEN-LAST:event_jLabel130MouseClicked

    private void jLabel131MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel131MouseClicked
        openWebpage("https://mirdsoft.org/mirdfit");
    }//GEN-LAST:event_jLabel131MouseClicked

    private void jLabel125MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel125MouseClicked
        openWebpage("https://www.snmmi.org/AboutSNMMI/CommitteeContent.aspx?ItemNumber=12475");
    }//GEN-LAST:event_jLabel125MouseClicked

    private void jLabel45MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel45MouseClicked
        openWebpage("https://mirdcell.njms.rutgers.edu/");
    }//GEN-LAST:event_jLabel45MouseClicked
    public static void openWebpage(String urlString){
        try{
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e){
            
        }
    }
	//action and effects showed in the canvas when rc, rn ,dis changed through the plus/minus buttons
	@Override
	public void actionPerformed(ActionEvent e) {
		//07/29/10 add rc < 11
	}

	//071310 add highlight feature to textarea
	private Action getAction(String name) {
		Action action = null;
		Action[] actions = jTextArea1.getActions();
		for (int i = 0; i < actions.length; i++) {
			if (name.equals(actions[i].getValue(Action.NAME).toString())) {
				action = actions[i];
				break;
			}
		}
		return action;
	}
        
        
         public static void main(String[] args) {
               JFrame frame = new JFrame(  );
		frame.setSize( 1096, 749 );
		final JApplet program = new Home1();
		frame.getContentPane().add(program);
		frame.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				program.stop();
				program.destroy();
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				program.stop();
				program.destroy();
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}
		} );
		frame.setVisible( true );
		program.init();
		program.start();
                
        }


	//<editor-fold desc="Variables: do not modify">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_drugExport;
    private javax.swing.JButton btn_drugImport;
    private java.awt.Button button3;
    private java.awt.Button button4;
    private java.awt.Button button5;
    private java.awt.Button button6;
    private java.awt.Button button8;
    private java.awt.Button button9;
    private javax.swing.JComboBox<String> cb_SF;
    private javax.swing.JComboBox<String> cb_uploadDrugData;
    private GUI.CellCanvasInfoNew cellCanvasInfoNew1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    public javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JRadioButton jRadioButton1;
    public javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JTabbedPane jTabbedPane7;
    public javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel label_2;
    private java.awt.Panel panel1;
    private java.awt.Panel panel2;
    private java.awt.Panel panel4;
    private java.awt.Panel panel5;
    public javax.swing.JTextField tf_tgtSF;
    // End of variables declaration//GEN-END:variables
	//</editor-fold>
}