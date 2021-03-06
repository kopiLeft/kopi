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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.Serializable;

/**
 * Registered objects are notified with actions performed
 * on a form connector.
 */
public interface FormListener extends Serializable {

  /**
   * Fired when a page is selected inside a form.
   * @param page The page index.
   */
  public void onPageSelection(int page);
  
  /**
   * Requests to go to the next position.
   */
  public void gotoNextPosition();

  /**
   * Requests to go to the previous position.
   */
  public void gotoPrevPosition();

  /**
   * Requests to go to the last position.
   */
  public void gotoLastPosition();

  /**
   * Requests to go to the last position.
   */
  public void gotoFirstPosition();

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  public void gotoPosition(int posno);
}
