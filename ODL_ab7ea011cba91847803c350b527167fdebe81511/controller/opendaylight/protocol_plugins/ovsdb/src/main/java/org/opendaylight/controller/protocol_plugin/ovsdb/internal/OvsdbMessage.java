package org.opendaylight.controller.protocol_plugin.ovsdb.internal;

import java.util.Random;

public class OvsdbMessage {
    String methodName;
    Object[] argument;
    String id;

    public OvsdbMessage(String method, Object[] arg, String id){
        this.methodName = method;
        this.argument = arg;
        Random x = new Random();
        this.id = Integer.toString(x.nextInt(10000));
    }
}
