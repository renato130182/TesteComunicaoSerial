/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
            
        
    public boolean BuscarArquivo(){           
        try (BufferedReader buffer = new BufferedReader(new FileReader(this.arquivo))) {
            this.setDados(buffer.readLine());                        
            return true;
        } catch (FileNotFoundException  ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return false;
    }
    
    public void escreverArquivo (){
     
        try {                            
            BufferedWriter buffWrite = new BufferedWriter(new FileWriter(this.arquivo));
            buffWrite.write(this.dados);
            buffWrite.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException ex) {
            System.out.println(ex);
        }                            
    }
}
