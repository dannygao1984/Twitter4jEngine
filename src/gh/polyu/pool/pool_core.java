package gh.polyu.pool;

import twitter4j.Twitter;

public interface pool_core<Template>  {

	
	public Template  request_instance();
	
	
	public int fresh_pool();

	
	public int initial_pool();
	
	public int initial_pool(Twitter[] twitters2Authoriz);
	
	public int initial_pool_withAuthorizedTwitters(Twitter[] authorizedTwitters);

	
	public boolean release(Template instance) ;

	
	public boolean statusOfInstance(Template instance) ;


}
