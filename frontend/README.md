# PRONOM Frontend project

This project is meant to assist in developing the frontend for the PRONOM Linked Data Beta. It provides developer tools such as transpilation, dependency management, live-reloading and bundling/optimisation through Webpack.

The directory structure is as follows:

```sh
.
├── README.md
├── assemble.config.js │
├── common.config.js   │
├── dev.config.js      │
├── prod.config.js     └──--> Webpack configuration files
├── dist/ -------------------> Built HTML/CSS/JS/Asset files
├── package.json ------------> Project/dependency configuration
├── src
│   ├── base ----------------> Templates
│   │   ├── about.html
│   │   └── index.html
│   ├── js ------------------> Custom JS code
│   │   ├── common.js -------> Automatically injected into all pages
│   │   ├── lib
│   │   │   └── util.js
│   │   └── pages -----------> Page specific JS entry points
│   │       ├── about.js
│   │       └── index.js
│   └── styles --------------> Project styles
│       ├── about.scss
│       └── main.scss
└── yarn.lock
```

## Build

There's 2 different modes of building. Production and Assembly.

### Production

A production build is a build that is ready to be served by a HTML server. To run the build:

```sh
yarn build
```

This will generate all the code and assets and place them in `dist/`

### Assembly

An assembly build is a build that takes all the production configuration but places the generated files in the backend resources directory so they can be built into the backendJAR file. To run the build:

```sh
yarn assemble
```

This will generate all the code and assets and place them in their right places within the `../backend/src/main/resources/` directory

## Development

This project contains a setup for live-reloading and Hot Module Replacement. To start the dev server:

```sh
yarn start
```

This will start a development server on port 8080 and serve the frontend assets.

### Pug Templates support

[Pug](https://pugjs.org/) is the successor of the Jade templating engine. templates can be used as the base template language instead of HTML.
Pug allows for, among other things, the use of partials, which is why it is used here so a library of partials can be used and reused across the project.

### Adding a new page

When adding a new website page, for it to be recognised by the bundling system there are a few steps:

* Create the HTML file in `src/base`:
  * `touch src/contact.html`
* Create the js entrypoint file:
  * `touch src/js/pages/contact.js`
  * The filename should be the same as the HTML file
  * The file should be located in `src/js/pages`

This allows the bundler configuration to attach the right Javascript file to each page.

In the entry point js file, all the (S)CSS files can be imported and that will build the attached styles file for each page.

The entry point can also import exported code from other modules, such as libraries in `src/js/lib` or even NPM modules.

An example entry point js file for the contact page:

```js
import { test } from '@js/lib/test'

import '@styles/contact.scss'

const App = () => {
  console.log("contact page JS")
  test()
}

App()
```

And the example Pug file:

```pug
include partials/head.pug

h1 Hello Contact
a(href='index.html') Home
```

### Partials

Partials are handled by the Pug Templating engine. More information can be found [here](https://pugjs.org/language/inheritance.html).

The recommended usage is as follows:

```pug
//- partials/hello.pug

h1 Hello World!
```

```pug
//- index.pug

include partials/head.pug

include partials/hello.pug
a(href='index.html') Home
```

### Markdown support

For the production deployment it is required that text can be edited through a system of markdown file editing. In order to tell the server-side templating engine to render the contents of a markdown file instead of the frontend template contents, a util function was created as an extension to Thymeleaf. When developing the frontend, all one has to do is to use a thymeleaf meta attribute in combination with a call to the util function. Like so:

```pug
block content
  .unique-page-content
    article(th:utext="${@templateUtils.md('index_main')}")
```

`@templateUtils.md()` is the util function and it takes 1 argument, a string pointing to the file name of the markdown file to include in this section.

This will include the rendered markdown inside of the article tag. It is important to use the meta attribute `th:utext` as opposed to `th:text` because utext allows for unescaped HTML to be rendered.

This can be used of course for different parts of the HTML for example, if one wanted to change the `<title></title>` of a page using a markdown file, one could:

```pug
html
  head
    title(th:text="${@templateUtils.md('index_title')}")
```

And then just include the `index_title` file in the markdown directory.

#### Local development

For local development purposes, and because the server-side engine will replace the contents of the tag where th:utext or th:text are used, one can include default HTML which will be rendered while using the local devServer. Example:

```pug
block content
  .unique-page-content
    article(th:utext="${@templateUtils.md('index_main')}")
      h2 What is Pronom?
      p
        | Lorem ipsum etc
        | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
      h3 How to submit to Pronom?
      p
        | Lorem ipsum etc
        | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
```

In this case, the Lorem Ipsum text will be shown during development and in production it will be replaced with the contents of the markdown files.
