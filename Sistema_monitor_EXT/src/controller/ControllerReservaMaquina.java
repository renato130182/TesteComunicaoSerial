/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ReservaMaquinaDAO;
import java.sql.Connection;
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
public class ControllerReservaMaquina {
    LogErro erro = new LogErro();
    
    public boolean  trocaCarrtelEntrada(Pesagem peSaida, Pesagem pesEntrada){        
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaMaquinaDAO dao = new ReservaMaquinaDAO(conec);
                if(dao.buscaIDReservaMaquinaPesagem(peSaida.getCodigo())){
                    if(dao.atualizaCarretelEntrada(pesEntrada)){                        
                        conec.commit();
                        db.desconectar();
                        return true;
                    }else{
                        conec.rollback();
                        db.desconectar();
                        return false;
                    }                    
                }                
                db.desconectar();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);            
        }
        return false;
    }
    
    public boolean registrarOperadorMaquina(Login login, String codMaquina){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaMaquinaDAO dao = new ReservaMaquinaDAO(conec);
                if(dao.atualizaOperadorTabelaReservaMaquina(codMaquina,login.getCodigoOperador())){                    
                    conec.commit();
                    db.desconectar();
                    return true;                                                            
                }                
                conec.rollback();
                db.desconectar();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public List<ReservaMaquina> buscaListaReservaMaquina(String codigoMaquina){
        List<ReservaMaquina> resMaq = new ArrayList<>();
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaMaquinaDAO dao = new ReservaMaquinaDAO(conec);
                resMaq = dao.buscaListaReservaMaquina(codigoMaquina);
                db.desconectar();
                return resMaq;                               
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
    
    public Item buscaItemMateriaPrima(String lote){
        Item it = new Item();
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaMaquinaDAO dao = new ReservaMaquinaDAO(conec);
                it = dao.BuscaItemMP(lote);
                db.desconectar();
                return it;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }

    public double buscaSaldoCansumoMP(String lote) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaMaquinaDAO dao = new ReservaMaquinaDAO(conec);               
                double saldo = dao.buscaSaldoMP(lote);
                db.desconectar();
                return saldo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }
    
    
}
