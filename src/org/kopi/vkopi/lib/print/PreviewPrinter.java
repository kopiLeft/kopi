/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.print;

import java.io.IOException;

import org.kopi.vkopi.lib.util.AbstractPrinter;
import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.visual.ApplicationContext;

/**
 * Local printer
 */
public class PreviewPrinter extends AbstractPrinter {

  /**
   *
   */
  public PreviewPrinter(String name, String command) {
    super(name);
    setCommand(command);
  }

  /**
   * Print a file and return the output of the command
   * @deprecated
   */
  public void setCommand(String command) {
    this.command = command;
  }

  public String print(PrintJob data) throws IOException, PrintException {
    try {
      ApplicationContext.getApplicationContext().getPreviewRunner().run(data, command);
    } catch (IOException ie) {
      throw ie;
    } catch (PrintException pe) {
      throw pe;
    }

    return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		command;
}
