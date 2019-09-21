;(function () {

    Bitfinex.onopen = function() {
        console.log("Collecting tickers...");
        Bitfinex.send(JSON.stringify({"event":"subscribe", "channel":"ticker", "symbol":"tBTCUSD"}));
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

        var DAILY_CHANGE_PERC = data[1][5];
        var LAST_PRICE = data[1][6];
        var VOLUME = data[1][7];
        var HIGH = data[1][8];
        var LOW = data[1][9];

        document.getElementById("btc").innerHTML = LAST_PRICE.toFixed(2);

    }
})();
