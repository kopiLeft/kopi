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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The welcome screen shared state.
 */
@SuppressWarnings("serial")
public class WelcomeViewState extends AbstractComponentState {

  /**
   * The application locale
   */
  @NoLayout
  public String			locale = "";
  
  /**
   * The supported languages.
   */
  @NoLayout
  public Map<String, String>	languages = new HashMap<String, String>();
  
  /**
   * The logo link
   */
  @NoLayout
  public String			href;
}
