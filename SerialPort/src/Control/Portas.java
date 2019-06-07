package Control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import gnu.io.*;
import java.util.Enumeration;

/**
 *
 * @author renato.soares
 */
public class Portas {
    private String[] portas;
    private String[] tipoPortas;
    
    
    public  String[] BuscarPortas(){
        Enumeration listaPortas;
        listaPortas=CommPortIdentifier.getPortIdentifiers();
        portas = new String[100];
        tipoPortas = new String[100];
        int i = 0;
        
        while (listaPortas.hasMoreElements()) {
            CommPortIdentifier numPorta = (CommPortIdentifier) listaPortas.nextElement();
            portas[i]=numPorta.getName();
            tipoPortas[i]=TipoPorta(numPorta.getPortType());
            i++;            
        }
        return portas;
    }
    
    public String TipoPorta(int tipoPorta){
        switch(tipoPorta){
            case CommPortIdentifier.PORT_SERIAL:
                return "Porta Serial";
            case CommPortIdentifier.PORT_I2C:
                return "Porta I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Porta Paralela";
            case CommPortIdentifier.PORT_RAW:
                return "Porta Raw";
            case CommPortIdentifier.PORT_RS485:
                return "Porta RS485";
            default:
                return "Porta Não identificada";
        }
    }

    public String[] getPortas() {
        return portas;
    }

    public String[] getTipoPortas() {
        return tipoPortas;
    }
    
}
