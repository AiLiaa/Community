$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});
function like(btn, entityType, entityId, entityUserId,postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : "赞");
                if (data.likeStatus == 1){
                    alert("点赞成功");
                }else {
                    alert("取消点赞");
                }
            } else {
                alert(data.msg);
            }
        }
    );
}
// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $("#topBtn").attr("disabled", "disabled");
                alert(data.msg);
            } else {
                //你没有访问此功能的权限
                alert(data.msg);
                // alert("服务器繁忙");
            }
        }
    );
}

// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
                alert(data.msg);
            } else {
                //你没有访问此功能的权限
                alert(data.msg);
                // alert("服务器繁忙");
            }
        }
    );
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
                alert(data.msg);
            } else {
                //你没有访问此功能的权限
                alert(data.msg);
                // alert("服务器繁忙");
            }
        }
    );
}