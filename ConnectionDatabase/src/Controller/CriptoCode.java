/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author renato.soares
 */
public class CriptoCode {
    private static final String KEYPRIVATE = "#Sistem@Condumig"; 
    private static final String IV = "CONDUMIGSISTEMAS";  // deve conter 16 caracteres
    
    public static byte [] encrypt(String dados){
        try {
            Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");
            SecretKeySpec key = new SecretKeySpec(KEYPRIVATE.getBytes("UTF-8"),"AES");
            encripta.init(Cipher.ENCRYPT_MODE,key,new IvParameterSpec(IV.getBytes("UTF-8")));
            return encripta.doFinal(dados.getBytes("UTF-8"));
           
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex);            
        }
        return null;       
    }
    
    public static String decrypt(byte[] dados){
        Cipher decripta;
        try {
            decripta = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");
            SecretKeySpec key = new SecretKeySpec(KEYPRIVATE.getBytes("UTF-8"),"AES");
            decripta.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(IV.getBytes("UTF-8")));
            return new String(decripta.doFinal(dados),"UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex);
        }                            
        return null;
    }
    
    public static byte [] converterStringByte(String dados,String separador){
        try {                    
            String dadosArquivo[] = dados.split(separador);
            byte[] dadocript = new byte[dadosArquivo.length];
            for (int i=0;i<dadosArquivo.length;i++){
                dadocript[i] = (byte)(Integer.parseInt(dadosArquivo[i]));
            }
            return dadocript;
        } catch (NumberFormatException e) {
            System.out.println(e);
            return null;
        }
    }
}
