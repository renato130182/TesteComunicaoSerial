/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author renato.soares
 */
public class Usuario {
    private String nome;
    private String usuario;
    private String Senha;
    private String code;
    private String nivel;
    private String codigoOperador;
    private String dataHoraLogin;
    private String codigoEncarregado;
    private String nomeEncarregado;
    private int metProduzida;

    public int getMetProduzida() {
        return metProduzida;
    }

    public void setMetProduzida(int metProduzida) {
        this.metProduzida = metProduzida;
    }

    public String getCodigoEncarregado() {
        return codigoEncarregado;
    }

    public void setCodigoEncarregado(String codigoEncarregado) {
        this.codigoEncarregado = codigoEncarregado;
    }

    public String getNomeEncarregado() {
        return nomeEncarregado;
    }

    public void setNomeEncarregado(String nomeEncarregado) {
        this.nomeEncarregado = nomeEncarregado;
    }

    public String getDataHoraLogin() {
        return dataHoraLogin;
    }

    public void setDataHoraLogin(String dataHoraLogin) {
        this.dataHoraLogin = dataHoraLogin;
    }
    
    public String getCodigoOperador() {
        return codigoOperador;
    }

    public void setCodigoOperador(String codigoOperador) {
        this.codigoOperador = codigoOperador;
    }

    public Usuario() {
        this.nome = "";
        this.Senha = "";
        this.code = "";
        this.usuario = "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String Senha) {
        this.Senha = Senha;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
    
}
