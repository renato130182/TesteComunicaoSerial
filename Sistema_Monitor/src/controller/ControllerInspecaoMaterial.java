/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.InspecaoMaterialDAO;
import java.sql.Connection;

/**
 *
 * @author renato.soares
 */
public class ControllerInspecaoMaterial {
    LogErro erro = new LogErro();
    
    public int buscaTipoInspecaoItem( String codItem){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                InspecaoMaterialDAO dao = new InspecaoMaterialDAO(conec);
                int tipo = dao.buscaTipoInspecaoItem(codItem);
                db.desconectar();
                return tipo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 99;
    }

    public boolean validaRegistroAmostra(int tipoInspecao, int numPesagem,
            String lote, String codItem,String CodOperador) {
        /* tipos de Inspeção
        0 - A cada Bobina
        1 - Uma por Lote
        2 - Uma por lote ou turno
        3 - Sem inspeção
        99 - falha ao  buscar tipo de inspeção
        */
        try {
            switch (tipoInspecao){
                case(0): 
                    // adiciona controle e seta aguardando inspeção
                    return adicionarControleSetaAguardando(numPesagem,lote,codItem);                    
                case(1):
                    //verifica se ja houve entregra/cadastro de controle de amostra para este lote senão
                    // adiciona controle e seta aguardando inspeção
                    return veririficarControleAmostraPorLote(numPesagem,lote,codItem);
                case(2):
                    //verifica se ja houve entregra/cadastro de controle de amostra para este lote, senão
                    // adiciona controle e seta aguardando inspeção, caso contrario
                    //verica se o operador cadastrado na pesagem da amostra entregue é o mesmo da pesagem atual, senão
                    // adiciona controle e seta aguardando inspeção, caso contrario                    
                    return veririficarControleAmostraPorLoteTurno(numPesagem,lote,codItem,CodOperador);
                default:
                    //apenas retornar a não necessida do cadastro de amostra.
                    return true;
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
    private boolean  adicionarControleSetaAguardando(int codPesagem,String lote, String codItem){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                conec.setAutoCommit(false);
                InspecaoMaterialDAO dao = new InspecaoMaterialDAO(conec);
                if(dao.adicionarControleAmostra(codPesagem)){
                    if(dao.setarAguardandoInspecao(lote,codItem)){
                        conec.commit();
                        db.desconectar();
                        return true;
                    }
                }
                conec.rollback();
                db.desconectar();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    private boolean veririficarControleAmostraPorLote(int numPesagem, String lote, String codItem) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                InspecaoMaterialDAO dao = new InspecaoMaterialDAO(conec);
                if(dao.loteAguardaCadastroAmostra(lote,codItem)){
                    conec.setAutoCommit(false);
                    if(dao.adicionarControleAmostra(numPesagem)){
                        if(dao.setarAguardandoInspecao(lote, codItem)){
                            conec.commit();
                            db.desconectar();
                            return true;
                        }
                    }
                    conec.rollback();
                    db.desconectar();
                    return false;                    
                }else{
                    db.desconectar();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);                        
        }
        return false;
    }

    private boolean veririficarControleAmostraPorLoteTurno(int numPesagem, String lote, String codItem, String codOperador) {
                try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();
                InspecaoMaterialDAO dao = new InspecaoMaterialDAO(conec);
                if(dao.loteAguardaCadastroAmostraTurno(lote,codItem,codOperador)){
                        conec.setAutoCommit(false);
                        if(dao.adicionarControleAmostra(numPesagem)){
                            if(dao.setarAguardandoInspecao(lote, codItem)){
                                conec.commit();
                                db.desconectar();
                                return true;
                            }
                        }
                        conec.rollback();
                        db.desconectar();
                        return false;                    
                }else{
                    db.desconectar();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);                        
        }
        return false;
    }
    
}
