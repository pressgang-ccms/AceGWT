// Copyright (c) 2011 David H. Hovemeyer <david.hovemeyer@gmail.com>
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

/**
 * Enumeration for ACE editor modes.
 * Note that the corresponding .js file must be loaded
 * before a mode can be set.
 */
public enum AceEditorMode {
	/** c9search */
	C9SEARCH("c9search"),
	/** C/C++. */
	C_CPP("c_cpp"),
	/** Clojure. */
	CLOJURE("clojure"),
	/** Coffee. */
	COFFEE("coffee"),
	/** ColdFusion. */
	COLDFUSION("coldfusion"),
	/** C#. */
	CSHARP("csharp"),
    /** CSP. */
    CSP("csp"),
	/** CSS. */
	CSS("css"),
	/** Diff. */
	DIFF("diff"),
    /** DOCBOOK 45. */
    DOCBOOK_45("docbook45"),
    /** DOCBOOK 50. */
    DOCBOOK_50("docbook50"),
	/** Go (http://golang.org/). */
	GOLANG("golang"),
	/** Groovy. */
	GROOVY("groovy"),
	/** Haxe. */
	HAXE("haxe"),
	/** HTML. */
	HTML("html"),
	/** JAVA. */
	JAVA("java"),
	/** Javascript. */
	JAVASCRIPT("javascript"),
	/** JSON. */
	JSON("json"),
	/** JSX. */
	JSX("jsx"),
	/** LaTeX. */
	LATEX("latex"),
	/** Less. */
	LESS("less"),
	/** Liquid. */
	LIQUID("liquid"),
	/** Lua. */
	LUA("lua"),
	/** Luapage. */
	LUAPAGE("luapage"),
	/** Markdown. */
	MARKDOWN("markdown"),
	/** OCaml. */
	OCAML("ocaml"),
	/** Perl. */
	PERL("perl"),
	/** PgSQL. */
	PGSQL("pgsql"),
	/** PHP. */
	PHP("php"),
	/** PowerShell. */
	POWERSHELL("powershell"),
	/** Python. */
	PYTHON("python"),
	/** Ruby. */
	RUBY("ruby"),
	/** Scad. */
	SCAD("scad"),
	/** Scala. */
	SCALA("scala"),
	/** SCSS. */
	SCSS("scss"),
	/** Sh (Bourne shell). */
	SH("sh"),
	/** SQL. */
	SQL("sql"),
	/** SVG. */
	SVG("svg"),
	/** Textile. */
	TEXTILE("textile"),
	/** Text. */
	TEXT("text"),
	/** XML. */
	XML("xml"),
	/** XQuery. */
	XQUERY("xquery"),
	/** YAML. */
	YAML("yaml");
	
	private final String name;
	
	private AceEditorMode(String name) {
		this.name = name;
	}
	
	/**
	 * @return mode name (e.g., "java" for Java mode)
	 */
	public String getName() {
		return name;
	}
}
