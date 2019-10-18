/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConfigSerialPort;

/**
 *
 * @author renato.soares
 */
public class ConfigSerialDAO {
    private String sql;
    public ConfigSerialPort buscaDadosConfigSerial(String configName){
        sql = "SELECT * FROM bd_sistema_monitor.tb_config_serial where configname = ?;";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, configName);
                ResultSet res = st.executeQuery();
                if(res.next()){
                    ConfigSerialPort cfg = new ConfigSerialPort();                    
                    cfg.setDataBits(res.getInt("databits"));
                    cfg.setFlowControl(res.getInt("flowcontrol"));
                    cfg.setParity(res.getInt("parity"));
                    cfg.setSerialPortName(res.getString("serialportname"));
                    cfg.setStopBits(res.getInt("stopbits"));
                    cfg.setTimeOut(res.getInt("timeout"));
                    cfg.setDataRate(res.getInt("datarate"));
                    return cfg;
                }else{
                    return null;
                }                    
            } catch (SQLException ex) {
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        db.desconectar();
        return null;
    }
    
    public boolean AtualizarConfigSerial(ConfigSerialPort cfg){
        sql = "update bd_sistema_monitor.tb_config_serial  set timeout=?, "
                + "databits=?, stopbits=?, parity=?, flowcontrol=?, serialportname=?, "
                + "datarate=? where configname = ?;";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setInt(1, cfg.getTimeOut());
                st.setInt(2, cfg.getDataBits());
                st.setInt(3,cfg.getStopBits());
                st.setInt(4,cfg.getParity());
                st.setInt(5,cfg.getFlowControl());
                st.setString(6,cfg.getSerialPortName());
                st.setInt(7,cfg.getDataRate());
                st.setString(8,cfg.getConfigName());               
                st.executeUpdate();
                db.desconectar();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        db.desconectar();
        return false;
    }
}
