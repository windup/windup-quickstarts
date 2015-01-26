package org.jboss.windup.qs.skiparchives.nexusreader;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;
import org.jboss.windup.util.Logging;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexToGavMappingConverterTest
{
    private static final Logger log = Logging.get(IndexToGavMappingConverterTest.class);


    public IndexToGavMappingConverterTest()
    {
    }


    @Test @Ignore
    public void testAUpdateIndex() throws Exception
    {
        final IndexToGavMappingConverter coverter = new IndexToGavMappingConverter(new File("target/"), "central", "http://repo1.maven.org/maven2");

        // Update the index (incremental update will happen if this is not 1st run and files are not deleted)
        coverter.updateIndex();
        coverter.close();
    }


    @Test
    public void testZPrintAllArtifacts() throws Exception
    {
        final IndexToGavMappingConverter coverter = new IndexToGavMappingConverter(new File("target/"), "central", "http://repo1.maven.org/maven2");

        final String shaToGavFile = "target/central.SHA1toGAVs.txt";
        log.info("Printing all artifacts to " + shaToGavFile);
        coverter.printAllArtifacts(new FileWriter(shaToGavFile));
        coverter.close();
        log.info("Sorting " + shaToGavFile);
        IndexToGavMappingConverter.sortFile(new File(shaToGavFile), new File("target/central.SHA1toGAVs.sorted.txt"));
    }
}
