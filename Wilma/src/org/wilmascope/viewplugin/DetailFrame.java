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

package org.wilmascope.viewplugin;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class DetailFrame extends JFrame {
  JPanel controlsPanel;
  JButton editButton, closeButton;
  HtmlPane html;
  String text;
  DefaultNodeView view;
  public DetailFrame(DefaultNodeView view, String helpFileName) {
    super("Node Details");
    html = new HtmlPane(helpFileName);
    this.text = helpFileName;
    this.view = view;
    init();
  }
  public DetailFrame(DefaultNodeView view, String type, String text) {
    super("Node Details");
    html = new HtmlPane(type, text);
    this.text = text;
    this.view = view;
    init();
  }

  private void init(){
    setBounds( 200, 25, 400, 400);
    getContentPane().setLayout(new BorderLayout());
    controlsPanel = new JPanel();
    getContentPane().add(BorderLayout.CENTER,html);
    getContentPane().add(BorderLayout.NORTH,controlsPanel);
    controlsPanel.add(editButton = new JButton("Edit Text"));
    controlsPanel.add(closeButton = new JButton("Close"));
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButtonActionPerformed();
      }
    });
    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editButtonActionPerformed();
      }
    });
    controlsPanel.setMaximumSize(new Dimension(12345,50));
    pack();
  }
  public void closeButtonActionPerformed() {
    this.dispose();
  }
  public void editButtonActionPerformed() {
    final JFrame editFrame = new JFrame();
    editFrame.getContentPane().setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane();
    final JEditorPane editPane = new JEditorPane("text",text);
    scrollPane.getViewport().add(editPane);
    editFrame.getContentPane().add(BorderLayout.CENTER,scrollPane);
    JPanel okPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        text = editPane.getText();
        editFrame.dispose();
        html.setText(text);
        view.setData(text);
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editFrame.dispose();
      }
    });
    okPanel.add(okButton);
    okPanel.add(cancelButton);
    editFrame.getContentPane().add(BorderLayout.NORTH,okPanel);
    editFrame.setSize(320,200);
    editFrame.pack();
    editFrame.show();
  }
}
class HtmlPane extends JScrollPane implements HyperlinkListener {
  JEditorPane html;
  public void setText(String text) {
    html.setText(text);
  }
  public void init() {
    html.addHyperlinkListener(this);
    JViewport vp = getViewport();
    html.setSize(400,400);
    vp.add(html);
    vp.updateUI();
  }
  public HtmlPane(String type, String text) {
    html = new JEditorPane(type, text);
    html.addHyperlinkListener(this);
    html.setEditable(false);
    init();
  }
  public HtmlPane(String urlText) {
    try {
      URL url = new URL(urlText);
      html = new JEditorPane(urlText);
      html.setEditable(false);
      init();
    } catch (MalformedURLException e) {
      System.out.println("Malformed URL: " + e);
    } catch (IOException e) {
      System.out.println("IO Exception: " + e);
    }
  }

  /**
   * Notification of a change relative to a
   * hyperlink.
   */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      linkActivated(e.getURL());
    }
  }

  /**
   * Follows the reference in an
   * link.  The given url is the requested reference.
   * By default this calls <a href="#setPage">setPage</a>,
   * and if an exception is thrown the original previous
   * document is restored and a beep sounded.  If an
   * attempt was made to follow a link, but it represented
   * a malformed url, this method will be called with a
   * null argument.
   *
   * @param u the URL to follow
   */
  protected void linkActivated(URL u) {
    Cursor c = html.getCursor();
    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    html.setCursor(waitCursor);
    SwingUtilities.invokeLater(new PageLoader(u, c));
  }

  /**
   * temporary class that loads synchronously (although
   * later than the request so that a cursor change
   * can be done).
   */
  class PageLoader implements Runnable {

    PageLoader(URL u, Cursor c) {
      url = u;
      cursor = c;
    }

    public void run() {
      if (url == null) {
        // restore the original cursor
        html.setCursor(cursor);

        // PENDING(prinz) remove this hack when
        // automatic validation is activated.
        Container parent = html.getParent();
        parent.repaint();
      } else {
        Document doc = html.getDocument();
        try {
          html.setPage(url);
        } catch (IOException ioe) {
          html.setDocument(doc);
          getToolkit().beep();
        } finally {
          // schedule the cursor to revert after
          // the paint has happended.
          url = null;
          SwingUtilities.invokeLater(this);
        }
      }
    }

    URL url;
    Cursor cursor;
  }

}
