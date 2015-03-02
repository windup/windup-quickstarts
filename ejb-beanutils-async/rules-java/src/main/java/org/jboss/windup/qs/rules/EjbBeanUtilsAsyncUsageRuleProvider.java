package org.jboss.windup.qs.rules;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceLocation;
import org.jboss.windup.rules.apps.xml.condition.XmlFile;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;

/**
 *
 * @author jsightler <jesse.sightler@gmail.com>
 */
public class EjbBeanUtilsAsyncUsageRuleProvider extends WindupRuleProvider
{
    @Override
    public void enhanceMetadata(Context context)
    {
        // this method simply associates some metadata with all of the rules provided by this
        // Rule Provider.
        super.enhanceMetadata(context);
        context.put(RuleMetadata.CATEGORY, "Java EE");
    }

    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
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
                ).endIteration()
            );
    }
    // @formatter:on

}
