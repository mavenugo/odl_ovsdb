package org.opendaylight.controller.protocol_plugin.ovsdb.internal;

import org.opendaylight.controller.sal.core.Node;

public interface IConnectionServiceInternal {
    public Connection getConnection(Node node);
}
