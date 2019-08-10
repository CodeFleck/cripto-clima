;(function () {

    Bitfinex.onopen = function() {
        Bitfinex.send(JSON.stringify({"event":"subscribe", "channel":"candles", "key":"trade:1m:tBTCUSD"}));
    };

    Bitfinex.onmessage = function(msg) {

        var response = JSON.parse(msg.data);

        var hb = response[1];
        if(hb != "hb") {

            var channelId = response[0];
            responseTicker(response);

        } else {
            console.log(response);
        }
    };

    function responseTicker(data) {

        var DATE = data[1][0]
        var OPEN = data[1][1];
        var CLOSE = data[1][2];
        var HIGH = data[1][3];
        var LOW = data[1][4];
        var VOLUME = data[1][5];

        document.getElementById("btc").innerHTML = CLOSE.toFixed(2);
    }

})();
