// Copyright (c) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package edu.ycp.cs.dh.acegwt.client.ace;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import edu.ycp.cs.dh.acegwt.client.tagdb.TagDB;
import edu.ycp.cs.dh.acegwt.client.typo.TypoJS;

/**
  A GWT widget for the Ajax.org Code Editor (ACE).

  Matthew Casperson - This class has been edited to remove the startEditor function. The ACE editor is created and destroyed
  using the attach events, values for things like text, mode, theme etc are cached until the ACE editor is available.

  The IsEditor interface has been implemented, to allow the ACE editor to bind to POJOs with the Editor framework. And
  references to "require" have been removed.

  Requirements:
  * ace.js from the src or src-min builds (https://github.com/ajaxorg/ace-builds/)
  * Resources for a slightly modified jQuery conext menu (https://github.com/pressgang-ccms/PressGangCCMSUI/blob/Development/src/main/webapp/javascript/contextmenu) and
  * typo.js (https://github.com/pressgang-ccms/PressGangCCMSUI/tree/Development/src/main/webapp/javascript/typojs)
  * styles for misspelt words:
        .ace_marker-layer div[class^='misspelled'] {
            position: absolute;
            z-index: -2;
            background-color:
            rgba(255, 0, 0, 0.2);
        }

        div[class^='misspelled'] {
            background-color: rgba(255, 0, 0, 0.2);
        }

        .ace_marker-layer div[class^='badword'] {
            position: absolute;
            z-index: -2;
            background-color: rgba(245, 255, 0, 0.2);
        }

        div[class^='badword'] {
            background-color: rgba(245, 255, 0, 0.2);
        }

  @see <a href="http://ace.ajax.org/">Ajax.org Code Editor</a>
 */
public class AceEditor extends Composite implements RequiresResize, IsEditor<LeafValueEditor<String>> {
    // Used to generate unique element ids for Ace widgets.
    private static int nextId = 0;

    private final String elementId;

    private final TypoJS positiveDictionary;
    private final TypoJS negativeDictionary;
    private final TypoJS negativePhraseDictionary;
    private final TagDB tagDB;

    private JavaScriptObject editor;

    private JavaScriptObject spellcheckInterval;
    private JavaScriptObject matchTagsInterval;

    private JsArray<AceAnnotation> annotations = JavaScriptObject.createArray().cast();
    
    private static final Logger logger = Logger.getLogger(AceEditor.class.getName());

    /**
     * This value is used as a buffer to hold the text before the editor is created
     */
    private String text = null;
    /**
     * This value is used as a buffer to hold the theme before the editor is created
     */
    private AceEditorTheme theme = null;
    /**
     * This value is used as a buffer to hold the mode before the editor is created
     */
    private AceEditorMode mode = null;
    /**
     * This value is used as a buffer to hold the readonly state before the editor is created
     */
    private boolean readOnly = false;
    /**
     * This value is used as a buffer to hold the "use soft tabs" state before the editor is created
     */
    private boolean useSoftTabs = false;
    /**
     * This value is used as a buffer to hold the "tab size" state before the editor is created
     */
    private int tabSize = 2;
    /**
     * This value is used as a buffer to hold the "always show horizontal scrollbar" state before the editor is created
     */
    private boolean hScrollBarAlwaysVisible = false;
    /**
     * This value is used as a buffer to hold the "show gutter" state before the editor is created
     */
    private boolean showGutter = true;
    /**
     * This value is used as a buffer to hold the "highlight word" state before the editor is created
     */
    private boolean highlightSelectedWord = false;
    /**
     * This value is used as a buffer to hold the "show print margin" state before the editor is created
     */
    private boolean showPrintMargin = false;
    /**
     * This value is used as a buffer to hold the "user wrap mode" state before the editor is created.
     */
    private boolean useWrap = true;
    /**
     * This value is used as a buffer to hold the "show invisible characters" state before the editor is created
     */
    private boolean showInvisibles = false;
    /**
     * The spell checking web worker
     */
    private JavaScriptObject spellCheckingWorker;
    /**
     * The tag matching web worker
     */
    private JavaScriptObject tagMatchingWorker;

    /**
     * This constructor will only work if the <code>.ace_editor</code> CSS class is set with
     * <code>position: relative !important;</code>. A better idea is to use the {@link AceEditor#AceEditor(boolean,TypoJS)} constructor
     * and pass it the value <code>true</code>; this will work without any changes to the <code>.ace_editor</code> class.
     */
    @Deprecated
    public AceEditor() {
        this(false, null, null, null, null);
    }

    public AceEditor(final boolean positionAbsolute) {
        this(positionAbsolute, null, null, null, null);
    }

    public AceEditor(final boolean positionAbsolute, final TypoJS positiveDictionary) {
        this(positionAbsolute, positiveDictionary, null, null, null);
    }

    public AceEditor(final boolean positionAbsolute, final TypoJS positiveDictionary, final TypoJS negativeDictionary) {
        this(positionAbsolute, positiveDictionary, negativeDictionary, null, null);
    }

    public AceEditor(final boolean positionAbsolute, final TypoJS positiveDictionary, final TypoJS negativeDictionary, final TypoJS negativePhraseDictionary) {
        this(positionAbsolute, positiveDictionary, negativeDictionary, negativePhraseDictionary, null);
    }

