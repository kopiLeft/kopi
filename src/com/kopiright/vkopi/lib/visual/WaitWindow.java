/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ProgressWindow.java 33836 2009-02-02 16:37:31Z bmwael $
 */

package com.kopiright.vkopi.lib.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * This class displays a window with a menu, a tool bar, a content panel
 * and a footbar
 */
public class WaitWindow {

  public WaitWindow(Frame frame) {
    this.frame = frame;
  }

  // ---------------------------------------------------------------------
  // INFORMATIONS
  // ---------------------------------------------------------------------
  class TimerListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      long newTime = System.currentTimeMillis();

      seconds += newTime - time;
      time = newTime;

      progressBar.setValue(seconds);
    }
  }
  /**
   * setWaitInfo
   */
  public final void setWaitDialog(String message, int maxTime) {
    if (waitDialog == null) {
      waitDialog = new JDialog(frame, "no frame", true);
      waitDialog.setUndecorated(true);
      waitDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

      progressBar = new JProgressBar(0, maxTime);
      progressBar.setValue(0);
      progressBar.setStringPainted(true);

      text = new JLabel(message);

      JPanel            panel = new JPanel();

      panel.setLayout(new BorderLayout());
      panel.add(new JLabel("<html><b> Bitte um Geduld ...</b><br>" + message), BorderLayout.NORTH);
      panel.add(progressBar, BorderLayout.SOUTH);
      panel.setBorder(new CompoundBorder(new LineBorder(Color.black, 1), new EmptyBorder(2,2,2,2)));

      waitDialog.getContentPane().add(panel);

      timer = new Timer(250, new TimerListener()); 
      timer.start();
      time = System.currentTimeMillis();

      Dimension         screen = Toolkit.getDefaultToolkit().getScreenSize();
      Point		parentPos = new Point(0, 0);

      waitDialog.pack(); 
      SwingUtilities.convertPointToScreen(parentPos, frame);

      int		posx = parentPos.x + frame.getSize().width / 2 - waitDialog.getSize().width / 2;
      int		posy = parentPos.y + frame.getSize().height / 2 - waitDialog.getSize().height / 2;

      if (posx < 0) {
        posx = 0;
      } else if (posx + waitDialog.getSize().width > screen.width) {
        posx = screen.width - waitDialog.getSize().width;
      }

      if (posy < 0) {
        posy = 0;
      } else if (posy + waitDialog.getSize().height > screen.height) {
        posy = screen.height - waitDialog.getSize().height;
      }
      posx = Math.max(posx, 0);
      posy = Math.max(posy, 0);

      waitDialog.setLocation(posx, posy);

      final JDialog  wd = waitDialog;
      SwingThreadHandler.start(new Runnable() {
          public void run () {
            wd.show();
          }
        });
    } else {
      // change dialog text
      text.setText("<html><b> Bitte um Beduld</b><br>" + message);
      progressBar.setMaximum(maxTime);
    }
  }

  /**
   * change mode to free state
   */
  public final void unsetWaitDialog() {
    waitDialog.setVisible(false);
    timer.stop();
    waitDialog.dispose();
    waitDialog = null;
  }

  Frame         frame;

  int           seconds = 0;
  long          time;

  JDialog       waitDialog;
  Timer         timer;
  JProgressBar  progressBar;
  JLabel        text;
}