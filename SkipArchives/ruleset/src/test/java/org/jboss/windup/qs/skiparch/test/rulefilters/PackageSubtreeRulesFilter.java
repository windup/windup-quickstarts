package org.jboss.windup.qs.skiparch.test.rulefilters;

import org.jboss.windup.config.RuleProvider;

/**
 * Accepts rule providers that belong under certain package and "subpackages".
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class PackageSubtreeRulesFilter extends PackageRulesFilter
{
    public PackageSubtreeRulesFilter(String... packages)
    {
        super(packages);
    }

    public PackageSubtreeRulesFilter(Package pkg)
    {
        super(pkg);
    }

    public PackageSubtreeRulesFilter(Class<? extends RuleProvider> pkgCls)
    {
        this(pkgCls.getPackage());
    }

    @Override
    public boolean accept(RuleProvider ruleProvider)
    {
        final Package rulePkg = ruleProvider.getClass().getPackage();
        if (rulePkg == null)
            return false;

        String rulePkgName = rulePkg.getName();

        for (String pkg : packages)
            if (rulePkgName.startsWith(pkg) && (rulePkgName.equals(pkg) || rulePkgName.charAt(rulePkgName.length()) == '.') )
                return true;

        return false;
    }

}// class
