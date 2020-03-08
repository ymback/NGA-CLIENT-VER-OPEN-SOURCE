
function toggleCollapse(id, text) {
    var collapse = document.getElementById('collapse'+id);
    var button = document.getElementById('collapseBtn'+id);
    if (collapse.style.display == 'none') {
        collapse.style.display = '';
        if (text == null) {
            button.innerHTML = "点击隐藏内容"
        } else {
            button.innerHTML = "点击隐藏内容 : " + text;
        }
    } else {
        collapse.style.display = 'none';
        if (text == null) {
            button.innerHTML = "点击显示内容"
        } else {
            button.innerHTML = "点击显示内容 : " + text;
        }
    }
}