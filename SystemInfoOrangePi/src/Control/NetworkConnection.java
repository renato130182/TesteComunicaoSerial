/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.NetworkInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Apenas para OrangePi
 * @author renato.soares
 */
public class NetworkConnection {
        private String nameHost;
        private String[] ipAddresses;
        private String[] fqdns;
        private String[] nameServers;
        
    public NetworkConnection() throws PlatformAlreadyAssignedException{
            
        try {
            PlatformManager.setPlatform(Platform.ORANGEPI);
            nameHost=NetworkInfo.getHostname();
            ipAddresses=NetworkInfo.getIPAddresses();
            fqdns=NetworkInfo.getFQDNs();
            nameServers=NetworkInfo.getNameservers();            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(NetworkConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String[] getIpAddresses() {
        return ipAddresses;
    }

    public String[] getFqdns() {
        return fqdns;
    }

    public String[] getNameServers() {
        return nameServers;
    }

    public String getNameHost() {
        return nameHost;
    }
    
}
