/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geradorrelatorioindisponibilidademanutencao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author renato.soares
 */
public class GeradorRelatorioIndisponibilidadeManutencao {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        System.out.println("Conectando a base de dados Manutenção.");  
        Thread.sleep(1000);
        try {
            Connection conn = ConexaoAcess.obterConexao();
            if(null==conn){
                System.out.println("Falha ou conectar com banco de dados");
                Thread.sleep(50000);
                return;
            }
            System.out.println("Conectado ao banca de dados Access com sucesso.");
            Thread.sleep(1000);
            buscarDadosManutencao(conn);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Falha ou conectar com banco de dados");
            Thread.sleep(50000);
            return;
        }
                        
    }
    
    private static void buscarDadosManutencao(Connection conn){
        try {                    
            DaoManumencao daoMan = new DaoManumencao(conn);            
            System.out.println("Buscando manutenções que não terminarão no mesmo dia.");
            List<Manutencao> listaManut = new ArrayList<>();                        
            listaManut = daoMan.buscaListaManutencoes();
            if(listaManut.size()>0){
                System.out.println("Lista carregada com sucesso!");
                Thread.sleep(1000);
                List<Manutencao> listaManutPrev = new ArrayList<>();
                listaManutPrev = listarManutencaoPreventiva(listaManut);
                List<Manutencao> listaManutCor = new ArrayList<>();
                listaManutCor = listarManutencaoCorretivas(listaManut);
                listaManutPrev = calcularTempoManutencao(listaManutPrev);
                listaManutCor = calcularTempoManutencao(listaManutCor);
                Connection conMysql = ConexaoMysql.obterConexao();
                DaoManumencao dao = new DaoManumencao(conMysql);
                if(dao.registrarListaManut(listaManutCor,"man_corretiva")){
                    System.out.println("Manutenções corretivas cadastradas com sucesso!!");
                }else{
                    System.out.println("Falha ao cadastrar manutenções corretivas!!");
                }
                if(dao.registrarListaManut(listaManutPrev,"man_preventivas")){
                    System.out.println("Manutenções preventivas cadastradas com sucesso!!");
                }else{
                    System.out.println("Falha ao cadastrar manutenções preventivas!!");
                }
                Thread.sleep(2000);
            }else{
                System.out.println("Não foram encontradas manuteções.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(GeradorRelatorioIndisponibilidadeManutencao.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private static List<Manutencao> listarManutencaoPreventiva(List<Manutencao> listaManut) {
        List<Manutencao> listaManutPrev = new ArrayList<>();
        try {
            for(int i=0;i<listaManut.size();i++){
                if(listaManut.get(i).getCodTipTrb().substring(0,2).equalsIgnoreCase("A1") ||
                        listaManut.get(i).getCodTipTrb().substring(0,2).equalsIgnoreCase("A2")){
                    listaManutPrev.add(listaManut.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return listaManutPrev;
    }

    private static List<Manutencao> listarManutencaoCorretivas(List<Manutencao> listaManut) {
        List<Manutencao> listaManutCor = new ArrayList<>();
        try {
            for (int i=0;i<listaManut.size();i++){
                if(listaManut.get(i).getCodTipTrb().substring(0,2).equalsIgnoreCase("C1") ||
                        listaManut.get(i).getCodTipTrb().substring(0,2).equalsIgnoreCase("B2")){
                    listaManutCor.add(listaManut.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaManutCor;
    }

    private static List<Manutencao> calcularTempoManutencao(List<Manutencao> lista)  {
        List<Manutencao> listaManut = new ArrayList<>();
        try {
            Connection conn = ConexaoMysql.obterConexao();
            DaoManumencao dao = new DaoManumencao(conn);
            try {
                for (int i=0;i<lista.size();i++){
                    if(dao.validaMaquinaProducao(lista.get(i).getCodObjeto())){
                        if(lista.get(i).getDatFimClb().equals(lista.get(i).getDatIniClb())){
                            System.out.println("Manutenção Intra Day");
                            long tempo = lista.get(i).getHoraFim().getTime()- lista.get(i).getHoraInicio().getTime();
                            System.out.println("Tempo em manutenção: " + String.valueOf(tempo));
                            lista.get(i).setTempoManutencao(tempo/60000);
                            listaManut.add(lista.get(i));
                        }else{
                            System.out.println("Manutenção Extra Day");
                            List<Time> horario = new ArrayList<>();
                            long dias = intervaloDeDias(lista.get(i).getDatIniClb(),lista.get(i).getDatFimClb());
                            if(dias>1){
                                horario = dao.buscaHorarioTurno(lista.get(i).getDatIniClb(),
                                        lista.get(i).getCodObjeto());
                                if(horario.size()>0){
                                    listaManut.add(setarDadosManutencao(lista.get(i),lista.get(i).getHoraFim(),
                                            horario.get(1),lista.get(i).getDatIniClb()));
                                    System.out.println("CodOrd: " + lista.get(i).getCodOrdClb());
                                    long dia = lista.get(i).getDatIniClb().getTime();
                                    for(long j=1;j<dias;j++){
                                        dia = dia + 86400000L;  
                                        Date data = new Date(dia);
                                        System.out.println("Data adicionada: " + String.valueOf(data));
                                        listaManut.add(setarDadosManutencao(lista.get(i), horario.get(0),
                                                horario.get(1), data));                                       
                                    }
                                    listaManut.add(setarDadosManutencao(lista.get(i),horario.get(0),
                                            lista.get(i).getHoraFim(),lista.get(i).getDatFimClb()));                                
                                }
                            }else{
                                
                                horario = dao.buscaHorarioTurno(lista.get(i).getDatIniClb(),
                                        lista.get(i).getCodObjeto());
                                if(horario.size()>0){
                                    System.out.println("CodOrd: " + lista.get(i).getCodOrdClb());                                    
                                    listaManut.add(setarDadosManutencao(lista.get(i),lista.get(i).getHoraFim(),
                                            horario.get(1),lista.get(i).getDatIniClb()));                        
                                    listaManut.add(setarDadosManutencao(lista.get(i),horario.get(0),
                                            lista.get(i).getHoraFim(),lista.get(i).getDatFimClb()));
                                    
                                }                            
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException ex) {
            try {
                ex.printStackTrace();
                System.out.println("Falha ao realizar conexão");
                Thread.sleep(5000);
                System.exit(0);
            } catch (InterruptedException ex1) {
                ex1.printStackTrace();
                System.out.println("Erro durante execução de interrupção sleep");
            }
        }                
        return listaManut;
    }
    
    private static Manutencao setarDadosManutencao(Manutencao man, Time hIni, Time hFim, Date data){
        Manutencao dados = new Manutencao();       
        dados.setCodObjeto(man.getCodObjeto());
        dados.setCodOrdClb(man.getCodOrdClb());
        dados.setCodTipTrb(man.getCodTipTrb());
        dados.setDatFimClb(man.getDatFimClb());
        dados.setDatIniClb(data);
        dados.setHoraInicio(hIni);
        dados.setHoraFim(hFim);
        dados.setTempoManutencao((dados.getHoraFim().getTime()-dados.getHoraInicio().getTime())/60000);
        return dados;
    }
    
    private static long intervaloDeDias(Date inicio, Date Fim){
        try {                     
            long dt = (Fim.getTime() - inicio.getTime()) + 3600000;      
            long dias = (dt / 86400000L);  
            return dias;
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return 0;
    }    
}
