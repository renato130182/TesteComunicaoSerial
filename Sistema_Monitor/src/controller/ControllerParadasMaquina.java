/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ParadasMaquinaDAO;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JTable;
import model.EventoMaquina;
import model.Paradas;
import model.ParadasMaquina;

/**
 *
 * @author renato.soares
 */
public class ControllerParadasMaquina {
    private ParadasMaquina paradasMaquina = new ParadasMaquina();
    private long ultimoEvento;
    public ControllerParadasMaquina(String cod_Maquina) {
        ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO();
        this.paradasMaquina = daoPar.buscaParadasmaquina(cod_Maquina);
    }
    
    public List<String> buscaListaParadasDescricao(){
        try {                    
            List<Paradas> listaParadas = new ArrayList<>();
            List<String> paradasDescricao = new ArrayList<>();
            listaParadas = paradasMaquina.getListaParadas();
            for (int i=0;i<listaParadas.size();i++){
                paradasDescricao.add(String.valueOf(listaParadas.get(i).getCodigo()) + " - "
                        + listaParadas.get(i).getDescricao());
            }
            return paradasDescricao;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> buscaInfoParadaPorCodigo(int codParada){
        try {                    
            List<String> infoParada = new ArrayList<>();
            List<Paradas> listaParadas = new ArrayList<>();
            listaParadas = paradasMaquina.getListaParadas();
            infoParada.add(String.valueOf(listaParadas.get(codParada).getCodigo()));
            infoParada.add(listaParadas.get(codParada).getAbreviacao());
            infoParada.add(listaParadas.get(codParada).getDescricao());
            return infoParada;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registraInicioParadamaquina(long metragem, String cod_maquina){
        try {
            ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO();      
            if(daoPar.buscarIDEventoAberto(cod_maquina)==0){
                EventoMaquina evt = new EventoMaquina();
                evt.setCod_maquina(cod_maquina);
                evt.setMetragem(metragem);            
                return  daoPar.incluirInicioEventoMaquina(evt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean registraRetornoParadamaquina(long metragem, String cod_maquina){
        try {
            ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO();      
            EventoMaquina evt = new EventoMaquina();            
            evt.setMetragem(metragem);            
            evt.setIdEvento(daoPar.buscarIDEventoAberto(cod_maquina));
            this.ultimoEvento = evt.getIdEvento();
            return  daoPar.RegistrarRetornoEventoMaquina(evt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getUltimoEvento() {
        return ultimoEvento;
    }
    
    public boolean registraMotivoParadaMaquina(String codigo,String obs){
        try {
            ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO();
            return daoPar.incluirMotivoEventoMaquina(Long.valueOf(codigo), ultimoEvento,obs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public ParadasMaquina buscaParadasProcessoAtual(String cod_maquina){
        try {                    
            ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO();
            ParadasMaquina par = daoPar.buscaParadasMaquinaProducaoAtual(cod_maquina);
            return par;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;               
    }
        
}
