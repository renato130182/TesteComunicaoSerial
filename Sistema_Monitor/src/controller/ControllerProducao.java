/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ProducaoDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ComposicaoCobre;
import model.Pesagem;
import model.ReservaPesagem;

/**
 *
 * @author renato.soares
 */
public class ControllerProducao {
    private List<String> listaMetragemObservacao = new ArrayList<>();
    LogErro erro = new LogErro();
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
                if(ControllerUtil.SoTemNumeros(lista[i])){
                    metros = Long.parseLong(lista[i]);                               
                    //metros = metragemOperador - metros;  descomentar para inverssão de metragens no aviso de eventos do carretel de entrada
                    dado = String.valueOf(metros) + "#" + codEmbalagem;                
                    listaMetragemObservacao.add(dado);
                }
            } catch (NumberFormatException e){
                erro.gravaErro(e);
            }
        }
        listaMetragemObservacao.sort(null);        
    }
    
    
    public boolean atualizaMetragemProduzida(List<Pesagem> lista, double metragemProd, String cod_maquina){
        try {                    
            ProducaoDAO daoProd = new ProducaoDAO();
            if(daoProd.atualizaMetragemProduzida(cod_maquina, String.valueOf(metragemProd))){
                for (int i=0;i<lista.size();i++){
                    if(!daoProd.atualizaSaldoConsumoEntrada(lista.get(i).getCodigo(),String.valueOf(metragemProd))) return false;
                }
            }else{
                return false;
            }
            return true;
        } catch (Exception e) {
            erro.gravaErro(e);
            return false;
        }        
    }    
    
    public List<ComposicaoCobre> montaComposicaoCobre(List<ReservaPesagem> res, int metragemProduzida, int qtosFios){
        try {
            ProducaoDAO dao = new ProducaoDAO();
            List<ComposicaoCobre> compCobre = new ArrayList<>();
            List<ComposicaoCobre> cobre = new ArrayList<>();
            List <Double> percMetragem = new ArrayList<>();
            for(int i=0;i<res.size();i++){
                if(res.get(i).getIdMatPrima()!=0){
                    cobre = dao.buscaComposicaoCobrePesagem(res.get(i).getIdMatPrima());
                }
                for(int j=0;j<cobre.size();j++){
                    compCobre.add(cobre.get(j));
                }
            }
            for(int i=0;i<res.size();i++){
                double perc = res.get(i).getQuantidade()/metragemProduzida;
                percMetragem.add(perc);
            }
            for (int i=0;i<res.size();i++){
                if(res.get(i).getIdMatPrima()!=0){
                    for(int j=0; j<compCobre.size();j++){
                        if(compCobre.get(j).getIdPesagem()==res.get(i).getIdMatPrima()){
                            compCobre.get(j).setPorcentagem(compCobre.get(j).getPorcentagem()*percMetragem.get(i));
                        }
                    }
                }
            }            
            Map<String,Double> laminadoras = new HashMap<>();
            for(int i=0;i<compCobre.size();i++){
                if(laminadoras.containsKey(compCobre.get(i).getLaminadora())){
                    laminadoras.put(compCobre.get(i).getLaminadora(),
                            laminadoras.get(compCobre.get(i).getLaminadora())+compCobre.get(i).getPorcentagem());
                }else{
                    laminadoras.put(compCobre.get(i).getLaminadora(),compCobre.get(i).getPorcentagem());
                }
            }
            List<ComposicaoCobre> compFinal = new ArrayList<>();
            for (Map.Entry<String, Double> entry : laminadoras.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();
                System.out.println(key + "    " + val);
                ComposicaoCobre resComp = new ComposicaoCobre();
                resComp.setLaminadora((String) key);
                resComp.setPorcentagem(((double) val)/qtosFios);
                compFinal.add(resComp);                                                
            }
            return compFinal;
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;        
    }
}
