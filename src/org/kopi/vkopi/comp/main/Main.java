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

package org.kopi.vkopi.comp.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.CompilerMessages;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.compiler.base.WarningFilter;
import org.kopi.compiler.tools.antlr.extra.InputBuffer;
import org.kopi.compiler.tools.antlr.extra.Parser;
import org.kopi.compiler.tools.antlr.runtime.ParserException;
import org.kopi.kopi.comp.kjc.CBinaryTypeContext;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.KjcClassReader;
import org.kopi.kopi.comp.kjc.KjcMessages;
import org.kopi.vkopi.comp.base.BaseMessages;
import org.kopi.vkopi.comp.base.BaseParser;
import org.kopi.vkopi.comp.base.VKCompilationUnit;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKEnvironment;
import org.kopi.vkopi.comp.base.VKInsert;
import org.kopi.vkopi.comp.base.VKInsertParser;
import org.kopi.vkopi.comp.base.VKKjcTypeFactory;
import org.kopi.vkopi.comp.base.VKOptions;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKStdType;
import org.kopi.vkopi.comp.base.VKTopLevel;
import org.kopi.vkopi.comp.chart.ChartParser;
import org.kopi.vkopi.comp.form.FormParser;
import org.kopi.vkopi.comp.print.PrintParser;
import org.kopi.vkopi.comp.report.ReportParser;
import org.kopi.xkopi.comp.xkjc.XKjcSignatureParser;

/**
 * This class implements the entry point of the Kopi compiler
 */
public class Main extends Compiler implements VKInsertParser {

  // ----------------------------------------------------------------------
  // ENTRY POINT
  // ----------------------------------------------------------------------

  /**
   * Entry point
   *
   * @param     args            the command line arguments
   */
  public static void main(String[] args) {
    boolean     success;

    success = new Main(null, null).run(args);
    System.exit(success ? 0 : 1);
  }

  /**
   * Creates a new compiler instance.
   *
   * @param     workingDirectory        the working directory
   * @param     diagnosticOutput        the diagnostic output stream
   */
  public Main(String workingDirectory, PrintWriter diagnosticOutput) {
    super(workingDirectory, diagnosticOutput);

    infiles = new Vector();
  }

  // --------------------------------------------------------------------
  // Language
  // --------------------------------------------------------------------

  /**
   * Returns the version of the source code
   *
   * @return     version of the code
   */
  public int getSourceVersion() {
    return  1;
  }

  // ----------------------------------------------------------------------
  // RUN FROM COMMAND LINE
  // ----------------------------------------------------------------------

