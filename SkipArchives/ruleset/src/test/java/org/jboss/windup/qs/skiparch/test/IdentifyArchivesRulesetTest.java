package org.jboss.windup.qs.skiparch.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.config.RuleProvider;
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.exec.rulefilters.RuleProviderFilter;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.apps.identarch.IdentifyArchivesLoadConfigRules;
import org.jboss.windup.rules.apps.identarch.IdentifyArchivesRules;
import org.jboss.windup.rules.apps.identarch.model.GAV;
import org.jboss.windup.rules.apps.identarch.model.GAVModel;
import org.jboss.windup.rules.apps.identarch.model.IdentifiedArchiveModel;
import org.jboss.windup.rules.apps.skiparch.SkipArchivesRules;
import org.jboss.windup.qs.skiparch.test.rulefilters.EnumerationOfRulesFilter;
import org.jboss.windup.qs.skiparch.test.rulefilters.PackageSubtreeRulesFilter;
import org.jboss.windup.qs.skiparch.test.rulefilters.RuleFilter;
import org.jboss.windup.util.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for the Victims ruleset.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@RunWith(Arquillian.class)
public class IdentifyArchivesRulesetTest
{
    private static final Logger log = Logging.get(IdentifyArchivesRulesetTest.class);

    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.windup.config:windup-config"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
        @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
        //@AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
        //@AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
        @AddonDependency(name = "org.jboss.windup.quickstarts:windup-skiparchives"),
        @AddonDependency(name = "org.jboss.windup.quickstarts:windup-skiparch-mappings"),
        @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
    })
    public static ForgeArchive getDeployment()
    {
        final File res = new File("target/classes" + IdentifyArchivesLoadConfigRules.CENTRAL_MAPPING_DATA_CLASSPATH);
        Assert.assertTrue(res.exists());

        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
            .addBeansXML()
            //.addPackages(true, IdentifyArchivesRulesetTest.class.getPackage())
            .addPackages(true, RuleFilter.class.getPackage())
            .addAsAddonDependencies(
                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec"),
                AddonDependencyEntry.create("org.jboss.windup.utils:windup-utils"),
                //AddonDependencyEntry.create("org.jboss.windup.rules.apps:windup-rules-java"),
                //AddonDependencyEntry.create("org.jboss.windup.reporting:windup-reporting"),
                AddonDependencyEntry.create("org.jboss.windup.quickstarts:windup-skiparchives"),
                AddonDependencyEntry.create("org.jboss.windup.quickstarts:windup-skiparch-mappings"),
                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
            )
            .addAsResource(res, IdentifyArchivesLoadConfigRules.CENTRAL_MAPPING_DATA_CLASSPATH);
            //.addAsResource(res, "/x.zip");

        return archive;
    }


    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;

    @Test
    public void testJarsAreIdentified()
    {
        try (GraphContext grCtx = contextFactory.create())
        {


            // Create some identifiable archives.
            GraphService<IdentifiedArchiveModel> archGS = new GraphService(grCtx, IdentifiedArchiveModel.class);
            IdentifiedArchiveModel archM = archGS.create();
            // 4e031bb61df09069aeb2bffb4019e7a5034a4ee0 junit:junit:4.11
            archM.setSHA1Hash("4e031bb61df09069aeb2bffb4019e7a5034a4ee0");

            runRule(SkipArchivesRules.class, grCtx);
            runRules(new PackageSubtreeRulesFilter(IdentifyArchivesRules.class), grCtx);

            // Check if the archives were identified.

            // Something found for that SHA1.
            GAVModel gav = archM.getGAV();
            Assert.assertNotNull(gav);

            // Correctly identified.
            final GAV expectedGav = GAV.fromSHA1AndGAV("4e031bb61df09069aeb2bffb4019e7a5034a4ee0 junit:junit:4.11");
            Assert.assertEquals(expectedGav, gav);

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }


    /**
     * @param ruleProvider RuleProvider to run, with all rules that must be run before.
     */
    private void runRule(Class<? extends RuleProvider> ruleProvider, GraphContext grCtx)
    {
        this.runRules(new EnumerationOfRulesFilter(ruleProvider), grCtx);
    }


    /**
     * TODO: Move this to some test utils?
     */
    private void runRules(RuleProviderFilter ruleFilter, GraphContext grCtx)
    {
        try
        {
            // Windup config.
            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(grCtx);
            wc.setInputPath(Paths.get("."));
            wc.setRuleProviderFilter(ruleFilter);
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            // Run.
            processor.execute(wc);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
