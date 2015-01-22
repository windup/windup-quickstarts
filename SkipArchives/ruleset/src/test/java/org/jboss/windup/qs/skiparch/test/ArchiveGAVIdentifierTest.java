package org.jboss.windup.qs.skiparch.test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.jboss.windup.qs.skiparch.lib.ArchiveGAVIdentifier;
import org.jboss.windup.qs.skiparch.lib.GAV;
import org.jboss.windup.qs.skiparch.lib.SkippedArchives;
import org.jboss.windup.util.Logging;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the library itself.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class ArchiveGAVIdentifierTest
{
    private static final Logger log = Logging.get(ArchiveGAVIdentifierTest.class);


    @Test
    public void testIdentifyArchive() throws IOException
    {
        ArchiveGAVIdentifier.addMappingsFrom(Paths.get("src/test/java/org/jboss/windup/qs/skipjars/test/data/sha1ToGAV.txt"));
        GAV gav = ArchiveGAVIdentifier.getGAVFromSHA1("11856de4eeea74ce134ef3f910ff8d6f989dab2e");
        Assert.assertEquals("org.jboss.windup", gav.getGroupId());
        Assert.assertEquals("windup-bootstrap", gav.getArtifactId());
        Assert.assertEquals("2.0.0.Beta7", gav.getVersion());
        Assert.assertEquals(null, gav.getClassifier());
    }

    @Test
    public void testSkippedArchives() throws IOException
    {
        SkippedArchives.addSkippedArchivesFrom(Paths.get("src/test/java/org/jboss/windup/qs/skipjars/test/data/skippedArchives.txt"));
        log.info("Skipped archives count: " + SkippedArchives.getCount());
        Assert.assertNotEquals("There are some skipped archives", 0, SkippedArchives.getCount());

        // org.jboss.windup.*:*:*
        GAV gav = new GAV("org.jboss.windup","windup-foo","1.2.3");
        Assert.assertTrue("GAV is skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // org.apache.commons.*:*:*
        gav = new GAV("org.apache.commons.foo","commons-foo","1.2.3");
        Assert.assertTrue("GAV is skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // org.jboss.bar:bar-*:*:*
        gav = new GAV("org.jboss.bar","bar-foo","1.2.3");
        Assert.assertTrue("GAV is skipped: " + gav,  SkippedArchives.isSkipped(gav));
        gav = new GAV("org.jboss.bar","just-foo","1.2.3");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // org.hibernate.*:hibernate-core:3.*~4.*
        gav = new GAV("org.hibernate.foo","hibernate-core","3.2.1");
        Assert.assertTrue("GAV is skipped: " + gav,  SkippedArchives.isSkipped(gav));
        gav = new GAV("org.hibernate.foo","hibernate-core","4.2.1");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // org.hibernate.*:hibernate-core:3.*~4.*
        gav = new GAV("org.hibernate.foo","hibernate-core","1.2.3");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // org.freemarker:freemarker-core:3.1
        gav = new GAV("org.freemarker.foo","freemarker-core","3.1");
        Assert.assertTrue("GAV is skipped: " + gav,  SkippedArchives.isSkipped(gav));
        gav = new GAV("org.freemarker.foo","freemarker-core","3.1.1");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));
        gav = new GAV("org.freemarker","freemarker-core","4.2.1");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));

        // Not listed at all
        gav = new GAV("cz.dynawest.foo","dynawest-foo","1.2.3");
        Assert.assertFalse("GAV is not skipped: " + gav,  SkippedArchives.isSkipped(gav));
    }



}// class
