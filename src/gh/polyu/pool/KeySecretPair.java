package gh.polyu.pool;

public class KeySecretPair {

	private String key = "";
	private String secret = "";
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public KeySecretPair(String key, String secret) {
		// TODO Auto-generated constructor stub
		this.key = key;
		this.secret = secret;
	}

}
