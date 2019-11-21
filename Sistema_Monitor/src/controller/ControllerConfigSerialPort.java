/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import Serial.SerialTxRx;
import dao.ConfigSerialDAO;
import model.ConfigSerialPort;

/**
 *
 * @author renato.soares
 */
public class ControllerConfigSerialPort {   
    
    
    public SerialTxRx configurarPortaSerial(String configName, String maqSerial){
        ConfigSerialPort cfg =  new ConfigSerialPort();                
        ConfigSerialDAO dao = new ConfigSerialDAO();
        SerialTxRx conn = new SerialTxRx();
        cfg=dao.buscaDadosConfigSerial(configName,maqSerial);
        if(!cfg.getSerialPortName().trim().equals("")){
            conn.setDATA_BITS(cfg.getDataBits());
            conn.setDATA_RATE(cfg.getDataRate());
            conn.setFLOW_CONTROL(cfg.getFlowControl());
            conn.setPARITY(cfg.getParity());
            conn.setSTOP_BITS(cfg.getStopBits());
            conn.setSerialPortName(cfg.getSerialPortName());
            conn.setTIME_OUT(cfg.getTimeOut());            
            return conn;
        }
        return null;
    }
}
