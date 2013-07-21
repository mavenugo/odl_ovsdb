package org.opendaylight.controller.protocol_plugin.ovsdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.opendaylight.controller.protocol_plugin.ovsdb.internal.ConfigurationService;
import org.opendaylight.controller.protocol_plugin.ovsdb.internal.ConnectionService;
import org.opendaylight.controller.sal.connection.ConnectionConstants;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OvsdbTestAddBridge {
    private static final Logger logger = LoggerFactory
            .getLogger(OvsdbTestAddBridge.class);

    @Test
    public void addBridgeDomain() throws IOException, Throwable {
        Node.NodeIDType.registerIDType("OVS", String.class);
        NodeConnector.NodeConnectorIDType.registerIDType("OVS", String.class, "OVS");

        ConnectionService connectionService = new ConnectionService();
        connectionService.init();
        String identifier = "TEST";
        Map<ConnectionConstants, String> params = new HashMap<ConnectionConstants, String>();
        params.put(ConnectionConstants.ADDRESS, "172.28.30.51");

        Node node = connectionService.connect(identifier, params);
        if (node == null) {
            logger.error("Could not connect to ovsdb server");
            return;
        }

        ConfigurationService configurationService = new ConfigurationService();
        configurationService.setConnectionServiceInternal(connectionService);
        configurationService.createBridgeDomain(node, "JUNIT_BRIDGE_TEST");
    }
}
