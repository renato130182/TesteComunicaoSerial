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
public class ConexaoAcess {
    private static volatile Connection conexao=null;
    private static final String BANCODADOS= "\\\\192.168.1.82\\Manutenção\\ManWinWin.mdb";
    
    private static  Connection obterInstancia() throws SQLException{
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            //"jdbc:ucanaccess://" + System.getProperty( "user.dir" )  + "\\Your-database-name.<mdb or accdb>";
            String dsn = "jdbc:ucanaccess://" + BANCODADOS;
            conexao = DriverManager.getConnection(dsn);
            
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }        
        return conexao;
    }
    
    public static Connection obterConexao() throws SQLException{
        if(conexao==null){
            synchronized(ConexaoAcess.class){
                if(conexao==null){
                    conexao = obterInstancia();
                }
            }            
        }        
        return conexao;
    }
    
}
