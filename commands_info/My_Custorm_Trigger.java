import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.RowUpdateBuilder;
import org.apace.cassandra.db.partitions.Partition;
import org.apache.cassandra.io.util.FileUtils;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.UUIDGen;


public class TestTrigger implements ITrigger {

    private Property properties = loadProperties ();

    public Collection<Mutation> augment (Partition update) {

        String k = properties.getProperty ("keyspace");
        String t = properties.getProperty ("table");

        RowUpdateBuilder rub = new RowUpdateBuilder (Schema.instance.getCFMetaData (k, t), FBUtilities.timestampMicros (), UUIDGen.getTimeUUID ());
        rub.add ("keyspace_name", update.metadata ().ksName);
        rub.add ("table_name", update.metadata ().cfname);
        rub.add ("primary_key", update.metadata ().getValidator ().getString (update.partitionKey ().getKey ()));
        
        return Collections.singletonList (rub.build ());
    }


    private static Properties loadProperties () {

        Properties properties = new Properties ();
        InputStream stream = TestTrigger.class.getClassLoader ().getResourceAsStream ("TestTrigger.properties");

        try {
            properties.load (stream);
        } catch (Exception e) {
            throw new RuntimeException (e);
        } finally {
           FileUtils.closeQuietly (stream);;
        }
        return properties;
    }
}
