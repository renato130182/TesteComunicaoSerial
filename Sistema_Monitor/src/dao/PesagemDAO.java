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
import model.Pesagem;

/**
 *
 * @author renato.soares
 */
public class PesagemDAO {
    private String sql;
    
    public List<Pesagem> buscapesagensMontagem(String codMaquina){
        List<Pesagem> lista = new ArrayList<Pesagem>();
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "SELECT pes.codigo, pes.observacao,pes.metragemoperador,pes.saldoconsumo,"
                        + " pes.codigoembalagem FROM condumigproducao.reservamaquina res "
                        + "Inner join condumigproducao.pesagem pes on pes.codigo = res.pesagem "
                        + "where res.codigomaquina = ?;";
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
                    lista.add(pes);
                }
                db.desconectar();
                return lista;
            } catch (SQLException ex) {
                Logger.getLogger(PesagemDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        db.desconectar();
        return null;
    }
}