  /**
   * Runs the compiler
   *
   * @param     args            the command line arguments
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }

    VKEnvironment      environment = createEnvironment(options);
    VKStdType.init(new CBinaryTypeContext(environment.getClassReader(),
                                          environment.getTypeFactory()),
                   this);

    topLevel = new VKTopLevel(this, this, environment);
    errorFound = false;


    gkopic.initialize(environment);
    org.kopi.xkopi.comp.xkjc.XUtils.initialize(environment, options.xkjcpath, !options.nooo, options.database);


    if (options.sqlCase != null) {
      if (options.sqlCase.equals("upper")) {
        org.kopi.xkopi.comp.xkjc.XUtils.sqlToUpper = true;
      } else {
        options.usage();
        inform(BaseMessages.BAD_SQL_CASE_OPTION);
        return false;
      }
    }

    if (verboseMode()) {
      inform(CompilerMessages.COMPILATION_STARTED, new Integer(infiles.size()));
    }

    try {
      infiles = verifyFiles(infiles);
    } catch (UnpositionedError e) {
      reportTrouble(e);
      return false;
    }

    options.destination = checkDestination(options.destination);

   if (options.localizationOnly) {
     VKInsert[] tree = new VKInsert[infiles.size()];

     for (int count = 0; count < tree.length; count++) {
       tree[count] = (VKInsert)parseFile((File)infiles.get(count), environment);
       tree[count].genLocalization(options.localizationDirectory);
     }
     return true;
   }

    // PARSING
    VKCompilationUnit[]  tree = new VKCompilationUnit[infiles.size()];

    for (int count = 0; count < tree.length; count++) {
      tree[count] = (VKCompilationUnit)parseFile((File)infiles.get(count), environment);
    }

    if (errorFound) {
      return false;
    }


    try {
      gkopic.setFiles(verifyFiles(gkopic.getFiles()));
    } catch (UnpositionedError e) {
      reportTrouble(e);
      return false;
    }

    if (!gkopic.parseFiles(environment)) {
      return false;
    }


   if (!options.beautify && !options.localizationOnly) {
     for (int count = 0; count < tree.length; count++) {
       try {
         tree[count].checkCode(new VKContext(this, topLevel, options.sqlTrigger));
         tree[count].genLocalization(options.localizationDirectory);
       } catch (PositionedError e) {
         reportTrouble(e);
       }
     }

     if (errorFound) {
       return false;
     }

     JCompilationUnit[] cunits = topLevel.genCUnits(this, tree);

     // $$$ IN ONE BIG METHOD IN GKOPIC

     if (!gkopic.join(cunits)) {
       return false;
     }
     if (!gkopic.checkInterface(cunits)) {
       return false;
     }
     if (!gkopic.prepareInitializers()) {
       return false;
     }
     if (!gkopic.checkInitializers()) {
       return false;
     }
     if (!gkopic.checkBody()) {
       return false;
     }
   }

   if (options.beautify) {
     for (int count = 0; count < tree.length; count++) {
       tree[count].genVKCode(options.destination, environment.getTypeFactory());
     }
   }



   gkopic.genCode(environment.getTypeFactory());

   if (verboseMode()) {
     inform(CompilerMessages.COMPILATION_ENDED);
   }

    if (environment.isDeprecatedUsed() && !environment.showDeprecated()) {
      // One warning in something deprecated is used
      inform(KjcMessages.SOMETHING_DEPRECATED_USED);
    }

    return true;
  }

  protected VKEnvironment createEnvironment(VKOptions options) {
    KjcClassReader      reader = new KjcClassReader(getWorkingDirectory(),
                                                    options.classpath,
                                                    options.extdirs,
                                                    new XKjcSignatureParser());
    return new VKEnvironment(reader,
                             new VKKjcTypeFactory(this, reader, options.source.equals("1.5")),
                             options);
  }

  /**
   * Parse the argument list and set flags
   *
   * @param     args            the command line arguments
   * @return    parsing error occur
   */
  protected boolean parseArguments(String[] args) {
    options = new VKOptions();
    gkopic = new org.kopi.vkopi.comp.trig.Main(getWorkingDirectory(), getDiagnosticOutput());      //!!!FIXME
    gkopic.setOptions(options);

    if (! options.parseCommandLine(args)) {
      return false;
    }

    List        gfiles = new Vector();

    for (int i = 0; i < options.nonOptions.length; i++) {
      String    suffix = options.nonOptions[i];

      if (suffix.endsWith(".vp") || suffix.endsWith(".vf") || suffix.endsWith(".vr") || suffix.endsWith(".vc")) {
        infiles.add(options.nonOptions[i]);
      } else {
        gfiles.add(options.nonOptions[i]);
      }
    }

    gkopic.setFiles(gfiles);

    return true;
  }

  // --------------------------------------------------------------------
  // COMPILER
  // --------------------------------------------------------------------

  /**
   * Reports a trouble (error or warning).
   *
   * @param     trouble         a description of the trouble to report.
   */
  public void reportTrouble(PositionedError trouble) {
    if (trouble instanceof CWarning) {
      if (options.warning != 0 &&
          filterWarning((CWarning)trouble)) {
        inform(trouble);
      }
    } else {
      if (trouble.getTokenReference() != TokenReference.NO_REF) {
        inform(trouble);
        errorFound = true;
      }
    }
  }
  protected boolean filterWarning(CWarning warning) {
    switch (getFilter().filter(warning)) {
    case WarningFilter.FLT_REJECT:
      return false;
    case WarningFilter.FLT_FORCE:
      return true;
    case WarningFilter.FLT_ACCEPT:
      return warning.getSeverityLevel() <= options.warning;
    default:
      throw new org.kopi.util.base.InconsistencyException();
    }
  }

  protected WarningFilter getFilter() {
    if (filter == null) {
      if (options.filter != null) {
        try {
          filter = (WarningFilter)Class.forName(options.filter).newInstance();
        } catch (Exception e) {
          inform(KjcMessages.FILTER_NOT_FOUND, options.filter);
        }
      }
      if (filter == null) {
        filter = new org.kopi.kopi.comp.kjc.DefaultFilter();
      }
    }

    return filter;
  }

  /**
   * Reports a trouble.
   *
   * @param     trouble         a description of the trouble to report.
   */
  public void reportTrouble(UnpositionedError trouble) {
    inform(trouble);
    errorFound = true;
  }

