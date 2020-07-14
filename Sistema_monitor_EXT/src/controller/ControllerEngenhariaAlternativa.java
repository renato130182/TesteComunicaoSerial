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
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                prods = dao.BuscaListaItenDescricaoEngAlternativa(codItem,codItemProducao);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }            

    public String buscaItemCobrePadrao(String codItemProducao) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                String prods = dao.BuscaItemCobrePadrao(codItemProducao);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }

    public String buscaCodigoItemPVCExtrusadoPadrao(long codigo) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                String prods = dao.BuscaItemPVCExtrusadoPadrao(codigo);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";        
    }

    public String buscaCodigoItemPVC_CoExtrusadoPadrao(long codigo) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                String prods = dao.BuscaItemPVC_CoExtrusadoPadrao(codigo);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return ""; 
    }

    public String buscaCodigoItemPigmentoPadrao(long codigo) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                EngenhariaAlternativaDAO dao  = new EngenhariaAlternativaDAO(conec);
                String prods = dao.BuscaItemPigmentoPadrao(codigo);
                db.desconectar();
                return prods;
            }
        } catch (Exception e) {            
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "";
    }
}
