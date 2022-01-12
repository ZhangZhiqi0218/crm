package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 展示所有权限信息
     * @return
     */
    public List<TreeDto> findModules(){
       return moduleMapper.selectModules();
    }

    /**
     *  根据角色id查找modules
     * @param roleId
     * @return
     */
    public List<TreeDto> findModulesByRoleId(Integer roleId){
        List<TreeDto> tlist = moduleMapper.selectModules();
        List<Integer> roleHasMids = permissionMapper.selectModuleByRoleId(roleId);

        for (TreeDto treeDto : tlist) {
            if (roleHasMids.contains(treeDto.getId())) {
                treeDto.setChecked(true);
            }
        }
        return tlist;
    }

    /**
     *  查询菜单管理信息
     * @return
     */
    public Map<String,Object> moduleList(){
        Map<String,Object> result = new HashMap<String,Object>();
        List<Module> modules =moduleMapper.queryModules();
        result.put("count",modules.size());
        result.put("data",modules);
        result.put("code",0);
        result.put("msg","");
        return result;
    }

}
