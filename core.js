// simple-todos.js
if (Meteor.isClient) {




    Template.hello.events({
        'click button': function () {
            console.log("clicked");
            // increment the counter when button is clicked
            message = 122
            Meteor.call("tail", message, function(err, response) {
                Session.set('code', response);

                console.log(response);
                console.log(err);
            });
        }
    });


}



if (Meteor.isServer) {
    // load future from fibers
    var Future=Npm.require("fibers/future");
// load exec
    var exec=Npm.require("child_process").exec;




    Meteor.methods({
        tail: function(message) {

            console.log(message);
            // Set up a future
            var fut = new Future();


            var command="pwd";
            exec(command,function(error,stdout,stderr){
                if(error){
                    console.log(error);
                    throw new Meteor.Error(500,command+" failed");
                }
                console.log(stdout.toString())
                fut.return(stdout.toString());
            });

            // Wait for async to finish before returning
            // the result
            return fut.wait();
        }
    });


    Meteor.methods({
        asyncJob: function(message) {



            var Future = Npm.require('fibers/future');
            var byline = Npm.require('byline');
            var f = new Future;

            var fs = Npm.require('fs');
// create stream in whatever way you like
            var instream = fs.createReadStream('/tmp/test.txt');
            var stream = byline.createStream(instream);

// run stream handling line-by-line events asynchronously
            stream.on('data', Meteor.bindEnvironment(function (line) {
                if (line) console.log(line)
                else f.return();
            }));

            // await on the future yielding to the other fibers and the line-by-line handling
            f.wait();


        }
    });


}








