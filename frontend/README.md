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

This will generate all the code and assets and place them in `../backend/src/main/resources/templates/`

## Development

This project contains a setup for live-reloading and Hot Module Replacement. To start the dev server:

```sh
yarn start
```

This will start a development server on port 8080 and serve the frontend assets.

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

And the example HTML file:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>PRONOM</title>
</head>
<body>
  <h1>Hello Contact</h1>
</body>
</html>
```

### Partials

TODO: configure and document template partials

### Markdown usage

TODO: configure and document markdown fields in templates
