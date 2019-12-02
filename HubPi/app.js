const http = require('http');

const PORT = 3000;

const hostname = '192.168.43.85';

var url = require('url');

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  var q = url.parse(req.url, true).query;
  console.log(req.url)
  console.log(q.request);
  
  var spawn = require("child_process").spawn;
  var process = spawn('python',["./nodeConnector.py", q.request, q.arg1, q.arg2, q.arg3]);

  var response = ""

  process.stdout.on('data', function(data) {
    response += data.toString();
    // res.end(data.toString());
    if (data.toString().split(" ").slice(-1)[0].includes("end") == true) {
      // console.log(response);
      res.end(response);
    }
  } )

  // res.end(response);
});

server.listen(PORT, hostname, () => {
  console.log(`Server running on port ${PORT}.`);
});
