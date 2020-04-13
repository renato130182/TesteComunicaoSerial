/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;





import Dao.DadosDefaultDAO;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Model.DadosConexao;

/**
 *
 * @author renato.soares
 */
public class ControllerConexaoDatabase extends DadosConexao{
    
    private static final boolean AMBPROD = false;
    private Connection conexao = null;
    private boolean infoDB=false;
    
    public ControllerConexaoDatabase() {   
        try {                    
            if(buscaDadosConexao()){
                infoDB = true;
            }else{
                infoDB = false;                
            }
        } catch (HeadlessException e) {
            infoDB= false;
        }
    }
    public ControllerConexaoDatabase(DadosConexao con) {
        super.setDriverName(con.getDriverName());
        super.setMyDatabase(con.getMyDatabase());
        super.setPassword(con.getPassword());
        super.setServerName(con.getServerName());
        super.setUrl(con.getUrl());
        super.setUserName(con.getUserName());
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
            if(!man.BuscarArquivo())return false;
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
            return false;
        }
    }
    
     public java.sql.Connection getConnection() {                    
         try {
            if(ControllerUtil.testaConexao(serverName)){
                Class.forName(driverName);
                conexao = DriverManager.getConnection(url + serverName + "/" + myDatabase ,userName,password);                
                //testando a conexão
                if(conexao != null){
                    System.out.println("Conexão realizada com banco de dados: " + this.serverName );
                }else{
                    System.err.println("Não foi possivel realizar a conexão com banco de dados" + this.serverName);
                    return null;
                }                                    
                return conexao;
            }else{
                System.out.println("Falha na conexão INTRANET");
                return null;
            }
        } catch (SQLException | ClassNotFoundException e){                           
            System.out.println("Falha ao conectar com banco de dados");            
            //erro.gravaErro(e);
            return null;
        }
    }
    public void desconectar(){
        if(this.conexao != null){
            try {
                conexao.close();
            } catch (SQLException ex) {
            }
        }
    }

    public boolean isInfoDB() {
        return infoDB;
    }
    
    
}
