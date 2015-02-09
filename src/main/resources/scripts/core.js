if (Meteor.isClient) {
    //Configure the template logss to bu updated when the server add an entry on the collection LOGSS.
    LOGSS = new Mongo.Collection("logs");
    EVENTS = new Mongo.Collection("commandEvents");


    Meteor.subscribe('commandEvents');
    Template.commandEvents.commandEvents = function () {
        return EVENTS.find();
    }

    Meteor.subscribe('logss');
    Template.logss.logs = function () {
        return LOGSS.find();
    }
    //Bootstrap JS for Semantic UI: https://github.com/nooitaf/meteor-semantic-ui
    Template.commands.rendered = function () {
        this.$('.ui.slider.checkbox').checkbox();
    }

    //Listener for the submit of the form
    Template.commands.events({
        "submit .new-task": function (event) {
            // This function is called when the new task form is submitted
            var command = "";
            $.each($('#myform').serializeArray(), function () {

                //If the conifugration contains a log tag, the html page is created with an hidden log path.
                //The server start to tail on the log.
                if (this.name == "hidden_log") {
                    Meteor.call("startTailOnLog", this.value);
                } else {
                    command += this.value + " ";
                }
            });

            console.log("Command from the form: " + command);

            //The server method executeCommand is called
            Meteor.call("executeCommand", command, function (err, response) {
                console.log(response);
                console.log(err);
            });
            // Prevent default form submit
            return false;
        }
    });


    Template.commandEvents.events({

        "click .my-pid .icon": function (event, template) {
            //TODO GET VALUE OF THE PID!
            // alert($(event.target.id()));
            console.log("my-pid cliecked");
            console.log(this.pid);
            //The server method executeCommand is called
            Meteor.call("killProcess", this.pid, function (err, response) {
                console.log(response);
                console.log(err);
            });
        }


    });

}

if (Meteor.isServer) {

    var logEntryCounter = 0;
    var Future = Npm.require("fibers/future");
    //Child_process is used to run the target command
    var exec = Npm.require("child_process").exec;
    var spawn = Npm.require("child_process").spawn;


    //Fiber is used to wrap collection insertions
    var Fiber = Npm.require('fibers');

    LOGSS = new Mongo.Collection("logs");
    EVENTS = new Mongo.Collection("commandEvents");


    //Clean up old logs...
    Meteor.startup(function () {
        LOGSS.remove({});
        EVENTS.remove({});
    });

    //Synchronize with the client...
    Meteor.publish("commandEvents", function () {
        return EVENTS.find();
    });

    //Synchronize with the client...
    Meteor.publish("logss", function () {
        return LOGSS.find();
    });

    //Tail on a given log file, when a new line is added insert in the LOGSS collection. The framework will
    // automatically update the logss template on the client due to publish/subscrive
    Meteor.methods({
        startTailOnLog: function (logPath) {

            console.log("Log path:  " + logPath);
            Tail = Npm.require('tail').Tail;
            tail = new Tail(logPath);
            tail.on("line", function (data) {
                console.log(data);
                var document = {
                    id: logEntryCounter,
                    content: data,
                    details: "details..."
                };
                console.log(logEntryCounter);
                Fiber(function () {
                    LOGSS.insert(document);
                }).run();
                logEntryCounter++;
            });
            tail.on("error", function (error) {
                console.log('ERROR: ', error);
            });
            //TODO refactor this..
            return "ok";
        }
    });

    var processCounter = 0;

    Meteor.methods({
        executeCommand: function (command) {

            console.log("The client say: EXEC --> " + command);
            // Set up a future
            var fut = new Future();

            // This should work for any async method
            setTimeout(function () {
                // Return the results
                fut.return(" (delayed for 2 seconds)");
            }, 2 * 1000);


             var child = exec(command);


            var isFirst = true;

            child.stdout.on('data', function (data) {
                if (isFirst == true) {
                    logEvent("Process Started", child.pid, "true");
                    isFirst = false;
                }


                console.log(child.pid);
                Fiber(function () {
                    var logEntry = {
                        id: logEntryCounter,
                        content: command + "output :" + data.toString(),
                        details: "details..."
                    };
                    LOGSS.insert(logEntry);
                    logEntryCounter++;
                }).run();
            });
            child.stderr.on('data', function (data) {
                console.log('stderr: ' + data);
                //In case of error, add the error on LOGSS collection
                Fiber(function () {
                    var logEntry = {
                        id: logEntryCounter,
                        content: command + "output : ERROR " + data.toString(),
                        details: "details..."
                    };
                    LOGSS.insert(logEntry);
                    logEntryCounter++;
                }).run();
            });
            child.on('close', function (code) {
                //The process has finish... insering log in the LOGSS collection

                if (code != 0) {
                    logEvent("Process finished with errors :(!", child.pid);
                } else {
                    logEvent("Process finished!", child.pid);
                }


                console.log('closing code: ' + code);
            });
            return fut.wait();
        }
    });

    Meteor.methods({
        killProcess: function (pid) {

            var child = exec("kill " + pid);
            child.on('close', function (code) {
                //The process has finish... insering log in the LOGSS collection

                if (code != 0) {
                    console.log("Process "+child.pid+" already killed?");
                    //logEvent("Cannot kill process!")
                } else {
                    logEvent("Process killed!",child.pid);
                }


                console.log('closing code: ' + code);
            });
        }
    });

    function logEvent(message, pid, isStart) {
        var date = new Date();
        var document = {
            id: processCounter,
            content: date + " " + message,
            pid: pid,
            isStart: isStart
        };
        Fiber(function () {
            EVENTS.insert(document);
        }).run();
        processCounter++;
    }

}