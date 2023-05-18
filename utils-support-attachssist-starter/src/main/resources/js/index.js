$(function () {
    //菜单点击
    J_iframe
    let hash = window.location.hash || "#about";
    $(".J_menuItem").on('click', function () {
        var url = $(this).attr('href');
        var id = $(this).attr('id');
        window.location.hash = "#" + id;
        $("#J_iframe").attr('src', url);
        return false;
    });
    $(hash).click();
});