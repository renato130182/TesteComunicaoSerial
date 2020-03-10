/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 * @author renato.soares
 */
public final class LogErro {

    public void gravaErro(Throwable erro) {
        PrintWriter out = null;
        String file = "Erro" + new Date().toString().replace(":", "_") + ".txt" ;
        file = file.replace(" ", "_");
        try {
            //abre o arquivo
            out = new PrintWriter(new FileWriter(file),true);
            //imprime o log 
            out.print("Data do erro: ");
            out.println(new Date());
            out.print("Mensagem de erro: ");
            out.println(erro.getMessage());
            out.print("Causa do erro: ");
            out.println(erro.getCause());
            out.print("Stacktrace: ");
            erro.printStackTrace(out);
            System.out.println(out);          
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //fecha o arquivo
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }    
}
