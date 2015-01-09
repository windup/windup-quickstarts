package org.jboss.windup.qs.skipjars.test.rulefilters;

import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.WindupRuleProvider;

/**
 * OR predicate which needs at least one of the contained predicates to accept.
 * It will stop on first true if you setStopWhenKnown(true).
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class OrPredicate extends AndPredicate
{
    public OrPredicate(Predicate ... preds)
    {
        super(preds);
    }

    @Override
    public boolean accept(WindupRuleProvider provider)
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
