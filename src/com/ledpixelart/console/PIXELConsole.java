package com.ledpixelart.console;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.onebeartoe.pixel.hardware.Pixel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.ledpixelart.pc.PixelApp;

/******** Pseudo code for this app ***************
Get the current matrix type from preferences or the command line
Get the gif filename that the user has selected
A. Check if the GIf has already been decoded
B. If it was already decoded, does the current matrix resolution match the decoded resolution
If A or B is no, then we need to decode the GIF
Now we can animate the GIF in a loop
We will make calls to the PIXEL class
*/

public class PIXELConsole extends IOIOConsoleApp {
@SuppressWarnings("deprecation")
//public class PIXELConsole  {
	private boolean ledOn_ = false;

	private static IOIO ioiO; 
	 
	private static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	 
	private static int weatherCode;
	 
	private static String weatherCondition;
     
	public static final Pixel pixel = new Pixel(KIND);
	
	private static int selectedFileResolution = 2048; 
	 
	public static String pixelFirmware = "Not Found";
	 
	public static String pixelHardwareID = "";
	    
	private static VersionType v;
	 
	private static int i;
	
	private static int u;
	
	private static int z = 0;
	    
    private static int numFrames = 0;
    
    private static String animation_name;
    
    private volatile static Timer timer;
    
    private static ActionListener AnimateTimer;
    
    private static ActionListener exitTimer_;
    
    private static String selectedFileName;
    
  	private static String decodedDirPath;
  	
  	private static byte[] BitmapBytes;
  	
  	private static short[] frame_;
  	
  	private static String framestring;
  	
  	//private static float fps = 0;

	private static Command command_;
	
	private static String zip_ = "";
	
	private static String gifFileName_ = "";
	
	private static String woeid_ = "";
	
	private static int zipInt_;
	
	private static int woeidInt_;
	
	private static boolean zipMode;
	
	private static boolean reportTomorrowWeather = false;
	
	private static boolean validCommandLine = false;
	
	private static boolean writeMode = false;
	
	private static String loopString;
	
	private static int loopInt;
	
	private static boolean loopMode;
	
	private static int loopCounter;
	
	private static boolean gifModeInternal = false;
	
	private static boolean gifModeExternal = false;
	
	private static boolean weatherMode = false;
	
	private static HttpGet getRequest;
	
	private static HttpResponse httpResponse;
	
	private static HttpEntity entity;
	
	private static NodeList nodi;
	
	private static InputStream inputXml = null;
	
    private static int selectedFileTotalFrames;
    
    private static int selectedFileDelay;
    
    private static float GIFfps;
    
    private static int GIFnumFrames;
    
    private static int GIFselectedFileDelay;
    
    private static int GIFresolution;
    
    private static String currentDir;

    private static int matrix_model;
    
    private static int frame_length;
    
    private static int currentResolution;
    
    private static int ledMatrixType = 3; //we'll default to PIXEL 32x32 and change this is a command line option is entered specifying otherwise
    
    private static int x;
    
    private static int scrollingTextDelay_ = 6;
    
    private static String scrollingText_;
    
    private static boolean scrollingTextMode = false;
    
    private static String scrollingTextSpeed_;
    
    private static int scrollingTextFontSize_ = 30; //default the scrolling text font to 30
    
    private static String scrollingTextFontSizeString;
    
    private static String scrollingTextColor_ = "red";
    
    private static Color textColor;
    
	private static enum Command {
		VERSIONS, FINGERPRINT, WRITE
	}
  	
