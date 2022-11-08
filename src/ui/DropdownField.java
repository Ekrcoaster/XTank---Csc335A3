package ui;
/*
 * This is a custom UI element that handles integers on the UI
 * Author: Ethan Rees
 */


import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class DropdownField extends JPanel {
	private JLabel label;
	private JComboBox<String> box;
	
	public DropdownField(String label, String value, String[] options, int labelWidth, int boxWidth, int height) {
		this.label = new JLabel(label);
		this.box = new JComboBox<String>(options);
		
		// add the elements
		add(this.label);
		add(this.box);
		
		// configure the widths of each
		this.label.setBounds(0, 0, labelWidth, height);
		
		//set the panel settings
		setMaximumSize(new Dimension(labelWidth + boxWidth, 32));
		setForeground(null);
		setBackground(null);
		setBorder(null);
		
		setValue(value);
	}

	public void setEnabled(boolean enabled) {
		box.setEnabled(enabled);
	}
	
	public void addChangeListener(ActionListener listener) {
		this.box.addActionListener(listener);
	}
	
	public String getValue() {
		return (String)box.getSelectedItem();
	}
	
	public void setValue(String value) {
		box.setSelectedItem(value);
	}
}
