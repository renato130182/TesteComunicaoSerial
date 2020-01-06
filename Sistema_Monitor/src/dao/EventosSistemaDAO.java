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
import javax.swing.JOptionPane;
import model.Paradas;

/**
 *
 * @author renato.soares
 */
public class EventosSistemaDAO {
    private final Connection conec;
    private Integer idEventoSistema;
    private String sql;
    LogErro erro = new LogErro();
    
    public EventosSistemaDAO(Connection conec) {
        this.conec = conec;
    }
    
    public boolean registraEventoSistema(Integer codEvento, String codMaquina){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_log (cod_evento,codigo_maquina) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,codEvento);
            st.setString(2, codMaquina);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean buscaIdEventoSistema(){
        try {
            sql = "select last_insert_id() from bd_sistema_monitor.tb_eventos_sistema_log limit 1;";
            PreparedStatement st = conec.prepareStatement(sql);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                this.idEventoSistema = res.getInt("last_insert_id()");
                return true;
            }else{
                return false;
            }    
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean registraUsuarioEventoSistema(String usuario){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_usuario "
                    + "(id_eventos_sistema_log, usuario) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setString(2,usuario);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    
    public boolean registraDiametroEventoSistema(double diametro){
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_diametro "
                    + "(id_evento_sistema_log, diametro) values (?,?);";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setDouble(2,diametro);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean registraMetragemEventoSistema(Integer metragem) {
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_metragem "
                    + "(id_evento_sistema_log, metragem) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setDouble(2,metragem);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean registraLoteEventoSistema(String lote) {
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_lote "
                    + "(id_evento_sistema_log, lote) values(?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setInt(1,this.idEventoSistema);
            st.setString(2,lote);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean ValidaPreApontamentoEventoSistema(String codParada, String codMauina,
                boolean msg){
        try {
            sql = "SELECT par.repete FROM bd_sistema_monitor.tb_maquina_parada_pre_apontamento pre " +
                "inner join condumigproducao.paradas par on pre.cod_parada = par.codigo " +
                "where pre.cod_maquina = ? and pre.cod_parada = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codMauina);
            st.setString(2, codParada);
            ResultSet res = st.executeQuery();
            if(res.next()){
                if(res.getString("repete").equals("0")){
                    if(msg){
                        JOptionPane.showMessageDialog(null,"Já existem um apontamento para este motivo \n "
                                + "e o mesmo não pode se repetir em uma unca parada! \n"
                                + "Por favor verifique e tente novamnete","Inclusão de motivo",JOptionPane.INFORMATION_MESSAGE);
                    }
                    return false;
                }else{
                    return true;
                }
            }else{
                return true;
            }    
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean registraPreApontamentoEventoSistema(String codMaquina, String codParada,String obs,int codPesagem) {
        try {
            sql = "insert into bd_sistema_monitor.tb_maquina_parada_pre_apontamento "
                    + "(cod_maquina, cod_parada,observacao,codPesagem) values (?,?,?,?)";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codMaquina);
            st.setString(2,codParada);            
            st.setString(3, obs);
            st.setInt(4,codPesagem);
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    
    public List<Paradas> BuscaPreApontamentoEventoSistema(String codMauina){
        List<Paradas> paradas = new ArrayList<>();
        try {
            sql = "SELECT pre.id,cod_parada,descricao,abreviacao,pre.observacao,pre.codPesagem "
                    + "FROM bd_sistema_monitor.tb_maquina_parada_pre_apontamento pre " +
                    "inner join condumigproducao.paradas par on pre.cod_parada = par.codigo " +
                    "where pre.cod_maquina = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codMauina);            
            ResultSet res = st.executeQuery();
            while(res.next()){
                Paradas parada = new Paradas();
                parada.setCodigo(res.getInt("cod_parada"));
                parada.setAbreviacao(res.getString("abreviacao"));
                parada.setDescricao(res.getString("descricao"));
                parada.setObservacao(res.getString("observacao"));
                parada.setIdRegistro(res.getInt("id"));
                parada.setCodPesagem(res.getInt("codPesagem"));
                paradas.add(parada);
            }
            return paradas;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
            
        } 
        return null;
    }
    
    public List<String> BuscaIdsApontamentoEventoSistema(String codMaquina){
        List<String> idsParadas = new ArrayList<>();
        try {
            sql = "SELECT pre.id FROM bd_sistema_monitor.tb_maquina_parada_pre_apontamento pre " +                    
                    "where pre.cod_maquina = ?;";
            PreparedStatement st = conec.prepareStatement(sql);            
            st.setString(1, codMaquina);            
            ResultSet res = st.executeQuery();
            while(res.next()){
                String id = new String();
                id = res.getString("id");
                idsParadas.add(id);
            }
            return idsParadas;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);            
        } 
        return null;
    }

    public boolean removePreApontamentoEventoSistema(String id) {
        try {
            sql = "delete from bd_sistema_monitor.tb_maquina_parada_pre_apontamento  where id = ?";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, id);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean removePreApontamentoEventoSistemaRegistrados(String codMaquina) {
        try {
            sql = "delete from bd_sistema_monitor.tb_maquina_parada_pre_apontamento  where cod_maquina = ?";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codMaquina);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;        
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public String buscaIdEventoSistemaUltimoMotivo(String codMaquina,int idMotivo,String codPesagem){
        try {
            sql = "";
            PreparedStatement st = conec.prepareStatement(sql);            
            ResultSet res = st.executeQuery();
            if(res.next()){                
                return res.getString("id");
            }else{
                return null;
            }    
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return null;
    }
}
