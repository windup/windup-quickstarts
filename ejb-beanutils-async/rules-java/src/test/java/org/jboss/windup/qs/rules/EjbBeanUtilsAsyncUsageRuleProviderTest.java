package org.jboss.windup.qs.rules;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.jboss.windup.reporting.model.InlineHintModel;
import org.jboss.windup.reporting.service.InlineHintService;
import org.jboss.windup.rules.apps.java.model.WindupJavaConfigurationModel;
import org.jboss.windup.rules.apps.java.service.WindupJavaConfigurationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EjbBeanUtilsAsyncUsageRuleProviderTest
{
    @Deployment
    @Dependencies({
                @AddonDependency(name = "org.jboss.windup.quickstarts:windup-ejb-beanutils-async-rules-java"),
                @AddonDependency(name = "org.jboss.windup.config:windup-config"),
                @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
                @AddonDependency(name = "org.jboss.windup.utils:utils"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java-ee"),
                @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
                @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
    })
    public static ForgeArchive getDeployment()
    {
        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
                    .addBeansXML()
                    .addAsResource(new File("../rules-xml/async-method.windup.xml"))
                    .addAsAddonDependencies(
                                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                                AddonDependencyEntry.create("org.jboss.windup.quickstarts:windup-ejb-beanutils-async-rules-java"),
                                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec"),
                                AddonDependencyEntry.create("org.jboss.windup.utils:utils"),
                                AddonDependencyEntry.create("org.jboss.windup.rules.apps:windup-rules-java"),
                                AddonDependencyEntry.create("org.jboss.windup.rules.apps:windup-rules-java-ee"),
                                AddonDependencyEntry.create("org.jboss.windup.reporting:windup-reporting"),
                                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
                    );

        return archive;
    }

    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;

    @Test
    public void testRules()
    {
        Path outPath = Paths.get("target/WindupReport");
        try (GraphContext context = contextFactory.create(outPath))
        {
            WindupJavaConfigurationModel javaCfg = WindupJavaConfigurationService.getJavaConfigurationModel(context);
            javaCfg.setSourceMode(true);

            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(context);
            wc.setInputPath(Paths.get("../test-files/src_example"));
            wc.setOutputDirectory(outPath);

            processor.execute(wc);

            InlineHintService hintService = new InlineHintService(context);
            Iterable<InlineHintModel> hints = hintService.findAll();
            boolean javaRuleHintFound = false;
            boolean xmlRuleHintFound = false;
            for (InlineHintModel hint : hints)
            {
                System.out.println("Hint: " + hint);
                if ("org.windup.examples.ejb.BeanUtilsAsyncUsingRemote uses a Seam @Asynchronous annotation, but that is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asynchronous annotation."
                            .equals(hint.getHint()))
                {
                    javaRuleHintFound = true;
                }
                else if ("XML Rule Example: org.windup.examples.ejb.BeanUtilsAsyncUsingRemote uses a Seam @Asynchronous annotation, but that is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asynchronous annotation."
                            .equals(hint.getHint()))
                {
                    xmlRuleHintFound = true;
                }
            }
            Assert.assertTrue(javaRuleHintFound);
            Assert.assertTrue(xmlRuleHintFound);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
