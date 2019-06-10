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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Apenas para OrangePi 
 * @author renato.soares
 */
public class MemoryInfo {
    private long totalMemory;
    private long usedMemory;
    private long freeMemory;
    private long sharedMemory;
    private long memoryBuffers;
    private long cachedMemory;
    
    public MemoryInfo() {
        try {
            try {            
                PlatformManager.setPlatform(Platform.ORANGEPI);
            } catch (PlatformAlreadyAssignedException ex) {
                Logger.getLogger(MemoryInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
            totalMemory=SystemInfo.getMemoryTotal();
            usedMemory=SystemInfo.getMemoryUsed();
            freeMemory=SystemInfo.getMemoryFree();
            sharedMemory=SystemInfo.getMemoryShared();
            memoryBuffers=SystemInfo.getMemoryBuffers();
            cachedMemory=SystemInfo.getMemoryCached();
            
        } catch (IOException ex) {
            Logger.getLogger(MemoryInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MemoryInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(MemoryInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getSharedMemory() {
        return sharedMemory;
    }

    public long getMemoryBuffers() {
        return memoryBuffers;
    }

    public long getCachedMemory() {
        return cachedMemory;
    }
}
