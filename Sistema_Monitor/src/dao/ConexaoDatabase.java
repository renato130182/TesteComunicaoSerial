/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.pi4j.system.NetworkInfo;
import controller.ControllerUtil;
import controller.CriptoCode;
import controller.LogErro;
import controller.ManipuladorArquivo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.DadosConexao;

/**
 *
 * @author renato.soares
 */
public class ConexaoDatabase extends DadosConexao{
    private Connection conexao = null;

    private static final boolean AMBPROD = false;
    private boolean infoDB=false;
    LogErro erro = new LogErro();
    
    public ConexaoDatabase() {   
        try {                    
            if(buscaDadosConexao()){
                infoDB = true;
            }else{
                infoDB = false;
                JOptionPane.showMessageDialog(null, "Não foi possivel buscar dados para conexão,"
                        + " Arquivo .cnf não encontrado","Conexão com Banco de dados",JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            erro.gravaErro(e);
            infoDB= false;
        }
    }
    
    private boolean buscaDadosConexao(){
        try{
            ManipuladorArquivo man = new ManipuladorArquivo();
            if(AMBPROD){
                man.setArquivo(DadosDefaultDAO.getARQDBPROD());               
            }else{
                man.setArquivo(DadosDefaultDAO.getARQDBTESTE());
            }
            //System.out.println("Arquivo: " + man.getArquivo());
            man.BuscarArquivo();
            String arqCripto = man.getDados().trim();        
            String[] dadosConexao = CriptoCode.decrypt(CriptoCode.converterStringByte(arqCripto, " ")).split(";");
            //System.out.println("Dados da conexao: " + dadosConexao[3]);
            if(dadosConexao.length==6){
                this.driverName=dadosConexao[0];
                this.myDatabase=dadosConexao[1];
                this.password=dadosConexao[2];
                this.serverName=dadosConexao[3];
                this.url=dadosConexao[4];
                this.userName=dadosConexao[5];
                return true;
            }else{
                System.out.println("Falha ao buscar informações da conexão!!");
                return false;
            }           
        }catch(Exception e){
            erro.gravaErro(e);
            return false;
        }
    }
    
     public java.sql.Connection getConnection() {                    
         try {
            Class.forName(driverName);
            conexao = DriverManager.getConnection(url + serverName + "/" + myDatabase ,userName,password);                
            //testando a conexão
            if(conexao != null){
                System.out.println("Conexão realizada com banco de dados: " + this.serverName );
            }else{
                System.err.println("Não foi possivel realizar a conexão com banco de dados" + this.serverName);
            }                                    
            return conexao;
        } catch (SQLException e){
            if(!ControllerUtil.testaConexao(serverName)){
                JOptionPane.showMessageDialog(null, "Falha na conexão com servidor: " + serverName + "\n" +
                    "Por favor informe ao setor de informática","Falha na conexão",JOptionPane.ERROR_MESSAGE);                
            }
            erro.gravaErro(e);
            return null;
        } catch (ClassNotFoundException ex) {
            erro.gravaErro(ex);
        }
    return null;
    }
    public void desconectar(){
        if(this.conexao != null){
            try {
                conexao.close();
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
        }
    }

    public boolean isInfoDB() {
        return infoDB;
    }
}
