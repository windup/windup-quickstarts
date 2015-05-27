package org.jboss.windup.qs.rules;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
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
    @AddonDependencies({
                @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
                @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
                @AddonDependency(name = "org.jboss.windup.config:windup-config"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java-ee"),
                @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
                @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
                @AddonDependency(name = "org.jboss.windup.quickstarts:windup-ejb-beanutils-async-rules-java"),
    })
    public static AddonArchive getDeployment()
    {
        final AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
            .addBeansXML();
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
                // Hint: [v[16128]={InlineHintModel:title: References annotation 'org.jboss.seam.annotations.async.Asynchronous', length: 239, InlineHintModel:effort: 8, lineNumber: 9, w:vertextype: [InlineHintModel, fileLocationModel, fileReferenceModel],
                //   InlineHintModel:hint: org.windup.examples.ejb.BeanUtilsAsyncUsingRemote uses the Seam @Asynchronous annotation. It is not compatible with JBoss EAP Remote EJBs and should be replaced with the standard Java EE 6 @Asynchronous annotation., startPosition: 0}]
                if ((""+hint.getHint()).matches(".*XML Rule Example.*BeanUtilsAsyncUsingRemote.*Seam.*@Asynchronous.*"))
                {
                    xmlRuleHintFound = true;
                }
                else if ((""+hint.getHint()).matches(".*BeanUtilsAsyncUsingRemote.*Seam.*@Asynchronous.*"))
                {
                    javaRuleHintFound = true;
                }
            }
            Assert.assertTrue("javaRuleHintFound", javaRuleHintFound);
            Assert.assertTrue("xmlRuleHintFound", xmlRuleHintFound);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
