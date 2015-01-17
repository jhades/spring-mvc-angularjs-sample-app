///////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Defines the javascript files that need to be loaded and their dependencies.
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////

require.config({
    paths: {
        angular: '../bower_components/angular/angular',
        angularMessages: '../bower_components/angular-messages/angular-messages',
        csrfInterceptor: '../bower_components/spring-security-csrf-token-interceptor/dist/spring-security-csrf-token-interceptor.min',
        lodash: "../bower_components/lodash/dist/lodash",
        jQuery: "./datetimepicker/jquery", // needed only by the date time picker
        datetimepicker: './datetimepicker/jquery.datetimepicker',
        editableTableWidgets: '../public/js/editable-table-widgets',
        frontendServices: 'frontend-services',
        caloriesCounterApp: "calories-counter-app"
    },
    shim: {
        jQuery: {
            exports: "jQuery"
        },
        angular: {
            exports: "angular"
        },
        csrfInterceptor: {
            deps: ['angular']
        },
        datetimepicker: {
            deps: ['jQuery']
        },
        angularMessages: {
            deps: ['angular']
        },
        editableTableWidgets: {
            deps: ['angular', 'lodash', 'datetimepicker', 'jQuery']
        },
        frontendServices: {
            deps: ['angular', 'lodash', 'csrfInterceptor']
        },
        caloriesCounterApp: {
            deps: [ 'lodash', 'angular', 'angularMessages', 'editableTableWidgets' , 'frontendServices']
        }
    }
});

require(['caloriesCounterApp'], function () {

    angular.bootstrap(document.getElementById('caloriesCounterApp'), ['caloriesCounterApp']);

});