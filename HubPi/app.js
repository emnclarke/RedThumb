// Setting up server settings
const http = require('http');
const PORT = 3000;
const hostname = '192.168.43.85';
var url = require('url');

// Configuring server response
const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  var q = url.parse(req.url, true).query; // Parsing url to get args
  console.log(req.url)
  console.log(q.request);
  
  var spawn = require("child_process").spawn;
  // Executing python connector. q holds the arguments passed in from the url
  var process = spawn('python',["./nodeConnector.py", q.request, q.arg1, q.arg2, q.arg3, q.arg4, q.arg5, q.arg6, q.arg7, q.arg8]);

  var response = ""

  // Listens for output from the python script
  process.stdout.on('data', function(data) {
    // adds python output to response
    response += data.toString();
    // when end is reached (signified by "end" at end of python output), sends response to
    // android app
    if (data.toString().split(" ").slice(-1)[0].includes("end") == true) {
      res.end(response);
    }
  } )
});

// Creating server listner
server.listen(PORT, hostname, () => {
  console.log(`Server running on port ${PORT}.`);
});
