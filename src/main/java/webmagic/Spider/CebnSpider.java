package webmagic.Spider;

import webmagic.domain.Article;
import webmagic.utils.MyUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

import java.util.List;

/**
 * CEBN爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class CebnSpider implements PageProcessor {

    public static final String HOME_PAGE = "^http://www\\.cebn\\.cn/news/$";
    //一级列表页和翻页URL
    public static final String FIRST_LIST_URL = "http://www\\.cebn\\.cn/news/list-\\d+\\S+\\.html";
    //文章详情URL
    public static final String ARTICAL_URL = "http://www\\.cebn\\.cn/news/show-\\d+\\.html";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain("cebn.cn").setSleepTime(3000).setUserAgent(USER_AGENT);

    @Override
    public void process(Page page) {

        if (page.getUrl().regex(HOME_PAGE).match()) {//进入主页
            System.out.println("进入主页");
            List<String> firsttUrlList = page.getHtml().xpath("//div[@class='box_body']/table/tbody/tr/td/a").links().regex(FIRST_LIST_URL).all();//获取一级列表页url
            page.addTargetRequests(firsttUrlList);
        } else if (page.getUrl().regex(FIRST_LIST_URL).match()) {//进入一级列表页
            System.out.println("进入一级列表页");
            List<String> firsttUrlList = page.getHtml().xpath("//div[@class='pages']/a").links().regex(FIRST_LIST_URL).all();//翻页链接
            page.addTargetRequests(firsttUrlList);

            List<String> articleUrlList = page.getHtml().xpath("//div[@class='catlist']/ul/li/a").links().regex(ARTICAL_URL).all();//获取文章url
            page.addTargetRequests(articleUrlList);

        } else {//进入文章详情页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//h1[@id='title']/text()").toString().trim();
            String date = page.getHtml().xpath("//div[@class='info']/text()").regex("\\d{4}-\\d+-\\d+").toString().trim();
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@id='article']").toString();
            content = MyUtils.removeTag(content);
            String topic = page.getHtml().xpath("//div[@class='pos']/a/text()").all().get(2).toString().trim();
            Article article = new Article(title, date, url, content, null, topic);//实例化Article

            if (article.getTitle() == null) {
                page.setSkip(true);
            } else {
                page.putField("article", article);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //Spider spider = Spider.create(new CecrcSpider()).addUrl("http://www.100ec.cn/search.cgi?p=1&f=search&terms=%BB%A8%DF%C2").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(1);
        //Spider spider = Spider.create(new CecrcSpider()).addUrl("http://www.100ec.cn/detail--6362819.html").addPipeline(new ConsolePipeline()).addPipeline(new FilePipeline()).thread(1);
        //// TODO: 2016/10/13 系统路径更改 
        Spider spider = Spider.create(new CebnSpider()).addUrl("http://www.cebn.cn/news/").setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(1000000000)))
                .addPipeline(new FilePipeline()).thread(30);
        spider.run();
    }
}
