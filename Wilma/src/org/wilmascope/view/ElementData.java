package org.wilmascope.view;
import javax.swing.JMenuItem;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ElementData {

  public ElementData() {
  }
  public JMenuItem getMenuItem() {
    return menuItem;
  }
  public void setMenuItem(JMenuItem menuItem) {
    this.menuItem = menuItem;
  }
  private JMenuItem menuItem;
}