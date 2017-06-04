$(document).ready(function() {
    var parm = GetQueryString();
    $('[data-price="' + parm.rate + '"] a').click();
});