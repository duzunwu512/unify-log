package com.gzl.log.elasticsearch;

import com.google.common.collect.Lists;
import com.gzl.log.LogApplication;
import com.gzl.log.entity.BizLog;
import com.gzl.log.entity.Conference;
import com.gzl.log.repository.elasticsearch.ElasticsearchBizLogDao;
import com.gzl.log.service.elasticsearch.ElasticsearchBizLogService;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.BizLogVo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterator;

/**
 * Test case to show Spring Data Elasticsearch functionality.
 *
 * @author WHP
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogApplication.class)
public class ElasticsearchBizLogServiceTest {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    ElasticsearchBizLogService elasticsearchBizLogService;

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    private ElasticsearchBizLogDao elasticsearchBizLogDao;

    @PreDestroy
    public void deleteIndex() {
        System.out.println("PreDestroy...........");
        operations.indexOps(BizLog.class).delete();
    }

    @Before
    public void before() {
        System.out.println("Before...........");
        operations.indexOps(BizLog.class).refresh();
    }

    @After
    public void after() {
        System.out.println("after...........");
        elasticsearchBizLogDao.deleteAll();
    }

    @Test
    @Ignore("testInsert is OK.")
    public void testInsert() {
        BizLog bl = new BizLog();

        bl.setCreaterId("createrId001");
        bl.setCreaterUserName("createrUserName001");
        bl.setCreaterRealName("createrRealName001");
        bl.setCreateDate(new Date());
        bl.setBusiness("business-add");
        bl.setBizId("bizId001");
        bl.setContent("content000001");
        bl.setCreateDeptId("createDeptId001");
        bl.setCreaterDeptName("createrDeptName001");
        bl.setOperateType("add");
        bl.setUpdateUserName("updateUserName001");
        bl.setUpdate(new Date());
        bl.setCompanyId("companyId001");
        bl.setCompanyName("companyName001");
        bl.setVersion(1l);

        elasticsearchBizLogService.insert(bl);

        Iterable<BizLog> bls = elasticsearchBizLogService.findAll();
        List<BizLog> list = Lists.newArrayList();//.newArrays();
        bls.forEach(one -> {
            list.add(one);
        });
        System.out.println("rslt1:" + list.toString());
        Assert.assertEquals(1, list.size());


        List<BizLog> lst = Lists.newArrayList();
        bl = new BizLog();
        bl.setCreaterId("createrId002");
        bl.setCreaterUserName("createrUserName002");
        bl.setCreaterRealName("createrRealName002");
        bl.setCreateDate(new Date());
        bl.setBusiness("business-add");
        bl.setBizId("bizId002");
        bl.setContent("content000002");
        bl.setCreateDeptId("createDeptId002");
        bl.setCreaterDeptName("createrDeptName002");
        bl.setOperateType("add");
        bl.setUpdateUserName("updateUserName002");
        bl.setUpdate(new Date());
        bl.setCompanyId("companyId002");
        bl.setCompanyName("companyName002");
        bl.setVersion(1l);
        lst.add(bl);

        bl = new BizLog();
        bl.setCreaterId("createrId003");
        bl.setCreaterUserName("createrUserName003");
        bl.setCreaterRealName("createrRealName003");
        bl.setCreateDate(new Date());
        bl.setBusiness("business-add");
        bl.setBizId("bizId003");
        bl.setContent("content000003");
        bl.setCreateDeptId("createDeptId003");
        bl.setCreaterDeptName("createrDeptName003");
        bl.setOperateType("add");
        bl.setUpdateUserName("updateUserName003");
        bl.setUpdate(new Date());
        bl.setCompanyId("companyId003");
        bl.setCompanyName("companyName003");
        bl.setVersion(1l);
        lst.add(bl);

        elasticsearchBizLogService.insert(lst);

        bls = elasticsearchBizLogService.findAll();
        list.clear();
        bls.forEach(one -> {
            list.add(one);
        });

        System.out.println("rslt2:" + list.toString());
        Assert.assertEquals(3, list.size());

    }

    @Test
    public void testFind() {
        BizLog bl = new BizLog();
        List<BizLog> lst = Lists.newArrayList();
        for (int i = 1; i <= 3; i++) {
            bl = new BizLog();
            bl.setCreaterId("createrId00" + i);
            bl.setCreaterUserName("createrUserName00" + i);
            bl.setCreaterRealName("createrRealName00" + i);
            bl.setCreateDate(new Date());
            bl.setBusiness("business-add");
            bl.setBizId("bizId00" + i);
            bl.setContent("content00000" + i);
            bl.setCreateDeptId("createDeptId00" + i);
            bl.setCreaterDeptName("createrDeptName00" + i);
            bl.setOperateType("add");
            bl.setUpdateUserName("updateUserName00" + i);
            bl.setUpdate(new Date());
            bl.setCompanyId("companyId00" + i);
            bl.setCompanyName("companyName002" + i);
            bl.setVersion(1l);
            lst.add(bl);
        }
        elasticsearchBizLogService.insert(lst);

        List<BizLog> list = Lists.newArrayList();//.newArrays();
        List<String> ids = Lists.newArrayList();//.newArrays();
        Iterable<BizLog> bls = elasticsearchBizLogService.findAll();
        list.clear();
        bls.forEach(one -> {
            list.add(one);
            ids.add((one.getId()));
        });

        System.out.println("rslt2:" + list.toString());
        Assert.assertEquals(3, list.size());

        String id = ids.get(0);
        BizLog bg = elasticsearchBizLogService.findById(id);
        System.out.println("id:" + id + " bg:" + bg.toString());

        List<BizLog> bzgs = elasticsearchBizLogService.findByBizId("bizId003");
        Assert.assertEquals(1, bzgs.size());

        elasticsearchBizLogService.delete(id);
        bls = elasticsearchBizLogService.findAll();
        list.clear();
        bls.forEach(one -> {
            list.add(one);
        });
        Assert.assertEquals(2, list.size());


        elasticsearchBizLogService.delete(ids.toArray(new String[ids.size()]));
        bls = elasticsearchBizLogService.findAll();
        list.clear();
        bls.forEach(one -> {
            list.add(one);
        });
        Assert.assertEquals(0, list.size());

    }

    @Test
    public void testUpdate() {
        BizLog bl = new BizLog();

        bl.setCreaterId("createrId001");
        bl.setCreaterUserName("createrUserName001");
        bl.setCreaterRealName("createrRealName001");
        bl.setCreateDate(new Date());
        bl.setBusiness("business-add");
        bl.setBizId("bizId001");
        bl.setContent("content000001");
        bl.setCreateDeptId("createDeptId001");
        bl.setCreaterDeptName("createrDeptName001");
        bl.setOperateType("add");
        bl.setUpdateUserName("updateUserName001");
        bl.setUpdate(new Date());
        bl.setCompanyId("companyId001");
        bl.setCompanyName("companyName001");
        bl.setVersion(1l);

        bl = elasticsearchBizLogService.insert(bl);
        System.out.println("bl:"+bl);
        Assert.assertEquals("createrId001", bl.getCreaterId());

        BizLogVo vo = new BizLogVo();
        vo.setId(bl.getId());
        vo.setCreaterId("createrId007");
        vo.setCreaterUserName("createrUserName007");
        vo.setCreaterRealName("createrRealName007");
        vo.setCreateDate(new Date());
        vo.setBusiness("business-add");
        vo.setBizId("bizId007");
        vo.setContent("content000007");
        vo.setCreateDeptId("createDeptId007");
        vo.setCreaterDeptName("createrDeptName007");
        vo.setOperateType("add");
        vo.setUpdateUserName("updateUserName007");
        //vo.setUpdate(new Date());
        vo.setCompanyId("companyId007");
        vo.setCompanyName("companyName007");
        vo.setVersion(1l);
        boolean b = elasticsearchBizLogService.update(vo);
        Assert.assertTrue(b);

        bl = elasticsearchBizLogService.findById(bl.getId());

        System.out.println("bl:"+bl);

    }

    @Test
    public void testPage() throws InterruptedException {
        BizLog bl = null;
        List<BizLog> lst = Lists.newArrayList();
        for (int i = 1; i <= 43; i++) {
            bl = new BizLog();
            bl.setCreaterId("createrId00" + i);
            bl.setCreaterUserName("createrUserName00" + i);
            bl.setCreaterRealName("createrRealName00" + i);
            bl.setCreateDate(new Date());
            bl.setBusiness("business-add");
            bl.setBizId("bizId00" + i);
            bl.setContent("content00000" + i);
            bl.setCreateDeptId("createDeptId00" + i);
            bl.setCreaterDeptName("createrDeptName00" + i);
            bl.setOperateType("add");
            bl.setUpdateUserName("updateUserName00" + i);
            bl.setUpdate(new Date());
            bl.setCompanyId("companyId00" + i);
            bl.setCompanyName("companyName00" + i);
            bl.setVersion(1l);
            lst.add(bl);
            TimeUnit.SECONDS.sleep(2);
        }
        elasticsearchBizLogService.insert(lst);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC,"createDate");


        PageInfo<BizLog> page = elasticsearchBizLogService.page(null, pageable);
        List<BizLog> list = page.getContent();
        System.out.println("------------0---------------------");
        list.forEach(one->{
            System.out.println(one);
        });

        pageable = PageRequest.of(3, 10, Sort.Direction.DESC,"createDate");
        page = elasticsearchBizLogService.page(null, pageable);
        list = page.getContent();
        System.out.println("------------3---------------------");
        list.forEach(one->{
            System.out.println(one);
        });

        pageable = PageRequest.of(4, 10, Sort.Direction.DESC,"createDate");
        page = elasticsearchBizLogService.page(null, pageable);
        list = page.getContent();
        System.out.println("------------3---------------------");
        list.forEach(one->{
            System.out.println(one);
        });

        BizLogVo vo = new BizLogVo();
        vo.setCreaterId("createrId001");
        pageable = PageRequest.of(0, 10, Sort.Direction.DESC,"createDate");
        page = elasticsearchBizLogService.page(vo, pageable);
        list = page.getContent();
        System.out.println("------------====---------------------"+page.getTotalElements());
        list.forEach(one->{
            System.out.println(one);
        });

        vo = new BizLogVo();
        vo.setCompanyName("companyName002");
        pageable = PageRequest.of(0, 10, Sort.Direction.DESC,"createDate");
        page = elasticsearchBizLogService.page(vo, pageable);
        list = page.getContent();
        System.out.println("------------****---------------------"+page.getTotalElements());
        list.forEach(one->{
            System.out.println(one);
        });

    }
}