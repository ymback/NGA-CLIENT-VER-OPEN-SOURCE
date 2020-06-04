function toggleCollapse(button, text) {
    var collapse = button.parentElement.getElementsByTagName("div")[0];

    if (collapse.style.display == 'none') {
        collapse.style.display = '';
        button.innerHTML = button.innerHTML.replace("显示", "隐藏");
    } else {
        collapse.style.display = 'none';
        button.innerHTML = button.innerHTML.replace("隐藏", "显示");
    }
}