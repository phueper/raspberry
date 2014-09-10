package net.hueper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by phueper on 9/10/14.
 * <p/>
 * copied and adapted from: http://www.sysstem.at/2013/09/7-segment-display-with-74hc595-shift-register-and-raspberry-pi/
 * <p/>
 * GPIOs were changed!!! see constants
 */
public class Test74HC595
{

    /*
        #inter character sleep
            icsleep=0.6
    
        #defining all the single LEDs
            led1=0x80 #10000000
        led2=0x40 #01000000
        led3=0x20 #00100000
        led4=0x10 #00010000
        led5=0x08 #00001000
        led6=0x04 #00000100
        led7=0x02 #00000010
        led8=0x01 #00000001
    
            #definition of all writeable letters and numbers
            letter={"0":0xFC,
            "1":0x30,
            "2":0xDA,
            "3":0x7A,
            "4":0x36,
            "5":0x6E,
            "6":0xEE,
            "7":0x38,
            "8":0xFE,
            "9":0x3E,
            "a":0xBE,
            "b":0xE6,
            "c":0xCC,
            "d":0xF2,
            "e":0xCE,
            "f":0x8E,
            "g":0x7E,
            "h":0xB6,
            "i":0x30,
            "j":0xF0,
            "l":0xC4,
            "n":0xBC,
            "o":0xFC,
            "p":0x9E,
            "s":0x6E,
            "t":0x38,
            "u":0xF4,
            "x":0xB4,
            "y":0x76,
            "z":0xDE
    }
    
    #loading function sequence
            load1=0x06 #00000110
            load2=0x22 #00100010
            load3=0x60 #01100000
            load4=0xC0 #11000000
            load5=0x82 #10000010
            load6=0x12 #00010010
            load7=0x18 #00011000
            load8=0x0C #00001100
    
            #up-down loading function sequence
            ud1=led8
            ud2=led2
            ud3=led1+led3
            ud4=led7
            ud5=led6+led4
            ud6=led5
    
            #left-right loading function sequence
            lr1=led1+led6
            lr2=led2+led5+led7
            lr3=led3+led4
            lr4=led8
    
            #rotational loading function sequence
            rot1=led2+led5
            rot2=led1+led6+led3+led4+led7
    
            #putting all segments of the sequences in a list
            letterrange=[]
            hexrange=[]
            for value in letter.values():
            letterrange.append(value)
            for value in letter.values():
            if value != "g":
            hexrange.append(value)
            loadrange=[load1,load2,load3,load4,load5,load6,load7,load8]
            udrange=[ud1,ud2,ud3,ud4,ud5,ud6]
            ledrange=[led1,led2,led3,led4,led5,led6,led7,led8]
            lrrange=[lr1,lr2,lr3,lr4]
            rotrange=[rot1,rot2]
            spinrange=[led1,led2,led3,led4,led5,led6]
    */
    /* define the GPIO PINS */
    private static final Pin DATA = RaspiPin.GPIO_01; // Connector GPIO18, WiringPi GPIO1, DATA = DS
    private static final Pin LATCH = RaspiPin.GPIO_04; // Connector GPIO23, WiringPi GPIO4, LATCH = STCP
    private static final Pin CLOCK = RaspiPin.GPIO_05; // Connector GPIO24, WiringPi GPIO5, CLOCK = SHCP
    private static final Pin CLEAR = RaspiPin.GPIO_06; // Connector GPIO25, WiringPi GPIO6, CLEAR = MR Low 
    private static final Pin ENABLE = RaspiPin.GPIO_10; // Connector GPIO8 (CE0), WiringPi GPIO10, ENABLE = OE Low

    /* sleep duration between updates */
    private static final int SLEEP_MS = 100;

    /* define LEDs, this changes with the wiring! 
    * 
    *  5  -- 
    *  6 |  | 4
    *  7  --
    *  1 |  | 3
    *  2  -- . 8
    *
    * */
    private static final int LED1 = 0x80; //10000000
    private static final int LED2 = 0x40; //01000000
    private static final int LED3 = 0x20; //00100000
    private static final int LED4 = 0x10; //00010000
    private static final int LED5 = 0x08; //00001000
    private static final int LED6 = 0x04; //00000100
    private static final int LED7 = 0x02; //00000010
    private static final int LED8 = 0x01; //00000001

    /* define all letters and number possible */
    private static final Map<Character, Integer> LETTERS = new LinkedHashMap<Character, Integer>(); // LinkedHashMap, since we want to keep the inserting order!

