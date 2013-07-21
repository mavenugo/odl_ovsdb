package org.opendaylight.controller.protocol_plugin.ovsdb;

import java.io.IOException;
import java.net.InetAddress;







import org.junit.Test;
import org.opendaylight.controller.protocol_plugin.ovsdb.database.*;
import org.opendaylight.controller.protocol_plugin.ovsdb.internal.*;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OvsdbTest {
    private static final Logger logger = LoggerFactory
            .getLogger(OvsdbTest.class);
    @Test
    public void messageTest() throws IOException, Throwable{
        Node.NodeIDType.registerIDType("OVS", String.class);
        NodeConnector.NodeConnectorIDType.registerIDType("OVS", String.class, "OVS");
        String identifier = "TEST";
        InetAddress address = InetAddress.getByName("172.28.30.51");
        Connection connection = OvsdbIO.connect(identifier, address);
        if (connection != null) {
            Object[] params = {"Open_vSwitch"};
            OvsdbMessage message = new OvsdbMessage("get_schema", params, "abc");
            connection.sendMessage(message);
            DatabaseSchema dbSchema = (DatabaseSchema) connection.readResponse(DatabaseSchema.class);
            TableSchema port = dbSchema.tables.get("Port");
            ColumnSchema vlan_mode = port.columns.get("vlan_mode");
            Object[] ovsdbEnum = vlan_mode.type.key.ovsdbEnum;
            logger.info(ovsdbEnum[1].toString());
        }
    }
}
