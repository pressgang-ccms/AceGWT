package edu.ycp.cs.dh.acegwt.client.tagdb;

/**
 * A callback used to get the REST server to call.
 */
public interface GetRESTServerCallback {
    /**
     * @return The base URL of the REST end points.
     */
    String getBaseRESTURL();
}