    static
    {
        LETTERS.put('0', LED1 | LED2 | LED3 | LED4 | LED5 | LED6);
        LETTERS.put('1', LED3 | LED4);
        LETTERS.put('2', LED1 | LED2 | LED4 | LED5 | LED7);
        LETTERS.put('3', LED2 | LED3 | LED4 | LED5 | LED7);
        LETTERS.put('4', LED3 | LED4 | LED6 | LED7);
        LETTERS.put('5', LED2 | LED3 | LED5 | LED6 | LED7);
        LETTERS.put('6', LED1 | LED2 | LED3 | LED5 | LED6 | LED7);
        LETTERS.put('7', LED3 | LED4 | LED5);
        LETTERS.put('8', LED1 | LED2 | LED3 | LED4 | LED5 | LED6 | LED7);
        LETTERS.put('9', LED2 | LED3 | LED4 | LED5 | LED6 | LED7);
        /* TODO: use LED values for letters */
/* i dont like the letters... lets not use them
        LETTERS.put('a', 0xBE);
        LETTERS.put('b', 0xE6);
        LETTERS.put('c', 0xCC);
        LETTERS.put('d', 0xF2);
        LETTERS.put('e', 0xCE);
        LETTERS.put('f', 0x8E);
        LETTERS.put('g', 0x7E);
        LETTERS.put('h', 0xB6);
        LETTERS.put('i', 0x30);
        LETTERS.put('j', 0xF0);
        LETTERS.put('l', 0xC4);
        LETTERS.put('n', 0xBC);
        LETTERS.put('o', 0xFC);
        LETTERS.put('p', 0x9E);
        LETTERS.put('s', 0x6E);
        LETTERS.put('t', 0x38);
        LETTERS.put('u', 0xF4);
        LETTERS.put('x', 0xB4);
        LETTERS.put('y', 0x76);
        LETTERS.put('z', 0xDE);
*/
    }

    /* define "snake around" loading sequence */
    private static final List<Integer> LOADING = new ArrayList<Integer>(); // LinkedHashMap, since we want to keep the inserting order!

    static
    {
        LOADING.add(LED2 | LED8);
        LOADING.add(LED1 | LED2);
        LOADING.add(LED1 | LED7);
        LOADING.add(LED4 | LED7);
        LOADING.add(LED4 | LED5);
        LOADING.add(LED5 | LED6);
        LOADING.add(LED6 | LED7);
        LOADING.add(LED3 | LED7);
        LOADING.add(LED3 | LED8);
    }


    /* define "up down" loading sequence */
    private static final List<Integer> UPDOWN = new ArrayList<Integer>(); // LinkedHashMap, since we want to keep the inserting order!

    static
    {
        UPDOWN.add(LED8);
        UPDOWN.add(LED2);
        UPDOWN.add(LED1 | LED3);
        UPDOWN.add(LED7);
        UPDOWN.add(LED4 | LED6);
        UPDOWN.add(LED5);
        UPDOWN.add(LED4 | LED6);
        UPDOWN.add(LED7);
        UPDOWN.add(LED1 | LED3);
        UPDOWN.add(LED2);
        UPDOWN.add(LED8);
    }

    /* define "chaser" loading sequence */
    private static final List<Integer> CHASER = new ArrayList<Integer>(); // LinkedHashMap, since we want to keep the inserting order!

    static
    {
        CHASER.add(LED1 | LED3);
        CHASER.add(LED2 | LED4);
        CHASER.add(LED3 | LED5);
        CHASER.add(LED4 | LED6);
        CHASER.add(LED1 | LED5);
        CHASER.add(LED2 | LED6);
    }

    private final GpioController gpio;

    private final GpioPinDigitalOutput dataOutput;
    private final GpioPinDigitalOutput latchOutput;
    private final GpioPinDigitalOutput clockOutput;
    private final GpioPinDigitalOutput clearOutput;
    private final GpioPinDigitalOutput enableOutput;

    public Test74HC595()
    {
        // create gpio controller
        gpio = GpioFactory.getInstance();

        dataOutput = gpio.provisionDigitalOutputPin(DATA, PinState.LOW); // Databit to be shifted into the register
        latchOutput = gpio.provisionDigitalOutputPin(LATCH, PinState.LOW); // Latch to output the current memory register 
        clockOutput = gpio.provisionDigitalOutputPin(CLOCK, PinState.LOW); // clock to shift the DATA value into memory register
        clearOutput = gpio.provisionDigitalOutputPin(CLEAR, PinState.LOW); // clear register if LOW, enable writing data if HIGH 
        enableOutput = gpio.provisionDigitalOutputPin(ENABLE, PinState.LOW); // output enabled if LOW 

        // set CLEAR to HIGH to enable writing
        clearOutput.high();
    }

