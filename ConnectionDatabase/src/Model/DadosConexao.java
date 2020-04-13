/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author renato.soares
 */
public class DadosConexao {
    protected String driverName;
    protected String serverName;
    protected String myDatabase;
    protected String url;
    protected String userName;
    protected String password;

    public DadosConexao() {
        this.driverName = "";
        this.serverName = "";
        this.myDatabase = "";
        this.url="";
        this.userName="";
        this.password="";
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getMyDatabase() {
        return myDatabase;
    }

    public void setMyDatabase(String myDatabase) {
        this.myDatabase = myDatabase;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
