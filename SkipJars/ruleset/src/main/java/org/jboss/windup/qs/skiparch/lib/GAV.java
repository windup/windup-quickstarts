package org.jboss.windup.qs.skiparch.lib;

import org.apache.commons.lang.StringUtils;

/**
 *  Maven G:A:V coordinates.
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class GAV
{


    @Override
    public String toString()
    {
        return "GAV{" + "sha1:" + sha1 + " " + groupId + ":" + artifactId + ":" + version + ":" + classifier + '}';
    }
    private final String sha1;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private String classifier;


    /**
     * Parses the information from "G:A:V[:C]".
     */
    public static GAV fromGAV(String gavStr)
    {
        String[] parts = StringUtils.split(gavStr, " :");
        if(parts.length < 3)
            throw new IllegalArgumentException("Expected GAV definition format is 'G:A:V[:C]', was: " + gavStr);
        GAV gav = new GAV(parts[0], parts[1], parts[2]);
        if(parts.length >= 4)
            gav.setClassifier(parts[3]);

        return gav;
    }

    /**
     * Parses the information from "SHA1 G:A:V[:C]".
     */
    public static GAV fromSHA1AndGAV(String sha1AndGAV)
    {
        String[] parts = StringUtils.split(sha1AndGAV, " :");
        if(parts.length < 4)
            throw new IllegalArgumentException("Expected GAV definition format is 'SHA1 G:A:V[:C]', was: " + sha1AndGAV);
        GAV gav = new GAV(parts[0], parts[1], parts[2], parts[3]);
        if(parts.length >= 5)
            gav.setClassifier(parts[4]);

        return gav;
    }


    public GAV(String sha1, String groupId, String artifactId, String version)
    {
        this.sha1 = sha1;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }


    public GAV(String groupId, String artifactId, String version)
    {
        this(null, groupId, artifactId, version);
    }




    public String getSha1()
    {
        return sha1;
    }


    public String getGroupId()
    {
        return groupId;
    }


    public String getArtifactId()
    {
        return artifactId;
    }


    public String getVersion()
    {
        return version;
    }


    public String getClassifier()
    {
        return classifier;
    }




    /**
     * TODO: Make this unmodifiable?
     */
    public void setClassifier(String classifier)
    {
        this.classifier = classifier;
    }



}// class
