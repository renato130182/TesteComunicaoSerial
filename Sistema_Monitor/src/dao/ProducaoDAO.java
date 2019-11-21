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
                sql ="SELECT res.codigoitemprod,res.loteproducao,prd.met_produzida,prd.carretel_saida "
                        + "FROM condumigproducao.reservamaquina res inner join bd_sistema_monitor.tb_maquina_producao "
                        + "prd on prd.cod_maq = res.codigomaquina where res.codigomaquina = ? group by res.codigomaquina;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                if(res.next()){
                    prod.setItemProducao(res.getString("codigoitemprod"));
                    prod.setLoteProducao(res.getString("loteproducao"));
                    prod.setCarretelSaida(res.getString("carretel_saida"));
                    prod.setMetragemProduzida(res.getLong("met_produzida"));
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
    
    public boolean atualizaMetragemProduzida (String maquina, String metragem){
        sql = "update bd_sistema_monitor.tb_maquina_producao set met_produzida "
                + "= met_produzida + ? where cod_maq = ?;";        
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, metragem);
                st.setString(2, maquina);
                st.executeUpdate();
                if(st.getUpdateCount()!=0){
                    db.desconectar();
                    return true;
                }else{
                    db.desconectar();
                    return false;
                }                                
            } catch (SQLException ex) {
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        db.desconectar();
        
        return false;
    }
}
