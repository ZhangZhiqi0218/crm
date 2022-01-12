var zTreeObj;
$(function () {
    loadModuleInfo();
});


function loadModuleInfo(){
    $.ajax({
        type: "post",
        url: ctx + "/module/findModules",
        dataType: "json",
        data:{"roleId":$("#roleId").val()},
        success: function (datas) {
            var setting = {
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                view: {
                    showLine: false
                },
                check: {
                    enable: true,
                },
                callback: {
                    onCheck: zTreeOnCheck
                }
            };
            var zNodes = datas;
            zTreeObj = $.fn.zTree.init($("#test1"), setting, zNodes);
        },

    });
}

function zTreeOnCheck(event, treeId, treeNode) {
    var nodes= zTreeObj.getCheckedNodes(true);
    var roleId=$("#roleId").val();
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+roleId,
        dataType:"json",
        success:function (data) {
            alert("授权成功了！");
        }
    })
}
