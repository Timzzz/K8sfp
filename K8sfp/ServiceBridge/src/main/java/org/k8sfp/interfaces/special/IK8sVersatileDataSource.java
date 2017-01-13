/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.k8sfp.interfaces.special;

import java.util.List;
import org.k8sfp.interfaces.IK8sDataElement;

/**
 * Versatile Datasource interface
 */
public interface IK8sVersatileDataSource {
    public void setCurrentData(List<IK8sDataElement> curr);
}
