package org.wonderly.doclets;

import java.util.*;

/**
 *  This class provides support for converting HTML tables into LaTeX tables.
 *  Some of the things <b>NOT</b> implemented include the following:
 *  <ul>
 *  <li>valign attributes are not procesed, but align= is.
 *  <li>rowspan attributes are not processed, but colspan= is.
 *  <li>the argument to border= in the table tag is not used to control line size
 *  </ul>
 *  <br>
 *  Here is an example table.
 *  <p>
 *  <table border>
 *  <tr><th>Column 1 Heading<th>Column two heading<th>Column three heading
 *  <tr><td>data<td colspan=2>Span two columns
 *  <tr><td><i>more data</i><td align=right>right<td align=left>left
 *  <tr><td colspan=3><table border>
 *  <tr><th colspan=3>A nested table example
 *  <tr><th>Column 1 Heading</th><th>Column two heading</th><th>Column three heading</th>
 *  <tr><td>data</td><td colspan=2>Span two columns</td>
 *  <tr><td><i>more data</i></td><td align=right>right</td><td align=left>left</td>
 *  <tr><td><pre>
 *    1
 *  2
 *  3
 *  4
 *  </pre></td>
 *  <td><pre>
 *    first line
 *  second line
 *  third line
 *  fourth line
 *  </pre></td>
 *  </table>
 *  </table>
 *
 *  @version 1.0
 *  @author <a href="mailto:gregg.wonderly@pobox.com">Gregg Wonderly</a>
 */
public class TableInfo {
	private int rowcnt = 0;
	private int colcnt = 0; 
	private boolean border = false;
	private boolean colopen = false;
	private Properties props;
	private int bordwid;
	private boolean parboxed;
	private boolean rowopen;
	static int tblcnt;
	int tblno;
	String tc;

	String hasProp( String prop, Properties p ) {
		if( p == null )
			return null;
		Enumeration e = p.keys();
		while( e.hasMoreElements() ) {
			String key = (String)e.nextElement();
			String val = p.getProperty( key );
			if( key.equalsIgnoreCase(prop) ) {
				return val;
			}
		}
		return null;
	}
	
	int hasNumProp( String prop, Properties p ) {
		String val = hasProp( prop, p ) ;
		if( val == null )
			return -1;
		try { return Integer.parseInt( val ); } catch( Exception ex ) {}
		return -1;
	}

	/**
	 *  Constructs a new table object and starts processing of the table by
	 *  scanning the <code>&lt;table&gt;</code> passed to count columns.
	 *
	 *  @param p properties found on the <code>&lt;table&gt;</code> tag
	 *  @param ret the result buffer that will contain the output
	 *  @param table the input string that has the entire table definition in it.
	 *  @param off the offset into <code>&lt;table&gt;</code> where scanning should start
	 */
	public TableInfo( Properties p, StringBuffer ret, String table, int off ) {
		props = p;
		tblno = tblcnt++;
		tc = ""+(char)('a'+(tblno/(26*26)))+
			  (char)((tblno/26)+'a')+
			  (char)((tblno%26)+'a');
		if( p == null )
			return;
		String val = hasProp( "border", p );
		border = false;
		if( val != null ) {
			border = true;
			bordwid = 2;
			if( val.equals("") == false ) {
				try {bordwid = Integer.parseInt( val ); } catch( Exception ex ) {}
				if( bordwid == 0 )
					border = false;
			}
		}
		ret.append("\n% Table #"+tblno+"\n");
		byte[]b = table.getBytes();
		int col = 0;
		int row = 0;
		for( int i = off; i < b.length; ++i ) {
			if( b[i] == '<' ) {
				if( table.substring( i, i+7 ) .equalsIgnoreCase("</table") ){
					break;
				} else if( table.substring( i, i+4 ) .equalsIgnoreCase("</tr") ){
					break;
				} else if( table.substring( i, i+3 ) .equalsIgnoreCase("<tr") ){
					if( row++ > 0 )
						break;
				} else if( table.substring( i, i+3 ) .equalsIgnoreCase("<td") ){
					Properties pp = new Properties();
					int idx = TexDoclet.getTagAttrs( table, pp, i+3 );
					int v = hasNumProp( "colspan",pp );
					if( v > 0 )
						col += v;
					else
						col++;
					i = idx-1;
				} else if( table.substring( i, i+3 ) .equalsIgnoreCase("<th") ){
					Properties pp = new Properties();
					int idx = TexDoclet.getTagAttrs( table, pp, i+3 );
					int v = hasNumProp( "colspan", pp );
					if( v > 0 )
						col += v;
					else
						col++;
					i = idx-1;
				}
			}
		}
		if( col == 0 )
			col = 1;
		for( int i = 0; i < col; ++i ) {
			String cc = ""+(char)('a'+(i/(26*26)))+
					  (char)((i/26)+'a')+
					  (char)((i%26)+'a');
			ret.append("\\newlength{\\tbl"+tc+"c"+cc+"w}\n");
			ret.append("\\setlength{\\tbl"+tc+"c"+cc+"w}{"+(1.0/col)+"\\hsize}\n");
		}
		ret.append("\\begin{tabular}{");
		if( border )
			ret.append("|");
		for( int i = 0; i < col; ++i ) {
			String cc = ""+(char)('a'+(i/(26*26)))+
					  (char)((i/26)+'a')+
					  (char)((i%26)+'a');
			ret.append("p{\\tbl"+tc+"c"+cc+"w}");
			if( border )
				ret.append("|");
		}
		ret.append("}\n");
	}
		
