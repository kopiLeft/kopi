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

package org.kopi.vkopi.lib.ui.swing.visual;

import java.awt.Component;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.vkopi.lib.print.PrintManager;
import org.kopi.vkopi.lib.visual.Application;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.FileHandler;
import org.kopi.vkopi.lib.visual.ImageHandler;
import org.kopi.vkopi.lib.visual.KopiExecutable;
import org.kopi.vkopi.lib.visual.ModelCloseListener;
import org.kopi.vkopi.lib.visual.Module;
import org.kopi.vkopi.lib.visual.PrinterManager;
import org.kopi.vkopi.lib.visual.PropertyException;
import org.kopi.vkopi.lib.visual.Registry;
import org.kopi.vkopi.lib.visual.UIFactory;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VMenuTree;
import org.kopi.vkopi.lib.visual.VWindow;
import org.kopi.vkopi.lib.visual.VerifyConfiguration;
import org.kopi.vkopi.lib.visual.WindowController;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.Query;

/**
 * {@code JApplication} is a swing implementation of a kopi application.
 */
public abstract class JApplication implements Application {

  // ---------------------------------------------------------------------
  // CONSTRUCTOR
  // ---------------------------------------------------------------------

  public JApplication(Registry registry) {
    JApplication.instance = this;
    this.registry = registry;
  }

  // ---------------------------------------------------------------------
  // STATIC ACCESSORS
  // ---------------------------------------------------------------------

  /**
   * Returns the application current instance.
   * @return The application current instance.
   */
  /*package*/ static Application getInstance() {
    return instance;
  }

  /**
   * Returns the application options.
   * @return The application options.
   */
  public static ApplicationOptions getApplicationOptions() {
    return instance != null ? ((JApplication)instance).options : null;
  }

  /**
   * Quits the application
   */
  public static void quit() {
    if (instance != null && instance.allowQuit()) {
      System.exit(0);
    }
  }

  // ---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------

  public void logout() {
    if (allowQuit()) {
      menuTree.getDisplay().closeWindow();
    }
  }
  
  public void startApplication() {
    if (options.form != null) {
      String    form;

      if (options.form.indexOf(".") != -1) {
	form = options.form;
      } else {
	// form name without qualification: qualify with application package
	String  appli = getClass().getName();
	int     index = appli.lastIndexOf('.');

	form = appli.substring(0, index + 1) + options.form;
      }

      try {
	KopiExecutable  module;

	module = Module.startForm(context, form, "initial form");
	if (module instanceof VWindow) {
	  ((VWindow) module).addModelCloseListener(new ModelCloseListener() {

	    
	    public void modelClosed(int type) {
	      exitWithError(type);
	    }
	  });
	} else {
	  exitWithError(1);
	}
      } catch (VException e) {
	e.printStackTrace();
	exitWithError(1);
      }
    } else {
      try {
	String url = getURL();

	menuTree = new VMenuTree(context);
	menuTree.setTitle(getUserName() + "@" + url.substring(url.indexOf("//") + 2));
	menuTree.doNotModal();
      } catch (VException e) {
	e.printStackTrace();
	exitWithError(1);
      }
    }
    removeSplashScreen();
  }
  
  public boolean allowQuit() {
    return true;
  }
  
  public PrintManager getPrintManager() {
    return printManager;
  }
  
  public void setPrintManager(PrintManager printManager) {
    this.printManager = printManager;
  }
  
  public PrinterManager getPrinterManager() {
    return printerManager;
  }
  
  public void setPrinterManager(PrinterManager printerManager) {
    this.printerManager = printerManager;
  }
  
  public ApplicationConfiguration getApplicationConfiguration() {
    return configuration;
  }
  
