package org.jboss.windup.qs.victims;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.loader.RuleLoaderContext;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.operation.Log;
import org.jboss.windup.config.operation.iteration.AbstractIterationOperation;
import org.jboss.windup.config.phase.MigrationRulesPhase;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.qs.victims.model.AffectedJarModel;
import org.jboss.windup.qs.victims.model.VulnerabilityModel;
import org.jboss.windup.util.Logging;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

import com.redhat.victims.VictimsException;
import com.redhat.victims.database.VictimsDB;
import com.redhat.victims.database.VictimsDBInterface;

/**
 * Victi.ms related rules: Archive hashes comparison.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(tags = { "java", "security" }, phase = MigrationRulesPhase.class)
public class CheckArchivesWithVictimsRules extends AbstractRuleProvider
{
    private static final Logger log = Logging.get(CheckArchivesWithVictimsRules.class);

    private VictimsDBInterface db;
    private Date victiomsLastUpdated;
    private int victimsRecordCount;

    // @formatter:off
    @Override
    public Configuration getConfiguration(final RuleLoaderContext ruleLoaderContext)
    {
        try
        {
            db = VictimsDB.db();
            victimsRecordCount = db.getRecordCount();
            victiomsLastUpdated = db.lastUpdated();
        }
        catch(VictimsException ex)
        {
            throw new WindupException("Failed initializing Victi.ms database: " + ex.getMessage(), ex);
        }


        return ConfigurationBuilder.begin()

            // Check the jars
            .addRule()
            .when(Query.fromType(ArchiveModel.class))
            .perform(
                    Log.message(Level.INFO, "Victims database last updated on: " + new SimpleDateFormat("y-M-d H:m:s").format(victiomsLastUpdated)),
                    Log.message(Level.INFO, "Victims database records:   " + victimsRecordCount),

                    Iteration.over(ArchiveModel.class) // TODO: Use IteratingRuleProvider?
                            .perform(
                                    new AbstractIterationOperation<ArchiveModel>() {
                                        @Override
                                        public void perform(GraphRewrite event, EvaluationContext context, ArchiveModel archive) {
                                            log.info("\tVicti.ms checking archive: " + archive.getFilePath());
                                            GraphService<VulnerabilityModel> vulGS = new GraphService<VulnerabilityModel>(event.getGraphContext(), VulnerabilityModel.class);
                                            String hash = archive.asVertex().getProperty(ComputeArchivesVictimsHashRules.KEY_VICTIMS_HASH);
                                            try {
                                                HashSet<String> vuls = db.getVulnerabilities(hash);
                                                if (vuls.isEmpty())
                                                    return;

                                                AffectedJarModel jar = GraphService.addTypeToModel(event.getGraphContext(), archive, AffectedJarModel.class);
                                                for (String vul : vuls) {
                                                    log.info("\t\tVulnerability found in " + archive.getFilePath() + ": " + vul);
                                                    VulnerabilityModel vulM = vulGS.create().setCve(vul);//.setArchive(jar);
                                                    vulM.setArchive(jar);
                                                    log.info("" + vulM);
                                                    jar.addVulnerability(vulM);
                                                }
                                            } catch (VictimsException ex) {
                                                log.severe("Error in Victi.ms when getting vulnerabilities for " + archive.getArchiveName());
                                            }
                                            vulGS.commit();
                                        }


                                        @Override
                                        public String toString() {
                                            return "Checking archives with Victi.ms";
                                        }
                                    }
                            ).endIteration()
            ).withId("CheckArchivesWithVictims");
    }
    // @formatter:on
}
