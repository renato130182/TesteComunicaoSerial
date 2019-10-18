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
import model.ProdutoMaquina;

/**
 *
 * @author renato.soares
 */
public class ProdutoMaquinaDAO {
    private String sql;
    
    public ProdutoMaquina buscaVelocidadeProdutoMaquina(String produto, String maquina){
        ProdutoMaquina prodMaq = new ProdutoMaquina();
        ConexaoDatabase db = new ConexaoDatabase();
        try {
            sql = "SELECT VelocidadeMaquina, Unidade FROM condumigproducao.paramproducao "
                    + "where Item = ? and codigomaquina = ?;";
            Connection conec = db.getConnection();
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, produto);
            st.setString(2, maquina);
            ResultSet res = st.executeQuery();
            if(res.next()){
                prodMaq.setVelocidade(res.getInt("VelocidadeMaquina"));
                prodMaq.setUnidade(res.getString("Unidade"));
                db.desconectar();
                return prodMaq;
            }
        } catch (SQLException e) {
            System.err.println("Falha ao buscar item em producao" + e.getMessage());
        }
        db.desconectar();
        return null;
    }
}
