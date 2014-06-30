package edu.ycp.cs.dh.acegwt.client.ace;

import edu.ycp.cs.dh.acegwt.client.tagdb.XMLElementDB;

public class AceEditorData {
    private String restUrl;
    private String typoJsBaseUrl;
    private String typeJsLang;
    private XMLElementDB XMLElementDB;

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getTypoJsBaseUrl() {
        return typoJsBaseUrl;
    }

    public void setTypoJsBaseUrl(String typoJsBaseUrl) {
        this.typoJsBaseUrl = typoJsBaseUrl;
    }

    public XMLElementDB getXMLElementDB() {
        return XMLElementDB;
    }

    public void setXMLElementDB(XMLElementDB XMLElementDB) {
        this.XMLElementDB = XMLElementDB;
    }

    public String getTypeJsLang() {
        return typeJsLang;
    }

    public void setTypeJsLang(String typeJsLang) {
        this.typeJsLang = typeJsLang;
    }
}
