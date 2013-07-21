/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.sal.implementation.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.opendaylight.controller.sal.configuration.INetworkConfigurationService;
import org.opendaylight.controller.sal.configuration.IPluginInNetworkConfigurationService;
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

public class ConfigurationService implements INetworkConfigurationService {
    protected static final Logger logger = LoggerFactory
            .getLogger(ConfigurationService.class);
    private Set<IConnectionListener> listeners = Collections
            .synchronizedSet(new HashSet<IConnectionListener>());
    private ConcurrentMap<String, IPluginInNetworkConfigurationService> pluginService =
            new ConcurrentHashMap<String, IPluginInNetworkConfigurationService>();

    void setPluginService (Map props, IPluginInNetworkConfigurationService s) {
        String type = null;
        logger.trace("Received setPluginService request");
        Object value = props.get(GlobalConstants.PROTOCOLPLUGINTYPE.toString());
        if (value instanceof String) {
            type = (String) value;
        }
        if (type == null) {
            logger.error("Received a PluginInConfigurationService without any "
                    + "protocolPluginType provided");
        } else {
            this.pluginService.put(type, s);
            logger.debug("Stored the PluginInConfigurationService for type: {}", type);
        }
    }

    void unsetPluginService(Map props, IPluginInNetworkConfigurationService s) {
        String type = null;
        logger.trace("Received unsetPluginService request");

        Object value = props.get(GlobalConstants.PROTOCOLPLUGINTYPE.toString());
        if (value instanceof String) {
            type = (String) value;
        }
        if (type == null) {
            logger.error("Received a PluginInConfigurationService without any "
                    + "protocolPluginType provided");
        } else if (this.pluginService.get(type).equals(s)) {
            this.pluginService.remove(type);
            logger.debug("Removed the PluginInConfigurationService for type: {}", type);
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
    public boolean createBridgeDomain(Node node, String bridgeIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.createBridgeDomain(node, bridgeIdentifier);
        return false;
    }

    @Override
    public boolean deleteBridgeDomain(Node node, String bridgeIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.deleteBridgeDomain(node, bridgeIdentifier);
        return false;
    }

    @Override
    public List<String> getBridgeDomains(Node node) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.getBridgeDomains(node);
        return null;
    }

    @Override
    public boolean addBridgeDomainConfig(Node node, String bridgeIdentifier, Map<String, String> config) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.addBridgeDomainConfig(node, bridgeIdentifier, config);
        return false;
    }

    @Override
    public boolean removeBridgeDomainConfig(Node node, String bridgeIdentifier, Map<String, String> config) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.removeBridgeDomainConfig(node, bridgeIdentifier, config);
        return false;
    }

    @Override
    public Map<String, String> getBridgeDomainConfigs(Node node, String bridgeIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.getBridgeDomainConfigs(node, bridgeIdentifier);
        return null;
    }

    @Override
    public boolean createBridgeConnector(Node node, String bridgeConnectorIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.createBridgeConnector(node, bridgeConnectorIdentifier);
        return false;
    }

    @Override
    public boolean deleteBridgeConnector(Node node, String bridgeConnectorIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.deleteBridgeConnector(node, bridgeConnectorIdentifier);
        return false;
    }

    @Override
    public boolean associateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.associateBridgeConnector(node, bridgeIdentifier, bridgeConnectorIdentifier);
        return false;
    }

    @Override
    public boolean disassociateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.disassociateBridgeConnector(node, bridgeIdentifier, bridgeConnectorIdentifier);
        return false;
    }

    @Override
    public boolean addBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map<String, String> config) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.addBridgeConnectorConfig(node, bridgeConnectorIdentifier, config);
        return false;
    }

    @Override
    public boolean removeBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map<String, String> config) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.removeBridgeConnectorConfig(node, bridgeConnectorIdentifier, config);
        return false;
    }

    @Override
    public Map<String, String> getBridgeConnectorConfigs(Node node, String bridgeConnectorIdentifier) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.getBridgeConnectorConfigs(node, bridgeConnectorIdentifier);
        return null;
    }

    @Override
    public Object genericConfigurationEvent(Node node, Map<String, String> config) {
        IPluginInNetworkConfigurationService configPlugin = pluginService.get(node.getType());
        if (configPlugin != null) return configPlugin.genericConfigurationEvent(node, config);
        return null;
    }
}
