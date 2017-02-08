package webmagic.Spider;

import org.apache.commons.lang.math.RandomUtils;
import webmagic.domain.Article;
import webmagic.utils.MyUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

import java.util.List;

/**
 * 100ec爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class CifnewsSpider implements PageProcessor {
    //一级列表页url
    public static final String First_LIST_URL = "http://www.cifnews.com/Tag/\\d+";
    //二级列表页URL(更多)
    public static final String SECOND_LIST_URL = "http://cweekly\\.cifnews\\.com/Index/\\d+";
    //文章详情URL
    public static final String ARTICAL_URL = "http://cweekly\\.cifnews\\.com/Article/\\d+";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain("www.cifnews.com").setSleepTime(3000).setUserAgent(USER_AGENT);

    @Override
    public void process(Page page) {
        int randomTime = RandomUtils.nextInt(15000);
        System.out.println("随机时间：" + randomTime);
        try {
            Thread.sleep(randomTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (page.getUrl().regex(SECOND_LIST_URL).match()) {//进入二级列表页
            System.out.println("二级列表页");
            List<String> articleUrlList = page.getHtml().xpath("//li[@class='item_small']/div/h1/a").links().regex(ARTICAL_URL).all();//获取文章页url
            List<String> pageUrlList = page.getHtml().xpath("//div[@class='page']/a").links().regex(SECOND_LIST_URL).all();//获取翻页列表页url
            //将获取的上述链接放入待请求列表中
            page.addTargetRequests(articleUrlList);
            page.addTargetRequests(pageUrlList);
        } else if (page.getUrl().regex(ARTICAL_URL).match()) {//进入文章页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//div[@class='article_til']/text()").toString().trim();
            String date = page.getHtml().xpath("//div[@class='author']/text()").regex("\\d{4}-\\d{2}-\\d{2}").toString().trim();
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@class='article_wen']").toString();
            content = MyUtils.removeTag(content);
            Article article = new Article(title, date, url, content, null, null);//实例化Article

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
        //Spider spider = Spider.create(new CifnewsSpider()).addUrl("http://www.cifnews.com/Tag/82").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(1);
        Spider spider = Spider.create(new CifnewsSpider()).addUrl("http://cweekly.cifnews.com/Index/2").setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(1000000000)))
                .addPipeline(new FilePipeline()).thread(2);
        //Spider spider = Spider.create(new CifnewsSpider()).addUrl("http://www.cifnews.com/article/22771").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(1);

        spider.run();
    }
}
