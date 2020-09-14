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
import model.Item;
import model.Pesagem;
import model.ReservaMaquina;

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
    
    public List<ReservaMaquina> buscaListaReservaMaquina(String codigoMaquina) {
        List<ReservaMaquina> resMaq = new ArrayList<>();        
        try {
            sql = "select * from condumigproducao.reservamaquina where codigomaquina = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codigoMaquina);
            ResultSet res = st.executeQuery();
            while(res.next()){
                ReservaMaquina r = new ReservaMaquina();
                r.setcodigoReserva(res.getLong("codigo"));
                r.setSeuqencia(res.getInt("sequencia"));
                r.setCodigoMaquina(res.getString("codigomaquina"));
                r.setCodigoOperador(res.getString("codigooperador1"));
                r.setCodItemProd(res.getString("codigoitemprod"));
                r.setLoteItemRes(res.getString("loteitemres"));
                r.setQtosFios(res.getInt("qtosfios"));
                r.setCodigoembalagem(res.getString("codigoembalagem"));
                r.setCodItemRes(res.getString("codigoitemres"));
                r.setQuantItemRes(res.getDouble("quantitemres"));
                r.setLoteProducao(res.getString("loteproducao"));
                r.setTipoExtrusao(res.getString("tipoextrusao"));
                r.setPesagem(res.getInt("pesagem"));
                
                resMaq.add(r);
                
            }    
            return resMaq;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
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
    
    public boolean atualizaOperadorTabelaReservaMaquina(String codMaquina, String CodOperador){
        try {
            sql = "update condumigproducao.reservamaquina set codigooperador1 = ? "
                    + "where codigomaquina = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1,CodOperador);
            st.setString(2, codMaquina);             
            st.executeUpdate();
            return st.getUpdateCount()!=0;       
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return  false;
    }

    public Item BuscaItemMP(String lote) {
        
        try {
            sql = "SELECT codigoitem FROM condumigproducao.lotemateriaprima lm "
                    + "inner join condumigproducao.materiaprima m on m.lotematprima = lm.loteinterno "
                    + "where concat(lm.loteinterno,lm.sequencial) = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            ResultSet res = st.executeQuery();
            if(res.next()){
                Item it = new Item();      
                it.setCodigo(res.getLong("codigoitem"));
                return it;
            }                
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;    
    }

    public double buscaSaldoMP(String lote) {
         try {
            sql = "SELECT saldo FROM condumigproducao.lotemateriaprima lm "
                    + "inner join condumigproducao.materiaprima m on m.lotematprima = lm.loteinterno "
                    + "where concat(lm.loteinterno,lm.sequencial) = ?;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, lote);
            ResultSet res = st.executeQuery();
            if(res.next()){                
                String saldo = res.getString("saldo");
                saldo = saldo.replace(",",".");
                return Double.valueOf(saldo);                        
            }                
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public boolean AtualizaReservaMaquina(ReservaMaquina r) {
        try {
            sql = "update condumigproducao.reservamaquina  set codigoitemprod = ?, "
                    + "loteitemres = ?,qtosfios = ?, codigoembalagem = ?,codigoitemres = ?, "
                    + "quantitemres = ?,loteproducao = ?,tipoextrusao = ? , pesagem = ? "
                    + "where codigo = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1,r.getCodItemProd());
            st.setString(2,r.getLoteItemRes());
            st.setInt(3, r.getQtosFios());
            st.setString(4, r.getCodigoembalagem());
            st.setString(5, r.getCodItemRes());
            st.setDouble(6, r.getQuantItemRes());
            st.setString(7, r.getLoteProducao());
            st.setString(8, r.getTipoExtrusao());
            st.setInt(9, r.getPesagem());
            st.setLong(10, r.getCodigoReserva());
            
            st.executeUpdate();
            return st.getUpdateCount()==1;       
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
}
