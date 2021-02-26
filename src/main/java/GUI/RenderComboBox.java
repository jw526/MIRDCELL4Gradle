
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
/**
 *
 * @author rusty
 */
public class RenderComboBox extends BasicComboBoxRenderer {
    private ListSelectionModel enabledItems;

    private Color disabledColor = Color.lightGray;
    

    public RenderComboBox() {}

    public RenderComboBox(ListSelectionModel enabled) {
        super();
        this.enabledItems = enabled;
    }

    public void setEnabledItems(ListSelectionModel enabled) {
        this.enabledItems = enabled;
    }

    public void setDisabledColor(Color disabledColor) {
        this.disabledColor = disabledColor;
    }
    
    public boolean isSelected(ListSelectionModel model, int index){
        return model.isSelectedIndex(index);
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (!enabledItems.isSelectedIndex(index)) {// not enabled
            if (isSelected) {
                c.setBackground(UIManager.getColor("ComboBox.background"));
            } else {
                c.setBackground(super.getBackground());
            }

            c.setForeground(disabledColor);

        } else {
            c.setBackground(super.getBackground());
            c.setForeground(super.getForeground());
        }
        return c;
    }
}

