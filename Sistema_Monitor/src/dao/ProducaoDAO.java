/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Producao;

/**
 *
 * @author renato.soares
 */
public class ProducaoDAO {
    private String sql;
    public Producao buscaItemProducao(String codMaquina){
        Producao prod = new Producao();
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                sql ="SELECT codigoitemprod,loteproducao FROM condumigproducao.reservamaquina where codigomaquina = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                if(res.next()){
                    prod.setItemProducao(res.getString("codigoitemprod"));
                    prod.setLoteProducao(res.getString("loteproducao"));
                    return prod;
                }else{
                    System.out.println("Não ha item em produção.");
                }
            }
        } catch (Exception e) {
            System.err.println("Falha ao buscar item em producao" + e.getMessage());
        }
        return null;
    }
}
