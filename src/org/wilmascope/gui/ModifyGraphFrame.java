package org.wilmascope.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.wilmascope.control.GraphControl;
import org.wilmascope.control.WilmaMain;
import org.wilmascope.graphgen.GeneratorManager;
import org.wilmascope.graphmodifiers.GraphModifier;
import org.wilmascope.graphmodifiers.ModifierManager;
import org.wilmascope.util.Registry.UnknownTypeException;

/**
 * @author dwyer
 * 
 * Controls for generating random test graphs
 */
public class ModifyGraphFrame extends JFrame {
  String title;

  boolean lineRend = true;

  private JComboBox modifiersComboBox = new JComboBox(ModifierManager
      .getInstance().getTypeList());;

  ButtonGroup radioGroup = new ButtonGroup();

  ButtonGroup radioGroup3 = new ButtonGroup();

  Box boxLayout;

  JPanel controlPanel = new JPanel();

  JButton okButton = new JButton("Modify Graph");

  GraphControl gc;

  GraphModifier modifier;

  public ModifyGraphFrame(String title, final GraphControl gc) {
    this.title = title;
    this.gc = gc;
    modifier = ModifierManager.getInstance().getDefault();
    ImageIcon icon = new ImageIcon(org.wilmascope.images.Images.class
        .getResource("WilmaW24.png"));
    this.setIconImage(icon.getImage());

    setTitle(title);
    boxLayout = Box.createVerticalBox();
    this.getContentPane().add(boxLayout, null);
    modifiersComboBox.setSelectedItem(modifier.getName());
    boxLayout.add(modifiersComboBox);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 1));
    //    buttonPanel.setBorder(new TitledBorder(""));
    okButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        modifier.modify(gc.getRootCluster());
      }
      
    });
    buttonPanel.add(okButton);
    boxLayout.add(buttonPanel); // i should tidy up the heirachy
    controlPanel = modifier.getControls();
    boxLayout.add(controlPanel);
    modifiersComboBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        modifiersComboBox_actionPerformed(e);
      }
    });
    pack();
  }

  public void modifiersComboBox_actionPerformed(ActionEvent e) {
    try {
      modifier = ModifierManager.getInstance().getPlugin(
          (String) modifiersComboBox.getSelectedItem());
    } catch (UnknownTypeException e1) {
      WilmaMain.showErrorDialog("Unknown modifier error!",e1);
    }
    if (controlPanel != null) {
      boxLayout.remove(controlPanel);
    }
    controlPanel = modifier.getControls();
    boxLayout.add(controlPanel);
    pack();
  }
}