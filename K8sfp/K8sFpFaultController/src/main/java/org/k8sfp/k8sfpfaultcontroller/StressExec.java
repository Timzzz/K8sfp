/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.k8sfpfaultcontroller;

/**
 *
 */
public class StressExec implements IFaultExecution{

    @Override
    public void execute(int timeout) {
        ExecUtils.RunCommand(String.format("stress -c 4 -t %d", timeout));
    }

    @Override
    public String getName() {
        return "StressCmd";
    }

}
