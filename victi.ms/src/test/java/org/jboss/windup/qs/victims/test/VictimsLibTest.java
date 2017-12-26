package org.jboss.windup.qs.victims.test;

import com.redhat.victims.VictimsException;
import com.redhat.victims.database.VictimsDB;
import com.redhat.victims.database.VictimsDBInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jboss.windup.qs.victims.ComputeArchivesVictimsHashRules;
import org.jboss.windup.util.Logging;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the library itself.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class VictimsLibTest
{
    private static final Logger log = Logging.get(VictimsLibTest.class);


    // Path to a jars known to contain a vulnerability.
    private static final String VULNERABLE_JAR1_PATH = "target/testJars/xercesImpl-2.9.1.jar";
    // Looks like the Xerces vulnerability is not in the Victims database. Adding another one.
    private static final String VULNERABLE_JAR2_PATH = "src/test/resources/commons-fileupload-1.0-beta-1.jar";

    // SHA-512 checksum of xerces:xercesImpl:2.9.1
    private static final String VULNERABLE_JAR1_SHA512 = "ec2200e5a5a70f5c64744f6413a546f5e4979b3fb1649b02756ff035d36dde31170eaadc70842230296b60896f04877270c26b40415736299aef44ac16c5811c";

    // Contained in FILEHASHES table. Not sure if it is supposed to be found by Victims API.
    private static final String SOME_VICTIMS_HASH = "851eba12748a1aada5829e3a8e2eba05435efaaef9f0e7f68f6246dc1f6407ca56830ef00d587e91c3d889bb70eaf605a305652479ba6986a90b3986f0e74daf";


    @Test
    public void test01Update() throws IOException, VictimsException
    {
        try {
            VictimsDBInterface db = VictimsDB.db();
            System.out.println(" DB records:   " + db.getRecordCount());
            System.out.println(" Syncing...");
            // Update (goes to ~/.victims)
            db.synchronize();
            System.out.println(" DB records:   " + db.getRecordCount());
            Assert.assertTrue("DB has some recods after update.", db.getRecordCount() > 0);
            System.out.println("Database last updated on: " + db.lastUpdated().toString());
        }
        catch (VictimsException ex){
            // Prevent failure if offline. Just a warning.
            if ("Failed to sync database".equals(ex.getMessage()))
                log.warning(ex.getMessage());
            else
                throw ex;
        }
    }

    @Test @Ignore
    public void test02IdentifyVulnerableJarHash(){
        try
        {
            VictimsDBInterface db = VictimsDB.db();
            final HashSet<String> vulnerabilities = db.getVulnerabilities(SOME_VICTIMS_HASH);
            Assert.assertTrue("Found some vulnerability for hash " + SOME_VICTIMS_HASH, !vulnerabilities.isEmpty());
        }
        catch (VictimsException ex){
            // Prevent failure if offline. Just a warning.
            throw new RuntimeException("Failed when identifying a vulnerable jar", ex);
        }
    }

    @Test
    public void test03IdentifyVulnerableXercesJarHash(){
        try
        {
            final File vulnerableJar = new File(VULNERABLE_JAR2_PATH);
            final String hash = ComputeArchivesVictimsHashRules.computeVictimsHash(new FileInputStream(vulnerableJar), vulnerableJar.getName());

            VictimsDBInterface db = VictimsDB.db();
            final HashSet<String> vulnerabilities = db.getVulnerabilities(hash);
            Assert.assertTrue("Found some vulnerability for hash " + hash, !vulnerabilities.isEmpty());
            log.info(String.format("Vulnerabilities found in %s: ", vulnerableJar.getPath()) + StreamSupport.stream(vulnerabilities.spliterator(), false).collect(Collectors.joining(", ")));
        }
        catch (VictimsException ex){
            // Prevent failure if offline. Just a warning.
            throw new RuntimeException("Failed when identifying a vulnerable jar", ex);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
