/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.list;

import com.kopiright.vkopi.lib.util.Message;

public class VBooleanColumn extends VListColumn {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VBooleanColumn(String title, String column, boolean sortAscending) {
    super(title, column, ALG_LEFT, Math.max(trueRep.length(), falseRep.length()), sortAscending);
  }

  /**
   * Returns a string representation of value
   */
  public Object formatObject(Object value) {
    return value == null ?
      VConstants.EMPTY_TEXT :
      ((Boolean)value).booleanValue() ? trueRep : falseRep;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static String		trueRep = Message.getMessage("true");
  private static String		falseRep = Message.getMessage("false");
}
