/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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
package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorIntegerField;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Connector for the editor integer fields.
 */
@SuppressWarnings("serial")
@Connect(value = GridEditorIntegerField.class, loadStyle = LoadStyle.DEFERRED)
public class EditorIntegerFieldConnector extends EditorTextFieldConnector {

  @Override
  public VEditorIntegerField getWidget() {
    return (VEditorIntegerField) super.getWidget();
  }
  
  @Override
  public EditorIntegerFieldState getState() {
    return (EditorIntegerFieldState) super.getState();
  }
}
