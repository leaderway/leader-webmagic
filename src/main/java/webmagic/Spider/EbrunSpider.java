package webmagic.Spider;

import webmagic.domain.Article;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 100ec爬虫
 *
 * @author ldh
 * @since 2016-10-13 10:23
 */
public class EbrunSpider implements PageProcessor {

    public static final String HOME_PAGE = "^http://www.ebrun.com/$";
    //一级列表页URL
    public static final String FIRST_LIST_URL = "http://www.ebrun.com/[a-z1-9]+/";
    //二级列表页URL(更多)
    public static final String SECOND_LIST_URL = "http://www.ebrun.com/[a-z]+/[a-z]+/";
    //翻页url
    public static final String PAGE_LIST_URL = "http://www.ebrun.com/retail/more\\.php\\S+";
    //文章详情URL
    public static final String ARTICAL_URL = "http://www.ebrun.com/[0-9]+/[0-9]+.shtml";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    private Site site = Site.me().setDomain("ebrun.com").setSleepTime(3000).addHeader("Accept-Encoding", "gzip, deflate").setUserAgent(USER_AGENT);

    @Override
    public void process(Page page) {

        if (page.getUrl().regex(HOME_PAGE).match()) {//进入主页
            System.out.println("进入主页");
            List<String> firsttUrlList = page.getHtml().xpath("//div[@class='ebMenu']").links().regex(FIRST_LIST_URL).all();//获取一级列表页url
            page.addTargetRequests(firsttUrlList);
        } else if (page.getUrl().regex(FIRST_LIST_URL).match()) {//进入一级列表页
            System.out.println("进入一级列表页");
            List<String> firsttUrlList = page.getHtml().links().regex(FIRST_LIST_URL).all();//获取一级列表页url

            page.addTargetRequests(firsttUrlList);
            List<String> secondUrlList = page.getHtml().links().regex(SECOND_LIST_URL).all();//获取二级列表url
            for (String url : encodeUrl(secondUrlList)) {
                System.out.println("转义后的url" + url);
            }
            //page.addTargetRequests(encodeUrl(secondUrlList));

        } else if (page.getUrl().regex(SECOND_LIST_URL).match()) {//进入二级列表页
            System.out.println("进入二级列表页");
            List<String> secondUrlList = page.getHtml().xpath("//div[@class='page']").links().regex(SECOND_LIST_URL).all();//获取翻页二级列表url
            //page.addTargetRequests(secondUrlList);
            List<String> articleUrlList = page.getHtml().xpath("//div[@class='cnews']").links().regex(ARTICAL_URL).all();//获取文章url
            for (String articleUrl : articleUrlList) {
                System.out.println("文章地址   " + articleUrl);
            }
            page.addTargetRequests(articleUrlList);

        } else {//进入文章详情页
            System.out.println("进入文章页");
            String title = page.getHtml().xpath("//div[@class='newsview']/h2/text()").toString();
            String date = page.getHtml().xpath("//div[@class='newsview']/div[@class='public f_hong']/text()").regex("\\d{4}年\\d+月\\d+日\\d+:\\d+").toString();
            String url = page.getUrl().toString();
            String content = page.getHtml().xpath("//div[@class='newsview']/div[@class='nr']").toString();
            List<String> keywords = page.getHtml().xpath("//div[@class='guanjz b f_qing2']//a[@class='f_qing']/text()").all();
            String topic = page.getHtml().xpath("//div[@class='website f_qing']//a[@href][3]/text()").toString();
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

    /**
     * url地址转码
     * @param urlString
     * @return
     */
    private static List<String> encodeUrl(List<String> urlString) {
        List<String> listEncode = new ArrayList<String>();
        try {
            for (String url : urlString) {
                int startIndex = url.indexOf("terms=");
                int endIndex = url.indexOf("&", startIndex);
                String commonUrl = url.substring(0, startIndex+6);//从url开始到terms=的字符串
                String param = "";
                String encodedUrl ;
                if (endIndex == -1) {
                    //如果关键词位于url最末尾
                    param = URLEncoder.encode(url.substring((startIndex+6)), "gbk");
                    encodedUrl = commonUrl + param;
                    System.out.println(commonUrl + "/" +param);
                }else {
                    //如果关键词位于url中间
                    param = URLEncoder.encode(url.substring((startIndex+6), (endIndex)), "gbk");
                    String endCommonUrl = url.substring(endIndex);//url中文关键词后面的部分
                    encodedUrl = commonUrl + param + endCommonUrl;
                    System.out.println(commonUrl + "/" +param + "/" + endCommonUrl);
                }

                listEncode.add(encodedUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listEncode;
    }
    public static void main(String[] args) {
        //Spider spider = Spider.create(new EbrunSpider()).addUrl("http://www.100ec.cn/search.cgi?p=1&f=search&terms=%BB%A8%DF%C2").addPipeline(new ConsolePipeline())
        //        .addPipeline(new FilePipeline()).thread(1);
        //Spider spider = Spider.create(new CecrcSpider()).addUrl("http://www.ebrun.com/20161025/198464.shtml").addPipeline(new ConsolePipeline()).thread(1);
        //// TODO: 2016/10/13 系统路径更改 
        Spider spider = Spider.create(new CecrcSpider()).addUrl("http://www.ebrun.com/").addPipeline(new FilePipeline()).addPipeline(new ConsolePipeline()).thread(5);
        spider.run();
    }
}
