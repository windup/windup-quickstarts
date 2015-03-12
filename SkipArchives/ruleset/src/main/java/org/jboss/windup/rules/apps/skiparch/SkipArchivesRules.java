package org.jboss.windup.rules.apps.skiparch;

import org.jboss.windup.qs.skiparch.lib.SkippedArchives;
import java.util.logging.Logger;
import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;

import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.operation.iteration.AbstractIterationOperation;
import org.jboss.windup.config.phase.DependentPhase;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.apps.identarch.IdentifyArchivesRules;
import org.jboss.windup.rules.apps.identarch.model.IdentifiedArchiveModel;
import org.jboss.windup.qs.skiparch.model.IgnoredArchiveModel;
import org.jboss.windup.util.Logging;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Rules which support skipping certain archives by their G:A:V definition.
 * The purpose is to speed up processing of the scanned deployments.
 * The archive that is defined to be skipped (currently in a bundled text file)
 * is marked in the graph with a "w:skip" property.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(tags = "java", after = {IdentifyArchivesRules.class, SkipArchivesLoadConfigRules.class}, phase = DependentPhase.class)
public class SkipArchivesRules extends AbstractRuleProvider
{
    private static final Logger log = Logging.get(SkipArchivesRules.class);


    // @formatter:off
    @Override
    public Configuration getConfiguration(final GraphContext grCtx)
    {
        return ConfigurationBuilder.begin()

        // Check the jars
        .addRule()
        .when(Query.fromType(IdentifiedArchiveModel.class))
        .perform(
            Iteration.over(IdentifiedArchiveModel.class) // TODO: Use IteratingRuleProvider?
            .perform(
                new AbstractIterationOperation<IdentifiedArchiveModel>()
                {
                    @Override
                    public void perform(GraphRewrite event, EvaluationContext evCtx, IdentifiedArchiveModel arch)
                    {
                        log.info("\tSkipArchives checking archive: " + arch.getFilePath());

                        //GAV archiveGav = ArchiveGAVIdentifier.getGAVFromSHA1(arch.getSHA1Hash());
                        // Identification moved to IdentifyArchiveRules.

                        if (SkippedArchives.isSkipped(arch.getGAV()))
                        {
                            GraphService.addTypeToModel(grCtx, arch, IgnoredArchiveModel.class);
                        }
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
