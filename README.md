# SensorDataCollector
Simple application to check out various sensors usually available on most phones these days  

Uses modern permissions interface and a simple Handler for managing data collection on seperate thread.  
Uses Room API for saving sensor data into a sqlite database and provides an option to export that data to csv file.  

Sensor data collected:  
- Microphone level  
- Location coordinates
- Accelerometer coordinates
- WiFi AP name
- WiFi AP signal

The app cannot retrieve wifi AP information unless location data is turned on. It's a known issue in the APIs.  
Solutions are welcome.
