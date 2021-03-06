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

package org.kopi.vkopi.lib.visual;

import java.net.URL;
import java.util.Locale;

import org.kopi.vkopi.lib.l10n.LocalizationManager;

/**
 * A special window that display an html help
 */
@SuppressWarnings("serial")
public class VHelpViewer extends VWindow {

  static {
    WindowController.getWindowController().registerWindowBuilder(Constants.MDL_HELP, new WindowBuilder() {

      public UWindow createWindow(VWindow model) {
	return (UWindow)UIFactory.getUIFactory().createView(model);
      }
    });
  }

  /**
   * Construct a new Editor
   */
  public VHelpViewer() {
    setTitle(VlibProperties.getString("help_viewer"));
    setActors(new VActor[] {
      new VActor("File",
                 HELPVIEWER_LOCALIZATION_RESOURCE,
                 "Close",
		 HELPVIEWER_LOCALIZATION_RESOURCE,
                 "quit",
		 java.awt.event.KeyEvent.VK_ESCAPE,
		 0)
/*        new SActor(org.kopi.vkopi.lib.visual.VlibProperties.getString("menu-action"),
		   org.kopi.vkopi.lib.visual.VlibProperties.getString("item-top"),
		   "top",
		   -1,
		   -1,
		 org.kopi.vkopi.lib.visual.VlibProperties.getString("help-top-help")),
        new SActor(org.kopi.vkopi.lib.visual.VlibProperties.getString("menu-action"),
		   org.kopi.vkopi.lib.visual.VlibProperties.getString("item-index"),
		   "index",
		   -1,
		   -1,
		   org.kopi.vkopi.lib.visual.VlibProperties.getString("help-index-help"))
*/
	});

    // localize the help viewer using the default locale
    localize(Locale.getDefault());

    getActor(CMD_QUIT).setNumber(CMD_QUIT);
  }

  public int getType() {
    return Constants.MDL_HELP;
  }

  /**
   * The user want to show an help
   */
  public void showHelp(String surl) {
    if (surl != null) {
      setURL(surl);
      WindowController.getWindowController().doNotModal(this);
    }
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  public void localize(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, ApplicationContext.getDefaultLocale());

    // localizes the actors in VWindow
    super.localizeActors(manager);

    manager = null;
  }

  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------


  /**
   * Performs the appropriate action.
   *
   * @param	actor		the number of the actor.
   * @return	true iff an action was found for the specified number
   */
  public void executeVoidTrigger(int key) {
    switch (key) {
    case CMD_QUIT:
      close(0);
      break;
//     case CMD_TOP:
//       setURL(Application.getDefaults().getHelpURL() + "index.htm");
//       break;
//     case CMD_INDEX:
//       setURL(Application.getDefaults().getHelpURL() + "Index.htm");
//       break;
    }
  }

  /**
   *
   */
  public URL getURL() {
    return url;
  }

  /**
   *
   */
  public void setURL(URL url) {
    this.url = url;
  }

  /**
   *
   */
  public void setURL(String surl) {
    try {
      setURL(url = new URL(surl));
    } catch (java.net.MalformedURLException exc) {
      System.err.println("Bad URL: " + surl);
      url = null;
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private URL			 url;
  private static final String    HELPVIEWER_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/HelpViewer";

  public static final int	CMD_QUIT	= 0;
//   private static final int	CMD_TOP		= 1;
//   private static final int	CMD_INDEX	= 2;
}
