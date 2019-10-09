/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
    
    public List<Maquina> buscarMaquinasCadastradas(){
        
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
