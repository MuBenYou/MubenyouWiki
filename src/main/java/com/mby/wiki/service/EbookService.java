package com.mby.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mby.wiki.aspect.LogAspect;
import com.mby.wiki.domain.Ebook;
import com.mby.wiki.domain.EbookExample;
import com.mby.wiki.mapper.EbookMapper;
import com.mby.wiki.req.EbookQueryReq;
import com.mby.wiki.req.EbookSaveReq;
import com.mby.wiki.resp.EbookQueryResp;
import com.mby.wiki.resp.PageResp;
import com.mby.wiki.utils.CopyUtil;
import com.mby.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public PageResp<EbookQueryResp> list(EbookQueryReq req){



        //domain下的example mybaits自动生成了很多方法
        EbookExample ebookExample = new EbookExample();
        //当作where语句
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){//不为空才执行
            criteria.andNameLike("%"+req.getName()+"%"); //模糊查询的条件
        }
        PageHelper.startPage(req.getPage(),req.getSize());//只会分页最近的需要查询的sql，当页面多条sql时 把分页和sql放一起
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);//查询到所有的Ebook实体

        PageInfo<Ebook> pageInfo=new PageInfo<>(ebookList);
        LOG.info("页数:{}",pageInfo.getPages());
        LOG.info("行数：{}",pageInfo.getTotal());
//        List<EbookResp> respList=new ArrayList<>();
        //遍历所有的Ebook属性给EbookResp 并过滤掉不需要返回的属性
//        for (Ebook e:ebookList) {
//            EbookResp ebookResp=new EbookResp();
//            BeanUtils.copyProperties(e,ebookResp);
//            EbookResp ebookResp = CopyUtil.copy(e, EbookResp.class);
//            respList.add(ebookResp);
//        }
        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        PageResp<EbookQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(EbookSaveReq req){
        Ebook ebook=CopyUtil.copy(req,Ebook.class);
        if(ObjectUtils.isEmpty(ebook.getId())){
            //新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }else{
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        //删除指定id的数据
        ebookMapper.deleteByPrimaryKey(id);//deleteByPrimaryKey根据主键来删除。

    }
}
