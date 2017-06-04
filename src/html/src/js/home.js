var Home = function() {

    var loadFlg = false;

    this.getPage = function() {
        return parseInt($(".main-content").attr("data-page"));
    };
    this.setPage = function(page) {
        $(".main-content").attr("data-page",page);
    };
    this.getNextPage = function() {
        var page = this.getPage() + 1;
        return page;
    };
    this.ajax = function() {
        var page = this.getNextPage();
        var home = this;
        $.ajax({
            type: "GET",
            url: "/myapp",
            data: { page: page }
        }).done(function( html ) {
            History.pushState({page:page}, "", "?page=" + page);
            var addHtml = $(html).eq(8).html();
            //HTML追加位置
            var lastIndex = $(".main-content").size()-1;
            var afterObj = $(".main-content").eq(lastIndex);
            afterObj.after(addHtml);
            home.setPage(page);
            loadFlg = false;
        });
    };

    this.scrollEvent = function() {

        if(loadFlg) {
            return;
        }

        var height = $(".container").eq(1).height() + $(".container").eq(1).position().top;
        var loadHeight = height - 800;

        var scTop = $(window).scrollTop();

        //次ページの読込み
        if( loadHeight<=scTop ) {
            loadFlg = true;
            this.ajax();
        }
    }

}

$(document).ready(function(){
    var home = new Home();

    $(window).scroll(function(){
        home.scrollEvent();
    });

    var webStrage = new WebStrage();

    if(webStrage.getItem("sortParm")!=null && webStrage.getItem("sortParm")!="null") {

    } else {
        var map = new JapanMap();
        map.show();
    }



});

$(document).on("click",".kisyu-link" , function() {
    var webStrage = new WebStrage();
    var href = $(this).data("href");
    var sort = webStrage.getItem("sortParm");

    var link = href + "&t=" + sort;
    location.href = link;
})