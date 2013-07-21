package org.opendaylight.controller.protocol_plugin.ovsdb.internal;

import java.util.ArrayList;
import java.util.Map;

import org.opendaylight.controller.protocol_plugin.ovsdb.database.OvsdbType;

public class TransactOperation {
    public String op;
    public String table;
    public Map<String, Object> row;

    public TransactOperation(String op, String table, String enumName, ArrayList set){
        this.op = op;
        this.table = table;
        ArrayList result = new ArrayList();
        result.add("set");
        result.add(set);
        this.row.put(enumName, result);
    }

    public TransactOperation(String op, String table, String enumName, Object[] map){
        this.op = op;
        this.table = table;

    }

    public TransactOperation(String op, String table, Map<String, Object> row){
        this.op = op;
        this.table = table;
        this.row = row;
    }
}
