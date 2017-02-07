/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.k8sfpfaultcontroller;

/**
 *
 * @author tim
 */
public interface IFaultExecution {
    /**
     * Executes the fault
     * @param timeout 
     */
    void execute(int timeout);
    
    /**
     * Returns the name of the fault
     * @return 
     */
    String getName();
}
