package edu.ycp.cs.dh.acegwt.client.tagdb;

import com.google.gwt.json.client.JSONObject;

/**
 * Holds a collection of XML tag names and a description of that tag
 */
public class TagDB {
    private boolean loaded = false;
    private final JSONObject database = new JSONObject();
    private String restEndpoint;

    /**
     * @return true if the database is populated, false otherwise
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded true if the database is populated, false otherwise
     */
    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * @return a mapping of XML tag names to descriptions
     */
    public JSONObject getDatabase() {
        return database;
    }

    /**
     *
     * @return a JSON representation of the database
     */
    public String getJSONDatabase() {
        return database.toString();
    }

    public TagDB() {

    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    public void setRestEndpoint(String restEndpoint) {
        this.restEndpoint = restEndpoint;
    }
}
