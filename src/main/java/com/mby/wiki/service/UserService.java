package com.mby.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mby.wiki.aspect.LogAspect;
import com.mby.wiki.domain.User;
import com.mby.wiki.domain.UserExample;
import com.mby.wiki.mapper.UserMapper;
import com.mby.wiki.req.UserQueryReq;
import com.mby.wiki.req.UserSaveReq;
import com.mby.wiki.resp.UserQueryResp;
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
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public PageResp<UserQueryResp> list(UserQueryReq req){



        //domain下的example mybaits自动生成了很多方法
        UserExample userExample = new UserExample();
        //当作where语句
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(),req.getSize());//只会分页最近的需要查询的sql，当页面多条sql时 把分页和sql放一起
        List<User> userList = userMapper.selectByExample(userExample);//查询到所有的User实体

        PageInfo<User> pageInfo=new PageInfo<>(userList);
        LOG.info("页数:{}",pageInfo.getPages());
        LOG.info("行数：{}",pageInfo.getTotal());
//        List<UserResp> respList=new ArrayList<>();
        //遍历所有的User属性给UserResp 并过滤掉不需要返回的属性
//        for (User e:userList) {
//            UserResp userResp=new UserResp();
//            BeanUtils.copyProperties(e,userResp);
//            UserResp userResp = CopyUtil.copy(e, UserResp.class);
//            respList.add(userResp);
//        }
        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req){
        User user=CopyUtil.copy(req,User.class);
        if(ObjectUtils.isEmpty(user.getId())){
            //新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }else{
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        //删除指定id的数据
        userMapper.deleteByPrimaryKey(id);//deleteByPrimaryKey根据主键来删除。

    }
}
