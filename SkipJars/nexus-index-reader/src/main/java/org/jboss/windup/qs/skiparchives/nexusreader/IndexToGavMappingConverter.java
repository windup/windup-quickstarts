package org.jboss.windup.qs.skiparchives.nexusreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.Bits;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.updater.IndexUpdateRequest;
import org.apache.maven.index.updater.IndexUpdateResult;
import org.apache.maven.index.updater.IndexUpdater;
import org.apache.maven.index.updater.ResourceFetcher;
import org.apache.maven.index.updater.WagonHelper;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.jboss.windup.util.Logging;


/**
 * Downloads Maven index from given repository and produces a list of all artifacts,
 * using this format: "SHA G:A:V[:C]".
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class IndexToGavMappingConverter
{
    private static final Logger log = Logging.get(IndexToGavMappingConverter.class);

    // Input
    private File dataDir;

    // Work objects

    private final PlexusContainer plexusContainer;
    private final Indexer indexer;
    private final IndexUpdater indexUpdater;
    private final Wagon httpWagon;
    private final IndexingContext centralContext;

    // Files where local cache is (if any) and Lucene Index should be located
    private final File centralLocalCache;
    private final File centralIndexDir;



    public IndexToGavMappingConverter(File dataDir, String id, String url)
        throws PlexusContainerException, ComponentLookupException, IOException
    {
        this.dataDir = dataDir;

        // Create Plexus container, the Maven default IoC container.
        final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
        config.setClassPathScanning( PlexusConstants.SCANNING_INDEX );
        this.plexusContainer = new DefaultPlexusContainer(config);

        // Lookup the indexer components from plexus.
        this.indexer = plexusContainer.lookup( Indexer.class );
        this.indexUpdater = plexusContainer.lookup( IndexUpdater.class );
        // Lookup wagon used to remotely fetch index.
        this.httpWagon = plexusContainer.lookup( Wagon.class, "http" );

        // Files where local cache is (if any) and Lucene Index should be located
        this.centralLocalCache = new File( this.dataDir, id + "-cache" );
        this.centralIndexDir = new File( this.dataDir,   id + "-index" );

        // Creators we want to use (search for fields it defines).
        // See https://maven.apache.org/maven-indexer/indexer-core/apidocs/index.html?constant-values.html
        List<IndexCreator> indexers = new ArrayList();
        // https://maven.apache.org/maven-indexer/apidocs/org/apache/maven/index/creator/MinimalArtifactInfoIndexCreator.html
        indexers.add( plexusContainer.lookup( IndexCreator.class, "min" ) );
        // https://maven.apache.org/maven-indexer/apidocs/org/apache/maven/index/creator/JarFileContentsIndexCreator.html
        //indexers.add( plexusContainer.lookup( IndexCreator.class, "jarContent" ) );
        // https://maven.apache.org/maven-indexer/apidocs/org/apache/maven/index/creator/MavenPluginArtifactInfoIndexCreator.html
        //indexers.add( plexusContainer.lookup( IndexCreator.class, "maven-plugin" ) );

        // Create context for central repository index.
        this.centralContext = this.indexer.createIndexingContext(
                id + "Context", id, this.centralLocalCache, this.centralIndexDir,
                url, null, true, true, indexers );
    }






    /**
     * Does all the downloading / partial update.
     */
    public void updateIndex() throws IOException
    {
        System.out.println( "Updating Index..." );
        System.out.println( "This might take a while on first run, so please be patient!" );
        // Create ResourceFetcher implementation to be used with IndexUpdateRequest
        // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
        TransferListener listener = new AbstractTransferListener()
        {
            public void transferStarted( TransferEvent transferEvent )
            {
                log.info("  Downloading " + transferEvent.getResource().getName() );
            }

            public void transferProgress( TransferEvent transferEvent, byte[] buffer, int length )
            {
            }

            public void transferCompleted( TransferEvent transferEvent )
            {
                log.info( " - Done" );
            }
        };

        // Let's go download.
        ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher( httpWagon, listener, null, null );

        Date repoCurrentTimestamp = this.centralContext.getTimestamp();
        IndexUpdateRequest updateRequest = new IndexUpdateRequest( this.centralContext, resourceFetcher );
        IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex( updateRequest );
        if ( updateResult.isFullUpdate() )
            log.info("Index of Maven repo '" + this.centralContext.getId() + "' updated. URL: " + this.centralContext.getRepositoryUrl());
        else if (updateResult.getTimestamp().equals(repoCurrentTimestamp))
            log.info("No update needed, index is up to date!");
        else
            log.info("Incremental update happened, change covered " + repoCurrentTimestamp
                    + " - " + updateResult.getTimestamp() + " period.");
    }


    /**
     * Prints all artifacts from the index, using format: SHA1 = G:A:V[:C].
     */
    public void printAllArtifacts(Writer out) throws IOException
    {
        final IndexSearcher searcher = this.centralContext.acquireIndexSearcher();
        try
        {
            final IndexReader ir = searcher.getIndexReader();
            Bits liveDocs = MultiFields.getLiveDocs(ir);
            for ( int i = 0; i < ir.maxDoc(); i++ )
            {
                if ( liveDocs == null || liveDocs.get( i ) )
                {
                    final Document doc = ir.document( i );
                    final ArtifactInfo ai = IndexUtils.constructArtifactInfo( doc, this.centralContext );

                    if (ai == null)
                        continue;
                    out.append(StringUtils.lowerCase(ai.getSha1())).append(' ');
                    out.append(ai.getGroupId()).append(":").append(ai.getArtifactId()).append(":").append(ai.getVersion()).append(":").append(ai.getClassifier());
                    out.append('\n');
                }
            }
        }
        finally
        {
            this.centralContext.releaseIndexSearcher( searcher );
        }
    }


    public void close() throws IOException
    {
        this.indexer.closeIndexingContext( this.centralContext, false );
    }


    // TODO: Remove.
    public static void main( String[] args )
        throws Exception
    {
        final IndexToGavMappingConverter converter = new IndexToGavMappingConverter(new File("target/"), "central", "http://repo1.maven.org/maven2");
        converter.updateIndex();
        final String shaToGavFile = "target/central.SHA1toGAVs.txt";
        log.info("Printing all artifacts to " + shaToGavFile);
        converter.printAllArtifacts(new FileWriter(shaToGavFile));
    }
}
