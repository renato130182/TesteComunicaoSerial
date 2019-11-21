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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Maquina;

/**
 *
 * @author renato.soares
 */
public class MaquinaDAO {
    private String sql;

    public MaquinaDAO() {
        sql = "";
    }
    
    public Maquina buscarDadosMaquina(String codMaquina){
        Maquina maq = new Maquina();
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            
            try {
                sql = "select maq.codigo, maq.descricao, al.metros_arrebentamento, "
                        + "al.percentual_velocidade_parada from "
                        + "condumigproducao.maquina maq left join "
                        + "bd_sistema_monitor.tb_maquina_alerta al "
                        + "on al.codigo_maquina_alerta = maq.codigo "
                        + "where maq.codigo = ?;";
                Connection conec = db.getConnection();
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, codMaquina);
                ResultSet res = st.executeQuery();
                if(res.next()){                  
                    maq.setCodigo(res.getString("maq.codigo"));
                    maq.setDescricao(res.getString("maq.descricao"));
                    maq.setAlertaMetrosParaArrebentamento(res.getInt("metros_arrebentamento"));
                    maq.setAlertaPercentualVelocidade(res.getFloat("percentual_velocidade_parada"));
                    db.desconectar();
                    return maq;
                }else{
                    System.out.println("Query não buscou dados da maquina");
                }} catch (SQLException ex) {
                Logger.getLogger(MaquinaDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        db.desconectar();
        return null;
    }
    
    public List<String> listarMaquinas() throws SQLException{
        List<String> lista = new ArrayList();
        sql = "SELECT codigo FROM condumigproducao.maquina where situacao = 1;";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            
            Connection conec = db.getConnection();
            Statement st = conec.prepareStatement(sql);
            ResultSet res = st.executeQuery(sql);
            if(res.next()){
                while(res.next()){
                    lista.add(res.getString("codigo"));
               }
               db.desconectar();
               return lista;
           }else{
               System.out.println("Query não listou maquinas");
           }
        }
        db.desconectar();
        return null;
    }
}
