package org.jboss.windup.qs.victims;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.IteratingRuleProvider;

import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Calculates SHA512 hash for each archive.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 *
 */
public class ComputeArchivesSHA512 extends IteratingRuleProvider<ArchiveModel>
{
    public static final String KEY_SHA512 = "SHA512";


    @Override
    public void enhanceMetadata(Context context)
    {
        super.enhanceMetadata(context);
        context.put(RuleMetadata.CATEGORY, "Java");
    }

    @Override
    public ConditionBuilder when()
    {
        return Query.find(ArchiveModel.class);
    }


    // @formatter:off
    @Override
    public void perform(GraphRewrite event, EvaluationContext context, ArchiveModel archive)
    {
        try (InputStream is = archive.asInputStream())
        {
            String hash = DigestUtils.sha512Hex(is);
            archive.asVertex().setProperty(KEY_SHA512, hash);
        }
        catch (IOException e)
        {
            throw new WindupException("Failed to read archive: " + archive.getFilePath() +
                "\n    Due to: " + e.getMessage(), e);
        }
    }
    // @formatter:on


    @Override
    public String toStringPerform()
    {
        return this.getClass().getSimpleName();
    }
}
