package org.jboss.windup.qs.victims.model;

import com.syncleus.ferma.annotations.Adjacency;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.jboss.windup.graph.model.TypeValue;
import org.jboss.windup.rules.apps.java.model.JarArchiveModel;

import java.util.List;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@TypeValue(AffectedJarModel.TYPE)
public interface AffectedJarModel extends JarArchiveModel
{
    public static final String TYPE = "victims:affectedJar";
    public static final String VULN = "victims:affectedBy";

    @Adjacency(label = VULN, direction = Direction.OUT)
    public List<VulnerabilityModel> getVulnerabilities();

    @Adjacency(label = VULN, direction = Direction.OUT)
    public AffectedJarModel addVulnerability(VulnerabilityModel vul);
}// class