  public void setApplicationConfiguration(ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

  // ---------------------------------------------------------------------
  // UTILS
  // ---------------------------------------------------------------------

  /**
   * Returns the database URL.
   * @return The database URL.
   */
  public String getURL() {
    return context.getDefaultConnection().getURL();
  }

  /**
   * This methods is called at the beginning
   * you should use it to define {@link Locale}, debugMode...
   */
  public void initialize() {
    if (registry != null) {
      registry.buildDependencies();
    }
  }

  /**
   * Returns application the splash screen.
   * @return application the splash screen.
   */
  protected ImageIcon getSplashScreenImage() {
    return org.kopi.vkopi.lib.ui.swing.base.Utils.getImage("splash.jpg");
  }

  /**
   * Displays the splash screen.
   */
  private void displaySplashScreen() {
    ImageIcon   img = getSplashScreenImage();

    if (img != null) {
      splash = new SplashScreen(img.getImage(), null);
      splash.setVisible(true);
    }
  }

  /**
   * Removes the splash screen from the display.
   */
  private void removeSplashScreen() {
    if (splash != null) {
      splash.setVisible(false);
      splash.dispose();
      splash = null;
    }
  }

  /**
   * Exits on error
   *
   * @param     code    code of the error
   */
  private void exitWithError(int code) {
    removeSplashScreen();
    System.exit(code);
  }

  public String getUserIP() {
    return null;
  }

  // ---------------------------------------------------------------------
  // INITIALISATION
  // ---------------------------------------------------------------------

  /**
   * Runs the application.
   */
  public boolean run(String[] args) {
    try {
      UIManager.setLookAndFeel(new org.kopi.vkopi.lib.ui.swing.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
      System.exit(1);
      return false;
    }

    if (! processCommandLine(args)) {
      return false;
    }

    displaySplashScreen();

    // do customer-sepcific initialisations
    initialize();

    if (! connectToDatabase()) {
      exitWithError(1);
      return false;
    }

     try {
       UIManager.setLookAndFeel(new org.kopi.vkopi.lib.ui.swing.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
     } catch (Exception e) {
       System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
     }
     //if (!DObject.isLookAndFeelInstalled()) {
     //  installLF(defaults.getKopiLFProperties());
     //}

    startApplication();

    return true;
  }

  /**
   * Processes the command line
   */
  private boolean processCommandLine(String[] args) {
    options = new ApplicationOptions();

    if (! options.parseCommandLine(args)) {
      return false;
    }

    if (options.locale == null) {
      System.err.println("Warning: a default locale was not specified!");
      defaultLocale = null;
    } else {
      char[]    chars = options.locale.toCharArray();

      if(chars.length != 5
         || chars[0] < 'a' || chars[0] > 'z'
         || chars[1] < 'a' || chars[1] > 'z'
         || chars[2] != '_'
         || chars[3] < 'A' || chars[3] > 'Z'
         || chars[4] < 'A' || chars[4] > 'Z'
         ) {
        System.err.println("Error: Wrong locale format.");
        options.usage();
        return false;
      } else {
        defaultLocale = new Locale(options.locale.substring(0,2),
                                   options.locale.substring(3,5));
      }
    }

    localizationManager = new LocalizationManager(defaultLocale, Locale.getDefault());
    return true;
  }

  public void verifyConfiguration() {
    VerifyConfiguration       verifyConfiguration = VerifyConfiguration.getVerifyConfiguration();
    try {
      verifyConfiguration.verifyConfiguration(ApplicationContext.getDefaults().getSMTPServer(),
	                                      ApplicationContext.getDefaults().getDebugMailRecipient(),
	                                      ApplicationContext.getDefaults().getApplicationName());
    } catch (PropertyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Connects to the database
   */
  private boolean connectToDatabase() {
    if (options.username != null) {
      try {
        DBContext.registerDriver(options.driver);
        context = new DBContext();
        context.setDefaultConnection(context.createConnection(options.database,
                                                              options.username,
                                                              options.password,
                                                              options.lookupUserId,
                                                              options.schema));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        options.usage();
        context = null;
      }
    }

    if (context == null) {
      //      installLF(defaults.getKopiLFProperties());

      removeSplashScreen();
      context = login(options.database,
                      options.driver,
                      options.username,
                      options.password,
                      options.schema);
      displaySplashScreen();
    }

    if (context != null) {
      Query.setTraceLevel(options.trace);
    }

    return context != null;
  }

  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------

  
  public boolean isNobugReport() {
    return options != null && options.nobugreport;
  }

  
  public Date getStartupTime() {
    return startupTime;
  }

  
  public VMenuTree getMenu() {
    return menuTree;
  }

  
  public void setGeneratingHelp() {
    isGeneratingHelp = true;
  }

  
  public boolean isGeneratingHelp() {
    return isGeneratingHelp;
  }

  
  public DBContext getDBContext() {
    return context;
  }

  
  public String getUserName() {
    return context.getDefaultConnection().getUserName();
  }

  
  public Registry getRegistry() {
    return registry;
  }

  
  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  
  public LocalizationManager getLocalizationManager() {
    return localizationManager;
  }

  
  public void displayError(UComponent parent, String message) {
    DWindow.displayError((Component)parent, message);
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------

  public void notice(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.notice(message);
  }
  
  public void error(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.error(message);
  }
  
  public void warn(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.warn(message);
  }
  
  public int ask(String message, boolean yesIsDefault) {
    return AWR_UNDEF;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static Application           	        instance;

  private ApplicationOptions       		options;
  private VMenuTree                      	menuTree;
  private DBContext                     	context;
  private boolean                       	isGeneratingHelp;
  private SplashScreen                  	splash;
  private Registry                      	registry;
  private Locale                        	defaultLocale;
  private LocalizationManager           	localizationManager;
  private PrintManager                          printManager;
  private PrinterManager                        printerManager;
  private ApplicationConfiguration              configuration;

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  private final Date             		startupTime = new Date(); // remembers the startup time

  static {
    ApplicationContext.setApplicationContext(new JApplicationContext());
    FileHandler.setFileHandler(new JFileHandler());
    ImageHandler.setImageHandler(new JImageHandler());
    WindowController.setWindowController(new JWindowController());
    UIFactory.setUIFactory(new JUIFactory());
  }
}
