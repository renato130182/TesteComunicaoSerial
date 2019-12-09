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
import model.EventoMaquina;
import model.Paradas;
import model.ParadasMaquina;

/**
 *
 * @author renato.soares
 */
public class ParadasMaquinaDAO {
    private String sql; 
    LogErro erro = new LogErro();
    
    public ParadasMaquina buscaParadasmaquina(String cod_maquina){
        ParadasMaquina paradasMaquina = new ParadasMaquina();
        List<Paradas> listaParadas = new ArrayList<>();
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "SELECT par.codigo,par.abreviacao,par.descricao FROM condumigproducao.paradasmaquina pmaq \n" +
                    "inner join condumigproducao.paradas par on pmaq.codigoparada = par.codigo\n" +
                    "where pmaq.codigomaquina = ?;";
                java.sql.Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, cod_maquina);
                ResultSet res = st.executeQuery();
                paradasMaquina.setCod_maquina(cod_maquina);
                while(res.next()){
                   Paradas parada = new Paradas();
                   parada.setCodigo(res.getInt("codigo"));
                   parada.setAbreviacao(res.getString("abreviacao"));
                   parada.setDescricao(res.getString("descricao"));
                   listaParadas.add(parada);
                }
                paradasMaquina.setListaParadas(listaParadas);
                db.desconectar();
                return paradasMaquina;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return null;            
    }
    
    public boolean incluirInicioEventoMaquina(EventoMaquina evt ){
        sql = "Insert into bd_sistema_monitor.tb_maquina_evento (cod_maquina, metragem_evento) values (?,?);";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1,evt.getCod_maquina());
                st.setLong(2,evt.getMetragem());
           
                st.executeUpdate();
                if(st.getUpdateCount()!=0){
                    db.desconectar();
                    return true;
                }else{
                    db.desconectar();
                    return false;
                }                                
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }            
        }
        db.desconectar();
        return false;    
    }
    public long buscarIDEventoAberto(String cod_maquina){
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "select id from bd_sistema_monitor.tb_maquina_evento where cod_maquina ="
                        + " ? and isnull(metragem_retorno)";
                java.sql.Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, cod_maquina);
                ResultSet res = st.executeQuery();                
                while(res.next()){
                    long id = res.getLong("id");
                    db.desconectar();
                    return id;
                }                                
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return 0;
    }
    
    public boolean RegistrarRetornoEventoMaquina(EventoMaquina evt ){
        sql = "update bd_sistema_monitor.tb_maquina_evento set metragem_retorno = ?, "
                + "data_hora_final = current_timestamp() where id = ?;";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);                
                st.setLong(1,evt.getMetragem());
                st.setLong(2,evt.getIdEvento());
           
                st.executeUpdate();
                if(st.getUpdateCount()!=0){
                    db.desconectar();
                    return true;
                }else{
                    db.desconectar();
                    return false;
                }                                
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }            
        }
        db.desconectar();
        return false;    
    }
    
    public boolean incluirMotivoEventoMaquina(long cod_parada, long id_Evento,String obs){
        sql = "insert into bd_sistema_monitor.tb_maquina_evento_parada "
                + "(id_maquina_evento, cod_parada_maquina) values (?,?)";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setLong(1,id_Evento);
                st.setLong(2,cod_parada);           
                st.executeUpdate();
                if(st.getUpdateCount()!=0){
                    if(!obs.trim().equals("")){                        
                        sql = "insert into bd_sistema_monitor.tb_maquina_evento_observacao "
                            + "(id_maquina_evento_parada, observacao) values (last_insert_id(), ? );";
                        PreparedStatement st1 = conec.prepareStatement(sql);
                        st1.setString(1, obs);
                        st1.executeUpdate();
                        if(st1.getUpdateCount()!=0){
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
                }else{
                    db.desconectar();
                    return false;
                }                                
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }            
        }
        db.desconectar();
        return false;    
    }
    
    public ParadasMaquina buscaParadasMaquinaProducaoAtual(String cod_maquina){
        ParadasMaquina paradasMaquina = new ParadasMaquina();
        List<Paradas> listaParadas = new ArrayList<>();
        //Long identificadores[] = null;
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "SELECT par.id as idPar, par.cod_parada_maquina as codParada, "
                        + "pardesc.abreviacao as abrev, obs.observacao as obs  "
                        + "FROM bd_sistema_monitor.tb_maquina_evento evt Inner join "
                        + "bd_sistema_monitor.tb_maquina_evento_parada par on "
                        + "evt.id = par.id_maquina_evento Inner join condumigproducao.paradas "
                        + "pardesc on par.cod_parada_maquina = pardesc.codigo "
                        + "left join bd_sistema_monitor.tb_maquina_evento_observacao obs "
                        + "on par.id = obs.id_maquina_evento_parada where evt.cod_maquina = ?";
                java.sql.Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, cod_maquina);
                ResultSet res = st.executeQuery();
                paradasMaquina.setCod_maquina(cod_maquina);
                //if(res.last()){
                //    identificadores = new Long[res.getRow()];
                //    res.beforeFirst();
                //}
                while(res.next()){
                   Paradas parada = new Paradas();
                   parada.setCodigo(res.getInt("codParada"));
                   parada.setAbreviacao(res.getString("abrev"));
                   parada.setObservacao(res.getString("obs"));                   
                   listaParadas.add(parada);
                   //identificadores[res.getRow()] = res.getLong("idPar");
                }
                paradasMaquina.setListaParadas(listaParadas);
                db.desconectar();
                return paradasMaquina;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return null;            
    }    
}
