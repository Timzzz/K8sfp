/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bytemanfi.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
class MemClass {
    public static List<String> map = new ArrayList<String>();
    public static void alloc() {
        for(int i=0; i<10; ++i) {
            map.add(clStr().intern());
        }
    }
    private static String clStr() {
        StringBuilder s = new StringBuilder();
        for(int i=0; i<10000; ++i) {
            s.append(""+(Math.random()*255));
        }
        return s.toString().intern();
    }
}
