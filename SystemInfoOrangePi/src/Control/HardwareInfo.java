/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.system.SystemInfo;
import com.pi4j.system.SystemInfo.BoardType;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author renato.soares
 */
public class HardwareInfo {
    private String serialNumber;
    private String cpuRevision;
    private String cpuArchitecture;
    private String cpuPart;
    private Float cpuTemperature;
    private Float cpuVoltage;
    private String cpuModelName;
    private String processor;
    private String hardware;
    private String hardwareRevision;
    private boolean hardFloatAbi;
    private BoardType boardType;
    
    
    public HardwareInfo() throws PlatformAlreadyAssignedException{
            //PlatformManager.setPlatform(Platform.ORANGEPI);
        try {
            serialNumber=SystemInfo.getSerial();
            cpuRevision=SystemInfo.getCpuRevision();
            cpuArchitecture=SystemInfo.getCpuArchitecture();
            cpuPart=SystemInfo.getCpuPart();
            cpuTemperature=SystemInfo.getCpuTemperature();
            cpuVoltage=SystemInfo.getCpuVoltage();
            cpuModelName=SystemInfo.getModelName();
            processor=SystemInfo.getProcessor();
            hardware=SystemInfo.getHardware();
            hardwareRevision=SystemInfo.getRevision();
            hardFloatAbi=SystemInfo.isHardFloatAbi();
            boardType=SystemInfo.getBoardType();
        } catch (IOException ex) {
            Logger.getLogger(HardwareInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HardwareInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(HardwareInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
            
     
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getCpuRevision() {
        return cpuRevision;
    }

    public String getCpuArchitecture() {
        return cpuArchitecture;
    }

    public String getCpuPart() {
        return cpuPart;
    }

    public Float getCpuTemperature() {
        return cpuTemperature;
    }

    public Float getCpuVoltage() {
        return cpuVoltage;
    }

    public String getCpuModelName() {
        return cpuModelName;
    }

    public String getProcessor() {
        return processor;
    }

    public String getHardware() {
        return hardware;
    }

    public String getHardwareRevision() {
        return hardwareRevision;
    }

    public boolean getHardFloatAbi() {
        return hardFloatAbi;
    }

    public BoardType getBoardType() {
        return boardType;
    }    
}
