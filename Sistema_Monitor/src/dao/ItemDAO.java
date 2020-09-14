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
import model.Item;

/**
 *
 * @author renato.soares
 */
public class ItemDAO {
    private String sql;
    LogErro erro = new LogErro();
    private final Connection conec;

    public ItemDAO(Connection conec) {
        this.conec = conec;
    }
    
      
     public Item buscaDescricaoItem(Long codigo) {
        try {
            sql = "select descricao from condumigproducao.item where codigo = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setLong(1, codigo);
            ResultSet res = st.executeQuery();
            if(res.next()){
                Item it = new Item(codigo);
                it.setCodigo(codigo);
                it.setDescricao(res.getString("descricao"));
                return it;
            }    
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
}
