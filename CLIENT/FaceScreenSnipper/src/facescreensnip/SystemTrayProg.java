/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facescreensnip;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;





/**
 *
 * @author zion
 */
public class SystemTrayProg {
    
       public static SystemTrayWindow stp;
    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        // makes a factory with the built-in clean theme
       
        
    File currdir = new File("");
    String iconpath = currdir.getAbsolutePath() + File.separator + "icon2.png";
    
    stp = new SystemTrayWindow();
    TrayIcon trayIcon = null;
    Image image = Toolkit.getDefaultToolkit().getImage(iconpath);
    Image updatedImage = Toolkit.getDefaultToolkit().getImage(iconpath);
    
if (SystemTray.isSupported()) {
    // get the SystemTray instance
    System.out.println("SYSTEM TRAY SUPPORTED");
    SystemTray tray = SystemTray.getSystemTray();
    // load an image
   
    // create a action listener to listen for default action executed on the tray icon
    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // execute default action of the application
            // ...
            System.out.println("ITEM CLICKED");
            
            stp.createTransparentWindow2(0,"");
            
        }
    };
    // create a popup menu
    PopupMenu popup = new PopupMenu();
    // create menu item for the default action
    MenuItem defaultItem = new MenuItem("Snip Screen");
    defaultItem.addActionListener(listener);
    popup.add(defaultItem);
    /// ... add other items
    // construct a TrayIcon
    trayIcon = new TrayIcon(image, "Tray", popup);
    // set the TrayIcon properties
    trayIcon.addActionListener(listener);
    // ...
    // add the tray image
    try {
        tray.add(trayIcon);
    } catch (AWTException e) {
        System.err.println(e);
    }
    
           
} else {
    // disable tray option in your application or
    // perform other actions
    
}
// ...
// some time later
// the application state has changed - update the image
if (trayIcon != null) {
    trayIcon.setImage(updatedImage);
}









        
}
    
    

    

    

    
}

  
  