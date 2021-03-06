/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

/**
 * MethodRefConstant's are used to make references to methods in classes
 *
 */

package org.kopi.bytecode.classfile;

public class MethodRefConstant extends ReferenceConstant {

  /**
   * Constructs a new method reference constant.
   *
   * @param	name	the qualified name of the referenced object
   * @param	type	the signature of the referenced object
   */
  public MethodRefConstant(String name, String type) {
    super(CST_METHOD, name, type);
  }

  /**
   * Constructs a new method reference constant.
   *
   * @param	name	the qualified name of the referenced object
   * @param	type	the signature of the referenced object
   */
  public MethodRefConstant(String owner, String name, String type) {
    super(CST_METHOD, owner, name, type);
  }

  /**
   * Constructs a method reference constant.
   *
   * @param	clazz		the class that defines the referenced object
   * @param	nametype	the simple name and signature of the referenced object
   */
  public MethodRefConstant(ClassConstant clazz, NameAndTypeConstant nametype) {
    super(CST_METHOD, clazz, nametype);
  }
}
