/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.EventoMaquina;
import model.Paradas;
import model.ParadasMaquina;

/**
 *
 * @author renato.soares
 */
public class ParadasMaquinaDAO {
    private String sql; 
    
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
                Logger.getLogger(ProgramacaoMaquinaDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ProgramacaoMaquinaDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        db.desconectar();
        return false;    
    }
}