  	private static void printUsage() {
		System.out.println("*** PIXEL: Console Version 1.4 ***");
		System.out.println();
		System.out.println("Usage:");
		System.out.println("pixelc <options>");
		System.out.println();
		System.out.println("Valid options are:");
		System.out.println("GIF MODE");
		System.out.println();
		System.out
		.println("--gif=your_filename.gif  Send this gif to PIXEL");
		System.out
		.println("--loop=number  How many times to loop the GIF before exiting, omit this parameter to loop indefinitely");
		System.out
		.println("--write  Puts PIXEL into write mode, default is streaming mode");
		System.out
		.println("--superpixel  change LED matrix to SUPER PIXEL 64x64");
		System.out
		.println("--16x32  change LED matrix to Adafruit's 16x32 LED matrix");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --gif=tree.gif");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 ~/pixel/pixelc.jar --gif=~/pixel/tree.gif");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 /Users/home/joe/pixelc.jar --gif=/Users/home/joe/tree.gif");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --gif=tree.gif --loop=10");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --gif=tree.gif --superpixel --write");
		
		System.out.println("\n");
		System.out.println("SCROLLING TEXT MODE");
		System.out.println();
		System.out
		.println("--text=\"your scrolling text\"    Make sure to enclose your text in double quotes");
		System.out
		.println("--speed=<number>  How fast to scroll, default value is 6. Higher is faster.");
		System.out
		.println("--fontsize=<number>  Default size is 30");
		System.out
		.println("--loop=number  How many times to loop the scrolling text before exiting, omit this parameter to loop indefinitely");
		System.out
		.println("--color=<color>    Supported colors are red, green, blue, cyan, gray, magenta, orange, pink, and yellow"); 
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --text=\"hello world\" --speed=10 --fontsize=36 --color=orange");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --text=\"hello world\" --speed=10 --fontsize=36 --color=orange --loop=1");
		
		System.out.println("\n");
		System.out.println("WEATHER MODE");
		System.out
				.println("--zip=your_zip_code Non-US users should use woeid");
		System.out
				.println("--woeid=your_woeid_code A numeric number that Yahoo uses\n to designate your location");
		System.out
		.println("--loop=number  How many times to loop the weather GIF before exiting, omit this parameter to loop indefinitely");
		System.out
				.println("--forecast Displays tomorrow's weather conditions, defaults \nto current weather conditions if not specified");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --zip=95050 --loop=10");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --zip=95050 --write");
		
		System.out.println("\n");
		System.out.println("Omitting -Dioio.SerialPorts=<Port of PIXEL> may still work\n" +
				"but will take longer for your computer to scan all ports to find PIXEL");
		System.out.println("<Port of PIXEL> examples:\n" +
				"\n" +
			"Windows: COMX\n" +
			"Mac: tty.usbmodem141X where X is typically 1 or 2\n" +
			"Raspberry Pi: Omit -Dioio.SerialPorts=<Port of PIXEL> \n" +
			"Linux & BeagleBone: IOIOX or /dev/ttyACM0 where X is typically 0 or 1");
		System.out.println("\n");
		System.out.println("See http://ledpixelart.com/raspberry-pi/ for Raspberry Pi setup instructions");
		System.out.println("Type q to quite this program");
		//need an option to display all possible gif names
		
	}

	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
		public static void main(String[] args) throws Exception {
			
			//System.out.println("Working Directory = " + System.getProperty("user.dir"));
			
			currentDir = System.getProperty("user.dir");
			
			if (args.length == 0) {
				printUsage();
				System.exit(1);
			}
			
			try {
				parseArgs(args);
			} catch (BadArgumentsException e) {
				System.err.println("Invalid command line.");
				System.err.println(e.getMessage());
				printUsage();
				System.exit(2);
			}
			
			new PIXELConsole().go(args);
		}
		
		private static void parseArgs(String[] args) throws BadArgumentsException {
			int nonOptionArgNum = 0;
			for (String arg : args) {
				if (arg.startsWith("--")) {
					parseOption(arg);
				} else {
					if (nonOptionArgNum == 0) {
						parseCommand(arg);
					}
				}
			}
		}

		private static void parseCommand(String arg) throws BadArgumentsException {
			try {
				command_ = Command.valueOf(arg.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new BadArgumentsException("Unrecognized command: " + arg);
			}
		}

		private static void parseOption(String arg) throws BadArgumentsException {
			
			if (arg.startsWith("--forecast")) {
				reportTomorrowWeather = true;
				System.out.println("Displaying the current weather conditions, use --forecast if you want tomorrow's forecast.\n");
			}
			
			if (arg.startsWith("--gifp=")) { //not using this one
				gifFileName_ = arg.substring(7);
				System.out.println("gif file name: " + gifFileName_);
				gifModeInternal = true;
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--gif=")) {
				gifFileName_ = arg.substring(6);
				//System.out.println("GIF file name: " + gifFileName_);
				gifModeExternal = true;
				validCommandLine = true;
				z++;
				
				File GIFfile = new File(currentDir + "/" + gifFileName_); 
				if (GIFfile.exists() && !GIFfile.isDirectory()) { 
					currentDir = System.getProperty("user.dir");
					System.out.println("GIF found using direct path, file name is: " + gifFileName_);
			    }
				
				else {
					
					String jarPath = PIXELConsole.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					try {
						String decodedPath = URLDecoder.decode(jarPath, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String jarDirpath = jarPath.
						    substring(0,jarPath.lastIndexOf(File.separator));
					
					currentDir = jarDirpath;
					System.out.println("Working Path is: " + currentDir);
					
					if (gifFileName_.contains("~")) { //~ shortcut for home directory
						
						gifFileName_ = gifFileName_.substring(gifFileName_.lastIndexOf(File.separator) + 1).trim();
						System.out.println("GIF found using home alias, file name is: " + gifFileName_);
						
					}
					
					else {
					
						File GIFfileAbsolute = new File(gifFileName_);
						if (GIFfileAbsolute.exists() && !GIFfileAbsolute.isDirectory()) { 
							
							//String path = gifFileName_.
							  //  substring(0,gifFileName_.lastIndexOf(File.separator));
							
							gifFileName_ = GIFfileAbsolute.getName();
							System.out.println("GIF found using absolute path, file name is: " + GIFfileAbsolute.getName());
					    }
						else {
							System.out.println("GIF not found, please check the spelling and/or path, exiting now...");
							exit(1,200);
						}
					}
				}
			}	
			
			if (arg.startsWith("--loop=")) {
				loopString = arg.substring(7);
				System.out.println("Number of times to loop = " + loopString);
				loopMode = true;
				loopInt =  Integer.parseInt(loopString);
			}
			
			if (arg.startsWith("--text=")) {
				scrollingText_ = arg.substring(7);
				System.out.println("Scrolling Text Mode Selected");
				scrollingTextMode = true;
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--speed=")) {
				scrollingTextSpeed_ = arg.substring(8);
				//System.out.println("scrolling text speed: " + scrollingTextSpeed_);
				scrollingTextDelay_ = Integer.parseInt(scrollingTextSpeed_);
			}	
			
			if (arg.startsWith("--fontsize=")) {
				scrollingTextFontSizeString = arg.substring(11);
				//System.out.println("font size is: " + scrollingTextFontSizeString);
				scrollingTextFontSize_ = Integer.parseInt(scrollingTextFontSizeString);
			}	
			
			if (arg.startsWith("--color=")) {
				scrollingTextColor_ = arg.substring(8);
				//System.out.println("scrolling text color is: " + scrollingTextFontSizeString);
			}	
			
			if (arg.startsWith("--image=")) {
				System.out.println("image file name: " + gifFileName_);
				gifModeExternal = true;
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--write")) {
				writeMode = true;
				System.out.println("PIXEL is in write mode\n");
			}
			
			if (arg.startsWith("--16x32")) {
				ledMatrixType = 1;
				System.out.println("16x32 LED matrix has been selected");
			}
			
			if (arg.startsWith("--superpixel")) {
				ledMatrixType = 10;
				System.out.println("SUPER PIXEL selected");
			}
			
			if (arg.startsWith("--zip=")) {
				zip_ = arg.substring(6);
				System.out.println("zip code: " + zip_);
				validCommandLine = true;
				zipMode = true;
				weatherMode = true;
				z++;
			} else if (arg.startsWith("--woeid")) {
				woeid_ = arg.substring(8);
				System.out.println("woeid: " + woeid_);
				validCommandLine = true;
				zipMode = false;
				weatherMode = true;
				z++;
			}
			
			if (validCommandLine == false) {
				throw new BadArgumentsException("Unexpected option: " + arg);
			}
		}
		
		private static class BadArgumentsException extends Exception {
			private static final long serialVersionUID = -5730905669013719779L;

			public BadArgumentsException(String message) {
				super(message);
			}
		}
	

	protected void run(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		boolean abort = false;
		String line;
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("t")) {
				//ledOn_ = !ledOn_;
			} else if (line.equals("n")) {
				//ledOn_ = true;
			} else if (line.equals("f")) {
				//ledOn_ = false;
			} else if (line.equals("q")) {
				abort = true;
				System.exit(1);
			} else {
				//System.out.println("Unknown input. t=toggle, n=on, f=off, q=quit.");
				
				System.out
				.println("Unknown input. q=quit.");
			}
		}
	
	}
	
	
	
	
	 private static void streamGIF(boolean writeMode) 
	    {
		
		if (pixel.GIFNeedsDecoding(currentDir, gifFileName_, currentResolution) == true) {    //resolution can be 16, 32, 64, 128 (String CurrentDir, String GIFName, int currentResolution)
			System.out.println("Decoding " + gifFileName_);
			pixel.decodeGIF(currentDir, gifFileName_, currentResolution,KIND.width,KIND.height);
			
		}
		else {
			System.out.println(gifFileName_ + " is already decoded, skipping decoding step");
		}
		
		GIFfps = pixel.getDecodedfps(currentDir, gifFileName_); //get the fps
	    GIFnumFrames = pixel.getDecodednumFrames(currentDir, gifFileName_);
	    GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, gifFileName_);  
	    GIFresolution = pixel.getDecodedresolution(currentDir, gifFileName_);
	    
	    System.out.println(gifFileName_ + " contains " + GIFnumFrames + " total frames, a " + GIFselectedFileDelay + "ms frame delay or " + GIFfps + " frames per second and a resolution of " + GIFresolution);
		
		//****** Now let's setup the animation ******
		    
		   // animation_name = selectedFileName;
		    i = 0;
		   // numFrames = selectedFileTotalFrames;
		    
		           
	            stopExistingTimer(); //is this needed, probably not
	    			
	    			if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //in write mode, we don't need a timer because we don't need a delay in between frames, we will first put PIXEL in write mode and then send all frames at once
	    					pixel.interactiveMode();
	    					//send loading image
	    					pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
	    					System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
	    					  int y;
	    				    	 
	    				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
	    				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
	    				 		
	    				 			//framestring = "animations/decoded/" + animation_name + ".rgb565";
	    				 			//System.out.println("Writing to PIXEL: Frame " + y + "of " + GIFnumFrames + " Total Frames");

	    			    			System.out.println("Writing " + gifFileName_ + " to PIXEL " + "frame " + y);
	    				 		    pixel.SendPixelDecodedFrame(currentDir, gifFileName_, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
	    				   	  } //end for loop
	    					pixel.playLocalMode(); //now tell PIXEL to play locally
	    					System.out.println("Writing " + gifFileName_ + " to PIXEL complete, now displaying...");
	    					//System.exit(0);
	    					exit(0,200);
	    			}
	    			else {   //we're not writing so let's just stream
	            
	            stopExistingTimer(); //is this needed, probably no
	    				   
	    				   ActionListener AnimateTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    
	    	               		 	i++;
	    	               			
	    	               			if (i >= GIFnumFrames - 1) 
	    	               			{
	    	               			    i = 0;
	    	               			    loopCounter++;
	    	               			    
	    	               			    if (loopMode == true && loopCounter >=loopInt) { //then we are done and let's exit out
	    	               			    	if (timer != null) timer.stop();
	    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
	    	               			    	//System.exit(0);
	    	               					exit(0,200);
	    	               			    }
	    	               			}
	    	               		 
	    	               		
	    	               		 pixel.SendPixelDecodedFrame(currentDir, gifFileName_, i, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
 	                    }
 	                };
	    				   
	    				   
	    				   timer = new Timer(GIFselectedFileDelay, AnimateTimer); //the timer calls this function per the interval of fps
	    				   timer.start();
	    			}    
		
	      }
	 
	 private static void scrollText(final String scrollingText, boolean writeMode) 
	    {
		 
		 stopExistingTimer(); //is this needed, probably not
			
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
		 
		 
	            stopExistingTimer(); //is this needed, probably no
	    				   
	    				   ActionListener ScrollingTextTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    			int w = KIND.width * 2;
	    	                    			int h = KIND.height* 2;
	    	                   	    
	    	                               BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    	                               
	    	                               //let's set the text color
	    	                               if (scrollingTextColor_.equals("red")) {
	    	                            	   textColor = Color.RED;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("green")) {
	    	                            	   textColor = Color.GREEN;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("blue")) {
	    	                            	   textColor = Color.BLUE;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("cyan")) {
	    	                            	   textColor = Color.CYAN;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("gray")) {
	    	                            	   textColor = Color.GRAY;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("magenta") || scrollingTextColor_.equals("purple")) {
	    	                            	   textColor = Color.MAGENTA;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("orange")) {
	    	                            	   textColor = Color.ORANGE;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("pink")) {
	    	                            	   textColor = Color.PINK;
	    	                               }
	    	                               else if (scrollingTextColor_.equals("yellow")) {
	    	                            	   textColor = Color.YELLOW;
	    	                               }
	    	                   	    
	    	                               Graphics2D g2d = img.createGraphics();
	    	                               g2d.setPaint(textColor);
	    	                               
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
	    	                               
	    	                              String message = scrollingText;
	    	                               //String message = "hard code test";
	    	                               
	    	                               FontMetrics fm = g2d.getFontMetrics();
	    	                               
	    	                               int y = fm.getHeight();   //30 = 30 * 16/32 = 15  
	    	                               y = y * KIND.height/32;
	    	                              // System.out.println("font height: " + y);

	    	                               try 
	    	                               {
	    	                                   additionalBackgroundDrawing(g2d);
	    	                               } 
	    	                               catch (Exception ex) 
	    	                               {
	    	                                  // Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
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
	    	                               int resetX = 0 - messageWidth;
	    	                               
	    	                             
	    	                               if(x < resetX)
	    	                               {
	    	                                   x = w;
	    	                                   
	    	                                   loopCounter++;
	   	    	               			    
		   	    	               			    if (loopMode == true && loopCounter >=loopInt) { //then we are done and let's exit out
		   	    	               			    	if (timer != null) timer.stop();
		   	    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
		   	    	               			    	//System.exit(0);
		   	    	               					exit(0,200);
		   	    	               			    }
	    	                                   
	    	                                   
	    	                               }
	    	                               else
	    	                               {
	    	                                   
	    	                                   x = x - scrollingTextDelay_;
	    	                               }
	                    }
	                };
	    				   
	    				   
	    				   //timer = new Timer(scrollingTextDelay_, ScrollingTextTimer); //the timer calls this function per the interval of fps
	    				   timer = new Timer(100, ScrollingTextTimer);
	    				   timer.start();
	    	}    
	    }
	      
	 
	 /**
	     * Override this to perform any additional background drawing on the image that get sent to the PIXEL
	     * @param g2d 
	     */
	    protected static void additionalBackgroundDrawing(Graphics2D g2d) throws Exception
	    {
	        
	    }    
	    
	    /**
	     * Override this to perform any additional foreground drawing on the image that get sent to the PIXEL
	     * @param g2d 
	     */
	    protected static void additionalForegroundDrawing(Graphics2D g2d) throws Exception
	    {
	        
	    }
	
	
	
	 private static void runWeatherAnimations() 
	    {

		String selectedFileName = weatherCondition;
		String decodedDirPath = "animations/decoded";
		String imagePath = decodedDirPath; //animations/decoded/rainx.rgb565

		String path = decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"; //animations/decoded/rain/rain.txt , this file tells us fps
		System.out.println("weather path: " + path);

		InputStream decodedFile = PIXELConsole.class.getClassLoader().getResourceAsStream(path);
		//note can't use file operator here as you can't reference files from a jar file

		if (decodedFile != null) 
		{
		    // ok good, now let's read it, we need to get the total numbers of frames and the frame speed
		    String line = "";

		    try 
		    {
				InputStreamReader streamReader = new InputStreamReader(decodedFile);
				BufferedReader br = new BufferedReader(streamReader);
				line = br.readLine();
		    } 
		    catch (IOException e) 
		    {
			    //You'll need to add proper error handling here
		    }

		    String fileAttribs = line.toString();  //now convert to a string	 
		    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
		    String[] fileAttribs2 = fileAttribs.split(fdelim);
		    int selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());
		    int selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());	
		    
		    if (selectedFileDelay != 0) {  //then we're doing the FPS override which the user selected from settings
	    		GIFfps = 1000.f / selectedFileDelay;
			} else { 
	    		GIFfps = 0;
	    	}
		    
		    //****** Now let's setup the animation ******

		    animation_name = selectedFileName;
		    u = 0;
		    numFrames = selectedFileTotalFrames;

	            stopExistingTimer(); //is this needed, probably not

	    			if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {
	    					pixel.interactiveMode();
	    					//send loading image
	    					pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
	    					sendFramesToPIXELWeather(); //send all the frame to PIXEL
	    					pixel.playLocalMode(); //now tell PIXEL to play locally
	    			}
	    			else {
	    				   stopExistingTimer();

	    				   ActionListener AnimateTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {

	    	               		 	u++;

	    	               			if (u >= numFrames - 1) 
	    	               			{
	    	               			    u = 0;
	    	               			    
	    	               			    loopCounter++;
	    	               			    
	    	               			    if (loopMode == true && loopCounter >=loopInt) { //then we are done and let's exit out
	    	               			    	if (timer != null) timer.stop();
	    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
	    	               			    	//System.exit(0);
	    	               					exit(0,200);
	    	               			    }
	    	               			}

	    	               		String framestring = "animations/decoded/" + animation_name + "/" + animation_name + u + ".rgb565";

	    	               		System.out.println("framestring: " + framestring);

	    	               		try 
	    	               		{
	    	               		    pixel.loadRGB565Weather(framestring);
	    	               		} 
	    	               		catch (ConnectionLostException e1) 
	    	               		{
	    	               		    // TODO Auto-generated catch block
	    	               		    e1.printStackTrace();
	    	               		}

 	                    }
 	                };


	    				   timer = new Timer(selectedFileDelay, AnimateTimer); //the timer calls this function per the interval of fps
	    				   timer.start();
	    				   System.out.println("file delay: " + selectedFileDelay);
	    			}    
			}
			else {
				System.err.println("ERROR:  Could not find decoded file. If you are a developer, make sure that you included\n the resources folder in the class folder in Eclipse");
			}
	    }
	
	 
	 private static void sendFramesToPIXEL() { 
   	  int y;
    	 
   	  for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
 		
 		//framestring = "animations/decoded/" + animation_name + "/" + animation_name + y + ".rgb565";
 		framestring = "animations/decoded/" + animation_name + ".rgb565";
 		
 			System.out.println("writing to PIXEL frame: " + framestring);

 		//  pixel.loadRGB565(framestring);
 		   pixel.SendPixelDecodedFrame(currentDir, gifFileName_, i, numFrames, selectedFileResolution,KIND.width,KIND.height);
   	  } //end for loop
     	 
     }
	 
	 private static void sendFramesToPIXELWeather() { 
	   	  int y;
	    	 
	   	  for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
	 		
	 		framestring = "animations/decoded/" + animation_name + "/" + animation_name + y + ".rgb565";
	 		
	 			System.out.println("writing to PIXEL frame: " + framestring);

	 		try 
	 		{
	 		    pixel.loadRGB565Weather(framestring);
	 		} 
	 		catch (ConnectionLostException e1) 
	 		{
	 		    // TODO Auto-generated catch block
	 		    e1.printStackTrace();
	 		}
	   	  } //end for loop
	     	 
	     }
	    
	    private static void stopExistingTimer()
	    {
	        if(timer != null && timer.isRunning() )
	        {
	            //System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
	            timer.stop();
	        }        
	    }
	 
	 private static void setupEnvironment() {
		 
		 switch (ledMatrixType) { 
		     case 0:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 1:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 2:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //an early version of the PIXEL LED panels, only used in a few early prototypes
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 3:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //the current version of PIXEL 32x32
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 4:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x32;
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;
		     case 5:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x64; 
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 
		     case 6:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_2_MIRRORED; 
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 	 
		     case 7:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		     case 8:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_128x32; //horizontal
		    	 frame_length = 8192;
		    	 currentResolution = 128;  
		    	 break;	 
		     case 9:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x128; //vertical mount
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 
		     case 10:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x64;
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 	 		 
		     default:	    		 
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
		    	 frame_length = 2048;
		    	 currentResolution = 32;
	     }
		 
		 System.out.println("CurrentResolution is: " + currentResolution + "\n");
	 }
	 
	 private static void getWeather() {
			
		    inputXml = null;
		    try
		    {

	           if (zipMode == true) {
	 	    	//	 zipInt_ = Integer.parseInt(zip_);
	 	    		 //System.out.println("zip code mode is: " + zipInt_); 
	 	    		 inputXml  = new URL("http://weather.yahooapis.com/forecastrss?p=" + zip_ + "&u=f").openConnection().getInputStream();
	 	    		// System.out.println("http://weather.yahooapis.com/forecastrss?p=" + zip_ + "&u=f"); 
	 		     }
	 		      else {
	 		    	  woeidInt_ = Integer.parseInt(woeid_);
	 		    	  inputXml  = new URL("http://weather.yahooapis.com/forecastrss?p=" + woeid_ + "&u=f").openConnection().getInputStream();
	 		      }

		       DocumentBuilderFactory factory = DocumentBuilderFactory.
		                                        newInstance();
		       DocumentBuilder builder = factory.newDocumentBuilder();
		       Document doc = builder.parse(inputXml);
		       
		       if (reportTomorrowWeather == true) {
		    	   nodi = doc.getElementsByTagName("yweather:forecast");
		       }
		       else {
		    	  // System.out.println("report now weather"); 
		    	   nodi = doc.getElementsByTagName("yweather:condition");    //yweather:condition is now
		       }

		       if (nodi.getLength() > 0)
		       {
		          Element nodo = (Element)nodi.item(0);

		          String parsedWeatherString = nodo.getAttribute("code");
		        //  System.out.println("code is: " + parsedWeatherString);
		        
		          weatherCode = Integer.parseInt(parsedWeatherString);
		          System.out.println("Yahoo Weather Code: " + weatherCode + "");

		        }
		    }
		    catch (Exception ex)
		    {
		       System.out.println(ex.getMessage());
		    }
		    finally
		    {
		       try
		       {
		          if (inputXml != null)
		          inputXml.close();
		       }
		       catch (IOException ex)
		       {
		          System.out.println(ex.getMessage());
		       }
		    }
		    
		    switch (weatherCode)  //gets the weather condition from yahoo
			{
				case 0: //tornado							
					weatherCondition = "rain";				
					break;
				case 1: //tropical storm					
					weatherCondition = "rain";	
					break;
				case 2: //hurricane					
					weatherCondition = "rain";	
					break;
				case 3: //severe thunderstorms					
					weatherCondition = "rain";	
					break;
				case 4: //thunderstorms					
					weatherCondition = "rain";	
					break;
				case 5: //mixed rain and snow
					weatherCondition = "rain";	
					break;
				case 6: //mised rain and sleet
					weatherCondition = "rain";	
					break;
				case 7: //mised snow and sleet
					weatherCondition = "snow";	
					break;
				case 8: //freezing drizzle
					weatherCondition = "rain";	
					break;
				case 9: //drizzle
					weatherCondition = "rain";	
					break;
				case 10: //freezing rain
					weatherCondition = "rain";	
					break;
				case 11: //showers
					weatherCondition = "rain";	
					break;	
				case 12: //showers
					weatherCondition = "rain";	
					break;	
				case 13: //snow flurries
					weatherCondition = "snow";	
					break;	
				case 14: //light snow showers
					weatherCondition = "snow";	
					break;	
				case 15: //blowing snow
					weatherCondition = "snow";	
					break;
				case 16: //snow
					weatherCondition = "snow";	
					break;
				case 17: //hail
					weatherCondition = "snow";	
					break;
				case 18: //sleet
					weatherCondition = "snow";	
					break;
				case 19: //dust
					weatherCondition = "rain";	
					break;
				case 20: //foggy
					weatherCondition = "rain";	
					break;
				case 21: //haze
					weatherCondition = "rain";	
					break;
				case 22: //smoky
					weatherCondition = "rain";	
					break;
				case 23: //blustery
					weatherCondition = "rain";	
					break;
				case 24: //windy
					weatherCondition = "cloudy";	
					break;
				case 25: //cold
					weatherCondition = "cloudy";	
					break;
				case 26: //cloudy
					weatherCondition = "cloudy";	
					break;
				case 27: //mostly cloudy (day)
					weatherCondition = "cloudy";	
					break;
				case 28: //mostly cloudy (night)
					weatherCondition = "cloudy";	
					break;
				case 29: //partly cloudy (night)
					weatherCondition = "sunny";	
					break;
				case 30: //partly cloudy (day)
					weatherCondition = "sunny";	
					break;
				case 31: //clear (night)
					weatherCondition = "sunny";	
					break;
				case 32: //sunny
					weatherCondition = "sunny";	
					break;
				case 33: //fair (night)
					weatherCondition = "sunny";	
					break;
				case 34: //fair (day)
					weatherCondition = "sunny";	
					break;
				case 35: //mixed rain and hail
					weatherCondition = "rain";	
					break;
				case 36: //hot
					weatherCondition = "sunny";	
					break;
				case 37: //isoldated thunderstorms
					weatherCondition = "rain";	
					break;
				case 38: //scattered thunderstorms
					weatherCondition = "rain";	
					break;
				case 39: //scattered thunderstorms
					weatherCondition = "rain";	
					break;
				case 40: //scattered showers
					weatherCondition = "rain";	
					break;
				case 41: //heavy snow
					weatherCondition = "snow";	
					break;	
				case 42: //scattered snow showers
					weatherCondition = "snow";	
					break;	
				case 43: //heavy snow
					weatherCondition = "snow";	
					break;	
				case 44: //partly cloudy
					weatherCondition = "cloudy";	
					break;
				case 45: //thundershowers
					weatherCondition = "rain";	
					break;
				case 46: //snow showers
					weatherCondition = "rain";	
					break;
				case 47: //isoldated thundershowers
					weatherCondition = "rain";	
					break;	
				case 3200: //not available
					weatherCondition = "cloudy";	
					break;						
				default:
					//showToast("Could not obtain weather, please check Internet connection");
			}
		    System.out.println("Weather Condition = " + weatherCondition);
		}
	
	 
	 
	 private static void weatherGIF() //not used
	    {
		 
		selectedFileName = weatherCondition;
		
		 //here we will send the selectedfilename to the pixel class, the pixel class will then look for the corresponding filename.txt meta-data file and return back the meta data
		
		if (pixel.GIFNeedsDecoding(currentDir, selectedFileName, currentResolution) == true) {    //resolution can be 16, 32, 64, 128 (String CurrentDir, String GIFName, int currentResolution)
			
			//decodeGIF
		}
		
		GIFfps = pixel.getDecodedfps(currentDir, selectedFileName); //get the fps //to do fix this later becaause we are getting from internal path
	    GIFnumFrames = pixel.getDecodednumFrames(currentDir, selectedFileName);
	    GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, selectedFileName);
	    GIFresolution = pixel.getDecodedresolution(currentDir, selectedFileName);
		
		//****** Now let's setup the animation ******
		   
		    i = 0;
	            
	            stopExistingTimer(); //is this needed, probably not
	    				   
	    				   ActionListener AnimateTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    
	    	               		 	i++;
	    	               			
	    	               			if (i >= numFrames - 1) 
	    	               			{
	    	               			    i = 0;
	    	               			    
	    	               			    loopCounter++;
	    	               			    
	    	               			    if (loopMode == true && loopCounter >=loopInt) { //then we are done and let's exit out
	    	               			    	if (timer != null) timer.stop();
	    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
	    	               			    	//System.exit(0);
	    	               					exit(0,200);
	    	               			    }
	    	               			    
	    	               			}
	    	               		 
	    	               		String framestring = "animations/decoded/" + selectedFileName;
	    	               		
	    	               		System.out.println("framestring: " + framestring);
	    	               		pixel.SendPixelDecodedFrame(currentDir, selectedFileName, i, GIFnumFrames, GIFresolution,KIND.width,KIND.height);
	    	               	
	    	      
	                    }
	                };
	    				   
	    				   
	    				   timer = new Timer(GIFselectedFileDelay, AnimateTimer); //the timer calls this function per the interval of fps
	    				   timer.start();
	    				   System.out.println("file delay: " + selectedFileDelay);
	    }   
	 

	 
	 private static void exit (final int status, int maxDelayMillis) {
		 
		 ActionListener exitTimer_ = new ActionListener() {
				
             public void actionPerformed(ActionEvent actionEvent) {
            	System.out.println("Preparing to exit...");
        		try {
            	 System.exit(status);
        		}
        		catch (Throwable ex) {
        			Runtime.getRuntime().halt(status);
        		}
		     }
		 };
		 
		 Timer etimer = new Timer(maxDelayMillis, exitTimer_); 
		 etimer.start();
		 
			
	 }

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
		    	//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER);
	  			//pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			//IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
	  			
  				PIXELConsole.this.ioiO = ioio_;
  				
  				setupEnvironment();  //here we set the PIXEL LED matrix type
  				
                //pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);   //AL could not make this work, did a quick hack, Roberto probably can change back to the right way
                pixel.matrix = ioio_.openRgbLedMatrix(KIND);
                pixel.ioiO = ioio_;
                System.out.println("Found PIXEL: " + pixel.matrix + "\n");
			
			//need to add if statements here, what happens if they choose weather and gif
			
				if (gifModeExternal == true) {
					
					if (writeMode == true) {
						streamGIF(true); //write to PIXEL's SD card
					}
					else {
						streamGIF(false);  //steam the GIF but don't write
					}
				}
				
				else if (weatherMode == true){
					getWeather();
					runWeatherAnimations();
				}
				
				else if (scrollingTextMode == true) {
					scrollText(scrollingText_, writeMode); //write or stream
				}
			
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				Thread.sleep(10);
			}
		};
	}
}

