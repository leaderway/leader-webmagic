package webmagic.Spider;

import webmagic.domain.Article;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * paidai爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class PaidaiSpider implements PageProcessor {
    public static final String HOME_PAGE_URL = "http://bbs.paidai.com";
    //板块url
    public static final String First_LIST_URL = "http://bbs.paidai.com/[0-9a-z]+";
    //二级列表页URL
    public static final String SECOND_LIST_URL = "http://bbs.paidai.com/[0-9a-z]+/reply\\S{0,}";
    //文章详情URL
    public static final String ARTICAL_URL = "http://bbs.paidai.com/topic/\\d+";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain(".paidai.com").setSleepTime(3000).setUserAgent(USER_AGENT)
            .addCookie("XForum_AuthCode", "deleted").addCookie("domain", ".paidai.com");



    @Override
    public void process(Page page) {

        if (page.getUrl().regex(HOME_PAGE_URL).match()) {// 进入主页
            System.out.println("进入主页");
            List<String> sectionUrlList = page.getHtml().xpath("//div[@id='all_section']/h3/strong/a").links().regex(First_LIST_URL).all();//获取板块url
            //将获取的上述链接放入待请求列表中
            page.addTargetRequests(sectionUrlList);
        }
        if (page.getUrl().regex(First_LIST_URL).match() || page.getUrl().regex(SECOND_LIST_URL).match()) {//进入二级列表页
            System.out.println("二级列表页");
            List<String> articleUrlList = page.getHtml().xpath("//h4[@class='post_title']/a").links().regex(ARTICAL_URL).all();//获取文章页url
            List<String> pageUrlList = page.getHtml().xpath("//div[@class='page-sep']/ul/li/a").links().regex(SECOND_LIST_URL).all();//获取翻页列表页url
            //将获取的上述链接放入待请求列表中
            page.addTargetRequests(articleUrlList);
            page.addTargetRequests(pageUrlList);
        } else if (page.getUrl().regex(ARTICAL_URL).match()) {//进入文章页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//h1[@class='t_title']").toString().trim();
            String date = page.getHtml().xpath("//p[@class='t_info']/span[2]").toString().trim();
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@id='topic_content']").toString();
            List<String> keywords = page.getHtml().xpath("//div[@class='mytags']/ul/li/a/text()").all();
            String topic = page.getHtml().xpath("//p[@class='t_info']/span/a").toString().trim();

            Article article = new Article(title, date, url, content, keywords, topic);//实例化Article

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

        Spider spider = Spider.create(new CifnewsSpider()).addUrl("http://bbs.paidai.com/topic/1129365").addPipeline(new ConsolePipeline())
                .addPipeline(new FilePipeline()).thread(1);

        spider.run();
    }
}
