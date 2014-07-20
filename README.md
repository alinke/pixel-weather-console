pixel-console version
=====================

A PIXEL console application example. Console meaning java command line based where no GUI is required. 
This is useful for automating tasks on a dedicated computer like a Raspberry Pi for example. 

*** PIXEL: Console Version ***

Usage:
pixel <options>

Valid options are:
GIF MODE

--gif=your_filename.gif  Send this gif to PIXEL
--write  Puts PIXEL into write mode, default is streaming mode
--superpixel  change LED matrix to SUPER PIXEL 64x64
--16x32  change LED matrix to Adafruit's 16x32 LED matrix
Ex. java -jar -Dioio.SerialPorts=COM14 pixel.jar --gif=tree.gif
Ex. java -jar -Dioio.SerialPorts=COM14 pixel.jar --gif=tree.gif --superpixel --w
rite


WEATHER MODE
--zip=your_zip_code Non-US users should use woeid
--woeid=your_woeid_code A numeric number that Yahoo uses
 to designate your location
--forecast Displays tomorrow's weather conditions, defaults
to current weather conditions if not specified
Ex. java -jar -Dioio.SerialPorts=COM14 pixel.jar --zip=95050
Ex. java -jar -Dioio.SerialPorts=COM14 pixel.jar --zip=95050 --write


Omitting -Dioio.SerialPorts=<Port of PIXEL> may still work
but will take longer for your computer to scan all ports to find PIXEL
<Port of PIXEL> examples:

Windows: COM40
Mac: tty.usbmodem1412
Linux/Raspberry Pi: IOIO0


See http://ledpixelart.com/raspberry-pi/ for Rasp Pi setup instructions
Type q to quite this program
