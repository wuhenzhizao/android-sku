package com.wuhenzhizao.sku;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuhenzhizao.bean.Sku;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wuhenzhizao on 2017/8/30.
 */

public class Data {
    public static List<Sku> skuList;

    static {
        String json = "[\n" +
                "    {\n" +
                "        \"id\":\"1000684\",\n" +
                "        \"itemId\":\"1000490\",\n" +
                "        \"originPrice\":20000,\n" +
                "        \"sellingPrice\":10000,\n" +
                "        \"mainImage\":\"http://img.dev.gomeplus.in/img/af90edb24953ea77d205c4c7cc563c19.jpg\",\n" +
                "        \"inStock\":true,\n" +
                "        \"stockQuantity\":5,\n" +
                "        \"attributes\":[\n" +
                "            {\n" +
                "                \"key\":\"大小\",\n" +
                "                \"value\":\"M\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\":\"颜色\",\n" +
                "                \"value\":\"红色\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":\"1000685\",\n" +
                "        \"itemId\":\"1000490\",\n" +
                "        \"originPrice\":28000,\n" +
                "        \"sellingPrice\":18000,\n" +
                "        \"mainImage\":\"http://img.dev.gomeplus.in/img/af90edb24953ea77d205c4c7cc563c19.jpg\",\n" +
                "        \"inStock\":true,\n" +
                "        \"stockQuantity\":28,\n" +
                "        \"attributes\":[\n" +
                "            {\n" +
                "                \"key\":\"大小\",\n" +
                "                \"value\":\"L\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\":\"颜色\",\n" +
                "                \"value\":\"白色\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":\"1000686\",\n" +
                "        \"itemId\":\"1000490\",\n" +
                "        \"originPrice\":20000,\n" +
                "        \"sellingPrice\":10000,\n" +
                "        \"mainImage\":\"http://img.dev.gomeplus.in/img/1e55d79b8005e4536c952e953faaa937.jpg\",\n" +
                "        \"inStock\":true,\n" +
                "        \"stockQuantity\":50,\n" +
                "        \"attributes\":[\n" +
                "            {\n" +
                "                \"key\":\"大小\",\n" +
                "                \"value\":\"S\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\":\"颜色\",\n" +
                "                \"value\":\"蓝色\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":\"1000687\",\n" +
                "        \"itemId\":\"1000490\",\n" +
                "        \"originPrice\":28000,\n" +
                "        \"sellingPrice\":18000,\n" +
                "        \"discountPercentage\":\"0\",\n" +
                "        \"mainImage\":\"http://img.dev.gomeplus.in/img/c2441c042517fdd7ed8b33bb0df963ae.jpg\",\n" +
                "        \"inStock\":true,\n" +
                "        \"stockQuantity\":46,\n" +
                "        \"attributes\":[\n" +
                "            {\n" +
                "                \"key\":\"大小\",\n" +
                "                \"value\":\"XL\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\":\"颜色\",\n" +
                "                \"value\":\"黑色\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
        skuList = new Gson().fromJson(json, new TypeToken<List<Sku>>() {}.getType());
    }
}
