/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author renato.soares
 */
public class ManipuladorArquivo {
    private String dados;    
    private String arquivo;  

    public ManipuladorArquivo() {
        //System.out.println("Iniciando manipulador de arquivos");
        dados = "";
        arquivo ="";
    }
    
    
    public String getDados() {     
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
    }
    
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
            
        
    public void BuscarArquivo(){
        try {
            
            try (BufferedReader buffer = new BufferedReader(new FileReader(this.arquivo))) {
                this.setDados(buffer.readLine());
                //System.out.println("dados bufferizados: " + this.getDados()) ;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManipuladorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void escreverArquivo (){
        try {
            try (BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.arquivo))) {   
                buffWrite.append(this.dados);
                //System.out.println("caminho gravado: " + this.arquivo);    
            }
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