    protected void shutdown()
    {
        // clear display
        clear();
        latch();
        // disable output
        enableOutput.high();
        // stop all GPIO activity/threads
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
    }

    private void clear()
    {
        clearOutput.low();
        clearOutput.high();
    }

    /**
     * shift in a new bit
     *
     * @param val if true, the shifted bit will be HIGH, else LOW
     */
    private void shift(boolean val)
    {
        PinState valState = val ? PinState.HIGH : PinState.LOW;
        System.out.println("Writing: " + valState);
        dataOutput.setState(valState);
        clockOutput.high();
        clockOutput.low();
        dataOutput.low();
    }

    /**
     * write the current memory register state to outputs and sleep
     *
     * @param ms time to sleep after writing in ms
     */
    private void latch(int ms)
    {
        latchOutput.high();
        latchOutput.low();
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Interrupted!");
        }
    }

    /**
     * write with default SLEEP_MS sleep
     */
    private void latch()
    {
        latch(SLEEP_MS);
    }

    /**
     * write a value to the register and latch it
     *
     * @param val the value to write
     */
    private void write(int val)
    {
        // write each bit of val to register
        for (int i = 0; i < 8; i++)
        {
            shift(((val >> i) & 1) == 1);
        }
        // and latch it (i.e. output it)
        latch();
    }

    private void dumpGpioState()
    {
        System.out.println("DATA:   " + dataOutput + dataOutput.getState());
        System.out.println("CLOCK:  " + clockOutput + clockOutput.getState());
        System.out.println("LATCH:  " + latchOutput + latchOutput.getState());
        System.out.println("CLEAR:  " + clearOutput + clearOutput.getState());
        System.out.println("ENABLE: " + enableOutput + enableOutput.getState());
    }
    
/*
    #
    Clean up
    GPIO,
    set display
    to no
    character

    def cleanup()

    :
            #
    Set all
    leds to

    off
    writenumber(0)

    #
    writeout stored
    in character

    writeout()

    #writeout"nothing"

    writeout()

    time.sleep(0.7)
            GPIO.cleanup()

            #
    shifts in

    a bit(but does not write it yet)

    def shift(input)

    :
            if input==1:
    input=True
    else:
    input=False

    GPIO.output(DATAIN,input)
        GPIO.output(CLOCK,GPIO.HIGH)
        GPIO.output(CLOCK,GPIO.LOW)
        GPIO.output(DATAIN,GPIO.LOW)

        #
    writes the
    stored data
    from register
    out to
    pins

    def writeout()

    :
            #
    Display LEDs
    GPIO.output(LATCH,GPIO.HIGH)
        #
    needed to
    read characters
    .
    otherwise the
    characters would
    be display
    to fast
    after each
    other
    time.sleep(icsleep)
        GPIO.output(LATCH,GPIO.LOW)

*/


    public static void main(String[] args)
    {

        Test74HC595 test74HC595;
        System.out.println("74HC595 Shift Register 7-segment display demo");

        test74HC595 = new Test74HC595();
 
        try
        {
        
            /* light each LED once */
            test74HC595.write(LED1);
            test74HC595.dumpGpioState();
            test74HC595.write(LED2);
            test74HC595.dumpGpioState();
            test74HC595.write(LED3);
            test74HC595.dumpGpioState();
            test74HC595.write(LED4);
            test74HC595.dumpGpioState();
            test74HC595.write(LED5);
            test74HC595.dumpGpioState();
            test74HC595.write(LED6);
            test74HC595.dumpGpioState();
            test74HC595.write(LED7);
            test74HC595.dumpGpioState();
            test74HC595.write(LED8);
            test74HC595.dumpGpioState();

            /* each LETTER once */
            for (Integer i : LETTERS.values())
            {
                test74HC595.write(i);
                test74HC595.dumpGpioState();
            }

            /* LOADING twice */
            for (Integer i : LOADING)
            {
                test74HC595.write(i);
                test74HC595.dumpGpioState();
            }
            for (Integer i : LOADING)
            {
                test74HC595.write(i);
                test74HC595.dumpGpioState();
            }

            /* UPDOWN twice */
            for (Integer i : UPDOWN)
            {
                test74HC595.write(i);
                test74HC595.dumpGpioState();
            }
            for (Integer i : UPDOWN)
            {
                test74HC595.write(i);
                test74HC595.dumpGpioState();
            }
            
            /* ... and CHASER until user breaks us ;) */
            //noinspection InfiniteLoopStatement
            while (true) {
                for (Integer i : CHASER)
                {
                    test74HC595.write(i);
                    test74HC595.dumpGpioState();
                }
            }
        }
        finally
        {
            test74HC595.shutdown();
        }

    }

}
