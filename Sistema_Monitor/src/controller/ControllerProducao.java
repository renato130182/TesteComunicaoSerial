/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author renato.soares
 */
public class ControllerProducao {
    private List<Long> listaMetragemObservacao = new ArrayList<Long>();

    public List<Long> getListaMetragemObservacao() {
        return listaMetragemObservacao;
    }

    public void setListaMetragemObservacao(List<Long> listaMetragemObservacao) {
        this.listaMetragemObservacao = listaMetragemObservacao;
    }
    
    public void AddicionarMetragensObservacao(String obs, Long metragemOperador){
        Long metros;
        String lista[] = obs.trim().split(" ");
        for (int i=0;i<lista.length;i++){
            try {
                lista[i] = lista[i].replace(".", "");
                lista[i] = lista[i].replace(",", "");
                metros = Long.parseLong(lista[i]);                               
                metros = metragemOperador - metros;
                listaMetragemObservacao.add(metros);
            } catch (NumberFormatException e){
            }
        }
        listaMetragemObservacao.sort(null);        
    }        
}
