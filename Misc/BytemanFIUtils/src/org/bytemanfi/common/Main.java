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
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Start");
        MemClass c = new MemClass();
        for(int i=0 ; i<1000; ++i) {
            try {
                c.alloc();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       System.out.println("End");
    }
    
}
