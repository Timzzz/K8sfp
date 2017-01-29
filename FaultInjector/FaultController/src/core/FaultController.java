/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

public class FaultController {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting Fault Controller");
        IFaultExecution e = new StressExec();
        e.execute(10);
        
        System.out.println("Exiting Fault Controller");
    }
    
}
