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

package org.kopi.kopi.comp.kjc;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

public class KopiImplicitReturnBlock extends JBlock {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public KopiImplicitReturnBlock(TokenReference where, JStatement[] body, JavaStyleComment[] comments) {
    super(where, body, comments);
  }

//   public JStatement[] getBody() {
//     return body;
//   }
  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CBlockContext	self = new CBlockContext(context, context.getEnvironment());

    for (int i = 0; i < body.length; i++) {
      if (! self.isReachable()) {
	throw new CLineError(body[i].getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
      }
      try {
	body[i].analyse(self);
      } catch (CLineError e) {
	self.reportTrouble(e);
      }
    }

    TokenReference      ref = getTokenReference();

    if (self.isReachable()) {
      implicitReturn = new KopiReturnStatement(ref,null,null);
      try {
        implicitReturn.analyse(self);
      } catch (CLineError le) {
        context.reportTrouble(le);
      }
    }

    self.close(ref);
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitBlockStatement(this, body, getComments());
    if (implicitReturn != null) {
      implicitReturn.accept(p);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    code.setLineNumber(getTokenReference().getLine());
    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }
    if (implicitReturn != null) {
      implicitReturn.genCode(context);
    }
  }


  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  protected JStatement          implicitReturn;
}
