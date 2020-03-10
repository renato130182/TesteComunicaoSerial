/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.LogErro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author renato.soares
 */
public class UtilDAO {
    private String sql;
    LogErro erro = new LogErro();
    private final Connection conec;

    public UtilDAO(Connection conec) {
        this.conec = conec;
    }
    
    public String buscaDataHoraBD(){
        try {                       
            sql ="select current_timestamp();";                
            PreparedStatement st = this.conec.prepareStatement(sql);            
            ResultSet res = st.executeQuery();
            if(res.next()){
             return res.getString("current_timestamp()");                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }
}
