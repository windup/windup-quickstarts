package org.jboss.windup.qs.identarch.lib;

import com.tinkerpop.frames.Property;
import org.jboss.windup.graph.model.WindupVertexFrame;

/**
 * Maven Artifact - only G:A:V:C.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public interface GAVModel extends WindupVertexFrame
{
    static final String PREFIX = "identarch:";

    @Property(PREFIX + "g")
    String getGroupId();
    @Property(PREFIX + "g")
    void setGroupId(String groupId);

    @Property(PREFIX + "a")
    String getArtifactId();
    @Property(PREFIX + "a")
    void setArtifactId(String artifactId);

    @Property(PREFIX + "c")
    String getClassifier();
    @Property(PREFIX + "c")
    void setClassifier(String classifier);

    @Property(PREFIX + "v")
    String getVersion();
    @Property(PREFIX + "v")
    void setVersion(String version);

    @Property(PREFIX + "sha1")
    String getSha1();
    @Property(PREFIX + "sha1")
    void setSha1(String sha1);
}
