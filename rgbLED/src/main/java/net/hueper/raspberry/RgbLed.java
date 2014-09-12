package net.hueper.raspberry;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.SoftPwm;

/**
 * Created by phueper on 9/11/14.
 * 
 * adapted from the sunfounder C code
 */
public class RgbLed
{

    public static final int RED_PIN = 0;
    public static final int GREEN_PIN = 1;
    public static final int BLUE_PIN = 2;

    public static void main(String[] args) throws InterruptedException
    {
        // initialize wiringPi library, needed before using SoftPwm
        com.pi4j.wiringpi.Gpio.wiringPiSetup();

        SoftPwm.softPwmCreate(RED_PIN, 0, 100);
        SoftPwm.softPwmCreate(GREEN_PIN, 0, 100);
        SoftPwm.softPwmCreate(BLUE_PIN, 0, 100);

        setRGBColor(0xff,0x00,0x00);   //red	
        Thread.sleep(500);
        setRGBColor(0x00,0xff,0x00);   //green
        Thread.sleep(500);
        setRGBColor(0x00,0x00,0xff);   //blue
        Thread.sleep(500);

        setRGBColor(0xff,0xff,0x00);   //yellow
        Thread.sleep(500);
        setRGBColor(0xff,0x00,0xff);   //pick
        Thread.sleep(500);
        setRGBColor(0xc0,0xff,0x3e);
        Thread.sleep(500);

        setRGBColor(0x94,0x00,0xd3);
        Thread.sleep(500);
        setRGBColor(0x76,0xee,0x00);
        Thread.sleep(500);
        setRGBColor(0x00,0xc5,0xcd);
        Thread.sleep(500);

        GpioController gpio = GpioFactory.getInstance();
        //gpio.shutdown();
    }

    private static void setRGBColor(int red, int green, int blue)
    {
        SoftPwm.softPwmWrite(RED_PIN, red);
        SoftPwm.softPwmWrite(GREEN_PIN, green);
        SoftPwm.softPwmWrite(BLUE_PIN, blue);
    }
}
