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
package org.kopi.xkopi.comp.database;

import org.kopi.kopi.comp.kjc.CStdType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.xkopi.comp.xkjc.XStdType;

/**
 * The type of a field which represents a integer-column in Database.k. 
 */
public class DatabaseByteColumn extends DatabaseCardinalColumn{

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are restricted.
   *
   * @param nullable true if empty entries are allowed
   * @param min the smallest value allowed in this column
   * @param max the bigest value allowed in this column
   */
  public DatabaseByteColumn(boolean isNullable, Integer min, Integer max) {
    super(isNullable, min, max);
  }

  /**
   * Creates a representation of a column with type byte. The values in 
   * the column are NOT restricted.
   */
  public DatabaseByteColumn(boolean isNullable) {
    super(isNullable);
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return isNullable ? (CType) XStdType.Byte : CStdType.Byte;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    return getCreationExpression(DatabaseByteColumn.class.getName().replace('.','/'));
  }

  /**
   * Returns true if dc is equivalent with the current column. 
   *
   * @param other other column
   * @param check specifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (! ((other instanceof DatabaseByteColumn)
           ||(((check & TYPE_CHECK_NARROWING) > 0) && (other instanceof DatabaseCardinalColumn)))) {
      return false;
    } else {
      return super.isEquivalentTo(other, check);
    }
  }

}
