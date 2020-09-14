/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.ContadorMetros;
import model.Metrador;
import model.Producao;

/**
 *
 * @author renato.soares
 */
public class ControllerContadorMetros {
    LogErro erro = new LogErro();
    private final double  pulsos_pro_metro;
    
    public ControllerContadorMetros(double pulsos_pro_metro ) {
        this.pulsos_pro_metro = pulsos_pro_metro;
    }
            
    public ContadorMetros setarDadosLeitura(String dados,ContadorMetros contMet,String codMaquina){
        try {
            if(contMet==null)contMet = new ContadorMetros();
            Metrador met = new Metrador();
            met = setarDadosMetrador(dados);
            
            contMet.setMetrador(met);
            long pulsos = met.getPulsos1()-contMet.getLastPuls();   
            contMet.setLastPuls(met.getPulsos1());
            contMet.setTime(System.currentTimeMillis()-contMet.getLastTime());
            contMet.setLastTime(System.currentTimeMillis());
            if(pulsos>0){
                contMet.setMetragemProduzida(pulsos/pulsos_pro_metro);
            }else{
                contMet.setMetragemProduzida(0);
            }
            ControllerProducao prod = new ControllerProducao();
            Producao prd = new Producao();
            prd = prod.buscaDadosMaquinaProducao(codMaquina);
            if(prd!=null){
                contMet.setMetragemAtual(prd.getMetragemProduzida()+contMet.getMetragemProduzida());
            }else{
                contMet.setMetragemAtual(contMet.getMetragemAtual()+contMet.getMetragemProduzida());
            }            
            return contMet;
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }

    private Metrador setarDadosMetrador(String dados) {
        try {
            String[] valores = dados.split(";");
            if(valores.length==6){
                Metrador met = new Metrador();
                for (String v : valores){
                    String tmp[] = v.split(":");
                    switch(tmp[0]){
                        case ("Pulsos1"):
                            met.setPulsos1(Integer.valueOf(tmp[1]));
                            break;
                        case("Pulsos2"):
                            met.setPulsos2(Integer.valueOf(tmp[1]));
                            break;
                        case ("Digital1"):
                            met.setDigital1(Integer.valueOf(tmp[1]));
                            break;
                        case ("Digital2"):
                            met.setDigital2(Integer.valueOf(tmp[1]));
                            break;
                        case ("Digital3"):
                            met.setDigital3(Integer.valueOf(tmp[1]));
                            break;
                        case ("Digital4"):
                            met.setDigital4(Integer.valueOf(tmp[1]));
                            break;
                    }
                }
                return met;
            }            
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
}
