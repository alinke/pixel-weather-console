package com.ledpixelart.console;

import ioio.lib.api.AnalogInput;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.onebeartoe.pixel.hardware.Pixel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.CharacterData;

import twitter4j.JSONException;
//import twitter4j.JSONArray;
//import twitter4j.JSONException;
//import twitter4j.JSONObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


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
    Print_Usage usage = new Print_Usage();
     static Scroll_Text scroll = new Scroll_Text();
     static Snow snow = new Snow();
    	 //static Weather weather = new Weather();
    	 static Weather_Wunderground weather = new Weather_Wunderground();
    	  static QuickBase base= new QuickBase();
    	  static YahooStock yahooStock= new YahooStock();
    	  static RunCompliment compliment = new RunCompliment();
     
    private boolean ledOn_ = false;
	private static IOIO ioiO; 	 
	//protected static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	protected static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
	private static int weatherCode;
	private static String weatherCondition;
	public static final Pixel pixel = new Pixel(KIND);
	private static int selectedFileResolution = 2048; 
	public static String pixelFirmware = "Not Found";
	public static String pixelHardwareID = "";
	private static VersionType v;
	static int i;
	private static int u;
	private static int z = 0;
	private static int q = 0;
	protected static int s = 0;
    private static int numFrames = 0;
    private static String animation_name;
    volatile static Timer timer;
    
    public static ActionListener TwitterTimer;
    public volatile static Timer twitterTimer;
    private static ActionListener AnimateTimer;
    private static ActionListener exitTimer_;
    private static String selectedFileName;
  	private static String decodedDirPath;
  	private static byte[] BitmapBytes;
  	private static short[] frame_;
  	private static String framestring;
  	//private static float fps = 0;
	private static Command command_;
	protected static String zip_ = "";
	private static String gifFullPath_ = "";
	private static String gifFilePath_ = "";
	private static String pngFullPath_ = "";
	private static String pngFilePath_ = "";
	private static String pngNameOnly_ = "";
	
	protected static String zmw_ = "";
	protected static String woeid_ = ""; // no longer used, this was for the yahoo api
	protected static int woeidInt_;
	protected static int zipInt_;
	protected static int zmwInt_;
	protected static boolean zipMode;
	protected static String apikey_;
	protected static boolean userAPIKey;
	protected static boolean reportTomorrowWeather = false;
	private static boolean validCommandLine = false;
	private static boolean writeMode = false;
	private static String loopString;
	static int loopInt;
	private static String frameDelayString;
	private static int frameDelayInt = 100; //in milliseconds
	private static boolean frameDelayOverride;
	public static String twitterSearchString;
	static boolean twitterMode;
	private static String twitterIntervalString = null;
	private static int twitterIntervalInt = 60; //in seconds
	public static String twitterResult = null;
	static boolean loopMode;
	static int loopCounter;
	private static boolean gifModeInternal = false;
	private static boolean gifModeExternal = false;
	private static boolean pngModeExternal = false;
	private static boolean weatherMode = false;
	private static HttpGet getRequest;
	private static HttpResponse httpResponse;
	private static HttpEntity entity;
	protected static NodeList nodi;
	
	private static InputStream authXml = null;
	private static InputStream dataXml = null;
    private static int selectedFileTotalFrames;
    private static int selectedFileDelay;
    private static float GIFfps;
    private static int GIFnumFrames;
    private static int GIFselectedFileDelay;
    private static int GIFresolution;
    public static String currentDir;
    private static int matrix_model;
    private static int frame_length;
    private static int currentResolution;
    //private static int ledMatrixType = 3; //we'll default to PIXEL 32x32 and change this is a command line option is entered specifying otherwise
    private static int ledMatrixType = 11;
    static int x;
    static int scrollingTextDelay_ = 6;
    protected static int scrollingSmoothness_ = 100; //put a smaller number like 10 for smoother scrolling but you'll need a USB connection for smooth, bluetooth there will be some delay
    private static String scrollingText_;
    private static boolean scrollingTextMode = false;
    private static String scrollingTextSpeed_;
    static int scrollingTextFontSize_ = 28; //default the scrolling text font to 30
    private static String scrollingTextFontSizeString;
    protected static String scrollingTextColor_ = "red";
    static Color textColor;
    static int fontOffset = 0;
    public static Twitter twitter;
    public static TwitterFactory tf;
    public static Query query;
    
    public static QueryResult result = null;
    
    private Status status;
    
    private String lastTweet;
    
    private Integer tweetCount = 0;
    
    private static boolean stayConnected = true;
    
    public static boolean filterTweets = false;
    
    private static boolean backgroundMode = false;
    
    private static boolean quickbaseMode = false;
    
    private static String quickbaseTicket = null;
    
    private static String quickBasePIXELString = null;
    
    private static boolean complimentsMode = false;
    
    ///*********** Edit QuickBase Parameters Here ******************
    private static String quickBaseDBID = null;
    private static String quickBaseToken = null; //the token is assigned at the QuickBase App level and will be different for each one
    private static String quickBaseDomain = null;
    private static String quickBaseSearchTermForDescriptionField = null;
    private static String quickBaseUserID = null;
    private static String quickBaseUserPassword = null;
    private static String quickBaseRootXMLNode = "qdbapi"; //you should NOT need to change this
    private static String quickBaseDataXMLNode = "record"; //you should NOT need to change this
    private static String quickBaseQueryFieldID = null;
    private static String quickBaseReturnFields = null;
    private static int    quickBaseRefreshInterval = 25;
    ///**************************************************************
 
    ///*********** Edit Service Now Parameters Here ******************
    protected static String snowDomain = "";
    protected static String snowUserID = "";
    protected static String snowUserPassword = "";
    protected static String snowDataXMLNode = "count"; //you should NOT need to change this
    protected static String snowBaseQuery = "active%3Dtrue%5EnumberSTARTSWITHINC%5EstateIN1%2C2%2C3%2C4%5E";
    private static String snowPriorityQuery ="priorityIN1%2C2"; //should not need to change this, note we didn't inlcude the %5E so don't forget to add that in later
    private static String snowSLAExceededQuery ="u_sla_alertSTARTSWITHRed"; //should not need to change this
    public static int    snowRefreshInterval = 20;
    private static boolean snowMode = false;
    private static String snowPIXELString;
    protected static String snowGroupOpen;
    private static String snowGroupPriority;
    private static String snowGroupExceededSLA;
    ///**************************************************************
    
    private static String proximityPinString_;
    
    private static int proximityPin_ = 34;
    
    private static boolean ProxSensor = false;
    
    private static boolean ProxShow = false;
    
    private static boolean ProxTriggered = false;
    
    static boolean ProxTriggerDone = true;
    
    private static boolean debug_ = false;
    
    private static int TriggerUpperThreshold_ = 500;
    
	private static String sensorLoopDelayString_ = null;
	
	private static int sensorLoopDelay_ = 500;
    
    private static HttpURLConnection conn;
    
    private static boolean quickBaseAuthSuccesful = false;
    
    private static String stockSymbols = "AMAT";
    
    private static String stockPrice = null;
    
    private static String stockPriceLast = "connection issue";
    
    private static String stockChangeString = null;
    
    private static Boolean stockMode = false;
    
    private static BigDecimal stockChange;
    
    private static String stockChangeLast = " ";
    
    protected static String complimentString = null;
    
	protected static String compliementColor = "green";
	private static ArrayList<String> snowGroupGUID;
	private static ArrayList<String> snowGroupName;
	static String snowname="";
	static String snowGUID="";
	
	private static String homePath_ = "";
	private static String gifNameOnly_ = "";
	private static String decodedDir_ = "";
	private static boolean homePathSpecified_ = false;
	public static boolean silentMode_ = false;
	private static boolean absolutePath_ = false;
	private static boolean relativePath_ = false;
	private static String selectedLEDMatrix = "Adafruit 32x32";
	public static String OS = System.getProperty("os.name").toLowerCase();
	public static String port_ = null;
	
    private static enum Command
		{
			VERSIONS, FINGERPRINT, WRITE
		}
  		
	private static void parseArgs(String[] args) throws BadArgumentsException {
			int nonOptionArgNum = 0;
			for (String arg : args)
			  {
				if (arg.startsWith("--"))
					parseOption(arg);
				else 
				 {
					if (nonOptionArgNum == 0) 
						parseCommand(arg);
					
				 }
			  }
		}
	private static void parseCommand(String arg) throws BadArgumentsException {
			try 
				{
				command_ = Command.valueOf(arg.toUpperCase());
				} catch (IllegalArgumentException e) {
				throw new BadArgumentsException("Unrecognized command: " + arg);
			}
			
			
		}

	private static void parseOption(String arg) throws BadArgumentsException {
			
			if (arg.startsWith("--silent")) {    
				silentMode_ = true;
			}		
			
			/*if (arg.startsWith("--path=")) {    //april 2019 , added this path because absolute path gifs were failing, so we need to pass to java the path where pixelc is
					homePath_ = arg.substring(7);
					validCommandLine = true;
					homePathSpecified_ = true;
			}	*/
		
			if (arg.startsWith("--proximity"))
				{
					ProxSensor = true;
					validCommandLine = true;
					if (!silentMode_) System.out.println("Proximity Sensor enabled");
			}
			
			if (arg.startsWith("--proximitypin="))
			{  //we'll use pin34 by default unless the user overrides here
				proximityPinString_ = arg.substring(15);
				
				if (Float.parseFloat(proximityPinString_) > 35 || Float.parseFloat(proximityPinString_) < 31 || proximityPinString_.contains(".")) 
					if (!silentMode_) System.out.println("The proximity pin number can be 31,32,33, or 34. Defaulting to a proximity sensor pin 34...");
				
				else 
				{
					proximityPin_ = Integer.parseInt(proximityPinString_);
					if (!silentMode_) System.out.println("Proximity Pin: " + proximityPinString_);
				}	
			}
			
			if (arg.startsWith("--proximityhigh=")) 
			{  //the upper limit threshold for the prox sensor to trigger, ie, will trigger if goes over this number
				String TriggerUpperThresholdString = arg.substring(16);
				
				if (Float.parseFloat(TriggerUpperThresholdString) > 1000 || proximityPinString_.contains(".")) 
					if (!silentMode_) System.out.println("The upper trigger threshold for the proximity sensor cannot be over 1000 and must also be a whole number. Defaulting to 500...");
				
				else
				{
					TriggerUpperThreshold_ = Integer.parseInt(TriggerUpperThresholdString);
					if (!silentMode_) System.out.println("Proximity Upper Trigger Threshold Override (Default:500) : " + TriggerUpperThresholdString);
				}	
			}
			
			if (arg.startsWith("--proximityshow")) 
			{
				if (!silentMode_) System.out.println("Displaying Proximity Sensor values");
				ProxShow = true;
			}
			
			
			if (arg.startsWith("--sensorloopdelay=")) {  //we'll use pin34 by default unless the user overrides here
				sensorLoopDelayString_ = arg.substring(18);
				
				if (Float.parseFloat(sensorLoopDelayString_) < 10 || Float.parseFloat(sensorLoopDelayString_) > 5000 || sensorLoopDelayString_.contains(".")) {
					if (!silentMode_) System.out.println("The sensor loop delay must be greater than 10 and less than 5000. Defaulting to 1000...");
				}
				else {
					sensorLoopDelay_ = Integer.parseInt(sensorLoopDelayString_);
					if (!silentMode_) System.out.println("Sensor Loop Delay Override (default: 500): " + sensorLoopDelayString_);
				}	
			}
			
			if (arg.startsWith("--forecast")) {
				reportTomorrowWeather = true;
				if (!silentMode_) System.out.println("Displaying tomorrow's forecast, omit if you want today's weather\n");
			}
			
			if (arg.startsWith("--gifp=")) { //not using this one
				gifFullPath_ = arg.substring(7);
				if (!silentMode_) System.out.println("gif file name: " + gifFullPath_);
				gifModeInternal = true;
				validCommandLine = true;
				z++;
			}	
			
			
			if (arg.startsWith("--gif=")) {
				
				gifFullPath_ = arg.substring(6);
				gifModeExternal = true;
				validCommandLine = true;
				z++;
				
				gifFilePath_ = FilenameUtils.getFullPath(gifFullPath_);  	//home/pi/pixel/atari2600/
				gifNameOnly_ = FilenameUtils.getName(gifFullPath_); 		//pacman.gif
				gifNameOnly_ = FilenameUtils.removeExtension(gifNameOnly_); //pacman
				
				//System.out.println("GIF File Path is: " + gifFilePath_); 	//to do remove this
				//System.out.println("GIF Name Only with no extension is: " + gifNameOnly_); //to do remove this
				
				if (OS.indexOf("win") >= 0) {   //if windows
					decodedDir_ = gifFilePath_ + "decoded\\";				// c:\max 2.10\pixel\atari2600\decoded\
				} else {
					decodedDir_ = gifFilePath_ + "decoded/";   				// /home/pi/pixel/atari2600/decoded/ 
				}
				
				File GIFfile = new File(gifFullPath_);
				if (GIFfile.exists() && !GIFfile.isDirectory()) { 
					//if (!silentMode_) System.out.println("GIF found, path=" + gifFullPath_);
			    }
				else {
					System.out.println("GIF not found, please check the spelling and/or path");
					System.out.println("You may enter a relative or absolute path to your GIF file, see examples below:");
					System.out.println("  Pitfall.gif (if in the same directory)");
					System.out.println("  /atari2600/Pitfall.gif (relative path)");
					System.out.println("  /home/pi/pixel/atari2600/Pitfall.gif (absolute path Linux or Mac)");
					System.out.println("  c:\\Max 2.10\\pixelatari2600\\Pitall.gif (absolute path Windows)");
					System.out.println("If the GIF filename has spaces or special characters, surround in quotes");
					System.out.println("Exiting...");
					//exit(1,200); //this did not exit
					System.exit(1);
				}
			}	
			
			if (arg.startsWith("--png=")) {
				
				pngFullPath_ = arg.substring(6);
				pngModeExternal = true;
				validCommandLine = true;
				z++;
				
				pngFilePath_ = FilenameUtils.getFullPath(pngFullPath_);  	//home/pi/pixel/atari2600/
				pngNameOnly_ = FilenameUtils.getName(pngFullPath_); 		//pacman.png
				pngNameOnly_ = FilenameUtils.removeExtension(pngNameOnly_); //pacman
				
				//System.out.println("GIF File Path is: " + gifFilePath_); 	//to do remove this
				//System.out.println("GIF Name Only with no extension is: " + gifNameOnly_); //to do remove this
				
			/*
			 * if (OS.indexOf("win") >= 0) { //if windows decodedDir_ = gifFilePath_ +
			 * "decoded\\"; // c:\max 2.10\pixel\atari2600\decoded\ } else { decodedDir_ =
			 * gifFilePath_ + "decoded/"; // /home/pi/pixel/atari2600/decoded/ }
			 */
				
				File PNGfile = new File(pngFullPath_);
				if (PNGfile.exists() && !PNGfile.isDirectory()) { 
					//if (!silentMode_) System.out.println("GIF found, path=" + gifFullPath_);
			    }
				else {
					System.out.println("PNG not found, please check the spelling and/or path");
					System.out.println("You may enter a relative or absolute path to your PNG file, see examples below:");
					System.out.println("  Pitfall.png (if in the same directory)");
					System.out.println("  /atari2600/Pitfall.png (relative path)");
					System.out.println("  /home/pi/pixel/atari2600/Pitfall.png (absolute path Linux or Mac)");
					System.out.println("  c:\\Max 2.10\\pixelatari2600\\Pitall.png (absolute path Windows)");
					System.out.println("If the PNG filename has spaces or special characters, surround in quotes");
					System.out.println("Exiting...");
					//exit(1,200); //this did not exit
					System.exit(1);
				}
			}	
			
			if (arg.startsWith("--loop=")) {
				loopString = arg.substring(7);
				if (!silentMode_) System.out.println("Number of times to loop = " + loopString);
				loopMode = true;
				setLoopInt(Integer.parseInt(loopString));
			}
			
			if (arg.startsWith("--framedelay=")) {
				frameDelayString = arg.substring(13);
				setFrameDelayOverride(true);
				
				if (Float.parseFloat(frameDelayString) < 1 || frameDelayString.contains(".")) {
					setFrameDelayOverride(false);
					if (!silentMode_) System.out.println("The frame delay must be a whole number between 1 and 1000 (milliseconds), not overriding the frame deay");
				}
				else {
					setFrameDelayInt(Integer.parseInt(frameDelayString));
					if (!silentMode_) System.out.println("Frame delay override specified at " + frameDelayString + " milliseconds");
				}
				
				if (getFrameDelayInt() > 1000) {
					setFrameDelayInt(1000);
					System.out.println("Sorry, the slowest frame delay possible is 1000 ms or 1 second, setting the frame delay to 1000 ms");
				}
			}
			
			if (arg.startsWith("--text=")) {
				scrollingText_ = arg.substring(7);
				if (!silentMode_) System.out.println("Scrolling Text Mode Selected");
				scrollingTextMode = true;
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--port=")) {
				port_ = arg.substring(7);
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--speed=")) {
				scrollingTextSpeed_ = arg.substring(8);
				//System.out.println("scrolling text speed: " + scrollingTextSpeed_);
				scrollingTextDelay_ = Integer.parseInt(scrollingTextSpeed_);
			}	
			
			if (arg.startsWith("--smooth=")) {
				String scrollingSmoothnessString = arg.substring(9);
				scrollingSmoothness_ = Integer.parseInt(scrollingSmoothnessString);
			}	
			
			
			if (arg.startsWith("--twitter=")) {
				twitterSearchString = arg.substring(10);
				if (!silentMode_) System.out.println("In Twitter mode with search term: " + twitterSearchString);
				scrollingTextMode = true;
				twitterMode = true;
				validCommandLine = true;
				z++;
				
				//originally was here, moved to the IOIO setup routine
				/*String tweetSearchTerm = twitterSearchString;
			   	query = new Query(tweetSearchTerm);
			        
					try {
						result = twitter.search(query);
						System.out.println("Number of matched tweets: " + result.getCount());
					} catch (TwitterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for (Status status : result.getTweets()) {
						
						if (filterTweets) { // then we don't want @ mentions or http:// tweets
							if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
								
								//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
								twitterResult = status.getText();
								System.out.println(status.getText());
							}
						}
						
						else {
							if (!status.getText().contains("RT")) {
								
								//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
								twitterResult = status.getText();
								System.out.println(status.getText()); //it's the last one so let's display it
							}
						}
			        }
					
					if (twitterResult == null) {
						twitterResult = "No match found for Twitter search: " + twitterSearchString;
					}*/
			}	
			
			if (arg.startsWith("--interval=")) {
				twitterIntervalString = arg.substring(11);
				
				if (!twitterIntervalString.matches("[0-9]+")) {
					if (!silentMode_) System.out.println("The Twitter search interval must be a whole number between 10 and 86400 (24 hours) in seconds. Defaulting to a Twitter search term refresh of 30 seconds...");
				}
				else {
					if (Float.parseFloat(twitterIntervalString) < 10 || twitterIntervalString.contains(".") || Float.parseFloat(twitterIntervalString) > 86400) {
						if (!silentMode_) System.out.println("The Twitter search interval must be a whole number between 10 and 86400 (24 hours) in seconds. Defaulting to a Twitter search term refresh of 30 seconds...");
					}
					else {
						twitterIntervalInt = Integer.parseInt(twitterIntervalString);
						if (!silentMode_) System.out.println("Twitter text from the search term will refresh every " + twitterIntervalString + " seconds");
					}
				}
			}	
			
			if (arg.startsWith("--filtertweets=")) {
				filterTweets = true;
				if (!silentMode_) System.out.println("Filtering Tweets that have RT, contain http:// or @");
			}
			
			//fix this later, not working fro some reason, twitterIntervalString is null even if --interval is there
			/*if (twitterMode && twitterIntervalString == null) { //just prompting the user that they can specifiy a twitter refresh interval
				System.out.println("You didn't specify a Twitter search term refresh interval so we'll default to 60 seconds.\nNext time you can add the option --interval=x where x is a whole number in seconds between 10 and 86400 (24 hours)");
			}*/
			
			if (arg.startsWith("--fontsize=")) {
				scrollingTextFontSizeString = arg.substring(11);
				//System.out.println("font size is: " + scrollingTextFontSizeString);
				scrollingTextFontSize_ = Integer.parseInt(scrollingTextFontSizeString);
			}	
			
			if (arg.startsWith("--color=")) {
				scrollingTextColor_ = arg.substring(8);
				//System.out.println("scrolling text color is: " + scrollingTextFontSizeString);
			}
			
			if (arg.startsWith("--offset=")) {
				String fontOffsetString = arg.substring(9);
				fontOffset = Integer.parseInt(fontOffsetString);
				//System.out.println("scrolling text color is: " + scrollingTextFontSizeString);
			}
			
			if (arg.startsWith("--image=")) {
				if (!silentMode_) System.out.println("image file name: " + gifFullPath_);
				gifModeExternal = true;
				validCommandLine = true;
				z++;
			}	
			
			if (arg.startsWith("--write")) {
				setWriteMode(true);
				//if (!silentMode_) System.out.println("PIXEL is in write mode\n");
			}
			
			if (arg.startsWith("--16x32")) {
				ledMatrixType = 1;
				selectedLEDMatrix = "32x16";
			}
			
			if (arg.startsWith("--adafruit32x16")) {
				ledMatrixType = 1;
				selectedLEDMatrix = "Adafruit 32x16";
			}
			
			if (arg.startsWith("--32x16")) {
				ledMatrixType = 1;
				selectedLEDMatrix = "Adafruit 32x16";
			}
			
			if (arg.startsWith("--adafruit32x32")) {
				ledMatrixType = 11;
				selectedLEDMatrix = "Adafruit 32x32";
			}
			
			if (arg.startsWith("--32x32")) {
				ledMatrixType = 11;
				selectedLEDMatrix = "Adafruit 32x32";
			}
			
			if (arg.startsWith("--adafruit32x32colorswap")) {
				ledMatrixType = 12;
				selectedLEDMatrix = "Adafruit 32x32 Color Swap";
			}
			
			if (arg.startsWith("--32x32colorswap")) {
				ledMatrixType = 12;
				selectedLEDMatrix = "Adafruit 32x32 Color Swap";
			}
			
			if (arg.startsWith("--64x16")) {
				ledMatrixType = 17;
				selectedLEDMatrix = "64x16";
			}
			
			if (arg.startsWith("--adafruit64x16")) {
				ledMatrixType = 17;
				selectedLEDMatrix = "Adafruit 64x16";
			}
			
			if (arg.startsWith("--superpixel")) {
				ledMatrixType = 10;
				selectedLEDMatrix = "Old SUPER PIXEL 64x64";
			}
			
			if (arg.startsWith("--adafruit64x64")) {
				ledMatrixType = 14;
				selectedLEDMatrix = "Adafruit 64x64";
			}
			
			if (arg.startsWith("--64x64")) {
				ledMatrixType = 14;
				selectedLEDMatrix = "Adafruit 64x64";
			}
			
			if (arg.startsWith("--adafruit64x32")) {
				ledMatrixType = 13;
				selectedLEDMatrix = "Adafruit 64x32";
			}
			
			if (arg.startsWith("--64x32")) {
				ledMatrixType = 13;
				selectedLEDMatrix = "Adafruit 64x32";
			}
			
			if (arg.startsWith("--adafruit64x32m")) {
				ledMatrixType = 18;
				selectedLEDMatrix = "Adafruit 64x32 Mirrored";
			}
			
			if (arg.startsWith("--64x32m")) {
				ledMatrixType = 18;
				selectedLEDMatrix = "Adafruit 64x32 Mirrored";
			}
			
			if (arg.startsWith("--seeed64x32m")) {
				ledMatrixType = 6;
				selectedLEDMatrix = "Seeed 64x32 Mirrored";
			}
			
			if (arg.startsWith("--adafruit256x16")) {
				ledMatrixType = 19;
				selectedLEDMatrix = "Adafruit 256x16";
			}
			
			if (arg.startsWith("--256x16")) {
				ledMatrixType = 19;
				selectedLEDMatrix = "Adafruit 256x16";
			}
			
			if (arg.startsWith("--adafruit32x32m")) {
				ledMatrixType = 20;
				selectedLEDMatrix = "Adafruit 32x32 Mirrored";
			}
			
			if (arg.startsWith("--32x32m")) {
				ledMatrixType = 20;
				selectedLEDMatrix = "Adafruit 32x32 Mirrored";
			}
			
			if (arg.startsWith("--adafruit32x324m")) {
				ledMatrixType = 21;
				selectedLEDMatrix = "Adafruit 32x32 4X Mirrored";
			}
			
			if (arg.startsWith("--32x324m")) {
				ledMatrixType = 21;
				selectedLEDMatrix = "Adafruit 32x32 4X Mirrored";
			}
			
			if (arg.startsWith("--adafruit128x16")) {
				ledMatrixType = 22;
				selectedLEDMatrix = "Adafruit 128x16";
			}
			
			if (arg.startsWith("--128x16")) {
				ledMatrixType = 22;
				selectedLEDMatrix = "Adafruit 128x16";
			}
			
			if (arg.startsWith("--adafruit128x32")) {
				ledMatrixType = 15;
				selectedLEDMatrix = "Adafruit 128x32";
			}
			
			if (arg.startsWith("--128x32")) {
				ledMatrixType = 15;
				selectedLEDMatrix = "Adafruit 128x32";
			}
			
			if (arg.startsWith("--adafruit64x32colorswap")) {
				ledMatrixType = 24;
				selectedLEDMatrix = "Adafruit 64x32 Color Swap";
			}
			
			if (arg.startsWith("--64x32colorswap")) {
				ledMatrixType = 24;
				selectedLEDMatrix = "Adafruit 64x32 Color Swap";
			}
			
			if (arg.startsWith("--adafruit64x64colorswap")) {
				ledMatrixType = 25;
				selectedLEDMatrix = "Adafruit 64x64 Color Swap";
			}
			
			if (arg.startsWith("--64x64colorswap")) {
				ledMatrixType = 25;
				selectedLEDMatrix = "Adafruit 64x64 Color Swap";
			}
			
			if (arg.startsWith("--32x32misc1")) {
				ledMatrixType = 23;
				selectedLEDMatrix = "32x32 Random Pixel Order #1";
			}
			
			if (arg.startsWith("--32x32misc2")) {
				ledMatrixType = 2;
				selectedLEDMatrix = "32x32 Random Pixel Order #2";
			}
			
			if (arg.startsWith("--32x32misc3")) {
				ledMatrixType = 3;
				selectedLEDMatrix = "32x32 Random Pixel Order #3";
			}
			
			if (arg.startsWith("--zip=")) {
				zip_ = arg.substring(6);
				if (!silentMode_) System.out.println("zip code: " + zip_);
				validCommandLine = true;
				zipMode = true;
				weatherMode = true;
				z++;
			} else if (arg.startsWith("--zmw")) {
				//zmw_ = arg.substring(0,7);
				zmw_ = arg.substring(6);
				if (!silentMode_) System.out.println("zmw: " + zmw_);
				validCommandLine = true;
				zipMode = false;
				weatherMode = true;
				z++;
			}
			
			if (arg.startsWith("--apikey=")) {
				apikey_ = arg.substring(9);
				if (!silentMode_) System.out.println("Wunderground Api key: " + apikey_);
				userAPIKey = true;
			}	
			
			if (arg.startsWith("--daemon")) { //not using this one
				if (!silentMode_) System.out.println("Daemon Mode");
				backgroundMode = true;
			}	
			
			if (arg.startsWith("--debug")) { //not using this one
				if (!silentMode_) System.out.println("Debug Mode");
				debug_ = true;
			}	
			
			///***************** 
			
			if (arg.startsWith("--qbuserid=")) {  //8 characters
				setQuickBaseUserID(arg.substring(11));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase User ID: " + getQuickBaseUserID());
			}
				
			if (arg.startsWith("--qbpassword=")) { //10 characters
				setQuickBaseUserPassword(arg.substring(13));
				setQ(getQ() + 1);
			}
			
			if (arg.startsWith("--qbdomain=")) { //8 characters
				setQuickBaseDomain(arg.substring(11));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase DB Domain: " + getQuickBaseDomain());
			}
			
			if (arg.startsWith("--qbdatabase=")) { 
				setQuickBaseDBID(arg.substring(13));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase DB ID: " + getQuickBaseDBID());
			}
			
			if (arg.startsWith("--qbqueryfield=")) { //12 characters
				setQuickBaseQueryFieldID(arg.substring(15));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase Field ID to Query: " + getQuickBaseQueryFieldID());
			}
			
			if (arg.startsWith("--qbsearchstring=")) { 
				String quickBaseSearchTermForDescriptionFieldString = arg.substring(17);
				setQ(getQ() + 1);
				setQuickBaseSearchTermForDescriptionField(new String(quickBaseSearchTermForDescriptionFieldString.replace(" ", "%20"))); //doesn't like it if there are spaces so need to replace space with %20
				if (!silentMode_) System.out.println("QuickBase Search String: " + getQuickBaseSearchTermForDescriptionField());
			}
			
			if (arg.startsWith("--qbreturnfields=")) { 
				setQuickBaseReturnFields(arg.substring(17));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase Field IDs to return: " + getQuickBaseReturnFields());
			}
			
			if (arg.startsWith("--qbtoken=")) { 
				setQuickBaseToken(arg.substring(10));
				setQ(getQ() + 1);
				if (!silentMode_) System.out.println("QuickBase Application Token #: " + getQuickBaseToken());
			}
			
			if (arg.startsWith("--quickbase")) { 
				if (!silentMode_) System.out.println("QuickBase Mode");
				quickbaseMode = true;
				validCommandLine = true;
				z++;
				
				//had to take this out from here and add to the IOIO setup loop, this was getting called before the above parameters were done
				
				/*try {
					runQuickBase();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
			
			if (arg.startsWith("--snowuserid=")) {  
				snowUserID = arg.substring(13);
				s++;
				if (!silentMode_) System.out.println("SNOW User ID: " + snowUserID);
			}
				
			if (arg.startsWith("--snowpassword=")) { 
				snowUserPassword = arg.substring(15);
				s++;
			}
			
			if (arg.startsWith("--snowdomain=")) { 
				snowDomain = arg.substring(13);
				s++;
				if (!silentMode_) System.out.println("SNOW Domain: " + snowDomain);
			}
		
		//Edited by :::::Sucheta ::::to make dynamic
			
			  if (arg.startsWith("--snowgroupid=")) { 
				snowGUID = arg.substring(14);        
			            snowGroupGUID = new ArrayList<String>(Arrays.asList(snowGUID.split(",")));  
				  s++;
			}
			
			if (arg.startsWith("--snowgroupname=")) { 
				snowname = arg.substring(16);
				snowGroupName = new ArrayList<String>(Arrays.asList(snowname.split(",")));
				
				s++;
			}
			
			//:::::::: 
						
			if (arg.startsWith("--snow")) { 
				if (!silentMode_) System.out.println("Service Now Mode");
				snowMode = true;
				validCommandLine = true;
				z++;
				
			}
			
			if (arg.startsWith("--snowrefresh=")) {
				String snowRefreshIntervalString = arg.substring(14);
				
				if (!snowRefreshIntervalString.matches("[0-9]+")) {
					if (!silentMode_) System.out.println("The Service Now refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 20 scrolling messages...");
				}
				else { //ok it's only a number so now let's make sure it's within the right range
					if (Float.parseFloat(snowRefreshIntervalString) < 1 || snowRefreshIntervalString.contains(".")) {
						if (!silentMode_) System.out.println("The Service Now refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 20 scrolling messages...");
					}
					else {
						snowRefreshInterval = Integer.parseInt(snowRefreshIntervalString);
						if (!silentMode_) System.out.println("Service Now will refresh every " + snowRefreshIntervalString + " time(s) a scrolling message completes");
					}
				}
			}
			
			if (arg.startsWith("--qbrefresh=")) {
				String quickBaseRefreshIntervalString = arg.substring(12);
				
				if (!quickBaseRefreshIntervalString.matches("[0-9]+")) {
					if (!silentMode_) System.out.println("The QuickBase refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 5 scrolling messages...");
				}
				else { //ok it's only a number so now let's make sure it's within the right range
					if (Float.parseFloat(quickBaseRefreshIntervalString) < 1 || quickBaseRefreshIntervalString.contains(".")) {
						if (!silentMode_)  System.out.println("The QuickBase refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 5 scrolling messages...");
					}
					else {
						setQuickBaseRefreshInterval(Integer.parseInt(quickBaseRefreshIntervalString));
						if (!silentMode_) System.out.println("QuickBase will refresh every " + quickBaseRefreshIntervalString + " time(s) a scrolling message completes");
					}
				}
			}
			
			if (arg.startsWith("--stock=")) { 
				stockSymbols = arg.substring(8);
				if (!silentMode_) System.out.println("Stock symbol requested: " + stockSymbols);
				stockMode = true;
			}
			
			if (arg.startsWith("--compliments")) { 
				stockSymbols = arg.substring(13);
				if (!silentMode_) System.out.println("Compliments mode turned on, please make sure you enabled --proximity too");
				complimentsMode = true;
			}
			
			
			if (validCommandLine == false) {
				throw new BadArgumentsException("Unexpected option: " + arg);
			}
			
		}
		
	public static class BadArgumentsException extends Exception {
			private static final long serialVersionUID = -5730905669013719779L;

			public BadArgumentsException(String message) {
				super(message);
			}
		}
		
	protected void run(String[] args) throws IOException {		
		//System.out.println("Pixel integration with delayed run() via sleep.");
		
		if (!silentMode_ && relativePath_) System.out.println("GIF found using direct path, file name is: " + gifFullPath_);
		if (!silentMode_ && absolutePath_) System.out.println("GIF found using absolute path, file name is: " + gifFullPath_);
		if (!silentMode_ && port_ != null) System.out.println("Specified Port: " + port_);
		if (!silentMode_) System.out.println("PIXEL is in write mode\n");
	
		if (backgroundMode) {
			while(stayConnected)
				            {
				                long duration = 1000 * 60 * 1;
				                try
				                {
				                    Thread.sleep(duration);
				                } 
				                catch (InterruptedException ex)
				                {
				                    String message = "Error sleeping for Pixel initialization: " + ex.getMessage();
				                   // logger.log(Level.INFO, message, ex);
				                   // System.out.println(Level.INFO, message, ex);
				                }
				            }
		}
		else {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));			
		boolean abort = false;
		String line;
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("t")) {
				//ledOn_ = !ledOn_;
			} else if (line.equals("n")) {
				//ledOn_ = true;
			} else if (line.equals("new")) {
				System.out.println("new input coming in");
			} else if (line.equals("q")) {
				abort = true;
				System.exit(1);
			} else {
				//System.out.println("Unknown input. t=toggle, n=on, f=off, q=quit.");
				System.out.println("Unknown input. q=quit.");
			}
		} 		
	}
}
	
	
	 private static void streamGIF(boolean writeMode) 
	    {
		
		try {
		
			if (pixel.GIFNeedsDecoding(gifFilePath_, gifNameOnly_, currentResolution, gifFullPath_,decodedDir_) == true) {	
				
				if (!silentMode_)  System.out.println("Decoding " + gifFullPath_);
				try {
					
					pixel.decodeGIF(gifFullPath_, gifNameOnly_, currentResolution,KIND.width,KIND.height,decodedDir_);
					
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				if (!silentMode_) System.out.println(gifFullPath_ + " is already decoded, skipping decoding step");
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    setGIFnumFrames(pixel.getDecodednumFrames(gifNameOnly_, decodedDir_));
	    setGIFresolution(pixel.getDecodedresolution(gifNameOnly_, decodedDir_));
	    
	    if (isFrameDelayOverride()) { 
	    	setGIFselectedFileDelay(getFrameDelayInt()); //use the override the user specified from the command line --framedelay=x
	    	setGIFfps(1000.f / getGIFselectedFileDelay());
	    }
	    else { //no override so just use as is
	    	 setGIFselectedFileDelay(pixel.getDecodedframeDelay(gifNameOnly_, decodedDir_));  
	    	 setGIFfps(pixel.getDecodedfps(gifNameOnly_, decodedDir_)); //get the fps
	    }
	    
	    if (!silentMode_) System.out.println(gifFullPath_ + " contains " + getGIFnumFrames() + " frames with a " + getGIFselectedFileDelay() + "ms frame delay");
	    if (!silentMode_) System.out.println("Selected LED Matrix: " + selectedLEDMatrix);
		//****** Now let's setup the animation ******
		    
		   // animation_name = selectedFileName;
		    i = 0;
		   // numFrames = selectedFileTotalFrames;
		    
	            stopExistingTimer(); //is this needed, probably not		
	    			if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //in write mode, we don't need a timer because we don't need a delay in between frames, we will first put PIXEL in write mode and then send all frames at once
	    					pixel.interactiveMode();
	    					//send loading image
	    					pixel.writeMode(getGIFfps()); //need to tell PIXEL the frames per second to use, how fast to play the animations
	    					if (!silentMode_) System.out.println("Writing to PIXEL's microSD card..."); 
	    					  int y;
	    				    	 
	    				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
	    				      for (y=0;y<getGIFnumFrames();y++) { //Al removed the -1, make sure to test that!!!!!
	    				 		
	    				 			//framestring = "animations/decoded/" + animation_name + ".rgb565";
	    				 			//System.out.println("Writing to PIXEL: Frame " + y + "of " + GIFnumFrames + " Total Frames");

	    				    	  if (!silentMode_) System.out.println("Writing frame " + y);
	    				 		  pixel.SendPixelDecodedFrame(gifNameOnly_, y, getGIFnumFrames(), getGIFresolution(), KIND.width,KIND.height, decodedDir_);
	    				   	  } //end for loop
	    				      
	    					pixel.playLocalMode(); //now tell PIXEL to play locally
	    					if (!silentMode_) System.out.println("Writing complete, now displaying...");
	    					//System.exit(0);
	    					exit(0,200);
	    			}
	    			else {   //we're not writing so let's just stream
	            
	            stopExistingTimer(); //is this needed, probably no
	    				   
	    				   ActionListener AnimateTimer = new ActionListener() {

	    	                    public void actionPerformed(ActionEvent actionEvent) {
	    	                    
	    	               		 	i++;
	    	               			
	    	               			if (i >= getGIFnumFrames() - 1) 
	    	               			{
	    	               			    i = 0;
	    	               			    loopCounter++;
	    	               			    
	    	               			    if (loopMode == true && loopCounter >=getLoopInt()) { //then we are done and let's exit out
	    	               			    	if (timer != null) timer.stop();
	    	               			    	if (!silentMode_) System.out.println("We've looped " + loopCounter + " times and are now exiting, you may omit the --loop command line option if you want to loop indefinitely");
	    	               			    	//System.exit(0);
	    	               					exit(0,200);
	    	               			    }
	    	               			}
	    	               		
	    	               		 pixel.SendPixelDecodedFrame(gifNameOnly_, i, getGIFnumFrames(), getGIFresolution(), KIND.width,KIND.height, decodedDir_);
 	                    }
 	                };
	    				   
	    				   timer = new Timer(getGIFselectedFileDelay(), AnimateTimer); //the timer calls this function per the interval of fps
	    				   timer.start();
	    			}    
	  }
	 
	 
	 private static void handlePNG(boolean writeMode) throws IOException 
	    {
		
		 //writeImagetoMatrix(BufferedImage originalImage, int frameWidth, int frameHeight) throws ConnectionLostException
		 
		 //to do add the write logic
		 
		 	String FilePathPNG = pngFullPath_;
		 	File FilePNG = new File(FilePathPNG);
		 	
		 	if (FilePNG.exists()) {
		 
			 	URL url = null; 
		        BufferedImage image;
		        url = FilePNG.toURI().toURL();
		        image = ImageIO.read(url);
		        System.out.println("PNG image found: " + url.toString());
		        //Pixel pixel = application.getPixel();
		        //pixel.stopExistingTimer();
		        
		        if (pixelHardwareID.substring(0,4).equals("PIXL") && writeMode) {
		        
			        pixel.interactiveMode();
			        pixel.writeMode(10);
			        
			        try {
						pixel.writeImagetoMatrix(image, KIND.width,KIND.height);
					} catch (ConnectionLostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} //to do add save parameter here
			        
			        pixel.playLocalMode();
			        if (!silentMode_) System.out.println("Writing complete, now displaying...");
					exit(0,200);
		        }
		        
		        else {
		        	  try {
							pixel.writeImagetoMatrix(image, KIND.width,KIND.height);
						} catch (ConnectionLostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} //to do add save parameter here
		        }
	        
		 	}
		 	
		 	
		 
		 	
		 	
		 	
		
	  }
	 
	 
	 
	 private static void CheckAndStartTimer() {
	    //TO DO make sure timer was initialized prior	
		 
		 if (pixel != null && !timer.isRunning()) {
		  		  pixel.interactiveMode(); //put into interactive mode as could have been stuck in local mode after a write
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
	/* private static void sendFramesToPIXEL()  //NOT USED
	 { 
   	  int y;
    	 
   	  for (y=0;y<getNumFrames()-1;y++) { //let's loop through and send frame to PIXEL with no delay
 		
 		//framestring = "animations/decoded/" + animation_name + "/" + animation_name + y + ".rgb565";
 		setFramestring("animations/decoded/" + getAnimation_name() + ".rgb565");
 		
 			System.out.println("writing to PIXEL frame: " + getFramestring());

 		//  pixel.loadRGB565(framestring);
 			
 		   pixel.SendPixelDecodedFrame(gifNameOnly_, i, getNumFrames(), selectedFileResolution,KIND.width,KIND.height,decodedDir_);
   	  } //end for loop
     	 
     }*/
	    
	    protected static void stopExistingTimer()
	    {
	        if (timer != null && timer.isRunning() )
	        {
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
	         case 11:
	    	   KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
                frame_length = 2048;
                currentResolution = 32; 
                break;	 
             case 12:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_ColorSwap;
                frame_length = 2048;
                currentResolution = 32; 
                break;	 	 
             case 13:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32;
                frame_length = 4096;
                currentResolution = 64; 
                break;	
             case 14:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64;
                frame_length = 8192;
                currentResolution = 128; 
                break;
             case 15:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_128x32;
                frame_length = 8192;
                currentResolution = 12832; 
                break;	 	 	
            case 16:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x128;
                frame_length = 8192;
                currentResolution = 128; 
                break;	
            case 17:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x16;
                frame_length = 2048;
                currentResolution = 6416; 
                break;	 	 
            case 18:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32_MIRRORED;
                frame_length = 8192;
                currentResolution = 128999;   //had to add the 999 to indicate mirroring because it won't re-encode if for example 4 32x32 mirror is selected and then 64x32 mirror 
                break;	 
            case 19:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_256x16;
                frame_length = 8192;
                currentResolution = 25616; 
                break;	
            case 20:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_MIRRORED;
                frame_length = 4096;
                currentResolution = 64999; //had to make unique
                break;	
            case 21:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_4X_MIRRORED;
                frame_length = 8192;
                currentResolution = 128; 
                break;	
            case 22:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_128x16;
                frame_length = 4096;
                currentResolution = 12816; 
                break;	
            case 23:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ALIEXPRESS_RANDOM1_32x32;
                frame_length = 2048;
                currentResolution = 32; 
                break;	
            case 24:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32_ColorSwap;
                frame_length = 4096;
                currentResolution = 64; 
                break;	
            case 25:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64_ColorSwap;
                frame_length = 8192;
                currentResolution = 128; 
                break;
		    	 
		     default:	    		 
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32; //v2 as the default
		    	 frame_length = 2048;
		    	 currentResolution = 32;
	     }
		 
		 //System.out.println("CurrentResolution is: " + currentResolution + "\n");
	 }
	 
	 static void exit (final int status, int maxDelayMillis) {
		 
		 ActionListener exitTimer_ = new ActionListener() {
				
             public void actionPerformed(ActionEvent actionEvent) {
            	//System.out.println("Exiting...");
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
	     
	 protected static String returnXMLNodeValue(String targetNode, String xmlString) {
		 
		  /* SNOW XML EXAMPLE
	        <?xml version="1.0" encoding="UTF-8"?>
	        <response>
	        <result>
	        <stats>
	        	<count>20</count>
	        	<avg><priority>2.5500</priority></avg>
	        </stats>
	        <groupby_fields>
	        	<field>assignment_group</field>
	        	<value>MSI SAP CRM</value>
	        </groupby_fields>
	        </result>
	        </response>		    */
		 
		 String xmlValue = null;
		 
		 try {
	            DocumentBuilderFactory dbf =
	                DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(xmlString));

	            Document doc = db.parse(is);
	            NodeList nodes = doc.getElementsByTagName("stats");
	          
	            for (int i = 0; i < nodes.getLength(); i++) {
	               Element element = (Element) nodes.item(i);

	               NodeList name = element.getElementsByTagName(targetNode);
	               Element line = (Element) name.item(0);
	               if (!silentMode_) System.out.println("Count: " + getCharacterDataFromElement(line));
	               xmlValue = getCharacterDataFromElement(line);
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		 return xmlValue;
	 }
	 
	 private static String getCharacterDataFromElement(Element e) {
		    Node child = e.getFirstChild();
		    if (child instanceof CharacterData) {
		       CharacterData cd = (CharacterData) child;
		       return cd.getData();
		    }
		    return "?";
		  } 
static void CheckandRunMode() throws IOException {
		 if (gifModeExternal == true) {
							
					if (isWriteMode() == true) {
						streamGIF(true); //write to PIXEL's SD card
					}
					else {
						streamGIF(false);  //steam the GIF but don't write
					}
		}
		 else if (pngModeExternal == true) {
			 
					 if (isWriteMode() == true) {
							handlePNG(true); //write to PIXEL's SD card
					}
					else {
							handlePNG(false);  //steam the GIF but don't write
					}
		 }
				
		else if (weatherMode == true){
					//old Yahoo Weather API
					//weather.getWeather();
					//weather.runWeatherAnimations();
					
					//old Wunderground Weather API
					//Weather_Wunderground.getWeather();
					//Weather_Wunderground.runWeatherAnimations();
					
				   //updated now with the Open Weather Map API
						
					try {
						Weather_openweathermap.getWeather();
						Weather_openweathermap.runWeatherAnimations();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		
				
				else if (scrollingTextMode == true) {
					
					if (twitterMode) { //so we're in twitter mode so let's start the twitter search timer
						runTwitter();
						twitterTimer = new Timer(twitterIntervalInt * 1000, TwitterTimer);
						twitterTimer.start();
						scroll.scrollText(twitterResult, scrollingTextColor_, 0,isWriteMode()); //0 menas loop forever
					}
					else {
						scroll.scrollText(scrollingText_, scrollingTextColor_, 0, isWriteMode()); //write is not supported, just stream right now
					}
				}
				
				else if (quickbaseMode == true) {
					//TO DO add the timer in here just like we did with Twitter
					//TO DO I suppose we'll also need to ensure that the quickbase data is retrieved before this IOIO Setup starts
					
					try {
							base.runQuickBase();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
		 
				else if(snowMode == true) {
				
						//TO DO right now this is hard coded to show two service now queue's / groups, would be better to make this dynamic depending on the number of queues entered in the command line arguments
						
						//Edited By :Sucheta
									try {
										snow.runSNOW(snowGroupName, snowGroupGUID);									
										
									} catch (ParserConfigurationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SAXException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						              catch (MalformedURLException e) {
							            // TODO Auto-generated catch block
							          e.printStackTrace();
					               	} catch (IOException e) {
						            	// TODO Auto-generated catch block
						           	  e.printStackTrace();
						}
				}
		 
		 	ProxTriggerDone = true;	//had to add this because if the user holds hand in front of prox sensor continuously, for some reason we weren't resetting this prox trigger flag but this add here seems to fix it
		}
	 
	 
	 
	 private static void runTwitter() {
		 String tweetSearchTerm = twitterSearchString;
		   	query = new Query(tweetSearchTerm);
		        
				try {
					result = twitter.search(query);
					if (!silentMode_) System.out.println("Number of matched tweets: " + result.getCount());
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (Status status : result.getTweets()) {
					
					if (filterTweets) { // then we don't want @ mentions or http:// tweets
						if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							if (!silentMode_) System.out.println(status.getText());
			                //TO DO have seen some cases where no results are returned, need to fix that and also we should show a different tweet if no new one
						}
					}
					
					else {
						if (!status.getText().contains("RT")) {
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							if (!silentMode_) System.out.println(status.getText()); //it's the last one so let's display it
						}
					}
		        }
				
				if (twitterResult == null) {
					twitterResult = "No match found for Twitter search: " + twitterSearchString;
				}
	 }
		 
	

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			
			private AnalogInput prox_;
	  		float proxValue;
	  		
			@Override
			protected void setup() throws ConnectionLostException,
				InterruptedException {
		    	//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER);
	  			//pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			//IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
	  			
	  			//**** informational messages here *****
	  			//if (!silentMode_) System.out.println("GIF file name: " + gifFullPath_);
	  			//**************************************
	  			
  				PIXELConsole.this.ioiO = ioio_;
  				
  				setupEnvironment();  //here we set the PIXEL LED matrix type
  				
  				
  				prox_ = ioio_.openAnalogInput(proximityPin_);
                //pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);   //AL could not make this work, did a quick hack, Roberto probably can change back to the right way
                pixel.matrix = ioio_.openRgbLedMatrix(KIND);
                pixel.ioiO = ioio_;
                if (!silentMode_) System.out.println("Found PIXEL: " + pixel.matrix + "\n");
			
                try {
					CheckandRunMode();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			
			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				
				//this is where you can add your own sensor input code
				
				if (ProxSensor) {
					proxValue = prox_.read();
	  	  			proxValue = proxValue * 1000;	
	  	  			int proxInt = (int)proxValue;
	  	  			if (debug_) System.out.println("Prox Sensor Value: " + proxInt + " " + ProxTriggerDone); //we normally don't want to show this as it will clutter up the console when you are SSH'ing into your Pi
	  	  			
		  	  	///******** we will interrupt any scrolling text with a stock readout if that is turned on 
	  	  			if (proxInt > TriggerUpperThreshold_ && ProxTriggerDone == true && complimentsMode == true) {
	  	  				ProxTriggerDone = false;

	  	  			RunCompliment.runCompliments(); //get compliment with pseudo random number generator
	  	                
	  	                x = KIND.width * 2; //because we are interrupting an existing scrolling message, we have to reset the x position
	  	                loopCounter = 0;
	  	                scroll.scrollText(complimentString, compliementColor, 1,false);
	  	  			}
	  	  			//**********************************************************************************
	  	  		
	  	  			
	  	  			///******** we will interrupt any scrolling text with a stock readout if that is turned on 
	  	  			if (proxInt > TriggerUpperThreshold_ && ProxTriggerDone == true && stockMode == true) {
	  	  				ProxTriggerDone = false;
	  	  				
	  	  				try {
							stockPrice = yahooStock.getStock(stockSymbols);
							
							//show these values in the event the Yahoo API isn't returning results, ie 503 error
							stockPriceLast = stockPrice;
							stockChangeLast = getStockChange().toString();
							//***************
							
							String StockScrollColor = "green"; //what color for the scrolling stock ticket
		  	  				if (getStockChange().signum() > 0) {
		  	  					 StockScrollColor = "green";
		  	  				}
		  	  				else {
		  	  					 StockScrollColor = "red";
		  	  				}
		  	  				
		  	  				x = KIND.width * 2; //because we are interrupting an existing scrolling message, we have to reset the x position
		  	  				loopCounter = 0;
		  	  				scroll.scrollText(stockSymbols + ": " + stockPrice + " Change " + getStockChange().toString() + "%", StockScrollColor, 1,false);
						
	  	  				} catch (IOException e) {
	  	  					x = KIND.width * 2; //because we are interrupting an existing scrolling message, we have to reset the x position
	  	  					loopCounter = 0;
	  	  					scroll.scrollText(stockSymbols + ": " + stockPriceLast + " Change " + stockChangeLast + "%", "purple", 1,false); //purple means visually it's not the latest due to Yahoo API 503 error or some other problem
							// TODO Auto-generated catch block
							e.printStackTrace();
							ProxTriggerDone = true;
							//loopCounter = 0;
						}
	  	  				
	  	  				
	  	  			}
	  	  			//**********************************************************************************
	  	  			
				}
				
				Thread.sleep(sensorLoopDelay_);  //default is 500
			}
		};
	}
	
	protected void twitter_timer()
	{
		TwitterTimer = new ActionListener() 
	  	{
	  	    public void actionPerformed(ActionEvent evt) 
	  	    {
	  	  	String tweetSearchTerm = twitterSearchString;
		   	query = new Query(tweetSearchTerm);
		        
				try {
					result = twitter.search(query);
					if (!silentMode_) System.out.println("Number of matched tweets: " + result.getCount());
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (Status status : result.getTweets()) {
					
					if (filterTweets) { // then we don't want @ mentions or http:// tweets
						if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							if (!silentMode_) System.out.println(status.getText());
						}
					}
					
					else {
						if (!status.getText().contains("RT")) {
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							twitterResult = status.getText();
							if (!silentMode_) System.out.println(status.getText()); //it's the last one so let's display it
						}
					}
		        }
				//we've finished the timer tick so now we need to reset the scrolling timer	
				CheckAndStartTimer();
				
				/*   private void CheckAndStartTimer() {
				    	if (pixel != null && !timer.isRunning()) {
					  		  pixel.interactiveMode(); //put into interactive mode as could have been stuck in local mode after a write
					  		  timer.start();
					  	  }
				    }*/
	  	    }
	  	};
	}
	
	public void config(String args[])
	{
		ConfigurationBuilder cb = new ConfigurationBuilder(); //For Twitter
	   	 cb.setDebugEnabled(true)
	   	   .setOAuthConsumerKey("Ax6lCfg9Yf2Niab22e9SsY75b")
	   	   .setOAuthConsumerSecret("3isp024VgehfZ60HwbEcBt1ZZzPyoXseaWYmO4NXxoxefKY65A")
	   	   .setOAuthAccessToken("") // we don't need these right now as we are just calling public twitter searches
	   	   .setOAuthAccessTokenSecret("");
	   	    tf = new TwitterFactory(cb.build());
	   	    twitter = tf.getInstance();
			currentDir = System.getProperty("user.dir");
			
			if (args.length == 0) {
				usage.printUsage();
				System.exit(1);
			}
			
			try {
				parseArgs(args);
			} catch (BadArgumentsException e) {
				System.err.println("Invalid command line.");
				System.err.println(e.getMessage());
				usage.printUsage();
				System.exit(2);
			}
	}

	public static int getLoopInt() {
		return loopInt;
	}

	public static void setLoopInt(int loopInt) {
		PIXELConsole.loopInt = loopInt;
	}

	/**
	 * @return the snowBaseQuery
	 */
	public static String getSnowBaseQuery() {
		return snowBaseQuery;
	}

	/**
	 * @param snowBaseQuery the snowBaseQuery to set
	 */
	public static void setSnowBaseQuery(String snowBaseQuery) {
		PIXELConsole.snowBaseQuery = snowBaseQuery;
	}

	/**
	 * @return the snowPriorityQuery
	 */
	public static String getSnowPriorityQuery() {
		return snowPriorityQuery;
	}

	/**
	 * @param snowPriorityQuery the snowPriorityQuery to set
	 */
	public static void setSnowPriorityQuery(String snowPriorityQuery) {
		PIXELConsole.snowPriorityQuery = snowPriorityQuery;
	}

	/**
	 * @return the snowSLAExceededQuery
	 */
	public static String getSnowSLAExceededQuery() {
		return snowSLAExceededQuery;
	}

	/**
	 * @param snowSLAExceededQuery the snowSLAExceededQuery to set
	 */
	public static void setSnowSLAExceededQuery(String snowSLAExceededQuery) {
		PIXELConsole.snowSLAExceededQuery = snowSLAExceededQuery;
	}

	/**
	 * @return the snowGroupPriority
	 */
	public static String getSnowGroupPriority() {
		return snowGroupPriority;
	}

	/**
	 * @param snowGroupPriority the snowGroupPriority to set
	 */
	public static void setSnowGroupPriority(String snowGroupPriority) {
		PIXELConsole.snowGroupPriority = snowGroupPriority;
	}

	/**
	 * @return the snowGroupExceededSLA
	 */
	public static String getSnowGroupExceededSLA() {
		return snowGroupExceededSLA;
	}

	/**
	 * @param snowGroupExceededSLA the snowGroupExceededSLA to set
	 */
	public static void setSnowGroupExceededSLA(String snowGroupExceededSLA) {
		PIXELConsole.snowGroupExceededSLA = snowGroupExceededSLA;
	}

	/**
	 * @return the snowPIXELString
	 */
	public static String getSnowPIXELString() {
		return snowPIXELString;
	}

	/**
	 * @param snowPIXELString the snowPIXELString to set
	 */
	public static void setSnowPIXELString(String snowPIXELString) {
		PIXELConsole.snowPIXELString = snowPIXELString;
	}

	/**
	 * @return the weatherCode
	 */
	public static int getWeatherCode() {
		return weatherCode;
	}

	/**
	 * @param weatherCode the weatherCode to set
	 */
	public static void setWeatherCode(int weatherCode) {
		PIXELConsole.weatherCode = weatherCode;
	}

	/**
	 * @return the weatherCondition
	 */
	public static String getWeatherCondition() {
		return weatherCondition;
	}

	/**
	 * @param weatherCondition the weatherCondition to set
	 */
	public static void setWeatherCondition(String weatherCondition) {
		PIXELConsole.weatherCondition = weatherCondition;
	}

	/**
	 * @return the gIFfps
	 */
	public static float getGIFfps() {
		return GIFfps;
	}

	/**
	 * @param gIFfps the gIFfps to set
	 */
	public static void setGIFfps(float gIFfps) {
		GIFfps = gIFfps;
	}

	/**
	 * @return the animation_name
	 */
	public static String getAnimation_name() {
		return animation_name;
	}

	/**
	 * @param animation_name the animation_name to set
	 */
	public static void setAnimation_name(String animation_name) {
		PIXELConsole.animation_name = animation_name;
	}

	/**
	 * @return the u
	 */
	public static int getU() {
		return u;
	}

	/**
	 * @param u the u to set
	 */
	public static void setU(int u) {
		PIXELConsole.u = u;
	}

	/**
	 * @return the numFrames
	 */
	public static int getNumFrames() {
		return numFrames;
	}

	/**
	 * @param numFrames the numFrames to set
	 */
	public static void setNumFrames(int numFrames) {
		PIXELConsole.numFrames = numFrames;
	}

	/**
	 * @return the writeMode
	 */
	public static boolean isWriteMode() {
		return writeMode;
	}

	/**
	 * @param writeMode the writeMode to set
	 */
	public static void setWriteMode(boolean writeMode) {
		PIXELConsole.writeMode = writeMode;
	}

	/**
	 * @return the selectedFileName
	 */
	public static String getSelectedFileName() {
		return selectedFileName;
	}

	/**
	 * @param selectedFileName the selectedFileName to set
	 */
	public static void setSelectedFileName(String selectedFileName) {
		PIXELConsole.selectedFileName = selectedFileName;
	}

	/**
	 * @return the gIFnumFrames
	 */
	public static int getGIFnumFrames() {
		return GIFnumFrames;
	}

	/**
	 * @param gIFnumFrames the gIFnumFrames to set
	 */
	public static void setGIFnumFrames(int gIFnumFrames) {
		GIFnumFrames = gIFnumFrames;
	}

	/**
	 * @return the gIFresolution
	 */
	public static int getGIFresolution() {
		return GIFresolution;
	}

	/**
	 * @param gIFresolution the gIFresolution to set
	 */
	public static void setGIFresolution(int gIFresolution) {
		GIFresolution = gIFresolution;
	}

	/**
	 * @return the frameDelayOverride
	 */
	public static boolean isFrameDelayOverride() {
		return frameDelayOverride;
	}

	/**
	 * @param frameDelayOverride the frameDelayOverride to set
	 */
	public static void setFrameDelayOverride(boolean frameDelayOverride) {
		PIXELConsole.frameDelayOverride = frameDelayOverride;
	}

	/**
	 * @return the gIFselectedFileDelay
	 */
	public static int getGIFselectedFileDelay() {
		return GIFselectedFileDelay;
	}

	/**
	 * @param gIFselectedFileDelay the gIFselectedFileDelay to set
	 */
	public static void setGIFselectedFileDelay(int gIFselectedFileDelay) {
		GIFselectedFileDelay = gIFselectedFileDelay;
	}

	/**
	 * @return the frameDelayInt
	 */
	public static int getFrameDelayInt() {
		return frameDelayInt;
	}

	/**
	 * @param frameDelayInt the frameDelayInt to set
	 */
	public static void setFrameDelayInt(int frameDelayInt) {
		PIXELConsole.frameDelayInt = frameDelayInt;
	}

	/**
	 * @return the selectedFileDelay
	 */
	public static int getSelectedFileDelay() {
		return selectedFileDelay;
	}

	/**
	 * @param selectedFileDelay the selectedFileDelay to set
	 */
	public static void setSelectedFileDelay(int selectedFileDelay) {
		PIXELConsole.selectedFileDelay = selectedFileDelay;
	}

	/**
	 * @return the authXml
	 */
	public static InputStream getAuthXml() {
		return authXml;
	}

	/**
	 * @param authXml the authXml to set
	 */
	public static void setAuthXml(InputStream authXml) {
		PIXELConsole.authXml = authXml;
	}

	/**
	 * @return the quickBaseDomain
	 */
	public static String getQuickBaseDomain() {
		return quickBaseDomain;
	}

	/**
	 * @param quickBaseDomain the quickBaseDomain to set
	 */
	public static void setQuickBaseDomain(String quickBaseDomain) {
		PIXELConsole.quickBaseDomain = quickBaseDomain;
	}

	/**
	 * @return the quickBaseUserID
	 */
	public static String getQuickBaseUserID() {
		return quickBaseUserID;
	}

	/**
	 * @param quickBaseUserID the quickBaseUserID to set
	 */
	public static void setQuickBaseUserID(String quickBaseUserID) {
		PIXELConsole.quickBaseUserID = quickBaseUserID;
	}

	/**
	 * @return the quickBaseUserPassword
	 */
	public static String getQuickBaseUserPassword() {
		return quickBaseUserPassword;
	}

	/**
	 * @param quickBaseUserPassword the quickBaseUserPassword to set
	 */
	public static void setQuickBaseUserPassword(String quickBaseUserPassword) {
		PIXELConsole.quickBaseUserPassword = quickBaseUserPassword;
	}

	/**
	 * @return the quickBaseRootXMLNode
	 */
	public static String getQuickBaseRootXMLNode() {
		return quickBaseRootXMLNode;
	}

	/**
	 * @param quickBaseRootXMLNode the quickBaseRootXMLNode to set
	 */
	public static void setQuickBaseRootXMLNode(String quickBaseRootXMLNode) {
		PIXELConsole.quickBaseRootXMLNode = quickBaseRootXMLNode;
	}

	/**
	 * @return the quickbaseTicket
	 */
	public static String getQuickbaseTicket() {
		return quickbaseTicket;
	}

	/**
	 * @param quickbaseTicket the quickbaseTicket to set
	 */
	public static void setQuickbaseTicket(String quickbaseTicket) {
		PIXELConsole.quickbaseTicket = quickbaseTicket;
	}

	/**
	 * @return the quickBaseAuthSuccesful
	 */
	public static boolean isQuickBaseAuthSuccesful() {
		return quickBaseAuthSuccesful;
	}

	/**
	 * @param quickBaseAuthSuccesful the quickBaseAuthSuccesful to set
	 */
	public static void setQuickBaseAuthSuccesful(boolean quickBaseAuthSuccesful) {
		PIXELConsole.quickBaseAuthSuccesful = quickBaseAuthSuccesful;
	}

	/**
	 * @return the dataXml
	 */
	public static InputStream getDataXml() {
		return dataXml;
	}

	/**
	 * @param dataXml the dataXml to set
	 */
	public static void setDataXml(InputStream dataXml) {
		PIXELConsole.dataXml = dataXml;
	}

	/**
	 * @return the quickBaseDBID
	 */
	public static String getQuickBaseDBID() {
		return quickBaseDBID;
	}

	/**
	 * @param quickBaseDBID the quickBaseDBID to set
	 */
	public static void setQuickBaseDBID(String quickBaseDBID) {
		PIXELConsole.quickBaseDBID = quickBaseDBID;
	}

	/**
	 * @return the quickBaseToken
	 */
	public static String getQuickBaseToken() {
		return quickBaseToken;
	}

	/**
	 * @param quickBaseToken the quickBaseToken to set
	 */
	public static void setQuickBaseToken(String quickBaseToken) {
		PIXELConsole.quickBaseToken = quickBaseToken;
	}

	/**
	 * @return the quickBaseQueryFieldID
	 */
	public static String getQuickBaseQueryFieldID() {
		return quickBaseQueryFieldID;
	}

	/**
	 * @param quickBaseQueryFieldID the quickBaseQueryFieldID to set
	 */
	public static void setQuickBaseQueryFieldID(String quickBaseQueryFieldID) {
		PIXELConsole.quickBaseQueryFieldID = quickBaseQueryFieldID;
	}

	/**
	 * @return the quickBaseSearchTermForDescriptionField
	 */
	public static String getQuickBaseSearchTermForDescriptionField() {
		return quickBaseSearchTermForDescriptionField;
	}

	/**
	 * @param quickBaseSearchTermForDescriptionField the quickBaseSearchTermForDescriptionField to set
	 */
	public static void setQuickBaseSearchTermForDescriptionField(String quickBaseSearchTermForDescriptionField) {
		PIXELConsole.quickBaseSearchTermForDescriptionField = quickBaseSearchTermForDescriptionField;
	}

	/**
	 * @return the quickBaseReturnFields
	 */
	public static String getQuickBaseReturnFields() {
		return quickBaseReturnFields;
	}

	/**
	 * @param quickBaseReturnFields the quickBaseReturnFields to set
	 */
	public static void setQuickBaseReturnFields(String quickBaseReturnFields) {
		PIXELConsole.quickBaseReturnFields = quickBaseReturnFields;
	}

	/**
	 * @return the quickBaseDataXMLNode
	 */
	public static String getQuickBaseDataXMLNode() {
		return quickBaseDataXMLNode;
	}

	/**
	 * @param quickBaseDataXMLNode the quickBaseDataXMLNode to set
	 */
	public static void setQuickBaseDataXMLNode(String quickBaseDataXMLNode) {
		PIXELConsole.quickBaseDataXMLNode = quickBaseDataXMLNode;
	}

	/**
	 * @return the quickBasePIXELString
	 */
	public static String getQuickBasePIXELString() {
		return quickBasePIXELString;
	}

	/**
	 * @param quickBasePIXELString the quickBasePIXELString to set
	 */
	public static void setQuickBasePIXELString(String quickBasePIXELString) {
		PIXELConsole.quickBasePIXELString = quickBasePIXELString;
	}

	/**
	 * @return the q
	 */
	public static int getQ() {
		return q;
	}

	/**
	 * @param q the q to set
	 */
	public static void setQ(int q) {
		PIXELConsole.q = q;
	}

	/**
	 * @return the quickBaseRefreshInterval
	 */
	public static int getQuickBaseRefreshInterval() {
		return quickBaseRefreshInterval;
	}

	/**
	 * @param quickBaseRefreshInterval the quickBaseRefreshInterval to set
	 */
	public static void setQuickBaseRefreshInterval(int quickBaseRefreshInterval) {
		PIXELConsole.quickBaseRefreshInterval = quickBaseRefreshInterval;
	}
	/**
	 * @return the framestring
	 */
	public static String getFramestring() {
		return framestring;
	}
	/**
	 * @param framestring the framestring to set
	 */
	public static void setFramestring(String framestring) {
		PIXELConsole.framestring = framestring;
	}
	/**
	 * @return the stockChange
	 */
	public static BigDecimal getStockChange() {
		return stockChange;
	}
	/**
	 * @param stockChange the stockChange to set
	 */
	public static void setStockChange(BigDecimal stockChange) {
		PIXELConsole.stockChange = stockChange;
	}
}


