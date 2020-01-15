/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ParadasMaquinaDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import model.EventoMaquina;
import model.Paradas;
import model.ParadasMaquina;

/**
 *
 * @author renato.soares
 */
public class ControllerParadasMaquina {
    private ParadasMaquina paradasMaquina = new ParadasMaquina();
    private long ultimoEvento;
    LogErro erro = new LogErro();
    
    public ControllerParadasMaquina(String cod_Maquina) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);
                this.paradasMaquina = daoPar.buscaParadasmaquina(cod_Maquina);
                db.desconectar();
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }
    
    public List<String> buscaListaParadasDescricao(){
        try {                    
            List<Paradas> listaParadas = new ArrayList<>();
            List<String> paradasDescricao = new ArrayList<>();
            listaParadas = paradasMaquina.getListaParadas();
            for (int i=0;i<listaParadas.size();i++){
                paradasDescricao.add(String.valueOf(listaParadas.get(i).getCodigo()) + " - "
                        + listaParadas.get(i).getDescricao());
            }
            return paradasDescricao;
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return null;
    }
    
    public List<String> buscaInfoParadaPorCodigo(int codParada){
        try {                    
            List<String> infoParada = new ArrayList<>();
            List<Paradas> listaParadas = new ArrayList<>();
            listaParadas = paradasMaquina.getListaParadas();
            infoParada.add(String.valueOf(listaParadas.get(codParada).getCodigo()));
            infoParada.add(listaParadas.get(codParada).getAbreviacao());
            infoParada.add(listaParadas.get(codParada).getDescricao());
            return infoParada;
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return null;
    }
    
    public boolean registraInicioParadamaquina(long metragem, String cod_maquina){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);      
                if(daoPar.buscarIDEventoAberto(cod_maquina)==0){
                    EventoMaquina evt = new EventoMaquina();
                    evt.setCod_maquina(cod_maquina);
                    evt.setMetragem(metragem);                                
                    if(daoPar.incluirInicioEventoMaquina(evt)){
                        db.desconectar();
                        return true;
                    }else{
                        db.desconectar();
                        return false;
                    }
                }else{
                    db.desconectar();
                    return true;
                }                
            }            
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return false;
    }
    
    public boolean registraRetornoParadamaquina(long metragem, String cod_maquina){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);      
                EventoMaquina evt = new EventoMaquina();            
                evt.setMetragem(metragem);            
                evt.setIdEvento(daoPar.buscarIDEventoAberto(cod_maquina));
                this.ultimoEvento = evt.getIdEvento();
                return  daoPar.RegistrarRetornoEventoMaquina(evt);
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return false;
    }

    public long getUltimoEvento() {
        return ultimoEvento;
    }
    
    public boolean registraMotivoParadaMaquina(String codigo,String obs,int codPesagemSaida,int codPesagemEntrada){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                conec.setAutoCommit(false);
                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);                
                if(daoPar.incluirMotivoEventoMaquina(Long.valueOf(codigo), ultimoEvento)){
                    if(!obs.trim().equals("")){
                        if(!daoPar.inluirObservcacaoEvento(obs)){
                            conec.rollback();
                            db.desconectar();
                            return false;
                        }                        
                    }
                    if(codPesagemSaida!=0){
                        if(!daoPar.incluirCodPesagem(codPesagemSaida,codPesagemEntrada)){
                            conec.rollback();
                            db.desconectar();
                            return false;
                        }
                    }
                    conec.commit();
                    db.desconectar();
                    return true;
                }                            
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public ParadasMaquina buscaParadasProcessoAtual(String cod_maquina){
        try {                    
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);
                ParadasMaquina par = daoPar.buscaParadasMaquinaProducaoAtual(cod_maquina);
                db.desconectar();
                return par;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;               
    }          
}
