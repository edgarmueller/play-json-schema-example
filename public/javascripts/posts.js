angular.module('app', ['ngResource', 'ngRoute'])

    .provider('Post', function() {
      this.$get = ['$resource', function($resource) {
        var Post = $resource('http://localhost:9000/posts/:id', {}, {
          update: {
            method: 'PUT'
          }
        });

        return Post;
      }];
    })

    .controller('MainCtrl', ['$scope', '$route', 'Post',
      function($scope, $route, Post) {

        $scope.alerts = [];
        $scope.post = new Post();
        $scope.posts = Post.query();

        $scope.newPost    = function() {
          $scope.post = new Post();
          $scope.editing = false;
        };

        $scope.activePost = function(post) {
          $scope.post = post;
          $scope.editing = true;
        };

        $scope.handleErrors = function(error) {
          for (var err in error.data) {
            for (var e in error.data[err]) {
              var errMsg = error.data[err][e].msg + " at " + err;
              $scope.alerts.push(errMsg);
            }
          }
          $scope.posts = Post.query();
        };

        $scope.save = function(post) {
          $scope.alerts = [];
          if ($scope.post.id) {
            Post.update({id: $scope.post.id}, $scope.post).$promise.then(
              function(response) {
                $scope.posts = Post.query();
              },
              function(errors) {
                $scope.handleErrors(errors);
            });
          } else {
            $scope.post.$save(function(response, headers) {
              $scope.alert = undefined;
              $scope.posts = Post.query();
            }, function(error) {
              $scope.handleErrors(error);

            });
          }
          $scope.editing = false;
          $scope.post = new Post();
        };

        $scope.delete = function(post) {
          Post.delete(post);
          _.remove($scope.posts, post)
        }
      }
    ]);