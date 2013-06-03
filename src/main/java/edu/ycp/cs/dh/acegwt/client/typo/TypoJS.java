package edu.ycp.cs.dh.acegwt.client.typo;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
    See https://github.com/swenson/ace_spell_check_js/blob/master/spellcheck_ace.js
 */
public class TypoJS {

    private static final Logger LOGGER = Logger.getLogger(TypoJS.class.getName());

    private JavaScriptObject dictionary;

    public JavaScriptObject getDictionary() {
        return dictionary;
    }

    public boolean isLoaded() {
        LOGGER.log(Level.INFO, "Check if dictionary is loaded");
        return dictionary != null;
    }

    public TypoJS() {
        loadDictionary();
    }

    private native void loadDictionary() /*-{
        if ($wnd.jQuery == undefined) {
            $wnd.alert("window.jQuery is undefined! Please make sure you have included the appropriate JavaScript files.");
            return;
        }

        if ($wnd.Typo == undefined) {
            $wnd.alert("window.Typo is undefined! Please make sure you have included the appropriate JavaScript files.");
            return;
        }

        var lang = "en_US";
        var dicPath = "javascript/typojs/en_US.dic";
        var affPath = "javascript/typojs/en_US.aff";
        var dicData, affData;

        // keep a reference to this, so we can use it inside the closures below.
        var me = this;

        $wnd.jQuery.get(dicPath, function(data) {
            dicData = data;
        }).done(function() {
                $wnd.jQuery.get(affPath, function(data) {
                    affData = data;
                }).done(function() {
                        console.log("Dictionary Loaded");
                        me.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::dictionary = new $wnd.Typo(lang, affData, dicData);
                    });
            });
    }-*/;


}
