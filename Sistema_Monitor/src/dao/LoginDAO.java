/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Usuario;

/**
 *
 * @author renato.soares
 */
public class LoginDAO{
    private String sql;
    private boolean logado;

    public boolean isLogado() {
        return logado;
    }
    
    public void validarLogin(Usuario us){
        sql = "SELECT nome,tipo FROM condumigproducao.usuario where codigo = "
                + "? and senha = md5(?); ";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, us.getUsuario());
                st.setString(2,us.getSenha());
                ResultSet res = st.executeQuery();
                if(res.next()){
                    us.setNome(res.getString("nome"));
                    us.setNivel(res.getString("tipo"));
                    this.logado = true;
                }else{
                    this.logado = false;
                }                    
            } catch (SQLException ex) {
                Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        db.desconectar();
    }    
}
