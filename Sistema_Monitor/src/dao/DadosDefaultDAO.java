/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.CriptoCode;
import controller.LogErro;
import controller.ManipuladorArquivo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.DadosConexao;

/**
 *
 * @author renato.soares
 */
public class DadosDefaultDAO {
    private String sql;
    LogErro erro = new LogErro();
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
            erro.gravaErro(e);
            return null;
        }
    }

    public String buscaCodigoMaquina(String serial){
        String codMaquina=null;
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            try {
                sql = "SELECT codigo_maquina_monitor FROM bd_sistema_monitor.tb_maquina_monitor where serial_maquina_monitor = ? ";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1,serial);                
                ResultSet res = st.executeQuery();
                if(res.next()){
                    codMaquina = res.getString("codigo_maquina_monitor");                 
                }
                db.desconectar();
                return codMaquina;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
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
                db.desconectar();
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro ao registrar dados",JOptionPane.ERROR_MESSAGE);
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return false;
    }
    
    public boolean atualizarMaquinaMonitor(String serial,String maquina){
    ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            try {
                sql = "update bd_sistema_monitor.tb_maquina_monitor set "
                        + "codigo_maquina_monitor = ? where serial_maquina_monitor = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1,maquina);
                st.setString(2,serial);
                st.executeUpdate();   
                db.desconectar();
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro ao atualizar dados",JOptionPane.ERROR_MESSAGE);
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return false;
    }
}
