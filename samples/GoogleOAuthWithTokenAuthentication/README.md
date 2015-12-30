# Sample Google OAuth Token Authentication

To run this app, you'll need an app registered with Google. You can go to your [Google Developer Console}(https://console.developers.google.com/project) and create one there or use one that you already have.

You'll need to pass the client ID and client secret so the permissions screen from Google will show the correct app name. To do that, you just pass them as arguments when starting this application, like the following:
 
```
java -jar build/libs/GoogleOAuthWithTokenAuthentication.jar --oauth.google.clientId=${YOUR_GOOGLE_APP_CLIENT_ID} --oauth.google.clientSecret=${YOUR_GOOGLE_APP_CLIENT_SECRET}
```

You can also set these in your IDE inside your run configuration passing them as program argument.
