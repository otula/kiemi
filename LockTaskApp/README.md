# LockTaskApp

Android application (wrapper) for running an arbitrary HTML page in "kiosk mode", limiting user access only to the given page.

The application must be given admin permissions using adb shell (dpm set-device-owner com.otula.locktaskapp/.DeviceAdmin to allow admin rights).

Modify MainActivity.java to change URL of the "kiosk page". 

The default implementation also injects username and password to HTML5 SessionStorage. You can disable this from onPageStarted() method, or alternatively run other applicable code after page has finished loading.

The application will also automatically reload the web page during nighttime (when users are presumably not using the device). You can disable this functionality in the onResume() method.

If you need to allow unencrypted cleartext communication, this can be enabled in res/xml/network_security_config.xml.

WARNING: DEPENDING ON YOUR DEVICE, IT MIGHT BE _EXTREMELY_ DIFFICULT TO EXIT THE APPLICATION, AND YOU MIGHT BE REQUIRED TO RE-DEPLOY THE APPLICATION WITHOUT android.intent.category.HOME and android.intent.category.DEFAULT INTENT-FILTERS

NOTE: on some devices, uninstalling the application without first removing the admin permissions might be impossible. The admin can be removed from adb shell by executing the command dpm remove-active-admin com.otula.locktaskapp/.DeviceAdmin. After this you should be able to uninstall the application normally or by using adb shell command (adb uninstall com.otula.locktaskapp or pm uninstall --user 0 com.otula.locktaskapp)
