;(function () {

    var ws = new WebSocket('wss://api-pub.bitfinex.com/ws/2');

    window.Bitfinex = ws;

})();