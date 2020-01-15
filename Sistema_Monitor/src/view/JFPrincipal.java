/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.pi4j.system.SystemInfo;
import controller.ControllerProducao;
import controller.Login;
import dao.DadosDefaultDAO;
import dao.MaquinaDAO;
import dao.PesagemDAO;
import dao.ProducaoDAO;
import dao.ProdutoMaquinaDAO;
import dao.ProgramacaoMaquinaDAO;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DadosConexao;
import model.Maquina;
import model.Pesagem;
import model.Producao;
import model.ProdutoCarretel;
import model.ProdutoMaquina;
import model.ProgramacaoMaquina;
import model.Usuario;
import Serial.*;
import controller.ControllerConfigSerialPort;
import controller.ControllerEventosSistema;
import controller.ControllerMicrometro;
import controller.ControllerParadasMaquina;
import controller.ControllerProdutoMetragem;
import controller.ControllerReservaMaquina;
import controller.ControllerUtil;
import controller.LogErro;
import dao.ProdutoCarretelDAO;
import java.awt.HeadlessException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.table.TableRowSorter;
import model.Micrometro;
import model.Paradas;
import model.ParadasMaquina;

/**
 *
 * @author renato.soares
 */
public class JFPrincipal extends javax.swing.JFrame implements ActionListener {
    private static final String SERIAL_RFID = "rfidserial";
    private static final String SERIAL_MICROMETRO = "micrometroserial";
    private  static String identificador;
    private static String codMaquina;
    private long tempoSistema = System.currentTimeMillis(); 
    private double[] mediaVel = {0,0,0,0,0,0,0,0,0,0};  
    private boolean maqParada = false;
    private boolean evtRegistrado = true;
    private boolean iniciaLeituras = true;
    private int resumoRelatorio,linhas=14;
    private int eventosTimer,qtdEvt=10;
    private List<String> metrosAlerta = new ArrayList<>();
    private boolean evtCarSaida,evtMetProg,evtCarEnt,evtSaldoEnt1,evtSaldoEnt2;
    private boolean evtDiaMin,evtDiaMax;
    private Login login = new Login();
    private Maquina maquina = new Maquina();
    private MaquinaDAO maqDao =  new MaquinaDAO();
    private ProdutoMaquina prodmaq = new ProdutoMaquina();
    private ProgramacaoMaquina prog = new ProgramacaoMaquina();
    private ProdutoCarretel prodCar = new ProdutoCarretel();
    private Producao prod = new Producao();
    private List<Pesagem> listaPesagens = new ArrayList<>();
    private SerialTxRx comRFID ;
    private SerialTxRx comMicrometro;
    private Micrometro leituraAtual = new Micrometro();
    private Micrometro leituraAnterior = new Micrometro();
    private List<Micrometro> relatorio = new ArrayList<>();
    private int metrosRelatorio=0;
    private ControllerMicrometro mic = new ControllerMicrometro();
    private ControllerParadasMaquina paradas;
    private LogErro erro = new LogErro();
    private Timer timerVelocimetro;
    
    /**
     * Creates new form JFPrincipal
     */
    
    private void iniciaTimerVelociametro(){
        timerVelocimetro = new Timer();
    }
    
