package com.foo;

import javax.ejb.EJBObject;
import com.beanutils.async.AsyncronousMethod;

/**
 */
public class BeanUtilsAsyncUsingRemote extends EJBObject {
    @AsyncronousMethod
    public void asyncMethod() {
        
    }
}
