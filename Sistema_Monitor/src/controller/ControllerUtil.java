/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 *
 * @author renato.soares
 */
public final class ControllerUtil {
        
        
    public static final boolean SoTemNumeros(String texto) { 
        try {                    
            if(texto.length()==0)return false;
            for (int i = 0; i < texto.length(); i++) { 
                if (!Character.isDigit(texto.charAt(i))) { 
                    return false; 
                } 
            } return true; 
        } catch (Exception e) {
            LogErro erro = new LogErro();
            erro.gravaErro(e);
            return false;
        }  
    }
    
    public static final boolean testaConexao(String address) { 
        try {
            address = address.replace(".", ";");
            String[] servidor = address.split(";");
            int ip[] = new int[4];
            for (int i=0;i<servidor.length;i++){
                ip[i]=Integer.valueOf(servidor[i]);
            }
            InetAddress add = Inet4Address.getByAddress(new byte[]{(byte)ip[0],(byte)ip[1],(byte)ip[2],(byte)ip[3]});
            return add.isReachable(1000);                      
        } catch (IOException e) {      
            LogErro erro = new LogErro();
            erro.gravaErro(e);
            e.printStackTrace();
            
        }
        return false;
    }
}
