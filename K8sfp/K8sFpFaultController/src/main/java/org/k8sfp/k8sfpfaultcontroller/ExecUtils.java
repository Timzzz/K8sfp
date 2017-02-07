/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.k8sfpfaultcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ExecUtils {
    public static boolean RunCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            return stdError.readLine() == null;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExecUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
