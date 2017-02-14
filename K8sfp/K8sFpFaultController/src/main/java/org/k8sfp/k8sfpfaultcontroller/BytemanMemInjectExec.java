/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.k8sfpfaultcontroller;

/**
 *
 */
public class BytemanMemInjectExec implements IFaultExecution {
    
    private static final String btmPath = "byteman/memLeak1.btm";
    
    @Override
    public void execute(int timeout) {
        System.out.println("FaultController exec: " + getName());
        start(getName());
        /*ExecUtils.RunCommand("bash", FaultController.BYTEMAN_PATH + "/bin/bmsubmit.sh", "-l", "byteman/memLeak1.btm");
        ExecUtils.createThread(
        (v) -> start(getName()),
        (v) -> end(getName()),
        timeout, false);*/
    }
    
    public void start(String name) {
        //FaultController.sendStartToDb(name);
        ExecUtils.RunCommand("bash", FaultController.BYTEMAN_PATH + "/bin/bmsubmit.sh", "-l", btmPath);
    }
    
    public void end(String name) {  // will not be called
        ExecUtils.RunCommand("bash", FaultController.BYTEMAN_PATH + "/bin/bmsubmit.sh", "-l", btmPath);
        //FaultController.sendStopToDb(name);
    }

    @Override
    public String getName() {
        return "memLeak1";
    }

}
