package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private UserService userService;

    /**
     *  修改数据
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdate(Integer id , Model model){
        if(id!=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            model.addAttribute("saleChance",saleChance);
        }

        return "saleChance/add_update";
    }

    /**
     *  访问页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     *  列表查询
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(SaleChanceQuery saleChanceQuery){
        //调用service中的方法
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        //返回数据，将map转化为json
        return map;
    }

    /**
     *  增加营销机会信息
     * @param request
     * @param saleChance
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        //通过request作用域获取cookie中的userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //通过userId可以找到trueName
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        //调用service中的方法
        saleChanceService.addSaleChance(saleChance);
        return success("添加数据成功了！");
    }

    /**
     *  修改营销机会信息
     * @param saleChance
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){
        //调用service中的方法
        saleChanceService.changeSaleChance(saleChance);
        return success("修改数据成功了！");
    }

    /**
     *  批量删除营销机会信息
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChances(Integer[] ids){
        //调用service中的方法
        saleChanceService.removeSaleChanceIds(ids);
        return success("批量删除数据成功了！");
    }

}
