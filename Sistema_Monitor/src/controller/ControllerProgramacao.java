/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ProgramacaoMaquinaDAO;
import java.sql.Connection;
import java.sql.SQLException;
import model.ProgramacaoMaquina;

/**
 *
 * @author renato.soares
 */
public class ControllerProgramacao extends ProgramacaoMaquina{
    LogErro erro = new LogErro();
    public ControllerProgramacao() {
        super();
    }
    
    public boolean setarMontagemLoteProducao(String lote,String item){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ProgramacaoMaquinaDAO dao = new ProgramacaoMaquinaDAO(conec);                
                if(dao.setarMontagemLoteProducao(lote,item)){                    
                    conec.commit();
                    db.desconectar();
                    return true;
                }        
                conec.rollback();
                db.desconectar();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
}
