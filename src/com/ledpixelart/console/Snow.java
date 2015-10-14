package com.ledpixelart.console;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

public class Snow extends PIXELConsole  {
	
	String PIXELString = "";
	void runSNOW(ArrayList<String> snowGroupName, ArrayList<String> snowGroupGUID) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
		 //Edited by ::: Sucheta
		 Map<String,String> map = combineListsIntoOrderedMap (snowGroupName, snowGroupGUID);
		 System.out.println(map);
		 //for verifying values
		 System.out.println("snowGroupName and snowGroupGUID Entered by User\n");
		 for (Map.Entry<String, String> entry : map.entrySet())
		 {	
			 System.out.println("snowGroupName : " + entry.getKey() + " snowGroupGUID : " + entry.getValue());//get name as key and ID as value
		 }
		 
		 for (Map.Entry<String, String> entry : map.entrySet())
		 {
			 System.out.println("\n\nGetting Info for :: snowGroupName : " + entry.getKey() + " snowGroupGUID : " + entry.getValue());//get name as key and ID as value
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
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + getSnowBaseQuery() + "assignment_group%" + entry.getValue() + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
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
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + getSnowBaseQuery() + "assignment_group%" + entry.getValue().toString() + "%5E" + getSnowPriorityQuery() + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
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
	        setSnowGroupPriority(returnXMLNodeValue(snowDataXMLNode,responseBody));
	      
	        //now let's get one that have exceeded SLA
	        //let's get the tickets in this queue that are of high priority   
	        try {
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + getSnowBaseQuery() + "assignment_group%" + entry.getValue().toString() + "%5E" + getSnowSLAExceededQuery() + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
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
	        
	        setSnowGroupExceededSLA(returnXMLNodeValue(snowDataXMLNode,responseBody));
	        
	        if (s < 5) { 
		    	System.out.println("Sorry! You didn't enter all the command line paramters that Service Now needs. Please modify and try again.");
		    	setSnowPIXELString("Sorry! You didn't enter all the command line paramters that Service Now needs. Please modify and try again.");
			}
	        
	        setSnowPIXELString(entry.getKey() +": " + "Open=" + snowGroupOpen + " Priority=" + getSnowGroupPriority() + " Overdue=" + getSnowGroupExceededSLA()) ;
	        PIXELString= PIXELString.concat(getSnowPIXELString())+" ";
		   } 
		    System.out.println(PIXELString);
		    scroll.scrollText(PIXELString, scrollingTextColor_, snowRefreshInterval,false); //will refresh by default after 25 scrolls, false means keep going forever
	        
	      }
	private Map<String, String> combineListsIntoOrderedMap(List<String> snowGroupName, List<String> snowGroupGUID) {
		// TODO Auto-generated method stub
		 if (snowGroupName.size() != snowGroupGUID.size())
		      throw new IllegalArgumentException ("Cannot combine lists with dissimilar sizes");
		    Map<String,String> map = new LinkedHashMap<String,String>();
		    for (int i=0; i<snowGroupName.size(); i++) {
		      map.put(snowGroupName.get(i), snowGroupGUID.get(i));
		    }
		    return map;
		  } 
	}