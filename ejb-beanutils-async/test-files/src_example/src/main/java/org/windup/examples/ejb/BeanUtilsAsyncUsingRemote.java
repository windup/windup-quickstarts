package org.windup.examples.ejb;

import javax.ejb.EJBObject;
import org.jboss.seam.annotations.async.Asynchronous;

/**
 */
public interface BeanUtilsAsyncUsingRemote extends EJBObject {
    @Asynchronous
    public void asyncMethod();
}