    /**
     * Preferred constructor. You should pass <code>true</code> to this constructor, unless you did something special to
     * redefine the <code>.ace_editor</code> CSS class.
     *
     * ACE builds up to the 17th of December 2012 (https://github.com/ajaxorg/ace-builds/tree/97bae1d132effbeca5eef8aaef2a2a1fa836b181)
     * use absolute positioning by default, and so positionAbsolute should be set to true. Later builds use relative positing by
     * default, and so positionAbsolute should be set to false.
     *
     * If you have upgraded your version of the ACE javascript files, and you see nothing on the screen, try setting positionAbsolute
     * to false.
     * 
     * @param positionAbsolute true if the <code>.ace_editor</code> CSS class is set with <code>position: absolute;</code>,
     *        which is the default; false if <code>.ace_editor</code> is set to use <code>position: relative;</code>
     */
    public AceEditor(final boolean positionAbsolute,
                     final TypoJS positiveDictionary,
                     final TypoJS negativeDictionary,
                     final TypoJS negativePhraseDictionary,
                     final TagDB tagDB) {
        this.positiveDictionary = positiveDictionary;
        this.negativeDictionary = negativeDictionary;
        this.negativePhraseDictionary = negativePhraseDictionary;
        this.tagDB = tagDB;

        elementId = "_aceGWT" + nextId;
        nextId++;

        HTML html;

        if (!positionAbsolute) {
            // Create a single div with width/height 100% with the generated
            // element id. The ACE editor will replace this div.
            // Note that the .ace_editor style must be set with
            // "position: relative !important;" for this this to work.
            html = new HTML("<div style=\"width: 100%; height: 100%;\" id=\"" + elementId + "\"></div>");
        } else {
            // Create a div with "position: relative;" that will expand to fill
            // its parent.
            // Then nest a div with the generated element id inside it.
            // The ACE editor will replace the inner div. Because ACE defaults
            // to absolute positioning, we can set left/right/top/bottom to 0,
            // causing ACE to completely expand to fill the outer div.
            html = new HTML("<div style=\"width: 100%; height: 100%; position: relative;\">"
                    + "<div style=\"top: 0px; bottom: 0px; left: 0px; right: 0px;\" id=\"" + elementId + "\"></div>" + "</div>");
        }

        initWidget(html);
    }

    /**
     * Does nothing - left for compatibility
     */
    public void startEditor() {

    }

    /**
     * Call this method to start the editor. Make sure that the widget has been attached to the DOM tree before calling this
     * method.
     * 
     * @param text The initial text to be placed into the editor
     */
    private native void startEditorNative(final String text, final String themeName, final String shortModeName,
            final boolean readOnly, final boolean useSoftTabs, final int tabSize, final boolean hScrollBarAlwaysVisible,
            final boolean showGutter, final boolean highlightSelectedWord, final boolean showPrintMargin,
            final boolean userWrap, final boolean showInvisibles) /*-{

		console.log("ENTER AceEditor.startEditorNative()");

		if ($wnd.ace == undefined) {
			$wnd.alert("window.ace is undefined! Please make sure you have included the appropriate JavaScript files.");
			return;
		}

		console.log("\tAssign ACE editor variable");
		var editor = $wnd.ace.edit(this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::elementId);
		editor.getSession().setUseWorker(false);
		this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor = editor;

		// I have been noticing sporadic failures of the editor
		// to display properly and receive key/mouse events.
		// Try to force the editor to resize and display itself fully.  See:
		//    https://groups.google.com/group/ace-discuss/browse_thread/thread/237262b521dcea33
		console.log("\tForce resize and redisplay");
		editor.resize();
		this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::redisplay();

		console.log("\tSetting Options");
		// Set code folding (choose from manual, markbegin, markbeginend)
		editor.getSession().setFoldStyle("markbeginend");

		// Set theme
		if (themeName != null)
		{
			console.log("\t\tSetting Theme");
			editor.setTheme("ace/theme/" + themeName);
		}

		// Set mode
		if (shortModeName != null)
		{
			console.log("\t\tSetting Mode");
			editor.getSession().setMode("ace/mode/" + shortModeName);
		}

		// Set read only
		console.log("\t\tSetting Read Only");
		editor.setReadOnly(readOnly);

		// Set soft tabs
		console.log("\t\tSetting Soft Tabs");
		editor.getSession().setUseSoftTabs(useSoftTabs);

		// Set the tab size
		console.log("\t\tSetting Tab Size");
		editor.getSession().setTabSize(tabSize);

		// Set horizontal scroll bar
		console.log("\t\tSetting Horizontal Scroll Bar");
		editor.renderer.setHScrollBarAlwaysVisible(hScrollBarAlwaysVisible);

		// Show gutter
		console.log("\t\tSetting Gutter");
		editor.renderer.setShowGutter(showGutter);

		// Highlight selected word
		console.log("\t\tSetting Highlight Word Select");
		editor.setHighlightSelectedWord(highlightSelectedWord);

		// Set print margin
		console.log("\t\tSetting Print Margin");
		editor.renderer.setShowPrintMargin(showPrintMargin);

		// Set wrapping. 
		console.log("\t\tSetting User Wrap");
        
        editor.getSession().setUseWrapMode(false);
        
        if (userWrap) {
            editor.getSession().setUseWrapMode(true);
        }

		// Show invisibles
		console.log("\t\tSetting Show Invisible Characters");
		editor.setShowInvisibles(showInvisibles);

        // Set text
        if (text != null)
        {
            console.log("\t\tSetting Text");
            editor.getSession().setValue(text);
        }

        console.log("\t\tEnabling Spell Checking");
        this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::setupContextMenu()();
        this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::enableSpellCheckingEnabledNative()();
        this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::enableTagMatching()();

        console.log("\t\tEnabling Snippets");
        this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::enableSnippets()();

		console.log("EXIT AceEditor.startEditorNative()");

    }-*/;
    
