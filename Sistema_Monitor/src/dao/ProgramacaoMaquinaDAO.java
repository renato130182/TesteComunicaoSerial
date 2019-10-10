/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Produto;
import model.ProgramacaoMaquina;

/**
 *
 * @author renato.soares
 */
public class ProgramacaoMaquinaDAO {
    private String sql;
    
    public List<ProgramacaoMaquina> buscaProgramacaoMaquina(String codMaquina){
        List<ProgramacaoMaquina> lista = new ArrayList<ProgramacaoMaquina>();
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.equals(db)){
            try {
                sql = "select prog.codigoitem, item.descricao, prog.loteproducao, "
                        + "prog.datacadastro,prog.quantloteprogramado,prog.metragemprogramada "
                        + ",prog.datacadastro from condumigproducao.programacaomaquina prog inner join "
                        + "condumigproducao.item on item.codigo = prog.codigoitem  "
                        + "where prog.montada = '0' and prog.situacao < 2 and "
                        + "prog.codigomaquina = ?;";
                java.sql.Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                while(res.next()){
                   ProgramacaoMaquina prog = new ProgramacaoMaquina();
                   prog.setProduto( new Produto(res.getString("prog.codigoitem"),
                           res.getString("item.descricao").trim()));
                   prog.setLoteproducao(res.getString("prog.loteproducao"));
                   prog.setQuantidadeProgramada(res.getInt("prog.quantloteprogramado"));
                   prog.setMetragemProgramada(res.getLong("prog.metragemprogramada"));
                   prog.setDataProgramada(res.getString("prog.datacadastro"));
                   lista.add(prog);
                }
                return lista;
            } catch (SQLException ex) {
                Logger.getLogger(ProgramacaoMaquinaDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
