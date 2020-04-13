/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author renato.soares
 */
public final class ControllerUtil {
          
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
            System.err.println(e);            
        }
        return false;
    }
    
    public static List<String> buscaDadosBD(String query){
        List<String> dados= new ArrayList<>();
        try {                    
            ControllerConexaoDatabase ctr =  new ControllerConexaoDatabase();
            if(ctr.isInfoDB()){
                Connection conec = ctr.getConnection();                
                if(conec!=null){                    
                    PreparedStatement st = conec.prepareStatement(query);                            
                    ResultSet res = st.executeQuery();    
                    String tmp;
                    while(res.next()){                       
                        int coluns = res.getMetaData().getColumnCount();
                        tmp="";
                        for (int i=1;i<=coluns;i++){      
                            //System.out.println("tipo de dado: " + res.getMetaData().getColumnTypeName(i));  
                       
                            if(i==coluns){
                                tmp = tmp + String.valueOf(res.getObject(i));
                            }else{


                                tmp = tmp + String.valueOf(res.getObject(i)) + ";";
                            }
                            
                        }
                        dados.add(tmp);
                    } 
                    conec.close();
                }
            }            

        } catch (SQLException e) {
            System.out.println(e);
            dados.clear();
        }
        return dados;
    }
    
}
