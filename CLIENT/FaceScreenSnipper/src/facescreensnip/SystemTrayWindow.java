/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facescreensnip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author zion
 */
class SystemTrayWindow{
    public  JFrame frame = null;
    public CapturePane cp = null;
    AbstractBorder brdrRight = new TextBubbleBorder(new Color(0,0,0,200),1,16,0);
    public SystemTrayWindow(){
        frame = new JFrame("main window");
        frame.setUndecorated(true);
        frame.setType(javax.swing.JFrame.Type.UTILITY);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setAlwaysOnTop(true);
       
    }
    public  Rectangle getVirtualBounds(int b) {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice lstGDs[] = ge.getScreenDevices();
        
        for (GraphicsDevice gd : lstGDs) {
            bounds.add(gd.getDefaultConfiguration().getBounds());
        }
        
        if(b == 1){
            bounds = new Rectangle(bounds.width - 300, 30, 290, 70);
        }
        
        return bounds;
    }
     public  Object createTransparentWindow2(int tpe,String label){
         
         JPanel jp = null;
          if(tpe == 0){
              if(jp != null){
                  for(int i = 0 ; i < jp.getComponentCount();i++){
                        jp.remove(i);
                  }
              }
              for(int i = 0 ; i < frame.getContentPane().getComponentCount();i++){
                  frame.getContentPane().remove(i);
              }
              frame.invalidate();
              frame.validate();
              frame.setBackground(new Color(0, 0, 0, 0));
              Rectangle bounds = getVirtualBounds(0);
              frame.setLocation(bounds.getLocation());
              frame.setSize(bounds.getSize());
              frame.add(new CapturePane());

          }
          else{
              //new JLabel("Face Recognized! Opening IMDB page..."));
              Rectangle bounds = getVirtualBounds(1);
              frame.setLocation(bounds.getLocation());
              frame.setSize(bounds.getSize());
              frame.setBackground(new Color(0, 0, 0,0));
              
              jp = new JPanel();
              jp.setLayout(null);
              jp.setBackground(new Color(0, 0, 0,230));
              JLabel jlb = new JLabel(label);
              Dimension sizejlb = jlb.getPreferredSize();
              jlb.setBounds(70, 10, sizejlb.width, sizejlb.height);
              jlb.setForeground(Color.white);
              
              JLabel thumb = new JLabel();
              File currdir = new File("");
              String iconpath = currdir.getAbsolutePath() + File.separator + "icon2.png"; 
              ImageIcon iconLogo = new ImageIcon(iconpath);
              Image image = iconLogo.getImage(); // transform it 
              Image newimg = image.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
              iconLogo = new ImageIcon(newimg);  // transform it back
              thumb.setIcon(iconLogo);
              Dimension size = thumb.getPreferredSize();
              thumb.setBounds(10, 10, size.width, size.height);
              
              jp.add(thumb);
              jp.add(jlb);
              frame.add(jp);

              
          }
            
        frame.setVisible(true);

        return jp;
         
        
    }
   
  }
