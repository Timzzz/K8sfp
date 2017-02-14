/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.k8sfp.k8sfpfaultcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ExecUtils {

    public interface Action<T> {

        void execute(T t);
    }

    public static boolean RunCommand(String... cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            Process p = pb.start();
            p.waitFor();
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            return stdError.readLine() == null;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExecUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExecUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static class ControllerThread implements Runnable {

        private Action<Void> beginFunc;
        private Action<Void> execFunc;
        private Action<Void> endFunc;
        private int timeout;

        public ControllerThread(Action<Void> beginFunc, Action<Void> execFunc, Action<Void> endFunc, int timeout) {
            this.beginFunc = beginFunc;
            this.execFunc = execFunc;
            this.endFunc = endFunc;
            this.timeout = timeout;
        }

        @Override
        public void run() {

            try {
                if (timeout < 0) {
                    if (beginFunc != null) {
                        beginFunc.execute(null);
                    }
                    if (execFunc != null) {
                        execFunc.execute(null);
                    }
                    if (endFunc != null) {
                        endFunc.execute(null);
                    }
                } else {
                    Thread t = new Thread() {
                        public void run() {
                            try {
                                if (beginFunc != null) {
                                    beginFunc.execute(null);
                                }
                                if (execFunc != null) {
                                    execFunc.execute(null);
                                }
                            } catch (Exception e) {
                                return;
                            }
                        }
                    };
                    t.start();
                    Thread.sleep(timeout);
                    t.interrupt();
                    if (endFunc != null) {
                        endFunc.execute(null);
                    }
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(ExecUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public static void createThread(Action<Void> execFunc,
            Action<Void> endFunc, int timeout, boolean runEndFuncOnNegativeTimeout) {
        boolean flag = true;
        if(!runEndFuncOnNegativeTimeout && timeout < 0) {
            flag = false;
        }
        Thread t = new Thread(new ControllerThread(null, execFunc, flag ? endFunc : null, timeout));
        t.start();
    }

}