    /**
     * Called before the widget is removed from the DOM. Save the state of the text, and set
     * wrapping mode to false to prevent a lock up with Chrome 23
     */
    @Override
    protected void onUnload()
    {
        logger.log(Level.INFO, "ENTER AceEditor.onUnload()");
        super.onUnload();
        this.text = this.getText();
        this.setUseWrapModeNative(false);
        logger.log(Level.INFO, "EXIT AceEditor.onUnload()");
    }
    
    /**
     * Called after the widget is removed from the DOM. Clean up the ACE editor.
     */
    @Override 
    protected void onDetach()
    {
        logger.log(Level.INFO, "ENTER AceEditor.onDetach()");
        super.onDetach();
        destroy();
        logger.log(Level.INFO, "EXIT AceEditor.onDetach()");
    }
    
    /** Called after a the widget is added to the DOM. Add the ACE editor */
    @Override
    protected void onLoad()
    {
        logger.log(Level.INFO, "ENTER AceEditor.onLoad()");
        super.onLoad();
        startEditorNative(text, theme != null ? theme.getName() : null, mode != null ? mode.getName() : null,
                readOnly, useSoftTabs, tabSize, hScrollBarAlwaysVisible, showGutter, highlightSelectedWord,
                showPrintMargin, useWrap, showInvisibles);
        logger.log(Level.INFO, "EXIT AceEditor.onLoad()");
    }

    /**
     * Call this to force the editor contents to be redisplayed. There seems to be a problem when an AceEditor is embedded in a
     * LayoutPanel: the editor contents don't appear, and it refuses to accept focus and mouse events, until the browser window
     * is resized. Calling this method works around the problem by forcing the underlying editor to redisplay itself fully. (?)
     */
    public native void redisplay() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;

