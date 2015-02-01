

// simple-todos.js
if (Meteor.isClient) {




// Inside the if (Meteor.isClient) block, right after Template.body.helpers:

    Template.logss.helpers({
        text: function () {
            return Session.get("logss");
        }

    });



    Template.commands.events({
        "submit .new-task": function (event) {
            // This function is called when the new task form is submitted
            var command = "";

            $.each($('#myform').serializeArray(), function() {
                command += this.value + " ";
            });

            console.log(command);


            Meteor.call("run", command, function(err, response) {
                Session.set('code', response);

                console.log(response);
                console.log(err);
                Session.set("logss", response);
            });



            // Prevent default form submit
            return false;
        }
    });


}

if (Meteor.isServer) {
    // load future from fibers
    var Future=Npm.require("fibers/future");
// load exec
    var exec=Npm.require("child_process").exec;




    Meteor.methods({
        run: function(command) {

            console.log(command);
            // Set up a future
            var fut = new Future();
            exec(command,function(error,stdout,stderr){
                if(error){
                    console.log(error);
                    console.log(stderr.toString());
                    throw new Meteor.Error(500,command+" failed");
                }
                console.log(stdout.toString())
                fut.return(stdout.toString());
            });

            // Wait for async to finish before returning
            // the result
            console.log("Finish!");
            return fut.wait();
        }
    });




}