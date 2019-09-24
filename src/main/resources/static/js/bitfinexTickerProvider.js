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

        }
    };

    function responseTicker(data) {

        console.log(" DATA-> " + data);

        var undefined = data[1][5];

        var LAST_PRICE = data[1][6];
        document.getElementById("lastPrice").innerHTML = LAST_PRICE.toFixed(2);

        var DAILY_CHANGE_PERC = data[1][5];
        const dailyChangeAsString = DAILY_CHANGE_PERC.toString();
        const splitString = dailyChangeAsString.split(".");
        if (dailyChangeAsString.charAt(0) === "-"){
            if (splitString[1].charAt(0) != 0){
                const formattedValue = "-" + splitString[1].charAt(0) + splitString[1].charAt(1) + "," + splitString[1].slice(2,splitString[1].length) + "%";
                document.getElementById("dailyChangePercentage").innerHTML = formattedValue;
                window.PercentageToday = formattedValue;
            } else {
                const formattedValue = "-" + splitString[1].charAt(1) + "," + splitString[1].slice(2,splitString[1].length) + "%";
                document.getElementById("dailyChangePercentage").innerHTML = formattedValue;
                window.PercentageToday = formattedValue;
            }

        } else {
            if (splitString[1].charAt(0) != 0){
                const formattedValue = splitString[1].charAt(0) + splitString[1].charAt(1) + "," + splitString[1].slice(2,splitString[1].length) + "%";
                document.getElementById("dailyChangePercentage").innerHTML = formattedValue;
                window.PercentageToday = formattedValue;
            } else {
                const formattedValue = splitString[1].charAt(1) + "," + splitString[1].slice(2,splitString[1].length) + "%";
                document.getElementById("dailyChangePercentage").innerHTML = formattedValue;
                window.PercentageToday = formattedValue;
            }
        }

        var HIGH = data[1][8];
        var LOW = data[1][9];


    }



})();
