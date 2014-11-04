package org.jboss.windup.qs.rules;

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
import org.jboss.windup.graph.model.resource.FileModel;
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
                @AddonDependency(name = "org.jboss.windup.quickstarts:windup-ejb-beanutils-async"),
                @AddonDependency(name = "org.jboss.windup.config:windup-config"),
                @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
                @AddonDependency(name = "org.jboss.windup.utils:utils"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:rules-java"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:rules-java-ee"),
                @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
                @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
    })
    public static ForgeArchive getDeployment()
    {
        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
                    .addBeansXML()
                    .addAsAddonDependencies(
                                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                                AddonDependencyEntry.create("org.jboss.windup.quickstarts:windup-ejb-beanutils-async"),
                                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec"),
                                AddonDependencyEntry.create("org.jboss.windup.utils:utils"),
                                AddonDependencyEntry.create("org.jboss.windup.rules.apps:rules-java"),
                                AddonDependencyEntry.create("org.jboss.windup.rules.apps:rules-java-ee"),
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
            FileModel fileModel = context.getFramed().addVertex(null, FileModel.class);
            fileModel.setFilePath("src/test/resources/app");

            WindupJavaConfigurationModel javaCfg = WindupJavaConfigurationService.getJavaConfigurationModel(context);
            javaCfg.setSourceMode(true);

            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(context);
            wc.setInputPath(Paths.get("src/test/resources/app/"));
            wc.setOutputDirectory(outPath);

            processor.execute(wc);

            InlineHintService hintService = new InlineHintService(context);
            Iterable<InlineHintModel> hints = hintService.findAll();
            boolean hintFound = false;
            for (InlineHintModel hint : hints)
            {
                if ("BeanUtils Asynchronous is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asynchronous annotation."
                            .equals(hint.getHint()))
                {
                    hintFound = true;
                }
            }
            Assert.assertTrue(hintFound);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
