package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.wilmascope.control.GraphControl;
import org.wilmascope.view.ElementData;
import org.wilmascope.view.EdgeView;
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
  Hashtable fmList = new Hashtable();
  Hashtable companyList = new Hashtable();
  public abstract class QueryNodeData extends ElementData {
    boolean expanded;
    Statement stmt;
    Hashtable neighbours = new Hashtable();
    GraphControl.NodeFacade node;
    void setExpanded() {
      setActionDescription("Hide Neighbours...");
      setActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          collapseNeighbours();
        }
      });
      expanded = true;
    }
    void setCollapsed() {
      setActionDescription("Expand Neighbours...");
      setActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          expandNeighbours();
        }
      });
      expanded = false;
    }
    void addNeighbour(GraphControl.NodeFacade n, String key) {
      neighbours.put(key, n);
    }
    abstract void collapseNeighbours();
    abstract void expandNeighbours();
  }
  public class CompanyNodeData extends QueryNodeData {
    String epic;
    public CompanyNodeData(GraphControl.NodeFacade companyNode, Statement stmt, String epic) {
      this.stmt = stmt;
      this.epic = epic;
      this.node = companyNode;
      setCollapsed();
    }
    void collapseNeighbours() {
      for(Enumeration e = neighbours.keys(); e.hasMoreElements();) {
        String fmcode = (String)e.nextElement();
        GraphControl.NodeFacade n = (GraphControl.NodeFacade)fmList.get(fmcode);
        if(n.getDegree() == 1) {
          fmList.remove(fmcode);
          n.delete();
          neighbours.remove(fmcode);
        }
      }
      graphRoot.unfreeze();
      setCollapsed();
    }
    void expandNeighbours() {
      try {
        ResultSet r = stmt.executeQuery(
          "select fmcode, fund_man, shares*share_pric as val " +
          "from holders " +
          "where epic = '" + epic +"' " +
          "order by val"
        );
        int i=0;
        while(r.next() && i++ < 10) {
          String fmcode = r.getString("fmcode");
          String fundman = r.getString("fund_man");
          GraphControl.NodeFacade n = (GraphControl.NodeFacade)fmList.get(fmcode);
          if(n==null) {
            n = graphRoot.addNode("DefaultNodeView");
            n.setColour(0.0f,0.8f,0.0f);
            n.setLabel(fundman);
            n.setPosition(node.getPosition());
            FMNodeData data = new FMNodeData(n, stmt, fmcode);
            data.addNeighbour(node, epic);
            n.setUserData(data);
            fmList.put(fmcode,n);
          }

          if(neighbours.get(fmcode)==null) {
            neighbours.put(fmcode,n);
            GraphControl.EdgeFacade e = graphRoot.addEdge(n, node, "Plain Edge", 0.005f);
          }
        }
        graphRoot.unfreeze();
        setExpanded();
      } catch(SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }
  public class FMNodeData extends QueryNodeData {
    String fmcode;
    public FMNodeData(GraphControl.NodeFacade fmnode, Statement stmt, String fmcode) {
      this.stmt = stmt;
      this.fmcode = fmcode;
      this.node = fmnode;
      setCollapsed();
    }
    void collapseNeighbours() {
      for(Enumeration e = neighbours.keys(); e.hasMoreElements();) {
        String epic = (String)e.nextElement();
        GraphControl.NodeFacade n = (GraphControl.NodeFacade)companyList.get(epic);
        if(n.getDegree() == 1) {
          companyList.remove(epic);
          n.delete();
          neighbours.remove(epic);
        }
      }
      graphRoot.unfreeze();
      setCollapsed();
    }
    void expandNeighbours() {
      try {
        ResultSet r = stmt.executeQuery(
          "select epic, full_name, shares*share_pric as val " +
          "from holders " +
          "where fmcode = " + fmcode +" " +
          "order by val"
        );
        int i=0;
        while(r.next() && i++ < 10) {
          String epic = r.getString("epic");
          String fullName = r.getString("full_name");
          GraphControl.NodeFacade n = (GraphControl.NodeFacade)companyList.get(epic);
          if(n==null) {
            n = graphRoot.addNode("DefaultNodeView");
            n.setLabel(fullName);
            n.setPosition(node.getPosition());
            CompanyNodeData data = new CompanyNodeData(n, stmt, epic);
            data.addNeighbour(node, fmcode);
            n.setUserData(data);
            companyList.put(epic, n);
          }

          if(neighbours.get(epic)==null) {
            neighbours.put(epic,n);
            GraphControl.EdgeFacade e = graphRoot.addEdge(node, n, "Plain Edge", 0.005f);
          }
        }
        graphRoot.unfreeze();
        setExpanded();
      } catch(SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }
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
    fmList.clear();
    companyList.clear();
    try {
      Connection con = DriverManager.getConnection(url, "dwyer", "");
      Statement stmt = con.createStatement();
      ResultSet r = stmt.executeQuery(
        "select fmcode, fund_man "+
        "from market "+
        "where fund_man like '%"+startFundman+"%' "
      );
      r.next();
      String fmcode = r.getString("fmcode");
      String fundman = r.getString("fund_man");
      GraphControl.NodeFacade n = graphRoot.addNode("DefaultNodeView");
      n.setColour(0f,0.8f,0f);
      n.setLabel(fundman);
      n.setUserData(new FMNodeData(n,stmt,fmcode));
      fmList.put(fmcode, n);
    } catch(SQLException e) {
      System.out.println(e.getMessage());
    }

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
        float weight = (shareValue - minValue)/(maxValue - minValue);
        addEdge(sellSector,buySector,weight);
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
  void addEdge(String fromID, String toID, float weight) {
    float radius = 0.005f * (2*weight + 1);

    GraphControl.NodeFacade start = (GraphControl.NodeFacade)nodes.get(fromID);
    GraphControl.NodeFacade end = (GraphControl.NodeFacade)nodes.get(toID);
    GraphControl.EdgeFacade edge = graphRoot.addEdge(start,end,"Arrow",radius);
    edge.setWeight(weight);
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
