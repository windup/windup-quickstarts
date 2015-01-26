package org.jboss.windup.qs.identarch.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.util.exception.WindupException;

/**
 * TODO: Merge to main GraphServiceWrap.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class GraphServiceWrap<T> //extends org.jboss.windup.graph.service.GraphServiceWrap<T>
{

    private Class<T> type;
    private GraphContext context;

    public GraphServiceWrap(GraphContext context, Class<T> type)
    {
        this.context = context;
        this.type = type;
    }


    /**
    * Takes an object implementing a model interface,
    * creates a vertex and copies the properties into the returned proxy.
    * Something like JPA's merge().
    * This method doesn't deal with any ID's; maybe in the future.
    * So, the vertex is always created and returned.
    */
    public T merge(T source)
    {
        T frame = this.context.getFramed().addVertex(null, this.type);
        try
        {
            copyProperties(source, frame);
        }
        catch( Exception ex )
        {
            throw new WindupException("Failed copying properties into frame from: "
                + frame.getClass().getName() + "\n " + ex.getMessage(), ex
            );
        }

        return frame;
    }


    /**
     * Finds the "writable properties" in the frame and copies values for those from the source, if available.
     */
    private void copyProperties( T source, T frame  ) {
        if( source == null ) throw new IllegalArgumentException("source is null.");
        List<Method> setters = PropertyUtils.findSetters( frame.getClass() );
        for( Method setter : setters ) {
            Method getter = PropertyUtils.findGetter( source.getClass(), deriveGetterName(setter.getName()), setter.getReturnType() );
            if( getter == null )
                continue;
            copyFromGetterToSetter( source, frame, getter,  setter);
        }
    }


    /**
     * Helper method to encapsulate the exceptions.
     */
    private void copyFromGetterToSetter( T source, T dest, Method getter, Method setter ) {
        Object val;

        // Get.
        try {
            val = getter.invoke( source );
        } catch( IllegalArgumentException | IllegalAccessException | InvocationTargetException ex ) {
            throw new RuntimeException("Unable to call getter "+getter.getDeclaringClass().getSimpleName() +"#"+ getter.getName()+": " + ex.getMessage(), ex);
        }

        // Set.
        try {
            setter.invoke( dest, val );
        } catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
            throw new RuntimeException("Unable to call setter "+getter.getDeclaringClass().getSimpleName() +"#"+ getter.getName()+": " + ex.getMessage(), ex);
        }
    }


    /**
     * Removes "set" and uncapitalizes.
     */
    private String stripSetterPropName( String name ) {
        return StringUtils.uncapitalize( StringUtils.removeStart(name, "set") );
    }


    /**
     * Calls stripSetterPropName(), capitalizes and adds get.
     */
    private String deriveGetterName( String name ) {
        return "get" + StringUtils.removeStart(name, "set");
        // Not bullet-proof.
    }



}// class
