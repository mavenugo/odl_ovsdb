
/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.connectionmanager;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import org.opendaylight.controller.sal.connection.ConnectionConstants;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.Status;

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

public interface IConnectionManager {
    /**
     * This method returns Connectivity Algorithm (Scheme) that is currently being used.
     *
     * @return ConnectionMgmtScheme Enum that represents the active scheme.
     */
    public ConnectionMgmtScheme getActiveScheme();

    /**
     * Method that will retrieve and return a Set of Nodes that is currently connected to the given controller.
     *
     * @param controller InetAddress of the Controller that is currently connected to a set of Nodes.
     *
     * @return Set<Node> Set of Nodes connected to a controller.
     */
    public Set<Node> getNodes(InetAddress controller);

    /**
     * Method that will retrieve and return a Set of Nodes that is currently connected to
     * the controller on which this method is executed.
     *
     * @return Set<Node> Set of Nodes connected to this controller.
     */
    public Set<Node> getLocalNodes();

    /**
     * Method to test if a node is local to a controller.
     *
     * @return true if node is local to this controller. false otherwise.
     */
    public boolean isLocal(Node node);

    /**
     * Disconnect a Node from the controller.
     *
     * @return Status of the disconnect Operation.
     */
    public Status disconnect(Node node);

    /**
     * Connect to a node
     *
     * @param type Type of the node representing NodeIDType.
     * @param identifier A String representation of the connection.
     * @param params Connection Params in Map format. This is entirely handled by the south-bound
     * plugins and is an opaque value for SAL or Connection Manager. Typical values keyed inside
     * this params are Management IP-Address, Username, Password, Security Keys, etc...
     *
     *  @return Status of the Connect Operation.
     */
    public Node connect (String type, String identifier, Map<ConnectionConstants, String> params);

    /**
     * Connect to a node without a Type. SAL will find out the best matching plugin based on params.
     *
     * @param identifier A String representation of the connection.
     * @param params Connection Params in Map format. This is entirely handled by the south-bound
     * plugins and is an opaque value for SAL or Connection Manager. Typical values keyed inside
     * this params are Management IP-Address, Username, Password, Security Keys, etc...
     *
     *  @return Status of the Connect Operation.
     */
    public Node connect (String identifier, Map<ConnectionConstants, String> params);
}
