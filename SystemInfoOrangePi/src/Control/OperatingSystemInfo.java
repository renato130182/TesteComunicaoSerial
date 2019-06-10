/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.SystemInfo;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author renato.soares
 */
public class OperatingSystemInfo {
    private String name;
    private String osVersion;
    private String osArchitecture;

    public OperatingSystemInfo(){
        try {
            try {            
                PlatformManager.setPlatform(Platform.ORANGEPI);
            } catch (PlatformAlreadyAssignedException ex) {
                Logger.getLogger(OperatingSystemInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
            name=SystemInfo.getOsName();
            osVersion=SystemInfo.getOsVersion();
            osArchitecture=SystemInfo.getOsArch();        
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(MemoryInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsArchitecture() {
        return osArchitecture;
    }

}
