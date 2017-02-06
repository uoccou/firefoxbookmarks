package ie.uoccou.firefox;

public class Bookmark {

	private String guid="";
	private String title="";
	private Integer index=0;
	private Long lastModified=0L;
	private Long dateAdded=0L;
	private Integer id=0;
	private String type="";
	private String root="";
	private Bookmark[] children;
	private String uri="";
	private Anno[] annos;
	private String tags;
	private String iconurl="";
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Long getLastModified() {
		return lastModified;
	}
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
	public Long getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Long dateAdded) {
		this.dateAdded = dateAdded;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public Bookmark[] getChildren() {
		return children;
	}
	public void setChildren(Bookmark[] children) {
		this.children = children;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Anno[] getAnnos() {
		return annos;
	}
	public void setAnnos(Anno[] annos) {
		this.annos = annos;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
}
