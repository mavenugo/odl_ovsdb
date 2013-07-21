package org.opendaylight.controller.connectionmanager.scheme;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.opendaylight.controller.clustering.services.CacheConfigException;
import org.opendaylight.controller.clustering.services.CacheExistException;
import org.opendaylight.controller.clustering.services.IClusterGlobalServices;
import org.opendaylight.controller.clustering.services.IClusterServices;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScheme {
    private static final Logger log = LoggerFactory.getLogger(AbstractScheme.class);

    protected IClusterGlobalServices clusterServices = null;
    protected ConcurrentMap <InetAddress, Set<Node>> nodeConnections;

    protected abstract boolean isConnectionAllowedInternal(Node node);

    protected AbstractScheme(IClusterGlobalServices clusterServices) {
        this.clusterServices = clusterServices;
        if (clusterServices != null) {
            allocateCaches();
            retrieveCaches();
        }
    }

    public boolean isConnectionAllowed (Node node) {
        if (clusterServices == null || nodeConnections == null) {
            return true;
        }

        boolean ret = isConnectionAllowedInternal(node);
        return ret;
    }

    @SuppressWarnings("deprecation")
    public void handleClusterViewChanged() {
        List<InetAddress> controllers = clusterServices.getClusteredControllers();

        List<InetAddress> toRemove = new ArrayList<InetAddress>();
        for (InetAddress c : nodeConnections.keySet()) {
            if (!controllers.contains(c)) {
                toRemove.add(c);
            }
        }

        for (InetAddress c : toRemove) {
            log.info("Removing Controller : {} from the Connections table", c);
            nodeConnections.remove(c);
        }
    }

    public Set<Node> getNodes(InetAddress controller) {
        if (nodeConnections == null) return null;
        return nodeConnections.get(controller);
    }

    @SuppressWarnings("deprecation")
    public boolean isLocal(Node node) {
        if (nodeConnections == null) return true;
        InetAddress myController = clusterServices.getMyAddress();
        Set<Node> nodes = nodeConnections.get(myController);
        if (nodes != null && nodes.contains(node)) return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    public Status removeNode (Node node) {
        return removeNodeFromController(node, clusterServices.getMyAddress());
    }

    protected Status removeNodeFromController (Node node, InetAddress controller) {
        if (node == null || controller == null) {
            return new Status(StatusCode.BADREQUEST);
        }

        if (clusterServices == null || nodeConnections == null) {
            return new Status(StatusCode.SUCCESS);
        }

        Set<Node> oldNodes = nodeConnections.get(controller);

        if (oldNodes != null && oldNodes.contains(node)) {
            Set<Node> newNodes = new HashSet<Node>(oldNodes);
            newNodes.remove(node);
            if (!nodeConnections.replace(controller, oldNodes, newNodes)) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                return removeNodeFromController(node, controller);
            }
        }
        return new Status(StatusCode.SUCCESS);

    }

    private Status putNodeToController (Node node, InetAddress controller) {
        if (clusterServices == null || nodeConnections == null) {
            return new Status(StatusCode.SUCCESS);
        }

        Set <Node> oldNodes = nodeConnections.get(controller);
        Set <Node> newNodes = null;
        if (oldNodes == null) {
            newNodes = new HashSet<Node>();
        } else {
            newNodes = new HashSet<Node>(oldNodes);
        }
        newNodes.add(node);

        if (nodeConnections.putIfAbsent(controller, newNodes) != null) {
            if (!nodeConnections.replace(controller, oldNodes, newNodes)) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                return putNodeToController(node, controller);
            }
        }
        return new Status(StatusCode.SUCCESS);
    }

    public Status addNode (Node node, InetAddress controller) {
        if (node == null || controller == null) {
            return new Status(StatusCode.BADREQUEST);
        }

        if (clusterServices == null || nodeConnections == null) {
            return new Status(StatusCode.SUCCESS);
        }

        for (InetAddress c : nodeConnections.keySet()) {
            removeNodeFromController(node, c);
        }

        return putNodeToController(node, controller);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private void retrieveCaches() {
        if (this.clusterServices == null) {
            log.error("un-initialized clusterServices, can't retrieve cache");
            return;
        }

        nodeConnections = (ConcurrentMap<InetAddress, Set<Node>>) clusterServices.getCache("connectionmanager.nodeconnections");

        if (nodeConnections == null) {
            log.error("\nFailed to get caches");
        }
    }

    @SuppressWarnings("deprecation")
    private void allocateCaches() {
        if (this.clusterServices == null) {
            log.error("un-initialized clusterServices, can't create cache");
            return;
        }

        try {
            clusterServices.createCache("connectionmanager.nodeconnections", EnumSet.of(IClusterServices.cacheMode.NON_TRANSACTIONAL));
        } catch (CacheExistException cee) {
            log.error("\nCache already exists - destroy and recreate if needed");
        } catch (CacheConfigException cce) {
            log.error("\nCache configuration invalid - check cache mode");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
