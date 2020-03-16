var app = angular.module('key_tool',['toastr', 'angular-loading-bar']);
app.controller('KeyToolController',function($scope, toastr, $http){

$scope.download = function() {
        $http({
            method:'POST',
            url:"/keyTool/create",
            responseType: "arraybuffer"
        }).success( function ( data ) {
                            var linkElement = document.createElement('a');
                            try {
                                var blob = new Blob([data], {type: "application/zip"});
                                var url = window.URL.createObjectURL(blob);
                                linkElement.setAttribute('href', url);
                                linkElement.setAttribute("download", "key.zip");
                                var clickEvent = new MouseEvent("click", {
                                    "view": window,
                                    "bubbles": true,
                                    "cancelable": false
                                });
                                linkElement.dispatchEvent(clickEvent);
                            } catch (ex) {
                                console.log(ex);
                            }
        });
    }

$scope.create = function() {
        $scope.serverConfig.key = "publickey";
        $http({
            method:'POST',
            url:"/server/config",
            dataType : "JSON",
            data:$scope.serverConfig,
        }).success( function ( data ) {
                  console.log(data)
                  $scope.serverConfig = data;
                  toastr.success("save success");
        }, function (result) {
                  toastr.error("save fail");
        });
    }
});