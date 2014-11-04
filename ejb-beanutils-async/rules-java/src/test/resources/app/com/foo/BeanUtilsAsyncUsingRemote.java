package com.foo;

import javax.ejb.EJBObject;
import com.beanutils.async.AsynchronousMethod;

/**
 */
public class BeanUtilsAsyncUsingRemote extends EJBObject {
    @AsynchronousMethod
    public void asyncMethod() {
        
    }
}