		if (editor != null) {
			editor.renderer.onResize(true);
			editor.renderer.updateFull();
			editor.resize();
			editor.focus();
		} else {
			console.log("editor == null. redisplay() was not called successfully.");
		}
    }-*/;

    /**
     * Cleans up the entire editor.
     */
    public native void destroy() /*-{
		try {
            console.log("ENTER AceEditor.destroy()");

            var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
            var spellcheckInterval = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellcheckInterval;
            var matchTagsInterval = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval;
            var spellingWorker = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellCheckingWorker;
            var tagMatchingWorker = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagMatchingWorker;



            // clean up pending operations
            if (spellcheckInterval != null) {
                clearInterval(spellcheckInterval);
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellcheckInterval = null;
            }

            // clean up pending operations
            if (matchTagsInterval != null) {
                clearInterval(matchTagsInterval);
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval = null;
            }

            if (spellingWorker != null) {
                spellingWorker.terminate();
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellCheckingWorker = null;
            }

            if (tagMatchingWorker != null) {
                tagMatchingWorker.terminate();
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagMatchingWorker = null;
            }

            if (editor != null) {

                //editor.getSession().removeAllListeners('change');
                //editor.getSession().removeAllListeners('changeCursor');

                editor.destroy();
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor = null;
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::positiveDictionary = null;


            } else {
                console.log("editor == null. destory() was not called successfully.");
            }
        } finally {
            console.log("EXIT AceEditor.destroy()");
        }


    }-*/;
    
    public native void focus() /*-{
        var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
        if (editor != null) {
            editor.focus();
        } else {
            console.log("editor == null. focus() was not called successfully.");
        }
           
    }-*/;

    private native void setShowInvisiblesNative(final boolean showInvisibles) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.setShowInvisibles(showInvisibles);
		} else {
			console.log("editor == null. setShowInvisiblesNative() was not called successfully.");
		}
    }-*/;

    public void setShowInvisibles(final boolean showInvisibles) {
        this.showInvisibles = showInvisibles;
        setShowInvisiblesNative(showInvisibles);
    }

    public boolean getShowInvisibles() {
        return this.showInvisibles;
    }

    /**
     * Set the theme.
     * 
     * @param theme the theme (one of the values in the {@link AceEditorTheme} enumeration)
     */
    public void setTheme(final AceEditorTheme theme) {
        this.theme = theme;
        setThemeByName(theme.getName());
    }

    /**
     * Set the theme by name.
     * 
     * @param themeName the theme name (e.g., "twilight")
     */
    public native void setThemeByName(String themeName) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.setTheme("ace/theme/" + themeName);
        }
    }-*/;

    /**
     * Set the mode.
     * 
     * @param mode the mode (one of the values in the {@link AceEditorMode} enumeration)
     */
    public void setMode(final AceEditorMode mode) {
        this.mode = mode;
        setModeByName(mode.getName());
    }

    /**
     * Set the mode by name.
     * 
     * @param shortModeName name of mode (e.g., "eclipse")
     */
    public native void setModeByName(String shortModeName) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			var modeName = "ace/mode/" + shortModeName;
			editor.getSession().setMode(modeName);
		}
    }-*/;

    /**
     * Register a handler for change events generated by the editor.
     * 
     * @param callback the change event handler
     */
    public native void addOnChangeHandler(AceEditorCallback callback) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		editor
				.getSession()
				.on(
						"change",
						function(e) {
							callback.@edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
						});
    }-*/;

    /**
     * Register a handler for cursor position change events generated by the editor.
     * 
     * @param callback the cursor position change event handler
     */
    public native void addOnCursorPositionChangeHandler(AceEditorCallback callback) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		editor.getSession().selection
				.on(
						"changeCursor",
						function(e) {
							callback.@edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
						});
    }-*/;

    /**
     * Set font size.
     */
    public native void setFontSize(final String fontSize) /*-{
		var elementId = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::elementId;
		var elt = $doc.getElementById(elementId);
		elt.style.fontSize = fontSize;
    }-*/;

    /**
     * Get the complete text in the editor as a String.
     * 
     * @return the text in the editor
     */
    private native String getTextNative() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null)
			return editor.getSession().getValue();
		return null;
    }-*/;

    public String getText() {
        final String thisText = getTextNative();
        return thisText == null ? this.text : thisText;
    };

    public void setText(final String text) {
        this.text = text;
        setTextNative(text);
    };

    /**
     * Set the complete text in the editor from a String.
     * 
     * @param text the text to set in the editor
     */
    private native void setTextNative(final String text) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.getSession().setValue(text);
		} else {
			console.log("editor == null. setTextNative() was not called successfully.");
		}
    }-*/;

    /**
     * Insert given text at the cursor.
     * 
     * @param text text to insert at the cursor
     */
    public native void insertAtCursor(final String text) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.insert(text);
		} else {
			console.log("editor == null. insertAtCursor() was not called successfully.");
		}
    }-*/;

    /**
     * Get the current cursor position.
     * 
     * @return the current cursor position
     */
    public native AceEditorCursorPosition getCursorPosition() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			var pos = editor.getCursorPosition();
			return this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::getCursorPositionImpl(DD)(pos.row, pos.column);
		} else {
			console.log("editor == null. getCursorPosition() was not called successfully.");
			return 0;
		}
    }-*/;

    private AceEditorCursorPosition getCursorPositionImpl(final double row, final double column) {
        return new AceEditorCursorPosition((int) row, (int) column);
    }

    /**
     * Set whether or not soft tabs should be used.
     * 
     * @param useSoftTabs true if soft tabs should be used, false otherwise
     */
    private native void setUseSoftTabsNative(final boolean useSoftTabs) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.getSession().setUseSoftTabs(useSoftTabs);
		} else {
			console.log("editor == null. setUseSoftTabsNative() was not called successfully.");
		}
    }-*/;

    public void setUseSoftTabs(final boolean useSoftTabs) {
        this.useSoftTabs = useSoftTabs;
        setUseSoftTabsNative(useSoftTabs);
    };

    /**
     * Set tab size. (Default is 4.)
     * 
     * @param tabSize the tab size to set
     */
    private native void setTabSizeNative(final int tabSize) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.getSession().setTabSize(tabSize);
		} else {
			console.log("editor == null. setTabSizeNative() was not called successfully.");
		}
    }-*/;

    public void setTabSize(final int tabSize) {
        this.tabSize = tabSize;
        setTabSizeNative(tabSize);
    }

    /**
     * Go to given line.
     * 
     * @param line the line to go to
     */
    public native void gotoLine(final int line) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.gotoLine(line);
		} else {
			console.log("editor == null. gotoLine() was not called successfully.");
		}
    }-*/;

    /**
     * Set whether or not the horizontal scrollbar is always visible.
     * 
     * @param hScrollBarAlwaysVisible true if the horizontal scrollbar is always visible, false if it is hidden when not needed
     */
    private native void setHScrollBarAlwaysVisibleNative(final boolean hScrollBarAlwaysVisible) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.renderer.setHScrollBarAlwaysVisible(hScrollBarAlwaysVisible);
		} else {
			console.log("editor == null. setHScrollBarAlwaysVisible() was not called successfully.");
		}
    }-*/;

    public void setHScrollBarAlwaysVisible(final boolean hScrollBarAlwaysVisible) {
        this.hScrollBarAlwaysVisible = hScrollBarAlwaysVisible;
        setHScrollBarAlwaysVisibleNative(hScrollBarAlwaysVisible);
    }

    /**
     * Set whether or not the gutter is shown.
     * 
     * @param showGutter true if the gutter should be shown, false if it should be hidden
     */
    private native void setShowGutterNative(final boolean showGutter) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.renderer.setShowGutter(showGutter);
		} else {
			console.log("editor == null. setShowGutterNative() was not called successfully.");
		}
    }-*/;

    public void setShowGutter(final boolean showGutter) {
        this.showGutter = showGutter;
        setShowGutterNative(showGutter);
    }

    /**
     * Set or unset read-only mode.
     * 
     * @param readOnly true if editor should be set to readonly, false if the editor should be set to read-write
     */
    private native void setReadOnlyNative(final boolean readOnly) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.setReadOnly(readOnly);
        }
    }-*/;

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        setReadOnlyNative(readOnly);
    }

    /**
     * Set or unset highlighting of currently selected word.
     * 
     * @param highlightSelectedWord true to highlight currently selected word, false otherwise
     */
    private native void setHighlightSelectedWordNative(final boolean highlightSelectedWord) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.setHighlightSelectedWord(highlightSelectedWord);
		} else {
			console.log("editor == null. setHighlightSelectedWordNative() was not called successfully.");
		}
    }-*/;

    public void setHighlightSelectedWord(final boolean highlightSelectedWord) {
        this.highlightSelectedWord = highlightSelectedWord;
        setHighlightSelectedWordNative(highlightSelectedWord);
    }

    /**
     * Set or unset the visibility of the print margin.
     * 
     * @param showPrintMargin true if the print margin should be shown, false otherwise
     */
    private native void setShowPrintMarginNative(final boolean showPrintMargin) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.renderer.setShowPrintMargin(showPrintMargin);
		} else {
			console.log("editor == null. setShowPrintMarginNative() was not called successfully.");
		}
    }-*/;

    public void setShowPrintMargin(final boolean showPrintMargin) {
        this.showPrintMargin = showPrintMargin;
        setShowPrintMarginNative(showPrintMargin);
    }

    /**
     * Set or unset the wrap mode.
     * 
     * @param userWrap true if the text should be wrapped, false otherwise
     */
    private native void setUseWrapModeNative(final boolean userWrap) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		
		if (editor != null) {		    
		    editor.getSession().setUseWrapMode(userWrap);
		} else {
			console.log("editor == null. setUserWrapModeNative() was not called successfully.");
		}
    }-*/;

    public void setUseWrapMode(final boolean userWrap) {
        this.useWrap = userWrap;
        setUseWrapModeNative(userWrap);
    }

    public boolean getUserWrapMode() {
        return this.useWrap;
    }

    public native int getScrollTop() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;

		if (editor != null) {
			editor.getSession().getScrollTop();
		} else {
			console.log("editor == null. getScrollTop() was not called successfully.");
            return 0;
		}
    }-*/;

    public native void setScrollTop(final int scroll) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;

		if (editor != null) {
			editor.getSession().setScrollTop(scroll);
		} else {
			console.log("editor == null. setScrollTop() was not called successfully.");
		}
	}-*/;

    private native void enableSnippets() /*-{

		if ($wnd.require == undefined) {
			$wnd.alert("window.require is undefined! Please make sure you have included the appropriate JavaScript files.");
			return;
		}

        var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;

		if (editor != null) {
            var snippetManager = $wnd.require("ace/snippets").snippetManager;

            if (snippetManager != null) {
                editor.commands.bindKey("Tab", function(editor) {
                    var success = snippetManager.expandWithTab(editor);
                    if (!success) {
                        editor.execCommand("indent");
                    }
                });
            }
        } else {
			console.log("editor == null. enableSnippets() was not called successfully.");
		}

    }-*/;

    /**
     * Clears all existing gutter decorations, and adds the new ones
     * @param lineNumbers An array containing the line numbers to add the style to
     * @param style The style to clear and then add
     */
    public native void clearAndAddGutterDecoration(final int[] lineNumbers, final String style) /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;

		if (editor == null) {
			console.log("editor == null. clearAndAddGutterDecoration() was not called successfully.");
			return;
		}

		if (lineNumbers == null) {
			console.log("lineNumbers == null. Gutter styles will not be modified.");
			return;
		}

		if (style == null) {
			console.log("style == null. Gutter styles will not be modified.");
			return;
		}

		var session = editor.getSession();

		var lines = session.getDocument().getAllLines();
		for (var i = 0, linesLength = lines.length; i < linesLength; ++i) {
			session.removeGutterDecoration(i, style);
		}

		for (var i = 0, lineNumbersLength = lineNumbers.length; i < lineNumbersLength; ++i) {
			session.addGutterDecoration(lineNumbers[i], style);
		}
    }-*/;

    /**
     * Initialize the context menu.
     */
    private native void setupContextMenu() /*-{

        var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
        var positiveDictionary = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::positiveDictionary;
        var tagDB = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagDB;

        if (editor == null) {
            console.log("editor == null. enableSpellCheckingEnabledNative() was not called successfully.");
            return;
        }

        var replaceWord = function(original, line, start, end, replacement) {
            var lines = original.split("\n");
            var output = "";
            for (var i = 0, _len = lines.length; i < _len; ++i) {
                if (i != line) {
                    output += lines[i] + (i == _len - 1 ? "" : "\n");
                } else {
                    output += lines[i].substring(0, start);
                    output += replacement;
                    output += lines[i].substring(end, lines[i].length) + (i == _len - 1 ? "" : "\n");
                }
            }

            return output;
        }

        // This stops two context menus from being displayed
        var processingSuggestions = false;

        $wnd.jQuery('#' + this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::elementId).contextMenu(function(cmenu,t,callback) {
            var retValue = [];
            var word = editor.getSession().getValue().split("\n")[this.wordData.line].substring(this.wordData.start, this.wordData.end);

            if (this.wordData.type == 'spelling') {
                    if (positiveDictionary != null) {

                    // Populate the context menu for the spelling options

                    processingSuggestions = true;

                    positiveDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::getDictionary()().suggest(word, 5, function(wordData) {
                        return function(suggestions) {
                            processingSuggestions = false;

                            if (suggestions.length == 0) {
                                var option = {};
                                option["No Suggestions"]=function(suggestion, wordData){};
                                retValue.push(option);
                            } else {
                                for (var i = 0, _len = suggestions.length; i < _len; i++) {
                                    var option = {};
                                    var suggestion = suggestions[i];
                                    option[suggestion] = function(suggestion, wordData){
                                        return function(menuItem,menu){
                                            var currentScroll = editor.getSession().getScrollTop();
                                            editor.getSession().setValue(
                                                replaceWord(
                                                    editor.getSession().getValue(),
                                                    wordData.line,
                                                    wordData.start,
                                                    wordData.end,
                                                    suggestion));
                                            editor.getSession().setScrollTop(currentScroll);
                                        };
                                    }(suggestion, wordData);

                                    retValue.push(option);
                                }
                            }

                            callback(retValue);

                        };
                    }(this.wordData));
                }
            } else if (this.wordData.type == 'tag') {
                if (tagDB != null) {
                    var database = tagDB.@edu.ycp.cs.dh.acegwt.client.tagdb.TagDB::getDatabase()();
                    var topicId =  database.@com.google.gwt.json.client.JSONObject::get(Ljava/lang/String;)(word);
                    if (topicId != null) {
                        var restServerCallback = tagDB.@edu.ycp.cs.dh.acegwt.client.tagdb.TagDB::getGetRESTServerCallback()();
                        var restServer = restServerCallback.@edu.ycp.cs.dh.acegwt.client.tagdb.GetRESTServerCallback::getBaseRESTURL()();

                        // get the topic XML
                        var getTopicRestUrl = restServer + "/1/topic/get/json/" + topicId;
                        $wnd.jQuery.ajax({
                            dataType: "json",
                            url: getTopicRestUrl,
                            success: function(topicData) {
                                // hold the XML
                                var holdXMLRestUrl = restServer + "/1/holdxml";
                                $wnd.jQuery.ajax({
                                    type: "POST",
                                    url: holdXMLRestUrl,
                                    data: "<?xml-stylesheet type='text/xsl' href='/pressgang-ccms-static/publican-docbook/html-single-renderonly.xsl'?>" + topicData.xml,
                                    dataType: 'application/xml',
                                    success: function(holdxmlData) {
                                        // echo the XML into an iframe
                                        var echoXMLRestUrl = restServer + "/1/echoxml?id=" + holdxmlData.value;
                                        retValue.push("<iframe style=\"width: 400px; height: 300px\" src=\"" + echoXMLRestUrl + "\"></iframe>")
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }, {theme:'human', beforeShow: function(event) {
            if (!processingSuggestions) {
                var retValue = false;

                this.wordData = {};

                $wnd.jQuery("div[class^='misspelled'], div[class^='badword']").each(
                    function(wordData){
                        return function(){
                            if ($wnd.jQuery(this).offset().left <= event.clientX &&
                                $wnd.jQuery(this).offset().left + $wnd.jQuery(this).width() >= event.clientX &&
                                $wnd.jQuery(this).offset().top <= event.clientY &&
                                $wnd.jQuery(this).offset().top + $wnd.jQuery(this).height() >= event.clientY) {

                                var classAttribute = $wnd.jQuery(this).attr('class');

                                if (classAttribute != null) {

                                    var matches = /(misspelled|badword)-(\d+)-(\d+)-(\d+)/.exec(classAttribute);
                                    if (matches != null && matches.length >= 5) {

                                        retValue = true;

                                        wordData['type'] = 'spelling';
                                        wordData['line'] = matches[2];
                                        wordData['start'] = matches[3];
                                        wordData['end'] = matches[4];
                                    }
                                }

                            }
                        };
                    }(this.wordData));

                return retValue;
            } else {
                return false;
            }
        }});
    }-*/;

    /**
     * Enable spell checking.
     * This requires that the project that is including this artifact include typo.js and jquery in the main HTML file,
     * as well as exposing the dictionaries in the locations identified by dicPath and affPath.
     */
    private native void enableSpellCheckingEnabledNative() /*-{

        try {
            console.log("ENTER AceEditor.enableSpellCheckingEnabledNative()");

            var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
            var spellcheckInterval = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellcheckInterval;
            var positiveDictionary = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::positiveDictionary;
			var negativeDictionary = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::negativeDictionary;
			var negativePhraseDictionary = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::negativePhraseDictionary;

            if (editor == null) {
                console.log("editor == null. enableSpellCheckingEnabledNative() was not called successfully.");
                return;
            }

            if (positiveDictionary == null) {
                console.log("positiveDictionary == null. Spell checking will not be enabled.");
                return;
            }

            // Add the CSS rules to highlight spelling errors
            //$wnd.jQuery("<style type='text/css'>.ace_marker-layer div[class^='misspelled'] { position: absolute; z-index: -2; background-color: rgba(255, 0, 0, 0.2); }</style>").appendTo("head");
            //$wnd.jQuery("<style type='text/css'>div[class^='misspelled'] { background-color: rgba(255, 0, 0, 0.2); }</style>").appendTo("head");
			//$wnd.jQuery("<style type='text/css'>.ace_marker-layer div[class^='badword'] { position: absolute; z-index: -2; background-color: rgba(245, 255, 0, 0.2); }</style>").appendTo("head");
			//$wnd.jQuery("<style type='text/css'>div[class^='badword'] { background-color: rgba(245, 255, 0, 0.2); }</style>").appendTo("head");

            var contentsModified = true;
			var currentlySpellchecking = false;
			var markersPresent = [];

            // Check for changes to the text
            editor.getSession().on('change', function(e) {
                contentsModified = true;
            });

            // Setup a worker to perform the spell checking, and handle the results
            this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellCheckingWorker = new Worker("javascript/typojs/checkspelling.js");
            var spellingWorker = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellCheckingWorker;
			// Set the dictionaries.
            spellingWorker.postMessage({
				positiveDictionary: positiveDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::getDictionary()(),
				negativeDictionary: negativeDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::getDictionary()(),
				negativePhraseDictionary: negativePhraseDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::getDictionary()()});
            spellingWorker.addEventListener('message', function(e){
				try {
					if (editor == null) {
                        return;
                    }

                    var lineData = e.data;

					var session = editor.getSession();

					// Clear the markers.
					for (var i in markersPresent) {
						session.removeMarker(markersPresent[i]);
					}
					markersPresent = [];

					var Range = $wnd.ace.require('ace/range').Range;

                    for (var lineDataIndex = 0, lineDataLength = lineData.length; lineDataIndex < lineDataLength; ++lineDataIndex) {
                        var misspellings = lineData[lineDataIndex];

                        for (var j in misspellings.misspelled) {
                            var range = new Range(lineDataIndex, misspellings.misspelled[j][0], lineDataIndex, misspellings.misspelled[j][1]);

                            // Add the information required to identify the misspelled word to the class itself. This
                            // gives us a way to go back from a click event to a word.
                            markersPresent[markersPresent.length] = session.addMarker(
                                range,
                                "misspelled-" + lineDataIndex + "-" + misspellings.misspelled[j][0] + "-" + misspellings.misspelled[j][1],
                                "typo",
                                true);
                        }

                        for (var j in misspellings.badWords) {
                            var range = new Range(lineDataIndex, misspellings.badWords[j][0], lineDataIndex, misspellings.badWords[j][1]);
                            markersPresent[markersPresent.length] = session.addMarker(
                                range,
                                "badword-" + lineDataIndex + "-" + misspellings.badWords[j][0] + "-" + misspellings.badWords[j][1],
                                "typo",
                                true);
                        }

                        for (var j in misspellings.badPhrases) {
                            var range = new Range(lineDataIndex, misspellings.badPhrases[j][0], lineDataIndex, misspellings.badPhrases[j][1]);
                            markersPresent[markersPresent.length] = session.addMarker(
                                range,
                                "badphrase-" + lineDataIndex + "-" + misspellings.badPhrases[j][0] + "-" + misspellings.badPhrases[j][1],
                                "typo",
                                true);
                        }

					}
				} finally {
					currentlySpellchecking = false;
				}
            });

            var spellCheck = function() {
                // Wait for the dictionary to be loaded.
                var loaded = positiveDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::isLoaded()() &&
                    negativeDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::isLoaded()() &&
                    negativePhraseDictionary.@edu.ycp.cs.dh.acegwt.client.typo.TypoJS::isLoaded()();

                if (!loaded) {
                    console.log("Waiting for dictionary to load.");
                    return;
                }

                if (currentlySpellchecking) {
                    return;
                }

                if (!contentsModified) {
                    return;
                }

                console.log("Checking Spelling");

				currentlySpellchecking = true;
				contentsModified = false;

				spellingWorker.postMessage({lines: editor.getSession().getDocument().getAllLines()});
            }

            // Enable spell checking on regular intervals
            if (spellcheckInterval != null) {
                clearInterval(spellcheckInterval);
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellcheckInterval = null;
            }

            this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::spellcheckInterval = setInterval(spellCheck, 500);
            spellCheck();

        } finally {
            console.log("EXIT AceEditor.enableSpellCheckingEnabledNative()");
        }

    }-*/;

    private native void enableTagMatching() /*-{
        var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
        var tagDB = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagDB;

        if (tagDB != null) {
            var currentlyMatchingTags = false;
            var markersPresent = [];
            var contentsModified = true;

            // Check for changes to the text
            editor.getSession().on('change', function(e) {
                contentsModified = true;
            });

            // Build the web worker to match tags

            this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagMatchingWorker = new Worker("javascript/tagdb/tagdb.js");
            var tagMatchingWorker = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::tagMatchingWorker;
            // Set the tag db
            tagMatchingWorker.postMessage({tagDB: tagDB.@edu.ycp.cs.dh.acegwt.client.tagdb.TagDB::getJSONDatabase()()});

            tagMatchingWorker.addEventListener('message', function(e){
                try {
                    if (editor == null) {
                        return;
                    }

                    var lineData = e.data;

                    var session = editor.getSession();

                    // Clear the markers.
                    for (var i in markersPresent) {
                        session.removeMarker(markersPresent[i]);
                    }
                    markersPresent = [];

                    var Range = $wnd.ace.require('ace/range').Range;

                    for (var lineDataIndex = 0, lineDataLength = lineData.length; lineDataIndex < lineDataLength; ++lineDataIndex) {
                        var tagMatches = lineData[lineDataIndex];

                        for (var j in tagMatches) {
                            var range = new Range(lineDataIndex, tagMatches[j][0], lineDataIndex, tagMatches[j][1]);
                            markersPresent[markersPresent.length] = session.addMarker(
                                range,
                                "tagmatch-" + lineDataIndex + "-" + tagMatches[j][0] + "-" + tagMatches[j][1],
                                "tagmatch",
                                true);
                        }

                    }
                } finally {
                    currentlyMatchingTags = false;
                }
            });


            var matchTags = function() {
                if (!tagDB.@edu.ycp.cs.dh.acegwt.client.tagdb.TagDB::isLoaded()()) {
                    console.log("Waiting for tag database to load.");
                    return;
                }

                if (currentlyMatchingTags) {
                    return;
                }

                if (!contentsModified) {
                    return;
                }

                console.log("Matching Tags");

                currentlyMatchingTags = true;
                contentsModified = false;

                tagMatchingWorker.postMessage({lines: editor.getSession().getDocument().getAllLines()});
            };

            // Enable spell checking on regular intervals
            if (this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval != null) {
                clearInterval(this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval);
                this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval = null;
            }

            this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::matchTagsInterval = setInterval(matchTags, 500);
            matchTags();
        }
    }-*/;

    /**
     * Add an annotation to a the local <code>annotations</code> JsArray<AceAnnotation>, but does not set it on the editor
     * 
     * @param row to which the annotation should be added
     * @param column to which the annotation applies
     * @param text to display as a tooltip with the annotation
     * @param type to be displayed (one of the values in the {@link AceAnnotationType} enumeration)
     */
    public void addAnnotation(final int row, final int column, final String text, final AceAnnotationType type) {
        annotations.push(AceAnnotation.create(row, column, text, type.getName()));
    }

    /**
     * Set any annotations which have been added via <code>addAnnotation</code> on the editor
     */
    public native void setAnnotations() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		var annotations = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::annotations;
		if (editor != null) {
			editor.getSession().setAnnotations(annotations);
		} else {
			console.log("editor == null. setAnnotations() was not called successfully.");
		}
    }-*/;

    /**
     * Clear any annotations from the editor and reset the local <code>annotations</code> JsArray<AceAnnotation>
     */
    public native void clearAnnotations() /*-{
		var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
		if (editor != null) {
			editor.getSession().clearAnnotations();
		} else {
			console.log("editor == null. clearAnnotations() was not called successfully.");
		}

		this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::resetAnnotations();
    }-*/;

    /**
     * Reset any annotations in the local <code>annotations</code> JsArray<AceAnnotation>
     */
    private void resetAnnotations() {
        annotations = JavaScriptObject.createArray().cast();
    }

    /**
     * Remove a command from the editor.
     * 
     * @param command the command (one of the values in the {@link AceCommand} enumeration)
     */
    public void removeCommand(final AceCommand command) {
        removeCommandByName(command.getName());
    }

    /**
     * Remove commands, that may not me required, from the editor
     * 
     * @param command to be removed, one of "gotoline", "findnext", "findprevious", "find", "replace", "replaceall"
     */
    public native void removeCommandByName(final String command) /*-{
    var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
    if (editor != null)
    {
	    editor.commands.removeCommand(command);
    } else
    {
	    console.log("editor == null. removeCommandByName() was not called successfully.");
    }
	}-*/;

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.ResizeComposite#onResize()
     */
    @Override
    public void onResize() {
        redisplay();
    }

    @Override
    public LeafValueEditor<String> asEditor() {
        return new LeafValueEditor<String>() {
            @Override
            public void setValue(final String value) {
                setText(value);
            }

            @Override
            public String getValue() {
                return getText();
            }
        };
    }
    
    /**
     * Wraps the currently selected text with the start and end strings
     * @param start The string to place before the selection
     * @param end The string to place after the selection
     */
    public native void wrapSelection(final String start, final String end)/*-{
        var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
        if (editor != null && start != null && end != null)
        {
            var range = editor.getSelectionRange();
            var selectedText = editor.getSession().getTextRange(range);
            editor.getSession().getDocument().replace(range, start + selectedText + end);
            
            if (selectedText == "") {
                editor.getSelection().moveCursorBy(0, -end.length);
            }
        } else {
            console.log("editor == null. wrapSelection() was not called successfully.");
        }
    }-*/;
    
    /**
     * Insert text at the current cursor location
     * @param text the text to insert
     */
    public native void insertText(final String text)/*-{
    var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
        if (editor != null) {
            editor.insert(text);
        } else {
            console.log("editor == null. insertText() was not called successfully.");
        }
    }-*/;
}
