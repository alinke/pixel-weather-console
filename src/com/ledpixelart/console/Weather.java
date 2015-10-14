package com.ledpixelart.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ioio.lib.api.exception.ConnectionLostException;

public class Weather extends PIXELConsole {
	private static InputStream inputXml = null;
	 protected static void sendFramesToPIXELWeather() 
	 { 
	   	  int y;
	    	 
	   	  for (y=0;y<getNumFrames()-1;y++) { //let's loop through and send frame to PIXEL with no delay
	 		
	 		setFramestring("animations/decoded/" + getAnimation_name() + "/" + getAnimation_name() + y + ".rgb565");
	 		
	 			System.out.println("writing to PIXEL frame: " + getFramestring());

	 		try 
	 		{
	 		    pixel.loadRGB565Weather(getFramestring());
	 		} 
	 		catch (ConnectionLostException e1) 
	 		{
	 		    // TODO Auto-generated catch block
	 		    e1.printStackTrace();
	 		}
	   	  } //end for loop
	     	 
	     }
	
	
	protected void getWeather(){

		
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
 		    	//  inputXml  = new URL("http://weather.yahooapis.com/forecastrss?p=" + woeid_ + "&u=f").openConnection().getInputStream();  
 		    	  
 		    	//   edited by Sucheta:::  
 		    	 inputXml = new URL(" http://weather.yahooapis.com/forecastrss?w=" + woeid_).openConnection().getInputStream();
 		    	  
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
	        
	          setWeatherCode(Integer.parseInt(parsedWeatherString));
	          System.out.println("Yahoo Weather Code: " + getWeatherCode() + "");

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
	    
	    switch (getWeatherCode())  //gets the weather condition from yahoo
		{
			case 0: //tornado							
				setWeatherCondition("rain");				
				break;
			case 1: //tropical storm					
				setWeatherCondition("rain");	
				break;
			case 2: //hurricane					
				setWeatherCondition("rain");	
				break;
			case 3: //severe thunderstorms					
				setWeatherCondition("rain");	
				break;
			case 4: //thunderstorms					
				setWeatherCondition("rain");	
				break;
			case 5: //mixed rain and snow
				setWeatherCondition("rain");	
				break;
			case 6: //mised rain and sleet
				setWeatherCondition("rain");	
				break;
			case 7: //mised snow and sleet
				setWeatherCondition("snow");	
				break;
			case 8: //freezing drizzle
				setWeatherCondition("rain");	
				break;
			case 9: //drizzle
				setWeatherCondition("rain");	
				break;
			case 10: //freezing rain
				setWeatherCondition("rain");	
				break;
			case 11: //showers
				setWeatherCondition("rain");	
				break;	
			case 12: //showers
				setWeatherCondition("rain");	
				break;	
			case 13: //snow flurries
				setWeatherCondition("snow");	
				break;	
			case 14: //light snow showers
				setWeatherCondition("snow");	
				break;	
			case 15: //blowing snow
				setWeatherCondition("snow");	
				break;
			case 16: //snow
				setWeatherCondition("snow");	
				break;
			case 17: //hail
				setWeatherCondition("snow");	
				break;
			case 18: //sleet
				setWeatherCondition("snow");	
				break;
			case 19: //dust
				setWeatherCondition("rain");	
				break;
			case 20: //foggy
				setWeatherCondition("rain");	
				break;
			case 21: //haze
				setWeatherCondition("rain");	
				break;
			case 22: //smoky
				setWeatherCondition("rain");	
				break;
			case 23: //blustery
				setWeatherCondition("rain");	
				break;
			case 24: //windy
				setWeatherCondition("cloudy");	
				break;
			case 25: //cold
				setWeatherCondition("cloudy");	
				break;
			case 26: //cloudy
				setWeatherCondition("cloudy");	
				break;
			case 27: //mostly cloudy (day)
				setWeatherCondition("cloudy");	
				break;
			case 28: //mostly cloudy (night)
				setWeatherCondition("cloudy");	
				break;
			case 29: //partly cloudy (night)
				setWeatherCondition("sunny");	
				break;
			case 30: //partly cloudy (day)
				setWeatherCondition("sunny");	
				break;
			case 31: //clear (night)
				setWeatherCondition("sunny");	
				break;
			case 32: //sunny
				setWeatherCondition("sunny");	
				break;
			case 33: //fair (night)
				setWeatherCondition("sunny");	
				break;
			case 34: //fair (day)
				setWeatherCondition("sunny");	
				break;
			case 35: //mixed rain and hail
				setWeatherCondition("rain");	
				break;
			case 36: //hot
				setWeatherCondition("sunny");	
				break;
			case 37: //isoldated thunderstorms
				setWeatherCondition("rain");	
				break;
			case 38: //scattered thunderstorms
				setWeatherCondition("rain");	
				break;
			case 39: //scattered thunderstorms
				setWeatherCondition("rain");	
				break;
			case 40: //scattered showers
				setWeatherCondition("rain");	
				break;
			case 41: //heavy snow
				setWeatherCondition("snow");	
				break;	
			case 42: //scattered snow showers
				setWeatherCondition("snow");	
				break;	
			case 43: //heavy snow
				setWeatherCondition("snow");	
				break;	
			case 44: //partly cloudy
				setWeatherCondition("cloudy");	
				break;
			case 45: //thundershowers
				setWeatherCondition("rain");	
				break;
			case 46: //snow showers
				setWeatherCondition("rain");	
				break;
			case 47: //isoldated thundershowers
				setWeatherCondition("rain");	
				break;	
			case 3200: //not available
				setWeatherCondition("cloudy");	
				break;						
			default:
				//showToast("Could not obtain weather, please check Internet connection");
		}
	    System.out.println("Weather Condition = " + getWeatherCondition());
	
	}
	
