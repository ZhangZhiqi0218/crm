layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
    // 引入 formSelects 模块
        formSelects = layui.formSelects;

    /**
     * 加载下拉框数据
     */
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/findRoles?userId="+$("input[name=id]").val(),
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);


    /**
     * 添加或更新用户
     */

    //表单触发
    form.on("submit(addOrUpdateUser)",function (data) {

        var url=ctx+"/user/save";
        //先判断是添加还是修改
        if($("input[name=id]").val()){
            url=ctx+"/user/update";
        }

        //发送ajax
        $.post(url,data.field,function(result){

            if(result.code==200){

                //刷新页面
                parent.location.reload();
            }else{
                layer.msg(result.msg,{icon:5});
            }
        },"json");
        //取消默认跳转
        return false;
    });


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

});

