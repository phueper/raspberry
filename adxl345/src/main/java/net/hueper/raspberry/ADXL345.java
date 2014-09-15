package net.hueper.raspberry;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Created by phueper on 9/11/14.
 * <p/>
 * adapted from the sunfounder C code, class to read data from the ADXL345 Accelerometer via i2c
 * see: http://www.analog.com/static/imported-files/data_sheets/ADXL345.pdf
 */
public class ADXL345
{

    /**
     * The i2c bus on which the ADXL345 is connected 
     */
    private static final int BUS_NUMBER = 1;
    /**
     * the i2c device address (per the datasheet address is 0x53 if ALT_ADDRESS is pulled high and 0x1d if ALT_ADDRESS is pulled low
     */
    private static final int DEVICE_ADDRESS = 0x53;
    
    private final I2CBus i2cBus;
    private final I2CDevice i2cDevice;

    public void cleanUp() throws IOException
    {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        i2cBus.close();
    }

    public class AccelerationData {
        short x = 0;
        short y = 0;
        short z = 0;
        
        public AccelerationData(short x, short y, short z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public short getX()
        {
            return x;
        }

        public short getY()
        {
            return y;
        }

        public short getZ()
        {
            return z;
        }
    }


    public ADXL345() throws IOException
    {
        // acquire I2CBus and I2CDevice
        i2cBus = I2CFactory.getInstance(BUS_NUMBER);
        i2cDevice = i2cBus.getDevice(DEVICE_ADDRESS);
        
        initADXL345();
    }

    /**
     * initialize the accelerometer 
     */
    private void initADXL345() throws IOException
    {
        // register 0x31 value 0x0b: DATA_FORMAT register: full resolution, right justified, +-16g Range
        i2cDevice.write(0x31, (byte) 0x0b);
        // register 0x2d value 0x08: POWER_CTL register: disable AUTO_SLEEP, enable Measure
        i2cDevice.write(0x2d, (byte) 0x08);
        // register 0x2e value 0x00: INT_ENABLE register: disable interrupts
        i2cDevice.write(0x2e, (byte) 0x00);
        
        // register 0x1e value 0x00: OFSX register: no X Offset
        i2cDevice.write(0x1e, (byte) 0x00);
        // register 0x1f value 0x00: OFSY register: no Y Offset
        i2cDevice.write(0x1f, (byte) 0x00);
        // register 0x20 value 0x00: OFSZ register: no Z Offset
        i2cDevice.write(0x20, (byte) 0x00);

        // register 0x21 value 0x00: DUR register: Disable single/double tap functions
        i2cDevice.write(0x21, (byte) 0x00);
        // register 0x22 value 0x00: Latent register: Disable double tap functions
        i2cDevice.write(0x22, (byte) 0x00);
        // register 0x23 value 0x00: Window register: Disable double tap functions
        i2cDevice.write(0x23, (byte) 0x00);

        // register 0x24 value 0x01: THRESH_ACT register: activity event threshold, not really needed, since no activity interrupt
        i2cDevice.write(0x24, (byte) 0x01);
        // register 0x25 value 0x0f: THRESH_INACT register: inactivity event threshold, not really needed, since no activity interrupt
        i2cDevice.write(0x25, (byte) 0x0f);
        // register 0x26 value 0x2b: TIME_INACT register: inactivity time, not needed, since no activity interrupt and no auto sleep
        i2cDevice.write(0x26, (byte) 0x2b);
        // register 0x27 value 0x00: ACT_INACT_CTL register: disable activity detection
        i2cDevice.write(0x27, (byte) 0x00);

        // register 0x28 value 0x09: THRESH_FF register: free fall detection threshold
        i2cDevice.write(0x28, (byte) 0x09);
        // register 0x29 value 0xff: TIME_FF register: free fall detection time
        i2cDevice.write(0x29, (byte) 0xff);
        
        // register 0x2a value 0x00: TAP_AXES register: disable tap detection
        i2cDevice.write(0x2a, (byte) 0x00);
        // register 0x2c value 0x0a: BW_RATE register: default value ~100Hz output rate
        i2cDevice.write(0x2c, (byte) 0x0a);
        // register 0x2f value 0x00: INT_MAP register: all interrupts to INT1 pin (they are disabled anyway)
        i2cDevice.write(0x2f, (byte) 0x00);
        // register 0x38 value 0x9f: FIFO_CTL register: FIFO Stream mode, max. FIFO entries before watermark interrupt
        i2cDevice.write(0x38, (byte) 0x9f);
    }
    
    public AccelerationData readAccelerationData() throws IOException
    {
        AccelerationData rval = null;
        // datasheet says we should try to read all 6 registers at once if possible, so the data in all registers matches one point in time
        // i have no idea if the implementation actually performs a multi-byte read... but we use a byte[6] anyway :)
        byte[] data = new byte[6];
        // read from register 0x32..0x37
        int bytesRead = i2cDevice.read(0x32, data, 0, data.length);
        // debug output
        Formatter formatter = new Formatter(Locale.getDefault());
        formatter.format("raw data: %#x, %#x, %#x, %#x, %#x, %#x", data[0], data[1], data[2], data[3], data[4], data[5]);
        System.out.println(formatter.toString());
        if (bytesRead == data.length) {
            short x = (short) (data[1] << 8 | data[0]);
            short y = (short) (data[3] << 8 | data[2]);
            short z = (short) (data[5] << 8 | data[4]);
            rval = new AccelerationData(x, y, z);
        }
        return rval;
    }

    public static void main(String[] args) throws InterruptedException, IOException
    {
        ADXL345 adxl345 = new ADXL345();
        
        while (true) {
            AccelerationData data = adxl345.readAccelerationData();
            Formatter formatter = new Formatter(Locale.getDefault());
            formatter.format("x: %#x, y: %#x, z: %#x", data.getX(), data.getY(), data.getZ());
            System.out.println(formatter.toString());
            System.out.println("x: " + data.getX() + ", y: " + data.getY() + ", z: " + data.getZ());
            Thread.sleep(500);
        }

    }

}
