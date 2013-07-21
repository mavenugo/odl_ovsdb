package org.opendaylight.controller.connectionmanager.scheme;

import java.net.InetAddress;
import java.util.Set;
import org.opendaylight.controller.clustering.services.IClusterGlobalServices;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AnyControllerScheme extends AbstractScheme {
    private static final Logger logger = LoggerFactory.getLogger(AnyControllerScheme.class);
    private static AbstractScheme myScheme= null;

    protected AnyControllerScheme(IClusterGlobalServices clusterServices) {
        super(clusterServices);
    }

    public static AbstractScheme getScheme(IClusterGlobalServices clusterServices) {
        if (myScheme == null) {
            myScheme = new AnyControllerScheme(clusterServices);
        }
        return myScheme;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isConnectionAllowedInternal(Node node) {
        if (nodeConnections == null) return true;
        ISwitchManager switchManager = (ISwitchManager) ServiceHelper
                .getInstance(ISwitchManager.class, "default", this);

        if (switchManager == null) return false;

        for (InetAddress controller : nodeConnections.keySet()) {
            if (!controller.equals(clusterServices.getMyAddress())) {
                Set<Node> nodes = nodeConnections.get(controller);
                if (nodes != null && nodes.contains(node)) return false;
            }
        }

        logger.info("Could NOT Match on "+node.toString());
        return true;
    }
}
