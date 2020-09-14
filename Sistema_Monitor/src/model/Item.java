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
public class Item implements Cloneable{
    private long codigo;
    private String descricao;

    public Item(long codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public Item(long codigo) {
        this.codigo = codigo;
    }
    
    public Item (){        
    }
            
    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
    
}
