package org.jboss.windup.qs.victims;

import java.util.List;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.operation.ruleelement.AbstractIterationOperation;
import org.jboss.windup.config.phase.ReportGeneration;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.graph.model.WindupConfigurationModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.graph.service.WindupConfigurationService;
import org.jboss.windup.qs.victims.model.AffectedJarModel;
import org.jboss.windup.qs.victims.model.VictimsReportModel;
import org.jboss.windup.rules.apps.java.model.WindupJavaConfigurationModel;
import org.jboss.windup.reporting.model.TemplateType;
import org.jboss.windup.reporting.service.ReportService;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;


/**
 * Creates a report for all the ignored files along with all the regexes they were matched against.
 *
 * @author Ondrej Zizka
 */
public class VictimsReportRules extends WindupRuleProvider
{
    public static final String TITLE = "Archives affected by security vulnerabilities";
    public static final String TEMPLATE_REPORT = "/org/jboss/windup/qs/victims/Report-Security.html";

    @Override
    public List<Class<? extends WindupRuleProvider>> getExecuteAfter()
    {
        return asClassList(UpdateVictimsDbRules.class, ReportGeneration.class);
    }


    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
    {
        ConditionBuilder applicationProjectModelsFound = Query.fromType(WindupJavaConfigurationModel.class);

        AbstractIterationOperation<WindupJavaConfigurationModel> addApplicationReport = new AbstractIterationOperation<WindupJavaConfigurationModel>()
        {
            @Override
            public void perform(GraphRewrite event, EvaluationContext context, WindupJavaConfigurationModel payload)
            {
                WindupConfigurationModel configurationModel = WindupConfigurationService.getConfigurationModel(event.getGraphContext());
                ProjectModel projectModel = configurationModel.getInputPath().getProjectModel();
                createReport(event.getGraphContext(), payload, projectModel);
            }

            @Override
            public String toString()
            {
                return "VictimsReportRules";
            }
        };

        return ConfigurationBuilder.begin()
                    .addRule()
                    .when(applicationProjectModelsFound)
                    .perform(addApplicationReport);

    }

    // @formatter:on


    private VictimsReportModel createReport(GraphContext graphCtx,
                WindupJavaConfigurationModel javaCfg, ProjectModel rootProjectModel)
    {
        GraphService<VictimsReportModel> reportServ = new GraphService(graphCtx, VictimsReportModel.class);
        VictimsReportModel reportM = reportServ.create();

        // Report metadata
        reportM.setReportPriority(1000);
        reportM.setReportName(TITLE);
        reportM.setMainApplicationReport(false);
        reportM.setDisplayInApplicationReportIndex(true);
        reportM.setProjectModel(rootProjectModel);
        reportM.setTemplatePath(TEMPLATE_REPORT);
        reportM.setTemplateType(TemplateType.FREEMARKER);
        reportM.setDisplayInApplicationList(false);

        // Get all jars
        GraphService<AffectedJarModel> jarService = new GraphService(graphCtx, AffectedJarModel.class);
        Iterable<AffectedJarModel> jars = jarService.findAll();

        // For each affected jar...
        for (AffectedJarModel jar : jars)
        {
            reportM.addAffectedJar(jar);
        }

        // Set the filename for the report
        ReportService reportService = new ReportService(graphCtx);
        reportService.setUniqueFilename(reportM, "vulnerableJars", "html");
        return reportM;
    }

}
