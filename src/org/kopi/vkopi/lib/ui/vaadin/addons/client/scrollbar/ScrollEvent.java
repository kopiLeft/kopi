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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * Represents a native scroll event.
 */
public class ScrollEvent extends DomEvent<ScrollHandler> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Default constructor
   */
  protected ScrollEvent() {}

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public final Type<ScrollHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ScrollHandler handler) {
    handler.onScroll(this);
  }
  
  /**
   * Gets the event type associated with scroll events.
   * 
   * @return the handler type
   */
  public static Type<ScrollHandler> getType() {
    return TYPE;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Event type for scroll events. Represents the meta-data associated with this
   * event.
   */
  private static final Type<ScrollHandler> TYPE = new Type<ScrollHandler>(BrowserEvents.SCROLL, new ScrollEvent());
}
