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

package org.kopi.bytecode.ksm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.kopi.bytecode.classfile.ClassFileFormatException;
import org.kopi.bytecode.classfile.ClassInfo;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.tools.antlr.runtime.ParserException;

/**
 * This class is the entry point for the KOPI assembler.
 */
public class Assembler {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Assembles an assembly language file to a class file
   *
   * @param	sourceFile		the name of the assembly language file
   * @param	destination		the directory root to place the classfile
   * @exception	KsmError		an error occurred
   */
  public void assemble(String sourceFile, String destination, int optimize) throws KsmError {
    ClassInfo		classInfo;

    classInfo = parseInput(sourceFile);

    if (optimize > 0) {
     
	org.kopi.bytecode.optimize.Main.optimizeClass(classInfo, optimize, false);
    
    }

    try {
      classInfo.write(destination);
    } catch (ClassFileFormatException e) {
      throw new KsmError(null, KsmMessages.SEMANTIC_ERROR, classInfo.getName(), e.getMessage());
    } catch (IOException e) {
      throw new KsmError(null, KsmMessages.IO_EXCEPTION, classInfo.getName(), e.getMessage());
    }
  }

  /**
   * Parses an assembly language file and creates a class info structure from it
   *
   * @param	sourceFile		the name of the source file
   * @return	a class info structure holding the information from the source
   */
  private ClassInfo parseInput(String sourceFile) throws KsmError {
    try {
      InputStream	input = new BufferedInputStream(new FileInputStream(sourceFile));
      KsmLexer		scanner = new KsmLexer(input);
      KsmParser		parser = new KsmParser(scanner);
      ClassInfo		classInfo = parser.aCompilationUnit();

      input.close();

      return classInfo;
    } catch (FileNotFoundException e) {
      throw new KsmError(null, KsmMessages.FILE_NOT_FOUND, sourceFile);
    } catch (IOException e) {
      throw new KsmError(null, KsmMessages.IO_EXCEPTION, sourceFile, e.getMessage());
    } catch (KsmError e) {
      // caught and rethrown because it is a subclass of antl.ParserException
      throw e;
    } catch (ParserException e) {
      throw new KsmError(new TokenReference(sourceFile, e.getLine()),
			 KsmMessages.SYNTAX_ERROR,
			 e.getMessage());
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

}
