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

| Name                              | Endpoint                  | Template              | Description |
|---|:--|:--|---|
| 1. Home                           | /                         | index.html            | Homepage |
| 2. Search                         | /search?q={query:string}  | search.html           | Search results page |
| 3. About                          | /about                    | about.html            | About PRONOM |
| 4. FAQ                            | /faq                      | faq.html              | Frequently asked questions |
| 5. External projects              | /external-projects        | external.html         | Page about external projects |
| 6. Contact                        | /contact                  | contact.html          | Contact page |
| | | | |
| 7. Contribute to Pronom           | /contribute               | contribute.html       | Contribution information |
| 7. Make a submission              | /contribute/form          | form-choice.html      | Page where user chooses to start a new form or clone from existing PUID |
| 7.1 New format                    | /contribute/form/new      | user-form.html        | External user form page (new format) |
| 7.2 Edit/clone format             | /contribute/form/{PUID}   | user-form.html        | External user form page (clone existing) |
| | | | |
| 10. Release notes                 | /release-notes            | rel-notes-list.html   | Displays the latest release notes and links to earlier notes |
| 11. Single release                | /release-notes/{version}  | rel-notes-single.html | Displays the release notes for a specific version of pronom |
| | | | |
| 12. Droid                         | /droid                    | droid.html            | Displays information about droid and download links |
| | | | |
| 13. PUID display page             | /{PUID}                   |                       | |
| 13.1 File Format                  | /{PUID}                   | file-format.html      | Displays information on a file format |
| 13.2 Software/Vendor/Generic PUID | /{PUID}                   | generic-puid.html     | Displays information on a generic PUID entity |

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

## Recommendation on page headers

Accessibility guidelines recommend that only 1 <h1/> header is present in each HTML page.
The defined markdown areas are either accessibility page titles or regions in the page body, therefore the markdown header 1 ('#') shouldn't be used as it renders as an h1 tag.

### Custom format for FAQ page

The FAQ page follows a specific Markdown format so categories can be parsed into the right format. The format is as follows:

**IMPORTANT:** Whitespace is important, especially in parsing the category headers. There is no white space between the start of a line and the header markers ('#'). 

```md
## Category 1 Title
### Cat 1 Item 1 Title
Cat 1 Item 1 text which can be infinitely long. 
It can also have line breaks
The only things that are not allowed here are headers 1 ('#') and 2 ('##') which will cause unexpected output if present at the start of a line

### Cat 1 Item 2 Title
Some text for the 2nd item of category 1

Random line breaks may occur and will be treated as they are normally by the markdown processor

## Category 2 Title
### Cat 2 Item 1 Title
Some text for cat 2 item 1
```

The markers the parser look for are the header markers at the start of a line, followed by a space: "^## " for category titles, "^### " for category items. Whatever comes after the line containing "^### " is considered the category text 

### Custom format for the steps in the contribute page

The submission steps in the contribute page also follow a specific markdown structure:

```md

# This is the title
This is the first step which explains how people can better help the PRONOM team with their submissions

It can include all markdown modifiers such as **bold** and _italic_

## headers can also be used
### as long as they are level 2+ since the first level header ('#') is reserved for titles.

> This goes in the example section of the numbered step

---
This is the second step and **all** modifiers can _be_ used, even [links](#)

\```
And fenced code blocks
\```
The marker for the example is a blockquote character at the start of a line (>)
The separator of numbered steps is the horizontal divider at the start of a line, 3 or more hyphen characters (---)

> This is an example, which can be
multi-line

---
# Third step title

some text

> example
```

This will generate a list of numbered steps that contain a title, an explanation and an example.

### Custom format for the team member cards in the about page

The team cards follow a specific structure as well

```md
[This is the image alt](https://placekitten.com/200/110)
Person Name
Person Title
person.email@nationalarchives.com

Text about the person. This can be infinitely long and use *all* __mardown__ allowed markers.

---
[A photo of Jane Doe](https://placekitten.com/400/220)
Jane Doe
Executive Assistant of Business
jane.doe@nationalarchives.com

Jane Doe started as PRONOM's executive assistant of business in... blah blah blah...
```

## Generating a runnable jar

Spring boot comes pre-configured to output a "fat" jar which includes all resources required to run the project. To generate it run the following:

```bash
./gradlew bootJar # outputs to build/libs/${rootProject.name}-${version}.jar
```

The resulting jar will be in build/libs:

```bash
cd build/libs/
tree
.
└── pronombackend-0.0.1-SNAPSHOT.jar
```

## Building a Docker image

Once the Jar exists a Docker image can be built using the provided Dockerfile:

```bash
docker build -t pronombackend:0.0.1 .
```
