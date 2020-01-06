/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.EngenhariaAlternativaDAO;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.Produto;

/**
 *
 * @author renato.soares
 */
public class ControllerEngenhariaAlternativa {
    LogErro erro = new LogErro();
    
    public List<Produto> buscaListaAlternativas(String codItem, String codItemProducao) {
        List<Produto> prods = new ArrayList<>();
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                prods = dao.BuscaItenDescricaoEngAlternativa(codItem,codItemProducao);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
    
    
    
}
