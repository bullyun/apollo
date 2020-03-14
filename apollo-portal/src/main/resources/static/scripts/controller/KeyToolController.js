server_config_module.controller('KeyToolController',
    ['$scope', '$window', '$translate', 'toastr', 'KeyToolService', 'AppUtil',
        function ($scope, $window, $translate, toastr, KeyToolService, AppUtil) {

            $scope.create = function () {
                KeyToolService.create("").then(function (result) {

                 console.log(result);

//                 var linkElement = document.createElement('a');
//                 try {
//                     var blob = new Blob([result], {type: "application/zip"});
//                     var url = window.URL.createObjectURL(blob);
//                     linkElement.setAttribute('href', url);
//                     linkElement.setAttribute("download", "test.zip");
//                     var clickEvent = new MouseEvent("click", {
//                         "view": window,
//                         "bubbles": true,
//                         "cancelable": false
//                     });
//                     linkElement.dispatchEvent(clickEvent);
//                 } catch (ex) {
//                     console.log(ex);
//                 }

                    var keyData = Object.keys(result).reduce((string, current) => {
                    if (current == "$promise" || current == "$resolved") {
                        return string
                    }
                      return string += result[current]
                    }, '')
                    createDownload(stringToBuffer(keyData), 'key.zip')
                    toastr.success();
                }, function (result) {
                    toastr.error();
                });
            };
        }]);

function createDownload(stream, filename) {
  const a = document.createElement('a')
  var blob = new Blob([stream], {type: "application/zip"});
  a.href = window.URL.createObjectURL(blob)
  a.download = filename
  a.click()
  window.URL.revokeObjectURL(a.href)
}
function stringToBuffer (string) {
  const view = new Uint16Array(new ArrayBuffer(string.length * 2))
  for (let i = 0; i < string.length; i++) {
    view[i] = string.charCodeAt(i)
  }
  return view.buffer
}