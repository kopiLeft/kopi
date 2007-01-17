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

package com.kopiright.vkopi.lib.form;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.list.VColorColumn;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.base.Query;

public class VColorField extends VField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public VColorField(int width, int height) {
    super(1, 1);
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Color[2 * block.getBufferSize()];
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("color-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Color");
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VColorColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(Object o) {
  }

  public int getType() {
    return MDL_FLD_COLOR;
  }
  // ---------------------------------------------------------------------
  // PROTECTED UTILS
  // ---------------------------------------------------------------------

  // MOVED TO VFIELDUI
//   /**
//    * Create a display widget for this field
//    */
//   protected DField createDisplay(DLabel label) {
//     return new DColorField(getUI(), label, getAlign(), 0);
//   }

  // ---------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ---------------------------------------------------------------------

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  public int getSearchType() {
    return VConstants.STY_NO_COND;
  }

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setColor(r, null);
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setColor(int r, Color v) {
    if (isChangedUI() || value[r] != v) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = v;
      // inform that value has changed
      setChanged(r);
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    if (v instanceof byte[]) {
      byte[]  b = (byte[])v;
      setColor(r, new Color(reformat(b[0]), reformat(b[1]), reformat(b[2])));
    } else {
      setColor(r, (Color)v);
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    if (query.isNull(column)) {
      return null;
    } else {
      byte[]  b = (byte[])query.getObject(column);

      return new Color(reformat(b[0]), reformat(b[1]), reformat(b[2]));
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a date value.
   */
  public Color getColor(int r) {
    return (Color) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    throw new InconsistencyException("UNEXPECTD GET TEXT");
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == null ? "NULL" : "?";
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasLargeObject(int r) {
    return  value[r] != null;
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public InputStream getLargeObject(int r) {
    if (value[r] == null) {
      return null;
    } else {
      return new ByteArrayInputStream((byte[])getObjectImpl(r));
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  protected String formatImage(Object value) {
    return "image";
  }


//   /**
//    * autofill
//    * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     Color f = JColorChooser.showDialog(getForm().getFrame(), Message.getMessage("color-chooser"), getColor(getBlock().getActiveRecord()));

//     setColor(f);
//     if (gotoNextField) {
//       getBlock().gotoNextField();
//     }
//   }
  /**
   * autofill
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur in gotoNextField
   */
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (handler != null) {
      setColor(block.getActiveRecord(),
               handler.selectColor(getColor(getBlock().getActiveRecord())));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Reformat a unsigned int from a byte
   */
  private int reformat(byte b) {
    return b < 0 ? b + 255 : b;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private Color[]		value;
}
