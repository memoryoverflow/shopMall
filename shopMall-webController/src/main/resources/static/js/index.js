/**
 * Created by Administrator on 2018/1/10.
 * form by 好好好先生
 * email 1570302023@qq.com
 *
 *
 *
 */
$(function () {
    $('.bot-img ul li').click(function () {
        var _this = $(this);
        _this.addClass('active').siblings('li').removeClass('active');
        var int = _this.index();
        $('.activeimg').animate({left: int * -430}, "slow");
    });
    var list = $('.bot-img ul li').length;
    $('.activeimg').css({
        width: list * 430,
    });
    $('.right').click(function () {
        next(list)

    })
    $('.left').click(function () {
        prev(list)
    });

    /*
    * 图片放大
    * */
    $(".smImg img").each(function (i) {
        $(".zoom:eq(0)").elevateZoom({
            zoomType: "inner",
            cursor: "crosshair",
            zoomWindowFadeIn: 500,
            zoomWindowFadeOut: 750
        })
        this.onclick = function () {
            $(".zoom:eq(" + i + ")").elevateZoom({
                zoomType: "inner",
                cursor: "crosshair",
                zoomWindowFadeIn: 500,
                zoomWindowFadeOut: 750
            })
        }
    })

})




