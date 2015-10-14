package com.ledpixelart.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ledpixelart.console.PIXELConsole.BadArgumentsException;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class PIXELConsole_main extends PIXELConsole{
	
	public static void main(String[] args) throws Exception{
	   
	    PIXELConsole  console = new PIXELConsole();
	    console.twitter_timer();
	    console.config(args);
	   //System.out.println("Working Directory = " + System.getProperty("user.dir"));
		new PIXELConsole().go(args);	
	    }
       }
