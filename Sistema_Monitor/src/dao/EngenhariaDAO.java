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
import model.Engenharia;
import model.Produto;

/**
 *
 * @author renato.soares
 */
public class EngenhariaDAO {
    
     private final Connection conec;
     LogErro erro = new LogErro();
     private String sql;

    public EngenhariaDAO(Connection conec) {
        this.conec = conec;
    }
     
    public List<Engenharia> buscaEngenhariaEmProducaoPVCPigmento(String codMaquina, String codItem){
        List<Engenharia> eng = new ArrayList<>();
        String tmp;
         try {
            sql = "SELECT res.codigoitemres as item,it.descricao as des,loteitemres as lote,"
                    + "ext.`quant-usada` as qtd,extAl.`quant-usada` as qtdAl "
                    + "FROM condumigproducao.reservamaquina  res inner join condumigproducao.item it on "
                    + "res.codigoitemres = it.codigo left join qlikview.cad_estrutura ext "
                    + "on res.codigoitemres = ext.`es-codigo` and ext.`it-codigo` = ? "
                    + "left join qlikview.cad_estrutura_alternativo extAl on res.codigoitemres = extAl.`al-codigo` "
                    + "and extAl.`it-codigo` = ? where res.codigomaquina = ? and res.codigoitemres like '20%';";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codItem);
            st.setString(2, codItem);
            st.setString(3, codMaquina);
            ResultSet res = st.executeQuery();
            while(res.next()){
                Engenharia dadoEng = new Engenharia();
                dadoEng.setCodItem(res.getString("item"));
                dadoEng.setDescricao(res.getString("des"));
                dadoEng.setLote(res.getString("lote"));
                tmp = String.valueOf(res.getString("qtd"));
                if(tmp.equalsIgnoreCase("null")){
                    tmp = String.valueOf(res.getString("qtdAl"));
                    tmp = tmp.replace(",",".");
                    dadoEng.setQuantidade(Double.valueOf(tmp));                
                }else{
                    tmp = tmp.replace(",",".");
                    dadoEng.setQuantidade(Double.valueOf(tmp));                
                }
                dadoEng.setUnidade("Kilos");
                eng.add(dadoEng);
            }                
            return eng;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    } 
    
}
