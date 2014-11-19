package org.jboss.windup.qs.victims.test;

import org.jboss.windup.qs.victims.test.rulefilters.EnumerationOfRulesFilter;
import org.jboss.windup.qs.victims.test.rulefilters.PhaseRulesFilter;
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
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.qs.victims.CheckArchivesWithVictimsRules;
import org.jboss.windup.qs.victims.ComputeArchivesSHA512;
import org.jboss.windup.qs.victims.UpdateVictimsDbRules;
import org.jboss.windup.qs.victims.VictimsReportRules;
import org.jboss.windup.qs.victims.model.AffectedJarModel;
import org.jboss.windup.qs.victims.model.VulnerabilityModel;
import org.jboss.windup.qs.victims.test.rulefilters.OrPredicate;
import org.jboss.windup.rules.apps.java.model.JarArchiveModel;
import org.jboss.windup.rules.apps.java.model.WindupJavaConfigurationModel;
import org.jboss.windup.rules.apps.java.scan.provider.UnzipArchivesToOutputRuleProvider;
import org.jboss.windup.rules.apps.java.service.WindupJavaConfigurationService;
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
public class VictimsRulesetTest
{
    private static final Logger log = Logging.get(VictimsRulesetTest.class);

    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.windup.config:windup-config"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
        @AddonDependency(name = "org.jboss.windup.utils:utils"),
        @AddonDependency(name = "org.jboss.windup.rules.apps:rules-java"),
        @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
        @AddonDependency(name = "org.jboss.windup.quickstarts:windup-victims"),
        @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
    })
    public static ForgeArchive getDeployment()
    {
        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
            .addBeansXML()
            //.addClasses(CheckArchivesWithVictimsRules.class, EnumerationOfRulesFilter.class, OrPredicate.class, )
            .addPackages(true,"org.jboss.windup.qs.victims.test")
            .addAsAddonDependencies(
                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec"),
                AddonDependencyEntry.create("org.jboss.windup.utils:utils"),
                AddonDependencyEntry.create("org.jboss.windup.rules.apps:rules-java"),
                AddonDependencyEntry.create("org.jboss.windup.reporting:windup-reporting"),
                AddonDependencyEntry.create("org.jboss.windup.quickstarts:windup-victims"),
                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
            );
        return archive;
    }


    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;

    @Test
    public void testAffectedJarsFound()
    {
        try (GraphContext ctx = contextFactory.create())
        {
            // Create a project.
            

            // Create JAR entry in the graph.
            JarArchiveModel jarM = ctx.getFramed().addVertex(null, JarArchiveModel.class);
            jarM.setFilePath("src/test/resources/xercesImpl-2.9.1.jar");
            jarM.setArchiveName("xercesImpl-2.9.1.jar");

            // Get Java config and notify that we will not scan a source.
            WindupJavaConfigurationModel javaCfg = WindupJavaConfigurationService.getJavaConfigurationModel(ctx);
            javaCfg.setSourceMode(false);

            // Windup config.
            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(ctx);
            // Only run Victims Rules and those it needs.
            //wc.setRuleProviderFilter(new RuleProviderWithDependenciesPredicate(CheckArchivesWithVictimsRules.class));
            wc.setRuleProviderFilter(
                new OrPredicate(
                    new EnumerationOfRulesFilter(
                        UnzipArchivesToOutputRuleProvider.class,
                        ComputeArchivesSHA512.class, CheckArchivesWithVictimsRules.class,
                        UpdateVictimsDbRules.class, VictimsReportRules.class),
                    new PhaseRulesFilter.ReportingRulesFilter()));
            wc.setInputPath(Paths.get("src/test/resources/xercesImpl-2.9.1.jar.war"));
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            // Run.
            processor.execute(wc);


            // Check the results. There should be 2 jars found - 1 for the .jar, 1 for the .jar from the .war.
            GraphService<AffectedJarModel> jarsGS = new GraphService(ctx, AffectedJarModel.class);

            boolean found = false;
            for (AffectedJarModel jar : jarsGS.findAll())
            {
                log.info(jar.getFilePath());
                found = true;
                for( VulnerabilityModel vul : jar.getVulnerabilities() )
                    log.info("  " + vul.getCve());
            }
            Assert.assertTrue(found);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
