package ui;
/*
 * This is a custom UI element that handles integers on the UI
 * Author: Ethan Rees
 */


import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

public class StringField extends JPanel {
	private JLabel label;
	private JTextField field;
	
	public StringField(String label, String value, int labelWidth, int boxWidth, int height) {
		this.label = new JLabel(label);
		//this.spinnerModule = new SpinnerNumberModel(value, min, max, 1);
		this.field = new JTextField(value);
		
		// add the elements
		add(this.label);
		add(this.field);
		
		// configure the widths of each
		this.label.setBounds(0, 0, labelWidth, height);
		this.field.setBounds(labelWidth, 0, boxWidth, height);
		this.field.setPreferredSize(new Dimension(boxWidth, height));
		this.field.setSize(new Dimension(boxWidth, height));
		
		//set the panel settings
		setMaximumSize(new Dimension(labelWidth + boxWidth, 32));
		setForeground(null);
		setBackground(null);
		setBorder(null);
		
	}
	
	public void setEnabled(boolean enabled) {
		field.setEnabled(enabled);
	}
	
	public void addChangeListener(DocumentListener listener) {
		this.field.getDocument().addDocumentListener(listener );
	}
	
	public String getValue() {
		return (String)field.getText();
	}
	
	public void setValue(String value) {
		field.setText(value);
	}
}
