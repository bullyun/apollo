package com.ctrip.framework.apollo.common.dto;

/**
 * @author bbb
 * @since 2020-03-13
 */
public class ServerConfigDTO extends BaseDTO{

    private String key;

    private String value;

    private String comment;

    private String cluster;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
