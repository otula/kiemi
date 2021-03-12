# NB-IoT-Temperature-Prototype
NB-IoT-Temperature-Prototype based on Olimex dev kit for Quectel bc66

- NB-IoT-based temperature sensor prototype running on Quectel bc66/OpenCPU.
- Can be used to send CIMI, temperatures measurements, voltage and signal strength to a remote server using HTTP
- Uses [DS18x20-uart-opencpu](https://github.com/otula/kiemi/tree/master/DS18x20-uart-opencpu) 1-wire protocol implementation for sensor communications

Before use, remember to modify the appropriate #defines and constants to match your environment. Namely APN_OPERATOR, SERVER_ADDRESS, SERVER_PORT and REQUEST_TEMPLATE.

The code should work "as is", though certain limitations do remain. You can check the TODOs in the source code for improvement ideas and/or unimplemented features.

For some reason, reading temperatures of UART/1-wire occasionally prevents the device from entering deepsleep state. This is fixed in the code with periodically reseting the device, but a better/more dynamic approach should be implemented for production use. 

Requires Wiz-IO's Quectel Framework for Platform IO, available [here](https://github.com/Wiz-IO/framework-quectel). Tested with Visual Studio Code (1.54.1), PlatformIO Core 5.1.0 / Home 3.3.3 and Quectel Framwork 2.1.03 (2.1.1+sha.cde2255).

