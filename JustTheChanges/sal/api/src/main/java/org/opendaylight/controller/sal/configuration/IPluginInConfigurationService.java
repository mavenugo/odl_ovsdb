/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.sal.configuration;

import java.util.List;
import java.util.Map;

import org.opendaylight.controller.sal.core.Node;

/**
 * @file IPluginInConfigurationService.java
 *
 */
public interface IPluginInConfigurationService {
    /**
     * Create a Bridge Domain
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     */
    public boolean createBridgeDomain(Node node, String bridgeIdentifier);

    /**
     * Delete a Bridge Domain
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     */
    public boolean deleteBridgeDomain(Node node, String bridgeIdentifier);

    /**
     * Returns the configured Bridge Domains
     *
     * @param node Node serving this configuration service
     * @return Bridge Domains
     */
    public List<String> getBridgeDomains(Node node);

    /**
     * add Bridge Domain Configuration
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     * @param configs Map representation of ConfigName and Configuration Value in Strings.
     */
    public boolean addBridgeDomainConfig(Node node, String bridgeIdentifier, Map <String, String> config);

    /**
     * Delete Bridge Domain Configuration
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     * @param configs Map representation of ConfigName and Configuration Value in Strings.
     */
    public boolean removeBridgeDomainConfig(Node node, String bridgeIdentifier, Map <String, String> config);

    /**
     * Returns Bridge Domain Configurations
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     * @return Bridge Domain configurations
     */

    public Map <String, String> getBridgeDomainConfigs(Node node, String bridgeIdentifier);

    /**
     * Create a Bridge Connector
     *
     * @param node Node serving this configuration service
     * @param bridgeConnectorIdentifier String representation of the node connector.
     */
    public boolean createBridgeConnector(Node node, String bridgeConnectorIdentifier);

    /**
     * Delete a Bridge Connector
     *
     * @param node Node serving this configuration service
     * @param bridgeConnectorIdentifier String representation of the node connector.
     */
    public boolean deleteBridgeConnector(Node node, String bridgeConnectorIdentifier);

    /**
     * Add/Associate BridgeConnectors on a given Bridge Domain
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     * @param bridgeConnectorIdentifier String representation of the node connector.
     */
    public boolean associateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier);

    /**
     * Add/Associate BridgeConnectors on a given Bridge Domain
     *
     * @param node Node serving this configuration service
     * @param bridgeDomainIdentifier String representation of a Bridge Domain
     * @param bridgeConnectorIdentifier String representation of the node connector.
     */
    public boolean disassociateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier);

    /**
     * add Bridge Connector Configuration
     *
     * @param node Node serving this configuration service
     * @param bridgeConnectorIdentifier String representation of the node connector.
     * @param config Map representation of ConfigName and Configuration Value in Strings.
     */
    public boolean addBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map <String, String> config);

    /**
     * Delete Bridge Connector Configuration
     *
     * @param node Node serving this configuration service
     * @param bridgeConnectorIdentifier String representation of the node connector.
     * @param config Map representation of ConfigName and Configuration Value in Strings.
     */
    public boolean removeBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map <String, String> config);

    /**
     * Returns Bridge Connector Configurations
     *
     * @param node Node serving this configuration service
     * @param bridgeConnectorIdentifier String representation of a Bridge Connector
     * @return Bridge Connector configurations
     */
    public Map <String, String> getBridgeConnectorConfigs(Node node, String bridgeConnectorIdentifier);
}