/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facescreensnip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author zion
 */
public class NotifWindow {
    public  JFrame frame = null;
    public static int TOPRIGHT = 1;
    public NotifWindow(){
        frame = new JFrame("Notif");
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0,100));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setAlwaysOnTop(true);
        frame.add(new JLabel("Hello"));
        Rectangle bounds = getVirtualBounds(NotifWindow.TOPRIGHT);
        frame.setLocation(bounds.getLocation());
        frame.setSize(bounds.getSize());
    }
    
     public  Rectangle getVirtualBounds(int loc) {
         Rectangle bounds = new Rectangle(0, 0, 0, 0);
        
         
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice lstGDs[] = ge.getScreenDevices();
        for (GraphicsDevice gd : lstGDs) {
            bounds.add(gd.getDefaultConfiguration().getBounds());
        }
        
        System.out.println(bounds.x);
        System.out.println(bounds.y);
        System.out.println(bounds.width);
        System.out.println(bounds.height);
        if(loc == 1){
            bounds = new Rectangle(bounds.width - 300, 20, 290, 100);
        }
        
        
        return bounds;
    }
     
     public void displayWindow(){
          frame.setVisible(true);
           Timer t = new Timer(5000, new ActionListener(){
              @Override
              public void actionPerformed(ActionEvent e) {
                  frame.dispose();
                  frame.removeAll();
                  frame = null;
              }
               
           });    // Timer in 10 seconds
           t.start();
     }
     
     
}
