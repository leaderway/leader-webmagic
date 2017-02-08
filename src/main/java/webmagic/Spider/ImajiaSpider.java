package webmagic.Spider;

import org.apache.commons.collections.CollectionUtils;
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
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * paidai爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class ImajiaSpider implements PageProcessor {
    public static final String LATEST_NEWS_URL = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db552ae61001552af457460001%2C8ac0c8db550614e80155064ab2750006%2C2c9180825558e85b015558fbd5e0136e%2C8ac0c8db54f0d3500154f11628220015%2C8ac0c8db54f0d3500154f1157a240013%2C8ac0c8db54f0d3500154f117a78b0019%2C2c9180825558e85b015558fa4cd91247%2C2c9180825558e85b015558fb42d412fd%2C8ac0c8db54f0d3500154f1189402001b%2C8ac0c8db550614e80155064a37a10004%2C8ac0c8db550614e80155064ab2750006%2C8a04288d556d69dc01557b49b73d0198%2C8a04288d556d69dc01557b491d690196";
    public static final String DIGITAL_APPLICANT = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db550614e80155064a37a10004";
    public static final String LING_SHOU_DIAN_SHANG = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db552ae61001552af457460001%2C8ac0c8db550614e80155064ab2750006%2C2c9180825558e85b015558fbd5e0136e%2C8ac0c8db54f0d3500154f11628220015%2C8ac0c8db54f0d3500154f1157a240013%2C8ac0c8db54f0d3500154f117a78b0019%2C2c9180825558e85b015558fa4cd91247%2C2c9180825558e85b015558fb42d412fd%2C8ac0c8db54f0d3500154f1189402001b%2C8ac0c8db550614e80155064a37a10004%2C8ac0c8db550614e80155064ab2750006%2C8a04288d556d69dc01557b49b73d0198%2C8a04288d556d69dc01557b491d690196";
    public static final String FU_SHI_MEI_ZHUANG = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db54f0d3500154f1157a240013";
    public static final String SHENG_XIAN_SHI_PIN = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db54f0d3500154f117a78b0019";
    public static final String KUA_JING_DIAN_SHANG = "http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=%d&catid=8ac0c8db54f0d3500154f11628220015";
    public static final String URLS[] = {LATEST_NEWS_URL, DIGITAL_APPLICANT, LING_SHOU_DIAN_SHANG, FU_SHI_MEI_ZHUANG, SHENG_XIAN_SHI_PIN, KUA_JING_DIAN_SHANG};

    public static final String AJAX_URL = "http://www\\.imaijia\\.com/xstb/front/authorArticleController\\.do\\?more-author-articles&pageSize=";
    //文章详情URL
    public static final String ARTICAL_URL = "http://www\\.imaijia\\.com\\/\\S+\\/\\S+\\.shtml";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain("imaijia.com").setSleepTime(300).setUserAgent(USER_AGENT);


    @Override
    public void process(Page page) {
        for (int i = 1; i < 311; i++) {
            page.addTargetRequest(LATEST_NEWS_URL.replace("%d", String.valueOf(i)));
        }
        //for (int i = 1; i < 40; i++) {
        //    page.addTargetRequest(DIGITAL_APPLICANT.replace("%d", String.valueOf(i)));
        //}
        //for (int i = 1; i < 300; i++) {
        //    page.addTargetRequest(LING_SHOU_DIAN_SHANG.replace("%d", String.valueOf(i)));
        //}
        //for (int i = 1; i < 18; i++) {
        //    page.addTargetRequest(FU_SHI_MEI_ZHUANG.replace("%d", String.valueOf(i)));
        //}
        //for (int i = 1; i < 18; i++) {
        //    page.addTargetRequest(SHENG_XIAN_SHI_PIN.replace("%d", String.valueOf(i)));
        //}
        //for (int i = 1; i < 18; i++) {
        //    page.addTargetRequest(KUA_JING_DIAN_SHANG.replace("%d", String.valueOf(i)));
        //}

        if (page.getUrl().regex(AJAX_URL).match()) {
            List<String> articleUrlList = new ArrayList<String>();//保存当前板块文章地址
            System.out.println("获取json");
            articleUrlList = new JsonPathSelector("$.[*].staticUrl").selectList(page.getRawText());//文章页
            if (CollectionUtils.isNotEmpty(articleUrlList)) {
                //将获取的上述链接放入待请求列表中
                page.addTargetRequests(articleUrlList);
            }
        } else if (page.getUrl().regex(ARTICAL_URL).match()) {//进入文章页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//div[@class='article-detail-bigtit']/text()").toString().trim();
            String date = page.getHtml().xpath("//span[@class='article-detail-info-time']/text()").toString().trim();
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@class='article-detail-cont']").toString();
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

        Spider spider = Spider.create(new ImajiaSpider()).addUrl("http://www.imaijia.com/xstb/front/authorArticleController.do?more-author-articles&pageSize=20&pageNo=3&catid=8ac0c8db550614e80155064a37a10004").setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(1000000000)))
                .addPipeline(new FilePipeline()).thread(50);

        spider.run();
    }
}
