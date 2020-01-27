/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ReservaPesagemDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ReservaPesagem;

/**
 *
 * @author renato.soares
 */
public class ControllerReservaPesagem {
    LogErro erro = new LogErro();
    
    public List<ReservaPesagem> buscaConsumoCobreProducaoAtual(List<ReservaPesagem> res,String codmaquina){
        try {
            List <ReservaPesagem> resCobre = new ArrayList<>();
             ConexaoDatabase db = new ConexaoDatabase();            
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec.setAutoCommit(false);
                ReservaPesagemDAO dao = new ReservaPesagemDAO(conec);
                resCobre=dao.buscaConsumoCobreReservaPesagem(codmaquina);
                if(resCobre != null){
                    boolean trocado =false;
                    
                    String codigoPesagem = res.get(0).getCodigoPesagem();
                    for (int i=resCobre.size()-1;i>=0;i--){                                              
                       double metragemTroca=0;
                       for (int j=0;j<resCobre.size();j++){
                           if(resCobre.get(i).getCodigoEmbalagem().equals(resCobre.get(j).getCodigoEmbalagelTroca())){
                               resCobre.get(i).setQuantidade(resCobre.get(i).getQuantidade()-resCobre.get(j).getQuantidade());
                           }                           
                       }             
                       resCobre.get(i).setCodigoPesagem(codigoPesagem);
                       res.add(resCobre.get(i));
                    }
                    /*
                    for(int i=0;i<resCobre.size();i++){
                        resCobre.get(i).setCodigoPesagem(codigoPesagem);
                        tmp=(int) resCobre.get(i).getQuantidade();
                        resCobre.get(i).setQuantidade(resCobre.get(i).getQuantidade()-metros);
                        metros=tmp;
                        res.add(resCobre.get(i));
                    } 
                */
                }
                db.desconectar();
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
    
    
}
