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
    private final LogErro erro = new LogErro();
    private final Connection conec;
    private int idMaquinaEventoParada;
    
    public ParadasMaquinaDAO(Connection conec) {
        this.conec = conec;
    }
    
    public ParadasMaquina buscaParadasmaquina(String cod_maquina){
        ParadasMaquina paradasMaquina = new ParadasMaquina();
        List<Paradas> listaParadas = new ArrayList<>();        
        try {
            sql = "SELECT par.codigo,par.abreviacao,par.descricao FROM condumigproducao.paradasmaquina pmaq \n" +
                "inner join condumigproducao.paradas par on pmaq.codigoparada = par.codigo\n" +
                "where pmaq.codigomaquina = ?;";
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
            return paradasMaquina;
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }
        return null;            
    }
    
    public boolean incluirInicioEventoMaquina(EventoMaquina evt ){
        sql = "Insert into bd_sistema_monitor.tb_maquina_evento (cod_maquina, metragem_evento) values (?,?);";                
        try {
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1,evt.getCod_maquina());
            st.setLong(2,evt.getMetragem());

            st.executeUpdate();
            return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }            
        return false;    
    }
    public long buscarIDEventoAberto(String cod_maquina){
            try {
                sql = "select id from bd_sistema_monitor.tb_maquina_evento where cod_maquina ="
                        + " ? and isnull(metragem_retorno)";
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, cod_maquina);
                ResultSet res = st.executeQuery();                
                while(res.next()){
                    long id = res.getLong("id");
                    return id;
                }                                
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        return 0;
    }
    
    public boolean RegistrarRetornoEventoMaquina(EventoMaquina evt ){
        sql = "update bd_sistema_monitor.tb_maquina_evento set metragem_retorno = ?, "
                + "data_hora_final = current_timestamp() where id = ?;";
        try {
            PreparedStatement st = conec.prepareStatement(sql);                
            st.setLong(1,evt.getMetragem());
            st.setLong(2,evt.getIdEvento());           
            st.executeUpdate();
        return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }            
        return false;    
    }
    
    public boolean incluirMotivoEventoMaquina(long cod_parada, long id_Evento){
        sql = "insert into bd_sistema_monitor.tb_maquina_evento_parada "
                + "(id_maquina_evento, cod_parada_maquina) values (?,?)";
        try {
            PreparedStatement st = conec.prepareStatement(sql);
            st.setLong(1,id_Evento);
            st.setLong(2,cod_parada);           
            st.executeUpdate();
            if(st.getUpdateCount()!=0){
                return buscaIdMaquinaEventoParada();
            }else{
                return false;
            }                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }            
        return false;    
    }
    public boolean buscaIdMaquinaEventoParada(){
        try {
            sql = "select last_insert_id() from bd_sistema_monitor.tb_maquina_evento_parada limit 1;";
                PreparedStatement st = conec.prepareStatement(sql);                
                ResultSet res = st.executeQuery();                
                if(res.next()){
                    this.idMaquinaEventoParada = res.getInt("last_insert_id()");
                    return true;
                }                                
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }                        
    public boolean  inluirObservcacaoEvento(String obs){
        try {
            sql = "insert into bd_sistema_monitor.tb_maquina_evento_observacao "
                        + "(id_maquina_evento_parada, observacao) values (?, ? );";
                    PreparedStatement st = conec.prepareStatement(sql);
                    st.setInt(1, this.idMaquinaEventoParada);
                    st.setString(2, obs);
                    st.executeUpdate();
                    return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public boolean incluirCodPesagem(int codPesagemSaida, int codPesagemEntrada){
        try {
            sql = "insert into bd_sistema_monitor.tb_maquina_evento_carretel_entrada "
                        + "(id_maquina_evento_parada, cod_pesagem_saida,cod_pesagem_entrada) values (?, ? ,?);";
                    PreparedStatement st = conec.prepareStatement(sql);
                    st.setInt(1, this.idMaquinaEventoParada);
                    st.setInt(2, codPesagemSaida);
                    st.setInt(3, codPesagemEntrada);
                    st.executeUpdate();
                    return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    } 
    public ParadasMaquina buscaParadasMaquinaProducaoAtual(String cod_maquina){
        ParadasMaquina paradasMaquina = new ParadasMaquina();
        List<Paradas> listaParadas = new ArrayList<>();
            try {
                sql = "SELECT par.id as idPar, par.cod_parada_maquina as codParada, "
                        + "pardesc.abreviacao as abrev, obs.observacao as obs, "
                        + "ent.cod_pesagem_saida, ent.cod_pesagem_entrada FROM bd_sistema_monitor.tb_maquina_evento evt "
                        + "Inner join bd_sistema_monitor.tb_maquina_evento_parada par on evt.id = par.id_maquina_evento "
                        + "Inner join condumigproducao.paradas pardesc on par.cod_parada_maquina = pardesc.codigo "
                        + "left join bd_sistema_monitor.tb_maquina_evento_observacao obs on par.id = obs.id_maquina_evento_parada "
                        + "left join bd_sistema_monitor.tb_maquina_evento_carretel_entrada ent on par.id = ent.id_maquina_evento_parada "
                        + "where evt.cod_maquina = ?;";
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, cod_maquina);
                ResultSet res = st.executeQuery();
                paradasMaquina.setCod_maquina(cod_maquina);
                while(res.next()){
                   Paradas parada = new Paradas();
                   parada.setCodigo(res.getInt("codParada"));
                   parada.setAbreviacao(res.getString("abrev"));
                   parada.setObservacao(res.getString("obs"));    
                   parada.setCodPesagemSaida(res.getInt("cod_pesagem_saida"));
                   parada.setCodPesagemEntrada(res.getInt("cod_pesagem_entrada"));
                   listaParadas.add(parada);
                }
                paradasMaquina.setListaParadas(listaParadas);
                return paradasMaquina;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        return null;            
    }        
    
}
