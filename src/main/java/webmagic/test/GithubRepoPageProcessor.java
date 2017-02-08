package webmagic.test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * github爬虫
 *
 * @author ldh
 * @since 2016-10-12 19:11
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
        List<String> targetRequest = page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all();
        page.addTargetRequests(targetRequest);
        String author =  page.getHtml().regex("https://github\\.com/(\\w+)/.*").toString();
        String name = page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString();
        page.putField("author",author);
        page.putField("name", name);
        if (page.getResultItems().get("name") == null) {
            page.setSkip(true);
        }
        String readme = page.getHtml().xpath("//div[@id='readme']/tidyText()").toString();
        page.putField("readme",readme);

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws Exception{
        Spider spider = Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft").thread(5);

        SpiderMonitor.instance().register(spider);
        spider.start();


    }
}
