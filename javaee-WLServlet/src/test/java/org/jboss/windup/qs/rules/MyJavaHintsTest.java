package org.jboss.windup.qs.rules;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.engine.WindupProcessor;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupConfigurationModel;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.reporting.model.ClassificationModel;
import org.jboss.windup.reporting.model.InlineHintModel;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceModel;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MyJavaHintsTest
{
    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.windup.config:windup-config"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
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
    private GraphContext context;

    
    @Test
    public void testJavaHints()
    {
        try
        {
            FileModel inputPath = context.getFramed().addVertex(null, FileModel.class);
            inputPath.setFilePath("src/test/java/org/jboss/windup/qs/rules");
            FileModel fileModel = context.getFramed().addVertex(null, FileModel.class);
            fileModel.setFilePath("src/test/java/org/jboss/windup/qs/rules/MyJavaHintsTest.java");
            
            WindupConfigurationModel config = GraphService.getConfigurationModel(context);
            config.setInputPath(inputPath);
            config.setSourceMode(true);
            
            processor.execute();
            
            
            GraphService<InlineHintModel> hintService = new GraphService<>(context, InlineHintModel.class);
            GraphService<ClassificationModel> classificationService = new GraphService<>(context, ClassificationModel.class);
            
            GraphService<TypeReferenceModel> typeRefService = new GraphService<>(context, TypeReferenceModel.class);
            Iterable<TypeReferenceModel> typeReferences = typeRefService.findAll();
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
