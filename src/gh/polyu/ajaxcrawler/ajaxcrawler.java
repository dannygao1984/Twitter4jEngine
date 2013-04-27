package gh.polyu.ajaxcrawler;

import org.apache.commons.configuration.ConfigurationException;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.condition.UrlCondition;
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;


public class ajaxcrawler {

	private static final String URL = "http://twitter.com/#!/dannygao/followers";

	private static final String ALL_ANCHORS = "a";
	private static final String LANGUAGE_TOOLS = "Log in";

	private static final String HEADER_XPATH = "//DIV[@data-user-id='user']";

	private static final int MAX_CRAWL_DEPTH = 1;
	private static final int MAX_STATES = 1000;

	private ajaxcrawler() {
		// Utility class
	}

	private static CrawlSpecification getCrawlSpecification() {
		CrawlSpecification crawler = new CrawlSpecification(URL);

		crawler.clickDefaultElements();
		crawler.dontClick(ALL_ANCHORS).underXPath(HEADER_XPATH);
		crawler.dontClick(ALL_ANCHORS).withText(LANGUAGE_TOOLS);
		crawler.dontClick("input").withAttribute("type", "submit");

		// limit the crawling scope
		crawler.setMaximumStates(MAX_STATES);
		crawler.setDepth(MAX_CRAWL_DEPTH);

		//crawler.setInputSpecification(getInputSpecification());	

		// Make sure we only crawl Google and no external web site
		crawler.addCrawlCondition("Only crawl Twitter", new UrlCondition("twitter"));

		return crawler;
	}

	private static InputSpecification getInputSpecification() {
		InputSpecification input = new InputSpecification();

		// enter "Crawljax" in the search field
		input.field("q").setValue("Crawljax");
		return input;
	}

	private static CrawljaxConfiguration getConfig() {
		CrawljaxConfiguration crawljaxConfiguration = new CrawljaxConfiguration();
		crawljaxConfiguration.setBrowser(BrowserType.ie);
		crawljaxConfiguration.setCrawlSpecification(getCrawlSpecification());

		// Generate a crawl report
		// crawljaxConfiguration.addPlugin(new CrawlOverview());
		return crawljaxConfiguration;
	}

	/**
	 * @param args
	 *            none.
	 */
	public static void main(String[] args) {
		try {
			CrawljaxController crawljax = new CrawljaxController(getConfig());
			crawljax.run();
		} catch (CrawljaxException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
