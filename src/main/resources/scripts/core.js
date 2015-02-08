// simple-todos.js
if (Meteor.isClient) {




        Template.logss.logs = function(){
            return LOGSS.find();
        }

// Inside the if (Meteor.isClient) block, right after Template.body.helpers:


// on the client
    Meteor.subscribe('logss');


    LOGSS = new Mongo.Collection("logs");

    Template.commands.rendered = function(){
        this.$('.ui.slider.checkbox').checkbox();
    }


    Template.commands.events({
        "submit .new-task": function (event) {
            // This function is called when the new task form is submitted
            alert('clciked');
            var command = "";

            $.each($('#myform').serializeArray(), function () {
                command += this.value + " ";
            });

            console.log(command);


            Meteor.call("run", command, function (err, response) {
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


    LOGSS = new Mongo.Collection("logs");

    Meteor.startup(function() {

        LOGSS.remove({});

    });


    Meteor.publish("logss", function () {
        return LOGSS.find();
    });


    Fiber = Npm.require('fibers');

    Tail = Npm.require('tail').Tail;

    tail = new Tail("/tmp/mylog.log");
    var count=0;
    tail.on("line", function (data) {
        console.log(data);
        var document = {
            id: count,
            content: data,
            details: "dddddd"
        };
        console.log(count);

        Fiber(function () {
            LOGSS.insert(document);
        }).run();
        count++;
    });

    tail.on("error", function (error) {
        console.log('ERROR: ', error);
    });


    // load future from fibers
    var Future = Npm.require("fibers/future");
// load exec
    var exec = Npm.require("child_process").exec;


    Meteor.methods({
        run: function (command) {

            Tail = Npm.require('tail').Tail;

            tail = new Tail("/tmp/mylog.log");

            tail.on("line", function (data) {
                console.log(data);

            });

            tail.on("error", function (error) {
                console.log('ERROR: ', error);
            });

            console.log(command);
            // Set up a future
            var fut = new Future();

            // This should work for any async method
            setTimeout(function () {

                // Return the results
                fut.return(" (delayed for 3 seconds)");

            }, 3 * 1000);

            exec(command, function (error, stdout, stderr) {
                if (error) {
                    console.log(error);
                    console.log(stderr.toString());
                    throw new Meteor.Error(500, command + " failed");
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