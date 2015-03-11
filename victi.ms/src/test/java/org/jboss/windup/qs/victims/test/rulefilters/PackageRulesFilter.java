package org.jboss.windup.qs.victims.test.rulefilters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jboss.windup.config.RuleProvider;

/**
 * A convenient filter for rule providers enumerated as constructor params.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class PackageRulesFilter implements RuleFilter
{
    private Set<String> packages;


    public PackageRulesFilter(String ... packages)
    {
        this.packages = new HashSet(Arrays.asList(packages));
    }


    @Override
    public boolean accept(RuleProvider ruleProvider)
    {
        return this.packages.contains(ruleProvider.getClass().getPackage().getName());
    }

}// class