  /**
   * @return true if compilation in verbose mode
   */
  public boolean verboseMode() {
    return options.verbose;
  }

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  /**
   * parse the givven file and return a compilation unit
   * side effect: increment error number
   * @param     file            the name of the file (assert exists)
   * @return    the compilation unit defined by this file
   */
  protected VKPhylum parseFile(File file, VKEnvironment environment) {
    InputBuffer buffer;

    try {
      buffer = new InputBuffer(file, options.encoding);
    } catch (UnsupportedEncodingException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.UNSUPPORTED_ENCODING,
                                          options.encoding));
      return null;
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
                                          file.getPath(),
                                          e.getMessage()));
      return null;
    }

    Parser      parser = null;
    VKPhylum    unit;
    long        lastTime = System.currentTimeMillis();

    try {
      if (options.localizationOnly) {
        parser = new BaseParser(this, buffer, environment);
        unit = ((BaseParser)parser).vkCompilationUnit();
      } else {
        if (file.getName().endsWith(".vf")) {
          parser = new FormParser(this, buffer, environment);
          unit = ((FormParser)parser).vfCompilationUnit();
        } else if (file.getName().endsWith(".vr")) {
          parser = new ReportParser(this, buffer, environment);
          unit = ((ReportParser)parser).vrCompilationUnit();
        } else if (file.getName().endsWith(".vc")) {
          parser = new ChartParser(this, buffer, environment);
          unit = ((ChartParser)parser).vcCompilationUnit();
        } else if (file.getName().endsWith(".vp")) {
          parser = new PrintParser(this, buffer, environment);
          unit = ((PrintParser)parser).prCompilationUnit();
        } else {
          inform(BaseMessages.UNKNOWN_FILE_SUFFIX, file.getPath());
          return null;
        }
      }
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      unit = null;
    } catch (Exception e) {
      e.printStackTrace();
      errorFound = true;
      unit = null;
    }

    if (verboseMode()) {
      inform(CompilerMessages.FILE_PARSED, file.getPath(), new Long(System.currentTimeMillis() - lastTime));
    }

    try {
      buffer.close();
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
                                          file.getPath(),
                                          e.getMessage()));
    }

    return unit;
  }

  /**
   * parse the givven file and return a compilation unit
   * side effect: increment error number
   * @param     file            the name of the file (assert exists)
   * @return    the compilation unit defined by this file
   */
  public VKInsert parseInsert(File file, VKEnvironment environment) {
    InputBuffer buffer;

    try {
      buffer = new InputBuffer(file, options.encoding);
    } catch (UnsupportedEncodingException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.UNSUPPORTED_ENCODING,
                                          options.encoding));
      return null;
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
                                          file.getPath(),
                                          e.getMessage()));
      return null;
    }

    Parser      parser = null;
    VKInsert    unit;
    long        lastTime = System.currentTimeMillis();

    try {
      if (file.getName().endsWith(".vf") || file.getName().endsWith(".vr") || file.getName().endsWith(".vc")) {
        parser = new BaseParser(this, buffer, environment);
        unit = ((BaseParser)parser).vkCompilationUnit();
      } else if (file.getName().endsWith(".vp")) {
        parser = new PrintParser(this, buffer, environment);
        unit = ((PrintParser)parser).prInsert();
      } else {
        inform(BaseMessages.UNKNOWN_FILE_SUFFIX, file.getPath());
        return null;
      }
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      unit = null;
    } catch (Exception e) {
      e.printStackTrace();
      errorFound = true;
      unit = null;
    }

    if (verboseMode()) {
      inform(CompilerMessages.FILE_PARSED, file.getPath(), new Long(System.currentTimeMillis() - lastTime));
    }

    try {
      buffer.close();
    } catch (IOException e) {
      reportTrouble(new UnpositionedError(CompilerMessages.IO_EXCEPTION,
                                          file.getPath(),
                                          e.getMessage()));
    }

    return unit;
  }

  // --------------------------------------------------------------------
  // COMPILER
  // --------------------------------------------------------------------

  /**
   * Do we parse comment
   */
  public boolean parseComments() {
    return options.beautify;
  }

  // ----------------------------------------------------------------------
  // PROTECTED DATA MEMBERS
  // ----------------------------------------------------------------------

  protected static org.kopi.vkopi.comp.trig.Main   gkopic;
  protected static boolean                              errorFound;
  private static VKTopLevel                             topLevel;

  // it must be initialized to null, otherwise the filter option is not used
  private WarningFilter filter = null;

  protected VKOptions   options;
  protected List        infiles;
}
