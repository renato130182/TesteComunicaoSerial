/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

/**
 *
 * @author renato.soares
 */
public class PlataforInfo {
    private String name;
    private String plataformId;

    public PlataforInfo() throws PlatformAlreadyAssignedException{       
        PlatformManager.setPlatform(Platform.ORANGEPI);
        name=PlatformManager.getPlatform().getLabel();
        plataformId=PlatformManager.getPlatform().getId();              
    }

    public String getName() {
        return name;
    }

    public String getPlataformId() {
        return plataformId;
    }        
}
