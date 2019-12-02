const http = require('http');

const hostname = '127.0.0.1';
const port = 3000;


http.createServer(function (req, res) {
    ///?request=
    res.writeHead(200, {'Content-Type': 'text/html'});
    var q = url.parse(req.url, true).query;
    //Get pot list
    var requestText = q.request;
    //Get pot request
    res.end(txt);
}).listen(8080);

server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);
});
