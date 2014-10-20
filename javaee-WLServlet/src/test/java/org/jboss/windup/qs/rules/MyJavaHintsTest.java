package org.jboss.windup.qs.rules;

import java.nio.file.Paths;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.engine.predicates.RuleProviderWithDependenciesPredicate;
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.reporting.model.ClassificationModel;
import org.jboss.windup.reporting.model.InlineHintModel;
import org.jboss.windup.rules.apps.java.model.WindupJavaConfigurationModel;
import org.jboss.windup.rules.apps.java.scan.ast.JavaTypeReferenceModel;
import org.jboss.windup.rules.apps.java.service.WindupJavaConfigurationService;
//import org.jboss.windup.util.Checks;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MyJavaHintsTest
{
    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.windup.config:windup-config"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
        @AddonDependency(name = "org.jboss.windup.utils:utils"),
        @AddonDependency(name = "org.jboss.windup.rules.apps:rules-java"),
        @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
        @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
    })
    public static ForgeArchive getDeployment()
    {
        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
            .addBeansXML()
            .addClass(MyHintsRuleProvider.class)
            .addAsAddonDependencies(
                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec"),
                AddonDependencyEntry.create("org.jboss.windup.utils:utils"),
                AddonDependencyEntry.create("org.jboss.windup.rules.apps:rules-java"),
                AddonDependencyEntry.create("org.jboss.windup.reporting:windup-reporting"),
                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
            );

        return archive;
    }

    @Inject
    private MyHintsRuleProvider provider;

    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;


    @Test
    public void testJavaHints()
    {
        try(GraphContext context = contextFactory.create())
        {
            FileModel fileModel = context.getFramed().addVertex(null, FileModel.class);
            //fileModel.setFilePath("src/test/java/org/jboss/windup/qs/rules/MyJavaHintsTest.java");
            fileModel.setFilePath("src/test/resources/app/com/foo/MyWLServletUsingClass.java");

            WindupJavaConfigurationModel javaCfg = WindupJavaConfigurationService.getJavaConfigurationModel(context);
            javaCfg.setSourceMode(true);

            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(context);
            wc.setRuleProviderFilter(new RuleProviderWithDependenciesPredicate(MyHintsRuleProvider.class));
            wc.setInputPath(Paths.get("src/test/resources/app/"));
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            processor.execute(wc);


            GraphService<InlineHintModel> hintService = new GraphService<>(context, InlineHintModel.class);
            GraphService<ClassificationModel> clsfService = new GraphService<>(context, ClassificationModel.class);
            GraphService<JavaTypeReferenceModel> typeRefService = new GraphService<>(context, JavaTypeReferenceModel.class);

            Iterable<InlineHintModel> hints = hintService.findAll();
            Iterable<ClassificationModel> clsf = clsfService.findAll();
            Iterable<JavaTypeReferenceModel> typeRefs = typeRefService.findAll();
            //Checks.checkNotEmpty(hints);
            //Checks.checkNotEmpty(clsf);
            //Checks.checkNotEmpty(typeRefs);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
