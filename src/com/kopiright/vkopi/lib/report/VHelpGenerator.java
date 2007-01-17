/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
 * $Id$
 */

package com.kopiright.vkopi.lib.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.visual.VCommand;

/**
 * This class implements a Kopi pretty printer
 */
public class VHelpGenerator extends com.kopiright.vkopi.lib.visual.VHelpGenerator {

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * prints a compilation unit
   */
  public String helpOnReport(String name,
                             VCommand[] commands,
                             MReport model,
                             String help)
  {
    File	file = null;
    FileWriter	fileWriter;
    String[]	version;

    try {
      file 		= Utils.getTempFile(name, "htm");
      fileWriter 	= new FileWriter(file);
      p 		= new PrintWriter( new BufferedWriter(fileWriter));

      p.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//DE\">");
      p.println("<!--Generated by kopi help generator-->");
      p.println("<TITLE>" + name + "</TITLE>");
      p.println("<META NAME=\"description\" CONTENT=\"" + name + "\">");
      p.println("<META NAME=\"keywords\" CONTENT=\"" + name + "\">");
      p.println("<META NAME=\"resource-type\" CONTENT=\"document\">");
      p.println("<META NAME=\"distribution\" CONTENT=\"global\">");
      p.println("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\">");
      p.println("</HEAD>");
      p.println("<BODY BGCOLOR=#FFFFFF>");
      p.println("<CENTER><H1>" + name + "</H1></CENTER>");
      if (help != null) {
	p.println("<P>" + help + "</P>");
      }

      if (commands != null) {
        helpOnCommands(commands);
      }

      int columnCount = model.getModelColumnCount();

      p.println("<TABLE border=\"0\" cellspacing=\"3\" cellpadding=\"2\">");
      p.println("<TR>");
      p.println("<TD><pre>    </pre>");
      p.println("</TD>");
      p.println("<TD>");
      p.println("<DL>");
      for (int i = 0; i < columnCount; i++) {
        VReportColumn  column = model.getModelColumn(i);

        if ((column.getOptions() & Constants.CLO_HIDDEN) == 0) {
          column.helpOnColumn(this);
        }
      }
      p.println("</DL>");
      p.println("</TD>");
      p.println("</TR>");
      p.println("</TABLE>");

      p.println("<BR>");
      p.println("<ADDRESS>");
      p.println("<I>kopiRight Managed Solutions GmbH</I><BR>");
      version = com.kopiright.vkopi.lib.visual.Utils.getVersion();
      for (int i=0; i<version.length; i++) {
	p.println("<I>" + version[i] + "</I><BR>");
      }
      p.println("</ADDRESS>");
      p.println("</BODY>");
      p.println("</HTML>");
      p.close();
      fileWriter.close();
    } catch (IOException e) {
      System.err.println("IO ERROR " + e);
    }
    return file.getPath();
  }


  /**
   * printlns a compilation unit
   */
  public void helpOnColumn(String label, 
                           String help)
  {
    if (label == null) {
      return;
    }
    p.println("<DT>");
    p.println("<H2>" + label + "</H2>");
    p.println("<DD>");
    if (help != null) {
      p.println("<P>" + help + "</P>");
    }
  }


}
