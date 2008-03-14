package org.wonderly.doclets;

import com.sun.javadoc.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;

/**
 *  This class provides a Java 2, <code>javadoc</code> Doclet which generates
 *  a LaTeX2e document out of the java classes that it is used on.  This is
 *  convienent for creating printable documentation complete with cross reference
 *  information.
 *  <p>
 *  Supported HTML tags within comments include the following
 *  <dl>
 *  <dt>&lt;dl&gt;
 *  <dd>with the associated &lt;dt&gt;&lt;dd&gt;&lt;/dl&gt; tags
 *  <dt>&lt;p&gt;
 *  <dd>but not align=center...yet
 *  <dt>&lt;br&gt;
 *  <dd>but not clear=xxx
 *  <dt>&lt;table&gt;
 *  <dd>including all the associcated &lt;td&gt;&lt;th&gt;&lt;tr&gt;&lt;/td&gt;&lt;/th&gt;&lt;/tr&gt;
 *  <dt>&lt;ol&gt;
 *  <dd>ordered lists
 *  <dt>&lt;ul&gt;
 *  <dd>unordered lists
 *  <dt>&lt;font&gt;
 *  <dd>font coloring
 *  <dt>&lt;pre&gt;
 *  <dd>preformatted text
 *  <dt>&lt;code&gt;
 *  <dd>fixed point fonts
 *  <dt>&lt;i&gt;
 *  <dd>italized fonts
 *  <dt>&lt;b&gt;
 *  <dd>bold fonts
 *	</dl>
 *
 * {@link #TexDoclet TexDoclet}
 * {@link #start(RootDoc) start}
 *  @version 1.1
 *  @author <a href="mailto:gregg.wonderly@pobox.com">Gregg Wonderly</a>
 */
public class TexDoclet extends Doclet {
	/** Writer for writing to output file */
	public static PrintWriter os = null;
	static Stack tblstk;
	static boolean inherited = true;
	static int colIdx = 0;
	static int verbat = 0;
	static Hashtable colors = new Hashtable(10);
	static Stack itemcnts;
	static int itemcnt;
	static TableInfo tblinfo;
	static Hashtable map;
	static String title;
	static String outfile = "docs.tex";
	static String docclass = "report";
	static String style = "myheadings";
	static String date;
	static String packageDoc = "package.html";
	static String author;
	static String setupFile = "docsetup.tex";
	static String finishFile = "docfinish.tex";
	static String packageFile = "docpackage.tex";
	static String initFile = "docinit.tex";
	static boolean verbose = false;
	static String packageDir = null;
	static ClassFilter clsFilt;
	static String SHORT_PACKAGE = "org.cishell";
	
	/**
	 *  Testing entry point for testing tables
	public static void main( String args[] ) {
		init();
		os.println( "\\documentstyle{book}" );
		os.println( "\\begin{document}");
		os.println( fixText(
			"<table border> "+
			"<tr><th>Heading 1<th>Heading 2<th>Heading 3"+
			"<tr><td>Column 1<td colspan=2 align=right>2 columns here"+
			"<tr><td colspan=2>two here as well<td>just 1"+
			"<tr><td align=left>1<td>2<td align=right>3"+
			"</table>" ) );
		os.println( "\\end{document}");
	}
	*/
	
