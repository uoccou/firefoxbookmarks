package ie.uoccou.firefox;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import ie.uoccou.util.HttpUtils;

public class JsonChecker {

	public JsonChecker() {
		super();
		// TODO Auto-generated constructor stub
	}

	private int found = 0;
	private int foundOk= 0;
	public static final String JsonFilename = "/mnt/mobey/My Backup/bookmarks-2017-01-28.json";
	private String sourceFile = null;
	private HttpUtils httpUtil = new HttpUtils();
	// public static Properties properties = null;
	// public static JsonObject JsonObject = null;
	//
	// static {
	// properties = new Properties();
	// }

	public JsonChecker(String sourceFile) {
		super();
		this.sourceFile = sourceFile;
	}

	public void parse() {
		JsonReader reader = null;
		
		
		try {
			
			reader = Json.createReader(new FileReader(getSourceFile()));
			JsonObject jsonObj = reader.readObject();

			// StringWriter stWriter = new StringWriter();
			// JsonWriter jsonWriter = Json.createWriter(stWriter);
			//
			// jsonWriter.writeObject(jsonObj);
			// jsonWriter.close();
			//
			Set<String> set = jsonObj.keySet();
			//JsonObject model = Json.createObjectBuilder().build();
			
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				// if it checks ok, keep it
				//trouble is bookmarks are nested/recursive objects - inside toolbar and menu objects.
				if (checkObject(jsonObj.get(key), key)) {
					//model.put(key, jsonObj.get(key));
				} else {
					// dont keep it
				}
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != reader)
				reader.close();
			System.out.println("Found " + foundOk + "/" + found + " URIs. ");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonChecker checker = new JsonChecker();
		checker.setSourceFile(JsonFilename);
		checker.parse();
		System.out.println("*** done ***");
	}

	private boolean checkObject(JsonValue tree, String key) {
		boolean rc = false;

//		if (key != null)
//			System.out.print("Key: " + key + ", Value: ");
		switch (tree.getValueType()) {
		case OBJECT:
			if ( parseObject(tree) ) {
				//we can keep it
				rc = true;
			} else {
				
			}
				
			break;
		case ARRAY:
			System.out.println("ARRAY");
			JsonArray array = (JsonArray) tree;
			for (JsonValue val : array){
				checkObject(val, null);
//				if ( checkObject(val, null) ){
//					//this might lead to an overcount @TEST 
//					//found++;
//				}
			}
			break;
		case TRUE:
		case FALSE:
		case NULL:
			System.out.println(tree.getValueType().toString()); //@TEST
			break;

		}

		return rc;
	}

	private boolean parseObject(JsonValue tree) {
		boolean rc = false;
		JsonObject object = (JsonObject) tree;
		
		for (String name : object.keySet()) {
			System.out.println("Parsing:" + name);
			JsonValue subtree = object.get(name);
			
			switch (subtree.getValueType()) {
			case STRING:
				
				JsonString st = (JsonString) subtree;
				System.out.println("PARSED STRING("+st+")");
				
				if ( name.equals("uri") ){
					found++;
					rc = checkUri(name, st);
				} else {
					System.out.println(name +" not for pinging.");
				}
				
				break;
			case ARRAY:
				System.out.println("PARSED ARRAY("+name+")");
				rc = checkObject(object.get(name), name);
				//rc = parseObject(subtree);
				break;
			case NUMBER:
				System.out.println("PARSED NUMBER("+name+")");
				break;
			case OBJECT:
				System.out.println("PARSED OBJECT("+name+")");
				break;
			case TRUE:
			case FALSE:
			case NULL:
				System.out.println(tree.getValueType().toString() + "(NULL)");
				break;
			}
			
		}
		return rc;
	}

	private boolean checkUri(String name, JsonString st) {
		boolean rc = false;
		String value = st.getString();
		System.out.println("*** FOUND ***- " + name +": " + st.getString());
		if ( name.equals("uri") && httpUtil.pingURL(value)){
			System.out.println("*** Pings OK ***");
			foundOk++;
			rc = true;	
		} else {
			System.out.println("*** No Ping ***");
		}
		return rc;
	}



	

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
}
