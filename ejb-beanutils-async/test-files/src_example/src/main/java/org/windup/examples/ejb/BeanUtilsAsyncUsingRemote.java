package org.windup.examples.ejb;

import javax.ejb.EJBObject;
import com.beanutils.async.AsynchronousMethod;

/**
 */
public class BeanUtilsAsyncUsingRemote extends EJBObject {
    @AsynchronousMethod
    public void asyncMethod() {
        
    }
}
