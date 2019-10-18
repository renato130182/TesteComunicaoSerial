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
import model.Producao;

/**
 *
 * @author renato.soares
 */
public class ProducaoDAO {
    private String sql;
    
    public Producao buscaItemProducao(String codMaquina){
        ConexaoDatabase db = new ConexaoDatabase();
        Producao prod = new Producao();
        try {            
            if(db.isInfoDB()){
                sql ="SELECT codigoitemprod,loteproducao FROM condumigproducao.reservamaquina where codigomaquina = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                if(res.next()){
                    prod.setItemProducao(res.getString("codigoitemprod"));
                    prod.setLoteProducao(res.getString("loteproducao"));
                    db.desconectar();
                    return prod;                    
                }else{
                    System.out.println("Não ha item em produção.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Falha ao buscar item em producao" + e.getMessage());
        }
        db.desconectar();
        return null;
    }
    
    public Long BuscaMetragemProduzida (String lote, String item){
        Long metragem;
        ConexaoDatabase db = new ConexaoDatabase();
        try {
            sql = "SELECT sum(metragemoperador) as met FROM condumigproducao.pesagem "
                    + "where loteproduzido = ? and codigoitem = ?;";
            Connection conec = db.getConnection();
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            st.setString(2, item);
            ResultSet res = st.executeQuery();
            if(res.next()){
                metragem = res.getLong("met");
                db.desconectar();
                return metragem;
            }
        } catch (SQLException e) {
            System.err.println("Falha ao buscar item em producao" + e.getMessage());
        }
        db.desconectar();
        return null;
    }
}
