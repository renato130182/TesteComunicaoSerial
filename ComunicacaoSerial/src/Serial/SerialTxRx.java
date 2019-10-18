/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serial;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 *
 * @author renato.soares
 */
public class SerialTxRx implements SerialPortEventListener{

    SerialPort serialPort = null;
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    private String dadosSerial;
    private int TIME_OUT=1000;
    private int DATA_RATE=9600;
    private int DATA_BITS=8;
    private int STOP_BITS=1;
    private int PARITY=0;
    private int FLOW_CONTROL=0;

    private String serialPortName = "COM3";
    private String[] portas;
    private String[] tipoPortas;
    
    private List<ActionListener> listeners = new ArrayList<ActionListener>(0);
    
    public void addActionListener(ActionListener listener) {
		this.listeners.add(listener);
	}
    public void dadosRecebidos(){
		for(ActionListener listener : listeners) {
			listener.actionPerformed(new ActionEvent(this,1,dadosSerial));
		}
	}
    
    public boolean iniciaSerial(){
        try {
            //obtem portas seriais no sistema;
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
            
            while(portId == null && portEnum.hasMoreElements()){
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();                
                if(currPortId.getName().equals(serialPortName)){
                    serialPort = (SerialPort) currPortId.open(appName,TIME_OUT);
                    portId = currPortId;
                    System.out.println("Conectado na porta: " + currPortId.getName());
                    break;
                }
            }
            if (portId == null || serialPort == null){
                return false;
            }
            serialPort.setSerialPortParams(DATA_RATE,DATA_BITS, STOP_BITS,PARITY);
            serialPort.setFlowControlMode(FLOW_CONTROL);
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            
            return true;
        } catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public void SendData(String data){
        try {
            output = serialPort.getOutputStream();
            output.write(data.getBytes());            
        } catch (IOException e) {
        }        
    }
    public synchronized boolean close(){
        try {            
            if(serialPort != null){
                
                serialPort.close(); 
                serialPort.removeEventListener();
                
            }
            return true;
        } catch (Exception e) {
            return false;
        }        
    }
    
    @Override
    public void serialEvent(SerialPortEvent spe) {
        //metodo que recebe os dados da serial
        try {
            switch(spe.getEventType()){
                case SerialPortEvent.DATA_AVAILABLE:
                    if(input == null){
                        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                    }                    
                    dadosSerial = input.readLine();
                    System.out.println(dadosSerial); 
                    //Sinalizar evento na porta serial
                    if(!dadosSerial.isEmpty())dadosRecebidos();
                    input=null;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
        }
        
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public void setSerialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }
    
    public  String[] BuscarPortas(){
        Enumeration listaPortas;
        listaPortas=CommPortIdentifier.getPortIdentifiers();
        portas = new String[100];
        tipoPortas = new String[100];
        int i = 0;
        
        while (listaPortas.hasMoreElements()) {
            CommPortIdentifier numPorta = (CommPortIdentifier) listaPortas.nextElement();
            portas[i]=numPorta.getName();
            tipoPortas[i]=TipoPorta(numPorta.getPortType());
            i++;            
        }
        return portas;
    }
    
    public String TipoPorta(int tipoPorta){
        switch(tipoPorta){
            case CommPortIdentifier.PORT_SERIAL:
                return "Porta Serial";
            case CommPortIdentifier.PORT_I2C:
                return "Porta I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Porta Paralela";
            case CommPortIdentifier.PORT_RAW:
                return "Porta Raw";
            case CommPortIdentifier.PORT_RS485:
                return "Porta RS485";
            default:
                return "Porta Não identificada";
        }
    }     

    public int getTIME_OUT() {
        return TIME_OUT;
    }

    public void setTIME_OUT(int TIME_OUT) {
        this.TIME_OUT = TIME_OUT;
    }

    public int getDATA_RATE() {
        return DATA_RATE;
    }

    public void setDATA_RATE(int DATA_RATE) {
        this.DATA_RATE = DATA_RATE;
    }

    public int getDATA_BITS() {
        return DATA_BITS;
    }

    public void setDATA_BITS(int DATA_BITS) {
        this.DATA_BITS = DATA_BITS;
    }

    public int getSTOP_BITS() {
        return STOP_BITS;
    }

    public void setSTOP_BITS(int STOP_BITS) {
        this.STOP_BITS = STOP_BITS;
    }

    public int getPARITY() {
        return PARITY;
    }

    public void setPARITY(int PARITY) {
        this.PARITY = PARITY;
    }

    public int getFLOW_CONTROL() {
        return FLOW_CONTROL;
    }

    public void setFLOW_CONTROL(int FLOW_CONTROL) {
        this.FLOW_CONTROL = FLOW_CONTROL;
    }
}
