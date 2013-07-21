/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.sal.implementation.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.opendaylight.controller.sal.connection.ConnectionConstants;
import org.opendaylight.controller.sal.connection.IConnectionListener;
import org.opendaylight.controller.sal.connection.IConnectionService;
import org.opendaylight.controller.sal.connection.IPluginInConnectionService;
import org.opendaylight.controller.sal.connection.IPluginOutConnectionService;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.GlobalConstants;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionService implements IPluginOutConnectionService, IConnectionService {
    protected static final Logger logger = LoggerFactory
            .getLogger(ConnectionService.class);
    private Set<IConnectionListener> listeners = Collections
            .synchronizedSet(new HashSet<IConnectionListener>());
    private ConcurrentMap<String, IPluginInConnectionService> pluginService =
            new ConcurrentHashMap<String, IPluginInConnectionService>();

    void setPluginService (Map props, IPluginInConnectionService s) {
        String type = null;
        logger.trace("Received setPluginService request");
        Object value = props.get(GlobalConstants.PROTOCOLPLUGINTYPE.toString());
        if (value instanceof String) {
            type = (String) value;
        }
        if (type == null) {
            logger.error("Received a PluginInConnectionService without any "
                    + "protocolPluginType provided");
        } else {
            this.pluginService.put(type, s);
            logger.debug("Stored the PluginInConnectionService for type: {}", type);
        }
    }

    void unsetPluginService(Map props, IPluginInConnectionService s) {
        String type = null;
        logger.trace("Received unsetPluginService request");

        Object value = props.get(GlobalConstants.PROTOCOLPLUGINTYPE.toString());
        if (value instanceof String) {
            type = (String) value;
        }
        if (type == null) {
            logger.error("Received a PluginInConnectionService without any "
                    + "protocolPluginType provided");
        } else if (this.pluginService.get(type).equals(s)) {
            this.pluginService.remove(type);
            logger.debug("Removed the PluginInConnectionService for type: {}", type);
        }
    }

    void setListener(IConnectionListener s) {
        if (this.listeners != null) {
            this.listeners.add(s);
        }
    }

    void unsetListener(IConnectionListener s) {
        if (this.listeners != null) {
            this.listeners.remove(s);
        }
    }

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    void init() {
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
        if (this.listeners != null) {
            this.listeners.clear();
        }
        if (this.pluginService != null) {
            this.pluginService.clear();
        }
    }

    @Override
    public boolean isConnectionAllowed(Node node) {
        if (this.listeners == null || this.listeners.size() <= 0) return true;
        synchronized (this.listeners) {
            for (IConnectionListener s : this.listeners) {
                if (!s.isConnectionAllowed(node)) return false;
            }
        }
        return true;
    }

    @Override
    public Node connect (String type, String connectionIdentifier, Map<ConnectionConstants, String> params) {
        IPluginInConnectionService s = pluginService.get(type);
        if (s != null) return s.connect(connectionIdentifier, params);
        return null;
    }

    @Override
    public Node connect (String connectionIdentifier, Map<ConnectionConstants, String> params) {
        synchronized (this.pluginService) {
            for (String pluginType : this.pluginService.keySet()) {
                IPluginInConnectionService s = pluginService.get(pluginType);
                Node node = s.connect(connectionIdentifier, params);
                if (node != null) return node;
            }
        }
        return null;
    }

    @Override
    public Status disconnect(Node node) {
        IPluginInConnectionService s = pluginService.get(node.getType());
        if (s != null) return s.disconnect(node);
        return new Status(StatusCode.NOTFOUND);
    }
}
