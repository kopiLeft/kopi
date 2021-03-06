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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Font Awesome icon widget.
 */
public class VIcon extends Widget implements HasClickHandlers {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the FontAwesome icon widget.
   */
  public VIcon() {
    setElement(Document.get().createElement("i"));
    setStyleName(Styles.FONT_AWESOME); // as defined in CSS
    sinkEvents(Event.ONCLICK);
    Roles.getImgRole().setAriaHiddenState(getElement(), true);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addHandler(handler, ClickEvent.getType());
  }
  
  /**
   * Sets the icon name. The name is based on the CSS definition in font awesome pack.
   * We add a style dependent name with the name of the icon. The inita
   * @param name
   */
  public void setName(String name) {
    addStyleDependentName(name);
  }
}
