
package org.onebeartoe.pixel.hardware;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;

import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

import com.ledpixelart.console.GifDecoder;

/*This class has the following methods

SendPixelDecodedFrame - sends one raw RGB565 frame to PIXEL . Typically you would use this method
in a loop from your main code to animate a gif

checkIfGIFDecoded - will check if the decoded rgb565 and txt files are there. If yes, then also
check that the resolution in the decoded file matches the resolution of the current LED matrix. 
If no or if the files don't exist, then call the decodeGIF method to re-encode

decodeGIF - will decode the gif into a raw rgb565 and corresponding txt metadata file
*/

/**
 * @author rmarquez and Al Linke
 */
public class Pixel 
{
    public static IOIO ioiO;
    
    public RgbLedMatrix matrix;
    
    public final RgbLedMatrix.Matrix KIND;
    
   // private static int frame_length = 0;
    
    public static AnalogInput analogInput1;
    
    public static  AnalogInput analogInput2;
    
    protected byte[] BitmapBytes;
    
    protected InputStream BitmapInputStream;
    
    protected short[] frame_;
    
    private float fps;
    
    private static String decodedDirPathExternal;
    
    public Pixel(RgbLedMatrix.Matrix KIND)
    {
	
	   this.KIND = KIND;
		
		BitmapBytes = new byte[KIND.width * KIND.height * 2]; //512 * 2 = 1024 or 1024 * 2 = 2048
		
		frame_ = new short[KIND.width * KIND.height];
	
    }
    
