/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author renato.soares
 */
public final class ControllerUtil {
        
        
    public static final boolean SoTemNumeros(String texto) { 
        try {                    
            if(texto.length()==0)return false;
            for (int i = 0; i < texto.length(); i++) { 
                if (!Character.isDigit(texto.charAt(i))) { 
                    return false; 
                } 
            } return true; 
        } catch (Exception e) {
            LogErro erro = new LogErro();
            erro.gravaErro(e);
            return false;
        }  
    }
}
