package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Menu;
import com.anju.springboot.entity.Role;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.UserRole;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.UserVo;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.RoleMapper;
import com.anju.springboot.mapper.RoleMenuMapper;
import com.anju.springboot.mapper.UserMapper;
import com.anju.springboot.mapper.UserRoleMapper;
import com.anju.springboot.service.MenuService;
import com.anju.springboot.service.UserService;
import com.anju.springboot.utils.HashUtils;
import com.anju.springboot.utils.JwtTokenUtils;
import com.anju.springboot.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private HashUtils hashUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public Result register(Param param) throws Exception {

        //判断用户名是否存在
        User user1 = userMapper.getByUserName(param.getUsername());

        if (ObjectUtil.isNotNull(user1)){
            //说明用户名已存在
            throw new CustomException("该用户名已存在！");
        }

        User user = new User();
        user.setUsername(param.getUsername());
        user.setPassword(hashUtils.sha256Hash(param.getPassword(),null));
        user.setSalt(redisUtil.get("Anju_salt").toString());
        user.setAvatar(param.getAvatar());
        user.setGender(param.getGender());
        user.setNickname(param.getNickname());
        user.setRealName(param.getRealName());
        user.setEmail(param.getEmail());
        user.setMobile(param.getMobile());
        user.setStatus(1);
        user.setCreateTime(DateUtil.now());

        int i = userMapper.insert(user);

        //将用户与角色关联
        User user2 = userMapper.getByUserName(user.getUsername());
        LambdaQueryWrapper<Role> queryWrapper1 = new LambdaQueryWrapper<>();
        if (Objects.equals(param.getRole(), Constant.ROLE_USER)) {
            queryWrapper1.eq(Role::getCode, Constant.ROLE_USER);
        } else {
            queryWrapper1.eq(Role::getCode, Constant.ROLE_LANDLORD);
        }
        Role role = roleMapper.selectOne(queryWrapper1);
        UserRole userRole = new UserRole();
        userRole.setUserId(user2.getId());
        userRole.setRoleId(role.getId());
        i += userRoleMapper.insert(userRole);

        if (i == 2){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public Result sendEmail(Param param) {

        String username = param.getUsername();
        String email = param.getEmail();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null){
            throw new CustomException("用户名不存在");
        }else {
            if (user.getEmail().equals(email)) {

                if(redisUtil.hasKey("AnJu_findBack_code") && redisUtil.hasKey("AnJu_findBack_username") && redisUtil.get("AnJu_findBack_username").toString().equals(param.getUsername())){
                    //如果验证码已经存在，而且属于同一个用户发送，则不能重复发送
                    throw new CustomException("当前验证码仍然有效，请不要重复发送");
                } else {
                    //验证码失效，或者是另外一个用户需要发送验证码
                    //生成随机4位验证码
                    String code = RandomUtil.randomNumbers(4);

                    //将验证码和验证码的用户名存到Redis数据库，并设置有效期5分钟
                    redisUtil.set("AnJu_findBack_code", code);
                    redisUtil.expire("AnJu_findBack_code",300);
                    redisUtil.set("AnJu_findBack_username", param.getUsername());
                    redisUtil.expire("AnJu_findBack_username",300);

                    //发送验证码
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(from);
                    message.setTo(email);
                    message.setSentDate(new Date());
                    message.setText("亲爱的"+ user.getNickname() +"，您本次找回密码的验证码是： " + code + "，验证码有效期为5分钟，打死也不要告诉其他人哦！");
                    message.setSubject("【安居找房】找回密码邮箱验证");
                    javaMailSender.send(message);
                    return Result.success();
                }
            }else {
                throw new CustomException("邮箱输入错误，请重新输入");
            }
        }
    }

    @Override
    public Result findBackPassword(Param param) throws Exception {

        if (redisUtil.hasKey("AnJu_findBack_code")) {
            String trueCode = redisUtil.get("AnJu_findBack_code").toString();
            if (trueCode.equals(param.getCode())) {

                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getUsername,param.getUsername());
                User user = userMapper.selectOne(queryWrapper);

                user.setPassword(hashUtils.sha256Hash(param.getPassword(),null));
                user.setSalt(redisUtil.get("Anju_salt").toString());
                int i = userMapper.updateById(user);

                if (i == 1){
                    redisUtil.del("AnJu_findBack_code");
                    return Result.success();
                }else {
                    throw new Exception();
                }

            } else {
                throw new CustomException("验证码输入错误，请重新输入");
            }
        } else {
            throw new CustomException("验证码已失效，请重新发送");
        }
    }

    @Override
    public Result login(Param param){

        User user = userMapper.getByUserName(param.getUsername());

        if (hashUtils.isPasswordValid(param.getPassword(), user.getPassword(),user.getSalt())) {
            if (user.getStatus() == 1) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);

                // 生成jwt token给前端
                String token = JwtTokenUtils.genToken(user.getId().toString(), user.getPassword());
                userVo.setToken(token);

                //如果登录用户角色不是普通用户，则需要返回对应角色信息和对应的菜单信息
                if (!Objects.equals(param.getRole(), Constant.ROLE_USER)) {

                    //获取角色信息
                    LambdaQueryWrapper<UserRole> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(UserRole::getUserId, user.getId());
                    UserRole userRole = userRoleMapper.selectOne(queryWrapper1);
                    Integer roleId = userRole.getRoleId();
                    Role role = roleMapper.selectById(roleId);
                    userVo.setRole(role.getCode());

                    //查询所有菜单信息
                    List<Menu> menus = menuService.getMenus("");
                    //将所有菜单信息存入返回的数据中
                    userVo.setAllMenus(menus);

                    //获取当前角色的菜单列表
                    List<Menu> roleMenu = getRoleMenu(roleId);
                    //将筛选出的菜单信息存入返回的数据中
                    userVo.setMenu(roleMenu);
                }
                return Result.success(userVo);
            } else {
                throw new CustomException("该账号已禁用");
            }
        } else {
            throw new CustomException("密码输入错误");
        }


    }

    @Override
    public Result getByCondition(Param param) {
        String username = param.getUsername();
        String nickname = param.getNickname();
        String realName = param.getRealName();

        Integer currentPage = param.getCurrentPage();
        Integer pageSize = param.getPageSize();
        Integer pageNo = (currentPage - 1) * pageSize;

        int total = userMapper.getTotalByCondition(username,nickname,realName,pageNo,pageSize);
        List<UserVo> list = userMapper.getByCondition(username,nickname,realName,pageNo,pageSize).stream()
                .map((item)->{
                    //根据用户id获取对应角色信息
                    Integer id = item.getId();
                    QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",id);
                    UserRole userRole = userRoleMapper.selectOne(queryWrapper);
                    Integer roleId = userRole.getRoleId();
                    Role role = roleMapper.selectById(roleId);
                    item.setRole(role.getName());
                    return item;
                }).collect(Collectors.toList());

        UserVo userVo = new UserVo();
        userVo.setRecords(list);
        userVo.setTotal(total);

        return Result.success(userVo);
    }

    @Override
    public Result saveUser(Param param) throws Exception {
        User user = new User();
        BeanUtils.copyProperties(param,user);
        user.setCreateTime(DateUtil.now());
        user.setStatus(1);
        //设置默认密码为123456
        user.setPassword(hashUtils.sha256Hash("123456",null));


        int i = userMapper.insert(user);

        //关联角色
        User one = userMapper.getByUserName(param.getUsername());
        UserRole userRole = new UserRole();
        userRole.setUserId(one.getId());
        userRole.setRoleId(param.getRoleId());
        i += userRoleMapper.insert(userRole);

        if (i > 1){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public Result disableAdminByIds(List<Integer> ids) throws Exception {

        int i = 0;

        for (Integer id : ids){
            LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRole::getUserId,id);
            UserRole userRole = userRoleMapper.selectOne(queryWrapper);
            Role role = roleMapper.selectById(userRole.getRoleId());
            if (role.getCode().equals(Constant.ROLE_ADMIN)) {
                throw new CustomException("当前选中的用户中有管理员，不能禁用");
            } else {
                User user = userMapper.selectById(id);
                if (user.getStatus() == 0){
                    user.setStatus(1);
                } else {
                    user.setStatus(0);
                }
                i += userMapper.updateById(user);
            }
        }

        if (i > 0) {
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @Override
    public Result updatePassword(Param param) throws Exception {

        //判断登录是否失效
        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNotNull(user)){
            //判断旧密码是否正确
            String password = user.getPassword();
            if (hashUtils.isPasswordValid(param.getOldPassword(),password,user.getSalt())){
                //旧密码正确
                String newPassword = hashUtils.sha256Hash(param.getNewPassword(),null);
                user.setPassword(newPassword);
                user.setSalt(redisUtil.get("Anju_salt").toString());
                int i = userMapper.updateById(user);
                if (i == 1){
                    return Result.success();
                } else {
                    throw new Exception();
                }
            } else {
                throw new CustomException("旧密码输入错误");
            }
        }else {
            //登录失效
            throw new TokenException("登录失效");
        }
    }

    @Override
    public Result getTotalExistAdmin() {

        return Result.success(userMapper.getTotalExistAdmin());

    }

    /**
     * 获取当前角色的菜单列表
     * @param roleId
     * @return
     */
    private List<Menu> getRoleMenu(Integer roleId){
        //获取该角色的菜单id
        List<Integer> menuIds = roleMenuMapper.getMenuIdsByRoleId(roleId);

        //查询所有菜单信息
        List<Menu> menus = menuService.getMenus("");

        //最后筛选完成之后的菜单信息
        List<Menu> roleMenu = new ArrayList<>();

        //筛选当前用户角色的菜单
        for(Menu menu : menus){
            if (menuIds.contains(menu.getId())) {
                roleMenu.add(menu);
            }
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenu;
    }
}