        private static AnalogInput getAnalogInput(int pinNumber) 
    {
	if(ioiO != null)
	{
	    try 
	    {
		analogInput1 = ioiO.openAnalogInput(pinNumber);
	    } 
	    catch (ConnectionLostException ex) 
	    {
		String message = "The IOIO connection was lost.";
		Logger.getLogger("Pixel").log(Level.SEVERE, message, ex);
	    }		
	}
        
        return analogInput1;
    }
   
    
  //*** Al added, this code is to support the SD card and local animations
    public void interactiveMode() {  //puts PIXEL into interactive mode
    	try {
			matrix.interactive();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void writeMode(float frameDelay) {  //puts PIXEL into write mode
    	try {
    		 matrix.writeFile(frameDelay); //put PIXEL in write mode
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void playLocalMode() {  //tells PIXEL to play the local files
    	try {
    		matrix.playFile();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
       
private int[] getDecodedMetadata(String currentDir, String gifName) {  //not using this one right now
	
    	String gifNamePath = currentDir + "/decoded/" + gifName + ".txt";
    	
    	File filemeta = new File(gifNamePath);
    	int[] decodedMetadata = null; //array    	
    	FileInputStream decodedFile = null; //fix this
    	try {
			decodedFile = new FileInputStream(gifNamePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
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
		    
		    int resolution = Integer.parseInt(fileAttribs2[1].trim());	  //TO DO FIX THIS
		    
		    decodedMetadata[0] = selectedFileTotalFrames;
		    decodedMetadata[1] = selectedFileDelay;
		    decodedMetadata[2] = resolution;
		    
		    
		    if (selectedFileDelay != 0) {  //then we're doing the FPS override which the user selected from settings
	    	    fps = 1000.f / selectedFileDelay;
			} else { 
	    		fps = 0;
	    	}

		   return (decodedMetadata); //we are returning an array here
	}

public boolean GIFNeedsDecoding(String currentDir, String gifName, int currentResolution) {
	
	/*In this method we will first check if the decoded files are there
	if they are present, then let's read them and make sure the resolution in the decoded file matches the current matrix
	if no match, then we need to re-encode
	if the files are not there, then we need to re-encode anyway*/
	
	/*GIFName will be tree
	GIF Path will be c:\animations\tree.gif
	decdoed path will be c:\animations\tree.gif\decoded\tree.rgb565 and tree.txt
	*/
	
	gifName = FilenameUtils.removeExtension(gifName); //with no extension
	
	System.out.println("PIXEL LED panel resolution is: " + currentResolution);
	
	String decodedGIFPathTXT = currentDir + "/decoded/" + gifName + ".txt";
	String decodedGIFPath565 = currentDir + "/decoded/" + gifName + ".rgb565";
	
	File filetxt = new File(decodedGIFPathTXT);
	File file565 = new File(decodedGIFPath565);
	
	if (filetxt.exists() && file565.exists()) { //need to ensure both files are there
		   
			if (getDecodedresolution(currentDir, gifName) == currentResolution) {  //does the resolution in the encoded txt file match the current matrix
				
				return false;
			}
			else {
				return true;
			}
	}
	else {
		return true;
	}
}
    
    public float getDecodedfps(String currentDir, String gifName) {  //need to return the meta data
    	
    	gifName = FilenameUtils.removeExtension(gifName); //with no extension, ex. tree instead of tree.gif
    	String gifNamePath = currentDir + "/decoded/" + gifName + ".txt"; 
    	File filemeta = new File(gifNamePath);
    	
    	FileInputStream decodedFile = null; //fix this
    	try {
			decodedFile = new FileInputStream(gifNamePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
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
		    int selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());	
		    
		    if (selectedFileDelay != 0) {  //then we're doing the FPS override which the user selected from settings
	    	    fps = 1000.f / selectedFileDelay;
			} else { 
	    		fps = 0;
	    	}

		   return (fps);
	}
    
    public int getDecodednumFrames(String currentDir, String gifName) {  //need to return the meta data
    	
    	gifName = FilenameUtils.removeExtension(gifName); //with no extension
    	//String framestring = "animations/decoded/" + animation_name + ".rgb565";
    	//String gifNamePath = gifName + ".txt";
    	String gifNamePath = currentDir + "/decoded/" + gifName + ".txt"; 
    	File filemeta = new File(gifNamePath);    	
    	FileInputStream decodedFile = null; //fix this
    	try {
			decodedFile = new FileInputStream(gifNamePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
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
		  
		   return (selectedFileTotalFrames);
	}
    
 public int getDecodedresolution(String currentDir, String gifName) {  //need to return the meta data
    	
	    gifName = FilenameUtils.removeExtension(gifName); //with no extension
	    //String framestring = "animations/decoded/" + animation_name + ".rgb565";
	   // String gifNamePath = gifName + ".txt";
	    String gifNamePath = currentDir + "/decoded/" + gifName + ".txt"; 
    	File filemeta = new File(gifNamePath);
    	
    	FileInputStream decodedFile = null; //fix this
    	try {
			decodedFile = new FileInputStream(gifNamePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
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
		    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60,32  where last 32 is the resolution
		    String[] fileAttribs2 = fileAttribs.split(fdelim);
		    int resolution = Integer.parseInt(fileAttribs2[2].trim());	
		  
		   return (resolution);
	}
    
    public int getDecodedframeDelay(String currentDir, String gifName) {  //need to return the meta data
    	
    	
    	gifName = FilenameUtils.removeExtension(gifName); //with no extension
    	String gifNamePath = currentDir + "/decoded/" + gifName + ".txt"; 
    	
    	File filemeta = new File(gifNamePath);
    	
    	FileInputStream decodedFile = null; //fix this
    	try {
			decodedFile = new FileInputStream(gifNamePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
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
		    int selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());

		   return (selectedFileDelay);
	}
    
    
    public void SendPixelDecodedFrame(String currentDir, String gifName, int x, int selectedFileTotalFrames, int selectedFileResolution, int frameWidth, int frameHeight) {
		 
    	BitmapBytes = new byte[frameWidth * frameHeight * 2]; //512 * 2 = 1024 or 1024 * 2 = 2048
		frame_ = new short[frameWidth * frameHeight];
    	
    	gifName = FilenameUtils.removeExtension(gifName); //with no extension
    	String gifNamePath = currentDir + "/decoded/" + gifName + ".rgb565";  //  ex. c:\animations\decoded\tree.rgb565
    	
    	File file = new File(gifNamePath);
			if (file.exists()) {
				
			/*Because the decoded gif is one big .rgb565 file that contains all the frames, we need
			to use the raf pointer and extract just a single frame at a time and then we'll move the 
			pointer to get the next frame until we reach the end of the file*/
				
     		RandomAccessFile raf = null;
			
			//let's setup the seeker object and set it at the beginning of the rgb565 file
			try {
				raf = new RandomAccessFile(file, "r");
				try {
					raf.seek(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}  // "r" means open the file for reading
			
			int frame_length;
			
			 switch (selectedFileResolution) {
	            case 16: frame_length = 1048;
	                     break;
	            case 32: frame_length = 2048;
	                     break;
	            case 64: frame_length = 4096;
	                     break;
	            case 128: frame_length = 8192;
                		break;
	            default: frame_length = 2048;
	                     break;
	          }
			
			//now let's see forward to a part of the file
			try {
				raf.seek(x*frame_length);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} 
			
   			 
   			if (frame_length > Integer.MAX_VALUE) {
   			    try {
					throw new IOException("The file is too big");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
   			}
   			 
   			// Create the byte array to hold the data
   			BitmapBytes = new byte[(int)frame_length];
   			
   			// Read in the bytes
   			int offset = 0;
   			int numRead = 0;
   			try {
				while (offset < BitmapBytes.length && (numRead=raf.read(BitmapBytes, offset, BitmapBytes.length-offset)) >= 0) {
				    offset += numRead;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
   			 
   			// Ensure all the bytes have been read in
   			if (offset < BitmapBytes.length) {
   			    try {
					throw new IOException("The file was not completely read: "+file.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
   			}
   			 
   			// Close the input stream, all file contents are in the bytes variable
   			try {
   				raf.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
   			
   			//now that we have the byte array loaded, load it into the frame short array
   			
   			int y = 0;
     		for (int i = 0; i < frame_.length; i++) {
     			frame_[i] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
     			y = y + 2;
     		}
     		
		   	try {
		   		matrix.frame(frame_);
				
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  // 	x++;  //this al commented out for this class as I don't think we need it as i is being incremental in the place this class is getting called
			}
			
			else {
				//showToast("We have a problem, couldn't find the decoded file");
				System.err.println("An error occured while trying to load " + gifNamePath + ".");
	    	    System.err.println("Make sure " + gifNamePath + "is included in the executable JAR.");
	    	   // e.printStackTrace();
			}
	}
    
	public void decodeGIF(String currentDir, String gifName, int currentResolution, int pixelMatrix_width, int pixelMatrix_height) {  //pass the matrix type
		
		//we're going to decode a native GIF into our RGB565 format
	    //we'll need to know the resolution of the currently selected matrix type: 16x32, 32x32, 32x64, or 64x64
		//and then we will receive the gif accordingly as we decode
		//we also need to get the original width and height of the gif which is easily done from the gif decoder class
		gifName = FilenameUtils.removeExtension(gifName); //with no extension
		String gifNamePath = currentDir + "/" + gifName + ".gif";  //   ex. c:\animation\tree.gif
		
		File file = new File(gifNamePath);
		if (file.exists()) {
			
			  //since we are decoding, we need to first make sure the .rgb565 and .txt decoded file is not there and delete if so.
			  String gifName565Path = currentDir + "/decoded/" + gifName + ".rgb565";  //   ex. c:\animation\decoded\tree.rgb565
			  String gifNameTXTPath = currentDir + "/decoded/" + gifName + ".txt";  //   ex. c:\animation\decoded\tree.txt
			  File file565 = new File(gifName565Path);
			  File fileTXT = new File(gifNameTXTPath);
			  
			  if (file565.exists()) file565.delete();
			  if (fileTXT.exists()) file565.delete();
			  //*******************************************************************************************
			
			  GifDecoder d = new GifDecoder();
	          d.read(gifNamePath);
	          int numFrames = d.getFrameCount(); 
	          int frameDelay = d.getDelay(1); //even though gifs have a frame delay for each frmae, pixel doesn't support this so we'll take the frame rate of the second frame and use this for the whole animation. We take the second frame because often times the frame delay of the first frame in a gif is much longer than the rest of the frames
	          
	          Dimension frameSize = d.getFrameSize();
	          int frameWidth = frameSize.width;
	          int frameHeight = frameSize.height;
	         
	          System.out.println("frame count: " + numFrames);
	          System.out.println("frame delay: " + frameDelay);
	          System.out.println("frame height: " + frameHeight);
	          System.out.println("frame width: " + frameWidth);
	          	          
	          for (int i = 0; i < numFrames; i++) { //loop through all the frames
	             BufferedImage rotatedFrame = d.getFrame(i);  
	            // rotatedFrame = Scalr.rotate(rotatedFrame, Scalr.Rotation.CW_90, null); //fixed bug, no longer need to rotate the image
	            // rotatedFrame = Scalr.rotate(rotatedFrame, Scalr.Rotation.FLIP_HORZ, null); //fixed bug, no longer need to flip the image
	             
	             // These worked too but using the scalr library gives quicker results
	             //rotatedFrame = getFlippedImage(rotatedFrame); //quick hack, for some reason the code below i think is flipping the image so we have to flip it here as a hack
	             //rotatedFrame = rotate90ToLeft(rotatedFrame);  //quick hack, same as above, have to rotate
	              
	    		 if (frameWidth != pixelMatrix_width || frameHeight != pixelMatrix_height) {
	    			 System.out.println("Resizing and encoding " + gifNamePath + " frame " + i);
	    			// rotatedFrame = Scalr.resize(rotatedFrame, pixelMatrix_width, pixelMatrix_height); //resize it, need to make sure we do not anti-alias
	    			 
	    			 try {
						rotatedFrame = getScaledImage(rotatedFrame, pixelMatrix_width,pixelMatrix_height);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			 
	    			 
	    		 }
	    		 else {
	    			 System.out.println("Encoding " + gifNamePath + " frame " + i);
	    		 }
	            
	             //this code here to convert a java image to rgb565 taken from stack overflow http://stackoverflow.com/questions/8319770/java-image-conversion-to-rgb565/
	    		 BufferedImage sendImg  = new BufferedImage(pixelMatrix_width, pixelMatrix_height, BufferedImage.TYPE_USHORT_565_RGB);
	             sendImg.getGraphics().drawImage(rotatedFrame, 0, 0, pixelMatrix_width, pixelMatrix_height, null);    

	             int numByte=0;
	             BitmapBytes = new byte[pixelMatrix_width*pixelMatrix_height*2];

	                int x=0;
	                int y=0;
	                int len = BitmapBytes.length;

	                for (x=0 ; x < pixelMatrix_width; x++) {
	                    for (y=0; y < pixelMatrix_height; y++) {

	                        Color c = new Color(sendImg.getRGB(y, x));  // x and y were switched in the original code which was causing the image to rotate by 90 degrees and was flipped horizontally, switching x and y fixes this bug
	                        int red = c.getRed();
	                        int green = c.getGreen();
	                        int blue = c.getBlue();

	                        //RGB565
	                        red = red >> 3;
	                        green = green >> 2;
	                        blue = blue >> 3;    
     			  		
	                        //A pixel is represented by a 4-byte (32 bit) integer, like so:
	                        //00000000 00000000 00000000 11111111
	                        //^ Alpha  ^Red     ^Green   ^Blue
	                        //Converting to RGB565

	                        short pixel_to_send = 0;
	                        int pixel_to_send_int = 0;
	                        pixel_to_send_int = (red << 11) | (green << 5) | (blue);
	                        pixel_to_send = (short) pixel_to_send_int;
	                        //dividing into bytes
	                        byte byteH=(byte)((pixel_to_send >> 8) & 0x0FF);
	                        byte byteL=(byte)(pixel_to_send & 0x0FF);

	                        //Writing it to array - High-byte is the first, big endian byte order
	                        BitmapBytes[numByte]=byteL;
	                        BitmapBytes[numByte+1]=byteH;
	                        
	                        numByte+=2;
	                    }
	                }
			   		    
			     decodedDirPathExternal = currentDir + "/decoded" ;   //  ex. c:\animations\decoded
			   		    
			   		 File decodeddir = new File(decodedDirPathExternal); //this could be gif, gif64, or usergif
					    if(decodeddir.exists() == false)
			             {
					    	decodeddir.mkdirs();
			             }
					
				   			try {
							
								appendWrite(BitmapBytes, decodedDirPathExternal + "/" + gifName + ".rgb565"); //this writes one big file instead of individual ones
								
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								//Log.e("PixelAnimate", "Had a problem writing the original unified animation rgb565 file");
								e1.printStackTrace();
							}
				  
	             
	          } //end for, we are done with the loop so let's now write the file
	          
	           //********** now let's write the meta-data text file
		   		
		   		if (frameDelay == 0 || numFrames == 1) {  //we can't have a 0 frame delay so if so, let's add a 100ms delay by default
		   			frameDelay = 100;
		   		}
		   		
		   		String filetag = String.valueOf(numFrames) + "," + String.valueOf(frameDelay) + "," + String.valueOf(currentResolution); //current resolution may need to change to led panel type
		   				
	     		   File myFile = new File(decodedDirPathExternal + "/" + gifName + ".txt");  				       
	     		   try {
					myFile.createNewFile();
					FileOutputStream fOut = null;
					fOut = new FileOutputStream(myFile);
			        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
					myOutWriter.append(filetag); 
					myOutWriter.close();
					fOut.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("ERROR, could not write " + gifName);
					e.printStackTrace();
				}
		}
		else {
			System.out.println("ERROR  Could not write " + decodedDirPathExternal + "/" + gifName + ".txt");
		}
			
	}  
          
  public static void appendWrite(byte[] data, String filename) throws IOException {
	 FileOutputStream fos = new FileOutputStream(filename, true);  //true means append, false is over-write
     fos.write(data);
     fos.close();
  }
  
  public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException { //resizes our image and preserves hard edges which we need for pixel art
	    int imageWidth  = image.getWidth();
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    double scaleY = (double)height/imageHeight;
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

	    return bilinearScaleOp.filter(
	        image,
	        new BufferedImage(width, height, image.getType()));
	}
  
  /*public static BufferedImage getFlippedImage(BufferedImage bi) { //we're not using
      BufferedImage flipped = new BufferedImage(
              bi.getWidth(),
              bi.getHeight(),
              bi.getType());
      AffineTransform tran = AffineTransform.getTranslateInstance(bi.getWidth(), 0);
      AffineTransform flip = AffineTransform.getScaleInstance(-1d, 1d);
      tran.concatenate(flip);

      Graphics2D g = flipped.createGraphics();
      g.setTransform(tran);
      g.drawImage(bi, 0, 0, null);
      g.dispose();

      return flipped;
  }
  
  
  private BufferedImage rotate90ToLeft( BufferedImage inputImage ){  //we're not using
		//The most of code is same as before
			int width = inputImage.getWidth();
			int height = inputImage.getHeight();
			BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );
		//We have to change the width and height because when you rotate the image by 90 degree, the
		//width is height and height is width <img src='http://forum.codecall.net/public/style_emoticons/<#EMO_DIR#>/smile.png' class='bbc_emoticon' alt=':)' />

			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					returnImage.setRGB(y, width - x - 1, inputImage.getRGB(x, y));
		//Again check the Picture for better understanding
				}
			}
			return returnImage;
		}
  
  private BufferedImage rotate90ToRight( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( height - y - 1, x, inputImage.getRGB( x, y  )  );
	//Again check the Picture for better understanding
			}
		}
		return returnImage;
	}
  
  public BufferedImage rotate180( BufferedImage inputImage ) {
	//We use BufferedImage because it’s provide methods for pixel manipulation
		int width = inputImage.getWidth(); //the Width of the original image
		int height = inputImage.getHeight();//the Height of the original image

		BufferedImage returnImage = new BufferedImage( width, height, inputImage.getType()  );
	//we created new BufferedImage, which we will return in the end of the program
	//it set up it to the same width and height as in original image
	// inputImage.getType() return the type of image ( if it is in RBG, ARGB, etc. )

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( width - x - 1, height - y - 1, inputImage.getRGB( x, y  )  );
			}
		}
	//so we used two loops for getting information from the whole inputImage
	//then we use method setRGB by whitch we sort the pixel of the return image
	//the first two parametres is the X and Y location of the pixel in returnImage and the last one is the //source pixel on the inputImage
	//why we put width – x – 1 and height –y – 1 is hard to explain for me, but when you rotate image by //180degree the pixel with location [0, 0] will be in [ width, height ]. The -1 is for not to go out of
	//Array size ( remember you always start from 0 so the last index is lower by 1 in the width or height
	//I enclose Picture for better imagination  ... hope it help you
		return returnImage;
	//and the last return the rotated image

	}*/
  
  public static AnalogInput getAnalogInput1() 
  {
      if (analogInput1 == null) 
	{
	    analogInput1 = getAnalogInput(31);			    
      }
      
      return analogInput1;
  }
  
  public static AnalogInput getAnalogInput2() 
  {
      if (analogInput2 == null) 
	{
	    analogInput2 = getAnalogInput(32);
      }
      
      return analogInput2;
  }
  
  public void loadRGB565Weather(String raw565ImagePath) throws ConnectionLostException //to do clean this up later, these are still using the rgb565 as single files
  {
	BitmapInputStream = getClass().getClassLoader().getResourceAsStream(raw565ImagePath);

	try 
	{   
	    int n = BitmapInputStream.read(BitmapBytes, 0, BitmapBytes.length);
	    Arrays.fill(BitmapBytes, n, BitmapBytes.length, (byte) 0);
	} 
	catch (IOException e) 
	{
	    System.err.println("An error occured while trying to load " + raw565ImagePath + ".");
	    System.err.println("Make sure " + raw565ImagePath + "is included in the executable JAR.");
	    e.printStackTrace();
	}

	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}
//      matrix = PixelApp.getMatrix();
	matrix.frame(frame_);
  }
    
    
    private void loadRGB565PNG() throws ConnectionLostException //not using this one
    {
	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{   
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}

//        matrix = PixelApp.getMatrix();
	if(matrix != null)
	{
	    matrix.frame(frame_);
	}
    }
    
    
    private void writeImagetoMatrix(BufferedImage originalImage) throws ConnectionLostException  //not using this one right now    
    {        
	//here we'll take a PNG, BMP, or whatever and convert it to RGB565 via a canvas, also we'll re-size the image if necessary
        int width_original = originalImage.getWidth();
        int height_original = originalImage.getHeight();

        if (width_original != KIND.width || height_original != KIND.height) 
        {  
            //the image is not the right dimensions, ie, 32px by 32px				
            BufferedImage ResizedImage = new BufferedImage(KIND.width, KIND.height, originalImage.getType());
            Graphics2D g = ResizedImage.createGraphics();
            //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(originalImage, 0, 0, KIND.width, KIND.height, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
            g.dispose();
            originalImage = ResizedImage;		
        }

        int numByte = 0;
        int i = 0;
        int j = 0;

        for (i = 0; i < KIND.height; i++) 
        {
            for (j = 0; j < KIND.width; j++) 
            {
                Color c = new Color(originalImage.getRGB(j, i));  //i and j were reversed which was rotationg the image by 90 degrees
//                int aRGBpix = originalImage.getRGB(j, i);  //i and j were reversed which was rotationg the image by 90 degrees
//                int alpha;
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                //RGB565
                red = red >> 3;
                green = green >> 2;
                blue = blue >> 3;
                //A pixel is represented by a 4-byte (32 bit) integer, like so:
                //00000000 00000000 00000000 11111111
                //^ Alpha  ^Red     ^Green   ^Blue
                //Converting to RGB565

                short pixel_to_send = 0;
                int pixel_to_send_int = 0;
                pixel_to_send_int = (red << 11) | (green << 5) | (blue);
                pixel_to_send = (short) pixel_to_send_int;

                //dividing into bytes
                byte byteH = (byte) ((pixel_to_send >> 8) & 0x0FF);
                byte byteL = (byte) (pixel_to_send & 0x0FF);

                //Writing it to array - High-byte is the first

                BitmapBytes[numByte + 1] = byteH;
                BitmapBytes[numByte] = byteL;
                numByte += 2;
            }
        }

	loadRGB565PNG();
    }
    
    /**          
     * this part of code writes to the LED matrix in code without any external file
     * this just writes a test pattern to the LEDs in code without using any external 
     * file	
     */
    private void writeTest()  //not using this one
    {
	for (int i = 0; i < frame_.length; i++) 
	{
	    //	frame_[i] = (short) (((short) 0x00000000 & 0xFF) | (((short) (short) 0x00000000 & 0xFF) << 8));  //all black
	    frame_[i] = (short) (((short) 0xFFF5FFB0 & 0xFF) | (((short) (short) 0xFFF5FFB0 & 0xFF) << 8));  //pink
	    //frame_[i] = (short) (((short) 0xFFFFFFFF & 0xFF) | (((short) (short) 0xFFFFFFFF & 0xFF) << 8));  //all white
	}
    }
}
