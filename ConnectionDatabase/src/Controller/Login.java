/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Dao.DadosDefaultDAO;
import Model.Usuario;



/**
 *
 * @author renato.soares
 */
public class Login extends Usuario{
    private String userDefault;
    private String passDefault;
    
    
    public boolean logar(Usuario us){
        try {                    
            if(buscaDadosDafault()){
                if(userDefault.equals(us.getUsuario()) && passDefault.equals(us.getSenha())){                    
                    return true;                
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
            
    private boolean buscaDadosDafault(){
        try {           
            //System.out.println("busncando dados default");
            ManipuladorArquivo man = new ManipuladorArquivo();
            man.setArquivo(DadosDefaultDAO.getUSERDEFAULT());
            //System.out.println("Nome do arquivo: " + man.getArquivo());
            if(!man.BuscarArquivo())return false;
            String arqCript = man.getDados().trim();
            //System.out.println("Dados Retornados: " + arqCript);
            String[] dadosUser = CriptoCode.decrypt(CriptoCode.converterStringByte(arqCript, " ")).split(";");            
            if(dadosUser.length==2){
                this.userDefault = dadosUser[0];
                this.passDefault = dadosUser[1];
                //System.out.println("Dados Descriptografados: " + this.userDefault + ", " + this.passDefault);
                return true;
            }else{
                System.out.println("Falha ao ler dados do usuario padrão");
                return false;                
            }
        } catch (Exception e) {            
            System.out.println(e);
            return false;
        }
    }
}
