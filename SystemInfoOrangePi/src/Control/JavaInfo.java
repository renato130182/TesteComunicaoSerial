/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import com.pi4j.system.SystemInfo;

/**
 * Multiplataforma
 * @author renato.soares
 */
public class JavaInfo {
    private final String javaVendor;
    private final String javaVendorUrl;
    private final String javaVersion;
    private final String javaVm;
    private final String javaRuntime;
    
    public JavaInfo(){    
        //PlatformManager.setPlatform(Platform.ORANGEPI);
        javaVendor=SystemInfo.getJavaVendor();
        javaVendorUrl=SystemInfo.getJavaVendorUrl();
        javaVersion=SystemInfo.getJavaVersion();
        javaVm=SystemInfo.getJavaVirtualMachine();
        javaRuntime=SystemInfo.getJavaRuntime();                      
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public String getJavaVendorUrl() {
        return javaVendorUrl;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaVm() {
        return javaVm;
    }

    public String getJavaRuntime() {
        return javaRuntime;
    }
}
