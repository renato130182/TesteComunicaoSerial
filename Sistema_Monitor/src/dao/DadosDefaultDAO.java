/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.CriptoCode;
import controller.ManipuladorArquivo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.DadosConexao;
import model.Usuario;

/**
 *
 * @author renato.soares
 */
public class DadosDefaultDAO {
    private String sql;
    public static String getARQDBPROD() {
        return ARQDBPROD;
    }

    public static String getARQDBTESTE() {
        return ARQDBTESTE;
    }
    private String dados = "";
    private byte[] msgCrito;
    private final static String USERDEFAULT = System.getProperty ("user.home") + System.getProperty ("file.separator") +  "UserDefault.cnf";
    private final static String ARQDBPROD =  System.getProperty ("user.home") + System.getProperty ("file.separator") + "DataBase.cnf";
    private final static String ARQDBTESTE =  System.getProperty ("user.home") + System.getProperty ("file.separator") + "DataBaseTeste.cnf";

    public static String getUSERDEFAULT() {
        return USERDEFAULT;
    }
    
    public boolean ArmazenarUserDefault(Usuario us){
        ManipuladorArquivo man = new ManipuladorArquivo();
        try {                  
            String msg =
            us.getUsuario()+ ";"+ us.getSenha();                                         
            preparaDados(msg);
            man.setArquivo(USERDEFAULT);
            man.setDados(dados);
            man.escreverArquivo();
            return true;
        } catch (Exception e) {            
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public boolean armazenaDadosConexao(DadosConexao d, boolean amb){
        ManipuladorArquivo man = new ManipuladorArquivo();
        try {
            String msg
            = d.getDriverName()+";"+d.getMyDatabase()+";"+d.getPassword()+";"
                    +d.getServerName()+";"+d.getUrl()+";"+d.getUserName();
            preparaDados(msg);
            if(amb){
                
                man.setArquivo(ARQDBPROD);
            }else{
                man.setArquivo(ARQDBTESTE);
            }           
            man.setDados(dados);
            man.escreverArquivo();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }        
    }
    
    public DadosConexao buscaDadosConexaoDefault (boolean amb){
        ManipuladorArquivo man = new ManipuladorArquivo();
        DadosConexao d = new DadosConexao();
        try {
            if(amb){
                man.setArquivo(ARQDBPROD);
            }else{
                man.setArquivo(ARQDBTESTE);
            }
            man.BuscarArquivo();            
            String arqCripto = man.getDados();
            System.out.println("dados Cripto: "  + arqCripto);
            String tmp = CriptoCode.decrypt(CriptoCode.converterStringByte(arqCripto, " "));
            System.out.println("dados descrip" + tmp);
            String[] dadosConexao = tmp.split(";");
            if(dadosConexao.length==6){
                d.setDriverName(dadosConexao[0]);                
                d.setMyDatabase(dadosConexao[1]);
                d.setPassword(dadosConexao[2]);
                d.setServerName(dadosConexao[3]);
                d.setUrl(dadosConexao[4]);
                d.setUserName(dadosConexao[5]);
            }
            return d;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private void preparaDados(String msg){
        dados = "";
        msgCrito = CriptoCode.encrypt(msg);
        for ( int i=0; i<msgCrito.length;i++){
            //System.out.print(new Integer(msgCrito[i])+" ");                    
            dados = dados + Byte.toString(msgCrito[i])+" ";
        }
    }
    public String buscaCodigoMaquina(String serial){
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            try {
                sql = "SELECT codigo_maquina_monitor FROM bd_sistema_monitor.tb_maquina_monitor where serial_maquina_monitor = ? ";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1,serial);                
                ResultSet res = st.executeQuery();
                if(res.next()){
                    return res.getString("codigo_maquina_monitor");
                }
            } catch (SQLException ex) {
                Logger.getLogger(DadosDefaultDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
        }
        db.desconectar();
        return null;
    }
    
    public boolean cadastrarMaquinaMonitor (String serial,String maquina){
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            try {
                sql = "insert into bd_sistema_monitor.tb_maquina_monitor "
                        + "(serial_maquina_monitor, codigo_maquina_monitor) "
                        + "values (?,?);";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1,serial);
                st.setString(2,maquina);
                st.execute();         
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro ao registrar dados",JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(DadosDefaultDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}
