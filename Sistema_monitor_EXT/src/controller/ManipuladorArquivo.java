/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author renato.soares
 */
public class ManipuladorArquivo {
    private String dados;    
    private String arquivo;  
    LogErro erro = new LogErro();
    public ManipuladorArquivo() {
        //System.out.println("Iniciando manipulador de arquivos");
        dados = "";
        arquivo ="";
    }
    
    
    public String getDados() {     
        return dados;
    }

    public void setDados(String dados) {
        if(dados==null){
            this.dados = "";
        }else{
            this.dados = dados;
        }
    }
    
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
            
        
    public void BuscarArquivo(){
        File f = new File(this.arquivo);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            this.setDados(br.readLine());  
            
        } catch (IOException ex) {
            ex.printStackTrace();
            erro.gravaErro(ex);
        }
    }
    
    public void escreverArquivo (){
     
        try {                   
            BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.arquivo));
            buffWrite.append(this.dados);
             buffWrite.close();
        } catch (FileNotFoundException e) {
            erro.gravaErro(e);
        } catch (IOException ex) {
            erro.gravaErro(ex);
        }                    
        
    }

}
