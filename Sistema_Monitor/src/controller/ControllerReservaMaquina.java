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
import model.Pesagem;
import sun.security.pkcs11.Secmod;

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
                conec = db.getConnection();
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
}
