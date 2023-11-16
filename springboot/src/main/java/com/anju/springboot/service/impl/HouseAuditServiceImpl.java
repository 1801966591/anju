package com.anju.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.HouseAudit;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.HouseAuditVo;
import com.anju.springboot.mapper.HouseAuditMapper;
import com.anju.springboot.mapper.HouseMapper;
import com.anju.springboot.mapper.UserMapper;
import com.anju.springboot.service.HouseAuditService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 房源审核表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-29
 */
@Service
public class HouseAuditServiceImpl extends ServiceImpl<HouseAuditMapper, HouseAudit> implements HouseAuditService {
    @Autowired
    private HouseAuditMapper auditMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result search(Param param) {
        //每页显示的信息数
        Integer pageSize = param.getPageSize();
        //当前页数
        Integer pageNo = (param.getCurrentPage() - 1) * pageSize;
        HouseAuditVo vo = new HouseAuditVo();
        List<HouseAuditVo> list = new ArrayList<>();
        List<House> houses = houseMapper.getByName(param.getName(),pageNo,pageSize);
        int total = houseMapper.getTotalByName(param.getName());
        if (!houses.isEmpty()){
            for (House house : houses){
                HouseAuditVo houseAuditVo = new HouseAuditVo();
                HouseAudit audit = auditMapper.getByHouseId(house.getId());
                BeanUtils.copyProperties(audit,houseAuditVo);
                Integer userId = house.getAuthorId();
                User user = userMapper.selectById(userId);
                houseAuditVo.setAuthor(user.getRealName());
                houseAuditVo.setName(house.getName());
                list.add(houseAuditVo);
            }
        }
        vo.setRecords(list);
        vo.setTotal(total);
        return Result.success(vo);
    }
}
