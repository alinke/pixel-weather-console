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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ioio.lib.api.exception.ConnectionLostException;


public class Weather_Wunderground extends PIXELConsole {
	private static InputStream inputXml = null;
	private static String currentCode = null;
	private static String forecastCode = null;
	private static String weatherCode;
	private static String APIKey = "";
	
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
	
	
	protected static void getWeather(){

		
	    inputXml = null;
	    try
	    {

	    	
	    	if (userAPIKey == true) {
	    		APIKey = apikey_;
	    	}
	    	else {
	    		APIKey = "9af894a415d42861";
	    	}
	    	
	    	
	    	if (zipMode == true) {
 	    		 
 	    		 inputXml  = new URL("http://api.wunderground.com/api/" + APIKey + "/forecast/q/" + zip_ + ".xml").openConnection().getInputStream();
 	    		 System.out.println("Wunderground API Call: http://api.wunderground.com/api/" + APIKey + "/forecast/q/" + zip_ + ".xml");
 	    		
 		     }
 		      else {
 		    	
 		    	inputXml = new URL("http://api.wunderground.com/api/" + APIKey + "/forecast/q/zmw:" + zmw_ + ".xml").openConnection().getInputStream();
 		    	System.out.println("Wunderground API Call: http://api.wunderground.com/api/" + APIKey + "/forecast/q/zmw:" + zmw_ + ".xml");
 		    	  
 		      }
          

	       DocumentBuilderFactory factory = DocumentBuilderFactory.
	                                        newInstance();
	       DocumentBuilder builder = factory.newDocumentBuilder();
	       Document doc = builder.parse(inputXml);
	       
	       doc.getDocumentElement().normalize();
           NodeList nodeList = doc.getElementsByTagName("forecastday");
           //System.out.println("Total # elements : " + nodeList.getLength());  
           System.out.println("----------------------------");
         
           for (int i = 0; i < nodeList.getLength() / 2; i++) {  //added the divide by 2 because forecast day shows up in the XML under multiple elements, we just want the first set
        	   
        	   Node nNode = nodeList.item(i);
        	   Element eElement = (Element) nNode;
               
               if (i == 0) {
            	   System.out.println("Current Weather Code : " + eElement.getElementsByTagName("icon").item(0).getTextContent());
                   System.out.println("Day : " + eElement.getElementsByTagName("title").item(0).getTextContent());
                   System.out.println("Description : " + eElement.getElementsByTagName("fcttext").item(0).getTextContent());
            	   currentCode = eElement.getElementsByTagName("icon").item(0).getTextContent();
               }
               
               if (i == 2) {
            	   System.out.println("----------------------------");
            	   System.out.println("Forecast Weather Code : " + eElement.getElementsByTagName("icon").item(0).getTextContent());
                   System.out.println("Day : " + eElement.getElementsByTagName("title").item(0).getTextContent());
                   System.out.println("Description : " + eElement.getElementsByTagName("fcttext").item(0).getTextContent());
                 //  System.out.println("Location : " + eElement.getElementsByTagName("tz_long").item(0).getTextContent());
            	   forecastCode = eElement.getElementsByTagName("icon").item(0).getTextContent();
               }
           }
         
           System.out.println("----------------------------");
           System.out.println("currentCode : " + currentCode);
           System.out.println("forecastCode : " + forecastCode);
	       
	       if (reportTomorrowWeather == true) {
	    	   weatherCode = forecastCode;
	       }
	       else {
	    	   weatherCode = currentCode;
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
	    
	    if (weatherCode != null) {
	    
		    switch (weatherCode)  //gets the weather condition from yahoo
			{
				case "chanceflurries": 							
					setWeatherCondition("snow");				
					break;
				case "chancerain": 			
					setWeatherCondition("rain");	
					break;
				case "chancesleet": 			
					setWeatherCondition("rain");	
					break;
				case "chancetstorms": 				
					setWeatherCondition("rain");	
					break;
				case "clear": 				
					setWeatherCondition("sunny");	
					break;
				case "cloudy": 				
					setWeatherCondition("cloudy");	
					break;
				case "flurries": 				
					setWeatherCondition("snow");	
					break;
				case "fog": 				
					setWeatherCondition("cloudy");	
					break;
				case "hazy": 				
					setWeatherCondition("cloudy");	
					break;
				case "mostlycloudy": 				
					setWeatherCondition("cloudy");	
					break;
				case "mostlysunny": 				
					setWeatherCondition("sunny");	
					break;
				case "partlysunny": 				
					setWeatherCondition("sunny");	
					break;
				case "snow": 				
					setWeatherCondition("snow");	
					break;
				case "rain": 				
					setWeatherCondition("rain");	
					break;
				case "sleet": 				
					setWeatherCondition("snow");	
					break;
				case "sunny": 				
					setWeatherCondition("sunny");	
					break;
				case "unknown": 				
					setWeatherCondition("cloudy");	
					break;
				case "tstorms": 				
					setWeatherCondition("rain");	
					break;
				case "partlycloudy": 				
					setWeatherCondition("cloudy");	
					break;
				case "chancesnow": 				
					setWeatherCondition("snow");	
					break;
				default:
					System.out.println("ERROR, could not get weather, defaulting to cloudy");
					setWeatherCondition("cloudy");
			}
		    System.out.println("LED Display Weather Condition = " + getWeatherCondition());
		    System.out.println("----------------------------");
	    }
	    else {
	    	System.out.println("ERROR, could not retrieve the weather code, check internet connection and then check that you've entered the rigth zip or zmw id");
		    System.out.println("----------------------------");
	    }
	}
	
	 protected static void runWeatherAnimations() {


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
