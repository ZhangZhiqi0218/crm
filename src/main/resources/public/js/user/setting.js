layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /*
        监听表单提交
     */
    form.on("submit(saveBtn)",function (data){

        var fieldData = data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/setting",
            data:{
                userName:fieldData.userName,
                phone:fieldData.phone,
                email:fieldData.email,
                trueName:fieldData.trueName,
                id:fieldData.id
            },
            dataType:"json",
            success:function (msg){
                if(msg.code==200){
                    layer.msg("修改成功",function (){
                        //清空cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //页面跳转
                        window.parent.location.href=ctx+"/index";
                    });
                }else {
                    layer.msg(msg.msg);
                }
            }
        })

        return false;
    });
});