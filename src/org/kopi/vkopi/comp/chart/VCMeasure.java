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

package org.kopi.vkopi.comp.chart;

import java.util.Vector;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JAssignmentExpression;
import org.kopi.kopi.comp.kjc.JCastExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.vkopi.comp.base.VKCodeType;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKFixnumType;
import org.kopi.vkopi.comp.base.VKTrigger;
import org.kopi.vkopi.comp.base.VKUtils;
import org.kopi.xkopi.comp.xkjc.XStdType;

public class VCMeasure extends VCField {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * This class represents the measure definition of a chart
   *
   * @param where		the token reference of this node
   * @param ident		the ident of this field
   * @param label		the label (text on the left)
   * @param help		the help text
   * @param type		the type of this field
   * @param commands		the commands accessible in this field
   * @param triggers		the triggers executed by this field
   */
  public VCMeasure(TokenReference where,
                   String ident,
                   String label,
                   String help,
                   VCFieldType type,
                   VKCommand[] commands,
                   VKTrigger[] triggers)
  {
    super(where, ident, label, help, type, commands, triggers);
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Checks the type of this field.
   * For dimensions, we allow all types but for measures we allow only number types.
   * @param context The compilation context.
   * @throws PositionedError Error caught as soon as possible
   */
  protected void checkType(VKContext context) throws PositionedError {
    CReferenceType	refType = org.kopi.vkopi.comp.trig.GVKAccess.translate(getType().getDef().getDefaultType());

    if (refType != XStdType.Fixed && refType != XStdType.Int) {
      throw new PositionedError(getTokenReference(), ChartMessages.MEASURE_NOT_NUMERIC, getIdent());
    }
  }

  /**
   * @Override
   */
  public JExpression genConstructorCall() {
    TokenReference		ref = getTokenReference();
    Vector<JExpression>		params = new Vector<JExpression>(9);

    params.addElement(VKUtils.toExpression(ref, ident));
    params.addElement(getColor());
    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genType());
      params.addElement(((VKCodeType)type.getDef()).genSource());
    }
    if (type.getDef() instanceof VKFixnumType) {
      // remove
      params.addElement(VKUtils.toExpression(ref, ((VKFixnumType)type.getDef()).getScale()));
    }

    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genIdents());
      params.addElement(((VKCodeType)type.getDef()).genValues());
    }

    return new JAssignmentExpression(ref,
				     getThis(),
				     new JUnqualifiedInstanceCreation(ref,
                                                                      type.getDef().getMeasureChartType(),
                                                                      (JExpression[])org.kopi.util.base.Utils.toArray(params, JExpression.class)));
  }

  /**
   * Generate a class for this element
   */
  public JFieldDeclaration genCode(CParseClassContext context) {
    return VKUtils.buildFieldDeclaration(getTokenReference(),
					 /*ACC_FINAL !!!*/0,
					 type.getDef().getMeasureChartType(),
					 getIdent() + "_",
					 null);
  }


  private JExpression getColor() {
    TokenReference	ref = getTokenReference();

    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & (1 << org.kopi.vkopi.lib.chart.CConstants.TRG_COLOR)) > 0) {
	JExpression	expr;

	expr = new JMethodCallExpression(ref,
					 null,
					 "callTrigger",
					 new JExpression[]{
					   VKUtils.toExpression(ref, org.kopi.vkopi.lib.chart.CConstants.TRG_COLOR),
					   VKUtils.toExpression(ref, getIndex() + 1)
					 });
	return new JCastExpression(ref, expr, COLOR_TYPE);
      }
    }

    return VKUtils.toExpression(ref, (String)null);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  @SuppressWarnings("deprecation")
  private static final CType	COLOR_TYPE = CReferenceType.lookup(org.kopi.vkopi.lib.visual.VColor.class.getName().replace('.','/'));
}
