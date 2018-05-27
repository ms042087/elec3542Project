# -*- coding: utf-8 -*-
"""
Created on Mon Mar 05 18:09:46 2018
@author: Ho Yu Hin
Last update on 20/05/18

Aim:
Continuously geting the speed and inclination info
Updating info to ThingSpeak
Check if dangerous, display the message

Design Details and Consideration:

1. Continuously geting the speed and inclination info
    from the Arduino and senseHAT
    # The speed is updated when the wheel of the car
        completed 1 revolution, i.e. The hall effect sensor senses
        the change in B-field
    # The inclination info is updated every 0.2 seconds
    # IMPORTANT: The inclination must be measured very FREQUENTLY
        since this data is created by combining multiple sensors data
        in senseHAT,including gyroscope, accelermeter, ...
        The data would be extremely inaccurate if the
        measurement interval is large
    # Thus, we CANNOT PAUSE the measurement to update info to ThingSpeak
        platform nor displaying warning message.
    # i.e. multithreading must be used
    
2. Updating info to ThingSpeak
    Use timeStamps to count the elasped time.
    Create a new THREAD to update the info
    to ThingSpeak platform every 2 seconds.
    
3. Check if dangerous, display the message
    Create a new THREAD to display the message if needed

"""
import requests
from sense_hat import SenseHat
import time
import serial
import threading

# Use ls /dev/tty* to check the serial port that connecting to Arduino
ser=serial.Serial("/dev/ttyUSB0",9600)
ser.baudrate=9600

sense = SenseHat()

def getSpeed():
    while(ser.in_waiting): # check if there is incoming data first
        line = ser.readline().strip(); # otherwise stuck at this line
        speed = line.decode('ascii')
        print("speed: ",speed)
        return speed

def getInclination():
    orientation = sense.get_orientation()
    r=round(orientation["roll"],0)
    print("inclination: ",r)
    return r

# return True if |inclination| > 20
def exceedInclination(r):
    if (r<180 and r>20) or (r>180 and r<340) :
        return True
    else:
        return False
    
# update the data to ThingSpeak platform
def send(speed, inclination):
    api_key = "OQWDMGCO9ZYVIYSF"
    data = {"api_key": api_key, "field1": speed, "field2": inclination}
    req = requests.post("https://api.thingspeak.com/update", data=data)
    return req.text

class alarm(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
    def start(self):
        threading.Thread.__init__(self)
        threading.Thread.start(self)
    def run(self):
        sense.show_message("!", text_colour=(255,0,0))
        time.sleep(1)
        sense.clear()
        
        
timeStampOld = time.time();
t2= alarm()

while(1):

    timeStampNew= time.time();
    speed = getSpeed();
    r = getInclination();
    
    if(timeStampNew - timeStampOld) > 2:
        t1 = threading.Thread(target=send, args=(speed,r))
        t1.start()
        timeStampOld = timeStampNew
        print("Updated to ThingSpeak")
    
    if(exceedInclination(r) == True):
        print("Dangerous")
        if(t2.isAlive() == False):
            t2.start()
            
    time.sleep(0.2)