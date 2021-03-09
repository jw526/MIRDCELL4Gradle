/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJPanel.java
 *
 * Created on Aug 26, 2009, 9:41:58 AM
 */

package GUI;

import File.FileReadWrite;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 *
 * @author johnnywu
 */
public class DrugPanel extends javax.swing.JPanel {

    /** change the initial value when default changes */
    public double amt, sfCutOff, tiac, numCells, L;  //max mean activity per cell
    boolean expanded = true;
    public Double[] aFractions;
    javax.swing.JLabel radInfo1;
    javax.swing.JPanel RadSummary;
    RadBioParaFrame radbio;
    InputCheck inputCheck;
    FileReadWrite frw;
    JFileChooser fc;
    ButtonGroup group;
    public DrugPanel() {
        initComponents();
        jLabel11.setVisible(false);
        monoPanel = new GUI.MonoParticlePanel();
        userPanel = new GUI.UserCreatedRadPanel();
        radbio = new RadBioParaFrame();
        radInfo1 = new javax.swing.JLabel();
        RadSummary = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT));
        inputCheck = new InputCheck();
        frw = new FileReadWrite();
        fc = new JFileChooser();
        aFractions = new Double[3];
        amt = inputCheck.getNum(TF_Amount);
        sfCutOff = inputCheck.getNum(TF_sfCutOff);
        tiac = inputCheck.getNum(TF_TimeIntegratedAct);
        numCells = inputCheck.getNum(TF_NumCellsLabeled);
        L = inputCheck.getNum(TF_L);
        aFractions[0] = inputCheck.getNum(TF_ActN)/100D;
        aFractions[1] = inputCheck.getNum(TF_ActCyto)/100D;
        aFractions[2] = inputCheck.getNum(TF_ActCS)/100D;
        //add radio button to a group so only 1 can be selected
        group = new ButtonGroup();
        group.add(predefRadPanel1.RB_BetaFull);
        group.add(predefRadPanel1.RB_BetaAvg);
        group.add(userPanel.RB_create);
        group.add(userPanel.RB_retrieve);
        group.add(monoPanel.RB_alpha);
        group.add(monoPanel.RB_e);
        predefRadPanel1.RB_BetaFull.addItemListener((java.awt.event.ItemEvent evt) -> {
            RB_BetaFullItemStateChanged(evt);
        });
