/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import dao.ConexaoDatabase;
import dao.ParadasMaquinaDAO;
import dao.ProducaoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import model.ComposicaoCobre;
import model.EventoMaquina;
import model.Maquina;
import model.ParadasMaquina;
import model.Pesagem;
import model.Producao;
import model.ProgramacaoMaquina;
import model.ReservaPesagem;
import model.Usuario;

/**
 *
 * @author renato.soares
 */
public class ControllerProducao {
    private List<String> listaMetragemObservacao = new ArrayList<>();
    LogErro erro = new LogErro();
    private int codPesagem=0;
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
                ProducaoDAO daoProd = new ProducaoDAO(conec);
                if(daoProd.atualizaMetragemProduzida(cod_maquina, String.valueOf(metragemProd))){
                    for (int i=0;i<lista.size();i++){
                        if(!daoProd.atualizaSaldoConsumoEntrada(lista.get(i).getCodigo(),String.valueOf(metragemProd))) {
                            db.desconectar();
                            return false;
                        }
                    }
                }
                db.desconectar();                            
            }            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
            return false;
        }        
    }    
    
    public List<ComposicaoCobre> montaComposicaoCobre(List<ReservaPesagem> res, int metragemProduzida, int qtosFios){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                           
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

    public boolean registrarApontamentoPesagem(Producao prod, int perdaEstimada, List<Usuario> usr, 
            Maquina maquina, ProgramacaoMaquina prog,String obsPesagem,List<ReservaPesagem> resPes,
            ParadasMaquina paradas,List<ComposicaoCobre> compCobre,Login login) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                   
                conec.setAutoCommit(false);
                ProducaoDAO dao = new ProducaoDAO(conec);
                List<String> dadosQuery = new ArrayList<>();
                dadosQuery = montarDadosQueryPesagem(prod, perdaEstimada, usr, maquina, prog, obsPesagem, dao);
                if(dadosQuery!=null){
                    if(dadosQuery.size()==25){
                        this.codPesagem = dao.registraDadosPesagem(dadosQuery);
                        if(this.codPesagem!=0){
                            if(registrarReservaPesagem(resPes, dao)){
                                if(resgistrarParadasPesagem(paradas,prod,dao)){
                                    if(registrarComposicaoCobrePesagem(compCobre,dao)){
                                        if(verificarDadosInspecaoAmostra(login,prod,conec)){
                                            if(dao.registrarApontamentoLogSistemaMonitor(maquina.getCodigo(),this.codPesagem)){
                                                ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec); 
                                                EventoMaquina evt = new EventoMaquina();
                                                evt.setCod_maquina(maquina.getCodigo());
                                                evt.setMetragemEvento(prod.getMetragemProduzida()); 
                                                evt.setIdEvento(daoPar.buscarIDEventoAberto(maquina.getCodigo()));
                                                //ControllerParadasMaquina ctrParadas = new ControllerParadasMaquina(maquina.getCodigo());
                                                if(daoPar.RegistrarRetornoEventoMaquina(evt)){
                                                //if(ctrParadas.registraRetornoParadamaquina(prod.getMetragemProduzida(),maquina.getCodigo())){
                                                    if(dao.registrarApontamentosMaquinaEvento(this.codPesagem,maquina.getCodigo())){
                                                        evt.setMetragemEvento(0);
                                                        if(daoPar.incluirInicioEventoMaquina(evt)){
                                                        //if(ctrParadas.registraInicioParadamaquina(0, maquina.getCodigo())){
                                                            if(dao.limparTabelaMaquinaProducao(maquina.getCodigo())){
                                                                System.out.println("Finalmente apontada"); 
                                                                conec.commit();
                                                                db.desconectar();
                                                                return true;                                                                       
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }                        
                    }
                }
                conec.rollback();
                db.desconectar();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    private List<String> montarDadosQueryPesagem (Producao prod, int perdaEstimada, List<Usuario> usr, 
            Maquina maquina, ProgramacaoMaquina prog,String obsPesagem,ProducaoDAO dao){
        try {                    
            List<String> dadosQuery = new ArrayList<>();
            dadosQuery.add(prod.getLoteProducao() + prod.getCarretelSaida() + "0");
            String[] tmp =  dao.buscaDataHoraFimProducao(maquina.getCodigo());
            for (int i=0;i<tmp.length;i++){
                dadosQuery.add(tmp[i]);
            }
            dadosQuery.add(usr.get(0).getCodigoOperador());
            dadosQuery.add(prod.getCarretelSaida());                               
            String codOperador1 = usr.get(0).getCodigoOperador();
            String codEncarregado1 = usr.get(0).getCodigoEncarregado();
            String codOperador2="";
            String codEncarregado2="";
            int metTrocaTurno=0;
            for (int i=0;i<usr.size();i++){
                if(!codOperador1.equals(usr.get(i).getCodigoOperador())){
                    codOperador2=usr.get(i).getCodigoOperador();
                    codEncarregado2=usr.get(i).getCodigoEncarregado();
                    metTrocaTurno=usr.get(i).getMetProduzida();
                    break;
                }
            }
            dadosQuery.add(codOperador1);
            dadosQuery.add(codEncarregado1);
            dadosQuery.add(codOperador2);
            dadosQuery.add(codEncarregado2);
            dadosQuery.add(maquina.getCodigo());
            dadosQuery.add(prod.getItemProducao());
            dadosQuery.add(String.valueOf(prod.getMetragemProduzida()));
            dadosQuery.add(String.valueOf(prod.getMetragemProduzida()));
            dadosQuery.add(String.valueOf(metTrocaTurno));
            dadosQuery.add(prod.getLoteProducao());
            dadosQuery.add(String.valueOf(prog.getQtdfiosSaida()));
            dadosQuery.add(buscaTempoGastoProducao(maquina.getCodigo()));
            dadosQuery.add(dao.buscaTipoItemProducao(prod.getItemProducao()));
            dadosQuery.add(obsPesagem);
            dadosQuery.add(buscaTempoTotalParadas(maquina.getCodigo()));
            dadosQuery.add(String.valueOf(prod.getMetragemProduzida()));
            dadosQuery.add(String.valueOf(prod.getMetragemProduzida()));
            dadosQuery.add(ControllerUtil.buscaMacAdrres());
            dadosQuery.add(prog.getLaminadora());
            dadosQuery.add(String.valueOf(perdaEstimada));
            return dadosQuery;
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    }
    private String buscaTempoGastoProducao(String codMaquina) {
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                         
                ParadasMaquinaDAO daoParadas = new ParadasMaquinaDAO(conec);
                List<EventoMaquina> evt = daoParadas.buscaTempoMetragemEventosApontamento(codMaquina);
                db.desconectar();
                if(evt!=null){
                    if(evt.size()>1){
                        long tempoProd=0;
                        for(int i=0;i<evt.size()-1;i++){
                            String tmpInicio = evt.get(i).getDataHoraFinal();
                            String tmpFinal = evt.get(i+1).getDataHoraInicio();
                            tempoProd= tempoProd + ControllerUtil.calculaTempoPercorridoSegundos(tmpInicio, tmpFinal);
                        }
                        
                        return String.valueOf(tempoProd/60);
                    }   
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "0";
    }

    private String buscaTempoTotalParadas(String codMaquina) {
        try {
             ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();                         
                ParadasMaquinaDAO daoParadas = new ParadasMaquinaDAO(conec);
                List<EventoMaquina> evt = daoParadas.buscaTempoMetragemEventosApontamento(codMaquina);
                db.desconectar();
                if(evt!=null){
                    if(evt.size()>1){
                        long tempoProd=0;
                        String tmpInicio;
                        String tmpFinal ;
                        for(int i=0;i<evt.size();i++){
                            tmpInicio = evt.get(i).getDataHoraInicio();
                            if(evt.size()-1 == i){
                                tmpFinal = ControllerUtil.buscaDataHoraAtualBD();
                            }else{
                                tmpFinal = evt.get(i).getDataHoraFinal();
                            }
                            tempoProd= tempoProd + ControllerUtil.calculaTempoPercorridoSegundos(tmpInicio, tmpFinal);
                        }
                        
                        return String.valueOf(tempoProd/60);
                    }   
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return "0";
    }

    public boolean registrarReservaPesagem(List<ReservaPesagem> reservaPesagem, ProducaoDAO dao) {
        try {
            for (int i=0;i<reservaPesagem.size();i++){
                reservaPesagem.get(i).setIdPesagem(this.codPesagem);
                if(!dao.registrarDadosReservaPesagem(reservaPesagem.get(i))){
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
            return false;
        }
        return true;
    }

    private boolean resgistrarParadasPesagem(ParadasMaquina paradas, Producao prod, ProducaoDAO dao) {
        try {
            String dataParada = "0000-00-00";
            int idEvento=0,tempoParada=0,qtdEvento=0;
            int tempoMotivo=0;
            ConexaoDatabase db = new ConexaoDatabase();
            if(!db.isInfoDB()) return false;
            Connection conec = db.getConnection();                              
            ParadasMaquinaDAO daoPar = new ParadasMaquinaDAO(conec);
            
            if(paradas.getListaParadas().size()>0){
                
                for (int i=0;i<paradas.getListaParadas().size();i++){
                    if(idEvento!=paradas.getListaParadas().get(i).getIdRegistro()){
                        List<String> dados =  daoPar.buscaDataHoraTempoEventoMaquinaPorID(paradas.getListaParadas()
                                .get(i).getIdRegistro());
                        dataParada = dados.get(0);
                        tempoParada= Integer.valueOf(dados.get(2));
                        qtdEvento=Integer.valueOf(dados.get(3));
                        tempoMotivo = (tempoParada/qtdEvento)/60;
                        if(tempoMotivo==0) tempoMotivo=1;        
                        idEvento=paradas.getListaParadas().get(i).getIdRegistro();
                    }
                    List<String> dadosParada = new ArrayList<>();
                    dadosParada.add(prod.getLoteProducao()+ prod.getCarretelSaida()+ "0");
                    dadosParada.add(String.valueOf(i+1));
                    dadosParada.add(prod.getItemProducao());
                    dadosParada.add(paradas.getCod_maquina());
                    dadosParada.add(String.valueOf(paradas.getListaParadas().get(i).getCodigo()));
                    dadosParada.add(dataParada);
                    dadosParada.add("0");
                    dadosParada.add(String.valueOf(tempoMotivo));
                    if(paradas.getListaParadas().get(i).getObservacao()==null){
                        dadosParada.add("");
                    }else{
                        dadosParada.add(paradas.getListaParadas().get(i).getObservacao());
                    }
                    dadosParada.add(String.valueOf(this.codPesagem));
                    if(!dao.resgistraParadaPesagem(dadosParada)){
                        db.desconectar();
                        return false;
                    }
                }
                db.desconectar();
            }else{
                db.desconectar();
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            erro.gravaErro(e);
            return false;
        }
        return true;
    }

    private boolean registrarComposicaoCobrePesagem(List<ComposicaoCobre> compCobre,ProducaoDAO dao) {
        try {
            if(compCobre.size()>0){
                for(int i=0;i<compCobre.size();i++){
                    List<String> dados = new ArrayList<>();
                    dados.add(String.valueOf(compCobre.get(i).getIdPesagem()));
                    dados.add(String.valueOf(compCobre.get(i).getPorcentagem()));
                    dados.add(compCobre.get(i).getLaminadora());
                    if(!dao.resgistraComposicaoCobre(dados)){                        
                        return false;
                    }
                }
            }else{
                return false;
            }            
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
            return false;
        }
        return true;
    }
    
    private boolean verificarDadosInspecaoAmostra(Login login,Producao prod,Connection conec){      
        ControllerInspecaoMaterial ctrInsp = new ControllerInspecaoMaterial();
        int tipoInspecao = ctrInsp.buscaTipoInspecaoItem(prod.getItemProducao());
        if(tipoInspecao!=99){
            if(ctrInsp.validaRegistroAmostra(tipoInspecao,this.codPesagem,prod.getLoteProducao(),
                    prod.getItemProducao(),login.getCodigoOperador(),conec)){                
                System.out.println("registros de inspeção OK!!!");
                return true;
            }else{                
                System.out.println("Falha ao validar registro de inspeção");
                return false;
            }                
        }else{
            System.out.println("Falha ao buscar tipo de inspeção");
            return false;            
        }
    }
        
}