	 protected void runWeatherAnimations() {


			String selectedFileName = getWeatherCondition();
			String decodedDirPath = "animations/decoded";
			//String imagePath = decodedDirPath; //animations/decoded/rainx.rgb565

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
		    		setGIFfps(1000.f / selectedFileDelay);
				} else { 
		    		setGIFfps(0);
		    	}
			    
			    //****** Now let's setup the animation ******

			    setAnimation_name(selectedFileName);
			    setU(0);
			    setNumFrames(selectedFileTotalFrames);

		            stopExistingTimer(); //is this needed, probably not

		    			if (pixelHardwareID.substring(0,4).equals("PIXL") && isWriteMode() == true) {
		    					pixel.interactiveMode();
		    					//send loading image
		    					pixel.writeMode(getGIFfps()); //need to tell PIXEL the frames per second to use, how fast to play the animations
		    					sendFramesToPIXELWeather(); //send all the frame to PIXEL
		    					pixel.playLocalMode(); //now tell PIXEL to play locally
		    			}
		    			else {
		    				   stopExistingTimer();

		    				   ActionListener AnimateTimer = new ActionListener() {

		    	                    public void actionPerformed(ActionEvent actionEvent) {

		    	               		 	setU(getU() + 1);

		    	               			if (getU() >= getNumFrames() - 1) 
		    	               			{
		    	               			    setU(0);
		    	               			    
		    	               			    loopCounter++;
		    	               			    
		    	               			    if (loopMode == true && loopCounter >=getLoopInt()) { //then we are done and let's exit out
		    	               			    	if (timer != null) timer.stop();
		    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
		    	               			    	//System.exit(0);
		    	               					exit(0,200);
		    	               			    }
		    	               			}

		    	               		String framestring = "animations/decoded/" + getAnimation_name() + "/" + getAnimation_name() + getU() + ".rgb565";

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
	 
	 private static void weatherGIF() //not used
	 {

		 
			setSelectedFileName(getWeatherCondition());
			
			 //here we will send the selectedfilename to the pixel class, the pixel class will then look for the corresponding filename.txt meta-data file and return back the meta data
			
			//if (pixel.GIFNeedsDecoding(currentDir, selectedFileName, currentResolution) == true) {    //resolution can be 16, 32, 64, 128 (String CurrentDir, String GIFName, int currentResolution)
				
				//decodeGIF
			//}
			
			//TO DO make sure to test the weather gifs now that we've made these changes
			
			/*GIFfps = pixel.getDecodedfps(currentDir, selectedFileName); //get the fps //to do fix this later becaause we are getting from internal path
		    GIFnumFrames = pixel.getDecodednumFrames(currentDir, selectedFileName);
		    GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, selectedFileName);
		    GIFresolution = pixel.getDecodedresolution(currentDir, selectedFileName);*/
		    
		    setGIFnumFrames(pixel.getDecodednumFrames(currentDir, getSelectedFileName()));
		    setGIFresolution(pixel.getDecodedresolution(currentDir, getSelectedFileName()));
		    
		    if (isFrameDelayOverride()) { 
		    	setGIFselectedFileDelay(getFrameDelayInt()); //use the override the user specified from the command line --framedelay=x
		    	setGIFfps(1000.f / getGIFselectedFileDelay());
		    }
		    else { //no override so just use as is
		    	 setGIFselectedFileDelay(pixel.getDecodedframeDelay(currentDir, getSelectedFileName()));  
		    	 setGIFfps(pixel.getDecodedfps(currentDir, getSelectedFileName())); //get the fps
		    }
			
			//****** Now let's setup the animation ******
			   
			    i = 0;
		            
		            stopExistingTimer(); //is this needed, probably not
		    				   
		    				   ActionListener AnimateTimer = new ActionListener() {

		    	                    public void actionPerformed(ActionEvent actionEvent) {
		    	                    
		    	               		 	i++;
		    	               			
		    	               			if (i >= getNumFrames() - 1) 
		    	               			{
		    	               			    i = 0;
		    	               			    
		    	               			    loopCounter++;
		    	               			    
		    	               			    if (loopMode == true && loopCounter >=getLoopInt()) { //then we are done and let's exit out
		    	               			    	if (timer != null) timer.stop();
		    	               					System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
		    	               			    	//System.exit(0);
		    	               					exit(0,200);
		    	               			    }
		    	               			    
		    	               			}
		    	               		 
		    	               		String framestring = "animations/decoded/" + getSelectedFileName();
		    	               		
		    	               		System.out.println("framestring: " + framestring);
		    	               		pixel.SendPixelDecodedFrame(currentDir, getSelectedFileName(), i, getGIFnumFrames(), getGIFresolution(),KIND.width,KIND.height);
		    	               	
		    	      
		                    }
		                };
		    				   
		    				   
		    				   timer = new Timer(getGIFselectedFileDelay(), AnimateTimer); //the timer calls this function per the interval of fps
		    				   timer.start();
		    				   System.out.println("file delay: " + getSelectedFileDelay());
		    
	 }
	
}