	static void init() {
		map = new Hashtable();
		itemcnts = new Stack();
		tblstk = new Stack();
		tblinfo = new TableInfo( null, null, "", 0 );
		try {
			os = new PrintWriter( new FileWriter( outfile ) );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	/**
	 *  Called by the framework to format the entire document
	 *
	 *  @param root the root of the starting document
	 */
	public static boolean start(RootDoc root) {
		System.out.println("TexDoclet v3.0, Copyright 2003 - Gregg Wonderly.");
		System.out.println("http://texdoclet.dev.java.net - on the World Wide Web.");
		init();
		if( os == null ) {
			System.err.println( "Can not create output file, processing aborted");
			System.exit(1);
		}
		
//		os.println("\\documentclass[11pt]{"+docclass+"}" );
		
		//os.println("\\newcommand{\\packagesheader}{");
		os.println("%%% Start_Header");
		os.println("\\def\\bl{\\mbox{}\\newline\\mbox{}\\newline{}}");
		os.println("\\usepackage{ifthen}");
		
		os.println("\\newcommand{\\hide}[2]{");
		os.println("\\ifthenelse{\\equal{#1}{inherited}}%");
		// if inherited
		os.println("{}%");
		// if not inherited
		os.println("{}%");
		os.println("}");
		
		os.println("\\newcommand{\\entityintro}[3]{%");
		os.println("  \\hbox to \\hsize{%");
		os.println("    \\vbox{%");
		os.println("      \\hbox to .2in{}%");
		os.println("    }%");
		os.println("    {\\bf #1}%");
		os.println("    \\dotfill\\pageref{#2}%");
		os.println("  }" );
		os.println("  \\makebox[\\hsize]{%");
		os.println("    \\parbox{.4in}{}%");
		os.println("    \\parbox[l]{5in}{%");
		os.println("      \\vspace{1mm}\\it%");
		os.println("      #3%");
		os.println("      \\vspace{1mm}%");
		os.println("    }%");
		os.println("  }%");
		os.println("}");
		
		os.println("\\newcommand{\\isep}[0]{%");
		os.println("\\setlength{\\itemsep}{-.4ex}");
		os.println("}");
		
		os.println("\\newcommand{\\sld}[0]{%");
		os.println("\\setlength{\\topsep}{0em}");
		os.println("\\setlength{\\partopsep}{0em}");
		os.println("\\setlength{\\parskip}{0em}");
		os.println("\\setlength{\\parsep}{-1em}");
		os.println("}");
		
		os.println("\\newcommand{\\headref}[3]{%");
		os.println("\\ifthenelse{#1 = 1}{%");
		os.println("\\addcontentsline{toc}{section}{\\hspace{\\qquad}\\protect\\numberline{}{#3}}%");
		os.println("}{}%");
		os.println("\\ifthenelse{#1 = 2}{%");
		os.println("\\addcontentsline{toc}{subsection}{\\hspace{\\qquad}\\protect\\numerline{}{#3}}%");
		os.println("}{}%");
		os.println("\\ifthenelse{#1 = 3}{%");
		os.println("\\addcontentsline{toc}{subsubsection}{\\hspace{\\qquad}\\protect\\numerline{}{#3}}%");
		os.println("}{}%");
		os.println("\\label{#3}%");
		os.println("\\makebox[\\textwidth][l]{#2 #3}%");
		os.println("}%");
		
		os.println("\\newcommand{\\membername}[1]{{\\it #1}\\linebreak}");
		os.println("\\newcommand{\\divideents}[1]{\\vskip -1em\\indent\\rule{2in}{.5mm}}" );
		
		os.println("\\newcommand{\\refdefined}[1]{%" );
		os.println("\\expandafter\\ifx\\csname r@#1\\endcsname\\relax%");
	    os.println("\\relax\\else%");
		os.println("{$($ in \\ref{#1}, page \\pageref{#1}$)$}%");
		os.println("\\fi}%" );

		os.println("\\newcommand{\\startsection}[4]{" );
		os.println( "\\gdef\\classname{#2}" );
		os.print("\\subsection*{" );
		os.print("\\label{#3}");
		os.println( "{\\bf {\\sc #1} #2}}{");
		os.println( "\\rule[1em]{\\hsize}{4pt}\\vskip -1em" );
		os.println( "\\vskip .1in ");
		os.println( "#4" );
		os.println( "}%\n}");
		
		os.println("\\newcommand{\\startsubsubsection}[2]{" );
		os.println("\\subsubsection*{\\sc #1}{%");
		os.println("\\rule[1em]{\\hsize}{2pt}%");
		os.println("#2}");
		os.println( "}");
		
		os.println("\\usepackage{color}");
		
		os.println("%%% End_Header");
		//os.println("}\n\n");
//		os.println("\\packagesheader{}");
		
/*		
        if( date == null )
            os.println("\\date{\\today}");
        else
            os.println("\\date{"+date+"}");
            
		os.println("\\pagestyle{"+style+"}");
		os.println("\\addtocontents{toc}{\\protect\\def\\protect\\packagename{}}");
		os.println("\\addtocontents{toc}{\\protect\\def\\protect\\classname{}}");
		os.println( "\\markboth{\\protect\\packagename -- \\protect\\classname}{\\protect\\packagename -- \\protect\\classname}" );
		os.println("\\oddsidemargin 0in");
		os.println("\\evensidemargin 0in");
		os.println("% \\topmargin -.8in");		
		os.println("\\chardef\\bslash=`\\\\");		
		os.println("\\textheight 9.4in");
		os.println("\\textwidth 6.5in");
		if( title != null )
			os.println("\\title{"+title+"}");
		if( author != null )
			os.println("\\author{"+author+"}");
*/
		
		addFile( initFile, "...Adding Document Initialization", false );

/*		
		os.println("\\begin{document}" );
		if( title != null )
			os.println("\\maketitle");
		os.println("\\sloppy");
		os.println("\\raggedright");
		os.println("\\tableofcontents");
*/
		ClassDoc[] cls = root.classes();
		
		addFile( setupFile, "...Adding Document Preface", false );

		ExecutableMemberDoc[]mems;

		if( clsFilt != null ) 
			System.out.println( "...Filtering Classes with: "+clsFilt );
		for (int i = 0; i < cls.length; ++i) {
			ClassDoc cd = cls[i];

			if( clsFilt != null && clsFilt.includeClass( cd ) == false ) {
				System.out.println( "...Filtering out Class: "+cd.qualifiedName() );
				continue;
			}

			Package v;
			String pkg = cd.containingPackage().name();
			if( (v = (Package)map.get(pkg)) == null ) {
				v = new Package(pkg);
				map.put( pkg, v );
			}
			v.addElement( cd );
		}

		
//		os.println( "\\gdef\\packagename{}" );
//		os.println( "\\gdef\\classname{}" );
		Enumeration e = map.elements();
		while( e.hasMoreElements() ) {
		
			Package pkg = (Package)e.nextElement();
			String cmd = pkg.pkg.replaceAll("\\.","");
			
			os.println( "\n\n\\newcommand{\\"+cmd+"}{" );
			os.println( "%%% Start: " + pkg.pkg );
/*
			System.out.println( "* Package: "+pkg.pkg );
			os.println( "\\newpage" );
			os.println( "\\def\\packagename{"+fixText(pkg.pkg)+"}" );
			addFile( packageFile, "...Adding Package Setup", true );
			os.println( "\\chapter{\\bf Package "+fixText(pkg.pkg)+"}{" );
			os.println( "\\vskip -.25in");
			os.println( "\\hbox to \\hsize{\\it Package Contents\\hfil Page}");
			os.println( "\\rule{\\hsize}{.7mm}");
			tocForClasses( "Interfaces", pkg.interfaces );
			tocForClasses( "Classes", pkg.classes );
			os.println( "\\vskip .1in" );
			os.println( "\\rule{\\hsize}{.7mm}");
			os.println( "\\vskip .1in" );
			if( findPackageDoc( packageDoc, pkg.pkg ) ) {
				os.println( "\\rule{\\hsize}{.7mm}");
				os.println( "\\vskip .1in" );
			}
			os.println("\\newpage");
*/

			System.out.println("Interfaces...");
			layoutClasses( pkg.pkg, pkg.interfaces );

			System.out.println("Classes...");
			layoutClasses( "Classes", pkg.classes );

			System.out.println("Exceptions...");
			layoutClasses( "Exceptions", pkg.exceptions );

			System.out.println("Errors...");
			layoutClasses( "Error", pkg.errors );
			os.println( "%%% End: " + pkg.pkg );
			
			os.println( "}" );
//			os.println( "\\"+cmd+"{}");
			
			//			os.println("}");
		}
		
		addFile( finishFile, "...Adding Document Post Script", false );

//		os.println("\\end{document}" );

		if( os != null ) {
			try { os.close(); } catch( Exception ex ) {}
		}

		return true;
	}
	
	static boolean findPackageDoc( String file, String pkg ) {
		File f = null;
		if( packageDir != null ) {
			f = new File( packageDir );
			if( f.exists() == false ) {
				if( verbose )
					System.out.println( "base packageDoc dir not found: "+packageDir );
				return false;
			}
		}
		if( pkg != null && pkg.equals("") == false ) {
			StringTokenizer st = new StringTokenizer(pkg, ".");
			while( st.hasMoreTokens() ) {
				if( f == null )
					f = new File( st.nextToken() );
				else
					f = new File( f, st.nextToken() );
			}
		}
		if( f == null )
			f = new File( file );
		else
			f = new File( f, file );

		if( verbose )
			System.out.print( "Check for package document at: "+f );
		
		if( f.exists() == false ) {
			if( verbose )
				System.out.println( "...none" );
			return false;
		}

		System.out.println( "...processing" );
		
		return addFile( f.toString(), "...Adding Package Docs for "+pkg, true );
	}
	
	static boolean addFile( String name, String annc, boolean fixText ) {
		try {
			File f = new File(name);
			if( f.exists() == false ) {
				if(verbose)
					System.out.println("WARNING: Can not include missing file: "+f.getAbsolutePath() );
				return false;
			}
			BufferedReader rd = new BufferedReader( new FileReader( f ) );
			try {
				if( annc != null )
					System.out.println( annc+": "+f.getAbsolutePath() );
				StringBuffer buf = new StringBuffer((int)f.length());
				String str;
				while( (str = rd.readLine()) != null ) {
					buf.append(str+"\n");
				}
				if( fixText ) {
					os.println( fixText( buf.toString() ) );
				} else {
					os.println( buf.toString() );
				}
			} finally {
				rd.close();
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *  Returns how many arguments would be consumed if <code>option</code>
	 *  is a recognized option.
	 *
	 *  @param option the option to check
	 */
	public static int optionLength(String option) {
		if( option.equals("-title") )
			return 2;
		else if( option.equals("-date") )
			return 2;
		else if( option.equals("-docclass") )
			return 2;
		else if( option.equals("-doctype") )
			return 2;
		else if( option.equals("-author") )
			return 2;
		else if( option.equals("-setup") )
			return 2;
		else if( option.equals("-initialize") )
			return 2;
		else if( option.equals("-finish") )
			return 2;
		else if( option.equals("-texpackage") )
			return 2;
		else if( option.equals("-packagefile") )
			return 2;
		else if( option.equals("-docdir") )
			return 2;
		else if( option.equals("-classfilter") )
			return 2;
		else if( option.equals("-noinherited") )
			return 1;
		else if( option.equals("-help") ) {
			System.err.println( "TexDoclet Usage:");
			System.err.println( "-title <title>        A title to use for the generated output document." );
			System.err.println( "                      No -title will result in no title page." );
			System.err.println( "-output <outfile>     Specifies the output file to write to.  If none");
			System.err.println( "                      specified, the default is docs.tex in the current");
			System.err.println( "                      directory." );
			System.err.println( "-docclass <class>     LaTeX2e document class, `report' is the default." );
			System.err.println( "-doctype <type>       LaTeX2e document style, `myheadings' is the default." );
			System.err.println( "-docdir <dirname>     Directory tree where package documentation exists." );
			System.err.println( "-packagefile <name>   The name of the HTML file that has the packages' documentation" );
			System.err.println( "-classfilter <name>   The name of a class implementing the ClassFilter interface.");
			System.err.println( "                      This class is used to indicate classes to be excluded.");
			System.err.println( "                      Typically there are classes in a package that are non-public,");
			System.err.println( "                      and this mechanism can be used to exclude them and only");
			System.err.println( "                      include the public classes.");
			System.err.println( "-date <date string>   The value to use for the document date.");
			System.err.println( "-author <author>      Specifies string to use for document Author." );
			System.err.println( "-texinit <file>       LaTeX2e statements included before \\begin{document}." );
			System.err.println( "-texsetup <file>      LaTeX2e statements included after \\begin{document}." );
			System.err.println( "-texfinish <file>     LaTeX2e statements included before \\end{document}." );
			System.err.println( "-texpackage <file>    LaTeX2e statements included before packages' \\chapter." );
			System.err.println( "-noinherited          Do not include inherited API information in output." );

			return 1;
		} else if( option.equals("-output") )
			return 2;
		System.out.println( "unknown option "+option);
		return Doclet.optionLength(option);
	}
	
	/**
	 *  Checks the passed options and their arguments for validity.
	 *
	 *  @param args the arguments to check
	 *  @param err the interface to use for reporting errors
	 */
	static public boolean validOptions( String[][] args, DocErrorReporter err ) {
		for( int i = 0; i < args.length; ++i ) {
			if( args[i][0].equals( "-output" ) ) {
				outfile = args[i][1];
			} else if( args[i][0].equals( "-date" ) ) {
				date = args[i][1];
			} else if( args[i][0].equals( "-title" ) ) {
				title = args[i][1];
			} else if( args[i][0].equals( "-author" ) ) {
				author = args[i][1];
			} else if( args[i][0].equals( "-verbose" ) ) {
				verbose = true;
			} else if( args[i][0].equals( "-docclass" ) ) {
				docclass = args[i][1];
			} else if( args[i][0].equals( "-classfilter" ) ) {
				String fcl = args[i][1];
				try {
					clsFilt = (ClassFilter)Class.forName( fcl ).newInstance();
				} catch( Exception ex ) {
					ex.printStackTrace();
					System.exit(2);
				}
			} else if( args[i][0].equals( "-docdir" ) ) {
				packageDir = args[i][1];
			} else if( args[i][0].equals( "-doctype" ) ) {
				style = args[i][1];
			} else if( args[i][0].equals( "-texsetup" ) ) {
				setupFile = args[i][1];
			} else if( args[i][0].equals( "-texinit" ) ) {
				initFile = args[i][1];
			} else if( args[i][0].equals( "-texfinish" ) ) {
				finishFile = args[i][1];
			} else if( args[i][0].equals( "-texpackage" ) ) {
				packageFile = args[i][1];
			} else if( args[i][0].equals( "-noinherited" ) ) {
				inherited = false;
			} else if( args[i][0].equals( "-packagefile" ) ) {
				packageDoc = args[i][1];
			}
		}
		return true;
	}

	static void tocForClasses( String title, Vector v ) {
		if( v.size() > 0 ) {
			os.println("\\vskip .13in" );
			os.println("\\hbox{\\bf "+fixText(title)+"}" );
			for( int i = 0; i < v.size(); ++i ) {
				ClassDoc cd = (ClassDoc)v.elementAt(i);
				os.print("\\entityintro{"+fixText(cd.name())+"}"+
						"{"+refName(makeRefKey(cd.qualifiedName()))+"}"+
						"{" );
				String text = cd.commentText();
				// Try to find the end of a sentence.
				String introText = "...no description...";
				if( text != null && text.length() > 2 ) {
					String txt = "";
					boolean done = false;
					for( int j = 0; !done && j < text.length(); ++j ) {
						txt += text.charAt(j);
						switch( text.charAt(j) ) {
							case '?':
							case '!':
							case '.':
								if( text.length() > j+1 ) {
									if( " \t\n\r".indexOf(text.charAt(j+1)) == -1 )
										continue;
								}
								done = true;
								break;
							default:
								break;
						}
					}
					if( txt.length() > 0 ) {
						if( verbose ) {
							System.out.println( "  * "+cd.name()+": "+
								txt.replace('\n',' ').replace('\r',' '));
						}
						introText = fixText(txt);
						introText = correctText( introText );
					}
				}
				os.println( introText+"}" );
			}
		}
	}
	
	static String correctText( String str ) {
		int cnt = 0;
		byte []arr = str.getBytes();
		for( int i = 0; i < arr.length; ++i ) {
			if( arr[i] == '{' )
				++cnt;
			else if( arr[i] == '}' )
				--cnt;
		}
		while( cnt-- > 0 ) {
			str = str + "}";
		}
		return str;
	}
	
	static int labno = 0;
	static Hashtable refs = new Hashtable();

	static String refName( String key ) {
		String lab;
		if( (lab = (String)refs.get(key)) == null ) {
			lab = "l"+labno++;
			refs.put( key, lab );
		}
		return lab;
	}			
	
	static void layoutClasses( String type, Vector classes ) {
		if( classes.size() > 0 && !("Classes".equals(type))) {
		    os.print("\\section{" );
			os.print( type );
			os.println( "}{" );
		}
		for (int i = 0; i < classes.size(); ++i) {
			ClassDoc cd = (ClassDoc)classes.elementAt(i);
System.out.print("    "+cd.name() );
//			os.println( "\\startsection{"+mtype+"}{"+fixText(cd.name())+"}{" );
			String mtype = "Class";
			if( type.equals("Classes") == false )
				mtype = "Interface";
			os.println( "\\startsection{"+mtype+"}{"+fixText(cd.name())+"}{"+refName(makeRefKey(cd.qualifiedName()))+"}{%" );
			String cmt = cd.commentText();
			if( cmt.equals("") == false ) {
				os.println( "{\\small "+fixText(cmt)+"}");
				os.println( "\\vskip .1in ");
			}
			os.println( "\\startsubsubsection{Declaration}{" );
			os.println( "\\fbox{\\vbox{");
			os.println( "\\hbox{\\vbox{\\small "+fixText(cd.modifiers())+" " );
			if( cd.isInterface() == false )
				os.println( "class ");
			os.println( fixText(cd.name())+"}}" );
			ClassDoc sc = cd.superclass();
			if( sc != null ) {
				os.println( "\\noindent\\hbox{\\vbox{{\\bf extends} "+
					fixText(sc.qualifiedName())+"}}");
			}
System.out.print("..interfaces.."); System.out.flush();
			ClassDoc intf[] = cd.interfaces();
			if( intf.length > 0 ) {
				os.println( "\\noindent\\hbox{\\vbox{{\\bf implements} " );
				for( int j = 0; j < intf.length; ++j ) {
					ClassDoc in = intf[j];
					String nm;
					if( in.containingPackage().name().equals(
										cd.containingPackage().name()) ) {
						nm = in.name();
					} else
						nm = in.qualifiedName();
					if( j > 0 )
						os.print( ", ");
					os.print( fixText(nm) );
				}
				os.println("}}");
			}
			os.println("}}}");
			ExecutableMemberDoc[]mems;
			FieldDoc[]flds;
System.out.print("serializable.."); System.out.flush();
			flds = cd.serializableFields();
			if( flds.length > 0 ) {
				printFields(cd, flds, "Serializable Fields");
			}
System.out.print("fields.."); System.out.flush();
			flds = cd.fields();
			if( flds.length > 0 ) {
				printFields(cd, flds, "Fields");
			}
System.out.print("cons.."); System.out.flush();
			mems = cd.constructors();
			if( mems.length > 0 ) {
				os.println("\\startsubsubsection{Constructors}{");
				printMembers(cd, mems, true);
				os.println("}");
			}
System.out.print("methods.."); System.out.flush();
			mems = cd.methods();
			if( mems.length > 0 ) {
				os.println("\\startsubsubsection{Methods}{");
				printMembers(cd, mems, true);
				os.println("}");
			}
			if( inherited == false ) {
				os.println( "\\hide{inherited}{" );
			}
			ClassDoc par = cd.superclass();
System.out.print("parents.."); System.out.flush();
			boolean yet = false;
			while( par != null &&
					par.qualifiedName().equals("java.lang.Object") == false ) {
				os.print("\\startsubsubsection{");
				os.println("Methods inherited from class {\\tt "+
					fixText(par.qualifiedName())+"}}{");
				os.println("\\par{\\small " );
				printRef( par.containingPackage(), par.name(), null );
				mems = par.methods();

				printMembers( par, mems, false );

				os.println("}}");
				par = par.superclass();
			}
System.out.println("done"); System.out.flush();
			if( inherited == false ) {
				os.println("}");
			}
			os.println("}");
		}
		if( classes.size() > 0 && !("Classes".equals(type))) {
			os.println("}");
		}
	}

	/**
	 *  Enumerates the fields passed and formats
	 *  them using Tex statements.
	 *
	 *  @param flds the fields to format
	 */
	static void printFields(ClassDoc cd, FieldDoc[] flds, String title) {
		boolean yet = false;
		for( int i = 0; i < flds.length; ++i ) {
			FieldDoc f = flds[i];
			if( f.isPublic() || f.isPrivate() ) {
				if( !yet ) {
					os.println("\\startsubsubsection{"+title+"}{");
					os.println( "\\begin{itemize}" );
					yet = true;
				}
				os.println("\\item{");
				os.print( fixText(f.modifiers())+" " );
				os.print( fixText(f.type().typeName()) +" " );
				os.print(fixText(f.name()) );
				
				String value = f.constantValueExpression();
				if (value != null) {
					value = fixText(value);
					if (value.startsWith("\"") && value.endsWith("\"")) {
						value = "``"+value.substring(1,value.length()-1)+"''";
					}
				    os.print( " = " + value );
				}
				
				os.println("\\begin{itemize}\\item{\\vskip -.9ex "+
					fixText(f.commentText())+"}\\end{itemize}");
				os.println("}");
			}
		}
		if( yet ) {
			os.println( "\\end{itemize}" );
			os.println("}");
		}
	}

	/**
	 *  Enumerates the members of a section of the document and formats
	 *  them using Tex statements.
	 *
	 *  @param mems the members of this entity
	 *  @see #start
	 */
	static void printMembers(ClassDoc cd, ExecutableMemberDoc[] dmems, boolean labels ) {
		if( dmems.length == 0 )
			return;
		os.println("\\vskip -2em");
		os.println("\\begin{itemize}");
		List l = Arrays.asList( dmems );
		Collections.sort(l);
		Iterator itr = l.iterator();
		for( int i = 0; itr.hasNext(); ++i ) {
			ExecutableMemberDoc mem = (ExecutableMemberDoc)itr.next();
			ParamTag[] params = mem.paramTags();

			if( i > 0 )
				os.println( "\\divideents{"+fixText(mem.name())+"}" );
			os.println("\\item{\\vskip -1.9ex " );
			os.println("\\membername{"+fixText(mem.name())+"}");
			os.print("{\\tt " );
			os.print( fixText(mem.modifiers()) );
			if( mem instanceof MethodDoc ) {
				if (((MethodDoc) mem).returnType().qualifiedTypeName().startsWith(SHORT_PACKAGE)) {
					os.print( " "+fixText(((MethodDoc)mem).returnType().typeName()) );
				} else {
					os.print( " "+fixText(((MethodDoc)mem).returnType().toString()) );
				}	
			}
			os.print(" {\\bf "+fixText(mem.name())+"}( " );
			Parameter[]parms = mem.parameters();
			int p = 0;
			String qparmstr="";
			String parmstr="";
			for( ; p < parms.length; ++p ) {
				if( p > 0 )
					os.println( ",");
				Type t = parms[p].type();
				if (t.qualifiedTypeName().startsWith(SHORT_PACKAGE)) {
					os.print( "{\\tt "+fixText(t.typeName())+"" );
				} else {
					os.print( "{\\tt "+fixText(t.qualifiedTypeName()).replaceAll("\\.", ".\\\\-")+"" );
				}
				
				os.print( fixText(t.dimension())+"} " );
				os.print( "{\\bf "+fixText(parms[p].name())+"}" );
				if( qparmstr.length() != 0 )
					qparmstr += ",";
				qparmstr += t.qualifiedTypeName()+t.dimension();
				if( parmstr.length() != 0 )
					parmstr += ",";
				parmstr += t.typeName()+t.dimension();
			}
			os.println( " )" );
			if( labels && qparmstr.startsWith("field") == false ) {
				os.print("\\label{"+refName(makeRefKey(cd.qualifiedName()+"."+mem.name()+
					(( qparmstr.length() > 0 ) ?
					 ("("+qparmstr+")") :
					  "" )))+"}" );
				os.print("\\label{"+refName(makeRefKey(cd.name()+"."+mem.name()+
					(( parmstr.length() > 0 ) ?
					("("+parmstr+")"):
					"")))+"}" );
			}
			os.println( "}%end signature");
			boolean yet = false;

			String cmnt = mem.commentText();
			if( cmnt != null && cmnt.equals("") == false ) {
				if( !yet ) {
					os.println( "\\begin{itemize}" );
					os.println("\\sld");
					yet = true;
				}
				os.println( "\\item{" );
				os.println("\\sld");
				os.println( "{\\bf Usage}\n  \\begin{itemize}\\isep\n   \\item{" );
				os.println( fixText(mem.commentText()) );
				os.println( "}%end item\n  \\end{itemize}\n}" );
			}

			if( params.length > 0 ) {
				if( !yet ) {
					os.println( "\\begin{itemize}" );
					os.println("\\sld");
					yet = true;
				}
				os.println( "\\item{" );
				os.println("\\sld");
				os.println( "{\\bf Parameters}" );
				os.println("\\sld\\isep");
				os.println("  \\begin{itemize}");
				os.println("\\sld\\isep");
				for (int j = 0;j < params.length; ++j) {
					os.println("   \\item{" );
					os.println("\\sld");
					os.println("{\\tt "+fixText(params[j].parameterName())+"}"+
						" - " + fixText(params[j].parameterComment())+"}");
				}
				os.println("  \\end{itemize}");
				os.println("}%end item");
			}

			if( mem instanceof MethodDoc ) {
				Tag[] ret = mem.tags("return");
				if( ret.length > 0 ) {
					if( !yet ) {
						os.println( "\\begin{itemize}" );
						os.println("\\sld");
						yet = true;
					}	        
					os.println("\\item{{\\bf Returns} - ");
					for( int j = 0; j < ret.length; ++j ) {
						os.println(fixText(ret[j].text())+" ");
					}
					os.println("}%end item");
				}
			}

			if( mem instanceof MethodDoc ) {
				ThrowsTag[] excp = ((MethodDoc)mem).throwsTags();
				if( excp.length > 0 ) {
					if( !yet ) {
						os.println( "\\begin{itemize}" );
						os.println("\\sld");
						yet = true;
					}	        
					os.println("\\item{{\\bf Exceptions}");
					os.println("  \\begin{itemize}");
					os.println("\\sld");
					for( int j = 0; j < excp.length; ++j ) {
						String ename = excp[j].exceptionName();
						ClassDoc cdoc = excp[j].exception();
						if( cdoc != null )
							ename = cdoc.qualifiedName();
						os.println("   \\item{\\vskip -.6ex{\\tt "+fixText(ename)+"} - "+fixText(excp[j].exceptionComment())+"}" );
					}
					os.println("  \\end{itemize}");
					os.println("}%end item");
				}
			}

			SeeTag[] sees = mem.seeTags();
			if( sees.length > 0 ) {
				if( !yet ) {
					os.println( "\\begin{itemize}" );
					yet = true;
				}
				os.println("\\item{{\\bf See Also}");
				os.println("  \\begin{itemize}");
				for( int j = 0; j < sees.length; ++j ) {
					PackageDoc pd = sees[j].referencedPackage();
					String pkg = "";
					if( pd != null ) {
						pkg = pd.name()+".";
					}
					String cls = sees[j].referencedClassName();
					String memn = sees[j].referencedMemberName();
					if (pkg.startsWith(SHORT_PACKAGE)) {
						os.print( "   \\item{{\\tt "+fixText(cls) );
					} else {
						os.print( "   \\item{{\\tt "+fixText(pkg+cls).replaceAll("\\.", ".\\\\-") );
					}
					
					if( memn != null && memn.equals("") == false ) {
						os.print( ".\\-"+fixText(memn).replaceAll("\\.", ".\\\\-").replaceAll("\\(", "( ").replaceAll("\\)", " )") );
					}
					os.println("} {\\small ");
					printRef( pd, cls, memn );
					os.println( "}%end \\small\n}%end item" );
				}
				os.println("  \\end{itemize}");
				os.println("}%end item");
			}
			if( yet )
				os.println("\\end{itemize}");
			os.println("}%end item");
		}
		os.println("\\end{itemize}");
	}
	
	static void stackTable( Properties p, StringBuffer ret, String txt, int off ) {
		tblstk.push( tblinfo );
		tblinfo = new TableInfo( p, ret, txt, off );
	}
	
	static void printRef( PackageDoc pd, String cls, String mem ) {
		String pkg = "";
		if( pd != null ) {
			pkg = pd.name()+".";
		}

		String lbl = pkg+cls;
		if( mem != null && mem.equals("") == false ) {
			lbl += "."+mem;
		}
		os.print( "\\refdefined{"+refName(makeRefKey(lbl))+"}" );
	}

	static void processBlock( String block, StringBuffer ret ) {
		if( block.substring(0,6).equalsIgnoreCase("@link ") ) {
			block = block.substring(6).trim().replaceAll("#", ".");

			StringTokenizer st = new StringTokenizer(block," \n\r\t");
			String key = st.nextToken();
			String text = key;
			if( st.hasMoreTokens() )
				text = st.nextToken("\001").trim();
			ret.append( fixText(text.trim())+
					"\\refdefined{"+refName(makeRefKey(key))+"}");
		} else {
			ret.append("{"+block+"}");
		}
	}

	static String makeRefKey( String key ) {
		return key;
	}

	static String block = "";
	static String refurl = "";
	static String refimg = "";
	static boolean collectBlock;
	static int chapt = 0;
	static int textdepth = 0;
	static String fixText( String str ) {
		StringBuffer ret = new StringBuffer(str.length());
		long start  = System.currentTimeMillis();
		boolean svcoll = false;
		String svblock = null;
		if( textdepth > 0 ) {
			svcoll = collectBlock;
			svblock = block;
		}
		++textdepth;
		for( int i = 0 ; i < str.length(); ++i ) { /* { */
			int c = str.charAt(i);
			if( collectBlock == true && c != '}') {
				block += str.charAt(i);
				continue;
			}
			switch(c) {
			case ' ':
				if( verbat > 0 ) {
					ret.append("\\phantom{ }");
				} else {
					ret.append(' ');
				}
				break;
			case '_':
			case '%':
			case '$':
			case '#':
				ret.append( '\\' );
				ret.append( (char)c );
				break;
			case '^': /* { */
				ret.append("$\\wedge$");
				break;
			case '}':
				if( collectBlock == false ) {
					ret.append("$\\}$");
					break;
				}
				collectBlock = false;
				processBlock( block, ret );
				break;
			case '{':
				if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("{@link") ) {
					block = "@link";
					collectBlock = true;
					i += 5;
				} else {
					ret.append("$\\{$");
				}
				break;
			case '<':
				if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("<pre>") ){

					ret.append( "{\\tt\n");
					verbat++;
					i+=4;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("</pre>") ){

					verbat--;
					ret.append( "}\n" );
					i+=5;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h1>") ){
					ret.append("\\headref{1}{\\Huge}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h1>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h2>") ){
					ret.append("\\headref{2}{\\huge}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h2>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h3>") ){
					ret.append("\\headref{3}{\\Large}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h3>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h4>") ){
					ret.append("\\headref{4}{\\normalsize}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h4>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h5>") ){
					ret.append("\\headref{5}{\\small}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h5>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h6>") ){
					ret.append("\\headref{6}{\\footnotesize}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h6>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h7>") ){
					ret.append("\\headref{7}{\\scriptsize}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h7>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<h8>") ){
					ret.append("\\headref{8}{\\tiny}{");
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</h8>") ){
					ret.append("}\\bl ");
					i+=4;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("<html>") ){
					i+=5;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</html>") ){
					if( chapt > 0 ) {
						ret.append("}");
						--chapt;
					}
					i+=6;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("<head>") ){
					i+=5;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</head>") ){
					i+=6;
				} else if( str.length() > i+7 && str.substring(i,i+8).equalsIgnoreCase("<center>") ){
					ret.append("\\makebox[\\hsize]{ ");
					i+=7;
				} else if( str.length() > i+8 && str.substring(i,i+9).equalsIgnoreCase("</center>") ){
					ret.append("}");
					i+=8;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("<meta") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("<title>") ){
					i+=6;
					ret.append("\\chapter{");
				} else if( str.length() > i+7 && str.substring(i,i+8).equalsIgnoreCase("</title>") ){
					ret.append("}{");
					i+=7;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("<form") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</form>") ){
					i+=6;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("<input") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
				} else if( str.length() > i+7 && str.substring(i,i+8).equalsIgnoreCase("</input>") ){
					i+=7;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("<body") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</body>") ){
					i+=6;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("<code>") ){
					ret.append( "{\\tt " );
					i+=5;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</code>") ){
					ret.append( "}" );
					i+=6;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</br>") ){
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<br>") ){
					ret.append( "\\mbox{}\\newline\n" );
					i+=3;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("</p>") ){
					i+=3;
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<p>") ){
					ret.append( "\\bl " );
					i+=2;
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<hr") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					String sz = p.getProperty("size");
					int size = 1;
					if( sz != null )
						size = Integer.parseInt(sz);
					ret.append( "\\newline\\rule[2mm]{\\hsize}{"+(1*size*.5)+"mm}\\newline\n" );
					i = idx;
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<b>") ){
					ret.append( "{\\bf " );
					i+=2;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("</b>") ){
					ret.append( "}" );
					i+=3;
				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("<strong>") ){
					ret.append( "{\\bf " );
					i+=6;
				} else if( str.length() > i+7 && str.substring(i,i+8).equalsIgnoreCase("</strong>") ){
					ret.append( "}" );
					i+=7;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("</img>") ){
					i+=5;
				} else if( str.length() > i+4 && str.substring(i,i+4).equalsIgnoreCase("<img") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+4 );
					refimg = p.getProperty("src");
					ret.append( "(see image at "+fixText(refimg)+")" );
					i = idx;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("</a>") ){
					if( refurl != null ) {
						ret.append( "} " );
						if( refurl.charAt(0) == '#' )
							ret.append("\\refdefined{"+refName(makeRefKey(refurl.substring(1)))+"}" );
						else
							ret.append("at "+fixText(refurl)+"" );
					}
					i+=3;
				} else if( str.length() > i+2 && str.substring(i,i+2).equalsIgnoreCase("<a") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					refurl = p.getProperty("href");
					String refname = p.getProperty("href");
					i = idx;
					if( refurl != null )
						ret.append( "{\\bf " );
					else if( refname != null )
						ret.append("\\label{"+refName(makeRefKey(refname))+"}" );
				} else if( str.length() > i+3 && str.substring(i,i+3).equalsIgnoreCase("<ol") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					ret.append( "\\begin{enumerate}\n" );
					itemcnts.push( new Integer(itemcnt) );
					itemcnt = 0;
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<dl") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					ret.append( "\\begin{itemize}\n" );
					itemcnts.push( new Integer(itemcnt) );
					itemcnt = 0;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<li>") ){
					if( itemcnt > 0 )
						ret.append( "}\n");
					++itemcnt;
					ret.append( "\\item{\\vskip -.8ex " );
					i+=3;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<dt>") ){
					if( itemcnt > 0 )
						ret.append( "}\n");
					++itemcnt;
					ret.append( "\\item[" );
					i+=3;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("<dd>") ){
					++itemcnt;
					ret.append( "]{" );
					i+=3;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</dl>") ){
					ret.append( "}\n\\end{itemize}" );
					itemcnt = 0;
					if( itemcnts.isEmpty() == false )
						itemcnt = ((Integer)itemcnts.pop()).intValue();
					i+=4;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</ol>") ){
					ret.append( "}\n\\end{enumerate}" );
					itemcnt = 0;
					if( itemcnts.isEmpty() == false )
						itemcnt = ((Integer)itemcnts.pop()).intValue();
					i+=4;
				} else if( str.length() > i+3 && str.substring(i,i+3).equalsIgnoreCase("<ul") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					ret.append( "\\begin{itemize}" );
					itemcnts.push( new Integer( itemcnt ) );
					itemcnt = 0;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</ul>") ){
					ret.append( "}\n\\end{itemize}\n" );
					itemcnt = 0;
					if( itemcnts.isEmpty() == false )
						itemcnt = ((Integer)itemcnts.pop()).intValue();
					i+=4;
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<i>") ){
					ret.append( "{\\it " );
					i+=2;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("</i>") ){
					ret.append( "}" );
					i+=3;
				} else if( str.length() > i+7 && str.substring(i,i+8).equalsIgnoreCase("</table>") ){
					tblinfo.endTable(ret);
					tblinfo = (TableInfo)tblstk.pop();
					i+=7;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</th>") ){
					tblinfo.endCol(ret);
					i+=4;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</td>") ){
					tblinfo.endCol(ret);
					i+=4;
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("</tr>") ){
					tblinfo.endRow(ret);
					i+=4;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("<table") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+6 );
					i = idx;
					stackTable( p, ret, str, i );
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<tr") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					tblinfo.startRow(ret,p);
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<td") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					tblinfo.startCol(ret, p);
				} else if( str.length() > i+2 && str.substring(i,i+3).equalsIgnoreCase("<th") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+3 );
					i = idx;
					tblinfo.startHeadCol(ret,p);
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("<font") ){
					Properties p = new Properties();
					int idx = getTagAttrs( str, p, i+5 );
					i = idx;
					String sz = p.getProperty("size");
					String col = p.getProperty("color");
					ret.append( "{" );
					if( col != null ) {
						if( "redgreenbluewhiteyellowblackcyanmagenta".indexOf(col) != -1 )
							ret.append("\\color{"+col+"}");
						else {
							if( "abcdefABCDEF0123456789".indexOf(col.charAt(0)) != -1 ) {
								Color cc = new Color( (int)Long.parseLong( col, 16 ) );
								String name = (String)colors.get("color"+cc.getRGB());
								if( name == null ) {
									ret.append("\\definecolor{color"+colIdx+"}[rgb]{"+(cc.getRed()/255.0)+","+(cc.getBlue()/255.0)+","+(cc.getGreen()/255.0)+"}");
									name = "color"+colIdx;
									colIdx++;
									colors.put( "color"+cc.getRGB(), name );
								}
								ret.append("\\color{"+name+"}" );
								++colIdx;
							}
						}
					}

				} else if( str.length() > i+6 && str.substring(i,i+7).equalsIgnoreCase("</font>") ){
					ret.append( "}" );
					i+=6;
				} else {
					ret.append("\\textless ");
				}
				break;
			case '\r':
			case '\n':
				if( tblstk.size() > 0 ) {
					// Swallow new lines while tables are in progress,
					// <tr> controls new line emission.
					if( verbat > 0 ) {
						ret.append( "\\newline\n" );
					} else
						ret.append(" ");
				} else {
					if( (i+1) < str.length() && str.charAt(i+1) == 10 ) {
						ret.append("\\bl ");
						++i;
					} else {
						if( verbat > 0 )
							ret.append( "\\mbox{}\\newline\n" );
						else
							ret.append( (char)c );
					}
				}
				break;
			case '/':
				ret.append("$/$");
				break;
			case '&':
				if( str.length() > i+4 && str.substring(i,i+2).equals("&#") ) {
					String it = str.substring(i+2);
					int stp = it.indexOf(';');
					if( stp > 0 ) {
						String v = it.substring(0,stp);
						int ch = -1;
						try {
							ch = Integer.parseInt( v );
						} catch( NumberFormatException ex ) {
							ch = -1;
						}
						if( ch >= 0 && ch < 128 ) {
							ret.append("\\verb"+((char)(ch+1))+((char)ch)+((char)(ch+1)));
						} else {
							ret.append( "\\&\\#"+v );
						}
						i+=v.length()+2;
					} else {
						ret.append( "\\&\\#" );
						i++;
					}
				} else if( str.length() > i+4 && str.substring(i,i+5).equalsIgnoreCase("&amp;") ) {
					ret.append("\\&");
					i+=4;
				} else if( str.length() > i+5 && str.substring(i,i+6).equalsIgnoreCase("&nbsp;") ) {
					ret.append("\\phantom{ }");
					i+=5;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("&lt;") ) {
					ret.append("\\textless ");
					i += 3;
				} else if( str.length() > i+3 && str.substring(i,i+4).equalsIgnoreCase("&gt;") ) {
					ret.append("\\textgreater ");
					i += 3;
				} else
					ret.append("\\&");
				break;
			case '>':
				ret.append("\\textgreater ");
				break;
			case '\\':
				ret.append("\\bslash ");
				break;
			default:
				ret.append( (char)c );
				break;
			}
		}
		if( textdepth > 0 ) {
			collectBlock = svcoll;
			block = svblock;
		}
		--textdepth;
		long to = System.currentTimeMillis()-start;
		if( to > 1000 ) {
			System.out.print("(text @"+to+" msecs)" );
			System.out.flush();
		}
		return ret.toString();
	}
	
