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

        var candle = {};
        candle.timestamp = DATE;
        candle.open = OPEN;
        candle.close = CLOSE;
        candle.high = HIGH;
        candle.low = LOW;
        candle.volume = VOLUME;

        var myJsonCandle = JSON.stringify(candle);

        xhr = new XMLHttpRequest();
        var url = "/candles";
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader( 'Authorization', 'Basic ' + btoa(  'admin:admin' ) );
        xhr.withCredentials = true;
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                 var json = JSON.parse(xhr.responseText);
                 console.log(json.toString())
            }
        }
        xhr.send(myJsonCandle);

    }
})();
