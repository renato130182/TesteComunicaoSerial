/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geradorrelatorioindisponibilidademanutencao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author renato.soares
 */
public class ConexaoMysql {
    private static volatile Connection conexao=null;
    private static final String DRIVERNAME="com.mysql.jdbc.Driver";
    private static final String MYDATABASE="condumigproducao";
    private static final String URL="jdbc:mysql://";
    private static final String SERVERNAME="192.168.1.74:3306";
    private static final String USERNAME="renatoinf";
    private static final String PSW="vitor@775";
    
    private static  Connection obterInstancia() throws SQLException{
        try {
            Class.forName(DRIVERNAME).newInstance();                       
            conexao = DriverManager.getConnection(URL + SERVERNAME + "/" + MYDATABASE ,USERNAME,PSW);
            
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.out.println("Falha ao conectar com banco de dados Mysql");
        }        
        return conexao;
    }
    
    public static Connection obterConexao() throws SQLException{
        if(conexao==null){
            synchronized(ConexaoMysql.class){
                if(conexao==null){
                    conexao = obterInstancia();
                }
            }            
        }        
        return conexao;
    }
}
