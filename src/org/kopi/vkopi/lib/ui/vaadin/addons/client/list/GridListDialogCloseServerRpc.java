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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Client side to server side communication for list close events
 */
public interface GridListDialogCloseServerRpc extends ServerRpc {

  /**
   * Fired when the list dialog is closed.
   * @param escaped Is it closed with escape code ?
   * @param newForm Is it closed with new form code ?
   */
  void closed(boolean escaped, boolean newForm);
}
