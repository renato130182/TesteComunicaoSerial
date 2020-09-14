/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ItemDAO;
import java.sql.Connection;
import java.sql.SQLException;
import model.Item;

/**
 *
 * @author renato.soares
 */
public class ControllerItem {
    
    LogErro erro = new LogErro();
    
    public Item  BuscaDadosItem(Long codigo){        
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ItemDAO dao = new ItemDAO(conec);
                Item it = new Item(codigo);
                it = dao.buscaDescricaoItem(codigo);
                db.desconectar();
                return it;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);            
        }
        return null;
    }
    
    
    
}
