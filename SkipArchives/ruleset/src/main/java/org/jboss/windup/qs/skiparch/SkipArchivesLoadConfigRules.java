package org.jboss.windup.qs.skiparch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import org.jboss.windup.qs.identarch.lib.ArchiveGAVIdentifier;
import org.jboss.windup.qs.skiparch.lib.SkippedArchives;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.jboss.windup.config.GraphRewrite;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.util.Logging;
import org.jboss.windup.util.WindupPathUtil;
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
public class SkipArchivesLoadConfigRules extends WindupRuleProvider
{
    private static final Logger log = Logging.get(SkipArchivesLoadConfigRules.class);

    private static final String SKIP_PROP = "w:skip";


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
        .perform(
            new GraphOperation()
            {
                public void perform(GraphRewrite event, EvaluationContext evCtx)
                {
                    loadConfig();
                }
            }
        ).withId("SkipArchivesLoadConfig");
    }
    // @formatter:on



    private void loadConfig()
    {

        // Load them from ~/.windup/config/SkipArchives
        final File confDir = WindupPathUtil.getWindupHome().resolve("config/SkipArchives").toFile();
        if (!confDir.exists())
            log.info("SkipArchives config dir not found at " + confDir.toString());
        else try
        {
            List<Path> gavs = findFilesBySuffix(confDir, ".gavMapping.txt");
            List<Path> skips = findFilesBySuffix(confDir, ".ignoredGavs.txt");

            for(Path gavMappingFile : gavs)
                ArchiveGAVIdentifier.addMappingsFrom(confDir.toPath().resolve(gavMappingFile).toFile());

            for(Path skippedArchivesConfig : skips)
                SkippedArchives.addSkippedArchivesFrom(confDir.toPath().resolve(skippedArchivesConfig).toFile());
        }
        catch (IOException ex)
        {
            Logger.getLogger(SkipArchivesLoadConfigRules.class.getName()).log(Level.SEVERE, null, ex);
        }

        // GAV's may also be bundled within the SkipArchives addon.
        final String GAVS_MAPPING_RESOURCE = "/org/jboss/windup/qs/skiparch/data/jboss.sha1ToGAV.txt";
        //final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GAVS_MAPPING_RESOURCE);

        if (!confDir.exists())
            log.info("SkipArchives' bundled G:A:V mappings not found at " + GAVS_MAPPING_RESOURCE);
        else try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GAVS_MAPPING_RESOURCE))
        {
            ArchiveGAVIdentifier.addMappingsFrom(is);
        }
        catch(IOException ex){} // Ignore ex from .close()
    }


    /**
     * Scans given directory for files with name ending with given suffix.
     *
     * @return  A list of paths to matching files, relative to baseDir.
     */
    private List<Path> findFilesBySuffix(final File baseDir, final String suffix) throws IOException
    {
        final LinkedList<Path> foundSubPaths = new LinkedList();

        final Path basePath = baseDir.toPath();

        new DirectoryWalker<Path>(DirectoryFileFilter.DIRECTORY, new SuffixFileFilter(suffix), -1)
        {
            void findArchives() throws IOException
            {
                this.walk(baseDir, foundSubPaths);
            }

            @Override
            protected void handleFile(File file, int depth, Collection<Path> results) throws IOException
            {
                Path relPath = basePath.relativize(file.toPath());
                results.add(relPath);
            }
        }.findArchives();

        return foundSubPaths;
    }
}
