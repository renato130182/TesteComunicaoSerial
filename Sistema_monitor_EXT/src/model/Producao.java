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
    private long metragemProduzida;
    private String carretelSaida;
    
    public Producao() {
        loteProducao = "";
        itemProducao = "";
        metragemProduzida =0;
        carretelSaida = "";
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

    public String getCarretelSaida() {
        return carretelSaida;
    }

    public void setCarretelSaida(String carretelSaida) {
        this.carretelSaida = carretelSaida;
    }

    public long getMetragemProduzida() {
        return metragemProduzida;
    }

    public void setMetragemProduzida(long metragemProduzida) {
        this.metragemProduzida = metragemProduzida;
    }
    
    
    
}
