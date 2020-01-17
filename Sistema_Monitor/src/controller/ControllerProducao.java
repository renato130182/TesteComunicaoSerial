/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.ProducaoDAO;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ComposicaoCobre;
import model.Pesagem;
import model.Producao;
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
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection(); 
                ProducaoDAO daoProd = new ProducaoDAO(conec);
                if(daoProd.atualizaMetragemProduzida(cod_maquina, String.valueOf(metragemProd))){
                    for (int i=0;i<lista.size();i++){
                        if(!daoProd.atualizaSaldoConsumoEntrada(lista.get(i).getCodigo(),String.valueOf(metragemProd))) {
                            db.desconectar();
                            return false;
                        }
                    }
                }else{
                    db.desconectar();
                    return false;
                }
            }            
            return true;
        } catch (Exception e) {
            erro.gravaErro(e);
            return false;
        }        
    }    
    
    public List<ComposicaoCobre> montaComposicaoCobre(List<ReservaPesagem> res, int metragemProduzida, int qtosFios){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
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
                db.desconectar();
                return compFinal;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;        
    }
    
    public Producao buscaDadosMaquinaProducao(String codMaquina){        
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                Producao prod = dao.buscaItemProducao(codMaquina);
                db.desconectar();
                return prod;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;    
    }
    
    public boolean atualizaCarretelSaida(String carretelSaida,String codMaquina){        
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                boolean tmp = dao.atualizaCarretelSaida(carretelSaida,codMaquina);
                db.desconectar();
                return tmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;    
    }
    
    public boolean verificaItemAlongamento(String codItem){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                boolean alonga = dao.validaItemAlongamento(codItem);
                db.desconectar();
                return alonga;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }

    public Object buscaMetragemProduzida(String loteProducao, String itemProducao) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                Long metragem = dao.buscaMetragemProduzida(loteProducao,itemProducao);
                db.desconectar();
                return metragem;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;    
    }

    public int buscaFatorPerdaItem(String itemProducao) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                int fator = dao.buscaFatorPerdaItem(itemProducao);
                db.desconectar();
                return fator;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public int buscaPerdaEstimadaMateriaPrima(List<ReservaPesagem> reservaPesagem) {
        int tmpPerda=0;
        int perda=0;
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                for(int i=0;i<reservaPesagem.size();i++){
                    if(reservaPesagem.get(i).getIdMatPrima()!=0){
                        tmpPerda=dao.buscaPerdaProcessualCodPesagem(reservaPesagem.get(i).getIdMatPrima());
                        if(perda<tmpPerda)perda=tmpPerda;
                    }
                }
                db.desconectar();
                return perda;
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return 0;
    }

    public boolean RegistrarApontamentoFichaControle(int codigoProgramacao, int codPesagem) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                
                conec = db.getConnection();            
                ProducaoDAO dao = new ProducaoDAO(conec);
                int ficha = dao.buscaMenorFichaAberto(codigoProgramacao);
                if(ficha!=0){
                    if(dao.marcarFichaApontada(codPesagem,ficha)){
                        db.desconectar();
                        return true;
                    }else{
                        db.desconectar();
                        return false;
                    }
                }
                db.desconectar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return true;
    }
}
