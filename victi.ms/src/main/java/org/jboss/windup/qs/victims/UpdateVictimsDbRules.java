package org.jboss.windup.qs.victims;

import com.redhat.victims.VictimsException;
import com.redhat.victims.database.VictimsDB;
import com.redhat.victims.database.VictimsDBInterface;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.RulePhase;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupConfigurationModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.apps.java.scan.provider.UnzipArchivesToOutputRuleProvider;
import org.jboss.windup.util.Logging;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Victi.ms related rules: database update, archive hashes comparison.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
public class UpdateVictimsDbRules extends WindupRuleProvider
{
    private static final Logger log = Logging.get(UpdateVictimsDbRules.class);


    @Override
    public RulePhase getPhase()
    {
        return RulePhase.POST_DISCOVERY;
    }

    @Override
    public List<Class<? extends WindupRuleProvider>> getExecuteAfter()
    {
        return asClassList(UnzipArchivesToOutputRuleProvider.class);
    }


    @Override
    public void enhanceMetadata(Context context)
    {
        super.enhanceMetadata(context);
        context.put(RuleMetadata.CATEGORY, "Java");
    }

    // @formatter:off
    @Override
    public Configuration getConfiguration(final GraphContext context)
    {
        return ConfigurationBuilder.begin()

            // Update Victims DB.
            .addRule()
            // If not offline...
            .when(
                new Condition()
                {
                    public boolean evaluate(Rewrite event, EvaluationContext context_)
                    {
                        return ! new GraphService<>(context, WindupConfigurationModel.class).getUnique().isOfflineMode();
                    }
                }
            )
            // ...then update.
            .perform(new GraphOperation()
                {
                    @Override
                    public void perform(GraphRewrite event, EvaluationContext context)
                    {
                        try {
                            VictimsDBInterface db = VictimsDB.db();
                            // Update (goes to ~/.victims)
                            db.synchronize();
                        }
                        catch(VictimsException ex){
                            log.log(Level.WARNING, "Failed updating Victi.ms database: " + ex.getMessage(), ex);
                        }
                    }
                }
            );
    }
    // @formatter:on
}
