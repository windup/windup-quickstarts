/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.windup.qs.skiparchives.nexusreader;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;
import org.jboss.windup.util.Logging;
import org.junit.Test;


/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class IndexToGavMappingConverterTest
{
    private static final Logger log = Logging.get(IndexToGavMappingConverterTest.class);


    public IndexToGavMappingConverterTest()
    {
    }


    @Test
    public void testUpdateIndex() throws Exception
    {
        final IndexToGavMappingConverter coverter = new IndexToGavMappingConverter(new File("target/"), "central", "http://repo1.maven.org/maven2");

        // Update the index (incremental update will happen if this is not 1st run and files are not deleted)
        coverter.updateIndex( );
        coverter.close();
    }


    @Test
    public void testPrintAllArtifacts() throws Exception
    {
        final IndexToGavMappingConverter coverter = new IndexToGavMappingConverter(new File("target/"), "central", "http://repo1.maven.org/maven2");

        final String shaToGavFile = "target/central.SHA1toGAVs.txt";
        log.info("Printing all artifacts to " + shaToGavFile);
        coverter.printAllArtifacts(new FileWriter(shaToGavFile));
        coverter.close();
    }
}
