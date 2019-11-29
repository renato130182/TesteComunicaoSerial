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

/**
 *
 * @author renato.soares
 */
public class EventosSistemaDAO {
    private final Connection conec;
    private Integer idEventoSistema;
    private String sql;
    
    public EventosSistemaDAO(Connection conec) {
        this.conec = conec;
    }
    
    public boolean registraEventoSistema(Integer codEvento){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_log (cod_evento) values(?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,codEvento);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean buscaIdEventoSistema(){
        try {
            sql = "select last_insert_id() from bd_sistema_monitor.tb_eventos_sistema_log limit 1;";
            PreparedStatement st = conec.prepareStatement(sql);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                this.idEventoSistema = res.getInt("last_insert_id()");
                return true;
            }else{
                return false;
            }    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean registraUsuarioEventoSistema(String usuario){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_usuario "
                    + "(id_eventos_sistema_log, usuario) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setString(2,usuario);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean registraDiametroEventoSistema(double diametro){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_diametro "
                    + "(id_evento_sistema_log, diametro) values (?,?);";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setDouble(2,diametro);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registraMetragemEventoSistema(Integer metragem) {
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_metragem "
                    + "(id_evento_sistema_log, metragem) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setDouble(2,metragem);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
