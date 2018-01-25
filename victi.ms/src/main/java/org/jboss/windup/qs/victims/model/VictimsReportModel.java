package org.jboss.windup.qs.victims.model;

import com.syncleus.ferma.annotations.Adjacency;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.jboss.windup.graph.model.TypeValue;
import org.jboss.windup.reporting.model.ApplicationReportModel;

/**
 * Root model for Victims report.
 *
 * @author Ondrej Zizka
 */
@TypeValue(VictimsReportModel.TYPE)
public interface VictimsReportModel extends ApplicationReportModel
{
    public static final String TYPE = "victims:report";
    public static final String AFFECTED_JARS = "affectedJars";

    /**
     * Jars affected by a vulnerability.
     */
    @Adjacency(label = AFFECTED_JARS, direction = Direction.OUT)
    public Iterable<AffectedJarModel> getAffectedJars();

    /**
     * Add a jar affected by a vulnerability.
     */
    @Adjacency(label = AFFECTED_JARS, direction = Direction.OUT)
    public void addAffectedJar(AffectedJarModel jar);
}
