/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.protocol_plugin.ovsdb.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.sal.configuration.IPluginInConfigurationService;
import org.opendaylight.controller.sal.core.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationService implements IPluginInConfigurationService
{
    private static final Logger logger = LoggerFactory
            .getLogger(ConfigurationService.class);

    IConnectionServiceInternal connectionService;

    void init() {
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy ()"
     * calls
     *
     */
    void stop() {
    }

    public void setConnectionServiceInternal(IConnectionServiceInternal connectionService) {
        this.connectionService = connectionService;
    }

    public void unsetConnectionServiceInternal(IConnectionServiceInternal connectionService) {
        if (this.connectionService == connectionService) {
            this.connectionService = null;
        }
    }

    @Override
    public boolean createBridgeDomain(Node node, String bridgeIdentifier) {
        if (connectionService == null) {
            logger.error("Couldnt refer to the ConnectionService");
            return false;
        }
        Connection connection = connectionService.getConnection(node);

        ArrayList nulist = new ArrayList();
        ArrayList nulist5 = new ArrayList();
        ArrayList nulist6 = new ArrayList();
        ArrayList nulist4 = new ArrayList();

        Map brmap1 = new LinkedHashMap();
        Map opinsert = new LinkedHashMap();
        Map opcomment = new LinkedHashMap();
        Map opmutate = new LinkedHashMap();

        ArrayList comarr = new ArrayList();
        ArrayList comarr2 = new ArrayList();
        ArrayList comarr3 = new ArrayList();
        ArrayList comarr4 = new ArrayList();

        ArrayList mutwhere = new ArrayList();
        ArrayList mutwhere2 = new ArrayList();
        ArrayList mutwhere3 = new ArrayList();
        ArrayList mutwhere4 = new ArrayList();
        ArrayList mutwhere5 = new ArrayList();
        ArrayList mutwhere6 = new ArrayList();

        brmap1.put("op", "insert");
        brmap1.put("table", "Bridge");
        brmap1.put("row", opinsert);
        opinsert.put("external_ids", nulist);
        nulist.add("map");
        nulist.add(nulist4);
        nulist4.add(nulist5);
        nulist4.add(nulist6);
        nulist5.add("brId");
        nulist5.add("11111111-1111-1111-1111-111111111111");
        nulist6.add("engine_test");
        nulist6.add("eTest");
        brmap1.put("uuid-name", "new_bridge");
        opinsert.put("name", bridgeIdentifier);

        // Mutate operation
        opmutate.put("op", "mutate");
        opmutate.put("table", "Open_vSwitch");
        opmutate.put("where", mutwhere);
        opmutate.put("mutations", mutwhere2);
        mutwhere2.add(mutwhere3);
        mutwhere3.add("bridges");
        mutwhere3.add("insert");
        mutwhere3.add(mutwhere4);
        mutwhere4.add("set");
        mutwhere4.add(mutwhere5);
        mutwhere5.add(mutwhere6);
        mutwhere6.add("named-uuid");
        mutwhere6.add("new_bridge");

        // Comment operation
        opcomment.put("op", "comment");
        opcomment.put("comment", "ovs-vsctl add-br x");

        /*
         * Add the 3 operations into the object
         * params along with the db_name OVS
         */

        Object[] params = { "Open_vSwitch", brmap1, opmutate, opcomment };

        OvsdbMessage message = new OvsdbMessage("transact", params, "efg");
        try {
            connection.sendMessage(message);
            connection.readResponse(String[].class);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteBridgeDomain(Node node, String bridgeIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getBridgeDomains(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addBridgeDomainConfig(Node node, String bridgeIdentifier, Map<String, String> config) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeBridgeDomainConfig(Node node, String bridgeIdentifier, Map<String, String> config) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, String> getBridgeDomainConfigs(Node node, String bridgeIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean createBridgeConnector(Node node, String bridgeConnectorIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteBridgeConnector(Node node, String bridgeConnectorIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean associateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean disassociateBridgeConnector(Node node, String bridgeIdentifier, String bridgeConnectorIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map<String, String> config) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeBridgeConnectorConfig(Node node, String bridgeConnectorIdentifier, Map<String, String> config) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, String> getBridgeConnectorConfigs(Node node, String bridgeConnectorIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

  }