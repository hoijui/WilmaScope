/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope organisation
 * (www.wilmascope.org) however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform us (tgdwyer@yahoo.com)
 * if you do anything really cool with it.
 *
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma
 *
 * -- Tim Dwyer, 2001
 */
package org.wilmascope.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.wilmascope.control.GraphControl.Cluster;
import org.wilmascope.graphanalysis.AnalysisManager;
import org.wilmascope.graphanalysis.AnalysisPanel;
import org.wilmascope.graphanalysis.GraphAnalysis;
import org.wilmascope.util.Registry.UnknownTypeException;

/**
 * Controls for analysis plugins and determining their mappings to visual
 * elements
 * 
 * @author dwyer
 */

public class AnalysisFrame extends JFrame {
  public AnalysisFrame(String title, final Cluster rootCluster) {
    setTitle(title);
    Box topBox = Box.createHorizontalBox();
    final Box analysersBox = Box.createVerticalBox();
    final AnalysisManager manager = AnalysisManager.getInstance();
    final JComboBox analysisComboBox = new JComboBox(manager.getTypeList());
    JButton addButton = new JButton("Add");
    JButton packButton = new JButton("Pack");
    packButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pack();
      }
    });
    addButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        try {
          GraphAnalysis plugin = manager.getPlugin((String) analysisComboBox
              .getSelectedItem());
          plugin.setCluster(rootCluster);
          final AnalysisPanel ap = (AnalysisPanel) plugin.getControls();
          ap.addRemoveListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
              analysersBox.remove(ap);
              pack();
            }

          });
          ap.addPackListener(new Observer() {
            public void update(Observable o, Object arg) {
              pack();
            }

          });
          analysersBox.add(ap);
          pack();
        } catch (UnknownTypeException e1) {
          e1.printStackTrace();
        }
      }

    });
    topBox.add(analysisComboBox);
    topBox.add(addButton);
    topBox.add(packButton);
    analysersBox.add(topBox);
    add(analysersBox);
    pack();
  }
}
