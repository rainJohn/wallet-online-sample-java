# Introduction #

This application is a sample integration of Google Wallet for online commerce.  It demonstrates how to integrate the API into your web application.


# Details #

Prerequisite
Google AppEngine Eclipse Plugin
https://developers.google.com/eclipse/

Running the application:

In eclipse import the folder as a new project
Right click on the project under Properities->Google->AppEngine check the box "Use Google Appengine"
Provide your project name and version(if you wish to deploy to appengine use the same name as your appengine instance), click ok
Download and include the following libraries in war/WEB-INF/lib:
  * jakarta velocity
  * jakarta commons logging
  * gson
  * jsontoken
  * google guava
  * joda time
Add the library jars to your build path under proprerties->Java Build Path->Libraries

Under war/WEB-INF/appengine-web.xml specify your sandbox and production ID and auth key.

Run the application as a web app

# Sample Applications #

Sample applications:

We've included two separate example implementations:
  * Single page ajax - The default example can be found at http://localhost:[port].  This example uses ajax to request jwts and uses css animations for the page transitions.  If you're planning on developing an ajax application, take a look at this sample.


  * Multi page posts - This example can be found at http://localhost:[port]/select.  This example uses posts to direct users to the next page in the purcase flow.  If you're planning on developing a mutli page application, take a look at this sample.