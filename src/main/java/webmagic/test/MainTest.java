package webmagic.test;

import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 各种方法测试
 *
 * @author ldh
 * @since 2016-10-25 15:36
 */
public class MainTest {
    public static void main(String[] args) throws Exception{
        List<String> list = new ArrayList<String>();
        list.add("http://www.100ec.cn/search.cgi?p=1&f=search&terms=青岛");
        list.add("http://www.100ec.cn/search.cgi?p=1&f=search&terms=郑州");
        list.add("http://www.100ec.cn/search.cgi?p=1&f=search&terms=浙江");
        list.add("http://www.100ec.cn/search.cgi?p=1&f=search&terms=厦门&f=search");
        List<String> listEncode = new ArrayList<String>();
        for (String url : list) {
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

    }
}
