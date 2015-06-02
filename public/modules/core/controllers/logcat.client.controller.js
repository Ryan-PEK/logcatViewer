'use strict';

angular.module('core').controller('LogcatController', ['$scope', '$rootScope', '$http', '$interval',
    function($scope, $rootScope, $http, $interval) {
        $scope.records = [];
        $scope.getNextPage = function() {
            $http.get('/logcats/' + $rootScope.imei + '/' + $scope.pageId).success(function(response) {
                $scope.pageId = response.next_id;
                for (var index in response.page_content) {
                    $scope.records.push(response.page_content[index]);
                }
                if ($rootScope.autoScroll) {
                    $(window).scrollTop($(document).height());
                }
            });
        };

        $scope.loadMore = function() {
            // var last = $scope.images[$scope.images.length - 1];
            // for (var i = 1; i <= 8; i++) {
            //     $scope.images.push(last + i);
            // }
            if ($rootScope.imei !== '') {
                if (typeof $scope.pageId === 'undefined') {
                    $http.get('/firstPageID/' + $rootScope.imei).success(function(response) {
                        if ((response - 0) === 0) {
                            return false;
                        }
                        $scope.pageId = response - 100;
                        $scope.getNextPage();
                    });
                } else if ($scope.pageId <= 0) {
                    $scope.isEnd = true;
                } else {
                    $scope.getNextPage();
                }
            }
        };

        $interval($scope.loadMore, 2000);

        $scope.$watch(function() {
            return $rootScope.imei;
        }, function() {
            $scope.pageId = undefined;
            $scope.records = [];
        });
    }
]);
