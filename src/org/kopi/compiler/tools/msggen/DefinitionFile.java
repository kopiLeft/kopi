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

package org.kopi.compiler.tools.msggen;

import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.kopi.util.base.InconsistencyException;


class DefinitionFile {

  /**
   * Constructs a token definition file
   */
  public DefinitionFile(String sourceFile,
                        String fileHeader,
                        String packageName,
                        String prefix,
                        String parent,
                        MessageDefinition[] definitions)
  {
    this.sourceFile = sourceFile;
    this.fileHeader = fileHeader;
    this.packageName = packageName;
    this.prefix = (prefix == null)? DEFAULT_PREFIX : prefix;
    this.parent = parent;
    this.definitions = definitions;
  }

  /**
   * Reads and parses an token definition file
   *
   * @param     sourceFile      the name of the source file
   * @return    a class info structure holding the information from the source
   */
  public static DefinitionFile read(String sourceFile) throws MsggenError {

    SAXBuilder          builder = new SAXBuilder();
    Element             root;
    Document            document;

    try {
      document = builder.build(new File(sourceFile));
    } catch (Exception e) {
      throw new InconsistencyException("Cannot load file " + sourceFile + ": " + e.getMessage());
    }
    
    root = document.getRootElement();     
    
    return new DefinitionFile(sourceFile,
                              root.getAttributeValue("fileHeader"),
                              root.getAttributeValue("package"),
                              root.getAttributeValue("prefix"),
                              root.getAttributeValue("parent"),
                              getMessages(root));
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Check for duplicate identifiers
   * @param     identifiers     a table of all token identifiers
   * @param     prefix          the literal prefix
   * @param     id              the id of the first token
   * @return    the id of the last token + 1
   */
  public void checkIdentifiers(Hashtable identifiers) throws MsggenError {

    for (int i = 0; i < definitions.length; i++) {
      definitions[i].checkIdentifiers(identifiers, sourceFile);
    }
  }

  /**
   * Prints the token definition to interface file (java)
   * @param     out             the output stream
   * @param     parent          the super interface
   */
  public void printFile(PrintWriter out) {

    if (fileHeader != null) {
      out.println(fileHeader);
    }

    out.print("// Generated by msggen from " + sourceFile);
    out.println();
    out.println("package " + packageName + ";");
    out.println();
    out.println("import org.kopi.util.base.MessageDescription;");
    out.println();
    out.print("public interface " + prefix + "Messages");
    out.print(parent == null ? "" : " extends " + parent);
    out.println(" {");

    for (int i = 0; i < definitions.length; i++) {
      definitions[i].printInterface(out, this.prefix);
    }

    out.println("}");
  }

  /**
   * Returns the package name
   */
  public String getClassName() {
    return packageName + "." + prefix + "Messages";
  }

  /**
   * Returns the package name
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * Returns the literal prefix
   */
  public String getPrefix() {
    return prefix;
  }

  public MessageDefinition[] getDefinition(){
    return definitions;
  }
  
  public String getParent() {
    return prefix;
  }
 
  /**
   * Reads options from the xml definition file
   *
   * @param     element         the xml root element.
   * @return    a MessageDefinition class holding the information from the source
   *
   */
  public static MessageDefinition[] getMessages(Element element) {

    List                msg;
    MessageDefinition[] messages;
    Iterator            iter;
    
    msg = element.getChildren("msg");
    messages = new MessageDefinition[msg.size()];
    iter = msg.iterator();
    
    for (int i = 0; iter.hasNext(); i++) {
      Element   current = (Element)iter.next();

      messages[i] = new MessageDefinition(current.getAttributeValue("identifier"),
                                          current.getAttributeValue("format"),
                                          current.getAttributeValue("reference"),
                                          Integer.parseInt(current.getAttributeValue("level")));
    }
    
    return messages;
  }


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final String           DEFAULT_PREFIX = "";

  private final String                  sourceFile;
  private final String                  fileHeader;
  private final String                  packageName;
  private final String                  prefix;
  private final String                  parent;
  private final MessageDefinition[]     definitions;
}
