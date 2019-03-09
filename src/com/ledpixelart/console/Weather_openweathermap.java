package com.ledpixelart.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ioio.lib.api.exception.ConnectionLostException;


import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;


public class Weather_openweathermap extends PIXELConsole {
	private static InputStream inputXml = null;
	private static String currentCode = null;
	private static String forecastCode = null;
	private static String weatherCode;
	private static String APIKey = "";
	public static String weatherIconCode;
	private String weatherIcon;
	
	
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
	 
	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

		  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
		    } finally {
		      is.close();
		    }
		  }
	
	protected static void getWeather() throws IOException, JSONException {
    
		if (userAPIKey == true) {
    		APIKey = apikey_;
    	}
    	else {
    		APIKey = "e7fc61dd49597f0cac7871393e8cd971";
    		System.out.println("*** IMPORTANT **** Please obtain your own free Open Weather Map API key from https://openweathermap.org/api");
    		System.out.println("And then add –apikey=\"Your-API-Key\" as an additional command line paramater");
			System.out.println("Otherwise the weather function will not be reliable");
			System.out.println("----------------------------");
    	}
    	
    	
    	if (zipMode == true) {
	    		 
    		   JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?zip=" + zip_ + "&appid=" + APIKey);
    		   System.out.println("Current Weather Raw Data");
    		   System.out.println(json.toString());
	    	   System.out.println("OpenWeatherMap API Call: http://api.openweathermap.org/data/2.5/weather?zip=" + zip_ + "&appid=" + APIKey);
	    	   System.out.println("----------------------------");
	    	   JSONObject topLevel = new JSONObject(json.toString());
	           JSONArray listArray = topLevel.getJSONArray("weather");
	           JSONObject firstObject = (JSONObject)listArray.get(0);
	           weatherIconCode = firstObject.getString("icon");
	           //System.out.println("TODAYs weather icon :" + weatherIconCode);
	    		
		     }
		      else {
		    	  JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?id=" + zmw_ + "&appid=" + APIKey);
		    	  System.out.println("Current Weather Raw Data");
		    	  System.out.println(json.toString());
		    	  System.out.println("----------------------------");
		    	  System.out.println("OpenWeatherMap API Call: http://api.openweathermap.org/data/2.5/weather?id=" + zmw_ + "&appid=" + APIKey);
		    	  JSONObject topLevel = new JSONObject(json.toString());
		          JSONArray listArray = topLevel.getJSONArray("weather");
		          JSONObject firstObject = (JSONObject)listArray.get(0);
		          weatherIconCode = firstObject.getString("icon");
		          //System.out.println("TODAYs weather icon :" + weatherIconCode);
		      }
    	
    	
    	   if (weatherIconCode == null || weatherIconCode.equals("99999")) {  //had to add this because if the user entered the wrong API key, this will crash
			   weatherIconCode = "99999";
			   System.out.println("Weather call failed, please check your API key in Settings in this app and then check that you've entered the rigth zip or city id");
			   System.out.println("ERROR, could not retrieve the weather code, check internet connection");
			   System.out.println("----------------------------");
		   }
	       
	       if (weatherIconCode.equals("01d") || weatherIconCode.equals("01n")) {
	    	   setWeatherCondition("sunny");
	       }
	       else if (weatherIconCode.equals("02d") || weatherIconCode.equals("02n")) {
	    	   setWeatherCondition("sunny");	
	       }
	       else if (weatherIconCode.equals("02d") || weatherIconCode.equals("02n")) {
	    	   setWeatherCondition("sunny");	
	       }
	       else if (weatherIconCode.equals("03d") || weatherIconCode.equals("03n")) {
	    		setWeatherCondition("cloudy");		
	       }
	       else if (weatherIconCode.equals("04d") || weatherIconCode.equals("04n")) {
	    		setWeatherCondition("cloudy");		
	       }
	       else if (weatherIconCode.equals("09d") || weatherIconCode.equals("09n")) {
	    	   setWeatherCondition("rain");	
	       }
	       else if (weatherIconCode.equals("10d") || weatherIconCode.equals("10n")) {
	    	   setWeatherCondition("rain");	
	       }
	       else if (weatherIconCode.equals("11d") || weatherIconCode.equals("11n")) {
	    	   setWeatherCondition("rain");		
	       }
	       else if (weatherIconCode.equals("13d") || weatherIconCode.equals("13n")) {
	    	   setWeatherCondition("snow");		
	       }
	       else if (weatherIconCode.equals("50d") || weatherIconCode.equals("50n")) {
	    	   setWeatherCondition("rain");	
	       }
	       else {
	    	   System.out.println("ERROR, could not retrieve the weather code, check internet connection and then check that you've entered the rigth zip or city id");
			   System.out.println("----------------------------");
	    	   setWeatherCondition("cloudy");	
	       }
        
		     /*  *	01d.png  	01n.png  	clear sky
		   	02d.png  	02n.png  	few clouds
		   	03d.png  	03n.png  	scattered clouds
		   	04d.png  	04n.png  	broken clouds
		   	09d.png  	09n.png  	shower rain
		   	10d.png  	10n.png  	rain
		   	11d.png  	11n.png  	thunderstorm
		   	13d.png  	13n.png  	snow
		   	50d.png  	50n.png  	mist*/
		    
		    System.out.println("LED Display Weather Condition = " + getWeatherCondition());
		    System.out.println("----------------------------");
	   
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
