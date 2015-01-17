angular.module('caloriesCounterApp', ['editableTableWidgets', 'frontendServices', 'spring-security-csrf-token-interceptor'])
    .filter('excludeDeleted', function () {
        return function (input) {
            return _.filter(input, function (item) {
                return item.deleted == undefined || !item.deleted;
            });
        }
    })
    .controller('CaloriesTrackerCtrl', ['$scope' , 'MealService', 'UserService', '$timeout',
        function ($scope, MealService, UserService, $timeout) {

            $scope.vm = {
                maxCaloriesPerDay: 2000,
                currentPage: 1,
                totalPages: 0,
                originalMeals: [],
                meals: [],
                isSelectionEmpty: true,
                errorMessages: [],
                infoMessages: []
            };

            updateUserInfo();
            loadMealData(null, null, null, null, 1);


            function showErrorMessage(errorMessage) {
                clearMessages();
                $scope.vm.errorMessages.push({description: errorMessage});
            }

            function updateUserInfo() {
                UserService.getUserInfo()
                    .then(function (userInfo) {
                        $scope.vm.userName = userInfo.userName;
                        $scope.vm.maxCaloriesPerDay = userInfo.maxCaloriesPerDay;
                        $scope.vm.todaysCalories = userInfo.todaysCalories ? userInfo.todaysCalories : 'None';
                        updateCaloriesCounterStatus();
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                    });
            }

            function markAppAsInitialized() {
                if ($scope.vm.appReady == undefined) {
                    $scope.vm.appReady = true;
                }
            }

            function loadMealData(fromDate, fromTime, toDate, toTime, pageNumber) {
                MealService.searchMeals(fromDate, fromTime, toDate, toTime, pageNumber)
                    .then(function (data) {

                        $scope.vm.errorMessages = [];
                        $scope.vm.currentPage = data.currentPage;
                        $scope.vm.totalPages = data.totalPages;

                        $scope.vm.originalMeals = _.map(data.meals, function (meal) {
                            meal.datetime = meal.date + ' ' + meal.time;
                            return meal;
                        });

                        $scope.vm.meals = _.cloneDeep($scope.vm.originalMeals);

                        _.each($scope.vm.meals, function (meal) {
                            meal.selected = false;
                        });

                        markAppAsInitialized();

                        if ($scope.vm.meals && $scope.vm.meals.length == 0) {
                            showInfoMessage("No results found.");
                        }
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                        markAppAsInitialized();
                    });
            }

            function clearMessages() {
                $scope.vm.errorMessages = [];
                $scope.vm.infoMessages = [];
            }

            function updateCaloriesCounterStatus() {
                var isCaloriesOK = $scope.vm.todaysCalories == 'None' || ($scope.vm.todaysCalories <= $scope.vm.maxCaloriesPerDay);
                $scope.vm.caloriesStatusStyle = isCaloriesOK ? 'cal-limit-ok' : 'cal-limit-nok';
            }

            function showInfoMessage(infoMessage) {
                $scope.vm.infoMessages = [];
                $scope.vm.infoMessages.push({description: infoMessage});
                $timeout(function () {
                    $scope.vm.infoMessages = [];
                }, 1000);
            }

            $scope.updateMaxCaloriesPerDay = function () {
                $timeout(function () {

                    if ($scope.vm.maxCaloriesPerDay < 0) {
                        return;
                    }

                    UserService.updateMaxCaloriesPerDay($scope.vm.maxCaloriesPerDay)
                        .then(function () {
                        },
                        function (errorMessage) {
                            showErrorMessage(errorMessage);
                        });
                    updateCaloriesCounterStatus();
                });
            };

            $scope.selectionChanged = function () {
                $scope.vm.isSelectionEmpty = !_.any($scope.vm.meals, function (meal) {
                    return meal.selected && !meal.deleted;
                });
            };

            $scope.pages = function () {
                return _.range(1, $scope.vm.totalPages + 1);
            };

            $scope.search = function (page) {

                var fromDate = new Date($scope.vm.fromDate);
                var toDate = new Date($scope.vm.toDate);

                console.log('search from ' + $scope.vm.fromDate + ' ' + $scope.vm.fromTime + ' to ' + $scope.vm.toDate + ' ' + $scope.vm.toTime);

                var errorsFound = false;

                if ($scope.vm.fromDate && !$scope.vm.toDate || !$scope.vm.fromDate && $scope.vm.toDate) {
                    showErrorMessage("Both from and to dates are needed");
                    errorsFound = true;
                    return;
                }

                if (fromDate > toDate) {
                    showErrorMessage("From date cannot be larger than to date");
                    errorsFound = true;
                }

                if (fromDate.getTime() == toDate.getTime() && $scope.vm.fromTime &&
                    $scope.vm.toTime && $scope.vm.fromTime > $scope.vm.toTime) {
                    showErrorMessage("Inside same day, from time cannot be larger than to time");
                    errorsFound = true;
                }

                if (!errorsFound) {
                    loadMealData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, page == undefined ? 1 : page);
                }

            };

            $scope.previous = function () {
                if ($scope.vm.currentPage > 1) {
                    $scope.vm.currentPage-= 1;
                    loadMealData($scope.vm.fromDate, $scope.vm.fromTime,
                        $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
                }
            };

            $scope.next = function () {
                if ($scope.vm.currentPage < $scope.vm.totalPages) {
                    $scope.vm.currentPage += 1;
                    loadMealData($scope.vm.fromDate, $scope.vm.fromTime,
                        $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
                }
            };

            $scope.goToPage = function (pageNumber) {
                if (pageNumber > 0 && pageNumber <= $scope.vm.totalPages) {
                    $scope.vm.currentPage = pageNumber;
                    loadMealData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, pageNumber);
                }
            };

            $scope.add = function () {
                $scope.vm.meals.unshift({
                    id: null,
                    datetime: null,
                    description: null,
                    calories: null,
                    selected: false,
                    new: true
                });
            };

            $scope.delete = function () {
                var deletedMealIds = _.chain($scope.vm.meals)
                    .filter(function (meal) {
                        return meal.selected && !meal.new;
                    })
                    .map(function (meal) {
                        return meal.id;
                    })
                    .value();

                MealService.deleteMeals(deletedMealIds)
                    .then(function () {
                        clearMessages();
                        showInfoMessage("deletion successful.");

                        _.remove($scope.vm.meals, function(meal) {
                            return meal.selected;
                        });

                        $scope.selectionChanged();
                        updateUserInfo();

                    },
                    function () {
                        clearMessages();
                        $scope.vm.errorMessages.push({description: "deletion failed."});
                    });
            };

            $scope.reset = function () {
                $scope.vm.meals = $scope.vm.originalMeals;
            };

            function getNotNew(meals) {
                return  _.chain(meals)
                    .filter(function (meal) {
                        return !meal.new;
                    })
                    .value();
            }

            function prepareMealsDto(meals) {
                return  _.chain(meals)
                    .each(function (meal) {
                        if (meal.datetime) {
                            var dt = meal.datetime.split(" ");
                            meal.date = dt[0];
                            meal.time = dt[1];
                        }
                    })
                    .map(function (meal) {
                        return {
                            id: meal.id,
                            date: meal.date,
                            time: meal.time,
                            description: meal.description,
                            calories: meal.calories,
                            version: meal.version
                        }
                    })
                    .value();
            }

            $scope.save = function () {

                var maybeDirty = prepareMealsDto(getNotNew($scope.vm.meals));

                var original = prepareMealsDto(getNotNew($scope.vm.originalMeals));

                var dirty = _.filter(maybeDirty).filter(function (meal) {

                    var originalMeal = _.filter(original, function (orig) {
                        return orig.id === meal.id;
                    });

                    if (originalMeal.length == 1) {
                        originalMeal = originalMeal[0];
                    }

                    return originalMeal && ( originalMeal.date != meal.date ||
                        originalMeal.time != meal.time || originalMeal.description != meal.description ||
                        originalMeal.calories != meal.calories)
                });

                var newItems = _.filter($scope.vm.meals, function (meal) {
                    return meal.new;
                });

                var saveAll = prepareMealsDto(newItems);
                saveAll = saveAll.concat(dirty);

                $scope.vm.errorMessages = [];

                // save all new items plus the ones that where modified
                MealService.saveMeals(saveAll).then(function () {
                        $scope.search($scope.vm.currentPage);
                        showInfoMessage("Changes saved successfully");
                        updateUserInfo();
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                    });

            };

            $scope.logout = function () {
                UserService.logout();
            }


        }]);

