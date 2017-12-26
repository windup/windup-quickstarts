package org.jboss.windup.qs.victims;

import com.redhat.victims.VictimsConfig;
import com.redhat.victims.VictimsRecord;
import com.redhat.victims.VictimsScanner;
import com.redhat.victims.fingerprint.*;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.phase.ArchiveExtractionPhase;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.config.ruleprovider.IteratingRuleProvider;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.rules.apps.java.scan.provider.UnzipArchivesToOutputRuleProvider;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Calculates the Victims proprietary normalized hash for each archive.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(tags = { "java" }, after = { UnzipArchivesToOutputRuleProvider.class }, phase = ArchiveExtractionPhase.class)
public class ComputeArchivesVictimsHashRules extends IteratingRuleProvider<ArchiveModel>
{
    public static final String KEY_VICTIMS_HASH = "VICTIMS_HASH";


    @Override
    public ConditionBuilder when()
    {
        return Query.fromType(ArchiveModel.class);
    }

    // @formatter:off
    @Override
    public void perform(GraphRewrite event, EvaluationContext context, ArchiveModel archive)
    {
        try (InputStream is = archive.asInputStream())
        {
            String hash = computeVictimsHash(is, archive.getFileName());
            archive.asVertex().setProperty(KEY_VICTIMS_HASH, hash);
        }
        catch (IOException e)
        {
            throw new WindupException("Failed to read archive: " + archive.getFilePath() +
                    "\n    Due to: " + e.getMessage(), e);
        }
    }
    // @formatter:on

    public static String computeVictimsHash(InputStream is, String fileName) throws IOException
    {
        // The Victims API is not much understandable so this may look chaotic.

        /*
        Artifact artifact = Processor.process(is, archive.getFileName());
        ArrayList<VictimsRecord> records = new ArrayList<VictimsRecord>();
        VictimsScanner.scanArtifact(artifact, new VictimsScanner.ArrayOutputStream(records));
        return records.get(0).hash;
        */

        // This only gives a simple hash?
        //Fingerprint fingerprint = Processor.fingerprint(IOUtils.toByteArray(is));
        //return fingerprint.get(VictimsConfig.DEFAULT_ALGORITHM_STRING);

        JarFile jarFile = new JarFile(is, fileName);
        return jarFile.getFingerprint().get(Algorithms.SHA512);
    }


    @Override
    public String toStringPerform()
    {
        return this.getClass().getSimpleName();
    }
}
