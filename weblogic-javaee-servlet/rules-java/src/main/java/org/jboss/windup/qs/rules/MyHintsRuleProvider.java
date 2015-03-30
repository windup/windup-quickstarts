package org.jboss.windup.qs.rules;

import org.jboss.windup.ast.java.data.TypeReferenceLocation;
import org.jboss.windup.config.AbstractRuleProvider;

import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.config.classification.Classification;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;

/**
 * Reports on Java classes that contain proprietary WebLogic
 * servlet annotations and provides links to documentation
 * that describe how to migrate to standard Java EE 6 code.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(
        // A RuleProvider may either specify metadata here,
        // or call AbstractRuleProvider.super(RuleProviderMetadata) in it's constructor.

        // Tags. See Windup documentation for what tags serve for.
        tags = "Java",

        // Specifying a value here will cause the Rules from this provider to execute after the rules
        // from the providers in the list.
        //
        // The following example specifies that this provider's rules should execute after the rules
        // in AnalyzeJavaFilesRuleProvider. This is technically unnecessary, as the rules in
        // AnalyzeJavaFilesRuleProvider are set to execute in an earlier phase, but is here only to demonstrate the concept.
        after = {}
)
public class MyHintsRuleProvider extends AbstractRuleProvider
{
    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
    {
        return ConfigurationBuilder.begin()
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLServlet").at(TypeReferenceLocation.ANNOTATION)
            )
            .perform(
                Classification.as("WebLogic @WLServlet")
                   .with(Link.to("Java EE 6 @WebServlet", "http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/package-summary.html"))
                   .withEffort(0)
                   .and(Hint.withText("Replace the proprietary WebLogic @WLServlet annotaion with the Java EE 6 standard @WebServlet annotation.\n\r" +
                        "For details on how to map the Servlet attributes, see: <a href=\"https://access.redhat.com/articles/1249423\">Migrate WebLogic Proprietary Servlet Annotations</a>"
                   ).withEffort(1)))
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLInitParam").at(TypeReferenceLocation.ANNOTATION)
            )
            .perform(
                Classification.as("WebLogic @WLInitParam")
                   .with(Link.to("Java EE 6 @WebInitParam", "http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/package-summary.html"))
                   .withEffort(0)
                   .and(Hint.withText("Replace the proprietary WebLogic @WLInitParam annotation with the Java EE 6 standard @WebInitParam annotation.\n\r" +
                        "For details on how to map the initialization parameter attributes, see: <a href=\"https://access.redhat.com/articles/1249423\">Migrate WebLogic Proprietary Servlet Annotations</a>").withEffort(2)))
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLFilter").at(TypeReferenceLocation.ANNOTATION)
            )
            .perform(
                Classification.as("WebLogic @WLFilter")
                   .with(Link.to("Java EE 6 @WebFilter", "http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/package-summary.html"))
                   .withEffort(0)
                   .and(Hint.withText("Replace the proprietary WebLogic @WLFilter annotation with Java EE 6 standard @WebFilter annotation.\n\r" +
                        "For details on how to map the filter attributes, see: <a href=\"https://access.redhat.com/articles/1249423\">Migrate WebLogic Proprietary Servlet Annotations</a>").withEffort(3))
            );
    }
    // @formatter:on
}
