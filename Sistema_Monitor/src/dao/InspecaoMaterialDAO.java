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
public class InspecaoMaterialDAO {
    private final Connection conec;
    private String sql;
    LogErro erro = new LogErro();

    public InspecaoMaterialDAO(Connection conec) {
        this.conec = conec;
    }
    
    public int buscaTipoInspecaoItem(String codItem){
        try {
            sql = "SELECT tip_Inspecao FROM condumigproducao.item_inspecao WHERE cod_Item = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codItem);
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getInt("tip_Inspecao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 99;
    }

    public boolean adicionarControleAmostra(int codPesagem) {
        try {
            sql ="INSERT INTO controledeamostras (codigo,dataHoraPesagem) "
                    + "SELECT codigo ,CONCAT(datapesagem , ' ', horapesagem ) From pesagem WHERE codigo = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1, codPesagem);
            st.executeUpdate();
            return st.getUpdateCount()!=0; 
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean setarAguardandoInspecao(String lote, String codItem) {
        try {
            sql ="UPDATE programacaomaquina SET entregue = '1' WHERE loteproducao = ? AND codigoitem = ?";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            st.setString(2, codItem);
            st.executeUpdate();
            return st.getUpdateCount()!=0; 
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean loteAguardaCadastroAmostra(String lote, String codItem) {
        try {
            sql = "SELECT id FROM condumigproducao.controledeamostras where codigo in "
                    + "(select codigo from condumigproducao.pesagem where loteproduzido = ? and codigoitem = ?);";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            st.setString(2, codItem);
            ResultSet res = st.executeQuery();
            return !res.next();
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean loteAguardaCadastroAmostraTurno(String lote, String codItem,String codOperador) {
        try {
            sql = "SELECT id FROM condumigproducao.controledeamostras where codigo in "
                    + "(select codigo from condumigproducao.pesagem where loteproduzido = ? "
                    + "and codigoitem = ? and codigooperador1 = ?);";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            st.setString(2, codItem);
            st.setString(3, codOperador);
            ResultSet res = st.executeQuery();
            return !res.next();
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;        
    }
    
}
