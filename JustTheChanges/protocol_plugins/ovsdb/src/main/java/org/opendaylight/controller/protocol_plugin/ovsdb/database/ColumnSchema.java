package org.opendaylight.controller.protocol_plugin.ovsdb.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColumnSchema {
    @JsonProperty("type")
    public OvsdbType type;
    @JsonProperty("ephemeral")
    public Boolean ephemeral;
    @JsonProperty("mutable")
    public Boolean mutable;

}
