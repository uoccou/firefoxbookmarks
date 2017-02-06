package ie.uoccou.firefox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.uoccou.util.HttpUtils;
/**
 * This talks directly to the SQLite firefox db. 
 * Dont use firefox while this is running as when the code gets to the deletion 
 * phase it throw an exception saying that db was updated by another process.
 * 
 * @author ultan
 *
 */
public class SQLiteBookmarksChecker {
	public static final String DEF_SQLLITE_FILE = "/home/ultan/.mozilla/firefox/8nghvy0l.default-1353957977070/places.sqlite";
	private static final String DEF_SELECT = "select p.id as placeId,p.url,p.title as placeTitle,b.title as bookmarkTitle, p.last_visit_date as lastVisitDate, b.id as bookmarkId ,b.fk,b.parent from moz_places p, moz_bookmarks b where p.id=b.fk;";
	private static final String DEF_COUNT = "select count(1) from moz_places p, moz_bookmarks b where p.id=b.fk;";
	private static final String DEF_DELETE = "delete from moz_bookmarks where fk=?";
	private static final String DEF_UPDATE_TEST = "update moz_bookmarks set title='FAILED' where fk=?";
	private static final Long FOUR_YEARS_MILLIS = 126144000000L;//(4 * 365 * 24 * 60 * 60 * 1000);
	private static final Long FORTY_YEARS_MILLIS = 1261440000000L;//(40 * 365 * 24 * 60 * 60 * 1000);
	private String query = DEF_SELECT;
	private String countQuery = DEF_COUNT;
	private Long oldest = FORTY_YEARS_MILLIS;//old enough not to delete by default
	//private String deleteQuery = DEF_UPDATE_TEST;
	private String deleteQuery = DEF_DELETE;
	
	private HttpUtils httpUtil = new HttpUtils();
	private Session sess = null;
	
	class Session {
		ResultSet rs = null;
		Statement stat = null;
		PreparedStatement prepStat = null;
		Connection conn = null;
		int count=0;
		boolean isReady() {
			return ( conn != null && stat != null && rs != null);
		}
	}
	private String sqlLiteFile = DEF_SQLLITE_FILE;
	
	public static void main(String[] args) throws Exception {
		SQLiteBookmarksChecker reader = new SQLiteBookmarksChecker();
		reader.checkBookmarks();
	}

	public void checkBookmarks() {
		int count = 0;
		
		try {
			init();
			List<Integer> notFounds = runCheck();
			System.out.println(notFounds.size() + " bookmarks were not reachable. ");
			deleteUnreachables(notFounds);
			commit();
		} catch (Exception e){
			System.out.println("Exception checking bookmarks : " + e.toString() );
		} finally {
			closeConnection();
			System.out.println("Done");
		}
		
	}
	
	private void init() throws Exception {
		Class.forName("org.sqlite.JDBC");
		sess = new Session();
		sess.conn = DriverManager.getConnection("jdbc:sqlite:" + getSqlLiteFile() + "//");
		sess.conn.setAutoCommit(false);
		sess.stat = sess.conn.createStatement();
		//SQLite doesnt do type sensitve or updatable
		//sess.stat = sess.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		sess.rs = sess.stat.executeQuery( getCountQuery() );
		while (sess.rs.next()){
			sess.count = sess.rs.getInt(1);
		}
		sess.rs = sess.stat.executeQuery( getQuery() );
		//for the deletes
		sess.prepStat = sess.conn.prepareStatement( getDeleteQuery() );
		httpUtil.setTimeout(10000);
		setOldest(FOUR_YEARS_MILLIS);
	}
	private void commit() throws Exception {
		if ( sess.isReady() )
			sess.conn.commit();
	}
	public String getSqlLiteFile() {
		return sqlLiteFile;
	}

	private void deleteUnreachables(List<Integer> rows) throws Exception {
		
		if ( sess.isReady() ){
			for (Integer id: rows){
				sess.prepStat.setInt(1, id);
			    int rc = sess.prepStat.executeUpdate();
			    System.out.println("Deleted bookmark :" + id);
			}
		}
	    
	}
	/**
	 * set the path to the sqllite file
	 * 
	 * @param sqlLiteFile
	 */
	public void setSqlLiteFile(String sqlLiteFile) {
		if ( null != sqlLiteFile )
			this.sqlLiteFile = sqlLiteFile;
	}
	
	private List<Integer> runCheck() throws Exception {
		int row=0;
		int found=0;
		
		List<Integer> badUrls = new ArrayList<Integer>();
		
		if ( sess.isReady() ) {
			
			System.out.println("Rows:" + sess.count);
			
			while ( sess.rs.next() ) {
				row++;
				String url = sess.rs.getString("url");
				String title = sess.rs.getString("bookmarkTitle");
				System.out.println("("+row+"): '" + title + "' at [" + url + "]");
				Integer placeId = sess.rs.getInt("placeId");
				Integer bookmarkId = sess.rs.getInt("bookmarkId");
				Integer fkId = sess.rs.getInt("fk");
				Long lastVisitDate = sess.rs.getLong("lastVisitDate");
				//check if its an old bookmark
				if ( checkLastVisitExpired( fkId, lastVisitDate) ) {
					found++;
					badUrls.add(fkId);
				}
				//if not already found [placeId=fk] or is older than we want
				//if ( !badUrls.contains(fkId) && found <=2000  ) {
				if ( !badUrls.contains(fkId) ) {
					if (httpUtil.pingURL( url ) )
						found++;
					else {
						//sess.rs.deleteRow();
						System.out.println( "Row " + row + " of " + sess.count +". Alive:"+ found);
						System.out.println ("Failed to reach : " + url + "(" + placeId + "," + bookmarkId+")");
						badUrls.add(placeId);
					} 
				}
				
			}
		}
		
		return badUrls;
		
	}

	private boolean checkLastVisitExpired(Integer fkId, Long lastVisitDate) {
		//if its an old bookmark, then zap it.
		boolean rc = false;
		if ( null != lastVisitDate && lastVisitDate > 0){
			//mozilla time is measured in microsecs, not millis - PRTime: developer.mozilla.org/en/PRTime
			Date date = new Date(lastVisitDate/1000);
			System.out.println("Last visit date: " + lastVisitDate +", " + date);
			long since = System.currentTimeMillis() - date.getTime();
			if ( since > getOldest() ){
				System.out.println("Older than oldest allowed");
				rc = true;
			}
		}
		return rc;
	}

	private void closeConnection() {
		// TODO Auto-generated method stub
		
			try {
				if ( null != sess.rs )
					sess.rs.close();
				if ( null != sess.stat )
					sess.stat.close();
				if ( null != sess.conn)
					sess.conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	

	public String getQuery() {
		return query;
	}

	/**
	 * set the query string to use to query for bookmarks
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		if ( null != query )
			this.query = query;
	}

	public String getCountQuery() {
		return countQuery;
	}

	public void setCountQuery(String countQuery) {
		if ( null != countQuery )
			this.countQuery = countQuery;
	}

	public String getDeleteQuery() {
		return deleteQuery;
	}

	public void setDeleteQuery(String deleteQuery) {
		if ( null != deleteQuery )
			this.deleteQuery = deleteQuery;
	}

	public Long getOldest() {
		return oldest;
	}

	public void setOldest(Long oldest) {
		if (null != oldest )
			this.oldest = oldest;
	}

}