    private void tarefaVelocidade(){
        int delay = 500;   // delay de 0.5 seg.
        int interval = 1000;  // intervalo de 1 seg.        
        
        timerVelocimetro.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {                                            
                        boolean alerta = false;
                        System.out.println("Enveto Timer");
                        jLabelAlerta.setSize(250,125);
                        if(eventosTimer >= qtdEvt){
                            radialLcdVelocidade.setValueAnimated(mediaVelocidade(0));
                            System.out.println("Parada pelo Timer!!!!");
                            if(!maqParada){                                
                                abrirTelaParadas();                                   
                            }
                            if(!evtRegistrado)evtRegistrado=paradas.registraInicioParadamaquina((long) 
                                displaySingleMetragemCarretel.getValue() , codMaquina);
                            if(evtRegistrado){
                                timerVelocimetro=null;
                                throw new RuntimeException("Forcada");
                            }
                        }else{
                            eventosTimer++;
                        }
                        if(displaySingleEvtCarEntrada.getValue()!=0){
                            if(displaySingleEvtCarEntrada.getValue() <= maquina.getAlertaMetrosParaArrebentamento()){
                                lightBulbAlertaEvento.setOn(true);                            
                                alerta = true;    
                                if(!evtCarEnt){
                                    ControllerEventosSistema ctr = new ControllerEventosSistema();
                                    evtCarEnt = ctr.registraEventos(7,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());                                    
                                }
                            }else{
                                lightBulbAlertaEvento.setOn(false);
                                evtCarEnt = false;                            
                            }                      
                        }else{
                            lightBulbAlertaEvento.setOn(false);
                            evtCarEnt = false;  
                        }
                        if(displaySingleSaldoCarretelEntrada1.getValue()<=maquina.getAlertaMetrosParaArrebentamento()){
                            lightBulbAlertaSaldoEntrada1.setOn(true);
                            alerta = true;
                            if(!evtSaldoEnt1){
                                ControllerEventosSistema ctr = new ControllerEventosSistema();
                                evtSaldoEnt1 = ctr.registraEventos(8,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());
                                if(evtSaldoEnt1)ctr.verificaPreApontamento("2",codMaquina,"",false,Integer.valueOf(listaPesagens.get(0).getCodigo()),0);
                            }
                        }else{
                            lightBulbAlertaSaldoEntrada1.setOn(false);    
                            evtSaldoEnt1 = false;
                        }
                        if(jPanelEventoEntrada3.isShowing()){
                            if(displaySingleSaldoCarretelEntrada2.getValue()<=maquina.getAlertaMetrosParaArrebentamento()){
                                lightBulbAlertaSaldoEntrada2.setOn(true);
                                alerta = true;
                                if(!evtSaldoEnt2){
                                ControllerEventosSistema ctr = new ControllerEventosSistema();
                                evtSaldoEnt2 = ctr.registraEventos(8,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());
                                if(evtSaldoEnt2)ctr.verificaPreApontamento("2",codMaquina,"",false,Integer.valueOf(listaPesagens.get(1).getCodigo()),0);
                            }
                            }else{
                                lightBulbAlertaSaldoEntrada2.setOn(false);
                                evtSaldoEnt2 = false;
                            }
                        }
                        if(displaySingleMetragemCarretel.getValue()>=(Double.valueOf(jLabelProducaoMetCarretel.getText())
                                -maquina.getAlertaMetrosParaArrebentamento())){
                            lightBulbMetragemSolicitada.setOn(true);
                            alerta = true;
                            if(!evtCarSaida){
                                ControllerEventosSistema ctr = new ControllerEventosSistema();
                                evtCarSaida = ctr.registraEventos(9,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());
                                if(evtCarSaida)ctr.verificaPreApontamento("1",codMaquina,"",false,0,0);
                            }
                        }else{
                            lightBulbMetragemSolicitada.setOn(false);
                            evtCarSaida = false;
                        }
                        if(displaySingleMetragemProgramado.getValue()>=(Double.valueOf(jLabelProducaoMetTotalProg.getText())
                                -maquina.getAlertaMetrosParaArrebentamento())){
                            lightBulbMetragemLoteProducao.setOn(true);   
                            alerta = true;
                            if(!evtMetProg){
                                ControllerEventosSistema ctr = new ControllerEventosSistema();
                                evtMetProg = ctr.registraEventos(10,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());
                            }
                        }else{
                            lightBulbMetragemLoteProducao.setOn(false);     
                            evtMetProg = false;
                        }
                        if(alerta){
                            jLabelAlerta.setVisible(true);
                        }else{
                            jLabelAlerta.setVisible(false);
                        }
                    } catch (NumberFormatException e) {
                        erro.gravaErro(e);                    
                    }
                }                
            }, delay, interval);
    }
    
    public JFPrincipal() {        
            try {                    
                
                if(System.getProperty("os.name").equals("Linux")){
                    try {
                        identificador = SystemInfo.getSerial();
                    } catch (IOException | InterruptedException | UnsupportedOperationException ex) {
                        erro.gravaErro(ex);
                    }
                }else{
                    identificador = "ADMINISTRADOR";
                }

                System.out.println("OS. name: " + System.getProperty("os.name"));
                System.out.println("Serial: " + identificador);
                initComponents();

                Canvas logo = new Canvas();
                this.jPLogo.add(logo);
                bloquearMenu();            
                DadosDefaultDAO dados = new DadosDefaultDAO();
                codMaquina = dados.buscaCodigoMaquina(identificador);
                if(codMaquina!=null){                
                    maquina = maqDao.buscarDadosMaquina(codMaquina);
                }
                abrirTelaLogin();
                ControllerEventosSistema ctr = new ControllerEventosSistema();
                ctr.registraEventos(1,"",0,0,codMaquina,"");
            } catch (Exception e) {
                erro.gravaErro(e);
        }
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jpRoot = new javax.swing.JPanel();
        jpProgramacao = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableProgramacao = new javax.swing.JTable();
        jpParadas = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel38 = new javax.swing.JLabel();
        jComboBoxParadasMaquina = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaObsParada = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableMotivosParada = new javax.swing.JTable();
        jButtonIncluirMotivoParada = new javax.swing.JButton();
        jButtonRemoverMotivoParada = new javax.swing.JButton();
        jButtonRegistrarParada = new javax.swing.JButton();
        jpConfigDefault = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtfUserDafaut = new javax.swing.JTextField();
        jpfSenhaDefault = new javax.swing.JPasswordField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jtfDriverProducao = new javax.swing.JTextField();
        jtfServidorProducao = new javax.swing.JTextField();
        jtfUrlProducao = new javax.swing.JTextField();
        jtfBDProducao = new javax.swing.JTextField();
        jtfUsuarioProducao = new javax.swing.JTextField();
        jtfDriverTeste = new javax.swing.JTextField();
        jtfServidorTeste = new javax.swing.JTextField();
        jtfUrlTeste = new javax.swing.JTextField();
        jtfBDTeste = new javax.swing.JTextField();
        jtfUsuarioTeste = new javax.swing.JTextField();
        jbCriarArquivosDefault = new javax.swing.JButton();
        jpfSenhaProducao = new javax.swing.JPasswordField();
        jpfSenhaTeste = new javax.swing.JPasswordField();
        jpProducao = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabelProducaoCodItem = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabelProducaoDescricaoItem = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabelProducaoQtdProg = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabelProducaoQtdProd = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabelProducaoOF = new javax.swing.JLabel();
        jLabelProducaoMetTotalProg = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabelProducaoMetTotalProd = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabelProducaoMetCarretel = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabelProducaoDMinimo = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabelProducaoDnominal = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabelProducaoDMaximo = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabelProducaoVelIdeal = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProducaoArrebentamentos = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableProducaoParadas = new javax.swing.JTable();
        jSeparator7 = new javax.swing.JSeparator();
        radialLcdVelocidade = new eu.hansolo.steelseries.gauges.Radial4Lcd();
        linearCarretelSaida = new eu.hansolo.steelseries.gauges.Linear();
        displaySingleMetragemCarretel = new eu.hansolo.steelseries.gauges.DisplaySingle();
        lightBulbMetragemSolicitada = new eu.hansolo.lightbulb.LightBulb();
        linearProgramacao = new eu.hansolo.steelseries.gauges.Linear();
        displaySingleMetragemProgramado = new eu.hansolo.steelseries.gauges.DisplaySingle();
        lightBulbMetragemLoteProducao = new eu.hansolo.lightbulb.LightBulb();
        jLabelAlerta = new javax.swing.JLabel();
        jPanelEventoEntrada = new javax.swing.JPanel();
        displaySingleEvtCarEntrada = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabelEvtEntrada = new javax.swing.JLabel();
        lightBulbAlertaEvento = new eu.hansolo.lightbulb.LightBulb();
        radialLcdDiametro = new eu.hansolo.steelseries.gauges.Radial4Lcd();
        jPanelEventoEntrada2 = new javax.swing.JPanel();
        displaySingleSaldoCarretelEntrada1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabelCarretel1 = new javax.swing.JLabel();
        lightBulbAlertaSaldoEntrada1 = new eu.hansolo.lightbulb.LightBulb();
        jPanelEventoEntrada3 = new javax.swing.JPanel();
        displaySingleSaldoCarretelEntrada2 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        jLabelCarretel2 = new javax.swing.JLabel();
        lightBulbAlertaSaldoEntrada2 = new eu.hansolo.lightbulb.LightBulb();
        jpLogin = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfUser = new javax.swing.JTextField();
        jpfPassword = new javax.swing.JPasswordField();
        jbLogin = new javax.swing.JButton();
        jPLogo = new javax.swing.JPanel();
        jPasswordFieldCartao = new javax.swing.JPasswordField();
        jLabel36 = new javax.swing.JLabel();
        menuPrincipal = new javax.swing.JMenuBar();
        menuLogin = new javax.swing.JMenu();
        jMenuItemLogin = new javax.swing.JMenuItem();
        jMenuItemSair = new javax.swing.JMenuItem();
        jMenuItemDesligar = new javax.swing.JMenuItem();
        jMenuItemReiniciar = new javax.swing.JMenuItem();
        menuProgramacao = new javax.swing.JMenu();
        jMenuItemProgramacao = new javax.swing.JMenuItem();
        menuParadas = new javax.swing.JMenu();
        jMenuItemParadas = new javax.swing.JMenuItem();
        menuProducao = new javax.swing.JMenu();
        jMenuItemProducao = new javax.swing.JMenuItem();
        menuConfiguracoes = new javax.swing.JMenu();
        jMenuConfigDefault = new javax.swing.JMenuItem();
        jMenuTrocarMaquina = new javax.swing.JMenuItem();
        jMenuConfigSerialRFID = new javax.swing.JMenuItem();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Sistema Condumig");
        setExtendedState(JFPrincipal.MAXIMIZED_BOTH);
        setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        setName("framePrincipal"); // NOI18N
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        jpRoot.setLayout(new java.awt.CardLayout());

        jpProgramacao.setBackground(new java.awt.Color(204, 255, 255));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setText("Acompanhamento de Programação");

        jLabel22.setText("Itens programados:");

        jTableProgramacao.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableProgramacao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo Item", "Descrição", "Lote", "Quantidade", "Metragem prog.", "Data "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableProgramacao.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(jTableProgramacao);
        if (jTableProgramacao.getColumnModel().getColumnCount() > 0) {
            jTableProgramacao.getColumnModel().getColumn(4).setHeaderValue("Metragem prog.");
            jTableProgramacao.getColumnModel().getColumn(5).setHeaderValue("Data ");
        }

        javax.swing.GroupLayout jpProgramacaoLayout = new javax.swing.GroupLayout(jpProgramacao);
        jpProgramacao.setLayout(jpProgramacaoLayout);
        jpProgramacaoLayout.setHorizontalGroup(
            jpProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProgramacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProgramacaoLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 753, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpProgramacaoLayout.createSequentialGroup()
                        .addGroup(jpProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
        );
        jpProgramacaoLayout.setVerticalGroup(
            jpProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProgramacaoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addGap(43, 43, 43)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpRoot.add(jpProgramacao, "jpProgramacao");

        jpParadas.setBackground(new java.awt.Color(208, 93, 79));

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel37.setText("Apontamento de paradas.");

        jLabel38.setText("Motivo da Parada:");

        jComboBoxParadasMaquina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel39.setText("Observações:");

        jTextAreaObsParada.setColumns(20);
        jTextAreaObsParada.setRows(5);
        jScrollPane4.setViewportView(jTextAreaObsParada);

        jTableMotivosParada.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Cod. Parada", "Abreviação", "Descriçao","observação"
            }
        ));
        jScrollPane5.setViewportView(jTableMotivosParada);

        jButtonIncluirMotivoParada.setText("Incluir Motivo");
        jButtonIncluirMotivoParada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIncluirMotivoParadaActionPerformed(evt);
            }
        });

        jButtonRemoverMotivoParada.setText("Remover Motivo");
        jButtonRemoverMotivoParada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoverMotivoParadaActionPerformed(evt);
            }
        });

        jButtonRegistrarParada.setText("Registrar Parada");
        jButtonRegistrarParada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarParadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpParadasLayout = new javax.swing.GroupLayout(jpParadas);
        jpParadas.setLayout(jpParadasLayout);
        jpParadasLayout.setHorizontalGroup(
            jpParadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpParadasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpParadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator8)
                    .addComponent(jScrollPane4)
                    .addComponent(jComboBoxParadasMaquina, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpParadasLayout.createSequentialGroup()
                        .addComponent(jButtonIncluirMotivoParada, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRemoverMotivoParada, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpParadasLayout.createSequentialGroup()
                        .addComponent(jButtonRegistrarParada, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpParadasLayout.setVerticalGroup(
            jpParadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpParadasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxParadasMaquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jpParadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonIncluirMotivoParada, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRemoverMotivoParada, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonRegistrarParada, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpRoot.add(jpParadas, "jpParadas");

        jpConfigDefault.setBackground(new java.awt.Color(204, 255, 255));
        jpConfigDefault.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setText("Configurações Default");

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel4.setText("Configurações usuário default");

        jLabel5.setText("Usuário:");

        jLabel6.setText("Senha:");

        jtfUserDafaut.setText("jtfUserDafaut");

        jpfSenhaDefault.setText("jPasswordField1");

        jLabel7.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel7.setText("Configurações de Banco de dados base produção:");

        jLabel8.setText("Nome do driver:");

        jLabel9.setText("Servidor:");

        jLabel10.setText("Url:");

        jLabel11.setText("Banco de dados:");

        jLabel12.setText("Usuario:");

        jLabel13.setText("Senha:");

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel14.setText("Configurações de Banco de dados base teste:");

        jLabel15.setText("Nome do driver:");

        jLabel16.setText("Servidor:");

        jLabel17.setText("Url:");

        jLabel18.setText("Banco de dados:");

        jLabel19.setText("Usuário:");

        jLabel20.setText("Senha:");

        jtfDriverProducao.setText("jtfDriverProducao");

        jtfServidorProducao.setText("jtfServidorProducao");

        jtfUrlProducao.setText("jtfUrlProducao");

        jtfBDProducao.setText("jtfBDProducao");

        jtfUsuarioProducao.setText("jtfUsuarioProducao");

        jtfDriverTeste.setText("jtfDriverTeste");

        jtfServidorTeste.setText("jtfServidorTeste");

        jtfUrlTeste.setText("jtfUrlTeste");

        jtfBDTeste.setText("jtfBDTeste");

        jtfUsuarioTeste.setText("jtfUsuarioTeste");

        jbCriarArquivosDefault.setText("Guardar");
        jbCriarArquivosDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCriarArquivosDefaultActionPerformed(evt);
            }
        });

        jpfSenhaProducao.setText("000000000000");

        jpfSenhaTeste.setText("jPasswordField1");

        javax.swing.GroupLayout jpConfigDefaultLayout = new javax.swing.GroupLayout(jpConfigDefault);
        jpConfigDefault.setLayout(jpConfigDefaultLayout);
        jpConfigDefaultLayout.setHorizontalGroup(
            jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                        .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jpfSenhaDefault)
                                    .addComponent(jtfUserDafaut)))
                            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtfServidorTeste)
                                    .addComponent(jtfUrlTeste)
                                    .addComponent(jtfBDTeste)
                                    .addComponent(jtfUsuarioTeste)
                                    .addComponent(jtfDriverTeste)))
                            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                                        .addComponent(jtfDriverProducao, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 180, Short.MAX_VALUE))
                                    .addComponent(jtfServidorProducao)
                                    .addComponent(jtfUrlProducao)
                                    .addComponent(jtfBDProducao)
                                    .addComponent(jtfUsuarioProducao)
                                    .addComponent(jpfSenhaProducao))))
                        .addContainerGap())
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addComponent(jSeparator2)
                    .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpfSenhaTeste)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCriarArquivosDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jpConfigDefaultLayout.setVerticalGroup(
            jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpConfigDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtfUserDafaut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jpfSenhaDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfDriverProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jtfServidorProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jtfUrlProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jtfBDProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jtfUsuarioProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jpfSenhaProducao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jtfDriverTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jtfServidorTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jtfUrlTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jtfBDTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jtfUsuarioTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpConfigDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jpfSenhaTeste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbCriarArquivosDefault))
                .addGap(197, 197, 197))
        );

        jpRoot.add(jpConfigDefault, "jpConfigDefault");
        jpConfigDefault.getAccessibleContext().setAccessibleName("");

        jpProducao.setBackground(new java.awt.Color(204, 255, 255));
        jpProducao.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setText("Acompanhamento de produção:");

        jLabel24.setText("Informações da ordem de fabricação:");

        jLabel25.setText("Codigo do Item:");

        jLabelProducaoCodItem.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoCodItem.setText("400500000000");

        jLabel26.setText("Desccrição: ");

        jLabelProducaoDescricaoItem.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoDescricaoItem.setText("jLabelProducaoDescriçãoItem");

        jLabel27.setText("Quantidade programada:");

        jLabelProducaoQtdProg.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoQtdProg.setText("00");

        jLabel28.setText("Quantidade produzida:");

        jLabelProducaoQtdProd.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoQtdProd.setText("00");

        jLabel29.setText("Metragem total programada :");

        jLabelProducaoOF.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabelProducaoMetTotalProg.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoMetTotalProg.setText("0000000");
        jLabelProducaoMetTotalProg.setToolTipText("");

        jLabel30.setText("Metragem total apontada:");
        jLabel30.setToolTipText("");

        jLabelProducaoMetTotalProd.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoMetTotalProd.setText("0000000");

        jLabel31.setText("Metragem por carretel:");

        jLabelProducaoMetCarretel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoMetCarretel.setText("000000");
        jLabelProducaoMetCarretel.setToolTipText("");

        jLabel32.setText("Diametro minimo:");

        jLabelProducaoDMinimo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoDMinimo.setText("0.00");

        jLabel33.setText("Diametro nominal:");

        jLabelProducaoDnominal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoDnominal.setText("0.00");

        jLabel34.setText("Diametro máximo:");

        jLabelProducaoDMaximo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoDMaximo.setText("0.00");

        jLabel35.setText("Velocidade de produção ideal:");

        jLabelProducaoVelIdeal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelProducaoVelIdeal.setText("000");

        jScrollPane2.setBackground(new java.awt.Color(204, 255, 255));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Arrebentamentos carretel de entrada:"));

        jTableProducaoArrebentamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Numero", "Metragem", "Cod. Embalagem"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableProducaoArrebentamentos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        jTableProducaoArrebentamentos.setCellSelectionEnabled(true);
        jScrollPane2.setViewportView(jTableProducaoArrebentamentos);
        if (jTableProducaoArrebentamentos.getColumnModel().getColumnCount() > 0) {
            jTableProducaoArrebentamentos.getColumnModel().getColumn(0).setResizable(false);
            jTableProducaoArrebentamentos.getColumnModel().getColumn(1).setResizable(false);
        }

        jScrollPane3.setBackground(new java.awt.Color(204, 255, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Paradas durante a produção:"));

        jTableProducaoParadas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Cod. Parada", "Abreviação", "Observação"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableProducaoParadas);
        if (jTableProducaoParadas.getColumnModel().getColumnCount() > 0) {
            jTableProducaoParadas.getColumnModel().getColumn(0).setResizable(false);
            jTableProducaoParadas.getColumnModel().getColumn(0).setPreferredWidth(1);
            jTableProducaoParadas.getColumnModel().getColumn(1).setResizable(false);
            jTableProducaoParadas.getColumnModel().getColumn(1).setPreferredWidth(1);
            jTableProducaoParadas.getColumnModel().getColumn(2).setResizable(false);
            jTableProducaoParadas.getColumnModel().getColumn(2).setPreferredWidth(1);
            jTableProducaoParadas.getColumnModel().getColumn(3).setResizable(false);
        }

        radialLcdVelocidade.setLedColor(eu.hansolo.steelseries.tools.LedColor.GREEN_LED);
        radialLcdVelocidade.setLedVisible(false);
        radialLcdVelocidade.setMaxMeasuredValueVisible(true);
        radialLcdVelocidade.setMaxValue(500.0);
        radialLcdVelocidade.setName(""); // NOI18N
        radialLcdVelocidade.setThreshold(500.0);
        radialLcdVelocidade.setThresholdVisible(true);
        radialLcdVelocidade.setTickLabelPeriod(50);
        radialLcdVelocidade.setTrackRange(0.0);
        radialLcdVelocidade.setTrackSection(0.0);
        radialLcdVelocidade.setTrackSectionColor(new java.awt.Color(0, 255, 0));
        radialLcdVelocidade.setTrackStartColor(new java.awt.Color(255, 0, 0));
        radialLcdVelocidade.setTrackVisible(true);

        linearCarretelSaida.setLedVisible(false);
        linearCarretelSaida.setThreshold(80.0);
        linearCarretelSaida.setThresholdVisible(true);
        linearCarretelSaida.setTitle("Carrele de Saida (%)");
        linearCarretelSaida.setTrackRange(100.0);
        linearCarretelSaida.setTrackSection(50.0);
        linearCarretelSaida.setTrackSectionColor(new java.awt.Color(0, 255, 0));
        linearCarretelSaida.setTrackStartColor(new java.awt.Color(255, 255, 0));
        linearCarretelSaida.setTrackStopColor(new java.awt.Color(255, 255, 0));
        linearCarretelSaida.setUnitString("Metros");
        linearCarretelSaida.setUnitStringVisible(false);

        displaySingleMetragemCarretel.setLcdDecimals(0);
        displaySingleMetragemCarretel.setUnitString("m");

        lightBulbMetragemSolicitada.setGlowColor(new java.awt.Color(255, 0, 0));
        lightBulbMetragemSolicitada.setPreferredSize(new java.awt.Dimension(56, 56));
        lightBulbMetragemSolicitada.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout linearCarretelSaidaLayout = new javax.swing.GroupLayout(linearCarretelSaida);
        linearCarretelSaida.setLayout(linearCarretelSaidaLayout);
        linearCarretelSaidaLayout.setHorizontalGroup(
            linearCarretelSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearCarretelSaidaLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(displaySingleMetragemCarretel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lightBulbMetragemSolicitada, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );
        linearCarretelSaidaLayout.setVerticalGroup(
            linearCarretelSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearCarretelSaidaLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(displaySingleMetragemCarretel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linearCarretelSaidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lightBulbMetragemSolicitada, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        linearProgramacao.setLedVisible(false);
        linearProgramacao.setTitle("Programação para o lote (%)");
        linearProgramacao.setTrackRange(10.0);
        linearProgramacao.setUnitStringVisible(false);

        displaySingleMetragemProgramado.setLcdDecimals(0);
        displaySingleMetragemProgramado.setUnitString("m");

        lightBulbMetragemLoteProducao.setGlowColor(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout linearProgramacaoLayout = new javax.swing.GroupLayout(linearProgramacao);
        linearProgramacao.setLayout(linearProgramacaoLayout);
        linearProgramacaoLayout.setHorizontalGroup(
            linearProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearProgramacaoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(displaySingleMetragemProgramado, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lightBulbMetragemLoteProducao, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        linearProgramacaoLayout.setVerticalGroup(
            linearProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearProgramacaoLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(linearProgramacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(displaySingleMetragemProgramado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightBulbMetragemLoteProducao, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabelAlerta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ezgif.com-resize.gif"))); // NOI18N
        jLabelAlerta.setText("jLabel37");

        jPanelEventoEntrada.setBackground(new java.awt.Color(204, 204, 204));
        jPanelEventoEntrada.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelEventoEntrada.setToolTipText("Proximo evento anotado no carretel de entrada.");
        jPanelEventoEntrada.setName("PainelEventosEntrada"); // NOI18N

        displaySingleEvtCarEntrada.setLcdDecimals(0);
        displaySingleEvtCarEntrada.setUnitString("m");

        jLabelEvtEntrada.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelEvtEntrada.setText("Evento Carretel de entrada");

        lightBulbAlertaEvento.setGlowColor(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelEventoEntradaLayout = new javax.swing.GroupLayout(jPanelEventoEntrada);
        jPanelEventoEntrada.setLayout(jPanelEventoEntradaLayout);
        jPanelEventoEntradaLayout.setHorizontalGroup(
            jPanelEventoEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelEventoEntradaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelEventoEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelEventoEntradaLayout.createSequentialGroup()
                        .addComponent(displaySingleEvtCarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lightBulbAlertaEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelEventoEntradaLayout.createSequentialGroup()
                        .addComponent(jLabelEvtEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanelEventoEntradaLayout.setVerticalGroup(
            jPanelEventoEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEventoEntradaLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabelEvtEntrada)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelEventoEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lightBulbAlertaEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(displaySingleEvtCarEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        radialLcdDiametro.setLcdDecimals(2);
        radialLcdDiametro.setLcdUnitString("mm");
        radialLcdDiametro.setLcdUnitStringVisible(true);
        radialLcdDiametro.setLedColor(eu.hansolo.steelseries.tools.LedColor.GREEN_LED);
        radialLcdDiametro.setLedVisible(false);
        radialLcdDiametro.setName(""); // NOI18N
        radialLcdDiametro.setThreshold(0.0);
        radialLcdDiametro.setThresholdVisible(true);
        radialLcdDiametro.setTickLabelPeriod(1);
        radialLcdDiametro.setTrackRange(0.0);
        radialLcdDiametro.setTrackSection(0.0);
        radialLcdDiametro.setTrackSectionColor(new java.awt.Color(0, 255, 0));
        radialLcdDiametro.setTrackStartColor(new java.awt.Color(255, 0, 0));
        radialLcdDiametro.setTrackVisible(true);

        jPanelEventoEntrada2.setBackground(new java.awt.Color(204, 204, 204));
        jPanelEventoEntrada2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelEventoEntrada2.setToolTipText("Proximo evento anotado no carretel de entrada.");
        jPanelEventoEntrada2.setName("PainelEventosEntrada"); // NOI18N

        displaySingleSaldoCarretelEntrada1.setLcdDecimals(0);
        displaySingleSaldoCarretelEntrada1.setUnitString("m");

        jLabelCarretel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelCarretel1.setText("Saldo Carretel de entrada: ");

        lightBulbAlertaSaldoEntrada1.setGlowColor(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelEventoEntrada2Layout = new javax.swing.GroupLayout(jPanelEventoEntrada2);
        jPanelEventoEntrada2.setLayout(jPanelEventoEntrada2Layout);
        jPanelEventoEntrada2Layout.setHorizontalGroup(
            jPanelEventoEntrada2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEventoEntrada2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEventoEntrada2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEventoEntrada2Layout.createSequentialGroup()
                        .addComponent(displaySingleSaldoCarretelEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lightBulbAlertaSaldoEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelCarretel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelEventoEntrada2Layout.setVerticalGroup(
            jPanelEventoEntrada2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEventoEntrada2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabelCarretel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEventoEntrada2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lightBulbAlertaSaldoEntrada1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(displaySingleSaldoCarretelEntrada1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelEventoEntrada3.setBackground(new java.awt.Color(204, 204, 204));
        jPanelEventoEntrada3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelEventoEntrada3.setToolTipText("Proximo evento anotado no carretel de entrada.");
        jPanelEventoEntrada3.setName("PainelEventosEntrada"); // NOI18N

        displaySingleSaldoCarretelEntrada2.setLcdDecimals(0);
        displaySingleSaldoCarretelEntrada2.setUnitString("m");

        jLabelCarretel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelCarretel2.setText("Saldo Carretel de entrada: ");

        lightBulbAlertaSaldoEntrada2.setGlowColor(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelEventoEntrada3Layout = new javax.swing.GroupLayout(jPanelEventoEntrada3);
        jPanelEventoEntrada3.setLayout(jPanelEventoEntrada3Layout);
        jPanelEventoEntrada3Layout.setHorizontalGroup(
            jPanelEventoEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelEventoEntrada3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEventoEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEventoEntrada3Layout.createSequentialGroup()
                        .addComponent(displaySingleSaldoCarretelEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lightBulbAlertaSaldoEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelEventoEntrada3Layout.createSequentialGroup()
                        .addComponent(jLabelCarretel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanelEventoEntrada3Layout.setVerticalGroup(
            jPanelEventoEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEventoEntrada3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabelCarretel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEventoEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(displaySingleSaldoCarretelEntrada2, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(lightBulbAlertaSaldoEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpProducaoLayout = new javax.swing.GroupLayout(jpProducao);
        jpProducao.setLayout(jpProducaoLayout);
        jpProducaoLayout.setHorizontalGroup(
            jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProducaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator5)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator6)
                    .addGroup(jpProducaoLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(jpProducaoLayout.createSequentialGroup()
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(radialLcdVelocidade, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(radialLcdDiametro, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(linearCarretelSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
                            .addComponent(linearProgramacao, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(jPanelEventoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanelEventoEntrada2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanelEventoEntrada3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jpProducaoLayout.createSequentialGroup()
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoQtdProg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoQtdProd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoMetTotalProg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoMetTotalProd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoMetCarretel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoCodItem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoDescricaoItem))
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoDMinimo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoDnominal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoDMaximo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelProducaoVelIdeal))
                            .addComponent(jLabel23))
                        .addGap(0, 90, Short.MAX_VALUE))
                    .addGroup(jpProducaoLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelProducaoOF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelAlerta, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118)))
                .addContainerGap())
        );
        jpProducaoLayout.setVerticalGroup(
            jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProducaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProducaoLayout.createSequentialGroup()
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelProducaoOF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jLabelProducaoQtdProg)
                            .addComponent(jLabel28)
                            .addComponent(jLabelProducaoQtdProd)
                            .addComponent(jLabel29)
                            .addComponent(jLabelProducaoMetTotalProg)
                            .addComponent(jLabel30)
                            .addComponent(jLabelProducaoMetTotalProd)
                            .addComponent(jLabel31)
                            .addComponent(jLabelProducaoMetCarretel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabelProducaoCodItem)
                            .addComponent(jLabel26)
                            .addComponent(jLabelProducaoDescricaoItem))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(jLabelProducaoDMinimo)
                            .addComponent(jLabel33)
                            .addComponent(jLabelProducaoDnominal)
                            .addComponent(jLabel34)
                            .addComponent(jLabelProducaoDMaximo)
                            .addComponent(jLabel35)
                            .addComponent(jLabelProducaoVelIdeal))
                        .addGap(11, 11, 11)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(linearCarretelSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(linearProgramacao, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpProducaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanelEventoEntrada2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanelEventoEntrada, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanelEventoEntrada3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jpProducaoLayout.createSequentialGroup()
                                .addComponent(radialLcdVelocidade, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(radialLcdDiametro, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabelAlerta, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(189, Short.MAX_VALUE))
        );

        jpRoot.add(jpProducao, "jpProducao");

        jpLogin.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setText("Usuario: ");

        jLabel2.setText("Senha:");

        jtfUser.setText("renatoinf");

        jpfPassword.setText("vitor");

        jbLogin.setText("Login");
        jbLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbLoginActionPerformed(evt);
            }
        });

        jPLogo.setBackground(new java.awt.Color(204, 255, 255));
        jPLogo.setName("jPLogo"); // NOI18N
        jPLogo.setLayout(new java.awt.BorderLayout(100, 100));

        jPasswordFieldCartao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordFieldCartaoKeyPressed(evt);
            }
        });

        jLabel36.setText("Cartão:");

        javax.swing.GroupLayout jpLoginLayout = new javax.swing.GroupLayout(jpLogin);
        jpLogin.setLayout(jpLoginLayout);
        jpLoginLayout.setHorizontalGroup(
            jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpLoginLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbLogin)
                    .addGroup(jpLoginLayout.createSequentialGroup()
                        .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtfUser)
                            .addComponent(jpfPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))))
                .addGap(72, 72, 72)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordFieldCartao, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
        );
        jpLoginLayout.setVerticalGroup(
            jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpLoginLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtfUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jpfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPasswordFieldCartao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbLogin)
                .addGap(18, 18, 18)
                .addComponent(jPLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(514, Short.MAX_VALUE))
        );

        jpRoot.add(jpLogin, "jpLogin");

        getContentPane().add(jpRoot, java.awt.BorderLayout.CENTER);

        menuLogin.setText("Incial");

        jMenuItemLogin.setText("Login");
        jMenuItemLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoginActionPerformed(evt);
            }
        });
        menuLogin.add(jMenuItemLogin);

        jMenuItemSair.setText("Sair");
        jMenuItemSair.setEnabled(false);
        jMenuItemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSairActionPerformed(evt);
            }
        });
        menuLogin.add(jMenuItemSair);

        jMenuItemDesligar.setText("Desligar");
        jMenuItemDesligar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDesligarActionPerformed(evt);
            }
        });
        menuLogin.add(jMenuItemDesligar);

        jMenuItemReiniciar.setText("Reiniciar");
        jMenuItemReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReiniciarActionPerformed(evt);
            }
        });
        menuLogin.add(jMenuItemReiniciar);

        menuPrincipal.add(menuLogin);

        menuProgramacao.setText("Programação");

        jMenuItemProgramacao.setText("Acompanhamento de programação");
        jMenuItemProgramacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProgramacaoActionPerformed(evt);
            }
        });
        menuProgramacao.add(jMenuItemProgramacao);

        menuPrincipal.add(menuProgramacao);

        menuParadas.setText("Paradas");

        jMenuItemParadas.setText("Paradas");
        jMenuItemParadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemParadasActionPerformed(evt);
            }
        });
        menuParadas.add(jMenuItemParadas);

        menuPrincipal.add(menuParadas);

        menuProducao.setText("Produção");

        jMenuItemProducao.setText("Produção");
        jMenuItemProducao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProducaoActionPerformed(evt);
            }
        });
        menuProducao.add(jMenuItemProducao);

        menuPrincipal.add(menuProducao);

        menuConfiguracoes.setText("Configurações");

        jMenuConfigDefault.setText("Default");
        jMenuConfigDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuConfigDefaultActionPerformed(evt);
            }
        });
        menuConfiguracoes.add(jMenuConfigDefault);

        jMenuTrocarMaquina.setText("Trocar maquina");
        jMenuTrocarMaquina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuTrocarMaquinaActionPerformed(evt);
            }
        });
        menuConfiguracoes.add(jMenuTrocarMaquina);

        jMenuConfigSerialRFID.setText("Serial RFID");
        jMenuConfigSerialRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuConfigSerialRFIDActionPerformed(evt);
            }
        });
        menuConfiguracoes.add(jMenuConfigSerialRFID);

        menuPrincipal.add(menuConfiguracoes);

        setJMenuBar(menuPrincipal);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemProgramacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProgramacaoActionPerformed
        // TODO add your handling code here:    
        abrirTelaProgramacao();
        
    }//GEN-LAST:event_jMenuItemProgramacaoActionPerformed

    private void jMenuItemLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLoginActionPerformed
        // TODO add your handling code here:        
        abrirTelaLogin();
    }//GEN-LAST:event_jMenuItemLoginActionPerformed

    private void jMenuItemParadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemParadasActionPerformed
        // TODO add your handling code here:
        try {                    
            Object[] options = { "Sim", "Não" }; 
            int i = JOptionPane.showOptionDialog(null, "Este menu é exclusivo para apontamento de eventos \n"
                    + "durante o processo de produção, ao acessa-lo será obrigado a apontar um evento. \n"
                    + "Deseja realmente apontar um evento?", 
                    "Tela de aopntamento de eventos durante a produção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, options, options[0]); 
            if (i == JOptionPane.YES_OPTION) {
                ControllerEventosSistema ctr = new ControllerEventosSistema();            
                ctr.registraEventos(4,login.getCodigoOperador(),0,0,codMaquina,jLabelProducaoOF.getText());           
                abrirTelaParadas(); 
            }
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jMenuItemParadasActionPerformed

    private void jMenuItemProducaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProducaoActionPerformed
        // TODO add your handling code here:
        abrirTelaProducao();        
    }//GEN-LAST:event_jMenuItemProducaoActionPerformed

    private void jbLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbLoginActionPerformed
        // TODO add your handling code here:        
        try {                    
            login.setUsuario(jtfUser.getText());
            //System.out.println("usuario: " + login.getUsuario());
            login.setSenha(jpfPassword.getText());
            //System.out.println("Psw: " + login.getSenha());
            if(login.logar(login)){
                usuariologado();
            }else{
                JOptionPane.showMessageDialog(null, "Usuario ou Senha invalidos","Login",JOptionPane.ERROR_MESSAGE);            
            }
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jbLoginActionPerformed

    private void jMenuConfigDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuConfigDefaultActionPerformed
        // TODO add your handling code here:
        try {                    
            limparCamposConfigDefault();
            DadosConexao dp =  new DadosConexao();
            DadosDefaultDAO dao = new DadosDefaultDAO();
            dp = dao.buscaDadosConexaoDefault(true);
            if(dp!=null){
                jtfBDProducao.setText(dp.getMyDatabase());
                jtfDriverProducao.setText(dp.getDriverName());
                jtfServidorProducao.setText(dp.getServerName());
                jtfUrlProducao.setText(dp.getUrl());
            }
            DadosConexao dt =  new DadosConexao();
            dt = dao.buscaDadosConexaoDefault(false);
            if(dt!=null){
                jtfBDTeste.setText(dt.getMyDatabase());
                jtfDriverTeste.setText(dt.getDriverName());
                jtfServidorTeste.setText(dt.getServerName());
                jtfUrlTeste.setText(dt.getUrl());
            }
            CardLayout card = (CardLayout) jpRoot.getLayout();
            card.show(jpRoot,"jpConfigDefault");
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jMenuConfigDefaultActionPerformed

    private void jbCriarArquivosDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCriarArquivosDefaultActionPerformed
        // TODO add your handling code here:
        try {                    
            boolean add = true;
            Usuario us = new Usuario();
            us.setUsuario(jtfUserDafaut.getText());
            us.setSenha(jpfSenhaDefault.getText());
            DadosDefaultDAO dao = new DadosDefaultDAO();
            if (!dao.ArmazenarUserDefault(us)) {
                JOptionPane.showMessageDialog(null, "Falha ao registrar dados usuario default","Usuário Default",JOptionPane.ERROR_MESSAGE);
                add = false;
            }
            DadosConexao d = new DadosConexao();
            d.setDriverName(jtfDriverProducao.getText());
            d.setMyDatabase(jtfBDProducao.getText());
            d.setPassword(jpfSenhaProducao.getText());
            d.setServerName(jtfServidorProducao.getText());
            d.setUrl(jtfUrlProducao.getText());
            d.setUserName(jtfUsuarioProducao.getText());
            if(!dao.armazenaDadosConexao(d,true)) {
                JOptionPane.showMessageDialog(null, "Falha ao registrar dados de conexão","Banco de dados Produção",JOptionPane.ERROR_MESSAGE);
                add = false;
            }
            d.setDriverName(jtfDriverTeste.getText());
            d.setMyDatabase(jtfBDTeste.getText());
            d.setPassword(jpfSenhaTeste.getText());
            d.setServerName(jtfServidorTeste.getText());
            d.setUrl(jtfUrlTeste.getText());
            d.setUserName(jtfUsuarioTeste.getText());
            if(!dao.armazenaDadosConexao(d,false)) {
                JOptionPane.showMessageDialog(null, "Falha ao registrar dados de conexão","Banco de dados Teste",JOptionPane.ERROR_MESSAGE);
                add = false;
            }
            if(add) JOptionPane.showMessageDialog(null,"Dados Registrados com sucesso");
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jbCriarArquivosDefaultActionPerformed

    private void jMenuItemSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSairActionPerformed
        // TODO add your handling code here:   
        try {                    
            ControllerEventosSistema ctr = new ControllerEventosSistema();        
            ctr.registraEventos(12,login.getCodigoOperador(),0,(int) displaySingleMetragemCarretel.getValue(),codMaquina,jLabelProducaoOF.getText()); 
            jMenuItemSair.setEnabled(false);
            jMenuItemLogin.setEnabled(true);
        abrirTelaLogin();
        } catch (Exception e) {
            erro.gravaErro(e);
        }               
    }//GEN-LAST:event_jMenuItemSairActionPerformed

    private void jMenuItemDesligarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDesligarActionPerformed
        try {
            // TODO add your handling code here:
            if(System.getProperty("os.name").equals("Linux")){
                ControllerEventosSistema ctr = new ControllerEventosSistema();
                if(login!=null){
                    ctr.registraEventos(2,login.getCodigoOperador(),0,0,codMaquina,"");
                }else{
                    ctr.registraEventos(2,"",0,0,codMaquina,"");
                }
                Runtime.getRuntime().exec("shutdown -h now");
            }
        } catch (IOException ex) {
            erro.gravaErro(ex);
        }
    }//GEN-LAST:event_jMenuItemDesligarActionPerformed

    private void jMenuTrocarMaquinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuTrocarMaquinaActionPerformed
        // TODO add your handling code here:
        try {                    
            JFCadastroMonitor cad = new JFCadastroMonitor();
            cad.setEdit(true);
            cad.setVisible(true);
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jMenuTrocarMaquinaActionPerformed

    private void jMenuConfigSerialRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuConfigSerialRFIDActionPerformed
        // TODO add your handling code here:
        try {                    
            JFConfigSerialRFID cfg = new JFConfigSerialRFID();
            cfg.setConfigName(SERIAL_RFID);
            cfg.setVisible(true);
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jMenuConfigSerialRFIDActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        //System.out.println("Fechando");
        try {                    
            if(login.getNivel().equals("0")) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }else{
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_formWindowClosing

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        // TODO add your handling code here:           
        try {
            if(!login.getNivel().equals("0"))  setExtendedState(MAXIMIZED_BOTH);
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_formWindowIconified

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        // TODO add your handling code here:
        //System.out.println("formWindowStateChanged");
        try {
            if(!login.getNivel().equals("0"))
                setExtendedState(MAXIMIZED_BOTH);           
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        // TODO add your handling code here:
        //System.out.println("formWindowDeactivated");
        try {
            if(!login.getNivel().equals("0"))
                setExtendedState(MAXIMIZED_BOTH);                              
        } catch (Exception e) {
            erro.gravaErro(e);
        }

    }//GEN-LAST:event_formWindowDeactivated

    private void jMenuItemReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReiniciarActionPerformed
        try {
            // TODO add your handling code here:
            if(System.getProperty("os.name").equals("Linux")){
                ControllerEventosSistema ctr = new ControllerEventosSistema();
                if(login!=null){
                    ctr.registraEventos(3,login.getCodigoOperador(),0,0,codMaquina,"");
                }else{
                    ctr.registraEventos(3,"",0,0,codMaquina,"");
                }
                Runtime.getRuntime().exec("reboot");
             }
        } catch (IOException ex) {
            erro.gravaErro(ex);
        }
    }//GEN-LAST:event_jMenuItemReiniciarActionPerformed

    private void jButtonRemoverMotivoParadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoverMotivoParadaActionPerformed
        // TODO add your handling code here:
        try {
            if(jTableMotivosParada.getRowCount()>0){                        
                if(jTableMotivosParada.getSelectedRow()!=(-1)){
                    ControllerEventosSistema ctr = new ControllerEventosSistema();
                    if(ctr.removerPreApontamento(jTableMotivosParada.getSelectedRow(), codMaquina)){
                        DefaultTableModel modelo = (DefaultTableModel)jTableMotivosParada.getModel();
                        modelo.removeRow(jTableMotivosParada.getSelectedRow());                    
                    }
                }else{
                    JOptionPane.showMessageDialog(rootPane,"Por favor escolha um motivo antes de solicitar a remoção",
                            "Remover Motivo da parada",JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jButtonRemoverMotivoParadaActionPerformed

    private void jButtonIncluirMotivoParadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIncluirMotivoParadaActionPerformed
        // TODO add your handling code here:
        int codPesagemSaida=0;
        int codPesagemEntrada=0;
        try {                       
            int motivoEscolhido = jComboBoxParadasMaquina.getSelectedIndex();
            List<String> infoParadas = paradas.buscaInfoParadaPorCodigo(motivoEscolhido);
            if(infoParadas.get(0).equals("2")){
                JFDEscolhaTrocaCarretelEntrada car = new JFDEscolhaTrocaCarretelEntrada(this,true);
                car.setCarreteisMontados(listaPesagens);
                car.setVisible(true);
                if(car.getReturnStatus()==1){                                        
                    JFDTrocaCarretelEntrada trc = new JFDTrocaCarretelEntrada(this,true);
                    trc.setPesSaida(car.getPesSaida());
                    trc.setMetragem((int) displaySingleMetragemCarretel.getValue());
                    trc.setCodMaquina(codMaquina);
                    trc.setLote(prod.getLoteProducao());
                    trc.buscaItensAlternativosMontagem(prod.getItemProducao());
                    trc.setVisible(true);
                    if(trc.getReturnStatus()==1){
                        codPesagemSaida=Integer.valueOf(trc.getPesSaida().getCodigo());
                        codPesagemEntrada=Integer.valueOf(trc.getPesEntrada().getCodigo());
                    }else{
                        return;
                    }
                }else{
                    return;
                }
            }
            ControllerEventosSistema ctr = new ControllerEventosSistema();
            if(ctr.verificaPreApontamento(infoParadas.get(0),codMaquina,jTextAreaObsParada.getText().trim(),true,codPesagemSaida,codPesagemEntrada)){
                DefaultTableModel modelo = (DefaultTableModel)jTableMotivosParada.getModel();
                modelo.addRow(new Object[]{infoParadas.get(0),infoParadas.get(1),infoParadas.get(2),
                        jTextAreaObsParada.getText().trim()});            
                jTextAreaObsParada.setText("");
            }            
           } catch (Exception e) {
               erro.gravaErro(e);
        }        
    }//GEN-LAST:event_jButtonIncluirMotivoParadaActionPerformed

    private void jButtonRegistrarParadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarParadaActionPerformed
        // TODO add your handling code here:        
        try {                    
            // if(!maqParada){ retornar apos testes
            if(maqParada){ //remover ampos testes
                if(jTableMotivosParada.getRowCount()>0){
                    registrarMotivoParadas();
                    habilitarMenu();
                    JOptionPane.showMessageDialog(rootPane,"Registros criados com sucesso!!!","Cadastro de paradas",JOptionPane.INFORMATION_MESSAGE);
                    
                    abrirTelaProducao();
                }else{
                    JOptionPane.showMessageDialog(rootPane, "Por favor indique o motivo da parada!","Aguardando motivo da parada",JOptionPane.ERROR_MESSAGE);
                }            
            }else{
                JOptionPane.showMessageDialog(rootPane, "Ainda não foi detectado o retorno de produção","Maquina permanece parada",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }//GEN-LAST:event_jButtonRegistrarParadaActionPerformed

    private void jPasswordFieldCartaoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldCartaoKeyPressed
        // TODO add your handling code here:
        if(jPasswordFieldCartao.getText().length()==10){
            if(ControllerUtil.SoTemNumeros(jPasswordFieldCartao.getText())){
                String code = jPasswordFieldCartao.getText();
                jPasswordFieldCartao.setText("");
                if(!login.getCode().equals(code)){            
                    if(!login.getCode().trim().equals("")){
                        code = login.getCode();
                    }
                    login.setCode(code);              
                    if(login.logarCode(login)){
                        usuariologado();            
                    }else{
                        login.setCode(code);
                        JOptionPane.showMessageDialog(null, "Codigo de Identificação não vinculado","Codigo invalido",JOptionPane.ERROR_MESSAGE);
                    }        
                }
            }
        }
    }//GEN-LAST:event_jPasswordFieldCartaoKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LogErro errLcl = new LogErro();
            errLcl.gravaErro(ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            try {                            
                new JFPrincipal().setVisible(true);                 
                if(codMaquina==null){
                    JOptionPane.showMessageDialog(null, "Não foi encontrada uma maquina cadastrada para este sistema",
                            "Registro necessário",JOptionPane.ERROR_MESSAGE);
                    new JFCadastroMonitor().setVisible(true);
                }
            } catch (HeadlessException e) {
                LogErro errLcl = new LogErro();
                errLcl.gravaErro(e);
            }
        });                  
    }

    public static void setCodMaquina(String codMaquina) {
        JFPrincipal.codMaquina = codMaquina;
    }

    public static String getIdentificador() {
        return identificador;
    }
    
    private void bloquearMenu(){
        menuParadas.setEnabled(false);
        menuProducao.setEnabled(false);
        menuProgramacao.setEnabled(false );
        menuConfiguracoes.setEnabled(false);        

    }
    private void habilitarMenu() {
        
        menuParadas.setEnabled(true);
        menuProducao.setEnabled(true);
        menuProgramacao.setEnabled(true);
        jMenuItemSair.setEnabled(true);
        if(login.getNivel().equals("0")){
            menuConfiguracoes.setEnabled(true);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingleEvtCarEntrada;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingleMetragemCarretel;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingleMetragemProgramado;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingleSaldoCarretelEntrada1;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingleSaldoCarretelEntrada2;
    private javax.swing.JButton jButtonIncluirMotivoParada;
    private javax.swing.JButton jButtonRegistrarParada;
    private javax.swing.JButton jButtonRemoverMotivoParada;
    private javax.swing.JComboBox<String> jComboBoxParadasMaquina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAlerta;
    private javax.swing.JLabel jLabelCarretel1;
    private javax.swing.JLabel jLabelCarretel2;
    private javax.swing.JLabel jLabelEvtEntrada;
    private javax.swing.JLabel jLabelProducaoCodItem;
    private javax.swing.JLabel jLabelProducaoDMaximo;
    private javax.swing.JLabel jLabelProducaoDMinimo;
    private javax.swing.JLabel jLabelProducaoDescricaoItem;
    private javax.swing.JLabel jLabelProducaoDnominal;
    private javax.swing.JLabel jLabelProducaoMetCarretel;
    private javax.swing.JLabel jLabelProducaoMetTotalProd;
    private javax.swing.JLabel jLabelProducaoMetTotalProg;
    private javax.swing.JLabel jLabelProducaoOF;
    private javax.swing.JLabel jLabelProducaoQtdProd;
    private javax.swing.JLabel jLabelProducaoQtdProg;
    private javax.swing.JLabel jLabelProducaoVelIdeal;
    private javax.swing.JMenuItem jMenuConfigDefault;
    private javax.swing.JMenuItem jMenuConfigSerialRFID;
    private javax.swing.JMenuItem jMenuItemDesligar;
    private javax.swing.JMenuItem jMenuItemLogin;
    private javax.swing.JMenuItem jMenuItemParadas;
    private javax.swing.JMenuItem jMenuItemProducao;
    private javax.swing.JMenuItem jMenuItemProgramacao;
    private javax.swing.JMenuItem jMenuItemReiniciar;
    private javax.swing.JMenuItem jMenuItemSair;
    private javax.swing.JMenuItem jMenuTrocarMaquina;
    private javax.swing.JPanel jPLogo;
    private javax.swing.JPanel jPanelEventoEntrada;
    private javax.swing.JPanel jPanelEventoEntrada2;
    private javax.swing.JPanel jPanelEventoEntrada3;
    private javax.swing.JPasswordField jPasswordFieldCartao;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTable jTableMotivosParada;
    private javax.swing.JTable jTableProducaoArrebentamentos;
    private javax.swing.JTable jTableProducaoParadas;
    private javax.swing.JTable jTableProgramacao;
    private javax.swing.JTextArea jTextAreaObsParada;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton jbCriarArquivosDefault;
    private javax.swing.JButton jbLogin;
    private javax.swing.JPanel jpConfigDefault;
    private javax.swing.JPanel jpLogin;
    private javax.swing.JPanel jpParadas;
    private javax.swing.JPanel jpProducao;
    private javax.swing.JPanel jpProgramacao;
    private javax.swing.JPanel jpRoot;
    private javax.swing.JPasswordField jpfPassword;
    private javax.swing.JPasswordField jpfSenhaDefault;
    private javax.swing.JPasswordField jpfSenhaProducao;
    private javax.swing.JPasswordField jpfSenhaTeste;
    private javax.swing.JTextField jtfBDProducao;
    private javax.swing.JTextField jtfBDTeste;
    private javax.swing.JTextField jtfDriverProducao;
    private javax.swing.JTextField jtfDriverTeste;
    private javax.swing.JTextField jtfServidorProducao;
    private javax.swing.JTextField jtfServidorTeste;
    private javax.swing.JTextField jtfUrlProducao;
    private javax.swing.JTextField jtfUrlTeste;
    private javax.swing.JTextField jtfUser;
    private javax.swing.JTextField jtfUserDafaut;
    private javax.swing.JTextField jtfUsuarioProducao;
    private javax.swing.JTextField jtfUsuarioTeste;
    private eu.hansolo.lightbulb.LightBulb lightBulbAlertaEvento;
    private eu.hansolo.lightbulb.LightBulb lightBulbAlertaSaldoEntrada1;
    private eu.hansolo.lightbulb.LightBulb lightBulbAlertaSaldoEntrada2;
    private eu.hansolo.lightbulb.LightBulb lightBulbMetragemLoteProducao;
    private eu.hansolo.lightbulb.LightBulb lightBulbMetragemSolicitada;
    private eu.hansolo.steelseries.gauges.Linear linearCarretelSaida;
    private eu.hansolo.steelseries.gauges.Linear linearProgramacao;
    private javax.swing.JMenu menuConfiguracoes;
    private javax.swing.JMenu menuLogin;
    private javax.swing.JMenu menuParadas;
    private javax.swing.JMenuBar menuPrincipal;
    private javax.swing.JMenu menuProducao;
    private javax.swing.JMenu menuProgramacao;
    private eu.hansolo.steelseries.gauges.Radial4Lcd radialLcdDiametro;
    private eu.hansolo.steelseries.gauges.Radial4Lcd radialLcdVelocidade;
    // End of variables declaration//GEN-END:variables

    private void limparCamposConfigDefault() {
        jtfUserDafaut.setText("");
        jpfSenhaDefault.setText("");
        jtfUsuarioProducao.setText("");
        jpfSenhaProducao.setText("");
        jtfUsuarioTeste.setText("");
        jpfSenhaTeste.setText("");
    }
    
    private void limparTelaLogin(){
        jtfUser.setText("");
        jpfPassword.setText("");
    }
   
    private void limparJTable(JTable tabela ){
        try {                    
            DefaultTableModel tblRemove = (DefaultTableModel)tabela.getModel();            
            while(tblRemove.getRowCount()>0){                        
                tblRemove.removeRow(0);                 
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }
        
    private void buscarProgramacaoMaquina() {   
        try {                    
            DefaultTableModel modelo = (DefaultTableModel)jTableProgramacao.getModel();
            ProgramacaoMaquinaDAO programacao =  new ProgramacaoMaquinaDAO();
            List<ProgramacaoMaquina> lista =  new ArrayList<>();
            lista = programacao.buscaProgramacaoMaquina(codMaquina);
            for(int i=0;i<lista.size();i++){
                modelo.addRow(new Object[]{lista.get(i).getProduto().getCodigo(),
                    lista.get(i).getProduto().getDescricao(),lista.get(i).getLoteproducao(),
                    lista.get(i).getQuantidadeProgramada(),lista.get(i).getMetragemProgramada(),lista.get(i).getDataProgramada()});
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }
    
    private boolean buscarInformaçoesProducao(){
        try {                    
            ProducaoDAO daoProd = new ProducaoDAO();
            prod = daoProd.buscaItemProducao(codMaquina);
            if(prod != null){
                if(prod.getCarretelSaida().trim().equals("")){
                    String carretelSaida = JOptionPane.showInputDialog(rootPane,"Carretel de Entrada","Por favor digite o numero do carretel de entrada",JOptionPane.QUESTION_MESSAGE);
                    System.out.println(carretelSaida);
                    if(carretelSaida!=null){
                        if(!carretelSaida.trim().equals("")){
                            if(ControllerUtil.SoTemNumeros(carretelSaida)){
                                //validar carretel produto metragem
                                ControllerProdutoMetragem ctrProdMet = new ControllerProdutoMetragem();
                                if(ctrProdMet.validaEmbagemProdutoMetragem(prod, carretelSaida, codMaquina)){
                                    if(!daoProd.atualizaCarretelSaida(carretelSaida,codMaquina)){
                                        JOptionPane.showMessageDialog(rootPane,"Falha ao registrar carretel de saida, por favor tente novamente","Falha de carretel de saida",JOptionPane.ERROR_MESSAGE);
                                    }else{
                                        prod.setCarretelSaida(carretelSaida);
                                        buscarInformaçoesProducao();
                                    }
                                }else{
                                    JOptionPane.showMessageDialog(rootPane,"Não foi encontrada uma relação para a flange da carretel digitada \n"
                                            + "com a maquina e item em produção.\n"
                                            + "Por favor solicite ao encarregado a conferencia destes dados em Produto x metragem no Projeto balança", "Flange de carretel não cadastrada", JOptionPane.ERROR_MESSAGE);
                                    abrirTelaProducao();
                                }
                            }else{
                                JOptionPane.showMessageDialog(rootPane,"Codigo do carretel de entrada invalido. "
                                        + "\n Codigo deve conter apenas numeros","Codigo Inválido",
                                        JOptionPane.ERROR_MESSAGE);
                                abrirTelaProgramacao();
                                return false;
                            }
                        }else{
                            abrirTelaProgramacao();
                            return false;
                        }
                    }else{
                        abrirTelaProgramacao();
                        return false;
                    }
                }else{    
                    ProgramacaoMaquinaDAO daoProg = new ProgramacaoMaquinaDAO();
                    ProdutoCarretelDAO daoProdCar =  new ProdutoCarretelDAO();
                    prog = daoProg.buscaProgramacaoLoteItem(prod.getLoteProducao(),prod.getItemProducao());
                    if(prog != null){
                        jLabelProducaoCodItem.setText(prod.getItemProducao());
                        jLabelProducaoOF.setText(prod.getLoteProducao());
                        jLabelProducaoDescricaoItem.setText(prog.getProduto().getDescricao());
                        jLabelProducaoQtdProg.setText(Integer.toString(prog.getQuantidadeProgramada()));
                        jLabelProducaoQtdProd.setText(Integer.toString(prog.getQuantidadeProduzida()));
                        jLabelProducaoMetTotalProg.setText(String.valueOf(prog.getMetragemTotalProgramada()));
                        jLabelProducaoMetCarretel.setText(String.valueOf(prog.getMetragemProgramada()));
                        jLabelProducaoDMinimo.setText(String.valueOf(prog.getProduto().getDiametroMinimo()));
                        jLabelProducaoDnominal.setText(String.valueOf(prog.getProduto().getDiametroNominal()));
                        jLabelProducaoDMaximo.setText(String.valueOf(prog.getProduto().getDiametroMaximo()));
                        jLabelProducaoMetTotalProd.setText(String.valueOf(daoProd.BuscaMetragemProduzida(prod.getLoteProducao(),prod.getItemProducao())));
                        ProdutoMaquinaDAO daoProdMaq = new ProdutoMaquinaDAO();
                        prodmaq = daoProdMaq.buscaVelocidadeProdutoMaquina(prod.getItemProducao(), codMaquina);
                        prodCar = daoProdCar.buscaDadosProdutoCarretel(prod.getItemProducao(),prod.getCarretelSaida(),codMaquina);
                        jLabelProducaoVelIdeal.setText(String.valueOf(prodmaq.getVelocidade())+ " " + prodmaq.getUnidade());
                        buscarRegistrosObservacaoPesagem();                    
                        return true;
                    }else{
                        JOptionPane.showMessageDialog(rootPane,"Falha ao busrcar dados da programação do item em produção, "
                                + "Por favor informe ao setor de Produção \n Itens de conrole referência: 1, 91 ou 20.","Falha ao buscar dados",JOptionPane.ERROR_MESSAGE);
                        abrirTelaProgramacao();
                        return false;
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Maquina sem producao, por favor realise a montagem",
                        "Maquina sem producao",JOptionPane.OK_OPTION);
                abrirTelaProgramacao();
                return false;
            }
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
        return false;
    }
    
    private void abrirTelaParadas() {
        try {
            
            if(paradas==null) paradas = new ControllerParadasMaquina(codMaquina);
            long metragem = (long) displaySingleMetragemCarretel.getValue();
            evtRegistrado=paradas.registraInicioParadamaquina(metragem, codMaquina);
            List<String> listaParadas = new ArrayList<>();
            listaParadas = paradas.buscaListaParadasDescricao();
            jComboBoxParadasMaquina.removeAllItems();
            for(int i=0;i<listaParadas.size();i++){
                jComboBoxParadasMaquina.addItem(listaParadas.get(i));
            } 
            maqParada = true;
            limparJTable(jTableMotivosParada);
            bloquearMenu();
            desbloquearMenuLigaDesliga();
            verificarMotivosPreApontados();
            CardLayout card = (CardLayout) jpRoot.getLayout();
            card.show(jpRoot,"jpParadas");
            
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }        
    }
    private void abrirTelaProgramacao() {
        try {                    
            limparJTable(jTableProgramacao);
            buscarProgramacaoMaquina();
            CardLayout card = (CardLayout) jpRoot.getLayout();
            card.show(jpRoot,"jpProgramacao");
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    private void abrirTelaProducao() {
        try {                    
            limparJTable(jTableProducaoArrebentamentos);
            limparJTable(jTableProducaoParadas);
            jLabelAlerta.setVisible(false);
            if(comMicrometro==null) comMicrometro = new SerialTxRx();
            if(parametrizarSerial(SERIAL_MICROMETRO)){
                if(comMicrometro.iniciaSerial()){
                    System.out.println("serial Micrometro iniciada");
                    comMicrometro.addActionListener(this);
                }
            }
            if(buscarInformaçoesProducao()){
                buscarParadasProcessoProducao();
                ajustarMostradorVelocidade();
                ajustarMostradoresMetragem();
                configurarMostradoresDiametro();          
                CardLayout card = (CardLayout) jpRoot.getLayout();
                card.show(jpRoot,"jpProducao");
                if(timerVelocimetro==null) {
                    iniciaTimerVelociametro();
                    this.tarefaVelocidade();
                }
            }
            //Apenas para testar sem o micrometro Remover para produção
            /*
            JFApontamentoProducao apt = new JFApontamentoProducao(maquina,login,prodmaq,prodCar,prod,prog);
            apt.setExtendedState(JFrame.MAXIMIZED_BOTH);
            apt.setVisible(true);
            */
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    private void abrirTelaLogin() {
        try {                                      
            if(comRFID==null) comRFID = new SerialTxRx();
            login.setUsuario("");
            login.setNome("");
            login.setNivel("");
            login.setSenha("");
            login.setCode("");
            if(false) limparTelaLogin(); //apenas para teste, passar para true em producao
            bloquearMenu();            
            if(parametrizarSerial(SERIAL_RFID)){
                if(comRFID.iniciaSerial()){
                    System.out.println("serial RFID iniciada");
                    comRFID.addActionListener(this);
                }            
            }            
            CardLayout card = (CardLayout) jpRoot.getLayout();
            card.show(jpRoot,"jpLogin");
            jPasswordFieldCartao.requestFocus();
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }
    private boolean parametrizarSerial(String configName ){
        try {                    
            ControllerConfigSerialPort cfg = new ControllerConfigSerialPort();
            if(configName.equals(SERIAL_RFID)){
                if(comRFID!=null){               
                   comRFID = cfg.configurarPortaSerial(configName,identificador);
                   if(comRFID!=null){                   
                       System.out.println("Serial comfigurada com sucesso!! " + comRFID.getSerialPortName());
                       return true;
                   }else{                   
                       System.out.println("Falha ao configurar porta serial");
                       return false;
                   }                    
               }
            }
            if(configName.equals(SERIAL_MICROMETRO)){
                if(comMicrometro!=null){               
                   comMicrometro = cfg.configurarPortaSerial(configName,identificador);
                   if(comMicrometro!=null){                   
                       System.out.println("Serial do micrometro comfigurada com sucesso!! " + comMicrometro.getSerialPortName());
                       return true;
                   }else{                   
                       System.out.println("Falha ao configurar porta serial para comunicação com micrometro");
                       return false;
                   }                    
               }
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return false;
        
    }
    private void buscarRegistrosObservacaoPesagem() {
        try {                    
            String dado[];        
            PesagemDAO daoPes = new PesagemDAO();
            ControllerProducao ctr = new ControllerProducao();
            listaPesagens = daoPes.buscapesagensMontagem(codMaquina);
            if(listaPesagens != null){
                DefaultTableModel modelo = (DefaultTableModel)jTableProducaoArrebentamentos.getModel();
                for(int i=0;i<listaPesagens.size();i++){
                    ctr.AddicionarMetragensObservacao(listaPesagens.get(i).getObservacao(),listaPesagens.get(i).getMetragemOperador(),listaPesagens.get(i).getCodEmbalagem());
                }

                metrosAlerta = ctr.getListaMetragemObservacao();
                for (int i=0;i<metrosAlerta.size();i++){
                    dado = metrosAlerta.get(i).split("#");
                    modelo.addRow(new Object[]{String.valueOf(i+1),Integer.valueOf(dado[0]),dado[1]});
                }
                TableRowSorter tableSorter = new TableRowSorter(modelo);
                jTableProducaoArrebentamentos.setRowSorter(tableSorter);
                tableSorter.toggleSortOrder(1);

                configuraMostradoresSaldoEntrada(listaPesagens);
            }
        } catch (NumberFormatException e) {
            erro.gravaErro(e);
        }
    }
    
    private void configuraMostradoresSaldoEntrada(List<Pesagem> lista){
        try {                    
            if(lista.size()==2){
                displaySingleSaldoCarretelEntrada1.setValue(lista.get(0).getSaldoConsumo());
                jLabelCarretel1.setText("Saldo Carretel de entrada: " + lista.get(0).getCodEmbalagem());
                displaySingleSaldoCarretelEntrada2.setVisible(true);
                displaySingleSaldoCarretelEntrada2.setValue(lista.get(1).getSaldoConsumo());
                jLabelCarretel2.setText("Saldo Carretel de entrada: " + lista.get(1).getCodEmbalagem());
            }else{
                displaySingleSaldoCarretelEntrada1.setValue(lista.get(0).getSaldoConsumo());
                jLabelCarretel1.setText("Saldo Carretel de entrada: " + lista.get(0).getCodEmbalagem());
                //displaySingleSaldoCarretelEntrada2.setVisible(false);
                jPanelEventoEntrada3.setVisible(false);
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {            
        try {                    
            if(e.getActionCommand().trim().equals("")) return;
            System.out.println(e.getActionCommand());
            if(e.getActionCommand().substring(0,3).equalsIgnoreCase("TAG")){
                tagEvent(e.getActionCommand());
            }else{
                if(e.getActionCommand().equals("**********  Troca de bobina  - Novo relatorio  **********")){
                    Object[] options = { "Sim", "Não" }; 
                    int i = JOptionPane.showOptionDialog(null, "Deseja realizar uma troca de carretel de saída?", 
                            "Troca de carretel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                            null, options, options[0]); 
                    if (i == JOptionPane.YES_OPTION) {
                        System.out.println("Apontar pesagem e trocar carretel de saida");                         
                        
                        JFApontamentoProducao apt = new JFApontamentoProducao(maquina,login,prodmaq,prodCar,prod,prog);
                        apt.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        apt.setVisible(true);
                    }else{
                        leituraAnterior.setMetragem(0);
                        System.out.println("Metragem leitura anterior: " + leituraAnterior.getMetragem());
                        return;
                    }
                }
                dadosSerialMicrometro(e.getActionCommand());
            }
        } catch (HeadlessException ex) {            
            erro.gravaErro(ex);
        }
    }
    
    private void dadosSerialMicrometro(String dados){
        double tempoCalculadoSistema;
        double metrosProduzidos;
        double velocidade;
        try {                                
            if(!iniciaLeituras){
                double metAnterior = (double) leituraAnterior.getMetragem();
                leituraAtual = mic.setarDadosMicrometro(dados);               
                tempoCalculadoSistema = (double)(System.currentTimeMillis() - tempoSistema);                
                double metAtual = (double) leituraAtual.getMetragem();
                
                if(metAnterior < metAtual) {
                    metrosProduzidos = metAtual - metAnterior;
                    atualizarMostradoresMetragem(metrosProduzidos);                    
                    velocidade = (metrosProduzidos/(tempoCalculadoSistema/1000.0));
                    tempoSistema = System.currentTimeMillis();
                    ControllerProducao prd = new ControllerProducao();
                    if(prd.atualizaMetragemProduzida(listaPesagens, metrosProduzidos, codMaquina)){
                        leituraAnterior = mic.setarDadosMicrometro(dados);  
                        double velMediana = mediaVelocidade(velocidade*60);                    
                        resumoRelatorio = 0;
                        if(velMediana>radialLcdVelocidade.getMaxValue()){
                            radialLcdVelocidade.setValue(radialLcdVelocidade.getMaxValue());
                        }else{
                            radialLcdVelocidade.setValue(velMediana);                             
                        }                    
                        if(maqParada)if(velMediana>=(radialLcdVelocidade.getTrackStart() + 5))registrarRetornoEvento();
                        eventosTimer = 0;
                        if(velMediana < radialLcdVelocidade.getTrackStart() - 5){
                            System.out.println("Parada por velocidade abaixo da minima");
                            if(!maqParada){
                                abrirTelaParadas();
                            }
                        }
                        atualizarMostradoresDiametro();
                        gerenciarColetorAmostraDiametro(metrosProduzidos);
                    }
                }else{
                    if(resumoRelatorio >= linhas){
                        if(radialLcdVelocidade.getValue()>0)radialLcdVelocidade.setValueAnimated(mediaVelocidade(0));
                        if (radialLcdVelocidade.getValue()==0){
                            System.out.println("Parada!!!");
                            if(!maqParada)abrirTelaParadas();
                        } 
                    }else{
                        resumoRelatorio ++;
                        System.out.println("Resumo relatoio: " + resumoRelatorio);
                    }
                }
            }else{            
                iniciarLeiturasSerial(dados);
            }
            
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }
    private void iniciarLeiturasSerial(String dados){
        try {
            leituraAnterior = mic.setarDadosMicrometro(dados);
            leituraAtual = mic.setarDadosMicrometro(dados);
            tempoSistema = System.currentTimeMillis();
            iniciaLeituras = false;
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }
    private double mediaVelocidade(double ultimaVel){
        double media=0;
        int interacaoes=0;
        try {                    
            for (int i=8;i>=0;i--){
                mediaVel[i+1]=mediaVel[i];    
            } 
            mediaVel[0]=ultimaVel;
            for (int i=0;i<=9;i++){
                media = media + mediaVel[i];
                interacaoes = interacaoes + 1;

            }
            media = media / interacaoes;            
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return media;
    }
    private void tagEvent(String tag){    
        try {                    
            String code=tag.substring(3,tag.length());
            if(!login.getCode().equals(code)){            
                if(!login.getCode().trim().equals("")){
                    code = login.getCode();
                }
                login.setCode(code);              
                if(login.logarCode(login)){
                    usuariologado();            
                }else{
                    login.setCode(code);
                    JOptionPane.showMessageDialog(null, "Codigo de Identificação não vinculado","Codigo invalido",JOptionPane.ERROR_MESSAGE);
                }        
            }
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }
    
    private void usuariologado() {
        try {                    
            if(!login.getNome().trim().equals("")) bloquearMenu();
                habilitarMenu();
                System.out.println("Logado com " + login.getNome()
                    + " e nivel de permissão: " + login.getNivel());             
                jMenuItemSair.setEnabled(true);
                jMenuItemLogin.setEnabled(false);
                if(maqParada){
                    abrirTelaParadas();
                }else{
                    abrirTelaProducao();           
                }
                ControllerEventosSistema ctr = new ControllerEventosSistema();            
                ctr.registraEventos(11,login.getCodigoOperador(),0,(int)displaySingleMetragemCarretel.getValue(),codMaquina,jLabelProducaoOF.getText()); 
                ControllerReservaMaquina ctrRes = new ControllerReservaMaquina();
                if(ctrRes.registrarOperadorMaquina(login, codMaquina)){
                    System.out.println("Tabela reserva maquina atualizada");
                }
            } catch (Exception e) {
                erro.gravaErro(e);
        }
    }

    private void ajustarMostradorVelocidade() {
        try {
            double maxVel = (double) prodmaq.getVelocidade() * 2;
            double alvoVel = (double) prodmaq.getVelocidade();
            double startTrack = (double) (alvoVel * maquina.getAlertaPercentualVelocidade());
            double rangeVel = (double) (maxVel - (2 * startTrack));
            System.out.println("Unidade: " + prodmaq.getUnidade());
            radialLcdVelocidade.setUnitString(prodmaq.getUnidade());
            radialLcdVelocidade.setTitle("Velocidade");
            radialLcdVelocidade.setMaxMeasuredValueVisible(true);
            radialLcdVelocidade.setMaxValue(maxVel);
            radialLcdVelocidade.setTrackStart(startTrack);
            radialLcdVelocidade.setTrackRange(rangeVel); 
            radialLcdVelocidade.setTrackSection(alvoVel);
            radialLcdVelocidade.setThreshold(alvoVel);
            
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }

    private void ajustarMostradoresMetragem() {
        try {
            double metAlvo;
            double metProgramada = (double) prog.getMetragemProgramada();
            double metMaxima = (double) prodCar.getMetragemMaxima();
            double metProduzida = (double) prod.getMetragemProduzida();
            double metTotalProduzida = Double.valueOf(jLabelProducaoMetTotalProd.getText());
            metTotalProduzida = metTotalProduzida + metProduzida;
            double metTotalProgramada = Double.valueOf(jLabelProducaoMetTotalProg.getText());
            if(metProgramada>metMaxima){
               metAlvo = (100);
            }else{
               metAlvo = ((metProgramada / metMaxima) * 100);
            }            
            //System.out.println("Met Alvo: " + metAlvo);            
            linearCarretelSaida.setThreshold(metAlvo);
            linearCarretelSaida.setThresholdVisible(true);
            if(metProduzida>metMaxima){
                linearCarretelSaida.setValue(100);
            }else{
                linearCarretelSaida.setValue((metProduzida/metMaxima)*100);
            }            
            displaySingleMetragemCarretel.setValue(metProduzida);
            displaySingleMetragemProgramado.setValue(metTotalProduzida);
            double percProduzido = (metTotalProduzida/metTotalProgramada) * 100;
            linearProgramacao.setValueAnimated(percProduzido);
            atualiMostradorParadaEntrada();
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }
    
    private void atualizarMostradoresMetragem( double metrosProduzidos){
        try {       
            if(metrosProduzidos>0){
                double prodPerc;                
                displaySingleMetragemCarretel.setValue(displaySingleMetragemCarretel.getValue() + metrosProduzidos);
                displaySingleMetragemProgramado.setValue(displaySingleMetragemProgramado.getValue() + metrosProduzidos);
                prodPerc = (displaySingleMetragemProgramado.getValue() / prog.getMetragemTotalProgramada()) * 100;
                if(prodPerc>100){
                    linearProgramacao.setValue(100);            
                }else{
                    linearProgramacao.setValue(prodPerc);            
                }
                prodPerc = (displaySingleMetragemCarretel.getValue() / prodCar.getMetragemMaxima())*100;
                if(prodPerc>100){
                    linearCarretelSaida.setValue(100);
                }else{
                    linearCarretelSaida.setValue(prodPerc);
                }                
                displaySingleSaldoCarretelEntrada1.setValue(displaySingleSaldoCarretelEntrada1.getValue() - metrosProduzidos);
                displaySingleSaldoCarretelEntrada2.setValue(displaySingleSaldoCarretelEntrada2.getValue() - metrosProduzidos);
                atualiMostradorParadaEntrada();                
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    private void atualiMostradorParadaEntrada() {
        /*
        Todo o codigo comentado serve para mudar a forma como são detectados os eventos na entrada,
        com ou sem a converssão da metragem apontada pelo praduzida
        */
        double metrosConsu1=0;
        double metrosConsu2=0;
        //double Alerta1=999999;
        //double Alerta2=999999;
        double Alerta1=0;
        double Alerta2=0;
        String dado[] = new String[2];
        try {
            if(listaPesagens.size() == 2){
                //metrosConsu1 = listaPesagens.get(0).getMetragemOperador() - displaySingleSaldoCarretelEntrada1.getValue();
                //metrosConsu2 = listaPesagens.get(1).getMetragemOperador() - displaySingleSaldoCarretelEntrada2.getValue();
                metrosConsu1 = displaySingleSaldoCarretelEntrada1.getValue();
                metrosConsu2 = displaySingleSaldoCarretelEntrada2.getValue();
            }else{
                //metrosConsu1 = listaPesagens.get(0).getMetragemOperador() - displaySingleSaldoCarretelEntrada1.getValue();
                metrosConsu1 = displaySingleSaldoCarretelEntrada1.getValue();
            }
            for (int i=0;i<metrosAlerta.size();i++){
                dado = metrosAlerta.get(i).split("#");
                if(dado[1].equals(listaPesagens.get(0).getCodEmbalagem())){
                    //if(metrosConsu1<Double.valueOf(dado[0])){
                    if(metrosConsu1>Double.valueOf(dado[0])){
                        //if(Alerta1>Double.valueOf(dado[0]))
                        if(Alerta1<Double.valueOf(dado[0]))
                            Alerta1 = Double.valueOf(dado[0]);
                    }
                }
            }
            if(listaPesagens.size() == 2){
                for (int i=0;i<metrosAlerta.size();i++){
                    dado = metrosAlerta.get(i).split("#");
                    if(dado[1].equals(listaPesagens.get(1).getCodEmbalagem())){
                        //if(metrosConsu2<Double.valueOf(dado[0])){
                        if(metrosConsu2>Double.valueOf(dado[0])){
                            //if(Alerta2>Double.valueOf(dado[0]))
                            if(Alerta2<Double.valueOf(dado[0]))
                                Alerta2 = Double.valueOf(dado[0]);
                        }
                    }
                }
            }
            //if(Alerta1==999999 && Alerta2==999999){
            if(Alerta1==0 && Alerta2==0){
                displaySingleEvtCarEntrada.setValue(0);
                jLabelEvtEntrada.setText("Sem Eventos na entrada");
                        
            }else{
                //if((Alerta1-metrosConsu1)<(Alerta2-metrosConsu2)){
                if((metrosConsu1-Alerta1)>(metrosConsu2-Alerta2)){
                    //displaySingleEvtCarEntrada.setValue(Alerta1-metrosConsu1);
                    displaySingleEvtCarEntrada.setValue(metrosConsu1 - Alerta1);
                    jLabelEvtEntrada.setText("Evento Carretel de entrada: " + listaPesagens.get(0).getCodEmbalagem());
                }else{
                    //displaySingleEvtCarEntrada.setValue(Alerta2-metrosConsu2);
                    displaySingleEvtCarEntrada.setValue(metrosConsu2 - Alerta2);
                    jLabelEvtEntrada.setText("Evento Carretel de entrada: " + listaPesagens.get(1).getCodEmbalagem());
                }
            }

        } catch (NumberFormatException e) {
            erro.gravaErro(e);
        }
    }

    private void registrarRetornoEvento() {
        try {                    
            if(paradas.registraRetornoParadamaquina((long) displaySingleMetragemCarretel.getValue(), codMaquina)){
                maqParada = false;
                eventosTimer = 0;            
                bloquearMenuligaDesliga();                       
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    private void registrarMotivoParadas() {                
        try {
            ControllerEventosSistema ctr = new ControllerEventosSistema();
            List<Paradas> lista = new ArrayList<>();
            lista = ctr.BuscaPreApontamentos(codMaquina);
            for(int i=0;i<lista.size();i++){
                if(!paradas.registraMotivoParadaMaquina(String.valueOf(lista.get(i).getCodigo()),
                        lista.get(i).getObservacao(),lista.get(i).getCodPesagemSaida(),lista.get(i).getCodPesagemEntrada())){                                        
                    JOptionPane.showMessageDialog(rootPane,"falha ao registrar motivo da parada","Falha no Registro",JOptionPane.ERROR_MESSAGE);
                }
            }
            ctr.removerPreApontamentosRegistrados(codMaquina);
        } catch (HeadlessException e) {
            erro.gravaErro(e);
        }
    }

    private void buscarParadasProcessoProducao() {
        try {
            DefaultTableModel table = (DefaultTableModel) jTableProducaoParadas.getModel();
            if(paradas==null) paradas = new ControllerParadasMaquina(codMaquina);  
            ParadasMaquina paradasProcesso = this.paradas.buscaParadasProcessoAtual(codMaquina);
            for (int i=0;i<paradasProcesso.getListaParadas().size();i++){
                String obs ="";
                if(paradasProcesso.getListaParadas().get(i).getCodPesagemSaida()!=0){
                    obs = String.valueOf(paradasProcesso.getListaParadas().get(i).getCodPesagemSaida());
                }
                if(paradasProcesso.getListaParadas().get(i).getObservacao()!=null){
                    if(!paradasProcesso.getListaParadas().get(i).getObservacao().trim().equals("")){
                        if(obs.trim().equals("")){
                            obs = paradasProcesso.getListaParadas().get(i).getObservacao();
                        }else{
                            obs = obs + " - " + paradasProcesso.getListaParadas().get(i).getObservacao();
                        }
                    }
                }
                table.addRow(new Object[]{String.valueOf(i + 1),paradasProcesso.getListaParadas().get(i).getCodigo(),
                    paradasProcesso.getListaParadas().get(i).getAbreviacao(), 
                    obs});
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }

    private void desbloquearMenuLigaDesliga() {
        jMenuItemDesligar.setEnabled(true);
        jMenuItemReiniciar.setEnabled(true);
    }
    private void bloquearMenuligaDesliga(){
        jMenuItemDesligar.setEnabled(false);
        jMenuItemReiniciar.setEnabled(false);
    }    

    private void atualizarMostradoresDiametro() {
        try {                    
            double diametro = (double)leituraAtual.getDiametroMedio();
            if(diametro<radialLcdDiametro.getMinValue()){
                radialLcdDiametro.setValue(radialLcdDiametro.getMinValue());
            }else{
                radialLcdDiametro.setValue(diametro);
            }
            
            if(diametro < prog.getProduto().getDiametroMinimo()){
                if(!evtDiaMin){
                    ControllerEventosSistema ctr = new ControllerEventosSistema();
                    evtDiaMin = ctr.registraEventos(5,login.getCodigoOperador(),diametro,
                            (int)displaySingleMetragemCarretel.getValue(),codMaquina,jLabelProducaoOF.getText());
                }
            }else{
                evtDiaMin = false;
            }
            if (diametro>prog.getProduto().getDiametroMaximo()){
                if(!evtDiaMax){
                    ControllerEventosSistema ctr = new ControllerEventosSistema();
                    evtDiaMax = ctr.registraEventos(6,login.getCodigoOperador(),diametro,
                            (int)displaySingleMetragemCarretel.getValue(),codMaquina,jLabelProducaoOF.getText());
                }
            }else{
                evtDiaMax = false;
            }
        } catch (Exception e) {
            erro.gravaErro(e);
        }
    }

    private void configurarMostradoresDiametro() {
        try {
            DecimalFormat formato = new DecimalFormat("#.##");  
            double range = prog.getProduto().getDiametroMaximo() - prog.getProduto().getDiametroMinimo();
            double diametoAlvo = prog.getProduto().getDiametroNominal();
            double diametroMax = prog.getProduto().getDiametroMaximo()+(range/2);
            String formatado = formato.format(diametroMax);
            diametroMax = Double.valueOf(formatado.replace(",","."));
            double startTrack = prog.getProduto().getDiametroMinimo();
            double diametroMinimo = startTrack-(range/2);
            formatado = formato.format(diametroMinimo);
            diametroMinimo = Double.valueOf(formatado.replace(",","."));            
            radialLcdDiametro.setTrackStart(startTrack);
            radialLcdDiametro.setTrackRange(range);
            radialLcdDiametro.setMaxValue(diametroMax);
            radialLcdDiametro.setTrackSection(diametoAlvo);
            radialLcdDiametro.setThreshold(diametoAlvo);   
            radialLcdDiametro.setMinValue(diametroMinimo);
            radialLcdDiametro.setUnitString("mm");
            radialLcdDiametro.setTitle("Diametro");            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }

    private void verificarMotivosPreApontados() {
        List<Paradas> preParadas = null;
        try {
            ControllerEventosSistema ctr = new ControllerEventosSistema();
            preParadas = ctr.BuscaPreApontamentos(codMaquina);
            verificaParadasInteracaoOperador(preParadas);
            if(preParadas!=null){
                DefaultTableModel modelo = (DefaultTableModel)jTableMotivosParada.getModel();
                for (int i=0;i<preParadas.size();i++){   
                    modelo.addRow(new Object[]{preParadas.get(i).getCodigo(),preParadas.get(i).getAbreviacao()
                            ,preParadas.get(i).getDescricao(),preParadas.get(i).getObservacao()});                    
                }                
            }
        } catch (NumberFormatException e) {
            erro.gravaErro(e);
        }
    }

    private void verificaParadasInteracaoOperador(List<Paradas> preParadas) {
        for (int i=0;i<preParadas.size();i++){
            if(preParadas.get(i).getCodigo()==1){
                JOptionPane.showMessageDialog(null,"Detectada a necessidade de troca do carretel de saída \n "
                        + "Por favor verifique.","Troca do carretel de saida.",JOptionPane.INFORMATION_MESSAGE);
            }
            if(preParadas.get(i).getCodigo()==2){
                Object[] options = { "Sim", "Não" }; 
                int q = JOptionPane.showOptionDialog(null, "Detectada a necessidade de troca do corretel de entrada \n"
                        + "Deseja realizar a troca agora?",
                        "Troca de carretel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                        null, options, options[0]); 
                if (q == JOptionPane.YES_OPTION) {
                    //System.out.println("Trocar carretel de entrada");
                    JFDTrocaCarretelEntrada trc = new JFDTrocaCarretelEntrada(this,true);
                    for(int j=0;j<listaPesagens.size();j++){
                        if(preParadas.get(i).getCodPesagemSaida()==Integer.valueOf(listaPesagens.get(j).getCodigo())){
                            trc.setPesSaida(listaPesagens.get(j));
                        }                            
                    }
                    trc.setMetragem((int) displaySingleMetragemCarretel.getValue());
                    trc.setCodMaquina(codMaquina);
                    trc.setLote(prod.getLoteProducao());
                    trc.buscaItensAlternativosMontagem(prod.getItemProducao());
                    trc.setVisible(true);
                    if(trc.getReturnStatus()!=1){                        
                        ControllerEventosSistema ctr = new ControllerEventosSistema();
                        ctr.removerPreApontamentoPodID(preParadas.get(i).getIdRegistro());
                        preParadas.remove(i);
                    }else{
                        ControllerEventosSistema ctr = new ControllerEventosSistema();
                        ctr.atualizaCarretelEntradaPreParada(Integer.valueOf(trc.getPesSaida().getCodigo()),
                                Integer.valueOf(trc.getPesEntrada().getCodigo()));
                    }
                    //System.out.println("Saiu da troca");
                }else{                        
                    ControllerEventosSistema ctr = new ControllerEventosSistema();
                    ctr.removerPreApontamentoPodID(preParadas.get(i).getIdRegistro());
                    preParadas.remove(i);
                }
            }
        }
    }

    private void gerenciarColetorAmostraDiametro(double metrosProduzidos) {
        try {
            if(metrosRelatorio<=maquina.getMetrosAmostraDiametro()){
                metrosRelatorio = metrosRelatorio + (int)metrosProduzidos;
                relatorio.add(leituraAtual);
            }else{
                //System.out.println("hora de Agir..");
                double diaMinAmostra=0.0;
                double diaMaxAmostra=0.0;
                double diaMedAmostra=0.0;
                double desvioMedio = 0.0;
                for (int i=0;i<relatorio.size();i++){
                    diaMinAmostra = diaMinAmostra + relatorio.get(i).getDiametroMinimo();
                    diaMedAmostra = diaMedAmostra + relatorio.get(i).getDiametroMedio();
                    diaMaxAmostra = diaMaxAmostra + relatorio.get(i).getDiametroMaximo();
                    desvioMedio = desvioMedio + relatorio.get(i).getDesvio();
                }
                Micrometro dados =  new Micrometro();
                dados.setDiametroMinimo((float)(diaMinAmostra / relatorio.size()));
                dados.setDiametroMedio((float)(diaMedAmostra / relatorio.size()));
                dados.setDiametroMaximo((float)(diaMaxAmostra / relatorio.size()));
                dados.setDesvio((float)(desvioMedio / relatorio.size()));
                ControllerMicrometro ctr = new ControllerMicrometro();
                if(ctr.registraRelatorioMicrometro(dados, codMaquina, prog.getLoteproducao(),
                        (int) displaySingleMetragemCarretel.getValue())){
                    relatorio.clear();
                    metrosRelatorio=0;
                }            
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
    }
}                                 
