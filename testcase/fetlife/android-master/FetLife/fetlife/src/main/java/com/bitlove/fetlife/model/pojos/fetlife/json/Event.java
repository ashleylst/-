
package com.bitlove.fetlife.model.pojos.fetlife.json;

import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    @JsonProperty("address")
    private String address;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("cost")
    private String cost;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dress_code")
    private String dressCode;

    @JsonProperty("end_date_time")
    private String endDateTime;

    @JsonProperty("id")
    private String id;

    @JsonProperty("location")
    private String location;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("name")
    private String name;

    @JsonProperty("start_date_time")
    private String startDateTime;

    @JsonProperty("tagline")
    private String tagline;

    @JsonProperty("url")
    private String url;


    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("cost")
    public String getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(String cost) {
        this.cost = cost;
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

    @JsonProperty("dress_code")
    public String getDressCode() {
        return dressCode;
    }

    @JsonProperty("dress_code")
    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    @JsonProperty("end_date_time")
    public String getEndDateTime() {
        return endDateTime;
    }

    @JsonProperty("end_date_time")
    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("start_date_time")
    public String getStartDateTime() {
        return startDateTime;
    }

    @JsonProperty("start_date_time")
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    @JsonProperty("tagline")
    public String getTagline() {
        return tagline;
    }

    @JsonProperty("tagline")
    public void setTagline(String tagline) {
        this.tagline = tagline;
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
