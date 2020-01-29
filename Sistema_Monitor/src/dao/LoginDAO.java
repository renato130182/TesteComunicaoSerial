/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.LogErro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Usuario;

/**
 *
 * @author renato.soares
 */
public class LoginDAO{
    private String sql;
    private boolean logado;
    LogErro erro = new LogErro();
    
    public boolean isLogado() {
        return logado;
    }
    
    public void validarLogin(Usuario us){
        sql = "SELECT usr.nome,usr.tipo,op.codigo,op.encarregado ,"
                + "enc.nome as nomeEncarregado,now() as dataHora "
                + "FROM condumigproducao.usuario usr "
                + "inner join condumigproducao.operador op on op.nome = usr.nome "
                + "left join condumigproducao.operador enc on op.encarregado = enc.codigo  "
                + "where usr.codigo = ? and usr.senha = md5(?);";
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
                    us.setCodigoOperador(res.getString("codigo"));
                    us.setCodigoEncarregado(res.getString("encarregado"));
                    us.setNomeEncarregado(res.getString("nomeEncarregado"));
                    us.setDataHoraLogin(res.getString("dataHora"));
                    this.logado = true;
                }else{
                    this.logado = false;
                }                    
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
            
        }
        db.desconectar();
    }    
    
    public void validarCodeLogin(Usuario us){
        sql = "SELECT usr.nome,usr.tipo,op.codigo FROM condumigproducao.usuario usr "
                + "inner join condumigproducao.operador op on op.nome = usr.nome where usr.coderfid = ?;";
        ConexaoDatabase db = new ConexaoDatabase();
        if(db.isInfoDB()){
            Connection conec = db.getConnection();
            try {
                PreparedStatement st = conec.prepareStatement(sql);
                st.setString(1, us.getCode());             
                ResultSet res = st.executeQuery();
                if(res.next()){
                    us.setNome(res.getString("nome"));
                    us.setNivel(res.getString("tipo"));
                    us.setCodigoOperador("codigo");
                    this.logado = true;
                }else{
                    this.logado = false;
                }                    
            } catch (SQLException ex) {
                erro.gravaErro(ex);
            }
            
        }
        db.desconectar();
    }
}
