package ui;
/*
 * This is a custom UI element that handles integers on the UI
 * Author: Ethan Rees
 */


import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class IntegerField extends JPanel {
	private SpinnerNumberModel spinnerModule;
	private JLabel label;
	private JSpinner spinner;
	
	public IntegerField(String label, int value, int min, int max, int labelWidth, int boxWidth, int height) {
		this.label = new JLabel(label);
		this.spinnerModule = new SpinnerNumberModel(value, min, max, 1);
		this.spinner = new JSpinner(this.spinnerModule);
		
		// add the elements
		add(this.label);
		add(this.spinner);
		
		// configure the widths of each
		this.label.setBounds(0, 0, labelWidth, height);
		this.spinner.setBounds(labelWidth, 0, boxWidth, height);
		this.spinner.setPreferredSize(new Dimension(boxWidth, height));
		this.spinner.setSize(new Dimension(boxWidth, height));
		
		//set the panel settings
		setMaximumSize(new Dimension(labelWidth + boxWidth, 32));
		setForeground(null);
		setBackground(null);
		setBorder(null);
	}

	public SpinnerNumberModel getSpinnerModule() {
		return spinnerModule;
	}
	
	public void setEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
	}
	
	public void addChangeListener(ChangeListener listener) {
		this.spinnerModule.addChangeListener(listener);
	}
	
	public int getValue() {
		return (int)spinnerModule.getNumber();
	}
	
	public void setValue(int value) {
		spinnerModule.setValue(value);
	}
}
