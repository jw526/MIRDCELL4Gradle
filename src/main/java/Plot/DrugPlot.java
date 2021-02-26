/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Plot;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 *
 * @author rusty
 */
public class DrugPlot {
    
    public void generateDrugSurvival(JPanel panel, Double[] sfPerCombo, java.util.List drugComboList){
       String Ylab = "SF";
       double minY = 1e-8;
       final DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 
       for(int i = 0; i < drugComboList.size(); i++){
           dataset.addValue(sfPerCombo[i], drugComboList.get(i).toString(), drugComboList.get(i).toString());
       }
        // generate the chart
        JFreeChart survivalBar = ChartFactory.createBarChart(
                        "SF Per Drug Combo",                             // Title
                        "Drug Combo",                       // x-axis Label    
                        Ylab,   // y-axis Label
                        dataset,                      // Dataset
                        PlotOrientation.VERTICAL,       // Plot Orientation
                        false,                          // Show Legend?
                        true,                           // Use tooltips?
                        false                           // Configure chart to generate URLs?
        );
        ChartPanel CPHR = new ChartPanel(survivalBar);

        CategoryPlot categoryPlot = survivalBar.getCategoryPlot();
        BarRenderer br = new BarRenderer();
        //set the bar starting from 1 instead of 0
        br.setBase(1.0);
        br.setShadowVisible(false);
        //mouse over action and show Tool Tip
        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        categoryPlot.setRenderer(br);
        LogAxis yAxis = new LogAxis(Ylab);
        yAxis.setNumberFormatOverride(new DecimalFormat("0E00"));
        yAxis.setAutoRange(true);
        yAxis.setRange(minY, 1.1);
        
        categoryPlot.setRangeAxis(yAxis);

        //CPHR.setDisplayToolTips(true);
        //categoryPlot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        //categoryPlot.getRangeAxis().setInverted(true);
        //categoryPlot.getRangeAxis().setRange(0, 1);
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(CPHR, BorderLayout.CENTER);
        panel.validate();
    }
    
    public void generateDrugSurvival(JPanel panel, List<Double> sfPerCombo, java.util.List drugComboList){
       String Ylab = "SF";
       double minY = 1e-8;
       final DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 
       for(int i = 0; i < drugComboList.size(); i++){
           dataset.addValue(sfPerCombo.get(i), drugComboList.get(i).toString(), drugComboList.get(i).toString());
       }
        // generate the chart
        JFreeChart survivalBar = ChartFactory.createBarChart(
                        "SF Per Drug Combo",                             // Title
                        "Drug Combo",                       // x-axis Label    
                        Ylab,   // y-axis Label
                        dataset,                      // Dataset
                        PlotOrientation.VERTICAL,       // Plot Orientation
                        false,                          // Show Legend?
                        true,                           // Use tooltips?
                        false                           // Configure chart to generate URLs?
        );
        ChartPanel CPHR = new ChartPanel(survivalBar);

        CategoryPlot categoryPlot = survivalBar.getCategoryPlot();
        BarRenderer br = new BarRenderer();
        //set the bar starting from 1 instead of 0
        br.setBase(1.0);
        br.setShadowVisible(false);
        br.setItemMargin(-4);

        //mouse over action and show Tool Tip
        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        categoryPlot.setRenderer(br);
        LogAxis yAxis = new LogAxis(Ylab);
        yAxis.setNumberFormatOverride(new DecimalFormat("0E00"));
        yAxis.setAutoRange(true);
        yAxis.setRange(minY, 1.1);
        
        categoryPlot.setRangeAxis(yAxis);

        //CPHR.setDisplayToolTips(true);
        //categoryPlot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        //categoryPlot.getRangeAxis().setInverted(true);
        //categoryPlot.getRangeAxis().setRange(0, 1);
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(CPHR, BorderLayout.CENTER);
        panel.validate();
    }
    
     public void generateActivityChart(JPanel panel, Map<List<String>, List<Double>> drugComboMap, int type){
       String Ylab = "Activity for Equieffect";
       //create data set
       final DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 
       JFreeChart survivalBar;
       ChartPanel CPHR = null;
       switch(type){
           case(0):                
               // generate the bar chart
               drugComboMap.keySet().forEach(k -> {
                List<Double> act = drugComboMap.get(k);
                double total = act.stream().mapToDouble(n -> n).sum();
                dataset.addValue(total, "total", k.toString());
                for(int i=0; i< act.size(); i++ ){
                    dataset.addValue(act.get(i), k.get(i), k.toString());
                }
             });
                survivalBar = ChartFactory.createBarChart(
                                "Activity Per Drug Combo",           // Title
                                "Drug Combo",                       // x-axis Label    
                                Ylab,                                // y-axis Label
                                dataset,                      // Dataset
                                PlotOrientation.VERTICAL,       // Plot Orientation
                                true,                          // Show Legend?
                                true,                           // Use tooltips?
                                false                           // Configure chart to generate URLs?
                );
                CPHR = new ChartPanel(survivalBar);
                CategoryPlot categoryPlot = survivalBar.getCategoryPlot();
                categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
               
                break;
          case(1):
              // generate the stacked bar chart
                
              for(Entry<List<String>, List<Double>> k: drugComboMap.entrySet()){
                List<Double> act = k.getValue();
                for(int i=0; i< act.size(); i++ ){
                    dataset.addValue(act.get(i), k.getKey().get(i), k.getKey().toString());
                }
             }
                survivalBar = ChartFactory.createStackedBarChart(
                                "Activity Per Drug Combo",           // Title
                                "Drug Combo",                       // x-axis Label    
                                Ylab,                                // y-axis Label
                                dataset,                      // Dataset
                                PlotOrientation.VERTICAL,       // Plot Orientation
                                true,                          // Show Legend?
                                true,                           // Use tooltips?
                                false                           // Configure chart to generate URLs?
                );
                CPHR = new ChartPanel(survivalBar);
                categoryPlot = survivalBar.getCategoryPlot();
                categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
                break;
          default:
              break;
       }
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(CPHR, BorderLayout.CENTER);
        panel.validate();
       
       
    }
    
}
