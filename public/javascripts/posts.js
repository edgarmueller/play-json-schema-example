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

        $scope.save = function(post) {
          $scope.alerts = [];
          if ($scope.post._id) {
            Post.update({_id: $scope.post._id}, $scope.post);
          } else {
            $scope.post.$save(function(response, headers) {
              $scope.posts.push(response)
            }, function(error) {
              for (var err in error.data) {
                for (var e in error.data[err]) {
                  var errMsg = error.data[err][e].msg;
                  $scope.alerts.push(errMsg);
                }
              }
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