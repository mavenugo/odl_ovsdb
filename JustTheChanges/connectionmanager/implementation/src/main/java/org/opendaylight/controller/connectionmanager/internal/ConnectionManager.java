
/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * Connection Manager provides south-bound connectivity services.
 * The APIs are currently focused towards Active-Active Clustering support
 * wherein the node can connect to any of the Active Controller in the Cluster.
 * This component can also host the necessary logic for south-bound connectivity
 * when partial cluster is identified during Partition scenarios.
 *
 * But this (and its corresponding implementation) component can also be used for
 * basic connectivity mechansims for various south-bound plugins.
 */

package org.opendaylight.controller.connectionmanager.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.controller.clustering.services.IClusterGlobalServices;
import org.opendaylight.controller.clustering.services.ICoordinatorChangeAware;
import org.opendaylight.controller.connectionmanager.ConnectionMgmtScheme;
import org.opendaylight.controller.connectionmanager.IConnectionManager;
import org.opendaylight.controller.connectionmanager.scheme.AbstractScheme;
import org.opendaylight.controller.connectionmanager.scheme.SchemeFactory;
import org.opendaylight.controller.sal.configuration.INetworkConfigurationService;
import org.opendaylight.controller.sal.connection.ConnectionConstants;
import org.opendaylight.controller.sal.connection.IConnectionListener;
import org.opendaylight.controller.sal.connection.IConnectionService;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.inventory.IListenInventoryUpdates;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class ConnectionManager implements IConnectionManager, IConnectionListener,
                                          ICoordinatorChangeAware, IListenInventoryUpdates,
                                          CommandProvider {
    private static final Logger logger = LoggerFactory
            .getLogger(ConnectionManager.class);
    private ConnectionMgmtScheme activeScheme = ConnectionMgmtScheme.ANY_CONTROLLER;
    private IClusterGlobalServices clusterServices;
    private ConcurrentMap<ConnectionMgmtScheme, AbstractScheme> schemes;
    private IConnectionService connectionService;
    private INetworkConfigurationService networkConfigurationService;

    public void setClusterServices(IClusterGlobalServices i) {
        this.clusterServices = i;
    }

    public void unsetClusterServices(IClusterGlobalServices i) {
        if (this.clusterServices == i) {
            this.clusterServices = null;
        }
    }

    public void setConnectionService(IConnectionService i) {
        this.connectionService = i;
    }

    public void unsetConnectionService(IConnectionService i) {
        if (this.connectionService == i) {
            this.connectionService = null;
        }
    }


    public void setNetworkConfigurationService(INetworkConfigurationService i) {
        this.networkConfigurationService = i;
    }

    public void unsetNetworkConfigurationService(INetworkConfigurationService i) {
        if (this.networkConfigurationService == i) {
            this.networkConfigurationService = null;
        }
    }

    public void init() {
        schemes = new ConcurrentHashMap<ConnectionMgmtScheme, AbstractScheme>();
        for (ConnectionMgmtScheme scheme : ConnectionMgmtScheme.values()) {
            AbstractScheme schemeImpl = SchemeFactory.getScheme(scheme, clusterServices);
            if (schemeImpl != null) schemes.put(scheme, schemeImpl);
        }
    }

    public void start() {
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this, null);
    }

    public void stop() {
        Set<Node> localNodes = getLocalNodes();
        if (localNodes != null) {
            for (Node localNode : localNodes) {
                connectionService.disconnect(localNode);
                AbstractScheme scheme = schemes.get(activeScheme);
                if (scheme != null) scheme.removeNode(localNode);
                logger.info("Disconnected Node : "+localNode);
            }
        }
    }

    @Override
    public ConnectionMgmtScheme getActiveScheme() {
        return activeScheme;
    }

    @Override
    public Set<Node> getNodes(InetAddress controller) {
        AbstractScheme scheme = schemes.get(activeScheme);
        if (scheme == null) return null;
        return scheme.getNodes(controller);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Set<Node> getLocalNodes() {
        return getNodes(clusterServices.getMyAddress());
    }

    public boolean isLocal(Node node) {
        AbstractScheme scheme = schemes.get(activeScheme);
        if (scheme == null) return true;
        return scheme.isLocal(node);
    }

    @Override
    public boolean isConnectionAllowed(Node node) {
        AbstractScheme scheme = schemes.get(activeScheme);
        if (scheme == null) return true;
        return scheme.isConnectionAllowed(node);
    }

    @Override
    public void updateNode(Node node, UpdateType type, Set<Property> props) {
        logger.debug("updateNode: {} type {} props {} for container {}",
                new Object[] { node, type, props });
        AbstractScheme scheme = schemes.get(activeScheme);
        if (scheme == null) return;
        switch (type) {
        case ADDED:
            scheme.addNode(node, clusterServices.getMyAddress());
            break;
        case REMOVED:
            scheme.removeNode(node);
            break;
        default:
                break;
        }
    }

    @Override
    public void updateNodeConnector(NodeConnector nodeConnector,
            UpdateType type, Set<Property> props) {
    }

    @Override
    public void coordinatorChanged() {
        AbstractScheme scheme = schemes.get(activeScheme);
        if (scheme == null) return;
        scheme.handleClusterViewChanged();
    }

    @Override
    public Status disconnect (Node node) {
        if (connectionService == null) return new Status(StatusCode.NOSERVICE);
        return connectionService.disconnect(node);
    }

    @Override
    public Node connect (String type, String identifier, Map<ConnectionConstants, String> params) {
        if (connectionService == null) return null;
        return connectionService.connect(type, identifier, params);
    }

    @Override
    public Node connect (String identifier, Map<ConnectionConstants, String> params) {
        if (connectionService == null) return null;
        return connectionService.connect(identifier, params);
    }

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---Connection Manager - As an OVSDB Testing tool ---\n");
        help.append("\t connect <identifier> <ip-address> [<port>]    -    Connect to ovsdb-server\n");
        help.append("\t disconnect <node-id>    -    Disconnect from ovsdb-server\n");
        help.append("\t addBridge <node-id> <Bridge-Identifier>     -    Add a bridge with name = Bridge-Identifier \n");
        return help.toString();
    }

    public void _addBridge(CommandInterpreter ci) {
        String st = ci.nextArgument();
        if (st == null) {
            ci.println("Please enter valid Node");
            return;
        }

        Node node = Node.fromString(st);
        if (node == null) {
            ci.println("Please enter node id");
            return;
        }

        String bi = ci.nextArgument();
        if (bi == null) {
            ci.println("Please enter valid Bridge Identifier");
            return;
        }

        networkConfigurationService.createBridgeDomain(node, bi);
    }

    public void _connect (CommandInterpreter ci) {
        String identifier = ci.nextArgument();
        if (identifier == null) {
            ci.println("Please enter valid Connection Identifier");
            return;
        }

        String address = ci.nextArgument();
        if (address == null) {
            ci.println("Please enter valid ovsdb-server IP Address");
            return;
        }

        try {
            InetAddress inet = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            ci.println("Please enter valid ovsdb-server IP Address");
            return;
        }

        String port = ci.nextArgument();
        if (port == null) {
            port = "6634";
        }
        Map<ConnectionConstants, String> params = new HashMap<ConnectionConstants, String>();
        params.put(ConnectionConstants.ADDRESS, address);
        params.put(ConnectionConstants.PORT, port);
        ci.println("Connected Node : " + this.connect(identifier, params));
    }

    public void _disconnect (CommandInterpreter ci) {
        String st = ci.nextArgument();
        if (st == null) {
            ci.println("Please enter valid Node");
            return;
        }

        Node node = Node.fromString(st);
        if (node == null) {
            ci.println("Please enter node id");
            return;
        }

        ci.println("Disconnect status : " + this.disconnect(node).toString());
    }

}
