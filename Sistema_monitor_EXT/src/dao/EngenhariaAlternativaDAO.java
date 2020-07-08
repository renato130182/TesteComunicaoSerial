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
import java.util.ArrayList;
import java.util.List;
import model.Produto;

/**
 *
 * @author renato.soares
 */
public class EngenhariaAlternativaDAO {
     private final Connection conec;
     LogErro erro = new LogErro();
     private String sql;
    public EngenhariaAlternativaDAO(Connection conec) {
        this.conec = conec;
    }

    public List<Produto> BuscaItenDescricaoEngAlternativa(String codItem, String codItemProducao) {
        List<Produto> prods = new ArrayList<>();
        try {
            sql = "SELECT al.`al-codigo` as codItem,it.descricao FROM qlikview.cad_estrutura_alternativo al " +
                "inner join condumigproducao.item it on al.`al-codigo` = it.codigo " +
                "where al.`es-codigo` = ? and al.`it-codigo` = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codItem);    
            st.setString(2, codItemProducao);
            ResultSet res = st.executeQuery();
            while(res.next()){
                Produto prod = new Produto();
                prod.item.setCodigo(res.getLong("codItem"));
                prod.item.setDescricao(res.getString("descricao"));
                prods.add(prod);
            }    
            
            return prods;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }          
}
