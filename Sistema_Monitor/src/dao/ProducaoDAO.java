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
import model.ComposicaoCobre;
import model.Producao;

/**
 *
 * @author renato.soares
 */
public class ProducaoDAO {
    private String sql;
    LogErro erro = new LogErro();
    private final Connection conec;

    public ProducaoDAO(Connection conec) {
        this.conec = conec;
    }
    
    
    public Producao buscaItemProducao(String codMaquina){       
        Producao prod = new Producao();
        try {            
            sql ="SELECT res.codigoitemprod,res.loteproducao,prd.met_produzida,prd.carretel_saida "
                    + "FROM condumigproducao.reservamaquina res inner join bd_sistema_monitor.tb_maquina_producao "
                    + "prd on prd.cod_maq = res.codigomaquina where res.codigomaquina = ? group by res.codigomaquina;";                
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, codMaquina);
            ResultSet res = st.executeQuery();
            if(res.next()){
                prod.setItemProducao(res.getString("codigoitemprod"));
                prod.setLoteProducao(res.getString("loteproducao"));
                prod.setCarretelSaida(res.getString("carretel_saida"));
                prod.setMetragemProduzida(res.getLong("met_produzida"));

                return prod;                    
            }else{
                System.out.println("Não ha item em produção.");
            }            
        } catch (SQLException e) {
            erro.gravaErro(e);
        }        
        return null;
    }
    
    public Long buscaMetragemProduzida (String lote, String item){
        Long metragem;        
        try {
            sql = "SELECT sum(metragemoperador) as met FROM condumigproducao.pesagem "
                    + "where loteproduzido = ? and codigoitem = ?;";            
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, lote);
            st.setString(2, item);
            ResultSet res = st.executeQuery();
            if(res.next()){
                metragem = res.getLong("met");
                return metragem;
            }
        } catch (SQLException e) {
            erro.gravaErro(e);
        }
        return null;
    }
    
    public boolean atualizaMetragemProduzida (String maquina, String metragem){
        
        try {
            sql = "update bd_sistema_monitor.tb_maquina_producao set met_produzida "
                + "= met_produzida + ? where cod_maq = ?;";                
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, metragem);
            st.setString(2, maquina);
            st.executeUpdate();
            return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }                    
        return false;
    }
    
    public boolean atualizaSaldoConsumoEntrada (String cod_Pesagem, String metragem){        
        try {
            sql = "update condumigproducao.pesagem set saldoconsumo = (saldoconsumo - ?) where codigo = ?";                
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, metragem);
            st.setString(2, cod_Pesagem);
            st.executeUpdate();
        return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }            
        return false;
    }

    public boolean atualizaCarretelSaida(String carretelSaida, String codMaquina) {

        try {
            sql = "update bd_sistema_monitor.tb_maquina_producao set carretel_saida = ? where cod_maq = ?";        
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, carretelSaida);
            st.setString(2, codMaquina);
            st.executeUpdate();
            return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }
        return false;
    }
    
    public List<ComposicaoCobre> buscaComposicaoCobrePesagem(int codPesagem){
        List<ComposicaoCobre> compCobre = new ArrayList<>();        
        try {
            sql = "SELECT * FROM condumigproducao.compcobrepesagem where idPesagem = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codPesagem);            
            ResultSet res = st.executeQuery();
            while(res.next()){
                ComposicaoCobre cobre = new ComposicaoCobre();
                cobre.setIdPesagem(codPesagem);
                cobre.setLaminadora(res.getString("laminadora"));
                cobre.setPorcentagem(res.getDouble("porcentagem"));
                compCobre.add(cobre);
            }
            return compCobre;            
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }

    public boolean validaItemAlongamento(String codItem) {
        try {
            sql = "SELECT * FROM item_Alongamento WHERE codigoItem = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, codItem);            
            ResultSet res = st.executeQuery();
            return res.next();
                   
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public int buscaFatorPerdaItem(String itemProducao) {
        try {
            sql = "SELECT porc_perda FROM perdaproc where codigoItem = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, itemProducao);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getInt("porc_perda");
            }
                   
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public int buscaPerdaProcessualCodPesagem(int idMatPrima) {
        try {
            sql = "SELECT perda FROM pesagem WHERE codigo = ? AND laminadora not like 'EX%';";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, idMatPrima);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getInt("perda");
            }                   
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public int buscaMenorFichaAberto(int codigoProgramacao) {
        try {
            sql = "SELECT min(ficha),id as ficha FROM condumigproducao.fichamontagem "
                    + "where codProgramacao = ? and isnull(idPesagem);";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codigoProgramacao);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getInt("ficha");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;

    }

    public boolean marcarFichaApontada(int codPesagem,int id) {
        try {
            sql = "update condumigproducao.fichamontagem  set situacao = 1, "
                    + "idPesagem = ?, observacao = 'Sistema Monitor' where id = ?;";        
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codPesagem);
            st.setInt(2, id);
            st.executeUpdate();
            return st.getUpdateCount()!=0;                                
        } catch (SQLException ex) {
            erro.gravaErro(ex);
        }
        return false;
        
    }

    public String[] buscaDataHoraFimProducao(String codMaquina) {
        try {
            sql = "SELECT data_hora_inicio as dataFimProducao FROM bd_sistema_monitor.tb_maquina_evento "
                    + "where id not in (select id from bd_sistema_monitor.tb_maquina_evento_apontamento) "
                    + "and cod_maquina = ? order by id desc limit 1;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, codMaquina);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                String[] tmp = res.getString("dataFimProducao").split(" ");
                return tmp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }

    public String buscaTipoItemProducao(String itemProducao) {
        try {
            sql = "SELECT tipoitem FROM condumigproducao.item where codigo = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, itemProducao);            
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getString("tipoitem");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }
}
