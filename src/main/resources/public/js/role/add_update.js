layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    form.on("submit(addOrUpdateRole)",function (data) {
        var url = ctx+"/role/save";
        if($("input[name=id]").val()){
            url=ctx+"/role/update";
        }
        //发送ajax
        $.post(url,data.field,function(result){
            if(result.code==200){
                layer.msg("操作成功！")
                parent.location.reload();
            }else{
                layer.msg(result.msg,{icon:5});
            }
        },"json");
        return false;
    });

    //取消
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index)
    })

});
