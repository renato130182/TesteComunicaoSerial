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

    public List<Produto> BuscaListaItenDescricaoEngAlternativa(String codItem, String codItemProducao) {
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

    public String BuscaItemCobrePadrao(String codItemProducao) {
        try {
            sql = "SELECT `es-codigo` as itemPadrao FROM qlikview.cad_estrutura"
                    + " where`it-codigo` = ? and `es-codigo` like '40%';";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codItemProducao);                
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getString("itemPadrao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }

    public String BuscaItemPVCExtrusadoPadrao(long codigo) {
        try {
            sql = "SELECT `es-codigo` as itemPadrao FROM qlikview.cad_estrutura "
                    + "where`it-codigo` = ? and `es-codigo` like '2002%' "
                    + "and `qtd-compon` =  (select max(`qtd-compon`) "
                    + "FROM qlikview.cad_estrutura where`it-codigo` = ? and "
                    + "`es-codigo` like '2002%');";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setLong(1, codigo);    
            st.setLong(2, codigo);                
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getString("itemPadrao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }

    public String BuscaItemPVC_CoExtrusadoPadrao(long codigo) {
        try {
            sql = "SELECT `es-codigo` as itemPadrao FROM qlikview.cad_estrutura "
                    + "where`it-codigo` = ? and `es-codigo` like '2002%'"
                    + "and `qtd-compon` =  (select min(`qtd-compon`) "
                    + "FROM qlikview.cad_estrutura where`it-codigo` = ? "
                    + "and `es-codigo` like '2002%');";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setLong(1, codigo);    
            st.setLong(2, codigo);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getString("itemPadrao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }

    public String BuscaItemPigmentoPadrao(long codigo) {
        try {
            sql = "SELECT `es-codigo` as itemPadrao FROM qlikview.cad_estrutura "
                    + "where`it-codigo` = ? and `es-codigo` like '2003%';";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setLong(1, codigo);             
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getString("itemPadrao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }
}
