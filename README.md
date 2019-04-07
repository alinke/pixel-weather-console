pixel-console version
=====================

A PIXEL command line app where no GUI is required. 
This is useful for automating tasks on a dedicated computer like a Raspberry Pi for example. 

This Java console app uses the following libraries:
IOIOLibPC - external
httpclient-4.3.4.jar - located in the jar folder
httpcore-4.3.2.jar - located in the jar folder

*** PIXEL: Command Line App (CLI) ***

Usage:
java -jar pixelc.jar <options>

Example project using a Raspberry Pi, PIXEL:Maker's Kit board, and a 64x32 LED Matrix https://www.instructables.com/id/Vertical-Bartop-Arcade-With-Integrated-PIXEL-LED-D/
Example usage:  java -jar pixelc.jar --gif=0pacman.gif --adafruit64x32 --write

See http://ledpixelart.com/console/ for all the command line options
