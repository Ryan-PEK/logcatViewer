'use strict';

angular.module('core').controller('HeaderController', ['$scope', '$rootScope',
	function($scope, $rootScope) {
		$scope.isCollapsed = false;

		$scope.toggleCollapsibleMenu = function() {
			$scope.isCollapsed = !$scope.isCollapsed;
		};

		// Collapsing the menu after navigation
		$scope.$on('$stateChangeSuccess', function() {
			$scope.isCollapsed = false;
		});

		$scope.refresh = function(){
			$('#page_refresh').click();
		};

		$rootScope.autoScroll = true;
        $scope.toggleScroll = function(){
        	$rootScope.autoScroll = !$rootScope.autoScroll;
        	console.log($rootScope.autoScroll);
        };
	}
]);