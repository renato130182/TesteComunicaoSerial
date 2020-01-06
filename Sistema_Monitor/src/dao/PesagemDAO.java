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
import model.Pesagem;

/**
 *
 * @author renato.soares
 */
public class PesagemDAO {
    private String sql;
    LogErro erro = new LogErro();
    
    public List<Pesagem> buscapesagensMontagem(String codMaquina){
        List<Pesagem> lista = new ArrayList<Pesagem>();
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "SELECT pes.codigo,pes.codigoitem,it.descricao, pes.observacao,pes.metragemoperador,pes.saldoconsumo," +
                        "pes.codigoembalagem,pes.qtosfios,pes.loteproduzido FROM condumigproducao.reservamaquina res " +
                        "Inner join condumigproducao.pesagem pes on pes.codigo = res.pesagem " +
                        "Inner join condumigproducao.item it on it.codigo = pes.codigoitem " +
                        "where res.codigomaquina = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                while(res.next()){
                    Pesagem pes = new Pesagem();
                    pes.setCodigo(res.getString("codigo"));
                    pes.setObservacao(res.getString("observacao"));
                    pes.setMetragemOperador(res.getLong("metragemoperador"));
                    pes.setCodEmbalagem(res.getString("codigoembalagem"));
                    pes.setSaldoConsumo(res.getLong("saldoconsumo"));
                    pes.setCodItem(res.getString("codigoitem"));
                    pes.setDecItem(res.getString("descricao"));
                    pes.setQtosFios(res.getInt("qtosfios"));
                    pes.setLote(res.getString("loteproduzido"));
                    lista.add(pes);
                }
                db.desconectar();
                return lista;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return null;
    }

    public Pesagem buscaPesagemCodigo(String codPesagem) {
        Pesagem pes = null;
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "SELECT pes.codigo,pes.codigoitem,it.descricao, pes.observacao,pes.metragemoperador,pes.saldoconsumo," +
                    "pes.codigoembalagem,pes.qtosfios,pes.loteproduzido FROM condumigproducao.pesagem pes " +
                    "Inner join condumigproducao.item it on it.codigo = pes.codigoitem " +
                    "where pes.codigo = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codPesagem);
                ResultSet res = st.executeQuery();
                while(res.next()){
                    pes = new Pesagem();
                    pes.setCodigo(res.getString("codigo"));
                    pes.setObservacao(res.getString("observacao"));
                    pes.setMetragemOperador(res.getLong("metragemoperador"));
                    pes.setCodEmbalagem(res.getString("codigoembalagem"));
                    pes.setSaldoConsumo(res.getLong("saldoconsumo"));
                    pes.setCodItem(res.getString("codigoitem"));
                    pes.setDecItem(res.getString("descricao"));
                    pes.setQtosFios(res.getInt("qtosfios"));
                    pes.setLote(res.getString("loteproduzido"));
                }
                db.desconectar();
                return pes;
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
        db.desconectar();
        return null;
    }
}
