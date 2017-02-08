package webmagic.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 测试自定义httpclient生成器
 *
 * @author ldh
 * @since 2016-11-11 17:27
 */
public class TestMyHttpClientGenerator {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1)Gecko/20100101 Firefox/4.0.1";
    //public static void main(String[] args) throws Exception{
    //    Site site = Site.me().setDomain("paidai.com").setSleepTime(3000).setUserAgent(USER_AGENT);
    //    BufferedReader in = null;
    //    //MyHttpClientGenerator myHttpClientGenerator = new MyHttpClientGenerator();
    //    HttpClientGenerator myHttpClientGenerator = new HttpClientGenerator();
    //    CloseableHttpClient httpClient = myHttpClientGenerator.getClient(site);
    //    HttpGet request = new HttpGet("http://bbs.paidai.com/r/reply-2");
    //    HttpResponse response = httpClient.execute(request);
    //    in = new BufferedReader(new InputStreamReader(response.getEntity()
    //            .getContent()));
    //    StringBuffer sb = new StringBuffer("");
    //    String line = "";
    //    String NL = System.getProperty("line.separator");
    //    while ((line = in.readLine()) != null) {
    //        sb.append(line + NL);
    //    }
    //    in.close();
    //    System.out.println(sb.toString());
    //
    //}
    public static void main(String[] args) throws Exception{
        Site site = Site.me().setDomain("paidai.com").setSleepTime(3000).setUserAgent(USER_AGENT);
        BufferedReader in = null;
        HttpClientGenerator myHttpClientGenerator = new HttpClientGenerator();
        CloseableHttpClient httpClient = myHttpClientGenerator.getClient(site);
        HttpGet request = new HttpGet("http://bbs.paidai.com/r/reply-2");
        HttpResponse response = httpClient.execute(request);
        in = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        System.out.println(sb.toString());

    }
}
