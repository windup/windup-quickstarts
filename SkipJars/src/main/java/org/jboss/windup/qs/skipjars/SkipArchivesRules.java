package org.jboss.windup.qs.skipjars;

import org.jboss.windup.qs.skipjars.lib.GAV;
import org.jboss.windup.qs.skipjars.lib.ArchiveGAVIdentifier;
import org.jboss.windup.qs.skipjars.lib.SkippedArchives;
import java.util.List;
import java.util.logging.Logger;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.RulePhase;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.operation.ruleelement.AbstractIterationOperation;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.util.Logging;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Rules which support skipping certain archives by their G:A:V definition.
 * The purpose is to speed up processing of the scanned deployments.
 * The archive that is defined to be skipped (currently in a bundled text file)
 * is marked in the graph with a "w:skip" property.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
public class SkipArchivesRules extends WindupRuleProvider
{
    private static final Logger log = Logging.get(SkipArchivesRules.class);


    @Override
    public RulePhase getPhase()
    {
        return RulePhase.POST_DISCOVERY;
    }

    @Override
    public void enhanceMetadata(Context context)
    {
        super.enhanceMetadata(context);
        context.put(RuleMetadata.CATEGORY, "Java");
    }

    @Override
    public List<Class<? extends WindupRuleProvider>> getExecuteAfter()
    {
        return asClassList();
    }


    // @formatter:off
    @Override
    public Configuration getConfiguration(final GraphContext grCtx)
    {
        return ConfigurationBuilder.begin()

            // Check the jars
            .addRule()
            .when(Query.fromType(ArchiveModel.class))
            .perform(
                Iteration.over(ArchiveModel.class) // TODO: Use IteratingRuleProvider?
                .perform(
                    new AbstractIterationOperation<ArchiveModel>()
                    {
                        @Override
                        public void perform(GraphRewrite event, EvaluationContext evCtx, ArchiveModel arch)
                        {
                            log.info("\tSkipArchives checking archive: " + arch.getFilePath());

                            GAV archiveGav = ArchiveGAVIdentifier.getGAVFromSHA1(arch.getSHA1Hash());

                            if(SkippedArchives.isSkipped(archiveGav))
                                arch.asVertex().setProperty("w:skip", true);
                        }


                        @Override
                        public String toString()
                        {
                            return "Checking archives with SkipArchives";
                        }
                    }
                ).endIteration()
            ).withId("CheckArchivesWithSkipArchives");
    }
    // @formatter:on
}
