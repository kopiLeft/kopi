/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.comp.base;

import java.util.Vector;

import org.kopi.util.base.Utils;

public class VKParseContext {

  protected VKParseContext() {
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addCommand(VKCommand command) {
    commands.addElement(command);
  }

  public void addTrigger(VKTrigger trigger) {
    triggers.addElement(trigger);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public VKCommand[] getCommands() {
    return (VKCommand[])Utils.toArray(commands, VKCommand.class);
  }

  public VKTrigger[] getTriggers() {
    return (VKTrigger[])Utils.toArray(triggers, VKTrigger.class);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector	commands = new Vector();
  private Vector	triggers = new Vector();
}
