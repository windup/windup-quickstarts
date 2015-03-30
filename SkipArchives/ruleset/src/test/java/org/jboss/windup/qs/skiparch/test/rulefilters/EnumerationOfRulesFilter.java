package org.jboss.windup.qs.skiparch.test.rulefilters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.windup.config.RuleProvider;

/**
 * A convenient filter for rule providers enumerated as constructor params.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class EnumerationOfRulesFilter implements RuleProviderFilter
{
    private final Set<? extends RuleProvider> classes;
    private final Set<String> classNames = new HashSet();


    public EnumerationOfRulesFilter(Class<? extends RuleProvider> ... classes)
    {
        this.classes = new HashSet(Arrays.asList(classes));
        for( Class<? extends RuleProvider> cls : classes )
            this.classNames.add(cls.getName());
    }


    @Override
    public boolean accept(RuleProvider ruleProvider)
    {
        //return this.classes.contains(ruleProvider.getClass());
        //return this.classNames.contains(ruleProvider.getClass().getName());
        String realClassName = StringUtils.substringBefore(ruleProvider.toString(), "@");
        realClassName = Proxies.unwrapProxyClassName(ruleProvider.getClass());
        return this.classNames.contains(realClassName);
    }

}// class