//        predefRadPanel1.RB_BetaAvg.addItemListener((java.awt.event.ItemEvent evt) -> {
//            RB_BetaAvgItemStateChanged(evt);
//        });
        predefRadPanel1.radList.addListSelectionListener((javax.swing.event.ListSelectionEvent evt) -> {
            radListValueChanged(evt);
        });
        monoPanel.RB_alpha.addItemListener((java.awt.event.ItemEvent evt) -> {
            RB_alphaItemStateChanged(evt);
        });
        monoPanel.RB_e.addItemListener((java.awt.event.ItemEvent evt) -> {
            RB_eItemStateChanged(evt);
        });
        userPanel.RB_retrieve.addItemListener((java.awt.event.ItemEvent evt) -> {
            RB_retrieveItemStateChanged(evt);
        });
        userPanel.RB_create.addItemListener((java.awt.event.ItemEvent evt) -> {
            RB_createItemStateChanged(evt);
        });
        userPanel.bn_open.addActionListener((java.awt.event.ActionEvent evt) -> {
            bn_openActionPerformed(evt);
        });
        userPanel.bn_confirm.addActionListener((java.awt.event.ActionEvent evt) -> {
            bn_confirmActionPerformed(evt);
        });
        userPanel.bn_addRad.addActionListener((java.awt.event.ActionEvent evt) -> {
            bn_addRadActionPerformed(evt);
        });
        userPanel.bn_save.addActionListener((java.awt.event.ActionEvent evt) -> {
            bn_saveActionPerformed(evt);
        });
        userPanel.bn_reset.addActionListener((java.awt.event.ActionEvent evt) -> {
            bn_resetActionPerformed(evt);
        });
    }
    
    public static void main(String[] args){
        DrugPanel drugPanel = new DrugPanel();
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(400, 900));
        //frame.setBackground(background);
        frame.add(drugPanel);
        frame.paint(null);
        frame.show();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.repaint();
    }
    public void collapseExpand(){
        if(!expanded){
        jLabel1.setText("+ Source Radiation");
        jComboBox1.setEnabled(false); 
        SrcRad.remove(jPanel2);
        SrcRad.add(RadSummary);
        SrcRad.remove(jLabel7);
        SrcRad.remove(jScrollPane3);
        SrcRad.setPreferredSize(new Dimension(380,80));
        jPanel1.setPreferredSize(new Dimension(380,500));
        switch (jComboBox1.getSelectedIndex()){
            case 0:
                String r = ((predefRadPanel1.radList.getSelectedValue()==null)? "Please select a radionuclide.": predefRadPanel1.radList.getSelectedValue().toString() ) ;
                if(predefRadPanel1.RB_BetaFull.isSelected()){
                    radInfo1.setText("  Beta Full Energy Spectrum: " + r);
                } else if(predefRadPanel1.RB_BetaAvg.isSelected()){
                    radInfo1.setText("  Beta Avg Energy Spectrum: " + r);
                }
                break;
            case 1: 
                jPanel2.add(monoPanel);
                if(monoPanel.RB_alpha.isSelected()){
                    radInfo1.setText("  Alpha Particle with yield of " + monoPanel.TF_yield.getText() + ", energy of " + monoPanel.TF_energy.getText());
                } else if (monoPanel.RB_e.isSelected()){
                    radInfo1.setText("  Electron with yield of " + monoPanel.TF_yield.getText() + ", energy of " + monoPanel.TF_energy.getText());
                }
                break;
            case 2:
                jPanel2.add(userPanel);
                radInfo1.setText("  User Created Radionuclide " +userPanel.TF_name.getText());

                break;
            default: 
                break;
        }
        RadSummary.setPreferredSize(new Dimension(360, 80));
        RadSummary.add(radInfo1);
        } else{
            SrcRad.setPreferredSize(new Dimension(380,570));
            jPanel1.setPreferredSize(new Dimension(380,1000));
            jLabel1.setText("- Source Radiation");
            jComboBox1.setEnabled(true);
            SrcRad.add(jPanel2);
            SrcRad.add(jLabel7);
            SrcRad.add(jScrollPane3);
            SrcRad.remove(RadSummary);
        }
        SrcRad.revalidate();
        SrcRad.repaint();
    }
    public String saveDrugParam(){
        String line = "";
        StringBuilder sb = new StringBuilder();
        sb.append(lb_drugName.getText()).append('\n');
        sb.append(jComboBox1.getSelectedIndex()).append('\n');        
        switch (jComboBox1.getSelectedIndex()){
            case 0:
                if (predefRadPanel1.RB_BetaFull.isSelected()){
                    sb.append(predefRadPanel1.RB_BetaFull.getText()).append('\n');
                }
                if (predefRadPanel1.RB_BetaAvg.isSelected()){
                    sb.append(predefRadPanel1.RB_BetaAvg.getText()).append('\n');
                }
                sb.append(predefRadPanel1.radList.getSelectedIndex()).append('\n');
                break;
            case 1:
                if (monoPanel.RB_alpha.isSelected()){
                    sb.append(monoPanel.RB_alpha.getText()).append('\n');
                }
                if (monoPanel.RB_e.isSelected()){
                    sb.append(monoPanel.RB_e.getText()).append('\n');
                }
                sb.append(monoPanel.en).append('\n');
                sb.append(monoPanel.yield).append('\n');
                break;
            case 2:
                break;
        }
        sb.append(jComboBox2.getSelectedIndex()).append('\n');
        for (int h = 0; h < radbio.jTable1.getRowCount(); h++) {
            for (int i = 2; i < radbio.jTable1.getColumnCount(); i++) {
                    line += ',' + radbio.jTable1.getModel().getValueAt(h, i).toString();
            }
            line = line.substring(1) + '\n';
            sb.append(line);
            line = "";
        }
        sb.append(aFractions[2]).append('\n');
        sb.append(aFractions[1]).append('\n');
        sb.append(aFractions[0]).append('\n');
        sb.append(amt).append('\n');
        sb.append(sfCutOff).append('\n');
        sb.append(tiac).append('\n');
        sb.append(L).append('\n');
        sb.append(";").append('\n');
        return sb.toString();
    }
    
    public void importDrugParam(String drugParam){
        Scanner sc = new Scanner(drugParam);
        while (sc.hasNextLine()) {
            lb_drugName.setText(sc.nextLine());
            int radIndex = Integer.parseInt(sc.nextLine());
            jComboBox1.setSelectedIndex(radIndex);
            String radioBtnName = sc.nextLine();
            switch (radIndex){
                case 0:
                    int radListIndex = Integer.parseInt(sc.nextLine());
                    if (predefRadPanel1.RB_BetaFull.getText().equals(radioBtnName)){
                        predefRadPanel1.RB_BetaFull.setSelected(true);
                    }
                    
                    if (predefRadPanel1.RB_BetaAvg.getText().equals(radioBtnName)){
                        predefRadPanel1.RB_BetaAvg.setSelected(true);
                    }
                    predefRadPanel1.radList.setSelectedIndex(radListIndex);
                    expanded = false;
                    collapseExpand();
                    break;
                case 1:
                    if (monoPanel.RB_alpha.getText().equals(radioBtnName)){
                        monoPanel.RB_alpha.setSelected(true);
                    }
                    
                    if (monoPanel.RB_e.getText().equals(radioBtnName)){
                        monoPanel.RB_e.setSelected(true);
                    }
                    double energy = Double.parseDouble(sc.nextLine());
                    double yield = Double.parseDouble(sc.nextLine());
                    monoPanel.TF_energy.setText(String.valueOf(energy));
                    monoPanel.TF_yield.setText(String.valueOf(yield));
                    monoPanel.en = inputCheck.getNum(monoPanel.TF_energy);
                    monoPanel.yield = inputCheck.getNum(monoPanel.TF_yield);
                    expanded = false;
                    collapseExpand();
                    break;
                case 2:
                    break;
            }
            jComboBox2.setSelectedIndex(Integer.parseInt(sc.nextLine()));
            for (int h = 0; h < radbio.jTable1.getRowCount(); h++) {
                if (sc.hasNextLine()) {
                    String arr[] = sc.nextLine().split(",");
                    int i;
                    for (i = 2; i < radbio.jTable1.getColumnCount() && i < arr.length + 2; i++) {
                        radbio.jTable1.getModel().setValueAt(Double.parseDouble(arr[i - 2]), h, i);
                    }
                    if (i < radbio.jTable1.getColumnCount())
                        System.err.println("Too few  entries in given data.  Please check that it is the correct file.");
                    if (i < arr.length)
                        System.err.println("Too many entries in given data. Please check that it is the correct file.");
                } else {
                    System.err.println("not enough lines in imported data");
                }
            }
            
            double AFSC = Double.parseDouble(sc.nextLine());
            double AFCyto = Double.parseDouble(sc.nextLine());
            double AFN = Double.parseDouble(sc.nextLine());
            double amount = Double.parseDouble(sc.nextLine());
            double SF = Double.parseDouble(sc.nextLine());
            double TIAC = Double.parseDouble(sc.nextLine());
            double l = Double.parseDouble(sc.nextLine());
            TF_ActCS.setText(String.valueOf(AFSC));
            TF_ActCyto.setText(String.valueOf(AFCyto));
            TF_ActN.setText(String.valueOf(AFN));
            TF_Amount.setText(String.valueOf(amount));
            TF_sfCutOff.setText(String.valueOf(SF));
            TF_TimeIntegratedAct.setText(String.valueOf(TIAC));
            TF_L.setText(String.valueOf(l));
            aFractions[2] = AFSC;
            aFractions[1] = AFCyto;
            aFractions[0] = AFN;
            amt = amount;
            sfCutOff = SF;
            tiac = TIAC;
            L = l;
        }
        
    }
    
    private void RB_BetaFullItemStateChanged(java.awt.event.ItemEvent evt) {                                             
//        jLabel18.setText("                                                           OUTPUT");
//        jLabel22.setText("                                                                                                                                       ");

        //add value to textField
        jTextArea1.setText("");
        //jTextArea2.setText("");

        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {

            if (evt.getSource() == predefRadPanel1.RB_BetaFull) {
                String[] isotopes = {"Ac-225", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

                Object[] listData;
                listData = (Object[]) isotopes;
                predefRadPanel1.radList.setListData(listData);

            }
        } else {
            String[] isotopes = {"Ac-225", "Ac-225+daughters", "Ag-109m", "Ag-111", "Al-28", "Am-241", "Ar-37", "As-72", "As-73", "As-74", "As-76", "As-77", "At-211", "At-211+daughters", "At-217", "At-218", "Au-195", "Au-195m", "Au-198", "Au-198m", "Au-199", "Ba-128", "Ba-131", "Ba-131m", "Ba-133", "Ba-133m", "Ba-135m", "Ba-137m", "Ba-140", "Be-7", "Bi-204", "Bi-206", "Bi-207", "Bi-210", "Bi-211", "Bi-212", "Bi-213", "Bi-213+daughters", "Bi-214", "Br-75", "Br-76", "Br-77", "Br-77m", "Br-80", "Br-80m", "Br-82", "C-10", "C-11", "C-14", "Ca-45", "Ca-47", "Ca-49", "Cd-109", "Cd-115", "Cd-115m", "Ce-134", "Ce-139", "Ce-141", "Cf-252", "Cl-34", "Cl-34m", "Cl-36", "Cl-38", "Co-55", "Co-56", "Co-57", "Co-58", "Co-60", "Cr-48", "Cr-51", "Cs-128", "Cs-129", "Cs-130", "Cs-131", "Cs-132", "Cs-134", "Cs-134m", "Cs-137", "Cu-57", "Cu-61", "Cu-62", "Cu-64", "Cu-67", "Cy-157", "Cy-159", "Cy-165", "Cy-166", "Er-165", "Er-167m", "Er-169", "Er-171", "Eu-145", "Eu-147", "Eu-152", "Eu-152m", "Eu-154", "F-17", "F-18", "Fe-52", "Fe-55", "Fe-59", "Fr-221", "Ga-66", "Ga-67", "Ga-68", "Ga-72", "Gd-147", "Gd-148", "Gd-153", "Ge-68", "Ge-77", "H-3", "Hg-195", "Hg-195m", "Hg-197", "Hg-197m", "Hg-203", "Hg-206", "Ho-166", "I-120", "I-120m", "I-122", "I-123", "I-124", "I-125", "I-130", "I-131", "I-132", "I-132m", "I-133", "I-135", "In-109", "In-109m", "In-110", "In-110m", "In-111", "In-111m", "In-133m", "In-114", "In-114m", "In-115m", "Ir-190", "Ir-190m", "Ir-190n", "Ir-191m", "Ir-192", "Ir-192m", "K-38", "K-40", "K-42", "K-43", "Kr-77", "Kr-79", "Kr-81", "Kr-81m", "Kr-83m", "Kr-85", "Kr-85m", "La-134", "La-140", "Lu-176", "Lu-177", "Mg-28", "Mn-51", "Mn-52", "Mn-52m", "Mn-54", "Mo-99", "N-13", "Na-22", "Na-24", "Nb-90", "Nb-95", "Nb-95m", "Nd-140", "Ne-19", "Ni-57", "Ni-63", "O-14", "O-15", "O-19", "Os-190m", "Os-191", "Os-191m", "P-30", "P-32", "P-33", "Pb-201", "Pb-201m", "Pb-203", "Pb-204m", "Pb-209", "Pb-210", "Pb-211", "Pb-212", "Pb-214", "Pd-100", "Pd-103", "Pd-109", "Pm-145", "Pm-147", "Pm-149", "Po-209", "Po-210", "Po-211", "Po-212", "Po-213", "Po-214", "Po-215", "Po-216", "Po-218", "Pr-140", "Pr-143", "Pt-191", "Pt-193", "Pt-193m", "Pt-195m", "Pt-197", "Pt-197m", "Pu-238", "Ra-223", "Ra-223+daughters", "Ra-224", "Ra-226", "Rb-77", "Rb-79", "Rb-81", "Rb-82", "Rb-82m", "Rb-83", "Rb-84", "Rb-86", "Re-186", "Re-188", "Rh-100", "Rh-103m", "Rh-105", "Rn-219", "Rn-220", "Rn-222", "Ru-97", "Ru-103", "Ru-105", "S-35", "Sb-118", "Sb-118m", "Sb-119", "Sc-46", "Sc-47", "Sc-49", "Se-72", "Se-73", "Se-73m", "Se-75", "Se-77m", "Sm145", "Sm-153", "Sn-110", "Sn-113", "Sn-113m", "Sn-117m", "Sr-82", "Sr-83", "Sr-85", "Sr-85m", "Sr-87m", "Sr-89", "Sr-90", "Ta-177", "Ta-178m", "Ta-179", "Ta-182", "Ta-182m", "Tb-146", "Tb-149", "Tb-152", "Tb-157", "Tc-92", "Tc-93", "Tc-94", "Tc-94m", "Tc-95", "Tc-95m", "Tc-96", "Tc-97m", "Tc-99", "Tc-99m", "Te-118", "Te-123", "Te-123m", "Th-227", "Tl-200", "Tl-201", "Tl-202", "Tl-206", "Tl-207", "Tl-208", "Tl-209", "Tl-210", "Tm-167", "Tm-170", "Tm-171", "V-48", "W-177", "W-178", "W-181", "W-188", "Xe-122", "Xe-123", "Xe-127", "Xe-127m", "Xe-129m", "Xe-131m", "Xe-133", "Xe-135", "Xe-135m", "Y-86", "Y-87", "Y-88", "Y-89m", "Y-90", "Y-91", "Y-91m", "Yb-169", "Zn-62", "Zn-63", "Zn-65", "Zn-69", "Zn-69m", "Zr-89", "Zr-95"};

            Object[] listData;
            listData = (Object[]) isotopes;
            predefRadPanel1.radList.setListData(listData);
        }

    }
    
//    private void RB_BetaAvgItemStateChanged(java.awt.event.ItemEvent evt) {                                             
//        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED){
//            predefRadPanel1.radList.setSelectedIndex(0);
//        }
//    }
    
    private void radListValueChanged(javax.swing.event.ListSelectionEvent evt) {                                     
     
//        jLabel18.setText("OUTPUT");
//        jLabel22.setText("                                                                                                                                       ");
        jTextArea1.setText("");
        
        if(!predefRadPanel1.RB_BetaAvg.isSelected() && !predefRadPanel1.RB_BetaFull.isSelected()){
            jTextArea1.setText("Please Select an energy spectrum!");
            return;
        }
        
        String directory = "";
        

        String getSelectedIsoName = "";
        if(predefRadPanel1.radList.getSelectedValue()!= null){
            getSelectedIsoName = predefRadPanel1.radList.getSelectedValue().toString();
        }
        
        if(!"".equals(getSelectedIsoName) && !evt.getValueIsAdjusting()){

            predefRadPanel1.radList.setSelectedIndex(predefRadPanel1.radList.getSelectedIndex());    
            if (userPanel.RB_create.isSelected() || userPanel.RB_retrieve.isSelected()) {
                directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/User/";
            }
            else if (getSelectedIsoName.contains(".out") || getSelectedIsoName.contains(".MIRD")) {
                directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/OUTPUT/";
            } else if (predefRadPanel1.RB_BetaFull.isSelected()) {//update 07/22/09
                directory = "https://mirdcell.njms.rutgers.edu/UMDNJ/DATA%20FILES/data/";
            } else if (predefRadPanel1.RB_BetaAvg.isSelected()) {
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

            } else if (predefRadPanel1.RB_BetaAvg.isSelected()) {
                NewFileName = directory.concat(getSelectedIsoName).concat(".RAD");
            } else {
                getSelectedIsoName2 = getSelectedIsoName.toLowerCase(Locale.ENGLISH);
                System.out.println("selected2:" + getSelectedIsoName2);
                NewFileName = directory.concat(getSelectedIsoName2).concat(".dat");

            }
            String monoNewFileName = directory.concat(getSelectedIsoName2);

            System.out.println("selected file URL:" + NewFileName);
            frw.readURLFile(NewFileName, jTextArea1);

            if (getSelectedIsoName.compareTo("new_Alpha.dat") == 0) {
                frw.readFile(monoNewFileName, jTextArea1);
            } else if (getSelectedIsoName.compareTo("new_Electron.dat") == 0) {
                frw.readFile(monoNewFileName, jTextArea1);
            }

            try {
                jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(1));
            } catch (Exception ex) {
                Logger.getLogger(DrugPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (predefRadPanel1.RB_BetaFull.isSelected()) {
                if (jTextArea1.getLineCount() < 3) {
                    return;
                }
                String t = jTextArea1.getText();
                Scanner s = new Scanner(t);
                String l = s.nextLine();
                l = s.nextLine();
                int i = l.length() - 1;
                String word = l.split("\\s+")[4];
                char unit = word.charAt(word.length() - 1);
                Double mag = 100.0;
                try {
                     mag = Double.parseDouble(word.substring(0, word.length() - 1)) * 1.443;
                } catch (Exception e){
                    TF_TimeIntegratedAct.setText("100");
                    System.err.println("Problem reading in full beta radionuclide");
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
                TF_TimeIntegratedAct.setText(f.format(mag));
            } else if (predefRadPanel1.RB_BetaAvg.isSelected()) {
                if (jTextArea1.getLineCount() < 3) {
                    return;
                }
                String t = jTextArea1.getText();
                Scanner s = new Scanner(t);
                String l = s.nextLine();
                l = s.nextLine();
                String w = l.split("\\s+")[1];
                char unit = w.charAt(w.length() - 1);
                Double mag = 100.0;
                try{
                    mag = Double.parseDouble(w.substring(0, w.length() - 1)) * 1.443;
                } catch(Exception e){
                    TF_TimeIntegratedAct.setText("100");
                    System.err.println("Problem reading in avg beta radionuclide");
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
                TF_TimeIntegratedAct.setText(f.format(mag));
            }
        }       
    }
    
    private void RB_alphaItemStateChanged(java.awt.event.ItemEvent evt) {                                          
        
        //add value to textField
        jTextArea1.setText("");
        if (monoPanel.RB_alpha.isSelected()) {
            jTextArea1.setText("Monoenergetic Alpha Particle ");
        } 
        monoPanel.TF_energy.setText("");       
    }
    
    private void RB_eItemStateChanged(java.awt.event.ItemEvent evt) {                                          
        
        //add value to textField
        jTextArea1.setText("");
        if (monoPanel.RB_e.isSelected()) {
            jTextArea1.setText("Monoenergetic Electron Particle ");
        } 
        monoPanel.TF_energy.setText("");       
    }
    
    private void RB_retrieveItemStateChanged(java.awt.event.ItemEvent evt) {                                             

        if (userPanel.RB_retrieve.isSelected()) {

            userPanel.bn_open.setEnabled(true);
            userPanel.TF_name.setEnabled(false);
            userPanel.tf_energy.setEnabled(false);
            userPanel.tf_yield.setEnabled(false);
            userPanel.bn_addRad.setEnabled(false);
            userPanel.bn_confirm.setEnabled(false);
            userPanel.bn_reset.setEnabled(false);
            userPanel.bn_save.setEnabled(false);
            userPanel.jComboBox1.setEnabled(false);

        } 
    }
    
    private void RB_createItemStateChanged(java.awt.event.ItemEvent evt) {                                             

        if (userPanel.RB_create.isSelected()) {
            userPanel.bn_open.setEnabled(false);
            userPanel.TF_name.setEnabled(true);
            userPanel.tf_energy.setEnabled(true);
            userPanel.tf_yield.setEnabled(true);
            userPanel.bn_addRad.setEnabled(true);
            userPanel.bn_confirm.setEnabled(true);
            userPanel.bn_reset.setEnabled(true);
            userPanel.bn_save.setEnabled(true);
            userPanel.jComboBox1.setEnabled(true);
            jTextArea1.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide: Enter the Name        Half-Life:         Number: " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");

        }
    }
    
    private void bn_openActionPerformed(java.awt.event.ActionEvent evt) {                                        
        jTextArea1.setText("");
        int option = fc.showOpenDialog(DrugPanel.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            FileWriter fstream = null;
            FileReader reader = null;
            String readLine;
            try {
                File file = fc.getSelectedFile();
                FileInputStream fis = new FileInputStream(file);
                //BufferedWriter out = new BufferedWriter(new FileWriter(file));
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                while ((readLine = in.readLine()) != null) {
                    jTextArea1.append(readLine + "\n");
                    if (readLine.contains("New-Radionuclide:")) {
                        userPanel.TF_name.setText(readLine.substring(readLine.indexOf(":") + 1, readLine.indexOf(" ", readLine.indexOf(":") + 2)));
                    }
                }
                in.close(); //writes the content to the file
            } catch (IOException ex) {
                Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }//end if
    } 
    
    private void bn_confirmActionPerformed(java.awt.event.ActionEvent evt) {                                           
        jTextArea1.append("END RADIATION RECORDS");
    }
    
    private void bn_addRadActionPerformed(java.awt.event.ActionEvent evt) {                                          
        //08/23/09

        if (jTextArea1.getText().contains("Enter the Name")) {
            String Name = userPanel.TF_name.getText();
            jTextArea1.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide:" + Name + "   Half-Life:         Number: " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");
        }
        String radType = userPanel.jComboBox1.getSelectedItem().toString();

        /**
        * ****************************
        * get number of radiation types, find num position (line (5,1)), add
        * number to that position format en1, yield1 into 0.0000E00 delta =
        * 2.13*en1*yield1 format: "id Radiation yield/decay Energy Detal"
        */
        int num1 = jTextArea1.getLineCount() - 6;
        NumberFormat formatter = new DecimalFormat("0.00000E00");
        double en11 = inputCheck.getNum(userPanel.tf_energy);
        double yield11 = inputCheck.getNum(userPanel.tf_yield);
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
        } else {
            return;
        }
        jTextArea1.append(icode + "   " + yield111 + "      " + en111 + "       " + radCode + "\n");
    }
     
    private void bn_saveActionPerformed(java.awt.event.ActionEvent evt) {                                        
        int option = fc.showSaveDialog(DrugPanel.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            FileWriter fstream = null;
            try {
                File file = fc.getSelectedFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                jTextArea1.write(out);
                out.flush();
                out.close();

                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(Home1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }//end if
    } 

    private void bn_resetActionPerformed(java.awt.event.ActionEvent evt) {                                          
		jTextArea1.setText(""); //clear text area

		if (userPanel.RB_create.isSelected()) {
			//updated user-created 11/01/2010
			jTextArea1.setText("Create new radionuclide here: " + "\n" + "New-Radionuclide:          Half-Life:         Number: " + "\n" + "T1/2 =  Decay Mode: " + "\n" + "Radiations of each type listed in increasing energy" + "\n\n" + "Radiations of each type listed in increasing energy" + "\n" + "ICODE    Y(/nt)    E(MeV)    Mnemonic" + "\n" + "START RADIATION RECORDS" + "\n");
		} else {
			jTextArea1.setText("This area will be populated upon" + "\n" + "selecting Source Radiation.");
		}
	}       
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        DrugName = new javax.swing.JPanel();
        lb_drugName = new javax.swing.JTextField();
        SrcRad = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        predefRadPanel1 = new GUI.PredefRadPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        TgtSrc = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TF_ActCS = new javax.swing.JTextField();
        TF_ActCyto = new javax.swing.JTextField();
        TF_ActN = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Labeling = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        TF_Amount = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        TF_sfCutOff = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        TF_TimeIntegratedAct = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        TF_NumCellsLabeled = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        TF_L = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        TF_SA = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        chk_saLimits = new javax.swing.JCheckBox();
        tf_saUpperLimit = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tf_saLowerLimit = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(400, 530));

        jPanel1.setMinimumSize(new java.awt.Dimension(380, 550));
        jPanel1.setPreferredSize(new java.awt.Dimension(380, 1000));

        DrugName.setPreferredSize(new java.awt.Dimension(380, 22));

        lb_drugName.setEditable(false);
        lb_drugName.setFont(new java.awt.Font("Tahoma", 3, 10)); // NOI18N
        lb_drugName.setText("jTextField2");
        lb_drugName.setAlignmentY(0.1F);
        lb_drugName.setMinimumSize(new java.awt.Dimension(6, 16));
        lb_drugName.setPreferredSize(new java.awt.Dimension(70, 16));
        lb_drugName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lb_drugNameFocusLost(evt);
            }
        });
        lb_drugName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_drugNameMouseClicked(evt);
            }
        });
        DrugName.add(lb_drugName);

        jPanel1.add(DrugName);

        SrcRad.setMinimumSize(new java.awt.Dimension(380, 100));
        SrcRad.setPreferredSize(new java.awt.Dimension(380, 560));
        SrcRad.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("- Source Radiation:");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        SrcRad.add(jLabel1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Predefined MIRD Radionuclide", "Mono Energetic Particle Emitter", "User Created Radionuclide" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        SrcRad.add(jComboBox1);

        jPanel2.setPreferredSize(new java.awt.Dimension(370, 250));
        jPanel2.add(predefRadPanel1);

        SrcRad.add(jPanel2);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Input Data for Calculation:");
        SrcRad.add(jLabel7);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(370, 250));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        SrcRad.add(jScrollPane3);

        jPanel1.add(SrcRad);

        TgtSrc.setPreferredSize(new java.awt.Dimension(380, 168));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Target Region(s):");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cell", "Nucleus", "Cytoplasm", "Nucleus & Cyto", "Cell Surface" }));
        jComboBox2.setSelectedIndex(1);
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Source Region:");

        jLabel4.setText("% activity on Cell Surface: ");

        jLabel5.setText("% activity in Cytoplasm: ");

        jLabel6.setText("% activity in Nucleus: ");

        TF_ActCS.setText("0");
        TF_ActCS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_ActCSFocusLost(evt);
            }
        });
        TF_ActCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_ActCSActionPerformed(evt);
            }
        });

        TF_ActCyto.setText("0");
        TF_ActCyto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_ActCytoFocusLost(evt);
            }
        });

        TF_ActN.setText("100");
        TF_ActN.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_ActNFocusLost(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText("Activity Must Add Up to 100!");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jButton1.setText("RadBio Parameters");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TgtSrcLayout = new javax.swing.GroupLayout(TgtSrc);
        TgtSrc.setLayout(TgtSrcLayout);
        TgtSrcLayout.setHorizontalGroup(
            TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TgtSrcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TgtSrcLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TF_ActN, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TF_ActCyto, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TF_ActCS, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(TgtSrcLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jButton1))
                    .addGroup(TgtSrcLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        TgtSrcLayout.setVerticalGroup(
            TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TgtSrcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(16, 16, 16)
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TF_ActCS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TF_ActCyto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TgtSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(TF_ActN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel1.add(TgtSrc);

        Labeling.setPreferredSize(new java.awt.Dimension(380, 250));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Administered Amount (relative): ");

        TF_Amount.setText("1");
        TF_Amount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_AmountFocusLost(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("SF Cutoff:");

        TF_sfCutOff.setEditable(false);
        TF_sfCutOff.setText("0.0");
        TF_sfCutOff.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_sfCutOffFocusLost(evt);
            }
        });

        jLabel85.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel85.setText("Time integrated activity coefficient (hr):");

        TF_TimeIntegratedAct.setText("100");
        TF_TimeIntegratedAct.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_TimeIntegratedActFocusLost(evt);
            }
        });

        jLabel82.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel82.setText("Number of Cells Labeled:");

        TF_NumCellsLabeled.setEditable(false);
        TF_NumCellsLabeled.setText("0");
        TF_NumCellsLabeled.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_NumCellsLabeledFocusLost(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("L ((molecules/cell)/(RFU/cell)):");

        TF_L.setText("100");
        TF_L.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TF_LFocusLost(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Specific Activity (GBq/mol):");

        TF_SA.setEditable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Tolerance (%):");

        jTextField1.setEditable(false);
        jTextField1.setText("10");

        chk_saLimits.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chk_saLimits.setText("Upper:");
        chk_saLimits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_saLimitsActionPerformed(evt);
            }
        });

        tf_saUpperLimit.setEditable(false);
        tf_saUpperLimit.setText("1.00E13");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Lower:");

        tf_saLowerLimit.setEditable(false);
        tf_saLowerLimit.setText("0.00E00");

        javax.swing.GroupLayout LabelingLayout = new javax.swing.GroupLayout(Labeling);
        Labeling.setLayout(LabelingLayout);
        LabelingLayout.setHorizontalGroup(
            LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LabelingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LabelingLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(TF_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LabelingLayout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TF_TimeIntegratedAct, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LabelingLayout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TF_NumCellsLabeled, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LabelingLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TF_L, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, LabelingLayout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(TF_SA))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, LabelingLayout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(TF_sfCutOff, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(LabelingLayout.createSequentialGroup()
                        .addComponent(chk_saLimits)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_saUpperLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_saLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        LabelingLayout.setVerticalGroup(
            LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LabelingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(TF_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TF_L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(13, 13, 13)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(TF_TimeIntegratedAct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(TF_NumCellsLabeled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TF_sfCutOff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(TF_SA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LabelingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_saLimits)
                    .addComponent(tf_saUpperLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(tf_saLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanel1.add(Labeling);

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TF_ActCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_ActCSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TF_ActCSActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        jTextArea1.setText("");
        jPanel2.removeAll();
        switch (jComboBox1.getSelectedIndex()){
            case 0:
                jPanel2.add(predefRadPanel1);
                break;
            case 1: 
                jPanel2.add(monoPanel);
                TF_TimeIntegratedAct.setText("100");
                break;
            case 2:
                jPanel2.add(userPanel);
                TF_TimeIntegratedAct.setText("100");
                break;
            default:
                break;
                
        }
        jPanel2.revalidate();
        jPanel2.repaint();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        
        expanded = !expanded;
        jLabel1.setFont(new Font("Tahoma", Font.BOLD,11));
        collapseExpand();
        
    }//GEN-LAST:event_jLabel1MouseClicked

    private void TF_AmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_AmountFocusLost
        amt = inputCheck.getNum(TF_Amount);
    }//GEN-LAST:event_TF_AmountFocusLost

    private void TF_ActCSFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_ActCSFocusLost
        aFractions[2] = inputCheck.getNum(TF_ActCS)/100D;
        if(Double.parseDouble(TF_ActCS.getText()) + Double.parseDouble(TF_ActCyto.getText()) + Double.parseDouble(TF_ActN.getText()) != 100D){
            jLabel11.setVisible(true);
        } else{
            jLabel11.setVisible(false);
        }
    }//GEN-LAST:event_TF_ActCSFocusLost

    private void TF_ActCytoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_ActCytoFocusLost
        aFractions[1] = inputCheck.getNum(TF_ActCyto)/100D;
        if(Double.parseDouble(TF_ActCS.getText()) + Double.parseDouble(TF_ActCyto.getText()) + Double.parseDouble(TF_ActN.getText()) != 100D){
            jLabel11.setVisible(true);
        }else{
            jLabel11.setVisible(false);
        }
    }//GEN-LAST:event_TF_ActCytoFocusLost

    private void TF_ActNFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_ActNFocusLost
        aFractions[0] = inputCheck.getNum(TF_ActN)/100D;
        if(Double.parseDouble(TF_ActCS.getText()) + Double.parseDouble(TF_ActCyto.getText()) + Double.parseDouble(TF_ActN.getText()) != 100D){
            jLabel11.setVisible(true);
        }else{
            jLabel11.setVisible(false);
        }
    }//GEN-LAST:event_TF_ActNFocusLost

    private void TF_sfCutOffFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_sfCutOffFocusLost
        sfCutOff = inputCheck.getNum(TF_sfCutOff);
    }//GEN-LAST:event_TF_sfCutOffFocusLost

    private void TF_TimeIntegratedActFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_TimeIntegratedActFocusLost
        tiac = inputCheck.getNum(TF_TimeIntegratedAct);
    }//GEN-LAST:event_TF_TimeIntegratedActFocusLost

    private void TF_NumCellsLabeledFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_NumCellsLabeledFocusLost
        numCells = inputCheck.getNum(TF_NumCellsLabeled);
    }//GEN-LAST:event_TF_NumCellsLabeledFocusLost

    private void TF_LFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TF_LFocusLost
        L = inputCheck.getNum(TF_L);
    }//GEN-LAST:event_TF_LFocusLost

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
                        
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        radbio.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lb_drugNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_drugNameMouseClicked
        lb_drugName.setEditable(true);
    }//GEN-LAST:event_lb_drugNameMouseClicked

    private void lb_drugNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lb_drugNameFocusLost
        lb_drugName.setEditable(false);
    }//GEN-LAST:event_lb_drugNameFocusLost

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        if (jComboBox2.getSelectedIndex() == 0) {
        jLabel5.setText("% activity in Cell");
        jLabel6.setVisible(false);
        TF_ActCyto.setText("" + (100D - Double.parseDouble(TF_ActCS.getText())));
        aFractions[0] = 0D;
        TF_ActN.setText("0");
        TF_ActN.setVisible(false);

        } else {
                jLabel5.setText("% activity in Cytoplasm");
                jLabel6.setVisible(true);
                TF_ActCyto.setText ("0");
                TF_ActCS.setText("0");
                TF_ActN.setText("100");
                TF_ActN.setVisible(true);
        }
        aFractions[0] = inputCheck.getNum(TF_ActN)/100D;
        aFractions[1] = inputCheck.getNum(TF_ActCyto)/100D;
        aFractions[2] = inputCheck.getNum(TF_ActCS)/100D;
        //set the radbio table default value
        if (jComboBox2.getSelectedIndex() == 0) {
                radbio.jTable1.setModel(new javax.swing.table.DefaultTableModel(
                        new Object[][]{
                                        {"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
                                        {"7", "Auger electrons", new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
                                        {"8", "Alpha particles", new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0)},
                                        {"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"10", "Fission fragments", new Double(1.0), new Double(0.0), new Double(1.0), new Double(0.0)},
                                        {"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
                        },
                        new String[]{
                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;NCS)</p> <p>(Gy<sup>-2</sup>)</p>"
                        }
                ) {
                        Class[] types = new Class[]{
                                        java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
                        };
                        boolean[] canEdit = new boolean[]{
                                        false, false, true, true, true, true
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return canEdit[columnIndex];
                        }
                });
        } else if (jComboBox2.getSelectedIndex() == 1) {
                radbio.jTable1.setModel(new javax.swing.table.DefaultTableModel(
                        new Object[][]{
                                        {"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.83), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
                                        {"7", "Auger electrons", new Double(2.3), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0)},
                                        {"8", "Alpha particles", new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0)},
                                        {"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
                        },
                        new String[]{
                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>"
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
        } else if (jComboBox2.getSelectedIndex() == 2) {
                radbio.jTable1.setModel(new javax.swing.table.DefaultTableModel(
                        new Object[][]{
                                        {"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0)},
                                        {"7", "Auger electrons", new Double(0.025), new Double(0.0), new Double(0.083), new Double(0.0), new Double(0.025), new Double(0.0)},
                                        {"8", "Alpha particles", new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0)},
                                        {"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
                        },
                        new String[]{
                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>"
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
        } else if (jComboBox2.getSelectedIndex() == 3) {

                radbio.jTable1.setModel(new javax.swing.table.DefaultTableModel(
                        new Object[][]{
                                        {"1, 2, 3", "gamma-rays, x-rays", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"4, 5, 6", "Beta particles, internal conversion electrons", new Double(0.83), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.083), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0)},
                                        {"7", "Auger electrons", new Double(2.3), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.25), new Double(0.0), new Double(0.230), new Double(0.0), new Double(0.025), new Double(0.0), new Double(0.025), new Double(0.0)},
                                        {"8", "Alpha particles", new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(1.4), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0), new Double(0.14), new Double(0.0)},
                                        {"9", "Daughter recoil (alpha decay)", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"10", "Fission fragments", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)},
                                        {"11", "Neutrons", new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0), new Double(0.0)}
                        },
                        new String[]{
                                        "ICODE", "Radiation", "<html> <p>&alpha - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(N&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;N)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;Cy)</p> <p>(Gy<sup>-2</sup>)</p>", "<html> <p>&alpha - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-1</sup>)</p> ", "<html> <p>&beta - self</p> <p>(Cy&larr;CS)</p> <p>(Gy<sup>-2</sup>)</p>"
                        }
                ) {
                        Class[] types = new Class[]{
                                        java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
                        };
                        boolean[] canEdit = new boolean[]{
                                        false, false, true, true, true, true, true, true, true, true, true, true, true, true
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return canEdit[columnIndex];
                        }
                });
        }
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void chk_saLimitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_saLimitsActionPerformed
        // TODO add your handling code here:
        if(chk_saLimits.isSelected()){
            tf_saUpperLimit.setEditable(true);
            tf_saLowerLimit.setEditable(true);
        } else {
            tf_saUpperLimit.setEditable(false);
            tf_saLowerLimit.setEditable(false);
            tf_saUpperLimit.setText("1.00E13");
            tf_saUpperLimit.setText("0.00E00");
        }
    }//GEN-LAST:event_chk_saLimitsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DrugName;
    private javax.swing.JPanel Labeling;
    private javax.swing.JPanel SrcRad;
    public javax.swing.JTextField TF_ActCS;
    public javax.swing.JTextField TF_ActCyto;
    public javax.swing.JTextField TF_ActN;
    public javax.swing.JTextField TF_Amount;
    public javax.swing.JTextField TF_L;
    public javax.swing.JTextField TF_NumCellsLabeled;
    public javax.swing.JTextField TF_SA;
    public javax.swing.JTextField TF_TimeIntegratedAct;
    public javax.swing.JTextField TF_sfCutOff;
    private javax.swing.JPanel TgtSrc;
    private javax.swing.JCheckBox chk_saLimits;
    private javax.swing.JButton jButton1;
    public javax.swing.JComboBox<String> jComboBox1;
    public javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    public javax.swing.JTextField lb_drugName;
    public GUI.PredefRadPanel predefRadPanel1;
    public javax.swing.JTextField tf_saLowerLimit;
    public javax.swing.JTextField tf_saUpperLimit;
    // End of variables declaration//GEN-END:variables
    public MonoParticlePanel monoPanel;
    public UserCreatedRadPanel userPanel;
}
