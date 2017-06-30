package com.bitlove.fetlife.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Group {

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("description")
    private String description;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member_count")
    private Integer memberCount;

    @JsonProperty("name")
    private String name;

    @JsonProperty("rules")
    private String rules;

    @JsonProperty("url")
    private String url;

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("member_count")
    public Integer getMemberCount() {
        return memberCount;
    }

    @JsonProperty("member_count")
    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

}