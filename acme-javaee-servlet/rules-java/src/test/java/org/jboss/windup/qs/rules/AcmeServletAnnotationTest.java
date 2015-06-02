package org.jboss.windup.qs.rules;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.engine.predicates.RuleProviderWithDependenciesPredicate;
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.reporting.model.ClassificationModel;
import org.jboss.windup.reporting.model.InlineHintModel;
import org.jboss.windup.reporting.service.ClassificationService;
import org.jboss.windup.reporting.service.InlineHintService;
import org.jboss.windup.rules.apps.java.model.WindupJavaConfigurationModel;
import org.jboss.windup.rules.apps.java.service.WindupJavaConfigurationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AcmeServletAnnotationTest
{
    @Deployment
    @AddonDependencies({
                @AddonDependency(name = "org.jboss.windup.quickstarts:windup-acme-javaee-servlet-rules-java"),
                @AddonDependency(name = "org.jboss.windup.config:windup-config"),
                @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
                @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
                @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
                @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
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
    public void testJavaHints()
    {
        try (GraphContext context = contextFactory.create())
        {
            FileModel fileModel = context.getFramed().addVertex(null, FileModel.class);
            fileModel.setFilePath("src/test/resources/app/com/foo/MyAcmeServletUsingClass.java");

            WindupJavaConfigurationModel javaCfg = WindupJavaConfigurationService.getJavaConfigurationModel(context);
            javaCfg.setSourceMode(true);

            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(context);
            wc.setRuleProviderFilter(new RuleProviderWithDependenciesPredicate(AcmeServletAnnotationRuleProvider.class));
            wc.setInputPath(Paths.get("src/test/resources/app/"));
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            processor.execute(wc);

            InlineHintService hintService = new InlineHintService(context);
            ClassificationService classificationService = new ClassificationService(context);

            Iterable<InlineHintModel> hints = hintService.findAll();
            Iterable<ClassificationModel> classificationModels = classificationService.findAll();

            boolean acmeClassificationFound = false;
            for (ClassificationModel cm : classificationModels)
            {
                if ("Acme @AcmeServlet".equals(cm.getClassification()))
                {
                    acmeClassificationFound = true;
                }
            }
            Assert.assertTrue(acmeClassificationFound);

            boolean acmeHintFound = false;
            for (InlineHintModel hint : hints)
            {
                String acmeHintString = "Replace the proprietary Acme @AcmeServlet annotation with the Java EE 6 standard @WebServlet annotation.";
                if (acmeHintString.equals(hint.getHint()))
                {
                    acmeHintFound = true;
                }
            }
            Assert.assertTrue(acmeHintFound);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
