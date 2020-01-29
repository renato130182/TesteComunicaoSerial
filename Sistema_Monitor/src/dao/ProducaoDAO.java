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
import model.ReservaPesagem;

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
            ex.printStackTrace();
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

    public int registraDadosPesagem(List<String> dadosQuery) {
        try {
            sql = "insert into condumigproducao.pesagem (codigopesagem, dataproducao, horafimproducao, datapesagem, "
                    + "horapesagem, tipopesagem, codigousuario, codigoembalagem, codigooperador1, encarregado1, "
                    + "codigooperador2, codigooperador3, encarregado2, codigooperador4, codigomaquina, codigoitem, "
                    + "massaliquida, metragemoperador, metragemteorica, metragemtrocaturno, loteproduzido, qtosfios, "
                    + "tempogasto, finalizada, inspecionada, tipoitem, observacao, tempoparada, saldoretorno, saldoconsumo, "
                    + "status, mac, turnomaquina, exportada, Kg_Mt, laminadora, perda) values "
                    + "(?,?,?,(select date_format(now(), \"%Y-%m-%d\")),(select date_format(now(),\"%H:%m:%s\")),"
                    + "0,?,?,?,?,'',?,?,'',?,?,'0',?,?,?,?,?,?,'0','0',?,?,?,?,?,'1',?,'0','0','0',?,?)";
            PreparedStatement st = this.conec.prepareStatement(sql);
            for (int i=1;i<=dadosQuery.size();i++){
                st.setString(i,dadosQuery.get(i-1));
            }
            st.executeUpdate();
            if (st.getUpdateCount()!=0){
                sql = "select last_insert_id();";
                st = this.conec.prepareStatement(sql);
                ResultSet res = st.executeQuery();
                if(res.next()){
                    return res.getInt("last_insert_id()");
                }
            }        
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public boolean registrarDadosReservaPesagem(ReservaPesagem resPes) {
        try {
            sql = "insert into condumigproducao.reservapesagem (codigopesagem, sequencia, "
                    + "itemreserva, lotereserva, qtosfios, codigoembalagem, quantidade, "
                    + "codigoembalagemtroca, lotereservatroca, idPesagem, idMatPrima) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, resPes.getCodigoPesagem());
            st.setInt(2, resPes.getSequencia());
            st.setString(3, resPes.getItemReserva());
            st.setString(4, resPes.getLoteReserva());
            st.setInt(5, resPes.getQtosfios());
            st.setString(6, resPes.getCodigoEmbalagem());
            st.setDouble(7, resPes.getQuantidade());
            st.setString(8, resPes.getCodigoEmbalagelTroca());
            st.setString(9, resPes.getLoteReservaTroca());
            st.setInt(10, resPes.getIdPesagem());
            st.setInt(11, resPes.getIdMatPrima());
            st.executeUpdate();
            return st.getUpdateCount()!=0;            
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    public boolean resgistraParadaPesagem(List<String> dados){
        try {
            sql = "insert into condumigproducao.paradaspesagem (codigopesagem, sequencia, "
                    + "codigoitem, codigomaquina, codigoparada, dataparada, origemparada, "
                    + "tempogasto, observacao, idPesagem) values(?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement st = this.conec.prepareStatement(sql);
            for (int i=1;i<=dados.size();i++){
                st.setString(i,dados.get(i-1));
            }
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    public boolean resgistraComposicaoCobre(List<String> dados){
        try {
            sql = "insert into condumigproducao.compcobrepesagem (idPesagem, "
                    + "porcentagem, laminadora) values (?,?,?);";
            PreparedStatement st = this.conec.prepareStatement(sql);
            for (int i=1;i<=dados.size();i++){
                st.setString(i,dados.get(i-1));
            }
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean registrarApontamentoLogSistemaMonitor(String codigo, int codPesagem) {
        try {
            sql = "insert into bd_sistema_monitor.tb_eventos_sistema_apontamento (id_evento_sistema_log, id_pesagem)"
                    + "SELECT id,? FROM bd_sistema_monitor.tb_eventos_sistema_log where codigo_maquina = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codPesagem);
            st.setString(2, codigo);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean registrarApontamentosMaquinaEvento(int codPesagem, String codigo) {
        try {
            sql = "insert into bd_sistema_monitor.tb_maquina_evento_apontamento (id_maquina_evento_parada, id_pesagem) "
                    + "SELECT id,? FROM bd_sistema_monitor.tb_maquina_evento where cod_maquina = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codPesagem);
            st.setString(2, codigo);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean limparTabelaMaquinaProducao(String codigo) {
        try {
            sql = "update bd_sistema_monitor.tb_maquina_producao set met_produzida = 0, "
                    + "carretel_saida = '' where cod_maq = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setString(1, codigo);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean adicionarApontametoProgMaruina(int codigoProgramacao) {
        try {
            sql = "update condumigproducao.programacaomaquina set quantloteproduzido = "
                    + "quantloteproduzido + 1 where codigo = ?;";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codigoProgramacao);            
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public boolean registraCodPesagemRelatorioMicromero(int codPesagem, String codigo, String loteProducao) {
        try {
            sql = "update bd_sistema_monitor.tb_maquina_dados_micrometro set codigo_pesagem = ? "
                    + "where cod_maquina = ? and lote = ? and isnull(codigo_pesagem);";
            PreparedStatement st = this.conec.prepareStatement(sql);
            st.setInt(1, codPesagem);            
            st.setString(2, codigo);
            st.setString(3, loteProducao);
            st.executeUpdate();
            return st.getUpdateCount()!=0;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
        
    }
}
