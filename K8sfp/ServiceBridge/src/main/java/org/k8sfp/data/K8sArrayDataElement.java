/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.data;

import org.k8sfp.interfaces.IK8sDataElementArray;

/**
 *
 */
public class K8sArrayDataElement implements IK8sDataElementArray {
    
    private Object[] data;

    public K8sArrayDataElement(Object[] data) {
        this.data = data;
    }
    
    public void setData(Object[] data) {
        this.data = data;
    }
    
    public Object[] getData() {
        return data;
    }

}
