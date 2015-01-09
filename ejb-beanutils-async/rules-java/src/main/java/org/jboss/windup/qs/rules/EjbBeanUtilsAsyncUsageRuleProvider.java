package org.jboss.windup.qs.rules;

import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.config.query.QueryGremlinCriterion;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupVertexFrame;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.reporting.model.FileReferenceModel;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.jboss.windup.rules.apps.java.model.JavaClassModel;
import org.jboss.windup.rules.apps.java.scan.ast.JavaTypeReferenceModel;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceLocation;
import org.jboss.windup.rules.apps.javaee.model.EjbSessionBeanModel;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;

import com.thinkaurelius.titan.core.attribute.Text;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

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
                Query
                    .fromType(EjbSessionBeanModel.class)
                        .piped(new QueryGremlinCriterion()
                        {   
                            @Override
                            public void query(GraphRewrite event, GremlinPipeline<Vertex, Vertex> pipeline)
                            {
                                pipeline.out(EjbSessionBeanModel.EJB_REMOTE);
                                pipeline.out(JavaClassModel.DECOMPILED_SOURCE, JavaClassModel.ORIGINAL_SOURCE);
                                pipeline.in(FileReferenceModel.FILE_MODEL);
                                pipeline.has(WindupVertexFrame.TYPE_PROP, Text.CONTAINS, JavaTypeReferenceModel.TYPE);
                            }
                        })
                        .as("sessionBeans")
                        .and(
                                JavaClass
                                    .from("sessionBeans")
                                    .references("com.beanutils.async.AsynchronousMethod")
                                    .at(TypeReferenceLocation.ANNOTATION)
                                    .as("asyncUsingSessionBeans")
                        )
            )
            .perform(
                Iteration.over("asyncUsingSessionBeans").perform(
                    Hint.withText("BeanUtils Asynchronous is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asynchronous annotation.")
                        .with(Link.to("Using Java EE 6 @Asynchronous.", "http://docs.oracle.com/javaee/6/tutorial/doc/gkkqg.html"))
                        .withEffort(8)
                ).endIteration()
            );
    }
    // @formatter:on

}
