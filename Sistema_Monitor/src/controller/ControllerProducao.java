/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ProducaoDAO;
import java.util.ArrayList;
import java.util.List;
import model.Pesagem;

/**
 *
 * @author renato.soares
 */
public class ControllerProducao {
    private List<String> listaMetragemObservacao = new ArrayList<>();

    public List<String> getListaMetragemObservacao() {
        return listaMetragemObservacao;
    }

    public void setListaMetragemObservacao(List<String> listaMetragemObservacao) {
        this.listaMetragemObservacao = listaMetragemObservacao;
    }
    
    public void AddicionarMetragensObservacao(String obs, Long metragemOperador, String codEmbalagem){
        long metros;
        String dado;
        String lista[] = obs.trim().split(" ");
        for (int i=0;i<lista.length;i++){
            try {
                lista[i] = lista[i].replace(".", "");
                lista[i] = lista[i].replace(",", "");
                if(SoTemNumeros(lista[i])){
                    metros = Long.parseLong(lista[i]);                               
                    //metros = metragemOperador - metros;  descomentar para inverssão de metragens no aviso de eventos do carretel de entrada
                    dado = String.valueOf(metros) + "#" + codEmbalagem;                
                    listaMetragemObservacao.add(dado);
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        listaMetragemObservacao.sort(null);        
    }
    public boolean SoTemNumeros(String texto) { 
        if(texto.length()==0)return false;
        for (int i = 0; i < texto.length(); i++) { 
            if (!Character.isDigit(texto.charAt(i))) { 
                return false; 
            } 
        } return true; 
    }
    
    public boolean atualizaMetragemProduzida(List<Pesagem> lista, double metragemProd, String cod_maquina){
        ProducaoDAO daoProd = new ProducaoDAO();
        if(daoProd.atualizaMetragemProduzida(cod_maquina, String.valueOf(metragemProd))){
            for (int i=0;i<lista.size();i++){
                if(!daoProd.atualizaSaldoConsumoEntrada(lista.get(i).getCodigo(),String.valueOf(metragemProd))) return false;
            }
        }else{
            return false;
        }
        return true;
    }
    
    
}
