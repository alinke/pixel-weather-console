pixel-weather-console
=====================

A PIXEL console application example. Console meaning java command line based where no GUI is required. 
This is useful for automating tasks on a dedicated computer like a Raspberry Pi for example. This particular
piece of code checks the weather conditions and then displays a corresponding weather animation on PIXEL.

Usage:

java -jar pixelweather.jar --zip=<your zip code>
--woeid can also be used in place of zip code

Use this command if PIXEL is not found to force the port detection

java -jar -Dioio.SerialPorts=<port of PIXEL> pixelweather.jar --zip=<your zip code>

Here are examples of the PIXEL port convetions:

Windows: COM40
Mac: tty.usbmodem41
Linux/Raspberry Pi: IOIO0

See https://github.com/ytai/ioio/wiki/Using-IOIO-With-a-PC for further documentation on ports and note there is a driver
that must be installed for Raspberry Pi and LINUX.
