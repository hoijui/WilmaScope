package org.wilmascope.areaChart3D;

/*
 * The following source code is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Christine Wu however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * areaChart3D as an influence and inform me(sainttale@hotmail.com)
 * if you do anything really cool with it.
 *
 * -- Christine, 2003
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Provides main method for running as stand alone app.
 * 
 * Opens up a chart with data from the specified file.
 * <p>
 * file format is:
 * <p>
 * first line: list of column names separated by '|'
 * <br>
 * subsequent lines: row heading|float data values separated by '|'
 * <p>
 * parser is not very robust so make sure file is correct.
 * @author star
 */

public class Main {

	/**
	 * @param args a chart window will be opened for 
	 * the data file specified on the command line
	 */
	public static void main(String[] args) {
			try {
				Chart mainFrame = new Chart(args[0]);
        mainFrame.setSize(400,400);
				mainFrame.setTitle(args[0]);
				mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
			} catch (IOException e) {
				System.err.println("IOError: " + e.getMessage());
			}
	}
}
