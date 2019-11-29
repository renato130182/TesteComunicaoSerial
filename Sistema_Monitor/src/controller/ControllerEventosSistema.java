/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.EventosSistemaDAO;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author renato.soares
 */
public class ControllerEventosSistema {
    
    public boolean registraEventos(Integer cod_Evento, String usuario,double diametro,Integer metragem ){
        boolean registrado = false;
        try {                    
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                if(dao.registraEventoSistema(cod_Evento)){
                    registrado = true;
                    if(dao.buscaIdEventoSistema()){
                        if(!usuario.trim().equals("")){
                            if(dao.registraUsuarioEventoSistema(usuario)){
                                registrado = true;
                            }else{
                                conec.rollback();
                                db.desconectar();
                                return false;
                            }                                
                        }
                        if(diametro > 0){
                            if(dao.registraDiametroEventoSistema(diametro)){
                                registrado = true;
                            }else{
                                conec.rollback();
                                db.desconectar();
                                return false;
                            }
                        }
                        if(metragem > 0){
                            if(dao.registraMetragemEventoSistema(metragem)){
                                registrado = true;
                            }else{
                                conec.rollback();
                                db.desconectar();
                                return false;
                            }
                        }
                    }
                }
                if(registrado){
                    conec.commit();
                    db.desconectar();
                    return true;
                }else{
                    conec.rollback();
                    db.desconectar();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }        
        return false;
    }    
}
