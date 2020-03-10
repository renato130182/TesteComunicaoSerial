/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.EngenhariaDAO;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.Engenharia;

/**
 *
 * @author renato.soares
 */
public class ControllerEngenharia {
    LogErro erro = new LogErro();
    
    public List<Engenharia> buscaListaEngenhariaEmProducao (String codItem, String codMaquina, long met) {
        List<Engenharia> eng = new ArrayList<>();        
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                EngenhariaDAO dao  = new EngenhariaDAO(conec);
                eng = dao.buscaEngenhariaEmProducaoPVCPigmento(codMaquina,codItem);                               
                db.desconectar();
                return eng;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
    
}
