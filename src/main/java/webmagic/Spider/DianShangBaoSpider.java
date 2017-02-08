package webmagic.Spider;

import org.apache.commons.collections.CollectionUtils;
import webmagic.domain.Article;
import webmagic.utils.MyUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.List;

/**
 * paidai爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class DianShangBaoSpider implements PageProcessor {
    public static final String ZI_XUN = "http://www.dsb.cn/list-9-%d.html";
    public static final String REN_WU = "http://www.dsb.cn/list-13-%d.html";
    public static final String DATA = "http://www.dsb.cn/list-10-%d.html";
    public static final String GAN_HUO = "http://www.dsb.cn/list-11-%d.html";
    public static final String O2O = "http://www.dsb.cn/list-24-%d.html";
    public static final String HANG_YE_YAN_JIU = "http://www.dsb.cn/list-25-%d.html";
    public static final String HANG_YE_GUAN_CHA = "http://www.dsb.cn/list-22-%d.html";

    public static final String HOME_PAGE = "^http://www\\.dsb\\.cn/$";
    //二级列表页
    public static final String SECOND_LIST_URL = "http://www\\.dsb\\.cn/list-\\S+\\.html";
    //文章详情URL
    public static final String ARTICAL_URL = "http://www\\.dsb\\.cn/\\d+\\.html";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain("www.dsb.cn").setSleepTime(3000).setRetryTimes(3).setUserAgent(USER_AGENT);


    @Override
    public void process(Page page) {
        if (page.getUrl().regex(HOME_PAGE).match()) {
            //资讯
            System.out.println("构造地址");
            for (int i = 1; i < 678; i++) {
                page.addTargetRequest(ZI_XUN.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 104; i++) {
                page.addTargetRequest(REN_WU.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 52; i++) {
                page.addTargetRequest(DATA.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 154; i++) {
                page.addTargetRequest(GAN_HUO.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 101; i++) {
                page.addTargetRequest(O2O.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 101; i++) {
                page.addTargetRequest(O2O.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 127; i++) {
                page.addTargetRequest(HANG_YE_GUAN_CHA.replace("%d", String.valueOf(i)));
            }
            for (int i = 1; i < 4; i++) {
                page.addTargetRequest(HANG_YE_YAN_JIU.replace("%d", String.valueOf(i)));
            }
        } else if (page.getUrl().regex(SECOND_LIST_URL).match()) {
            System.out.println("获取文章列表");
            List<String> secondUrlList = page.getHtml().xpath("//div[@class='news_title']/strong/a").links().regex(ARTICAL_URL).all();//获取文章列表
            page.addTargetRequests(secondUrlList);
        } else if (page.getUrl().regex(ARTICAL_URL).match()) {//进入文章页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//div[@class='contents']/h1/a").toString().trim();
            System.out.println("============="+title);
            String date = page.getHtml().xpath("//span[@class='contents-time']/text()").toString().trim().substring(0,16);
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@class='contents-con']").toString();
            content = MyUtils.removeTag(content);
            Article article = new Article(title, date, url, content, null, "");//实例化Article

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
        //主页
        //Spider spider = Spider.create(new PaidaiSpider()).addUrl("http://bbs.paidai.com").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(5);

        //二级列表页
        //Spider spider = Spider.create(new CifnewsSpider()).addUrl("http://bbs.paidai.com/r/reply-2").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(1);.setDownloader(new PaidaiDownloader())

        Spider spider = Spider.create(new DianShangBaoSpider()).addUrl("http://www.dsb.cn/").setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(1000000000)))
                .addPipeline(new FilePipeline()).thread(20);

        spider.run();
    }
}
