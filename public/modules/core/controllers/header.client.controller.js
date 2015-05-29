'use strict';

angular.module('core').controller('HeaderController', ['$scope', '$rootScope','Authentication', 'Menus',
	function($scope, $rootScope, Authentication, Menus) {
		$scope.authentication = Authentication;
		$scope.isCollapsed = false;
		$scope.menu = Menus.getMenu('topbar');

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

		$rootScope.autoScroll = false;
        $scope.toggleScroll = function(){
        	$rootScope.autoScroll = !$rootScope.autoScroll;
        	console.log($rootScope.autoScroll);
        };
	}
]);