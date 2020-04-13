/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.LogErro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Micrometro;

/**
 *
 * @author renato.soares
 */
public class MicrometroDAO {
    private final Connection conec;
     LogErro erro = new LogErro();
     private String sql;

    public MicrometroDAO(Connection conec) {
        this.conec = conec;
    }
    
    public boolean registraDadosMicrometro(Micrometro dados,String codMaquina,String lote, int metragem,int velocidade){
        try {
            sql = "insert into bd_sistema_monitor.tb_maquina_dados_micrometro "
                    + "(cod_maquina, lote, diametro_minimo, diametro_medio, diametro_maximo, devio_medio, metragem,velocidade_media)"
                    + " values (?,?,?,?,?,?,?,?);";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codMaquina);
            st.setString(2,lote);
            st.setFloat(3, dados.getDiametroMinimo());
            st.setFloat(4, dados.getDiametroMedio());
            st.setFloat(5, dados.getDiametroMaximo());
            st.setFloat(6, dados.getDesvio());
            st.setInt(7, metragem);
            st.setInt(8, velocidade);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean registrarLote(String loteproducao) {
        try {
            sql = "update bd_sistema_monitor.tb_maquina_dados_micrometro set lote = ? where lote = '0';";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, loteproducao);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
}
