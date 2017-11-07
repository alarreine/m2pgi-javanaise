# Javanaise-mvn


## Project Setup



This project should build out of the box with Maven.

## Running the project

Running this can be a somewhat involved task, but here's the simple way to do it:

1. Run the JvnCoordinator.  Make sure that when setting the RMI server codebase, the path ends with a /, otherwise **it will not work**.  ``java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:C:\path\to\classes\ jvn.cordinator.JvnCoordImpl``
2. Run the Client.  Make sure that when setting the RMI server codebase, the path ends with a /, otherwise **it will not work**.  Also ensure that client.policy is accessible by the application.  ``java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:C:\path\to\classes\ -Djava.security.policy=java.policy irc.Irc``

Everything should now come up and you should see a message in the server window.  The client prints out the number of characters that it sent to the server.

## Other Notes

If you improve this example in any way, feel free to send a pull request.
