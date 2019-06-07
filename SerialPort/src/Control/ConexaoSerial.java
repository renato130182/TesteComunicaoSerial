/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 *
 * @author renato.soares
 */
public class ConexaoSerial  implements SerialPortEventListener{
    private SerialPort serialPort = null;
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    public boolean  Conectar(String portName,int baudRate, int parity, 
            int bitsDados,int bitsparada,int controleFluxo  ){
        
        try {            
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
            
            while(portId == null && portEnum.hasMoreElements()){
                CommPortIdentifier currPortId = (CommPortIdentifier)portEnum.nextElement();
                if(currPortId.getName().equals(portName)){
                    serialPort = (SerialPort) currPortId.open(appName, 0);
                    portId = currPortId;
                    System.err.println("conectado a Porta:" + currPortId.getName());
                    break;
                }
            }
            if(portId == null || serialPort == null){
                System.err.println("Falha ao conectar com a porta: " + portName);
                return false;
            }
            
            serialPort.setSerialPortParams(baudRate,serialPort.getDataBits(), 
                    serialPort.getStopBits(),serialPort.getParity());
            
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
             return false;
        }
        return true;
    }
    
    public synchronized boolean  Desconectar(){
        try {           
            if(serialPort != null){
                serialPort.removeEventListener();
                serialPort.close();
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //Recepção de dados
    public void serialEvent(SerialPortEvent spe){
        try {
            switch(spe.getEventType()){
                case SerialPortEvent.DATA_AVAILABLE:
                    //if(input == null)){
                        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                    //}
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public static final int buscaDatabit(int bitDados){
        switch (bitDados){
            case 5:
                return SerialPort.DATABITS_5;
            case 6:
                return SerialPort.DATABITS_6;
            case 7: 
                return SerialPort.DATABITS_7;
            case 8:
                return SerialPort.DATABITS_8;
            default:
                return 0;
            }
        }
}
