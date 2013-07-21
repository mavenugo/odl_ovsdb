package org.opendaylight.controller.protocol_plugin.ovsdb;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import org.opendaylight.controller.protocol_plugin.ovsdb.internal.Connection;
import org.opendaylight.controller.protocol_plugin.ovsdb.internal.OvsdbIO;
import org.opendaylight.controller.protocol_plugin.ovsdb.internal.OvsdbMessage;

public class OvsdbClient {

    private static final OvsdbIO ioHandler = new OvsdbIO();

    public void listDatabases() throws IOException, Throwable{
        try{
            String identifier = "TEST";
            InetAddress address = InetAddress.getByName("172.28.30.51");
            int port = 6634;
            Connection connection = OvsdbIO.connect(identifier, address, port);
            if (connection != null) {
                OvsdbMessage message = new OvsdbMessage("list_dbs", null, "test");
                connection.sendMessage(message);
                connection.readResponse(String[].class);
            }
        }catch(Exception e){
        }
    }
}
