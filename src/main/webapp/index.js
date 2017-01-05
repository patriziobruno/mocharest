print(require('./api.js'));
$mr.get('/test/{foo:int}?test1', function (request, response, parameters, pathParameters) {
    var deferred = new Deferred();
    deferred
            .done(function (p) {
                print(p);
                var pr = new Deferred();
                pr.resolve('pippo');
                return pr;
            })
            .fail(function (e) {
                print(e);
            })
            .done(function (p) {
                print(p);
                throw new Error();
            });
    deferred.resolve('ciao');
    return {testString: 'test', testInt: pathParameters['foo'], test: parameters['test1'] + parameters['test2']};
});

$mr.post('/test', function (request, response, parameters, pathParameters) {
    return request.getBody();
});

$mr.options('/test', function (request, response) {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
});