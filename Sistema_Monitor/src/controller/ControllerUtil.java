/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import dao.ConexaoDatabase;
import dao.UtilDAO;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 *
 * @author renato.soares
 */
public final class ControllerUtil {
          
    public static final boolean SoTemNumeros(String texto) { 
        try {                    
            if(texto.length()==0)return false;
            texto = texto.replace(".","");
            texto = texto.replace(",","");
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
            return add.isReachable(250);                      
        } catch (IOException e) {      
           // LogErro erro = new LogErro();
            //erro.gravaErro(e);
            //e.printStackTrace();
            
        }
        return false;
    }
    
    public static final int calculaTempoPercorridoSegundos(String dataInicio, String dataFim) { 
        try {                    
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dataHoraInicial = sdf.parse(dataInicio);
            Date dataHoraFinal = sdf.parse(dataFim);
            long tempo = dataHoraFinal.getTime()-dataHoraInicial.getTime();
            return (int) (tempo/1000);            
        } catch (ParseException e) {
            LogErro erro = new LogErro();
            erro.gravaErro(e);
            return 0;
        }  
    }

    public static final String buscaDataHoraAtualBD() {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                UtilDAO dao = new UtilDAO(conec);
                String dado = dao.buscaDataHoraBD();
                db.desconectar();
                return dado;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogErro erro = new LogErro();  
            erro.gravaErro(e);
        }
        return "";
    }
    
    public static final String buscaMacAdrres()  {
        InetAddress ip;                    
            try {
                if(System.getProperty("os.name").equals("Linux")){
                    return "00:00:00:00:00:00";
                }
                ip = InetAddress.getLocalHost();
                System.out.println("IP " + ip);
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                byte[] mac = network.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                System.out.println("");
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                return sb.toString();
            } catch (UnknownHostException | SocketException ex) {
                Logger.getLogger(ControllerUtil.class.getName()).log(Level.SEVERE, null, ex);
            }                                 
        return "";
    }

    public static boolean bancoRespondendo() {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();
                boolean tmp = conec!=null;
                db.desconectar();
                return tmp;                
            }
        }catch(Exception e){
            e.printStackTrace();
        }    
        return false;
    }
}
