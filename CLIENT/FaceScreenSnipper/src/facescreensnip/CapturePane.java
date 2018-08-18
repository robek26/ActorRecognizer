package facescreensnip;


import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.json.*;
import static facescreensnip.SystemTrayProg.stp;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zion
 */
public class CapturePane extends JPanel {

        private Rectangle selectionBounds;
        private Point clickPoint;
        
        private int mx,my,w,h;
        
        public boolean WAIT = false;
        
       
        private boolean makeinv = false;
       

        public CapturePane() {
            this.setOpaque(false);
            

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        System.exit(0);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    clickPoint = e.getPoint();
                    selectionBounds = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    clickPoint = null;
                    // capture the screen
                    
                    screenCapture(mx, my, w, h);
                    
                    selectionBounds = getVirtualBounds();
                    makeinv = true;
                    repaint();
                    
                    
                    

                    
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point dragPoint = e.getPoint();
                    int x = Math.min(clickPoint.x, dragPoint.x);
                    int y = Math.min(clickPoint.y, dragPoint.y);
                    int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
                    int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
                    mx = x;
                    my = y;
                    w = width;
                    h = height;
                    selectionBounds = new Rectangle(x, y, width, height);
                    
                    repaint();
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        private void sendDataToServer(){
          
                  
            Thread t = new Thread(() -> {
                  
  

                String charset = "UTF-8";
                File ff = new File("");
                String newpath = ff.getAbsolutePath() + File.separator + "tmpimg/unk.png";
                File uploadFile1 = new File(newpath);

                String requestURL = "http://localhost:23000";

                try {
                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                    multipart.addHeaderField("User-Agent", "mozilla");
                    multipart.addHeaderField("Test-Header", "Header-Value");

                   // multipart.addFormField("description", "Cool Pictures");
                   // multipart.addFormField("keywords", "Java,upload,Spring");

                    multipart.addFilePart("fileUpload", uploadFile1);

                    List<String> response = multipart.finish();

                    System.out.println("SERVER REPLIED:");

                    for (String line : response) {
                        System.out.println(line);
                        JSONObject obj = new JSONObject(line);
                        String name = obj.getString("name");
                        String url = obj.getString("url");
                        System.out.println(name);
                        System.out.println(url);
                        String label = "";
                        if(name.equals("Unknown Actor!!!")){
                            label = "<html>Unable to recognize!<br/>Please try with different image...</html>";
                        }
                        else{
                            label = "<html>Face Recognized!<br/> <span style='color:#00ff00;'>"+name+".</span><br/>Opening IMDB page...</html>";
                             if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(new URI(url));
                                } catch (URISyntaxException | IOException ex) {
                                    Logger.getLogger(SystemTrayProg.class.getName()).log(Level.SEVERE, null, ex);
                                }


                            }   
                        }
                        final String lbl = label;
                        
                        
                           
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    //UI code here


                                    JPanel fr = (JPanel)SystemTrayProg.stp.createTransparentWindow2(1,lbl);

                                    Timer t = new Timer(2000, (ActionEvent e) -> {
                                        stp.frame.setVisible(false);
                                        for(int i = 0 ; i < fr.getComponentCount();i++){
                                            fr.remove(i);
                                        }
                                        for(int i = 0 ; i < stp.frame.getContentPane().getComponentCount();i++){
                                            stp.frame.getContentPane().remove(i);
                                        }
                                        
                                        


                                    });
                                    t.setRepeats(false);
                                    t.start();


                                }
                            });
                            
                        
                    }
                    
                    
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            });
            t.start();
        }
        public  Rectangle getVirtualBounds() {
            Rectangle bounds = new Rectangle(0, 0, 0, 0);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice lstGDs[] = ge.getScreenDevices();
            for (GraphicsDevice gd : lstGDs) {
                bounds.add(gd.getDefaultConfiguration().getBounds());
            }
            return bounds;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(255, 255, 255, 150));

            Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
            if (selectionBounds != null) {
                fill.subtract(new Area(selectionBounds));
            }
            g2d.fill(fill);
            if (selectionBounds != null) {
                g2d.setColor(Color.BLACK);
                g2d.draw(selectionBounds);
            }
            g2d.dispose();
            if(makeinv){
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(CapturePane.this);
                topFrame.setVisible(false);
                selectionBounds = null;
                repaint();
                makeinv = false;
            }
           
        }
        

        private void screenCapture(int x, int y, int width,int height){
            Robot robot;
            try {
                robot = new Robot();

                // The hard part is knowing WHERE to capture the screen shot from
                BufferedImage screenShot = robot.createScreenCapture(new Rectangle(x, y, width, height));
                
                try {
                    // Save your screen shot with its label
                    File ff = new File("");
                    String newpath = ff.getAbsolutePath() + File.separator + "tmpimg/unk.png";
                    ImageIO.write(screenShot, "png", new File(newpath));
       
                    
                    sendDataToServer();
                    
                } catch (IOException ex) {
                    Logger.getLogger(SystemTrayProg.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (AWTException ex) {
                Logger.getLogger(SystemTrayProg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }