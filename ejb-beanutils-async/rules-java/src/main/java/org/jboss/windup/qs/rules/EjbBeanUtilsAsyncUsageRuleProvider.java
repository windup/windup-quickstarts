package org.jboss.windup.qs.rules;

import org.jboss.windup.config.loader.RuleLoaderContext;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.windup.ast.java.data.TypeReferenceLocation;
import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.rules.apps.xml.condition.XmlFile;


/**
 * @author Ondrej Zizka, zizka@seznam.cz
 * @author jsightler <jesse.sightler@gmail.com>
 */
@RuleMetadata(tags = {"java-ee"})
public class EjbBeanUtilsAsyncUsageRuleProvider extends AbstractRuleProvider
{
    // @formatter:off
    @Override
    public Configuration getConfiguration(RuleLoaderContext ruleLoaderContext)
    {
        return ConfigurationBuilder.begin()
        .addRule()
        .when(
            XmlFile.matchesXpath("/ejb:ejb-jar//ejb:session[windup:matches(ejb:remote/text(), '{remoteclass}')]")
            .namespace("ejb", "http://java.sun.com/xml/ns/javaee")
            .as("beanRemoteInterfaces")
            .and(
                JavaClass
                    .references("org.jboss.seam.annotations.async.Asynchronous")
                    .inType("{remoteclass}")
                    .at(TypeReferenceLocation.ANNOTATION)
                    .as("asyncUsingSessionBeans")
            )
        )
        .perform(
            Iteration.over("asyncUsingSessionBeans").perform(
                Hint.withText("{remoteclass} uses the Seam @Asynchronous annotation. It is not compatible with JBoss EAP Remote EJBs and should be replaced with the standard Java EE 6 @Asynchronous annotation.")
                    .with(Link.to("Using Java EE 6 @Asynchronous.", "http://docs.oracle.com/javaee/6/tutorial/doc/gkkqg.html"))
                    .withEffort(8)
                    .withTags(new HashSet<String>( Arrays.asList("seam") ))
            ).endIteration()
        );
    }
    // @formatter:on
}
