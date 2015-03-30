package org.jboss.windup.qs.skiparch.test.rulefilters;

import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.RuleProvider;

/**
 * OR predicate which needs at least one of the contained predicates to accept.
 * It will stop on first true if you setStopWhenKnown(true).
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class OrFilter extends AndFilter
{
    public OrFilter(RuleProviderFilter ... filters)
    {
        super(filters);
    }

    @Override
    public boolean accept(RuleProvider provider)
    {
        boolean res = false;
        if (this.predicates.isEmpty())
            return false;

        for( Predicate pred : this.predicates )
        {
            if (pred.accept(provider)){
                res = true;
                if(this.stopWhenKnown)
                    return true;
            }
        }
        return res;
    }

}// class
