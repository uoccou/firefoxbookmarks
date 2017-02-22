package ie.uoccou.firefox;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
/**
 * 
 * @author ultan
 * 
 * push again,again
 *
 */
public class SQLiteBookmarksCheckerTest {

	@Ignore @Test
	public void testMain() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testCheckBookmarks() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testGetSqlLiteFile() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testSetSqlLiteFile() throws Exception {
		//test null setting, bad file, good file
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testGetQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testSetQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testGetCountQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testSetCountQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testGetDeleteQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testSetDeleteQuery() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testGetOldest() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Ignore @Test
	public void testSetOldest() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testSetProcessCount() throws Exception {
		SQLiteBookmarksChecker c = new SQLiteBookmarksChecker();
		
		int GOOD_NUM=1;
		int NEG_NUM=-1;
		int ZERO=0;
		
		c.setProcessCount(GOOD_NUM);
		;if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with good num failed.");
		
		//should still be 1!
		c.setProcessCount(NEG_NUM);
		if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with negative num failed.");
		
		c.setProcessCount(ZERO);
		if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with zero failed.");
		
		
	}

	@Test
	public void testSetProcessCountString() throws Exception {
		
		SQLiteBookmarksChecker c = new SQLiteBookmarksChecker();
		
		String GOOD_NUM="1";
		String BAD_NUM="NaN";
		String NEG_NUM="-1";
		String ZERO="0";
		
		c.setProcessCount(GOOD_NUM);
		;if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with good num string failed.");
		
		//should still be 1!
		c.setProcessCount(BAD_NUM);
		if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with bad num string failed.");
		
		c.setProcessCount(NEG_NUM);
		if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with negative num string failed.");
		
		c.setProcessCount(ZERO);
		if ( c.getProcessCount()!=1 )
			throw new RuntimeException("Setting process count with zero string failed.");
		

	}

	@Test
	/**
	 * tests num of arg handling only
	 * @see testSetSqlLiteFile(String)
	 * @see testSetProcessCount(String)
	 * 
	 * @throws Exception
	 */
	public void testRunWithArgs() throws Exception {
		
		//fixture data for SQLlite ?
		
		//test 0 args
		SQLiteBookmarksChecker c = new SQLiteBookmarksChecker();
		c.runWithArgs(null);
		assertTrue( "Unexpected BookMarks file change!", c.getSqlLiteFile() == SQLiteBookmarksChecker.DEF_SQLLITE_FILE );
		assertTrue( "Unexpected bookmarks processing count change!", c.getProcessCount() == SQLiteBookmarksChecker.PROCESS_ALL );
		
		//test 1, 2 args
		c = new SQLiteBookmarksChecker();
		c.runWithArgs( new String[] {"something"} );
		assertTrue( "Unexpected BookMarks file setting!", c.getSqlLiteFile() == "something" );
		assertTrue( "Unexpected bookmarks processing count change!", c.getProcessCount() == SQLiteBookmarksChecker.PROCESS_ALL);
		c.runWithArgs( new String[] {"something", "1"} );
		assertTrue( "Unexpected BookMarks file setting!", c.getSqlLiteFile() == "something" );
		assertTrue( "Unexpected bookmarks processing count setting!", c.getProcessCount() == 1 );
		
		//test more than 2 args
		c = new SQLiteBookmarksChecker();
		c.runWithArgs( new String[] {"something", "1", "xyz"} );
		assertTrue( "Unexpected BookMarks file change!", c.getSqlLiteFile() == SQLiteBookmarksChecker.DEF_SQLLITE_FILE );
		assertTrue( "Unexpected bookmarks processing count change!", c.getProcessCount() == SQLiteBookmarksChecker.PROCESS_ALL );
		//test bad args
		//this is up to the getters and setters
	}

}
