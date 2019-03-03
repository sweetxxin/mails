function getObjectURL(file) {
    var url = null;
    if (window.createObjcectURL != undefined) {
        url = window.createOjcectURL(file);
    } else if (window.URL != undefined) {
        url = window.URL.createObjectURL(file);
    } else if (window.webkitURL != undefined) {
        url = window.webkitURL.createObjectURL(file);
    }
    return url;
}
function getFileName(file) {
    var from = file.lastIndexOf("/");
    return   file.substring(from+1,file.length);
}
function add0(m){return m<10?'0'+m:m }
function format(shijianchuo) {
    var time = new Date(shijianchuo);
    var y = time.getFullYear();
    var m = time.getMonth()+1;
    var d = time.getDate();
    var h = time.getHours();
    var mm = time.getMinutes();
    var s = time.getSeconds();
    return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
}
function getDetail(mid,from,to) {
    $(this).parent().removeClass("unSee");
    window.location = "detail.jsp?mid="+mid+"&from="+from+"&to="+to;
}
function dragFunc(id) {
    var Drag = document.getElementById(id);
    Drag.onmousedown = function(event) {
        var ev = event || window.event;
        event.stopPropagation();
        var disX = ev.clientX - Drag.offsetLeft;
        var disY = ev.clientY - Drag.offsetTop;
        document.onmousemove = function(event) {
            var ev = event || window.event;
            Drag.style.left = ev.clientX - disX + "px";
            Drag.style.top = ev.clientY - disY + "px";
            Drag.style.cursor = "move";
        };
    };
    Drag.onmouseup = function() {
        document.onmousemove = null;
        this.style.cursor = "default";
    };
};
function reply(mid,from,to,time) {
    window.location = "detail.jsp?mid="+mid+"&from="+from+"&to="+to+"&type=reply&time="+time;
}
function forward(mid,from,to,time) {
    window.location = "detail.jsp?mid="+mid+"&from="+from+"&to="+to+"&type=forward&time="+time;
}