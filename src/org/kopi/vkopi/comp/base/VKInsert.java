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

import java.io.File;
import java.io.IOException;

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CParseCompilationUnitContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CTypeVariable;
import org.kopi.kopi.comp.kjc.JClassDeclaration;
import org.kopi.kopi.comp.kjc.JClassImport;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.JPackageImport;
import org.kopi.util.base.NotImplementedException;

/**
 * This class represents the definition of a form
 */
public class VKInsert
  extends VKPhylum
  implements org.kopi.vkopi.lib.form.VConstants, org.kopi.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param environment		the compilation environment
   * @param locale              the locale specified in the source
   */
  public VKInsert(TokenReference where,
                  VKEnvironment environment,
                  String locale)
  {
    super(where);

    this.environment = environment;
    this.cunit = new CParseCompilationUnitContext();
    this.clazz = new CParseClassContext();
    this.locale = locale;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this class
   */
  public String getIdent() {
    int		start = Math.max(0,
				 Math.max(1 + getTokenReference().getFile().lastIndexOf('/'),
					  1 + getTokenReference().getFile().lastIndexOf('\\')));

    int		dot = getTokenReference().getFile().lastIndexOf('.');

    return getTokenReference().getFile().substring(start, dot);
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    if (coll == null) {
      coll = new VKDefinitionCollector(environment.getInsertDirectories());
      coll.setInsert(this);
    }

    return coll;
  }

  /**
   * Returns a collector for definitiion
   */
  public CParseCompilationUnitContext getCompilationUnitContext() {
    return cunit;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public VKContext getContext(VKContext context) throws PositionedError {
    return new VKContext(context, true, clazz, cunit.getPackageName().getName() + "/" + getIdent());
  }

  /**
   * Insert all local definition to the class declaration
   */
  public void checkDefinition(VKContext context) throws PositionedError {
    getDefinitionCollector().checkDefinition(getContext(context));
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompilationUnit genCode(Compiler compiler) {
    // BUILD THE CLASS
    JClassDeclaration		self = new JClassDeclaration(getTokenReference(),
							     ACC_PUBLIC,
							     getIdent(),
                                                             CTypeVariable.EMPTY,
							     null,
							     CReferenceType.EMPTY,
							     clazz.getFields(),
							     clazz.getMethods(),
							     clazz.getInnerClasses(),
							     clazz.getBody(),
							     null,
							     null);

    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/chart", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/report", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/type", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    cunit.addTypeDeclaration(environment.getClassReader(), self);

    JCompilationUnit compilUnit = new JCompilationUnit(getTokenReference(),
                                                       environment,
						       cunit.getPackageName(),
						       cunit.getPackageImports(),
						       cunit.getClassImports(),
						       cunit.getTypeDeclarations());

    return compilUnit;
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // VK XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   */
  public void genLocalization(String directory) {
    String        baseName;

    baseName = getTokenReference().getFile();

    baseName = getTokenReference().getFile();
    if (baseName.lastIndexOf(File.separatorChar) != -1) {
	baseName = baseName.substring(baseName.lastIndexOf(File.separatorChar) + 1, baseName.lastIndexOf("."));
    } else {
	baseName = baseName.substring(0, baseName.lastIndexOf("."));
    }

    try {
      final VKLocalizationWriter writer;

      writer = new VKLocalizationWriter();
      genLocalization(writer);
      writer.write(directory, baseName, locale);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.err.println("cannot write : " + baseName);
    }
  }

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genInsert(coll);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKDefinitionCollector			coll;
  private CParseCompilationUnitContext		cunit;
  private CParseClassContext			clazz;
  protected final VKEnvironment                 environment;
  private final String                          locale;
}
