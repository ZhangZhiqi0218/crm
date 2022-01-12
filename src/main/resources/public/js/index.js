layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(login)', function (data) {
        var fieldData = data.field;

        if (fieldData.username == 'undefined' || fieldData.username.trim() == '') {
            layer.msg("用户名不能为空")
            return false;
        }
        if (fieldData.password == 'undefined' || fieldData.password.trim() == '') {
            layer.msg("密码不能为空")
            return false;
        }

        //发送ajax
        $.ajax({
            type: "post",
            url: ctx + "/user/login",
            data: {
                "userName": fieldData.username,
                "userPwd": fieldData.password
            },
            dataType: "json",
            success: function (data) {
                //resultInfo
                if (data.code == 200) {
                    layer.msg("登录成功！", function () {
                        // 将用户信息存到cookie中
                        var result = data.result;
                        $.cookie("userIdStr", result.userIdStr);
                        $.cookie("userName", result.userName);
                        $.cookie("trueName", result.trueName);

                        //是否选中记住我
                        if($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr", result.userIdStr,{expires:7});
                            $.cookie("userName", result.userName,{expires:7});
                            $.cookie("trueName", result.trueName,{expires:7});
                        }
                        // 登录成功后，跳转到首页
                        window.location.href = ctx + "/main";
                    });
                } else {
                    //失败提示
                    layer.msg(data.msg);
                }
            }
        });

        //取消默认行为
        return false;
    });
});