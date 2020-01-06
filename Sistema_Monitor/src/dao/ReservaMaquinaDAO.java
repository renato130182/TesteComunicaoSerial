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
import model.Pesagem;

/**
 *
 * @author renato.soares
 */
public class ReservaMaquinaDAO {
    private int idReservaMaquina;
    private final Connection conec;
    private LogErro erro = new LogErro();
    private String sql;
    public ReservaMaquinaDAO(Connection conec) {
        this.conec = conec;
    }
    
    public boolean buscaIDReservaMaquinaPesagem(String codigo) {
        try {
            sql = "select codigo from condumigproducao.reservamaquina where pesagem = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codigo);
            ResultSet res = st.executeQuery();
            if(res.next()){
                this.idReservaMaquina = res.getInt("codigo");
                return true;
            }else{
                return false;
            }    
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean atualizaCarretelEntrada(Pesagem pesEntrada) {
        try {
            sql = "update condumigproducao.reservamaquina set loteitemres = ?, "
                    + "qtosfios = ?,codigoembalagem = ?, codigoitemres = ?,pesagem = ? where codigo = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1,pesEntrada.getLote());
            st.setInt(2, pesEntrada.getQtosFios());
            st.setString(3,pesEntrada.getCodEmbalagem());
            st.setString(4,pesEntrada.getCodItem());
            st.setString(5,pesEntrada.getCodigo());
            st.setInt(6, this.idReservaMaquina);
            st.executeUpdate();
            return st.getUpdateCount()!=0;       
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
}
