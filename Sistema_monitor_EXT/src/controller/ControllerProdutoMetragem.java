/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ProdutoCarretelDAO;
import model.Producao;

/**
 *
 * @author renato.soares
 */
public class ControllerProdutoMetragem {
    
    public boolean validaEmbagemProdutoMetragem(Producao prod, String codEmbalagem, String codMaquina){
        if(!prod.getItemProducao().trim().equals("")){
            if(!codMaquina.trim().equals("")){
                ProdutoCarretelDAO dao = new ProdutoCarretelDAO();
                return dao.verificaCarretelProdutoMetragem(codEmbalagem, codMaquina, prod.getItemProducao());
            }
        }
        return false;
    }
}
