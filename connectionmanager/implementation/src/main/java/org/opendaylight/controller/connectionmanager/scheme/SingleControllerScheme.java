package org.opendaylight.controller.connectionmanager.scheme;

import java.net.InetAddress;
import java.util.Set;
import org.opendaylight.controller.clustering.services.IClusterGlobalServices;
import org.opendaylight.controller.sal.core.Node;

class SingleControllerScheme extends AbstractScheme {

    private static AbstractScheme myScheme= null;

    protected SingleControllerScheme(IClusterGlobalServices clusterServices) {
        super(clusterServices);
    }

    public static AbstractScheme getScheme(IClusterGlobalServices clusterServices) {
        if (myScheme == null) {
            myScheme = new SingleControllerScheme(clusterServices);
        }
        return myScheme;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isConnectionAllowedInternal(Node node) {
        if (nodeConnections == null) return true;
        for (InetAddress controller : nodeConnections.keySet()) {
            if (controller.equals(clusterServices.getMyAddress())) continue;
            Set<Node> nodes = nodeConnections.get(controller);
            if (nodes != null && nodes.size() > 1) return false;
        }
        return true;
    }

}
