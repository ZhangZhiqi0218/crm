package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.security.PublicKey;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    /**
     *  查询所有用户信息
     * @return
     */
    public List<Map<String,Object>> findAllRoles(Integer userId){
        return roleMapper.selectAllRoles(userId);
    }

    /**
     *  角色的条件查询
     * @param roleQuery
     * @return
     */
    public Map<String,Object> findRoleByParam(RoleQuery roleQuery){
        Map<String,Object> map = new HashMap<>();

        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> rlist = new PageInfo<>(selectByParams(roleQuery));

        map.put("code",0);
        map.put("msg","success");
        map.put("count",rlist.getTotal());
        map.put("data",rlist.getList());

        return map;
    }

    /**
     * 添加角色
     * @param role
     */
    public void savaRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名！");
        Role temp = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(null!=temp,"该角色已存在");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(insertHasKey(role)<1,"添加记录失败");
    }

    /**
     *  修改角色
     * @param role
     */
    public void updateRole(Role role){
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(null==temp,"修改角色不存在");
        Role temp2 = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(null!=temp2 && !(temp2.getId().equals(role.getId())),"该角色已存在");
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"修改角色信息失败");
    }

    /**
     *  删除角色
     * @param role
     */
    public void removeRoleId(Role role) {
        AssertUtil.isTrue(role.getId()==null||selectByPrimaryKey(role.getId())==null,"请选择要删除的数据");
        role.setIsValid(0);
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"删除失败了");

    }

    /**
     *  赋予授权
     *  如果原来存在权限，删除，赋予新的权限
     * @param roleId
     * @param mids
     */
    public void addGrant(Integer roleId,Integer[] mids){
        //验证角色存在与否
        Role role = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId || null==role,"待授权的角色不存在！");
        //统计当前角色原来有多少权限
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            //删除当前角色存在的权限
            AssertUtil.isTrue(permissionMapper.deletePermissionByRoleId(roleId)!=count,"权限分配失败！");
        }
        //存放信息
        if(null!=mids && mids.length>0){
            List<Permission> permissions = new ArrayList<>();
            for (Integer mid : mids) {
                Permission permission = new Permission();
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //获取权限码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissions.add(permission);
            }
            AssertUtil.isTrue(permissionMapper.insertBatch(permissions)!=permissions.size(),"授权失败！");
        }
    }

}

