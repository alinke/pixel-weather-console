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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.onebeartoe.pixel.hardware.Pixel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.CharacterData;

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


import java.util.List;
//import org.json.simple.*;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

//import org.jdom2.Document;
//import org.jdom2.Element;
//import org.jdom2.JDOMException;
//import org.jdom2.input.SAXBuilder;

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
	
	private static int q = 0;
	
	private static int s = 0;
	    
    private static int numFrames = 0;
    
    private static String animation_name;
    
    private volatile static Timer timer;
    
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
	
	private static String frameDelayString;
	
	private static int frameDelayInt = 100; //in milliseconds
	
	private static boolean frameDelayOverride;
	
	private static String twitterSearchString;
	
	private static boolean twitterMode;
	
	private static String twitterIntervalString = null;
	
	private static int twitterIntervalInt = 60; //in seconds
	
	private static String twitterResult = null;
	
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
	
	private static InputStream authXml = null;
	
	private static InputStream dataXml = null;
	
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
    
    private static int scrollingSmoothness_ = 100; //put a smaller number like 10 for smoother scrolling but you'll need a USB connection for smooth, bluetooth there will be some delay
    
    private static String scrollingText_;
    
    private static boolean scrollingTextMode = false;
    
    private static String scrollingTextSpeed_;
    
    private static int scrollingTextFontSize_ = 30; //default the scrolling text font to 30
    
    private static String scrollingTextFontSizeString;
    
    private static String scrollingTextColor_ = "red";
    
    private static Color textColor;
    
    private static int fontOffset = 0;
    
    private static Twitter twitter;
    
    private static TwitterFactory tf;
    
    private static Query query;
    
    private static QueryResult result = null;
    
    private Status status;
    
    private String lastTweet;
    
    private Integer tweetCount = 0;
    
    private static boolean stayConnected = true;
    
    private static boolean filterTweets = false;
    
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
    private static String snowDomain = "";
    private static String snowUserID = "";
    private static String snowUserPassword = "";
    private static String snowDataXMLNode = "count"; //you should NOT need to change this
    //private static ArrayList<snowGroupPair> values = new ArrayList<snowGroupPair>(); //we use for the SNOW Group name and GUI, ex. CRM, 3D8fe5d018006981001e5e98712087dadc
    private static String snowGroup1GUID = "";
    private static String snowGroup2GUID = "";
    private static String snowGroup1Name = "";
    private static String snowGroup2Name = "";
    private static String snowBaseQuery = "active%3Dtrue%5EnumberSTARTSWITHINC%5EstateIN1%2C2%2C3%2C4%5E";
    private static String snowPriorityQuery ="priorityIN1%2C2"; //should not need to change this, note we didn't inlcude the %5E so don't forget to add that in later
    private static String snowSLAExceededQuery ="u_sla_alertSTARTSWITHRed"; //should not need to change this
    private static int    snowRefreshInterval = 20;
    private static boolean snowMode = false;
    private static String snowPIXELString;
    private static String snowGroupOpen;
    private static String snowGroupPriority;
    private static String snowGroupExceededSLA;
    ///**************************************************************
    
    private static String proximityPinString_;
    
    private static int proximityPin_ = 34;
    
    private static boolean ProxSensor = false;
    
    private static boolean ProxShow = false;
    
    private static boolean ProxTriggered = false;
    
    private static boolean ProxTriggerDone = true;
    
    private static boolean debug_ = false;
    
    private static int TriggerUpperThreshold_ = 500;
    
	private static String sensorLoopDelayString_ = null;
	
	private static int sensorLoopDelay_ = 500;
    
    private static HttpURLConnection conn;
    
    private static boolean quickBaseAuthSuccesful = false;
    
    private static String stockSymbols = "AMAT";
    
    private static String stockPrice = null;
    
    private static String stockChangeString = null;
    
    private static Boolean stockMode = false;
    
    private static BigDecimal stockChange;
    
    private static String complimentString = null;
    
	private static String compliementColor = "green";
	
    
	private static enum Command {
		VERSIONS, FINGERPRINT, WRITE
	}
  	
  	private static void printUsage() {
		System.out.println("*** PIXEL: Console V2.6 ***");
		System.out.println();
		System.out.println("Usage:");
		System.out.println("pixelc <options>");
		System.out.println();
		System.out.println("Valid options are:");
		System.out.println("GIF MODE");
		System.out.println();
		System.out
		.println("--gif=your_filename.gif  Send this GIF to PIXEL. IMPORTANT: You must place GIFs in the same directory as pixelc.jar");
		System.out
		.println("--loop=number  How many times to loop the GIF before exiting, omit this parameter to loop indefinitely");
		System.out
		.println("--write  Puts PIXEL into write mode, default is streaming mode");
		System.out
		.println("--framedelay=x  Overrides the GIF speed/frame delay where x is a whole number representing milliseconds between 1 and 1000");
		System.out
		.println("--superpixel  change LED matrix to SUPER PIXEL 64x64");
		System.out
		.println("--16x32  change LED matrix to Adafruit's 16x32 LED matrix");
		System.out
		.println("--adafruit32x32  change LED matrix to Adafruit's 32x32 LED matrix or 1/16 scan other 32x32 panels");
		System.out
		.println("--64x16  change LED matrix to 64x16 which is two daisy chained 32x16");
		System.out
		.println("--adafruit64x64  change LED matrix to Adafruit 64x64");
		System.out
		.println("--adafruit64x32  change LED matrix to Adafruit 64x32");
		System.out
		.println("--daemon  runs as a background process AND you must also add & at the end of the command line");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=/dev/tty.usbmodem1421 pixelc.jar --gif=tree.gif");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=/dev/tty.usbmodem1421 pixelc.jar --gif=tree.gif --daemon &");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 ~/pixel/pixelc.jar --gif=~/pixel/tree.gif");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --gif=tree.gif --loop=10 --framedelay=200");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --gif=tree.gif --superpixel --write");
		
		System.out.println("\n");
		System.out.println("SCROLLING TEXT MODE / QUICKBASE SEARCH / SERVICE NOW / TWITTER FEED");
		System.out.println();
		System.out
		.println("--quickbase    QuickBase mode, send some scrolling text from QuickBase");
		System.out
		.println("--qbuserid=<text>    QuickBase user id");
		System.out
		.println("--qbpassword=<text>    QuickBase password");
		System.out
		.println("--qbdomain=<text>    QuickBase domain");
		System.out
		.println("--qbdatabase=<text>    ID of the target Quickbase Database");
		System.out
		.println("--qbqueryfield=<number>    The field id of the field in QuickBase to query against");
		System.out
		.println("--qbsearchstring=\"your search term\"    Search string to query the QuickBase, Make sure to enclose your text in double quotes");
		System.out
		.println("--qbreturnfields=<number.number.number.number>    Field IDs to return in the xml data, separate with a dot like this 3.5.7.10 which returns fields with IDs 3,5,7, and 10");
		System.out
		.println("--qbtoken=<text>    The QuickBase application token string, get this from your QuickBase administrator");
		System.out
		.println("--qbrefresh=<number>   How many times to scroll result before checking QuickBase again for the latest data: Omit this for the default of 10");
		System.out
		.println("--snow   Service Now mode, show status of two Service Now Queues/Groups, total open, total high priority, and total exceeded SLA");
		System.out
		.println("--snowuserid=<text>   Service Now id for API access");
		System.out
		.println("--snowpassword=<text>  Service Now password for API access");
		System.out
		.println("--snowdomain=<text>  Service now URL ex. x.service-now.com");
		System.out
		.println("--snowgroup1id=<text>  GUID of the desired Service Now Queue/Group 1");
		System.out
		.println("--snowgroup1name=<text>  Name to display on the scrolling LED text for Service Now Queue/Group 1");
		System.out
		.println("--snowgroup2id=<text>  GUID of the desired Service Now Queue/Group 2");
		System.out
		.println("--snowgroup2name=<text>  Name to display on the scrolling LED text for Service Now Queue/Group 2");
		System.out
		.println("--text=\"your scrolling text\"  Scrolls your message.  Make sure to enclose your text in double quotes");
		System.out
		.println("--twitter=\"your Twitter search term\"    Make sure to enclose search term in double quotes. Use --text or --twitter but not both.");
		System.out
		.println("--filtertweets  Filter Tweets that have RT, contain http:// or @");
		System.out
		.println("--interval=<number>    How often in seconds to update the Twitter feed where x is a whole number between 10 and 86400 (24 hours)"); 
		System.out
		.println("--speed=<number>  How fast to scroll, default value is 6. Higher is faster.");
		System.out
		.println("--smooth=<number>  How smooth the scrolling text is, default value is 100. The combo of smooth=15 and speed=1 will be smooth but you'll need a USB connection, Bluetooth will have a lag");
		System.out
		.println("--fontsize=<number>  Default size is 30");
		System.out
		.println("--loop=<number>  How many times to loop the scrolling text before exiting, omit this parameter to loop indefinitely");
		System.out
		.println("--color=<text>    Supported values are red, green, blue, cyan, gray, magenta, orange, pink, and yellow"); 
		System.out
		.println("--offset=<number>   Use this if your scrolling text is not centered, a postive numbers moves the text up and negative moves down, just experiment until your text is centered"); 
		System.out
		.println("--proximity  Turns on the proximity sensor for interactive applications"); 
		System.out
		.println("--debug  Displays the proximity sensor value on the console"); 
		System.out
		.println("--proximitypin=<number>   The default proximity pin is 34, use this to specific a different pin, options are 31,32,33, or 34"); 
		System.out
		.println("--proximityhigh=<number>   The upper limit threshold for the prox sensor to trigger, ie, will trigger if goes over this number"); 
		System.out
		.println("--proximityshow=<number>   Display the proximity sensor value on the LED display"); 
		System.out
		.println("--sensorloopdelay=<number>  time in milliseconds to poll the sensor, omit this parameter to use default: 500"); 
		System.out
		.println("--stock=<text>  Scrolls stock ticket upon proximity trigger, currently only one stock symbol is supported, proximity sesor must be turned on"); 
		System.out
		.println("--compliments  Scrolls a compliment message upon proximity trigger, proximity sensor must be turned on"); 
		System.out
		.println("--daemon  runs as a background process AND you must also add & at the end of the command line");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=/dev/tty.usbmodem1421 pixelc.jar --twitter=\"cats and dogs\" --interval=30 --adafruit32x32");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --twitter=\"cats and dogs\" --interval=30 --daemon &");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --text=\"hello world\" --speed=10 --fontsize=36 --color=orange");
		System.out.println("Ex. java -jar -Dioio.SerialPorts=COM14 pixelc.jar --text=\"hello world\" --speed=10 --fontsize=36 --color=orange --loop=1");
		System.out.println("Ex. QuickBase example java -jar -Dioio.SerialPorts=/dev/tty.usbmodem1411 pixelc.jar --quickbase --smooth=15 --speed=1 --offset=-10 --color=blue --64x16 --qbuserid=Your QB User ID --qbpassword=Your QB Password --qbdomain=Your QB Domain --qbdatabase=Your QB Database ID --qbsearchstring==\"Your Search Text\" --qbqueryfield=QB Field ID to Search --qbtoken=Your QB Application Token");
		System.out.println("Ex. QuickBase example with stock ticker proximity interrupt java -jar pixelc.jar --quickbase --smooth=15 --speed=1 --offset=-10 --color=blue --64x16 --qbuserid=Your QB User ID --qbpassword=Your QB Password --qbdomain=Your QB Domain --qbdatabase=Your QB Database ID --qbsearchstring==\"Your Search Text\" --qbqueryfield=QB Field ID to Search --qbtoken=Your QB Application Token --qbrefresh=1 --stock=AAPL --proximity");
		System.out.println("Ex. Twitter Feed with proximity interrupt to display compliment messages java -jar pixelc.jar --twitter=\"cats and dogs\" --smooth=15 --speed=1 --offset=-10 --color=blue --64x16 --proximity --compliments");

		
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
		public static void main(String[] args) throws Exception {
			
			TwitterTimer = new ActionListener() 
		  	{
		  	    public void actionPerformed(ActionEvent evt) 
		  	    {
		  	  	String tweetSearchTerm = twitterSearchString;
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
		
		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
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
			
			if (arg.startsWith("--proximity")) {
				ProxSensor = true;
				validCommandLine = true;
				System.out.println("Proximity Sensor enabled");
			}
			
			if (arg.startsWith("--proximitypin=")) {  //we'll use pin34 by default unless the user overrides here
				proximityPinString_ = arg.substring(15);
				
				if (Float.parseFloat(proximityPinString_) > 35 || Float.parseFloat(proximityPinString_) < 31 || proximityPinString_.contains(".")) {
					System.out.println("The proximity pin number can be 31,32,33, or 34. Defaulting to a proximity sensor pin 34...");
				}
				else {
					proximityPin_ = Integer.parseInt(proximityPinString_);
					System.out.println("Proximity Pin: " + proximityPinString_);
				}	
			}
			
			if (arg.startsWith("--proximityhigh=")) {  //the upper limit threshold for the prox sensor to trigger, ie, will trigger if goes over this number
				String TriggerUpperThresholdString = arg.substring(16);
				
				if (Float.parseFloat(TriggerUpperThresholdString) > 1000 || proximityPinString_.contains(".")) {
					System.out.println("The upper trigger threshold for the proximity sensor cannot be over 1000 and must also be a whole number. Defaulting to 500...");
				}
				else {
					TriggerUpperThreshold_ = Integer.parseInt(TriggerUpperThresholdString);
					System.out.println("Proximity Upper Trigger Threshold Override (Default:500) : " + TriggerUpperThresholdString);
				}	
			}
			
			if (arg.startsWith("--proximityshow")) {
				System.out.println("Displaying Proximity Sensor values");
				ProxShow = true;
			}
			
			
			if (arg.startsWith("--sensorloopdelay=")) {  //we'll use pin34 by default unless the user overrides here
				sensorLoopDelayString_ = arg.substring(18);
				
				if (Float.parseFloat(sensorLoopDelayString_) < 10 || Float.parseFloat(sensorLoopDelayString_) > 5000 || sensorLoopDelayString_.contains(".")) {
					System.out.println("The sensor loop delay must be greater than 10 and less than 5000. Defaulting to 1000...");
				}
				else {
					sensorLoopDelay_ = Integer.parseInt(sensorLoopDelayString_);
					System.out.println("Sensor Loop Delay Override (default: 500): " + sensorLoopDelayString_);
				}	
			}
			
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
							
						
							gifFileName_ = GIFfileAbsolute.getName();
							
							//the commented out code here did not work on the Raspberry Pi so changed to above
							/*String path = gifFileName_.
							    substring(0,gifFileName_.lastIndexOf(File.separator));
							
							gifFileName_ = GIFfileAbsolute.getName();
							System.out.println("GIF found using absolute path, file name is: " + GIFfileAbsolute.getName());*/
							
							// gifFileName_ = gifFileName_.
							//    substring(0,gifFileName_.lastIndexOf(File.separator));
							//gifFileName_ = GIFfileAbsolute.getName();
							System.out.println("GIF found using absolute path, file name is: " + gifFileName_);
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
			
			if (arg.startsWith("--framedelay=")) {
				frameDelayString = arg.substring(13);
				frameDelayOverride = true;
				
				if (Float.parseFloat(frameDelayString) < 1 || frameDelayString.contains(".")) {
					frameDelayOverride = false;
					System.out.println("The frame delay must be a whole number between 1 and 1000 (milliseconds), not overriding the frame deay");
				}
				else {
					frameDelayInt =  Integer.parseInt(frameDelayString);
					System.out.println("Frame delay override specified at " + frameDelayString + " milliseconds");
				}
				
				if (frameDelayInt > 1000) {
					frameDelayInt = 1000;
					System.out.println("Sorry, the slowest frame delay possible is 1000 ms or 1 second, setting the frame delay to 1000 ms");
				}
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
			
			if (arg.startsWith("--smooth=")) {
				String scrollingSmoothnessString = arg.substring(9);
				scrollingSmoothness_ = Integer.parseInt(scrollingSmoothnessString);
			}	
			
			
			if (arg.startsWith("--twitter=")) {
				twitterSearchString = arg.substring(10);
				System.out.println("In Twitter mode with search term: " + twitterSearchString);
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
					System.out.println("The Twitter search interval must be a whole number between 10 and 86400 (24 hours) in seconds. Defaulting to a Twitter search term refresh of 30 seconds...");
				}
				else {
					if (Float.parseFloat(twitterIntervalString) < 10 || twitterIntervalString.contains(".") || Float.parseFloat(twitterIntervalString) > 86400) {
						System.out.println("The Twitter search interval must be a whole number between 10 and 86400 (24 hours) in seconds. Defaulting to a Twitter search term refresh of 30 seconds...");
					}
					else {
						twitterIntervalInt = Integer.parseInt(twitterIntervalString);
						System.out.println("Twitter text from the search term will refresh every " + twitterIntervalString + " seconds");
					}
				}
			}	
			
			if (arg.startsWith("--filtertweets=")) {
				filterTweets = true;
				System.out.println("Filtering Tweets that have RT, contain http:// or @");
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
			
			if (arg.startsWith("--adafruit32x32")) {
				ledMatrixType = 11;
				System.out.println("Adafruit 32x32 LED matrix has been selected");
			}
			
			if (arg.startsWith("--64x16")) {
				ledMatrixType = 17;
				System.out.println("64x16 LED matrix has been selected");
			}
			
			if (arg.startsWith("--superpixel")) {
				ledMatrixType = 10;
				System.out.println("SUPER PIXEL selected");
			}
			
			if (arg.startsWith("--adafruit64x64")) {
				ledMatrixType = 14;
				System.out.println("Adafruit 64x64 selected");
			}
			
			if (arg.startsWith("--adafruit64x32")) {
				ledMatrixType = 13;
				System.out.println("Adafruit 64x32 selected");
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
			
			if (arg.startsWith("--daemon")) { //not using this one
				System.out.println("Daemon Mode");
				backgroundMode = true;
			}	
			
			if (arg.startsWith("--debug")) { //not using this one
				System.out.println("Debug Mode");
				debug_ = true;
			}	
			
			///***************** 
			
			if (arg.startsWith("--qbuserid=")) {  //8 characters
				quickBaseUserID = arg.substring(11);
				q++;
				System.out.println("QuickBase User ID: " + quickBaseUserID);
			}
				
			if (arg.startsWith("--qbpassword=")) { //10 characters
				quickBaseUserPassword = arg.substring(13);
				q++;
			}
			
			if (arg.startsWith("--qbdomain=")) { //8 characters
				quickBaseDomain = arg.substring(11);
				q++;
				System.out.println("QuickBase DB Domain: " + quickBaseDomain);
			}
			
			if (arg.startsWith("--qbdatabase=")) { 
				quickBaseDBID = arg.substring(13);
				q++;
				System.out.println("QuickBase DB ID: " + quickBaseDBID);
			}
			
			if (arg.startsWith("--qbqueryfield=")) { //12 characters
				quickBaseQueryFieldID = arg.substring(15);
				q++;
				System.out.println("QuickBase Field ID to Query: " + quickBaseQueryFieldID);
			}
			
			if (arg.startsWith("--qbsearchstring=")) { 
				String quickBaseSearchTermForDescriptionFieldString = arg.substring(17);
				q++;
				quickBaseSearchTermForDescriptionField = new String(quickBaseSearchTermForDescriptionFieldString.replace(" ", "%20")); //doesn't like it if there are spaces so need to replace space with %20
				System.out.println("QuickBase Search String: " + quickBaseSearchTermForDescriptionField);
			}
			
			if (arg.startsWith("--qbreturnfields=")) { 
				quickBaseReturnFields = arg.substring(17);
				q++;
				System.out.println("QuickBase Field IDs to return: " + quickBaseReturnFields);
			}
			
			if (arg.startsWith("--qbtoken=")) { 
				quickBaseToken = arg.substring(10);
				q++;
				System.out.println("QuickBase Application Token #: " + quickBaseToken);
			}
			
			if (arg.startsWith("--quickbase")) { 
				System.out.println("QuickBase Mode");
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
				System.out.println("SNOW User ID: " + snowUserID);
			}
				
			if (arg.startsWith("--snowpassword=")) { 
				snowUserPassword = arg.substring(15);
				s++;
			}
			
			if (arg.startsWith("--snowdomain=")) { 
				snowDomain = arg.substring(13);
				s++;
				System.out.println("SNOW Domain: " + snowDomain);
			}
			
			if (arg.startsWith("--snowgroup1id=")) { 
				snowGroup1GUID = arg.substring(15);
				s++;
				System.out.println("SNOW Group 1 GUID: " + snowGroup1GUID);
			}
			
			if (arg.startsWith("--snowgroup1name=")) { 
				snowGroup1Name = arg.substring(17);
				s++;
				System.out.println("SNOW Group Name 1: " + snowGroup1Name);
			}
			
			if (arg.startsWith("--snowgroup2id=")) { 
				snowGroup2GUID = arg.substring(15);
				s++;
				System.out.println("SNOW Group 2 GUID: " + snowGroup2GUID);
			}
			
			if (arg.startsWith("--snowgroupname2=")) { 
				snowGroup2Name = arg.substring(17);
				s++;
				System.out.println("SNOW Group 2 Name: " + snowGroup2Name);
			}
						
			if (arg.startsWith("--snow")) { 
				System.out.println("Service Now Mode");
				snowMode = true;
				validCommandLine = true;
				z++;
				
			/*	try {
						try {
							runSNOW("CRM", snowGroup1GUID);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						String snowPIXELString1 = snowPIXELString;
						
						try {
							runSNOW("VC", snowGroup2GUID);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						String snowPIXELString2 = snowPIXELString;
						snowPIXELString = snowPIXELString1 + " " + snowPIXELString2;
						System.out.println(snowPIXELString);
				
				
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
			}
			
			if (arg.startsWith("--snowrefresh=")) {
				String snowRefreshIntervalString = arg.substring(14);
				
				if (!snowRefreshIntervalString.matches("[0-9]+")) {
					System.out.println("The Service Now refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 20 scrolling messages...");
				}
				else { //ok it's only a number so now let's make sure it's within the right range
					if (Float.parseFloat(snowRefreshIntervalString) < 1 || snowRefreshIntervalString.contains(".")) {
						System.out.println("The Service Now refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 20 scrolling messages...");
					}
					else {
						snowRefreshInterval = Integer.parseInt(snowRefreshIntervalString);
						System.out.println("Service Now will refresh every " + snowRefreshIntervalString + " time(s) a scrolling message completes");
					}
				}
			}
			
			if (arg.startsWith("--qbrefresh=")) {
				String quickBaseRefreshIntervalString = arg.substring(12);
				
				if (!quickBaseRefreshIntervalString.matches("[0-9]+")) {
					System.out.println("The QuickBase refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 5 scrolling messages...");
				}
				else { //ok it's only a number so now let's make sure it's within the right range
					if (Float.parseFloat(quickBaseRefreshIntervalString) < 1 || quickBaseRefreshIntervalString.contains(".")) {
						System.out.println("The QuickBase refresh interval must be a whole number 1 or greater. Defaulting to refreshing after 5 scrolling messages...");
					}
					else {
						quickBaseRefreshInterval = Integer.parseInt(quickBaseRefreshIntervalString);
						System.out.println("QuickBase will refresh every " + quickBaseRefreshIntervalString + " time(s) a scrolling message completes");
					}
				}
			}
			
			if (arg.startsWith("--stock=")) { 
				stockSymbols = arg.substring(8);
				System.out.println("Stock symbol requested: " + stockSymbols);
				stockMode = true;
			}
			
			if (arg.startsWith("--compliments")) { 
				stockSymbols = arg.substring(13);
				System.out.println("Compliments mode turned on, please make sure you enabled --proximity too");
				complimentsMode = true;
			}
			
			
			if (validCommandLine == false) {
				throw new BadArgumentsException("Unexpected option: " + arg);
			}
			
			
			//after here we go to the ioio loop
			
		}
		
		private static class BadArgumentsException extends Exception {
			private static final long serialVersionUID = -5730905669013719779L;

			public BadArgumentsException(String message) {
				super(message);
			}
		}
	

	protected void run(String[] args) throws IOException {
		
		System.out.println("Pixel integration with delayted run() via sleep.");
		
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
		
	    GIFnumFrames = pixel.getDecodednumFrames(currentDir, gifFileName_);
	    GIFresolution = pixel.getDecodedresolution(currentDir, gifFileName_);
	    
	    if (frameDelayOverride) { 
	    	GIFselectedFileDelay = frameDelayInt; //use the override the user specified from the command line --framedelay=x
	    	GIFfps = 1000.f / GIFselectedFileDelay;
	    }
	    else { //no override so just use as is
	    	 GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, gifFileName_);  
	    	 GIFfps = pixel.getDecodedfps(currentDir, gifFileName_); //get the fps
	    }
	    
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
	 
	 private static void CheckAndStartTimer() {
	    //TO DO make sure timer was initialized prior	
		 
		 if (pixel != null && !timer.isRunning()) {
		  		  pixel.interactiveMode(); //put into interactive mode as could have been stuck in local mode after a write
		  		  timer.start();
		  	  }
	 }
	 
	 private static void scrollText(final String scrollingText, final String scrollingTextColor, final int numberLoops, boolean writeMode) 
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
                currentResolution = 128; 
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
		    	 
		     default:	    		 
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
		    	 frame_length = 2048;
		    	 currentResolution = 32;
	     }
		 
		 System.out.println("CurrentResolution is: " + currentResolution + "\n");
	 }
	 
	private static String getStock(String symbol) throws java.io.IOException {
		//Yahoo API here http://financequotes-api.com
		Stock stock = YahooFinance.get(symbol);
		 
		BigDecimal price = stock.getQuote().getPrice();
		stockChange = stock.getQuote().getChangeInPercent();
		BigDecimal peg = stock.getStats().getPeg();
		BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
		
		System.out.println(symbol + " Stock Price: " + price);
		System.out.println(symbol + " Stock Price Change: " + stockChange.toString() +"%");
		
		/*stockPrice = price.toString();
		return stockPrice;*/
		
		if (price.toString() == null) {
			return "Connectivity Problem";
		}
		else {
			return price.toString();
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
	
	 
	 
	 private static void weatherGIF() //not used
	    {
		 
		selectedFileName = weatherCondition;
		
		 //here we will send the selectedfilename to the pixel class, the pixel class will then look for the corresponding filename.txt meta-data file and return back the meta data
		
		//if (pixel.GIFNeedsDecoding(currentDir, selectedFileName, currentResolution) == true) {    //resolution can be 16, 32, 64, 128 (String CurrentDir, String GIFName, int currentResolution)
			
			//decodeGIF
		//}
		
		//TO DO make sure to test the weather gifs now that we've made these changes
		
		/*GIFfps = pixel.getDecodedfps(currentDir, selectedFileName); //get the fps //to do fix this later becaause we are getting from internal path
	    GIFnumFrames = pixel.getDecodednumFrames(currentDir, selectedFileName);
	    GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, selectedFileName);
	    GIFresolution = pixel.getDecodedresolution(currentDir, selectedFileName);*/
	    
	    GIFnumFrames = pixel.getDecodednumFrames(currentDir, selectedFileName);
	    GIFresolution = pixel.getDecodedresolution(currentDir, selectedFileName);
	    
	    if (frameDelayOverride) { 
	    	GIFselectedFileDelay = frameDelayInt; //use the override the user specified from the command line --framedelay=x
	    	GIFfps = 1000.f / GIFselectedFileDelay;
	    }
	    else { //no override so just use as is
	    	 GIFselectedFileDelay = pixel.getDecodedframeDelay(currentDir, selectedFileName);  
	    	 GIFfps = pixel.getDecodedfps(currentDir, selectedFileName); //get the fps
	    }
		
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
	 
	
	 
	 private static void runQuickBase() throws MalformedURLException, IOException {
		 
		
		 
		/* here is the format of the returned XML we get when we QuickBase authenticate
		 * <qdbapi>
		 <action>API_Authenticate</action>
		 <errcode>0</errcode>
		 <errtext>No error</errtext>
		 <ticket>
		 some_long_number
		 </ticket>
		 <userid>numbers.4chars</userid>
		 </qdbapi>*/
		 
		    authXml = null;
		    try
		    {
		       authXml  = new URL("https://" + quickBaseDomain + "/db/main?a=API_Authenticate&username=" + quickBaseUserID + "&password=" + quickBaseUserPassword).openConnection().getInputStream();
	 		   DocumentBuilderFactory factory = DocumentBuilderFactory.
		                                        newInstance();
	 		   
		       DocumentBuilder builder = factory.newDocumentBuilder();
		       Document doc = builder.parse(authXml);
		       
		       try {
		    		//optional, but recommended
		    		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		    		doc.getDocumentElement().normalize();
		    	 
		    		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    		
		    		NodeList nList = doc.getElementsByTagName(quickBaseRootXMLNode);
		    	 
		    		System.out.println("----------------------------");
		    	 
		    		for (int temp = 0; temp < nList.getLength(); temp++) {
		    	 
		    			Node nNode = nList.item(temp);
		    	 
		    			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		    	 
		    			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	 
		    				Element eElement = (Element) nNode;
		    				System.out.println("QB Auth Ticket : " + eElement.getElementsByTagName("ticket").item(0).getTextContent());
		    				quickbaseTicket = eElement.getElementsByTagName("ticket").item(0).getTextContent();
		    				
		    				System.out.println("QB Auth Error Code : " + eElement.getElementsByTagName("errcode").item(0).getTextContent());
		    				String errCode = eElement.getElementsByTagName("errcode").item(0).getTextContent();
		    				
		    				System.out.println("QB Auth Error Code Text : " + eElement.getElementsByTagName("errtext").item(0).getTextContent());
		    				System.out.println("QB Auth User ID " + eElement.getElementsByTagName("userid").item(0).getTextContent());
		    				
		    				if (errCode.equals("0")) {
		    					System.out.println("Yeah! QuickBase Authentication Successful...");
		    					quickBaseAuthSuccesful = true;
		    				}
		    				else {
		    					System.out.println("Sorry, QuickBase Authentication Failed!!!");
		    					quickBaseAuthSuccesful = false;
		    				}
		    			}
		    		}
		    	    } catch (Exception e) {
		    		e.printStackTrace();
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
		          if (authXml != null)
		        	authXml.close();
		       }
		       catch (IOException ex)
		       {
		          System.out.println(ex.getMessage());
		       }
		    }
		    
		    ///**** now we have our token so let's get some data
		    
		    dataXml = null;
		    
		    try
		    {
		    	String URLString = "https://" + quickBaseDomain + "/db/" + quickBaseDBID +"?a=API_DoQuery&includeRids=1&ticket=" + quickbaseTicket + "&apptoken=" + quickBaseToken + "&udata=mydata&query={%27" + quickBaseQueryFieldID + "%27.CT.%27" + quickBaseSearchTermForDescriptionField + "%27}&clist=" + quickBaseReturnFields + "&slist=3&options=nosort&fmt=structured";
		    	
		       dataXml = new URL(URLString).openConnection().getInputStream();
	 		   DocumentBuilderFactory factory = DocumentBuilderFactory.
		                                        newInstance();
	 		   
		       DocumentBuilder builder = factory.newDocumentBuilder();
		       Document doc = builder.parse(dataXml);
		       
		       try {
		    		//optional, but recommended
		    		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		    		doc.getDocumentElement().normalize();
		    	 
		    		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    	 
		    		NodeList nList = doc.getElementsByTagName(quickBaseDataXMLNode); //this is record by default, you should not have to change unless the QB API changes
		    	 
		    		System.out.println("----------------------------");
		    		System.out.println("Total Matched Records: " + nList.getLength());
		    		
		    		if (nList.getLength() == 1) {
		    			quickBasePIXELString = nList.getLength() + " project behind schedule: "; //this is the text we will send to the scrolling LED display and will be specific to your application so change accordingly
		    		}
		    		
		    		else {
		    			quickBasePIXELString = nList.getLength() + " projects behind schedule: "; //this is the text we will send to the scrolling LED display and will be specific to your application so change accordingly
		    		}
		    		
		    		for (int temp = 0; temp < nList.getLength(); temp++) {
		    	 
		    			Node nNode = nList.item(temp);
		    	 
		    			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		    	 
		    			//******* This part of the code will be specific to your application so change accordingly *********
		    			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	 
		    				Element eElement = (Element) nNode;
		    				System.out.println("Project ID : " + eElement.getElementsByTagName("f").item(0).getTextContent());
		    				
		    				if (temp == nList.getLength() - 1) { //it's the last record so don't add the comma at the end
		    					quickBasePIXELString = quickBasePIXELString + eElement.getElementsByTagName("f").item(0).getTextContent();
		    				}
		    				else {
		    					quickBasePIXELString = quickBasePIXELString + eElement.getElementsByTagName("f").item(0).getTextContent() + ", ";
		    				}
		    				
		    				System.out.println("Description : " + eElement.getElementsByTagName("f").item(1).getTextContent());
		    				System.out.println("Priority : " + eElement.getElementsByTagName("f").item(2).getTextContent());
		    				System.out.println("Budget " + eElement.getElementsByTagName("f").item(3).getTextContent());
		    			}
		    			//******************************************************************************************************
		    		}
		    	    } catch (Exception e) {
		    		e.printStackTrace();
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
		          if (dataXml != null)
		        	  dataXml.close();
		       }
		       catch (IOException ex)
		       {
		          System.out.println(ex.getMessage());
		       }
		    }
		    
		    if (quickBaseAuthSuccesful) {
		    	System.out.println("LED Scrolling Text = " + quickBasePIXELString);
		    }
		    else {
		    	System.out.println("Sorry, unable to authenticate to QuickBase");
		    }
		    
		    if (q != 8) { //8 is the number of command line parameters required for QuickBase
		    	System.out.println("Sorry! You didn't enter all the command line paramters that QuickBase needs. Please modify and try again.");
		    	quickBasePIXELString = "Sorry! You didn't enter all the command line paramters that QuickBase needs. Please modify and try again.";
			}
		    

	    	scrollText(quickBasePIXELString, scrollingTextColor_, quickBaseRefreshInterval,false); //0 means loop forever
		    
		    //TO DO add a loop if the auth fails to try again after x seconds
		    // http://www.mkyong.com/webservices/jax-ws/suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target/  //this problems only happened on my PC, was fine when compiling on Mac
		   //TO DO put the above in a timer such that it re-runs itself every x minutes as defined by a command line
	 }
	 
	 private static void runSNOW(String snowGroupName, String snowGroupGUID) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
		 
				 
		 String responseBody = null;
		 
	        CredentialsProvider credsProvider = new BasicCredentialsProvider();
	        credsProvider.setCredentials(
	                new AuthScope(new HttpHost(snowDomain)),
	                new UsernamePasswordCredentials(snowUserID, snowUserPassword));
	        CloseableHttpClient httpclient = HttpClients.custom()
	                .setDefaultCredentialsProvider(credsProvider)
	                .build();
	 
	        //let's get the total number of open tickets in this queue
	        try {
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + snowBaseQuery + "assignment_group%" + snowGroupGUID + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
	        	httpget.setHeader("Accept", "application/xml");
	            System.out.println("Executing request " + httpget.getRequestLine());
	            CloseableHttpResponse response = httpclient.execute(httpget);
	            try {
	                System.out.println("----------------------------------------");
	                System.out.println(response.getStatusLine());
	                responseBody = EntityUtils.toString(response.getEntity());
	                System.out.println(responseBody);
	            } finally {
	                response.close();
	            }
	        } finally {
	            //httpclient.close();
	        }
	        
	        snowGroupOpen = returnXMLNodeValue(snowDataXMLNode,responseBody);
	        
	        //let's get the tickets in this queue that are of high priority
	        try {
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + snowBaseQuery + "assignment_group%" + snowGroupGUID + "%5E" + snowPriorityQuery + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
	        	httpget.setHeader("Accept", "application/xml");
	            System.out.println("Executing request " + httpget.getRequestLine());
	            CloseableHttpResponse response = httpclient.execute(httpget);
	            try {
	                System.out.println("----------------------------------------");
	                System.out.println(response.getStatusLine());
	                responseBody = EntityUtils.toString(response.getEntity());
	                System.out.println(responseBody);
	            } finally {
	                response.close();
	            }
	        } finally {
	            //httpclient.close();
	        }
	        //ok we have it, now let's get the total count
	        snowGroupPriority = returnXMLNodeValue(snowDataXMLNode,responseBody);
	        
	        //now let's get one that have exceeded SLA
	        //let's get the tickets in this queue that are of high priority
	       
	        
	        try {
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + snowBaseQuery + "assignment_group%" + snowGroupGUID + "%5E" + snowSLAExceededQuery + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
	        	httpget.setHeader("Accept", "application/xml");
	            System.out.println("Executing request " + httpget.getRequestLine());
	            CloseableHttpResponse snowXML = httpclient.execute(httpget);
	            try {
	                System.out.println("----------------------------------------");
	                System.out.println(snowXML.getStatusLine());
	                responseBody = EntityUtils.toString(snowXML.getEntity());
	                System.out.println(responseBody);
	            } finally {
	            	snowXML.close();
	            }
	        } finally {
	            httpclient.close();
	        }
	        
	        snowGroupExceededSLA = returnXMLNodeValue(snowDataXMLNode,responseBody);
	        
	        if (s < 5) { 
		    	System.out.println("Sorry! You didn't enter all the command line paramters that Service Now needs. Please modify and try again.");
		    	snowPIXELString = "Sorry! You didn't enter all the command line paramters that Service Now needs. Please modify and try again.";
			}
	        
	        snowPIXELString = snowGroupName + ": " + "Open=" + snowGroupOpen + " Priority=" + snowGroupPriority + " Overdue=" + snowGroupExceededSLA ;
	        System.out.println(snowPIXELString);
	        
	      }

	     
	 private static String returnXMLNodeValue(String targetNode, String xmlString) {
		 
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
	               System.out.println("Count: " + getCharacterDataFromElement(line));
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



private static void CheckandRunMode() {
			
		 
		 
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
					
					if (twitterMode) { //so we're in twitter mode so let's start the twitter search timer
						runTwitter();
						twitterTimer = new Timer(twitterIntervalInt * 1000, TwitterTimer);
						twitterTimer.start();
						scrollText(twitterResult, scrollingTextColor_, 0,writeMode); //0 menas loop forever
					}
					else {
						scrollText(scrollingText_, scrollingTextColor_, 0, writeMode); //write is not supported, just stream right now
					}
				}
				
				else if (quickbaseMode == true) {
					//TO DO add the timer in here just like we did with Twitter
					//TO DO I suppose we'll also need to ensure that the quickbase data is retrieved before this IOIO Setup starts
					
					try {
							runQuickBase();
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
						
						try {
									try {
										runSNOW(snowGroup1Name, snowGroup1GUID);
									} catch (ParserConfigurationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SAXException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									String snowPIXELString1 = snowPIXELString;
									
									try {
										runSNOW(snowGroup2Name, snowGroup2GUID);
									} catch (ParserConfigurationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SAXException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									String snowPIXELString2 = snowPIXELString;
									snowPIXELString = snowPIXELString1 + " " + snowPIXELString2;
									System.out.println(snowPIXELString);
									
									scrollText(snowPIXELString, scrollingTextColor_, snowRefreshInterval,false); //will refresh by default after 25 scrolls, false means keep going forever
							        
							
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
		 
		 	ProxTriggerDone = true;	//had to add this because if the user holds hand in front of prox sensor continuously, for some reason we weren't resetting this prox trigger flag but this add here seems to fix it
		}
	 
	 private static void runCompliments() {
		 
		 int randomInt = (int)(60.0 * Math.random());
		 System.out.println("pseudo random number between 0 and 60 : " + randomInt );
		 
		 switch (randomInt) { 
	        
		 	case 0:
	        	complimentString = "You could probably lead a rebellion";	
	        	compliementColor = "orange";
	        	break;
		 	case 1:
	        	complimentString = "Smile, you're beautiful";	
	        	compliementColor = "cyan";
	        	break;
	        case 2:
	        	complimentString = "You look great!";	
	        	compliementColor = "green";
	        	break;
	        case 3:
	        	complimentString = "Is that a new Shirt?";	
	        	compliementColor = "purple";
	            break;
	        case 4:
	        	complimentString = "Your hair rocks";	
	        	compliementColor = "green";	
	            break;	                
	        case 5:
	        	complimentString = "I like your style";	
	        	compliementColor = "yellow";	
	            break;    
	        case 6:
	        	complimentString = "Have you been working out?";	
	        	compliementColor = "cyan";	            	
	        	break;
	        case 7:
	        	complimentString = "The Force is strong with you";	
	        	compliementColor = "purple";		
	            break;
	        case 8:
	        	complimentString = "You're the Bee's Knees";	
	        	compliementColor = "green";	
	            break;
	        case 9:
	        	complimentString = "Is it hot or just you?";	
	        	compliementColor = "red";
	            break;	                
	        case 10:
	        	complimentString = "You complete me";	
	        	compliementColor = "grey";	
	            break;    
	        case 11:
	        	complimentString = "Hey good looking";	
	        	compliementColor = "purple";	            	
	        	break;
	        case 12:
	        	complimentString = "You're pretty high on my list of people with whom I would want to be stranded on an island";	
	        	compliementColor = "yellow";
	            break;
	        case 13:
	        	complimentString = "I like your shoes";	
	        	compliementColor = "cyan";		
	            break;
	        case 14:
	        	complimentString = "I admire your skills";	
	        	compliementColor = "orange";	
	            break;	                
	        case 15:
	        	complimentString = "Your Skin is Radiant";	
	        	compliementColor = "purple";
	            break;    
	        case 16:
	        	complimentString = "You're so smart";	
	        	compliementColor = "purple";		            	
	        	break;
	        case 17:
	        	complimentString = "You smell great";	
	        	compliementColor = "orange";	
	            break;
	        case 18:
	        	complimentString = "You make me smile";	
	        	compliementColor = "cyan";	
	            break;
	        case 19:
	        	complimentString = "You look stunning";	
	        	compliementColor = "pink";	
	            break;	                
	        case 20:
	        	complimentString = "Even my cat likes you";	
	        	compliementColor = "green";
	            break;    
	        case 21:
	        	complimentString = "You deserve a promotion";	
	        	compliementColor = "green";		            	
	        	break;
	        case 22:
	        	complimentString = "I wish I was your mirror";	
	        	compliementColor = "purple";
	            break; 
	        case 23:
	        	complimentString = "Take a break; you've earned it";	
	        	compliementColor = "orange";
	            break;
	        case 24:
	        	complimentString = "I would share my dessert with you";	
	        	compliementColor = "pink";
	            break; 
	        case 25:
	        	complimentString = "All of your ideas are brilliant!";	
	        	compliementColor = "green";
	            break; 
	        case 26:
	        	complimentString = "You make my data circuits skip a beat";	
	        	compliementColor = "yellow";
	            break; 
	        case 27:
	        	complimentString = "If I had to choose between you or Mr. Rogers, it would be you";	
	        	compliementColor = "orange";
	            break; 
	        case 28:
	        	complimentString = "I support all of your decisions";	
	        	compliementColor = "green";
	            break; 
	        case 29:
	        	complimentString = "You could survive a zombie apocalypse";	
	        	compliementColor = "purple";
	            break; 
	        case 30:
	        	complimentString = "I wish I could move your furniture";	
	        	compliementColor = "cyan";
	            break; 
	        case 31:
	        	complimentString = "You're nicer than a day on the beach";	
	        	compliementColor = "orange";
	            break; 
	        case 32:
	        	complimentString = "I would do your taxes any day";	
	        	compliementColor = "green";
	            break; 
	        case 33:
	        	complimentString = "You're more fun than bubble wrap";	
	        	compliementColor = "blue";
	            break; 
	        case 34:
	        	complimentString = "You're invited to my birthday party";	
	        	compliementColor = "orange";
	            break; 
	        case 35:
	        	complimentString = "You could probably get a bird to land on your shoulder and hang out with you";	
	        	compliementColor = "orange";
	            break; 
	        case 36:
	        	complimentString = "My mom always asks me why I can't be more like you";	
	        	compliementColor = "cyan";
	            break; 
	        case 37:
	        	complimentString = "I am having trouble coming up with a compliment worthy enough for you";	
	        	compliementColor = "green";
	            break; 
	        case 38:
	        	complimentString = "If we were playing kickball, I'd pick you first";	
	        	compliementColor = "grey";
	            break; 
	        case 39:
	        	complimentString = "You're cooler than ice on the rocks";	
	        	compliementColor = "blue";
	            break; 
	        case 40:
	        	complimentString = "I wish I could choose your handwriting as a font";	
	        	compliementColor = "pink";
	            break; 
	        case 41:
	        	complimentString = "I named all my appliances after you";	
	        	compliementColor = "cyan";
	            break; 
	        case 42:
	        	complimentString = "Can you teach me how to be as awesome as you?";	
	        	compliementColor = "yellow";
	            break; 
	        case 43:
	        	complimentString = "You could invent words and people would use them";	
	        	compliementColor = "orange";
	            break; 
	        case 44:
	        	complimentString = "You have powerful sweaters";	
	        	compliementColor = "pink";
	            break; 
	        case 45:
	        	complimentString = "You are better than unicorns and sparkles combined!";	
	        	compliementColor = "yellow";
	            break; 
	        case 46:
	        	complimentString = "You are the watermelon in my fruit salad. Yum!";	
	        	compliementColor = "cyan";
	            break; 
	        case 47:
	        	complimentString = "If you were in a movie you wouldn't get killed off";	
	        	compliementColor = "blue";
	            break; 
	        case 48:
	        	complimentString = "They should name an ice cream flavor after you";	
	        	compliementColor = "red";
	            break; 
	        case 49:
	        	complimentString = "I would volunteer to take your place in the Hunger Games";	
	        	compliementColor = "cyan";
	            break; 
	        case 50:
	        	complimentString = "I'd let you steal the white part of my Oreo";	
	        	compliementColor = "grey";
	            break; 
	        case 51:
	        	complimentString = "Your mouse told me that you have very soft hands";	
	        	compliementColor = "orange";
	            break; 
	        case 52:
	        	complimentString = "I like your socks";	
	        	compliementColor = "orange";
	            break; 
	        case 53:
	        	complimentString = "How do you get your hair to look that great?";	
	        	compliementColor = "green";
	            break; 
	        case 54:
	        	complimentString = "Say, aren't you that famous model from TV?";	
	        	compliementColor = "blue";
	            break; 
	        case 55:
	        	complimentString = "I would love to visit you, but I live on the Internet";	
	        	compliementColor = "cyan";
	            break; 
	        case 56:
	        	complimentString = "If I freeze, it's not a computer virus. I was just stunned by your awesomeness";	
	        	compliementColor = "yellow";
	            break; 
	        case 57:
	        	complimentString = "If I had to choose between you or Mr. Rogers, it would be you";	
	        	compliementColor = "cyan";
	            break; 
	        case 58:
	        	complimentString = "You could go longer without a shower than most people";	
	        	compliementColor = "orange";
	            break; 
	        case 59:
	        	complimentString = "You have ten of the best fingers I have ever seen!";	
	        	compliementColor = "yellow";
	            break; 
	        case 60:
	        	complimentString = "You're pretty high on my list of people with whom I would want to be stranded on an island";	
	        	compliementColor = "grey";
	            break; 
	        default :
	        	complimentString = "I'd let you steal the white part of my Oreo";	
	        	compliementColor = "yellow";
	            break; 
	  }	   
	 }
	 
	 private static void runTwitter() {
		 String tweetSearchTerm = twitterSearchString;
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
			                //TO DO have seen some cases where no results are returned, need to fix that and also we should show a different tweet if no new one
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
	  			
  				PIXELConsole.this.ioiO = ioio_;
  				
  				setupEnvironment();  //here we set the PIXEL LED matrix type
  				
  				
  				prox_ = ioio_.openAnalogInput(proximityPin_);
                //pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);   //AL could not make this work, did a quick hack, Roberto probably can change back to the right way
                pixel.matrix = ioio_.openRgbLedMatrix(KIND);
                pixel.ioiO = ioio_;
                System.out.println("Found PIXEL: " + pixel.matrix + "\n");
			
                CheckandRunMode();
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

	  	                runCompliments(); //get compliment with pseudo random number generator
	  	                
	  	                x = KIND.width * 2; //because we are interrupting an existing scrolling message, we have to reset the x position
	  	                loopCounter = 0;
	  	                scrollText(complimentString, compliementColor, 1,false);
	  	  			}
	  	  			//**********************************************************************************
	  	  		
	  	  			
	  	  			///******** we will interrupt any scrolling text with a stock readout if that is turned on 
	  	  			if (proxInt > TriggerUpperThreshold_ && ProxTriggerDone == true && stockMode == true) {
	  	  				ProxTriggerDone = false;
	  	  				
	  	  				try {
							stockPrice = getStock(stockSymbols);
							
							String StockScrollColor = "green"; //what color for the scrolling stock ticket
		  	  				if (stockChange.signum() > 0) {
		  	  					 StockScrollColor = "green";
		  	  				}
		  	  				else {
		  	  					 StockScrollColor = "red";
		  	  				}
		  	  				
		  	  				x = KIND.width * 2; //because we are interrupting an existing scrolling message, we have to reset the x position
		  	  				loopCounter = 0;
		  	  				scrollText(stockSymbols + ": " + stockPrice + " Change " + stockChange.toString() + "%", StockScrollColor, 1,false);
						} catch (IOException e) {
							scrollText("Could not get stock, pls check Internet connection", "red", 1,false);
							// TODO Auto-generated catch block
							e.printStackTrace();
							ProxTriggerDone = true;
							loopCounter = 0;
						}
	  	  				
	  	  				
	  	  			}
	  	  			//**********************************************************************************
	  	  			
				}
				
				Thread.sleep(sensorLoopDelay_);  //default is 500
			}
		};
	}
}

