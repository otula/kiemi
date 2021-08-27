# LockTaskApp

Android application (wrapper) for running an arbitrary HTML page in "kiosk mode", limiting user access only to the given page.

The application must be given admin permissions using adb shell (dpm set-device-owner com.otula.locktaskapp/.DeviceAdmin to allow admin rights).

Modify MainActivity.java to change URL of the "kiosk page". 

The default implementation also injects username and password to HTML5 SessionStorage. You can disable this from onPageStarted() method, or alternatively run other applicable code after page has finished loading.

The application will also automatically reload the web page during nighttime (when users are presumably not using the device). You can disable this functionality in the onResume() method.

If you need to allow unencrypted cleartext communication, this can be enabled in res/xml/network_security_config.xml.
