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

import java.util.Date;
import java.util.Locale;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.vkopi.lib.print.PrintManager;
import org.kopi.xkopi.lib.base.DBContext;

/**
 * {@code Application} is the top level interface for all kopi applications.
 * The {@code Application} should give a way how to login to the application.
 */
public interface Application extends MessageListener {

  /**
   * Logins to the application.
   * @param database The database URL.
   * @param driver The database driver.
   * @param username The user name.
   * @param password The user password.
   * @param schema The database schema.
   * @return The {@link DBContext} containing database connection information.
   */
  public DBContext login(String database, String driver, String username, String password, String schema);

  /**
   * Signs out from the application.
   */
  public void logout();
  
  /**
   * Starts the application.
   */
  public void startApplication();

  /**
   * Returns {@code true} if an application quit is allowed.
   * @return {@code true} if an application quit is allowed.
   */
  public boolean allowQuit();

  /**
   * Returns {@code true} if no bug report is sent.
   * @return {@code true if no bug report is sent.
   */
  public boolean isNobugReport();

  /**
   * Returns the start up time.
   * @return The start up time.
   */
  public Date getStartupTime();

  /**
   * Returns the application menu.
   * @return The application menu.
   */
  public VMenuTree getMenu();

  /**
   * Sets the application in help generating mode.
   */
  public void setGeneratingHelp();

  /**
   * Returns {@code true} if the application in help generating  mode.
   * @return {@code true} if the application in help generating  mode.
   */
  public boolean isGeneratingHelp();

  /**
   * Returns the {@link DBContext} containing user connection information.
   * @return The {@link DBContext} containing user connection information.
   */
  public DBContext getDBContext();

  /**
   * Returns the connected user name.
   * @return The connected user name.
   */
  public String getUserName();
  
  /**
   * Returns the connected user IP address.
   * @return The connected user IP address.
   */
  public String getUserIP();

  /**
   * Returns the application {@link Registry}.
   * @return The application {@link Registry}.
   */
  public Registry getRegistry();

  /**
   * Returns the application default {@link Locale}.
   * @return The application default {@link Locale}.
   */
  public Locale getDefaultLocale();

  /**
   * Returns the application {@link LocalizationManager}.
   * @return The application {@link LocalizationManager}.
   */
  public LocalizationManager getLocalizationManager();

  /**
   * Displays a message box when we are not in a model context.
   * @param parent The parent component.
   * @param message The message to be displayed.
   */
  public void displayError(UComponent parent, String message);
  
  /**
   * Returns the print manager of the application instance.
   * @return The print manager of the application instance.
   */
  public PrintManager getPrintManager();
  
  /**
   * Sets the print manager to the application.
   * @param printManager The print manager instance.
   */
  public void setPrintManager(PrintManager printManager);
  
  /**
   * Returns the printer manger of the application.
   * @return The printer manger of the application.
   */
  public PrinterManager getPrinterManager();
  
  /**
   * Sets the printer manager of the application instance.
   * @param printerManager The printer manager.
   */
  public void setPrinterManager(PrinterManager printerManager);
  
  /**
   * Returns the application configuration instance.
   * @return The application configuration instance.
   */
  public ApplicationConfiguration getApplicationConfiguration();
  
  /**
   * Sets the application configuration.
   * @param configuration The application configuration instance.
   */
  public void setApplicationConfiguration(ApplicationConfiguration configuration);
}
