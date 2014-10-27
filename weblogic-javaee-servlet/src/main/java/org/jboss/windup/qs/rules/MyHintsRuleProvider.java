package org.jboss.windup.qs.rules;

import java.util.Collections;
import java.util.List;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.config.Classification;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceLocation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;

/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class MyHintsRuleProvider extends WindupRuleProvider
{

    @Override
    public List<Class<? extends WindupRuleProvider>> getExecuteAfter()
    {
        return Collections.emptyList();
        // Returning a value here will cause the Rules from this provider to execute after the rules
        // from the providers in the list.
        //
        // The following example specifies that this provider's rules should execute after the rules
        // in AnalyzeJavaFilesRuleProvider. This is technically unnecessary, as the rules in
        // AnalyzeJavaFilesRuleProvider are set to execute in an earlier phase, but is here only to
        // demonstrate the concept.
        //
        // return asClassList(AnalyzeJavaFilesRuleProvider.class);
    }

    @Override
    public void enhanceMetadata(Context context)
    {
        // Associates some metadata with all of the rules provided by this RuleProvider.
        super.enhanceMetadata(context);
        context.put(RuleMetadata.CATEGORY, "Java");
    }

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
                   .and(Hint.withText("Replace @WLServlet with Java EE 6 @WebServlet.\n\r" +
                               "For details on how to map the Servlet attributes, see: <a href=\"https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/index.html\">Red Hat JBoss Documentation</a>"
                   ).withEffort(1)))
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLInitParam").at(TypeReferenceLocation.ANNOTATION)
            )
            .perform(
                Classification.as("WebLogic @WLInitParam")
                   .with(Link.to("Java EE 6 @WLInitParam", "http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/package-summary.html"))
                   .withEffort(0)
                   .and(Hint.withText("Replace @WLInitParam with Java EE 6 @WebInitParam.\n\r" +
                               "For details on how to map the initialization parameter attributes, see: <a href=\"https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/index.html\">Red Hat JBoss Documentation</a>").withEffort(2)))
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLFilter").at(TypeReferenceLocation.ANNOTATION)
            )
            .perform(
                Classification.as("WebLogic @WLFilter")
                   .with(Link.to("Java EE 6 @WebFilter", "http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/package-summary.html"))
                   .withEffort(0)
                   .and(Hint.withText("Replace @WLFilter annotation with Java EE 6 @WebFilter.\n\r" +
                                   "For details on how to map the filter attributes, see: <a href=\"https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/index.html\">Red Hat JBoss Documentation</a>").withEffort(3))
            );
    }
    // @formatter:on

}