	/**
	 *  Starts a new column, possibly closing the current column if needed
	 *
	 *  @param ret the output buffer to put LaTeX into
	 *  @param p the properties from the <code>&lt;td&gt;</code> tag
	 */
	public void startCol( StringBuffer ret, Properties p ) {
		endCol(ret);
		int span = hasNumProp("colspan", p);
		if( colcnt > 0 ) {
			ret.append(" & " );
		}
		String align = hasProp("align", p );
		if( align != null && span < 0 )
			span = 1;
		if( span > 0 ) {
			ret.append("\\multicolumn{"+span+"}{" );
			if( border && colcnt == 0)
				ret.append("|");
			String cc = ""+(char)('a'+(colcnt/(26*26)))+
					  (char)((colcnt/26)+'a')+
					  (char)((colcnt%26)+'a');
			if( align != null ) {
				String h = align.substring(0,1);
				if( "rR".indexOf(h) >= 0 )
					ret.append("r");
				else if( "lL".indexOf(h) >= 0 )
					ret.append("p{\\tbl"+tc+"c"+cc+"w}");
				else if( "cC".indexOf(h) >= 0 )
					ret.append("p{\\tbl"+tc+"c"+cc+"w}");
			} else 
				ret.append("p{\\tbl"+tc+"c"+cc+"w}");
			if( border )
				ret.append("|");
			ret.append("}");
		}
		String wid=p.getProperty("texwidth");
		ret.append("{");
		if( wid != null ) {
			ret.append("\\parbox{"+wid+"}{\\vskip 1ex ");
			parboxed = true;
		}
		colcnt++;
		colopen = true;
	}
		
		
	/**
	 *  Starts a new Heading column, possibly closing the current column
	 *  if needed.  A Heading column has a Bold Face font directive around
	 *  it.
	 *
	 *  @param ret the output buffer to put LaTeX into
	 *  @param p the properties from the <code>&lt;th&gt;</code> tag
	 */
	public void startHeadCol( StringBuffer ret, Properties p ) {
		startCol( ret, p );
		ret.append("\\bf ");
	}
	
		
	/**
	 *  Ends the current column.
	 *
	 *  @param ret the output buffer to put LaTeX into
	 */
	public void endCol( StringBuffer ret ) {
		if( colopen ) {
			colopen = false;
			if(parboxed)
				ret.append("\\vskip 1ex}");
			parboxed = false;
			ret.append("}");
		}					
	}
	
		
	/**
	 *  Starts a new row, possibly closing the current row if needed
	 *
	 *  @param ret the output buffer to put LaTeX into
	 *  @param p the properties from the <code>&lt;tr&gt;</code> tag
	 */
	public void startRow( StringBuffer ret, Properties p ) {
		endRow(ret);
		if( rowcnt == 0 ) {
			if( border )
				ret.append(" \\hline " );
		}
		colcnt = 0;
		++rowcnt;
		rowopen = true;
	}
	
		
	/**
	 *  Ends the current row.
	 *
	 *  @param ret the output buffer to put LaTeX into
	 */
	public void endRow( StringBuffer ret ) {
		if( rowopen ) {
			endCol(ret);
			ret.append( " \\\\" );
			if( border )
				ret.append( " \\hline" );
			rowopen = false;
			ret.append("\n");
		}
	}
		
		
	/**
	 *  Ends the table, closing the last row as needed
	 *
	 *  @param ret the output buffer to put LaTeX into
	 */
	public void endTable( StringBuffer ret ) {
		endRow( ret );
		ret.append("\\end{tabular}\n");
	}
}
