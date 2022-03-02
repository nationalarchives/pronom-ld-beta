# pronom-backend

This is the skeleton app for Pronom-backend.

How-to guide:

1. git clone https://github.com/wallscope/pronom-backend.git
2. Use ./gradlew bootRun in pronom-backend directory to launch the app.

Authentication is taken care of by Spring Security - configuration can be found in src/main/resources/application.properties
Default:
user - user
password - password

Available endpoints:

- hello world - http://localhost:8080/hello

## Templates

Templates are handled by the frontend project. To generate the current template files and associated styles and js:

```bash
cd ../frontend
yarn assemble # npm run can be used instead of yarn: npm run assemble
```
 This will generate the template files and place them in `src/main/resources` which is where the webserver expects them to be.
 
## Dynamic markdown content

The template files use a markdown generation system to make the content dynamic in specific parts of the website that are prone to being changed more frequently.

In order for the application to pick up the markdown files and render their contents it needs to be told where the markdown files are held.

This can be done through the `MARKDOWN_DIR` environment variable. It defaults to `md`, meaning it will look for markdown files in a directory called `md` wherever the application is run from in the filesystem.
The application assumes the directory exists, and it doesn't attempt to create it.

If a file is referenced in a template but doesn't exist, an error is logged and an empty string is returned.