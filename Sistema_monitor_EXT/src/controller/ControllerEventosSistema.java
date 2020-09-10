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
import java.util.ArrayList;
import java.util.List;
import model.Paradas;
import model.Usuario;

/**
 *
 * @author renato.soares
 */
public class ControllerEventosSistema {
    LogErro erro =  new LogErro();
    
    public boolean registraEventos(Integer cod_Evento, String user,double diametro,
            Integer metragem,String codMaquina,String lote ){
        boolean registrado = false;
        try {                    
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();  
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                if(dao.registraEventoSistema(cod_Evento,codMaquina)){
                    registrado = true;
                    if(dao.buscaIdEventoSistema()){
                        if(user!=null){
                            if(!user.trim().equals("")){
                                if(dao.registraUsuarioEventoSistema(user)){
                                    registrado = true;
                                }else{
                                    conec.rollback();
                                    db.desconectar();
                                    return false;
                                }                                
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
                        if(!lote.trim().equals("")){
                            if(dao.registraLoteEventoSistema(lote)){
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
            erro.gravaErro(e);
        }        
        return false;
    } 
    
    public boolean verificaPreApontamento(String codParada, String codMaquina,String obs,
            boolean msg,int codPesagemSaida,int codPesagemEntrada){
        try {                    
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();   
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                if(dao.ValidaPreApontamentoEventoSistema(codParada, codMaquina,msg,codPesagemSaida)){
                    if(dao.registraPreApontamentoEventoSistema(codMaquina, codParada,obs,codPesagemSaida,codPesagemEntrada)){
                        conec.commit();
                        db.desconectar();
                        return true;
                    }else{
                        conec.rollback();
                        db.desconectar();
                        return false;
                    }
                }else{
                    conec.rollback();
                    db.desconectar();
                    return false;
                }
            }
        } catch (SQLException e) {
            erro.gravaErro(e);
            
        }                
        return false;
    }
    
    public List<Paradas>  BuscaPreApontamentos(String codMaquina){  
        try {                    
            ConexaoDatabase db = new ConexaoDatabase();
            List<Paradas> paradas = new ArrayList<>();
                if(db.isInfoDB()){
                    Connection conec = db.getConnection();     
                    if(conec==null)return null;
                    EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                    paradas = dao.BuscaPreApontamentoEventoSistema(codMaquina);
                }
            db.desconectar();
            return paradas;
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);            
        }
        return null;
    }
    
    public boolean removerPreApontamento(Integer linha, String codMaquina){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            List<String> ids = new ArrayList<>();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();      
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                ids = dao.BuscaIdsApontamentoEventoSistema(codMaquina);
                if(ids.size()>=linha){
                    if(dao.validaPossibilidadeRemocaoPorID(ids.get(linha))){
                        if(dao.removePreApontamentoEventoSistema(ids.get(linha))){
                            conec.commit();
                            db.desconectar();
                            return true;
                        }else{
                            conec.rollback();
                            db.desconectar();
                            return false;
                        }                        
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean removerPreApontamentosRegistrados(String codMaquina) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();           
            if(db.isInfoDB()){
                Connection conec = db.getConnection();    
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);
                if(dao.removePreApontamentoEventoSistemaRegistrados(codMaquina)){
                    conec.commit();
                    db.desconectar();
                    return true;
                }else{
                    conec.rollback();
                    db.desconectar();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;                
    }
    
    public boolean removerPreApontamentoPodID(Integer idPreParada){
        try {
            ConexaoDatabase db = new ConexaoDatabase();            
            if(db.isInfoDB()){
                Connection conec = db.getConnection();     
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);                
                if(idPreParada!=null){
                    if(dao.removePreApontamentoEventoSistema(String.valueOf(idPreParada))){
                        conec.commit();
                        db.desconectar();
                        return true;
                    }else{
                        conec.rollback();
                        db.desconectar();
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean atualizaCarretelEntradaPreParada(int codPesSaida,int codPesEntrada ) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();            
            if(db.isInfoDB()){
                Connection conec = db.getConnection();     
                if(conec==null)return false;
                conec.setAutoCommit(false);
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);                              
                if(dao.setarCarretelEntradaPreParada(codPesSaida,codPesEntrada)){
                    conec.commit();
                    db.desconectar();
                    return true;
                }else{
                    conec.rollback();
                    db.desconectar();
                    return false;
                }
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public List<Usuario> buscaListaEventosUsuarioLogin(String loteProducao){
        try {
            ConexaoDatabase db = new ConexaoDatabase();            
            if(db.isInfoDB()){
                Connection conec = db.getConnection();        
                if(conec==null)return null;
                EventosSistemaDAO dao = new EventosSistemaDAO(conec);  
                List<Usuario> user = dao.buscaHistoricoLoginOperador(loteProducao);
                db.desconectar();
                return user;
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }

    public boolean verificaPreApontamentoRealizado(String codParada, String codMaquina, boolean msg, int codPesagemSaida) {
    ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();   
            if(conec==null)return false;
            EventosSistemaDAO dao = new EventosSistemaDAO(conec);
            boolean ap = dao.ValidaPreApontamentoEventoSistema(codParada, codMaquina,msg,codPesagemSaida);
            db.desconectar();
            return ap;
        }
        return false;
    }

    public void atualizaLoteUltimoEventoLogin(String lote) {
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();   
            if(conec==null)return;
            EventosSistemaDAO dao = new EventosSistemaDAO(conec);
            dao.atualizaUltimoEventoLoginLote(lote);
            db.desconectar();
        }
    }
       
    
}
