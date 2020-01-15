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
import model.ReservaPesagem;

/**
 *
 * @author renato.soares
 */
public class ReservaPesagemDAO {
     private final Connection conec;
    private LogErro erro = new LogErro();
    private String sql;

    public ReservaPesagemDAO(Connection conec) {
        this.conec = conec;
    }
    
    public List<ReservaPesagem> buscaConsumoCobreReservaPesagem(String codMaquina){
        List<ReservaPesagem> list = new ArrayList<>();
        try {
            sql = "SELECT  pesEntrada.codigoItem as itemReserva, itemEntrada.descricao,"
                    + "pesEntrada.loteproduzido as loteReserva,pesEntrada.codigoEmbalagem ,"
                    + "pesEntrada.codigo as idMatPRima,evt.metragem_evento,pesEntrada.qtosfios, "
                    + "pesSaida.codigoembalagem as embTroca, pesSaida.loteproduzido as loteTroca "
                    + "FROM bd_sistema_monitor.tb_maquina_evento evt "
                    + "inner join bd_sistema_monitor.tb_maquina_evento_carretel_entrada car on evt.id = car.id_maquina_evento_parada "
                    + "inner join condumigproducao.pesagem pesEntrada on pesEntrada.codigo = car.cod_pesagem_saida "
                    + "inner join condumigproducao.pesagem pesSaida on pesSaida.codigo = car.cod_pesagem_entrada "
                    + "left join condumigproducao.item itemEntrada on pesEntrada.codigoitem = itemEntrada.codigo "
                    + "left join condumigproducao.item itemSaida on pesSaida.codigoitem = itemSaida.codigo "
                    + "where evt.cod_maquina = ? and evt.id not in "
                    + "(select id_maquina_evento_parada from bd_sistema_monitor.tb_maquina_evento_apontamento) "
                    + "union SELECT pes.codigoitem as itemReserva,it.descricao,pes.loteproduzido as loteReserva,pes.codigoembalagem,"
                    + "pes.codigo as idMatPRima,(SELECT met_produzida FROM bd_sistema_monitor.tb_maquina_producao "
                    + "where cod_maq = ?), pes.qtosfios,'','' FROM condumigproducao.reservamaquina res "
                    + "Inner join condumigproducao.pesagem pes on pes.codigo = res.pesagem "
                    + "Inner join condumigproducao.item it on it.codigo = pes.codigoitem where res.codigomaquina = ? ;";
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codMaquina);
            st.setString(2, codMaquina);
            st.setString(3, codMaquina);
            ResultSet res = st.executeQuery();
            while(res.next()){
                ReservaPesagem resPes = new ReservaPesagem();
                resPes.setItemReserva(res.getString("itemReserva"));
                resPes.setLoteReserva(res.getString("loteReserva"));
                resPes.setItemDescricao(res.getString("descricao"));
                resPes.setCodigoEmbalagem(res.getString("codigoEmbalagem"));
                resPes.setIdMatPrima(res.getInt("idMatPRima"));
                resPes.setQuantidade(res.getDouble("metragem_evento"));
                String tmp = res.getString("embTroca");
                if(!tmp.trim().equalsIgnoreCase("null")){
                    resPes.setCodigoEmbalagelTroca(res.getString("embTroca"));
                    resPes.setLoteReservaTroca(res.getString("loteTroca"));
                }else{
                    resPes.setCodigoEmbalagelTroca("");
                    resPes.setLoteReservaTroca("");
                }
                resPes.setUnidade("Metros");
                list.add(resPes);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    
        return null;
    }
}
