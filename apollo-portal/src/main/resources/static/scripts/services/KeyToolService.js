appService.service('KeyToolService', ['$resource', '$q', function ($resource, $q) {
    var key_tool_resource = $resource('', {}, {
        create_key: {
            method: 'POST',
            url: '/keyTool/create'
//            responseType: 'blob'
        }
    });
    return {
        create: function () {
            var d = $q.defer();
            key_tool_resource.create_key({}, "" , function (result, headers) {
                             console.log("111： " + result);
//                             headers = headers();
//                             var contentType = headers['content-type'];
//                             var linkElement = document.createElement('a');
//                             try {
//                                 var blob = new Blob([result], {type: contentType});
//                                 var url = window.URL.createObjectURL(blob);
//                                 linkElement.setAttribute('href', url);
//                                 linkElement.setAttribute("download", "test");
//                                 var clickEvent = new MouseEvent("click", {
//                                     "view": window,
//                                     "bubbles": true,
//                                     "cancelable": false
//                                 });
//                                 linkElement.dispatchEvent(clickEvent);
//                             } catch (ex) {
//                                 console.log(ex);
//                             }
                d.resolve(result, headers);
//                d.resolve(headers);
            }, function (result) {
                d.reject(result);
            });
            return d.promise;
        }
    }
}]);

