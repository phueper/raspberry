package net.hueper.raspberry;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by phueper on 9/11/14.
 * 
 * adapted from the sunfounder C code
 */
public class PwmLed
{
    public static void main(String[] args) throws InterruptedException
    {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinPwmOutput pwmOutput = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);

        while(true){
            for (int i = 0; i < 1024; i++)
            {
                pwmOutput.setPwm(i);
                Thread.sleep(2);
            }
            Thread.sleep(1000);
            for (int i = 1023; i >= 0 ; i--)
            {
                pwmOutput.setPwm(i);
                Thread.sleep(2);
            }
        }


    }
}
