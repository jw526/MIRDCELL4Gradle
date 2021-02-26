/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author JCW
 */
public class InputCheck {
    double num;
    
    public double getNum(JTextField input){
        input.setBackground(Color.white);
        try{
            num = Double.parseDouble(input.getText());
        } catch (Exception e){
            input.setBackground(Color.pink);
        }

        return num;
    }
    /**
    *round a number x to n significant number
    * @return rounded double
    */
    public double round(double x, int n){
        BigDecimal bd = new BigDecimal(x);
        bd = bd.round(new MathContext(n));
        return bd.doubleValue();
    }
    
    /**
 * Installs a listener to receive notification when the text of any
 * {@code JTextComponent} is changed. Internally, it installs a
 * {@link DocumentListener} on the text component's {@link Document},
 * and a {@link PropertyChangeListener} on the text component to detect
 * if the {@code Document} itself is replaced.
 * 
 * @param text any text component, such as a {@link JTextField}
 *        or {@link JTextArea}
 * @param changeListener a listener to receive {@link ChangeEvent}s
 *        when the text is changed; the source object for the events
 *        will be the text component
 * @throws NullPointerException if either parameter is null
 */
    public void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }

        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document)e.getOldValue();
            Document d2 = (Document)e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }
   
}
