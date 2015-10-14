package com.ledpixelart.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import ioio.lib.api.exception.ConnectionLostException;

public class Scroll_Text extends PIXELConsole {

	 void scrollText(final String scrollingText, final String scrollingTextColor, final int numberLoops, boolean writeMode) 
	    {
		 
		 stopExistingTimer(); 
			
			if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //in write mode, we don't need a timer because we don't need a delay in between frames, we will first put PIXEL in write mode and then send all frames at once
					//pixel.interactiveMode();
					//float textFPS = 1000.f / scrollingTextDelay_;
					//pixel.writeMode(textFPS); //need to tell PIXEL the frames per second to use, how fast to play the animations
					//System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
					System.out.println("Sorry, writing scrolling text is not yet supported..."); 
					/*  int y;
				    	 
				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
				 		
				 			//framestring = "animations/decoded/" + animation_name + ".rgb565";
				 			//System.out.println("Writing to PIXEL: Frame " + y + "of " + GIFnumFrames + " Total Frames");

			    			System.out.println("Writing " + gifFileName_ + " to PIXEL " + "frame " + y);
				 		    pixel.SendPixelDecodedFrame(currentDir, gifFileName_, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
				   	  } //end for loop
*/					//pixel.playLocalMode(); //now tell PIXEL to play locally
					//System.out.println("Writing " + gifFileName_ + " to PIXEL complete, now displaying...");
			}
			else {   //we're not writing so let's just stream
		 
		 
	            stopExistingTimer(); 
	    				   
	    				   ActionListener ScrollingTextTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    			int w = KIND.width * 2;
	    	                    			int h = KIND.height* 2;
	    	                    			
	    	                    			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    	                               
	    	                               //let's set the text color
	    	                               if (scrollingTextColor.equals("red")) {
	    	                            	   textColor = Color.RED;
	    	                               }
	    	                               else if (scrollingTextColor.equals("green")) {
	    	                            	   textColor = Color.GREEN;
	    	                               }
	    	                               else if (scrollingTextColor.equals("blue")) {
	    	                            	   textColor = Color.BLUE;
	    	                               }
	    	                               else if (scrollingTextColor.equals("cyan")) {
	    	                            	   textColor = Color.CYAN;
	    	                               }
	    	                               else if (scrollingTextColor.equals("gray")) {
	    	                            	   textColor = Color.GRAY;
	    	                               }
	    	                               else if (scrollingTextColor.equals("magenta") || scrollingTextColor_.equals("purple")) {
	    	                            	   textColor = Color.MAGENTA;
	    	                               }
	    	                               else if (scrollingTextColor.equals("orange")) {
	    	                            	   textColor = Color.ORANGE;
	    	                               }
	    	                               else if (scrollingTextColor.equals("pink")) {
	    	                            	   textColor = Color.PINK;
	    	                               }
	    	                               else if (scrollingTextColor.equals("yellow")) {
	    	                            	   textColor = Color.YELLOW;
	    	                               }
	    	                   	    
	    	                               Graphics2D g2d = img.createGraphics();
	    	                               g2d.setPaint(textColor);
	    	                               
	    	                              //TO DO we could add different fonts later
	    	                               // Font tr = new Font("TimesRoman", Font.PLAIN, scrollingTextFontSize_);
	    	                               Font tr = new Font("Arial", Font.PLAIN, scrollingTextFontSize_);
	    	                              // Font trb = new Font("TimesRoman", Font.BOLD, scrollingTextFontSize_);
	    	                              // Font tri = new Font("TimesRoman", Font.ITALIC, scrollingTextFontSize_);
	    	                               
	    	                               //String fontFamily = fontFamilyChooser.getSelectedItem().toString();
	    	                               
	    	                              /* Font font = fonts.get(fontFamily);
	    	                               if(font == null)
	    	                               {
	    	                                   font = new Font(fontFamily, Font.PLAIN, 32);
	    	                                   fonts.put(fontFamily, font);
	    	                               }    */        
	    	                               
	    	                               g2d.setFont(tr);
	    	                               
	    	                               String message = null;
	    	                               if (twitterMode && ProxTriggerDone == true) { //have to check the prox trigger as if that's not done, then it means there is an active proximity trigger going on and we want to interrupt the twitter text
	    	                            	   message = twitterResult;
	    	                               }
	    	                               else {
	    	                            	   message = scrollingText;
	    	                               }
	    	                               
	    	                               FontMetrics fm = g2d.getFontMetrics();
	    	                               
	    	                               int y = fm.getHeight();   //30 = 30 * 16/32 = 15  
	    	                              // y = y * KIND.height/32;
	    	                               y = y * KIND.height/(h + fontOffset);	    	                               
	    	                              
	    	                              // System.out.println("font height: " + y);

	    	                               try 
	    	                               {
	    	                                   additionalBackgroundDrawing(g2d);
	    	                               } 
	    	                               catch (Exception ex) 
	    	                               {
	    	                                  // Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                               }
	    	                               
	    	                               if (message ==null) {
	    	                            	   message = ("Sorry, could not retrieve text, please check Internet connection");
	    	                               }
	    	                               
	    	                               g2d.drawString(message, x, y);
	    	                               
	    	                               try 
	    	                               {
	    	                                   additionalForegroundDrawing(g2d);
	    	                               } 
	    	                               catch (Exception ex) 
	    	                               {
	    	                                   //Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                               }
	    	                               
	    	                               g2d.dispose();

	    	                               if(pixel != null)
	    	                               {
	    	                                   try 
	    	                                   {  
	    	                                       pixel.writeImagetoMatrix(img,KIND.width,KIND.height);
	    	                                   } 
	    	                                   catch (ConnectionLostException ex) 
	    	                                   {
	    	                                       //Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    	                                   }                
	    	                               }
	    	                                           
	    	                               int messageWidth = fm.stringWidth(message);   
	    	                              // messageWidth = fm.stringWidth(message);   
	    	                               int resetX = 0 - messageWidth;
	    	                               
	    	                             
	    	                               if(x < resetX)
	    	                               {
	    	                                   x = w;
	    	                                   
	    	                                   loopCounter++;
	   	    	               			    
		   	    	               			   //we have two loop statements here because some existing users are already using --loop for use cases where they need to exit the program so didn't want to screw that up, hence the extra if statement with the numberLoops parameter 
	    	                                   if (loopMode == true && loopCounter >=loopInt) { //then we are done and let's exit out
		   	    	               			    	if (timer != null) timer.stop();
		   	    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
		   	    	               			    	//System.exit(0); //this did not always exit
		   	    	               					loopCounter = 0;
		   	    	               					exit(0,200);
		   	    	               			    }
		   	    	               			    
			   	    	               			if (numberLoops != 0 && loopCounter >= numberLoops) { //then we are done and let's exit out
		   	    	               			    	if (timer != null) timer.stop();
		   	    	               					System.out.println("We've looped " + loopCounter + " times and are now resetting the mode");
		   	    	               					loopCounter = 0;
		   	    	               					ProxTriggerDone = true; //let's reset the prox trigger flag so it can be triggered again
		   	    	               					CheckandRunMode(); //now let's run the original setup and reset to the mode we were originally in per the command line parameters
		   	    	               			    }
	    	                               }
	    	                               else
	    	                               {
	    	                                   x = x - scrollingTextDelay_;
	    	                               }
	                    }
	                };
	    				   
	    				   
	    				   //timer = new Timer(scrollingTextDelay_, ScrollingTextTimer); //the timer calls this function per the interval of fps
	    				   timer = new Timer(scrollingSmoothness_, ScrollingTextTimer);
	    				   timer.start();
	    	}    
	    }
	 
}
