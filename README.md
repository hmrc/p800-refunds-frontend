

# :construction: p800-refunds-frontend

This service allows taxpayers to request refunds for overpaid tax without need for logging in.
It integrates with Ecospend and HMRC systems.

# Project setup in intellij

![img.png](readme/intellij-sbt-setup.png)

## Project specific sbt commands

### Turn off strict building

In sbt command in intellij:
```
sbt> relax
```
This will turn off strict building for this sbt session.
When you restart it or you build on jenkins, this will be turned on.

### Run with test only endpoints

```
sbt> runTestOnly
```


## Application architecture

### Journey States
Journey states correspond to the result of the submission on pages (or endpoints).


### Navigating through quickly with Tampermonkey
A script has been created to be used with [Tampermonkey](https://www.tampermonkey.net/) to enable fast navigation through
the service to make testing easier. To make use of it, install the Tampermonkey browser extension on your browser and
then install [this script](https://raw.githubusercontent.com/hmrc/p800-refunds-frontend/main/tampermonkey/quickJourney.js). After
installation, a green "Quick submit" button will be visible near the top-left of each page in the service. Clicking this
button will autocomplete the inputs on the page (including the test-only start page) and automatically click the continue
button on that page.

---

### Testing features requiring `True-Client-IP` header

Optionally, you can setup an Nginx server as a reverse proxy to test and debug with custom values for
`True-Client-IP`.

This can be useful for testing the lockout mechanism.

Install with brew and start the service:

```bash
brew install nginx
brew services start nginx
```

Edit the configuration under `/usr/local/etc/nginx/nginx.conf` to contain the following `server` block within the
`http` block.

```nginx
server {
    listen 127.0.0.1:8008;

    location / {
        proxy_set_header True-Client-IP 10.10.10.10;
        proxy_pass http://127.0.0.1:10150;
    }
}
```

The port used for the `listen` directive can be adjusted to any free port. The IP address given to the
`proxy_set_header` directive can be changed.

After making any changes make sure to run `brew services restart nginx`.

Now using the application normally via the new port, `localhost:8008` in this example, each request will send an
additional `True-Client-IP` header.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
