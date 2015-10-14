package com.ledpixelart.console;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuickBase extends PIXELConsole {
	
	
	protected void runQuickBase() throws MalformedURLException, IOException {
		

		 
		
		 
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
		 
		    setAuthXml(null);
		    try
		    {
		       setAuthXml(new URL("https://" + getQuickBaseDomain() + "/db/main?a=API_Authenticate&username=" + getQuickBaseUserID() + "&password=" + getQuickBaseUserPassword()).openConnection().getInputStream());
	 		   DocumentBuilderFactory factory = DocumentBuilderFactory.
		                                        newInstance();
	 		   
		       DocumentBuilder builder = factory.newDocumentBuilder();
		       Document doc = builder.parse(getAuthXml());
		       
		       try {
		    		//optional, but recommended
		    		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		    		doc.getDocumentElement().normalize();
		    	 
		    		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    		
		    		NodeList nList = doc.getElementsByTagName(getQuickBaseRootXMLNode());
		    	 
		    		System.out.println("----------------------------");
		    	 
		    		for (int temp = 0; temp < nList.getLength(); temp++) {
		    	 
		    			Node nNode = nList.item(temp);
		    	 
		    			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		    	 
		    			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	 
		    				Element eElement = (Element) nNode;
		    				System.out.println("QB Auth Ticket : " + eElement.getElementsByTagName("ticket").item(0).getTextContent());
		    				setQuickbaseTicket(eElement.getElementsByTagName("ticket").item(0).getTextContent());
		    				
		    				System.out.println("QB Auth Error Code : " + eElement.getElementsByTagName("errcode").item(0).getTextContent());
		    				String errCode = eElement.getElementsByTagName("errcode").item(0).getTextContent();
		    				
		    				System.out.println("QB Auth Error Code Text : " + eElement.getElementsByTagName("errtext").item(0).getTextContent());
		    				System.out.println("QB Auth User ID " + eElement.getElementsByTagName("userid").item(0).getTextContent());
		    				
		    				if (errCode.equals("0")) {
		    					System.out.println("Yeah! QuickBase Authentication Successful...");
		    					setQuickBaseAuthSuccesful(true);
		    				}
		    				else {
		    					System.out.println("Sorry, QuickBase Authentication Failed!!!");
		    					setQuickBaseAuthSuccesful(false);
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
		          if (getAuthXml() != null)
		        	getAuthXml().close();
		       }
		       catch (IOException ex)
		       {
		          System.out.println(ex.getMessage());
		       }
		    }
		    
		    ///**** now we have our token so let's get some data
		    
		    setDataXml(null);
		    
		    try
		    {
		    	String URLString = "https://" + getQuickBaseDomain() + "/db/" + getQuickBaseDBID() +"?a=API_DoQuery&includeRids=1&ticket=" + getQuickbaseTicket() + "&apptoken=" + getQuickBaseToken() + "&udata=mydata&query={%27" + getQuickBaseQueryFieldID() + "%27.CT.%27" + getQuickBaseSearchTermForDescriptionField() + "%27}&clist=" + getQuickBaseReturnFields() + "&slist=3&options=nosort&fmt=structured";
		    	
		       setDataXml(new URL(URLString).openConnection().getInputStream());
	 		   DocumentBuilderFactory factory = DocumentBuilderFactory.
		                                        newInstance();
	 		   
		       DocumentBuilder builder = factory.newDocumentBuilder();
		       Document doc = builder.parse(getDataXml());
		       
		       try {
		    		//optional, but recommended
		    		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		    		doc.getDocumentElement().normalize();
		    	 
		    		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    	 
		    		NodeList nList = doc.getElementsByTagName(getQuickBaseDataXMLNode()); //this is record by default, you should not have to change unless the QB API changes
		    	 
		    		System.out.println("----------------------------");
		    		System.out.println("Total Matched Records: " + nList.getLength());
		    		
		    		if (nList.getLength() == 1) {
		    			setQuickBasePIXELString(nList.getLength() + " project behind schedule: "); //this is the text we will send to the scrolling LED display and will be specific to your application so change accordingly
		    		}
		    		
		    		else {
		    			setQuickBasePIXELString(nList.getLength() + " projects behind schedule: "); //this is the text we will send to the scrolling LED display and will be specific to your application so change accordingly
		    		}
		    		
		    		for (int temp = 0; temp < nList.getLength(); temp++) {
		    	 
		    			Node nNode = nList.item(temp);
		    	 
		    			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		    	 
		    			//******* This part of the code will be specific to your application so change accordingly *********
		    			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	 
		    				Element eElement = (Element) nNode;
		    				System.out.println("Project ID : " + eElement.getElementsByTagName("f").item(0).getTextContent());
		    				
		    				if (temp == nList.getLength() - 1) { //it's the last record so don't add the comma at the end
		    					setQuickBasePIXELString(getQuickBasePIXELString() + eElement.getElementsByTagName("f").item(0).getTextContent());
		    				}
		    				else {
		    					setQuickBasePIXELString(getQuickBasePIXELString() + eElement.getElementsByTagName("f").item(0).getTextContent() + ", ");
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
		          if (getDataXml() != null)
		        	  getDataXml().close();
		       }
		       catch (IOException ex)
		       {
		          System.out.println(ex.getMessage());
		       }
		    }
		    
		    if (isQuickBaseAuthSuccesful()) {
		    	System.out.println("LED Scrolling Text = " + getQuickBasePIXELString());
		    }
		    else {
		    	System.out.println("Sorry, unable to authenticate to QuickBase");
		    }
		    
		    if (getQ() != 8) { //8 is the number of command line parameters required for QuickBase
		    	System.out.println("Sorry! You didn't enter all the command line paramters that QuickBase needs. Please modify and try again.");
		    	setQuickBasePIXELString("Sorry! You didn't enter all the command line paramters that QuickBase needs. Please modify and try again.");
			}
		    

	    	
			scroll.scrollText(getQuickBasePIXELString(), scrollingTextColor_, getQuickBaseRefreshInterval(),false); //0 means loop forever
		    
		    //TO DO add a loop if the auth fails to try again after x seconds
		    // http://www.mkyong.com/webservices/jax-ws/suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target/  //this problems only happened on my PC, was fine when compiling on Mac
		   //TO DO put the above in a timer such that it re-runs itself every x minutes as defined by a command line
	 
		
		
	}

}
