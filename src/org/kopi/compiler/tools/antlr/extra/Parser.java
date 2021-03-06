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

package org.kopi.compiler.tools.antlr.extra;

import java.util.Vector;

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.CompilerMessages;
import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.JavadocComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.tools.antlr.runtime.LLkParser;
import org.kopi.compiler.tools.antlr.runtime.ParserException;

/**
 * This class describes the capabilities of parsers.
 */
public abstract class Parser extends LLkParser {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new parser instance.
   * @param	compiler	the invoking compiler.
   * @param	scanner		the token stream generator
   * @param	lookahead	lookahead
   */
  protected Parser(Compiler compiler, Scanner scanner, int lookahead) {
    super(scanner, lookahead);
    this.compiler = compiler;
    this.scanner = scanner;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the compiler driver which invoked the parser.
   */
  public Compiler getCompiler() {
    return compiler;
  }

  /**
   * Returns the input buffer.
   */
  public final InputBuffer getBuffer() {
    return scanner.getBuffer();
  }

  /**
   * Returns a reference to the current position in the source file.
   */
  protected final TokenReference buildTokenReference() {
    return scanner.getTokenReference();
  }

  /**
   *
   */
  protected final JavaStyleComment[] getStatementComment() {
    return scanner.getStatementComment();
  }

  /**
   *
   */
  protected final JavadocComment getJavadocComment() {
    return scanner.getJavadocComment();
  }

  /**
   *
   */
  protected Vector getComment() {
    return null; // scanner.getComment();
  }

  /**
   * Reports that an error has been detected in the lexical analyser.
   * The handling is delegated to the compiler driver.
   * @param	error		the error to report
   */
  protected final void reportTrouble(PositionedError trouble) {
    compiler.reportTrouble(trouble);
  }

  /**
   * Generate an human readable error message
   */
  public PositionedError beautifyParseError(ParserException e) {
    String	message = e.toString(); // can do better

    if (message == null) {
      message = "unknown";
    } else {
      int	idx = message.indexOf(",");

      if (idx >= 0) {
	message = message.substring(idx + 1);
      }
    }

    return new PositionedError(scanner.getTokenReference(), CompilerMessages.SYNTAX_ERROR, message);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final Compiler	compiler;
  private final Scanner		scanner;
}
