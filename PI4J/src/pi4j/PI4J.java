/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pi4j;

import com.pi4j.io.gpio.*;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

/*
 *
 * @author renato.soares
 */
public class PI4J {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws InterruptedException, PlatformAlreadyAssignedException{
        System.out.println("Minha Primeiro GPIO");
        PlatformManager.setPlatform(Platform.ORANGEPI);
        final Console console = new Console();
        console.title("Teste GPIO");
        console.promptForExit();
        public GpioController gpio = GpioFactory.getInstance();
        
        Pin pin = CommandArgumentParser.getPin(OrangePiPin.class, OrangePiPin.GPIO_01,args);
        
        GpioPinPwmOutput pwm = gpio.provisionSoftPwmOutputPin(pin);
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);
        pwm.setPwm(500);
        
        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(OrangePiPin.GPIO_15);
        while (true) {
            if(led.isHigh()){
                led.low();
                System.out.println("Apagar Led");
            }else{
                led.high();
                System.out.println("Acender Led");
            }
            Thread.sleep(1000);
        } // TODO code application logic here
    }
    
}