	/**
	 *  This method parses HTML tags to extract the tag attributes and place
	 *  them into a Properties object.
	 *
	 *  @param str the string that is the whole HTML tag (at least)
	 *  @param i the offset in the string where the tag starts
	 */
	static int getTagAttrs( String str, Properties p, int i ) {
//	static Properties getTagAttrs( String str, int i ) {
		byte b[] = str.getBytes();
		String name = "";
		String value = "";
		int state = 0;
		while( i < b.length ) {
			switch((char)b[i]) {
			case ' ':
				if( state == 2 ) {
					p.put( name.toLowerCase(), value );
					state = 1;
					name = "";
					value = "";
				} else if( state == 3 ) {
					value += " ";
				}
				break;
			case '=':
				if( state == 1 ) {
					state = 2;
					value = "";
				} else if( state > 1 ) {
					value += '=';
				}
				break;
			case '"':
				if( state == 2 ) {
					state = 3;
				} else if( state == 3 ) {
					state = 1;
					p.put( name.toLowerCase(), value );
					name = "";
					value = "";
				}
				break;		
			case '>':
				if( state == 1 ) {
					p.put(name.toLowerCase(),"" );
				} else if( state == 2 ) {
					p.put(name.toLowerCase(),value);
				}
				return i;
			default:
				if( state == 0 )
					state = 1;
				if( state == 1 ) {
					name = name + (char)b[i];
				} else {
					value = value + (char)b[i];
				}
			}
			++i;
		}
		return i;
	}
}
