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

## System user guide

### Generating signature files

Hi all, there seem to be a few problems with generating signatures with the release manager update. Here's a quick guide taken from the upcoming but not finalised documentation:

NOTE: the /signature.xml endpoint was always a test/debugging endpoint and never meant to be included in the final release as it wasn't present in the original website nor asked for. There are alternatives in the editorial dashboard to generate the exact same signature you would get from that endpoint.

The process of generating a signature file is split into 3 simple steps for convenience and flexibility.

1: Select between Binary and Container signature
There are 2 types of signature file: Binary signature file, Container signature file. The first step simply asks the user to select one of these.

2: Select between test signature and release signature
This is the step where the user selects whether they want to generate a one-off ad-hoc signature file that can be used for testing or an actual PRONOM release which will be listed on the public facing frontend.

2a: Test signature
The test signature file is an ad-hoc file generated from file formats which may still be a work in progress/in review.
If the option "Generate test signature" is selected the UI reveals 2 extra controls:
Release stage: Stage at which file formats will be included in the generated signature file
Full release: Choice to include or not previously published file formats.

Release Stage:
This functionality gives the PRONOM team the flexibility to generate files with signatures at different stages of readiness,
Contributions - All file formats that are in the 'contributions' list and above (Next release plan, In testing, Ready for release) will be included in the generated signature file.
Next release plan - All file formats that are in the 'next release plan' list and above (In testing, Ready for release) will be included in the generated signature file.
In testing - All file formats that are in the 'in testing' list and above (Ready for release) will be included in the generated signature file.
Ready for release - All file formats that are in the 'ready for release' list will be included in the generated signature file.

Full release:
When checked this option will include all previously published pronom file formats in the signature file, in essence creating a test file that is identical to a release file once all the file formats are moved to the 'ready to release' column.
When not checked the generated signature file will only include the file formats that are being worked on according to the selected release stage (see Release stage). This functionality can be used when developing new signatures as a way of ensuring that all the signatures are generated as expected. In a way, it means PRONOM can be used to generate ad-hoc single format signatures like tools like github.com/exponential-decay/signature-development-utility would generate, except there's a guarantee that the signature will be exactly the same when published, as it's generated by the same system.

2b: Publish to PRONOM
The "Publish current release plan" option will trigger a set of actions that will make available to the public the file formats that are being worked on by the pronom team.
The file formats published will only be the ones marked as 'Ready to release' and only the ones that have been assigned a PUID (this is ensured by not allowing the user to move file formats that have not been assigned a PUID to the "Ready to release" column).
When published, the Submissions will no longer appear in the editorial interface as the file formats they relate to have now been upgraded to fully-fledged, visible by the public File Formats. To edit one of these newly published file formats, the same process that is used to edit legacy file formats should be used.

When selected, this option will reveal 1 extra text input, asking for a version tag. The version tags follow the naming schemes used previously to generate signature file names:

Binary signature: This affects the file name and the Version field in the generated XML:.
The filename becomes: DROID_SignatureFile_V{VERSION}.xml -> eg: the user types "999" into the text box -> DROID_SignatureFile_V999.xml

Container signature: This affects only the Version number in the generated XML output.
The filename is automatically generated based on the date when the release is published: container-signature-{yyyyMMdd}.xml -> eg: The user creates a release on the 26th of March 1994 -> container-signature-19940326.xml

NOTE: once released, files can still be edited by going to the /editorial/releases endpoint.

3: Generate signature
Push the button and get a signature file or a release according to previously selected parameters.