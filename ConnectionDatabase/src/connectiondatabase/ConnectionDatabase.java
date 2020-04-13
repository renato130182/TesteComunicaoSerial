/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectiondatabase;

import View.JDLogin;

/**
 *
 * @author renato.soares
 */
public class ConnectionDatabase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
             // TODO code application logic here
             JDLogin login = new JDLogin(null, true);
             login.setLocationRelativeTo(null);
             login.setVisible(true);
    }
    
}
