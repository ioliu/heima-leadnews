package com.heima.crawler.test;

import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.process.original.impl.CsdnOriginalDataProcess;
import com.heima.model.crawler.core.parse.ParseItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CsdnOriginalDataProcessTest {

    @Autowired
    private CsdnOriginalDataProcess csdnOriginalDataProcess;

    @Test
    public void test1(){
        List<ParseItem> parseItemList = csdnOriginalDataProcess.parseOriginalRequestData(new ProcessFlowData());
        System.out.println(parseItemList);
    }
}
