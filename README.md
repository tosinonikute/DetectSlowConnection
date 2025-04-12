# detect slow connection
- A sample app that shows different ways of detecting slow connections

## In Android, there are ways to determine  your  connection speed

1.  Using NetworkInfo class, ConnectivityManager and TelephonyManager to determine your network type.
2.  Another test is to download some file from the internet & calculate how long it took vs number of bytes in the file.
3.  Use Facebook’s Network Connection Class library to figure out the quality of the current user’s internet connection on the fly.

- Connection library provides different connection qualities

**_POOR_**  _Bandwidth under_ _150_ kbps.
**MODERATE**  Bandwidth between 150 and 550 kbps.  
**GOOD** Bandwidth over 2000 kbps.  
**_EXCELLENT_** Bandwidth over 2000 kbps.  
**_UNKNOWN_** connection quality cannot be found.
