package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.wilmascope.control.GraphControl;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class QueryFrame extends JFrame {
  JLabel jLabel2 = new JLabel();
  JTextField endDateField = new JTextField();
  JPanel jPanel1 = new JPanel();
  JButton okButton = new JButton();
  GraphControl.ClusterFacade graphRoot;

  public QueryFrame(GraphControl c) {
    this.graphRoot = c.getRootCluster();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    jLabel2.setText("End Date:");
    endDateField.setPreferredSize(new Dimension(100, 27));
    endDateField.setText("01-apr-01");
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jLabel1.setText("Start Date:");
    fmMovementPanel.setLayout(gridLayout1);
    startDateField.setPreferredSize(new Dimension(100, 27));
    startDateField.setText("01-jan-01");
    gridLayout1.setRows(2);
    gridLayout1.setColumns(2);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jLabel3.setText("First Fund Manager:");
    fundmanField.setText("Sharelink");
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(okButton, null);
    this.getContentPane().add(queryPane,  BorderLayout.NORTH);
    queryPane.add(fmMovementPanel,   "FM Movement");
    fmMovementPanel.add(jLabel1, null);
    fmMovementPanel.add(startDateField, null);
    fmMovementPanel.add(jLabel2, null);
    fmMovementPanel.add(endDateField, null);
    queryPane.add(ownershipPanel,  "Ownership");
    ownershipPanel.add(jLabel3, null);
    ownershipPanel.add(fundmanField, null);
    jPanel1.add(cancelButton, null);
    pack();
  }

  void okButton_actionPerformed(ActionEvent ev) {
    Component selected = queryPane.getSelectedComponent();
    if(selected == fmMovementPanel) {
      fmMovementQuery(startDateField.getText(),endDateField.getText());
    } else if (selected == ownershipPanel) {
      ownershipQuery(fundmanField.getText());
    }
    this.dispose();
  }
  void ownershipQuery(String startFundman) {
    String url = new String("jdbc:postgresql:citywatch");
    nodes.clear();
    String fmcodeQueryString =
      "select fmcode "+
      "from market "+
      "where fund_man like '%"+startFundman+"%' ";
  }
  void fmMovementQuery(String startDate, String endDate) {
    String url = new String("jdbc:postgresql:citywatch");
    nodes.clear();
    String maxMinQueryString =
      "select max(val) as maxVal, min(val) as minVal "+
      "from (select b.fund_man, b.sector, s.sector, "+
      "      sum(b.shares*b.share_pric) + sum(s.shares*s.share_pric) as val "+
      "      from buy b, sell s "+
      "      where b.fund_man = s.fund_man "+
      "      and b.notified between date('"+startDate+"') "+
      "                         and date('"+endDate+"') "+
      "      and s.notified between date('"+startDate+"') "+
      "                         and date('"+endDate+"')"+
      "      group by b.fund_man, b.sector, s.sector) as subselect";

    String queryString =
      "select b.fund_man, b.sector as buy_sector, s.sector as sell_sector, "+
      " b.sec_name as buy_sec_name, s.sec_name as sell_sec_name, " +
      " sum(b.shares*b.share_pric) + sum(s.shares*s.share_pric) as value, "+
      " count(*) "+
      "from buy b, sell s "+
      "where b.fund_man = s.fund_man "+
      "and b.notified between date('"+startDate+"') and date('"+endDate+"') "+
      "and s.notified between date('"+startDate+"') and date('"+endDate+"') "+
      "group by 1,2,3,4,5 ";

    try {
      Connection con = DriverManager.getConnection(url, "dwyer", "");
      Statement stmt = con.createStatement();
      ResultSet r = stmt.executeQuery(maxMinQueryString);
      r.next();
      float minValue = r.getFloat("minVal");
      float maxValue = r.getFloat("maxVal");

      r = stmt.executeQuery(queryString);
      while(r.next()) {
        System.out.println(r.getString(1));
        String buySector = r.getString("buy_sector");
        String sellSector = r.getString("sell_sector");
        if(buySector.equals(sellSector)) {
          continue;
        }
        addNode(buySector,r.getString("buy_sec_name"));
        addNode(sellSector,r.getString("sell_sec_name"));
	      float shareValue = r.getFloat("value");
	      float edginess = (shareValue - minValue)/(maxValue - minValue);
        addEdge(sellSector,buySector,edginess);
        System.out.println("Buy Sector = "+buySector+", Sell Sector = "+sellSector);
      }
      stmt.close();
      con.close();
    } catch(SQLException e) {
      System.out.println(e.getMessage());
    }
    graphRoot.unfreeze();
  }
  void addNode(String id, String label) {
    if(!nodes.containsKey(id)) {
      GraphControl.NodeFacade n = graphRoot.addNode("LabelOnly");
      n.setLabel(label);
      nodes.put(id,n);
    }
  }
  void addEdge(String fromID, String toID, float edginess) {
    float radius = 0.005f * (2*edginess + 1);
    float hue = (231f/357f * (1-edginess));
    java.awt.Color c = Color.getHSBColor(hue,1f,1f);

    GraphControl.NodeFacade start = (GraphControl.NodeFacade)nodes.get(fromID);
    GraphControl.NodeFacade end = (GraphControl.NodeFacade)nodes.get(toID);
    GraphControl.EdgeFacade edge = graphRoot.addEdge(start,end,"Arrow",radius);
    edge.setColour(c);
    System.out.println("edginess="+edginess+", radius="+radius+", hue="+hue);
  }
  Hashtable nodes = new Hashtable();
  JTabbedPane queryPane = new JTabbedPane();
  JPanel fmMovementPanel = new JPanel();
  JLabel jLabel1 = new JLabel();
  GridLayout gridLayout1 = new GridLayout();
  JTextField startDateField = new JTextField();
  JPanel ownershipPanel = new JPanel();
  JButton cancelButton = new JButton();
  JLabel jLabel3 = new JLabel();
  JTextField fundmanField = new JTextField();

  void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }
}
