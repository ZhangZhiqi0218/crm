layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    //监听提交
    form.on('submit(saveBtn)', function(data){

        var fieldData = data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePassword",
            data:{
                "oldPassword":fieldData.old_password,
                "newPassword":fieldData.new_password,
                "confirmPassword":fieldData.again_password
            },
            dataType:"json",
            success:function (data){
                if(data.code==200){
                    layer.msg("修改密码成功了，系统三秒后退出",function (){
                        //清楚cookie信息
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("TrueName",{domain:"localhost",path:"/crm"});
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    })
                }else{
                    layer.msg(data.msg);
                }
            }
        })

        return false;
    });

});