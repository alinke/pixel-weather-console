package com.ledpixelart.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Timer;
//import javax.lang.model.element.Element;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.onebeartoe.pixel.hardware.Pixel;


import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PIXELConsole extends IOIOConsoleApp {
@SuppressWarnings("deprecation")
//public class PIXELConsole  {
	private boolean ledOn_ = false;

	private static IOIO ioiO; 
	 
	private static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	 
	private static int weatherCode;
	 
	private static String weatherCondition;
     
	public static final Pixel pixel = new Pixel(KIND);
	 
	public static String pixelFirmware = "Not Found";
	 
	public static String pixelHardwareID = "";
	    
	private static VersionType v;
	 
	private static int i;
	    
    private static int numFrames = 0;
    
    private static String animation_name;
    
    private volatile static Timer timer;
    
    private static ActionListener AnimateTimer;
    
    private static String selectedFileName;
    
  	private static String decodedDirPath;
  	
  	private static byte[] BitmapBytes;
  	
  	private static short[] frame_;
  	
  	private static String framestring;
  	
  	private static float fps = 0;

	private static Command command_;
	
	private static String zip_ = "";
	
	private static String woeid_ = "";
	
	private static int zipInt_;
	
	private static int woeidInt_;
	
	private static boolean zipMode;
	
	private static boolean reportTomorrowWeather = false;
	
	private static boolean validCommandLine = false;
	
	private static HttpGet getRequest;
	
	private static HttpResponse httpResponse;
	
	private static HttpEntity entity;
	
	private static NodeList nodi;
	
	private static InputStream inputXml = null;
	
	private static enum Command {
		VERSIONS, FINGERPRINT, WRITE
	}
  	
  	private static void printUsage() {
		System.err.println("PIXEL Weather: Console Version");
		System.err.println();
		System.err.println("Usage:");
		System.err.println("pixelweather <options>");
		System.err.println();
		System.err.println("Valid options are:");
		System.err
				.println("--zip=your_zip_code Non-US users should use woeid");
		System.err
				.println("--woeid=your_woeid_code A numeric number that Yahoo uses to designate your location");
		System.err
				.println("--forecast Displays tomorrow's weather conditions, defaults to current weather conditions if not specified");
	}

	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
		public static void main(String[] args) throws Exception {
			
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
			//it can only be zip or woeid
			
			if (arg.startsWith("--forecast")) {
				reportTomorrowWeather = true;
				System.out.println("Displaying the current weather conditions, use --forecast if you want tomorrow's forecast.\n");
			}
			
			if (arg.startsWith("--zip=")) {
				zip_ = arg.substring(6);
				System.out.println("zip code: " + zip_);
				validCommandLine = true;
				zipMode = true;
			} else if (arg.startsWith("--woeid")) {
				woeid_ = arg.substring(8);
				System.out.println("woeid: " + woeid_);
				validCommandLine = true;
				zipMode = false;
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
			} else {
				System.out
						.println("Unknown input. t=toggle, n=on, f=off, q=quit.");
			}
		}
	
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
	
	 private static void runAnimations() 
	    {
		 
		String selectedFileName = weatherCondition;
		String decodedDirPath = "animations/decoded";
		String imagePath = decodedDirPath; //animations/decoded/rainx.rgb565

		String path = decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"; //animations/decoded/rain/rain.txt , this file tells us fps
		//System.out.println("path: " + path);
		
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
	    		fps = 1000.f / selectedFileDelay;
			} else { 
	    		fps = 0;
	    	}

		    //****** Now let's setup the animation ******
		    
		    animation_name = selectedFileName;
		    i = 0;
		    numFrames = selectedFileTotalFrames;
	            
	            stopExistingTimer(); //is this needed, probably not
	    	
	    			if (pixelFirmware.equals("PIXL0003")) {
	    					pixel.interactiveMode();
	    					//send loading image
	    					pixel.writeMode(fps); //need to tell PIXEL the frames per second to use, how fast to play the animations
	    					sendFramesToPIXEL(); //send all the frame to PIXEL
	    					pixel.playLocalMode(); //now tell PIXEL to play locally
	    			}
	    			else {
	    				   stopExistingTimer();
	    				   
	    				   ActionListener AnimateTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    
	    	               		 	i++;
	    	               			
	    	               			if (i >= numFrames - 1) 
	    	               			{
	    	               			    i = 0;
	    	               			}
	    	               		
	    	               		String framestring = "animations/decoded/" + animation_name + "/" + animation_name + i + ".rgb565";
	    	               		
	    	               		System.out.println("framestring: " + framestring);

	    	               		try 
	    	               		{
	    	               		    pixel.loadRGB565(framestring);
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
	    }
	 
	 private static void sendFramesToPIXEL() { 
   	  int y;
    	 
   	  for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
 		
 		framestring = "animations/decoded/" + animation_name + "/" + animation_name + y + ".rgb565";
 		
 			System.out.println("writing to PIXEL frame: " + framestring);

 		try 
 		{
 		    pixel.loadRGB565(framestring);
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
	
	 public void writeImage() 
	    {	
	
	        String imagePath = "images/apple.png";
		
	        try 
	        {
	            System.out.println("Attemping to load " + imagePath + " from the classpath.");
	            URL url = PIXELConsole.class.getClassLoader().getResource(imagePath);

			    BufferedImage originalImage = ImageIO.read(url);
			    
				if (pixelFirmware.equals("PIXL0003")) {  //change this to board
						pixel.interactiveMode();
						//send loading image
						pixel.writeMode(10); //need to tell PIXEL the frames per second to use, how fast to play the animations
						pixel.writeImagetoMatrix(originalImage);
						pixel.playLocalMode(); //now tell PIXEL to play locally
					}
					else {
						pixel.writeImagetoMatrix(originalImage);
					}
		        } 
		        catch (Exception e1) 
		        {
		            e1.printStackTrace();
		        }
	    }

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			//private DigitalOutput led_;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				//led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
		    	
		    	//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			//pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER);
	  			//IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
		    	
	  			//	led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
	  				PIXELConsole.this.ioiO = ioio_;
	                pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
	                pixel.ioiO = ioio_;
	                
		
			System.out.println("Found PIXEL: " + pixel.matrix + "\n");
			
			getWeather();
			//writeImage(); //change this to animate
			runAnimations();
			
			
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				//led_.write(!ledOn_);
				Thread.sleep(10);
			}
		};
	}
}

