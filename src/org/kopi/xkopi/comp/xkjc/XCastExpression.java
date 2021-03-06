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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JBooleanLiteral;
import org.kopi.kopi.comp.kjc.JByteLiteral;
import org.kopi.kopi.comp.kjc.JCastExpression;
import org.kopi.kopi.comp.kjc.JCharLiteral;
import org.kopi.kopi.comp.kjc.JDoubleLiteral;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFloatLiteral;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JLongLiteral;
import org.kopi.kopi.comp.kjc.JNullLiteral;
import org.kopi.kopi.comp.kjc.JShortLiteral;
import org.kopi.kopi.comp.kjc.KjcMessages;
import org.kopi.kopi.comp.kjc.TypeFactory;

/**
 * This class represents a cast expression '((byte)2)'
 */
public class XCastExpression extends JCastExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a node in the parsing tree.
   * This method is directly called by the parser.
   * @param	where           the line of this node in the source code
   * @param	expr            the expression to be casted
   * @param	dest            the type of this expression after cast
   * @param     synthetic       expression generated by the compiler ?
   */
  public XCastExpression(TokenReference where,
                         JExpression expr,
                         CType dest,
                         boolean synthetic)
  {
    super(where, expr, dest);
    this.synthetic = synthetic;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    expr = expr.analyse(context);

    try {
      dest = dest.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    if (!synthetic && getTokenReference() != TokenReference.NO_REF) {
      if (expr.getType(factory).equals(dest)
          || (!expr.getType(factory).isPrimitive() 
              && expr.getType(factory).isAssignableTo(context, dest) 
              && expr.getType(factory) != context.getTypeFactory().getNullType()))
      {
        context.reportTrouble(new CWarning(getTokenReference(),
                                           KjcMessages.UNNECESSARY_CAST,
                                           expr.getType(factory),
                                           dest));
      }
    }

    if (!expr.getType(factory).isCastableTo(context, dest) || castToPrimitive(expr.getType(factory), getType(factory))) {
      // not java castable...
      JExpression	left = null;

      // may try all explicitly loaded classes
      if (dest.isReference()) {
	left = new JCastExpression(getTokenReference(),
				   new JNullLiteral(getTokenReference()),
				   dest);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_BYTE)) {
	left = new JByteLiteral(getTokenReference(), (byte)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_SHORT)) {
	left = new JShortLiteral(getTokenReference(), (short)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_CHAR)) {
	left = new JCharLiteral(getTokenReference(), (char)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
	left = new JIntLiteral(getTokenReference(), (int)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	left = new JLongLiteral(getTokenReference(), (long)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_FLOAT)) {
	left = new JFloatLiteral(getTokenReference(), (float)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_DOUBLE)) {
	left = new JDoubleLiteral(getTokenReference(),(double)0);
      } else if (dest == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN)) {
	left = new JBooleanLiteral(getTokenReference(), true);
      } else {
	check(context, false, KjcMessages.ASSIGNMENT_BADTYPE, expr.getType(factory), dest);
      }

      XOverloadedMethodCallExpression	overload;

      overload = new XOverloadedMethodCallExpression(getTokenReference(),
						     null,
						     "operator$" + XConstants.OPE_CAST,
						     new JExpression[]{ left, expr });

      try {
	JExpression	convert = XUtils.fetchOverloadedOperator(context, overload);

	if (convert != null) {
	  return convert.analyse(context);
	}
      } catch (PositionedError error) {
	//context.reportTrouble(error); // prevent user that there is a probleme with overloading
      }
      check(context, false, KjcMessages.CAST_CANT, expr.getType(factory), dest);
    }

    if (expr.isConstant() /*&& expr.getType(factory).isPrimitive() */) {
      return expr.convertType(context, dest);
    }

    if (!dest.isAssignableTo(context, expr.getType(factory))) {
      return expr.convertType(context, dest);
    }

    return this;
  }

  private boolean castToPrimitive(CType from, CType to) {
    if (!(to instanceof XPrimitiveClassType)) {
      return false;
    }
    if (!(from instanceof CReferenceType)) {
      return false;
    }

    return ((XPrimitiveClassType)to).getNotNullableClass().equals(from);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBESR
  // ----------------------------------------------------------------------

  private final boolean                 synthetic;
}
