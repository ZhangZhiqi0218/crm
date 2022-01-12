layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    var url=ctx+"/sale_chance/save";
    //对传来的是 修改 还是 添加 做出判断
    if($("input[name=id]").val()){
        url=ctx+"/sale_chance/update";
    }

    /**
     * 监听表单
     */
    form.on("submit(addOrUpdateSaleChance)",function (obj){

        $.ajax({
            type:"post",
            url:url,
            data:obj.field,
            dataType:"json",
            success:function (obj){
                if(obj.code==200){
                    layer.msg("添加成功了！");
                    window.parent.location.reload();
                }else{
                    layer.msg(obj.msg);
                }
            }
        });

       return false;
    });


    //取消功能
    $("#closeBtn").click(function (){
       var index = parent.layer.getFrameIndex(window.name);
       parent.layer.close(index);
    });

    //加载下拉框的内容

    var assignMan=$("input[name=man]").val();

    $.ajax({
        type:"post",
        url:ctx+"/user/sales",
        dataType:"json",
        success:function (data){
            for (var x in data) {
                if(data[x].id==assignMan){
                    $("#assignMan").append("<option selected value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }else{
                    $("#assignMan").append("<option value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }
            }
            layui.form.render("select");
        }
    })

})