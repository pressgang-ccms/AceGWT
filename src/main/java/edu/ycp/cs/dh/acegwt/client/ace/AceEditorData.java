package edu.ycp.cs.dh.acegwt.client.ace;

import edu.ycp.cs.dh.acegwt.client.tagdb.XMLElementDB;
import edu.ycp.cs.dh.acegwt.client.typo.TypoJS;

public class AceEditorData {
    private String restUrl;
    private TypoJS positiveDictionary;
    private TypoJS negativeDictionary;
    private TypoJS negativePhraseDictionary;
    private XMLElementDB XMLElementDB;

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public TypoJS getPositiveDictionary() {
        return positiveDictionary;
    }

    public void setPositiveDictionary(TypoJS positiveDictionary) {
        this.positiveDictionary = positiveDictionary;
    }

    public TypoJS getNegativeDictionary() {
        return negativeDictionary;
    }

    public void setNegativeDictionary(TypoJS negativeDictionary) {
        this.negativeDictionary = negativeDictionary;
    }

    public TypoJS getNegativePhraseDictionary() {
        return negativePhraseDictionary;
    }

    public void setNegativePhraseDictionary(TypoJS negativePhraseDictionary) {
        this.negativePhraseDictionary = negativePhraseDictionary;
    }

    public XMLElementDB getXMLElementDB() {
        return XMLElementDB;
    }

    public void setXMLElementDB(XMLElementDB XMLElementDB) {
        this.XMLElementDB = XMLElementDB;
    }
}
