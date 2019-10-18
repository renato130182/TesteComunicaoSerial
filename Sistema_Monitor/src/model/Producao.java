/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author renato.soares
 */
public class Producao {
    private  String loteProducao;
    private String itemProducao;
    private String metragemProduzida;
    public Producao() {
    }

    public String getLoteProducao() {
        return loteProducao;
    }

    public void setLoteProducao(String loteProducao) {
        this.loteProducao = loteProducao;
    }

    public String getItemProducao() {
        return itemProducao;
    }

    public void setItemProducao(String itemProducao) {
        this.itemProducao = itemProducao;
    }

    public String getMetragemProduzida() {
        return metragemProduzida;
    }

    public void setMetragemProduzida(String metragemProduzida) {
        this.metragemProduzida = metragemProduzida;
    }
    
    
    
}
