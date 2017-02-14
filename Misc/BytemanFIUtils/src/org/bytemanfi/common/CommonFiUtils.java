/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bytemanfi.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class CommonFiUtils {
    
    public static void allocateMemory() {
        System.out.println("BYTEMANFI allocateMemory() - " + MemClass.map.size());
        MemClass.alloc();
               
        /*for(int i=0 ; i<1000; ++i) {
        try {
        MemClass.alloc();
        Thread.sleep(100);
        } catch (InterruptedException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        }*/
    }
    public void increaseCpu() {
        System.out.println("BYTEMANFI increaseCpu()");
    }
}
