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

package org.kopi.compiler.base;

import org.kopi.util.base.Message;
import org.kopi.util.base.MessageDescription;

/**
 * This class represents warnings in the compiler error hierarchy
 */
public class CWarning extends PositionedError {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  
/**
   * An error with a formatted message as argument
   * @param	where		the position in the source code
   * @param	message		the formatted message
   */
  public CWarning(TokenReference where, Message message) {
    super(where, message);
  }

  /**
   * An error with an arbitrary number of parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameters	the array of parameters
   */
  public CWarning(TokenReference where, MessageDescription description, Object[] parameters) {
    super(where, description, parameters);
  }

  /**
   * An error with two parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   */
  public CWarning(TokenReference where,
		  MessageDescription description,
		  Object parameter1,
		  Object parameter2)
  {
    super(where, description, parameter1, parameter2);
  }

  /**
   * An error with one parameter
   * @param	where		the position in the source code
   * @param	description	the message description
   * @param	parameter	the parameter
   */
  public CWarning(TokenReference where, MessageDescription description, Object parameter) {
    super(where, description, parameter);
  }

  /**
   * An error without parameters
   * @param	where		the position in the source code
   * @param	description	the message description
   */
  public CWarning(TokenReference where, MessageDescription description) {
    super(where, description);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the severity level
   */
  public int getSeverityLevel() {
    return getFormattedMessage().getDescription().getLevel();
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5284372577017854576L;

}
